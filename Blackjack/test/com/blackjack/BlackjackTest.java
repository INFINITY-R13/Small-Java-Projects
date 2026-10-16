package com.blackjack;

import com.blackjack.game.Dealer;
import com.blackjack.game.GameStats;
import com.blackjack.model.*;

/**
 * Standalone test runner for verifying Blackjack business logic and rules.
 * Requires zero external dependencies.
 */
public class BlackjackTest {
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("Running Blackjack Test Suite...\n");

        testCardValues();
        testDeckOperations();
        testHardHandEvaluation();
        testSoftAceCalculations();
        testMultipleAces();
        testNaturalBlackjack();
        testSplitEligibility();
        testDoubleDownEligibility();
        testDealerRules();
        testGameStats();

        System.out.println("\n-------------------------------------------");
        System.out.println(String.format("Tests Passed: %d | Tests Failed: %d", testsPassed, testsFailed));
        if (testsFailed == 0) {
            System.out.println("ALL TESTS PASSED SUCCESSFULLY! ✓");
        } else {
            System.err.println("SOME TESTS FAILED! ✗");
            System.exit(1);
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        if (condition) {
            System.out.println("  [PASS] " + testName);
            testsPassed++;
        } else {
            System.err.println("  [FAIL] " + testName);
            testsFailed++;
        }
    }

    private static void assertEquals(String testName, int expected, int actual) {
        if (expected == actual) {
            System.out.println("  [PASS] " + testName + " (expected: " + expected + ", actual: " + actual + ")");
            testsPassed++;
        } else {
            System.err.println("  [FAIL] " + testName + " (expected: " + expected + ", actual: " + actual + ")");
            testsFailed++;
        }
    }

    private static void testCardValues() {
        System.out.println("Testing Card Values:");
        Card ace = new Card(Rank.ACE, Suit.SPADES);
        Card king = new Card(Rank.KING, Suit.HEARTS);
        Card ten = new Card(Rank.TEN, Suit.DIAMONDS);
        Card two = new Card(Rank.TWO, Suit.CLUBS);

        assertEquals("Ace initial value is 11", 11, ace.getValue());
        assertEquals("King value is 10", 10, king.getValue());
        assertEquals("Ten value is 10", 10, ten.getValue());
        assertEquals("Two value is 2", 2, two.getValue());
        assertTrue("Ace detected as Ace", ace.isAce());
        assertTrue("King not detected as Ace", !king.isAce());
    }

    private static void testDeckOperations() {
        System.out.println("\nTesting Deck Operations:");
        Deck singleDeck = new Deck(1);
        assertEquals("Single deck starts with 52 cards", 52, singleDeck.remainingCards());

        Deck shoe = new Deck(4);
        assertEquals("4-deck shoe starts with 208 cards", 208, shoe.remainingCards());

        Card dealt = shoe.dealCard();
        assertTrue("Dealt card is non-null", dealt != null);
        assertEquals("4-deck shoe has 207 remaining after 1 deal", 207, shoe.remainingCards());

        assertTrue("Cut card not reached initially", !shoe.isCutCardReached());
    }

    private static void testHardHandEvaluation() {
        System.out.println("\nTesting Hard Hands:");
        Hand hand = new Hand(50);
        hand.addCard(new Card(Rank.TEN, Suit.SPADES));
        hand.addCard(new Card(Rank.SEVEN, Suit.HEARTS));

        assertEquals("10 + 7 equals 17", 17, hand.calculateValue());
        assertTrue("17 is not bust", !hand.isBust());
        assertTrue("17 is not soft", !hand.isSoft());

        hand.addCard(new Card(Rank.FIVE, Suit.CLUBS));
        assertEquals("10 + 7 + 5 equals 22", 22, hand.calculateValue());
        assertTrue("22 is bust", hand.isBust());
    }

    private static void testSoftAceCalculations() {
        System.out.println("\nTesting Soft Hands with Ace:");
        Hand hand = new Hand(50);
        hand.addCard(new Card(Rank.ACE, Suit.SPADES));
        hand.addCard(new Card(Rank.SIX, Suit.HEARTS));

        assertEquals("A + 6 evaluates to 17 (soft)", 17, hand.calculateValue());
        assertTrue("A + 6 is soft", hand.isSoft());

        // Hit with an 8: 11 + 6 + 8 = 25 -> Ace becomes 1 -> 1 + 6 + 8 = 15
        hand.addCard(new Card(Rank.EIGHT, Suit.DIAMONDS));
        assertEquals("A + 6 + 8 adjusts Ace to 1, total 15", 15, hand.calculateValue());
        assertTrue("Hand is no longer soft after reduction", !hand.isSoft());
        assertTrue("Hand is not bust", !hand.isBust());
    }

    private static void testMultipleAces() {
        System.out.println("\nTesting Multiple Aces:");
        Hand hand = new Hand(50);
        hand.addCard(new Card(Rank.ACE, Suit.SPADES));
        hand.addCard(new Card(Rank.ACE, Suit.HEARTS));

        // One Ace at 11, one at 1 -> total 12
        assertEquals("A + A equals 12", 12, hand.calculateValue());
        assertTrue("A + A is soft (one Ace is 11)", hand.isSoft());

        // Add 9: 11 + 1 + 9 = 21!
        hand.addCard(new Card(Rank.NINE, Suit.CLUBS));
        assertEquals("A + A + 9 equals 21", 21, hand.calculateValue());
        assertTrue("A + A + 9 is twenty-one", hand.isTwentyOne());

        // Add 5: 1 + 1 + 9 + 5 = 16
        hand.addCard(new Card(Rank.FIVE, Suit.DIAMONDS));
        assertEquals("A + A + 9 + 5 adjusts both Aces to 1, total 16", 16, hand.calculateValue());
        assertTrue("No longer soft when both Aces are 1", !hand.isSoft());
    }

