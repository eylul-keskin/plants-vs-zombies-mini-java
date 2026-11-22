package pvz;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Image;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GameFrame extends JFrame {
    private static final String START_CARD = "start";
    private static final String GAME_CARD = "game";

    private final AssetManager assetManager;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final StartMenuPanel startMenuPanel;

    private JPanel gameContainer;
    private GameEngine gameEngine;
    private GamePanel gamePanel;
    private PlantSelectionPanel plantSelectionPanel;

    public GameFrame() {
        super("Garden Defense");
        UiTheme.installDefaults();
        this.assetManager = new AssetManager();
        this.cardLayout = new CardLayout();
        this.cardPanel = new JPanel(cardLayout);
        this.cardPanel.setBackground(UiTheme.BACKGROUND);
        this.startMenuPanel = new StartMenuPanel(this, assetManager);

        Image iconImage = assetManager.getWindowIconImage();
        if (iconImage != null) {
            setIconImage(iconImage);
        }

        cardPanel.add(startMenuPanel, START_CARD);
        add(cardPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        Rectangle available = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        setSize(Math.min(GameConstants.FRAME_WIDTH, available.width),
                Math.min(GameConstants.FRAME_HEIGHT, available.height));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void startNewGame() {
        createGameScreen();
        gameEngine.startNewGame();
        plantSelectionPanel.refresh();
        cardLayout.show(cardPanel, GAME_CARD);
    }

    public void loadSavedGame() {
        if (!Files.exists(Path.of(GameConstants.SAVE_FILE_PATH))) {
            JOptionPane.showMessageDialog(
                    this,
                    "No save file found at " + GameConstants.SAVE_FILE_PATH,
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        createGameScreen();
        try {
            gameEngine.loadGame();
            plantSelectionPanel.refresh();
            cardLayout.show(cardPanel, GAME_CARD);
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE
            );
            returnToStartMenu();
        }
    }

    public void returnToStartMenu() {
        if (gameEngine != null) {
            gameEngine.shutdown();
        }
        cardLayout.show(cardPanel, START_CARD);
        cardPanel.repaint();
    }

    private void createGameScreen() {
        if (gameEngine != null) {
            gameEngine.shutdown();
        }

        if (gameContainer != null) {
            cardPanel.remove(gameContainer);
        }

        gameEngine = new GameEngine(assetManager);
        gamePanel = new GamePanel(gameEngine);
        plantSelectionPanel = new PlantSelectionPanel(gameEngine, this);

        gameEngine.setUiRefreshCallback(() -> SwingUtilities.invokeLater(() -> {
            gamePanel.repaint();
            plantSelectionPanel.refresh();
        }));

        gameContainer = new JPanel(new BorderLayout());
        gameContainer.add(plantSelectionPanel, BorderLayout.NORTH);
        gameContainer.add(gamePanel, BorderLayout.CENTER);
        cardPanel.add(gameContainer, GAME_CARD);
        cardPanel.revalidate();
        cardPanel.repaint();
    }
}
