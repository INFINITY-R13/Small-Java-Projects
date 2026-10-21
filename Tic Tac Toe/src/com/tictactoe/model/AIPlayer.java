package com.tictactoe.model;

import java.util.List;
import java.util.Random;

/**
 * Represents an AI player with configurable difficulty:
 * - EASY: Completely random moves.
 * - MEDIUM: Heuristic-based (takes winning moves, blocks opponent's wins, prefers center).
 * - HARD: Perfect play via Minimax algorithm (unbeatable).
 */
public class AIPlayer implements Player {

    private final String name;
    private final char symbol;
    private final char opponentSymbol;
    private final Difficulty difficulty;
    private final Random random;

    public AIPlayer(String name, char symbol, Difficulty difficulty) {
        this.name = name;
        this.symbol = symbol;
        this.opponentSymbol = (symbol == Board.PLAYER_X) ? Board.PLAYER_O : Board.PLAYER_X;
        this.difficulty = difficulty;
        this.random = new Random();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public char getSymbol() {
        return symbol;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    @Override
    public int getMove(Board board) {
        System.out.printf("[%s (%c)] Thinking...%n", name, symbol);
        pause(400);

        int move;
        switch (difficulty) {
            case EASY:
                move = getRandomMove(board);
                break;
            case MEDIUM:
                move = getMediumMove(board);
                break;
            case HARD:
            default:
                move = getBestMove(board);
                break;
        }

        System.out.printf("[%s (%c)] chose position %d%n", name, symbol, move);
        return move;
    }

    /**
     * Selects a completely random available move.
     */
    private int getRandomMove(Board board) {
        List<Integer> available = board.getAvailableMoves();
        return available.get(random.nextInt(available.size()));
    }

    /**
     * Medium difficulty heuristic:
     * 1. If AI can win this turn, take the win.
     * 2. If opponent can win next turn, block them.
     * 3. Take center (position 5) if available (70% chance).
     * 4. Otherwise, pick randomly.
     */
    private int getMediumMove(Board board) {
        List<Integer> available = board.getAvailableMoves();

        // 1. Check for immediate win
        for (int move : available) {
            board.makeMove(move, symbol);
            boolean win = board.hasWon(symbol);
            board.undoMove(move);
            if (win) {
                return move;
            }
        }

        // 2. Block opponent's immediate win
        for (int move : available) {
            board.makeMove(move, opponentSymbol);
            boolean opponentWin = board.hasWon(opponentSymbol);
            board.undoMove(move);
            if (opponentWin) {
                return move;
            }
        }

        // 3. Take center position 5 if open
        if (board.isValidMove(5) && random.nextDouble() < 0.70) {
            return 5;
        }

        // 4. Random fallback
        return getRandomMove(board);
    }

    /**
     * Unbeatable AI using the Minimax algorithm.
     */
    private int getBestMove(Board board) {
        int bestScore = Integer.MIN_VALUE;
        int bestMove = -1;

        List<Integer> availableMoves = board.getAvailableMoves();

        // If whole board is empty, pick a corner or center quickly for variety
        if (availableMoves.size() == 9) {
            int[] openingMoves = {1, 3, 5, 7, 9};
            return openingMoves[random.nextInt(openingMoves.length)];
        }

        for (int move : availableMoves) {
            board.makeMove(move, symbol);
            int score = minimax(board, 0, false);
            board.undoMove(move);

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove != -1 ? bestMove : availableMoves.get(0);
    }

    /**
     * Minimax recursive evaluation.
     *
     * @param board current board state
     * @param depth recursion depth
     * @param isMaximizing true if maximizing player's turn (AI), false if minimizing (opponent)
     * @return heuristic score of board state
     */
    private int minimax(Board board, int depth, boolean isMaximizing) {
        if (board.hasWon(symbol)) {
            return 10 - depth;
        }
        if (board.hasWon(opponentSymbol)) {
            return depth - 10;
        }
        if (board.isFull()) {
            return 0;
        }

        List<Integer> availableMoves = board.getAvailableMoves();

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (int move : availableMoves) {
                board.makeMove(move, symbol);
                int eval = minimax(board, depth + 1, false);
                board.undoMove(move);
                maxEval = Math.max(maxEval, eval);
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int move : availableMoves) {
                board.makeMove(move, opponentSymbol);
                int eval = minimax(board, depth + 1, true);
                board.undoMove(move);
                minEval = Math.min(minEval, eval);
            }
            return minEval;
        }
    }

    private void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
