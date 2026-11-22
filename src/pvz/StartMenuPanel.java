package pvz;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

public class StartMenuPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final Color CREAM = new Color(249, 245, 224);
    private static final Color SOFT_GREEN = new Color(191, 210, 173);

    private final GameFrame gameFrame;
    private final AssetManager assetManager;

    public StartMenuPanel(GameFrame gameFrame, AssetManager assetManager) {
        this.gameFrame = gameFrame;
        this.assetManager = assetManager;

        setPreferredSize(new Dimension(GameConstants.FRAME_WIDTH, GameConstants.FRAME_HEIGHT));
        setBackground(UiTheme.BACKGROUND);
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(26, 44, 22, 44));

        JPanel header = transparentPanel(new BorderLayout());
        header.add(label("GARDEN DEFENSE", 12f, Font.BOLD, UiTheme.GOLD), BorderLayout.WEST);
        header.add(label("A BACKYARD BATTLE", 11f, Font.BOLD, SOFT_GREEN), BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel content = transparentPanel(new GridBagLayout());
        GridBagConstraints heroConstraints = new GridBagConstraints();
        heroConstraints.weightx = 1;
        heroConstraints.weighty = 1;
        heroConstraints.fill = GridBagConstraints.BOTH;
        heroConstraints.insets = new Insets(0, 0, 0, 38);
        content.add(new HeroPanel(), heroConstraints);

        GridBagConstraints menuConstraints = new GridBagConstraints();
        menuConstraints.gridx = 1;
        menuConstraints.anchor = GridBagConstraints.CENTER;
        content.add(createMenuCard(), menuConstraints);
        add(content, BorderLayout.CENTER);

        JPanel footer = transparentPanel(new BorderLayout());
        footer.add(label("A little sunshine. A lot of strategy.", 12f, Font.PLAIN, SOFT_GREEN),
                BorderLayout.WEST);
        footer.add(label("PLANT. PROTECT. PREVAIL.", 10f, Font.BOLD, SOFT_GREEN), BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createMenuCard() {
        JPanel card = new MenuCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(30, 28, 28, 28));
        card.setPreferredSize(new Dimension(354, 470));

        card.add(label("LET'S GET GROWING", 10f, Font.BOLD, new Color(75, 107, 61)));
        card.add(Box.createVerticalStrut(14));
        card.add(label("The lawn", 33f, Font.BOLD, UiTheme.INK));
        card.add(label("is yours.", 33f, Font.BOLD, UiTheme.INK));
        card.add(Box.createVerticalStrut(10));
        card.add(label("The garden needs a hero.", 13f, Font.PLAIN, new Color(94, 112, 85)));
        card.add(Box.createVerticalStrut(25));

        JButton newGameButton = createMenuButton("New Game", UiTheme.GOLD, UiTheme.INK, 52);
        newGameButton.addActionListener(event -> gameFrame.startNewGame());
        card.add(newGameButton);
        card.add(Box.createVerticalStrut(10));

        JButton loadButton = createMenuButton("Load Game", UiTheme.PANEL, CREAM, 49);
        loadButton.addActionListener(event -> gameFrame.loadSavedGame());
        card.add(loadButton);
        card.add(Box.createVerticalStrut(10));

        JButton exitButton = createMenuButton("Exit", new Color(231, 232, 210), UiTheme.INK, 44);
        exitButton.addActionListener(event -> System.exit(0));
        card.add(exitButton);
        card.add(Box.createVerticalStrut(22));

        JSeparator divider = new JSeparator();
        divider.setForeground(new Color(211, 216, 188));
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setAlignmentX(LEFT_ALIGNMENT);
        card.add(divider);
        card.add(Box.createVerticalStrut(16));
        card.add(label("Collect sun. Grow your defense.", 11f, Font.BOLD, new Color(92, 112, 75)));
        return card;
    }

    private JButton createMenuButton(String text, Color background, Color foreground, int height) {
        JButton button = new UiButton(text, background, foreground);
        button.setAlignmentX(LEFT_ALIGNMENT);
        button.setFont(UiTheme.font(15f, Font.BOLD));
        button.setPreferredSize(new Dimension(298, height));
        button.setMinimumSize(new Dimension(250, height));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        return button;
    }

    private static JLabel label(String text, float size, int style, Color foreground) {
        JLabel label = new JLabel(text);
        label.setFont(UiTheme.font(size, style));
        label.setForeground(foreground);
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }

    private static JPanel transparentPanel(java.awt.LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(false);
        return panel;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics.create();
        try {
            UiTheme.quality(g2);
            Image background = assetManager.getMenuBackgroundImage();
            if (background != null && background.getWidth(null) > 0 && background.getHeight(null) > 0) {
                double scale = Math.max((double) getWidth() / background.getWidth(null),
                        (double) getHeight() / background.getHeight(null));
                int width = (int) Math.ceil(background.getWidth(null) * scale);
                int height = (int) Math.ceil(background.getHeight(null) * scale);
                g2.drawImage(background, (getWidth() - width) / 2, (getHeight() - height) / 2,
                        width, height, null);
            }
            g2.setPaint(new GradientPaint(0, 0, new Color(13, 36, 25, 230),
                    getWidth(), getHeight(), new Color(24, 52, 33, 210)));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(new Color(242, 215, 127, 13));
            g2.setStroke(new BasicStroke(1f));
            g2.drawOval(-180, getHeight() / 2, 680, 680);
            g2.drawOval(-155, getHeight() / 2 + 25, 630, 630);
        } finally {
            g2.dispose();
        }
    }

    private final class HeroPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        private HeroPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(540, 550));
            setMinimumSize(new Dimension(360, 400));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2 = (Graphics2D) graphics.create();
            try {
                UiTheme.quality(g2);
                double scale = Math.min(1.08, Math.min(getWidth() / 540.0, getHeight() / 550.0));
                g2.translate(0, Math.max(0, (getHeight() - 550 * scale) / 2));
                g2.scale(scale, scale);

                g2.setColor(UiTheme.GOLD);
                g2.fillOval(1, 15, 7, 7);
                g2.setFont(UiTheme.font(11f, Font.BOLD));
                g2.drawString("YOUR LAWN. YOUR LAST LINE.", 19, 22);
                drawTitle(g2, "GARDEN", 0, 120, UiTheme.ACCENT);
                g2.setColor(new Color(245, 205, 107, 80));
                g2.drawLine(3, 150, 351, 150);
                drawTitle(g2, "DEFENSE", 0, 241, CREAM);

                g2.setFont(UiTheme.font(16f, Font.PLAIN));
                g2.setColor(SOFT_GREEN);
                g2.drawString("A sunny lawn. A stubborn horde.", 3, 280);
                g2.drawString("The defense starts with you.", 3, 303);

                g2.setColor(new Color(242, 248, 218, 7));
                g2.fillRoundRect(0, 347, 526, 195, 28, 28);
                g2.setColor(new Color(180, 214, 121, 13));
                g2.fillOval(15, 354, 190, 172);
                g2.setColor(new Color(245, 205, 107, 12));
                g2.fillOval(335, 353, 172, 172);
                g2.setColor(new Color(7, 24, 13, 90));
                g2.fillOval(19, 493, 483, 23);

                UiTheme.drawImageFit(g2, assetManager.getSnowPeaImage(), 16, 346, 174, 174);
                UiTheme.drawImageFit(g2, assetManager.getSunFlowerImage(), 158, 423, 64, 78);
                UiTheme.drawImageFit(g2, assetManager.getTankZombieImage(), 355, 299, 132, 229);

                g2.setColor(UiTheme.ACCENT);
                g2.fillOval(239, 418, 11, 11);
                g2.setColor(new Color(143, 204, 103, 130));
                g2.fillOval(281, 420, 8, 8);
                g2.setColor(new Color(143, 204, 103, 65));
                g2.fillOval(320, 422, 5, 5);
                g2.setFont(UiTheme.font(9f, Font.BOLD));
                g2.setColor(SOFT_GREEN);
                g2.drawString("GROW YOUR DEFENSE", 22, 533);
                g2.drawString("HOLD THE LINE", 371, 533);
            } finally {
                g2.dispose();
            }
        }

        private void drawTitle(Graphics2D graphics, String text, int x, int y, Color color) {
            graphics.setFont(new Font("Impact", Font.PLAIN, 86));
            graphics.setColor(new Color(5, 20, 11, 110));
            graphics.drawString(text, x + 3, y + 5);
            graphics.setColor(color);
            graphics.drawString(text, x, y);
        }
    }

    private static final class MenuCard extends JPanel {
        private static final long serialVersionUID = 1L;

        private MenuCard() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2 = (Graphics2D) graphics.create();
            try {
                UiTheme.quality(g2);
                UiTheme.shadow(g2, 1, 1, getWidth() - 3, getHeight() - 10, 28);
                g2.setColor(CREAM);
                g2.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 10, 28, 28);
                g2.setColor(new Color(246, 216, 133));
                g2.fillRoundRect(28, 1, 47, 4, 4, 4);
                g2.setColor(new Color(255, 255, 255, 100));
                g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 12, 27, 27);
            } finally {
                g2.dispose();
            }
        }
    }
}
