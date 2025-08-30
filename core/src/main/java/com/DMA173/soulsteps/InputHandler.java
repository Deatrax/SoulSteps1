package com.DMA173.soulsteps;

import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.ui.UIManager;
import com.DMA173.soulsteps.world.WorldManager; // <-- CHANGE IMPORT
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class InputHandler {
    private OrthographicCamera camera;
    private Player player;
    private UIManager uiManager;
    private WorldManager worldManager; // <-- CHANGED from NPCManager
    private Boolean debugMode = true;

    public InputHandler(OrthographicCamera camera, Player player, UIManager uiManager, WorldManager worldManager) { // <--
                                                                                                                    // CHANGED
        this.camera = camera;
        this.player = player;
        this.uiManager = uiManager;
        this.worldManager = worldManager; // <-- CHANGED
    }

    public void handleInput(float delta) {
        if (uiManager.isPaused()) {
            handlePauseMenuInput();
            return;
        }

        handleUIInput();
        handlePlayerMovement(delta);
        handleCameraControls(delta);
        handleInteractions(); // This method now uses worldManager

        if(debugMode){
            handleDebugControls();
        }
    }

    private void handleInteractions() {
        // Interaction press
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            boolean interacted = worldManager.handleInteraction(player); // <-- CHANGED
            if (interacted) {
                uiManager.setInteractionHint("");
            } else {
                uiManager.showNotification("Nothing to interact with here.");
            }
        }

        // Interaction hint update
        String hint = worldManager.getInteractionHint(player); // <-- CHANGED
        if (hint != null) {
            uiManager.setInteractionHint(hint);
        } else {
            uiManager.clearInteractionHint();
        }

        // Inventory key
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            handleInventoryInput();
        }

        // Pause/Menu key
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            handlePauseInput();
        }

        // Toggle debug mode
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            debugMode = !debugMode;
            System.out.println("Debug mode: " + (debugMode ? "ON" : "OFF"));
        }
    }

    /**
     * Handle inventory input (I key)
     */
    private void handleInventoryInput() {
        System.out.println("Inventory key pressed - opening inventory");
        // TODO: Implement inventory system
        // - Show inventory UI
        // - Display collected items and evidence
    }

    /**
     * Handle pause/menu input (Escape key)
     */
    private void handlePauseInput() {
        System.out.println("Escape pressed - opening pause menu");
        // TODO: Implement pause menu
        // - Show pause menu UI
        // - Pause game logic
        // - Provide save/load options
    }

    /**
     * Debug method to print all player statistics
     */
    private void printPlayerStats() {
        System.out.println("=== PLAYER STATS ===");
        System.out.println("Name: " + player.getName());
        System.out.println("Position: (" + player.getPosition().x + ", " + player.getPosition().y + ")");
        System.out.println("Kindness: " + player.getKindnessLevel() + " (" +
                String.format("%.1f", player.getKindnessPercentage() * 100) + "%)");
        System.out.println("Evidence Count: " + player.getEvidenceCount());
        System.out.println("Has Water Limiter: " + player.hasWaterLimiter());
        System.out.println("Danger Zone Active: " + player.isDangerZoneActive());
        System.out.println("Speed: " + player.getSpeed());
        System.out.println("==================");
    }

    // ... (keep all other methods like handlePlayerMovement, handlePauseMenuInput,
    // etc. exactly as they were in your original file)
    private void handlePauseMenuInput() {
        /* ... your original code ... */ 
    }

    private void handleUIInput() {
        /* ... your original code ... */ 
    }

    private void handlePlayerMovement(float delta) {
        /* ... your original code ... */ 
    }

    private void handleCameraControls(float delta) {
        // Zoom controls
        if (Gdx.input.isKeyPressed(Input.Keys.PLUS) || Gdx.input.isKeyPressed(Input.Keys.EQUALS)) {
            camera.zoom -= 0.01f;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
            camera.zoom += 0.01f;
        }

        // Clamp zoom to reasonable values
        camera.zoom = Math.max(0.2f, Math.min(2.0f, camera.zoom));
    }

    public boolean isPaused() {
        return uiManager.isPaused();
    }

    /**
     * Handle interaction input (E key)
     */
    private void handleInteractionInput() {
        System.out.println("Interaction key pressed - looking for nearby objects/NPCs");
        // TODO: Implement interaction system
        // - Check for nearby NPCs
        // - Check for interactable objects
        // - Trigger dialogue or object interaction
    }

    /**
     * Handles debug-only input (remove in final version)
     */
    private void handleDebugControls() {
        // Debug: Test kindness adjustment
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            player.adjustKindness(10);
            System.out.println("DEBUG: Kindness increased! Current: " + player.getKindnessLevel());

            if (player.isDangerZoneActive()) {
                System.out.println("DEBUG: DANGER ZONE ACTIVE!");
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.J)) {
            player.adjustKindness(-10);
            System.out.println("DEBUG: Kindness decreased! Current: " + player.getKindnessLevel());

            if (player.isDangerZoneActive()) {
                System.out.println("DEBUG: DANGER ZONE ACTIVE!");
            }
        }

        // Debug: Test evidence collection
        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            player.collectEvidence();
            System.out.println("DEBUG: Evidence collected! Total: " + player.getEvidenceCount());
        }

        // Debug: Test water limiter discovery
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            player.findWaterLimiter();
        }

        // Debug: Print player stats
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            printPlayerStats();
        }
    }

}
