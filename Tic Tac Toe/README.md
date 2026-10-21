# Tic Tac Toe (Java)

A console-based Tic Tac Toe game written in Java featuring an Object-Oriented design, multiple game modes, and an unbeatable AI powered by the Minimax algorithm.

---

## Features

- **Multiple Game Modes**:
  1. **Two Player (Pass & Play)**: Play locally with a friend on the same terminal.
  2. **Single Player (vs AI Computer)**: Play against the computer with configurable difficulty.
  3. **AI vs AI (Spectator Demo)**: Watch two computer bots compete against each other.
- **AI Difficulty Levels**:
  - **Easy**: Completely random moves.
  - **Medium**: Tactical play (seizes immediate wins, blocks opponent winning moves, and targets the center).
  - **Hard**: Unbeatable AI using the **Minimax** recursive decision algorithm.
- **Intuitive Board & Keypad Guide**:
  - Displays the current board alongside an interactive 1–9 numeric guide for fast, error-free input.
- **Robust Input Validation**:
  - Handles invalid numbers, non-numeric strings, and occupied cell attempts with informative prompts.
- **Session Scoreboard**:
  - Tracks Player 1 wins, Player 2/AI wins, draws, and total matches played across rounds.
- **Clean Object-Oriented Architecture**:
  - Clear separation of concerns between game logic, board state, player models, and user interface.

---

## Board Layout & Controls

The board uses a standard 1–9 keypad mapping:

```text
     Current Board             Position Guide
       X |   | O                 1 | 2 | 3
      ---+---+---               ---+---+---
         | X |                   4 | 5 | 6
      ---+---+---               ---+---+---
       O |   |                   7 | 8 | 9
```

When it is your turn, enter any available position number from `1` to `9` and press `Enter`.

---

## Project Structure

```text
Tic Tac Toe/
├── .gitignore
├── README.md
└── src/
    └── com/
        └── tictactoe/
            ├── Main.java              # Application entry point & main menu
            ├── game/
            │   ├── GameScore.java      # Scoreboard tracking (wins, losses, ties)
            │   └── TicTacToeGame.java  # Match loop, turn cycling & win evaluation
            └── model/
                ├── Board.java          # 3x3 grid state, validation, win checks
                ├── Difficulty.java     # AI difficulty enum (EASY, MEDIUM, HARD)
                ├── Player.java         # Player interface
                ├── HumanPlayer.java    # Human console input implementation
                └── AIPlayer.java       # AI player with Minimax algorithm
```

---

## Prerequisites

- **Java Development Kit (JDK)**: Version 8 or newer (JDK 17, 21, or 25 recommended).
- Check your installed version:
  ```bash
  javac -version
  java -version
  ```

---

## How to Compile & Run

### 1. Compile

From the root project directory:

```bash
# macOS / Linux
javac -d bin $(find src -name "*.java")

# Windows (Command Prompt)
dir /s /b src\*.java > sources.txt
javac -d bin @sources.txt
del sources.txt

# Windows (PowerShell)
javac -d bin (Get-ChildItem -Recurse -Filter *.java src | Select-Object -ExpandProperty FullName)
```

### 2. Run

```bash
java -cp bin com.tictactoe.Main
```

### Quick One-Liner (Compile and Run)

```bash
javac -d bin $(find src -name "*.java") && java -cp bin com.tictactoe.Main
```

---

## Rules of Tic Tac Toe

1. The game is played on a 3x3 grid.
2. Player 1 is assigned **`X`** and Player 2 (or AI) is assigned **`O`**.
3. Players take turns placing their mark in an empty square (1–9).
4. The first player to align 3 of their marks horizontally, vertically, or diagonally wins.
5. If all 9 squares are filled without 3 in a row, the match ends in a draw.

---

## Object-Oriented Design Highlights

- **`Player` Interface**: Abstracts player actions via `int getMove(Board board)`. Both `HumanPlayer` and `AIPlayer` implement this interface, allowing the game loop to interact with any player type polymorphically.
- **`Board` Encapsulation**: Manages internal cell state, move application, undo operations (used for AI lookahead), and win/draw detection.
- **Minimax Algorithm**:
  - The `AIPlayer` evaluates board states recursively.
  - Scores terminal states with depth penalties (`+10 - depth` for AI win, `-10 + depth` for loss, `0` for draw) to ensure optimal and fastest possible winning paths.
- **Extensibility**: Easy to add new game modes (e.g. timed moves, custom board sizes, or a GUI).

---

## License

This project is open-source and free to use for educational and personal projects.
