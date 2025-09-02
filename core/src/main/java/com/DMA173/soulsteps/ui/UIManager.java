package com.DMA173.soulsteps.ui;

import java.util.function.Consumer;

import com.DMA173.soulsteps.Charecters.Player;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;

/**
 * Updated UIManager that handles in-game UI elements and integrates with the new pause menu system.
 * Uses proper scaling and positioning that stays centered during window resize.
 */
public class UIManager {
    
    // UI Camera (separate from game world camera)
    private OrthographicCamera uiCamera;
    private SpriteBatch uiBatch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    
    // Menu integration
    private PauseMenu pauseMenu;
    private Game game;
    
    // UI State
    private boolean showDebugInfo = false;
    private String currentObjective = "Investigate the water supply system";
    private String interactionHint = "";
    
    // UI Layout constants (now relative to screen size for proper scaling)
    private static final float HEALTH_BAR_WIDTH_PERCENT = 0.15f; // 15% of screen width
    private static final float HEALTH_BAR_HEIGHT = 20f;
    private static final float KINDNESS_BAR_WIDTH_PERCENT = 0.15f; // 15% of screen width
    private static final float KINDNESS_BAR_HEIGHT = 20f;
    private static final float UI_MARGIN_PERCENT = 0.02f; // 2% of screen size
    
    // Colors
    private static final Color HEALTH_COLOR = Color.RED;
    private static final Color KINDNESS_COLOR = Color.CYAN;
    private static final Color BAR_BACKGROUND = Color.DARK_GRAY;
    private static final Color OBJECTIVE_COLOR = Color.YELLOW;


    // --- NEW: Dialogue Box System ---
    private boolean isDialogueActive = false;
    private String dialogueText;
    private String speakerName;
    private String[] dialogueChoices;
    private Consumer<Integer> choiceCallback; // This will execute the choice
    private Texture dialogueBoxTexture; // For your future PNG background
    
    public UIManager(Game game) {
        this.game = game;
        initializeUI();
    }
    
    private void initializeUI() {
        // Create UI camera that doesn't move with the world
        uiCamera = new OrthographicCamera();
        updateUICamera();
        
        // UI rendering tools
        uiBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        // Load font
        font = new BitmapFont(); // Default font
        font.getData().setScale(1.2f); // Make text bigger
        
        // Initialize pause menu
        pauseMenu = new PauseMenu(game);
        
        // --- FUTURE ASSET LOADING FOR DIALOGUE BOX ---
        // When you have a file like 'assets/ui/dialogue_box.png', uncomment this.
        /*
        try {
            dialogueBoxTexture = new Texture(Gdx.files.internal("ui/dialogue_box.png"));
        } catch (Exception e) {
            System.err.println("Dialogue box texture not found. Using solid color.");
            dialogueBoxTexture = null;
        }
        */

        System.out.println("UI Manager initialized with new menu system");
    }
    
    private void updateUICamera() {
        uiCamera.viewportWidth = Gdx.graphics.getWidth();
        uiCamera.viewportHeight = Gdx.graphics.getHeight();
        uiCamera.position.set(uiCamera.viewportWidth / 2f, uiCamera.viewportHeight / 2f, 0);
        uiCamera.update();
    }
    
    /**
     * Update UI logic including menu handling
     */
    public void update(float delta) {
        if (pauseMenu != null) {
            pauseMenu.update(delta);
            
            // Check if pause menu wants to return to main menu
            if (pauseMenu.shouldReturnToMainMenu()) {
                returnToMainMenu();
            }
        }
    }
    
    /**
     * Render all UI elements including menus when active
     */
    public void render(Player player) {
        // Set UI camera projection
        uiBatch.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        
        // Render the in-game HUD (health, objectives, etc)
        renderGameUI(player);

        // --- NEW: Render the dialogue box if it's active ---
        if (isDialogueActive) {
            renderDialogueBox();
        }
        
        // Render pause menu on top if active
        if (pauseMenu != null) {
            pauseMenu.render();
        }
    }
    
    /**
     * Render in-game UI elements (health, kindness, objectives, etc.)
     * Now uses percentage-based positioning for proper scaling
     */
    private void renderGameUI(Player player) {
        renderHealthBar(player);
        renderKindnessBar(player);
        renderObjectiveText();
        renderInteractionHint();
        renderDebugInfo(player);
    }
    
