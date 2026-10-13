#!/usr/bin/env bash
set -e

# Find Java tools
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/jar" ]; then
    JAR="$JAVA_HOME/bin/jar"
elif [ -x "/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home/bin/jar" ]; then
    JAR="/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home/bin/jar"
elif command -v jar >/dev/null 2>&1; then
    JAR="jar"
else
    echo "Error: jar command could not be found."
    exit 1
fi

./build.sh

echo "==> Creating manifest..."
echo "Main-Class: com.endless.EndlessApp" > /tmp/manifest.mf

echo "==> Packaging Endless.jar..."
$JAR cfm Endless.jar /tmp/manifest.mf -C build .
rm /tmp/manifest.mf

echo "==> Endless.jar created successfully!"
