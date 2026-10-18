# 📅 Calendar Maker (Java) - Indian Holidays Edition

A feature-packed, zero-dependency Calendar generator and event management suite built with modern Java. Designed according to **Indian National Holidays, Gazetted Holidays, and Major Cultural Festivals**, it features an interactive terminal browser with Unicode box-drawing and ANSI styling, along with a multi-format export engine that generates modern HTML (printable & responsive), GitHub Flavored Markdown tables, iCalendar (`.ics`), CSV spreadsheets, and plain ASCII text.

---

## ✨ Key Features

- **🇮🇳 Designed for Indian Holidays & Festivals**:
  - **3 National Gazetted Holidays**: Republic Day (Jan 26), Independence Day (Aug 15), and Mahatma Gandhi Jayanti (Oct 02).
  - **Major Gazetted Festivals**: Diwali / Deepavali, Holi, Dussehra / Vijaya Dashami, Eid-ul-Fitr (Ramzan Eid), Bakrid / Eid-ul-Adha, Mahavir Jayanti, Buddha Purnima, Guru Nanak Jayanti, Good Friday, Muharram, Milad-un-Nabi, and Christmas.
  - **Important Cultural & Harvest Festivals**: Makar Sankranti / Pongal, Maha Shivratri, Raksha Bandhan, Krishna Janmashtami, Ganesh Chaturthi, Dr. B.R. Ambedkar Jayanti, Chhath Puja, Bhai Dooj, Govardhan Puja, Karwa Chauth, and Rashtriya Ekta Diwas.
  - **Multi-Year Festival Calendar**: Exact astronomical/calendar dates across years (2024, 2025, 2026, 2027, and algorithmic fallback for other years).
- **🖥️ Dual Interface Modes**:
  - **Interactive Terminal Browser**: Navigate month-by-month with hotkeys (`[N]`ext, `[P]`rev, `[T]`oday, `[J]`ump, `[E]`xport), view full 12-month year overviews, and manage events interactively.
  - **Command-Line Interface (CLI)**: Automate calendar generation with flexible flags (`--year`, `--month`, `--format`, `--output`, `--week-start`, etc.).
- **🎨 Multi-Format Export Engine**:
  - **🌐 Modern HTML**: Clean, responsive layout with CSS Grid, dark/light mode toggle, event chips, and printer-friendly `@media print` styling (optimized for letter/A4 printing).
  - **📝 GitHub Markdown (`.md`)**: Beautiful Markdown tables with date headers and bulleted event agendas.
  - **📅 iCalendar (`.ics`)**: RFC 5545 compliant format ready for 1-click import into Google Calendar, Apple Calendar, and Microsoft Outlook.
  - **📊 CSV Spreadsheet (`.csv`)**: Tabular export ready for Excel, Google Sheets, or data pipelines.
  - **📄 ASCII Text (`.txt`)**: Clean monospaced table format with box dividers for terminal or plain text files.
- **📌 Event & Reminder Management**:
  - Add, list, search, and delete events with date, title, category, description, and annual recurrence.
  - Categories with visual icons and color badges:
    - `🎉 Holiday`
    - `🎂 Birthday`
    - `💼 Meeting`
    - `🏢 Work`
    - `👤 Personal`
    - `⏰ Reminder`
    - `📌 Other`
  - JSON persistence: Custom events are automatically saved to and loaded from `events.json`.
- **📆 Customizable Calendar Settings**:
  - Start week on **Sunday** or **Monday** (Monday is widely used in corporate and Indian standard calendars).
  - Toggle Indian holidays integration on/off via menu or `--no-holidays`.
  - Toggle ANSI color escape sequences on/off for headless environments.
- **⚡ Zero External Dependencies**:
  - Built entirely using standard Java SE library (`java.time.*`, `java.nio.*`).
  - No Maven/Gradle download requirements; compiles and runs out of the box on any machine with Java 8 or newer.
- **🧪 Automated Self-Contained Test Suite**:
  - 12 built-in unit tests verifying leap years, Indian national holidays, major festivals (Diwali, Holi, Dussehra, etc.), recurring events, persistence, and all format renderers.

