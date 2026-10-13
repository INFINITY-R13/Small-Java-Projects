#!/usr/bin/env bash
set -e

# Resolve JDK
if [ -z "$JAVA_HOME" ]; then
    if [ -d "/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home" ]; then
        export JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home"
    elif [ -x "/usr/libexec/java_home" ]; then
        export JAVA_HOME="$(/usr/libexec/java_home 2>/dev/null || true)"
    fi
fi

JAVAC="${JAVA_HOME:+$JAVA_HOME/bin/}javac"
JAR="${JAVA_HOME:+$JAVA_HOME/bin/}jar"

echo "==> Compiling AuraEdit..."
mkdir -p bin
"$JAVAC" -d bin src/aura/*.java src/aura/*/*.java

echo "==> Packaging AuraEdit.jar..."
"$JAR" --create --file AuraEdit.jar --main-class aura.AuraEdit -C bin .

echo "==> Build complete! You can run the app with ./run.sh or: java -jar AuraEdit.jar"
