# To-Do List Application (Java Swing)

A modern, responsive desktop To-Do List application built with Java Swing and pure Java standard libraries (zero third-party dependencies required). Tasks are automatically persisted locally in a clean, human-readable JSON format (`tasks.json`).

---

## ✨ Features

- **Intuitive GUI Interface**: Modern Swing look-and-feel with a clean table view, custom styling, and comfortable row spacing.
- **Task Attributes**:
  - **Title** (required)
  - **Description** (optional, multi-line)
  - **Priority**: `Low` (Green badge), `Medium` (Orange badge), `High` (Red badge)
  - **Due Date**: formatted dates with indicators for **⭐ Today** and **⚠️ Overdue** tasks.
  - **Status**: Quick toggle checkbox with visual strikethrough for completed tasks.
- **Live Search & Filtering**:
  - Instant text filtering by title or description.
  - Dropdown filter: *All Tasks*, *Active Tasks*, *Completed Tasks*, *High Priority*, *Due Today / Overdue*.
- **Quick Date Controls**: Convenient 1-click date buttons (*Today*, *Tomorrow*, *+7 Days*, *Clear*) when adding or editing tasks.
- **Local JSON Persistence**: Automatically saves on any addition, edit, completion toggle, or deletion to `tasks.json`.
- **Keyboard Shortcuts**:
  - <kbd>Space</kbd> : Toggle completed status of the selected task.
  - <kbd>Enter</kbd> or Double-Click : Edit the selected task.
  - <kbd>Delete</kbd> or <kbd>Backspace</kbd> : Delete the selected task.
  - <kbd>Esc</kbd> : Close modal dialogs.
- **Real-Time Statistics**: Live counter in the status bar tracking Total, Active, and Completed tasks.

---

## 📁 Project Structure

```
To Do List/
├── build.sh                      # Build & package script (creates TodoList.jar)
├── run.sh                        # Launcher script (builds if needed and runs app)
├── TodoList.jar                  # Executable standalone JAR
├── tasks.json                    # Saved task data (created automatically)
└── src/
    └── todolist/
        ├── Main.java             # Application entry point & L&F initialization
        ├── model/
        │   ├── Priority.java     # Priority enum with colors and labels
        │   └── Task.java         # Task entity with dates and status logic
        ├── storage/
        │   ├── TaskStorage.java  # Storage interface
        │   └── JsonTaskStorage.java # Zero-dependency JSON serializer/deserializer
        └── ui/
            ├── TodoListFrame.java    # Main JFrame window & toolbar
            ├── TaskTableModel.java   # JTable model with reactive event hooks
            ├── TaskDialog.java       # Add/Edit modal dialog with date helpers
            └── TaskCellRenderers.java# Badges, strikethrough, and date formatters
```

---

## 🚀 How to Run

### Option 1: Using the runner script
```bash
./run.sh
```

### Option 2: Running the executable JAR directly
```bash
/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home/bin/java -jar TodoList.jar
```
*(or simply `java -jar TodoList.jar` if Java is in your system PATH)*

### Option 3: Compiling from source
To rebuild the project and package into `TodoList.jar`:
```bash
./build.sh
```
Or manually with `javac`:
```bash
mkdir -p bin
javac -d bin $(find src -name "*.java")
java -cp bin todolist.Main
```

---

## 💾 Data Persistence Format

Tasks are stored in `tasks.json` in standard JSON:

```json
[
  {
    "id": "c1f7b884-6997-4c7b-a25b-01ba53818e5b",
    "title": "Welcome to your To-Do List!",
    "description": "Explore features: add, edit, mark complete, filter, and prioritize tasks.",
    "priority": "HIGH",
    "dueDate": "2026-10-12",
    "completed": false,
    "createdAt": "2026-10-12T13:20:00"
  }
]
```
If `tasks.json` does not exist or is empty, starter sample tasks are provided to help you get started right away.
