package com.endless.audio;

import com.endless.model.Track;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Thread-safe audio player powered by javax.sound.sampled SourceDataLine.
 * Delivers zero-latency seeking, smooth volume scaling, and real-time visualizer analysis.
 */
public class AudioPlayer implements VisualizerDataProvider {

    public enum State {
        STOPPED, PLAYING, PAUSED
    }

    public interface PlaybackListener {
        void onPlaybackStateChanged(State state);
        void onProgress(long positionMs, long durationMs);
        void onTrackEnded();
    }

    private final List<PlaybackListener> listeners = new ArrayList<>();

    // Audio format & buffer
    private byte[] audioBuffer = null;
    private AudioFormat audioFormat = null;
    private SourceDataLine line = null;

    private volatile int currentSampleIndex = 0; // byte position in audioBuffer
    private volatile long durationMs = 0;
    private volatile float volume = 0.85f; // 0.0 to 1.0

    private volatile State state = State.STOPPED;
    private final AtomicBoolean isPlayingFlag = new AtomicBoolean(false);
    private Thread playbackThread = null;

    // Visualizer real-time capture data
    private final float[] liveWaveform = new float[256];
    private final float[] frequencyBands = new float[32];
    private volatile float instantaneousEnergy = 0.0f;

    private Track currentTrack = null;

    public void addListener(PlaybackListener listener) {
        listeners.add(listener);
    }

    public void removeListener(PlaybackListener listener) {
        listeners.remove(listener);
    }

