#!/usr/bin/env bash
set -e

# Resolve directory of this script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Ensure a functional Java runtime is configured
if ! java -version >/dev/null 2>&1; then
    for jvm in /Library/Java/JavaVirtualMachines/*/Contents/Home /opt/homebrew/opt/openjdk*; do
        if [ -x "$jvm/bin/java" ]; then
            export JAVA_HOME="$jvm"
            export PATH="$JAVA_HOME/bin:$PATH"
            break
        fi
    done
fi

# Create output dir and compile
mkdir -p bin
javac -d bin src/bagels/*.java

# Run the game
exec java -cp bin bagels.Bagels "$@"
