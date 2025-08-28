package com.DMA173.soulsteps.UI;

import com.DMA173.soulsteps.Charecters.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;

/**
 * Manages all UI elements that should remain fixed to the screen.
 * Uses a separate camera for UI rendering independent of world camera.
 */
public class UIManager {
    
    // UI Camera (separate from game world camera)
    private OrthographicCamera uiCamera;
    private SpriteBatch uiBatch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    
    // UI State
    private boolean isPaused = false;
    private boolean showDebugInfo = false;
    private String currentObjective = "Investigate the water supply system";
    private String interactionHint = "";
    
    // UI Layout constants
    private static final float HEALTH_BAR_WIDTH = 200f;
    private static final float HEALTH_BAR_HEIGHT = 20f;
    private static final float KINDNESS_BAR_WIDTH = 200f;
    private static final float KINDNESS_BAR_HEIGHT = 20f;
    private static final float UI_MARGIN = 20f;
    
    // Colors
    private static final Color HEALTH_COLOR = Color.RED;
    private static final Color KINDNESS_COLOR = Color.CYAN;
    private static final Color BAR_BACKGROUND = Color.DARK_GRAY;
    private static final Color OBJECTIVE_COLOR = Color.YELLOW;
    private static final Color PAUSE_OVERLAY = new Color(0, 0, 0, 0.7f);
    
    public UIManager() {
        initializeUI();
    }
    
    private void initializeUI() {
        // Create UI camera that doesn't move with the world
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        // UI rendering tools
        uiBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        // Load font (you might want to use a custom font file)
        font = new BitmapFont(); // Default font
        font.getData().setScale(1.2f); // Make text bigger
        
        System.out.println("UI Manager initialized");
    }
    
    /**
     * Render all UI elements
     */
    public void render(Player player) {
        // Set UI camera projection
        uiBatch.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        
        // Render UI elements
        renderHealthBar(player);
        renderKindnessBar(player);
        renderObjectiveText();
        renderInteractionHint();
        renderDebugInfo(player);
        
        // Render pause menu if paused
        if (isPaused) {
            renderPauseMenu();
        }
    }
    
    /**
     * Render health bar in top-left corner
     */
    private void renderHealthBar(Player player) {
        float x = UI_MARGIN;
        float y = Gdx.graphics.getHeight() - UI_MARGIN - HEALTH_BAR_HEIGHT;
        
        // Background bar
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(BAR_BACKGROUND);
        shapeRenderer.rect(x, y, HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT);
        
        // Health bar fill
        float healthPercent = player.getHealth() / 100f;
        shapeRenderer.setColor(HEALTH_COLOR);
        shapeRenderer.rect(x, y, HEALTH_BAR_WIDTH * healthPercent, HEALTH_BAR_HEIGHT);
        shapeRenderer.end();
        
        // Health text
        uiBatch.begin();
        font.setColor(Color.WHITE);
        font.draw(uiBatch, "Health: " + player.getHealth() + "/100", 
                 x, y + HEALTH_BAR_HEIGHT + 15);
        uiBatch.end();
    }
    
    /**
     * Render kindness bar below health bar
     */
    private void renderKindnessBar(Player player) {
        float x = UI_MARGIN;
        float y = Gdx.graphics.getHeight() - UI_MARGIN - HEALTH_BAR_HEIGHT - 60;
        
        // Background bar
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(BAR_BACKGROUND);
        shapeRenderer.rect(x, y, KINDNESS_BAR_WIDTH, KINDNESS_BAR_HEIGHT);
        
        // Kindness bar fill
        float kindnessPercent = player.getKindnessPercentage();
        Color kindnessColor = player.isDangerZoneActive() ? Color.ORANGE : KINDNESS_COLOR;
        shapeRenderer.setColor(kindnessColor);
        shapeRenderer.rect(x, y, KINDNESS_BAR_WIDTH * kindnessPercent, KINDNESS_BAR_HEIGHT);
        shapeRenderer.end();
        
        // Kindness text
        uiBatch.begin();
        font.setColor(Color.WHITE);
        font.draw(uiBatch, "Kindness: " + player.getKindnessLevel() + "/100", 
                 x, y + KINDNESS_BAR_HEIGHT + 15);
        
        if (player.isDangerZoneActive()) {
            font.setColor(Color.RED);
            font.draw(uiBatch, "DANGER ZONE!", x, y - 10);
        }
        uiBatch.end();
    }
    
