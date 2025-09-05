package com.DMA173.soulsteps.Charecters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.DMA173.soulsteps.story.GameStateManager;
import com.DMA173.soulsteps.ui.UIManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * UPDATED NPC CLASS
 * 
 * Now supports dynamic dialogue based on story progression.
 * 
 * HOW TO CREATE STORY-AWARE NPCs:
 * 1. Set initial dialogue in constructor
 * 2. Use interact() method to check story state and change behavior
 * 3. Use GameStateManager flags and objectives to track conversation progress
 * 4. Update dialogue based on story progression
 */
public class NPC extends Character {
    protected  String dialogue;
    private boolean hasBeenTalkedTo;
    private String npcType;
    private String npcId; // Unique identifier for story tracking


    // --- NEW: State management for NPC behavior ---
    public enum NpcState { IDLE, WALKING, PERFORMING_ACTION }
    private NpcState currentState = NpcState.IDLE;
    private Animation<TextureRegion> currentActionAnimation;

    // --- NEW: Pathfinding variables (for Part 2) ---
    private List<Vector2> pathPoints;
    private int currentPathIndex;
    private float arrivalThreshold = 2f; // How close to a point to be considered "arrived"
    private boolean isPathLooping = false;

    // --- MODIFY: We no longer need to replace the character's animation ---
    private Animation<TextureRegion> currentEffectAnimation; // Changed from currentActionAnimation

    // --- ADD THIS PUBLIC GETTER METHOD ---
    /**
     * Returns the current behavior state of the NPC.
     * This allows external classes like WorldManager to check if the NPC is busy.
     * @return The current NpcState (IDLE, WALKING, or PERFORMING_ACTION).
     */
    public NpcState getCurrentState() {
        return currentState;
    }
    // ------------------------------------


    public NPC(CharecterAssets assets, int characterType, float startX, float startY, String name, String npcType) {
        super(assets, characterType, startX, startY, 0f, getAppearanceForNPCType(npcType));
        this.name = name;
        this.npcType = npcType;
        this.npcId = name.toLowerCase().replace(" ", "_"); // Generate ID from name
        this.isInteractable = true;
        this.hasBeenTalkedTo = false;
        this.dialogue = "Hello there.";
    }

    public NPC(CharecterAssets assets, int characterType, float startX, float startY, String name, String npcType, boolean interatable) {
        super(assets, characterType, startX, startY, 0f, getAppearanceForNPCType(npcType));
        this.name = name;
        this.npcType = npcType;
        this.npcId = name.toLowerCase().replace(" ", "_"); // Generate ID from name
        this.isInteractable = interatable;
        this.hasBeenTalkedTo = false;
        this.dialogue = "Hello there.";
    }
    
    public NPC(CharecterAssets assets, int characterType, float startX, float startY, String name, String npcType, String initialDialogue) {
        this(assets, characterType, startX, startY, name, npcType);
        this.dialogue = initialDialogue;
    }
    
    private static ClothesContainer getAppearanceForNPCType(String npcType) {
         switch (npcType.toLowerCase()) {
            case "veridia_employee": return new ClothesContainer(2, 103);
            case "police": return new ClothesContainer(1, 101);
            case "firefighter": return new ClothesContainer(1, 102);
            case "delivery_person": return new ClothesContainer(3, 104);
            case "resident_formal": return new ClothesContainer(2, 5);
            case "resident_casual": return new ClothesContainer(4, 6);
            case "resident_woman": return new ClothesContainer(5, 1);
            case "cold_weather": return new ClothesContainer(3, 2);
            case "winter_resident": return new ClothesContainer(6, 4);
            case "begger": return new ClothesContainer(2,3);
            case "ally":
            default: return new ClothesContainer(3, 5);
        }
    }

    @Override
    public void update(float delta) {
        updateStateTime(delta);
        
        switch (currentState) {
            case IDLE:
                this.setMoving(false);
                break;
            case WALKING:
                updatePathMovement(delta);
                break;
            case PERFORMING_ACTION:
                // When performing an action, the NPC is not moving.
                this.setMoving(false);
                break;
        }
    }

