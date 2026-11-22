package pvz;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.BorderFactory;
import javax.swing.JButton;

/** A standard Swing button with a small, consistent custom-painted surface. */
public class UiButton extends JButton {
    private static final long serialVersionUID = 1L;

    public UiButton(String text, Color background, Color foreground) {
        super(text);
        setBackground(background);
        setForeground(foreground);
        setFont(UiTheme.font(13f, Font.BOLD));
        setPreferredSize(new Dimension(112, 38));
        setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        try {
            UiTheme.quality(g2);
            int offset = getModel().isPressed() && isEnabled() ? 1 : 0;
            Color fill = getBackground();
            Color text = getForeground();
            if (!isEnabled()) {
                fill = blend(fill, UiTheme.PANEL, 0.68);
                text = UiTheme.MUTED;
            } else if (getModel().isPressed()) {
                fill = blend(fill, Color.BLACK, 0.12);
            } else if (getModel().isRollover()) {
                fill = blend(fill, Color.WHITE, 0.10);
            }
            g2.setColor(new Color(0, 0, 0, isEnabled() ? 25 : 10));
            g2.fillRoundRect(1, 3, getWidth() - 2, getHeight() - 3, 14, 14);
            g2.setColor(fill);
            g2.fillRoundRect(1, 1 + offset, getWidth() - 2, getHeight() - 4, 14, 14);
            g2.setStroke(new BasicStroke(isFocusOwner() ? 2f : 1f));
            g2.setColor(isFocusOwner() ? UiTheme.GOLD : new Color(255, 255, 255, 30));
            g2.drawRoundRect(1, 1 + offset, getWidth() - 3, getHeight() - 5, 14, 14);
            g2.setFont(getFont());
            g2.setColor(text);
            FontMetrics metrics = g2.getFontMetrics();
            int textX = (getWidth() - metrics.stringWidth(getText())) / 2;
            int textY = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent() - 1 + offset;
            g2.drawString(getText(), textX, textY);
        } finally { g2.dispose(); }
    }

    private static Color blend(Color from, Color to, double amount) {
        return new Color(
                (int) Math.round(from.getRed() * (1 - amount) + to.getRed() * amount),
                (int) Math.round(from.getGreen() * (1 - amount) + to.getGreen() * amount),
                (int) Math.round(from.getBlue() * (1 - amount) + to.getBlue() * amount));
    }
}
