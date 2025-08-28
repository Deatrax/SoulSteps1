package com.DMA173.soulsteps.Charecters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * The Player class represents Elian, the protagonist of SoulSteps.
 * Now supports the clothing system.
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
    
    public Player(CharecterAssets assets, float startX, float startY) {
        // Use character type 3 for Elian's appearance with specific clothing configuration
        // ClothesContainer(hairType, outfitType, modelType, accessoryType)
        super(assets, 3, startX, startY, 100f, new ClothesContainer(1, 1, 1, 6));
        
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
     * Constructor with clothing
     */
    public Player(CharecterAssets assets, float startX, float startY, ClothesContainer clothes) {
        // Use character type 3 for Elian's appearance with specified clothing
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
    
    /**
     * Implements the abstract update method with player-specific behavior
     * Demonstrates polymorphism
     */
    @Override
    public void update(float delta) {
        updateStateTime(delta);
        handleInput(delta);
        updateKindnessBar();
    }
    
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
    
    // --- Getters for game state ---
    public int getKindnessLevel() { return kindnessLevel; }
    public boolean isDangerZoneActive() { return dangerZoneActive; }
    public int getEvidenceCount() { return evidenceCount; }
    public boolean hasWaterLimiter() { return hasWaterLimiter; }
    
    public float getKindnessPercentage() {
        return (float) kindnessLevel / maxKindness;
    }

}