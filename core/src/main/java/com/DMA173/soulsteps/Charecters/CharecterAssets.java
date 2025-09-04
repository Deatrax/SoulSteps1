package com.DMA173.soulsteps.Charecters;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * This class acts as a manager for all character spritesheet assets including hair and clothing.
 * It loads the main texture and pre-compiles all animations for all character variants, their hair, and their outfits.
 * Game objects like Player or Enemy can then request their specific animations from this class.
 */
public class CharecterAssets {

    public enum Direction {
        DOWN, LEFT, RIGHT, UP
    }

    private Texture characterSheet;
    private Texture hairSheet;
    private Map<String, Texture> outfitTextures;

    // --- Spritesheet Dimensions ---
    private final int FRAME_WIDTH = 32;
    private final int FRAME_HEIGHT = 32;
    private final int H_SPACING = 0; // Horizontal spacing between frames
    private final int V_SPACING = 0;  // Vertical spacing between character rows
    private final int MARGIN_LEFT = 0;
    private final int MARGIN_TOP = 0;
    private final int NUM_CHARACTER_TYPES = 6; // You have 6 rows of characters
    private final int NUM_HAIR_TYPES = 6; // Assuming 6 hair types (adjust as needed)

    // --- Animation Storage ---
    // Character animations
    private Animation<TextureRegion>[] walkDownAnims;
    private Animation<TextureRegion>[] walkLeftAnims;
    private Animation<TextureRegion>[] walkRightAnims;
    private Animation<TextureRegion>[] walkUpAnims;
    private TextureRegion[] idleFrames;
    
    // Hair animations
    private Animation<TextureRegion>[] hairWalkDownAnims;
    private Animation<TextureRegion>[] hairWalkLeftAnims;
    private Animation<TextureRegion>[] hairWalkRightAnims;
    private Animation<TextureRegion>[] hairWalkUpAnims;
    private TextureRegion[] hairIdleFrames;
    
    // Clothing animations - organized by outfit path and row
    private Map<String, Animation<TextureRegion>[]> outfitWalkDownAnims;
    private Map<String, Animation<TextureRegion>[]> outfitWalkLeftAnims;
    private Map<String, Animation<TextureRegion>[]> outfitWalkRightAnims;
    private Map<String, Animation<TextureRegion>[]> outfitWalkUpAnims;
    private Map<String, TextureRegion[]> outfitIdleFrames;


    // --- NEW: Storage for action animations ---
    // We use a Map to store different actions by name (e.g., "spray_paint", "typing", "panhandling")
    private Map<String, Animation<TextureRegion>> actionAnimations;

    @SuppressWarnings("unchecked")
    public void init() {
        // Initialize character sheet
        characterSheet = new Texture("Character/MetroCity/CharacterModel/Character Model.png");
        
        // Initialize hair sheet
        hairSheet = new Texture("Character/MetroCity/Hair/Hairs.png");
        
        // Initialize outfit textures map
        outfitTextures = new HashMap<>();
        
        // Initialize character animation arrays
        walkDownAnims = new Animation[NUM_CHARACTER_TYPES];
        walkLeftAnims = new Animation[NUM_CHARACTER_TYPES];
        walkRightAnims = new Animation[NUM_CHARACTER_TYPES];
        walkUpAnims = new Animation[NUM_CHARACTER_TYPES];
        idleFrames = new TextureRegion[NUM_CHARACTER_TYPES * 4];
        
        // Initialize hair animation arrays
        hairWalkDownAnims = new Animation[NUM_HAIR_TYPES];
        hairWalkLeftAnims = new Animation[NUM_HAIR_TYPES];
        hairWalkRightAnims = new Animation[NUM_HAIR_TYPES];
        hairWalkUpAnims = new Animation[NUM_HAIR_TYPES];
        hairIdleFrames = new TextureRegion[NUM_HAIR_TYPES * 4];
        
        // Initialize outfit animation maps
        outfitWalkDownAnims = new HashMap<>();
        outfitWalkLeftAnims = new HashMap<>();
        outfitWalkRightAnims = new HashMap<>();
        outfitWalkUpAnims = new HashMap<>();
        outfitIdleFrames = new HashMap<>();

        // --- NEW: Initialize the action map ---
        actionAnimations = new HashMap<>();
        loadActionAnimations();

        // Load character animations
        TextureRegion[][] allCharacterFrames = extractFramesManually(characterSheet);
        for (int i = 0; i < NUM_CHARACTER_TYPES; i++) {
            if (i < allCharacterFrames.length) {
                createAnimationsForCharacter(i, allCharacterFrames[i]);
            }
        }
        
        // Load hair animations
        TextureRegion[][] allHairFrames = extractFramesManually(hairSheet);
        for (int i = 0; i < NUM_HAIR_TYPES; i++) {
            if (i < allHairFrames.length) {
                createHairAnimationsForType(i, allHairFrames[i]);
            }
        }
        
        // Load outfit animations
        loadOutfitAnimations();
        
        System.out.println("Character assets loaded with hair and clothing system.");
    }
    
