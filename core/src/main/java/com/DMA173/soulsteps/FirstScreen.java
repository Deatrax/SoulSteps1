package com.DMA173.soulsteps;

import com.DMA173.soulsteps.Charecters.CharecterAssets;
import com.DMA173.soulsteps.Charecters.NPC;
import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.story.GameState;
import com.DMA173.soulsteps.ui.DialogueUI;
import com.DMA173.soulsteps.ui.ObjectiveUI;
import com.DMA173.soulsteps.world.WorldManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;

public class FirstScreen extends ScreenAdapter {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private CharecterAssets characterAssets;

    // Game Objects
    private Player elian;

    // New Story and World Systems
    private GameState gameState;
    private WorldManager worldManager;
    private OrthogonalTiledMapRenderer mapRenderer;
    
    // UI Components
    private DialogueUI dialogueUI;
    private ObjectiveUI objectiveUI;

    // Input Handling
    private InputHandler inputHandler;

    @Override
    public void show() {
        // --- Standard Initialization ---
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 0.5f;
        batch = new SpriteBatch();
        
        characterAssets = new CharecterAssets();
        characterAssets.init();
        
        elian = new Player(characterAssets, 0, 0); // Initial position doesn't matter, worldManager will set it

        // --- New System Initialization ---
        gameState = GameState.getInstance();
        dialogueUI = new DialogueUI(); // Create UI
        objectiveUI = new ObjectiveUI();

        // WorldManager needs the player, assets, and UI to function
        worldManager = new WorldManager(elian, characterAssets, dialogueUI);
        
        // Input handler needs access to the managers
        inputHandler = new InputHandler(camera, elian, worldManager, dialogueUI);
        Gdx.input.setInputProcessor(inputHandler); // Set input processor

        // Load the starting zone, which provides the first map
        worldManager.loadZone("town_square");
        mapRenderer = new OrthogonalTiledMapRenderer(worldManager.getCurrentMap());

        // Show intro dialogue
        dialogueUI.showNarration("Welcome to Aethelgard. Use WASD to move, E to interact.");
        gameState.completeObjective("tutorial_complete");
    }

    @Override
    public void render(float delta) {
        // --- Input and Logic Updates ---
        // Input is now handled by the InputProcessor, no need to call a handle method here
        // if dialogue is active, don't update the game world
        if (!dialogueUI.isActive()) {
            elian.update(delta);
            worldManager.update(delta);
        }
        
        updateCamera();

        // --- Rendering ---
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // If the map has changed, update the renderer
        if (worldManager.justChangedZones()) {
            mapRenderer.setMap(worldManager.getCurrentMap());
        }
        mapRenderer.setView(camera);
        
        // Render map background layers
        mapRenderer.render(new int[]{0}); // Adjust layer indices as needed

        // Render game objects
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        elian.render(batch);
        for (NPC npc : worldManager.getCurrentNPCs()) {
            npc.render(batch);
        }
        batch.end();

        // Render map foreground layers
        mapRenderer.render(new int[]{1, 2, 3, 4, 5, 6});

        // Render UI on top of everything
        batch.begin();
        dialogueUI.render(batch);
        objectiveUI.render(batch, gameState);
        batch.end();
    }
    
    private void updateCamera() {
        float cameraSpeed = 4.0f;
        Vector3 targetPosition = new Vector3(elian.getPosition().x, elian.getPosition().y, 0);
        camera.position.lerp(targetPosition, cameraSpeed * Gdx.graphics.getDeltaTime());
        camera.update();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        characterAssets.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
        // worldManager disposes maps as it loads new ones
        dialogueUI.dispose();
        objectiveUI.dispose();
    }
}