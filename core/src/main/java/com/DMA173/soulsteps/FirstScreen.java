package com.DMA173.soulsteps;

import com.DMA173.soulsteps.Charecters.CharecterAssets;
import com.DMA173.soulsteps.Charecters.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

public class FirstScreen extends ScreenAdapter {
    private OrthographicCamera camera;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private SpriteBatch batch;
    
    // --- Define your map layers ---
    private int[] backgroundLayers = new int[]{0}; // ground/roads
    private int[] foregroundLayers = new int[]{1, 2, 3, 4, 5, 6}; // decorations/buildings
    
    // --- New Player System ---
    private Player elian;
    private CharecterAssets characterAssets;

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 0.5f; // zoom in

        // Load the map
        map = new TmxMapLoader().load("Tile_City.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        batch = new SpriteBatch();

        // Get map size in pixels
        float mapWidth = map.getProperties().get("width", Integer.class)
                * map.getProperties().get("tilewidth", Integer.class);
        float mapHeight = map.getProperties().get("height", Integer.class)
                * map.getProperties().get("tileheight", Integer.class);

        // Initialize Character Assets and Elian
        characterAssets = new CharecterAssets();
        characterAssets.init();
        
        // Create Elian at the center of the map
        elian = new Player(characterAssets, mapWidth / 2f, mapHeight / 2f);

        // Position camera initially
        camera.position.set(elian.getPosition().x, elian.getPosition().y, 0);
        camera.update();
    }

    @Override
    public void render(float delta) {
        handleInput(delta);
        
        // Update Elian
        elian.update(delta);
        
        // Camera follows Elian
        Vector2 elianPos = elian.getPosition();
        camera.position.set(elianPos.x, elianPos.y, 0);
        camera.update();

        // Clear screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.setView(camera);
        
        // Render background layers
        mapRenderer.render(backgroundLayers);

        // Render Elian
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        elian.render(batch);
        batch.end();

        // Render foreground layers
        mapRenderer.render(foregroundLayers);
    }

    private void handleInput(float delta) {
        // The Player class now handles its own movement input in its update() method
        // So we don't need to handle WASD here anymore
        
        // Zoom controls (keep these in the screen)
        if (Gdx.input.isKeyPressed(Input.Keys.PLUS) || Gdx.input.isKeyPressed(Input.Keys.EQUALS)) {
            camera.zoom -= 0.01f;
        }
        
        if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
            camera.zoom += 0.01f;
        }
        
        // Clamp zoom to reasonable values
        camera.zoom = Math.max(0.2f, Math.min(2.0f, camera.zoom));
        
        // Future game controls (these don't affect movement)
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            // Interaction key - will be used for talking to NPCs, examining objects
            System.out.println("Interaction key pressed - E");
            // TODO: Implement interaction system
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            // Inventory key
            System.out.println("Inventory key pressed - I");
            // TODO: Implement inventory system
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            // Pause/Menu key
            System.out.println("Escape pressed - pause menu");
            // TODO: Implement pause menu
        }
        
        // Debug keys (remove these in final version)
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            // Debug: Test kindness adjustment
            elian.adjustKindness(10);
            System.out.println("Kindness increased! Current: " + elian.getKindnessLevel());
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.J)) {
            // Debug: Test kindness decrease
            elian.adjustKindness(-10);
            System.out.println("Kindness decreased! Current: " + elian.getKindnessLevel());
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        batch.dispose();
        
        // Dispose character assets
        if (characterAssets != null) {
            characterAssets.dispose();
        }
    }
}
