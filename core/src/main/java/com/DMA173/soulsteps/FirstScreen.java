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
        worldManager.loadZone("Tile_City");

        mapRenderer = new OrthogonalTiledMapRenderer(worldManager.getCurrentMap());

        initializeCollision();

        elian = new Player(characterAssets, 330, 130);
        elian.setCurrentMapName(worldManager.getCurrentZoneName());
        elian.setCollisionLayer(collisionLayer);

        uiManager = new UIManager(game);
        inputHandler = new InputHandler(camera, elian, uiManager, worldManager);

        storyManager = new StoryProgressionManager(uiManager, worldManager);
        inputHandler.setStoryManager(storyManager);
    }

    private void initializeCollision() {
        collisionLayer = (TiledMapTileLayer) worldManager.getCurrentMap().getLayers().get("Collision");
        if (collisionLayer != null) {
            tileWidth = collisionLayer.getTileWidth();
            tileHeight = collisionLayer.getTileHeight();
        } else {
            tileWidth = 32;
            tileHeight = 32;
        }
    }

    @Override
    public void render(float delta) {
        uiManager.update(delta);
        inputHandler.handleInput(delta);

        if (!inputHandler.isPaused()) {
            elian.update(delta);
            worldManager.update(delta);
            storyManager.update(delta, elian);
            updateCamera();
            checkTriggers();
        }

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (worldManager.hasMapChanged()) {
            mapRenderer.setMap(worldManager.getCurrentMap());
            initializeCollision();
            elian.setCollisionLayer(collisionLayer);
            worldManager.confirmMapChange();
        }

        mapRenderer.setView(camera);

        int characterLayerIndex = -1;
        if (worldManager.getCurrentMap().getLayers().get(worldManager.getCharacterLayerName()) != null) {
            characterLayerIndex = worldManager.getCurrentMap().getLayers().getIndex(worldManager.getCharacterLayerName());
        }

        if (characterLayerIndex == -1) {
            mapRenderer.render();
        } else {
            for (int i = 0; i < characterLayerIndex; i++) {
                mapRenderer.render(new int[]{i});
            }
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        worldManager.getCurrentNpcManager().render(batch);
        elian.render(batch);
        batch.end();

        if (characterLayerIndex != -1) {
            for (int i = characterLayerIndex; i < worldManager.getCurrentMap().getLayers().getCount(); i++) {
                if (worldManager.getCurrentMap().getLayers().get(i).isVisible()) {
                    mapRenderer.render(new int[]{i});
                }
            }
        }

        uiManager.render(elian, camera, storyManager);
    }

    private void checkTriggers() {
        // MapLayer triggerLayer = worldManager.getCurrentMap().getLayers().get("Triggers");
        // if (triggerLayer == null) return;

        // for (MapObject obj : triggerLayer.getObjects()) {
        //     if (obj instanceof RectangleMapObject) {
        //         Rectangle triggerRect = ((RectangleMapObject) obj).getRectangle();

                if (triggerRect.contains(elian.getPosition())) {
                    String type = obj.getProperties().get("type", String.class);

                    if ("pipeGame".equals(type)) {
                        uiManager.showChoice(
                            "System",
                            "The pipe looks broken. Wanna fix it?",
                            new String[]{"Yes", "No"},
                            choice -> {
                                if (choice == 1) {
                                    game.setScreen(new pipepuzzle(game, worldManager.getCurrentZoneName(), this));
                                } else {
                                    uiManager.showNotification("You decided not to fix the pipe.");
                                }
                                uiManager.hideDialogue();
                                elian.getPosition().y -= 20;
                            }
                        );
                    }

                    
                }
            }
        }
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
        if (cell == null || cell.getTile() == null) return false;
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
