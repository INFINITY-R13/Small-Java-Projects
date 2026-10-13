package aura.editor;

import aura.theme.Theme;
import aura.theme.ThemeManager;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;

/**
 * Aesthetic code editor pane featuring current line highlight, bracket matching,
 * auto-indentation, smart pair closing, and smooth zoom.
 */
public class CodeEditorPane extends JTextPane {
    private static final String[] MONO_FONTS = {
        "JetBrains Mono", "SF Mono", "Fira Code", "Menlo", "Cascadia Code", "Consolas", "Monaco"
    };

    private float baseFontSize = 14.0f;
    private float currentFontSize = 14.0f;
    private final UndoManager undoManager = new UndoManager();
    private int matchingBracketPos = -1;
    private int activeBracketPos = -1;

    public CodeEditorPane() {
        setOpaque(false);
        setCaretColor(ThemeManager.getTheme().accent());
        setSelectionColor(ThemeManager.getTheme().bgSelection());
        setSelectedTextColor(ThemeManager.getTheme().fgSelection());

        Font editorFont = resolveEditorFont(baseFontSize);
        setFont(editorFont);

        setMargin(new Insets(6, 10, 6, 10));

        // Setup Undo Manager
        getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));

        // Caret & Bracket matching listener
        addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                updateBracketMatching();
                repaint();
            }
        });

        // Key bindings and smart pairs
        setupKeyboardActions();

        // Mouse wheel zoom (Ctrl/Cmd + Wheel)
        addMouseWheelListener(e -> {
            if (e.isControlDown() || e.isMetaDown()) {
                if (e.getWheelRotation() < 0) {
                    zoomIn();
                } else if (e.getWheelRotation() > 0) {
                    zoomOut();
                }
                e.consume();
            }
        });

        ThemeManager.addListener(this::applyTheme);
    }

    private Font resolveEditorFont(float size) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] available = ge.getAvailableFontFamilyNames();
        for (String preferred : MONO_FONTS) {
            for (String fontName : available) {
                if (fontName.equalsIgnoreCase(preferred)) {
                    return new Font(fontName, Font.PLAIN, Math.round(size));
                }
            }
        }
        return new Font(Font.MONOSPACED, Font.PLAIN, Math.round(size));
    }

    private void applyTheme(Theme theme) {
        setCaretColor(theme.accent());
        setSelectionColor(theme.bgSelection());
        setSelectedTextColor(theme.fgSelection());
        setForeground(theme.fgEditor());
        repaint();
    }

    private void setupKeyboardActions() {
        InputMap im = getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap am = getActionMap();
        int shortcutKeyMask = InputEvent.CTRL_DOWN_MASK;
        try {
            if (!GraphicsEnvironment.isHeadless()) {
                shortcutKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
            }
        } catch (Throwable ignored) {}
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, shortcutKeyMask), "auraUndo");
        am.put("auraUndo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undo();
            }
        });

        // Redo: Cmd/Ctrl + Shift + Z & Cmd/Ctrl + Y
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, shortcutKeyMask | InputEvent.SHIFT_DOWN_MASK), "auraRedo");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, shortcutKeyMask), "auraRedo");
        am.put("auraRedo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redo();
            }
        });

        // Duplicate line: Cmd/Ctrl + D
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, shortcutKeyMask), "auraDuplicateLine");
        am.put("auraDuplicateLine", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                duplicateCurrentLine();
            }
        });

        // Toggle line comment: Cmd/Ctrl + /
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, shortcutKeyMask), "auraToggleComment");
        am.put("auraToggleComment", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleComment("//");
            }
        });

        // Zoom keys: Cmd/Ctrl + Plus / Minus / 0
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, shortcutKeyMask), "auraZoomIn");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, shortcutKeyMask), "auraZoomIn");
        am.put("auraZoomIn", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomIn();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, shortcutKeyMask), "auraZoomOut");
        am.put("auraZoomOut", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomOut();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_0, shortcutKeyMask), "auraResetZoom");
        am.put("auraResetZoom", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetZoom();
            }
        });

        // Smart Key Handling (Auto Indent, Smart Pair Close)
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (handleEnterKey()) {
                        e.consume();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    if ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0) {
                        unindentSelection();
                    } else {
                        indentSelectionOrInsertTab();
                    }
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    if (handleBackspacePair()) {
                        e.consume();
                    }
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                char ch = e.getKeyChar();
                if (handleSmartPairTyping(ch)) {
                    e.consume();
                }
            }
        });
    }

    public void undo() {
        if (undoManager.canUndo()) {
            try {
                undoManager.undo();
            } catch (CannotUndoException ex) {
                // ignore
            }
        }
    }

    public void redo() {
        if (undoManager.canRedo()) {
            try {
                undoManager.redo();
            } catch (CannotRedoException ex) {
                // ignore
            }
        }
    }

    public void zoomIn() {
        if (currentFontSize < 36.0f) {
            setFontSize(currentFontSize + 1.5f);
        }
    }

    public void zoomOut() {
        if (currentFontSize > 9.0f) {
            setFontSize(currentFontSize - 1.5f);
        }
    }

    public void resetZoom() {
        setFontSize(baseFontSize);
    }

    public int getZoomPercentage() {
        return Math.round((currentFontSize / baseFontSize) * 100);
    }

    public void setFontSize(float size) {
        this.currentFontSize = Math.max(8.0f, Math.min(40.0f, size));
        setFont(getFont().deriveFont(this.currentFontSize));
        repaint();
    }

    private boolean handleEnterKey() {
        try {
            int caret = getCaretPosition();
            Document doc = getDocument();
            Element root = doc.getDefaultRootElement();
            int lineIdx = root.getElementIndex(caret);
            Element lineElem = root.getElement(lineIdx);
            int start = lineElem.getStartOffset();
            int len = caret - start;
            String linePrefix = doc.getText(start, len);

            // Calculate indentation
            StringBuilder indent = new StringBuilder();
            for (int i = 0; i < linePrefix.length(); i++) {
                char c = linePrefix.charAt(i);
                if (c == ' ' || c == '\t') {
                    indent.append(c);
                } else {
                    break;
                }
            }

            String trimmed = linePrefix.trim();
            boolean openBrace = trimmed.endsWith("{") || trimmed.endsWith(":") || trimmed.endsWith("(") || trimmed.endsWith("[");
            
            // Check if caret was between { and }
            boolean betweenBraces = false;
            if (caret < doc.getLength()) {
                char nextChar = doc.getText(caret, 1).charAt(0);
                if (trimmed.endsWith("{") && nextChar == '}') {
                    betweenBraces = true;
                }
            }

            if (betweenBraces) {
                String insertion = "\n" + indent + "    \n" + indent;
                doc.insertString(caret, insertion, null);
                setCaretPosition(caret + 1 + indent.length() + 4);
                return true;
            }

            if (openBrace) {
                indent.append("    ");
            }

            doc.insertString(caret, "\n" + indent, null);
            return true;
        } catch (BadLocationException e) {
            return false;
        }
    }

    private void indentSelectionOrInsertTab() {
        try {
            int start = getSelectionStart();
            int end = getSelectionEnd();
            Document doc = getDocument();

            if (start == end) {
                // Just insert 4 spaces
                doc.insertString(start, "    ", null);
            } else {
                Element root = doc.getDefaultRootElement();
                int startLine = root.getElementIndex(start);
                int endLine = root.getElementIndex(end);
                for (int i = startLine; i <= endLine; i++) {
                    Element line = root.getElement(i);
                    doc.insertString(line.getStartOffset(), "    ", null);
                }
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void unindentSelection() {
        try {
            int start = getSelectionStart();
            int end = getSelectionEnd();
            Document doc = getDocument();
            Element root = doc.getDefaultRootElement();
            int startLine = root.getElementIndex(start);
            int endLine = root.getElementIndex(end);

            for (int i = startLine; i <= endLine; i++) {
                Element line = root.getElement(i);
                int lineStart = line.getStartOffset();
                String lineText = doc.getText(lineStart, Math.min(4, line.getEndOffset() - lineStart));
                int spacesToRemove = 0;
                while (spacesToRemove < lineText.length() && lineText.charAt(spacesToRemove) == ' ') {
                    spacesToRemove++;
                }
                if (spacesToRemove > 0) {
                    doc.remove(lineStart, spacesToRemove);
                } else if (lineText.startsWith("\t")) {
                    doc.remove(lineStart, 1);
                }
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private boolean handleBackspacePair() {
        try {
            int caret = getCaretPosition();
            if (caret > 0 && caret < getDocument().getLength()) {
                char prev = getText(caret - 1, 1).charAt(0);
                char next = getText(caret, 1).charAt(0);
                if ((prev == '(' && next == ')') ||
                    (prev == '[' && next == ']') ||
                    (prev == '{' && next == '}') ||
                    (prev == '"' && next == '"') ||
                    (prev == '\'' && next == '\'')) {
                    getDocument().remove(caret - 1, 2);
                    return true;
                }
            }
        } catch (BadLocationException e) {
            // ignore
        }
        return false;
    }

    private boolean handleSmartPairTyping(char ch) {
        try {
            int caret = getCaretPosition();
            int len = getDocument().getLength();

            // Skip existing closing character
            if (caret < len) {
                char next = getText(caret, 1).charAt(0);
                if (ch == next && (ch == ')' || ch == ']' || ch == '}' || ch == '"' || ch == '\'')) {
                    setCaretPosition(caret + 1);
                    return true;
                }
            }

            // Auto-close open characters
            String pair = switch (ch) {
                case '(' -> "()";
                case '[' -> "[]";
                case '{' -> "{}";
                case '"' -> "\"\"";
                case '\'' -> "''";
                default -> null;
            };

            if (pair != null) {
                // Don't auto-close quotes if directly before a word character
                if ((ch == '"' || ch == '\'') && caret < len) {
                    char next = getText(caret, 1).charAt(0);
                    if (Character.isLetterOrDigit(next)) {
                        return false;
                    }
                }
                getDocument().insertString(caret, pair, null);
                setCaretPosition(caret + 1);
                return true;
            }
        } catch (BadLocationException e) {
            // ignore
        }
        return false;
    }

    public void duplicateCurrentLine() {
        try {
            int caret = getCaretPosition();
            Document doc = getDocument();
            Element root = doc.getDefaultRootElement();
            int lineIdx = root.getElementIndex(caret);
            Element line = root.getElement(lineIdx);
            int start = line.getStartOffset();
            int len = line.getEndOffset() - start;
            String text = doc.getText(start, len);
            if (!text.endsWith("\n")) {
                text = text + "\n";
            }
            doc.insertString(line.getEndOffset(), text, null);
            setCaretPosition(caret + len);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void toggleComment(String prefix) {
        try {
            int start = getSelectionStart();
            int end = getSelectionEnd();
            Document doc = getDocument();
            Element root = doc.getDefaultRootElement();
            int startLine = root.getElementIndex(start);
            int endLine = root.getElementIndex(end);

            boolean allCommented = true;
            for (int i = startLine; i <= endLine; i++) {
                Element line = root.getElement(i);
                String lineText = doc.getText(line.getStartOffset(), line.getEndOffset() - line.getStartOffset()).trim();
                if (!lineText.isEmpty() && !lineText.startsWith(prefix)) {
                    allCommented = false;
                    break;
                }
            }

            for (int i = startLine; i <= endLine; i++) {
                Element line = root.getElement(i);
                int lineStart = line.getStartOffset();
                String lineText = doc.getText(lineStart, line.getEndOffset() - lineStart);
                if (allCommented) {
                    int idx = lineText.indexOf(prefix);
                    if (idx != -1) {
                        int removeLen = prefix.length();
                        if (lineText.length() > idx + removeLen && lineText.charAt(idx + removeLen) == ' ') {
                            removeLen++;
                        }
                        doc.remove(lineStart + idx, removeLen);
                    }
                } else {
                    doc.insertString(lineStart, prefix + " ", null);
                }
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void updateBracketMatching() {
        matchingBracketPos = -1;
        activeBracketPos = -1;
        int caret = getCaretPosition();
        Document doc = getDocument();
        int docLen = doc.getLength();
        if (docLen == 0) return;

        try {
            int targetPos = -1;
            char targetChar = '\0';

            if (caret > 0) {
                char before = doc.getText(caret - 1, 1).charAt(0);
                if (isBracket(before)) {
                    targetPos = caret - 1;
                    targetChar = before;
                }
            }
            if (targetPos == -1 && caret < docLen) {
                char at = doc.getText(caret, 1).charAt(0);
                if (isBracket(at)) {
                    targetPos = caret;
                    targetChar = at;
                }
            }

            if (targetPos != -1) {
                activeBracketPos = targetPos;
                matchingBracketPos = findMatchingBracket(targetPos, targetChar);
            }
        } catch (BadLocationException e) {
            // ignore
        }
    }

    private boolean isBracket(char c) {
        return c == '(' || c == ')' || c == '{' || c == '}' || c == '[' || c == ']';
    }

    private int findMatchingBracket(int pos, char bracket) throws BadLocationException {
        Document doc = getDocument();
        int docLen = doc.getLength();
        char match;
        int step;
        boolean forward = (bracket == '(' || bracket == '{' || bracket == '[');

        switch (bracket) {
            case '(' -> { match = ')'; step = 1; }
            case ')' -> { match = '('; step = -1; }
            case '{' -> { match = '}'; step = 1; }
            case '}' -> { match = '{'; step = -1; }
            case '[' -> { match = ']'; step = 1; }
            case ']' -> { match = '['; step = -1; }
            default -> { return -1; }
        }

        int depth = 1;
        int i = pos + step;
        while (i >= 0 && i < docLen) {
            char c = doc.getText(i, 1).charAt(0);
            if (c == bracket) {
                depth++;
            } else if (c == match) {
                depth--;
                if (depth == 0) return i;
            }
            i += step;
        }
        return -1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        Theme theme = ThemeManager.getTheme();

        // 1. Paint editor background
        g2d.setColor(theme.bgEditor());
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // 2. Paint current line highlight
        try {
            int caret = getCaretPosition();
            Rectangle2D r = modelToView2D(caret);
            if (r != null) {
                g2d.setColor(theme.bgCurrentLine());
                g2d.fillRect(0, (int) r.getY(), getWidth(), (int) r.getHeight());
            }
        } catch (BadLocationException e) {
            // ignore
        }

        g2d.dispose();

        // 3. Paint text content
        super.paintComponent(g);

        // 4. Paint bracket highlights
        if (activeBracketPos != -1 && matchingBracketPos != -1) {
            Graphics2D gBrackets = (Graphics2D) g.create();
            gBrackets.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gBrackets.setColor(theme.accent());
            gBrackets.setStroke(new BasicStroke(1.2f));

            drawBracketRect(gBrackets, activeBracketPos);
            drawBracketRect(gBrackets, matchingBracketPos);
            gBrackets.dispose();
        }
    }

    private void drawBracketRect(Graphics2D g2d, int pos) {
        try {
            Rectangle2D r = modelToView2D(pos);
            if (r != null) {
                g2d.drawRoundRect((int) r.getX(), (int) r.getY() + 1, (int) r.getWidth(), (int) r.getHeight() - 2, 3, 3);
            }
        } catch (BadLocationException e) {
            // ignore
        }
    }
}
