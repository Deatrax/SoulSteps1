package com.DMA173.soulsteps.ui;

import com.DMA173.soulsteps.story.GameState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ObjectiveUI {
    private BitmapFont font;

    public ObjectiveUI() {
        font = new BitmapFont();
    }

    public void render(SpriteBatch batch, GameState gameState) {
        String objectiveText = "Current Chapter: " + gameState.getCurrentChapter().name();
        
        // In a real game, you would get a list of active objectives
        // from the WorldManager and display them here.
        
        font.draw(batch, objectiveText, 10, Gdx.graphics.getHeight() - 10);
    }
    
    public void dispose() { font.dispose(); }
}