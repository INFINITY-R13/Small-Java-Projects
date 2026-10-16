package com.blackjack;

import com.blackjack.game.BlackjackGame;
import com.blackjack.ui.ConsoleColors;

/**
 * Application entry point for the Blackjack console game.
 */
public class Main {
    public static void main(String[] args) {
        // Allow disabling color via --no-color flag or NO_COLOR environment variable
        for (String arg : args) {
            if ("--no-color".equalsIgnoreCase(arg)) {
                ConsoleColors.setEnabled(false);
            }
        }
        if (System.getenv("NO_COLOR") != null) {
            ConsoleColors.setEnabled(false);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println(ConsoleColors.RESET);
        }));

        BlackjackGame game = new BlackjackGame();
        game.start();
    }
}
