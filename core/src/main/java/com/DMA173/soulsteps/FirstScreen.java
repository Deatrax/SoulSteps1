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
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer; // ** NOW: The correct renderer for top-down maps **
import com.badlogic.gdx.math.Vector2;

public class FirstScreen extends ScreenAdapter {

    private OrthographicCamera camera;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer; // ** WAS: IsometricTiledMapRenderer **

    private SpriteBatch batch;
    private Texture playerTex;
    private Vector2 playerPos;
    private float speed = 250f;

    // --- Define layers based on your new map ---
    // Layer indices start from 0 at the bottom in Tiled.
    private int[] backgroundLayers = new int[]{0}; // Layer 0: "road"
    private int[] foregroundLayers = new int[]{1, 2}; // Layer 1: "cars", Layer 2: "door"

    @Override
public void show() {
    camera = new OrthographicCamera();
    camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    // --- Zoom in more (lower = closer) ---
    camera.zoom = 0.5f; // try 0.5 or even 0.4 for a closer view

    // Load map
    map = new TmxMapLoader().load("modernCityMap1.tmx");
    mapRenderer = new OrthogonalTiledMapRenderer(map);

    batch = new SpriteBatch();
    playerTex = new Texture("player.png"); 

    // Center player in middle of map instead of fixed 400,400
    float mapWidth = (float) map.getProperties().get("width", Integer.class) 
                     * (float) map.getProperties().get("tilewidth", Integer.class);
    float mapHeight = (float) map.getProperties().get("height", Integer.class) 
                      * (float) map.getProperties().get("tileheight", Integer.class);

    playerPos = new Vector2(mapWidth / 2f, mapHeight / 2f);

    camera.position.set(playerPos.x, playerPos.y, 0);
    camera.update();
}

    @Override
    public void render(float delta) {
        handleInput(delta);

        camera.position.set(playerPos.x, playerPos.y, 0);
        camera.update();

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1); // A dark grey background
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.setView(camera);

        // 1. Render the background layer(s)
        mapRenderer.render(backgroundLayers);

        // 2. Render the player
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(playerTex, playerPos.x - playerTex.getWidth() / 2f, playerPos.y); // Draw player with feet at playerPos.y
        batch.end();

        // 3. Render the foreground layer(s) over the player
        mapRenderer.render(foregroundLayers);
    }

    private void handleInput(float delta) {
        // --- Simple movement for a top-down map ---
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
        if (Gdx.input.isKeyPressed(Input.Keys.PLUS) || Gdx.input.isKeyPressed(Input.Keys.EQUALS)) {
            camera.zoom -= 0.01f; // zoom in
        }
        if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
            camera.zoom += 0.01f; // zoom out
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