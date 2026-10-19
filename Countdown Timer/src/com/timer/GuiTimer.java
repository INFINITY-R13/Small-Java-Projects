package com.timer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Modern Swing Graphical User Interface for the Countdown Timer.
 */
public class GuiTimer extends JFrame implements TimerListener {

    private final CountdownTimer timer = new CountdownTimer();

    // Color Palette
    private static final Color BG_DARK = new Color(24, 31, 46);
    private static final Color CARD_BG = new Color(33, 42, 62);
    private static final Color ACCENT_CYAN = new Color(56, 189, 248);
    private static final Color ACCENT_GREEN = new Color(34, 197, 94);
    private static final Color ACCENT_AMBER = new Color(245, 158, 11);
    private static final Color ACCENT_RED = new Color(239, 68, 68);
    private static final Color TEXT_LIGHT = new Color(241, 245, 249);
    private static final Color TEXT_MUTED = new Color(148, 163, 184);

    // UI Components
    private JLabel clockLabel;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private JButton startPauseButton;
    private JButton resetButton;

    private JSpinner hoursSpinner;
    private JSpinner minutesSpinner;
    private JSpinner secondsSpinner;

    private JCheckBox pinToTopCheck;
    private JCheckBox soundCheck;

    private long configuredSeconds = 25 * 60; // Default to 25 mins

