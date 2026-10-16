package com.blackjack.game;

import com.blackjack.model.Card;
import com.blackjack.model.Deck;
import com.blackjack.model.Hand;
import com.blackjack.model.HandStatus;
import com.blackjack.model.Rank;
import com.blackjack.ui.CardRenderer;
import com.blackjack.ui.ConsoleColors;
import com.blackjack.ui.InputHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * Main game engine orchestrating rules, betting, player actions, dealer AI, and settlements.
 */
public class BlackjackGame {
    public static final int MIN_BET = 10;
    public static final int DEFAULT_BANKROLL = 1000;
    public static final int REBUY_AMOUNT = 500;
    public static final int MAX_SPLIT_HANDS = 4;

    private final Deck deck;
    private final Dealer dealer;
    private Player player;
    private GameStats stats;
    private final InputHandler input;

    public BlackjackGame() {
        this(new InputHandler());
    }

    public BlackjackGame(InputHandler input) {
        this.deck = new Deck(Deck.DEFAULT_DECK_COUNT);
        this.dealer = new Dealer();
        this.input = input;
    }

    /**
     * Starts the interactive Blackjack game session.
     */
    public void start() {
        printBanner();

        String name = input.readString(ConsoleColors.colorize(ConsoleColors.CYAN, "Enter your name [default: Player]: "), "Player");
        int bankroll = input.readInt(
                ConsoleColors.colorize(ConsoleColors.CYAN, String.format("Enter starting bankroll ($%d - $100,000) [default: $%,d]: ", MIN_BET, DEFAULT_BANKROLL)),
                MIN_BET,
                100000
        );

        player = new Player(name, bankroll);
        stats = new GameStats(bankroll);

        System.out.println(ConsoleColors.colorize(ConsoleColors.GREEN,
                String.format("\nWelcome, %s! You're sitting at a 4-deck table with $%,d chips. Good luck!\n", player.getName(), player.getChips())));

        boolean keepPlaying = true;
        while (keepPlaying) {
            if (player.getChips() < MIN_BET) {
                System.out.println(ConsoleColors.colorize(ConsoleColors.RED,
                        String.format("\nYour chip balance ($%,d) is below the minimum bet ($%,d)!", player.getChips(), MIN_BET)));
                boolean rebuy = input.readYesNo("Would you like to rebuy $500 chips to continue playing?", true);
                if (rebuy) {
                    player.addChips(REBUY_AMOUNT);
                    stats.updateBalance(player.getChips());
                    System.out.println(ConsoleColors.colorize(ConsoleColors.GREEN,
                            String.format("Rebuy successful! New balance: $%,d\n", player.getChips())));
                } else {
                    System.out.println(ConsoleColors.colorize(ConsoleColors.YELLOW, "You've cashed out!"));
                    break;
                }
            }

            playRound();

            if (player.getChips() >= MIN_BET) {
                keepPlaying = input.readYesNo("\nPlay another hand?", true);
            }
        }

        System.out.println();
        System.out.println(stats.generateSummary());
        System.out.println(ConsoleColors.colorize(ConsoleColors.BOLD_GREEN, "Thank you for playing Blackjack! Come back soon.\n"));
    }

