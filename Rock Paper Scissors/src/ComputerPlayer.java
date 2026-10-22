import java.util.Random;

public class ComputerPlayer {
    private final String name;
    private final Random random;

    public ComputerPlayer(String name) {
        this.name = (name == null || name.isBlank()) ? "Computer" : name;
        this.random = new Random();
    }

    public ComputerPlayer() {
        this("Computer");
    }

    public String getName() {
        return name;
    }

    /**
     * Randomly picks a Move (ROCK, PAPER, or SCISSORS).
     *
     * @return chosen Move
     */
    public Move makeMove() {
        Move[] moves = Move.values();
        return moves[random.nextInt(moves.length)];
    }
}
