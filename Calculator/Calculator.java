import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import javax.swing.*;

/**
 * Scientific Calculator — Java Swing
 *
 * <p>Supports basic arithmetic as well as scientific / advanced mathematics:
 * trigonometric functions (sin, cos, tan and their inverses), hyperbolic
 * functions (sinh, cosh, tanh), logarithms (log₁₀, ln), square root, cube
 * root, arbitrary power (xʸ), reciprocal (1/x), factorial, modulo, absolute
 * value, floor / ceiling, memory (MS / MR / M+ / MC), and a DEG / RAD
 * angle‑mode toggle.
 */
public class Calculator extends JFrame {

    // ── Display ─────────────────────────────────────────────────────────────
    private final JTextField display     = new JTextField("0");
    private final JLabel     expression  = new JLabel(" ", SwingConstants.RIGHT);
    private final JLabel     modeLabel   = new JLabel("DEG", SwingConstants.LEFT);
    private final JLabel     memLabel    = new JLabel("", SwingConstants.RIGHT);

    // ── State ────────────────────────────────────────────────────────────────
    private double  storedValue    = 0;
    private String  pendingOperator = null;
    private boolean startNewNumber  = true;
    private boolean isDegrees       = true;   // angle mode
    private double  memory          = 0;

    // ── History (last 10 expressions) ────────────────────────────────────────
    private final Deque<String> history = new ArrayDeque<>(10);

    // ── Colours ──────────────────────────────────────────────────────────────
    private static final Color COL_BG      = new Color(245, 245, 250);
    private static final Color COL_DISPLAY = Color.WHITE;
    private static final Color COL_NUM     = new Color(255, 255, 255);
    private static final Color COL_OP      = new Color(210, 228, 255);
    private static final Color COL_SCI     = new Color(220, 255, 230);
    private static final Color COL_UTIL    = new Color(238, 238, 238);
    private static final Color COL_EQ      = new Color(70,  130, 230);
    private static final Color COL_MEM     = new Color(255, 240, 200);