    /**
     * Executes a single round of Blackjack.
     */
    public void playRound() {
        // 1. Check cut-card reshuffle
        if (deck.isCutCardReached()) {
            System.out.println(ConsoleColors.colorize(ConsoleColors.YELLOW,
                    " Cut card reached (" + deck.remainingCards() + " cards left). Reshuffling 4-deck shoe..."));
            deck.resetAndShuffle();
            pause(500);
        }

        player.clearHands();
        dealer.clearHand();

        // 2. Betting phase
        System.out.println(ConsoleColors.colorize(ConsoleColors.BOLD_WHITE, "────────────────────────────────────────────────────────────"));
        System.out.println(String.format("Current Balance: %s",
                ConsoleColors.colorize(ConsoleColors.BOLD_GREEN, String.format("$%,d", player.getChips()))));

        int bet = input.readInt(
                String.format("Place your bet ($%d - $%,d): ", MIN_BET, player.getChips()),
                MIN_BET,
                player.getChips()
        );

        player.deductChips(bet);
        Hand initialHand = new Hand(bet);
        player.addHand(initialHand);

        // 3. Initial Deal
        initialHand.addCard(deck.dealCard());
        dealer.addCard(deck.dealCard());
        initialHand.addCard(deck.dealCard());
        dealer.addCard(deck.dealCard());

        System.out.println("\nDealing cards...\n");
        pause(400);

        // 4. Show Initial Table
        displayTable(true);

        // 5. Insurance Check
        if (dealer.hasAceUp()) {
            handleInsurance(bet);
        }

        // 6. Check Dealer Natural Blackjack
        if (dealer.hasAceUp() || dealer.hasTenUp()) {
            if (dealer.isBlackjack()) {
                System.out.println(ConsoleColors.colorize(ConsoleColors.BOLD_RED, "\nDealer checks hole card... DEALER HAS BLACKJACK!"));
                displayTable(false);
                settleDealerBlackjack();
                stats.recordRound();
                stats.updateBalance(player.getChips());
                return;
            } else if (dealer.hasAceUp()) {
                System.out.println(ConsoleColors.colorize(ConsoleColors.YELLOW, "Dealer does not have Blackjack. Hand continues."));
            }
        }

        // 7. Check Player Natural Blackjack
        if (initialHand.isNaturalBlackjack()) {
            System.out.println(ConsoleColors.colorize(ConsoleColors.BOLD_GREEN, "\n★ BLACKJACK! Natural 21! ★"));
            displayTable(false);
            int winAmount = bet + (int)(bet * 1.5);
            player.addChips(winAmount);
            int profit = (int)(bet * 1.5);
            System.out.println(ConsoleColors.colorize(ConsoleColors.BOLD_GREEN,
                    String.format("You win $%,d (3:2 payout)! Total returned: $%,d", profit, winAmount)));
            stats.recordWin(true);
            stats.recordRound();
            stats.updateBalance(player.getChips());
            return;
        }

        // 8. Player Turn (supports multiple hands from splits)
        for (int i = 0; i < player.getHandCount(); i++) {
            playPlayerHand(i);
        }

        // 9. Dealer Turn
        boolean allBustedOrSurrendered = true;
        for (Hand h : player.getHands()) {
            if (h.getStatus() != HandStatus.BUSTED && h.getStatus() != HandStatus.SURRENDERED) {
                allBustedOrSurrendered = false;
                break;
            }
        }

        if (allBustedOrSurrendered) {
            System.out.println("\nAll player hands busted or surrendered.");
            displayTable(false);
        } else {
            playDealerTurn();
        }

        // 10. Settlement
        settleRound();
        stats.recordRound();
        stats.updateBalance(player.getChips());
    }

    /**
     * Handles optional insurance bet when dealer shows an Ace.
     */
    private void handleInsurance(int originalBet) {
        int maxInsurance = originalBet / 2;
        if (maxInsurance > 0 && player.hasSufficientChips(maxInsurance)) {
            System.out.println(ConsoleColors.colorize(ConsoleColors.YELLOW,
                    String.format("Dealer shows an Ace. Insurance pays 2:1 against dealer Blackjack.")));
            boolean take = input.readYesNo(
                    String.format("Take insurance for $%,d?", maxInsurance), false);
            if (take) {
                player.deductChips(maxInsurance);
                player.setInsuranceBet(maxInsurance);
                System.out.println(ConsoleColors.colorize(ConsoleColors.GREEN,
                        String.format("Insurance placed for $%,d.", maxInsurance)));
            }
        }
    }

