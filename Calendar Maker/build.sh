#!/usr/bin/env bash
set -e

# Detect or set JAVA_HOME if not already configured
if [ -z "$JAVA_HOME" ]; then
    if [ -x "/usr/libexec/java_home" ]; then
        JAVA_HOME=$(/usr/libexec/java_home 2>/dev/null || true)
    fi
    if [ -z "$JAVA_HOME" ] && [ -d "/Library/Java/JavaVirtualMachines" ]; then
        for jvm in /Library/Java/JavaVirtualMachines/*/Contents/Home; do
            if [ -d "$jvm" ]; then
                JAVA_HOME="$jvm"
                break
            fi
        done
    fi
fi

if [ -n "$JAVA_HOME" ]; then
    export JAVA_HOME
    export PATH="$JAVA_HOME/bin:$PATH"
fi

echo "=== Building Calendar Maker ==="
echo "Using Java: $(java -version 2>&1 | head -n 1)"
echo "Using javac: $(javac -version 2>&1)"

# Create output directories
mkdir -p bin

# Compile source code
echo "Compiling sources..."
javac -d bin src/com/calendarmaker/*.java src/com/calendarmaker/*/*.java test/com/calendarmaker/*.java

# Run test suite
echo "Running automated test suite..."
java -cp bin com.calendarmaker.CalendarMakerTest

# Build executable JAR
echo "Packaging CalendarMaker.jar..."
jar cfe CalendarMaker.jar com.calendarmaker.Main -C bin com

echo "Build successful! Created CalendarMaker.jar"
