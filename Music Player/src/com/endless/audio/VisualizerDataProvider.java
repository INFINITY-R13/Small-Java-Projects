package com.endless.audio;

/**
 * Interface for providing real-time audio sample and frequency data to the visualizer.
 */
public interface VisualizerDataProvider {
    /**
     * Returns the latest time-domain waveform samples (normalized -1.0 to 1.0).
     */
    float[] getWaveformSamples();

    /**
     * Returns energy in frequency bands (normalized 0.0 to 1.0).
     */
    float[] getFrequencyBands(int bandsCount);

    /**
     * Returns instantaneous overall audio energy (0.0 to 1.0).
     */
    float getInstantaneousEnergy();

    /**
     * Returns whether audio is currently playing.
     */
    boolean isPlaying();
}
