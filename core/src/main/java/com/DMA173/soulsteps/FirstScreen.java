package com.DMA173.soulsteps;

import com.DMA173.soulsteps.Charecters.CharecterAssets;
import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.ui.UIManager;
import com.DMA173.soulsteps.world.WorldManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class FirstScreen implements Screen {
    private Game game;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer mapRenderer;
    private SpriteBatch batch;

    private Player elian;
    private CharecterAssets characterAssets;
    private UIManager uiManager;
    private InputHandler inputHandler;
    private WorldManager worldManager;

    // --- PAUSE MENU SYSTEM ---
    private Stage pauseStage;
    private Skin pauseSkin;
    private boolean isPaused = false;

    public FirstScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 0.5f;

        batch = new SpriteBatch();
        characterAssets = new CharecterAssets();
        characterAssets.init();

        worldManager = new WorldManager(characterAssets);
        worldManager.loadZone("town_square");

        mapRenderer = new OrthogonalTiledMapRenderer(worldManager.getCurrentMap());

        float mapWidth = worldManager.getCurrentMap().getProperties().get("width", Integer.class) * 32;
        float mapHeight = worldManager.getCurrentMap().getProperties().get("height", Integer.class) * 32;
        elian = new Player(characterAssets, mapWidth / 2f, mapHeight / 2f);

        uiManager = new UIManager();
        inputHandler = new InputHandler(camera, elian, uiManager, worldManager, this); // Pass `this` screen

        createPauseMenu(); // Create the pause menu stage

        // By default, the input processor is the game input handler
        Gdx.input.setInputProcessor(inputHandler);
    }

    private void createPauseMenu() {
        pauseStage = new Stage(new ScreenViewport());
        pauseSkin = new Skin(); // Create a simple skin for the pause menu
        pauseSkin.add("default", new BitmapFont());

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = pauseSkin.getFont("default");
        style.fontColor = Color.WHITE;
        pauseSkin.add("default", style);

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        pauseStage.addActor(table);

        TextButton resumeButton = new TextButton("Resume", pauseSkin);
        TextButton mainMenuButton = new TextButton("Main Menu", pauseSkin);

        table.add(resumeButton).fillX().width(250).height(50).pad(10);
        table.row();
        table.add(mainMenuButton).fillX().width(250).height(50).pad(10);

        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setPaused(false);
            }
        });

        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
    }

    @Override
    public void render(float delta) {
        // --- LOGIC UPDATE ---
        // Only update the game world if it's not paused
        if (!isPaused) {
            inputHandler.handleInput(delta);
            elian.update(delta);
            worldManager.update(delta);
        }
        updateCamera();

        // --- RENDERING ---
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.setMap(worldManager.getCurrentMap());
        mapRenderer.setView(camera);

        mapRenderer.render(new int[] { 0 });

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        worldManager.getCurrentNpcManager().render(batch);
        elian.render(batch);
        batch.end();

        mapRenderer.render(new int[] { 1, 2, 3, 4, 5, 6 });

        // The UIManager is separate from the pause menu's Scene2D stage
        // so we render it separately.
        uiManager.render(elian);

        // If the game is paused, draw the pause menu stage on top of everything
        if (isPaused) {
            pauseStage.act(delta);
            pauseStage.draw();
        }
    }

    // This public method allows the InputHandler to pause the game.
    public void setPaused(boolean paused) {
        this.isPaused = paused;
        if (paused) {
            // When pausing, switch input to the pause menu
            Gdx.input.setInputProcessor(pauseStage);
        } else {
            // When resuming, switch input back to the game
            Gdx.input.setInputProcessor(inputHandler);
        }
    }

    private void updateCamera() {
        camera.position.lerp(new Vector3(elian.getPosition().x, elian.getPosition().y, 0),
                8.0f * Gdx.graphics.getDeltaTime());
        camera.update();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
        uiManager.resize(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        characterAssets.dispose();
        worldManager.dispose();
        uiManager.dispose();
        pauseStage.dispose();
        pauseSkin.dispose();
    }

    @Override
    public void pause() {
        isPaused = true;
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }
}