     // --- UPGRADE the render method to draw the effect as an overlay ---
    @Override
    public void render(Batch batch) {
        // Step 1: Render the character itself as normal (idle, walking, etc.)
        super.render(batch);

        // Step 2: If the NPC is performing an action, draw the effect on top.
        if (currentState == NpcState.PERFORMING_ACTION && currentEffectAnimation != null) {
            TextureRegion effectFrame = currentEffectAnimation.getKeyFrame(stateTime, true);
            
            if (effectFrame != null) {
                // --- POSITIONING LOGIC for the effect ---
                // We want to position the spray can near the character's hands.
                float offsetX = 0;
                float offsetY = 12; // Position it slightly above the character's feet (adjust as needed)

                // Adjust the offset based on the character's facing direction
                switch (this.currentDir) {
                    case RIGHT:
                        offsetX = 10; // To the right of the character's center
                        break;
                    case LEFT:
                        offsetX = -10 - effectFrame.getRegionWidth(); // To the left, accounting for width
                        break;
                    case UP:
                        offsetY = 16;
                        break;
                    case DOWN:
                        offsetY = 8;
                        break;
                }

                // We don't need to scale the effect, so we draw it at its normal size
                batch.draw(effectFrame, position.x + offsetX, position.y + offsetY);
            }
        }
    }


    // --- NEW: Public method to make an NPC perform an action ---
    /**
     * Assigns a special, looping action animation to this NPC.
     * @param actionId The ID of the animation loaded in CharecterAssets (e.g., "spray_paint").
     */
    public void performAction(String actionId) {
        // Animation<TextureRegion> actionAnim = assets.getActionAnimation(actionId);
        // if (actionAnim != null) {
        //     this.currentState = NpcState.PERFORMING_ACTION;
        //     this.currentActionAnimation = actionAnim;
        // } else {
        //     System.err.println("Could not find action animation with ID: " + actionId);
        // }
    }

    // --- NEW: Method to stop the action and return to idle ---
    public void stopAction() {
        this.currentState = NpcState.IDLE;
        this.currentActionAnimation = null;
    }

    // --- Pathfinding logic will go here in Part 2 ---




     /**
     * STORY-AWARE INTERACT METHOD (Refactored for On-Screen Dialogue)
     * 
     * This method uses your original, detailed story logic and displays it
     * using the UIManager's dialogue box instead of the console.
     */
    public void interact(Player player, GameStateManager gsm, UIManager uiManager) {
        
        // We use a switch on the NPC's name to route to the correct logic.
        // This keeps the code organized as you add more important characters.
        switch (this.name) {
            case "Lena":
                handleLenaInteraction(player, gsm, uiManager);
                break;
            
            // FUTURE: Add other important NPCs here
            // case "Ms. Chen":
            //    handleReceptionistInteraction(player, gsm, uiManager);
            //    break;

            // ... etc.
             // --- NEW: Add a case for Kael ---
            case "Kael":
                uiManager.showChoice(
                    "Mysterious Figure (Kael)",
                    "You're the one who was at Dan's place, right? I saw you. You're looking into the water problem. We need to talk, but not here.",
                    new String[]{"Who are you?", "How do you know that?"},
                    (choice) -> {
                        // Regardless of the choice, the outcome is the same for this ambush
                        uiManager.showNarration("Kael", "Names don't matter right now. Meet me at the old mechanic shop on the west side of town. I can explain everything.");
                        
                        // --- THIS IS THE KEY STORY PROGRESSION ---
                        gsm.completeObjective("meet_the_informant");
                    }
                );
                break;
            // --------------------------------

            default:
                // For any other NPC that doesn't have special logic,
                // just show their default dialogue line.
                uiManager.showNarration(this.name, this.dialogue);
                this.hasBeenTalkedTo = true;
                break;
        }
    }


