package com.DMA173.soulsteps;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {
    private Game game;
    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;
    
    // --- Placeholders for your future art assets ---
    private Texture backgroundTexture;
    
    // For Keyboard Navigation
    private List<TextButton> buttons = new ArrayList<>();
    private int selectedButtonIndex = 0;

    public MainMenuScreen(Game game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();

        // --- BACKGROUND IMPLEMENTATION ---
        // FUTURE: When you have 'main_menu_bg.png' in your 'assets/ui/' folder, uncomment this block.
        /*
        try {
            backgroundTexture = new Texture(Gdx.files.internal("ui/main_menu_bg.png"));
        } catch (Exception e) {
            System.err.println("Main menu background not found.");
            backgroundTexture = null;
        }
        */

        createSkin();

        Table table = new Table();
        table.setFillParent(true); // This ensures the table resizes with the window
        table.center();
        stage.addActor(table);
        
        // --- Create and Add Buttons ---
        TextButton newGameButton = new TextButton("New Game", skin);
        TextButton continueButton = new TextButton("Continue", skin); // Placeholder
        TextButton settingsButton = new TextButton("Settings", skin); // Placeholder
        TextButton creditsButton = new TextButton("Credits", skin);   // Placeholder
        TextButton exitButton = new TextButton("Exit", skin);

        buttons.add(newGameButton);
        buttons.add(continueButton);
        buttons.add(settingsButton);
        buttons.add(creditsButton);
        buttons.add(exitButton);

        table.add(newGameButton).fillX().uniformX().width(300).height(50).pad(10);
        table.row();
        table.add(continueButton).fillX().uniformX().width(300).height(50).pad(10);
        table.row();
        table.add(settingsButton).fillX().uniformX().width(300).height(50).pad(10);
        table.row();
        table.add(creditsButton).fillX().uniformX().width(300).height(50).pad(10);
        table.row();
        table.add(exitButton).fillX().uniformX().width(300).height(50).pad(10);

        // --- Add MOUSE CLICK Listeners ---
        newGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startNewGame();
            }
        });
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        // Add listeners for other buttons here...
    }

    private void createSkin() {
        skin = new Skin();
        skin.add("default", new BitmapFont());

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = skin.getFont("default");
        style.fontColor = Color.WHITE;
        
        // --- BUTTON STYLE IMPLEMENTATION ---
        // FUTURE: To use your own button images, you would load them into the skin here.
        // For example:
        // Skin skin = new Skin(Gdx.files.internal("ui/myskin.json"));
        // This programmatic approach is a placeholder.
        skin.add("default", style);
    }
    
    private void handleKeyboardInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedButtonIndex = (selectedButtonIndex - 1 + buttons.size()) % buttons.size();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedButtonIndex = (selectedButtonIndex + 1) % buttons.size();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            // "Click" the selected button
            buttons.get(selectedButtonIndex).getClickListener().clicked(null, 0, 0);
        }
    }
    
    private void updateButtonStyles() {
        for (int i = 0; i < buttons.size(); i++) {
            if (i == selectedButtonIndex) {
                buttons.get(i).getLabel().setColor(Color.YELLOW); // Highlight selected button
            } else {
                buttons.get(i).getLabel().setColor(Color.WHITE);
            }
        }
    }
    
    private void startNewGame() {
        // This cleanly disposes the menu and creates a fresh game screen.
        System.out.println("Starting a new game...");
        game.setScreen(new FirstScreen(game));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        handleKeyboardInput();
        updateButtonStyles();

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (backgroundTexture != null) {
            batch.begin();
            batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.end();
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        batch.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}