package com.DMA173.soulsteps.Charecters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * The Player class represents Elian, the protagonist of SoulSteps.
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
        // Use character type 0 for Elian's appearance
        super(assets, 0, startX, startY, 100f);
        
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
    
    // --- Getters for game state ---
    public int getKindnessLevel() { return kindnessLevel; }
    public boolean isDangerZoneActive() { return dangerZoneActive; }
    public int getEvidenceCount() { return evidenceCount; }
    public boolean hasWaterLimiter() { return hasWaterLimiter; }
    
    public float getKindnessPercentage() {
        return (float) kindnessLevel / maxKindness;
    }
}
