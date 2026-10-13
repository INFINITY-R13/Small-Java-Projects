package com.endless.audio;

import com.endless.model.MetadataExtractor;
import com.endless.model.Track;

import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Algorithmic music synthesizer that generates high-fidelity ambient, chillwave,
 * and neo-classical demo tracks and accompanying artwork for out-of-the-box enjoyment.
 */
public class TrackSynthesizer {

    public static List<Track> ensureDemoTracksExist(File demoDir) {
        List<Track> tracks = new ArrayList<>();
        if (!demoDir.exists()) {
            demoDir.mkdirs();
        }

        File track1 = new File(demoDir, "01_Infinite_Horizon.wav");
        File track2 = new File(demoDir, "02_Starlight_Pulse.wav");
        File track3 = new File(demoDir, "03_Morning_Mist.wav");

        if (!track1.exists()) generateTrack1(track1);
        if (!track2.exists()) generateTrack2(track2);
        if (!track3.exists()) generateTrack3(track3);

        if (track1.exists()) {
            Track t = MetadataExtractor.extract(track1);
            t.setTitle("Infinite Horizon");
            t.setArtist("Endless Ambient");
            t.setAlbum("Celestial Spaces");
            t.setDemo(true);
            t.setArtwork(MetadataExtractor.generateAppleCover("Infinite Horizon", "Endless Ambient"));
            tracks.add(t);
        }
        if (track2.exists()) {
            Track t = MetadataExtractor.extract(track2);
            t.setTitle("Starlight Pulse");
            t.setArtist("Aura & Pulse");
            t.setAlbum("Midnight Frequency");
            t.setDemo(true);
            t.setArtwork(MetadataExtractor.generateAppleCover("Starlight Pulse", "Aura & Pulse"));
            tracks.add(t);
        }
        if (track3.exists()) {
            Track t = MetadataExtractor.extract(track3);
            t.setTitle("Morning Mist");
            t.setArtist("Lumina Strings");
            t.setAlbum("Acoustic Reflections");
            t.setDemo(true);
            t.setArtwork(MetadataExtractor.generateAppleCover("Morning Mist", "Lumina Strings"));
            tracks.add(t);
        }

        return tracks;
    }

