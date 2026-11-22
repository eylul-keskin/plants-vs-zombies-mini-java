package pvz;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.UIManager;

/** Shared presentation values; no game rules or persistent settings live here. */
public final class UiTheme {
    public static final Color BACKGROUND = new Color(23, 43, 35);
    public static final Color PANEL = new Color(32, 56, 45);
    public static final Color SURFACE = new Color(247, 244, 231);
    public static final Color ACCENT = new Color(143, 204, 103);
    public static final Color GOLD = new Color(245, 205, 107);
    public static final Color TEXT = SURFACE;
    public static final Color MUTED = new Color(174, 192, 169);
    public static final Color INK = new Color(35, 56, 43);
    public static final Color LINE = new Color(218, 227, 202);

    private UiTheme() { }

    public static Font font(float size, int style) {
        return new Font("Segoe UI", style, Math.round(size)).deriveFont(size);
    }

    public static void quality(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    public static void drawImageFit(Graphics2D graphics, Image image, int x, int y, int width, int height) {
        if (image == null || width <= 0 || height <= 0) return;
        int sourceWidth = image.getWidth(null);
        int sourceHeight = image.getHeight(null);
        if (sourceWidth <= 0 || sourceHeight <= 0) return;
        double scale = Math.min((double) width / sourceWidth, (double) height / sourceHeight);
        int drawWidth = Math.max(1, (int) Math.round(sourceWidth * scale));
        int drawHeight = Math.max(1, (int) Math.round(sourceHeight * scale));
        graphics.drawImage(image, x + (width - drawWidth) / 2, y + (height - drawHeight) / 2,
                drawWidth, drawHeight, null);
    }

    public static void shadow(Graphics2D graphics, int x, int y, int width, int height, int arc) {
        graphics.setColor(new Color(0, 0, 0, 24));
        graphics.fillRoundRect(x, y + 7, width, height, arc, arc);
        graphics.setColor(new Color(0, 0, 0, 15));
        graphics.fillRoundRect(x - 2, y + 3, width + 4, height + 3, arc + 2, arc + 2);
    }

    public static void installDefaults() {
        Font body = font(13f, Font.PLAIN);
        for (String key : new String[]{"Label.font", "Button.font", "OptionPane.messageFont", "OptionPane.buttonFont"}) {
            UIManager.put(key, body);
        }
        UIManager.put("Panel.background", SURFACE);
        UIManager.put("OptionPane.background", SURFACE);
        UIManager.put("OptionPane.messageForeground", INK);
        UIManager.put("Label.foreground", INK);
        UIManager.put("Button.background", SURFACE);
        UIManager.put("Button.foreground", INK);
        UIManager.put("Button.focus", ACCENT);
        UIManager.put("OptionPane.border", BorderFactory.createEmptyBorder(18, 20, 18, 20));
    }
}
