package aura.editor;

import aura.theme.Theme;
import aura.theme.ThemeManager;
import aura.util.FileType;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fast, debounced regex-based syntax highlighter for multiple programming languages.
 */
public class SyntaxHighlighter implements DocumentListener {
    private final JTextPane textPane;
    private FileType fileType;
    private final Timer debounceTimer;
    private boolean isHighlighting = false;

    // Precompiled patterns
    private static final Pattern JAVA_KEYWORDS = Pattern.compile(
        "\\b(abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|" +
        "do|double|else|enum|extends|final|finally|float|for|goto|if|implements|import|instanceof|" +
        "int|interface|long|native|new|package|private|protected|public|return|short|static|strictfp|" +
        "super|switch|synchronized|this|throw|throws|transient|try|void|volatile|while|record|var|" +
        "yield|sealed|permits|non-sealed)\\b"
    );

    private static final Pattern JAVA_TYPES = Pattern.compile(
        "\\b(String|Integer|Long|Double|Float|Boolean|Character|Byte|Short|Object|Class|System|" +
        "Math|List|ArrayList|LinkedList|Map|HashMap|Set|HashSet|Queue|Deque|Optional|Stream|" +
        "Exception|RuntimeException|Throwable|Error|Thread|Runnable)\\b"
    );

    private static final Pattern PYTHON_KEYWORDS = Pattern.compile(
        "\\b(and|as|assert|async|await|break|class|continue|def|del|elif|else|except|finally|" +
        "for|from|global|if|import|in|is|lambda|nonlocal|not|or|pass|raise|return|try|while|with|yield|" +
        "True|False|None)\\b"
    );

    private static final Pattern PYTHON_TYPES = Pattern.compile(
        "\\b(self|int|str|float|bool|list|dict|set|tuple|bytes|bytearray|object|super|len|range|print)\\b"
    );

    private static final Pattern JS_KEYWORDS = Pattern.compile(
        "\\b(async|await|break|case|catch|class|const|continue|debugger|default|delete|do|else|" +
        "export|extends|finally|for|function|if|import|in|instanceof|new|return|super|switch|" +
        "this|throw|try|typeof|var|void|while|with|yield|let|static|enum|implements|interface|" +
        "package|private|protected|public|null|undefined|true|false)\\b"
    );

    private static final Pattern SQL_KEYWORDS = Pattern.compile(
        "(?i)\\b(select|from|where|insert|into|update|delete|drop|alter|table|create|index|view|" +
        "join|inner|left|right|full|outer|on|group|by|order|having|limit|offset|union|all|distinct|" +
        "as|and|or|not|null|is|in|between|like|exists|case|when|then|else|end|count|sum|avg|min|max|" +
        "primary|key|foreign|references|values|set|add|constraint)\\b"
    );

    private static final Pattern NUMBERS = Pattern.compile("\\b(\\d+(\\.\\d+)?[fFdDlL]?|0x[0-9a-fA-F]+)\\b");
    private static final Pattern STRINGS = Pattern.compile("\"(\\\\.|[^\"\\\\])*\"|'(\\\\.|[^'\\\\])*'");
    private static final Pattern MULTILINE_STRINGS = Pattern.compile("\"\"\"[\\s\\S]*?\"\"\"|'''[\\s\\S]*?'''");
    private static final Pattern LINE_COMMENTS_SLASH = Pattern.compile("//.*");
    private static final Pattern LINE_COMMENTS_HASH = Pattern.compile("#.*");
    private static final Pattern BLOCK_COMMENTS = Pattern.compile("/\\*[\\s\\S]*?\\*/");
    private static final Pattern ANNOTATIONS = Pattern.compile("@[A-Za-z0-9_]+");
    private static final Pattern MD_HEADERS = Pattern.compile("^(#{1,6}\\s+.*)$", Pattern.MULTILINE);
    private static final Pattern MD_CODE = Pattern.compile("`[^`]+`");
    private static final Pattern MD_BOLD = Pattern.compile("\\*\\*[^*]+\\*\\*|__[^_]+__");
    private static final Pattern MD_LINKS = Pattern.compile("\\[[^\\]]+\\]\\([^)]+\\)");

    public SyntaxHighlighter(JTextPane textPane, FileType fileType) {
        this.textPane = textPane;
        this.fileType = fileType;

        this.debounceTimer = new Timer(120, e -> highlightDocument());
        this.debounceTimer.setRepeats(false);

        textPane.getDocument().addDocumentListener(this);
        ThemeManager.addListener(theme -> triggerHighlight());
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
        triggerHighlight();
    }

