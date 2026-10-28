<div align="center">

# 📒 Contact Book

### A modern, lightweight contact manager built with Java Swing

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?style=for-the-badge&logo=apachemaven)
![FlatLaf](https://img.shields.io/badge/FlatLaf-3.5.1-5C7CFA?style=for-the-badge)
![Gson](https://img.shields.io/badge/Gson-2.10.1-4285F4?style=for-the-badge&logo=google)
![License](https://img.shields.io/badge/License-MIT-22C55E?style=for-the-badge)

</div>

---

## ✨ Features

- 📇 **Add, edit & delete contacts** – name, phone, email, birthday, Instagram handle
- 🔍 **Real-time search** – filter by name, email, phone, or Instagram as you type
- 🔤 **Always sorted** – contacts stay alphabetically ordered automatically
- 🎂 **Birthday reminders** – a 🎂 badge appears on a contact's card on their birthday
- 🎨 **Beautiful UI** – modern flat design with pastel avatar circles and smooth hover effects
- 💾 **Persistent storage** – contacts saved to `~/.contactbook/contacts.json` via Gson
- ⌨️ **Keyboard shortcuts** – `Ctrl+N` (or `⌘N` on macOS) to add a new contact
- 📭 **Empty states** – friendly messages when no contacts or no search results
- 🔢 **Contact count** – live badge in the sidebar header

---

## 🖼 UI Preview

```
┌─────────────────────────────────────────────────────────────────────────┐
│  📒 Contact Book                                          [●][●][●]     │
├─────────────────────────────┬───────────────────────────────────────────┤
│  📒 Contact Book       [12] │                                           │
│  ┌─────────────────────┐   │               ╭──────────╮                │
│  │ ⌕ Search contacts…  │   │               │    AJ    │                │
│  └─────────────────────┘   │               ╰──────────╯                │
│                             │            Alice Johnson                  │
│  ╭───────────────────────╮  │          alice@example.com                │
│  │ 🟣 AJ  Alice Johnson  │  │  ─────────────────────────────────────   │
│  │        +1 555-0100    │  │  📱 Phone                                │
│  ╰───────────────────────╯  │     +1 555-0100                           │
│  ╭───────────────────────╮  │  📧 Email                                │
│  │ 🔵 BW  Bob Wilson     │  │     alice@example.com                    │
│  │        +1 555-0200    │  │  🎂 Birthday                             │
│  ╰───────────────────────╯  │     14 March 1990 (34 years old)         │
│  ╭───────────────────────╮  │  📸 Instagram                            │
│  │ 🟢 CS  Carol Smith 🎂 │  │     @alicejohnson                        │
│  │        carol@mail.com │  │                                           │
│  ╰───────────────────────╯  │    [ ✏  Edit ]        [ 🗑  Delete ]     │
│                             │                                           │
│  [＋  Add Contact          ]│                                           │
└─────────────────────────────┴───────────────────────────────────────────┘
```

---

## 🚀 Getting Started

### Prerequisites

| Tool | Version |
|------|---------|
| JDK  | 17+     |
| Maven | 3.8+   |

### Build

```bash
https://github.com/INFINITY-R13/Small-Java-Projects.git
cd contact-book
mvn clean package
```

This produces a **fat JAR** at `target/contact-book.jar`.

### Run

```bash
java -jar target/contact-book.jar
```

Or with Maven directly during development:

```bash
mvn exec:java -Dexec.mainClass=com.contactbook.Main
```

---

## ⌨️ Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| `Ctrl+N` / `⌘N` | Open "Add Contact" dialog |
| `Escape` | Close dialog |
| `Enter` | Confirm / Save in dialog |

---

## 📁 Project Structure

```
Contact Book/
├── pom.xml                          # Maven build descriptor
├── README.md                        # You are here
├── .gitignore
└── src/main/java/com/contactbook/
    ├── Main.java                    # Entry point, FlatLaf setup
    ├── model/
    │   └── Contact.java             # Data model (name, phone, email, birthday, instagram)
    ├── service/
    │   └── ContactService.java      # Business logic + search + sorting
    ├── storage/
    │   └── ContactStorage.java      # Gson JSON persistence (~/.contactbook/contacts.json)
    └── ui/
        ├── UIConstants.java         # Colours, fonts, avatar palette
        ├── MainFrame.java           # Root JFrame, layout, keyboard shortcuts
        ├── ContactListPanel.java    # Left sidebar with search + card list + add button
        ├── ContactDetailPanel.java  # Right panel showing selected contact details
        ├── ContactCard.java         # Single contact card component (avatar + name + phone)
        ├── ContactFormDialog.java   # Add/Edit modal dialog
        └── SearchPanel.java        # Rounded search field with clear button
```

---

## 🎨 Design Tokens

| Token | Value | Usage |
|-------|-------|-------|
| Background | `#F0F2F5` | Main area |
| Sidebar | `#E8EAED` | Left panel |
| Card | `#FFFFFF` | Contact cards |
| Accent | `#5C7CFA` | Buttons, selection highlight |
| Text primary | `#1A1A2E` | Names, values |
| Text secondary | `#6B7280` | Labels, placeholders |
| Danger | `#EF4444` | Delete button |

---

## 🛠 Tech Stack

| Library | Version | Purpose |
|---------|---------|---------|
| [FlatLaf](https://www.formdev.com/flatlaf/) | 3.5.1 | Modern flat Look & Feel for Swing |
| [Gson](https://github.com/google/gson) | 2.10.1 | JSON serialization / deserialization |
| Java Swing | JDK 17 | UI framework |
| Maven | 3.8+ | Build & dependency management |

---

## 📄 License

This project is licensed under the **MIT License** — feel free to use, modify, and distribute.

```
MIT License

Copyright (c) 2026 Contact Book Contributors

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

<div align="center">
Made with ❤️ and Java
</div>
