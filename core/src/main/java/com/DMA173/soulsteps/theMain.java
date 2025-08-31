package com.DMA173.soulsteps;

import com.badlogic.gdx.Game;

public class theMain extends Game {
    @Override
    public void create() {
        // The game now starts on a dedicated main menu screen.
        setScreen(new MainMenuScreen(this));
    }
}