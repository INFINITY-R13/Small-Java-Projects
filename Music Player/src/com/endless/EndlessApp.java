package com.endless;

import com.endless.audio.AudioPlayer;
import com.endless.audio.TrackSynthesizer;
import com.endless.model.Playlist;
import com.endless.model.Track;
import com.endless.ui.MainWindow;

import javax.swing.*;
import java.io.File;
import java.util.List;

/**
 * Endless - An aesthetic, minimalist music player crafted with Apple design principles.
 */
public class EndlessApp {

    public static void main(String[] args) {
        // macOS UI properties
        System.setProperty("apple.awt.application.name", "Endless");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.appearance", "system");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        System.setProperty("sun.java2d.opengl", "true");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Initialize audio engine and playlist
        AudioPlayer player = new AudioPlayer();
        Playlist playlist = new Playlist();

        // Launch UI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow(player, playlist);
            window.setVisible(true);

            // Synthesize and load demo tracks asynchronously
            new Thread(() -> {
                File demoDir = new File("demo-tracks");
                List<Track> demoTracks = TrackSynthesizer.ensureDemoTracksExist(demoDir);
                SwingUtilities.invokeLater(() -> {
                    playlist.addTracks(demoTracks);
                    if (!demoTracks.isEmpty()) {
                        playlist.setCurrentIndex(0);
                    }
                });
            }, "DemoTracksInitializer").start();
        });
    }
}
