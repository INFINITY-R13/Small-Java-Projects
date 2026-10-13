package demo;

import java.util.ArrayList;
import java.util.List;

/**
 * Aesthetic code sample demonstrating AuraEdit's Java syntax highlighting.
 */
public class Example {
    private final String title;
    private final List<String> tags;

    public Example(String title) {
        this.title = title;
        this.tags = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Example{title='" + title + "', count=" + tags.size() + "}";
    }

    public static void main(String[] args) {
        Example demo = new Example("AuraEdit Aesthetics");
        demo.tags.add("Clean");
        demo.tags.add("Modern");
        demo.tags.add("Fast");

        System.out.println("Status: " + demo);
    }
}
