package com.DMA173.soulsteps.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.function.Consumer;

public class DialogueUI {
    private boolean active = false;
    private String currentText;
    private String[] choices;
    private Consumer<Integer> choiceCallback;
    private BitmapFont font;
    private GlyphLayout layout;

    public DialogueUI() {
        font = new BitmapFont();
        layout = new GlyphLayout();
    }

    public void showNarration(String text) {
        this.currentText = text;
        this.choices = null;
        this.active = true;
    }

    public void showChoice(String prompt, String[] choices, Consumer<Integer> callback) {
        this.currentText = prompt;
        this.choices = choices;
        this.choiceCallback = callback;
        this.active = true;
    }
    
    public void handleInput(int keycode) {
        if (!active) return;

        if (choices != null) {
            if (keycode >= Input.Keys.NUM_1 && keycode <= Input.Keys.NUM_9) {
                int choiceIndex = keycode - Input.Keys.NUM_1;
                if (choiceIndex < choices.length) {
                    choiceCallback.accept(choiceIndex + 1);
                    active = false;
                }
            }
        } else {
            // Any key press closes narration
            active = false;
        }
    }

    public void render(SpriteBatch batch) {
        if (!active) return;
        
        float width = Gdx.graphics.getWidth();
        float boxHeight = 150;
        // In a real game, you would draw a background box here.
        
        // Render prompt/narration
        layout.setText(font, currentText, com.badlogic.gdx.graphics.Color.WHITE, width - 20, com.badlogic.gdx.utils.Align.left, true);
        font.draw(batch, layout, 10, boxHeight - 10);
        
        // Render choices
        if (choices != null) {
            float yPos = boxHeight - 40;
            for (int i = 0; i < choices.length; i++) {
                font.draw(batch, (i+1) + ". " + choices[i], 20, yPos);
                yPos -= 20;
            }
        } else {
            font.draw(batch, "Press any key to continue...", 20, 30);
        }
    }

    public boolean isActive() { return active; }
    
    public void dispose() { font.dispose(); }
}