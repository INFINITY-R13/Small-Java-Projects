package aura.ui;

import aura.editor.CodeEditorPane;
import aura.theme.Theme;
import aura.theme.ThemeManager;
import aura.util.Icons;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Floating Find and Replace bar with match counter, regex/case options, and live document highlighting.
 */
public class FindReplaceBar extends JPanel {
    private final JTextField txtFind;
    private final JTextField txtReplace;
    private final JLabel lblMatches;
    private final JCheckBox chkCase;
    private final JCheckBox chkWholeWord;
    private final JPanel replaceRow;
    private final ModernButton btnPrev;
    private final ModernButton btnNext;
    private final ModernButton btnReplace;
    private final ModernButton btnReplaceAll;
    private final ModernButton btnClose;

    private CodeEditorPane currentEditor;
    private final List<int[]> matchPositions = new ArrayList<>();
    private int currentMatchIndex = -1;

    private final Object highlightTagKey = new Object();

    public FindReplaceBar() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        setOpaque(false);

        // Find Row
        JPanel findRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        findRow.setOpaque(false);

        txtFind = createStyledTextField("Find...", 160);
        lblMatches = new JLabel("No matches");
        lblMatches.setFont(new Font("-apple-system", Font.PLAIN, 11));

        chkCase = new JCheckBox("Aa");
        chkCase.setToolTipText("Match Case");
        chkCase.setOpaque(false);
        chkCase.setFocusPainted(false);
        chkCase.setFont(new Font("-apple-system", Font.BOLD, 11));

        chkWholeWord = new JCheckBox("\\b");
        chkWholeWord.setToolTipText("Match Whole Word");
        chkWholeWord.setOpaque(false);
        chkWholeWord.setFocusPainted(false);
        chkWholeWord.setFont(new Font("-apple-system", Font.BOLD, 11));

        btnPrev = new ModernButton("▲");
        btnPrev.setToolTipText("Previous Match (Shift+Enter)");
        btnPrev.setPreferredSize(new Dimension(28, 26));

        btnNext = new ModernButton("▼");
        btnNext.setToolTipText("Next Match (Enter)");
        btnNext.setPreferredSize(new Dimension(28, 26));

        btnClose = new ModernButton("×");
        btnClose.setToolTipText("Close (Esc)");
        btnClose.setPreferredSize(new Dimension(28, 26));

        findRow.add(txtFind);
        findRow.add(lblMatches);
        findRow.add(chkCase);
        findRow.add(chkWholeWord);
        findRow.add(btnPrev);
        findRow.add(btnNext);
        findRow.add(btnClose);

        // Replace Row
        replaceRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        replaceRow.setOpaque(false);

        txtReplace = createStyledTextField("Replace with...", 160);
        btnReplace = new ModernButton("Replace");
        btnReplace.setPreferredSize(new Dimension(75, 26));

        btnReplaceAll = new ModernButton("Replace All");
        btnReplaceAll.setPreferredSize(new Dimension(90, 26));

        replaceRow.add(txtReplace);
        replaceRow.add(btnReplace);
        replaceRow.add(btnReplaceAll);

        add(findRow);
        add(replaceRow);

