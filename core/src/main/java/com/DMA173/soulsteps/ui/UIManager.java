package com.DMA173.soulsteps.ui;

import com.DMA173.soulsteps.Charecters.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;

/**
 * Manages in-game UI elements that should remain fixed to the screen.
 * Now works alongside MenuManager for comprehensive UI management.
 * Uses a separate camera for UI rendering independent of world camera.
 */
public class UIManager {
    
    // UI Camera (separate from game world camera)
    private OrthographicCamera uiCamera;
    private SpriteBatch uiBatch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    
    // Menu integration
    private MenuManager menuManager;
    
    // UI State
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
    
    public UIManager() {
        initializeUI();
        menuManager = new MenuManager();
        menuManager.hideAllMenus(); // Start with menus hidden in game
    }
    
    private void initializeUI() {
        // Create UI camera that doesn't move with the world
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        // UI rendering tools
        uiBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        // Load font
        font = new BitmapFont(); // Default font
        font.getData().setScale(1.2f); // Make text bigger
        
        System.out.println("UI Manager initialized");
    }
    
    /**
     * Update UI logic including menu handling
     */
    public void update(float delta) {
        if (menuManager != null) {
            menuManager.update(delta);
        }
    }
    
    /**
     * Render all UI elements including menus when active
     */
    public void render(Player player) {
        // Set UI camera projection
        uiBatch.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        
        // Only render game UI if no menu is active
        if (!menuManager.isMenuActive()) {
            renderGameUI(player);
        }
        
        // Always render menu system (it handles its own visibility)
        if (menuManager != null) {
            menuManager.render();
        }
    }
    
    /**
     * Render in-game UI elements (health, kindness, objectives, etc.)
     */
    private void renderGameUI(Player player) {
        renderHealthBar(player);
        renderKindnessBar(player);
        renderObjectiveText();
        renderInteractionHint();
        renderDebugInfo(player);
    }
    
    private void renderHealthBar(Player player) {
        float x = UI_MARGIN;
        float y = Gdx.graphics.getHeight() - UI_MARGIN - HEALTH_BAR_HEIGHT;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(BAR_BACKGROUND);
        shapeRenderer.rect(x, y, HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT);
        
        float healthPercent = player.getHealth() / 100f;
        shapeRenderer.setColor(HEALTH_COLOR);
        shapeRenderer.rect(x, y, HEALTH_BAR_WIDTH * healthPercent, HEALTH_BAR_HEIGHT);
        shapeRenderer.end();
        
        uiBatch.begin();
        font.setColor(Color.WHITE);
        font.draw(uiBatch, "Health: " + player.getHealth() + "/100", x, y + HEALTH_BAR_HEIGHT + 15);
        uiBatch.end();
    }
    
    private void renderKindnessBar(Player player) {
        float x = UI_MARGIN;
        float y = Gdx.graphics.getHeight() - UI_MARGIN - HEALTH_BAR_HEIGHT - 60;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(BAR_BACKGROUND);
        shapeRenderer.rect(x, y, KINDNESS_BAR_WIDTH, KINDNESS_BAR_HEIGHT);
        
        float kindnessPercent = player.getKindnessPercentage();
        Color kindnessColor = player.isDangerZoneActive() ? Color.ORANGE : KINDNESS_COLOR;
        shapeRenderer.setColor(kindnessColor);
        shapeRenderer.rect(x, y, KINDNESS_BAR_WIDTH * kindnessPercent, KINDNESS_BAR_HEIGHT);
        shapeRenderer.end();
        
        uiBatch.begin();
        font.setColor(Color.WHITE);
        font.draw(uiBatch, "Kindness: " + player.getKindnessLevel() + "/100", x, y + KINDNESS_BAR_HEIGHT + 15);
        
        if (player.isDangerZoneActive()) {
            font.setColor(Color.RED);
            font.draw(uiBatch, "DANGER ZONE!", x, y - 10);
        }
        uiBatch.end();
    }
    
    private void renderObjectiveText() {
        if (currentObjective.isEmpty()) return;
        
        float x = UI_MARGIN;
        float y = Gdx.graphics.getHeight() / 2f + 50;
        
        uiBatch.begin();
        font.setColor(OBJECTIVE_COLOR);
        font.draw(uiBatch, "OBJECTIVE:", x, y + 20);
        font.draw(uiBatch, currentObjective, x, y, 300f, Align.left, true);
        uiBatch.end();
    }
    
    private void renderInteractionHint() {
        if (interactionHint.isEmpty()) return;
        
        float x = Gdx.graphics.getWidth() / 2f;
        float y = 60;
        
        uiBatch.begin();
        font.setColor(Color.WHITE);
        font.draw(uiBatch, interactionHint, x, y, 0, Align.center, false);
        uiBatch.end();
    }
    
    private void renderDebugInfo(Player player) {
        if (!showDebugInfo) return;
        
        float x = Gdx.graphics.getWidth() - 250;
        float y = Gdx.graphics.getHeight() - 30;
        
        uiBatch.begin();
        font.setColor(Color.GREEN);
        font.draw(uiBatch, "Player Pos: " + String.format("%.1f, %.1f", player.getPosition().x, player.getPosition().y), x, y);
        font.draw(uiBatch, "Evidence: " + player.getEvidenceCount(), x, y - 20);
        font.draw(uiBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), x, y - 40);
        uiBatch.end();
    }
    
    public void resize(int width, int height) {
        uiCamera.viewportWidth = width;
        uiCamera.viewportHeight = height;
        uiCamera.update();
        
        if (menuManager != null) {
            menuManager.resize(width, height);
        }
    }
    
    // --- Menu Integration Methods ---
    public void showPauseMenu() {
        if (menuManager != null) {
            menuManager.showPauseMenu();
        }
    }
    
    public void hidePauseMenu() {
        if (menuManager != null) {
            menuManager.hideAllMenus();
        }
    }
    
    public boolean isPaused() {
        return menuManager != null && menuManager.isPauseMenuActive();
    }
    
    public boolean isAnyMenuActive() {
        return menuManager != null && menuManager.isMenuActive();
    }
    
    // --- UI State Management Methods ---
    public void toggleDebugMode() { showDebugInfo = !showDebugInfo; }
    public void setObjective(String objective) { this.currentObjective = objective; }
    public void setInteractionHint(String hint) { this.interactionHint = hint; }
    public void clearInteractionHint() { this.interactionHint = ""; }
    
    public void showNotification(String message) {
        System.out.println("NOTIFICATION: " + message);
        // TODO: Add animated floating text notifications here
        // You can implement a queue of notifications with timers and positions
    }
    
    public void dispose() {
        if (uiBatch != null) uiBatch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (font != null) font.dispose();
        if (menuManager != null) menuManager.dispose();
    }
}