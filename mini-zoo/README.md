# Mini Zoo

A small console program demonstrating inheritance, abstract classes, interfaces,
and Java serialization. The user picks an animal from a menu, sets its properties,
and can make it move, eat, save its state to disk, or load it back.

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
     Eat (interface)          Walk (interface)     Swim (interface)
  eatingFood()                  walking()            swimming()
  eatingCompleted()
          |
          |  implements
          v
     Animal (abstract, Serializable)
     name, weight, height, age
          |
    +-----+-----+----------+
    |           |          |
  Tiger      Dolphin    Penguin
  + Walk      + Swim     + Walk, Swim
```

| File | Role |
| --- | --- |
| `Eat.java` | Interface: `eatingFood()`, `eatingCompleted()` |
| `Walk.java` | Interface: `walking()` |
| `Swim.java` | Interface: `swimming()` |
| `Animal.java` | Abstract base class; holds name, weight, height, age; implements `Serializable` |
| `Tiger.java` | Walks; tracks stripes, speed, roar sound level |
| `Dolphin.java` | Swims; tracks colour and swimming speed |
| `Penguin.java` | Walks and swims; tracks both speeds and current mode |
| `Main.java` | Menu loop, keyboard input, file save/load logic |

## Using the program

Two nested menus drive the application.

**Animal choice menu** — choose an action (1–5):

1. Tiger
2. Dolphin
3. Penguin
4. Save all animals to file
5. Display saved animals from file

**Animal details menu** — for the chosen animal (1–4):

1. Set properties — prompts for species-specific fields
2. Display properties — prints all stored values
3. Display movement — tiger walks, dolphin swims, penguin walks or swims based on its current mode
4. Display eating — prints the start and end of the animal's meal

After each action you are asked whether to stay with the same animal; after
leaving an animal you are asked whether to return to the main menu.  
Enter `1` for yes or `2` for no at both prompts.

Each animal is created once at startup, so values you set persist for the
whole session.

### Saving and loading

Option 4 serializes all three animals to `tiger.txt`, `penguin.txt`, and
`dolphin.txt` in the working directory using Java's `ObjectOutputStream`.  
Option 5 reads those files back with `ObjectInputStream` and prints each
animal's stored state.

### Example session

```
******* ZOO ANIMAL choice menu ******
1. Tiger
2. Dolphin
3. Penguin
4. Save animals to file
5. Display saved animals from file
Enter choice of animal (1-5): 1

The animal which is chosen is : Tiger
******* ANIMAL details menu for: Tiger ******
1. Set properties
2. Display properties
3. Display movement
4. Display eating
Enter choice (1-4): 1
Enter the number of Stripes:
100
Enter speed:
60
Enter decibel of roar:
9
Continue with this animal ? (Enter 1 for yes/ 2 for no): 2

Continue main Zoo menu? (Enter 1 for yes/ 2 for no): 1

******* ZOO ANIMAL choice menu ******
...
Enter choice of animal (1-5): 4
Tiger saved to tiger.txt
Penguin saved to penguin.txt
Dolphin saved to dolphin.txt
```

## Design notes

**Serialization.** `Animal` implements `java.io.Serializable` with an explicit
`serialVersionUID`. All three concrete classes also declare `implements Serializable`,
keeping the intent visible. State is written and read back using
`ObjectOutputStream` / `ObjectInputStream`.

**Polymorphism.** `Animal` implements `Eat` and provides a default `eatingFood()`.
Subclasses override it where their diet differs (Dolphin, Penguin). `eatingCompleted()`
is left abstract (declared in `Eat`) so every subclass must supply its own.

**Movement.** `Walk` and `Swim` are separate interfaces. Tiger implements only
`Walk`, Dolphin only `Swim`, and Penguin implements both. The menu checks
`penguinObject.isSwimming()` to decide which behaviour to invoke.

**Input.** All keyboard reads go directly through `Scanner`. A `keyboard.nextLine()`
call before reading the dolphin's colour string clears the leftover newline
after the previous `nextInt()`.

## Known limitations

- Weight, height, and age are set only via the constructor defaults (200, 2, 5);
  the menu does not prompt to change them.
- Only one instance of each species exists per session.
- The serialized `.txt` files contain binary data (Java object streams), not
  human-readable text.
