package com.DMA173.soulsteps;

import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.story.GameState;
import com.DMA173.soulsteps.ui.DialogueUI;
import com.DMA173.soulsteps.world.WorldManager;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class InputHandler extends InputAdapter {
    private OrthographicCamera camera;
    private Player player;
    private WorldManager worldManager;
    private DialogueUI dialogueUI;

    public InputHandler(OrthographicCamera camera, Player player, WorldManager worldManager, DialogueUI dialogueUI) {
        this.camera = camera;
        this.player = player;
        this.worldManager = worldManager;
        this.dialogueUI = dialogueUI;
    }

    @Override
    public boolean keyDown(int keycode) {
        // If the dialogue UI is active, it consumes the input first
        if (dialogueUI.isActive()) {
            dialogueUI.handleInput(keycode);
            return true;
        }

        // --- Game Controls ---
        if (keycode == Input.Keys.E) {
            worldManager.handleInteraction();
            return true;
        }

        // --- Debug Controls ---
        if (keycode == Input.Keys.F5) {
            System.out.println("DEBUG: Current Chapter: " + GameState.getInstance().getCurrentChapter());
        }
        if (keycode == Input.Keys.F7) {
             System.out.println("DEBUG: Unlocking residential_area");
             GameState.getInstance().unlockZone("residential_area");
        }

        return false; // Input not handled
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        // Zoom with mouse wheel
        camera.zoom += amountY * 0.1f;
        camera.zoom = Math.max(0.2f, Math.min(2.0f, camera.zoom));
        return true;
    }
}