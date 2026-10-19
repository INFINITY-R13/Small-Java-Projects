# ⏳ Java Countdown Timer

A modern, precision countdown timer built in Java featuring both an **interactive terminal interface (CLI)** with real-time ASCII progress bars and a **sleek Graphical User Interface (GUI)** with desktop alerts and Pomodoro productivity presets.

---

## ✨ Features

- **Dual Interfaces**:
  - **🖥️ Desktop GUI (Swing)**: Sleek dark-mode window with a large digital clock readout, progress bar, Pomodoro/break presets, quick adjust buttons, and "Always on Top" mode.
  - **⚡ Terminal CLI**: In-place single-line updating (`\r`) with color-coded progress bars (Green $\to$ Yellow $\to$ Red) and clean ASCII banners.
- **Drift-Free Precision**: Uses target wall-clock timestamps rather than naive thread sleeps to guarantee exact countdown timing even under system load.
- **Flexible Time Input**: Supports seconds, colon-delimited time, and natural unit formats:
  - `90` or `90s` (90 seconds)
  - `05:00` (5 minutes)
  - `01:30:00` (1 hour 30 minutes)
  - `1h 30m 15s`, `25m`, `10min`, `2 hours`
- **Productivity Presets**: Built-in 25-minute Pomodoro focus timer, 5-minute short break, and 15-minute long break.
- **Audio & Visual Alerts**:
  - Polyphonic ascending chime using Java's built-in MIDI synthesizer.
  - System bell & terminal chime (`\007`) fallback.
  - Completion dialog & celebratory ASCII art.
- **Thread-Safe Architecture**: Clean separation between the core timer engine, state management, event listeners, and UI layers.
- **Zero External Dependencies**: Standard Java SE with pure JDK libraries (`java.desktop`, `javax.sound.midi`, `java.util.concurrent`).

---

## 📁 Project Structure

```text
Countdown Timer/
├── .gitignore               # Ignores build outputs, .class, .jar, and IDE files
├── README.md                # Project documentation and guide
├── build.sh                 # Compilation & packaging script
├── run.sh                   # Application launcher script
└── src/
    └── com/
        └── timer/
            ├── CountdownTimer.java   # Core thread-safe, drift-free timer engine
            ├── TimerState.java       # Lifecycle state enum (READY, RUNNING, PAUSED, etc.)
            ├── TimerListener.java    # Event listener interface for tick and state changes
            ├── TimeUtils.java        # Time parser, HH:MM:SS formatter & progress bar
            ├── SoundUtils.java       # MIDI multi-tone chime & audio alerts
            ├── ConsoleTimer.java     # CLI interface with live updating terminal bar
            ├── GuiTimer.java         # Swing GUI with dark theme & controls
            └── Main.java             # Entry point routing CLI args, menu & GUI
```

---

## 📋 Prerequisites

- **Java Development Kit (JDK)**: Version 11 or higher (Tested and compatible with OpenJDK / Eclipse Temurin 25).
- macOS, Linux, or Windows.

> [!TIP]
> If `java` or `javac` is not directly on your system `PATH`, configure your `JAVA_HOME`:
> ```bash
> export JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home"
> export PATH="$JAVA_HOME/bin:$PATH"
> ```
> The included `./build.sh` and `./run.sh` scripts automatically detect Temurin JDK on macOS.

---

## 🚀 Quick Start

### 1. Using Helper Scripts (Recommended)

Make sure the scripts have execute permissions (already set):
```bash
chmod +x build.sh run.sh
```

**Build the project and generate the runnable JAR:**
```bash
./build.sh
```

**Run the interactive CLI menu:**
```bash
./run.sh
```

**Launch the Graphical Desktop UI:**
```bash
./run.sh --gui
```

**Run a countdown directly:**
```bash
./run.sh 5m             # 5-minute countdown
./run.sh 02:30          # 2 minutes 30 seconds
./run.sh 45s            # 45 seconds
./run.sh --pomodoro     # 25-minute Pomodoro focus session
```

---

### 2. Manual Compilation & Execution

#### Compile to `bin/`:
```bash
mkdir -p bin
javac -d bin src/com/timer/*.java
```

#### Run with class path:
```bash
# Interactive CLI menu
java -cp bin com.timer.Main

# Desktop GUI
java -cp bin com.timer.Main --gui

# Direct duration
java -cp bin com.timer.Main 1h 15m
```

#### Package and run as an Executable JAR:
```bash
jar cfe countdown-timer.jar com.timer.Main -C bin .
java -jar countdown-timer.jar --gui
```

---

## 💻 Usage & Command Reference

### Command Line Flags

| Option / Argument | Description |
|---|---|
| *(none)* | Launches the interactive terminal menu |
| `--gui`, `-g` | Opens the graphical Swing desktop interface |
| `--cli`, `-c` | Launches interactive terminal menu |
| `--pomodoro`, `-p` | Starts a 25-minute Pomodoro timer immediately |
| `--short-break` | Starts a 5-minute break timer |
| `--long-break` | Starts a 15-minute break timer |
| `<duration>` | Starts a timer with custom duration (e.g. `90`, `05:00`, `1h 30m`) |
| `--help`, `-h` | Prints available options and input examples |

### Duration Format Examples

- **Seconds**: `30`, `90`, `45s`
- **Minutes & Seconds (MM:SS)**: `05:00`, `25:00`, `02:30`
- **Hours, Minutes & Seconds (HH:MM:SS)**: `01:30:00`, `00:15:30`
- **Natural Units**: `15m`, `2h`, `1h 30m`, `5m 10s`

---

## 🖥️ Graphical Interface (GUI) Guide

Launch via `./run.sh --gui`:

1. **Digital Display**: Crisp `HH:MM:SS` monospace display showing exact time remaining.
2. **Progress Bar**: Live visualization of elapsed/remaining percentage.
3. **Preset Buttons**: One-click configuration for:
   - **Pomodoro**: 25 minutes
   - **Short Break**: 5 minutes
   - **Long Break**: 15 minutes
   - **Quick Test**: 1 minute
4. **Custom Time Spinners**: Set exact Hours, Minutes, and Seconds and click **Apply**.
5. **Quick Add**: Add `+1 Min`, `+5 Min`, or `+10 Min` on the fly.
6. **Controls**:
   - **START / PAUSE / RESUME**: Toggle active counting.
   - **RESET**: Restore timer to original duration.
7. **Options**:
   - **Always on Top**: Keeps the timer window floating over other applications while you work.
   - **Sound Notification**: Toggle audio chime on countdown completion.

---

## 🏗️ Architecture & Design

- **Decoupled Architecture**:
  The core engine [`CountdownTimer.java`](file:///Users/infinity/Documents/Java-Learn/Small-Java-Projects/Countdown%20Timer/src/com/timer/CountdownTimer.java) maintains no UI dependencies. It communicates with callers via the [`TimerListener`](file:///Users/infinity/Documents/Java-Learn/Small-Java-Projects/Countdown%20Timer/src/com/timer/TimerListener.java) observer pattern.
- **Concurrency & Precision**:
  Utilizes `java.util.concurrent.ScheduledExecutorService` with high-frequency check polling against monotonic millisecond timestamps, eliminating drift caused by variable thread scheduling.
- **Graceful Termination**:
  A JVM shutdown hook is registered in [`ConsoleTimer.java`](file:///Users/infinity/Documents/Java-Learn/Small-Java-Projects/Countdown%20Timer/src/com/timer/ConsoleTimer.java) ensuring that cancellation signals (`SIGINT` / `Ctrl+C`) cleanly stop worker threads and restore terminal state.

---