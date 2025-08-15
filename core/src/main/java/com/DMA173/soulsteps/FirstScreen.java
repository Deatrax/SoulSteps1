package com.DMA173.soulsteps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
    private Texture playerTex;
    private Vector2 playerPos;
    private float speed = 250f;

    // --- Define your map layers ---
    private int[] backgroundLayers = new int[]{0}; // ground/roads
    private int[] foregroundLayers = new int[]{1, 2, 3, 4, 5, 6}; // decorations/buildings

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 0.5f; // zoom in

        // Load the map
        map = new TmxMapLoader().load("Tile_City.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        batch = new SpriteBatch();
        playerTex = new Texture("player.png");

        // Get map size in pixels
        float mapWidth = map.getProperties().get("width", Integer.class) 
                         * map.getProperties().get("tilewidth", Integer.class);
        float mapHeight = map.getProperties().get("height", Integer.class) 
                          * map.getProperties().get("tileheight", Integer.class);

        // Start player at center of map
        playerPos = new Vector2(mapWidth / 2f, mapHeight / 2f);

        // Position camera initially
        camera.position.set(playerPos.x, playerPos.y, 0);
        camera.update();
    }

    @Override
    public void render(float delta) {
        handleInput(delta);

        // Make camera follow player
        camera.position.set(playerPos.x, playerPos.y, 0);
        camera.update();

        // Clear screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.setView(camera);

        // Render background layers
        mapRenderer.render(backgroundLayers);

       

        // Render foreground layers
        mapRenderer.render(foregroundLayers);

         // Render player
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(playerTex, 
                   playerPos.x - playerTex.getWidth() / 2f, 
                   playerPos.y - playerTex.getHeight() / 2f); // Centered
        batch.end();
    }

    private void handleInput(float delta) {
        // Movement
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            playerPos.y += speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            playerPos.y -= speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            playerPos.x -= speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            playerPos.x += speed * delta;
        }

        // Zoom
        if (Gdx.input.isKeyPressed(Input.Keys.PLUS) || Gdx.input.isKeyPressed(Input.Keys.EQUALS)) {
            camera.zoom -= 0.01f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
            camera.zoom += 0.01f;
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
        playerTex.dispose();
        batch.dispose();
    }
}
