#!/usr/bin/env bash
set -e

# Find Java compiler
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/javac" ]; then
    JAVAC="$JAVA_HOME/bin/javac"
elif [ -x "/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home/bin/javac" ]; then
    JAVAC="/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home/bin/javac"
elif command -v javac >/dev/null 2>&1; then
    JAVAC="javac"
else
    echo "Error: javac could not be found."
    exit 1
fi

echo "==> Building Endless with: $JAVAC"
mkdir -p build
$JAVAC -d build $(find src -name "*.java")
echo "==> Compilation successful!"
