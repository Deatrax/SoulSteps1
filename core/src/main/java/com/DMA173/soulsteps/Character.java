package com.DMA173.soulsteps;

import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Character {
    public enum Direction { UP, DOWN, LEFT, RIGHT, IDLE }

    private final Vector2 position = new Vector2();
    private float speed;
    private float stateTime;

    // Animations
    private Animation<TextureRegion> walkUp, walkDown, walkLeft, walkRight;
    private Animation<TextureRegion> currentAnim;
    private Direction currentDir = Direction.DOWN; // default facing

    // Keep textures we own to dispose later
    private final Array<Texture> ownedTextures = new Array<>();

    // Drawing options
    private float drawOriginX = 0f; // adjust if you want feet alignment
    private float drawOriginY = 0f;

    public Character(String animFolder, float startX, float startY, float speed, float frameDuration) {
        this.speed = speed;
        this.position.set(startX, startY);
        loadFromFolder(animFolder, frameDuration);
        currentAnim = walkDown != null ? walkDown : firstNonNull(walkUp, walkLeft, walkRight);
    }

    private Animation<TextureRegion> firstNonNull(Animation<TextureRegion>... a) {
        for (Animation<TextureRegion> x : a) if (x != null) return x;
        return null;
    }

    /** Loads animations from subfolders: up/, down/, left/, right/ (PNG frames). */
    private void loadFromFolder(String baseFolder, float frameDuration) {
        walkUp    = loadAnim(baseFolder + "/up",    frameDuration);
        walkDown  = loadAnim(baseFolder + "/down",  frameDuration);
        walkLeft  = loadAnim(baseFolder + "/left",  frameDuration);
        walkRight = loadAnim(baseFolder + "/right", frameDuration);

        // Fallback: allow flat folder with prefixes up_*.png, etc. if subfolders not found
        if (walkUp == null)    walkUp    = loadAnimFlat(baseFolder, "up_",    frameDuration);
        if (walkDown == null)  walkDown  = loadAnimFlat(baseFolder, "down_",  frameDuration);
        if (walkLeft == null)  walkLeft  = loadAnimFlat(baseFolder, "left_",  frameDuration);
        if (walkRight == null) walkRight = loadAnimFlat(baseFolder, "right_", frameDuration);

        if (walkUp == null && walkDown == null && walkLeft == null && walkRight == null) {
            throw new IllegalStateException("No animations found in '" + baseFolder + "'. " +
                "Expected subfolders up/down/left/right with PNG frames, or files prefixed up_/down_/left_/right_.");
        }
    }

    private Animation<TextureRegion> loadAnim(String folder, float frameDuration) {
        FileHandle dir = Gdx.files.internal(folder);
        if (!dir.exists() || !dir.isDirectory()) return null;

        FileHandle[] files = dir.list();
        Array<FileHandle> pngs = new Array<>();
        for (FileHandle f : files) {
            String n = f.name().toLowerCase();
            if (n.endsWith(".png")) pngs.add(f);
        }
        if (pngs.size == 0) return null;

        // Sort alphanumerically so frame_01.png, frame_02.png ... frame_10.png are in order
        pngs.sort(Comparator.comparing(FileHandle::name));

        Array<TextureRegion> frames = new Array<>(pngs.size);
        for (FileHandle f : pngs) {
            Texture t = new Texture(f);              // owns the texture
            t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            ownedTextures.add(t);
            frames.add(new TextureRegion(t));
        }
        Animation<TextureRegion> anim = new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
        return anim;
    }

    /** Flat folder fallback: picks files like prefix_*.png (e.g., up_01.png). */
    private Animation<TextureRegion> loadAnimFlat(String baseFolder, String prefix, float frameDuration) {
        FileHandle dir = Gdx.files.internal(baseFolder);
        if (!dir.exists() || !dir.isDirectory()) return null;

        FileHandle[] files = dir.list();
        Array<FileHandle> pngs = new Array<>();
        for (FileHandle f : files) {
            String n = f.name();
            String ln = n.toLowerCase();
            if (ln.endsWith(".png") && n.startsWith(prefix)) pngs.add(f);
        }
        if (pngs.size == 0) return null;

        pngs.sort(Comparator.comparing(FileHandle::name));

        Array<TextureRegion> frames = new Array<>(pngs.size);
        for (FileHandle f : pngs) {
            Texture t = new Texture(f);
            t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            ownedTextures.add(t);
            frames.add(new TextureRegion(t));
        }
        return new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
    }

    /** Simple built-in input + movement (optional — call from your Screen). */
    public void handleInput(float delta) {
        boolean moved = false;
        if (Gdx.input.isKeyPressed(Input.Keys.UP))    { position.y += speed * delta; setDir(Direction.UP);    moved = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  { position.y -= speed * delta; setDir(Direction.DOWN);  moved = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  { position.x -= speed * delta; setDir(Direction.LEFT);  moved = true; }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) { position.x += speed * delta; setDir(Direction.RIGHT); moved = true; }
        if (!moved) setDir(Direction.IDLE);
    }

    public void setDir(Direction dir) {
        if (dir == Direction.IDLE) { currentDir = Direction.IDLE; return; }
        currentDir = dir;
        switch (dir) {
            case UP:    if (walkUp    != null) currentAnim = walkUp;    break;
            case DOWN:  if (walkDown  != null) currentAnim = walkDown;  break;
            case LEFT:  if (walkLeft  != null) currentAnim = walkLeft;  break;
            case RIGHT: if (walkRight != null) currentAnim = walkRight; break;
        }
    }

    public void update(float delta) {
        stateTime += delta;
    }

    public void render(Batch batch) {
        TextureRegion frame;
        if (currentDir == Direction.IDLE) {
            // first frame of the last facing direction
            frame = currentAnim.getKeyFrames()[0];
        } else {
            frame = currentAnim.getKeyFrame(stateTime, true);
        }
        batch.draw(frame, position.x - drawOriginX, position.y - drawOriginY);
    }

    public Vector2 getPosition() { return position; }
    public void setSpeed(float speed) { this.speed = speed; }
    public float getSpeed() { return speed; }

    /** Optional: set where to anchor drawing (e.g., feet alignment). */
    public void setDrawOrigin(float originX, float originY) {
        this.drawOriginX = originX;
        this.drawOriginY = originY;
    }

    public void dispose() {
        for (Texture t : ownedTextures) t.dispose();
        ownedTextures.clear();
    }
}