package com.DMA173.soulsteps.Charecters;

import com.DMA173.soulsteps.story.GameState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class Player extends Character {
    
    private int kindnessLevel;
    private int maxKindness;
    private boolean dangerZoneActive;
    
    public Player(CharecterAssets assets, float startX, float startY) {
        super(assets, 3, startX, startY, 100f, new ClothesContainer(1, 4)); // Default appearance
        this.name = "Elian";
        this.kindnessLevel = 100;
        this.maxKindness = 100;
        this.dangerZoneActive = false;
        this.isInteractable = false;
    }
    
    @Override
    public void update(float delta) {
        updateStateTime(delta);
        handleInput(delta);
        updateKindnessBar();
    }
    
    private void handleInput(float delta) {
        boolean moved = false;
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
    
    public void adjustKindness(int amount) {
        kindnessLevel = Math.max(0, Math.min(maxKindness, kindnessLevel + amount));
        System.out.println("Kindness is now: " + kindnessLevel);
    }
    
    private void updateKindnessBar() {
        boolean previousDangerState = dangerZoneActive;
        dangerZoneActive = (kindnessLevel < maxKindness * 0.3f);
        
        if (dangerZoneActive && !previousDangerState) {
            GameState.getInstance().setFlag("entered_danger_zone", true);
            System.out.println("DANGER ZONE ACTIVE: Veridia is now hostile!");
        }
    }

    public int getKindnessLevel() { return kindnessLevel; }
    public boolean isDangerZoneActive() { return dangerZoneActive; }
}