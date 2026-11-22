package pvz;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class PlantSelectionPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int RESOURCE_WIDTH = 216;
    private static final int CONTROLS_WIDTH = 214;

    private final GameEngine gameEngine;
    private final GameFrame gameFrame;
    private final Map<PlantType, PlantCardButton> plantButtons;
    private final ResourcePanel resourcePanel;
    private final WaveProgressPanel waveProgressPanel;
    private final UiButton pauseButton;
    private final UiButton saveButton;
    private final UiButton menuButton;

    public PlantSelectionPanel(GameEngine gameEngine, GameFrame gameFrame) {
        this.gameEngine = gameEngine;
        this.gameFrame = gameFrame;
        this.plantButtons = new EnumMap<>(PlantType.class);
        this.resourcePanel = new ResourcePanel();
        this.waveProgressPanel = new WaveProgressPanel();
        this.pauseButton = new UiButton("Pause", UiTheme.GOLD, UiTheme.INK);
        this.saveButton = new UiButton("Save", new Color(65, 93, 70), UiTheme.SURFACE);
        this.menuButton = new UiButton("Menu", new Color(65, 93, 70), UiTheme.SURFACE);

        setPreferredSize(new Dimension(GameConstants.GAME_PANEL_WIDTH, GameConstants.SELECTION_PANEL_HEIGHT));
        setBackground(UiTheme.PANEL);
        setLayout(new BorderLayout(16, 0));
        setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        add(resourcePanel, BorderLayout.WEST);
        add(buildPlantPanel(), BorderLayout.CENTER);
        add(buildControlPanel(), BorderLayout.EAST);

        refresh();
    }

    private JPanel buildPlantPanel() {
        JPanel panel = new JPanel(new GridLayout(1, PlantType.values().length, 10, 0));
        panel.setOpaque(false);

        for (PlantType plantType : PlantType.values()) {
            PlantCardButton button = new PlantCardButton(
                    plantType, gameEngine.getAssetManager().getPlantImage(plantType));
            button.addActionListener(event -> gameEngine.setSelectedPlantType(plantType));
            plantButtons.put(plantType, button);
            panel.add(button);
        }

        return panel;
    }

    private JPanel buildControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(CONTROLS_WIDTH, 132));
        JPanel buttons = new JPanel(new GridLayout(1, 3, 6, 0));
        buttons.setOpaque(false);
        buttons.setPreferredSize(new Dimension(CONTROLS_WIDTH, 36));
        pauseButton.setFont(UiTheme.font(12f, Font.BOLD));
        saveButton.setFont(UiTheme.font(12f, Font.BOLD));
        menuButton.setFont(UiTheme.font(12f, Font.BOLD));
        menuButton.setToolTipText("Return to the start menu");

        pauseButton.addActionListener(event -> {
            if (gameEngine.getState() == GameState.IN_GAME) {
                gameEngine.pauseGame();
            } else if (gameEngine.getState() == GameState.PAUSED) {
                gameEngine.resumeGame();
            }
        });

        saveButton.addActionListener(event -> {
            try {
                gameEngine.saveGame();
                JOptionPane.showMessageDialog(this, "Game saved successfully.");
            } catch (IllegalStateException | IOException exception) {
                JOptionPane.showMessageDialog(
                        this,
                        exception.getMessage(),
                        "Save Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        menuButton.addActionListener(event -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Return to the start menu?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION
            );
            if (choice == JOptionPane.YES_OPTION && gameFrame != null) {
                gameFrame.returnToStartMenu();
            }
        });

        buttons.add(pauseButton);
        buttons.add(saveButton);
        buttons.add(menuButton);
        panel.add(buttons, BorderLayout.NORTH);
        panel.add(waveProgressPanel, BorderLayout.CENTER);
        return panel;
    }

    public void refresh() {
        boolean inGame = gameEngine.getState() == GameState.IN_GAME;
        boolean paused = gameEngine.getState() == GameState.PAUSED;

        for (Map.Entry<PlantType, PlantCardButton> entry : plantButtons.entrySet()) {
            PlantCardButton button = entry.getValue();
            button.setEnabled(inGame);
            button.updateState(entry.getKey() == gameEngine.getSelectedPlantType(), gameEngine.getSunAmount());
        }

        pauseButton.setText(paused ? "Resume" : "Pause");
        pauseButton.setEnabled(inGame || paused);
        pauseButton.setToolTipText(paused ? "Resume the game" : "Pause the game");
        saveButton.setEnabled(paused);
        saveButton.setToolTipText(paused ? "Save your current game" : "Pause the game to save");
        menuButton.setEnabled(true);
        resourcePanel.setToolTipText(gameEngine.getStatusMessage());
        resourcePanel.repaint();
        waveProgressPanel.repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics.create();
        try {
            UiTheme.quality(g2);
            g2.setPaint(new GradientPaint(0, 0, new Color(38, 66, 50), 0, getHeight(), UiTheme.BACKGROUND));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(new Color(255, 255, 255, 14));
            g2.drawLine(0, 0, getWidth(), 0);
            g2.setColor(new Color(0, 0, 0, 40));
            g2.fillRect(0, getHeight() - 2, getWidth(), 2);
        } finally {
            g2.dispose();
        }
    }

    private final class ResourcePanel extends JPanel {
        private static final long serialVersionUID = 1L;

        private ResourcePanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(RESOURCE_WIDTH, 132));
            getAccessibleContext().setAccessibleName("Sun reserve and current selection");
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            try {
                UiTheme.quality(g2);
                g2.setColor(new Color(247, 244, 231));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(new Color(232, 225, 199));
                g2.fillRoundRect(12, 12, 43, 48, 14, 14);
                drawSun(g2, 33, 36, 12);
                g2.setFont(UiTheme.font(10f, Font.BOLD));
                g2.setColor(new Color(102, 118, 87));
                g2.drawString("SUN RESERVE", 66, 23);
                g2.setFont(UiTheme.font(31f, Font.BOLD));
                g2.setColor(UiTheme.INK);
                g2.drawString(String.valueOf(gameEngine.getSunAmount()), 64, 55);
                g2.setFont(UiTheme.font(11.5f, Font.BOLD));
                PlantType selected = gameEngine.getSelectedPlantType();
                String selection = selected == null ? "Choose your first plant" : "Selected: " + PlantCardButton.displayName(selected);
                g2.drawString(fitText(selection, g2.getFontMetrics(), getWidth() - 28), 14, 80);
                g2.setColor(new Color(217, 218, 199));
                g2.drawLine(14, 89, getWidth() - 14, 89);
                g2.setFont(UiTheme.font(11f, Font.PLAIN));
                g2.setColor(new Color(105, 117, 97));
                drawStatus(g2, gameEngine.getStatusMessage(), 14, 106, getWidth() - 28);
            } finally {
                g2.dispose();
            }
        }
    }

    private final class WaveProgressPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        private WaveProgressPanel() {
            setOpaque(false);
            getAccessibleContext().setAccessibleName("Wave progress");
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            try {
                UiTheme.quality(g2);
                int completed = gameEngine.getWaveCompletedCount();
                boolean active = gameEngine.isWaveActive();
                boolean victory = gameEngine.getState() == GameState.VICTORY;
                int wave = Math.min(GameConstants.TOTAL_WAVES,
                        active ? gameEngine.getCurrentWave() : completed + 1);
                g2.setColor(new Color(255, 255, 255, 6));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setFont(UiTheme.font(10.5f, Font.BOLD));
                g2.setColor(UiTheme.MUTED);
                g2.drawString("WAVE " + wave + " / " + GameConstants.TOTAL_WAVES, 12, 20);
                String phase = victory ? "CLEARED" : gameEngine.getState() == GameState.PAUSED ? "PAUSED" : active ? "ACTIVE" : "PREPARE";
                g2.setColor(active ? UiTheme.GOLD : UiTheme.ACCENT);
                g2.drawString(phase, getWidth() - 12 - g2.getFontMetrics().stringWidth(phase), 20);
                int barWidth = getWidth() - 24;
                g2.setColor(new Color(9, 28, 20));
                g2.fillRoundRect(12, 32, barWidth, 9, 9, 9);
                int totalSpawns = wave == 1 ? GameConstants.WAVE_ONE_TOTAL_SPAWNS : GameConstants.WAVE_TWO_TOTAL_SPAWNS;
                double partial = active ? Math.min(0.9, (double) gameEngine.getWaveSpawnedCount() / totalSpawns * 0.9) : 0;
                double progress = victory ? 1 : (completed + partial) / GameConstants.TOTAL_WAVES;
                g2.setColor(UiTheme.ACCENT);
                if (progress > 0) g2.fillRoundRect(12, 32, Math.max(8, (int) (barWidth * progress)), 9, 9, 9);
                g2.setColor(new Color(34, 58, 45));
                for (int i = 1; i < GameConstants.TOTAL_WAVES; i++) {
                    g2.fillRect(12 + barWidth * i / GameConstants.TOTAL_WAVES - 1, 32, 2, 9);
                }
                String detail;
                if (victory) {
                    detail = "All waves defeated";
                } else if (gameEngine.getState() == GameState.DEFEAT) {
                    detail = "The zombies reached home";
                } else if (active) {
                    int zombies = gameEngine.getZombies().size();
                    detail = zombies + (zombies == 1 ? " zombie on the lawn" : " zombies on the lawn");
                } else {
                    detail = "Build your garden defense";
                }
                g2.setFont(UiTheme.font(11f, Font.PLAIN));
                g2.setColor(UiTheme.TEXT);
                g2.drawString(fitText(detail, g2.getFontMetrics(), barWidth), 12, 64);
            } finally {
                g2.dispose();
            }
        }
    }

    private static void drawSun(Graphics2D g2, int x, int y, int radius) {
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(196, 150, 42));
        for (int i = 0; i < 8; i++) {
            double angle = Math.PI * i / 4;
            g2.drawLine(x + (int) (Math.cos(angle) * (radius + 3)), y + (int) (Math.sin(angle) * (radius + 3)),
                    x + (int) (Math.cos(angle) * (radius + 6)), y + (int) (Math.sin(angle) * (radius + 6)));
        }
        g2.setColor(UiTheme.GOLD);
        g2.fillOval(x - radius, y - radius, radius * 2, radius * 2);
        g2.setColor(new Color(196, 150, 42));
        g2.drawOval(x - radius, y - radius, radius * 2, radius * 2);
    }

    private static String fitText(String text, FontMetrics metrics, int width) {
        if (metrics.stringWidth(text) <= width) return text;
        String suffix = "\u2026";
        int length = text.length();
        while (length > 0 && metrics.stringWidth(text.substring(0, length) + suffix) > width) length--;
        return text.substring(0, length).trim() + suffix;
    }

    private static void drawStatus(Graphics2D graphics, String message, int x, int y, int width) {
        String remaining = message == null ? "" : message.trim();
        FontMetrics metrics = graphics.getFontMetrics();
        for (int line = 0; line < 2 && !remaining.isEmpty(); line++) {
            if (line == 1 || metrics.stringWidth(remaining) <= width) {
                graphics.drawString(fitText(remaining, metrics, width), x, y + line * 14);
                break;
            }
            int end = remaining.length();
            while (end > 0 && metrics.stringWidth(remaining.substring(0, end)) > width) end--;
            int space = remaining.lastIndexOf(' ', end);
            if (space > 0) end = space;
            if (end == 0) break;
            graphics.drawString(remaining.substring(0, end), x, y + line * 14);
            remaining = remaining.substring(end).trim();
        }
    }
}
