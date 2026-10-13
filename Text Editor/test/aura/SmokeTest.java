package aura;

import aura.editor.CodeEditorPane;
import aura.editor.EditorTab;
import aura.theme.Theme;
import aura.theme.ThemeManager;
import aura.util.FileType;
import aura.util.Icons;

import java.io.File;

public class SmokeTest {
    public static void main(String[] args) {
        System.out.println("Starting AuraEdit headless smoke tests...");

        // 1. ThemeManager tests
        assert ThemeManager.getThemes().size() >= 5 : "Should have at least 5 themes";
        Theme current = ThemeManager.getTheme();
        System.out.println("Default theme: " + current.name());

        ThemeManager.toggleDarkLight();
        System.out.println("After toggle: " + ThemeManager.getTheme().name());
        assert !ThemeManager.getTheme().name().equals(current.name()) : "Toggle theme should change theme";

        // 2. FileType detection tests
        assert FileType.fromFileName("App.java") == FileType.JAVA;
        assert FileType.fromFileName("script.py") == FileType.PYTHON;
        assert FileType.fromFileName("notes.md") == FileType.MARKDOWN;
        assert FileType.fromFileName("config.json") == FileType.JSON;
        assert FileType.fromFileName("query.sql") == FileType.SQL;
        System.out.println("FileType detections passed.");

        // 3. Vector Icons creation tests
        assert Icons.createFileIcon(current.accent(), 16) != null;
        assert Icons.createFolderIcon(current.accent(), 16) != null;
        assert Icons.createPlusIcon(current.accent(), 16) != null;
        assert Icons.createSearchIcon(current.accent(), 16) != null;
        System.out.println("Vector Icons creation passed.");

        // 4. Editor Tab buffer tests
        EditorTab tab = new EditorTab("Test.java", null);
        CodeEditorPane pane = tab.getEditorPane();
        pane.setText("public class Test {\n    public static void main(String[] args) {}\n}");
        assert tab.isModified() : "Editing text should mark tab modified";
        System.out.println("Editor buffer & modification tracking passed.");

        // 5. Test Markdown preview toggle
        EditorTab mdTab = new EditorTab("Doc.md", new File("demo/Welcome.md"));
        mdTab.toggleMarkdownPreview();
        assert mdTab.isPreviewActive() : "Markdown preview should be active";
        System.out.println("Markdown preview passed.");

        System.out.println("ALL SMOKE TESTS PASSED SUCCESSFULLY! ✓");
    }
}