    private void createHairAnimationsForType(int hairIndex, TextureRegion[] frames) {
        final int FRAMES_PER_DIRECTION = 6;
        if (frames.length < FRAMES_PER_DIRECTION * 4) {
            System.out.println("Not enough frames for hair type " + hairIndex);
            return;
        }

        // Extract frames for each direction
        TextureRegion[] downFrames = new TextureRegion[6];
        TextureRegion[] rightFrames = new TextureRegion[6];
        TextureRegion[] upFrames = new TextureRegion[6];
        TextureRegion[] leftFrames = new TextureRegion[6];
        
        System.arraycopy(frames, 0, downFrames, 0, 6);      // Down: 0-5
        System.arraycopy(frames, 6, rightFrames, 0, 6);     // Right: 6-11
        System.arraycopy(frames, 12, upFrames, 0, 6);       // Up: 12-17
        System.arraycopy(frames, 18, leftFrames, 0, 6);     // Left: 18-23

        // Create walking animations
        hairWalkDownAnims[hairIndex] = new Animation<>(0.1f, downFrames);
        hairWalkRightAnims[hairIndex] = new Animation<>(0.1f, rightFrames);
        hairWalkUpAnims[hairIndex] = new Animation<>(0.1f, upFrames);
        hairWalkLeftAnims[hairIndex] = new Animation<>(0.1f, leftFrames);

        // Store the idle frames
        hairIdleFrames[hairIndex * 4 + 0] = frames[0];  // Down
        hairIdleFrames[hairIndex * 4 + 1] = frames[18]; // Left
        hairIdleFrames[hairIndex * 4 + 2] = frames[6];  // Right
        hairIdleFrames[hairIndex * 4 + 3] = frames[12]; // Up
    }
    
    private void loadOutfitAnimations() {
        // Load individual outfit files (Outfit1.png to Outfit6.png)
        for (int i = 1; i <= 6; i++) {
            String outfitPath = "Character/MetroCity/Outfits/Outfit" + i + ".png";
            loadOutfitTexture(outfitPath, 1); // Each outfit file has 1 row
        }
        
        // Load suit file (Suit.png with 4 rows)
        String suitPath = "Character/MetroCity/Outfits/Suit.png";
        loadOutfitTexture(suitPath, 4); // Suit file has 4 rows
    }
    
