package com.DMA173.soulsteps.ui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.List;

/**
 * Main menu that appears when the game starts.
 * Handles transitioning to the game screen.
 */
public class MainMenu {
    private Game game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private BitmapFont titleFont;
    
    private List<MenuButton> buttons;
    private int selectedButtonIndex = 0;
    private boolean shouldStartGame = false;
    
    // Background
    private Texture backgroundTexture;
    
    // Layout constants
    private static final float BUTTON_WIDTH = 200f;
    private static final float BUTTON_HEIGHT = 50f;
    private static final float BUTTON_SPACING = 20f;
    
    public MainMenu(Game game) {
        this.game = game;
        initialize();
    }
    
    private void initialize() {
        camera = new OrthographicCamera();
        updateCamera();
        
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        // Initialize fonts
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        
        titleFont = new BitmapFont();
        titleFont.getData().setScale(3f);
        
        // Load background
        loadBackground();
        
        // Create buttons
        createButtons();
        
        System.out.println("Main Menu initialized");
    }
    
    private void loadBackground() {
        // TODO: Load PNG background when available
        try {
            backgroundTexture = new Texture("ui/backgrounds/MainMenuBackgroundGemini1.png");
        } catch (Exception e) {
            System.out.println("Background texture not found, using solid color");
            backgroundTexture = null;
        }
    }
    
    private void createButtons() {
        buttons = new ArrayList<>();
        
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        float startY = centerY + 50f;
        
        // Create menu buttons with proper positioning
        buttons.add(new MenuButton("New Game", 
            centerX - BUTTON_WIDTH/2f, startY - 0 * (BUTTON_HEIGHT + BUTTON_SPACING), 
            BUTTON_WIDTH, BUTTON_HEIGHT, this::startNewGame));
            
        buttons.add(new MenuButton("Continue", 
            centerX - BUTTON_WIDTH/2f, startY - 1 * (BUTTON_HEIGHT + BUTTON_SPACING), 
            BUTTON_WIDTH, BUTTON_HEIGHT, this::continueGame));
            
        buttons.add(new MenuButton("Settings", 
            centerX - BUTTON_WIDTH/2f, startY - 2 * (BUTTON_HEIGHT + BUTTON_SPACING), 
            BUTTON_WIDTH, BUTTON_HEIGHT, this::openSettings));
            
        buttons.add(new MenuButton("Credits", 
            centerX - BUTTON_WIDTH/2f, startY - 3 * (BUTTON_HEIGHT + BUTTON_SPACING), 
            BUTTON_WIDTH, BUTTON_HEIGHT, this::openCredits));
            
        buttons.add(new MenuButton("Exit", 
            centerX - BUTTON_WIDTH/2f, startY - 4 * (BUTTON_HEIGHT + BUTTON_SPACING), 
            BUTTON_WIDTH, BUTTON_HEIGHT, this::exitGame));
        
        // Set first button as selected
        if (!buttons.isEmpty()) {
            buttons.get(0).setSelected(true);
        }
    }
    
    public void update(float delta) {
        handleInput();
        
        // Update all buttons
        for (MenuButton button : buttons) {
            button.update(delta);
        }
    }
    
    private void handleInput() {
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
        
        // Mouse navigation - check if any button is hovered
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
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        
        // Render background
        if (backgroundTexture != null) {
            batch.begin();
            batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.end();
        } else {
            // Fallback solid color
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0.1f, 0.1f, 0.2f, 1f);
            shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            shapeRenderer.end();
        }
        
        // Render title
        batch.begin();
        titleFont.setColor(Color.CYAN);
        titleFont.draw(batch, "SOUL STEPS", Gdx.graphics.getWidth() / 2f, 
                      Gdx.graphics.getHeight() / 2f + 200, 0, Align.center, false);
        batch.end();
        
        // Render buttons
        for (MenuButton button : buttons) {
            button.render(batch, shapeRenderer, font);
        }
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
    
    // Button action methods
    private void startNewGame() {
        System.out.println("Starting new game from main menu...");
        shouldStartGame = true;
    }
    
    private void continueGame() {
        System.out.println("Continuing game from main menu...");
        // TODO: Load save file
        shouldStartGame = true;
    }
    
    private void openSettings() {
        System.out.println("Opening settings...");
        // TODO: Implement settings screen
    }
    
    private void openCredits() {
        System.out.println("Opening credits...");
        // TODO: Implement credits screen
    }
    
    private void exitGame() {
        System.out.println("Exiting game...");
        Gdx.app.exit();
    }
    
    public boolean shouldTransitionToGame() {
        return shouldStartGame;
    }
    
    public void dispose() {
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (font != null) font.dispose();
        if (titleFont != null) titleFont.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        
        for (MenuButton button : buttons) {
            button.dispose();
        }
    }
}