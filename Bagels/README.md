# 🥯 Bagels - Deductive Logic Game in Java

A clean, modern, and robust Java implementation of **Bagels**, the classic deductive number-guessing game popularized in books such as *The Big Book of Small Python Projects* by Al Sweigart and 1970s mainframe logic games.

---

## 🎯 How to Play

The computer generates a secret number of unique, non-repeating digits (default: 3 digits). Your goal is to deduce the secret number within a limited number of guesses (default: 10 guesses).

### Clues Reference

After each valid guess, the game gives you clues:

| Clue | Meaning |
| :--- | :--- |
| **`Fermi`** | One digit is **correct** and in the **correct position**. |
| **`Pico`** | One digit is **correct** but in the **wrong position**. |
| **`Bagels`** | **No digits** are correct. |

> [!NOTE]
> **Why are clues sorted alphabetically?**  
> Clues are always sorted alphabetically (`Fermi` before `Pico`). This ensures that the ordering of the clues does **not** give away which digit corresponds to which clue!

### Example

Suppose the secret number is **`248`**:

- If your guess is **`843`**:
  - `4` is in position 2 (same as secret) $\rightarrow$ **`Fermi`**
  - `8` is in position 1 (secret has it at position 3) $\rightarrow$ **`Pico`**
  - `3` is not in the secret $\rightarrow$ no clue
  - **Returned Clues**: `Fermi Pico`
- If your guess is **`135`**:
  - None of `1`, `3`, or `5` are in `248`
  - **Returned Clues**: `Bagels`
- If your guess is **`482`**:
  - All three digits are present but in wrong positions
  - **Returned Clues**: `Pico Pico Pico`
- If your guess is **`248`**:
  - Exact match! $\rightarrow$ **`🎉 YOU GOT IT!`**

---

## ✨ Features

- **Classic Gameplay**: Faithfully adheres to the original Bagels rules with accurate clue resolution and alphabetical clue sorting.
- **Difficulty Modes**:
  - **Easy**: 3 digits, 12 guesses (great for beginners)
  - **Normal** *(Classic)*: 3 digits, 10 guesses
  - **Hard**: 4 digits, 10 guesses
  - **Expert**: 5 digits, 12 guesses
  - **Custom**: Configure any number of digits (1–10) and guesses (1–50)
- **Input Validation**: Rejects invalid guesses (incorrect length, non-numeric characters, or duplicate digits) with friendly feedback **without penalizing or consuming your guess count**.
- **In-Game Commands**:
  - `help` / `rules`: View the rules anytime mid-game.
  - `stats`: View your session stats without burning a turn.
  - `quit` / `exit`: Forfeit a round with confirmation (reveals the secret).
- **Session Statistics**: Tracks games played, won, lost, win percentage, current streak, best streak, and fewest guesses.
- **Terminal Styling**: ANSI colored output for clues (Green = Fermi, Yellow = Pico, Red = Bagels). Automatically respects `NO_COLOR` and can be toggled in the menu.
- **Unit Tested**: Comes with a self-contained test suite covering 107 test assertions with zero external dependencies.

---

## 🚀 Getting Started

### Prerequisites

- Java 17+ (JDK 25 recommended, or any modern JDK).

### Quick Run (macOS / Linux)

Run the included launch script directly from the project directory:

```bash
./run.sh
```

### Manual Compilation & Run

If you prefer using standard `javac` and `java` commands:

```bash
# 1. Compile source files to bin directory
mkdir -p bin
javac -d bin src/bagels/*.java

# 2. Run the game
java -cp bin bagels.Bagels
```

---

## 🧪 Running Unit Tests

A comprehensive test suite verifies secret generation, validation, edge cases (such as duplicate digit handling in guesses), difficulty bounds, and session stats:

```bash
./test.sh
```

Or manually:

```bash
javac -d bin src/bagels/*.java test/bagels/*.java
java -cp bin bagels.BagelsTest
```

---

## 📁 Project Architecture

```
Bagels/
├── src/
│   └── bagels/
│       ├── Bagels.java        # Interactive CLI runner, menus, and input handling
│       ├── BagelsGame.java    # Pure game logic (secret generator, clue evaluator, validation)
│       ├── Difficulty.java    # Difficulty record and presets (Easy, Normal, Hard, Expert, Custom)
│       ├── GameStats.java     # Session score and streak tracking
│       └── Ansi.java          # Terminal formatting and clue coloring
├── test/
│   └── bagels/
│       └── BagelsTest.java    # Self-contained unit test suite (107 test assertions)
├── run.sh                     # Launch script
├── test.sh                    # Test runner script
└── README.md                  # Documentation and game rules
```

---

## 📜 License

This project is created for learning and personal exploration. Feel free to modify, extend, and share!
