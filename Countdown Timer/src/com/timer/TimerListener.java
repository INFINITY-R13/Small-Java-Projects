package com.timer;

/**
 * Listener interface for receiving timer events.
 */
public interface TimerListener {
    /**
     * Triggered every tick (usually every second).
     *
     * @param remainingSeconds Number of seconds left until 0.
     * @param totalSeconds     Initial total duration in seconds.
     */
    void onTick(long remainingSeconds, long totalSeconds);

    /**
     * Triggered when the timer changes state (e.g., RUNNING -> PAUSED).
     *
     * @param oldState Previous state.
     * @param newState New current state.
     */
    void onStateChange(TimerState oldState, TimerState newState);

    /**
     * Triggered when the countdown reaches 0.
     */
    void onComplete();
}
