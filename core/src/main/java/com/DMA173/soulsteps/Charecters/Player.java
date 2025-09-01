package com.DMA173.soulsteps.Charecters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/**
 * The Player class represents Elian, the protagonist of SoulSteps.
 * Now supports the hair and clothing system.
 * Demonstrates inheritance and polymorphism by extending Character.
 */
public class Player extends Character {
    
    // --- SoulSteps-specific player properties ---
    private int kindnessLevel;
    private boolean dangerZoneActive;
    private int maxKindness;
    
    // --- Player inventory and interaction ---
    private boolean hasWaterLimiter;
    private int evidenceCount;

    // MERGED: Add variables for collision detection
    private TiledMapTileLayer collisionLayer;
    
    public Player(CharecterAssets assets, float startX, float startY) {
        // Use character type 3 for Elian's appearance with specific hair and clothing configuration
        // ClothesContainer(hairType, outfitType, modelType, accessoryType)
        super(assets, 1, startX, startY, 100f, new ClothesContainer(3, 6, 3, 6));
        
        // Initialize Elian's properties
        this.name = "Elian";
        this.kindnessLevel = 100;
        this.maxKindness = 100;
        this.dangerZoneActive = false;
        this.hasWaterLimiter = false;
        this.evidenceCount = 0;
        this.isInteractable = false; // Player doesn't interact with himself
    }
    
    /**
     * Constructor with clothing and hair
     */
    public Player(CharecterAssets assets, float startX, float startY, ClothesContainer clothes) {
        // Use character type 3 for Elian's appearance with specified clothing and hair
        super(assets, 3, startX, startY, 100f, clothes);
        
        // Initialize Elian's properties
        this.name = "Elian";
        this.kindnessLevel = 100;
        this.maxKindness = 100;
        this.dangerZoneActive = false;
        this.hasWaterLimiter = false;
        this.evidenceCount = 0;
        this.isInteractable = false; // Player doesn't interact with himself
    }

    // MERGED: A new method to give the player the collision layer from FirstScreen
    public void setCollisionLayer(TiledMapTileLayer collisionLayer) {
        this.collisionLayer = collisionLayer;
    }
    
    /**
     * Implements the abstract update method with player-specific behavior
     * Demonstrates polymorphism
     */
    @Override
    public void update(float delta) {
        // We no longer call handleInput from here, as the movement logic is now combined
        // to include collision checks.
        updateStateTime(delta);
        
        // MERGED: The movement logic is now inside the player's update loop
        handleMovementWithCollision(delta);

        updateKindnessBar();
    }
    

    /*  Old handleInput method
    private void handleInput(float delta) {
        boolean moved = false;
        
        // WASD movement controls
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            moveInDirection(CharecterAssets.Direction.UP, delta);
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            moveInDirection(CharecterAssets.Direction.DOWN, delta);
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            moveInDirection(CharecterAssets.Direction.LEFT, delta);
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            moveInDirection(CharecterAssets.Direction.RIGHT, delta);
            moved = true;
        }
        
        this.setMoving(moved);
    }
    */


    // MERGED: This new method replaces your old `handleInput` and `moveInDirection` methods.
    // It combines movement input with collision checking.
    private void handleMovementWithCollision(float delta) {
        boolean movedX = false;
        boolean movedY = false;
        
        float oldX = position.x;
        float oldY = position.y;
        
        // --- X-axis movement ---
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            position.x -= speed * delta;
            setCurrentDir(CharecterAssets.Direction.LEFT);
            movedX = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            position.x += speed * delta;
            setCurrentDir(CharecterAssets.Direction.RIGHT);
            movedX = true;
        }
        
        // Check for X-axis collision
        if (isCellBlocked(position.x, oldY)) {
            position.x = oldX; // If blocked, revert X movement
        }

