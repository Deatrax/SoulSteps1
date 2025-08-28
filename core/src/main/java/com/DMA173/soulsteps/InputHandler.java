package com.DMA173.soulsteps;

import com.DMA173.soulsteps.Charecters.NPC;
import com.DMA173.soulsteps.Charecters.NPCManager;
import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.UI.UIManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Handles all input for the game including player movement, UI interactions, and game controls.
 * Demonstrates separation of concerns by isolating input handling from rendering and game logic.
 */
public class InputHandler {
    private OrthographicCamera camera;
    private Player player;
    private UIManager uiManager;
    private NPCManager npcManager;
    
    // Input state tracking
    private boolean escapePressed = false;
    private boolean f3Pressed = false;
    private boolean ePressed = false;
    
    public InputHandler(OrthographicCamera camera, Player player, UIManager uiManager, NPCManager npcManager) {
        this.camera = camera;
        this.player = player;
        this.uiManager = uiManager;
        this.npcManager = npcManager;
    }
    
    public void handleInput(float delta) {
        // If game is paused, only handle pause-related inputs
        if (uiManager.isPaused()) {
            handlePauseMenuInput();
            return;
        }
        
        // Handle UI inputs first
        handleUIInput();
        
        // Handle player movement (only if not paused)
        handlePlayerMovement(delta);
        
        // Handle camera controls
        handleCameraControls(delta);
        
        // Handle interaction
        handleInteractions();
    }
    
    /**
     * Handle inputs when game is paused
     */
    private void handlePauseMenuInput() {
        // ESC - Resume game
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            uiManager.setPaused(false);
            System.out.println("Game resumed");
        }
        
        // R - Restart level (placeholder)
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            System.out.println("Restart level - Not implemented yet");
            // TODO: Implement level restart
        }
        
        // Q - Quit to menu (placeholder)
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            System.out.println("Quit to menu - Not implemented yet");
            // TODO: Implement quit to main menu
        }
    }
    
    /**
     * Handle UI-related inputs
     */
    private void handleUIInput() {
        // ESC - Toggle pause menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            uiManager.togglePause();
            return;
        }
        
        // F3 - Toggle debug mode
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            uiManager.toggleDebugMode();
        }
        
        // I - Toggle inventory (placeholder)
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            System.out.println("Inventory opened - Not implemented yet");
            // TODO: Implement inventory system
        }
        
        // M - Toggle map (placeholder)
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            System.out.println("Map opened - Not implemented yet");
            // TODO: Implement map system
        }
        
        // Tab - Toggle objective list (placeholder)
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            System.out.println("Objective list - Not implemented yet");
            // TODO: Show detailed objective list
        }
    }
    
    /**
     * Handle player movement inputs
     */
    private void handlePlayerMovement(float delta) {
        boolean moved = false;
        
        // WASD movement controls
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            player.moveInDirection(com.DMA173.soulsteps.Charecters.CharecterAssets.Direction.UP, delta);
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            player.moveInDirection(com.DMA173.soulsteps.Charecters.CharecterAssets.Direction.DOWN, delta);
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.moveInDirection(com.DMA173.soulsteps.Charecters.CharecterAssets.Direction.LEFT, delta);
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.moveInDirection(com.DMA173.soulsteps.Charecters.CharecterAssets.Direction.RIGHT, delta);
            moved = true;
        }
        
        player.setMoving(moved);
    }
    
    /**
     * Handle camera control inputs
     */
    private void handleCameraControls(float delta) {
        // Zoom controls
        if (Gdx.input.isKeyPressed(Input.Keys.PLUS) || Gdx.input.isKeyPressed(Input.Keys.EQUALS)) {
            camera.zoom = Math.max(0.1f, camera.zoom - 0.02f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
            camera.zoom = Math.min(2.0f, camera.zoom + 0.02f);
        }
        
        // Reset zoom
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
            camera.zoom = 0.5f;
            System.out.println("Camera zoom reset");
        }
    }
    
    /**
     * Handle interaction inputs
     */
    private void handleInteractions() {
        // E - Interact with NPCs
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            boolean interacted = npcManager.handleInteraction(player);
            if (interacted) {
                uiManager.setInteractionHint(""); // Clear hint after interaction
            } else {
                uiManager.showNotification("Nothing to interact with here");
            }
        }
        
        // Check for nearby NPCs to show interaction hint
        updateInteractionHints();
    }
    
    /**
     * Update interaction hints based on nearby NPCs
     */
    private void updateInteractionHints() {
        // Check if there's an NPC nearby
        boolean nearbyNPC = false;
        for (NPC npc : npcManager.getAllNPCs()) {
            float distance = npc.getPosition().dst(player.getPosition());
            if (distance <= 50f && npc.isInteractable()) {
                nearbyNPC = true;
                uiManager.setInteractionHint("Press E to talk to " + npc.getName());
                break;
            }
        }
        
        if (!nearbyNPC) {
            uiManager.clearInteractionHint();
        }
    }
    
    /**
     * Handle special debug/cheat inputs (remove in final build)
     */
    private void handleDebugInputs() {
        // Only process debug inputs if debug mode is on
        if (!Gdx.input.isKeyPressed(Input.Keys.F3)) return;
        
        // Shift+H - Heal player
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            player.setHealth(100);
            uiManager.showNotification("Health restored");
        }
        
        // Shift+K - Restore kindness
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            player.adjustKindness(100);
            uiManager.showNotification("Kindness restored");
        }
        
        // Shift+E - Add evidence
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            player.collectEvidence();
            uiManager.showNotification("Evidence added");
        }
        
        // Number keys 1-6 - Change player outfit
        for (int i = 1; i <= 6; i++) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i - 1)) {
                player.changeOutfit(i);
                uiManager.showNotification("Changed to outfit " + i);
            }
        }
        
        // Shift + Number keys 1-6 - Change player hair
        for (int i = 1; i <= 6; i++) {
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i - 1)) {
                player.changeHairstyle(i);
                uiManager.showNotification("Changed to hairstyle " + i);
            }
        }
    }
    
    // Getters for external access
    public boolean isPaused() {
        return uiManager.isPaused();
    }
}