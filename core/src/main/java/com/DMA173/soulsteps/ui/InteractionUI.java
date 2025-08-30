package com.DMA173.soulsteps.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

/**
 * A simple UI component dedicated to drawing the "Press E to Interact" prompt.
 */
public class InteractionUI {
    private BitmapFont font;
    private GlyphLayout layout;
    private String promptText; // The text to be displayed, e.g., "[E] Talk to Lena"

    public InteractionUI() {
        font = new BitmapFont(); // Using default LibGDX font for now
        layout = new GlyphLayout();
    }

    /**
     * The main game screen will call this method every frame to tell the UI what to draw.
     * If the text is null, nothing will be drawn.
     * @param text The text for the prompt.
     */
    public void setPromptText(String text) {
        this.promptText = text;
    }

    public void render(SpriteBatch batch) {
        // Only draw if there is text to display
        if (promptText == null || promptText.isEmpty()) {
            return;
        }

        float screenWidth = Gdx.graphics.getWidth();
        
        // Use GlyphLayout to calculate the width of the text to center it
        layout.setText(font, promptText, com.badlogic.gdx.graphics.Color.WHITE, screenWidth, Align.center, false);
        
        // Draw the text at the top-center of the screen
        float x = (screenWidth - layout.width) / 2;
        float y = Gdx.graphics.getHeight() - 20; // 20 pixels from the top

        font.draw(batch, layout, x, y);
    }

    public void dispose() {
        font.dispose();
    }
}