    private void loadOutfitTexture(String texturePath, int numRows) {
        try {
            Texture outfitTexture = new Texture(texturePath);
            outfitTextures.put(texturePath, outfitTexture);
            
            // Extract frames for this outfit
            TextureRegion[][] outfitFrames = extractFramesManually(outfitTexture);
            
            // Create animations for each row in this outfit
            Animation<TextureRegion>[] downAnims = new Animation[numRows];
            Animation<TextureRegion>[] leftAnims = new Animation[numRows];
            Animation<TextureRegion>[] rightAnims = new Animation[numRows];
            Animation<TextureRegion>[] upAnims = new Animation[numRows];
            TextureRegion[] idles = new TextureRegion[numRows * 4];
            
            for (int row = 0; row < numRows && row < outfitFrames.length; row++) {
                createClothingAnimationsForRow(row, outfitFrames[row], downAnims, leftAnims, rightAnims, upAnims, idles);
            }
            
            // Store in maps
            outfitWalkDownAnims.put(texturePath, downAnims);
            outfitWalkLeftAnims.put(texturePath, leftAnims);
            outfitWalkRightAnims.put(texturePath, rightAnims);
            outfitWalkUpAnims.put(texturePath, upAnims);
            outfitIdleFrames.put(texturePath, idles);
            
        } catch (Exception e) {
            System.out.println("Could not load outfit texture: " + texturePath + " - " + e.getMessage());
        }
    }

    private void loadActionAnimations() {
        try {
            Texture spraySheet = new Texture("Character/actions/spray_effect.png");
            // Let's say you have 3 frames
            TextureRegion[] sprayFrames = new TextureRegion[3];
            
            for (int i = 0; i < 2; i++) {
                // Assuming each frame is 14x10
                sprayFrames[i] = new TextureRegion(spraySheet, i * 14, 0, 14, 10);
            }
            
            Animation<TextureRegion> sprayAnimation = new Animation<>(0.15f, sprayFrames);
            sprayAnimation.setPlayMode(Animation.PlayMode.LOOP);
            // We'll give this effect a unique ID
            actionAnimations.put("spray_effect", sprayAnimation);

            System.out.println("Loaded effect animation: spray_effect");

        } catch (Exception e) {
            System.out.println("Could not load effect animation spritesheet: " + e.getMessage());
        }
    }
    
    private void createClothingAnimationsForRow(int rowIndex, TextureRegion[] frames, 
                                               Animation<TextureRegion>[] downAnims,
                                               Animation<TextureRegion>[] leftAnims, 
                                               Animation<TextureRegion>[] rightAnims,
                                               Animation<TextureRegion>[] upAnims,
                                               TextureRegion[] idles) {
        
        // Layout: Each row has 24 frames: down(0-5), right(6-11), up(12-17), left(18-23)
        final int FRAMES_PER_DIRECTION = 6;
        if (frames.length < FRAMES_PER_DIRECTION * 4) {
            System.out.println("Not enough frames for clothing row " + rowIndex);
            return;
        }

        // Extract frames for each direction
        TextureRegion[] downFrames = new TextureRegion[6];
        TextureRegion[] rightFrames = new TextureRegion[6];
        TextureRegion[] upFrames = new TextureRegion[6];
        TextureRegion[] leftFrames = new TextureRegion[6];
        
        System.arraycopy(frames, 0, downFrames, 0, 6);      // Down: 0-5
        System.arraycopy(frames, 6, rightFrames, 0, 6);     // Right: 6-11
        System.arraycopy(frames, 12, upFrames, 0, 6);       // Up: 12-17
        System.arraycopy(frames, 18, leftFrames, 0, 6);     // Left: 18-23

        // Create animations
        downAnims[rowIndex] = new Animation<>(0.1f, downFrames);
        rightAnims[rowIndex] = new Animation<>(0.1f, rightFrames);
        upAnims[rowIndex] = new Animation<>(0.1f, upFrames);
        leftAnims[rowIndex] = new Animation<>(0.1f, leftFrames);

        // Store idle frames
        idles[rowIndex * 4 + 0] = frames[0];  // Down
        idles[rowIndex * 4 + 1] = frames[18]; // Left
        idles[rowIndex * 4 + 2] = frames[6];  // Right
        idles[rowIndex * 4 + 3] = frames[12]; // Up
    }

