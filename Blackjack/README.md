# Blackjack 21 (Java)

A feature-rich, object-oriented console implementation of **Blackjack (Twenty-One)** built in Java with full casino rules, side-by-side ASCII card rendering, dynamic hand evaluation, and session analytics.

---

## Table of Contents

- [Features](#features)
- [Game Rules & Payouts](#game-rules--payouts)
- [Project Architecture](#project-architecture)
- [Requirements](#requirements)
- [Quick Start](#quick-start)
- [How to Play](#how-to-play)
- [Testing](#testing)
- [License](#license)

---

## Features

- **Side-by-Side ASCII Art Cards**: Beautiful visual card graphics rendered directly in your terminal, complete with suits (`♠`, `♥`, `♦`, `♣`), ranks, and face-down hidden dealer cards.
- **ANSI Terminal Colors**: Color-coded suits (red hearts/diamonds, cyan clubs/spades), alerts, and bankroll indicators. Easily disable colors via `--no-color` or `NO_COLOR=1`.
- **Authentic Casino Rules**:
  - **4-Deck Shoe**: Realistic multi-deck shoe with shuffle penetration and automatic reshuffling when the cut card is reached.
  - **Dynamic Ace Evaluation**: Aces automatically adjust between 11 and 1 (soft vs. hard hands) to give you the highest non-busting total.
  - **Hit & Stand**: Standard core decision-making.
  - **Double Down**: Double your wager on your initial two cards in exchange for exactly one more card.
  - **Split Pairs**: Split matching card values (e.g. 8-8, 10-K) into separate hands, each played independently with its own bet (supports up to 4 hands).
  - **Surrender**: Late surrender allows you to forfeit half your wager on difficult hands.
  - **Insurance**: Offered when the dealer's visible upcard is an Ace, paying 2:1 against a dealer natural Blackjack.
- **Dealer House AI**: Dealer must hit on 16 or lower and stand on all 17s.
- **Bankroll & Statistics Tracking**:
  - Chip stack tracking with configurable starting bankroll.
  - Bankruptcy protection with optional chip rebuy ($500).
  - Comprehensive post-session summary: rounds played, wins, losses, pushes, natural blackjacks, win rate, peak balance, and net profit/loss.
- **Zero External Dependencies**: Built using pure modern Java standard library (`java.base`).

---

## Game Rules & Payouts

| Outcome / Action | Condition | Payout |
| :--- | :--- | :--- |
| **Natural Blackjack** | Ace + 10-value card on initial deal | **3:2** (e.g., $100 bet wins $150 profit) |
| **Standard Win** | Player total beats Dealer total (≤ 21) or Dealer busts | **1:1** (e.g., $100 bet wins $100 profit) |
| **Push (Tie)** | Player and Dealer share the same score | **Bet returned** (net $0) |
| **Insurance** | Dealer shows Ace and has Blackjack | **2:1** on the insurance bet |
| **Surrender** | Forfeit hand before hitting | **50% of bet returned** |
| **Double Down** | Double initial bet, receive 1 final card | **1:1** on doubled bet |
| **Dealer Stand** | Dealer reaches 17 or higher | Stands on all 17s (hard or soft) |

---

## Project Architecture

```
Blackjack/
├── run.sh                              # Build and run script with JDK auto-discovery
├── .gitignore                          # Comprehensive Java / IDE ignore rules
├── README.md                           # Documentation
├── src/
│   └── com/
│       └── blackjack/
│           ├── Main.java               # Application entrypoint & CLI argument parser
│           ├── model/
│           │   ├── Card.java           # Immutable playing card (rank + suit)
│           │   ├── Rank.java           # Enum for ranks (2-10, J, Q, K, A) & values
│           │   ├── Suit.java           # Enum for suits (Hearts, Diamonds, Clubs, Spades)
│           │   ├── Deck.java           # Multi-deck shoe with shuffle & cut card logic
│           │   ├── Hand.java           # Hand math, soft Aces, splits, and blackjack checks
│           │   └── HandStatus.java     # State enum (IN_PLAY, STOOD, BUSTED, etc.)
│           ├── game/
│           │   ├── BlackjackGame.java  # Main game engine, round loop, and rules orchestration
│           │   ├── Dealer.java         # House rules AI (hit ≤ 16, stand ≥ 17)
│           │   ├── Player.java         # Player bankroll, multiple hands, and bets
│           │   ├── GameStats.java      # Session win/loss/profit metrics
│           │   └── RoundResult.java    # Hand evaluation enum with payout multipliers
│           └── ui/
│               ├── CardRenderer.java   # Side-by-side ASCII card rendering
│               ├── ConsoleColors.java  # ANSI escape code styling
│               └── InputHandler.java   # Robust console input validation
└── test/
    └── com/
        └── blackjack/
            └── BlackjackTest.java      # Standalone unit test suite (48 assertions)
```

---

## Requirements

- **Java Development Kit (JDK)**: Version 17 or newer (tested with JDK 17, 21, and 25).

---

## Quick Start

### 1. Using the Launcher Script (`run.sh`)

The included `run.sh` script automatically detects your installed JDK and handles compiling and running:

```bash
# Start the game
./run.sh

# Run unit tests
./run.sh test

# Compile only
./run.sh build

# Clean compiled classes
./run.sh clean

# Run without ANSI colors
./run.sh --no-color
```

### 2. Manual Compilation with `javac`

You can also compile and run directly using standard Java tools:

```bash
# 1. Compile source files
javac -d bin $(find src -name "*.java")

# 2. Run the game
java -cp bin com.blackjack.Main

# 3. Disable colors (optional)
java -cp bin com.blackjack.Main --no-color
```

---

## How to Play

### 1. Setup
When you launch the game, you'll be prompted for:
- Your name (press `Enter` to use `"Player"`).
- Starting chip bankroll (e.g., `$1,000`).

### 2. Betting
Before each round, enter your bet amount (minimum `$10`, maximum = current balance).

### 3. Reading the Table
Cards are displayed side-by-side:
```
── DEALER ──
┌─────────┐  ┌─────────┐
│ K       │  │ ░░░░░░░ │
│         │  │ ░░░░░░░ │
│    ♠    │  │ ░░ ? ░░ │
│         │  │ ░░░░░░░ │
│       K │  │ ░░░░░░░ │
└─────────┘  └─────────┘
Visible Total: 10 + ?

── PLAYER ──
[Bet: $50 - Status: In Play]
┌─────────┐  ┌─────────┐
│ A       │  │ 8       │
│         │  │         │
│    ♥    │  │    ♦    │
│         │  │         │
│       A │  │       8 │
└─────────┘  └─────────┘
Total: 9 or 19 (Soft)
```

### 4. Player Actions
Enter the letter corresponding to your desired move:
- **`[H]` Hit**: Draw another card. If total exceeds 21, you bust!
- **`[S]` Stand**: Keep current hand total and end your turn.
- **`[D]` Double Down**: Double your bet, receive exactly one more card, and stand.
- **`[P]` Split**: Split a matching pair into two separate hands (deducts an equal matching bet).
- **`[R]` Surrender**: Forfeit half your wager and end the hand immediately.

---

## Testing

A standalone test suite is included in `test/com/blackjack/BlackjackTest.java` that validates game mechanics without requiring external testing frameworks:

```bash
./run.sh test
```

### Test Coverage Highlights:
- **Hard Hand Arithmetic**: 10 + 7 = 17, busting totals.
- **Soft Ace Calculations**: Ace dynamically adjusting between 11 and 1 (A + 6 = 17, A + 6 + 8 = 15).
- **Multiple Aces**: A + A = 12, A + A + 9 = 21, A + A + 9 + 5 = 16.
- **Natural Blackjack Detection**: 2-card 21 vs. 3-card 21 vs. split 21.
- **Split & Double Down Eligibility Rules**.
- **Dealer Rules**: Stand on 17+, hit on ≤ 16.
- **Bankroll & Statistics Tracking**.

---

## License

This project is open-source and available under the [MIT License](LICENSE).