    private static void generateTrack1(File file) {
        // "Infinite Horizon": Warm analog pad chords + bell arpeggio + sub bass
        int sampleRate = 44100;
        int durationSec = 24;
        int totalSamples = sampleRate * durationSec;
        byte[] pcm = new byte[totalSamples * 4]; // 16-bit stereo

        // Chord progression: Dm9 (D, F, A, C, E) -> Bbmaj7 (Bb, D, F, A) -> Fmaj7 (F, A, C, E) -> Cmaj7 (C, E, G, B)
        double[][] chordFreqs = {
            {146.83, 174.61, 220.00, 261.63, 329.63}, // Dm9
            {116.54, 146.83, 174.61, 220.00, 293.66}, // Bbmaj7
            {174.61, 220.00, 261.63, 329.63, 392.00}, // Fmaj7
            {130.81, 164.81, 196.00, 246.94, 329.63}  // Cmaj7
        };
        double[] chordDurations = {6.0, 6.0, 6.0, 6.0};

        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / sampleRate;
            int chordIdx = (int) (t / 6.0) % chordFreqs.length;
            double chordT = t % 6.0;
            double[] freqs = chordFreqs[chordIdx];

            // Pad envelope (smooth attack and release)
            double padEnv = Math.sin(Math.PI * (chordT / 6.0));
            if (padEnv < 0) padEnv = 0;

            double left = 0;
            double right = 0;

            // Pad oscillators with subtle stereo detuning
            for (int k = 0; k < freqs.length; k++) {
                double f = freqs[k];
                double osc1 = Math.sin(2.0 * Math.PI * f * t);
                double osc2 = Math.sin(2.0 * Math.PI * (f * 1.003) * t + 0.3); // slight detune
                double osc3 = 0.5 * Math.sin(2.0 * Math.PI * (f * 0.997) * t + 0.7);
                double padVoice = (osc1 + osc2 + osc3) * 0.25 * padEnv;

                left += padVoice * (0.8 + 0.2 * Math.sin(t * 0.5 + k));
                right += padVoice * (0.8 + 0.2 * Math.cos(t * 0.5 + k));
            }

            // Bell arpeggio on top (repeating 1/4 note bell chimes)
            double arpSpeed = 2.0; // 2 notes per sec
            double arpStep = (t * arpSpeed) % freqs.length;
            int arpNoteIdx = (int) arpStep;
            double noteTime = (t * arpSpeed) - Math.floor(t * arpSpeed);
            double bellEnv = Math.exp(-noteTime * 4.0); // fast decay
            double bellFreq = freqs[arpNoteIdx] * 2.0; // octave up
            double bell = Math.sin(2.0 * Math.PI * bellFreq * t) * bellEnv * 0.2;

            left += bell * 0.7;
            right += bell * 0.9;

            // Sub bass
            double rootFreq = freqs[0] * 0.5;
            double bass = Math.sin(2.0 * Math.PI * rootFreq * t) * 0.25 * padEnv;
            left += bass;
            right += bass;

            // Subtle tape warmth / vinyl dust
            double noise = (Math.random() - 0.5) * 0.008;
            left += noise;
            right += noise;

            // Master limiter & clamping
            left = Math.max(-0.95, Math.min(0.95, left));
            right = Math.max(-0.95, Math.min(0.95, right));

            short sLeft = (short) (left * 28000);
            short sRight = (short) (right * 28000);

            int idx = i * 4;
            pcm[idx] = (byte) (sLeft & 0xFF);
            pcm[idx + 1] = (byte) ((sLeft >> 8) & 0xFF);
            pcm[idx + 2] = (byte) (sRight & 0xFF);
            pcm[idx + 3] = (byte) ((sRight >> 8) & 0xFF);
        }