    private void renderHealthBar(Player player) {
        float margin = Gdx.graphics.getWidth() * UI_MARGIN_PERCENT;
        float barWidth = Gdx.graphics.getWidth() * HEALTH_BAR_WIDTH_PERCENT;
        float x = margin;
        float y = Gdx.graphics.getHeight() - margin - HEALTH_BAR_HEIGHT;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(BAR_BACKGROUND);
        shapeRenderer.rect(x, y, barWidth, HEALTH_BAR_HEIGHT);
        
        float healthPercent = player.getHealth() / 100f;
        shapeRenderer.setColor(HEALTH_COLOR);
        shapeRenderer.rect(x, y, barWidth * healthPercent, HEALTH_BAR_HEIGHT);
        shapeRenderer.end();
        
        uiBatch.begin();
        font.setColor(Color.WHITE);
        font.draw(uiBatch, "Health: " + player.getHealth() + "/100", x, y + HEALTH_BAR_HEIGHT + 15);
        uiBatch.end();
    }
    
    private void renderKindnessBar(Player player) {
        float margin = Gdx.graphics.getWidth() * UI_MARGIN_PERCENT;
        float barWidth = Gdx.graphics.getWidth() * KINDNESS_BAR_WIDTH_PERCENT;
        float x = margin;
        float y = Gdx.graphics.getHeight() - margin - HEALTH_BAR_HEIGHT - 60;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(BAR_BACKGROUND);
        shapeRenderer.rect(x, y, barWidth, KINDNESS_BAR_HEIGHT);
        
        float kindnessPercent = player.getKindnessPercentage();
        Color kindnessColor = player.isDangerZoneActive() ? Color.ORANGE : KINDNESS_COLOR;
        shapeRenderer.setColor(kindnessColor);
        shapeRenderer.rect(x, y, barWidth * kindnessPercent, KINDNESS_BAR_HEIGHT);
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
        
        float margin = Gdx.graphics.getWidth() * UI_MARGIN_PERCENT;
        float x = margin;
        float y = Gdx.graphics.getHeight() / 2f + 50;
        
        uiBatch.begin();
        font.setColor(OBJECTIVE_COLOR);
        font.draw(uiBatch, "OBJECTIVE:", x, y + 20);
        font.draw(uiBatch, currentObjective, x, y, Gdx.graphics.getWidth() * 0.3f, Align.left, true);
        uiBatch.end();
    }
    
    private void renderInteractionHint() {
        if (interactionHint.isEmpty()) return;
        
        float x = Gdx.graphics.getWidth() / 2f;
        float y = Gdx.graphics.getHeight() * 0.1f; // 10% from bottom
        
        uiBatch.begin();
        font.setColor(Color.WHITE);
        font.draw(uiBatch, interactionHint, x, y, 0, Align.center, false);
        uiBatch.end();
    }
    
