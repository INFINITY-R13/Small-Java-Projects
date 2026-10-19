package com.timer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Thread-safe, drift-free Countdown Timer engine.
 * Supports start, pause, resume, reset, stop, and listener callbacks.
 */
public class CountdownTimer {

    private final List<TimerListener> listeners = new CopyOnWriteArrayList<>();

    private volatile TimerState state = TimerState.READY;
    private volatile long totalSeconds = 0;
    private volatile long remainingSeconds = 0;
    private volatile long endTimeMillis = 0;
    private volatile long lastDispatchedSecond = -1;

    private ScheduledExecutorService executor;
    private ScheduledFuture<?> timerTask;

    public CountdownTimer() {
        // Ready for configuration
    }

    public CountdownTimer(long initialSeconds) {
        setDuration(initialSeconds);
    }

    /**
     * Registers a listener for timer events.
     */
    public void addListener(TimerListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Unregisters a listener.
     */
    public void removeListener(TimerListener listener) {
        listeners.remove(listener);
    }

    /**
     * Sets the total duration without starting the timer.
     */
    public synchronized void setDuration(long seconds) {
        if (state == TimerState.RUNNING) {
            throw new IllegalStateException("Cannot change duration while timer is running.");
        }
        if (seconds <= 0) {
            throw new IllegalArgumentException("Duration must be > 0.");
        }
        this.totalSeconds = seconds;
        this.remainingSeconds = seconds;
        this.lastDispatchedSecond = -1;
        changeState(TimerState.READY);
    }

    /**
     * Starts the countdown timer with a specific duration in seconds.
     */
    public synchronized void start(long seconds) {
        setDuration(seconds);
        start();
    }

    /**
     * Starts the countdown using the preset duration.
     */
    public synchronized void start() {
        if (totalSeconds <= 0) {
            throw new IllegalStateException("Timer duration has not been set.");
        }
        if (state == TimerState.RUNNING) {
            return; // Already running
        }

        this.remainingSeconds = totalSeconds;
        this.endTimeMillis = System.currentTimeMillis() + (totalSeconds * 1000L);
        this.lastDispatchedSecond = totalSeconds;

        changeState(TimerState.RUNNING);
        notifyTick(remainingSeconds, totalSeconds);

        startScheduler();
    }

    /**
     * Pauses the running timer, preserving the remaining seconds.
     */
    public synchronized void pause() {
        if (state != TimerState.RUNNING) {
            return;
        }

        // Calculate exact remaining seconds at pause moment
        long now = System.currentTimeMillis();
        long currentRemaining = Math.max(0, (endTimeMillis - now + 999) / 1000);
        this.remainingSeconds = currentRemaining;

        stopScheduler();
        changeState(TimerState.PAUSED);
    }

    /**
     * Resumes a paused countdown timer.
     */
    public synchronized void resume() {
        if (state != TimerState.PAUSED) {
            return;
        }

        if (remainingSeconds <= 0) {
            changeState(TimerState.COMPLETED);
            notifyComplete();
            return;
        }

        this.endTimeMillis = System.currentTimeMillis() + (remainingSeconds * 1000L);
        this.lastDispatchedSecond = remainingSeconds;

        changeState(TimerState.RUNNING);
        startScheduler();
    }

    /**
     * Stops and cancels the timer.
     */
    public synchronized void stop() {
        if (state == TimerState.STOPPED || state == TimerState.READY) {
            return;
        }
        stopScheduler();
        changeState(TimerState.STOPPED);
    }

    /**
     * Resets the timer back to its initial ready state.
     */
    public synchronized void reset() {
        stopScheduler();
        this.remainingSeconds = totalSeconds;
        this.lastDispatchedSecond = -1;
        changeState(TimerState.READY);
        notifyTick(remainingSeconds, totalSeconds);
    }

    /**
     * Releases system resources when timer is completely discarded.
     */
    public synchronized void dispose() {
        stopScheduler();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
        }
    }

    public TimerState getState() {
        return state;
    }

    public long getTotalSeconds() {
        return totalSeconds;
    }

    public long getRemainingSeconds() {
        return remainingSeconds;
    }

    public boolean isRunning() {
        return state == TimerState.RUNNING;
    }

    public boolean isPaused() {
        return state == TimerState.PAUSED;
    }

    // --- Internal Helpers ---

    private synchronized void startScheduler() {
        stopScheduler();

        if (executor == null || executor.isShutdown()) {
            executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
                Thread t = new Thread(runnable, "CountdownTimer-Worker");
                t.setDaemon(true);
                return t;
            });
        }

        // Poll at 200ms intervals to prevent drift and ensure crisp 1-second ticks
        timerTask = executor.scheduleAtFixedRate(this::tickCheck, 0, 200, TimeUnit.MILLISECONDS);
    }

    private synchronized void stopScheduler() {
        if (timerTask != null) {
            timerTask.cancel(false);
            timerTask = null;
        }
    }

    private void tickCheck() {
        synchronized (this) {
            if (state != TimerState.RUNNING) {
                return;
            }

            long now = System.currentTimeMillis();
            long remaining = Math.max(0, (endTimeMillis - now + 999) / 1000);

            if (remaining != lastDispatchedSecond) {
                this.remainingSeconds = remaining;
                this.lastDispatchedSecond = remaining;
                notifyTick(remaining, totalSeconds);
            }

            if (remaining <= 0) {
                stopScheduler();
                changeState(TimerState.COMPLETED);
                notifyComplete();
            }
        }
    }

    private void changeState(TimerState newState) {
        TimerState oldState = this.state;
        this.state = newState;
        if (oldState != newState) {
            for (TimerListener listener : listeners) {
                try {
                    listener.onStateChange(oldState, newState);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    private void notifyTick(long remaining, long total) {
        for (TimerListener listener : listeners) {
            try {
                listener.onTick(remaining, total);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private void notifyComplete() {
        for (TimerListener listener : listeners) {
            try {
                listener.onComplete();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}
