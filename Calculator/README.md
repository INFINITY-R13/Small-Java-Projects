# Java Scientific Calculator

A desktop scientific calculator built with **Java Swing**.  
It goes well beyond the four basic operations — it includes trigonometric, hyperbolic, and logarithmic functions, roots and powers, memory registers, angle-mode switching, and more.

---

## Features

### Basic arithmetic
`+` `−` `×` `÷` with chained-operation support, decimal point, sign toggle, and backspace.

### Scientific & advanced mathematics

| Category | Functions |
|---|---|
| **Trigonometry** | `sin`, `cos`, `tan` |
| **Inverse trig** | `asin`, `acos`, `atan` |
| **Hyperbolic** | `sinh`, `cosh`, `tanh` |
| **Logarithms** | `log` (base 10), `ln` (natural) |
| **Roots** | `√` (square root), `∛x` (cube root) |
| **Powers** | `x²`, `x³`, `xʸ` (arbitrary power) |
| **Other unary** | `1/x`, `n!` (factorial 0–170), `\|x\|` (absolute value) |
| **Rounding** | `⌊x⌋` (floor), `⌈x⌉` (ceiling) |
| **Modulo** | `mod` (binary, like `÷`) |
| **Constants** | `π`, `e` |
| **Angle mode** | `DEG / RAD` toggle (affects sin/cos/tan and their inverses) |

### Memory
`MS` (store) · `MR` (recall) · `M+` (add) · `M−` (subtract) · `MC` (clear)  
A small `M=` indicator appears on-screen when memory holds a non-zero value.

### Display
- Expression line shows the current operation or last completed calculation.
- Automatic **scientific notation** for very large (`≥ 10¹⁵`) or very small (`< 10⁻⁹`) results.
- Descriptive error messages (e.g., `Math Error`, `Undefined`) for out-of-domain inputs.
- Last 10 calculations kept in an internal history (foundation for a future history panel).

---

## Requirements

- Java Development Kit (JDK) **11 or later** (switch expressions used)

---

## Build & Run

Open a terminal in this directory and compile:

```sh
javac Calculator.java
```

Then run:

```sh
java Calculator
```

---

## Button Reference

### Utility row
| Button | Action |
|---|---|
| `C` | Clear everything |
| `⌫` | Delete last digit |
| `±` | Toggle sign |
| `DEG/RAD` | Switch angle mode |

### Memory row
| Button | Action |
|---|---|
| `MC` | Clear memory |
| `MR` | Recall memory to display |
| `MS` | Store display value to memory |
| `M+` | Add display value to memory |
| `M−` | Subtract display value from memory |

### Scientific functions
All unary functions operate immediately on the displayed value.  
`xʸ` and `mod` are binary — press the button, enter the second number, then press `=`.

---

## Error Handling

| Condition | Display |
|---|---|
| Division by zero | `Error` / `Undefined` |
| √ of a negative number | `Math Error` |
| log / ln of zero or negative | `Math Error` |
| tan at 90° / 270° … | `Math Error` |
| asin / acos out of `[−1, 1]` | `Math Error` |
| Factorial of negative / non-integer / > 170 | `Math Error` |