    public synchronized void play(Track track) {
        if (track == null || track.getFile() == null) return;

        stop();
        this.currentTrack = track;

        // Decode audio in background worker to keep UI fluid
        new Thread(() -> {
            try {
                AudioFileDecoder.DecodedAudio decoded = AudioFileDecoder.decode(track.getFile());
                AudioInputStream stream = decoded.stream;
                AudioFormat format = decoded.format;

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] temp = new byte[16384];
                int read;
                while ((read = stream.read(temp)) != -1) {
                    baos.write(temp, 0, read);
                }
                stream.close();

                byte[] rawData = baos.toByteArray();
                long frames = rawData.length / format.getFrameSize();
                long calcDuration = (long) ((frames / format.getFrameRate()) * 1000.0);
                if (calcDuration > 0) {
                    track.setDurationMs(calcDuration);
                }

                synchronized (AudioPlayer.this) {
                    audioBuffer = rawData;
                    audioFormat = format;
                    durationMs = track.getDurationMs() > 0 ? track.getDurationMs() : calcDuration;
                    currentSampleIndex = 0;

                    // Open audio output line
                    DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
                    if (line != null) {
                        line.close();
                    }
                    line = (SourceDataLine) AudioSystem.getLine(info);
                    line.open(audioFormat, 16384);
                    line.start();

                    state = State.PLAYING;
                    isPlayingFlag.set(true);
                    startPlaybackThread();
                }

                notifyStateChanged();
            } catch (Exception e) {
                System.err.println("Failed to play track: " + e.getMessage());
                state = State.STOPPED;
                notifyStateChanged();
            }
        }, "AudioLoaderThread").start();
    }

    public synchronized void pause() {
        if (state == State.PLAYING) {
            state = State.PAUSED;
            isPlayingFlag.set(false);
            if (line != null) {
                line.stop();
            }
            notifyStateChanged();
        }
    }

    public synchronized void resume() {
        if (state == State.PAUSED && line != null && audioBuffer != null) {
            state = State.PLAYING;
            isPlayingFlag.set(true);
            line.start();
            startPlaybackThread();
            notifyStateChanged();
        }
    }

    public synchronized void stop() {
        state = State.STOPPED;
        isPlayingFlag.set(false);
        if (playbackThread != null) {
            playbackThread.interrupt();
            playbackThread = null;
        }
        if (line != null) {
            try {
                line.stop();
                line.flush();
                line.close();
            } catch (Exception ignored) {
            }
            line = null;
        }
        audioBuffer = null;
        currentSampleIndex = 0;
        instantaneousEnergy = 0.0f;
        notifyStateChanged();
    }

    public synchronized void togglePlayPause() {
        if (state == State.PLAYING) {
            pause();
        } else if (state == State.PAUSED) {
            resume();
        }
    }

    public synchronized void seekTo(long targetMs) {
        if (audioBuffer == null || audioFormat == null || durationMs <= 0) return;

        targetMs = Math.max(0, Math.min(targetMs, durationMs));
        double fraction = (double) targetMs / durationMs;
        int frameSize = audioFormat.getFrameSize();
        int totalFrames = audioBuffer.length / frameSize;
        int targetFrame = (int) (fraction * totalFrames);
        int targetByte = targetFrame * frameSize;

        // Ensure alignment
        targetByte = (targetByte / frameSize) * frameSize;
        targetByte = Math.max(0, Math.min(targetByte, audioBuffer.length));

        currentSampleIndex = targetByte;
        if (line != null) {
            line.flush();
        }
        notifyProgress(targetMs, durationMs);
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float vol) {
        this.volume = Math.max(0.0f, Math.min(vol, 1.0f));
    }

    public long getPositionMs() {
        if (audioBuffer == null || audioFormat == null || durationMs <= 0) return 0;
        int total = audioBuffer.length;
        if (total == 0) return 0;
        return (long) (((double) currentSampleIndex / total) * durationMs);
    }

    public long getDurationMs() {
        return durationMs;
    }

    public State getState() {
        return state;
    }

    public Track getCurrentTrack() {
        return currentTrack;
    }

    private void startPlaybackThread() {
        if (playbackThread != null && playbackThread.isAlive()) {
            return;
        }

        playbackThread = new Thread(() -> {
            byte[] chunk = new byte[2048];
            byte[] processedChunk = new byte[2048];
            long lastProgressUpdate = 0;

            while (isPlayingFlag.get() && state == State.PLAYING) {
                byte[] buf;
                int readIndex;
                AudioFormat fmt;
                SourceDataLine l;

                synchronized (AudioPlayer.this) {
                    buf = audioBuffer;
                    readIndex = currentSampleIndex;
                    fmt = audioFormat;
                    l = line;
                }

                if (buf == null || l == null || fmt == null) break;

                int frameSize = fmt.getFrameSize();
                int remaining = buf.length - readIndex;
                if (remaining <= 0) {
                    // Track reached the end
                    state = State.STOPPED;
                    isPlayingFlag.set(false);
                    notifyTrackEnded();
                    break;
                }

                int toRead = Math.min(chunk.length, remaining);
                toRead = (toRead / frameSize) * frameSize; // Align to frame boundary
                if (toRead <= 0) break;

                System.arraycopy(buf, readIndex, chunk, 0, toRead);

                // Software attenuation & real-time visualizer sample capture
                float gain = (float) Math.pow(volume, 2.0); // Natural quadratic loudness curve
                int samples = toRead / 2; // 16-bit
                float peak = 0;

                for (int i = 0; i < toRead; i += 2) {
                    short s = (short) ((chunk[i] & 0xFF) | (chunk[i + 1] << 8));
                    float sampleNorm = s / 32768.0f;
                    float absNorm = Math.abs(sampleNorm);
                    if (absNorm > peak) peak = absNorm;

                    // Capture waveform sample
                    int waveIdx = (i / 2) % liveWaveform.length;
                    liveWaveform[waveIdx] = sampleNorm;

                    // Apply volume
                    short scaled = (short) (s * gain);
                    processedChunk[i] = (byte) (scaled & 0xFF);
                    processedChunk[i + 1] = (byte) ((scaled >> 8) & 0xFF);
                }

                // Update visualizer peak and compute frequency energy approximation
                instantaneousEnergy = instantaneousEnergy * 0.7f + peak * 0.3f;
                updateFrequencyBands(processedChunk, toRead);

                // Write to audio output line
                l.write(processedChunk, 0, toRead);

                synchronized (AudioPlayer.this) {
                    currentSampleIndex += toRead;
                }

                // Periodic progress notification (~30 FPS)
                long now = System.currentTimeMillis();
                if (now - lastProgressUpdate > 33) {
                    lastProgressUpdate = now;
                    notifyProgress(getPositionMs(), durationMs);
                }
            }
        }, "EndlessAudioPlayback");

        playbackThread.setPriority(Thread.MAX_PRIORITY);
        playbackThread.start();
    }

    private void updateFrequencyBands(byte[] chunk, int length) {
        int sampleCount = length / 2;
        int bands = frequencyBands.length;
        int samplesPerBand = Math.max(1, sampleCount / bands);

        for (int b = 0; b < bands; b++) {
            float sum = 0;
            int start = b * samplesPerBand * 2;
            int end = Math.min(length, start + samplesPerBand * 2);
            for (int i = start; i + 1 < end; i += 2) {
                short s = (short) ((chunk[i] & 0xFF) | (chunk[i + 1] << 8));
                sum += Math.abs(s / 32768.0f);
            }
            float bandAvg = sum / Math.max(1, (end - start) / 2);
            // Decay smoothing
            frequencyBands[b] = frequencyBands[b] * 0.65f + bandAvg * 0.35f;
        }
    }

    private void notifyStateChanged() {
        for (PlaybackListener l : listeners) {
            l.onPlaybackStateChanged(state);
        }
    }

    private void notifyProgress(long cur, long total) {
        for (PlaybackListener l : listeners) {
            l.onProgress(cur, total);
        }
    }

    private void notifyTrackEnded() {
        for (PlaybackListener l : listeners) {
            l.onTrackEnded();
        }
    }

    // VisualizerDataProvider implementation
    @Override
    public float[] getWaveformSamples() {
        return liveWaveform;
    }

    @Override
    public float[] getFrequencyBands(int bandsCount) {
        if (bandsCount == frequencyBands.length) {
            return frequencyBands;
        }
        float[] res = new float[bandsCount];
        for (int i = 0; i < bandsCount; i++) {
            int srcIdx = (int) (((float) i / bandsCount) * frequencyBands.length);
            res[i] = frequencyBands[srcIdx];
        }
        return res;
    }

    @Override
    public float getInstantaneousEnergy() {
        return instantaneousEnergy;
    }

    @Override
    public boolean isPlaying() {
        return state == State.PLAYING;
    }
}
