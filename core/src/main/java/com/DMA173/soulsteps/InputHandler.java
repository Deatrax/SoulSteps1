package com.DMA173.soulsteps;

import com.DMA173.soulsteps.Charecters.CharecterAssets;
import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.ui.UIManager;
import com.DMA173.soulsteps.world.WorldManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class InputHandler extends InputAdapter {
    private FirstScreen gameScreen;
    private Player player;
    private WorldManager worldManager;
    private UIManager uiManager;

    public InputHandler(FirstScreen screen, OrthographicCamera camera, Player player, UIManager uiManager, WorldManager worldManager) {
        this.gameScreen = screen;
        this.player = player;
        this.worldManager = worldManager;
        this.uiManager = uiManager;
    }

    // This method is for continuous actions like movement
    public void handleContinuousInput(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) player.moveInDirection(CharecterAssets.Direction.UP, delta);
        if (Gdx.input.isKeyPressed(Input.Keys.S)) player.moveInDirection(CharecterAssets.Direction.DOWN, delta);
        if (Gdx.input.isKeyPressed(Input.Keys.A)) player.moveInDirection(CharecterAssets.Direction.LEFT, delta);
        if (Gdx.input.isKeyPressed(Input.Keys.D)) player.moveInDirection(CharecterAssets.Direction.RIGHT, delta);
    }

    // This method is for single press actions
    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            gameScreen.setPaused(true);
            return true;
        }
        if (keycode == Input.Keys.E) {
            worldManager.handleInteraction(player);
            return true;
        }
        return false;
    }
}