    // --- RENAME the performAction method for clarity ---
    /**
     * Assigns a looping effect animation to this NPC. The NPC will continue
     * to render its base idle/walk animation underneath the effect.
     * @param effectId The ID of the animation loaded in CharecterAssets (e.g., "spray_effect").
     */
    public void performEffect(String effectId) {
        Animation<TextureRegion> effectAnim = assets.getActionAnimation(effectId);
        if (effectAnim != null) {
            this.currentState = NpcState.PERFORMING_ACTION;
            this.currentEffectAnimation = effectAnim;
            // The NPC's base animation can be set to idle
            this.setMoving(false); 
        } else {
            System.err.println("Could not find effect animation with ID: " + effectId);
        }
    }

    // --- RENAME the stopAction method for clarity ---
    public void stopEffect() {
        this.currentState = NpcState.IDLE;
        this.currentEffectAnimation = null;
    }


    /**
     * Assigns a path for the NPC to walk along.
     * The NPC will move from its current position to point A, then B, C, etc.
     * @param speed The movement speed for this path.
     * @param points A series of Vector2 points to walk to.
     */
    public void walkPath(float speed, Vector2... points) {
        if (points.length == 0) return;

        this.speed = speed;
        this.pathPoints = new ArrayList<>(Arrays.asList(points));
        this.currentPathIndex = 0;
        this.currentState = NpcState.WALKING;
        this.setMoving(true); // Set the animation state to walking
    }

    /**
     * Assigns a path for the NPC to walk along.
     * @param speed The movement speed for this path.
     * @param loop If true, the NPC will return to the first point after reaching the last.
     * @param points A series of Vector2 points to walk to.
     */
    public void walkPath(float speed, boolean loop, Vector2... points) {
        if (points.length == 0) return;

        this.speed = speed;
        this.isPathLooping = loop; // Store the loop flag
        this.pathPoints = new ArrayList<>(Arrays.asList(points));
        this.currentPathIndex = 0;
        this.currentState = NpcState.WALKING;
        this.setMoving(true);
    }

    /**
     * This method is called by update() when the NPC is in the WALKING state.
     * It handles moving the NPC towards the current target point on its path.
     */
    private void updatePathMovement(float delta) {
    // --- PRE-CONDITION CHECKS ---
    if (pathPoints == null || pathPoints.isEmpty()) {
        // No path to follow, switch to IDLE and exit.
        currentState = NpcState.IDLE;
        setMoving(false);
        setCurrentDir(CharecterAssets.Direction.DOWN); // Reset to default facing direction
        isPathLooping = false; // Reset loop flag
        return;
    }

    // --- STEP 1: GET THE CURRENT TARGET ---
    // If the path index is somehow invalid, reset the path.
    if (currentPathIndex >= pathPoints.size()) {
         if (isPathLooping) {
            currentPathIndex = 0;
        } else {
            currentState = NpcState.IDLE;
            setMoving(false);
            setCurrentDir(CharecterAssets.Direction.DOWN); // Reset to default facing direction
            isPathLooping = false;
            return;
        }
    }
    Vector2 targetPoint = pathPoints.get(currentPathIndex);

    // --- STEP 2: CALCULATE MOVEMENT ---
    // Calculate the vector from our position to the target.
    Vector2 movementVector = new Vector2(targetPoint).sub(position);
    float distanceToTarget = movementVector.len();

    // Normalize the vector to get a pure direction.
    Vector2 direction = movementVector.nor();

    // --- STEP 3: MOVE THE CHARACTER ---
    // Calculate how much to move this frame.
    float moveAmount = speed * delta;

    // An important check: Do not overshoot the target!
    // If the distance to the target is less than how much we would move,
    // just move directly to the target. This prevents jittering at the destination.
    if (distanceToTarget < moveAmount) {
        position.set(targetPoint);
    } else {
        // Otherwise, move normally along the direction vector.
        position.add(direction.scl(moveAmount));
    }
    
    // Update the character's visual facing direction.
    updateFacingDirection(direction);

    // --- STEP 4: CHECK FOR ARRIVAL AND ADVANCE PATH ---
    // Check if we have arrived at or passed the target point.
    if (position.dst(targetPoint) < arrivalThreshold) {
        // We've arrived. Advance to the next point in the path for the *next* frame.
        currentPathIndex++;

        // Now, check if we have finished the entire path.
        if (currentPathIndex >= pathPoints.size()) {
            if (isPathLooping) {
                // If looping, reset to the beginning of the path.
                currentPathIndex = 0;
            } else {
                // If not looping, the path is complete. Stop.
                currentState = NpcState.IDLE;
                setMoving(false);
                setCurrentDir(CharecterAssets.Direction.DOWN); // Reset to default facing direction
                isPathLooping = false; // Clear the flag
            }
        }
    }
}
    