    public GuiTimer() {
        super("Countdown Timer");
        timer.addListener(this);
        initUI();
    }

    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            GuiTimer gui = new GuiTimer();
            gui.setVisible(true);
        });
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(460, 520));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(12, 12));

        // 1. Top Card: Digital Clock Display & Status
        JPanel displayCard = new JPanel(new BorderLayout(8, 8));
        displayCard.setBackground(CARD_BG);
        displayCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        statusLabel = new JLabel("READY", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        statusLabel.setForeground(ACCENT_CYAN);

        clockLabel = new JLabel("00:25:00", SwingConstants.CENTER);
        clockLabel.setFont(new Font("Monospaced", Font.BOLD, 54));
        clockLabel.setForeground(TEXT_LIGHT);

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(ACCENT_CYAN);
        progressBar.setBackground(BG_DARK);
        progressBar.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        displayCard.add(statusLabel, BorderLayout.NORTH);
        displayCard.add(clockLabel, BorderLayout.CENTER);
        displayCard.add(progressBar, BorderLayout.SOUTH);

        add(displayCard, BorderLayout.NORTH);

        // 2. Middle Panel: Presets & Custom Input
        JPanel controlsPanel = new JPanel(new GridBagLayout());
        controlsPanel.setBackground(BG_DARK);
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        // Preset buttons row
        JPanel presetsPanel = new JPanel(new GridLayout(1, 4, 8, 0));
        presetsPanel.setBackground(BG_DARK);
        JButton pomodoroBtn = createStyledButton("Pomodoro (25m)", CARD_BG, TEXT_LIGHT);
        JButton shortBreakBtn = createStyledButton("Short (5m)", CARD_BG, TEXT_LIGHT);
        JButton longBreakBtn = createStyledButton("Long (15m)", CARD_BG, TEXT_LIGHT);
        JButton quickBtn = createStyledButton("Quick (1m)", CARD_BG, TEXT_LIGHT);

        pomodoroBtn.addActionListener(e -> setPreset(25 * 60));
        shortBreakBtn.addActionListener(e -> setPreset(5 * 60));
        longBreakBtn.addActionListener(e -> setPreset(15 * 60));
        quickBtn.addActionListener(e -> setPreset(60));

        presetsPanel.add(pomodoroBtn);
        presetsPanel.add(shortBreakBtn);
        presetsPanel.add(longBreakBtn);
        presetsPanel.add(quickBtn);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        controlsPanel.add(presetsPanel, gbc);

        // Custom Spinner inputs (Hours, Minutes, Seconds)
        JPanel timeInputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        timeInputPanel.setBackground(BG_DARK);

        hoursSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
        minutesSpinner = new JSpinner(new SpinnerNumberModel(25, 0, 59, 1));
        secondsSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));

        styleSpinner(hoursSpinner);
        styleSpinner(minutesSpinner);
        styleSpinner(secondsSpinner);

        timeInputPanel.add(new JLabel("H:"));
        timeInputPanel.add(hoursSpinner);
        timeInputPanel.add(new JLabel("M:"));
        timeInputPanel.add(minutesSpinner);
        timeInputPanel.add(new JLabel("S:"));
        timeInputPanel.add(secondsSpinner);

        JButton applyBtn = createStyledButton("Apply", CARD_BG, ACCENT_CYAN);
        applyBtn.addActionListener(e -> applyCustomDuration());
        timeInputPanel.add(applyBtn);

        gbc.gridy = 1;
        controlsPanel.add(timeInputPanel, gbc);

        // Quick add buttons (+1m, +5m)
        JPanel quickAddPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
        quickAddPanel.setBackground(BG_DARK);
        JButton add1mBtn = createStyledButton("+1 Min", CARD_BG, TEXT_MUTED);
        JButton add5mBtn = createStyledButton("+5 Min", CARD_BG, TEXT_MUTED);
        JButton add10mBtn = createStyledButton("+10 Min", CARD_BG, TEXT_MUTED);

        add1mBtn.addActionListener(e -> addSeconds(60));
        add5mBtn.addActionListener(e -> addSeconds(300));
        add10mBtn.addActionListener(e -> addSeconds(600));

        quickAddPanel.add(add1mBtn);
        quickAddPanel.add(add5mBtn);
        quickAddPanel.add(add10mBtn);

        gbc.gridy = 2;
        controlsPanel.add(quickAddPanel, gbc);

        add(controlsPanel, BorderLayout.CENTER);

        // 3. Bottom Panel: Actions (Start/Pause, Reset) & Preferences
        JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));
        bottomPanel.setBackground(BG_DARK);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 16, 20));

        JPanel actionButtonsPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        actionButtonsPanel.setBackground(BG_DARK);

        startPauseButton = createStyledButton("START", ACCENT_GREEN, Color.WHITE);
        startPauseButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        startPauseButton.addActionListener(e -> handleStartPause());

        resetButton = createStyledButton("RESET", ACCENT_RED, Color.WHITE);
        resetButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        resetButton.addActionListener(e -> handleReset());

        actionButtonsPanel.add(startPauseButton);
        actionButtonsPanel.add(resetButton);

        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 4));
        optionsPanel.setBackground(BG_DARK);

        pinToTopCheck = new JCheckBox("Always on Top");
        pinToTopCheck.setForeground(TEXT_MUTED);
        pinToTopCheck.setBackground(BG_DARK);
        pinToTopCheck.setFocusPainted(false);
        pinToTopCheck.addActionListener(e -> setAlwaysOnTop(pinToTopCheck.isSelected()));

        soundCheck = new JCheckBox("Sound Notification", true);
        soundCheck.setForeground(TEXT_MUTED);
        soundCheck.setBackground(BG_DARK);
        soundCheck.setFocusPainted(false);

        optionsPanel.add(pinToTopCheck);
        optionsPanel.add(soundCheck);

        bottomPanel.add(actionButtonsPanel, BorderLayout.CENTER);
        bottomPanel.add(optionsPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        updateDisplay(configuredSeconds, configuredSeconds);
    }

    private void setPreset(long seconds) {
        if (timer.isRunning()) {
            timer.stop();
        }
        this.configuredSeconds = seconds;
        hoursSpinner.setValue((int) (seconds / 3600));
        minutesSpinner.setValue((int) ((seconds % 3600) / 60));
        secondsSpinner.setValue((int) (seconds % 60));
        timer.setDuration(seconds);
        updateDisplay(seconds, seconds);
        setStatus("READY - " + TimeUtils.formatAuto(seconds), ACCENT_CYAN);
    }

    private void applyCustomDuration() {
        int h = (Integer) hoursSpinner.getValue();
        int m = (Integer) minutesSpinner.getValue();
        int s = (Integer) secondsSpinner.getValue();

        long total = (h * 3600L) + (m * 60L) + s;
        if (total <= 0) {
            JOptionPane.showMessageDialog(this, "Duration must be greater than 0.", "Invalid Duration", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (timer.isRunning()) {
            timer.stop();
        }
        this.configuredSeconds = total;
        timer.setDuration(total);
        updateDisplay(total, total);
        setStatus("READY", ACCENT_CYAN);
    }

    private void addSeconds(long extraSeconds) {
        long current = timer.isRunning() ? timer.getRemainingSeconds() : configuredSeconds;
        long updated = current + extraSeconds;
        if (timer.isRunning()) {
            timer.start(updated);
        } else {
            setPreset(updated);
        }
    }

    private void handleStartPause() {
        if (!timer.isRunning() && !timer.isPaused()) {
            // Start fresh
            try {
                timer.start(configuredSeconds);
            } catch (IllegalStateException e) {
                applyCustomDuration();
                timer.start(configuredSeconds);
            }
        } else if (timer.isRunning()) {
            timer.pause();
        } else if (timer.isPaused()) {
            timer.resume();
        }
    }

    private void handleReset() {
        timer.reset();
        updateDisplay(configuredSeconds, configuredSeconds);
        setStatus("READY", ACCENT_CYAN);
        startPauseButton.setText("START");
        startPauseButton.setBackground(ACCENT_GREEN);
        clockLabel.setForeground(TEXT_LIGHT);
        setTitle("Countdown Timer");
    }

    private void updateDisplay(long remaining, long total) {
        clockLabel.setText(TimeUtils.formatHHMMSS(remaining));
        int percent = total > 0 ? (int) Math.round(((double) remaining / (double) total) * 100) : 0;
        progressBar.setValue(percent);
        progressBar.setString(percent + "% remaining");

        if (timer.isRunning()) {
            setTitle("(" + TimeUtils.formatAuto(remaining) + ") Countdown Timer");
        }
    }

    private void setStatus(String text, Color color) {
        statusLabel.setText(text);
        statusLabel.setForeground(color);
    }

    @Override
    public void onTick(long remainingSeconds, long totalSeconds) {
        SwingUtilities.invokeLater(() -> updateDisplay(remainingSeconds, totalSeconds));
    }

    @Override
    public void onStateChange(TimerState oldState, TimerState newState) {
        SwingUtilities.invokeLater(() -> {
            switch (newState) {
                case RUNNING -> {
                    startPauseButton.setText("PAUSE");
                    startPauseButton.setBackground(ACCENT_AMBER);
                    setStatus("FOCUS TIME - RUNNING", ACCENT_GREEN);
                    clockLabel.setForeground(TEXT_LIGHT);
                }
                case PAUSED -> {
                    startPauseButton.setText("RESUME");
                    startPauseButton.setBackground(ACCENT_CYAN);
                    setStatus("PAUSED", ACCENT_AMBER);
                    setTitle("[Paused] Countdown Timer");
                }
                case COMPLETED -> {
                    startPauseButton.setText("START");
                    startPauseButton.setBackground(ACCENT_GREEN);
                    setStatus("🎉 TIME'S UP!", ACCENT_GREEN);
                    clockLabel.setForeground(ACCENT_GREEN);
                    setTitle("🎉 TIME'S UP! - Countdown Timer");
                }
                case STOPPED, READY -> {
                    startPauseButton.setText("START");
                    startPauseButton.setBackground(ACCENT_GREEN);
                    setStatus("READY", ACCENT_CYAN);
                    setTitle("Countdown Timer");
                }
            }
        });
    }

    @Override
    public void onComplete() {
        SwingUtilities.invokeLater(() -> {
            if (soundCheck.isSelected()) {
                SoundUtils.playCompletionChime();
            }
            JOptionPane.showMessageDialog(
                    this,
                    "⏰ Countdown finished!\nTake a break or start your next session.",
                    "Time's Up!",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        btn.setOpaque(true);
        return btn;
    }

    private void styleSpinner(JSpinner spinner) {
        spinner.setPreferredSize(new Dimension(55, 30));
        spinner.setBorder(BorderFactory.createLineBorder(CARD_BG.brighter()));
    }
}
