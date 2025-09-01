package com.DMA173.soulsteps;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;

import java.util.*;

public class ObjectFinder implements Screen {

    theMain game;

    public ObjectFinder(theMain game) {
        this.game = game;
    }

    SpriteBatch batch;
    Texture backgroundTex;
    Texture keyTex, box1Tex, bookTex, appleTex, bananaTex, bottle1Tex, bottle2Tex,
            box2Tex, bulbTex, canTex, can2Tex, cigarTex, coneTex, cuptex, fish1Tex, paperTex, phoneTex, wrapperTex;

    List<Item> items;
    BitmapFont font;

    boolean found = false;
    boolean showCelebration = false;
    float keyScale = 1f;
    float animationTime = 0f;
    boolean animationFinished = false;

    Item draggedItem = null;

    class Item {
        Texture texture;
        boolean isDustbin;
        float x, y, width, height;
        boolean beingDragged = false;
        float offsetX, offsetY;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.valueOf("#f5b042"));
        font.getData().setScale(2);
        

        // Load background
        backgroundTex = new Texture("background.png");

        // Load textures
        keyTex = new Texture("key.png");
        box1Tex = new Texture("box1.png");
        bookTex = new Texture("book.png");
        appleTex = new Texture("apple.png");
        bananaTex = new Texture("banana.png");
        bottle1Tex = new Texture("bottle1.png");
        bottle2Tex = new Texture("bottle2.png");
        box2Tex = new Texture("box2.png");
        bulbTex = new Texture("bulb.png");
        canTex = new Texture("can.png");
        can2Tex = new Texture("can2.png");
        cigarTex = new Texture("cigar.png");
        coneTex = new Texture("cone.png");
        cuptex = new Texture("cup.png");
        fish1Tex = new Texture("fish1.png");
        paperTex = new Texture("paper.png");
        phoneTex = new Texture("phone.png");
        wrapperTex = new Texture("wrapper.png");

        items = new ArrayList<>();
        Random rand = new Random();

       // Random key position near center
float centerX = Gdx.graphics.getWidth() / 2f;
float centerY = Gdx.graphics.getHeight() / 2f;

// Allow some small variation around the center (±80 px horizontally, ±80 px vertically)
float keyX = centerX - 32 + rand.nextInt(160) - 80;
float keyY = centerY - 32 + rand.nextInt(160) - 80;


        // Add the key
        Item key = new Item();
        key.texture = keyTex;
        key.isDustbin = true;
        key.width = 64;
        key.height = 64;
        key.x = keyX;
        key.y = keyY;
        items.add(key);

        // Array of other textures
        Texture[] textures = new Texture[]{
                box1Tex, bookTex, appleTex, bananaTex, bottle1Tex, bottle2Tex,
                box2Tex, bulbTex, canTex, can2Tex, cigarTex, coneTex, cuptex,
                fish1Tex, paperTex, phoneTex, wrapperTex
        };

        // Cover key with 4 objects
        for (int i = 0; i < 2; i++) {
            Item obj = new Item();
            obj.texture = textures[rand.nextInt(textures.length)];
            obj.isDustbin = false;
            obj.width = 90;
            obj.height = 90;

            obj.x = key.x + rand.nextInt(10) - 5;
            obj.y = key.y + rand.nextInt(10) - 5;
            items.add(obj);
        }

        // Add remaining objects randomly
     // Add remaining objects randomly but clustered around the key
for (int i = 0; i < 16; i++) {
    Item obj = new Item();
    obj.texture = textures[rand.nextInt(textures.length)];
    obj.isDustbin = false;
    obj.width = 90;
    obj.height = 90;

    // Cluster within ±80 pixels of the key position
    obj.x = key.x + rand.nextInt(160) - 80;
    obj.y = key.y + rand.nextInt(160) - 80;

    items.add(obj);
}

    
}
    

@Override
public void render(float delta) {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    batch.begin();

    // Draw background
    batch.draw(backgroundTex, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    // Always show top-center text (with shadow)
    String message = found ? "You found the Key!" : "Find the Key!";
    GlyphLayout layout = new GlyphLayout(font, message);
    float x = (Gdx.graphics.getWidth() - layout.width) / 2;
    float y = Gdx.graphics.getHeight() - 20; // a little below top

    // Shadow
    font.setColor(Color.BLACK);
    font.draw(batch, layout, x + 2, y - 2);

    // Main text
    font.setColor(Color.valueOf("#f5b042"));
    font.draw(batch, layout, x, y);

    // Animate key if celebration
    if (showCelebration) {
        float keyWidth, keyHeight, centerX, centerY;

        if (!animationFinished) {
            // Animate key (dance/pulse)
            animationTime += delta;
            keyScale = 1f + 0.5f * (float) Math.sin(animationTime * 5);

            if (animationTime > 1f) {
                animationFinished = true;
                keyScale = 1.2f; // final enlarged size
            }
        }

        keyWidth = keyTex.getWidth() * keyScale;
        keyHeight = keyTex.getHeight() * keyScale;
        centerX = Gdx.graphics.getWidth() / 2 - keyWidth / 2;
        centerY = Gdx.graphics.getHeight() / 2 - keyHeight / 2;

        batch.draw(keyTex, centerX, centerY, keyWidth, keyHeight);
    } else {
        // Draw all items normally
        for (Item item : items) {
            batch.draw(item.texture, item.x, item.y, item.width, item.height);
        }
    }

    batch.end();

    // --- Input Handling (unchanged) ---
    if (!showCelebration) {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        if (Gdx.input.justTouched()) {
            ListIterator<Item> li = items.listIterator(items.size());
            while (li.hasPrevious()) {
                Item item = li.previous();
                Rectangle rect = new Rectangle(item.x, item.y, item.width, item.height);

                if (rect.contains(mouseX, mouseY)) {
                    if (item.isDustbin) {
                        found = true;
                        showCelebration = true;
                        animationTime = 0f;
                        animationFinished = false;
                        System.out.println("Key clicked! You win!");
                    } else {
                        draggedItem = item;
                        draggedItem.beingDragged = true;
                        draggedItem.offsetX = mouseX - item.x;
                        draggedItem.offsetY = mouseY - item.y;
                        items.remove(item);
                        items.add(item);
                    }
                    break;
                }
            }
        }

        if (draggedItem != null && draggedItem.beingDragged && Gdx.input.isTouched()) {
            draggedItem.x = mouseX - draggedItem.offsetX;
            draggedItem.y = mouseY - draggedItem.offsetY;
        }

        if (draggedItem != null && !Gdx.input.isTouched()) {
            draggedItem.beingDragged = false;
            draggedItem = null;
        }
    }
}


    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        backgroundTex.dispose();
        keyTex.dispose();
        box1Tex.dispose();
        bookTex.dispose();
        appleTex.dispose();
        bananaTex.dispose();
        bottle1Tex.dispose();
        bottle2Tex.dispose();
        box2Tex.dispose();
        bulbTex.dispose();
        canTex.dispose();
        can2Tex.dispose();
        cigarTex.dispose();
        coneTex.dispose();
        cuptex.dispose();
        fish1Tex.dispose();
        paperTex.dispose();
        phoneTex.dispose();
        wrapperTex.dispose();
        font.dispose();
    }
}


