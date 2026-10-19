package com.timer;

import java.awt.Toolkit;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;

/**
 * Utility class for audio alert notifications across platforms.
 */
public final class SoundUtils {

    private SoundUtils() {
        // Utility class
    }

    /**
     * Plays an audible completion alert asynchronously.
     * Uses MIDI synthesizer if available for a pleasant chime,
     * falling back to AWT Toolkit beep and terminal bell.
     */
    public static void playCompletionChime() {
        Thread chimeThread = new Thread(() -> {
            boolean midiPlayed = false;
            try {
                Synthesizer synth = MidiSystem.getSynthesizer();
                synth.open();
                MidiChannel[] channels = synth.getChannels();
                if (channels != null && channels.length > 0) {
                    MidiChannel channel = channels[0];
                    // Program 11: Vibraphone, 14: Tubular Bells, 9: Celesta
                    channel.programChange(11);

                    // Ascending major chord fanfare (C5, E5, G5, C6)
                    int[] notes = {72, 76, 79, 84};
                    for (int note : notes) {
                        channel.noteOn(note, 95);
                        Thread.sleep(130);
                    }
                    Thread.sleep(350);
                    for (int note : notes) {
                        channel.noteOff(note);
                    }
                    midiPlayed = true;
                }
                synth.close();
            } catch (Throwable ignored) {
                // MIDI not supported in this environment
            }

            if (!midiPlayed) {
                try {
                    Toolkit.getDefaultToolkit().beep();
                } catch (Throwable ignored) {
                    // System beep fallback
                }
            }

            // Always send terminal bell control character
            System.out.print("\u0007");
            System.out.flush();
        }, "Chime-Thread");

        chimeThread.setDaemon(true);
        chimeThread.start();
    }
}
