package com.DMA173.soulsteps;

import com.DMA173.soulsteps.Charecters.CharecterAssets;
import com.DMA173.soulsteps.Charecters.NPC;
import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.story.GameState;
import com.DMA173.soulsteps.ui.DialogueUI;
import com.DMA173.soulsteps.ui.InteractionUI;
import com.DMA173.soulsteps.ui.ObjectiveUI;
import com.DMA173.soulsteps.world.WorldManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;

public class FirstScreen implements Screen {
    private Game game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private CharecterAssets characterAssets;

    private Player elian;
    private GameState gameState;
    private WorldManager worldManager;
    private OrthogonalTiledMapRenderer mapRenderer;
    
    private DialogueUI dialogueUI;
    private ObjectiveUI objectiveUI;
    private InteractionUI interactionUI;

    private InputHandler inputHandler;

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
        
        elian = new Player(characterAssets, 0, 0);

        gameState = GameState.getInstance();
        dialogueUI = new DialogueUI();
        objectiveUI = new ObjectiveUI();
        interactionUI = new InteractionUI();

        worldManager = new WorldManager(elian, characterAssets);
        
        inputHandler = new InputHandler(camera, elian, worldManager, dialogueUI);
        Gdx.input.setInputProcessor(inputHandler);

        worldManager.loadZone("town_square");
        mapRenderer = new OrthogonalTiledMapRenderer(worldManager.getCurrentMap());

        dialogueUI.showNarration("Welcome to Aethelgard. Use WASD to move, E to interact.");
        gameState.completeObjective("tutorial_complete");
    }

    @Override
    public void render(float delta) {
        // --- LOGIC UPDATES ---
        
        // Always check for interaction prompts, regardless of pause state.
        // This is the fix for the missing prompt.
        String prompt = worldManager.checkProximityForPrompt();
        interactionUI.setPromptText(prompt);

        // Only update the game world if dialogue is not active (i.e., game is not paused)
        if (!dialogueUI.isActive()) {
            elian.update(delta);
            worldManager.update(delta); // This now only handles movement/zone transitions
        }
        
        updateCamera();

        // --- RENDERING ---
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (worldManager.justChangedZones()) {
            mapRenderer.setMap(worldManager.getCurrentMap());
        }
        mapRenderer.setView(camera);
        
        mapRenderer.render(new int[]{0});

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        elian.render(batch);
        for (NPC npc : worldManager.getCurrentNPCs()) {
            npc.render(batch);
        }
        batch.end();

        mapRenderer.render(new int[]{1, 2, 3, 4, 5, 6});

        // --- UI RENDERING (on top of everything else) ---
        batch.begin();
        dialogueUI.render(batch);
        objectiveUI.render(batch, gameState);
        interactionUI.render(batch);
        batch.end();
    }
    
    private void updateCamera() {
        camera.position.lerp(new Vector3(elian.getPosition().x, elian.getPosition().y, 0), 4.0f * Gdx.graphics.getDeltaTime());
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
        dialogueUI.dispose();
        objectiveUI.dispose();
        interactionUI.dispose();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}
}