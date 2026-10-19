package com.timer;

/**
 * Represents the lifecycle states of a countdown timer.
 */
public enum TimerState {
    /** Timer has not started yet or has been reset. */
    READY,
    /** Timer is actively counting down. */
    RUNNING,
    /** Timer is currently paused. */
    PAUSED,
    /** Timer reached 0 and completed successfully. */
    COMPLETED,
    /** Timer was manually stopped/cancelled before reaching 0. */
    STOPPED
}