    // ── Constructor ──────────────────────────────────────────────────────────
    private Calculator() {
        super("Scientific Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 680);
        setMinimumSize(new Dimension(480, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(COL_BG);
        setLayout(new BorderLayout(0, 4));

        add(buildScreen(),    BorderLayout.NORTH);
        add(buildKeypad(),    BorderLayout.CENTER);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  UI builders
    // ════════════════════════════════════════════════════════════════════════

    private JPanel buildScreen() {
        JPanel screen = new JPanel(new BorderLayout(4, 0));
        screen.setBorder(BorderFactory.createEmptyBorder(12, 16, 4, 16));
        screen.setBackground(COL_BG);

        // top row: mode label + memory indicator
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(COL_BG);
        modeLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        modeLabel.setForeground(new Color(70, 130, 230));
        memLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        memLabel.setForeground(new Color(180, 120, 0));
        topRow.add(modeLabel, BorderLayout.WEST);
        topRow.add(memLabel,  BorderLayout.EAST);

        expression.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        expression.setForeground(new Color(105, 105, 105));

        display.setEditable(false);
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 38));
        display.setBackground(COL_DISPLAY);
        display.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        screen.add(topRow,      BorderLayout.NORTH);
        screen.add(expression,  BorderLayout.CENTER);
        screen.add(display,     BorderLayout.SOUTH);
        return screen;
    }

    /**
     * Builds the full keypad.  Layout:
     *
     *  Row 0 – DEG/RAD │ MC  │ MR  │ MS  │ M+
     *  Row 1 – sin  cos  tan  asin  acos  atan
     *  Row 2 – sinh cosh tanh log  ln   √
     *  Row 3 – xʸ   x²  x³   ∛x   1/x  n!
     *  Row 4 – |x|  mod  ⌊x⌋  ⌈x⌉  π    e
     *  Row 5 –  C    ⌫   ±   ÷   (history)
     *  Row 6 –  7    8   9   ×
     *  Row 7 –  4    5   6   −
     *  Row 8 –  1    2   3   +
     *  Row 9 –  0        .   =
     */
    private JPanel buildKeypad() {
        JPanel outer = new JPanel(new BorderLayout(0, 6));
        outer.setBackground(COL_BG);
        outer.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        // ── Scientific rows (5 rows × 6 cols) ──────────────────────────────
        String[][] sciRows = {
            { "DEG/RAD", "MC", "MR", "MS", "M+", "M−" },
            { "sin",  "cos",  "tan",  "asin",  "acos",  "atan"  },
            { "sinh", "cosh", "tanh", "log",   "ln",    "√"     },
            { "xʸ",   "x²",   "x³",   "∛x",   "1/x",  "n!"    },
            { "|x|",  "mod",  "⌊x⌋",  "⌈x⌉",  "π",    "e"     },
        };

        JPanel sciPanel = new JPanel(new GridLayout(sciRows.length, 6, 5, 5));
        sciPanel.setBackground(COL_BG);
        for (String[] row : sciRows) {
            for (String label : row) {
                JButton btn = makeButton(label, colourFor(label));
                sciPanel.add(btn);
            }
        }

        // ── Basic rows (5 rows × 4 cols) ────────────────────────────────────
        String[][] basicRows = {
            { "C",  "⌫",  "±",  "÷" },
            { "7",  "8",  "9",  "×" },
            { "4",  "5",  "6",  "−" },
            { "1",  "2",  "3",  "+" },
            { "0",  ".",  "=",  ""  },
        };

        JPanel basicPanel = new JPanel(new GridLayout(basicRows.length, 4, 5, 5));
        basicPanel.setBackground(COL_BG);
        for (String[] row : basicRows) {
            for (String label : row) {
                if (label.isEmpty()) {
                    basicPanel.add(new JLabel()); // spacer
                } else {
                    JButton btn = makeButton(label, colourFor(label));
                    if (label.equals("=")) {
                        btn.setForeground(Color.WHITE);
                    }
                    basicPanel.add(btn);
                }
            }
        }

        outer.add(sciPanel,   BorderLayout.NORTH);
        outer.add(basicPanel, BorderLayout.CENTER);
        return outer;
    }

    private JButton makeButton(String label, Color bg) {
        JButton btn = new JButton(label);
        btn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        btn.setFocusPainted(false);
        btn.setBackground(bg);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 210), 1, true),
                BorderFactory.createEmptyBorder(6, 4, 6, 4)));
        btn.setActionCommand(label);
        btn.addActionListener(e -> handleKey(e.getActionCommand()));
        // hover effect
        btn.addMouseListener(new MouseAdapter() {
            final Color base = bg;
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(base.darker()); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(base); }
        });
        return btn;
    }

    private Color colourFor(String label) {
        return switch (label) {
            case "="                          -> COL_EQ;
            case "+", "−", "×", "÷", "mod"   -> COL_OP;
            case "C", "⌫", "±"               -> COL_UTIL;
            case "MC","MR","MS","M+","M−"     -> COL_MEM;
            case "DEG/RAD"                    -> new Color(230, 220, 255);
            default -> label.matches("\\d")   ? COL_NUM : COL_SCI;
        };
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Key dispatcher
    // ════════════════════════════════════════════════════════════════════════

    private void handleKey(String key) {
        if (key.matches("\\d"))             { enterDigit(key);         return; }
        if (key.equals("."))                { enterDecimalPoint();      return; }
        if (key.equals("C"))                { clear();                  return; }
        if (key.equals("⌫"))               { backspace();              return; }
        if (key.equals("±"))               { changeSign();             return; }
        if (key.equals("="))                { calculate();              return; }
        if (key.equals("DEG/RAD"))          { toggleMode();             return; }
        if (key.equals("π"))                { enterConstant(Math.PI);   return; }
        if (key.equals("e"))                { enterConstant(Math.E);    return; }
        if (isBinaryOp(key))               { chooseBinaryOp(key);      return; }
        // memory
        switch (key) {
            case "MC" -> { memory = 0;                  updateMemLabel(); return; }
            case "MR" -> { enterConstant(memory);                         return; }
            case "MS" -> { memory = readDisplay();      updateMemLabel(); return; }
            case "M+" -> { memory += readDisplay();     updateMemLabel(); return; }
            case "M−" -> { memory -= readDisplay();     updateMemLabel(); return; }
        }
        // unary / scientific
        applyUnary(key);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Digit / display entry helpers
    // ════════════════════════════════════════════════════════════════════════

    private void enterDigit(String digit) {
        if (startNewNumber || display.getText().equals("Error")) {
            display.setText(digit);
            startNewNumber = false;
        } else if (display.getText().equals("0")) {
            display.setText(digit);
        } else {
            display.setText(display.getText() + digit);
        }
    }

    private void enterDecimalPoint() {
        if (startNewNumber || display.getText().equals("Error")) {
            display.setText("0.");
            startNewNumber = false;
        } else if (!display.getText().contains(".")) {
            display.setText(display.getText() + ".");
        }
    }

    private void enterConstant(double value) {
        display.setText(format(value));
        startNewNumber = false;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Binary operators  (+, −, ×, ÷, xʸ, mod)
    // ════════════════════════════════════════════════════════════════════════

    private void chooseBinaryOp(String operator) {
        if (display.getText().equals("Error")) { clear(); return; }
        double current = readDisplay();
        if (pendingOperator != null && !startNewNumber) {
            Double result = applyBinary(storedValue, current, pendingOperator);
            if (result == null) { showError("Undefined"); return; }
            display.setText(format(result));
            storedValue = result;
        } else {
            storedValue = current;
        }
        pendingOperator = operator;
        startNewNumber  = true;
        expression.setText(format(storedValue) + " " + operator);
    }

    private void calculate() {
        if (pendingOperator == null || display.getText().equals("Error")) return;
        double right = readDisplay();
        String expr  = format(storedValue) + " " + pendingOperator + " " + format(right) + " =";
        Double result = applyBinary(storedValue, right, pendingOperator);
        if (result == null) { showError("Undefined"); return; }
        pushHistory(expr + " " + format(result));
        display.setText(format(result));
        expression.setText(expr);
        storedValue     = result;
        pendingOperator = null;
        startNewNumber  = true;
    }

    /**
     * Applies a binary operator and returns {@code null} for undefined results
     * (e.g., division by zero).
     */
    private Double applyBinary(double left, double right, String op) {
        return switch (op) {
            case "+"  -> left + right;
            case "−"  -> left - right;
            case "×"  -> left * right;
            case "÷"  -> right == 0 ? null : left / right;
            case "xʸ" -> Math.pow(left, right);
            case "mod"-> right == 0 ? null : left % right;
            default   -> right;
        };
    }

    private boolean isBinaryOp(String key) {
        return switch (key) {
            case "+", "−", "×", "÷", "xʸ", "mod" -> true;
            default -> false;
        };
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Unary / scientific functions
    // ════════════════════════════════════════════════════════════════════════

    private void applyUnary(String fn) {
        if (display.getText().equals("Error")) return;
        double x      = readDisplay();
        double radians = isDegrees ? Math.toRadians(x) : x;   // for trig input
        String label   = fn + "(" + format(x) + ")";

        Double result = switch (fn) {
            // ── Trigonometric (input in current angle mode) ──────────────
            case "sin"  -> Math.sin(radians);
            case "cos"  -> Math.cos(radians);
            case "tan"  -> {
                if (isDegrees && x % 180 == 90) yield null;   // asymptote
                yield Math.tan(radians);
            }
            // ── Inverse trig (output converted back to current angle mode)
            case "asin" -> {
                if (x < -1 || x > 1) yield null;
                double r = Math.asin(x);
                yield isDegrees ? Math.toDegrees(r) : r;
            }
            case "acos" -> {
                if (x < -1 || x > 1) yield null;
                double r = Math.acos(x);
                yield isDegrees ? Math.toDegrees(r) : r;
            }
            case "atan" -> {
                double r = Math.atan(x);
                yield isDegrees ? Math.toDegrees(r) : r;
            }
            // ── Hyperbolic ───────────────────────────────────────────────
            case "sinh" -> Math.sinh(x);
            case "cosh" -> Math.cosh(x);
            case "tanh" -> Math.tanh(x);
            // ── Logarithms ───────────────────────────────────────────────
            case "log"  -> x <= 0 ? null : Math.log10(x);
            case "ln"   -> x <= 0 ? null : Math.log(x);
            // ── Roots & powers ───────────────────────────────────────────
            case "√"    -> x < 0  ? null : Math.sqrt(x);
            case "∛x"   -> Math.cbrt(x);
            case "x²"   -> x * x;
            case "x³"   -> x * x * x;
            // ── Other ────────────────────────────────────────────────────
            case "1/x"  -> x == 0  ? null : 1.0 / x;
            case "n!"   -> factorial(x);
            case "|x|"  -> Math.abs(x);
            case "⌊x⌋"  -> Math.floor(x);
            case "⌈x⌉"  -> Math.ceil(x);
            default     -> x;
        };

        if (result == null || !Double.isFinite(result)) {
            showError("Math Error");
        } else {
            pushHistory(label + " = " + format(result));
            expression.setText(label + " =");
            display.setText(format(result));
            startNewNumber = true;
        }
    }

    /**
     * Computes n! for non-negative integers up to 170 (beyond that, overflow).
     * Returns {@code null} for invalid inputs (negative or non-integer, &gt;170).
     */
    private Double factorial(double n) {
        if (n < 0 || n != Math.floor(n) || n > 170) return null;
        double result = 1;
        for (int i = 2; i <= (int) n; i++) result *= i;
        return result;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Editing helpers
    // ════════════════════════════════════════════════════════════════════════

    private void backspace() {
        if (startNewNumber || display.getText().equals("Error")) return;
        String v = display.getText();
        if (v.length() <= 1 || (v.length() == 2 && v.startsWith("-"))) {
            display.setText("0");
            startNewNumber = true;
        } else {
            display.setText(v.substring(0, v.length() - 1));
        }
    }

    private void changeSign() {
        if (display.getText().equals("Error") || readDisplay() == 0) return;
        display.setText(display.getText().startsWith("-")
                ? display.getText().substring(1)
                : "-" + display.getText());
    }

    private void toggleMode() {
        isDegrees = !isDegrees;
        modeLabel.setText(isDegrees ? "DEG" : "RAD");
    }

    private void updateMemLabel() {
        memLabel.setText(memory == 0 ? "" : "M=" + format(memory));
    }

    private void clear() {
        display.setText("0");
        expression.setText(" ");
        storedValue     = 0;
        pendingOperator = null;
        startNewNumber  = true;
    }

    private void showError(String msg) {
        display.setText("Error");
        expression.setText(msg);
        storedValue     = 0;
        pendingOperator = null;
        startNewNumber  = true;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  History
    // ════════════════════════════════════════════════════════════════════════

    private void pushHistory(String entry) {
        if (history.size() >= 10) history.pollFirst();
        history.addLast(entry);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Number helpers
    // ════════════════════════════════════════════════════════════════════════

    private double readDisplay() {
        try {
            return Double.parseDouble(display.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String format(double value) {
        if (!Double.isFinite(value)) return "Error";
        // Use scientific notation for very large or very small values
        if (Math.abs(value) >= 1e15 || (value != 0 && Math.abs(value) < 1e-9)) {
            DecimalFormat sci = new DecimalFormat("0.##########E0",
                    DecimalFormatSymbols.getInstance(Locale.ROOT));
            return sci.format(value);
        }
        DecimalFormat fmt = new DecimalFormat("0.##########",
                DecimalFormatSymbols.getInstance(Locale.ROOT));
        return fmt.format(value);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Entry point
    // ════════════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Calculator().setVisible(true));
    }
}
