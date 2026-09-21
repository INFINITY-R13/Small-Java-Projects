/**
 * A tiger: walks on land, eats meat.
 */
public class Tiger extends Animal implements Walk {

    private int numberOfStripes;
    private double speed;
    private int roarSoundLevel;

    public Tiger() {
        super("Tiger");
    }

    public Tiger(String nameOfAnimal) {
        super(nameOfAnimal);
    }

    public int getNumberOfStripes() {
        return numberOfStripes;
    }

    public void setNumberOfStripes(int numberOfStripes) {
        if (numberOfStripes < 0) {
            throw new IllegalArgumentException(
                    "Number of stripes cannot be negative.");
        }
        this.numberOfStripes = numberOfStripes;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        if (speed < 0) {
            throw new IllegalArgumentException("Speed cannot be negative.");
        }
        this.speed = speed;
    }

    public int getRoarSoundLevel() {
        return roarSoundLevel;
    }

    public void setRoarSoundLevel(int roarSoundLevel) {
        if (roarSoundLevel < 0) {
            throw new IllegalArgumentException(
                    "Roar sound level cannot be negative.");
        }
        this.roarSoundLevel = roarSoundLevel;
    }

    /**
     * Was missing in the original, so a tiger fell back to Animal's generic
     * "The animal: Tiger is eating." message.
     */
    @Override
    public void eatingFood() {
        System.out.println("Tiger: I am eating delicious meat");
    }

    @Override
    public void eatingCompleted() {
        System.out.println("Tiger: I have eaten meat");
    }

    @Override
    public void walking() {
        System.out.println("Tiger: I am moving at the speed of "
                + speed + " mph");
    }

    @Override
    public void move() {
        walking();
    }

    @Override
    public void displayProperties() {
        super.displayProperties();
        System.out.println("Number of stripes: " + numberOfStripes);
        System.out.println("Speed: " + speed + " mph");
        System.out.println("Roar sound level: " + roarSoundLevel);
    }
}
