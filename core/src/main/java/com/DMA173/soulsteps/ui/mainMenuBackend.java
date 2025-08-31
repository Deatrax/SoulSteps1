package com.DMA173.soulsteps.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;

/**
 * Manages all game menus including Main Menu, Pause Menu, Settings, etc.
 * Designed to easily support background images and button sprites later.
 */
public class mainMenuBackend {
    
    public enum MenuState {
        MAIN_MENU,
        PAUSE_MENU,
        SETTINGS_MENU,
        CREDITS_MENU,
        SAVE_LOAD_MENU,
        HIDDEN // When no menu is active
    }
    
    private MenuState currentMenuState;
    private OrthographicCamera menuCamera;
    private SpriteBatch menuBatch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private BitmapFont titleFont;
    
    // Menu navigation
    private int selectedOption;
    private float buttonPressTimer;
    private final float BUTTON_PRESS_DELAY = 0.2f;
    
    // Background and button assets (placeholders for future PNG implementation)
    private Texture mainMenuBackground;
    private Texture pauseMenuBackground;
    private Texture settingsMenuBackground;
    private Texture creditsMenuBackground;
    
    // Button textures (placeholders for future PNG implementation)
    private Texture buttonNormal;
    private Texture buttonHover;
    private Texture buttonPressed;
    
    // Menu dimensions and layout
    private final float BUTTON_WIDTH = 200f;
    private final float BUTTON_HEIGHT = 50f;
    private final float BUTTON_SPACING = 20f;
    
    // Colors (used until PNG assets are implemented)
    private final Color BUTTON_COLOR = new Color(0.2f, 0.2f, 0.3f, 0.8f);
    private final Color BUTTON_HOVER_COLOR = new Color(0.3f, 0.3f, 0.5f, 0.9f);
    private final Color BUTTON_TEXT_COLOR = Color.WHITE;
    private final Color MENU_OVERLAY = new Color(0, 0, 0, 0.8f);
    
    public mainMenuBackend() {
        currentMenuState = MenuState.MAIN_MENU;
        selectedOption = 0;
        buttonPressTimer = 0;
        initializeMenuSystem();
        loadMenuAssets();
    }
    
    private void initializeMenuSystem() {
        menuCamera = new OrthographicCamera();
        menuCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        menuBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        // Initialize fonts
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        
        titleFont = new BitmapFont();
        titleFont.getData().setScale(3f);
        
        System.out.println("Menu Manager initialized");
    }
    
    /**
     * Load menu assets. Currently uses placeholders, but designed for easy PNG replacement.
     * TODO: Replace with actual PNG loading when assets are ready
     */
    private void loadMenuAssets() {
        // PLACEHOLDER: Load background textures
        // Uncomment and modify these lines when you have PNG backgrounds:
        /*
        try {
            mainMenuBackground = new Texture("ui/backgrounds/main_menu_bg.png");
            pauseMenuBackground = new Texture("ui/backgrounds/pause_menu_bg.png");
            settingsMenuBackground = new Texture("ui/backgrounds/settings_menu_bg.png");
            creditsMenuBackground = new Texture("ui/backgrounds/credits_menu_bg.png");
        } catch (Exception e) {
            System.out.println("Background textures not found, using solid colors");
        }
        */
        
        // PLACEHOLDER: Load button textures
        // Uncomment and modify these lines when you have PNG buttons:
        /*
        try {
            buttonNormal = new Texture("ui/buttons/button_normal.png");
            buttonHover = new Texture("ui/buttons/button_hover.png");
            buttonPressed = new Texture("ui/buttons/button_pressed.png");
        } catch (Exception e) {
            System.out.println("Button textures not found, using rectangles");
        }
        */
    }
    
    public void update(float delta) {
        buttonPressTimer -= delta;
        handleMenuInput();
    }
    
