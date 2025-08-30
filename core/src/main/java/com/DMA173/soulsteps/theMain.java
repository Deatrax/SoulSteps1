// In theMain.java

package com.DMA173.soulsteps;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class theMain extends Game {
    @Override
    public void create() {
        // --- CHANGE THIS LINE ---
        // Old: setScreen(new FirstScreen());
        // New:
        setScreen(new MainMenuScreen(this)); 
    }
}