    // ... (the rest of your NPC.java file remains the same)
 

    /**
     * A helper method to make the NPC's sprite face the direction it's moving.
     */
    private void updateFacingDirection(Vector2 direction) {
        // Prioritize horizontal or vertical facing direction for 4-directional sprites
        if (Math.abs(direction.x) > Math.abs(direction.y)) {
            // Moving more horizontally
            if (direction.x > 0) {
                setCurrentDir(CharecterAssets.Direction.RIGHT);
            } else {
                setCurrentDir(CharecterAssets.Direction.LEFT);
            }
        } else {
            // Moving more vertically
            if (direction.y > 0) {
                setCurrentDir(CharecterAssets.Direction.UP);
            } else {
                setCurrentDir(CharecterAssets.Direction.DOWN);
            }
        }
    }
    
    // --- Start of Necessary Change ---
    /**
     * Immediately stops the NPC's current movement path by clearing the path
     * and setting the state to IDLE.
     */
    public void stopWalking() {
        if (this.pathPoints != null) {
            this.pathPoints.clear();
        }
        this.currentState = NpcState.IDLE;
        this.setMoving(false); // Ensure animation stops
    }
    // --- End of Necessary Change ---

// ━━━━━━━━━━━━━━━━━━━━━┏┓━┏┓━━━━━━━━━━━━━━━━┏┓━━━━━━┏┓━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┏┓━━━━━
// ━━━━━━━━━━━━━━━━━━━━┏┛┗┓┃┃━━━━━━━━━━━━━━━━┃┃━━━━━━┃┃━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┃┃━━━━━
// ┏━━┓┏┓┏┓┏━━┓┏━┓┏┓━┏┓┗┓┏┛┃┗━┓┏┓┏━┓━┏━━┓━━━━┃┗━┓┏━━┓┃┃━┏━━┓┏┓┏┓┏┓━━━━┏┓┏━━┓━━━━┏━━┓┏┓┏┓┏━━┓━┏┓┏┓┏━━┓┃┃━┏━━┓
// ┃┏┓┃┃┗┛┃┃┏┓┃┃┏┛┃┃━┃┃━┃┃━┃┏┓┃┣┫┃┏┓┓┃┏┓┃━━━━┃┏┓┃┃┏┓┃┃┃━┃┏┓┃┃┗┛┗┛┃━━━━┣┫┃━━┫━━━━┃┏┓┃┗╋╋┛┗━┓┃━┃┗┛┃┃┏┓┃┃┃━┃┏┓┃
// ┃┃━┫┗┓┏┛┃┃━┫┃┃━┃┗━┛┃━┃┗┓┃┃┃┃┃┃┃┃┃┃┃┗┛┃━━━━┃┗┛┃┃┃━┫┃┗┓┃┗┛┃┗┓┏┓┏┛━━━━┃┃┣━━┃━━━━┃┃━┫┏╋╋┓┃┗┛┗┓┃┃┃┃┃┗┛┃┃┗┓┃┃━┫
// ┗━━┛━┗┛━┗━━┛┗┛━┗━┓┏┛━┗━┛┗┛┗┛┗┛┗┛┗┛┗━┓┃━━━━┗━━┛┗━━┛┗━┛┗━━┛━┗┛┗┛━━━━━┗┛┗━━┛━━━━┗━━┛┗┛┗┛┗━━━┛┗┻┻┛┃┏━┛┗━┛┗━━┛
// ━━━━━━━━━━━━━━━┏━┛┃━━━━━━━━━━━━━━━┏━┛┃━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┃┃━━━━━━━━━
// ━━━━━━━━━━━━━━━┗━━┛━━━━━━━━━━━━━━━┗━━┛━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┗┛━━━━━━━━━


