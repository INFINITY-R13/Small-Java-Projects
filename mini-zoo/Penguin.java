/**
 * A penguin: walks on land and swims in water, eats fish.
 */
public class Penguin extends Animal implements Walk, Swim {

    private boolean swimming;
    private double walkSpeed;
    private double swimSpeed;

    public Penguin() {
        super("Penguin");
    }

    public Penguin(String nameOfAnimal) {
        super(nameOfAnimal);
    }

    /** Boolean getters conventionally read isX() rather than getIsX(). */
    public boolean isSwimming() {
        return swimming;
    }

    public void setSwimming(boolean swimming) {
        this.swimming = swimming;
    }

    public double getWalkSpeed() {
        return walkSpeed;
    }

    public void setWalkSpeed(double walkSpeed) {
        if (walkSpeed < 0) {
            throw new IllegalArgumentException(
                    "Walking speed cannot be negative.");
        }
        this.walkSpeed = walkSpeed;
    }

    public double getSwimSpeed() {
        return swimSpeed;
    }

    public void setSwimSpeed(double swimSpeed) {
        if (swimSpeed < 0) {
            throw new IllegalArgumentException(
                    "Swimming speed cannot be negative.");
        }
        this.swimSpeed = swimSpeed;
    }

    @Override
    public void eatingFood() {
        System.out.println("Penguin: I am eating delicious fish");
    }

    @Override
    public void eatingCompleted() {
        System.out.println("Penguin: I have eaten fish");
    }

    @Override
    public void walking() {
        System.out.println("Penguin: I am moving at the speed of "
                + walkSpeed + " mph");
    }

    @Override
    public void swimming() {
        System.out.println("Penguin: I am swimming at the speed of "
                + swimSpeed + " nautical miles per hour");
    }

    /**
     * The walk-or-swim decision belongs here, not in the menu code that used
     * to test getIsSwimming() before calling one method or the other.
     */
    @Override
    public void move() {
        if (swimming) {
            swimming();
        } else {
            walking();
        }
    }

    @Override
    public void displayProperties() {
        super.displayProperties();
        System.out.println("Currently swimming: " + swimming);
        System.out.println("Walking speed: " + walkSpeed + " mph");
        System.out.println("Swimming speed: " + swimSpeed
                + " nautical miles per hour");
    }
}
