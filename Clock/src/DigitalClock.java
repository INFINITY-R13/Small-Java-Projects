import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * DigitalClock - A Swing-based digital clock application.
 *
 * Displays the current time (HH:mm:ss) and date, with a modern dark UI.
 * The clock updates every second using a java.util.Timer.
 */
public class DigitalClock extends JFrame {

    // ── UI Components ─────────────────────────────────────────────────────────
    private final JLabel timeLabel;
    private final JLabel dateLabel;
    private final JLabel amPmLabel;

    // ── Date Formatters ───────────────────────────────────────────────────────
    private final SimpleDateFormat timeFormat  = new SimpleDateFormat("hh:mm:ss");
    private final SimpleDateFormat dateFormat  = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
    private final SimpleDateFormat amPmFormat  = new SimpleDateFormat("a");

    // ── Colors ────────────────────────────────────────────────────────────────
    private static final Color BG_COLOR      = new Color(18, 18, 24);
    private static final Color TIME_COLOR    = new Color(0, 230, 200);
    private static final Color DATE_COLOR    = new Color(160, 160, 180);
    private static final Color AM_PM_COLOR   = new Color(0, 200, 170);
    private static final Color BORDER_COLOR  = new Color(40, 40, 60);

    public DigitalClock() {
        // ── Frame setup ───────────────────────────────────────────────────────
        super("Digital Clock");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // ── Root panel ────────────────────────────────────────────────────────
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG_COLOR);
        root.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 2),
                BorderFactory.createEmptyBorder(30, 50, 30, 50)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill  = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 4, 0);

        // ── Time row (HH:mm:ss + AM/PM) ───────────────────────────────────────
        JPanel timeRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        timeRow.setBackground(BG_COLOR);

        timeLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        timeLabel.setFont(loadMonoFont(72f));
        timeLabel.setForeground(TIME_COLOR);
        timeRow.add(timeLabel);

        amPmLabel = new JLabel("AM", SwingConstants.LEFT);
        amPmLabel.setFont(loadMonoFont(22f));
        amPmLabel.setForeground(AM_PM_COLOR);
        amPmLabel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        timeRow.add(amPmLabel);

        gbc.gridy = 0;
        root.add(timeRow, gbc);

        // ── Separator ─────────────────────────────────────────────────────────
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COLOR);
        sep.setBackground(BORDER_COLOR);
        gbc.gridy = 1;
        gbc.insets = new Insets(6, 0, 6, 0);
        root.add(sep, gbc);

        // ── Date label ────────────────────────────────────────────────────────
        dateLabel = new JLabel("", SwingConstants.CENTER);
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        dateLabel.setForeground(DATE_COLOR);
        gbc.gridy = 2;
        gbc.insets = new Insets(4, 0, 4, 0);
        root.add(dateLabel, gbc);

        setContentPane(root);

        // ── Initial tick + pack ───────────────────────────────────────────────
        tick();
        pack();
        setLocationRelativeTo(null); // center on screen
    }

    /**
     * Returns a monospaced font for the time display.
     * Falls back to the system monospaced font if a custom one is unavailable.
     */
    private Font loadMonoFont(float size) {
        // Try to get a nice monospaced font; fall back gracefully
        for (String name : new String[]{"Consolas", "Courier New", "Monospaced"}) {
            Font f = new Font(name, Font.BOLD, (int) size);
            if (!f.getFamily().equals("Dialog")) {
                return f.deriveFont(size);
            }
        }
        return new Font(Font.MONOSPACED, Font.BOLD, (int) size);
    }

    /**
     * Updates the time and date labels to the current system time.
     * Always called on the Event Dispatch Thread.
     */
    private void tick() {
        Date now = new Date();
        timeLabel.setText(timeFormat.format(now));
        dateLabel.setText(dateFormat.format(now));
        amPmLabel.setText(amPmFormat.format(now));
    }

    /**
     * Starts the background timer that fires a tick every second.
     */
    public void startClock() {
        Timer timer = new Timer(true); // daemon timer — exits with the app
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Always update Swing components on the EDT
                SwingUtilities.invokeLater(DigitalClock.this::tick);
            }
        }, 0, 1000);
    }

    // ── Entry Point ───────────────────────────────────────────────────────────
    public static void main(String[] args) {
        // Set system look-and-feel for native window decorations
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { /* keep default L&F */ }

        SwingUtilities.invokeLater(() -> {
            DigitalClock clock = new DigitalClock();
            clock.startClock();
            clock.setVisible(true);
        });
    }
}
