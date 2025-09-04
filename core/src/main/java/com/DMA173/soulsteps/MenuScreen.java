package com.DMA173.soulsteps;

import com.DMA173.soulsteps.ui.MainMenu;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;

/**
 * Updated MenuScreen that uses the new MainMenu system.
 * Much cleaner and properly handles transitions.
 */
public class MenuScreen extends ScreenAdapter {
    private Game game;
    private MainMenu mainMenu;
    
    public MenuScreen(Game game) {
        this.game = game;
    }
    
    @Override
    public void show() {
        mainMenu = new MainMenu(game);
        System.out.println("Menu Screen initialized with new menu system");
    }
    
    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Update menu logic
        if (mainMenu != null) {
            mainMenu.update(delta);
            
            // Check if we should transition to game
            if (mainMenu.shouldTransitionToGame()) {
                System.out.println("Transitioning to game...");
                game.setScreen(new pipepuzzle()); // Pass game reference
                return;
            }
            
            // Render menu
            mainMenu.render();
        }
    }
    
    @Override
    public void resize(int width, int height) {
        if (mainMenu != null) {
            mainMenu.resize(width, height);
        }
    }
    
    @Override
    public void dispose() {
        if (mainMenu != null) {
            mainMenu.dispose();
        }
    }
}