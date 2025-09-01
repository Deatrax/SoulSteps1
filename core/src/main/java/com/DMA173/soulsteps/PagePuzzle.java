package com.DMA173.soulsteps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;

public class PagePuzzle extends ScreenAdapter implements InputProcessor {

    private SpriteBatch batch;
    private Texture docTexture;
    private Texture background;
    private TextureRegion[][] split;
    private List<PuzzlePiece> pieces;
    private Vector2[] gridPositions;
    private boolean[] res;

    private final int rows = 3, cols = 3;
    private int tileWidth, tileHeight;
    private PuzzlePiece selectedPiece = null;
    private float offsetX, offsetY;

    // Default font
    private BitmapFont font;

    // Camera + Viewport
    private OrthographicCamera camera;
    private Viewport viewport;

    // For grid drawing
    private ShapeRenderer shapeRenderer;

    // --- Win animation variables ---
    private boolean won = false;
    private float zoomScale = 1f;
    private final float zoomSpeed = 0.5f; // zoom speed per second
    private Vector2 puzzleCenter;         // center of solved puzzle
    private Vector2 targetCenter;         // center of the screen
    private Vector2 currentOffset = new Vector2(0,0);
    private final float moveSpeed = 200f; // pixels per second for moving puzzle

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        font = new BitmapFont();

        background = new Texture(Gdx.files.internal("bg.jpg"));
        docTexture = new Texture(Gdx.files.internal("document.jpg"));

        tileWidth = docTexture.getWidth() / cols;
        tileHeight = docTexture.getHeight() / rows;
        split = TextureRegion.split(docTexture, tileWidth, tileHeight);

        pieces = new ArrayList<>();
        gridPositions = new Vector2[rows * cols];
        res = new boolean[rows * cols];

        float worldWidth = 1000f;
        float worldHeight = 800f;
        camera = new OrthographicCamera();
        viewport = new FitViewport(worldWidth, worldHeight, camera);
        viewport.apply();
        camera.position.set(worldWidth / 2f, worldHeight / 2f, 0);
        camera.update();

        float startX = 50f;
        float startY = 280f;

        int id = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                gridPositions[id] = new Vector2(startX + c * tileWidth, startY + r * tileHeight);

                float pileX = 560f + (float) Math.random() * 300f;
                float pileY = 240f + (float) Math.random() * 300f;