    public void render() {
        menuBatch.setProjectionMatrix(menuCamera.combined);
        shapeRenderer.setProjectionMatrix(menuCamera.combined);
        
        switch (currentMenuState) {
            case MAIN_MENU:
                renderMainMenu();
                break;
            case PAUSE_MENU:
                renderPauseMenu();
                break;
            case SETTINGS_MENU:
                renderSettingsMenu();
                break;
            case CREDITS_MENU:
                renderCreditsMenu();
                break;
            case SAVE_LOAD_MENU:
                renderSaveLoadMenu();
                break;
            case HIDDEN:
                // Don't render any menu
                break;
        }
    }
    
    private void handleMenuInput() {
        if (buttonPressTimer > 0) return; // Prevent rapid button presses
        
        switch (currentMenuState) {
            case MAIN_MENU:
                handleMainMenuInput();
                break;
            case PAUSE_MENU:
                handlePauseMenuInput();
                break;
            case SETTINGS_MENU:
                handleSettingsMenuInput();
                break;
            case CREDITS_MENU:
                handleCreditsMenuInput();
                break;
            case SAVE_LOAD_MENU:
                handleSaveLoadMenuInput();
                break;
        }
    }
    
    // === MAIN MENU ===
    private void renderMainMenu() {
        // Background
        renderMenuBackground(mainMenuBackground, new Color(0.1f, 0.1f, 0.2f, 1f));
        
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        
        menuBatch.begin();
        
        // Title
        titleFont.setColor(Color.CYAN);
        titleFont.draw(menuBatch, "SOUL STEPS", centerX, centerY + 150, 0, Align.center, false);
        
        menuBatch.end();
        
        // Menu options
        String[] options = {"New Game", "Continue", "Settings", "Credits", "Exit"};
        renderMenuButtons(options, centerX, centerY);
    }
    
