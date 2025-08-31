package com.DMA173.soulsteps;

import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.ui.UIManager;
import com.DMA173.soulsteps.world.WorldManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Updated InputHandler that works properly with the new menu system.
 * No longer manages its own menu backend.
 */
public class InputHandler {
    private OrthographicCamera camera;
    private Player player;
    private UIManager uiManager;
    private WorldManager worldManager;
    private Boolean debugMode = true;

    public InputHandler(OrthographicCamera camera, Player player, UIManager uiManager, WorldManager worldManager) {
        this.camera = camera;
        this.player = player;
        this.uiManager = uiManager;
        this.worldManager = worldManager;
    }

    public void handleInput(float delta) {
        // Check if any menu is active - if so, don't handle game input
        if (uiManager.isAnyMenuActive()) {
            return; // Let the menu system handle all input
        }

        handleUIInput();
        handlePlayerMovement(delta);
        handleCameraControls(delta);
        handleInteractions();

        if(debugMode){
            handleDebugControls();
        }
    }

    private void handleInteractions() {
        // Interaction press
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            boolean interacted = worldManager.handleInteraction(player);
            if (interacted) {
                uiManager.setInteractionHint("");
            } else {
                uiManager.showNotification("Nothing to interact with here.");
            }
        }

        // Interaction hint update
        String hint = worldManager.getInteractionHint(player);
        if (hint != null) {
            uiManager.setInteractionHint(hint);
        } else {
            uiManager.clearInteractionHint();
        }

        // Inventory key
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            handleInventoryInput();
        }

        // Pause/Menu key - Now properly shows pause menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            uiManager.showPauseMenu();
        }

        // Toggle debug mode
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            debugMode = !debugMode;
            uiManager.toggleDebugMode();
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

    private void handleUIInput() {
        // Additional UI controls can go here
    }

    private void handlePlayerMovement(float delta) {
        // Player movement is now handled in the Player class itself
        // This method is kept for potential future movement restrictions or special cases
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
        return uiManager.isAnyMenuActive();
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

        // Debug: Show pause menu (alternative to ESC)
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            uiManager.showPauseMenu();
        }
    }
}