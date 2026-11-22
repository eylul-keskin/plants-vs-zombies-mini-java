package pvz;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GamePanel extends JPanel {
    private static final Color LAWN_LIGHT = new Color(128, 164, 86);
    private static final Color LAWN_DARK = new Color(117, 154, 77);
    private static final Color INVALID = new Color(237, 134, 111);
    private final GameEngine gameEngine;
    private int hoverX = -1;
    private int hoverY = -1;

    public GamePanel(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        setPreferredSize(new Dimension(GameConstants.GAME_PANEL_WIDTH, GameConstants.GAME_PANEL_HEIGHT));
        setOpaque(true);
        setBackground(UiTheme.BACKGROUND);

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                if (SwingUtilities.isLeftMouseButton(event)) {
                    handleMousePress(event.getX(), event.getY());
                    updateHover(event.getX(), event.getY());
                }
            }

            @Override
            public void mouseMoved(MouseEvent event) {
                updateHover(event.getX(), event.getY());
            }

            @Override
            public void mouseExited(MouseEvent event) {
                updateHover(-1, -1);
            }
        };
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    private void handleMousePress(int mouseX, int mouseY) {
        if (gameEngine.getState() != GameState.IN_GAME) {
            return;
        }

        if (gameEngine.collectSunAt(mouseX, mouseY)) {
            return;
        }

        // Check pixels before integer division, which rounds negative offsets toward zero.
        if (!isOnBoard(mouseX, mouseY)) {
            return;
        }

        int col = (mouseX - GameConstants.BOARD_X) / GameConstants.CELL_WIDTH;
        int row = (mouseY - GameConstants.BOARD_Y) / GameConstants.CELL_HEIGHT;
        gameEngine.placeSelectedPlant(row, col);
    }

    private boolean isOnBoard(int x, int y) {
        return x >= GameConstants.BOARD_X
                && x < GameConstants.BOARD_X + GameConstants.BOARD_WIDTH
                && y >= GameConstants.BOARD_Y
                && y < GameConstants.BOARD_Y + GameConstants.BOARD_HEIGHT;
    }

    private void updateHover(int x, int y) {
        hoverX = x;
        hoverY = y;
        int cursor = Cursor.DEFAULT_CURSOR;
        if (gameEngine.getState() == GameState.IN_GAME) {
            if (isOverSun(x, y)) {
                cursor = Cursor.HAND_CURSOR;
            } else if (isOnBoard(x, y) && gameEngine.getSelectedPlantType() != null) {
                cursor = Cursor.CROSSHAIR_CURSOR;
            }
        }
        setCursor(Cursor.getPredefinedCursor(cursor));
        repaint();
    }

    private boolean isOverSun(int x, int y) {
        for (Sun sun : gameEngine.getSuns()) {
            if (sun.contains(x, y)) return true;
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics.create();
        UiTheme.quality(g2);

        drawBackground(g2);
        drawBoard(g2);
        drawPlantPreview(g2);
        drawPlants(g2);
        drawBullets(g2);
        drawZombies(g2);
        drawSuns(g2);
        drawFooter(g2);
        drawOverlay(g2);

        g2.dispose();
    }

    private void drawBackground(Graphics2D g2) {
        g2.setPaint(new GradientPaint(0, 0, UiTheme.PANEL, 0, getHeight(), UiTheme.BACKGROUND));
        g2.fillRect(0, 0, getWidth(), getHeight());

        Image backgroundImage = gameEngine.getAssetManager().getYardImage();
        if (backgroundImage != null) {
            g2.setComposite(AlphaComposite.SrcOver.derive(0.09f));
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
            g2.setComposite(AlphaComposite.SrcOver);
        }

        g2.setFont(UiTheme.font(10f, Font.BOLD));
        g2.setColor(UiTheme.MUTED);
        g2.drawString("YOUR GARDEN", GameConstants.BOARD_X, 24);
        drawRightAlignedText(g2, "DEFEND ALL FIVE LANES", GameConstants.BOARD_X + GameConstants.BOARD_WIDTH, 24);
        g2.setColor(new Color(174, 192, 169, 130));
        g2.drawString("INCOMING", GameConstants.BOARD_X + GameConstants.BOARD_WIDTH + 23, 24);
    }

    private void drawBoard(Graphics2D g2) {
        int boardX = GameConstants.BOARD_X;
        int boardY = GameConstants.BOARD_Y;
        int boardWidth = GameConstants.BOARD_WIDTH;
        int boardHeight = GameConstants.BOARD_HEIGHT;
        UiTheme.shadow(g2, boardX - 6, boardY - 6, boardWidth + 12, boardHeight + 12, 22);
        g2.setColor(new Color(205, 220, 158));
        g2.fillRoundRect(boardX - 5, boardY - 5, boardWidth + 10, boardHeight + 10, 18, 18);

        Graphics2D lawn = (Graphics2D) g2.create();
        lawn.clip(new RoundRectangle2D.Float(boardX, boardY, boardWidth, boardHeight, 10, 10));
        for (int row = 0; row < GameConstants.ROWS; row++) {
            int y = GameEngine.getCellPixelY(row);
            lawn.setColor(row % 2 == 0 ? LAWN_LIGHT : LAWN_DARK);
            lawn.fillRect(boardX, y, boardWidth, GameConstants.CELL_HEIGHT);
            for (int col = 0; col < GameConstants.COLS; col++) {
                int x = GameEngine.getCellPixelX(col);
                if ((row + col) % 2 == 0) {
                    lawn.setColor(new Color(242, 249, 200, 10));
                    lawn.fillRect(x, y, GameConstants.CELL_WIDTH, GameConstants.CELL_HEIGHT);
                }
                lawn.setColor(new Color(43, 85, 42, 25));
                lawn.drawLine(x + 15, y + 70, x + 13, y + 65);
                lawn.drawLine(x + 15, y + 70, x + 19, y + 66);
            }
        }

        lawn.setStroke(new BasicStroke(1f));
        lawn.setColor(new Color(45, 83, 37, 48));
        for (int row = 1; row < GameConstants.ROWS; row++) {
            int y = GameEngine.getCellPixelY(row);
            lawn.drawLine(boardX, y, boardX + boardWidth, y);
        }
        lawn.setColor(new Color(237, 246, 202, 48));
        for (int col = 0; col <= GameConstants.COLS; col++) {
            int x = GameEngine.getCellPixelX(col);
            lawn.drawLine(x, boardY, x, boardY + boardHeight);
        }
        lawn.dispose();

        for (int row = 0; row < GameConstants.ROWS; row++) {
            int centerY = GameEngine.getCellCenterY(row);
            g2.setColor(new Color(16, 36, 27, 115));
            g2.fillRoundRect(27, centerY - 18, 35, 36, 12, 12);
            g2.setColor(new Color(219, 232, 192, 50));
            g2.drawRoundRect(27, centerY - 18, 35, 36, 12, 12);
            g2.setFont(UiTheme.font(12f, Font.BOLD));
            g2.setColor(UiTheme.MUTED);
            drawCenteredText(g2, "0" + (row + 1), 27, centerY - 18, 35, 36);

            g2.setColor(new Color(218, 227, 202, 28));
            int markerX = boardX + boardWidth + 49;
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(markerX + 6, centerY - 5, markerX, centerY);
            g2.drawLine(markerX, centerY, markerX + 6, centerY + 5);
        }
    }

    private void drawPlantPreview(Graphics2D g2) {
        PlantType selected = gameEngine.getSelectedPlantType();
        if (gameEngine.getState() != GameState.IN_GAME || selected == null
                || !isOnBoard(hoverX, hoverY) || isOverSun(hoverX, hoverY)) {
            return;
        }

        int col = (hoverX - GameConstants.BOARD_X) / GameConstants.CELL_WIDTH;
        int row = (hoverY - GameConstants.BOARD_Y) / GameConstants.CELL_HEIGHT;
        int x = GameEngine.getCellPixelX(col);
        int y = GameEngine.getCellPixelY(row);
        boolean empty = gameEngine.getBoard().getPlant(row, col) == null;
        boolean valid = empty && gameEngine.getSunAmount() >= selected.getCost();
        Color color = valid ? new Color(228, 249, 173) : INVALID;

        g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 42));
        g2.fillRoundRect(x + 3, y + 3, GameConstants.CELL_WIDTH - 6, GameConstants.CELL_HEIGHT - 6, 14, 14);
        if (empty) {
            Graphics2D preview = (Graphics2D) g2.create();
            preview.setComposite(AlphaComposite.SrcOver.derive(valid ? 0.5f : 0.25f));
            drawEntityOrPlaceholder(preview, gameEngine.getAssetManager().getPlantImage(selected),
                    x + (GameConstants.CELL_WIDTH - GameConstants.PLANT_WIDTH) / 2,
                    y + (GameConstants.CELL_HEIGHT - GameConstants.PLANT_HEIGHT) / 2,
                    GameConstants.PLANT_WIDTH, GameConstants.PLANT_HEIGHT, UiTheme.ACCENT, selected.getDisplayName());
            preview.dispose();
        }
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(color);
        g2.drawRoundRect(x + 3, y + 3, GameConstants.CELL_WIDTH - 7, GameConstants.CELL_HEIGHT - 7, 14, 14);
    }

    private void drawPlants(Graphics2D g2) {
        for (Plant plant : gameEngine.getBoard().getPlants()) {
            int x = GameEngine.getCellPixelX(plant.getCol()) + (GameConstants.CELL_WIDTH - GameConstants.PLANT_WIDTH) / 2;
            int y = GameEngine.getCellPixelY(plant.getRow()) + (GameConstants.CELL_HEIGHT - GameConstants.PLANT_HEIGHT) / 2;
            Image image = gameEngine.getAssetManager().getPlantImage(plant.getType());
            drawGroundShadow(g2, x, y, GameConstants.PLANT_WIDTH, GameConstants.PLANT_HEIGHT);
            drawEntityOrPlaceholder(g2, image, x, y, GameConstants.PLANT_WIDTH, GameConstants.PLANT_HEIGHT,
                    new Color(77, 126, 34), plant.getDisplayName());
            drawHealthBar(g2, x + 11, GameEngine.getCellPixelY(plant.getRow()) + 4,
                    GameConstants.PLANT_WIDTH - 22, plant.getHealth(), plant.getMaxHealth());
        }
    }

    private void drawZombies(Graphics2D g2) {
        for (Zombie zombie : gameEngine.getZombies()) {
            int x = (int) Math.round(zombie.getX());
            int y = GameEngine.getCellPixelY(zombie.getRow()) + (GameConstants.CELL_HEIGHT - GameConstants.ZOMBIE_HEIGHT) / 2;
            Image image = gameEngine.getAssetManager().getZombieImage(zombie.getTypeName());
            drawGroundShadow(g2, x, y, GameConstants.ZOMBIE_WIDTH, GameConstants.ZOMBIE_HEIGHT);
            if (zombie.getSlowTicksRemaining() > 0) {
                g2.setColor(new Color(181, 234, 246, 80));
                g2.fillOval(x + 8, y + GameConstants.ZOMBIE_HEIGHT - 15, GameConstants.ZOMBIE_WIDTH - 16, 16);
            }
            drawEntityOrPlaceholder(g2, image, x, y, GameConstants.ZOMBIE_WIDTH, GameConstants.ZOMBIE_HEIGHT,
                    new Color(115, 115, 115), zombie.getTypeName());
            drawHealthBar(g2, x + 13, GameEngine.getCellPixelY(zombie.getRow()) + 4,
                    GameConstants.ZOMBIE_WIDTH - 26, zombie.getHealth(), zombie.getMaxHealth());
        }
    }

    private void drawBullets(Graphics2D g2) {
        for (Bullet bullet : gameEngine.getBullets()) {
            int x = (int) Math.round(bullet.getX());
            int y = GameEngine.getCellCenterY(bullet.getRow()) - GameConstants.BULLET_SIZE / 2;
            g2.setColor(new Color(24, 58, 28, 60));
            g2.fillOval(x, y + 4, GameConstants.BULLET_SIZE, GameConstants.BULLET_SIZE);
            g2.setColor(bullet.isSnowBullet() ? new Color(160, 229, 240) : new Color(169, 223, 91));
            g2.fillOval(x, y, GameConstants.BULLET_SIZE, GameConstants.BULLET_SIZE);
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(new Color(52, 100, 43));
            g2.drawOval(x, y, GameConstants.BULLET_SIZE, GameConstants.BULLET_SIZE);
            g2.setColor(new Color(255, 255, 237, 180));
            g2.fillOval(x + 3, y + 2, 4, 4);
        }
    }

    private void drawSuns(Graphics2D g2) {
        Image sunImage = gameEngine.getAssetManager().getSunImage();
        for (Sun sun : gameEngine.getSuns()) {
            int x = (int) Math.round(sun.getX());
            int y = (int) Math.round(sun.getY());
            boolean hovered = gameEngine.getState() == GameState.IN_GAME && sun.contains(hoverX, hoverY);
            g2.setColor(new Color(255, 226, 136, hovered ? 90 : 38));
            g2.fillOval(x - 6, y - 6, GameConstants.SUN_SIZE + 12, GameConstants.SUN_SIZE + 12);
            if (sunImage != null) {
                UiTheme.drawImageFit(g2, sunImage, x, y, GameConstants.SUN_SIZE, GameConstants.SUN_SIZE);
            } else {
                g2.setColor(UiTheme.GOLD);
                g2.fillOval(x, y, GameConstants.SUN_SIZE, GameConstants.SUN_SIZE);
                g2.setStroke(new BasicStroke(1.5f));
                g2.setColor(new Color(173, 124, 36));
                g2.drawOval(x, y, GameConstants.SUN_SIZE, GameConstants.SUN_SIZE);
                g2.setFont(UiTheme.font(12f, Font.BOLD));
                drawCenteredText(g2, "+" + sun.getValue(), x, y, GameConstants.SUN_SIZE, GameConstants.SUN_SIZE);
            }
            if (hovered) {
                g2.setStroke(new BasicStroke(1.5f));
                g2.setColor(UiTheme.GOLD);
                g2.drawOval(x - 4, y - 4, GameConstants.SUN_SIZE + 8, GameConstants.SUN_SIZE + 8);
            }
        }
    }

    private void drawGroundShadow(Graphics2D g2, int x, int y, int width, int height) {
        g2.setColor(new Color(26, 56, 27, 56));
        g2.fillOval(x + 5, y + height - 14, width - 10, 17);
        g2.setColor(new Color(26, 56, 27, 30));
        g2.fillOval(x + 12, y + height - 10, width - 24, 9);
    }

    private void drawHealthBar(Graphics2D g2, int x, int y, int width, int health, int maxHealth) {
        if (maxHealth <= 0 || health >= maxHealth || health <= 0) return;
        double fraction = Math.max(0, Math.min(1, (double) health / maxHealth));
        g2.setColor(new Color(24, 47, 28, 185));
        g2.fillRoundRect(x - 1, y - 1, width + 2, 6, 6, 6);
        g2.setColor(fraction > 0.35 ? new Color(212, 239, 139) : INVALID);
        g2.fillRoundRect(x, y, Math.max(2, (int) Math.round(width * fraction)), 4, 4, 4);
    }

    private void drawEntityOrPlaceholder(
            Graphics2D g2,
            Image image,
            int x,
            int y,
            int width,
            int height,
            Color placeholderColor,
            String text
    ) {
        if (image != null) {
            UiTheme.drawImageFit(g2, image, x, y, width, height);
            return;
        }

        g2.setColor(placeholderColor);
        g2.fillRoundRect(x + 6, y + 7, width - 12, height - 12, 24, 24);
        g2.setStroke(new BasicStroke(1f));
        g2.setColor(new Color(239, 247, 215, 150));
        g2.drawRoundRect(x + 6, y + 7, width - 12, height - 12, 24, 24);
        g2.setFont(UiTheme.font(17f, Font.BOLD));
        drawCenteredText(g2, text.replaceAll("[^A-Z]", ""), x, y, width, height);
    }

    private void drawCenteredText(Graphics2D g2, String text, int x, int y, int width, int height) {
        int textWidth = g2.getFontMetrics().stringWidth(text);
        int textX = x + (width - textWidth) / 2;
        int textY = y + (height - g2.getFontMetrics().getHeight()) / 2 + g2.getFontMetrics().getAscent();
        g2.drawString(text, textX, textY);
    }

    private void drawRightAlignedText(Graphics2D g2, String text, int right, int baseline) {
        g2.drawString(text, right - g2.getFontMetrics().stringWidth(text), baseline);
    }

    private void drawFooter(Graphics2D g2) {
        int y = GameConstants.BOARD_Y + GameConstants.BOARD_HEIGHT + 18;
        int x = GameConstants.BOARD_X;
        int width = GameConstants.BOARD_WIDTH;
        g2.setColor(new Color(14, 32, 24, 125));
        g2.fillRoundRect(x, y, width, 35, 12, 12);
        g2.setColor(UiTheme.GOLD);
        g2.fillOval(x + 14, y + 13, 9, 9);
        g2.setFont(UiTheme.font(11f, Font.PLAIN));
        g2.setColor(UiTheme.MUTED);

        String hint = "Choose a seed packet, then click an empty tile. Collect the glowing sun.";
        PlantType selected = gameEngine.getSelectedPlantType();
        if (gameEngine.getState() == GameState.PAUSED) {
            hint = "Your garden is safe while paused. Use the controls above to continue.";
        } else if (gameEngine.getState() == GameState.VICTORY) {
            hint = "Garden protected. Every wave has been cleared.";
        } else if (gameEngine.getState() == GameState.DEFEAT) {
            hint = "A new garden starts with a new plan. Return to the menu to try again.";
        } else if (isOverSun(hoverX, hoverY)) {
            hint = "Click to collect sun and grow your defense.";
        } else if (selected != null && isOnBoard(hoverX, hoverY)) {
            int row = (hoverY - GameConstants.BOARD_Y) / GameConstants.CELL_HEIGHT;
            int col = (hoverX - GameConstants.BOARD_X) / GameConstants.CELL_WIDTH;
            if (gameEngine.getBoard().getPlant(row, col) != null) {
                hint = "This tile is planted. Choose an empty tile for " + selected.getDisplayName() + ".";
            } else if (gameEngine.getSunAmount() < selected.getCost()) {
                hint = "Collect " + (selected.getCost() - gameEngine.getSunAmount()) + " more sun to plant " + selected.getDisplayName() + ".";
            } else {
                hint = "Click to plant " + selected.getDisplayName() + " in lane " + (row + 1) + ".";
            }
        } else if (selected != null) {
            hint = selected.getDisplayName() + " selected. Find an empty tile to plant your defense.";
        }
        g2.drawString(hint, x + 32, y + 22);
        g2.setFont(UiTheme.font(10f, Font.BOLD));
        g2.setColor(new Color(174, 192, 169, 155));
        drawRightAlignedText(g2, "5 LANES  /  9 TILES", x + width - 14, y + 22);
    }

    private void drawOverlay(Graphics2D g2) {
        if (gameEngine.getState() == GameState.IN_GAME) {
            return;
        }

        String title;
        String subtitle;
        String detail;
        String label;
        Color accent;
        if (gameEngine.getState() == GameState.PAUSED) {
            title = "Take a breather.";
            subtitle = "Your garden will be right here.";
            detail = "Resume, save your progress, or return to the menu above.";
            label = "GAME PAUSED";
            accent = UiTheme.GOLD;
        } else if (gameEngine.getState() == GameState.VICTORY) {
            title = "A garden worth defending.";
            subtitle = "All waves cleared. The lawn is yours.";
            detail = "Return to the menu whenever you are ready for another round.";
            label = "VICTORY";
            accent = UiTheme.ACCENT;
        } else if (gameEngine.getState() == GameState.DEFEAT) {
            title = "A little room to grow.";
            subtitle = "A zombie made it through the garden.";
            detail = "Return to the menu and try a different planting strategy.";
            label = "GARDEN OVERRUN";
            accent = INVALID;
        } else {
            return;
        }

        g2.setColor(new Color(13, 29, 22, 165));
        g2.fillRect(0, 0, getWidth(), getHeight());

        int boxWidth = 570;
        int boxHeight = 218;
        int boxX = (getWidth() - boxWidth) / 2;
        int boxY = (getHeight() - boxHeight) / 2;

        UiTheme.shadow(g2, boxX, boxY, boxWidth, boxHeight, 26);
        g2.setColor(UiTheme.SURFACE);
        g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 26, 26);
        g2.setColor(accent);
        g2.fillRoundRect(boxX + (boxWidth - 42) / 2, boxY + 24, 42, 4, 4, 4);

        g2.setColor(new Color(99, 121, 87));
        g2.setFont(UiTheme.font(10f, Font.BOLD));
        drawCenteredText(g2, label, boxX, boxY + 37, boxWidth, 22);

        g2.setColor(UiTheme.INK);
        g2.setFont(UiTheme.font(29f, Font.BOLD));
        drawCenteredText(g2, title, boxX + 20, boxY + 65, boxWidth - 40, 43);
        g2.setFont(UiTheme.font(15f, Font.PLAIN));
        drawCenteredText(g2, subtitle, boxX + 20, boxY + 113, boxWidth - 40, 28);
        g2.setColor(new Color(100, 115, 93));
        g2.setFont(UiTheme.font(12f, Font.PLAIN));
        drawCenteredText(g2, detail, boxX + 20, boxY + 157, boxWidth - 40, 26);
    }
}
