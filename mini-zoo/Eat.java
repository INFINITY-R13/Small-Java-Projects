/**
 * Behaviour shared by every animal that eats.
 */
public interface Eat {

    /** Describes the animal starting its meal. */
    void eatingFood();

    /** Describes the animal finishing its meal. */
    void eatingCompleted();
}