    /**
     * Your original complex story logic for Lena, now using the UIManager.
     */
    private void handleLenaInteraction(Player player, GameStateManager gsm, UIManager uiManager) {
        
        // FIRST MEETING: Introduces the problem and gives the player a choice.
        if (!gsm.hasCompletedObjective("talked_to_lena_first_time")) {
            
            uiManager.showChoice(
                "Lena", // Speaker
                "Elian! Thank goodness. The water pressure is terrible. Could you investigate?", // Prompt
                new String[] { "Of course. I'll look into it.", "I'm busy with other jobs right now." }, // Choices
                (choice) -> {
                    // This code runs AFTER the player makes a choice from the dialogue box.
                    if (choice == 1) { // Chose to help
                        player.adjustKindness(5);
                        gsm.completeObjective("talked_to_lena_first_time");
                        this.setDialogue("Did you find anything about the water system yet?");
                        
                        // Give the player their next objective via a narration box
                        uiManager.showNarration(null, "New Objective: Investigate the water system.");
                        uiManager.setObjective("Investigate the water system");
                        
                    } else { // Chose to refuse
                        player.adjustKindness(-5);
                        gsm.setFlag("refused_to_help_lena", true);
                        this.setDialogue("If you change your mind, I'll be here.");
                        
                        uiManager.showNarration("Lena", "Oh... alright then.");
                    }
                }
            );

        // WAITING FOR EVIDENCE: Player has talked to Lena but hasn't found anything yet.
        } else if (!gsm.hasCompletedObjective("found_first_evidence") && player.getEvidenceCount() == 0) {
            // This is a simple narration, no choice.
            uiManager.showNarration("Lena", dialogue + " Try looking around town for clues.");
            
        // PLAYER HAS EVIDENCE: React to the player's findings.
        } else if (player.getEvidenceCount() > 0 && !gsm.getFlag("lena_knows_evidence")) {
            uiManager.showNarration("Lena", "You found something? Excellent work, Elian! You should investigate that Veridia building downtown.");
            
            player.adjustKindness(3);
            gsm.setFlag("lena_knows_evidence", true);
            this.setDialogue("Be careful investigating Veridia. They're powerful people.");
            
        // ONGOING SUPPORT: General encouragement for the rest of the game.
        } else {
            uiManager.showNarration("Lena", this.dialogue);
            
            // You can even have conditional follow-up dialogue
            if (player.isDangerZoneActive()) {
                // This will show up AFTER the player dismisses the first dialogue box.
                // It makes the conversation feel more dynamic.
                // Note: For a true multi-line conversation, you would build a more advanced dialogue queue system.
                // For now, this is a simple way to add extra context.
                // uiManager.showNarration("Lena", "You seem stressed, Elian. Remember to stay true to your values.");
                // player.adjustKindness(2);
            }
        }
    }
    