    private TextureRegion[][] extractFramesManually(Texture texture) {
        int framesPerRow = (texture.getWidth() - MARGIN_LEFT * 2 + H_SPACING) / (FRAME_WIDTH + H_SPACING);
        int numRows = (texture.getHeight() - MARGIN_TOP + V_SPACING) / (FRAME_HEIGHT + V_SPACING);
        
        TextureRegion[][] frames = new TextureRegion[numRows][framesPerRow];

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < framesPerRow; col++) {
                int x = MARGIN_LEFT + col * (FRAME_WIDTH + H_SPACING);
                int y = MARGIN_TOP + row * (FRAME_HEIGHT + V_SPACING);
                if (x + FRAME_WIDTH <= texture.getWidth() && y + FRAME_HEIGHT <= texture.getHeight()) {
                    frames[row][col] = new TextureRegion(texture, x, y, FRAME_WIDTH, FRAME_HEIGHT);
                }
            }
        }
        return frames;
    }

    private void createAnimationsForCharacter(int characterIndex, TextureRegion[] frames) {
        final int FRAMES_PER_DIRECTION = 6;
        if (frames.length < FRAMES_PER_DIRECTION * 4) {
            System.out.println("Not enough frames for character " + characterIndex);
            return;
        }

        // Extract frames for each direction
        TextureRegion[] downFrames = new TextureRegion[6];
        TextureRegion[] rightFrames = new TextureRegion[6];
        TextureRegion[] upFrames = new TextureRegion[6];
        TextureRegion[] leftFrames = new TextureRegion[6];
        
        System.arraycopy(frames, 0, downFrames, 0, 6);
        System.arraycopy(frames, 6, rightFrames, 0, 6);
        System.arraycopy(frames, 12, upFrames, 0, 6);
        System.arraycopy(frames, 18, leftFrames, 0, 6);

        // Create walking animations
        walkDownAnims[characterIndex] = new Animation<>(0.1f, downFrames);
        walkRightAnims[characterIndex] = new Animation<>(0.1f, rightFrames);
        walkUpAnims[characterIndex] = new Animation<>(0.1f, upFrames);
        walkLeftAnims[characterIndex] = new Animation<>(0.1f, leftFrames);

        // Store the idle frames
        idleFrames[characterIndex * 4 + 0] = frames[0];  // Down
        idleFrames[characterIndex * 4 + 1] = frames[18]; // Left
        idleFrames[characterIndex * 4 + 2] = frames[6];  // Right
        idleFrames[characterIndex * 4 + 3] = frames[12]; // Up
    }

    /**
     * Gets the walking animation for a specific character type and direction.
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
     * Gets the walking animation for hair based on hair type.
     */
    public Animation<TextureRegion> getHairWalkAnimation(int hairType, Direction direction) {
        // /*DEBUG */ hairType =4;
        if (hairType <= 0 || hairType > NUM_HAIR_TYPES) return null; // Hair type 0 = no hair
        
        int hairIndex = hairType - 1; // Convert to 0-based index
        
        switch (direction) {
            case DOWN: return hairWalkDownAnims[hairIndex];
            case LEFT: return hairWalkLeftAnims[hairIndex];
            case RIGHT: return hairWalkRightAnims[hairIndex];
            case UP: return hairWalkUpAnims[hairIndex];
            default: return null;
        }
    }
    
    /**
     * Gets the walking animation for clothing based on ClothesContainer.
     */
    public Animation<TextureRegion> getClothingWalkAnimation(ClothesContainer clothes, Direction direction) {
        if (clothes.outfitType == 0) return null; // No outfit
        
        String outfitPath = getOutfitPath(clothes.outfitType);
        if (outfitPath == null) return null;
        
        int rowIndex = getOutfitRowIndex(clothes.outfitType);
        
        Map<String, Animation<TextureRegion>[]> animMap = null;
        switch (direction) {
            case DOWN: animMap = outfitWalkDownAnims; break;
            case LEFT: animMap = outfitWalkLeftAnims; break;
            case RIGHT: animMap = outfitWalkRightAnims; break;
            case UP: animMap = outfitWalkUpAnims; break;
        }
        
        if (animMap != null && animMap.containsKey(outfitPath)) {
            Animation<TextureRegion>[] anims = animMap.get(outfitPath);
            if (rowIndex >= 0 && rowIndex < anims.length) {
                return anims[rowIndex];
            }
        }
        
        return null;
    }
    
    /**
     * Gets the idle frame for a specific character type and direction.
     */
    public TextureRegion getIdleFrame(int characterType, Direction direction) {
        // /*DEBUG*/ characterType =1;
        if (characterType < 0 || characterType >= NUM_CHARACTER_TYPES) return null;
        
        int index = characterType * 4;
        switch (direction) {
            case DOWN: return idleFrames[index + 0];
            case LEFT: return idleFrames[index + 1];
            case RIGHT: return idleFrames[index + 2];
            case UP: return idleFrames[index + 3];
            default: return idleFrames[index + 0];
        }
    }
    
    /**
     * Gets the idle frame for hair based on hair type.
     */
    public TextureRegion getHairIdleFrame(int hairType, Direction direction) {
        if (hairType <= 0 || hairType > NUM_HAIR_TYPES) return null; // Hair type 0 = no hair
        
        int hairIndex = hairType - 1; // Convert to 0-based index
        int index = hairIndex * 4;
        
        if (index >= hairIdleFrames.length) return null;
        
        switch (direction) {
            case DOWN: return hairIdleFrames[index + 0];
            case LEFT: return hairIdleFrames[index + 1];
            case RIGHT: return hairIdleFrames[index + 2];
            case UP: return hairIdleFrames[index + 3];
            default: return hairIdleFrames[index + 0];
        }
    }
    
    /**
     * Gets the idle frame for clothing based on ClothesContainer.
     */
    public TextureRegion getClothingIdleFrame(ClothesContainer clothes, Direction direction) {
        if (clothes.outfitType == 0) return null; // No outfit
        
        String outfitPath = getOutfitPath(clothes.outfitType);
        if (outfitPath == null || !outfitIdleFrames.containsKey(outfitPath)) return null;
        
        int rowIndex = getOutfitRowIndex(clothes.outfitType);
        TextureRegion[] idles = outfitIdleFrames.get(outfitPath);
        
        if (idles == null || rowIndex < 0) return null;
        
        int index = rowIndex * 4;
        if (index >= idles.length) return null;
        
        switch (direction) {
            case DOWN: return idles[index + 0];
            case LEFT: return idles[index + 1];
            case RIGHT: return idles[index + 2];
            case UP: return idles[index + 3];
            default: return idles[index + 0];
        }
    }
    
    /**
     * Get outfit file path based on outfit type integer
     */
    private String getOutfitPath(int outfitType) {
        if (outfitType >= 1 && outfitType <= 6) {
            return "Character/MetroCity/Outfits/Outfit" + outfitType + ".png";
        } else if (outfitType >= 101 && outfitType <= 104) {
            return "Character/MetroCity/Outfits/Suit.png";
        }
        return null;
    }
    
    /**
     * Get outfit row index based on outfit type integer
     */
    private int getOutfitRowIndex(int outfitType) {
        if (outfitType >= 1 && outfitType <= 6) {
            return 0; // Regular outfits always use row 0
        } else if (outfitType >= 101 && outfitType <= 104) {
            return outfitType - 101; // Suits use rows 0-3
        }
        return -1;
    }

    // --- NEW: A public getter for action animations ---
    public Animation<TextureRegion> getActionAnimation(String actionId) {
        return actionAnimations.get(actionId);
    }

    public void dispose() {
        if (characterSheet != null) {
            characterSheet.dispose();
        }
        
        if (hairSheet != null) {
            hairSheet.dispose();
        }
        
        // Dispose all outfit textures
        for (Texture texture : outfitTextures.values()) {
            if (texture != null) {
                texture.dispose();
            }
        }
        outfitTextures.clear();
    }
}