    private void handleMainMenuInput() {
        String[] options = {"New Game", "Continue", "Settings", "Credits", "Exit"};
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedOption = (selectedOption - 1 + options.length) % options.length;
            buttonPressTimer = BUTTON_PRESS_DELAY;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedOption = (selectedOption + 1) % options.length;
            buttonPressTimer = BUTTON_PRESS_DELAY;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            executeMainMenuOption(selectedOption);
            buttonPressTimer = BUTTON_PRESS_DELAY;
        }
    }
    
    private void executeMainMenuOption(int option) {
        switch (option) {
            case 0: // New Game
                System.out.println("Starting new game...");
                startNewGame();
                break;
            case 1: // Continue
                System.out.println("Continue game...");
                continueGame();
                break;
            case 2: // Settings
                currentMenuState = MenuState.SETTINGS_MENU;
                selectedOption = 0;
                break;
            case 3: // Credits
                currentMenuState = MenuState.CREDITS_MENU;
                selectedOption = 0;
                break;
            case 4: // Exit
                Gdx.app.exit();
                break;
        }
    }
    
    // === PAUSE MENU ===
    private void renderPauseMenu() {
        // Semi-transparent overlay
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(MENU_OVERLAY);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        
        // Background
        renderMenuBackground(pauseMenuBackground, new Color(0.1f, 0.1f, 0.2f, 0.9f));
        
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        
        menuBatch.begin();
        
        // Title
        titleFont.setColor(Color.YELLOW);
        titleFont.draw(menuBatch, "PAUSED", centerX, centerY + 100, 0, Align.center, false);
        
        menuBatch.end();
        
        // Menu options
        String[] options = {"Resume", "Save Game", "Load Game", "Settings", "Main Menu"};
        renderMenuButtons(options, centerX, centerY);
    }
    
    private void handlePauseMenuInput() {
        String[] options = {"Resume", "Save Game", "Load Game", "Settings", "Main Menu"};
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedOption = (selectedOption - 1 + options.length) % options.length;
            buttonPressTimer = BUTTON_PRESS_DELAY;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedOption = (selectedOption + 1) % options.length;
            buttonPressTimer = BUTTON_PRESS_DELAY;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            executePauseMenuOption(selectedOption);
            buttonPressTimer = BUTTON_PRESS_DELAY;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            currentMenuState = MenuState.HIDDEN; // Resume game
            buttonPressTimer = BUTTON_PRESS_DELAY;
        }
    }
    
    private void executePauseMenuOption(int option) {
        switch (option) {
            case 0: // Resume
                currentMenuState = MenuState.HIDDEN;
                break;
            case 1: // Save Game
                System.out.println("Save game...");
                saveGame();
                break;
            case 2: // Load Game
                System.out.println("Load game...");
                currentMenuState = MenuState.SAVE_LOAD_MENU;
                selectedOption = 0;
                break;
            case 3: // Settings
                currentMenuState = MenuState.SETTINGS_MENU;
                selectedOption = 0;
                break;
            case 4: // Main Menu
                returnToMainMenu();
                break;
        }
    }
    
    // === SETTINGS MENU ===
    private void renderSettingsMenu() {
        renderMenuBackground(settingsMenuBackground, new Color(0.1f, 0.2f, 0.1f, 0.9f));
        
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        
        menuBatch.begin();
        
        // Title
        titleFont.setColor(Color.GREEN);
        titleFont.draw(menuBatch, "SETTINGS", centerX, centerY + 150, 0, Align.center, false);
        
        menuBatch.end();
        
        // Settings options
        String[] options = {"Volume: 100%", "Graphics: High", "Controls", "Reset Game", "Back"};
        renderMenuButtons(options, centerX, centerY);
    }
    
    private void handleSettingsMenuInput() {
        String[] options = {"Volume: 100%", "Graphics: High", "Controls", "Reset Game", "Back"};
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedOption = (selectedOption - 1 + options.length) % options.length;
            buttonPressTimer = BUTTON_PRESS_DELAY;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedOption = (selectedOption + 1) % options.length;
            buttonPressTimer = BUTTON_PRESS_DELAY;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            executeSettingsOption(selectedOption);
            buttonPressTimer = BUTTON_PRESS_DELAY;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            goBackToPreviousMenu();
            buttonPressTimer = BUTTON_PRESS_DELAY;
        }
    }
    
    private void executeSettingsOption(int option) {
        switch (option) {
            case 0: // Volume
                System.out.println("Adjust volume...");
                // TODO: Implement volume slider
                break;
            case 1: // Graphics
                System.out.println("Graphics settings...");
                // TODO: Implement graphics options
                break;
            case 2: // Controls
                System.out.println("Control settings...");
                // TODO: Implement control remapping
                break;
            case 3: // Reset Game
                System.out.println("Reset game progress...");
                resetGameProgress();
                break;
            case 4: // Back
                goBackToPreviousMenu();
                break;
        }
    }
    
    // === CREDITS MENU ===
    private void renderCreditsMenu() {
        renderMenuBackground(creditsMenuBackground, new Color(0.2f, 0.1f, 0.2f, 0.9f));
        
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        
        menuBatch.begin();
        
        // Title
        titleFont.setColor(Color.MAGENTA);
        titleFont.draw(menuBatch, "CREDITS", centerX, centerY + 150, 0, Align.center, false);
        
        // Credits text
        font.setColor(Color.WHITE);
        font.draw(menuBatch, "Game Design: [Your Name]", centerX, centerY + 50, 0, Align.center, false);
        font.draw(menuBatch, "Programming: [Your Name]", centerX, centerY + 20, 0, Align.center, false);
        font.draw(menuBatch, "Art Assets: [Asset Source]", centerX, centerY - 10, 0, Align.center, false);
        font.draw(menuBatch, "Music: [Music Source]", centerX, centerY - 40, 0, Align.center, false);
        font.draw(menuBatch, "Special Thanks: LibGDX Community", centerX, centerY - 70, 0, Align.center, false);
        
        font.setColor(Color.YELLOW);
        font.draw(menuBatch, "Press ESC to go back", centerX, centerY - 120, 0, Align.center, false);
        
        menuBatch.end();
    }
    
    private void handleCreditsMenuInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || 
            Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
            Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            goBackToPreviousMenu();
            buttonPressTimer = BUTTON_PRESS_DELAY;
        }
    }
    
    // === SAVE/LOAD MENU ===
    private void renderSaveLoadMenu() {
        renderMenuBackground(null, new Color(0.1f, 0.1f, 0.1f, 0.9f));
        
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        
        menuBatch.begin();
        
        // Title
        titleFont.setColor(Color.ORANGE);
        titleFont.draw(menuBatch, "SAVE / LOAD", centerX, centerY + 150, 0, Align.center, false);
        
        menuBatch.end();
        
        // Save slot options
        String[] options = {"Save Slot 1", "Save Slot 2", "Save Slot 3", "Auto Save", "Back"};
        renderMenuButtons(options, centerX, centerY);
    }
    
    private void handleSaveLoadMenuInput() {
        String[] options = {"Save Slot 1", "Save Slot 2", "Save Slot 3", "Auto Save", "Back"};
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedOption = (selectedOption - 1 + options.length) % options.length;
            buttonPressTimer = BUTTON_PRESS_DELAY;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedOption = (selectedOption + 1) % options.length;
            buttonPressTimer = BUTTON_PRESS_DELAY;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            executeSaveLoadOption(selectedOption);
            buttonPressTimer = BUTTON_PRESS_DELAY;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            goBackToPreviousMenu();
            buttonPressTimer = BUTTON_PRESS_DELAY;
        }
    }
    
    private void executeSaveLoadOption(int option) {
        switch (option) {
            case 0: // Save Slot 1
                System.out.println("Save to slot 1...");
                saveToSlot(1);
                break;
            case 1: // Save Slot 2
                System.out.println("Save to slot 2...");
                saveToSlot(2);
                break;
            case 2: // Save Slot 3
                System.out.println("Save to slot 3...");
                saveToSlot(3);
                break;
            case 3: // Auto Save
                System.out.println("Load auto save...");
                loadAutoSave();
                break;
            case 4: // Back
                goBackToPreviousMenu();
                break;
        }
    }
    
    // === UTILITY RENDERING METHODS ===
    
    /**
     * Renders a menu background. Uses PNG if available, otherwise solid color.
     */
    private void renderMenuBackground(Texture backgroundTexture, Color fallbackColor) {
        if (backgroundTexture != null) {
            // PLACEHOLDER: Render PNG background when available
            /*
            menuBatch.begin();
            menuBatch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            menuBatch.end();
            */
        } else {
            // Fallback: Solid color background
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(fallbackColor);
            shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            shapeRenderer.end();
        }
    }
    
    /**
     * Renders menu buttons. Uses PNG sprites if available, otherwise rectangles.
     */
    private void renderMenuButtons(String[] options, float centerX, float centerY) {
        float startY = centerY + (options.length - 1) * (BUTTON_HEIGHT + BUTTON_SPACING) / 2f;
        
        for (int i = 0; i < options.length; i++) {
            float buttonX = centerX - BUTTON_WIDTH / 2f;
            float buttonY = startY - i * (BUTTON_HEIGHT + BUTTON_SPACING);
            
            renderSingleButton(options[i], buttonX, buttonY, i == selectedOption);
        }
    }
    
    /**
     * Renders a single button. Easily replaceable with PNG sprites.
     */
    private void renderSingleButton(String text, float x, float y, boolean isSelected) {
        // PLACEHOLDER: Use PNG button sprites when available
        if (buttonNormal != null && buttonHover != null) {
            /*
            menuBatch.begin();
            Texture buttonTexture = isSelected ? buttonHover : buttonNormal;
            menuBatch.draw(buttonTexture, x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
            menuBatch.end();
            */
        } else {
            // Fallback: Rectangle buttons
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(isSelected ? BUTTON_HOVER_COLOR : BUTTON_COLOR);
            shapeRenderer.rect(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
            shapeRenderer.end();
        }
        
        // Button text
        menuBatch.begin();
        font.setColor(BUTTON_TEXT_COLOR);
        font.draw(menuBatch, text, x + BUTTON_WIDTH / 2f, y + BUTTON_HEIGHT / 2f + 10, 0, Align.center, false);
        menuBatch.end();
    }
    
    // === MENU STATE MANAGEMENT ===
    
    public void showMainMenu() {
        currentMenuState = MenuState.MAIN_MENU;
        selectedOption = 0;
    }
    
    public void showPauseMenu() {
        currentMenuState = MenuState.PAUSE_MENU;
        selectedOption = 0;
    }
    
    public void hideAllMenus() {
        currentMenuState = MenuState.HIDDEN;
        selectedOption = 0;
    }
    
    private void goBackToPreviousMenu() {
        // Simple back navigation - you can make this more sophisticated
        switch (currentMenuState) {
            case SETTINGS_MENU:
            case CREDITS_MENU:
            case SAVE_LOAD_MENU:
                currentMenuState = MenuState.MAIN_MENU;
                break;
            default:
                currentMenuState = MenuState.MAIN_MENU;
                break;
        }
        selectedOption = 0;
    }
    
    // === GAME STATE ACTIONS ===
    
    private void startNewGame() {
        // TODO: Integrate with your story system
        hideAllMenus();
        System.out.println("New game started!");
    }
    
    private void continueGame() {
        // TODO: Load the most recent save
        hideAllMenus();
        System.out.println("Continuing game...");
    }
    
    private void saveGame() {
        // TODO: Implement save system
        System.out.println("Game saved!");
    }
    
    private void saveToSlot(int slot) {
        // TODO: Save to specific slot
        System.out.println("Saved to slot " + slot);
    }
    
    private void loadAutoSave() {
        // TODO: Load auto save
        System.out.println("Loading auto save...");
        hideAllMenus();
    }
    
    private void resetGameProgress() {
        // TODO: Reset all progress and return to main menu
        System.out.println("Game progress reset!");
        currentMenuState = MenuState.MAIN_MENU;
        selectedOption = 0;
    }
    
    private void returnToMainMenu() {
        // TODO: Save current progress and return to main menu
        System.out.println("Returning to main menu...");
        currentMenuState = MenuState.MAIN_MENU;
        selectedOption = 0;
    }
    
    // === GETTERS ===
    
    public boolean isMenuActive() {
        return currentMenuState != MenuState.HIDDEN;
    }
    
    public MenuState getCurrentMenuState() {
        return currentMenuState;
    }
    
    public boolean isPauseMenuActive() {
        return currentMenuState == MenuState.PAUSE_MENU;
    }
    
    public boolean isMainMenuActive() {
        return currentMenuState == MenuState.MAIN_MENU;
    }
    
    // === CLEANUP ===
    
    public void resize(int width, int height) {
        menuCamera.viewportWidth = width;
        menuCamera.viewportHeight = height;
        menuCamera.update();
    }
    
    public void dispose() {
        if (menuBatch != null) menuBatch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (font != null) font.dispose();
        if (titleFont != null) titleFont.dispose();
        
        // Dispose background textures when implemented
        if (mainMenuBackground != null) mainMenuBackground.dispose();
        if (pauseMenuBackground != null) pauseMenuBackground.dispose();
        if (settingsMenuBackground != null) settingsMenuBackground.dispose();
        if (creditsMenuBackground != null) creditsMenuBackground.dispose();
        
        // Dispose button textures when implemented
        if (buttonNormal != null) buttonNormal.dispose();
        if (buttonHover != null) buttonHover.dispose();
        if (buttonPressed != null) buttonPressed.dispose();
    }
}