                TextureRegion region = split[rows - 1 - r][c];
                pieces.add(new PuzzlePiece(region, id, pileX, pileY));
                res[id] = false;
                id++;
            }
        }

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.9f, 0.9f, 0.9f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);

        // Draw background
        batch.begin();
        batch.draw(background, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        // Heading text (centered)
        String heading = "Solve the puzzle to unveil the clue";
        font.getData().setScale(3f);
        font.setColor(Color.BLACK);
        com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();
        layout.setText(font, heading);
        float x = (viewport.getWorldWidth() - layout.width) / 2f;
        float y = viewport.getWorldHeight() - 30f;
        font.draw(batch, heading, x, y);
        batch.end();

        // Update zoom and move if won
        if (won) {
            // Zoom
            zoomScale += zoomSpeed * delta;
            if (zoomScale > 1.5f) zoomScale = 1.5f;

            // Move puzzle toward center
            float dx = targetCenter.x - puzzleCenter.x - currentOffset.x;
            float dy = targetCenter.y - puzzleCenter.y - currentOffset.y;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist > 1f) {
                float moveStep = moveSpeed * delta;
                if (moveStep > dist) moveStep = dist;
                currentOffset.x += dx / dist * moveStep;
                currentOffset.y += dy / dist * moveStep;
            }
        }

        // Draw grid
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1f, 1f, 1f, 1f);
        for (Vector2 pos : gridPositions) {
            float drawX = won ? puzzleCenter.x + (pos.x - puzzleCenter.x) * zoomScale + currentOffset.x : pos.x;
            float drawY = won ? puzzleCenter.y + (pos.y - puzzleCenter.y) * zoomScale + currentOffset.y : pos.y;
            float w = tileWidth * (won ? zoomScale : 1f);
            float h = tileHeight * (won ? zoomScale : 1f);
            shapeRenderer.rect(drawX, drawY, w, h);
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0f, 0f, 0f, 1f);
        for (Vector2 pos : gridPositions) {
            float drawX = won ? puzzleCenter.x + (pos.x - puzzleCenter.x) * zoomScale + currentOffset.x : pos.x;
            float drawY = won ? puzzleCenter.y + (pos.y - puzzleCenter.y) * zoomScale + currentOffset.y : pos.y;
            float w = tileWidth * (won ? zoomScale : 1f);
            float h = tileHeight * (won ? zoomScale : 1f);
            shapeRenderer.rect(drawX, drawY, w, h);
        }
        shapeRenderer.end();

        // Draw pieces
        batch.begin();
        for (PuzzlePiece p : pieces) {
            float drawX = won ? puzzleCenter.x + (p.currentPosition.x - puzzleCenter.x) * zoomScale + currentOffset.x : p.currentPosition.x;
            float drawY = won ? puzzleCenter.y + (p.currentPosition.y - puzzleCenter.y) * zoomScale + currentOffset.y : p.currentPosition.y;
            batch.draw(p.image, drawX, drawY, tileWidth * (won ? zoomScale : 1f), tileHeight * (won ? zoomScale : 1f));
        }

        // Left-aligned win message
        if (isWin()) {
            font.getData().setScale(3f);
            font.setColor(Color.BLUE);
            float leftX = 50f;
            font.draw(batch, "YOU HAVE SUCCESSFULLY BUILT THE MAP!!", leftX, 170f);
        }
        batch.end();
    }

    private boolean isWin() {
        for (boolean b : res) if (!b) return false;

        if (!won) {
            won = true;

            // Compute center of solved puzzle
            float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
            float maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE;
            for (Vector2 pos : gridPositions) {
                minX = Math.min(minX, pos.x);
                minY = Math.min(minY, pos.y);
                maxX = Math.max(maxX, pos.x + tileWidth);
                maxY = Math.max(maxY, pos.y + tileHeight);
            }
            puzzleCenter = new Vector2((minX + maxX) / 2f, (minY + maxY) / 2f);

            // Set target center of screen
            targetCenter = new Vector2(viewport.getWorldWidth() / 2f, viewport.getWorldHeight() / 2f);
            currentOffset.set(0,0);
        }

        return true;
    }

    // --- Input ---
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector2 world = viewport.unproject(new Vector2(screenX, screenY));
        float wx = world.x, wy = world.y;

        for (int i = pieces.size() - 1; i >= 0; i--) {
            PuzzlePiece p = pieces.get(i);
            if (wx >= p.currentPosition.x && wx <= p.currentPosition.x + tileWidth &&
                wy >= p.currentPosition.y && wy <= p.currentPosition.y + tileHeight) {
                selectedPiece = p;
                offsetX = wx - p.currentPosition.x;
                offsetY = wy - p.currentPosition.y;
                pieces.remove(i);
                pieces.add(p);
                break;
            }
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (selectedPiece != null) {
            Vector2 world = viewport.unproject(new Vector2(screenX, screenY));
            selectedPiece.currentPosition.set(world.x - offsetX, world.y - offsetY);
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (selectedPiece != null) {
            for (int i = 0; i < gridPositions.length; i++) {
                if (selectedPiece.currentPosition.dst(gridPositions[i]) < Math.max(tileWidth, tileHeight) / 2f) {
                    if (selectedPiece.id == i) {
                        selectedPiece.currentPosition.set(gridPositions[i]);
                        res[i] = true;
                    }
                    break;
                }
            }
            selectedPiece = null;
        }
        return true;
    }

    // Unused input
    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    @Override public boolean touchCancelled(int x, int y, int pointer, int button) { return false; }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(viewport.getWorldWidth() / 2f, viewport.getWorldHeight() / 2f, 0);
    }

    @Override
    public void dispose() {
        batch.dispose();
        docTexture.dispose();
        background.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }

    // PuzzlePiece inner class
    private static class PuzzlePiece {
        final TextureRegion image;
        final int id;
        final Vector2 currentPosition;

        PuzzlePiece(TextureRegion image, int id, float startX, float startY) {
            this.image = image;
            this.id = id;
            this.currentPosition = new Vector2(startX, startY);
        }
    }
}

