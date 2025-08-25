package com.DMA173.soulsteps.testClasses;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class CharacterTest {
    public enum Direction { DOWN, LEFT, RIGHT, UP, IDLE }
    
    //private SpriteBatch batch;
    private Texture sheet;
    
    // Animations for different directions
    private Animation<TextureRegion> walkDown, walkLeft, walkRight, walkUp;
    private Animation<TextureRegion> idleDown, idleLeft, idleRight, idleUp;
    private Animation<TextureRegion> currentAnimation;
    
    private float stateTime;
    private Direction currentDirection = Direction.IDLE;
    private Direction facingDirection = Direction.DOWN; // Last movement direction
    
    // Character position and movement
    private Vector2 position;
    private float speed = 200f;
    
    // Frame dimensions - adjust these based on your actual spritesheet
    private int frameWidth = 16;   // Character width
    private int frameHeight = 30;  // Character height
    private int spacing = 16;      // Spacing between frames
    private int HSpacing = 16;
    private int VSpacing = 2;
    private int marginLeft = 8;    // Left margin from edge of file
    private int marginRight = 8;   // Right margin from edge of file  
    private int marginTop = 1;     // Top margin from edge of file
    private int marginBottom = 1;  // Bottom margin (adjust if needed)
    
    // Which character row to use (0 = first character, 1 = second, etc.)
    private int characterRow = 0;
    
    public void init() {
        try {
            // batch = new SpriteBatch();
            sheet = new Texture("Character/MetroCity/CharacterModel/Character Model.png");
            position = new Vector2(400, 300); // Starting position
            
            System.out.println("Texture size: " + sheet.getWidth() + "x" + sheet.getHeight());
            
            // Manual frame extraction to handle margins and spacing properly
            TextureRegion[][] allFrames = extractFramesManually();
            
            if (allFrames.length == 0 || allFrames[0].length == 0) {
                throw new RuntimeException("No frames found in spritesheet - check frame dimensions");
            }
            
            System.out.println("Spritesheet: " + allFrames.length + " rows, " + allFrames[0].length + " frames per row");
            
            // Extract frames for the selected character row
            if (characterRow >= allFrames.length) {
                System.err.println("Character row " + characterRow + " not found, using row 0");
                characterRow = 0;
            }
            
            TextureRegion[] characterFrames = allFrames[characterRow];
            
            // Create animations - frames are arranged horizontally: down(0-5), right(6-11), up(12-17), left(18-23)
            createAnimations(characterFrames);
            
            // Start with idle animation facing down
            currentAnimation = idleDown != null ? idleDown : 
                              (walkDown != null ? walkDown : null);
            
            if (currentAnimation == null) {
                throw new RuntimeException("No valid animations created - check spritesheet layout");
            }
            
            stateTime = 0f;
            
            System.out.println("CharacterTest initialized successfully");
            System.out.println("Frame size: " + frameWidth + "x" + frameHeight);
            System.out.println("Margins: left=" + marginLeft + ", top=" + marginTop + ", spacing=" + spacing);
            
        } catch (Exception e) {
            System.err.println("Error initializing CharacterTest: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    private TextureRegion[][] extractFramesManually() {
        // Calculate how many frames fit horizontally and vertically
        int availableWidth = sheet.getWidth() - marginLeft - marginRight;
        int availableHeight = sheet.getHeight() - marginTop - marginBottom;
        
        // Each frame takes frameWidth + spacing, except the last frame in each direction
        int framesPerRow = (availableWidth + HSpacing) / (frameWidth + HSpacing);
        int numRows = (availableHeight + VSpacing) / (frameHeight + VSpacing);
        
        System.out.println("Calculated: " + framesPerRow + " frames per row, " + numRows + " rows");
        System.out.println("Available space: " + availableWidth + "x" + availableHeight);
        
        TextureRegion[][] frames = new TextureRegion[numRows][framesPerRow];
        
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < framesPerRow; col++) {
                // Calculate the exact pixel position for this frame
                int x = marginLeft + col * (frameWidth + HSpacing);
                int y = marginTop + row * (frameHeight + VSpacing);
                
                // Create the texture region for this specific frame
                frames[row][col] = new TextureRegion(sheet, x, y, frameWidth, frameHeight);
                
                // Debug: print first few frame positions
                if (row == 0 && col < 4) {
                    System.out.println("Frame [" + row + "][" + col + "] at (" + x + "," + y + ") size " + frameWidth + "x" + frameHeight);
                }
            }
        }
        
        return frames;
    }
    
    private void createAnimations(TextureRegion[] frames) {
        // Layout: Each row has 24 frames arranged horizontally
        // down(0-5), right(6-11), up(12-17), left(18-23)
        
        System.out.println("Creating animations from " + frames.length + " frames");
        
        if (frames.length >= 24) {
            // Down animation: frames 0-5 (first 6 frames)
            TextureRegion[] downFrames = new TextureRegion[6];
            for (int i = 0; i < 6; i++) {
                downFrames[i] = frames[i];
            }
            
            // Right animation: frames 6-11 (second group of 6)
            TextureRegion[] rightFrames = new TextureRegion[6];
            for (int i = 0; i < 6; i++) {
                rightFrames[i] = frames[6 + i];
            }
            
            // Up animation: frames 12-17 (third group of 6)
            TextureRegion[] upFrames = new TextureRegion[6];
            for (int i = 0; i < 6; i++) {
                upFrames[i] = frames[12 + i];
            }
            
            // Left animation: frames 18-23 (fourth group of 6)
            TextureRegion[] leftFrames = new TextureRegion[6];
            for (int i = 0; i < 6; i++) {
                leftFrames[i] = frames[18 + i];
            }
            
            // Create walking animations (faster frame rate for smooth walking)
            walkDown = new Animation<>(0.1f, downFrames);
            walkRight = new Animation<>(0.1f, rightFrames);
            walkUp = new Animation<>(0.1f, upFrames);
            walkLeft = new Animation<>(0.1f, leftFrames);
            
            // Create idle animations (using the first frame of each direction - typically the "standing" frame)
            idleDown = new Animation<>(1.0f, downFrames[0]);
            idleRight = new Animation<>(1.0f, rightFrames[0]);
            idleUp = new Animation<>(1.0f, upFrames[0]);
            idleLeft = new Animation<>(1.0f, leftFrames[0]);
            
            System.out.println("Successfully created all 4 direction animations with 6 frames each");
            
        } else {
            System.err.println("Not enough frames! Expected 24, got " + frames.length);
            System.err.println("Layout should be: down(0-5), right(6-11), up(12-17), left(18-23)");
            
            // Fallback: create simple animations with available frames
            if (frames.length >= 4) {
                // Assume first 4 frames are one for each direction
                idleDown = new Animation<>(1.0f, frames[0]);
                idleRight = new Animation<>(1.0f, frames[1]);
                idleUp = new Animation<>(1.0f, frames[2]);
                idleLeft = new Animation<>(1.0f, frames[3]);
                
                walkDown = walkRight = walkUp = walkLeft = idleDown;
            } else if (frames.length > 0) {
                // Ultimate fallback
                TextureRegion fallback = frames[0];
                idleDown = walkDown = new Animation<>(1.0f, fallback);
                idleLeft = walkLeft = idleDown;
                idleRight = walkRight = idleDown;
                idleUp = walkUp = idleDown;
            }
        }
        
        // Set all walking animations to loop
        if (walkDown != null) walkDown.setPlayMode(Animation.PlayMode.LOOP);
        if (walkLeft != null) walkLeft.setPlayMode(Animation.PlayMode.LOOP);
        if (walkRight != null) walkRight.setPlayMode(Animation.PlayMode.LOOP);
        if (walkUp != null) walkUp.setPlayMode(Animation.PlayMode.LOOP);
    }
    
    public void handleInput(float delta) {
        boolean moving = false;
        
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            position.y += speed * delta;
            setDirection(Direction.UP);
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            position.y -= speed * delta;
            setDirection(Direction.DOWN);
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            position.x -= speed * delta;
            setDirection(Direction.LEFT);
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            position.x += speed * delta;
            setDirection(Direction.RIGHT);
            moving = true;
        }
        
        if (!moving) {
            setDirection(Direction.IDLE);
        }
        
        
    }
    
    private void setDirection(Direction dir) {
        if (dir != Direction.IDLE) {
            facingDirection = dir;
        }
        
        if (currentDirection == dir) return; // No change
        
        currentDirection = dir;
        
        if (dir == Direction.IDLE) {
            // Set idle animation based on facing direction
            switch (facingDirection) {
                case DOWN: currentAnimation = idleDown; break;
                case LEFT: currentAnimation = idleLeft; break;
                case RIGHT: currentAnimation = idleRight; break;
                case UP: currentAnimation = idleUp; break;
            }
        } else {
            // Set walking animation
            switch (dir) {
                case DOWN: currentAnimation = walkDown; break;
                case LEFT: currentAnimation = walkLeft; break;
                case RIGHT: currentAnimation = walkRight; break;
                case UP: currentAnimation = walkUp; break;
            }
        }
        
        stateTime = 0f; // Reset animation time
    }
    
    // private void switchCharacter(int row) {
    //     if (row < 0) return;
        
    //     characterRow = row;
        
    //     // Safely dispose current texture
    //     if (sheet != null) {
    //         sheet.dispose();
    //         sheet = null;
    //     }
        
    //     try {
    //         sheet = new Texture("Character/Character Model.png");
            
    //         // Use manual frame extraction
    //         TextureRegion[][] allFrames = extractFramesManually();
            
    //         if (row < allFrames.length && allFrames[row].length > 0) {
    //             TextureRegion[] characterFrames = allFrames[row];
                
    //             createAnimations(characterFrames);
    //             setDirection(currentDirection); // Refresh current animation
    //             System.out.println("Switched to character row: " + row + " (character variant " + (row + 1) + ")");
    //         } else {
    //             System.err.println("Invalid character row: " + row + " (max: " + (allFrames.length - 1) + ")");
    //         }
    //     } catch (Exception e) {
    //         System.err.println("Error switching character: " + e.getMessage());
    //         e.printStackTrace();
    //     }
    // }
    
    public void update(float delta) {
        handleInput(delta);
        stateTime += delta;
    }
    
    public void render(OrthographicCamera camera, SpriteBatch batch) {
        if (currentAnimation == null || batch == null) return;
        
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        if (currentFrame == null || currentFrame.getTexture() == null) return;
        
        try {
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            batch.draw(currentFrame, 
                       position.x - frameWidth / 2f, 
                       position.y - frameHeight / 2f);
            batch.end();
        } catch (Exception e) {
            System.err.println("Error rendering character: " + e.getMessage());
            // Try to clean up if batch is in bad state
            if (batch.isDrawing()) {
                try {
                    batch.end();
                } catch (Exception ignored) {}
            }
        }
    }
    
    // Overloaded version for testing without camera
    public void render(SpriteBatch batch) {
        if (currentAnimation == null || batch == null) return;
        
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        if (currentFrame == null || currentFrame.getTexture() == null) return;
        
        try {
            batch.begin();
            batch.draw(currentFrame, position.x, position.y);
            batch.end();
        } catch (Exception e) {
            System.err.println("Error rendering character: " + e.getMessage());
            // Try to clean up if batch is in bad state
            if (batch.isDrawing()) {
                try {
                    batch.end();
                } catch (Exception ignored) {}
            }
        }
    }
    
    public Vector2 getPosition() { return position; }
    public void setPosition(float x, float y) { position.set(x, y); }
    
    // Adjust these if the character appears cut off or too small/large
    public void setFrameDimensions(int width, int height) {
        this.frameWidth = width;
        this.frameHeight = height;
        // You'll need to call init() again after changing dimensions
    }
    
    // Adjust spacing if frames are cutting off or overlapping
    public void setSpacing(int spacing, int marginLeft, int marginTop) {
        this.spacing = spacing;
        this.marginLeft = marginLeft;
        this.marginTop = marginTop;
        // You'll need to call init() again after changing spacing
    }
    
    public void dispose() {
        try {

            //NO NEED in class batch
            // if (batch != null) {
            //     if (batch.isDrawing()) {
            //         batch.end();
            //     }
            //     batch.dispose();
            //     batch = null;
            // }
            if (sheet != null) {
                sheet.dispose();
                sheet = null;
            }
        } catch (Exception e) {
            System.err.println("Error during dispose: " + e.getMessage());
        }
    }
}