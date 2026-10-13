#!/usr/bin/env bash
set -e

# Find Java runtime
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA="$JAVA_HOME/bin/java"
elif [ -x "/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home/bin/java" ]; then
    JAVA="/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home/bin/java"
elif command -v java >/dev/null 2>&1; then
    JAVA="java"
else
    echo "Error: java could not be found."
    exit 1
fi

# Compile if build directory is empty or missing
if [ ! -d "build/com/endless" ]; then
    ./build.sh
fi

echo "==> Launching Endless Music Player..."
exec "$JAVA" \
    -Dapple.awt.application.name="Endless" \
    -Dapple.laf.useScreenMenuBar=true \
    -Dapple.awt.application.appearance=system \
    -Dawt.useSystemAAFontSettings=on \
    -Dswing.aatext=true \
    -Dsun.java2d.opengl=true \
    -cp build \
    com.endless.EndlessApp "$@"
