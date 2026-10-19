#!/usr/bin/env bash
# ==============================================================================
# Build Script for Java Countdown Timer
# Compiles source files into bin/ and generates countdown-timer.jar
# ==============================================================================

set -e

# Resolve script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Locate Java compiler and tools
if [ -z "$JAVA_HOME" ]; then
    # Look for macOS Temurin / OpenJDK installations if default java is not linked
    if [ -d "/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home" ]; then
        export JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home"
    elif [ -x "$(command -v /usr/libexec/java_home 2>/dev/null)" ]; then
        DETECTED_HOME="$(/usr/libexec/java_home 2>/dev/null || true)"
        if [ -n "$DETECTED_HOME" ]; then
            export JAVA_HOME="$DETECTED_HOME"
        fi
    fi
fi

if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/javac" ]; then
    JAVAC="$JAVA_HOME/bin/javac"
    JAR="$JAVA_HOME/bin/jar"
else
    JAVAC="javac"
    JAR="jar"
fi

echo "🔨 Compiling Countdown Timer using $($JAVAC -version 2>&1)..."

# Ensure output directory exists
mkdir -p bin

# Compile all source files
$JAVAC -d bin src/com/timer/*.java

# Package runnable JAR file
$JAR cfe countdown-timer.jar com.timer.Main -C bin .

echo "✅ Build completed successfully!"
echo "📦 Executable JAR created: countdown-timer.jar"
echo ""
echo "To run:"
echo "  ./run.sh                    # Interactive CLI"
echo "  ./run.sh --gui              # Graphical UI"
echo "  ./run.sh 5m                 # Quick 5-minute countdown"
