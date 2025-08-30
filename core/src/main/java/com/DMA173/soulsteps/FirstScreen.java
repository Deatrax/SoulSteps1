package com.DMA173.soulsteps;

import com.DMA173.soulsteps.Charecters.CharecterAssets;
import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.ui.UIManager;
import com.DMA173.soulsteps.world.WorldManager; // <-- ADD IMPORT
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;

public class FirstScreen extends ScreenAdapter {
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer mapRenderer;
    private SpriteBatch batch;
    
    private Player elian;
    private CharecterAssets characterAssets;
    private UIManager uiManager;
    private InputHandler inputHandler;
    
    // --- NEW MANAGER SYSTEM ---
    private WorldManager worldManager;
    
    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 0.5f;

        batch = new SpriteBatch();
        characterAssets = new CharecterAssets();
        characterAssets.init();
        
        // --- INITIALIZE NEW SYSTEM ---
        worldManager = new WorldManager(characterAssets);
        worldManager.loadZone("town_square"); // This loads the map and NPCs
        
        // The map renderer now gets its map from the world manager
        mapRenderer = new OrthogonalTiledMapRenderer(worldManager.getCurrentMap());

        // Player is created after map is loaded to get dimensions
        float mapWidth = worldManager.getCurrentMap().getProperties().get("width", Integer.class) * 32; // Assuming 32x32 tiles
        float mapHeight = worldManager.getCurrentMap().getProperties().get("height", Integer.class) * 32;
        elian = new Player(characterAssets, mapWidth / 2f, mapHeight / 2f);

        uiManager = new UIManager();
        inputHandler = new InputHandler(camera, elian, uiManager, worldManager); // Pass worldManager
        
        System.out.println("SoulSteps - Game initialized successfully with new manager system!");
    }

    @Override
    public void render(float delta) {
        inputHandler.handleInput(delta);
        
        if (!inputHandler.isPaused()) {
            elian.update(delta);
            worldManager.update(delta); // Update the world (NPCs inside are updated)
            updateCamera();
        }
        
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Always make sure the map renderer has the latest map from the world manager
        mapRenderer.setMap(worldManager.getCurrentMap());
        mapRenderer.setView(camera);
        
        // Render background
        mapRenderer.render(new int[]{0});

        // Render characters
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        worldManager.getCurrentNpcManager().render(batch); // Render NPCs from the current zone
        elian.render(batch);
        batch.end();

        // Render foreground
        mapRenderer.render(new int[]{1, 2, 3, 4, 5, 6});
        
        // Render UI on top
        uiManager.render(elian);
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
}