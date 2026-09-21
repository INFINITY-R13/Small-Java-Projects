/**
 * Base type for every animal in the zoo.
 *
 * Animal is abstract, so it is allowed to leave eatingCompleted() from the
 * Eat interface unimplemented: each concrete subclass must supply it.
 */
public abstract class Animal implements Eat {

    private String nameOfAnimal;
    private int weight;
    private int height;
    private int age;

    /**
     * True once the user has filled in this animal's properties. Used by the
     * menu to warn that displayed values are still defaults.
     */
    private boolean propertiesSet;

    protected Animal() {
        this("Unknown Animal");
    }

    protected Animal(String nameOfAnimal) {
        setNameOfAnimal(nameOfAnimal);
    }

    public String getNameOfAnimal() {
        return nameOfAnimal;
    }

    /**
     * Final because it is called from the constructor: an overridable method
     * called during construction would run before the subclass is initialised.
     */
    public final void setNameOfAnimal(String nameOfAnimal) {
        if (nameOfAnimal == null || nameOfAnimal.trim().isEmpty()) {
            this.nameOfAnimal = "Unknown Animal";
        } else {
            this.nameOfAnimal = nameOfAnimal.trim();
        }
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Weight cannot be negative.");
        }
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        if (height < 0) {
            throw new IllegalArgumentException("Height cannot be negative.");
        }
        this.height = height;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative.");
        }
        this.age = age;
    }

    public boolean arePropertiesSet() {
        return propertiesSet;
    }

    public void markPropertiesSet() {
        this.propertiesSet = true;
    }

    /**
     * Moves the animal in whatever way is natural for it. Each subclass
     * delegates to its own walking() or swimming() method, so the caller never
     * has to test the concrete type.
     */
    public abstract void move();

    /**
     * Prints the properties shared by all animals. Subclasses override this,
     * call super.displayProperties(), then add their own fields.
     */
    public void displayProperties() {
        System.out.println("Name: " + nameOfAnimal);
        System.out.println("Weight: " + weight + " kg");
        System.out.println("Height: " + height + " cm");
        System.out.println("Age: " + age + " years");
    }

    /**
     * Fallback description of eating. Every current subclass overrides this
     * with something species-specific.
     */
    @Override
    public void eatingFood() {
        System.out.println("The animal: " + nameOfAnimal + " is eating.");
    }
}
