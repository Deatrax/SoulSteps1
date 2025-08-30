package com.DMA173.soulsteps;

import com.badlogic.gdx.Game;

public class theMain extends Game {
    @Override
    public void create() {
        // This now starts your game on the Main Menu screen.
        setScreen(new MainMenuScreen(this));
    }
}