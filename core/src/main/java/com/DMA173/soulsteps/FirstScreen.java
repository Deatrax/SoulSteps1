package com.DMA173.soulsteps;

import com.DMA173.soulsteps.Charecters.CharecterAssets;
import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.story.StoryProgressionManager;
import com.DMA173.soulsteps.ui.UIManager;
import com.DMA173.soulsteps.world.WorldManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
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
    
    private WorldManager worldManager;
    private StoryProgressionManager storyManager;
    
    // --- MERGED: Features from teammate's code ---
    private TiledMapTileLayer collisionLayer;
    private float tileWidth, tileHeight;
    
    private float camZoom = 0.3f;
    
    public FirstScreen(Game game) {
        this.game = game;
    }
    
    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = camZoom;

        batch = new SpriteBatch();
        characterAssets = new CharecterAssets();
        characterAssets.init();
        
        worldManager = new WorldManager(characterAssets);
        
        System.err.println("Starting new game.....");
        worldManager.loadZone("Tile_City");
        
        // The map renderer now gets its map from the world manager
        mapRenderer = new OrthogonalTiledMapRenderer(worldManager.getCurrentMap());

        // MERGED: Initialize collision layer and tile properties after the map is loaded
        System.out.println("entering collision init...");
        initializeCollision();

        float mapWidth = worldManager.getCurrentMap().getProperties().get("width", Integer.class) * tileWidth;
        float mapHeight = worldManager.getCurrentMap().getProperties().get("height", Integer.class) * tileHeight;
        elian = new Player(characterAssets, 330, 130); //story start point
        elian.setCurrentMapName(worldManager.getCurrentZoneName());
        elian.setCollisionLayer(collisionLayer); // MERGED: Give the player access to the collision layer

        uiManager = new UIManager(game);
        inputHandler = new InputHandler(camera, elian, uiManager, worldManager);
        
        storyManager = new StoryProgressionManager(uiManager, worldManager);
        inputHandler.setStoryManager(storyManager);
        
        System.out.println("SoulSteps - Game initialized successfully with MERGED collision and story systems!");
    }
    
    // MERGED: New method to handle collision layer setup
    private void initializeCollision() {
        System.out.println("....initializing collision.....");
        collisionLayer = (TiledMapTileLayer) worldManager.getCurrentMap().getLayers().get("Collision");
        if (collisionLayer != null) {
            tileWidth = collisionLayer.getTileWidth();
            tileHeight = collisionLayer.getTileHeight();
            System.out.println("Collision layer loaded successfully.");
        } else {
            System.out.println("WARNING: 'Collision' layer not found in map. Collision will not work.");
            // Set default tile sizes to prevent crashes
            tileWidth = 32;
            tileHeight = 32;
        }
    }

    @Override
    public void render(float delta) {
        // --- Update Logic (no changes here) ---
        uiManager.update(delta);
        inputHandler.handleInput(delta);
        
        if (!inputHandler.isPaused()) {
            elian.update(delta);
            worldManager.update(delta);
            storyManager.update(delta, elian);
            updateCamera();
            checkTriggers();
        }
        
        // --- Clear Screen (no changes here) ---
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // --- Map Change Logic (no changes here) ---
        if (worldManager.hasMapChanged()) {
            mapRenderer.setMap(worldManager.getCurrentMap());
            initializeCollision();
            elian.setCollisionLayer(collisionLayer);
            worldManager.confirmMapChange();
        }
        
        mapRenderer.setView(camera);
        
        // --- NEW: DYNAMIC LAYER RENDERING ---

        int characterLayerIndex = -1;
        // Find the index of the layer named by getCharacterLayerName()
        if (worldManager.getCurrentMap().getLayers().get(worldManager.getCharacterLayerName()) != null) {
            characterLayerIndex = worldManager.getCurrentMap().getLayers().getIndex(worldManager.getCharacterLayerName());
        }

        if (characterLayerIndex == -1) {
            // FALLBACK: If the character layer is not found, render all layers as background.
            System.err.println("Warning: Map '" + worldManager.getCurrentZoneName() + "' is missing the '" + worldManager.getCharacterLayerName() + "' layer. Player will be drawn on top of everything.");
            mapRenderer.render(); // Render all layers
        } else {
            // DYNAMICALLY RENDER BACKGROUND LAYERS
            // This renders all layers from the bottom up to (but not including) the character layer.
            for (int i = 0; i < characterLayerIndex; i++) {
                mapRenderer.render(new int[]{i});
            }
        }

        // --- RENDER CHARACTERS (at the correct depth) ---
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        worldManager.getCurrentNpcManager().render(batch);
        elian.render(batch);
        batch.end();

        if (characterLayerIndex != -1) {
            // DYNAMICALLY RENDER FOREGROUND LAYERS
            // This renders the character layer itself, plus all layers above it.
            for (int i = characterLayerIndex; i < worldManager.getCurrentMap().getLayers().getCount(); i++) {
                // We also make sure the layer is visible before rendering it
                if (worldManager.getCurrentMap().getLayers().get(i).isVisible()) {
                    mapRenderer.render(new int[]{i});
                }
            }
        }

        // --- RENDER UI (on top of everything) ---
        uiManager.render(elian, camera, storyManager);
    }
    
    // MERGED: Trigger checking logic from your teammate
    private void checkTriggers() {
        // MapLayer triggerLayer = worldManager.getCurrentMap().getLayers().get("Triggers");
        // if (triggerLayer == null) return;

        // for (MapObject obj : triggerLayer.getObjects()) {
        //     if (obj instanceof RectangleMapObject) {
        //         Rectangle triggerRect = ((RectangleMapObject) obj).getRectangle();

        //         if (triggerRect.contains(elian.getPosition())) {
        //             String type = obj.getProperties().get("type", String.class);
        //             if ("pipeGame".equals(type)) {
        //                 System.out.println("Pipe game trigger activated!");
        //                 // TODO: Implement the pipe game screen transition
        //                 // game.setScreen(new PipeGameScreen(game, this)); // Example
        //             }
        //             // Add other trigger types here as needed
        //         }
        //     }
        // }
    }
    
    private void updateCamera() {
        camera.zoom = updateCamZoom();
        camera.position.lerp(new Vector3(elian.getPosition().x, elian.getPosition().y, 0), 8.0f * Gdx.graphics.getDeltaTime());
        camera.update();
    }

    private float updateCamZoom() {
        switch (worldManager.getCurrentZoneName()) {
            case "interior":
            case "interior2":
                camZoom = 1f;
                break;
            case "Tile_City":
                camZoom = 0.3f;
                break;
            default:
                return camZoom;
        }
        return camZoom;
    }

    private boolean isCellBlocked(float x, float y) {
        int cellX = (int) (x / tileWidth);
        int cellY = (int) (y / tileHeight);

        TiledMapTileLayer.Cell cell = collisionLayer.getCell(cellX, cellY);

        if (cell == null || cell.getTile() == null) {
            return false; // no tile → walkable
        }

        // Check if the tile has a "blocked" property in Tiled
        Object blocked = cell.getTile().getProperties().get("blocked");
        return blocked != null && blocked.equals(true);
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