---

## 📁 Project Structure

```
Calendar Maker/
├── .gitignore                     # Git ignore rules for Java, IDEs, and OS files
├── README.md                      # Project documentation
├── build.sh                       # Unix/macOS build script (auto-detects JDK)
├── run.sh                         # Unix/macOS run script
├── events.json                    # Saved events database (JSON)
├── src/
│   └── com/
│       └── calendarmaker/
│           ├── Main.java          # CLI entry point and dispatcher
│           ├── model/
│           │   ├── CalendarEvent.java     # Event entity with recurrence logic
│           │   ├── EventCategory.java     # Category enum with icons & colors
│           │   ├── EventManager.java      # Event repository & JSON persistence
│           │   └── HolidayProvider.java   # Indian National & Gazetted holiday engine
│           ├── generator/
│           │   ├── CalendarRenderer.java       # Renderer interface
│           │   ├── ConsoleCalendarRenderer.java # Unicode box & ANSI renderer
│           │   ├── HtmlCalendarRenderer.java    # Responsive HTML & CSS renderer
│           │   ├── MarkdownCalendarRenderer.java# GFM table renderer
│           │   ├── TextCalendarRenderer.java    # Monospaced ASCII renderer
│           │   ├── IcsCalendarRenderer.java     # RFC 5545 iCalendar renderer
│           │   └── CsvCalendarRenderer.java     # CSV spreadsheet renderer
│           ├── cli/
│           │   ├── CommandLineParser.java # Argument parser and help documentation
│           │   └── InteractiveMenu.java   # Terminal UI menu & browser
│           └── util/
│               ├── AnsiColor.java         # ANSI color styling utility
│               └── DateUtils.java         # Date calculations and formatting
└── test/
    └── com/
        └── calendarmaker/
            └── CalendarMakerTest.java     # Automated unit & integration test runner
```

---

## 🚀 Getting Started

### Prerequisites

- **Java JDK 8** or higher (tested with OpenJDK 8 through 25).
- macOS or Linux.

---

### Quick Build & Run

```bash
# 1. Build and run automated tests
./build.sh

# 2. Launch interactive mode
./run.sh

# Or run with CLI options
./run.sh --month 6 --year 2026
```

#### Manual Compilation:

```bash
# Compile
javac -d bin src/com/calendarmaker/*.java src/com/calendarmaker/*/*.java test/com/calendarmaker/*.java

# Run Tests
java -cp bin com.calendarmaker.CalendarMakerTest

# Run Application
java -cp bin com.calendarmaker.Main
```

---

## 🎮 Interactive Mode

When launched with no arguments (or `./run.sh -i`), the interactive console menu appears:

```
  ____      _                _             __  __       _
 / ___|__ _| | ___ _ __   __| | __ _ _ __ |  \/  | __ _| | _____ _ __
| |   / _` | |/ _ \ '_ \ / _` |/ _` | '__|| |\/| |/ _` | |/ / _ \ '__|
| |__| (_| | |  __/ | | | (_| | (_| | |   | |  | | (_| |   <  __/ |
 \____\__,_|_|\___|_| |_|\__,_|\__,_|_|   |_|  |_|\__,_|_|\_\___|_|

       ✨ Java Calendar Maker & Export Suite (Indian Edition) ✨

================ MAIN MENU ================
  1. 📅 View Month Calendar (Interactive Browser)
  2. 🗓️ View Full Year Calendar
  3. 📝 Manage Events, Holidays & Reminders
  4. 💾 Generate & Export Calendar (HTML, MD, ICS, CSV, TXT)
  5. ⚙️ Settings (Week Start, Holidays, Colors)
  6. 🚪 Exit
===========================================
```

### Hotkeys in Month Viewer:
- `N`: Next month
- `P`: Previous month
- `T`: Reset to current month (Today)
- `J`: Jump to any `YYYY-MM`
- `E`: Export current month directly
- `M` / `Q`: Return to main menu

---

## 💻 CLI Commands & Examples

### 1. View Calendar in Terminal

