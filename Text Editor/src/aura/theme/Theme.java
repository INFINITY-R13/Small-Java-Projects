package aura.theme;

import java.awt.Color;

/**
 * Record representing a complete aesthetic theme definition for AuraEdit.
 */
public record Theme(
    String id,
    String name,
    boolean isDark,

    // Backgrounds
    Color bgEditor,
    Color bgGutter,
    Color bgSidebar,
    Color bgHeader,
    Color bgStatusBar,
    Color bgSurface,
    Color bgHover,
    Color bgActive,
    Color bgCurrentLine,
    Color bgSelection,

    // Foregrounds
    Color fgEditor,
    Color fgMuted,
    Color fgHeader,
    Color fgSelection,

    // Accents & Borders
    Color accent,
    Color accentHover,
    Color border,
    Color borderFocused,

    // Syntax colors
    Color syntaxKeyword,
    Color syntaxType,
    Color syntaxString,
    Color syntaxNumber,
    Color syntaxComment,
    Color syntaxOperator,
    Color syntaxTag,
    Color syntaxAttribute,

    // Scrollbar
    Color scrollThumb,
    Color scrollThumbHover,
    Color scrollTrack
) {}
