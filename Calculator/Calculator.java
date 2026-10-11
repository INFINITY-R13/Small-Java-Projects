import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/** A small desktop calculator built with Swing. */
public class Calculator extends JFrame {
    private final JTextField display = new JTextField("0");
    private final JLabel expression = new JLabel(" ", SwingConstants.RIGHT);
    private double storedValue;
    private String pendingOperator;
    private boolean startNewNumber = true;

    private Calculator() {
        super("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(340, 460);
        setMinimumSize(new java.awt.Dimension(300, 400));
        setLocationRelativeTo(null);

        JPanel screen = new JPanel(new BorderLayout(4, 0));
        screen.setBorder(BorderFactory.createEmptyBorder(12, 16, 4, 16));
        expression.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        expression.setForeground(new Color(105, 105, 105));
        display.setEditable(false);
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 38));
        display.setBackground(Color.WHITE);
        display.setBorder(BorderFactory.createEmptyBorder(8, 0, 12, 0));
        screen.add(expression, BorderLayout.NORTH);
        screen.add(display, BorderLayout.CENTER);
        add(screen, BorderLayout.NORTH);

        JPanel keys = new JPanel(new GridLayout(5, 4, 8, 8));
        keys.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));
        String[] labels = {
            "C", "⌫", "±", "÷",
            "7", "8", "9", "×",
            "4", "5", "6", "−",
            "1", "2", "3", "+",
            "0", ".", "=", ""
        };
        for (String label : labels) {
            JButton button = new JButton(label);
            button.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 22));
            button.setFocusPainted(false);
            if (label.isEmpty()) {
                button.setVisible(false);
            } else {
                button.setActionCommand(label);
                button.addActionListener(event -> handleKey(event.getActionCommand()));
                if (isOperator(label) || label.equals("=")) {
                    button.setBackground(new Color(225, 237, 255));
                } else if (label.equals("C") || label.equals("⌫")) {
                    button.setBackground(new Color(238, 238, 238));
                }
            }
            keys.add(button);
        }
        add(keys, BorderLayout.CENTER);
    }

    private void handleKey(String key) {
        if (key.matches("\\d")) {
            enterDigit(key);
        } else if (key.equals(".")) {
            enterDecimalPoint();
        } else if (key.equals("C")) {
            clear();
        } else if (key.equals("⌫")) {
            backspace();
        } else if (key.equals("±")) {
            changeSign();
        } else if (key.equals("=")) {
            calculate();
        } else if (isOperator(key)) {
            chooseOperator(key);
        }
    }

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

    private void chooseOperator(String operator) {
        if (display.getText().equals("Error")) {
            clear();
            return;
        }
        double current = readDisplay();
        if (pendingOperator != null && !startNewNumber) {
            Double result = apply(storedValue, current, pendingOperator);
            if (result == null) {
                showError();
                return;
            }
            display.setText(format(result));
            storedValue = result;
        } else {
            storedValue = current;
        }
        pendingOperator = operator;
        startNewNumber = true;
        expression.setText(format(storedValue) + " " + operator);
    }

    private void calculate() {
        if (pendingOperator == null || display.getText().equals("Error")) {
            return;
        }
        double right = readDisplay();
        String completedExpression = format(storedValue) + " " + pendingOperator
                + " " + format(right) + " =";
        Double result = apply(storedValue, right, pendingOperator);
        if (result == null) {
            showError();
            return;
        }
        display.setText(format(result));
        expression.setText(completedExpression);
        storedValue = result;
        pendingOperator = null;
        startNewNumber = true;
    }

    private Double apply(double left, double right, String operator) {
        switch (operator) {
            case "+": return left + right;
            case "−": return left - right;
            case "×": return left * right;
            case "÷": return right == 0 ? null : left / right;
            default: return right;
        }
    }

    private void backspace() {
        if (startNewNumber || display.getText().equals("Error")) {
            return;
        }
        String value = display.getText();
        if (value.length() <= 1 || (value.length() == 2 && value.startsWith("-"))) {
            display.setText("0");
            startNewNumber = true;
        } else {
            display.setText(value.substring(0, value.length() - 1));
        }
    }

    private void changeSign() {
        if (display.getText().equals("Error") || readDisplay() == 0) {
            return;
        }
        display.setText(display.getText().startsWith("-")
                ? display.getText().substring(1)
                : "-" + display.getText());
    }

    private void clear() {
        display.setText("0");
        expression.setText(" ");
        storedValue = 0;
        pendingOperator = null;
        startNewNumber = true;
    }

    private void showError() {
        display.setText("Error");
        expression.setText(" ");
        storedValue = 0;
        pendingOperator = null;
        startNewNumber = true;
    }

    private double readDisplay() {
        return Double.parseDouble(display.getText());
    }

    private String format(double value) {
        if (!Double.isFinite(value)) {
            return "Error";
        }
        DecimalFormat formatter = new DecimalFormat("0.##########",
                DecimalFormatSymbols.getInstance(Locale.ROOT));
        return formatter.format(value);
    }

    private boolean isOperator(String key) {
        return key.equals("+") || key.equals("−") || key.equals("×") || key.equals("÷");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Calculator().setVisible(true));
    }
}
