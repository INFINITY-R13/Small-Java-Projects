# Java Calculator

A small desktop calculator built with Java Swing. It supports addition, subtraction, multiplication, division, decimal numbers, sign changes, clearing, and backspace. The expression line shows the selected operation and the completed calculation.

## Requirements

- Java Development Kit (JDK) 8 or later

## Run

Open a terminal in this directory and compile the app:

```sh
javac Calculator.java
```

Then launch it:

```sh
java Calculator
```

## Buttons

| Button | Action |
| --- | --- |
| `+` `−` `×` `÷` | Choose an arithmetic operation |
| `=` | Calculate the result |
| `.` | Enter a decimal point |
| `±` | Change the sign of the displayed number |
| `⌫` | Delete the last digit |
| `C` | Clear the calculation |

Division by zero displays `Error`.
