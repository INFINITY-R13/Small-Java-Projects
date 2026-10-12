#!/usr/bin/env bash
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

# 1. Detect java
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA_CMD="$JAVA_HOME/bin/java"
elif [ -x "/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home/bin/java" ]; then
    JAVA_CMD="/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home/bin/java"
elif java -version >/dev/null 2>&1; then
    JAVA_CMD="java"
else
    echo "Error: Working Java runtime (java) not found. Please install Java or set JAVA_HOME."
    exit 1
fi

# Build if TodoList.jar doesn't exist
if [ ! -f "TodoList.jar" ]; then
    echo "TodoList.jar not found. Running build..."
    ./build.sh
fi

echo "==> Launching To-Do List Application with $JAVA_CMD..."
exec "$JAVA_CMD" -jar TodoList.jar
