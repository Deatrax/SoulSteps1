package com.DMA173.soulsteps;

import com.DMA173.soulsteps.Charecters.NPC;
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
    private GameState gameState;

    public InputHandler(OrthographicCamera camera, Player player, WorldManager worldManager, DialogueUI dialogueUI) {
        this.camera = camera;
        this.player = player;
        this.worldManager = worldManager;
        this.dialogueUI = dialogueUI;
        this.gameState = GameState.getInstance();
    }

    @Override
    public boolean keyDown(int keycode) {
        // If the dialogue UI is active, it gets first priority for input.
        // This is the fix for the freeze.
        if (dialogueUI.isActive()) {
            dialogueUI.handleInput(keycode);
            return true;
        }

        // If dialogue is not active, handle game world input.
        switch (keycode) {
            case Input.Keys.E:
                NPC targetNpc = worldManager.findNearbyNpc(player.getPosition(), 60f);
                if (targetNpc != null) {
                    // Tell the NPC to handle its own interaction logic.
                    targetNpc.interact(gameState, dialogueUI, player);
                }
                return true;
            // Add other controls like ESC for pause menu here if needed.
        }
        return false;
    }
    
    @Override
    public boolean scrolled(float amountX, float amountY) {
        camera.zoom += amountY * 0.1f;
        camera.zoom = Math.max(0.2f, Math.min(2.0f, camera.zoom));
        return true;
    }
}