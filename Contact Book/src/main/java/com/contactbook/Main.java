package com.contactbook;

import com.contactbook.service.ContactService;
import com.contactbook.ui.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

/**
 * Application entry point.
 *
 * <p>Installs FlatLightLaf <em>before</em> any Swing components are created,
 * then hands off to the EDT for UI construction.
 */
public class Main {

    public static void main(String[] args) {
        // Install FlatLaf as early as possible, before the EDT is touched
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            ContactService service = new ContactService();
            MainFrame frame = new MainFrame(service);
            frame.setVisible(true);
        });
    }
}
