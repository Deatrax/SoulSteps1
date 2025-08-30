package com.DMA173.soulsteps;

import com.badlogic.gdx.Game;

/** 
 * Updated main class to start with menu screen instead of directly into game.
 * The menu will handle transitioning to the game screen.
 */
public class theMain extends Game {
    @Override
    public void create() {
        // Start with the menu screen instead of directly into the game
        setScreen(new MenuScreen(this));
    }
}