package com.DMA173.soulsteps;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
    private Skin skin; // The "stylesheet" for our UI

    public MainMenuScreen(Game game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());

        // Create a simple skin programmatically (without loading files)
        createBasicSkin();

        // Create a table to hold our buttons
        Table table = new Table();
        table.setFillParent(true); // Make the table fill the whole screen
        stage.addActor(table);
        
        // --- Create Buttons ---
        TextButton newGameButton = new TextButton("New Game", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        // --- Add Buttons to the Table ---
        table.add(newGameButton).fillX().uniformX().pad(10);
        table.row(); // Go to the next row
        table.add(exitButton).fillX().uniformX().pad(10);

        // --- Add Logic to Buttons (Listeners) ---
        newGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("New Game button clicked!");
                // Switch to the main game screen
                game.setScreen(new FirstScreen(game)); 
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Exit button clicked!");
                Gdx.app.exit(); // Close the application
            }
        });
    }
    
    // This method creates a basic skin for our buttons without needing any PNG files
    private void createBasicSkin() {
        skin = new Skin();
        BitmapFont font = new BitmapFont();
        skin.add("default", font);

        // For a more advanced skin, you would load a TextureAtlas with your button images
        // For now, we'll create a simple colored button style
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = skin.getFont("default");
        
        // You would replace these with Drawable images from your assets
        skin.add("default", textButtonStyle);
    }

    @Override
    public void show() {
        // IMPORTANT: You must set the input processor to the stage for buttons to work
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Tell the stage to act and draw itself
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}