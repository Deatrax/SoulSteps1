package com.DMA173.soulsteps;

import com.DMA173.soulsteps.Charecters.CharecterAssets;
import com.DMA173.soulsteps.Charecters.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * FirstScreen handles the main game rendering and update loop.
 * Input handling is delegated to InputHandler class.
 * Demonstrates separation of concerns in OOP design.
 */
public class FirstScreen extends ScreenAdapter {
    // Rendering components
    private OrthographicCamera camera;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private SpriteBatch batch;
    
    // Game objects
    private Player elian;
    private CharecterAssets characterAssets;
    
    // Input handling
    private InputHandler inputHandler;
    
    // Map layers
    private int[] backgroundLayers = new int[]{0}; // ground/roads
    private int[] foregroundLayers = new int[]{1, 2, 3, 4, 5, 6}; // decorations/buildings

    @Override
    public void show() {
        initializeCamera();
        initializeMap();
        initializeRendering();
        initializePlayer();
        initializeInput();
        
        System.out.println("SoulSteps - Game initialized successfully!");
        System.out.println("Controls: WASD/Arrow Keys - Move, E - Interact, I - Inventory, ESC - Menu");
        System.out.println("Camera: +/- - Zoom, F3 - Toggle Debug Mode");
    }
    
    private void initializeCamera() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 0.5f; // Start zoomed in for better visibility
    }
    
    private void initializeMap() {
        map = new TmxMapLoader().load("Tile_City.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);
    }
    
    private void initializeRendering() {
        batch = new SpriteBatch();
    }
    
    private void initializePlayer() {
        // Get map dimensions for player positioning
        float mapWidth = map.getProperties().get("width", Integer.class)
                * map.getProperties().get("tilewidth", Integer.class);
        float mapHeight = map.getProperties().get("height", Integer.class)
                * map.getProperties().get("tileheight", Integer.class);

        // Initialize character assets and create Elian
        characterAssets = new CharecterAssets();
        characterAssets.init();
        
        // Create Elian at the center of the map
        elian = new Player(characterAssets, mapWidth / 2f, mapHeight / 2f);

        // Position camera initially on player
        camera.position.set(elian.getPosition().x, elian.getPosition().y, 0);
        camera.update();
    }
    
    private void initializeInput() {
        // Create input handler with dependencies
        inputHandler = new InputHandler(camera, elian);
    }

    @Override
    public void render(float delta) {
        // Handle input first
        inputHandler.handleInput(delta);
        
        // Update game objects
        updateGame(delta);
        
        // Update camera
        updateCamera();
        
        // Render everything
        renderGame();
    }
    
    private void updateGame(float delta) {
        // Update Elian
        elian.update(delta);
        
        // TODO: Update other game objects (NPCs, animations, etc.)
    }
    
    private void updateCamera() {
        // Make camera follow Elian smoothly
        Vector2 elianPos = elian.getPosition();
        
        // Option 1: Smooth camera following with lerp (CORRECTED)
        float cameraSpeed = 8.0f;
        Vector3 targetPosition = new Vector3(elianPos.x, elianPos.y, 0);
        camera.position.lerp(targetPosition, cameraSpeed * Gdx.graphics.getDeltaTime());
        
        // Option 2: Direct camera following (simpler, no smoothing)
        // camera.position.set(elianPos.x, elianPos.y, 0);
        
        camera.update();
    }

    
    private void renderGame() {
        // Clear screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Set up map renderer
        mapRenderer.setView(camera);
        
        // Render background layers (ground, roads)
        mapRenderer.render(backgroundLayers);

        // Render characters
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        elian.render(batch);
        // TODO: Render NPCs and other characters here
        batch.end();

        // Render foreground layers (buildings, decorations)
        mapRenderer.render(foregroundLayers);
        
        // TODO: Render UI elements here (kindness bar, inventory, etc.)
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override
    public void dispose() {
        // Dispose of resources in reverse order of creation
        if (characterAssets != null) {
            characterAssets.dispose();
        }
        
        if (batch != null) {
            batch.dispose();
        }
        
        if (mapRenderer != null) {
            mapRenderer.dispose();
        }
        
        if (map != null) {
            map.dispose();
        }
        
        System.out.println("SoulSteps - Resources disposed successfully!");
    }
    
    // Getter methods for debugging or external access
    public Player getPlayer() {
        return elian;
    }
    
    public InputHandler getInputHandler() {
        return inputHandler;
    }
}
