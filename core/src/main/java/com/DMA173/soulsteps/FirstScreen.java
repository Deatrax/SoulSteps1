package com.DMA173.soulsteps;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class FirstScreen extends ScreenAdapter {

    private final Game game;   // reference to main Game

    private OrthographicCamera camera;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    private SpriteBatch batch;
    private Texture playerTex;
    private Vector2 playerPos;
    private float speed = 250f;

    // Collision
    private TiledMapTileLayer collisionLayer;
    private float tileWidth, tileHeight;

    // --- Define your map layers ---
    private int[] backgroundLayers = new int[]{0}; // ground/roads
    private int[] foregroundLayers = new int[]{1, 2, 3, 4, 5, 6}; //  for Tile_City.tmx
    //private int[] foregroundLayers = new int[]{1, 2}; // for interior.tmx, office.tmx
    

    public FirstScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 0.5f; // zoom in

        // Load the map
        map = new TmxMapLoader().load("Tile_City.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        // Get the collision layer
        collisionLayer = (TiledMapTileLayer) map.getLayers().get("Collision");
        tileWidth = collisionLayer.getTileWidth();
        tileHeight = collisionLayer.getTileHeight();

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
        checkTriggers();

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
        float nextX = playerPos.x;
        float nextY = playerPos.y;

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            nextY += speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            nextY -= speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            nextX -= speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            nextX += speed * delta;
        }

        // Only move if not blocked
        if (!isCellBlocked(nextX, nextY)) {
            playerPos.set(nextX, nextY);
        }

        // Zoom
        if (Gdx.input.isKeyPressed(Input.Keys.PLUS) || Gdx.input.isKeyPressed(Input.Keys.EQUALS)) {
            camera.zoom -= 0.01f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
            camera.zoom += 0.01f;
        }
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
    }

    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        playerTex.dispose();
        batch.dispose();
    }

    private void checkTriggers() {
        MapLayer triggerLayer = map.getLayers().get("Triggers");
        if (triggerLayer == null) return;

        for (MapObject obj : triggerLayer.getObjects()) {
            if (obj instanceof RectangleMapObject) {
                RectangleMapObject rectObj = (RectangleMapObject) obj;
                Rectangle rect = rectObj.getRectangle();

                // Simple auto-trigger when player center enters the rectangle
                if (rect.contains(playerPos.x, playerPos.y)) {
                    String type = obj.getProperties().get("type", String.class);
                    if ("pipeGame".equals(type)) {
                        boolean launchOnce = obj.getProperties().containsKey("launchOnce")
                                && (Boolean) obj.getProperties().get("launchOnce");
                        if (launchOnce) {
                            obj.getProperties().put("type", "used"); // disable retrigger
                        }

                        //game.setScreen(new PipeGameScreen(game)); // <-- fixed
                        return;
                    }
                }
            }
        }
    }
}


