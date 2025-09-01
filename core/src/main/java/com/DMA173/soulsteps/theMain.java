package com.DMA173.soulsteps;

import com.badlogic.gdx.Game;

/** 
 * Updated main class that properly handles screen transitions.
 * Starts with menu screen and properly manages game flow.
 */
public class theMain extends Game {
    @Override
    public void create() {
        // Start with the menu screen
        setScreen(new MenuScreen(this));
        System.out.println("SoulSteps game started with new menu system");

        ///setScreen(new ObjectFinder(this));
        ///setScreen(new pipepuzzle());
    }
}
