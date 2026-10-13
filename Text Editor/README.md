<div align="center">

# ✦ AuraEdit

**An ultra-modern, aesthetic, and distraction-free code editor crafted in pure Java.**

[![Java](https://img.shields.io/badge/Java-17%2B-orange?style=flat-square&logo=openjdk&logoColor=white)](https://jdk.java.net/)
[![Dependencies](https://img.shields.io/badge/Dependencies-Zero-brightgreen?style=flat-square)](https://github.com/)
[![Themes](https://img.shields.io/badge/Themes-5_Curated-blueviolet?style=flat-square)](src/aura/theme/ThemeManager.java)
[![Platform](https://img.shields.io/badge/Platform-macOS_·_Linux_·_Windows-blue?style=flat-square)](build.sh)
[![Build](https://img.shields.io/badge/Build-Standalone_JAR-informational?style=flat-square)](AuraEdit.jar)

<br/>

[Overview](#-overview) • [Quick Start](#-quick-start) • [Curated Themes](#-curated-themes) • [Key Features](#-key-features) • [Shortcuts](#-keyboard-shortcuts) • [Architecture](#-project-architecture)

<br/>

</div>

---

## ◈ Overview

**AuraEdit** is a lightweight, aesthetic text and code editor built entirely with standard Java (Swing & Java2D). Designed from the ground up for focused development, it pairs modern UI sensibilities—inspired by Catppuccin and Tokyo Night palettes—with thoughtful editing ergonomics, live markdown rendering, and instant zero-dependency launch times.

- **Zero Third-Party Dependencies** — Pure Java standard library. Runs out of the box with any JDK 17+.
- **Subpixel Anti-Aliased Graphics** — Native HiDPI vector icons and curated monospace font cascades.
- **Instant Responsiveness** — Lightweight footprint, debounced syntax tokenization, and asynchronous event distribution.

---

## ⚡ Quick Start

### Prerequisites
- **Java SE Development Kit (JDK) 17+** (JDK 17, 21, or 25 recommended, e.g. Eclipse Temurin).

### Launch

```bash
# Clone or enter the workspace
cd "Text Editor"

# Launch using the one-click script (auto-builds if needed)
./run.sh
```

Or execute the pre-built standalone JAR directly:

```bash
java -jar AuraEdit.jar
```

### Build from Source

```bash
# Compiles all source files into bin/ and packages AuraEdit.jar
./build.sh
```

<details>
<summary>Manual Build (Windows Command Prompt / PowerShell)</summary>

```bat
mkdir bin
javac -d bin src\aura\*.java src\aura\*\*.java
jar --create --file AuraEdit.jar --main-class aura.AuraEdit -C bin .
java -jar AuraEdit.jar
```
</details>

---

## 🎨 Curated Themes

Switch themes on the fly via the top header bar dropdown or with `Cmd / Ctrl + T`. Every theme recalibrates the editor surface, gutter, tabs, scrollbars, status bar, and syntax tokens.

| Theme | Type | Accent | Background | Mood & Aesthetic |
|---|:---:|:---:|:---:|---|
| **Catppuccin Mocha** | Dark | `#cba6f7` (Mauve) | `#1e1e2e` | Default soothing warm pastel dark |
| **Tokyo Night** | Dark | `#7aa2f7` (Blue) | `#1a1b26` | High-contrast indigo with neon highlights |
| **Nord Frost** | Dark | `#88c0d0` (Frost) | `#2e3440` | Arctic cool slate & muted aurora tones |
| **GitHub Light** | Light | `#0969da` (Blue) | `#ffffff` | Crisp, distraction-free daylight contrast |
| **Solarized Warm** | Light | `#b58900` (Amber) | `#fdf6e3` | Warm cream reading palette for reduced eye strain |

---

## ✨ Key Features

### 🧠 Editor Ergonomics & Smart Pairs
- **Automatic Indentation** — Matches the indentation level of the preceding line on Enter, automatically applying extra indentation after `{`, `:`, `(`, or `[`.
- **Intelligent Bracket & Quote Pairing** — Automatically closes `()`, `[]`, `{}`, `""`, and `''`. Overtyping closing characters advances the caret without duplicating characters; pressing `Backspace` between empty pairs deletes both.
- **Active Caret & Bracket Match Glow** — Subtle highlight behind the current active line and synchronized bracket-matching detection.
- **Smart Line Actions** — Duplicate line (`Cmd/Ctrl + D`), toggle line comments (`Cmd/Ctrl + /`), and block indent/unindent with `Tab` / `Shift + Tab` (4 soft spaces).
- **Fluid Typography Zoom** — Scale font sizes seamlessly via `Cmd/Ctrl + (+ / - / 0)` or `Cmd/Ctrl + Mouse Wheel` across an intelligent font fallback cascade (`JetBrains Mono` → `SF Mono` → `Fira Code` → `Menlo` → `Cascadia Code` → `Consolas` → `Monaco`).

### 🗂️ Document Management & Smart Tabs
- **Multi-Tab Workspace** — Work across multiple files simultaneously with individual undo/redo histories.
- **Visual Tab Badges** — Extension-colored language indicators, unsaved modification dots (`•`), and hover close buttons (`×`).
- **Drag-and-Drop Ingestion** — Drag any file or directory directly into the window to open it immediately.
- **Clean File Lifecycle** — Standard file open, save, and save-as dialogs with confirmation on unsaved changes upon tab or window close.

### 🌈 Real-Time Syntax Highlighting
Fast, debounced, non-blocking regex highlighting with custom token coloring per theme:

| Category | Supported Languages & Formats |
|---|---|
| **Core Languages** | Java, Python, JavaScript, TypeScript, C / C++ |
| **Web & Markup** | HTML, CSS / SCSS, XML, SVG, Markdown |
| **Data & Query** | JSON, SQL, Shell Scripts (`bash`, `sh`, `zsh`) |
| **Plain Text** | Clean typography with full editor ergonomics |

### 👁️ Synchronized Markdown Preview
- **Side-by-Side Rendered View** — Toggle live HTML preview for `.md` files using the toolbar eye button (`👁️`).
- **Theme-Integrated Stylesheet** — Headers, fenced code blocks, inline tags, blockquotes, and lists render styled to match the active theme.

### 🧭 Collapsible Workspace Explorer
- **Directory Tree** — Browse project hierarchies and open files with double-click ease.
- **Vector Iconography** — Pure Java2D vector icons rendered crisply on Retina and High-DPI screens.
- **Toggleable Drawer** — Collapse or expand with `Cmd/Ctrl + B` or the header toolbar button.

### 🔍 Floating Find & Replace HUD
- **Non-Intrusive Card** — Opens smoothly with `Cmd/Ctrl + F` (Find) or `Cmd/Ctrl + H` (Replace).
- **Real-Time Match Counter** — Displays live match progress (`Match 3 of 14`) and highlights all occurrences across the viewport.
- **Refined Search Filters** — Match case (`Aa`), whole-word matching (`\b`), directional cycling (`▲` / `▼`), individual replacement, and global replace-all.

### 📊 Telemetry Status Bar
- **Real-Time Metrics** — Caret location (`Ln 42, Col 18`), live document word count, character count, encoding (`UTF-8`), language mode badge, and zoom scale (`100%`).
- **Toast Notifications** — Transient status indicators for save operations, file loads, and theme changes.

---

## ⌨️ Keyboard Shortcuts

All shortcuts automatically map to `Cmd` on macOS and `Ctrl` on Windows and Linux.

### Workspace & Documents
| Shortcut | Action |
|---|---|
| `Cmd / Ctrl + N` | Create new document tab |
| `Cmd / Ctrl + O` | Open file via picker |
| `Cmd / Ctrl + Shift + O` | Open directory in sidebar explorer |
| `Cmd / Ctrl + S` | Save current document |
| `Cmd / Ctrl + Shift + S` | Save current document as... |
| `Cmd / Ctrl + W` | Close active tab |
| `Cmd / Ctrl + B` | Toggle workspace sidebar |

### Editing & Code Ergonomics
| Shortcut | Action |
|---|---|
| `Cmd / Ctrl + Z` | Undo last edit |
| `Cmd / Ctrl + Shift + Z` / `Y` | Redo undone edit |
| `Cmd / Ctrl + D` | Duplicate current line |
| `Cmd / Ctrl + /` | Toggle line comment (`//` or `#`) |
| `Tab` | Indent selection or insert 4 spaces |
| `Shift + Tab` | Unindent selection by 4 spaces |

### Search & Navigation
| Shortcut | Action |
|---|---|
| `Cmd / Ctrl + F` | Open Find bar |
| `Cmd / Ctrl + H` | Open Find & Replace bar |
| `Enter` (in Find) | Jump to next match |
| `Shift + Enter` (in Find) | Jump to previous match |
| `Esc` | Close Find & Replace bar |

### View & Appearance
| Shortcut | Action |
|---|---|
| `Cmd / Ctrl + T` | Cycle next theme |
| `Cmd / Ctrl + +` / `=` | Zoom in (increase font size) |
| `Cmd / Ctrl + -` | Zoom out (decrease font size) |
| `Cmd / Ctrl + 0` | Reset zoom to 100% (14pt default) |
| `Cmd / Ctrl + Mouse Wheel` | Smooth font zoom |

---

## 📁 Project Architecture

```
Text Editor/
├── src/aura/
│   ├── AuraEdit.java                 # Main application frame, action router & layout engine
│   ├── editor/
│   │   ├── CodeEditorPane.java       # Text engine: smart pairs, indent, line highlight, zoom
│   │   ├── EditorTab.java            # Tab container managing document state, I/O & preview
│   │   ├── LineNumberGutter.java     # Synchronized gutter with active line tracking
│   │   ├── MarkdownPreviewPane.java  # HTML renderer styled to match active color palette
│   │   └── SyntaxHighlighter.java    # Debounced regex-based multi-language tokenizer
│   ├── theme/
│   │   ├── Theme.java                # Immutable record holding color definitions
│   │   └── ThemeManager.java         # Palette registry, listener dispatcher & theme switcher
│   ├── ui/
│   │   ├── FindReplaceBar.java       # Floating find/replace HUD with live highlights
│   │   ├── ModernButton.java         # Flat rounded button with hover & press transitions
│   │   ├── ModernScrollBarUI.java    # Sleek pill scrollbars without legacy arrows
│   │   ├── ModernTabBar.java         # Document strip with dirty indicators & close buttons
│   │   ├── Sidebar.java              # Collapsible file explorer tree with custom renderer
│   │   └── StatusBar.java            # Bottom metrics bar (Ln/Col, words, chars, encoding)
│   └── util/
│       ├── FileType.java             # File extension detector, badges & language metadata
│       └── Icons.java                # HiDPI vector icons drawn natively with Graphics2D
├── demo/                             # Ready-to-test sample files
│   ├── Welcome.md                    # In-app welcome guide & feature cheatsheet
│   ├── Example.java                  # Sample Java source file
│   ├── script.py                     # Sample Python script
│   └── data.json                     # Sample JSON dataset
├── build.sh                          # Compilation script to package AuraEdit.jar
├── run.sh                            # Executable launch script
└── AuraEdit.jar                      # Standalone executable JAR package
```

---

## 🛠️ Design & Technical Highlights

- **Pure Java Standard Library** — Requires zero external JARs, package managers, or native bindings.
- **Java2D Vector Engine** — All UI iconography is mathematically generated through `Graphics2D` paths, guaranteeing razor-sharp clarity across standard, Retina, and 4K displays.
- **Debounced Highlighting Pipeline** — The syntax highlighter listens to document mutations and buffers updates through a 120ms swing timer, ensuring smooth typing even in large files.
- **Decoupled Theme Architecture** — Components register listeners to `ThemeManager`, enabling instantaneous theme propagation across the entire interface without reloading documents.

---

## 📄 License

Open-source under the [MIT License](LICENSE) — free to use, modify, and distribute.
