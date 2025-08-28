package com.DMA173.soulsteps.Charecters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * An abstract base class for all characters in the game (Player, Enemies, NPCs).
 * Now supports hair and clothing system with ClothesContainer.
 * Demonstrates core OOP principles: abstraction, encapsulation, inheritance.
 * 
 * Rendering order: Character Base → Hair → Clothing
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
    protected ClothesContainer clothes; // Clothing system including hair
    
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
        this.clothes = new ClothesContainer(); // Default: no outfit or hair
    }
    
    public Character(CharecterAssets assets, int characterType, float startX, float startY, float speed, ClothesContainer clothes) {
        this.assets = assets;
        this.characterType = characterType;
        this.position = new Vector2(startX, startY);
        this.speed = speed;
        this.currentDir = CharecterAssets.Direction.DOWN;
        this.stateTime = 0f;
        this.isMoving = false;
        this.health = 100; // Default health
        this.isInteractable = false;
        this.clothes = clothes != null ? clothes : new ClothesContainer();
    }
    
    /**
     * Abstract method - each character type implements their own behavior
     * Demonstrates abstraction principle
     */
    public abstract void update(float delta);
    
    /**
     * Shared rendering logic for all characters with hair and clothing support
     * Rendering order: Character Base → Hair → Clothing
     * Demonstrates code reuse through inheritance
     */
    public void render(Batch batch) {
        // Layer 1: Render base character
        renderCharacterLayer(batch);
        
        // Layer 2: Render hair on top of character if present
        if (clothes.hairType != 0) {
            renderHairLayer(batch);
        }
        
        // Layer 3: Render clothing on top if present
        if (clothes.outfitType != 0) {
            renderClothingLayer(batch);
        }
    }
    
    /**
     * Render the base character layer
     */
    private void renderCharacterLayer(Batch batch) {
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
    
    /**
     * Render the hair layer on top of character
     */
    private void renderHairLayer(Batch batch) {
        TextureRegion hairFrame;
        
        if (isMoving) {
            Animation<TextureRegion> hairAnimation = assets.getHairWalkAnimation(this.clothes.hairType, this.currentDir);
            if (hairAnimation != null) {
                hairFrame = hairAnimation.getKeyFrame(stateTime, true);
            } else {
                hairFrame = assets.getHairIdleFrame(this.clothes.hairType, this.currentDir);
            }
        } else {
            hairFrame = assets.getHairIdleFrame(this.clothes.hairType, this.currentDir);
        }
        
        if (hairFrame != null) {
            float drawX = position.x - hairFrame.getRegionWidth() / 2f;
            float drawY = position.y;
            batch.draw(hairFrame, drawX, drawY);
        }
    }
    
    /**
     * Render the clothing layer on top of hair
     */
    private void renderClothingLayer(Batch batch) {
        TextureRegion clothingFrame;
        
        if (isMoving) {
            Animation<TextureRegion> clothingAnimation = assets.getClothingWalkAnimation(this.clothes, this.currentDir);
            if (clothingAnimation != null) {
                clothingFrame = clothingAnimation.getKeyFrame(stateTime, true);
            } else {
                clothingFrame = assets.getClothingIdleFrame(this.clothes, this.currentDir);
            }
        } else {
            clothingFrame = assets.getClothingIdleFrame(this.clothes, this.currentDir);
        }
        
        if (clothingFrame != null) {
            float drawX = position.x - clothingFrame.getRegionWidth() / 2f;
            float drawY = position.y;
            batch.draw(clothingFrame, drawX, drawY);
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
    
    // --- Hair and Clothing methods ---
    public void changeHair(int hairType) {
        this.clothes.hairType = hairType;
    }
    
    public void changeOutfit(int outfitType) {
        this.clothes.outfitType = outfitType;
    }
    
    public void changeAppearance(ClothesContainer newClothes) {
        this.clothes = newClothes != null ? newClothes : new ClothesContainer();
    }
    
    public void removeHair() {
        this.clothes.hairType = 0;
    }
    
    public void removeOutfit() {
        this.clothes.outfitType = 0;
    }
    
    public void changeAccessory(int accessoryType) {
        this.clothes.accessoryType = accessoryType;
    }
    
    public void changeModel(int modelType) {
        this.clothes.modelType = modelType;
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
    public ClothesContainer getClothes() { return clothes; }
    public void setClothes(ClothesContainer clothes) { this.clothes = clothes != null ? clothes : new ClothesContainer(); }
    public int getCharacterType() { return characterType; }
    public void setCharacterType(int characterType) { this.characterType = characterType; }
}