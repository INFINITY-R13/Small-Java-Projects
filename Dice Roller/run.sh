#!/usr/bin/env bash
#
# Helper script to compile and run the Java Dice Roller.
# Works out of the box on macOS and Linux.
#

set -e

# Change directory to the project root where run.sh is located
cd "$(dirname "$0")"

# Detect Java if not working or JAVA_HOME is not set
if ! java -version >/dev/null 2>&1; then
    # Check common macOS Temurin / OpenJDK locations
    for jdk in /Library/Java/JavaVirtualMachines/*/Contents/Home /opt/homebrew/opt/openjdk*; do
        if [ -d "$jdk" ] && [ -x "$jdk/bin/java" ]; then
            export JAVA_HOME="$jdk"
            export PATH="$JAVA_HOME/bin:$PATH"
            break
        fi
    done

    # Fallback to /usr/libexec/java_home
    if ! java -version >/dev/null 2>&1 && [ -x "/usr/libexec/java_home" ]; then
        DETECTED_HOME="$(/usr/libexec/java_home 2>/dev/null || true)"
        if [ -n "$DETECTED_HOME" ]; then
            export JAVA_HOME="$DETECTED_HOME"
            export PATH="$JAVA_HOME/bin:$PATH"
        fi
    fi
fi

# Ensure javac and java are available
if ! command -v javac >/dev/null 2>&1; then
    echo "[!] Error: javac could not be found. Please ensure a JDK is installed." >&2
    exit 1
fi

# Compile into bin directory
mkdir -p bin
javac -d bin src/com/diceroller/*.java

# Run application passing any command-line arguments
java -cp bin com.diceroller.Main "$@"
