package pvz;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

final class GardenArt {
    private static final Color INK = new Color(24, 36, 47);
    private static final Color TEAL = new Color(75, 178, 150);
    private static final Color LEAF = new Color(145, 207, 125);
    private static final Color GOLD = new Color(249, 202, 104);
    private static final Color IVORY = new Color(255, 242, 192);

    private GardenArt() {
    }

    static BufferedImage ground(Color first, Color second) {
        return draw(480, 300, graphic -> {
            graphic.setPaint(new GradientPaint(0, 0, first, 480, 300, second));
            graphic.fillRect(0, 0, 480, 300);
            for (int row = 0; row < 12; row++) {
                for (int col = 0; col < 17; col++) {
                    int x = col * 30 + (row % 2) * 12;
                    int y = row * 27 + (col % 3) * 5;
                    graphic.setColor(new Color(215, 240, 190, 30));
                    graphic.drawLine(x, y + 4, x + 5, y);
                    graphic.drawLine(x + 1, y + 5, x + 9, y + 5);
                }
            }
        });
    }

    static BufferedImage sun() {
        return draw(100, 100, graphic -> {
            graphic.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            graphic.setColor(GOLD);
            for (int step = 0; step < 8; step++) {
                double angle = step * Math.PI / 4;
                graphic.drawLine((int) (50 + 30 * Math.cos(angle)), (int) (50 + 30 * Math.sin(angle)),
                        (int) (50 + 41 * Math.cos(angle)), (int) (50 + 41 * Math.sin(angle)));
            }
            fillOval(graphic, GOLD, 27, 27, 46, 46);
            fillOval(graphic, IVORY, 35, 34, 22, 20);
        });
    }

    static BufferedImage turret(Color cap, Color light) {
        return draw(100, 110, graphic -> {
            plantBase(graphic);
            rounded(graphic, INK, 24, 19, 59, 53, 16);
            rounded(graphic, cap, 29, 23, 49, 42, 14);
            rounded(graphic, light, 36, 28, 32, 9, 6);
            rounded(graphic, INK, 61, 41, 27, 15, 6);
            rounded(graphic, light, 75, 44, 9, 8, 4);
            fillOval(graphic, IVORY, 41, 44, 6, 6);
        });
    }

    static BufferedImage bloom() {
        return draw(100, 110, graphic -> {
            plantBase(graphic);
            for (int petal = 0; petal < 6; petal++) {
                double angle = petal * Math.PI / 3;
                int x = (int) (50 + 20 * Math.cos(angle));
                int y = (int) (44 + 20 * Math.sin(angle));
                fillOval(graphic, GOLD, x - 10, y - 10, 20, 20);
            }
            fillOval(graphic, INK, 33, 27, 34, 34);
            fillOval(graphic, IVORY, 39, 33, 22, 22);
            fillOval(graphic, TEAL, 46, 40, 8, 8);
        });
    }

    static BufferedImage stone() {
        return draw(100, 110, graphic -> {
            shadow(graphic);
            rounded(graphic, INK, 16, 48, 70, 46, 15);
            rounded(graphic, new Color(131, 152, 163), 20, 51, 62, 38, 12);
            rounded(graphic, new Color(75, 98, 114), 30, 25, 44, 27, 10);
            rounded(graphic, new Color(172, 189, 189), 31, 29, 36, 13, 8);
            graphic.setColor(IVORY);
            graphic.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            graphic.drawLine(34, 67, 61, 67);
            graphic.drawLine(40, 77, 55, 77);
        });
    }

    static BufferedImage burstBud() {
        return draw(100, 110, graphic -> {
            plantBase(graphic);
            Color coral = new Color(224, 113, 112);
            fillOval(graphic, INK, 28, 30, 44, 43);
            fillOval(graphic, coral, 32, 34, 36, 34);
            fillOval(graphic, IVORY, 39, 39, 11, 8);
            graphic.setColor(GOLD);
            graphic.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            graphic.drawLine(50, 24, 50, 15);
            graphic.drawLine(36, 26, 31, 19);
            graphic.drawLine(65, 26, 70, 19);
        });
    }

    static BufferedImage creature(Color body, Color eyes, int width, int rise) {
        return draw(100, 110, graphic -> {
            shadow(graphic);
            int left = (100 - width) / 2;
            int top = 30 - rise;
            graphic.setColor(INK);
            graphic.fillRoundRect(left - 3, top - 3, width + 6, 68 + rise, 25, 25);
            rounded(graphic, body, left, top, width, 61 + rise, 23);
            rounded(graphic, body.brighter(), left + 5, top + 5, width - 10, 16, 12);
            rounded(graphic, INK, left + 5, 51, width - 10, 23, 10);
            fillOval(graphic, eyes, 35, 56, 8, 10);
            fillOval(graphic, eyes, 56, 56, 8, 10);
            rounded(graphic, INK, left + 5, 86, 11, 11, 5);
            rounded(graphic, INK, left + width - 16, 86, 11, 11, 5);
        });
    }

    static BufferedImage tool() {
        return draw(100, 100, graphic -> {
            graphic.setColor(IVORY);
            graphic.setStroke(new BasicStroke(9f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            graphic.drawLine(31, 30, 60, 60);
            rounded(graphic, TEAL, 53, 55, 26, 25, 9);
        });
    }

    private static BufferedImage draw(int width, int height, Consumer<Graphics2D> paint) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphic = image.createGraphics();
        try {
            graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            paint.accept(graphic);
        } finally {
            graphic.dispose();
        }
        return image;
    }

    private static void plantBase(Graphics2D graphic) {
        shadow(graphic);
        rounded(graphic, TEAL, 46, 57, 9, 36, 5);
        fillOval(graphic, LEAF, 28, 72, 24, 13);
        fillOval(graphic, TEAL, 52, 77, 24, 10);
    }

    private static void shadow(Graphics2D graphic) {
        fillOval(graphic, new Color(18, 28, 40, 87), 18, 90, 66, 11);
    }

    private static void fillOval(Graphics2D graphic, Color color, int x, int y, int w, int h) {
        graphic.setColor(color);
        graphic.fillOval(x, y, w, h);
    }

    private static void rounded(Graphics2D graphic, Color color, int x, int y, int w, int h, int r) {
        graphic.setColor(color);
        graphic.fillRoundRect(x, y, w, h, r, r);
    }
}
