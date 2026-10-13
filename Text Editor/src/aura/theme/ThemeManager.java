package aura.theme;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Manages themes and broadcasts theme change events across AuraEdit.
 */
public class ThemeManager {
    private static final List<Theme> THEMES = new ArrayList<>();
    private static Theme currentTheme;
    private static final List<Consumer<Theme>> LISTENERS = new ArrayList<>();

    static {
        // 1. Catppuccin Mocha (Default)
        Theme catppuccinMocha = new Theme(
            "catppuccin-mocha",
            "Catppuccin Mocha",
            true,
            new Color(0x1e, 0x1e, 0x2e), // bgEditor
            new Color(0x18, 0x18, 0x25), // bgGutter
            new Color(0x18, 0x18, 0x25), // bgSidebar
            new Color(0x11, 0x11, 0x1b), // bgHeader
            new Color(0x11, 0x11, 0x1b), // bgStatusBar
            new Color(0x31, 0x32, 0x44), // bgSurface
            new Color(0x45, 0x47, 0x5a), // bgHover
            new Color(0x31, 0x32, 0x44), // bgActive
            new Color(0x24, 0x25, 0x3a), // bgCurrentLine
            new Color(0x45, 0x47, 0x5a, 0x99), // bgSelection
            new Color(0xcd, 0xd6, 0xf4), // fgEditor
            new Color(0x6c, 0x70, 0x86), // fgMuted
            new Color(0xba, 0xc2, 0xde), // fgHeader
            new Color(0xf5, 0xe0, 0xdc), // fgSelection
            new Color(0xcb, 0xa6, 0xf7), // accent (Mauve)
            new Color(0xd8, 0xbe, 0xf9), // accentHover
            new Color(0x31, 0x32, 0x44), // border
            new Color(0x89, 0xb4, 0xfa), // borderFocused
            new Color(0xcb, 0xa6, 0xf7), // syntaxKeyword (Mauve)
            new Color(0x89, 0xdc, 0xeb), // syntaxType (Sky)
            new Color(0xa6, 0xe3, 0xa1), // syntaxString (Green)
            new Color(0xfa, 0xb3, 0x87), // syntaxNumber (Peach)
            new Color(0x6c, 0x70, 0x86), // syntaxComment (Subtext)
            new Color(0x89, 0xb4, 0xfa), // syntaxOperator (Blue)
            new Color(0xf3, 0x8b, 0xa8), // syntaxTag (Red)
            new Color(0xf9, 0xe2, 0xaf), // syntaxAttribute (Yellow)
            new Color(0x58, 0x5b, 0x70, 0x80), // scrollThumb
            new Color(0x7f, 0x84, 0x9c, 0xcc), // scrollThumbHover
            new Color(0x18, 0x18, 0x25, 0x00)  // scrollTrack
        );

        // 2. Tokyo Night
        Theme tokyoNight = new Theme(
            "tokyo-night",
            "Tokyo Night",
            true,
            new Color(0x1a, 0x1b, 0x26), // bgEditor
            new Color(0x16, 0x16, 0x1e), // bgGutter
            new Color(0x16, 0x16, 0x1e), // bgSidebar
            new Color(0x13, 0x13, 0x1a), // bgHeader
            new Color(0x13, 0x13, 0x1a), // bgStatusBar
            new Color(0x24, 0x28, 0x3b), // bgSurface
            new Color(0x2f, 0x35, 0x4e), // bgHover
            new Color(0x24, 0x28, 0x3b), // bgActive
            new Color(0x1f, 0x23, 0x35), // bgCurrentLine
            new Color(0x36, 0x4a, 0x82, 0x88), // bgSelection
            new Color(0xc0, 0xca, 0xf5), // fgEditor
            new Color(0x56, 0x5f, 0x89), // fgMuted
            new Color(0xa9, 0xb1, 0xd6), // fgHeader
            new Color(0xff, 0xff, 0xff), // fgSelection
            new Color(0x7a, 0xa2, 0xf7), // accent
            new Color(0x9d, 0xbe, 0xff), // accentHover
            new Color(0x29, 0x2e, 0x42), // border
            new Color(0x7a, 0xa2, 0xf7), // borderFocused
            new Color(0xbb, 0x9a, 0xf7), // syntaxKeyword
            new Color(0x2a, 0xc3, 0xde), // syntaxType
            new Color(0x9e, 0xce, 0x6a), // syntaxString
            new Color(0xff, 0x9e, 0x64), // syntaxNumber
            new Color(0x56, 0x5f, 0x89), // syntaxComment
            new Color(0x7d, 0xcf, 0xff), // syntaxOperator
            new Color(0xf7, 0x76, 0x8e), // syntaxTag
            new Color(0xe0, 0xaf, 0x68), // syntaxAttribute
            new Color(0x41, 0x48, 0x68, 0x80), // scrollThumb
            new Color(0x56, 0x5f, 0x89, 0xcc), // scrollThumbHover
            new Color(0x16, 0x16, 0x1e, 0x00)  // scrollTrack
        );

        // 3. Nord
        Theme nord = new Theme(
            "nord",
            "Nord Frost",
            true,
            new Color(0x2e, 0x34, 0x40), // bgEditor
            new Color(0x24, 0x29, 0x33), // bgGutter
            new Color(0x24, 0x29, 0x33), // bgSidebar
            new Color(0x1d, 0x21, 0x2a), // bgHeader
            new Color(0x1d, 0x21, 0x2a), // bgStatusBar
            new Color(0x3b, 0x42, 0x52), // bgSurface
            new Color(0x43, 0x4c, 0x5e), // bgHover
            new Color(0x3b, 0x42, 0x52), // bgActive
            new Color(0x35, 0x3c, 0x4a), // bgCurrentLine
            new Color(0x43, 0x4c, 0x5e, 0x99), // bgSelection
            new Color(0xd8, 0xde, 0xe9), // fgEditor
            new Color(0x61, 0x6e, 0x88), // fgMuted
            new Color(0xe5, 0xe9, 0xf0), // fgHeader
            new Color(0xec, 0xef, 0xf4), // fgSelection
            new Color(0x88, 0xc0, 0xd0), // accent
            new Color(0x8f, 0xbc, 0xbb), // accentHover
            new Color(0x3b, 0x42, 0x52), // border
            new Color(0x81, 0xa1, 0xc1), // borderFocused
            new Color(0x81, 0xa1, 0xc1), // syntaxKeyword
            new Color(0x8f, 0xbc, 0xbb), // syntaxType
            new Color(0xa3, 0xbe, 0x8c), // syntaxString
            new Color(0xb4, 0x8e, 0xad), // syntaxNumber
            new Color(0x4c, 0x56, 0x6a), // syntaxComment
            new Color(0x88, 0xc0, 0xd0), // syntaxOperator
            new Color(0xbf, 0x61, 0x6a), // syntaxTag
            new Color(0xeb, 0xcb, 0x8b), // syntaxAttribute
            new Color(0x4c, 0x56, 0x6a, 0x80), // scrollThumb
            new Color(0x61, 0x6e, 0x88, 0xcc), // scrollThumbHover
            new Color(0x24, 0x29, 0x33, 0x00)  // scrollTrack
        );

        // 4. GitHub Light
        Theme githubLight = new Theme(
            "github-light",
            "GitHub Light",
            false,
            new Color(0xff, 0xff, 0xff), // bgEditor
            new Color(0xf6, 0xf8, 0xfa), // bgGutter
            new Color(0xf6, 0xf8, 0xfa), // bgSidebar
            new Color(0xee, 0xf1, 0xf4), // bgHeader
            new Color(0xee, 0xf1, 0xf4), // bgStatusBar
            new Color(0xe1, 0xe4, 0xe8), // bgSurface
            new Color(0xe8, 0xeb, 0xef), // bgHover
            new Color(0xff, 0xff, 0xff), // bgActive
            new Color(0xf4, 0xf7, 0xfb), // bgCurrentLine
            new Color(0xb6, 0xd7, 0xa8, 0x66), // bgSelection
            new Color(0x24, 0x29, 0x2f), // fgEditor
            new Color(0x8c, 0x95, 0x9f), // fgMuted
            new Color(0x37, 0x41, 0x51), // fgHeader
            new Color(0x1f, 0x23, 0x28), // fgSelection
            new Color(0x09, 0x69, 0xda), // accent
            new Color(0x21, 0x8b, 0xff), // accentHover
            new Color(0xd0, 0xd7, 0xde), // border
            new Color(0x09, 0x69, 0xda), // borderFocused
            new Color(0xcf, 0x22, 0x2e), // syntaxKeyword
            new Color(0x95, 0x38, 0x00), // syntaxType
            new Color(0x0a, 0x6c, 0x42), // syntaxString
            new Color(0x05, 0x50, 0xae), // syntaxNumber
            new Color(0x6e, 0x77, 0x81), // syntaxComment
            new Color(0x09, 0x69, 0xda), // syntaxOperator
            new Color(0x11, 0x63, 0x29), // syntaxTag
            new Color(0x82, 0x50, 0xdf), // syntaxAttribute
            new Color(0x8c, 0x95, 0x9f, 0x60), // scrollThumb
            new Color(0x6e, 0x77, 0x81, 0x99), // scrollThumbHover
            new Color(0xf6, 0xf8, 0xfa, 0x00)  // scrollTrack
        );

        // 5. Solarized Light
        Theme solarizedLight = new Theme(
            "solarized-light",
            "Solarized Warm",
            false,
            new Color(0xfd, 0xf6, 0xe3), // bgEditor
            new Color(0xee, 0xe8, 0xd5), // bgGutter
            new Color(0xee, 0xe8, 0xd5), // bgSidebar
            new Color(0xe4, 0xdd, 0xc7), // bgHeader
            new Color(0xe4, 0xdd, 0xc7), // bgStatusBar
            new Color(0xdf, 0xd8, 0xc2), // bgSurface
            new Color(0xe0, 0xd9, 0xc4), // bgHover
            new Color(0xfd, 0xf6, 0xe3), // bgActive
            new Color(0xf5, 0xee, 0xd9), // bgCurrentLine
            new Color(0x83, 0x94, 0x96, 0x40), // bgSelection
            new Color(0x58, 0x6e, 0x75), // fgEditor
            new Color(0x93, 0xa1, 0xa1), // fgMuted
            new Color(0x07, 0x36, 0x42), // fgHeader
            new Color(0x07, 0x36, 0x42), // fgSelection
            new Color(0xb5, 0x89, 0x00), // accent (Yellow/Amber)
            new Color(0xcb, 0x4b, 0x16), // accentHover
            new Color(0xd3, 0x36, 0x82), // border
            new Color(0x26, 0x8b, 0xd2), // borderFocused
            new Color(0x85, 0x99, 0x00), // syntaxKeyword
            new Color(0x26, 0x8b, 0xd2), // syntaxType
            new Color(0x2a, 0xa1, 0x98), // syntaxString
            new Color(0xd3, 0x36, 0x82), // syntaxNumber
            new Color(0x93, 0xa1, 0xa1), // syntaxComment
            new Color(0x65, 0x7b, 0x83), // syntaxOperator
            new Color(0xcb, 0x4b, 0x16), // syntaxTag
            new Color(0xb5, 0x89, 0x00), // syntaxAttribute
            new Color(0x93, 0xa1, 0xa1, 0x60), // scrollThumb
            new Color(0x65, 0x7b, 0x83, 0x99), // scrollThumbHover
            new Color(0xee, 0xe8, 0xd5, 0x00)  // scrollTrack
        );

        THEMES.add(catppuccinMocha);
        THEMES.add(tokyoNight);
        THEMES.add(nord);
        THEMES.add(githubLight);
        THEMES.add(solarizedLight);

        currentTheme = catppuccinMocha;
    }

    public static Theme getTheme() {
        return currentTheme;
    }

    public static List<Theme> getThemes() {
        return Collections.unmodifiableList(THEMES);
    }

    public static void setTheme(Theme theme) {
        if (theme == null || theme.equals(currentTheme)) return;
        currentTheme = theme;
        for (Consumer<Theme> listener : new ArrayList<>(LISTENERS)) {
            try {
                listener.accept(currentTheme);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void toggleDarkLight() {
        if (currentTheme.isDark()) {
            setTheme(getThemeById("github-light"));
        } else {
            setTheme(getThemeById("catppuccin-mocha"));
        }
    }

    public static Theme getThemeById(String id) {
        for (Theme t : THEMES) {
            if (t.id().equalsIgnoreCase(id)) return t;
        }
        return THEMES.get(0);
    }

    public static void addListener(Consumer<Theme> listener) {
        LISTENERS.add(listener);
        listener.accept(currentTheme);
    }

    public static void removeListener(Consumer<Theme> listener) {
        LISTENERS.remove(listener);
    }
}