```bash
# View October 2026 (Gandhi Jayanti, Dussehra, etc.)
./run.sh --month 10 --year 2026

# View November 2026 (Diwali, Govardhan Puja, Bhai Dooj, Guru Nanak Jayanti)
./run.sh --month 11 --year 2026

# View full year 2026
./run.sh --year 2026 --full-year

# Change week start to Monday (standard in Indian workplaces)
./run.sh --month 10 --year 2026 --week-start mon

# Exclude holidays
./run.sh --month 10 --year 2026 --no-holidays
```

### 2. Export to Files

```bash
# Export printable HTML calendar
./run.sh --year 2026 --month 10 --format html -o calendar_oct2026.html

# Export full 12-month annual booklet in HTML
./run.sh --year 2026 --full-year --format html -o calendar_2026.html

# Export Markdown calendar
./run.sh --year 2026 --month 10 --format md -o calendar_oct2026.md

# Export iCalendar (.ics) for Apple/Google Calendar
./run.sh --year 2026 --format ics -o indian_holidays_2026.ics

# Export CSV for spreadsheets
./run.sh --year 2026 --format csv -o indian_holidays_2026.csv
```

### 3. Manage Events via Command Line

```bash
# Add an annual recurring event (Birthday)
./run.sh --add-event "2026-06-13|Raghuraj's Birthday|Birthday|Raghuraj's Birthday|true"

# List events for June 2026
./run.sh --list-events 2026-06

# List all events and holidays for 2026
./run.sh --list-events 2026
```

---

## 📊 Sample Terminal Output

### October 2026 (Indian Holidays):

```
┌───────────────────────────────────────────────────────┐
│                     OCTOBER 2026                      │
├───────┬───────┬───────┬───────┬───────┬───────┬───────┤
│  Sun  │  Mon  │  Tue  │  Wed  │  Thu  │  Fri  │  Sat  │
├───────┼───────┼───────┼───────┼───────┼───────┼───────┤
│       │       │       │       │   1   │  2*   │   3   │
├───────┼───────┼───────┼───────┼───────┼───────┼───────┤
│   4   │   5   │   6   │   7   │   8   │   9   │  10   │
├───────┼───────┼───────┼───────┼───────┼───────┼───────┤
│  11   │  12   │  13   │  14   │  15   │  16   │  17   │
├───────┼───────┼───────┼───────┼───────┼───────┼───────┤
│  18   │  19   │  20*  │  21   │  22   │  23   │  24   │
├───────┼───────┼───────┼───────┼───────┼───────┼───────┤
│  25   │  26   │  27   │  28   │  29*  │  30   │  31*  │
└───────┴───────┴───────┴───────┴───────┴───────┴───────┘

── EVENTS & REMINDERS (4) ──
• 2026-10-02 [🎉 Holiday]: Mahatma Gandhi Jayanti (Annual) - National Gazetted Holiday
• 2026-10-20 [🎉 Holiday]: Dussehra / Vijaya Dashami - Gazetted Holiday
• 2026-10-29 [🎉 Holiday]: Karwa Chauth - Cultural Observance / Restricted
• 2026-10-31 [🎉 Holiday]: Rashtriya Ekta Diwas (Annual) - Sardar Patel Jayanti
```

---

## 🧪 Testing

The test suite runs automatically during `./build.sh` or can be run independently:

```bash
java -cp bin com.calendarmaker.CalendarMakerTest
```

Test coverage includes:
- [x] Leap year validation (e.g. 2024, 2000 vs. 2026, 1900)
- [x] Indian National Holidays validation (Republic Day, Independence Day, Gandhi Jayanti)
- [x] Major Indian Festivals calculations (Diwali, Holi, Dussehra, Eid-ul-Fitr, etc.)
- [x] Event creation, date matching, and deletion
- [x] Annual recurring events (e.g. birthdays across multiple years)
- [x] JSON persistence and deserialization
- [x] Console renderer output formatting
- [x] HTML renderer structure and `@media print` tags
- [x] Markdown table generation
- [x] RFC 5545 iCalendar (`.ics`) format compliance
- [x] CSV export headers and escaping
- [x] Week start day adjustment (Sunday vs Monday)

---