package com.ororura;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class GamePanel extends JPanel {
    private final GameState state = new GameState();
    private final BufferedImage[] idleFrames;
    private final BufferedImage[] rightFrames;
    private final BufferedImage[] leftFrames;
    private final BufferedImage coinSprite;
    private int tickCount = 0;

    private void registerKey(String key, String direction) {
        bind("pressed " + key, key + "-press", direction, true);
        bind("released " + key, key + "-release", direction, false);
    }

    public GamePanel() {
        setPreferredSize(new Dimension(GameState.WIDTH, GameState.HEIGHT));
        setBackground(Color.WHITE);
        BufferedImage spriteSheet = loadImage("/sprites/miku.png");
        idleFrames = extractFrames(spriteSheet, 0, 7);
        rightFrames = extractFrames(spriteSheet, 1, 7);
        leftFrames = extractFrames(spriteSheet, 2, 7);
        coinSprite = loadImage("/sprites/onion.png");
        configureControls();

        Timer timer = new Timer(16, e -> tick());
        timer.start();
    }


    private void tick() {
        state.update();
        tickCount++;
        repaint();
    }


    private BufferedImage getCurrentSprite() {
        int frame = (tickCount / 6) % 7;

        if (state.isMovingRight()) {
            return rightFrames[frame];
        }

        if (state.isMovingLeft()) {
            return leftFrames[frame];
        }

        return idleFrames[frame];
    }


    private BufferedImage[] extractFrames(
            BufferedImage sheet, int row, int count) {

        int frameWidth = sheet.getWidth() / 8;
        int frameHeight = sheet.getHeight() / 11;

        BufferedImage[] frames = new BufferedImage[count];

        for (int i = 0; i < count; i++) {
            int x = i * frameWidth;
            int y = row * frameHeight;

            frames[i] = sheet.getSubimage(
                    x, y, frameWidth, frameHeight);
        }

        return frames;
    }


    private BufferedImage loadImage(String path) {
        try (InputStream input =
                     getClass().getResourceAsStream(path)) {

            if (input == null) {
                throw new IllegalArgumentException(
                        "Файл не найден: " + path);
            }

            BufferedImage image = ImageIO.read(input);

            if (image == null) {
                throw new IllegalArgumentException(
                        "Неверный формат: " + path);
            }

            return image;
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Ошибка загрузки: " + path, e);
        }
    }


    private void configureControls() {
        registerKey("W", "UP");
        registerKey("UP", "UP");

        registerKey("S", "DOWN");
        registerKey("DOWN", "DOWN");

        registerKey("A", "LEFT");
        registerKey("LEFT", "LEFT");

        registerKey("D", "RIGHT");
        registerKey("RIGHT", "RIGHT");
    }

    private void bind(String stroke, String action, String direction, boolean pressed) {
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(stroke), action);
        getActionMap().put(action, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                state.setMoving(direction, pressed);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();

        try {
            drawHud(g);
            drawObject(g);
            if (state.isWon()) drawWinScreen(g);
        } finally {
            g.dispose();
        }
    }

    public void drawHud(Graphics2D g2d) {
        g2d.setColor(Color.DARK_GRAY);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2d.drawString("Луковицы: " + state.getScore() + " / 5", 16, 26);
    }

    private void drawObject(Graphics2D g2d) {

        Rectangle player = state.getPlayerBounds();
        Rectangle coin = state.getCoinBounds();

        int spriteWidth = 64;
        int spriteHeight = 70;

        int x = player.x - (spriteWidth - player.width) / 2;
        int y = player.y + player.height - spriteHeight;
        g2d.drawImage(getCurrentSprite(), x, y, spriteWidth, spriteHeight, null);

        int coinSpriteWidth = 64;
        int coinSpriteHeight = 64;

        int coinX = coin.x - (coinSpriteWidth - coin.width) / 2;
        int coinY = coin.y + coin.height - coinSpriteHeight;

        g2d.drawImage(coinSprite, coinX, coinY, coinSpriteWidth, coinSpriteHeight, null);
    }

    private void drawWinScreen(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 170));
        g2d.fillRect(0, 0, GameState.WIDTH, GameState.HEIGHT);

        String message = "Победа! Вы собрали все луковицы!";
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
        g2d.drawString(message, (GameState.WIDTH - g2d.getFontMetrics().stringWidth(message)) / 2, 240);
    }
}