        // Event wiring
        txtFind.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { performSearch(); }
            @Override public void removeUpdate(DocumentEvent e) { performSearch(); }
            @Override public void changedUpdate(DocumentEvent e) { performSearch(); }
        });

        chkCase.addActionListener(e -> performSearch());
        chkWholeWord.addActionListener(e -> performSearch());

        btnNext.addActionListener(e -> findNext());
        btnPrev.addActionListener(e -> findPrevious());
        btnReplace.addActionListener(e -> replaceCurrent());
        btnReplaceAll.addActionListener(e -> replaceAll());
        btnClose.addActionListener(e -> hideBar());

        txtFind.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (e.isShiftDown()) findPrevious();
                    else findNext();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    hideBar();
                }
            }
        });

        txtReplace.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    replaceCurrent();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    hideBar();
                }
            }
        });

        ThemeManager.addListener(this::applyTheme);
    }

    private JTextField createStyledTextField(String placeholder, int width) {
        JTextField tf = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Theme theme = ThemeManager.getTheme();
                g2d.setColor(theme.bgEditor());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2d.setColor(hasFocus() ? theme.accent() : theme.border());
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2d.dispose();
                super.paintComponent(g);

                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D gText = (Graphics2D) g.create();
                    gText.setColor(ThemeManager.getTheme().fgMuted());
                    gText.setFont(getFont());
                    gText.drawString(placeholder, 8, getHeight() / 2 + 5);
                    gText.dispose();
                }
            }
        };
        tf.setOpaque(false);
        tf.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        tf.setPreferredSize(new Dimension(width, 28));
        tf.setFont(new Font("-apple-system", Font.PLAIN, 12));
        return tf;
    }

    private void applyTheme(Theme theme) {
        lblMatches.setForeground(theme.fgMuted());
        chkCase.setForeground(theme.fgMuted());
        chkWholeWord.setForeground(theme.fgMuted());
        txtFind.setForeground(theme.fgEditor());
        txtFind.setCaretColor(theme.accent());
        txtReplace.setForeground(theme.fgEditor());
        txtReplace.setCaretColor(theme.accent());
        repaint();
    }

    public void showForEditor(CodeEditorPane editor, boolean showReplace) {
        this.currentEditor = editor;
        replaceRow.setVisible(showReplace);
        setVisible(true);
        txtFind.requestFocusInWindow();
        txtFind.selectAll();
        performSearch();
    }

    public void hideBar() {
        clearHighlights();
        setVisible(false);
        if (currentEditor != null) {
            currentEditor.requestFocusInWindow();
        }
    }

    private void performSearch() {
        clearHighlights();
        matchPositions.clear();
        currentMatchIndex = -1;

        if (currentEditor == null) return;
        String query = txtFind.getText();
        if (query.isEmpty()) {
            lblMatches.setText("No matches");
            return;
        }

        try {
            String text = currentEditor.getDocument().getText(0, currentEditor.getDocument().getLength());
            String patternStr = Pattern.quote(query);
            if (chkWholeWord.isSelected()) {
                patternStr = "\\b" + patternStr + "\\b";
            }
            int flags = chkCase.isSelected() ? 0 : Pattern.CASE_INSENSITIVE;
            Pattern pattern = Pattern.compile(patternStr, flags);
            Matcher matcher = pattern.matcher(text);

            Highlighter highlighter = currentEditor.getHighlighter();
            Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(
                new Color(255, 215, 0, 90)
            );

            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                matchPositions.add(new int[]{start, end});
                highlighter.addHighlight(start, end, painter);
            }

            int total = matchPositions.size();
            if (total == 0) {
                lblMatches.setText("0 matches");
            } else {
                // Find closest match to current caret
                int caret = currentEditor.getCaretPosition();
                currentMatchIndex = 0;
                for (int i = 0; i < total; i++) {
                    if (matchPositions.get(i)[0] >= caret) {
                        currentMatchIndex = i;
                        break;
                    }
                }
                lblMatches.setText((currentMatchIndex + 1) + " of " + total);
                selectCurrentMatch();
            }
        } catch (BadLocationException e) {
            // ignore
        }
    }

    private void findNext() {
        if (matchPositions.isEmpty()) return;
        currentMatchIndex = (currentMatchIndex + 1) % matchPositions.size();
        selectCurrentMatch();
    }

    private void findPrevious() {
        if (matchPositions.isEmpty()) return;
        currentMatchIndex = (currentMatchIndex - 1 + matchPositions.size()) % matchPositions.size();
        selectCurrentMatch();
    }

    private void selectCurrentMatch() {
        if (currentMatchIndex >= 0 && currentMatchIndex < matchPositions.size() && currentEditor != null) {
            int[] pos = matchPositions.get(currentMatchIndex);
            currentEditor.select(pos[0], pos[1]);
            lblMatches.setText((currentMatchIndex + 1) + " of " + matchPositions.size());
        }
    }

    private void replaceCurrent() {
        if (currentEditor == null || matchPositions.isEmpty() || currentMatchIndex < 0) return;
        int[] pos = matchPositions.get(currentMatchIndex);
        String replacement = txtReplace.getText();

        try {
            currentEditor.getDocument().remove(pos[0], pos[1] - pos[0]);
            currentEditor.getDocument().insertString(pos[0], replacement, null);
            performSearch();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void replaceAll() {
        if (currentEditor == null || matchPositions.isEmpty()) return;
        String query = txtFind.getText();
        String replacement = txtReplace.getText();

        try {
            String text = currentEditor.getDocument().getText(0, currentEditor.getDocument().getLength());
            String patternStr = Pattern.quote(query);
            if (chkWholeWord.isSelected()) {
                patternStr = "\\b" + patternStr + "\\b";
            }
            int flags = chkCase.isSelected() ? 0 : Pattern.CASE_INSENSITIVE;
            Pattern pattern = Pattern.compile(patternStr, flags);
            String newText = pattern.matcher(text).replaceAll(replacement);

            currentEditor.getDocument().remove(0, currentEditor.getDocument().getLength());
            currentEditor.getDocument().insertString(0, newText, null);
            performSearch();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void clearHighlights() {
        if (currentEditor != null) {
            currentEditor.getHighlighter().removeAllHighlights();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Theme theme = ThemeManager.getTheme();

        // Card background
        g2d.setColor(theme.bgSurface());
        g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);

        // Border
        g2d.setColor(theme.border());
        g2d.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 10, 10);

        g2d.dispose();
        super.paintComponent(g);
    }
}
