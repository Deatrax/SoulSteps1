package com.DMA173.soulsteps.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

/**
 * A central place to manage all game fonts.
 * This class loads the TTF file and generates BitmapFonts of various sizes.
 * This is the most efficient way to handle fonts in LibGDX.
 */
public class FontManager {

    private static FreeTypeFontGenerator generator;
    
    // We can store different sizes of the same font
    public static BitmapFont standardFont;
    public static BitmapFont titleFont;

    /**
     * This must be called once when the game starts.
     */
    public static void load() {
        try {
            // Load the TTF file from your assets folder
            generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Minecraft.ttf"));
            
            // --- Configure the STANDARD font size ---
            FreeTypeFontParameter standardParam = new FreeTypeFontParameter();
            standardParam.size = 22; // The size in pixels
            standardParam.color = Color.WHITE;
            standardParam.shadowColor = new Color(0, 0, 0, 0.7f);
            standardParam.shadowOffsetX = 2;
            standardParam.shadowOffsetY = 2;
            standardParam.borderColor = Color.BLACK;
            standardParam.borderWidth = 1.5f;

            // --- Configure the TITLE font size ---
            FreeTypeFontParameter titleParam = new FreeTypeFontParameter();
            titleParam.size = 48; // A larger size for titles
            titleParam.color = Color.WHITE;
            titleParam.shadowColor = new Color(0, 0, 0, 0.7f);
            titleParam.shadowOffsetX = 3;
            titleParam.shadowOffsetY = 3;
            titleParam.borderColor = Color.BLACK;
            titleParam.borderWidth = 2f;
            
            // Generate the BitmapFonts
            standardFont = generator.generateFont(standardParam);
            titleFont = generator.generateFont(titleParam);

            System.out.println("Minecraft.ttf font loaded and generated successfully.");

        } catch (Exception e) {
            System.out.println("[Font loader]Could not load Minecraft.ttf. Falling back to default font. Exception: "+e.getMessage());
            // If the font fails to load, create a default font to prevent crashes
            standardFont = new BitmapFont();
            titleFont = new BitmapFont();
        }
    }

    /**
     * This must be called when the game closes to free up memory.
     */
    public static void dispose() {
        if (standardFont != null) standardFont.dispose();
        if (titleFont != null) titleFont.dispose();
        if (generator != null) generator.dispose();
    }
}