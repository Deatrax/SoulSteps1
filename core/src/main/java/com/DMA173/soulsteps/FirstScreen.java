package com.DMA173.soulsteps;

import com.DMA173.soulsteps.Charecters.CharecterAssets;
import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.ui.UIManager;
import com.DMA173.soulsteps.world.WorldManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;

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
    
    private boolean isPaused = false;

    // We pass the Game instance in so we can switch back to the main menu
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

        uiManager = new UIManager(game); // Pass game instance to UIManager for its menus
        inputHandler = new InputHandler(this, camera, elian, uiManager, worldManager);
        
        Gdx.input.setInputProcessor(inputHandler);
    }
    
    @Override
    public void render(float delta) {
        // Pause state is now managed by the UIManager's menu visibility
        isPaused = uiManager.isAnyMenuActive();

        if (!isPaused) {
            elian.update(delta);
            worldManager.update(delta);
        }
        
        updateCamera();
        
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.setMap(worldManager.getCurrentMap());
        mapRenderer.setView(camera);
        
        mapRenderer.render(new int[]{0});

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        worldManager.getCurrentNpcManager().render(batch);
        elian.render(batch);
        batch.end();

        mapRenderer.render(new int[]{1, 2, 3, 4, 5, 6});
        
        // UIManager now handles rendering both the HUD and any active menus (like pause)
        uiManager.render(elian);
    }
    
    public void setPaused(boolean paused) {
        this.isPaused = paused;
        if (isPaused) {
            uiManager.showPauseMenu();
        } else {
            uiManager.hideAllMenus();
        }
    }
    
    private void updateCamera() {
        camera.position.lerp(new Vector3(elian.getPosition().x, elian.getPosition().y, 0), 8.0f * Gdx.graphics.getDeltaTime());
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
    }

    @Override public void pause() { setPaused(true); }
    @Override public void resume() {}
    @Override public void hide() { dispose(); } // IMPORTANT: Dispose the game screen when leaving it
}