package com.DMA173.soulsteps.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;

/**
 * In-game pause menu that overlays the game screen.
 * Allows returning to main menu or continuing the game.
 */
public class PauseMenu {
    private Game game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    
    //font no longer here
    // private BitmapFont font;
    // private BitmapFont titleFont;
    
    private List<MenuButton> buttons;
    private int selectedButtonIndex = 0;
    private boolean isVisible = false;
    private boolean shouldReturnToMainMenu = false;
    
    // Background
    private Texture backgroundTexture;
    
    // Layout constants
    private static final float BUTTON_WIDTH = 200f;
    private static final float BUTTON_HEIGHT = 50f;
    private static final float BUTTON_SPACING = 20f;
    
    public PauseMenu(Game game) {
        this.game = game;
        initialize();
    }
    
    private void initialize() {
        camera = new OrthographicCamera();
        updateCamera();
        
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        // Initialize fonts no longer needed
        // font = new BitmapFont();
        // font.getData().setScale(1.5f);
        
        // titleFont = new BitmapFont();
        // titleFont.getData().setScale(2.5f);
        
        // Load background
        loadBackground();
        
        // Create buttons
        createButtons();
        
        System.out.println("Pause Menu initialized");
    }
    
    private void loadBackground() {
        // TODO: Load PNG background when available
        try {
            backgroundTexture = new Texture("ui/backgrounds/PauseMenuBackground.png");
        } catch (Exception e) {
            System.out.println("Pause menu background texture not found, using overlay");
            backgroundTexture = null;
        }
    }
    
    private void createButtons() {
        buttons = new ArrayList<>();
        
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        float startY = centerY + 50f;
        
        // Create pause menu buttons
        buttons.add(new MenuButton("Resume", 
            centerX - BUTTON_WIDTH/2f, startY - 0 * (BUTTON_HEIGHT + BUTTON_SPACING), 
            BUTTON_WIDTH, BUTTON_HEIGHT, this::resumeGame));
            
        buttons.add(new MenuButton("Save Game", 
            centerX - BUTTON_WIDTH/2f, startY - 1 * (BUTTON_HEIGHT + BUTTON_SPACING), 
            BUTTON_WIDTH, BUTTON_HEIGHT, this::saveGame));
            
        buttons.add(new MenuButton("Load Game", 
            centerX - BUTTON_WIDTH/2f, startY - 2 * (BUTTON_HEIGHT + BUTTON_SPACING), 
            BUTTON_WIDTH, BUTTON_HEIGHT, this::loadGame));
            
        buttons.add(new MenuButton("Settings", 
            centerX - BUTTON_WIDTH/2f, startY - 3 * (BUTTON_HEIGHT + BUTTON_SPACING), 
            BUTTON_WIDTH, BUTTON_HEIGHT, this::openSettings));
            
        buttons.add(new MenuButton("Main Menu", 
            centerX - BUTTON_WIDTH/2f, startY - 4 * (BUTTON_HEIGHT + BUTTON_SPACING), 
            BUTTON_WIDTH, BUTTON_HEIGHT, this::returnToMainMenu));
        
        // Set first button as selected
        if (!buttons.isEmpty()) {
            buttons.get(0).setSelected(true);
        }
    }
    
    public void update(float delta) {
        if (!isVisible) return;
        
        handleInput();
        
        // Update all buttons
        for (MenuButton button : buttons) {
            button.update(delta);
        }
    }
    
    private void handleInput() {
        // ESC to close pause menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            hide();
            return;
        }
        