        // --- Y-axis movement ---
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            position.y += speed * delta;
            setCurrentDir(CharecterAssets.Direction.UP);
            movedY = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            position.y -= speed * delta;
            setCurrentDir(CharecterAssets.Direction.DOWN);
            movedY = true;
        }
        
        // Check for Y-axis collision
        if (isCellBlocked(position.x, position.y)) {
            position.y = oldY; // If blocked, revert Y movement
        }

        // Update animation state
        this.setMoving(movedX || movedY);
    }

    // MERGED: Collision checking logic from your teammate, adapted for this class
    private boolean isCellBlocked(float x, float y) {
        if (collisionLayer == null) {
            return false; // If there's no collision layer, nothing is blocked
        }

        float tileWidth = collisionLayer.getTileWidth();
        float tileHeight = collisionLayer.getTileHeight();

        // Check the four corners of the player's bounding box for simplicity
        // You can make this more precise later if needed
        float playerWidth = 16; // Approximate width of your character sprite
        float playerHeight = 30; // Approximate height

        boolean bottomLeft = isTileBlocked(x - playerWidth / 2, y);
        boolean bottomRight = isTileBlocked(x + playerWidth / 2, y);
        boolean topLeft = isTileBlocked(x - playerWidth / 2, y + playerHeight);
        boolean topRight = isTileBlocked(x + playerWidth / 2, y + playerHeight);

        return bottomLeft || bottomRight || topLeft || topRight;
    }


    private boolean isTileBlocked(float x, float y) {
        int cellX = (int) (x / collisionLayer.getTileWidth());
        int cellY = (int) (y / collisionLayer.getTileHeight());

        TiledMapTileLayer.Cell cell = collisionLayer.getCell(cellX, cellY);
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("blocked");
    }
    
    
    // --- SoulSteps-specific methods ---
    public void adjustKindness(int amount) {
        kindnessLevel = Math.max(0, Math.min(maxKindness, kindnessLevel + amount));
    }
    
    private void updateKindnessBar() {
        // Trigger danger zone if kindness drops below 30%
        dangerZoneActive = (kindnessLevel < maxKindness * 0.3f);
    }
    
    public void makeEthicalChoice(String choice, int kindnessImpact) {
        adjustKindness(kindnessImpact);
        System.out.println("Elian made choice: " + choice + " (Kindness: " + kindnessLevel + ")");
    }
    
    public void collectEvidence() {
        evidenceCount++;
        System.out.println("Evidence collected! Total: " + evidenceCount);
    }
    
    public void findWaterLimiter() {
        if (!hasWaterLimiter) {
            hasWaterLimiter = true;
            adjustKindness(10); // Finding evidence increases kindness
            System.out.println("Elian found a water limiter device!");
        }
    }
    
    // --- Hair-specific methods for Player ---
    public void changeHairstyle(int hairType) {
        changeHair(hairType);
        System.out.println("Elian changed hairstyle to type " + hairType);
    }
    
    public void removeHairstyle() {
        //removeHair();
        System.out.println("Elian removed hair (bald look)");
    }
    
    // --- Clothing-specific methods for Player ---
    public void equipWorkOutfit() {
        changeOutfit(5); // Outfit5.png - shirt and pants for men
        System.out.println("Elian equipped work outfit");
    }
    
    public void equipPoliceSuit() {
        changeOutfit(101); // Suit.png row 0 - police
        System.out.println("Elian equipped police suit for investigation");
    }
    
    public void equipDeliveryUniform() {
        changeOutfit(104); // Suit.png row 3 - delivery
        System.out.println("Elian equipped delivery uniform for disguise");
    }
    
    public void equipJacket() {
        changeOutfit(2); // Outfit2.png - coat/jacket
        System.out.println("Elian put on a jacket");
    }
    
    public void goUndercover() {
        // Change to office suit for blending in at Veridia Corporation
        changeOutfit(103); // Suit.png row 2 - office suit
        System.out.println("Elian changed into office attire for undercover work");
    }
    
    public void changeToCasualWear() {
        changeOutfit(6); // Outfit6.png - shirt and pants for men (variant 2)
        System.out.println("Elian changed to casual wear");
    }
    
    // --- Complete appearance change methods ---
    public void changeCompleteAppearance(int hairType, int outfitType) {
        changeHair(hairType);
        changeOutfit(outfitType);
        System.out.println("Elian changed complete appearance: Hair " + hairType + ", Outfit " + outfitType);
    }
    
    public void changeCompleteAppearance(ClothesContainer newClothes) {
        changeAppearance(newClothes);
        System.out.println("Elian changed complete appearance: " + newClothes.toString());
    }
    
    // --- Disguise methods for story events ---
    public void disguiseAsOfficeWorker() {
        // Professional look for infiltrating Veridia Corporation
        changeCompleteAppearance(2, 103); // Professional hair, office suit
        System.out.println("Elian disguised as office worker");
    }
    
    public void disguiseAsDeliveryPerson() {
        // Casual look for delivery disguise
        changeCompleteAppearance(1, 104); // Casual hair, delivery uniform
        System.out.println("Elian disguised as delivery person");
    }
    
    public void disguiseAsPoliceOfficer() {
        // Official look for police infiltration
        changeCompleteAppearance(1, 101); // Short hair, police uniform
        System.out.println("Elian disguised as police officer");
    }
    
    public void returnToNormalAppearance() {
        // Return to Elian's default appearance
        changeCompleteAppearance(new ClothesContainer(1, 4, 1, 6));
        System.out.println("Elian returned to normal appearance");
    }
    
    // --- Getters for game state ---
    public int getKindnessLevel() { return kindnessLevel; }
    public boolean isDangerZoneActive() { return dangerZoneActive; }
    public int getEvidenceCount() { return evidenceCount; }
    public boolean hasWaterLimiter() { return hasWaterLimiter; }
    
    public float getKindnessPercentage() {
        return (float) kindnessLevel / maxKindness;
    }
}