    /**
     * Render objective text on center-left of screen
     */
    private void renderObjectiveText() {
        if (currentObjective.isEmpty()) return;
        
        float x = UI_MARGIN;
        float y = Gdx.graphics.getHeight() / 2f + 50; // Center-left, slightly up
        
        uiBatch.begin();
        font.setColor(OBJECTIVE_COLOR);
        
        // Draw objective title
        font.draw(uiBatch, "OBJECTIVE:", x, y + 20);
        
        // Draw objective description (with text wrapping)
        font.draw(uiBatch, currentObjective, x, y, 300f, Align.left, true);
        uiBatch.end();
    }
    
    /**
     * Render interaction hint at bottom of screen
     */
    private void renderInteractionHint() {
        if (interactionHint.isEmpty()) return;
        
        float x = Gdx.graphics.getWidth() / 2f;
        float y = 60; // Bottom of screen
        
        uiBatch.begin();
        font.setColor(Color.WHITE);
        font.draw(uiBatch, interactionHint, x, y, 0, Align.center, false);
        uiBatch.end();
    }
    
    /**
     * Render debug information
     */
    private void renderDebugInfo(Player player) {
        if (!showDebugInfo) return;
        
        float x = Gdx.graphics.getWidth() - 250;
        float y = Gdx.graphics.getHeight() - 30;
        
        uiBatch.begin();
        font.setColor(Color.GREEN);
        
        // Player position
        font.draw(uiBatch, "Player Pos: " + 
                 String.format("%.1f, %.1f", player.getPosition().x, player.getPosition().y), 
                 x, y);
        
        // Evidence count
        font.draw(uiBatch, "Evidence: " + player.getEvidenceCount(), x, y - 20);
        
        // FPS
        font.draw(uiBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), x, y - 40);
        
        // Controls reminder
        font.draw(uiBatch, "F3: Toggle Debug", x, y - 60);
        font.draw(uiBatch, "ESC: Pause", x, y - 80);
        font.draw(uiBatch, "E: Interact", x, y - 100);
        
        uiBatch.end();
    }
    
    /**
     * Render pause menu overlay
     */
    private void renderPauseMenu() {
        // Semi-transparent overlay
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(PAUSE_OVERLAY);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        
        // Pause menu content
        uiBatch.begin();
        font.setColor(Color.WHITE);
        
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        
        // Title
        font.getData().setScale(2f);
        font.draw(uiBatch, "GAME PAUSED", centerX, centerY + 100, 0, Align.center, false);
        
        // Menu options
        font.getData().setScale(1.5f);
        font.draw(uiBatch, "ESC - Resume Game", centerX, centerY + 20, 0, Align.center, false);
        font.draw(uiBatch, "R - Restart Level", centerX, centerY - 20, 0, Align.center, false);
        font.draw(uiBatch, "Q - Quit to Menu", centerX, centerY - 60, 0, Align.center, false);
        
        // Reset font scale
        font.getData().setScale(1.2f);
        uiBatch.end();
    }
    
    /**
     * Update UI camera when screen resizes
     */
    public void resize(int width, int height) {
        uiCamera.viewportWidth = width;
        uiCamera.viewportHeight = height;
        uiCamera.update();
    }
    
    // --- UI State Management ---
    public void togglePause() {
        isPaused = !isPaused;
    }
    
    public void setPaused(boolean paused) {
        isPaused = paused;
    }
    
    public boolean isPaused() {
        return isPaused;
    }
    
    public void toggleDebugMode() {
        showDebugInfo = !showDebugInfo;
        System.out.println("Debug mode: " + (showDebugInfo ? "ON" : "OFF"));
    }
    
    public void setObjective(String objective) {
        this.currentObjective = objective;
    }
    
    public void setInteractionHint(String hint) {
        this.interactionHint = hint;
    }
    
    public void clearInteractionHint() {
        this.interactionHint = "";
    }
    
    /**
     * Show floating text notification
     */
    public void showNotification(String message) {
        // You could implement a floating text system here
        System.out.println("NOTIFICATION: " + message);
        // For now, just print to console, but you could add animated floating text
    }
    
    public void dispose() {
        if (uiBatch != null) uiBatch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (font != null) font.dispose();
    }
}