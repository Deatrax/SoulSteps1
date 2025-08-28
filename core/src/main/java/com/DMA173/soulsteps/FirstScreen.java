package com.DMA173.soulsteps;

import com.DMA173.soulsteps.Charecters.CharecterAssets;
import com.DMA173.soulsteps.Charecters.NPCManager;
import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.UI.UIManager;
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
 * Now includes NPCs and UI management.
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
    private NPCManager npcManager;
    
    // UI Management
    private UIManager uiManager;
    
    // Input handling
    private InputHandler inputHandler;
    
    // Map layers
    private int[] backgroundLayers = new int[]{0}; // ground/roads
    private int[] foregroundLayers = new int[]{1, 2, 3, 4, 5, 6}; // decorations/buildings
    
    // Game state
    private boolean gameInitialized = false;

    @Override
    public void show() {
        initializeCamera();
        initializeMap();
        initializeRendering();
        initializePlayer();
        initializeNPCs();
        initializeUI();
        initializeInput();
        
        gameInitialized = true;
        
        System.out.println("SoulSteps - Game initialized successfully!");
        System.out.println("=== CONTROLS ===");
        System.out.println("Movement: WASD or Arrow Keys");
        System.out.println("Interact: E");
        System.out.println("Pause Menu: ESC");
        System.out.println("Debug Mode: F3");
        System.out.println("Inventory: I (placeholder)");
        System.out.println("Map: M (placeholder)");
        System.out.println("Camera: +/- for zoom, 0 to reset");
        System.out.println("================");
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
    
    private void initializeNPCs() {
        // Get map dimensions
        float mapWidth = map.getProperties().get("width", Integer.class)
                * map.getProperties().get("tilewidth", Integer.class);
        float mapHeight = map.getProperties().get("height", Integer.class)
                * map.getProperties().get("tileheight", Integer.class);

        // Create NPC manager and spawn NPCs
        npcManager = new NPCManager(characterAssets);
        npcManager.initializeNPCs(mapWidth, mapHeight, elian.getPosition());
    }
    
    private void initializeUI() {
        uiManager = new UIManager();
        uiManager.setObjective("Investigate the water supply system");
    }
    
    private void initializeInput() {
        // Create input handler with all dependencies
        inputHandler = new InputHandler(camera, elian, uiManager, npcManager);
    }

    @Override
    public void render(float delta) {
        // Handle input first
        inputHandler.handleInput(delta);
        
        // Only update game if not paused
        if (!inputHandler.isPaused()) {
            updateGame(delta);
            updateCamera();
        }
        
        // Always render (so we can see pause menu)
        renderGame();
        
        // Always render UI last (so it appears on top)
        uiManager.render(elian);
    }
    
    private void updateGame(float delta) {
        // Update Elian
        elian.update(delta);
        
        // Update NPCs
        npcManager.update(delta);
        
        // Update game events/story progression here
        updateGameEvents();
    }
    
    private void updateGameEvents() {
        // Example: Change objective based on evidence collected
        if (elian.getEvidenceCount() >= 3) {
            uiManager.setObjective("Find the source of the water contamination");
        } else if (elian.getEvidenceCount() >= 1) {
            uiManager.setObjective("Collect more evidence about the water supply");
        }
        
        // Example: Story events based on kindness level
        if (elian.isDangerZoneActive()) {
            // Maybe spawn hostile NPCs or change dialogue
        }
        
        // You can add more story logic here
    }
    
    private void updateCamera() {
        // Make camera follow Elian smoothly
        Vector2 elianPos = elian.getPosition();
        
        // Smooth camera following with lerp
        float cameraSpeed = 8.0f;
        Vector3 targetPosition = new Vector3(elianPos.x, elianPos.y, 0);
        camera.position.lerp(targetPosition, cameraSpeed * Gdx.graphics.getDeltaTime());
        
        // Optional: Add camera bounds to prevent showing outside map
        // You can uncomment and adjust these if needed:
        /*
        float mapWidth = map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class);
        float mapHeight = map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class);
        
        float halfViewWidth = camera.viewportWidth * camera.zoom / 2f;
        float halfViewHeight = camera.viewportHeight * camera.zoom / 2f;
        
        camera.position.x = Math.max(halfViewWidth, Math.min(mapWidth - halfViewWidth, camera.position.x));
        camera.position.y = Math.max(halfViewHeight, Math.min(mapHeight - halfViewHeight, camera.position.y));
        */
        
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

        // Render characters (player and NPCs)
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        // Render NPCs first (so player appears on top if overlapping)
        npcManager.render(batch);
        
        // Render player
        elian.render(batch);
        
        batch.end();

        // Render foreground layers (buildings, decorations)
        mapRenderer.render(foregroundLayers);
    }

    @Override
    public void resize(int width, int height) {
        // Update game camera
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
        
        // Update UI camera
        uiManager.resize(width, height);
    }

    @Override
    public void dispose() {
        // Dispose of resources in reverse order of creation
        if (inputHandler != null) {
            // InputHandler doesn't have resources to dispose
        }
        
        if (uiManager != null) {
            uiManager.dispose();
        }
        
        if (npcManager != null) {
            // NPCManager doesn't currently have resources to dispose
        }
        
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
    
    // --- Public methods for external access ---
    public Player getPlayer() {
        return elian;
    }
    
    public NPCManager getNPCManager() {
        return npcManager;
    }
    
    public UIManager getUIManager() {
        return uiManager;
    }
    
    public InputHandler getInputHandler() {
        return inputHandler;
    }
    
    // --- Game state methods ---
    public void changeObjective(String newObjective) {
        if (uiManager != null) {
            uiManager.setObjective(newObjective);
        }
    }
    
    public void showNotification(String message) {
        if (uiManager != null) {
            uiManager.showNotification(message);
        }
    }
    
    public boolean isGameReady() {
        return gameInitialized;
    }
}