        saveWav(pcm, sampleRate, file);
    }

    private static void generateTrack2(File file) {
        // "Starlight Pulse": Lo-fi downtempo with smooth kicks and electric Rhodes chords
        int sampleRate = 44100;
        int durationSec = 22;
        int totalSamples = sampleRate * durationSec;
        byte[] pcm = new byte[totalSamples * 4];

        // Am7 -> Fmaj7 -> Cmaj7 -> G
        double[][] chords = {
            {220.00, 261.63, 329.63, 392.00}, // Am7
            {174.61, 220.00, 261.63, 329.63}, // Fmaj7
            {130.81, 164.81, 196.00, 246.94}, // Cmaj7
            {196.00, 246.94, 293.66, 392.00}  // G
        };

        double bpm = 90.0;
        double beatSec = 60.0 / bpm;

        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / sampleRate;
            int bar = (int) (t / (beatSec * 4));
            int chordIdx = bar % chords.length;
            double beat = (t / beatSec);
            double beatFrac = beat - Math.floor(beat);

            double left = 0;
            double right = 0;

            // Electric piano chords on beats 1 and 3
            double chordFrac = (beat % 2.0);
            double epEnv = Math.exp(-chordFrac * 2.2);
            for (double f : chords[chordIdx]) {
                // Rhodes tone = sine fundamental + bell harmonic
                double tone = Math.sin(2.0 * Math.PI * f * t) + 0.25 * Math.sin(2.0 * Math.PI * f * 3.0 * t);
                double voice = tone * epEnv * 0.18;
                left += voice;
                right += voice * 1.05;
            }

            // Soft lo-fi kick on beat 0 and 2.5
            int currentBeatInt = (int) Math.floor(beat) % 4;
            if (currentBeatInt == 0 || (currentBeatInt == 2 && beatFrac > 0.5)) {
                double kickTime = beatFrac;
                double kickFreq = 120.0 * Math.exp(-kickTime * 25.0) + 45.0;
                double kickEnv = Math.exp(-kickTime * 10.0);
                double kick = Math.sin(2.0 * Math.PI * kickFreq * kickTime) * kickEnv * 0.35;
                left += kick;
                right += kick;
            }

            // Soft shaker / vinyl dust on 8th notes
            double eighth = (beat * 2) - Math.floor(beat * 2);
            double shaker = (Math.random() - 0.5) * Math.exp(-eighth * 18.0) * 0.05;
            left += shaker * 0.8;
            right += shaker * 1.2;

            left = Math.max(-0.95, Math.min(0.95, left));
            right = Math.max(-0.95, Math.min(0.95, right));

            short sLeft = (short) (left * 27000);
            short sRight = (short) (right * 27000);

            int idx = i * 4;
            pcm[idx] = (byte) (sLeft & 0xFF);
            pcm[idx + 1] = (byte) ((sLeft >> 8) & 0xFF);
            pcm[idx + 2] = (byte) (sRight & 0xFF);
            pcm[idx + 3] = (byte) ((sRight >> 8) & 0xFF);
        }

        saveWav(pcm, sampleRate, file);
    }

    private static void generateTrack3(File file) {
        // "Morning Mist": Neo-classical acoustic piano & ambient cello
        int sampleRate = 44100;
        int durationSec = 20;
        int totalSamples = sampleRate * durationSec;
        byte[] pcm = new byte[totalSamples * 4];

        // Em9 -> Cmaj7 -> G -> D
        double[][] notes = {
            {164.81, 196.00, 246.94, 293.66, 329.63},
            {130.81, 164.81, 196.00, 246.94, 261.63},
            {196.00, 246.94, 293.66, 392.00, 196.00},
            {146.83, 185.00, 220.00, 293.66, 146.83}
        };

        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / sampleRate;
            int chordIdx = (int) (t / 5.0) % notes.length;
            double chordT = t % 5.0;

            double left = 0;
            double right = 0;

            // Ambient cello drone
            double droneFreq = notes[chordIdx][0] * 0.5;
            double cello = (Math.sin(2 * Math.PI * droneFreq * t) + 0.3 * Math.sin(4 * Math.PI * droneFreq * t)) * 0.22;
            left += cello;
            right += cello;

            // Sequential delicate piano notes
            double noteInterval = 1.0;
            int noteSub = (int) (chordT / noteInterval) % notes[chordIdx].length;
            double noteTime = (chordT % noteInterval);
            double pianoEnv = Math.exp(-noteTime * 3.5);
            double pf = notes[chordIdx][noteSub];
            double piano = (Math.sin(2 * Math.PI * pf * t) + 0.4 * Math.sin(4 * Math.PI * pf * t) + 0.15 * Math.sin(6 * Math.PI * pf * t)) * pianoEnv * 0.28;

            left += piano * (0.6 + 0.4 * Math.sin(noteSub));
            right += piano * (0.6 + 0.4 * Math.cos(noteSub));

            left = Math.max(-0.95, Math.min(0.95, left));
            right = Math.max(-0.95, Math.min(0.95, right));

            short sLeft = (short) (left * 27000);
            short sRight = (short) (right * 27000);

            int idx = i * 4;
            pcm[idx] = (byte) (sLeft & 0xFF);
            pcm[idx + 1] = (byte) ((sLeft >> 8) & 0xFF);
            pcm[idx + 2] = (byte) (sRight & 0xFF);
            pcm[idx + 3] = (byte) ((sRight >> 8) & 0xFF);
        }

        saveWav(pcm, sampleRate, file);
    }

    private static void saveWav(byte[] pcmData, int sampleRate, File file) {
        try {
            AudioFormat format = new AudioFormat(sampleRate, 16, 2, true, false);
            ByteArrayInputStream bais = new ByteArrayInputStream(pcmData);
            AudioInputStream ais = new AudioInputStream(bais, format, pcmData.length / 4);
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
        } catch (Exception e) {
            System.err.println("Error saving synthesized WAV: " + e.getMessage());
        }
    }
}
