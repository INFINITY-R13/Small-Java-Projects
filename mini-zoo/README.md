# Zoo Animals

A small console program demonstrating inheritance, abstract classes, and
interfaces in Java. The user picks an animal from a menu, fills in its
properties, and asks it to move or eat.

## Requirements

- JDK 8 or later
- A terminal (the program reads from standard input)

## Build and run

From the directory containing the source files:

```bash
javac *.java
java Main
```

To check for common mistakes while compiling:

```bash
javac -Xlint:all *.java
```

## Class structure

```
        Eat (interface)              Walk (interface)      Swim (interface)
     eatingFood()                    walking()             swimming()
     eatingCompleted()
            |
            |  implements
            v
       Animal (abstract)
       name, weight, height, age
       move()  <- abstract
       displayProperties()
            |
    +-------+-------+-----------------+
    |               |                 |
  Tiger          Dolphin           Penguin
  + Walk         + Swim            + Walk, Swim
```

| File | Role |
| --- | --- |
| `Eat.java` | Interface: `eatingFood()`, `eatingCompleted()` |
| `Walk.java` | Interface: `walking()` |
| `Swim.java` | Interface: `swimming()` |
| `Animal.java` | Abstract base class holding name, weight, height and age |
| `Tiger.java` | Walks; tracks stripes, speed, roar level |
| `Dolphin.java` | Swims; tracks colour and swimming speed |
| `Penguin.java` | Walks and swims; tracks both speeds and which one is in use |
| `Main.java` | Menu loop and all keyboard input |

`Animal` is abstract, so it is allowed to implement `Eat` while leaving
`eatingCompleted()` to its subclasses. It supplies a generic `eatingFood()`
as a fallback, though all three current animals override it.

## Using the program

Two nested menus:

**Animal choice menu** — pick 1 (Tiger), 2 (Dolphin), or 3 (Penguin).

**Animal details menu** — for the chosen animal:

1. Set properties — prompts for the species-specific fields, then weight,
   height and age
2. Display properties — prints everything currently stored
3. Display movement — the tiger walks, the dolphin swims, the penguin does
   whichever matches its current state
4. Display eating — prints the start and end of the animal's meal

After each action you are asked whether to stay with the same animal, and
after leaving an animal, whether to return to the main menu. Enter `1` for
yes or `2` for no at both prompts.

Each animal is created once at startup, so values you set persist for the
whole session and are still there when you come back to that animal.

### Example session

```
******* ZOO ANIMAL choice menu ******
1. Tiger
2. Dolphin
3. Penguin
Enter choice of animal (1-3): 1

The animal which is chosen is: Tiger
******* ANIMAL details menu for: Tiger ******
1. Set properties
2. Display properties
3. Display movement
4. Display eating
Enter choice (1-4): 1
Enter number of stripes: 100
Enter speed (mph): 35
Enter roar sound level (0-10): 9
Enter weight (kg): 220
Enter height (cm): 110
Enter age (years): 7
Continue with this animal? (1 = yes, 2 = no): 1

The animal which is chosen is: Tiger
...
Enter choice (1-4): 3
Tiger: I am moving at the speed of 35.0 mph
```

## Design notes

**Polymorphic movement.** `Animal.move()` is abstract; each subclass
delegates to its own `walking()` or `swimming()`. The penguin decides between
the two inside its own `move()`, so the menu code never tests the concrete
type to work out how an animal gets around.

**Polymorphic display.** `Animal.displayProperties()` prints the shared
fields; each subclass overrides it, calls `super.displayProperties()`, then
adds its own. This keeps the "display properties" menu option to a single
line.

**One menu loop.** `Main.runAnimalMenu(Scanner, Animal)` handles every
animal. The only place the concrete type still matters is
`promptForProperties()`, which casts in order to reach the subclass-specific
setters.

**Input handling.** All reads go through `readInt`, `readDouble`,
`readBoolean` and `readWord`. Each checks `hasNextInt()` (or the equivalent)
first, discards the offending token on a mismatch, and re-prompts, so a typo
re-asks rather than throwing `InputMismatchException`. The numeric helpers
also enforce a min and max, which is why the menu switches can rely on
getting a valid choice.

**Validation.** Setters reject negative weights, heights, ages and speeds by
throwing `IllegalArgumentException`. `setNameOfAnimal` falls back to
`"Unknown Animal"` for null or blank input and is `final`, because the
constructor calls it and an overridable call during construction would run
before the subclass finished initialising.

**Uninitialised state.** Fields default to `0` / `false` until the user sets
them. The `propertiesSet` flag on `Animal` lets the menu print a short warning
when you display or move an animal that has not been filled in yet.

## Known limitations

- Nothing is saved; all values are lost when the program exits.
- Only one instance of each species exists, and the constructor that takes a
  custom name is never used by the menu.
- Weight and height are whole numbers, so fractional values are not supported.
- Units are assumed rather than configurable (kg, cm, mph, knots).

## Extending it

To add a species: extend `Animal`, implement `Walk` and/or `Swim`, supply
`eatingFood()`, `eatingCompleted()` and `move()`, and override
`displayProperties()`. Then add it to the choice menu in `Main` and give it a
branch in `promptForProperties()`.
