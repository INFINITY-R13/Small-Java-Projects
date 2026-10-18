#!/usr/bin/env bash

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

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

if [ ! -f "CalendarMaker.jar" ]; then
    echo "CalendarMaker.jar not found. Running build first..."
    ./build.sh
fi

java -jar CalendarMaker.jar "$@"
