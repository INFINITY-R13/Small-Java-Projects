#!/usr/bin/env bash
#
# Blackjack Game Launcher and Build Script
#

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BIN_DIR="${SCRIPT_DIR}/bin"
SRC_DIR="${SCRIPT_DIR}/src"
TEST_DIR="${SCRIPT_DIR}/test"

# Discover Java & Javac
find_jdk() {
    if command -v javac >/dev/null 2>&1 && command -v java >/dev/null 2>&1 && java -version >/dev/null 2>&1; then
        JAVAC_BIN="javac"
        JAVA_BIN="java"
        return
    fi

    # Check common macOS Temurin / OpenJDK paths
    local candidate_dirs=(
        "/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home"
        "/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home"
        "/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home"
        "/opt/homebrew/opt/openjdk/bin"
        "/usr/local/opt/openjdk/bin"
    )

    for dir in "${candidate_dirs[@]}"; do
        if [ -x "${dir}/bin/javac" ] && [ -x "${dir}/bin/java" ]; then
            JAVAC_BIN="${dir}/bin/javac"
            JAVA_BIN="${dir}/bin/java"
            return
        elif [ -x "${dir}/javac" ] && [ -x "${dir}/java" ]; then
            JAVAC_BIN="${dir}/javac"
            JAVA_BIN="${dir}/java"
            return
        fi
    done

    # Fallback to JAVA_HOME if set
    if [ -n "${JAVA_HOME}" ] && [ -x "${JAVA_HOME}/bin/javac" ]; then
        JAVAC_BIN="${JAVA_HOME}/bin/javac"
        JAVA_BIN="${JAVA_HOME}/bin/java"
        return
    fi

    echo "Error: Java Development Kit (JDK) not found. Please install a JDK (version 17 or newer recommended)." >&2
    exit 1
}

find_jdk

build_project() {
    echo "Compiling Java sources..."
    mkdir -p "${BIN_DIR}"
    local sources
    sources=$(find "${SRC_DIR}" "${TEST_DIR}" -name "*.java")
    # shellcheck disable=SC2086
    "${JAVAC_BIN}" -d "${BIN_DIR}" ${sources}
    echo "Build complete."
}

clean_project() {
    echo "Cleaning build artifacts..."
    rm -rf "${BIN_DIR}"
    echo "Clean complete."
}

run_tests() {
    build_project
    echo ""
    "${JAVA_BIN}" -cp "${BIN_DIR}" com.blackjack.BlackjackTest
}

run_game() {
    if [ ! -d "${BIN_DIR}/com/blackjack" ]; then
        build_project
        echo ""
    fi
    "${JAVA_BIN}" -cp "${BIN_DIR}" com.blackjack.Main "$@"
}

case "$1" in
    build)
        build_project
        ;;
    clean)
        clean_project
        ;;
    test)
        run_tests
        ;;
    help|--help|-h)
        echo "Usage: ./run.sh [build|clean|test|help|<game arguments>]"
        echo "  (No arguments runs the interactive game)"
        ;;
    *)
        run_game "$@"
        ;;
esac
