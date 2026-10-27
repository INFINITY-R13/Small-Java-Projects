package com.contactbook.ui;

import com.contactbook.model.Contact;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.DateFormatter;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Consumer;

/**
 * Modal dialog for adding or editing a contact.
 *
 * <p>Fields: Name (required), Phone, Email, Birthday (spinner), Instagram.
 */
public class ContactFormDialog extends JDialog {

    private final boolean isEdit;

    private JTextField   nameField;
    private JTextField   phoneField;
    private JTextField   emailField;
    private JSpinner     birthdaySpinner;
    private JCheckBox    birthdayCheck;
    private JTextField   instagramField;
    private JLabel       nameError;

    private final Consumer<Contact> onSave;
    private final Contact           existing; // null when adding

    // ── Constructors ──────────────────────────────────────────────────────────

    /** Opens the dialog for creating a new contact. */
    public ContactFormDialog(Frame owner, Consumer<Contact> onSave) {
        this(owner, null, onSave);
    }

    /** Opens the dialog pre-filled for editing {@code existing}. */
    public ContactFormDialog(Frame owner, Contact existing, Consumer<Contact> onSave) {
        super(owner, existing == null ? "Add Contact" : "Edit Contact", true);
        this.existing = existing;
        this.isEdit   = existing != null;
        this.onSave   = onSave;

        setSize(480, 540);
        setMinimumSize(new Dimension(420, 480));
        setLocationRelativeTo(owner);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        buildUI();
        if (isEdit) prefill();
    }

