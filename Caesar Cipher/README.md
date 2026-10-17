# Caesar Cipher in Java

A robust, object-oriented implementation of the classic **Caesar Cipher** algorithm written in Java.

---

## Table of Contents

- [Overview](#overview)
- [How It Works](#how-it-works)
- [Features](#features)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Compilation & Execution](#compilation--execution)
  - [Compiling](#compiling)
  - [Running the Interactive CLI](#running-the-interactive-cli)
  - [Running the Automated Tests](#running-the-automated-tests)
- [Usage as a Library](#usage-as-a-library)
  - [Static Utility API](#static-utility-api)
  - [Object-Oriented API](#object-oriented-api)
  - [Brute-Force Cracking](#brute-force-cracking)
- [Security Disclaimer](#security-disclaimer)

---

## Overview

The **Caesar Cipher** is one of the earliest and simplest encryption techniques. Named after Julius Caesar, who used it to communicate privately with his generals, it is a monoalphabetic substitution cipher where each letter in the plaintext is shifted by a fixed number of positions along the alphabet.

---

## How It Works

For an alphabet of size $N = 26$ and a shift key $k$:

- **Encryption Formula:**
  $$E_k(x) = (x + k) \pmod{26}$$

- **Decryption Formula:**
  $$D_k(x) = (x - k) \pmod{26}$$

Where $x$ is the 0-indexed position of a letter ($A = 0, B = 1, \dots, Z = 25$).

### Example

With shift $k = 3$:
- Plaintext:  `HELLO`
- Encrypted:  `KHOOR`

Non-alphabetic characters (such as digits, spaces, and punctuation) are left untouched.

---

## Features

- **Full Alphabet Support**: Accurately encrypts and decrypts standard 26-letter Latin alphabet characters.
- **Case Preservation**: Uppercase letters stay uppercase (`A` $\to$ `D`) and lowercase stay lowercase (`a` $\to$ `d`).
- **Punctuation & Symbols**: Spaces, numbers, and punctuation are preserved without corruption.
- **Arbitrary Shift Keys**: Handles positive shifts, negative shifts (e.g. $-3$), and shifts larger than 26 (e.g. $55 \equiv 3 \pmod{26}$) via modulo arithmetic.
- **Brute-Force Decryption**: Tests all 25 possible shifts to crack ciphertext without knowing the original key.
- **Interactive CLI**: Menu-driven console interface for manual encryption, decryption, and cracking.
- **Standalone Test Suite**: Built-in automated tests covering 20+ edge cases without external dependencies.

---

## Project Structure

```text
Caesar Cipher/
├── .gitignore             # Ignores compiled class files, IDEs, and OS artifacts
├── README.md              # Documentation and usage guide
└── src/
    ├── CaesarCipher.java      # Core cipher logic and brute-force solver
    ├── Main.java              # Interactive command-line interface
    └── CaesarCipherTest.java  # Automated unit test suite
```

---

## Prerequisites

- **Java Development Kit (JDK)**: JDK 8 or higher (tested with OpenJDK 25 LTS).

To check your Java installation:
```bash
javac -version
java -version
```

---

## Compilation & Execution

### Compiling

Compile all Java files into a `bin/` output directory:

```bash
mkdir -p bin
javac -d bin src/*.java
```

### Running the Interactive CLI

Run the interactive terminal interface:

```bash
java -cp bin Main
```

**CLI Menu Options:**
1. **Encrypt a message**: Enter plaintext and any shift key to get ciphertext.
2. **Decrypt a message**: Enter ciphertext and the shift key to recover plaintext.
3. **Brute-force crack**: Enter ciphertext to inspect all 25 possible shift variations.
4. **Run quick demonstration**: Performs an automated encryption/decryption integrity round-trip.
5. **Exit**: Closes the application.

### Running the Automated Tests

Run the standalone test suite:

```bash
java -cp bin CaesarCipherTest
```

Expected output:
```text
Running Caesar Cipher Test Suite...

[PASS] Standard uppercase shift (k=3)
[PASS] Standard lowercase shift (k=3)
[PASS] Decryption reverses encryption
[PASS] Encrypt with mixed case
[PASS] Decrypt with mixed case
[PASS] Decrypt text preserving symbols/digits
[PASS] Large positive shift (k=29 equals k=3)
[PASS] Full 2-rotation shift (k=52 equals k=0)
[PASS] Negative shift (k=-3 equals k=23)
[PASS] Decrypt with negative shift reverses correctly
[PASS] Shift of 0 returns identical string
[PASS] Decrypt with shift 0 returns identical string
[PASS] Empty string encryption
[PASS] Empty string decryption
[PASS] Null input encryption
[PASS] Null input decryption
[PASS] Brute-force returns exactly 25 combinations
[PASS] Brute-force contains original text at key 11
[PASS] Instance encrypt matches static encrypt
[PASS] Instance decrypt matches static decrypt
[PASS] Instance getShift returns normalized shift

--------------------------------------------------
Test Results: 21 passed, 0 failed.
--------------------------------------------------
```

---

## Usage as a Library

You can easily integrate `CaesarCipher` into other Java classes.

### Static Utility API

```java
// Encrypt
String encrypted = CaesarCipher.encrypt("Hello, World!", 3);
// Output: "Khoor, Zruog!"

// Decrypt
String decrypted = CaesarCipher.decrypt(encrypted, 3);
// Output: "Hello, World!"

// Negative shift
String shiftedBack = CaesarCipher.encrypt("Hello, World!", -3);
// Output: "Ebiil, Tloia!"
```

### Object-Oriented API

```java
CaesarCipher cipher = new CaesarCipher(7);

String ciphertext = cipher.encrypt("Secret Document");
String plaintext = cipher.decrypt(ciphertext);
```

### Brute-Force Cracking

```java
import java.util.Map;

Map<Integer, String> possibleDecryptions = CaesarCipher.bruteForce("KHOOR");
for (Map.Entry<Integer, String> entry : possibleDecryptions.entrySet()) {
    System.out.printf("Key %2d: %s%n", entry.getKey(), entry.getValue());
}
// Key 3: HELLO
```

---

## Security Disclaimer

> [!CAUTION]
> The Caesar Cipher has only 25 unique non-trivial keys and provides **no cryptographic security** against modern computation or frequency analysis. It is implemented here for educational and demonstration purposes only. For securing sensitive data in production systems, use modern cryptographic algorithms such as AES-GCM via the `javax.crypto` package.
