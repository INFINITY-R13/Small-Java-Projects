/**
 * A dolphin: swims, eats fish.
 */
public class Dolphin extends Animal implements Swim {

    private String color = "unknown";
    private double swimmingSpeed;

    public Dolphin() {
        super("Dolphin");
    }

    public Dolphin(String nameOfAnimal) {
        super(nameOfAnimal);
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        if (color == null || color.trim().isEmpty()) {
            this.color = "unknown";
        } else {
            this.color = color.trim();
        }
    }

    public double getSwimmingSpeed() {
        return swimmingSpeed;
    }

    public void setSwimmingSpeed(double swimmingSpeed) {
        if (swimmingSpeed < 0) {
            throw new IllegalArgumentException(
                    "Swimming speed cannot be negative.");
        }
        this.swimmingSpeed = swimmingSpeed;
    }

    @Override
    public void eatingFood() {
        System.out.println("Dolphin: I am eating delicious fish");
    }

    /** The original was missing the "Dolphin: " prefix the other animals use. */
    @Override
    public void eatingCompleted() {
        System.out.println("Dolphin: I have eaten fish");
    }

    @Override
    public void swimming() {
        System.out.println("Dolphin: I am swimming at the speed of "
                + swimmingSpeed + " nautical miles per hour");
    }

    @Override
    public void move() {
        swimming();
    }

    @Override
    public void displayProperties() {
        super.displayProperties();
        System.out.println("Color: " + color);
        System.out.println("Swimming speed: " + swimmingSpeed
                + " nautical miles per hour");
    }
}
