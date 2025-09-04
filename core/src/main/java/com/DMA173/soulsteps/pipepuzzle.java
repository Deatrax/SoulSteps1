package com.DMA173.soulsteps;

import com.DMA173.soulsteps.world.WorldManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

public class pipepuzzle extends ScreenAdapter implements InputProcessor {

    private enum ToolType { BRUSH, WRENCH }
    private ToolType currentTool;

    private WorldManager worldManager;

    private SpriteBatch batch;
    private Texture cleanPipeTexture, dirtSpotTexture, brushTexture;
    private Texture crackTexture, wrenchTexture;
    private Texture brushIconTexture, wrenchIconTexture, selectionHighlightTexture;
    private Texture toolBgTexture;
    private Texture winPipeTexture;

    private ArrayList<DirtSpot> dirtSpots;
    private ArrayList<Crack> cracks;

    private OrthographicCamera camera;
    private Viewport viewport;
    private Vector3 worldCoordinates = new Vector3();

    private Rectangle brushSelectionBounds;
    private Rectangle wrenchSelectionBounds;
    private Rectangle toolBgBounds;

    private BitmapFont font;
    private int clearedCount = 0;
    private boolean allFixed = false;
    private int totalProblems = 0;

    private static final float WORLD_WIDTH = 1280;
    private static final float WORLD_HEIGHT = 720;

    private Game game;
    private String previousMapName;
    private ScreenAdapter previousScreen;

    class DirtSpot {
        Vector2 position;
        Rectangle bounds;
        float cleanProgress = 0f;
        boolean counted = false;

        DirtSpot(float x, float y, Texture texture) {
            this.position = new Vector2(x, y);
            this.bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
        }

        void clean() {
            if (isClean()) return;
            cleanProgress += Gdx.graphics.getDeltaTime() * 1.5f;
            if (cleanProgress >= 1f && !counted) {
                clearedCount++;
                counted = true;
                cleanProgress = 1f;
            }
        }

        boolean isClean() { return cleanProgress >= 1f; }
    }

    class Crack {
        Vector2 position;
        Rectangle bounds;
        float repairProgress = 0f;
        boolean counted = false;

        Crack(float x, float y, Texture texture) {
            this.position = new Vector2(x, y);
            this.bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
        }

        void repair() {
            if (isFixed()) return;
            repairProgress += Gdx.graphics.getDeltaTime() * 1.2f;
            if (repairProgress >= 1f && !counted) {
                clearedCount++;
                counted = true;
                repairProgress = 1f;
            }
        }

        boolean isFixed() { return repairProgress >= 1f; }
    }

    public pipepuzzle(Game game, String previousMapName, ScreenAdapter previousScreen) {
        this.game = game;
        this.previousMapName = previousMapName;
        this.previousScreen = previousScreen;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        batch = new SpriteBatch();

        currentTool = ToolType.BRUSH;

        cleanPipeTexture = new Texture("clean_pipe_layout.jpg");
        dirtSpotTexture = new Texture("dirt_spot.png");
        brushTexture = new Texture("brush.png");
        crackTexture = new Texture("crackk.png");
        wrenchTexture = new Texture("wrench.png");
        brushIconTexture = new Texture("brush_icon.jpg");
        wrenchIconTexture = new Texture("wrench_icon.png");
        selectionHighlightTexture = new Texture("selection_highlight.png");
        toolBgTexture = new Texture("tool-bg.png");
        winPipeTexture = new Texture("pipe.png");

        dirtSpots = new ArrayList<>();
        cracks = new ArrayList<>();

        // Example positions
        dirtSpots.add(new DirtSpot(157, 550, dirtSpotTexture));
        dirtSpots.add(new DirtSpot(350, 380, dirtSpotTexture));
        dirtSpots.add(new DirtSpot(617, 260, dirtSpotTexture));
        dirtSpots.add(new DirtSpot(900, 172, dirtSpotTexture));
        dirtSpots.add(new DirtSpot(1010, 300, dirtSpotTexture));

        cracks.add(new Crack(115, 210, crackTexture));
        cracks.add(new Crack(440, 210, crackTexture));
        cracks.add(new Crack(570, 560, crackTexture));
        cracks.add(new Crack(810, 455, crackTexture));
        cracks.add(new Crack(1050, 500, crackTexture));

        totalProblems = dirtSpots.size() + cracks.size();

        float iconSize = 80;
        brushSelectionBounds = new Rectangle(20, 20, iconSize, iconSize);
        wrenchSelectionBounds = new Rectangle(110, 20, iconSize, iconSize);

        float bgPadding = 10;
        toolBgBounds = new Rectangle(
            brushSelectionBounds.x - bgPadding,
            brushSelectionBounds.y - bgPadding,
            iconSize * 2 + 10 + bgPadding * 2,
            iconSize + bgPadding * 2
        );

        font = new BitmapFont();
        font.setColor(Color.BLACK);
        font.getData().setScale(2f);

        Gdx.input.setInputProcessor(this);
    }

