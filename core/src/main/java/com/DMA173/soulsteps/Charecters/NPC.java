package com.DMA173.soulsteps.Charecters;

import com.DMA173.soulsteps.story.GameStateManager; // <-- ADD THIS IMPORT

/**
 * NPC class. The only change is to the `interact` method.
 */
public class NPC extends Character {
    // ... (keep ALL your existing variables and constructors)
    private String dialogue;
    private boolean hasBeenTalkedTo;
    private String npcType;

    public NPC(CharecterAssets assets, int characterType, float startX, float startY, String name, String npcType) {
        super(assets, characterType, startX, startY, 0f, getAppearanceForNPCType(npcType));
        this.name = name;
        this.npcType = npcType;
        this.isInteractable = true;
        this.hasBeenTalkedTo = false;
        this.dialogue = "Hello there.";
    }

    // ... (keep the other constructor and getAppearanceForNPCType as they are)
    private static ClothesContainer getAppearanceForNPCType(String npcType) {
        switch (npcType.toLowerCase()) {
            case "veridia_employee":
                return new ClothesContainer(2, 103);
            case "police":
                return new ClothesContainer(1, 101);
            case "firefighter":
                return new ClothesContainer(1, 102);
            case "delivery_person":
                return new ClothesContainer(3, 104);
            case "resident_formal":
                return new ClothesContainer(2, 5);
            case "resident_casual":
                return new ClothesContainer(4, 6);
            case "resident_woman":
                return new ClothesContainer(5, 1);
            case "cold_weather":
                return new ClothesContainer(3, 2);
            case "winter_resident":
                return new ClothesContainer(6, 4);
            case "ally":
            default:
                return new ClothesContainer(3, 5);
        }
    }

    @Override
    public void update(float delta) {
        updateStateTime(delta);
        this.setMoving(false);
    }

    /**
     * REFACTORED INTERACT METHOD
     * It now receives the GameStateManager so the NPC can trigger story events.
     */
    // public void interact(Player player, GameStateManager gsm) {
    // // Example of a story-aware interaction:
    // if (this.getName().equals("Lena") &&
    // !gsm.hasCompletedObjective("talked_to_lena_first_time")) {
    // System.out.println(name + ": " + dialogue);
    // player.adjustKindness(5); // Being an ally gives kindness
    // gsm.completeObjective("talked_to_lena_first_time"); // IMPORTANT: This
    // triggers the story progression!
    // this.setDialogue("Did you find anything yet?"); // Change dialogue for next
    // time
    // } else {
    // // Default interaction for other NPCs or subsequent talks
    // System.out.println(name + ": " + this.dialogue);
    // }
    // }
    // In NPC.java

    //@Override
    public void interact(Player player, GameStateManager gsm) {
        if (this.getName().equals("Lena")) {
            // Lena's specific story logic
            if (!gsm.hasCompletedObjective(GameStateManager.OBJ_TALK_TO_LENA)) {
                // This is the first time talking to her
                System.out.println(name + ": Elian! I've been looking for you. The water pressure is terrible!");
                player.adjustKindness(5);

                // --- THIS IS THE TRIGGER ---
                // Completing this objective progresses the story.
                gsm.completeObjective(GameStateManager.OBJ_TALK_TO_LENA);

                this.setDialogue("Please, check the main valve in the residential district.");
            } else {
                // This is what she says any other time you talk to her
                System.out.println(name + ": " + this.dialogue);
            }
        } else {
            // Default interaction for all other NPCs
            System.out.println(name + ": " + this.dialogue);
        }
    }

    // ... (keep all other methods like setDialogue, getters, etc.)
    public String getDialogue() {
        return dialogue;
    }

    public void setDialogue(String dialogue) {
        this.dialogue = dialogue;
    }

    public String getNpcType() {
        return npcType;
    }
    // ... etc.
}