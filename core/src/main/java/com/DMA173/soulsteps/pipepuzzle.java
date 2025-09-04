package com.DMA173.soulsteps;

import com.DMA173.soulsteps.story.StoryProgressionManager;
import com.DMA173.soulsteps.ui.UIManager;
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

    private enum ToolType {
        BRUSH, WRENCH, SCREWDRIVER
    }
    private ToolType currentTool;

    private SpriteBatch batch;
    private Texture cleanPipeTexture, dirtSpotTexture, brushTexture;
    private Texture crackTexture, wrenchTexture;
    private Texture brushIconTexture, wrenchIconTexture, selectionHighlightTexture;
    private Texture toolBgTexture;
    private Texture winPipeTexture;
    private Texture limiterTexture, screwdriverTexture, screwdriverIconTexture;

    private ArrayList<DirtSpot> dirtSpots;
    private ArrayList<Crack> cracks;
    private ArrayList<Limiter> limiters;

    private OrthographicCamera camera;
    private Viewport viewport;
    private Vector3 worldCoordinates = new Vector3();

    private Rectangle brushSelectionBounds;
    private Rectangle wrenchSelectionBounds;
    private Rectangle screwdriverSelectionBounds;
    private Rectangle toolBgBounds;

    private BitmapFont font;
    private int clearedCount = 0;
    private boolean allFixed = false;
    private int totalProblems = 0;

    private Game game;
    private ScreenAdapter previousScreen;
    private UIManager uiManager;
    private StoryProgressionManager storyManager;

    private static final float WORLD_WIDTH = 1280;
    private static final float WORLD_HEIGHT = 720;

    class DirtSpot {
        Vector2 position;
        Rectangle bounds;
        float cleanProgress = 0f;
        boolean counted = false;
        private Runnable onCleanedCallback = null;

        DirtSpot(float x, float y, Texture texture) {
            this.position = new Vector2(x, y);
            this.bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
        }

        public void setOnCleanedCallback(Runnable callback) {
            this.onCleanedCallback = callback;
        }

        void clean() {
            if (isClean()) return;
            this.cleanProgress += Gdx.graphics.getDeltaTime() * 1.5f;

            if (this.cleanProgress >= 1f) {
                this.cleanProgress = 1f;
                if (!counted) {
                    clearedCount++;
                    counted = true;
                    System.out.println("dirt cleaned");

                    if (onCleanedCallback != null) {
                        onCleanedCallback.run();
                    }
                }
            }
        }

        boolean isClean() {
            return cleanProgress >= 1f;
        }
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
            this.repairProgress += Gdx.graphics.getDeltaTime() * 1.2f;

            if (this.repairProgress >= 1f) {
                this.repairProgress = 1f;
                if (!counted) {
                    clearedCount++;
                    counted = true;
                }
            }
        }

        boolean isFixed() {
            return repairProgress >= 1f;
        }
    }

    class Limiter {
        Vector2 position;
        Rectangle bounds;
        float removalProgress = 0f;
        boolean counted = false;
        private boolean isRemovable = false;

        Limiter(float x, float y, Texture texture) {
            this.position = new Vector2(x, y);
            this.bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
        }
        
        public void setRemovable(boolean removable) {
            this.isRemovable = removable;
        }

        void remove() {
            if (isRemoved() || !isRemovable) return;
            
            this.removalProgress += Gdx.graphics.getDeltaTime() * 1.8f;

            if (this.removalProgress >= 1f) {
                this.removalProgress = 1f;
                if (!counted) {
                    clearedCount++;
                    counted = true;
                }
            }
        }

        boolean isRemoved() {
            return removalProgress >= 1f;
        }
    }

    public pipepuzzle(Game game, String previousMapName, ScreenAdapter previousScreen, StoryProgressionManager story, WorldManager world, UIManager uiManager) {
        this.game = game;
        this.previousScreen = previousScreen;
        this.uiManager = uiManager;
        this.storyManager = story;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        batch = new SpriteBatch();

        currentTool = ToolType.BRUSH;

        cleanPipeTexture = new Texture("clean_pipe_layout.png");
        dirtSpotTexture = new Texture("dirt_spot.png");
        brushTexture = new Texture("brush.png");
        crackTexture = new Texture("crackk.png");
        wrenchTexture = new Texture("wrench.png");
        brushIconTexture = new Texture("brush_icon.jpg");
        wrenchIconTexture = new Texture("wrench_icon.png");
        selectionHighlightTexture = new Texture("selection_highlight.png");
        toolBgTexture = new Texture("tool-bg.png");
        winPipeTexture = new Texture("pipe.png");
        limiterTexture = new Texture("limiter.png");
        screwdriverTexture = new Texture("screwdriver.png");
        screwdriverIconTexture = new Texture("screwdriver_icon.png");
        
        dirtSpots = new ArrayList<>();
        cracks = new ArrayList<>();
        limiters = new ArrayList<>();

        final Limiter coveredLimiter = new Limiter(700, 350, limiterTexture);
        limiters.add(coveredLimiter);

        DirtSpot dirtOnLimiter = new DirtSpot(700, 350, dirtSpotTexture);
        
        dirtOnLimiter.setOnCleanedCallback(() -> {
            uiManager.showChoice(
                "System",
                "The limiter is now exposed. Remove it?",
                new String[]{"Yes", "No"},
                choice -> {
                    if (choice == 1) {
                        coveredLimiter.setRemovable(true);
                        uiManager.showNotification("You can now use the screwdriver.");
                    } else {
                        totalProblems--;
                        uiManager.showNotification("You leave the limiter in place.");
                    }
                    uiManager.hideDialogue();
                }
            );
        });
        
        dirtSpots.add(dirtOnLimiter);
        
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
        
        totalProblems = dirtSpots.size() + cracks.size() + limiters.size();
        
        float iconSize = 80;
        float iconSpacing = 10;
        
        brushSelectionBounds = new Rectangle(20, 20, iconSize, iconSize);
        wrenchSelectionBounds = new Rectangle(20 + iconSize + iconSpacing, 20, iconSize, iconSize);
        screwdriverSelectionBounds = new Rectangle(20 + (iconSize + iconSpacing) * 2, 20, iconSize, iconSize);

        float bgPadding = 10;
        toolBgBounds = new Rectangle(
            brushSelectionBounds.x - bgPadding,
            brushSelectionBounds.y - bgPadding,
            (iconSize * 3) + (iconSpacing * 2) + (bgPadding * 2),
            iconSize + (bgPadding * 2)
        );

        font = new BitmapFont();
        font.setColor(Color.BLACK);
        font.getData().setScale(2f);

        Gdx.input.setInputProcessor(this);
    }

    private void handleInteraction(int screenX, int screenY) {
        worldCoordinates.set(screenX, screenY, 0);
        viewport.unproject(worldCoordinates);

        Texture currentCursorTexture;
        if (currentTool == ToolType.BRUSH) {
            currentCursorTexture = brushTexture;
        } else if (currentTool == ToolType.WRENCH) {
            currentCursorTexture = wrenchTexture;
        } else {
            currentCursorTexture = screwdriverTexture;
        }

        Rectangle interactionBounds = new Rectangle(
                worldCoordinates.x - currentCursorTexture.getWidth() / 2f,
                worldCoordinates.y - currentCursorTexture.getHeight() / 2f,
                currentCursorTexture.getWidth(),
                currentCursorTexture.getHeight());

        if (currentTool == ToolType.BRUSH) {
            for (DirtSpot spot : dirtSpots) {
                if (!spot.isClean() && spot.bounds.overlaps(interactionBounds)) {
                    spot.clean();
                }
            }
        } else if (currentTool == ToolType.WRENCH) {
            for (Crack crack : cracks) {
                if (!crack.isFixed() && crack.bounds.overlaps(interactionBounds)) {
                    crack.repair();
                }
            }
        } else if (currentTool == ToolType.SCREWDRIVER) {
            for (Limiter limiter : limiters) {
                if (!limiter.isRemoved() && limiter.bounds.overlaps(interactionBounds)) {
                    limiter.remove();
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        uiManager.resize(width, height);
    }

    @Override
    public void render(float delta) {
        uiManager.update(delta);
        uiManager.handleDialogueInput();

        if (!allFixed && clearedCount >= totalProblems) {
            allFixed = true;
        }

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(cleanPipeTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        for (Limiter limiter : limiters) {
            batch.setColor(1, 1, 1, 1 - limiter.removalProgress);
            batch.draw(limiterTexture, limiter.position.x, limiter.position.y);
        }

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
        batch.draw(screwdriverIconTexture, screwdriverSelectionBounds.x, screwdriverSelectionBounds.y, screwdriverSelectionBounds.width, screwdriverSelectionBounds.height);

        if (currentTool == ToolType.BRUSH) {
            batch.draw(selectionHighlightTexture, brushSelectionBounds.x, brushSelectionBounds.y, brushSelectionBounds.width, brushSelectionBounds.height);
        } else if (currentTool == ToolType.WRENCH) {
            batch.draw(selectionHighlightTexture, wrenchSelectionBounds.x, wrenchSelectionBounds.y, wrenchSelectionBounds.width, wrenchSelectionBounds.height);
        } else {
            batch.draw(selectionHighlightTexture, screwdriverSelectionBounds.x, screwdriverSelectionBounds.y, screwdriverSelectionBounds.width, screwdriverSelectionBounds.height);
        }
        
        String progressText = "Fixed: " + clearedCount + " / " + totalProblems;
        font.draw(batch, progressText, 20, WORLD_HEIGHT - 20);

        if (!allFixed && !uiManager.isDialogueActive()) {
            worldCoordinates.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(worldCoordinates);
            
            Texture cursorTexture;
            if (currentTool == ToolType.BRUSH) {
                cursorTexture = brushTexture;
            } else if (currentTool == ToolType.WRENCH) {
                cursorTexture = wrenchTexture;
            } else {
                cursorTexture = screwdriverTexture;
            }
            
            batch.draw(cursorTexture, worldCoordinates.x - cursorTexture.getWidth() / 2f, worldCoordinates.y - cursorTexture.getHeight() / 2f);
        }

        if (allFixed) {
            float imageWidth = winPipeTexture.getWidth();
            float imageHeight = winPipeTexture.getHeight();
            float x = (WORLD_WIDTH - imageWidth) / 2f;
            float y = (WORLD_HEIGHT - imageHeight) / 2f;
            
            batch.draw(winPipeTexture, x, y);

            if(Gdx.input.justTouched()){
                 game.setScreen(previousScreen);
                 // --- Start of Necessary Change ---
                 // REMOVED the dispose() call from here.
                 // --- End of Necessary Change ---
            }
        }

        batch.end();
        
        uiManager.render(null, camera, storyManager);
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

        limiterTexture.dispose();
        screwdriverTexture.dispose();
        screwdriverIconTexture.dispose();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (uiManager.isDialogueActive()) {
            return false;
        }
        
        if (allFixed) {
            // --- Start of Necessary Change ---
            // Let the render loop handle the final click to exit.
            // Returning true here consumes the click so the puzzle doesn't try to process it.
            return true;
            // --- End of Necessary Change ---
        }

        worldCoordinates.set(screenX, screenY, 0);
        viewport.unproject(worldCoordinates);

        if (brushSelectionBounds.contains(worldCoordinates.x, worldCoordinates.y)) {
            currentTool = ToolType.BRUSH;
            return true;
        }
        if (wrenchSelectionBounds.contains(worldCoordinates.x, worldCoordinates.y)) {
            currentTool = ToolType.WRENCH;
            return true;
        }
        if (screwdriverSelectionBounds.contains(worldCoordinates.x, worldCoordinates.y)) {
            currentTool = ToolType.SCREWDRIVER;
            return true;
        }

        handleInteraction(screenX, screenY);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (uiManager.isDialogueActive() || allFixed) {
            return false;
        }
        
        handleInteraction(screenX, screenY);
        return true;
    }

    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean touchCancelled(int x, int y, int pointer, int button) { return false; }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}