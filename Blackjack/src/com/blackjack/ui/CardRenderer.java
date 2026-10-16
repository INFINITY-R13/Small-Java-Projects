package com.blackjack.ui;

import com.blackjack.model.Card;
import com.blackjack.model.Hand;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility for rendering cards and hands as ASCII art in the console.
 */
public class CardRenderer {

    private static final String TOP_BORDER    = "┌─────────┐";
    private static final String BOTTOM_BORDER = "└─────────┘";
    private static final String EMPTY_LINE    = "│         │";
    private static final String HIDDEN_LINE   = "│ ░░░░░░░ │";
    private static final String HIDDEN_MID    = "│ ░░ ? ░░ │";

    /**
     * Generates a 7-line representation of a single card.
     */
    public static List<String> renderCardLines(Card card) {
        List<String> lines = new ArrayList<>(7);
        String symbol = card.getRank().getSymbol();
        String suitSymbol = card.getSuit().getSymbol();
        String color = card.getSuit().getColorCode();

        lines.add(TOP_BORDER);

        // Line 1: Top rank
        if (symbol.length() == 1) {
            lines.add("│ " + ConsoleColors.colorize(color, symbol) + "       │");
        } else {
            lines.add("│ " + ConsoleColors.colorize(color, symbol) + "      │");
        }

        // Line 2: Empty
        lines.add(EMPTY_LINE);

        // Line 3: Centered Suit
        lines.add("│    " + ConsoleColors.colorize(color, suitSymbol) + "    │");

        // Line 4: Empty
        lines.add(EMPTY_LINE);

        // Line 5: Bottom rank
        if (symbol.length() == 1) {
            lines.add("│       " + ConsoleColors.colorize(color, symbol) + " │");
        } else {
            lines.add("│      " + ConsoleColors.colorize(color, symbol) + " │");
        }

        // Line 6: Bottom border
        lines.add(BOTTOM_BORDER);

        return lines;
    }

    /**
     * Generates a 7-line representation of a hidden (face-down) card.
     */
    public static List<String> renderHiddenCardLines() {
        List<String> lines = new ArrayList<>(7);
        String border = ConsoleColors.colorize(ConsoleColors.CYAN, TOP_BORDER);
        String bottom = ConsoleColors.colorize(ConsoleColors.CYAN, BOTTOM_BORDER);
        String fill = ConsoleColors.colorize(ConsoleColors.BLUE, HIDDEN_LINE);
        String mid = ConsoleColors.colorize(ConsoleColors.YELLOW, HIDDEN_MID);

        lines.add(border);
        lines.add(fill);
        lines.add(fill);
        lines.add(mid);
        lines.add(fill);
        lines.add(fill);
        lines.add(bottom);
        return lines;
    }

    /**
     * Renders a hand horizontally side-by-side.
     * If hideSecondCard is true, the 2nd card is rendered as a hidden face-down card.
     */
    public static String renderHand(Hand hand, boolean hideSecondCard) {
        if (hand == null || hand.getCards().isEmpty()) {
            return "[Empty Hand]";
        }

        List<List<String>> renderedCards = new ArrayList<>();
        List<Card> cards = hand.getCards();

        for (int i = 0; i < cards.size(); i++) {
            if (i == 1 && hideSecondCard) {
                renderedCards.add(renderHiddenCardLines());
            } else {
                renderedCards.add(renderCardLines(cards.get(i)));
            }
        }

        StringBuilder sb = new StringBuilder();
        int lineCount = 7;
        for (int line = 0; line < lineCount; line++) {
            for (int c = 0; c < renderedCards.size(); c++) {
                sb.append(renderedCards.get(c).get(line));
                if (c < renderedCards.size() - 1) {
                    sb.append("  ");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    public static String renderHand(Hand hand) {
        return renderHand(hand, false);
    }
}
