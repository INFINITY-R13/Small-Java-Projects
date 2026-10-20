# 🎲 Java Dice Roller

A modular, feature-rich terminal dice roller written in Java. Supports standard 6-sided dice with ASCII art rendering, custom polyhedral RPG dice (`d4`, `d8`, `d10`, `d12`, `d20`, `d100`, or any $N$-sided die), standard dice notation expressions (`NdS+/-M`), roll history, and session statistics.

---

## ✨ Features

- **🎲 Standard 6-Sided Dice**: Roll up to 20 dice at once with horizontal ASCII face rendering.
- **🔮 Polyhedral & Custom Dice**: Roll any die type—whether standard tabletop RPG dice (`d4`, `d6`, `d8`, `d10`, `d12`, `d20`, `d100`) or any custom number of sides (2 to 10,000).
- **📜 Dice Notation Formula Parser**: Parse standard RPG dice expressions like `2d6+3`, `1d20-1`, `3d8+4`, or `d100`.
- **📊 Session Statistics & Roll History**: View aggregate statistics (total rolls, total dice rolled, highest/lowest rolls, grand sum, average) and a history table of recent rolls.
- **🖥️ Dual Execution Modes**:
  - **Interactive Terminal Menu**: Easy-to-use menu with prompts and options.
  - **Direct CLI Mode**: Pass one or more dice formulas as arguments for instant results (e.g., `java -cp bin com.diceroller.Main 2d6+3 1d20`).
- **🛡️ Robust Input Validation**: Gracefully handles invalid inputs, empty lines, and out-of-range values without crashing.
- **🧪 Built-in Test Suite**: 189 unit and smoke tests covering core rolling logic, boundary conditions, notation parsing, and formatting without external dependencies.

---

## 📁 Project Structure

```text
Dice Roller/
├── .gitignore                      # Git ignore file for Java, IDEs, and OS metadata
├── README.md                       # Comprehensive project documentation
├── run.sh                          # Helper script to compile and run on macOS/Linux
├── src/
│   └── com/
│       └── diceroller/
│           ├── Die.java            # Represents an individual die with N sides
│           ├── DiceAsciiArt.java   # Terminal ASCII / box-drawing renderer
│           ├── DiceNotationParser.java # Parser for expressions like '2d6+3'
│           ├── DiceRollResult.java # Immutable model holding roll outcomes & stats
│           ├── RollHistory.java    # Session history and aggregate statistics
│           ├── DiceRollerTest.java # Standalone test suite (189 test assertions)
│           └── Main.java           # Main entry point (CLI & interactive menu)
└── bin/                            # Compiled bytecode (.class files, gitignored)
```

---

## 🚀 Quick Start

### Prerequisites
- **JDK 11+** (JDK 17, 21, or 25 recommended). Verify with:
  ```bash
  javac -version
  java -version
  ```

> **Note for macOS users**: If you see `Unable to locate a Java Runtime`, ensure `JAVA_HOME` is exported (the provided `./run.sh` script detects this automatically).

---

### Option 1: Using the Helper Script (`run.sh`)

The script automatically detects your JDK, compiles the code into `bin/`, and launches the program.

```bash
# Make executable (if not already)
chmod +x run.sh

# Launch Interactive Menu
./run.sh

# Or roll directly via CLI arguments
./run.sh 2d6+3 1d20
```

---

### Option 2: Standard `javac` and `java`

#### 1. Compile the source code:
```bash
javac -d bin src/com/diceroller/*.java
```

#### 2. Run in Interactive Mode:
```bash
java -cp bin com.diceroller.Main
```

#### 3. Run in Direct CLI Mode:
```bash
# Roll 2 six-sided dice with a +3 modifier and 1 twenty-sided die:
java -cp bin com.diceroller.Main 2d6+3 1d20
```

---

## 🧪 Running the Test Suite

A standalone test suite is included to verify rolling logic, boundary conditions, and notation parsing:

```bash
# Compile and run tests
javac -d bin src/com/diceroller/*.java
java -cp bin com.diceroller.DiceRollerTest
```

Expected output:
```text
🧪 Running Dice Roller Test Suite...

  [PASS] Default die has 6 sides
  [PASS] Roll within range 1..6
  ...
----------------------------------------
Test Results: 189 Passed, 0 Failed
----------------------------------------
```

---

## 🎮 Usage Examples

### 1. Rolling Standard 6-Sided Dice (with ASCII Art)

```text
Rolling...
==================================================
Result: 3d6
Rolls : [3, 5, 2]
TOTAL : 10
Stats : Min = 2 | Max = 5 | Avg = 3.33
==================================================

┌───────┐  ┌───────┐  ┌───────┐
│ *     │  │ *   * │  │ *     │
│   *   │  │   *   │  │       │
│     * │  │ *   * │  │     * │
└───────┘  └───────┘  └───────┘
```

### 2. Rolling Polyhedral Dice (e.g., d20)

```text
Rolling...
==================================================
Result: 2d20
Rolls : [18, 9]
TOTAL : 27
Stats : Min = 9 | Max = 18 | Avg = 13.50
==================================================

┌───────┐  ┌───────┐
│  d20  │  │  d20  │
│ [18]  │  │  [9]  │
│       │  │       │
└───────┘  └───────┘
```

### 3. Rolling Dice Notation (`3d8+4`)

```text
Rolling...
==================================================
Result: 3d8+4
Rolls : [7, 3, 6]
Sum of dice: 16 | Modifier: +4
TOTAL : 20
Stats : Min = 3 | Max = 7 | Avg = 5.33
==================================================
```

### 4. Viewing Session Statistics & Roll History

```text
==================================================
           📊 SESSION STATISTICS 📊
==================================================
  Total Roll Commands : 4
  Total Dice Rolled   : 9
  Highest Roll Total  : 27
  Lowest Roll Total   : 10
  Grand Total Sum     : 71
  Average Roll Total  : 17.75
==================================================

==================================================================
 #    | Time     | Formula      | Rolls                    | Total 
------------------------------------------------------------------
 1    | 09:28:44 | 3d6          | [3, 5, 2]                | 10    
 2    | 09:28:44 | 2d20         | [18, 9]                  | 27    
 3    | 09:28:44 | 3d8+4        | [7, 3, 6]                | 20    
 4    | 09:28:44 | 1d12         | [14]                     | 14    
==================================================================
```

---

## 🏗️ Architecture & Design

- **`Die`**: Encapsulates a single die with configurable sides (minimum 2 sides) and random generation powered by `ThreadLocalRandom`.
- **`DiceRollResult`**: Immutable value object storing roll expressions, individual rolls, modifiers, sums, min/max/average, and timestamp.
- **`DiceNotationParser`**: Regular expression-based parser supporting expressions matching `(?i)^(\d+)?d(\d+)(?:([+-])(\d+))?$`.
- **`DiceAsciiArt`**: Pure terminal visualizer that arranges 5-line box dice horizontally with automatic row wrapping for clean display.
- **`RollHistory`**: Thread-safe manager for session history, recent roll table formatting, and cumulative metrics.
- **`Main`**: Unified entry point supporting both an interactive command-line interface and direct command-line arguments.

---