    // ── UI construction ───────────────────────────────────────────────────────

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIConstants.BG_CARD);
        setContentPane(root);

        // ── Title bar ─────────────────────────────────────────────────────────
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(UIConstants.BG_CARD);
        titleBar.setBorder(new EmptyBorder(20, 24, 12, 24));

        JLabel titleLabel = new JLabel(isEdit ? "✏  Edit Contact" : "＋  Add Contact");
        titleLabel.setFont(UIConstants.FONT_TITLE);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        titleBar.add(titleLabel, BorderLayout.WEST);

        root.add(titleBar, BorderLayout.NORTH);

        // ── Form body ─────────────────────────────────────────────────────────
        JPanel form = new JPanel();
        form.setBackground(UIConstants.BG_CARD);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(0, 24, 0, 24));

        // Name *
        nameField = createTextField("Full name");
        nameError = new JLabel("Name is required");
        nameError.setFont(UIConstants.FONT_SMALL);
        nameError.setForeground(UIConstants.DANGER);
        nameError.setVisible(false);

        form.add(buildFieldGroup("Name *", nameField));
        form.add(Box.createVerticalStrut(2));
        form.add(nameError);
        form.add(Box.createVerticalStrut(10));

        // Phone
        phoneField = createTextField("e.g. +1 555-0100");
        form.add(buildFieldGroup("Phone", phoneField));
        form.add(Box.createVerticalStrut(12));

        // Email
        emailField = createTextField("e.g. alice@example.com");
        form.add(buildFieldGroup("Email", emailField));
        form.add(Box.createVerticalStrut(12));

        // Birthday (optional)
        form.add(buildBirthdayGroup());
        form.add(Box.createVerticalStrut(12));

        // Instagram
        instagramField = createTextField("@username");
        form.add(buildFieldGroup("Instagram", instagramField));

        JScrollPane scrollPane = new JScrollPane(form);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        root.add(scrollPane, BorderLayout.CENTER);

        // ── Button row ────────────────────────────────────────────────────────
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        btnRow.setBackground(UIConstants.BG_CARD);
        btnRow.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UIConstants.DIVIDER),
                new EmptyBorder(4, 16, 4, 16)));

        JButton cancelBtn = createOutlineButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        JButton saveBtn = createFilledButton(isEdit ? "Save Changes" : "Add Contact");
        saveBtn.addActionListener(e -> handleSave());

        btnRow.add(cancelBtn);
        btnRow.add(saveBtn);
        root.add(btnRow, BorderLayout.SOUTH);

        // Keyboard shortcut – Enter triggers save
        getRootPane().setDefaultButton(saveBtn);

        // Escape closes
        KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(e -> dispose(), esc, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    // ── Field group builders ──────────────────────────────────────────────────

    private JPanel buildFieldGroup(String labelText, JComponent field) {
        JPanel group = new JPanel();
        group.setOpaque(false);
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(UIConstants.FONT_SMALL);
        label.setForeground(UIConstants.TEXT_SECONDARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        group.add(label);
        group.add(Box.createVerticalStrut(4));
        group.add(field);
        return group;
    }

    private JPanel buildBirthdayGroup() {
        JPanel group = new JPanel();
        group.setOpaque(false);
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel labelRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelRow.setOpaque(false);

        JLabel label = new JLabel("Birthday");
        label.setFont(UIConstants.FONT_SMALL);
        label.setForeground(UIConstants.TEXT_SECONDARY);

        birthdayCheck = new JCheckBox("(set)");
        birthdayCheck.setFont(UIConstants.FONT_SMALL);
        birthdayCheck.setForeground(UIConstants.TEXT_SECONDARY);
        birthdayCheck.setOpaque(false);
        birthdayCheck.setFocusPainted(false);
        birthdayCheck.addActionListener(e -> birthdaySpinner.setEnabled(birthdayCheck.isSelected()));

        labelRow.add(label);
        labelRow.add(Box.createHorizontalStrut(8));
        labelRow.add(birthdayCheck);

        // Spinner with date model
        SpinnerDateModel model = new SpinnerDateModel(new Date(), null, new Date(), Calendar.DAY_OF_MONTH);
        birthdaySpinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(birthdaySpinner, "dd/MM/yyyy");
        birthdaySpinner.setEditor(editor);
        birthdaySpinner.setFont(UIConstants.FONT_BODY);
        birthdaySpinner.setEnabled(false);
        birthdaySpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        birthdaySpinner.setAlignmentX(Component.LEFT_ALIGNMENT);

        group.add(labelRow);
        group.add(Box.createVerticalStrut(4));
        group.add(birthdaySpinner);
        return group;
    }

    // ── Prefill for edit mode ─────────────────────────────────────────────────

    private void prefill() {
        if (existing.getName() != null)      nameField.setText(existing.getName());
        if (existing.getPhone() != null)     phoneField.setText(existing.getPhone());
        if (existing.getEmail() != null)     emailField.setText(existing.getEmail());
        if (existing.getInstagram() != null) instagramField.setText(existing.getInstagram());

        if (existing.getBirthDate() != null) {
            birthdayCheck.setSelected(true);
            birthdaySpinner.setEnabled(true);
            Date d = Date.from(existing.getBirthDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
            birthdaySpinner.setValue(d);
        }
    }

    // ── Save handler ──────────────────────────────────────────────────────────

    private void handleSave() {
        String name = nameField.getText().trim();
        if (name.isBlank()) {
            nameError.setVisible(true);
            nameField.requestFocusInWindow();
            return;
        }
        nameError.setVisible(false);

        String phone     = phoneField.getText().trim();
        String email     = emailField.getText().trim();
        String instagram = stripAt(instagramField.getText().trim());

        LocalDate birthday = null;
        if (birthdayCheck.isSelected()) {
            Date d = (Date) birthdaySpinner.getValue();
            birthday = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        Contact contact;
        if (isEdit) {
            contact = existing;
            contact.setName(name);
            contact.setPhone(phone.isBlank()     ? null : phone);
            contact.setEmail(email.isBlank()     ? null : email);
            contact.setInstagram(instagram.isBlank() ? null : instagram);
            contact.setBirthDate(birthday);
        } else {
            contact = new Contact(
                    name,
                    email.isBlank()     ? null : email,
                    phone.isBlank()     ? null : phone,
                    birthday,
                    instagram.isBlank() ? null : instagram
            );
        }

        if (onSave != null) onSave.accept(contact);
        dispose();
    }

    // ── Widget factories ──────────────────────────────────────────────────────

    private JTextField createTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(UIConstants.FONT_BODY);
        tf.setForeground(UIConstants.TEXT_PRIMARY);
        tf.setCaretColor(UIConstants.ACCENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UIConstants.DIVIDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)));

        // Placeholder via FocusListener painting trick
        tf.putClientProperty("JTextField.placeholderText", placeholder);
        return tf;
    }

    private JButton createFilledButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? UIConstants.ACCENT_HOVER : UIConstants.ACCENT;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), UIConstants.ARC, UIConstants.ARC);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(UIConstants.FONT_BODY);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 36));
        return btn;
    }

    private JButton createOutlineButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? new Color(0xF3F4F6) : UIConstants.BG_CARD;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, UIConstants.ARC, UIConstants.ARC);
                g2.setColor(UIConstants.DIVIDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, UIConstants.ARC, UIConstants.ARC);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(UIConstants.FONT_BODY);
        btn.setForeground(UIConstants.TEXT_PRIMARY);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 36));
        return btn;
    }

    // ── Utility ──────────────────────────────────────────────────────────────

    private String stripAt(String s) {
        if (s == null) return "";
        return s.startsWith("@") ? s.substring(1) : s;
    }
}
