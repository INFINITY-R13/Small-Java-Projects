package aura.editor;

import aura.theme.Theme;
import aura.theme.ThemeManager;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Gutter displaying line numbers with current line highlighting and synchronized scrolling.
 */
public class LineNumberGutter extends JPanel implements DocumentListener, CaretListener {
    private final CodeEditorPane editor;
    private int currentLine = 0;
    private static final int HORIZONTAL_PADDING = 12;

    public LineNumberGutter(CodeEditorPane editor) {
        this.editor = editor;
        editor.getDocument().addDocumentListener(this);
        editor.addCaretListener(this);

        setOpaque(true);
        updateWidth();
        ThemeManager.addListener(this::applyTheme);
    }

    private void applyTheme(Theme theme) {
        setBackground(theme.bgGutter());
        repaint();
    }

    public void updateWidth() {
        Element root = editor.getDocument().getDefaultRootElement();
        int lineCount = Math.max(1, root.getElementCount());
        String maxDigits = String.valueOf(lineCount);
        FontMetrics fm = getFontMetrics(editor.getFont());
        int width = fm.stringWidth(maxDigits) + (HORIZONTAL_PADDING * 2);
        width = Math.max(width, 42); // minimum pleasant width
        setPreferredSize(new Dimension(width, editor.getHeight()));
        revalidate();
        repaint();
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        try {
            int caret = editor.getCaretPosition();
            Element root = editor.getDocument().getDefaultRootElement();
            int line = root.getElementIndex(caret);
            if (line != currentLine) {
                currentLine = line;
                repaint();
            }
        } catch (Exception ex) {
            // ignore
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        updateWidth();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updateWidth();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        updateWidth();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        Theme theme = ThemeManager.getTheme();
        int width = getWidth();
        int height = getHeight();

        // Background
        g2d.setColor(theme.bgGutter());
        g2d.fillRect(0, 0, width, height);

        // Right separator hairline
        g2d.setColor(theme.border());
        g2d.drawLine(width - 1, 0, width - 1, height);

        Font editorFont = editor.getFont();
        g2d.setFont(editorFont.deriveFont(Font.PLAIN, editorFont.getSize2D() * 0.9f));
        FontMetrics fm = g2d.getFontMetrics();

        Element root = editor.getDocument().getDefaultRootElement();
        int lineCount = root.getElementCount();

        Rectangle clip = g2d.getClipBounds();

        for (int i = 0; i < lineCount; i++) {
            Element lineElem = root.getElement(i);
            int startOffset = lineElem.getStartOffset();
            try {
                Rectangle2D r = editor.modelToView2D(startOffset);
                if (r == null) continue;

                int y = (int) r.getY();
                int lineHeight = (int) r.getHeight();

                if (clip != null && (y + lineHeight < clip.y || y > clip.y + clip.height)) {
                    continue; // Skip off-screen lines for high performance
                }

                boolean isActive = (i == currentLine);
                if (isActive) {
                    g2d.setColor(theme.accent());
                    g2d.setFont(editorFont.deriveFont(Font.BOLD, editorFont.getSize2D() * 0.9f));
                } else {
                    g2d.setColor(theme.fgMuted());
                    g2d.setFont(editorFont.deriveFont(Font.PLAIN, editorFont.getSize2D() * 0.9f));
                }

                String lineNum = String.valueOf(i + 1);
                int stringWidth = fm.stringWidth(lineNum);
                int textX = width - HORIZONTAL_PADDING - stringWidth;
                int textY = y + fm.getAscent() + (lineHeight - fm.getHeight()) / 2;

                g2d.drawString(lineNum, textX, textY);
            } catch (BadLocationException e) {
                // ignore
            }
        }

        g2d.dispose();
    }
}
