import java.util.Scanner;

/**
 * Console menu for the zoo.
 *
 * All keyboard reading goes through the read* helpers at the bottom of this
 * class, so bad input re-prompts instead of throwing InputMismatchException.
 */
public class Main {

    private static final int YES = 1;

    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);

        Tiger tigerObject = new Tiger();
        Dolphin dolphinObject = new Dolphin();
        Penguin penguinObject = new Penguin();

        int continueOuterLoop;

        do {
            Animal chosenAnimal;
            switch (animalChoiceMenu(keyboard)) {
                case 1:
                    chosenAnimal = tigerObject;
                    break;
                case 2:
                    chosenAnimal = dolphinObject;
                    break;
                default:
                    chosenAnimal = penguinObject;
                    break;
            }

            runAnimalMenu(keyboard, chosenAnimal);

            continueOuterLoop = readInt(keyboard,
                    "Continue main Zoo menu? (1 = yes, 2 = no): ", 1, 2);

        } while (continueOuterLoop == YES);

        System.out.println("Goodbye.");
        keyboard.close();
    }

    /**
     * Runs the details menu for one animal. Takes an Animal rather than a
     * concrete type, which is what collapses the three near-identical blocks
     * of the original into a single loop.
     */
    private static void runAnimalMenu(Scanner keyboard, Animal animal) {
        int continueInnerLoop;

        do {
            System.out.println();
            System.out.println("The animal which is chosen is: "
                    + animal.getNameOfAnimal());

            switch (animalDetailsManipulationMenu(keyboard, animal)) {
                case 1:
                    promptForProperties(keyboard, animal);
                    break;

                case 2:
                    warnIfNotConfigured(animal);
                    animal.displayProperties();
                    break;

                case 3:
                    warnIfNotConfigured(animal);
                    animal.move();
                    break;

                case 4:
                    animal.eatingFood();
                    animal.eatingCompleted();
                    break;

                default:
                    // Unreachable: the menu helper only returns 1-4.
                    System.out.println("Invalid choice");
                    break;
            }

            continueInnerLoop = readInt(keyboard,
                    "Continue with this animal? (1 = yes, 2 = no): ", 1, 2);

        } while (continueInnerLoop == YES);
    }

    private static void warnIfNotConfigured(Animal animal) {
        if (!animal.arePropertiesSet()) {
            System.out.println(
                    "(Properties have not been set yet - showing defaults.)");
        }
    }

    /**
     * Reads the species-specific fields, then the common ones.
     *
     * The casts are the one place the concrete type still matters: the fields
     * being filled in only exist on the subclass.
     */
    private static void promptForProperties(Scanner keyboard, Animal animal) {
        if (animal instanceof Tiger) {
            Tiger tiger = (Tiger) animal;
            tiger.setNumberOfStripes(
                    readInt(keyboard, "Enter number of stripes: ", 0, 1000));
            tiger.setSpeed(
                    readDouble(keyboard, "Enter speed (mph): ", 0, 200));
            tiger.setRoarSoundLevel(
                    readInt(keyboard, "Enter roar sound level (0-10): ", 0, 10));

        } else if (animal instanceof Dolphin) {
            Dolphin dolphin = (Dolphin) animal;
            dolphin.setColor(readWord(keyboard, "Enter color: "));
            dolphin.setSwimmingSpeed(readDouble(keyboard,
                    "Enter swimming speed (nautical mph): ", 0, 100));

        } else if (animal instanceof Penguin) {
            Penguin penguin = (Penguin) animal;
            penguin.setSwimming(readBoolean(keyboard,
                    "Is the penguin swimming? (true/false): "));
            penguin.setWalkSpeed(readDouble(keyboard,
                    "Enter walking speed (mph): ", 0, 100));
            penguin.setSwimSpeed(readDouble(keyboard,
                    "Enter swimming speed (nautical mph): ", 0, 100));
        }

        promptForCommonProperties(keyboard, animal);
        animal.markPropertiesSet();
    }

    private static void promptForCommonProperties(
            Scanner keyboard, Animal animal) {

        animal.setWeight(readInt(keyboard, "Enter weight (kg): ", 0, 10000));
        animal.setHeight(readInt(keyboard, "Enter height (cm): ", 0, 1000));
        animal.setAge(readInt(keyboard, "Enter age (years): ", 0, 200));
    }

    private static int animalChoiceMenu(Scanner keyboard) {
        System.out.println();
        System.out.println("******* ZOO ANIMAL choice menu ******");
        System.out.println("1. Tiger");
        System.out.println("2. Dolphin");
        System.out.println("3. Penguin");

        return readInt(keyboard, "Enter choice of animal (1-3): ", 1, 3);
    }

    private static int animalDetailsManipulationMenu(
            Scanner keyboard, Animal animal) {

        System.out.println("******* ANIMAL details menu for: "
                + animal.getNameOfAnimal() + " ******");
        System.out.println("1. Set properties");
        System.out.println("2. Display properties");
        System.out.println("3. Display movement");
        System.out.println("4. Display eating");

        return readInt(keyboard, "Enter choice (1-4): ", 1, 4);
    }

    // ---------------------------------------------------------------
    // Input helpers: re-prompt on bad input instead of throwing.
    // ---------------------------------------------------------------

    private static int readInt(
            Scanner keyboard, String prompt, int min, int max) {

        while (true) {
            System.out.print(prompt);
            requireInput(keyboard);

            if (keyboard.hasNextInt()) {
                int value = keyboard.nextInt();
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Please enter a whole number between "
                        + min + " and " + max + ".");
            } else {
                System.out.println("That is not a whole number.");
                keyboard.next(); // discard the bad token so we do not loop forever
            }
        }
    }

    private static double readDouble(
            Scanner keyboard, String prompt, double min, double max) {

        while (true) {
            System.out.print(prompt);
            requireInput(keyboard);

            if (keyboard.hasNextDouble()) {
                double value = keyboard.nextDouble();
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Please enter a number between "
                        + min + " and " + max + ".");
            } else {
                System.out.println("That is not a number.");
                keyboard.next();
            }
        }
    }

    private static boolean readBoolean(Scanner keyboard, String prompt) {
        while (true) {
            System.out.print(prompt);
            requireInput(keyboard);

            if (keyboard.hasNextBoolean()) {
                return keyboard.nextBoolean();
            }
            System.out.println("Please enter true or false.");
            keyboard.next();
        }
    }

    private static String readWord(Scanner keyboard, String prompt) {
        System.out.print(prompt);
        requireInput(keyboard);
        return keyboard.next();
    }

    /**
     * Guards against the input stream running out (end of file, or a piped
     * input file that is shorter than the program expects). Without this the
     * helpers above would throw NoSuchElementException.
     */
    private static void requireInput(Scanner keyboard) {
        if (!keyboard.hasNext()) {
            System.out.println();
            System.out.println("No more input available. Exiting.");
            keyboard.close();
            System.exit(0);
        }
    }
}
