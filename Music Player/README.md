<div align="center">

# Endless
### Pure sound. Uncompromising minimalism.

*An aesthetic, minimalist music player crafted in Java following Apple Human Interface Guidelines.*

<br/>

[![Java](https://img.shields.io/badge/Java-25%20LTS-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Platform](https://img.shields.io/badge/Platform-macOS%20%7C%20Cross--Platform-000000?style=flat-square&logo=apple&logoColor=white)](https://apple.com)
[![Design](https://img.shields.io/badge/Design-Apple%20HIG-FA2D48?style=flat-square)](https://developer.apple.com/design/human-interface-guidelines)
[![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)](#)

</div>

---

## ✦ Design Philosophy

**Endless** is designed around the three foundational pillars of the **Apple Human Interface Guidelines (HIG)**:

```
┌────────────────────────────────────────────────────────────────────────┐
│                                                                        │
│   CLARITY                    DEFERENCE                  DEPTH          │
│   Content-first layout       Subtle materials           Fluid motion   │
│   San Francisco type         Dynamic ambient glow       60 FPS waves   │
│   Spacious negative space    Adaptive dark/light mode   Micro-feedback │
│                                                                        │
└────────────────────────────────────────────────────────────────────────┘
```

### 1. Clarity & Typography
- **San Francisco Typeface**: Uses macOS native `.AppleSystemUIFont` / `SF Pro Display` with fractional metrics and LCD subpixel anti-aliasing.
- **Purposeful Hierarchy**: Strong bold track titles, medium subtitles for artists, and muted tertiary metadata for albums and durations.
- **Pure Vector SF Symbols**: All icons (Play, Pause, Shuffle, Repeat, Volume stages, Heart, Spotlight Search, Infinity mark `∞`) are rendered via vector `Graphics2D` paths—crisp and retina-sharp at any display resolution.

### 2. Deference & Materials
- **Adaptive Appearance**:
  - **Midnight Obsidian (Dark Mode)**: Deep canvas (`#121214`), subtle sidebar (`#18181B`), elevated card surfaces (`#222226`), and soft 1px borders (`rgba(255, 255, 255, 0.08)`).
  - **Studio White (Light Mode)**: Pristine canvas (`#F6F6F8`), pure white cards (`#FFFFFF`), subtle cool grays, and high-contrast typography.
  - *Instant toggle via header button or <kbd>⌘</kbd> + <kbd>L</kbd>.*
- **Dynamic Ambient Backlight**: The hero album card projects a multi-layered, diffused backlight aura derived from the dominant color of the current track's artwork.
- **Unified Window Chrome**: 16px corner radius borderless window with interactive macOS **Traffic Lights** (Red `#FF5F56`, Yellow `#FFBD2E`, Green `#27C93F`) featuring micro-glyphs (`✕`, `—`, `+`) on hover and smooth window dragging across the title bar.

### 3. Depth & Fluid Motion
- **60 FPS Real-Time Audio Visualizer**:
  - **Spectrum Bars**: Fluid Apple Music equalizer columns with peak decay indicators.
  - **Fluid Waveform**: Organic Bezier spline reacting to live time-domain audio samples.
  - **Ambient Aura**: Breathing concentric pulse reacting to musical energy.
  - *Click directly on the visualizer to cycle between modes.*
- **Interactive Scrubber**: Ultra-slim 4px track expanding to 6px on hover with a glowing thumb handle and drag-to-seek timestamp tooltips.
- **Animated Row Equalizer**: Playing tracks display a dancing 3-bar equalizer icon in the library table.

---

## ✦ Audio Engine & Architecture

Endless pairs high-level UI elegance with a low-latency, modular audio pipeline:

```
┌─────────────┐       ┌──────────────────────┐       ┌─────────────────────┐
│ Audio Files │ ────► │  AudioFileDecoder    │ ────► │     AudioPlayer     │
│ (WAV / MP3  │       │  - Native WAV/AIFF   │       │  - In-Memory PCM    │
│  M4A / FLAC)│       │  - afconvert Transcode│      │  - SourceDataLine   │
└─────────────┘       │  - MD5 Disk Cache    │       │  - Zero-Latency Seek│
                      └──────────────────────┘       └──────────┬──────────┘
                                                                │
                                              ┌─────────────────┴─────────────────┐
                                              ▼                                   ▼
                                     ┌─────────────────┐                 ┌─────────────────┐
                                     │ 60 FPS Visualizer│                │ Dynamic Volume  │
                                     │ Frequency/Wave  │                 │ Attenuation     │
                                     └─────────────────┘                 └─────────────────┘
```

- **Low-Latency Streaming**: Built on `javax.sound.sampled.SourceDataLine` with threaded background loading, avoiding UI freezes.
- **Zero-Latency In-Memory Playback**: Audio buffers are decoded into PCM in worker threads, enabling sample-accurate seeking, smooth loops, and real-time visualizer data capture.
- **Universal Format Support**:
  - **Native Playback**: `.wav`, `.aiff`, `.au`.
  - **macOS CoreAudio Transcoding**: `.mp3`, `.m4a`, `.aac`, `.flac` are transcoded on demand using `afconvert` and cached in `~/.endless/cache` for subsequent instant loads.
- **ID3v2 Metadata & Generative Artwork**: Automatically parses ID3v2 tags (Title, Artist, Album, and `APIC` cover images). If cover art is missing, Endless procedurally generates a multi-point gradient mesh cover matching Apple Music aesthetics.
- **Out-of-the-Box Procedural Ambient Synthesizer**: On first run, Endless generates 3 studio-quality 44.1kHz stereo ambient demo tracks directly into `demo-tracks/`:
  1. **Infinite Horizon** (*Ambient Chillwave*) — Analog pad chords (Dm9 -> Bbmaj7 -> Fmaj7 -> Cmaj7), bell arpeggios, warm sub-bass, and vinyl warmth.
  2. **Starlight Pulse** (*Lo-Fi Downtempo*) — Syncopated kick groove with Rhodes electric piano and warm texture.
  3. **Morning Mist** (*Neo-Classical*) — Acoustic piano motifs over ambient cello drones.

---

## ✦ Interface Tour

| Component | Apple HIG Implementation |
| :--- | :--- |
| **Title Bar** | macOS Traffic Lights with hover glyphs, app title with infinity mark `∞`, dynamic `● PLAYING` / `❚❚ PAUSED` capsule pill, and theme toggle. |
| **Hero Card** | 175x175 album artwork with 18px rounded corners and ambient backlight aura, large track typography, and live 60 FPS visualizer. |
| **Spotlight Search** | Rounded pill search input (`⌘F`) with magnifying glass icon, real-time filtering, and clear action. |
| **Library Table** | Clean rows with thumbnail art, track title, artist subtitle, album, duration, animated 3-bar playing equalizer, and favorite heart toggle. |
| **Sidebar** | Quick library categories (*All Tracks*, *Favorites*, *Ambient Demos*), "+ Add Tracks" and "+ Add Folder" buttons, and track count badges. |
| **Bottom Dock** | Mini track preview, prominent circular play/pause pill, shuffle & repeat buttons, sleek progress scrubber, and custom Apple volume slider. |
| **Apple Slider** | Slim capsule track with hover expansion, drop-shadowed white circular knob, and two-way keyboard shortcut synchronization. |

---

## ✦ Keyboard Shortcuts

| Key | Action |
| :---: | :--- |
| <kbd>Space</kbd> | Play / Pause |
| <kbd>→</kbd> | Seek forward 5 seconds |
| <kbd>←</kbd> | Seek backward 5 seconds |
| <kbd>↑</kbd> | Volume up 5% (synchronizes slider and icon) |
| <kbd>↓</kbd> | Volume down 5% (synchronizes slider and icon) |
| <kbd>⌘</kbd> + <kbd>→</kbd> | Next track in queue |
| <kbd>⌘</kbd> + <kbd>←</kbd> | Previous track in queue |
| <kbd>⌘</kbd> + <kbd>F</kbd> | Focus Spotlight Search bar |
| <kbd>⌘</kbd> + <kbd>L</kbd> | Toggle Dark / Light Appearance |
| <kbd>⌘</kbd> + <kbd>S</kbd> | Toggle Shuffle mode |
| <kbd>⌘</kbd> + <kbd>R</kbd> | Cycle Repeat mode (Off ➔ All ➔ One) |

---

## ✦ Quick Start

### Prerequisites
- **Java 17+** (Developed and verified with Temurin JDK 25 LTS)
- **macOS** (Recommended for native system fonts, window transparency, and CoreAudio transcoding)

### 1. Run Directly
```bash
./run.sh
```

### 2. Build from Source
```bash
./build.sh
```

### 3. Package Standalone Executable JAR
```bash
./package.sh
java -jar Endless.jar
```

---

## ✦ Drag & Drop Workflow

You can drag audio files (`.wav`, `.mp3`, `.m4a`, `.flac`, `.aiff`) or entire music folders directly from **macOS Finder** into the Endless window. Tracks are automatically parsed, added to your library, and immediately ready to play.

---

## ✦ Project Structure

```
Music Player/
├── src/
│   └── com/
│       └── endless/
│           ├── EndlessApp.java               # App entry point, macOS system flags & EDT launch
│           ├── audio/
│           │   ├── AudioPlayer.java          # Low-latency playback engine & visualizer hooks
│           │   ├── AudioFileDecoder.java     # PCM decoding & CoreAudio afconvert transcoding
│           │   ├── VisualizerDataProvider.java# Real-time sample feed interface
│           │   └── TrackSynthesizer.java     # Algorithmic ambient music generator
│           ├── model/
│           │   ├── Track.java                # Track metadata model & dominant color extractor
│           │   ├── Playlist.java             # Queue manager, shuffle deck, and repeat states
│           │   └── MetadataExtractor.java    # ID3v2 parser & generative Apple gradient artwork
│           └── ui/
│               ├── MainWindow.java           # Undecorated rounded window, hero card & shortcuts
│               ├── Theme.java                # Apple design tokens, color palettes & typography
│               └── components/
│                   ├── TitleBar.java         # Traffic lights, window drag & theme switcher
│                   ├── AlbumArtPanel.java    # Artwork card with dynamic ambient backlight aura
│                   ├── ControlsPanel.java    # Playback dock, scrubber, volume & track summary
│                   ├── AppleSlider.java      # Custom Apple volume slider with hover knob
│                   ├── AppleButton.java      # Scalable Apple pill and icon buttons
│                   ├── ProgressScrubber.java # Sleek progress bar with hover thumb & time labels
│                   ├── WaveformVisualizer.java# 60 FPS spectrum bars, fluid wave & aura modes
│                   ├── PlaylistView.java     # Track table with animated mini equalizer
│                   ├── SearchBar.java        # Apple Spotlight pill with real-time query filter
│                   ├── Sidebar.java          # Library navigation & track import dialogs
│                   └── SFIcon.java           # Scalable vector SF Symbols icon generator
├── demo-tracks/                              # Generated high-fidelity ambient WAV tracks
├── Endless.jar                               # Standalone executable JAR
├── .gitignore                                # macOS, Java, IDE, and cache ignore rules
├── build.sh                                  # Compilation script
├── package.sh                                # JAR packaging script
├── run.sh                                    # Quick launch script
└── README.md                                 # Documentation
```

---

<div align="center">
  <sub>Crafted with precision for pure listening. © Endless Music Player.</sub>
</div>
