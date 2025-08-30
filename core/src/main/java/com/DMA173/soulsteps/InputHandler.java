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

    public InputHandler(OrthographicCamera camera, Player player, UIManager uiManager, WorldManager worldManager) { // <-- CHANGED
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
    }

    // ... (keep all other methods like handlePlayerMovement, handlePauseMenuInput, etc. exactly as they were in your original file)
    private void handlePauseMenuInput() { /* ... your original code ... */ }
    private void handleUIInput() { /* ... your original code ... */ }
    private void handlePlayerMovement(float delta) { /* ... your original code ... */ }
    private void handleCameraControls(float delta) { /* ... your original code ... */ }
    public boolean isPaused() { return uiManager.isPaused(); }
}