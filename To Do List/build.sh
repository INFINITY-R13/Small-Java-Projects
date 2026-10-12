#!/usr/bin/env bash
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

# 1. Detect javac
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/javac" ]; then
    JAVAC_CMD="$JAVA_HOME/bin/javac"
    JAR_CMD="$JAVA_HOME/bin/jar"
elif [ -x "/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home/bin/javac" ]; then
    JAVAC_CMD="/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home/bin/javac"
    JAR_CMD="/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home/bin/jar"
elif javac -version >/dev/null 2>&1; then
    JAVAC_CMD="javac"
    JAR_CMD="jar"
else
    echo "Error: Working Java compiler (javac) not found. Please install a JDK or set JAVA_HOME."
    exit 1
fi

echo "==> Using Java compiler: $JAVAC_CMD"
"$JAVAC_CMD" -version

echo "==> Compiling Java sources..."
rm -rf bin
mkdir -p bin

find src -name "*.java" > sources.txt
"$JAVAC_CMD" -d bin @sources.txt
rm sources.txt

echo "==> Packaging executable TodoList.jar..."
"$JAR_CMD" --create --file TodoList.jar --main-class todolist.Main -C bin .

echo "==> Build successful! TodoList.jar created."
