package com.DMA173.soulsteps.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;

/**
 * A menu button that supports both keyboard and mouse input.
 * Designed to easily support PNG button sprites when available.
 */
public class MenuButton {
    private String text;
    private Rectangle bounds;
    private boolean isSelected;
    private boolean isPressed;
    private Runnable action;
    
    // Visual styling
    private Color normalColor = new Color(0.2f, 0.2f, 0.3f, 0.8f);
    private Color hoverColor = new Color(0.3f, 0.3f, 0.5f, 0.9f);
    private Color pressedColor = new Color(0.4f, 0.4f, 0.6f, 1.0f);
    private Color textColor = Color.WHITE;
    
    // PNG button textures (commented out for now)
    /*
    private Texture buttonNormalTexture;
    private Texture buttonHoverTexture;
    private Texture buttonPressedTexture;
    */
    
    public MenuButton(String text, float x, float y, float width, float height, Runnable action) {
        this.text = text;
        this.bounds = new Rectangle(x, y, width, height);
        this.action = action;
        this.isSelected = false;
        this.isPressed = false;
        
        // TODO: Load PNG button textures when available
        /*
        try {
            buttonNormalTexture = new Texture("ui/buttons/button_normal.png");
            buttonHoverTexture = new Texture("ui/buttons/button_hover.png");
            buttonPressedTexture = new Texture("ui/buttons/button_pressed.png");
        } catch (Exception e) {
            System.out.println("Button textures not found, using rectangles");
        }
        */
    }
    
    public void update(float delta) {
        // Check mouse hover
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        // Convert screen coordinates to world coordinates (flip Y)
        mousePos.y = Gdx.graphics.getHeight() - mousePos.y;
        
        boolean wasHovered = isSelected;
        isSelected = bounds.contains(mousePos.x, mousePos.y);
        
        // Check for mouse click
        if (isSelected && Gdx.input.justTouched()) {
            trigger();
        }
        
        // Reset pressed state
        if (isPressed) {
            isPressed = false;
        }
    }
    
    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, BitmapFont font) {
        Color buttonColor = isPressed ? pressedColor : (isSelected ? hoverColor : normalColor);
        
        // Render button background
        /*
        // PNG version (uncomment when textures are available)
        if (buttonNormalTexture != null) {
            batch.begin();
            Texture currentTexture = isPressed ? buttonPressedTexture : 
                                   (isSelected ? buttonHoverTexture : buttonNormalTexture);
            if (currentTexture != null) {
                batch.draw(currentTexture, bounds.x, bounds.y, bounds.width, bounds.height);
            }
            batch.end();
        } else {
        */
            // Rectangle fallback
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(buttonColor);
            shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
            shapeRenderer.end();
        // }
        
        // Render button text
        batch.begin();
        font.setColor(textColor);
        font.draw(batch, text, bounds.x + bounds.width / 2f, bounds.y + bounds.height / 2f + 8, 
                 0, Align.center, false);
        batch.end();
    }
    
    public void trigger() {
        isPressed = true;
        if (action != null) {
            action.run();
        }
    }
    
    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }
    
    public boolean isSelected() {
        return isSelected;
    }
    
    public Rectangle getBounds() {
        return bounds;
    }
    
    public void setBounds(float x, float y, float width, float height) {
        bounds.set(x, y, width, height);
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getText() {
        return text;
    }
    
    public void dispose() {
        /*
        // Dispose PNG textures when implemented
        if (buttonNormalTexture != null) buttonNormalTexture.dispose();
        if (buttonHoverTexture != null) buttonHoverTexture.dispose();
        if (buttonPressedTexture != null) buttonPressedTexture.dispose();
        */
    }
}