    /**
     * Handles payouts when dealer has a natural Blackjack.
     */
    private void settleDealerBlackjack() {
        int insBet = player.getInsuranceBet();
        if (insBet > 0) {
            int insPayout = insBet * 3; // 2:1 profit + original bet
            player.addChips(insPayout);
            System.out.println(ConsoleColors.colorize(ConsoleColors.GREEN,
                    String.format("Insurance bet paid 2:1! You receive $%,d.", insPayout)));
        }

        Hand hand = player.getHand(0);
        if (hand.isNaturalBlackjack()) {
            System.out.println(ConsoleColors.colorize(ConsoleColors.YELLOW, "Both you and Dealer have Blackjack! Push (Tie)."));
            player.addChips(hand.getBet());
            stats.recordPush();
        } else {
            System.out.println(ConsoleColors.colorize(ConsoleColors.RED,
                    String.format("Dealer Blackjack beats player hand. You lose $%,d.", hand.getBet())));
            stats.recordLoss();
        }
    }

    /**
     * Plays out a single hand for the player.
     */
    private void playPlayerHand(int handIndex) {
        Hand hand = player.getHand(handIndex);

        if (player.getHandCount() > 1) {
            System.out.println(ConsoleColors.colorize(ConsoleColors.BOLD_CYAN,
                    String.format("\n════════ Playing Hand %d of %d (Bet: $%,d) ════════",
                            handIndex + 1, player.getHandCount(), hand.getBet())));
        }

        while (hand.getStatus() == HandStatus.IN_PLAY && !hand.isBust() && !hand.isTwentyOne()) {
            System.out.println("\nYour hand:");
            System.out.println(CardRenderer.renderHand(hand));
            System.out.println(String.format("Current Total: %s", formatHandTotal(hand)));

            // Build allowed choices
            Set<Character> validChoices = new HashSet<>();
            StringBuilder menu = new StringBuilder("Actions: [H] Hit | [S] Stand");
            validChoices.add('H');
            validChoices.add('S');

            boolean canDouble = hand.canDoubleDown() && player.hasSufficientChips(hand.getBet());
            if (canDouble) {
                menu.append(" | [D] Double Down");
                validChoices.add('D');
            }

            boolean canSplit = hand.canSplit() && player.hasSufficientChips(hand.getBet())
                    && player.getHandCount() < MAX_SPLIT_HANDS;
            if (canSplit) {
                menu.append(" | [P] Split");
                validChoices.add('P');
            }

            boolean canSurrender = hand.cardCount() == 2 && player.getHandCount() == 1;
            if (canSurrender) {
                menu.append(" | [R] Surrender");
                validChoices.add('R');
            }

            char choice = input.readChoice(menu + " > ", validChoices);

            switch (choice) {
                case 'H':
                    Card hitCard = deck.dealCard();
                    hand.addCard(hitCard);
                    System.out.println(String.format("You drew: %s", hitCard.getFullDisplay()));
                    if (hand.isBust()) {
                        System.out.println(ConsoleColors.colorize(ConsoleColors.BOLD_RED,
                                String.format("BUSTED! Total is %d (> 21).", hand.calculateValue())));
                    } else if (hand.isTwentyOne()) {
                        System.out.println(ConsoleColors.colorize(ConsoleColors.BOLD_GREEN, "21! Automatically standing."));
                        hand.setStatus(HandStatus.STOOD);
                    }
                    break;

                case 'S':
                    hand.setStatus(HandStatus.STOOD);
                    System.out.println(String.format("You stand with %d.", hand.calculateValue()));
                    break;

                case 'D':
                    player.deductChips(hand.getBet());
                    hand.setBet(hand.getBet() * 2);
                    hand.setStatus(HandStatus.DOUBLED);
                    Card dCard = deck.dealCard();
                    hand.addCard(dCard);
                    System.out.println(String.format("Doubled down! Bet increased to $%,d.", hand.getBet()));
                    System.out.println(String.format("You drew final card: %s", dCard.getFullDisplay()));
                    if (hand.isBust()) {
                        System.out.println(ConsoleColors.colorize(ConsoleColors.BOLD_RED,
                                String.format("BUSTED on double down! Total is %d.", hand.calculateValue())));
                    } else {
                        System.out.println(String.format("Double down final total: %d.", hand.calculateValue()));
                        hand.setStatus(HandStatus.STOOD);
                    }
                    break;

                case 'P':
                    performSplit(handIndex);
                    // Re-evaluate current hand after receiving second card
                    return;

                case 'R':
                    hand.setStatus(HandStatus.SURRENDERED);
                    System.out.println(ConsoleColors.colorize(ConsoleColors.YELLOW,
                            String.format("Surrendered hand. Half of bet ($%,d) will be returned.", hand.getBet() / 2)));
                    break;
            }
        }
    }

