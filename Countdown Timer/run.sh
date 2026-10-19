#!/usr/bin/env bash
# ==============================================================================
# Run Script for Java Countdown Timer
# Launches the Countdown Timer (CLI, GUI, or direct duration)
# ==============================================================================

set -e

# Resolve script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Locate Java Runtime
if [ -z "$JAVA_HOME" ]; then
    if [ -d "/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home" ]; then
        export JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home"
    elif [ -x "$(command -v /usr/libexec/java_home 2>/dev/null)" ]; then
        DETECTED_HOME="$(/usr/libexec/java_home 2>/dev/null || true)"
        if [ -n "$DETECTED_HOME" ]; then
            export JAVA_HOME="$DETECTED_HOME"
        fi
    fi
fi

if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA="$JAVA_HOME/bin/java"
else
    JAVA="java"
fi

# If JAR doesn't exist, build first
if [ ! -f "countdown-timer.jar" ] || [ ! -d "bin" ]; then
    echo "⚡ No compiled build found. Building first..."
    ./build.sh
fi

exec "$JAVA" -jar countdown-timer.jar "$@"