    private void handleInteraction(int screenX, int screenY) {
        worldCoordinates.set(screenX, screenY, 0);
        viewport.unproject(worldCoordinates);

        Texture currentCursorTexture = (currentTool == ToolType.BRUSH) ? brushTexture : wrenchTexture;
        Rectangle interactionBounds = new Rectangle(
                worldCoordinates.x - currentCursorTexture.getWidth() / 2f,
                worldCoordinates.y - currentCursorTexture.getHeight() / 2f,
                currentCursorTexture.getWidth(),
                currentCursorTexture.getHeight());

        if (currentTool == ToolType.BRUSH) {
            for (DirtSpot spot : dirtSpots) if (!spot.isClean() && spot.bounds.overlaps(interactionBounds)) spot.clean();
        } else {
            for (Crack crack : cracks) if (!crack.isFixed() && crack.bounds.overlaps(interactionBounds)) crack.repair();
        }
    }

    @Override
    public void resize(int width, int height) { viewport.update(width, height, true); }

    @Override
    public void render(float delta) {
        if (!allFixed && clearedCount >= totalProblems) allFixed = true;

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(cleanPipeTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        for (DirtSpot spot : dirtSpots) {
            batch.setColor(1, 1, 1, 1 - spot.cleanProgress);
            batch.draw(dirtSpotTexture, spot.position.x, spot.position.y);
        }

        for (Crack crack : cracks) {
            batch.setColor(1, 1, 1, 1 - crack.repairProgress);
            batch.draw(crackTexture, crack.position.x, crack.position.y);
        }
        batch.setColor(1, 1, 1, 1);

        batch.draw(toolBgTexture, toolBgBounds.x, toolBgBounds.y, toolBgBounds.width, toolBgBounds.height);
        batch.draw(brushIconTexture, brushSelectionBounds.x, brushSelectionBounds.y, brushSelectionBounds.width, brushSelectionBounds.height);
        batch.draw(wrenchIconTexture, wrenchSelectionBounds.x, wrenchSelectionBounds.y, wrenchSelectionBounds.width, wrenchSelectionBounds.height);

        if (currentTool == ToolType.BRUSH) batch.draw(selectionHighlightTexture, brushSelectionBounds.x, brushSelectionBounds.y, brushSelectionBounds.width, brushSelectionBounds.height);
        else batch.draw(selectionHighlightTexture, wrenchSelectionBounds.x, wrenchSelectionBounds.y, wrenchSelectionBounds.width, wrenchSelectionBounds.height);

        font.draw(batch, "Fixed: " + clearedCount + " / " + totalProblems, 20, WORLD_HEIGHT - 20);

        if (!allFixed) {
            worldCoordinates.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(worldCoordinates);
            Texture cursorTexture = (currentTool == ToolType.BRUSH) ? brushTexture : wrenchTexture;
            batch.draw(cursorTexture, worldCoordinates.x - cursorTexture.getWidth() / 2f, worldCoordinates.y - cursorTexture.getHeight() / 2f);
        }

        if (allFixed) {
            float x = (WORLD_WIDTH - winPipeTexture.getWidth()) / 2f;
            float y = (WORLD_HEIGHT - winPipeTexture.getHeight()) / 2f;
            batch.draw(winPipeTexture, x, y);
            
            if (Gdx.input.justTouched()) {
                game.setScreen(previousScreen);
            }
        }

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        cleanPipeTexture.dispose();
        dirtSpotTexture.dispose();
        brushTexture.dispose();
        crackTexture.dispose();
        wrenchTexture.dispose();
        brushIconTexture.dispose();
        wrenchIconTexture.dispose();
        selectionHighlightTexture.dispose();
        toolBgTexture.dispose();
        winPipeTexture.dispose();
        font.dispose();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (allFixed) return false;

        worldCoordinates.set(screenX, screenY, 0);
        viewport.unproject(worldCoordinates);

        if (brushSelectionBounds.contains(worldCoordinates.x, worldCoordinates.y)) {
            currentTool = ToolType.BRUSH; return true;
        }
        if (wrenchSelectionBounds.contains(worldCoordinates.x, worldCoordinates.y)) {
            currentTool = ToolType.WRENCH; return true;
        }

        handleInteraction(screenX, screenY);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (allFixed) return false;
        handleInteraction(screenX, screenY);
        return true;
    }

    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean touchCancelled(int x, int y, int pointer, int button) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
}
