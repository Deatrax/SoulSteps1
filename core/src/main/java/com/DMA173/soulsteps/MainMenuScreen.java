package com.DMA173.soulsteps;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {

    private Game game;
    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;

    // --- FUTURE ART ASSETS ---
    // These are placeholders. You can replace these file paths later.
    private Texture backgroundTexture;
    
    public MainMenuScreen(Game game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        this.batch = new SpriteBatch();

        // --- BACKGROUND IMPLEMENTATION ---
        // This is where you will load your background image.
        // For now, it's commented out. When you have a file like 'assets/ui/main_menu_bg.png', uncomment this.
        // try {
        //     backgroundTexture = new Texture(Gdx.files.internal("ui/main_menu_bg.png"));
        // } catch (Exception e) {
        //     System.out.println("No background texture found. Using solid color.");
        //     backgroundTexture = null;
        // }
        
        createSkin();

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        TextButton newGameButton = new TextButton("New Game", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        table.add(newGameButton).fillX().uniformX().width(300).height(50).pad(10);
        table.row();
        table.add(exitButton).fillX().uniformX().width(300).height(50).pad(10);

        newGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new FirstScreen(game)); // Pass the game instance
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }

    private void createSkin() {
        skin = new Skin();
        BitmapFont font = new BitmapFont();
        skin.add("default", font);

        // --- BUTTON STYLE IMPLEMENTATION ---
        // This creates a basic, functional button style programmatically.
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = skin.getFont("default");
        textButtonStyle.fontColor = Color.WHITE;
        
        // FUTURE: This is where you will use your custom button PNGs.
        // To use custom images, you'll create a TextureAtlas and load a JSON skin file.
        // For now, we will create simple colored rectangles for the button states.
        skin.add("default", textButtonStyle);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.15f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the background image if it exists
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
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}