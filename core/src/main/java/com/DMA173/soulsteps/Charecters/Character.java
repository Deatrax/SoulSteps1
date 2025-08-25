package com.DMA173.soulsteps.Charecters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * An abstract base class for all characters in the game (Player, Enemies, NPCs).
 * Demonstrates core OOP principles: abstraction, encapsulation, inheritance.
 */
public abstract class Character {
    protected CharecterAssets assets;
    
    // --- Core Character State (Encapsulation) ---
    protected Vector2 position;
    protected CharecterAssets.Direction currentDir;
    protected float stateTime;
    protected boolean isMoving;
    protected int characterType;
    protected float speed;
    
    // --- Game-specific properties for SoulSteps ---
    protected String name;
    protected int health;
    protected boolean isInteractable;
    
    public Character(CharecterAssets assets, int characterType, float startX, float startY, float speed) {
        this.assets = assets;
        this.characterType = characterType;
        this.position = new Vector2(startX, startY);
        this.speed = speed;
        this.currentDir = CharecterAssets.Direction.DOWN;
        this.stateTime = 0f;
        this.isMoving = false;
        this.health = 100; // Default health
        this.isInteractable = false;
    }
    
    /**
     * Abstract method - each character type implements their own behavior
     * Demonstrates abstraction principle
     */
    public abstract void update(float delta);
    
    /**
     * Shared rendering logic for all characters
     * Demonstrates code reuse through inheritance
     */
    public void render(Batch batch) {
        TextureRegion currentFrame;
        
        if (isMoving) {
            Animation<TextureRegion> walkAnimation = assets.getWalkAnimation(this.characterType, this.currentDir);
            if (walkAnimation != null) {
                currentFrame = walkAnimation.getKeyFrame(stateTime, true);
            } else {
                currentFrame = assets.getIdleFrame(this.characterType, this.currentDir);
            }
        } else {
            currentFrame = assets.getIdleFrame(this.characterType, this.currentDir);
        }
        
        if (currentFrame != null) {
            float drawX = position.x - currentFrame.getRegionWidth() / 2f;
            float drawY = position.y;
            batch.draw(currentFrame, drawX, drawY);
        }
    }
    
    protected void updateStateTime(float delta) {
        this.stateTime += delta;
    }
    
    // --- Movement methods for shared functionality ---
    protected void moveInDirection(CharecterAssets.Direction direction, float delta) {
        this.currentDir = direction;
        this.isMoving = true;
        
        switch(direction) {
            case UP:
                position.y += speed * delta;
                break;
            case DOWN:
                position.y -= speed * delta;
                break;
            case LEFT:
                position.x -= speed * delta;
                break;
            case RIGHT:
                position.x += speed * delta;
                break;
        }
    }
    
    // --- Getters and Setters (Encapsulation) ---
    public Vector2 getPosition() { return position; }
    public void setPosition(float x, float y) { this.position.set(x, y); }
    public float getSpeed() { return speed; }
    public void setSpeed(float speed) { this.speed = speed; }
    public void setCurrentDir(CharecterAssets.Direction dir) { this.currentDir = dir; }
    public void setMoving(boolean moving) { this.isMoving = moving; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
    public boolean isInteractable() { return isInteractable; }
    public void setInteractable(boolean interactable) { this.isInteractable = interactable; }
}
