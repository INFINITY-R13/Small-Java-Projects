# 🪨 Rock Paper Scissors (Java)

A console-based **Rock Paper Scissors** game implemented in Java using clean Object-Oriented Programming (OOP) principles. Play endless quick rounds or compete against the computer in a series tournament!

---

## 🎮 Game Rules

| Choice | Beats | Reason |
| :--- | :--- | :--- |
| **🪨 Rock** | ✂️ Scissors | *Rock crushes Scissors* |
| **📄 Paper** | 🪨 Rock | *Paper covers Rock* |
| **✂️ Scissors** | 📄 Paper | *Scissors cut Paper* |

*Identical choices result in a **Tie** (🤝).*

---

## ✨ Features

- **Multiple Game Modes**:
  - **Quick Play Mode**: Endless casual rounds until you choose to exit.
  - **Series Mode**: Tournament style! Play Best-of-3, Best-of-5, or configure a custom target number of wins.
- **Flexible & Forgiving Input**:
  - Pick by number: `1` (Rock), `2` (Paper), `3` (Scissors)
  - Pick by letter: `r` / `p` / `s` (case-insensitive)
  - Pick by name: `rock` / `paper` / `scissors`
  - In-game commands: `q` to return/quit, `stats` to check score on the fly, `help` for rules.
- **Detailed Scoreboard & Statistics**:
  - Tracks total rounds, user wins, computer wins, and ties.
  - Calculates win rate percentage.
  - Monitors current win streak and records personal best win streak.
  - Option to reset statistics at any time.
- **Clean Object-Oriented Architecture**:
  - Separated concerns: `Move` enum with win rules, `GameResult` evaluation, `ScoreBoard` metrics tracker, `ComputerPlayer` logic, and `RockPaperScissors` game loop.
- **Zero External Dependencies**:
  - Uses standard Java libraries (`java.util.Scanner`, `java.util.Random`).

---

## 📁 Project Structure

```text
Rock Paper Scissors/
├── src/
│   ├── Move.java               # Enum representing moves (ROCK, PAPER, SCISSORS) & rules
│   ├── GameResult.java         # Enum representing round outcomes (WIN, LOSE, TIE)
│   ├── ScoreBoard.java         # Score tracking, win rates, and win streaks
│   ├── ComputerPlayer.java     # Computer opponent move generator
│   └── RockPaperScissors.java  # Main application entry point & interactive UI
├── .gitignore                  # Git ignore rules for Java & common IDEs
└── README.md                   # Project documentation
```

---

## 🚀 Getting Started

### Prerequisites

- **Java Development Kit (JDK)**: Version 17 or higher (tested on JDK 17, 21, and 25).
- Verify Java installation:
  ```bash
  java -version
  javac -version
  ```

### Compile and Run

1. **Clone or navigate to the project directory**:
   ```bash
   cd "Rock Paper Scissors"
   ```

2. **Compile the source files into a `bin/` directory**:
   ```bash
   javac -d bin src/*.java
   ```

3. **Run the game**:
   ```bash
   java -cp bin RockPaperScissors
   ```

> **Tip:** You can also compile and run directly in one command:
> ```bash
> javac -d bin src/*.java && java -cp bin RockPaperScissors
> ```

---

## 🕹️ Example Gameplay

```text
======================================================
        🪨   📄   ✂️   ROCK PAPER SCISSORS   ✂️   📄   🪨
======================================================
 Welcome to the ultimate command-line Rock-Paper-Scissors!
 Test your luck and strategy against the computer.

================== MAIN MENU ==================
  1. Quick Play (Endless rounds)
  2. Series Mode (Best-of-N competition)
  3. View Scoreboard & Statistics
  4. Rules & Help
  5. Reset Scoreboard
  6. Exit
===============================================
Enter your choice: 1

--- Quick Play Mode ---
Type '1' (Rock), '2' (Paper), '3' (Scissors), or 'q' to return to menu.

--- [Round 1] ---
Choose [1] Rock 🪨, [2] Paper 📄, [3] Scissors ✂️ (or 'q' to quit): 1

  Your choice     : 🪨 Rock
  Computer choice : ✂️ Scissors
  Result          : You Won! 🎉
  Details         : Rock crushes Scissors!

[Score] You: 1 | Computer: 0 | Ties: 0 (Win Streak: 1)
```

### Career Scoreboard Sample

```text
+-----------------------------------------+
|           CAREER SCOREBOARD             |
+-----------------------------------------+
|  Total Rounds Played : 10               |
|  Your Wins           : 6                |
|  Computer Wins       : 3                |
|  Ties                : 1                |
|  Win Rate            : 60.0%            |
|  Current Win Streak  : 2                |
|  Best Win Streak     : 4                |
+-----------------------------------------+
```

---