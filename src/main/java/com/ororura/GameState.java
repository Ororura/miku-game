package com.ororura;

import java.awt.*;
import java.util.Random;

public class GameState {
    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;

    private static final int COIN_SIZE = 20;
    private static final int PLAYER_SIZE = 50;
    private static final int SPEED = 4;
    private static final int HUD_HEIGHT = 40;
    private static final int TARGET_SCORE = 5;

    private final Random random = new Random();

    private int playerX = 100;
    private int playerY = 100;
    private int coinX;
    private int coinY;
    private int score = 0;

    private boolean up;
    private boolean down;
    private boolean left;
    private boolean right;

    public GameState() {
        spawnCoin();
    }

    public void update() {
        if (isWon()) {
            return;
        }

        int dx = (right ? 1 : 0) - (left ? 1 : 0);
        int dy = (down ? 1 : 0) - (up ? 1 : 0);

        playerX = clamp(playerX + dx * SPEED, 0, WIDTH - PLAYER_SIZE);
        playerY = clamp(playerY + dy * SPEED, HUD_HEIGHT, HEIGHT - PLAYER_SIZE);

        if (getPlayerBounds().intersects(getCoinBounds())) {
            score++;
            if (!isWon()) {
                spawnCoin();
            }
        }
    }

    public void setMoving(String direction, boolean isPressed) {
        switch (direction) {
            case "UP" -> up = isPressed;
            case "DOWN" -> down = isPressed;
            case "LEFT" -> left = isPressed;
            case "RIGHT" -> right = isPressed;
            default -> throw new IllegalArgumentException("Unknown direction: " + direction);
        }
    }

    private void spawnCoin() {
        do {
            coinX = random.nextInt(WIDTH - COIN_SIZE + 1);
            coinY = HUD_HEIGHT + random.nextInt(HEIGHT - HUD_HEIGHT - COIN_SIZE + 1);
        } while (getPlayerBounds().intersects(getCoinBounds()));
    }

    private int clamp(int value, int min, int max) {
        return Math.clamp(value, min, max);
    }

    public Rectangle getPlayerBounds() {
        return new Rectangle(playerX, playerY, PLAYER_SIZE, PLAYER_SIZE);
    }

    public Rectangle getCoinBounds() {
        return new Rectangle(coinX, coinY, COIN_SIZE, COIN_SIZE);
    }

    public boolean isMovingRight() {
        return right && !left;
    }

    public boolean isMovingLeft() {
        return left && !right;
    }

    public int getScore() {
        return score;
    }

    public boolean isWon() {
        return score >= TARGET_SCORE;
    }
}
