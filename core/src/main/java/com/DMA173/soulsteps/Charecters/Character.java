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

    //map or zone name for applying scaling 
    private String currentMapName;
    private float scale = 1.0f;
    
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
    
    // /**
    //  * Shared rendering logic for all characters with hair and clothing support
    //  * Rendering order: Character Base → Hair → Clothing
    //  * Demonstrates code reuse through inheritance
    //  */
    // public void render(Batch batch) {
    //     // Layer 1: Render base character
    //     renderCharacterLayer(batch);
        
    //     // Layer 2: Render hair on top of character if present
    //     if (clothes.hairType != 0) {
    //         renderHairLayer(batch);
    //     }
        
    //     // Layer 3: Render clothing on top if present
    //     if (clothes.outfitType != 0) {
    //         renderClothingLayer(batch);
    //     }
    // }
    
    // /**
    //  * Render the base character layer
    //  */
    // private void renderCharacterLayer(Batch batch) {
    //     TextureRegion currentFrame;
        
    //     if (isMoving) {
    //         Animation<TextureRegion> walkAnimation = assets.getWalkAnimation(this.characterType, this.currentDir);
    //         if (walkAnimation != null) {
    //             currentFrame = walkAnimation.getKeyFrame(stateTime, true);
    //         } else {
    //             currentFrame = assets.getIdleFrame(this.characterType, this.currentDir);
    //         }
    //     } else {
    //         currentFrame = assets.getIdleFrame(this.characterType, this.currentDir);
    //     }
        
    //     if (currentFrame != null) {
    //         float drawX = position.x - currentFrame.getRegionWidth() / 2f;
    //         float drawY = position.y;
    //         batch.draw(currentFrame, drawX, drawY);
    //     }
    // }
    
    // /**
    //  * Render the hair layer on top of character
    //  */
    // private void renderHairLayer(Batch batch) {
    //     TextureRegion hairFrame;
        
    //     if (isMoving) {
    //         Animation<TextureRegion> hairAnimation = assets.getHairWalkAnimation(this.clothes.hairType, this.currentDir);
    //         if (hairAnimation != null) {
    //             hairFrame = hairAnimation.getKeyFrame(stateTime, true);
    //         } else {
    //             hairFrame = assets.getHairIdleFrame(this.clothes.hairType, this.currentDir);
    //         }
    //     } else {
    //         hairFrame = assets.getHairIdleFrame(this.clothes.hairType, this.currentDir);
    //     }
        
    //     if (hairFrame != null) {
    //         float drawX = position.x - hairFrame.getRegionWidth() / 2f;
    //         float drawY = position.y;
    //         batch.draw(hairFrame, drawX, drawY);
    //     }
    // }
    
    // /**
    //  * Render the clothing layer on top of hair
    //  */
    // private void renderClothingLayer(Batch batch) {
    //     TextureRegion clothingFrame;
        
    //     if (isMoving) {
    //         Animation<TextureRegion> clothingAnimation = assets.getClothingWalkAnimation(this.clothes, this.currentDir);
    //         if (clothingAnimation != null) {
    //             clothingFrame = clothingAnimation.getKeyFrame(stateTime, true);
    //         } else {
    //             clothingFrame = assets.getClothingIdleFrame(this.clothes, this.currentDir);
    //         }
    //     } else {
    //         clothingFrame = assets.getClothingIdleFrame(this.clothes, this.currentDir);
    //     }
        
    //     if (clothingFrame != null) {
    //         float drawX = position.x - clothingFrame.getRegionWidth() / 2f;
    //         float drawY = position.y;
    //         batch.draw(clothingFrame, drawX, drawY);
    //     }
    // }


     /**
     * This is the main render method. It has been simplified and now delegates
     * the drawing of each layer to a new helper method that handles scaling.
     */
   
    public void render(Batch batch) {
        // Layer 1: Render base character
        TextureRegion characterFrame = getFrameForRender(isMoving, characterType, currentDir);
        drawLayer(batch, characterFrame);
        
        // Layer 2: Render hair
        if (clothes.hairType != 0) {
            TextureRegion hairFrame = getHairFrameForRender(isMoving, clothes.hairType, currentDir);
            drawLayer(batch, hairFrame);
        }
        
        // Layer 3: Render clothing
        if (clothes.outfitType != 0) {
            TextureRegion clothingFrame = getClothingFrameForRender(isMoving, clothes, currentDir);
            drawLayer(batch, clothingFrame);
        }
    }

    /**
     * A new, unified helper method that draws any texture region with the correct scale.
     * This prevents code duplication.
     */
    protected void drawLayer(Batch batch, TextureRegion frame) {
        if (frame == null) return;

        // Calculate the scaled width and height
        float drawWidth = frame.getRegionWidth() * this.scale;
        float drawHeight = frame.getRegionHeight() * this.scale;

        // Calculate position to keep the character centered at their feet
        float drawX = position.x - drawWidth / 2f;
        float drawY = position.y;

        batch.draw(frame, drawX, drawY, drawWidth, drawHeight);
    }
    
    // --- Helper methods to get the correct animation frame ---
    // These methods simply find the right frame but do not draw it.
    
    private TextureRegion getFrameForRender(boolean moving, int charType, CharecterAssets.Direction dir) {
        if (moving) {
            Animation<TextureRegion> anim = assets.getWalkAnimation(charType, dir);
            return (anim != null) ? anim.getKeyFrame(stateTime, true) : assets.getIdleFrame(charType, dir);
        } else {
            return assets.getIdleFrame(charType, dir);
        }
    }
    
    private TextureRegion getHairFrameForRender(boolean moving, int hairType, CharecterAssets.Direction dir) {
        if (moving) {
            Animation<TextureRegion> anim = assets.getHairWalkAnimation(hairType, dir);
            return (anim != null) ? anim.getKeyFrame(stateTime, true) : assets.getHairIdleFrame(hairType, dir);
        } else {
            return assets.getHairIdleFrame(hairType, dir);
        }
    }

    private TextureRegion getClothingFrameForRender(boolean moving, ClothesContainer clothing, CharecterAssets.Direction dir) {
        if (moving) {
            Animation<TextureRegion> anim = assets.getClothingWalkAnimation(clothing, dir);
            return (anim != null) ? anim.getKeyFrame(stateTime, true) : assets.getClothingIdleFrame(clothing, dir);
        } else {
            return assets.getClothingIdleFrame(clothing, dir);
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

      /**
     * Sets the current map name and updates the character's scale accordingly.
     * This method contains the logic for which maps use a different scale.
     */
    public void setCurrentMapName(String currentMapName) {
        this.currentMapName = currentMapName;

        switch (currentMapName) {
            case "interior2":
            case "interior":
                // For the 'interior' map, make the character 50% larger.
                this.scale = 4f;
                this.speed = this.speed*4;
                System.out.println("[ Character] Applied scaling factor = "+ scale + "in the new map");
                break;

            case "Tile_City":
                // For the 'interior' map, make the character 50% larger.
                this.scale = 1f;
                this.speed = 100f;
                System.out.println("[ Character] Applied scaling factor = "+ scale + "in the new map");
                break;
                
            // --- FUTURE: Add other maps that need scaling here ---
            
            case "office/office":
                this.scale = 1.8f;
                break;
            
            
            default:
                // For all other maps, use the default scale.
                this.scale = 1.0f;
                break;
        }
    }

}