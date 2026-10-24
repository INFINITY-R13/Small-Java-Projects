# 🔢 Guess The Number

A simple, colourful terminal-based number guessing game written in plain Java (no external dependencies).

## Features

- 🎯 Random secret number between **1 and 100**
- ⚠️ **7 attempts** per round with "Too high / Too low" hints
- 🔄 **Multi-round** support — play as many rounds as you like
- 📊 **Final stats** — rounds played, won, lost, and win rate
- 🎨 ANSI colour output for a polished terminal experience
- ✅ Robust input validation (handles non-numeric and out-of-range input)

## How to Play

1. The computer picks a secret number between 1 and 100.
2. You have **7 guesses** to find it.
3. After each wrong guess you'll be told if your guess was **too high** or **too low**.
4. Win by guessing correctly before running out of attempts!

## Getting Started

### Prerequisites

- Java 8 or later (just the JDK)

### Compile & Run

```bash
# Navigate to the project folder
cd "Guess The Number"

# Compile
javac GuessTheNumber.java

# Run
java GuessTheNumber
```

### One-liner (compile + run)

```bash
javac GuessTheNumber.java && java GuessTheNumber
```

## Project Structure

```
Guess The Number/
├── GuessTheNumber.java   # Main source file
├── .gitignore            # Ignores .class files, IDE folders, etc.
└── README.md             # This file
```

## Configuration

All game parameters live at the top of `GuessTheNumber.java` as constants — easy to tweak:

| Constant       | Default | Description                    |
|----------------|---------|--------------------------------|
| `MIN_NUMBER`   | `1`     | Lower bound of the secret range |
| `MAX_NUMBER`   | `100`   | Upper bound of the secret range |
| `MAX_ATTEMPTS` | `7`     | Maximum guesses allowed per round |

## Sample Session

```
╔══════════════════════════════════════╗
║       🔢  GUESS  THE  NUMBER  🔢      ║
╚══════════════════════════════════════╝

🎯  Round 1 — Guess a number between 1 and 100.
     You have 7 attempts.

  [Attempt 1/7] Enter your guess: 50
     ↑  Too low!  (6 attempts left)
  [Attempt 2/7] Enter your guess: 75
     ↓  Too high! (5 attempts left)
  [Attempt 3/7] Enter your guess: 62
🎉  Correct! The number was 62. You got it in 3 attempts!

Play again? [y / n]: n

── Final Stats ────────────────────────
   Rounds played : 1
   Rounds won    : 1
   Rounds lost   : 0
   Win rate      : 100%
───────────────────────────────────────
  Thanks for playing! 👋
```