    /**
     * EXAMPLE 2: Building receptionist with security-aware responses
     * Shows how NPCs can react to player appearance and story progress
     */
    private void handleReceptionistInteraction(Player player, GameStateManager gsm) {
        if (!hasBeenTalkedTo) {
            // FIRST MEETING: Standard greeting
            System.out.println("Ms. Chen: Welcome to Veridia Corporation. How may I assist you today?");
            System.out.println("Ms. Chen: Are you here for a job interview or business meeting?");
            
            gsm.completeObjective("talked_to_receptionist_veridia");
            hasBeenTalkedTo = true;
            setDialogue("Is there anything specific I can help you find?");
            
        } else if (player.getEvidenceCount() > 2) {
            // SUSPICIOUS BEHAVIOR: Reacts to too much evidence
            System.out.println("Ms. Chen: You've been asking a lot of questions around here...");
            System.out.println("Ms. Chen: Security! We have a situation!");
            
            player.adjustKindness(-5); // Getting caught reduces kindness
            gsm.setFlag("receptionist_suspicious", true);
            setDialogue("I've called security. Please leave immediately.");
            
        } else if (gsm.getFlag("wearing_disguise")) {
            // DISGUISE INTERACTION: Different response if player is disguised
            System.out.println("Ms. Chen: Good morning! I don't think I've seen you before.");
            System.out.println("Ms. Chen: The employee break room is on the second floor if you need it.");
            
        } else {
            // NORMAL REPEAT INTERACTION
            System.out.println("Ms. Chen: " + dialogue);
        }
    }
    
    /**
     * EXAMPLE 3: Residents react to player's kindness level
     * Shows how NPCs can respond to player stats
     */
    private void handleResidentInteraction(Player player, GameStateManager gsm) {
        if (player.isDangerZoneActive()) {
            // LOW KINDNESS: Residents are wary
            System.out.println(name + ": You seem upset about something. Is everything alright?");
            if (!hasBeenTalkedTo) {
                System.out.println(name + ": Maybe you should take some time to cool down before investigating.");
                player.adjustKindness(1); // Small kindness boost from concern
            }
        } else if (player.getKindnessLevel() > 80) {
            // HIGH KINDNESS: Residents are helpful
            System.out.println(name + ": You seem like such a kind person! I'm happy to help.");
            if (!hasBeenTalkedTo && this.npcType.equals("resident_formal")) {
                System.out.println(name + ": I did notice some workers from Veridia around the water meters last week.");
                player.collectEvidence(); // Helpful information counts as evidence
            }
        } else {
            // NORMAL KINDNESS: Standard interaction
            System.out.println(name + ": " + dialogue);
        }
        
        hasBeenTalkedTo = true;
    }

    
    /**
     * EXAMPLE 4: Police respond to evidence and story progress
     * Shows how authority figures can help or hinder based on story state
     */
    private void handlePoliceInteraction(Player player, GameStateManager gsm) {
        if (gsm.hasCompletedObjective("discover_water_limiter") && player.hasWaterLimiter()) {
            // PLAYER HAS CONCRETE EVIDENCE: Police take action
            System.out.println(name + ": This is serious evidence of corporate tampering!");
            System.out.println(name + ": We'll investigate Veridia Corporation immediately. Thank you, citizen.");
            
            player.adjustKindness(10); // Reporting crime increases kindness
            gsm.setFlag("police_investigating", true);
            setDialogue("We're building a case against Veridia Corp thanks to your evidence.");
            
        } else if (player.getEvidenceCount() > 0) {
            // PARTIAL EVIDENCE: Police are interested but need more
            System.out.println(name + ": Interesting findings, but we need more concrete evidence.");
            System.out.println(name + ": Keep investigating, but be careful not to break any laws.");
            
        } else {
            // NO EVIDENCE: Standard police response
            System.out.println(name + ": " + dialogue);
            if (!hasBeenTalkedTo) {
                System.out.println(name + ": If you have any information about crimes, please report them.");
            }
        }
        
        hasBeenTalkedTo = true;
    }

     // Add Game parameter to the interact method
    public void interact(Player player, GameStateManager gsm, UIManager uiManager, Game game) {
        // ... (Default logic)
    }
    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    /// ////////////////////////////////////////////////////////////////////////////////////
    
    
    // GETTERS AND SETTERS
    public String getDialogue() { return dialogue; }
    public void setDialogue(String dialogue) { this.dialogue = dialogue; }
    public String getNpcType() { return npcType; }
    public String getNpcId() { return npcId; }
    public boolean hasBeenTalkedTo() { return hasBeenTalkedTo; }
    public void setHasBeenTalkedTo(boolean talkedTo) { this.hasBeenTalkedTo = talkedTo; }
}