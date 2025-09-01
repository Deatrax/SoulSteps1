package com.DMA173.soulsteps.Charecters;

import com.DMA173.soulsteps.story.GameStateManager;
import com.DMA173.soulsteps.ui.UIManager;

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
    private String dialogue;
    private boolean hasBeenTalkedTo;
    private String npcType;
    private String npcId; // Unique identifier for story tracking

    public NPC(CharecterAssets assets, int characterType, float startX, float startY, String name, String npcType) {
        super(assets, characterType, startX, startY, 0f, getAppearanceForNPCType(npcType));
        this.name = name;
        this.npcType = npcType;
        this.npcId = name.toLowerCase().replace(" ", "_"); // Generate ID from name
        this.isInteractable = true;
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
            case "ally":
            default: return new ClothesContainer(3, 5);
        }
    }

    @Override
    public void update(float delta) {
        updateStateTime(delta);
        this.setMoving(false);
    }

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

            default:
                // For any other NPC that doesn't have special logic,
                // just show their default dialogue line.
                uiManager.showNarration(this.name, this.dialogue);
                this.hasBeenTalkedTo = true;
                break;
        }
    }
    
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
    
    // GETTERS AND SETTERS
    public String getDialogue() { return dialogue; }
    public void setDialogue(String dialogue) { this.dialogue = dialogue; }
    public String getNpcType() { return npcType; }
    public String getNpcId() { return npcId; }
    public boolean hasBeenTalkedTo() { return hasBeenTalkedTo; }
    public void setHasBeenTalkedTo(boolean talkedTo) { this.hasBeenTalkedTo = talkedTo; }
}