    /**
     * Splits a hand into two separate hands and deals an additional card to each.
     */
    private void performSplit(int handIndex) {
        Hand originalHand = player.getHand(handIndex);
        int bet = originalHand.getBet();
        player.deductChips(bet);

        Card secondCard = originalHand.removeCard(1);
        originalHand.setFromSplit(true);

        Hand newHand = new Hand(bet);
        newHand.setFromSplit(true);
        newHand.addCard(secondCard);

        player.addHand(newHand);

        // Deal one card to each split hand
        Card c1 = deck.dealCard();
        Card c2 = deck.dealCard();
        originalHand.addCard(c1);
        newHand.addCard(c2);

        System.out.println(ConsoleColors.colorize(ConsoleColors.GREEN,
                String.format("Hand split! Additional $%,d bet placed.", bet)));

        // If split Aces, casino standard: deal 1 card each and immediately stand
        if (originalHand.getCard(0).getRank() == Rank.ACE) {
            System.out.println(ConsoleColors.colorize(ConsoleColors.YELLOW,
                    "Split Aces receive exactly one card each and stand."));
            originalHand.setStatus(HandStatus.STOOD);
            newHand.setStatus(HandStatus.STOOD);
        } else {
            // Re-play this hand and subsequently the new hand
            playPlayerHand(handIndex);
        }
    }

    /**
     * Dealer draws cards according to casino rules: hits on 16 or lower, stands on 17+.
     */
    private void playDealerTurn() {
        System.out.println(ConsoleColors.colorize(ConsoleColors.BOLD_CYAN, "\n════════════════ Dealer's Turn ════════════════"));
        pause(400);

        displayTable(false);

        while (dealer.shouldHit()) {
            pause(800);
            Card card = deck.dealCard();
            dealer.addCard(card);
            System.out.println(String.format("Dealer hits and draws: %s", card.getFullDisplay()));
            System.out.println(String.format("Dealer Total: %d", dealer.getValue()));
            if (dealer.isBust()) {
                System.out.println(ConsoleColors.colorize(ConsoleColors.BOLD_GREEN, "Dealer BUSTS!"));
                break;
            }
        }

        if (!dealer.isBust()) {
            pause(500);
            System.out.println(String.format("Dealer stands with %d.", dealer.getValue()));
        }
    }

    /**
     * Evaluates payouts and records statistics for all active hands.
     */
    private void settleRound() {
        System.out.println(ConsoleColors.colorize(ConsoleColors.BOLD_WHITE, "\n════════════════ Round Results ════════════════"));

        int dealerScore = dealer.getValue();
        boolean dealerBusted = dealer.isBust();

        for (int i = 0; i < player.getHandCount(); i++) {
            Hand hand = player.getHand(i);
            String handPrefix = player.getHandCount() > 1 ? String.format("Hand %d: ", i + 1) : "";
            int bet = hand.getBet();
            int playerScore = hand.calculateValue();

            if (hand.getStatus() == HandStatus.SURRENDERED) {
                int returned = bet / 2;
                player.addChips(returned);
                System.out.println(handPrefix + ConsoleColors.colorize(ConsoleColors.YELLOW,
                        String.format("Surrendered. Forfeited $%,d (Returned $%,d).", bet - returned, returned)));
                stats.recordLoss();
            } else if (hand.isBust()) {
                System.out.println(handPrefix + ConsoleColors.colorize(ConsoleColors.RED,
                        String.format("Bust (%d) vs Dealer. Lost $%,d.", playerScore, bet)));
                stats.recordLoss();
            } else if (dealerBusted) {
                int winTotal = bet * 2;
                player.addChips(winTotal);
                System.out.println(handPrefix + ConsoleColors.colorize(ConsoleColors.BOLD_GREEN,
                        String.format("Dealer busted! You WIN $%,d! (Payout: $%,d)", bet, winTotal)));
                stats.recordWin(false);
            } else if (playerScore > dealerScore) {
                int winTotal = bet * 2;
                player.addChips(winTotal);
                System.out.println(handPrefix + ConsoleColors.colorize(ConsoleColors.BOLD_GREEN,
                        String.format("You win %d to %d! WIN $%,d! (Payout: $%,d)", playerScore, dealerScore, bet, winTotal)));
                stats.recordWin(false);
            } else if (playerScore < dealerScore) {
                System.out.println(handPrefix + ConsoleColors.colorize(ConsoleColors.RED,
                        String.format("Dealer wins %d to %d. Lost $%,d.", dealerScore, playerScore, bet)));
                stats.recordLoss();
            } else {
                player.addChips(bet);
                System.out.println(handPrefix + ConsoleColors.colorize(ConsoleColors.BOLD_YELLOW,
                        String.format("Push (Tie at %d). Bet $%,d returned.", playerScore, bet)));
                stats.recordPush();
            }
        }
        System.out.println(ConsoleColors.colorize(ConsoleColors.BOLD_WHITE, "───────────────────────────────────────────────"));
        System.out.println(String.format("Updated Balance: %s",
                ConsoleColors.colorize(ConsoleColors.BOLD_GREEN, String.format("$%,d", player.getChips()))));
    }

