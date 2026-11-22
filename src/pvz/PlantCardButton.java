package pvz;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.JButton;

/** Seed packet presentation; selection and purchasing rules remain in GameEngine. */
final class PlantCardButton extends JButton {
    private static final long serialVersionUID = 1L;
    private final PlantType plantType;
    private final Image plantImage;
    private boolean selected;
    private int missingSun;

    PlantCardButton(PlantType plantType, Image plantImage) {
        super(displayName(plantType));
        this.plantType = plantType;
        this.plantImage = plantImage;
        setPreferredSize(new Dimension(116, 132));
        setMinimumSize(new Dimension(104, 132));
        setBorder(BorderFactory.createEmptyBorder());
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(UiTheme.font(12.5f, Font.BOLD));
        getAccessibleContext().setAccessibleName(displayName(plantType) + ", " + plantType.getCost() + " sun");
    }

    void updateState(boolean selected, int sunAmount) {
        int missing = Math.max(0, plantType.getCost() - sunAmount);
        boolean changed = this.selected != selected || missingSun != missing;
        this.selected = selected;
        this.missingSun = missing;
        String description = selected ? "Selected. " : "";
        description += missing > 0 ? "Collect " + missing + " more sun to plant." : "Ready to plant.";
        if (changed || getToolTipText() == null) {
            setToolTipText(displayName(plantType) + " — " + plantType.getCost() + " sun. " + description);
            getAccessibleContext().setAccessibleDescription(description);
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        try {
            UiTheme.quality(g2);
            int width = getWidth();
            int height = getHeight();
            int top = getModel().isPressed() && isEnabled() ? 2 : 0;
            Color fill = selected ? new Color(236, 244, 211) : UiTheme.SURFACE;
            if (isEnabled() && getModel().isRollover()) fill = selected ? new Color(242, 249, 226) : Color.WHITE;
            if (!isEnabled()) fill = new Color(218, 225, 207);
            g2.setColor(new Color(0, 0, 0, 33));
            g2.fillRoundRect(0, 4, width, height - 4, 16, 16);
            g2.setColor(fill);
            g2.fillRoundRect(0, top, width, height - 4, 16, 16);
            g2.setStroke(new BasicStroke(selected || isFocusOwner() ? 2f : 1f));
            g2.setColor(selected ? UiTheme.GOLD : isFocusOwner() ? UiTheme.ACCENT : new Color(213, 222, 197));
            g2.drawRoundRect(1, 1 + top, width - 3, height - 6, 15, 15);

            g2.setFont(UiTheme.font(8.5f, Font.BOLD));
            g2.setColor(missingSun > 0 ? new Color(144, 91, 60) : new Color(114, 131, 93));
            String role = missingSun > 0 ? "NEED " + missingSun + " SUN" : role(plantType);
            centered(g2, role, width, 16 + top);

            g2.setColor(new Color(226, 234, 208));
            g2.fillOval((width - 60) / 2, 22 + top, 60, 54);
            if (plantImage != null) {
                UiTheme.drawImageFit(g2, plantImage, (width - 66) / 2, 23 + top, 66, 53);
            } else {
                drawSeedling(g2, width / 2, 57 + top);
            }
            if (selected) {
                int badgeX = width - 23;
                g2.setColor(new Color(74, 119, 56));
                g2.fillOval(badgeX, 24 + top, 16, 16);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(badgeX + 4, 32 + top, badgeX + 7, 35 + top);
                g2.drawLine(badgeX + 7, 35 + top, badgeX + 12, 29 + top);
            }

            g2.setFont(getFont());
            g2.setColor(UiTheme.INK);
            centered(g2, displayName(plantType), width, 92 + top);
            int chipWidth = 72;
            int chipX = (width - chipWidth) / 2;
            g2.setColor(missingSun > 0 ? new Color(232, 218, 191) : new Color(238, 222, 166));
            g2.fillRoundRect(chipX, 100 + top, chipWidth, 21, 11, 11);
            g2.setColor(new Color(187, 143, 38));
            g2.fillOval(chipX + 9, 106 + top, 9, 9);
            g2.setFont(UiTheme.font(11.5f, Font.BOLD));
            g2.setColor(missingSun > 0 ? new Color(129, 99, 68) : UiTheme.INK);
            g2.drawString(String.valueOf(plantType.getCost()), chipX + 26, 115 + top);
        } finally {
            g2.dispose();
        }
    }

    private static void centered(Graphics2D graphics, String text, int width, int baseline) {
        FontMetrics metrics = graphics.getFontMetrics();
        graphics.drawString(text, (width - metrics.stringWidth(text)) / 2, baseline);
    }

    private static void drawSeedling(Graphics2D graphics, int x, int y) {
        graphics.setColor(new Color(87, 139, 54));
        graphics.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.drawLine(x, y + 13, x, y - 12);
        graphics.fillOval(x - 18, y - 12, 18, 10);
        graphics.fillOval(x, y - 18, 19, 11);
    }

    static String displayName(PlantType type) {
        return type.getDisplayName();
    }

    private static String role(PlantType type) {
        return switch (type) {
            case PEA_SHOOTER -> "RANGED";
            case SUN_FLOWER -> "SUN PRODUCER";
            case WALL_NUT -> "DEFENSE";
            case SNOW_PEA -> "SLOWING";
            case CHERRY_BOMB -> "EXPLOSIVE";
        };
    }
}
