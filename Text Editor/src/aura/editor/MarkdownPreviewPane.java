package aura.editor;

import aura.theme.Theme;
import aura.theme.ThemeManager;
import aura.ui.ModernScrollBarUI;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;

/**
 * Aesthetic Markdown rendered preview pane styled dynamically with the current theme.
 */
public class MarkdownPreviewPane extends JPanel {
    private final JEditorPane editorPane;
    private final JScrollPane scrollPane;
    private String markdownSource = "";

    public MarkdownPreviewPane() {
        setLayout(new BorderLayout());

        editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        editorPane.setOpaque(false);

        scrollPane = new JScrollPane(editorPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());

        add(scrollPane, BorderLayout.CENTER);

        ThemeManager.addListener(this::applyTheme);
    }

    public void updateContent(String markdown) {
        this.markdownSource = markdown;
        renderMarkdown();
    }

    private void applyTheme(Theme theme) {
        setBackground(theme.bgEditor());
        scrollPane.setBackground(theme.bgEditor());
        scrollPane.getViewport().setBackground(theme.bgEditor());
        renderMarkdown();
    }

    private void renderMarkdown() {
        Theme theme = ThemeManager.getTheme();
        String htmlBody = convertMarkdownToHtml(markdownSource);

        String css = String.format("""
            body {
                font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif;
                font-size: 14px;
                line-height: 1.6;
                color: %s;
                background-color: %s;
                padding: 20px 28px;
            }
            h1 { font-size: 24px; color: %s; border-bottom: 1px solid %s; padding-bottom: 6px; margin-top: 18px; margin-bottom: 12px; }
            h2 { font-size: 20px; color: %s; border-bottom: 1px solid %s; padding-bottom: 4px; margin-top: 16px; margin-bottom: 10px; }
            h3 { font-size: 16px; color: %s; margin-top: 14px; margin-bottom: 8px; }
            p { margin-top: 0; margin-bottom: 12px; }
            code {
                font-family: Menlo, Monaco, Consolas, monospace;
                font-size: 13px;
                background-color: %s;
                color: %s;
                padding: 2px 5px;
                border-radius: 4px;
            }
            pre {
                background-color: %s;
                padding: 12px 14px;
                border-radius: 6px;
                border: 1px solid %s;
                font-family: Menlo, Monaco, Consolas, monospace;
                font-size: 13px;
                color: %s;
                overflow: auto;
            }
            blockquote {
                border-left: 4px solid %s;
                padding: 6px 14px;
                color: %s;
                background-color: %s;
                margin: 0 0 12px 0;
            }
            ul, ol { padding-left: 24px; margin-bottom: 12px; }
            li { margin-bottom: 4px; }
            hr { border: 0; border-top: 1px solid %s; margin: 18px 0; }
            a { color: %s; text-decoration: none; }
            """,
            toHex(theme.fgEditor()),
            toHex(theme.bgEditor()),
            toHex(theme.accent()),
            toHex(theme.border()),
            toHex(theme.syntaxType()),
            toHex(theme.border()),
            toHex(theme.syntaxKeyword()),
            toHex(theme.bgSurface()),
            toHex(theme.syntaxNumber()),
            toHex(theme.bgSurface()),
            toHex(theme.border()),
            toHex(theme.fgEditor()),
            toHex(theme.accent()),
            toHex(theme.fgMuted()),
            toHex(theme.bgCurrentLine()),
            toHex(theme.border()),
            toHex(theme.syntaxOperator())
        );

        HTMLEditorKit kit = new HTMLEditorKit();
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule(css);
        editorPane.setEditorKit(kit);
        editorPane.setText("<html><body>" + htmlBody + "</body></html>");
        editorPane.setCaretPosition(0);
    }

    private String convertMarkdownToHtml(String md) {
        if (md == null || md.isBlank()) return "<p><i>Empty document</i></p>";

        StringBuilder html = new StringBuilder();
        String[] lines = md.split("\\R");
        boolean inCodeBlock = false;
        boolean inList = false;

        for (String rawLine : lines) {
            String line = rawLine;

            // Fenced code blocks
            if (line.trim().startsWith("```")) {
                if (inCodeBlock) {
                    html.append("</pre>\n");
                    inCodeBlock = false;
                } else {
                    if (inList) { html.append("</ul>\n"); inList = false; }
                    html.append("<pre>");
                    inCodeBlock = true;
                }
                continue;
            }

            if (inCodeBlock) {
                html.append(escapeHtml(line)).append("\n");
                continue;
            }

            // Horizontal rule
            if (line.trim().equals("---") || line.trim().equals("***")) {
                if (inList) { html.append("</ul>\n"); inList = false; }
                html.append("<hr/>\n");
                continue;
            }

            // Headers
            if (line.startsWith("# ")) {
                if (inList) { html.append("</ul>\n"); inList = false; }
                html.append("<h1>").append(formatInline(line.substring(2))).append("</h1>\n");
                continue;
            } else if (line.startsWith("## ")) {
                if (inList) { html.append("</ul>\n"); inList = false; }
                html.append("<h2>").append(formatInline(line.substring(3))).append("</h2>\n");
                continue;
            } else if (line.startsWith("### ")) {
                if (inList) { html.append("</ul>\n"); inList = false; }
                html.append("<h3>").append(formatInline(line.substring(4))).append("</h3>\n");
                continue;
            }

            // Blockquote
            if (line.startsWith("> ")) {
                if (inList) { html.append("</ul>\n"); inList = false; }
                html.append("<blockquote>").append(formatInline(line.substring(2))).append("</blockquote>\n");
                continue;
            }

            // Bullet list
            if (line.trim().startsWith("- ") || line.trim().startsWith("* ")) {
                if (!inList) {
                    html.append("<ul>\n");
                    inList = true;
                }
                String item = line.trim().substring(2);
                html.append("<li>").append(formatInline(item)).append("</li>\n");
                continue;
            } else if (inList) {
                html.append("</ul>\n");
                inList = false;
            }

            // Paragraph
            if (line.trim().isEmpty()) {
                html.append("<br/>\n");
            } else {
                html.append("<p>").append(formatInline(line)).append("</p>\n");
            }
        }

        if (inCodeBlock) html.append("</pre>\n");
        if (inList) html.append("</ul>\n");

        return html.toString();
    }

    private String formatInline(String text) {
        String s = escapeHtml(text);
        // Inline code: `code`
        s = s.replaceAll("`([^`]+)`", "<code>$1</code>");
        // Bold: **text**
        s = s.replaceAll("\\*\\*([^*]+)\\*\\*", "<strong>$1</strong>");
        // Italic: *text*
        s = s.replaceAll("\\*([^*]+)\\*", "<em>$1</em>");
        // Links: [title](url)
        s = s.replaceAll("\\[([^\\]]+)\\]\\(([^)]+)\\)", "<a href=\"$2\">$1</a>");
        return s;
    }

    private String escapeHtml(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private String toHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }
}
