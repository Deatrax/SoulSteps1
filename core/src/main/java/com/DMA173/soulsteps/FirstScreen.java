package com.DMA173.soulsteps;

import com.DMA173.soulsteps.Charecters.CharecterAssets;
import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.ui.UIManager;
import com.DMA173.soulsteps.world.WorldManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;

/**
 * Updated FirstScreen that properly integrates with the new menu system.
 * Now passes the Game reference for proper screen transitions.
 */
public class FirstScreen extends ScreenAdapter {
    private Game game;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer mapRenderer;
    private SpriteBatch batch;
    
    private Player elian;
    private CharecterAssets characterAssets;
    private UIManager uiManager;
    private InputHandler inputHandler;
    
    // --- NEW MANAGER SYSTEM ---
    private WorldManager worldManager;
    
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
        
        // --- INITIALIZE NEW SYSTEM ---
        worldManager = new WorldManager(characterAssets);
        worldManager.loadZone("town_square"); // This loads the map and NPCs
        
        // The map renderer now gets its map from the world manager
        mapRenderer = new OrthogonalTiledMapRenderer(worldManager.getCurrentMap());

        // Player is created after map is loaded to get dimensions
        float mapWidth = worldManager.getCurrentMap().getProperties().get("width", Integer.class) * 32; // Assuming 32x32 tiles
        float mapHeight = worldManager.getCurrentMap().getProperties().get("height", Integer.class) * 32;
        elian = new Player(characterAssets, mapWidth / 2f, mapHeight / 2f);

        uiManager = new UIManager(game); // Pass game reference for menu transitions
        inputHandler = new InputHandler(camera, elian, uiManager, worldManager);
        
        System.out.println("SoulSteps - Game initialized successfully with updated menu system!");
    }

    @Override
    public void render(float delta) {
        // Update UI system first (including menus)
        uiManager.update(delta);
        
        // Handle input
        inputHandler.handleInput(delta);
        
        // Only update game logic if not paused by menus
        if (!inputHandler.isPaused()) {
            elian.update(delta);
            worldManager.update(delta);
            updateCamera();
        }
        
        // Clear screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Always render game world (even when paused for visual feedback)
        mapRenderer.setMap(worldManager.getCurrentMap());
        mapRenderer.setView(camera);
        
        // Render background
        mapRenderer.render(new int[]{0});

        // Render characters
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        worldManager.getCurrentNpcManager().render(batch);
        elian.render(batch);
        batch.end();

        // Render foreground
        mapRenderer.render(new int[]{1, 2, 3, 4, 5, 6});
        
        // Render UI and menus on top
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