    /**
     * Displays current dealer and player hands in ASCII visual cards.
     */
    public void displayTable(boolean hideDealerHoleCard) {
        System.out.println(ConsoleColors.colorize(ConsoleColors.BOLD_BLUE, "── DEALER ──"));
        if (hideDealerHoleCard) {
            System.out.println(CardRenderer.renderHand(dealer.getHand(), true));
            Card up = dealer.getUpCard();
            String upScore = up != null ? String.valueOf(up.getValue()) : "?";
            System.out.println("Visible Total: " + upScore + " + ?");
        } else {
            System.out.println(CardRenderer.renderHand(dealer.getHand(), false));
            System.out.println(String.format("Dealer Total: %s", formatHandTotal(dealer.getHand())));
        }

        System.out.println(ConsoleColors.colorize(ConsoleColors.BOLD_BLUE, "\n── " + player.getName().toUpperCase() + " ──"));
        for (int i = 0; i < player.getHandCount(); i++) {
            Hand hand = player.getHand(i);
            if (player.getHandCount() > 1) {
                System.out.println(ConsoleColors.colorize(ConsoleColors.CYAN,
                        String.format("[Hand %d - Bet: $%,d - Status: %s]", i + 1, hand.getBet(), hand.getStatus())));
            } else {
                System.out.println(ConsoleColors.colorize(ConsoleColors.CYAN,
                        String.format("[Bet: $%,d - Status: %s]", hand.getBet(), hand.getStatus())));
            }
            System.out.println(CardRenderer.renderHand(hand, false));
            System.out.println(String.format("Total: %s", formatHandTotal(hand)));
        }
        System.out.println();
    }

    private String formatHandTotal(Hand hand) {
        int val = hand.calculateValue();
        if (hand.isSoft() && val < 21) {
            int lowVal = val - 10;
            return String.format("%d or %d (Soft)", lowVal, val);
        }
        return String.valueOf(val);
    }

    private void printBanner() {
        System.out.println(ConsoleColors.colorize(ConsoleColors.BOLD_CYAN,
                "╔══════════════════════════════════════════════════════════════════════╗\n" +
                "║                                                                      ║\n" +
                "║   ♠ ♥                   B L A C K J A C K  2 1               ♦ ♣     ║\n" +
                "║                                                                      ║\n" +
                "║       Standard Casino Rules  •  Split  •  Double  •  Insurance       ║\n" +
                "║                                                                      ║\n" +
                "╚══════════════════════════════════════════════════════════════════════╝\n"
        ));
    }

    private void pause(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    public Player getPlayer() {
        return player;
    }

    public Dealer getDealer() {
        return dealer;
    }

    public Deck getDeck() {
        return deck;
    }

    public GameStats getStats() {
        return stats;
    }
}
