package aura.editor;

import aura.theme.Theme;
import aura.theme.ThemeManager;
import aura.ui.ModernScrollBarUI;
import aura.ui.StatusBar;
import aura.util.FileType;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates an open document, editor pane, gutter, scroll pane, and optional Markdown preview.
 */
public class EditorTab extends JPanel {
    private File file;
    private String title;
    private boolean modified = false;
    private FileType fileType;

    private final CodeEditorPane editorPane;
    private final LineNumberGutter gutter;
    private final JScrollPane scrollPane;
    private final SyntaxHighlighter highlighter;

    private MarkdownPreviewPane previewPane;
    private JSplitPane splitPane;
    private boolean previewActive = false;

    private StatusBar statusBar;
    private final List<Runnable> modificationListeners = new ArrayList<>();

    public EditorTab(String title, File file) {
        this.title = title;
        this.file = file;
        this.fileType = FileType.fromFileName(file != null ? file.getName() : title);

        setLayout(new BorderLayout());

        editorPane = new CodeEditorPane();
        gutter = new LineNumberGutter(editorPane);

        scrollPane = new JScrollPane(editorPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setRowHeaderView(gutter);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());

        highlighter = new SyntaxHighlighter(editorPane, fileType);

        add(scrollPane, BorderLayout.CENTER);

        // Document change tracking
        editorPane.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                markModified();
                updateMetrics();
                if (previewActive && previewPane != null) {
                    previewPane.updateContent(editorPane.getText());
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                markModified();
                updateMetrics();
                if (previewActive && previewPane != null) {
                    previewPane.updateContent(editorPane.getText());
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateMetrics();
            }
        });

        // Caret position tracking
        editorPane.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                updateCaretStatus();
            }
        });

        if (file != null && file.exists()) {
            loadFile(file);
        }

        ThemeManager.addListener(this::applyTheme);
    }

    public void setStatusBar(StatusBar statusBar) {
        this.statusBar = statusBar;
        updateCaretStatus();
        updateMetrics();
        if (statusBar != null) {
            statusBar.updateLanguage(fileType);
            statusBar.updateZoom(editorPane.getZoomPercentage());
        }
    }

    private void applyTheme(Theme theme) {
        setBackground(theme.bgEditor());
        scrollPane.setBackground(theme.bgEditor());
        scrollPane.getViewport().setBackground(theme.bgEditor());
        if (splitPane != null) {
            splitPane.setBackground(theme.border());
        }
        repaint();
    }

    public void toggleMarkdownPreview() {
        if (fileType != FileType.MARKDOWN) return;

        previewActive = !previewActive;
        removeAll();

        if (previewActive) {
            if (previewPane == null) {
                previewPane = new MarkdownPreviewPane();
            }
            previewPane.updateContent(editorPane.getText());

            splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, previewPane);
            splitPane.setResizeWeight(0.5);
            splitPane.setDividerSize(4);
            splitPane.setBorder(BorderFactory.createEmptyBorder());
            splitPane.setBackground(ThemeManager.getTheme().border());
            add(splitPane, BorderLayout.CENTER);
        } else {
            add(scrollPane, BorderLayout.CENTER);
        }

        revalidate();
        repaint();
    }

    public boolean isPreviewActive() {
        return previewActive;
    }

    public void loadFile(File f) {
        try {
            String content = Files.readString(f.toPath(), StandardCharsets.UTF_8);
            editorPane.setText(content);
            editorPane.setCaretPosition(0);
            this.file = f;
            this.title = f.getName();
            this.fileType = FileType.fromFileName(f.getName());
            this.highlighter.setFileType(fileType);
            this.modified = false;
            fireModificationChanged();
            if (statusBar != null) {
                statusBar.updateLanguage(fileType);
                statusBar.setStatusMessage("Loaded " + f.getName());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to load file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean save() {
        if (file == null) {
            return false; // Requires Save As
        }
        try {
            Files.writeString(file.toPath(), editorPane.getText(), StandardCharsets.UTF_8);
            this.modified = false;
            fireModificationChanged();
            if (statusBar != null) {
                statusBar.setStatusMessage("Saved " + file.getName());
            }
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to save file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean saveAs(File targetFile) {
        try {
            Files.writeString(targetFile.toPath(), editorPane.getText(), StandardCharsets.UTF_8);
            this.file = targetFile;
            this.title = targetFile.getName();
            this.fileType = FileType.fromFileName(targetFile.getName());
            this.highlighter.setFileType(fileType);
            this.modified = false;
            fireModificationChanged();
            if (statusBar != null) {
                statusBar.updateLanguage(fileType);
                statusBar.setStatusMessage("Saved as " + targetFile.getName());
            }
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to save file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void markModified() {
        if (!modified) {
            modified = true;
            fireModificationChanged();
        }
    }

    private void fireModificationChanged() {
        for (Runnable r : modificationListeners) {
            r.run();
        }
    }

    public void addModificationListener(Runnable r) {
        modificationListeners.add(r);
    }

    private void updateCaretStatus() {
        if (statusBar == null) return;
        try {
            int caret = editorPane.getCaretPosition();
            Element root = editorPane.getDocument().getDefaultRootElement();
            int line = root.getElementIndex(caret) + 1;
            int col = caret - root.getElement(line - 1).getStartOffset() + 1;
            statusBar.updateCaret(line, col);
            statusBar.updateZoom(editorPane.getZoomPercentage());
        } catch (Exception ex) {
            // ignore
        }
    }

    private void updateMetrics() {
        if (statusBar == null) return;
        String text = editorPane.getText();
        int chars = text.length();
        int words = 0;
        boolean inWord = false;
        for (int i = 0; i < chars; i++) {
            char c = text.charAt(i);
            if (Character.isWhitespace(c)) {
                inWord = false;
            } else if (!inWord) {
                inWord = true;
                words++;
            }
        }
        statusBar.updateMetrics(words, chars);
    }

    public File getFile() {
        return file;
    }

    public String getTitle() {
        return title;
    }

    public boolean isModified() {
        return modified;
    }

    public FileType getFileType() {
        return fileType;
    }

    public CodeEditorPane getEditorPane() {
        return editorPane;
    }
}