        // Keyboard navigation
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            navigateUp();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            navigateDown();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (selectedButtonIndex >= 0 && selectedButtonIndex < buttons.size()) {
                buttons.get(selectedButtonIndex).trigger();
            }
        }
        
        // Mouse navigation
        updateMouseSelection();
    }
    
    private void navigateUp() {
        if (!buttons.isEmpty()) {
            buttons.get(selectedButtonIndex).setSelected(false);
            selectedButtonIndex = (selectedButtonIndex - 1 + buttons.size()) % buttons.size();
            buttons.get(selectedButtonIndex).setSelected(true);
        }
    }
    
    private void navigateDown() {
        if (!buttons.isEmpty()) {
            buttons.get(selectedButtonIndex).setSelected(false);
            selectedButtonIndex = (selectedButtonIndex + 1) % buttons.size();
            buttons.get(selectedButtonIndex).setSelected(true);
        }
    }
    
    private void updateMouseSelection() {
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).isSelected() && i != selectedButtonIndex) {
                // Mouse is over a different button
                buttons.get(selectedButtonIndex).setSelected(false);
                selectedButtonIndex = i;
                break;
            }
        }
    }
    
    public void render() {
        if (!isVisible) return;
        
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        
        // Render semi-transparent overlay
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        
        // Render background panel
        if (backgroundTexture != null) {
            batch.begin();
            float panelWidth = 400f;
            float panelHeight = 500f;
            float panelX = (Gdx.graphics.getWidth() - panelWidth) / 2f;
            float panelY = (Gdx.graphics.getHeight() - panelHeight) / 2f;
            batch.draw(backgroundTexture, panelX, panelY, panelWidth, panelHeight);
            batch.end();
        } else {
            // Fallback panel
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0.1f, 0.1f, 0.2f, 0.9f);
            float panelWidth = 400f;
            float panelHeight = 500f;
            float panelX = (Gdx.graphics.getWidth() - panelWidth) / 2f;
            float panelY = (Gdx.graphics.getHeight() - panelHeight) / 2f;
            shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);
            shapeRenderer.end();
        }
        
        // Render title
        batch.begin();
        // titleFont.setColor(Color.YELLOW);
        // titleFont.draw(batch, "PAUSED", Gdx.graphics.getWidth() / 2f, 
        //                Gdx.graphics.getHeight() / 2f + 150, 0, Align.center, false);

        FontManager.titleFont.setColor(Color.YELLOW);
        FontManager.titleFont.draw(batch, "PAUSED", Gdx.graphics.getWidth() / 2f, 
                                   Gdx.graphics.getHeight() / 2f + 150, 0, Align.center, false);

        batch.end();
        
        // Render buttons
        for (MenuButton button : buttons) {
            // --- PASS FONT FROM MANAGER ---
            button.render(batch, shapeRenderer, FontManager.standardFont);
        }
    }
    
    public void show() {
        isVisible = true;
        selectedButtonIndex = 0;
        if (!buttons.isEmpty()) {
            // Clear all selections first
            for (MenuButton button : buttons) {
                button.setSelected(false);
            }
            buttons.get(0).setSelected(true);
        }
    }
    
    public void hide() {
        isVisible = false;
    }
    
    public boolean isVisible() {
        return isVisible;
    }
    
    public void resize(int width, int height) {
        updateCamera();
        repositionButtons();
    }
    
    private void updateCamera() {
        camera.viewportWidth = Gdx.graphics.getWidth();
        camera.viewportHeight = Gdx.graphics.getHeight();
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();
    }
    
    private void repositionButtons() {
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        float startY = centerY + 50f;
        
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setBounds(
                centerX - BUTTON_WIDTH/2f, 
                startY - i * (BUTTON_HEIGHT + BUTTON_SPACING),
                BUTTON_WIDTH, 
                BUTTON_HEIGHT
            );
        }
    }
    
    public boolean shouldReturnToMainMenu() {
        return shouldReturnToMainMenu;
    }
    
    // Button action methods
    private void resumeGame() {
        System.out.println("Resuming game...");
        hide();
    }
    
    private void saveGame() {
        System.out.println("Saving game...");
        // TODO: Implement save system
    }
    
    private void loadGame() {
        System.out.println("Loading game...");
        // TODO: Implement load system
    }
    
    private void openSettings() {
        System.out.println("Opening settings from pause menu...");
        // TODO: Implement settings screen
    }
    
    private void returnToMainMenu() {
        System.out.println("Returning to main menu...");
        shouldReturnToMainMenu = true;
        hide();
    }
    
    public void dispose() {
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        //font is no longer needed
        // if (font != null) font.dispose();
        // if (titleFont != null) titleFont.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        
        for (MenuButton button : buttons) {
            button.dispose();
        }
    }
}