    private void renderDebugInfo(Player player) {
        if (!showDebugInfo) return;
        
        float margin = Gdx.graphics.getWidth() * UI_MARGIN_PERCENT;
        float x = Gdx.graphics.getWidth() - 250 - margin;
        float y = Gdx.graphics.getHeight() - 30;
        
        uiBatch.begin();
        font.setColor(Color.GREEN);
        font.draw(uiBatch, "Player Pos: " + String.format("%.1f, %.1f", player.getPosition().x, player.getPosition().y), x, y);
        font.draw(uiBatch, "Evidence: " + player.getEvidenceCount(), x, y - 20);
        font.draw(uiBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), x, y - 40);
        font.draw(uiBatch, "Screen: " + Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight(), x, y - 60);
        uiBatch.end();
    }
    
    public void resize(int width, int height) {
        updateUICamera();
        if (pauseMenu != null) {
            pauseMenu.resize(width, height);
        }
    }
    
    // --- Menu Integration Methods ---
    public void showPauseMenu() {
        if (pauseMenu != null) {
            pauseMenu.show();
        }
    }
    
    public void hidePauseMenu() {
        if (pauseMenu != null) {
            pauseMenu.hide();
        }
    }
    
    public boolean isPaused() {
        return pauseMenu != null && pauseMenu.isVisible();
    }
    
    public boolean isAnyMenuActive() {
        return isPaused();
    }
    
    private void returnToMainMenu() {
        // This method will be called when the pause menu requests returning to main menu
        if (game != null) {
            game.setScreen(new com.DMA173.soulsteps.MenuScreen(game));
        }
    }
    
    // --- UI State Management Methods ---
    public void toggleDebugMode() { 
        showDebugInfo = !showDebugInfo; 
        System.out.println("Debug mode: " + (showDebugInfo ? "ON" : "OFF"));
    }
    
    public void setObjective(String objective) { 
        this.currentObjective = objective;
        System.out.println("[UI] Objective updated: " + objective);
    }
    
    public void updateObjective(String newObjective) {
        setObjective(newObjective);
    }
    
    public void setInteractionHint(String hint) { 
        this.interactionHint = hint; 
    }
    
    public void clearInteractionHint() { 
        this.interactionHint = ""; 
    }
    
    public void showNotification(String message) {
        System.out.println("NOTIFICATION: " + message);
        // TODO: Add animated floating text notifications here
        // You can implement a queue of notifications with timers and positions
    }



     // --- NEW: Public methods to control the Dialogue Box ---

    /**
     * Shows a simple line of dialogue or narration.
     * @param speaker The name of the character speaking (or null for narration).
     * @param text The text to display.
     */
    public void showNarration(String speaker, String text) {
        this.speakerName = speaker;
        this.dialogueText = text;
        this.dialogueChoices = null;
        this.choiceCallback = null;
        this.isDialogueActive = true;
    }

    /**
     * Shows dialogue with multiple choices for the player.
     * @param speaker The name of the character asking the question.
     * @param text The question or prompt.
     * @param choices An array of strings representing the choices.
     * @param callback A function that will be executed with the player's choice (1, 2, 3...).
     */
    public void showChoice(String speaker, String text, String[] choices, Consumer<Integer> callback) {
        this.speakerName = speaker;
        this.dialogueText = text;
        this.dialogueChoices = choices;
        this.choiceCallback = callback;
        this.isDialogueActive = true;
    }

    /**
     * Hides the dialogue box.
     */
    public void hideDialogue() {
        this.isDialogueActive = false;
    }

    /**
     * The InputHandler will call this when the dialogue box is active.
     */
    public void handleDialogueInput() {
        if (!isDialogueActive) return;

        if (dialogueChoices != null) {
            // Handle number key presses for choices
            for (int i = 0; i < dialogueChoices.length; i++) {
                System.err.println("[UIManager] iterating dialogue choices =" + i);
                if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i)) {
                    choiceCallback.accept(i + 1); // Execute the chosen action
                    //hideDialogue();
                    return;
                }
            }
        } else {
            // If it's just narration, any key press continues
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.justTouched()) {
                hideDialogue();
            }
        }
    }

    private void renderDialogueBox() {
        float boxHeight = Gdx.graphics.getHeight() * 0.3f; // Box takes up bottom 30% of the screen
        float boxY = 0;
        float boxX = 0;
        float padding = 20f;

        // --- RENDER BACKGROUND ---
        if (dialogueBoxTexture != null) {
            // FUTURE: When you have a PNG, this will draw it.
            // uiBatch.begin();
            // uiBatch.draw(dialogueBoxTexture, boxX, boxY, Gdx.graphics.getWidth(), boxHeight);
            // uiBatch.end();
        } else {
            // Fallback: A simple dark rectangle
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 0.85f);
            shapeRenderer.rect(boxX, boxY, Gdx.graphics.getWidth(), boxHeight);
            shapeRenderer.end();
        }

        // --- RENDER TEXT ---
        uiBatch.begin();
        // Speaker Name
        if (speakerName != null && !speakerName.isEmpty()) {
            font.setColor(Color.YELLOW);
            font.draw(uiBatch, speakerName, boxX + padding, boxY + boxHeight - padding);
        }

        // Main Dialogue Text (with wrapping)
        font.setColor(Color.WHITE);
        GlyphLayout layout = new GlyphLayout(font, dialogueText, Color.WHITE, Gdx.graphics.getWidth() - (padding * 2), Align.left, true);
        font.draw(uiBatch, layout, boxX + padding, boxY + boxHeight - padding - (speakerName != null ? 30 : 0));

        // Choices or "Continue" prompt
        if (dialogueChoices != null) {
            float choiceY = boxY + padding + ((dialogueChoices.length - 1) * 25);
            for (int i = 0; i < dialogueChoices.length; i++) {
                font.draw(uiBatch, (i + 1) + ". " + dialogueChoices[i], boxX + padding, choiceY);
                choiceY -= 25;
            }
        } else {
            font.draw(uiBatch, "Press SPACE to continue...", Gdx.graphics.getWidth() - padding, boxY + padding, 0, Align.right, false);
        }
        uiBatch.end();
    }
    
    // --- State Getters ---
    public boolean isDialogueActive() {
        return isDialogueActive;
    }


    
    public void dispose() {
        if (uiBatch != null) uiBatch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (font != null) font.dispose();
        if (pauseMenu != null) pauseMenu.dispose();
    }
}