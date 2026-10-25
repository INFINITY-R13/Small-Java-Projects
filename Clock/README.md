# 🕐 Digital Clock

A minimal, good-looking **digital clock** built with **Java Swing**.  
Displays the current time in 12-hour format (hh:mm:ss AM/PM) and the full date, updating every second.

---

## Preview

```
╔══════════════════════════════════╗
║       08:45:32  PM               ║
║  ────────────────────────────    ║
║  Sunday, October 25, 2026        ║
╚══════════════════════════════════╝
```

> Dark background · teal time display · monospaced font

---

## Features

- ⏱ Live clock — updates every **1 second** via `java.util.Timer`  
- 🎨 Dark, modern UI (no external libraries — pure Swing)  
- 📅 Shows full weekday + date below the time  
- 🔡 12-hour format with AM / PM indicator  
- 💻 Cross-platform — runs anywhere Java 8+ is available  

---

## Project Structure

```
Clock/
├── src/
│   └── DigitalClock.java   # Main source file (single-class app)
├── .gitignore
└── README.md
```

---

## Requirements

| Tool | Minimum version |
|------|----------------|
| JDK  | 8 (Java SE 8)  |

No third-party dependencies.

---

## How to Run

### 1 — Compile

```bash
cd Clock
javac -d out src/DigitalClock.java
```

### 2 — Run

```bash
java -cp out DigitalClock
```

### One-liner (compile + run)

```bash
javac src/DigitalClock.java -d out && java -cp out DigitalClock
```

---

## How It Works

| Component | Role |
|-----------|------|
| `JFrame`  | Top-level window |
| `JLabel` (time) | Displays `hh:mm:ss` in a large monospaced font |
| `JLabel` (AM/PM) | Small superscript-style AM/PM indicator |
| `JLabel` (date) | Full date string below the separator |
| `java.util.Timer` | Fires `tick()` every 1 000 ms on a daemon thread |
| `SwingUtilities.invokeLater` | Ensures all UI updates happen on the EDT |

---