    public void triggerHighlight() {
        if (!debounceTimer.isRunning()) {
            debounceTimer.restart();
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        triggerHighlight();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        triggerHighlight();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        // Style changes don't retrigger highlighting
    }

    private void highlightDocument() {
        if (isHighlighting || fileType == null || !fileType.supportsHighlighting()) return;
        StyledDocument doc = textPane.getStyledDocument();
        int length = doc.getLength();
        if (length == 0 || length > 100_000) return; // avoid lagging huge files

        isHighlighting = true;
        SwingUtilities.invokeLater(() -> {
            try {
                String text = doc.getText(0, length);
                Theme theme = ThemeManager.getTheme();

                // Base style: clear formatting
                SimpleAttributeSet baseStyle = new SimpleAttributeSet();
                StyleConstants.setForeground(baseStyle, theme.fgEditor());
                StyleConstants.setBold(baseStyle, false);
                StyleConstants.setItalic(baseStyle, false);
                doc.setCharacterAttributes(0, length, baseStyle, true);

                switch (fileType) {
                    case JAVA, CPP -> highlightJava(doc, text, theme);
                    case PYTHON -> highlightPython(doc, text, theme);
                    case JAVASCRIPT, TYPESCRIPT, JSON -> highlightJS(doc, text, theme);
                    case SQL -> highlightSQL(doc, text, theme);
                    case MARKDOWN -> highlightMarkdown(doc, text, theme);
                    case SHELL -> highlightShell(doc, text, theme);
                    default -> {}
                }
            } catch (Exception ex) {
                // Ignore transient document mutation exceptions
            } finally {
                isHighlighting = false;
            }
        });
    }

    private void applyStyle(StyledDocument doc, Matcher matcher, Color color, boolean bold, boolean italic) {
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, color);
        if (bold) StyleConstants.setBold(style, true);
        if (italic) StyleConstants.setItalic(style, true);

        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            doc.setCharacterAttributes(start, end - start, style, false);
        }
    }

    private void highlightJava(StyledDocument doc, String text, Theme theme) {
        applyStyle(doc, NUMBERS.matcher(text), theme.syntaxNumber(), false, false);
        applyStyle(doc, JAVA_TYPES.matcher(text), theme.syntaxType(), true, false);
        applyStyle(doc, JAVA_KEYWORDS.matcher(text), theme.syntaxKeyword(), true, false);
        applyStyle(doc, ANNOTATIONS.matcher(text), theme.syntaxAttribute(), false, true);
        applyStyle(doc, STRINGS.matcher(text), theme.syntaxString(), false, false);
        applyStyle(doc, LINE_COMMENTS_SLASH.matcher(text), theme.syntaxComment(), false, true);
        applyStyle(doc, BLOCK_COMMENTS.matcher(text), theme.syntaxComment(), false, true);
    }

    private void highlightPython(StyledDocument doc, String text, Theme theme) {
        applyStyle(doc, NUMBERS.matcher(text), theme.syntaxNumber(), false, false);
        applyStyle(doc, PYTHON_TYPES.matcher(text), theme.syntaxType(), true, false);
        applyStyle(doc, PYTHON_KEYWORDS.matcher(text), theme.syntaxKeyword(), true, false);
        applyStyle(doc, STRINGS.matcher(text), theme.syntaxString(), false, false);
        applyStyle(doc, MULTILINE_STRINGS.matcher(text), theme.syntaxString(), false, false);
        applyStyle(doc, LINE_COMMENTS_HASH.matcher(text), theme.syntaxComment(), false, true);
    }

    private void highlightJS(StyledDocument doc, String text, Theme theme) {
        applyStyle(doc, NUMBERS.matcher(text), theme.syntaxNumber(), false, false);
        applyStyle(doc, JS_KEYWORDS.matcher(text), theme.syntaxKeyword(), true, false);
        applyStyle(doc, STRINGS.matcher(text), theme.syntaxString(), false, false);
        applyStyle(doc, LINE_COMMENTS_SLASH.matcher(text), theme.syntaxComment(), false, true);
        applyStyle(doc, BLOCK_COMMENTS.matcher(text), theme.syntaxComment(), false, true);
    }

    private void highlightSQL(StyledDocument doc, String text, Theme theme) {
        applyStyle(doc, NUMBERS.matcher(text), theme.syntaxNumber(), false, false);
        applyStyle(doc, SQL_KEYWORDS.matcher(text), theme.syntaxKeyword(), true, false);
        applyStyle(doc, STRINGS.matcher(text), theme.syntaxString(), false, false);
        applyStyle(doc, LINE_COMMENTS_SLASH.matcher(text), theme.syntaxComment(), false, true);
    }

    private void highlightShell(StyledDocument doc, String text, Theme theme) {
        applyStyle(doc, NUMBERS.matcher(text), theme.syntaxNumber(), false, false);
        applyStyle(doc, STRINGS.matcher(text), theme.syntaxString(), false, false);
        applyStyle(doc, LINE_COMMENTS_HASH.matcher(text), theme.syntaxComment(), false, true);
    }

    private void highlightMarkdown(StyledDocument doc, String text, Theme theme) {
        applyStyle(doc, MD_CODE.matcher(text), theme.syntaxNumber(), false, false);
        applyStyle(doc, MD_BOLD.matcher(text), theme.syntaxType(), true, false);
        applyStyle(doc, MD_LINKS.matcher(text), theme.syntaxOperator(), false, false);
        applyStyle(doc, MD_HEADERS.matcher(text), theme.syntaxKeyword(), true, false);
    }
}
