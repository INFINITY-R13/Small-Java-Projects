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

JAVA="${JAVA_HOME:+$JAVA_HOME/bin/}java"

# If AuraEdit.jar does not exist, build first
if [ ! -f "AuraEdit.jar" ]; then
    ./build.sh
fi

echo "==> Launching AuraEdit..."
exec "$JAVA" -jar AuraEdit.jar "$@"
