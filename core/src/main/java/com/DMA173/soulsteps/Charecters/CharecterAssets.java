package com.DMA173.soulsteps.Charecters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * This class acts as a manager for all character spritesheet assets.
 * It loads the main texture and pre-compiles all animations for all character variants.
 * Game objects like Player or Enemy can then request their specific animations from this class.
 */
public class CharecterAssets {

    public enum Direction {
        DOWN, LEFT, RIGHT, UP
    }

    private Texture sheet;

    // --- Spritesheet Dimensions ---
    private final int FRAME_WIDTH = 32;
    private final int FRAME_HEIGHT = 32;
    private final int H_SPACING = 0; // Horizontal spacing between frames
    private final int V_SPACING = 0;  // Vertical spacing between character rows
    private final int MARGIN_LEFT = 0;
    private final int MARGIN_TOP = 0;
    private final int NUM_CHARACTER_TYPES = 6; // You have 6 rows of characters

    // --- Animation Storage ---
    // Arrays to hold the animations for each character type.
    // Index 0 is for the first character, index 1 for the second, and so on.
    private Animation<TextureRegion>[] walkDownAnims;
    private Animation<TextureRegion>[] walkLeftAnims;
    private Animation<TextureRegion>[] walkRightAnims;
    private Animation<TextureRegion>[] walkUpAnims;
    private TextureRegion[] idleFrames; // A single idle frame for each direction and character type

    @SuppressWarnings("unchecked")
    public void init() {
        sheet = new Texture("Character/MetroCity/CharacterModel/Character Model.png");

        // Initialize the animation arrays to hold animations for all character types
        walkDownAnims = new Animation[NUM_CHARACTER_TYPES];
        walkLeftAnims = new Animation[NUM_CHARACTER_TYPES];
        walkRightAnims = new Animation[NUM_CHARACTER_TYPES];
        walkUpAnims = new Animation[NUM_CHARACTER_TYPES];
        idleFrames = new TextureRegion[NUM_CHARACTER_TYPES * 4]; // 4 directions per character

        TextureRegion[][] allFrames = extractFramesManually();

        // Loop through each row of frames (each character type) and create their animations
        for (int i = 0; i < NUM_CHARACTER_TYPES; i++) {
            if (i < allFrames.length) {
                createAnimationsForCharacter(i, allFrames[i]);
            }
        }
        System.out.println("Character assets loaded and all animations created.");
    }

    private TextureRegion[][] extractFramesManually() {
        int framesPerRow = (sheet.getWidth() - MARGIN_LEFT * 2 + H_SPACING) / (FRAME_WIDTH + H_SPACING);
        int numRows = (sheet.getHeight() - MARGIN_TOP + V_SPACING) / (FRAME_HEIGHT + V_SPACING);
        
        TextureRegion[][] frames = new TextureRegion[numRows][framesPerRow];

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < framesPerRow; col++) {
                int x = MARGIN_LEFT + col * (FRAME_WIDTH + H_SPACING);
                int y = MARGIN_TOP + row * (FRAME_HEIGHT + V_SPACING);
                if (x + FRAME_WIDTH <= sheet.getWidth() && y + FRAME_HEIGHT <= sheet.getHeight()) {
                    frames[row][col] = new TextureRegion(sheet, x, y, FRAME_WIDTH, FRAME_HEIGHT);
                }
            }
        }
        return frames;
    }

    private void createAnimationsForCharacter(int characterIndex, TextureRegion[] frames) {
        // Layout: Each row has 24 frames: down(0-5), right(6-11), up(12-17), left(18-23)
        final int FRAMES_PER_DIRECTION = 6;
        if (frames.length < FRAMES_PER_DIRECTION * 4) {
            System.err.println("Not enough frames for character " + characterIndex);
            return;
        }

        // Use GDX Array for easier subList operations
        // Down animation: frames 0-5 (first 6 frames)
            TextureRegion[] downFrames = new TextureRegion[6];
            System.arraycopy(frames, 0, downFrames, 0, 6);
            
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

        // Create walking animations
        walkDownAnims[characterIndex] = new Animation<>(0.1f,downFrames);
        walkRightAnims[characterIndex] = new Animation<>(0.1f, rightFrames);
        walkUpAnims[characterIndex] = new Animation<>(0.1f,upFrames);
        walkLeftAnims[characterIndex] = new Animation<>(0.1f, leftFrames);
    
        // Store the idle frames (first frame of each walk cycle)
        idleFrames[characterIndex * 4 + 0] = frames[0]; // Down
        idleFrames[characterIndex * 4 + 1] = frames[18]; // Left
        idleFrames[characterIndex * 4 + 2] = frames[6]; // Right
        idleFrames[characterIndex * 4 + 3] = frames[12]; // Up
    }

    /**
     * Gets the walking animation for a specific character type and direction.
     * @param characterType The row of the character on the spritesheet (0-5).
     * @param direction The direction of movement.
     * @return The requested Animation object.
     */
    public Animation<TextureRegion> getWalkAnimation(int characterType, Direction direction) {
        if (characterType < 0 || characterType >= NUM_CHARACTER_TYPES) return null;

        switch (direction) {
            case DOWN: return walkDownAnims[characterType];
            case LEFT: return walkLeftAnims[characterType];
            case RIGHT: return walkRightAnims[characterType];
            case UP: return walkUpAnims[characterType];
            default: return null;
        }
    }
    
    /**
     * Gets the idle frame for a specific character type and direction.
     * @param characterType The row of the character on the spritesheet (0-5).
     * @param direction The direction the character is facing.
     * @return The requested TextureRegion.
     */
    public TextureRegion getIdleFrame(int characterType, Direction direction) {
        if (characterType < 0 || characterType >= NUM_CHARACTER_TYPES) return null;
        
        int index = characterType * 4;
        switch (direction) {
            case DOWN: return idleFrames[index + 0];
            case LEFT: return idleFrames[index + 1];
            case RIGHT: return idleFrames[index + 2];
            case UP: return idleFrames[index + 3];
            default: return idleFrames[index + 0]; // Default to facing down
        }
    }

    public void dispose() {
        if (sheet != null) {
            sheet.dispose();
        }
    }
}