    private static void testNaturalBlackjack() {
        System.out.println("\nTesting Natural Blackjack Detection:");
        Hand naturalBj = new Hand(100);
        naturalBj.addCard(new Card(Rank.ACE, Suit.SPADES));
        naturalBj.addCard(new Card(Rank.KING, Suit.HEARTS));
        assertTrue("A + K is natural blackjack", naturalBj.isNaturalBlackjack());

        Hand threeCard21 = new Hand(100);
        threeCard21.addCard(new Card(Rank.SEVEN, Suit.SPADES));
        threeCard21.addCard(new Card(Rank.SEVEN, Suit.HEARTS));
        threeCard21.addCard(new Card(Rank.SEVEN, Suit.DIAMONDS));
        assertEquals("7 + 7 + 7 value is 21", 21, threeCard21.calculateValue());
        assertTrue("7 + 7 + 7 is NOT natural blackjack", !threeCard21.isNaturalBlackjack());

        Hand split21 = new Hand(100);
        split21.setFromSplit(true);
        split21.addCard(new Card(Rank.ACE, Suit.SPADES));
        split21.addCard(new Card(Rank.QUEEN, Suit.HEARTS));
        assertEquals("Split A + Q value is 21", 21, split21.calculateValue());
        assertTrue("Split 21 is NOT natural blackjack", !split21.isNaturalBlackjack());
    }

    private static void testSplitEligibility() {
        System.out.println("\nTesting Split Eligibility:");
        Hand pairOfEights = new Hand(50);
        pairOfEights.addCard(new Card(Rank.EIGHT, Suit.SPADES));
        pairOfEights.addCard(new Card(Rank.EIGHT, Suit.HEARTS));
        assertTrue("8 and 8 can split", pairOfEights.canSplit());

        Hand tenAndKing = new Hand(50);
        tenAndKing.addCard(new Card(Rank.TEN, Suit.SPADES));
        tenAndKing.addCard(new Card(Rank.KING, Suit.HEARTS));
        assertTrue("10 and King (both value 10) can split", tenAndKing.canSplit());

        Hand eightAndNine = new Hand(50);
        eightAndNine.addCard(new Card(Rank.EIGHT, Suit.SPADES));
        eightAndNine.addCard(new Card(Rank.NINE, Suit.HEARTS));
        assertTrue("8 and 9 cannot split", !eightAndNine.canSplit());
    }

    private static void testDoubleDownEligibility() {
        System.out.println("\nTesting Double Down Eligibility:");
        Hand hand = new Hand(50);
        hand.addCard(new Card(Rank.FIVE, Suit.SPADES));
        hand.addCard(new Card(Rank.SIX, Suit.HEARTS));
        assertTrue("2-card initial hand can double down", hand.canDoubleDown());

        hand.addCard(new Card(Rank.TWO, Suit.DIAMONDS));
        assertTrue("3-card hand cannot double down", !hand.canDoubleDown());
    }

    private static void testDealerRules() {
        System.out.println("\nTesting Dealer Rules:");
        Dealer dealer = new Dealer();
        dealer.addCard(new Card(Rank.TEN, Suit.SPADES));
        dealer.addCard(new Card(Rank.SIX, Suit.HEARTS));

        assertEquals("Dealer has 16", 16, dealer.getValue());
        assertTrue("Dealer hits on 16", dealer.shouldHit());

        dealer.clearHand();
        dealer.addCard(new Card(Rank.TEN, Suit.SPADES));
        dealer.addCard(new Card(Rank.SEVEN, Suit.HEARTS));
        assertEquals("Dealer has 17", 17, dealer.getValue());
        assertTrue("Dealer stands on 17", !dealer.shouldHit());

        dealer.clearHand();
        dealer.addCard(new Card(Rank.ACE, Suit.SPADES));
        dealer.addCard(new Card(Rank.EIGHT, Suit.HEARTS));
        assertTrue("Dealer detects Ace upcard", dealer.hasAceUp());
    }

    private static void testGameStats() {
        System.out.println("\nTesting Game Statistics:");
        GameStats stats = new GameStats(1000);
        assertEquals("Starting chips is 1000", 1000, stats.getStartingChips());
        assertEquals("Initial net profit is 0", 0, stats.getNetProfit());

        stats.recordRound();
        stats.recordWin(false);
        stats.updateBalance(1100);

        assertEquals("Net profit is +100", 100, stats.getNetProfit());
        assertEquals("Peak chips is 1100", 1100, stats.getPeakChips());
        assertTrue("Win rate is 100%", Math.abs(stats.getWinRate() - 100.0) < 0.001);

        stats.recordRound();
        stats.recordLoss();
        stats.updateBalance(1000);
        assertTrue("Win rate is 50% after 1 win and 1 loss", Math.abs(stats.getWinRate() - 50.0) < 0.001);
    }
}
