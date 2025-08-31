package com.DMA173.soulsteps;

import com.DMA173.soulsteps.ui.mainMenuBackend;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;

/**
 * Screen that handles all menu rendering and input.
 * Transitions to game screen when "New Game" or "Continue" is selected.
 */
public class MenuScreen extends ScreenAdapter {
    private Game game;
    private mainMenuBackend menuBackend;
    
    public MenuScreen(Game game) {
        this.game = game;
    }
    
    @Override
    public void show() {
        menuBackend = new mainMenuBackend();
        System.out.println("Menu Screen initialized");
    }
    
    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Update menu logic
        menuBackend.update(delta);
        
        // Check if we should transition to game
        if (shouldTransitionToGame()) {
            game.setScreen(new FirstScreen());
            return;
        }
        
        // Render menu
        menuBackend.render();
    }
    
    /**
     * Check if the menu manager indicates we should start the game.
     * This will be triggered when user selects "New Game" or "Continue".
     */
    private boolean shouldTransitionToGame() {
        // TODO: Add proper game state checking here
        // For now, we'll check if the menu was hidden (which happens when game starts)
        return !menuBackend.isMenuActive();
    }
    
    @Override
    public void resize(int width, int height) {
        if (menuBackend != null) {
            menuBackend.resize(width, height);
        }
    }
    
    @Override
    public void dispose() {
        if (menuBackend != null) {
            menuBackend.dispose();
        }
    }
}