package com.DMA173.soulsteps.Charecters;

import com.DMA173.soulsteps.story.GameState;
import com.DMA173.soulsteps.ui.DialogueUI;

public class NPC extends Character {
    private String dialogue;
    private String npcType;

    public NPC(CharecterAssets assets, int characterType, float startX, float startY, String name, String npcType) {
        super(assets, characterType, startX, startY, 0f, getAppearanceForNPCType(npcType));
        this.name = name;
        this.npcType = npcType;
        this.isInteractable = true;
        this.dialogue = "Hello.";
    }

    @Override
    public void update(float delta) {
        updateStateTime(delta);
        this.setMoving(false);
    }
    
    /**
     * The NPC's "brain". This method contains the logic for what happens when the player interacts with this specific NPC.
     * @param gameState The central game state, which this NPC can read from and write to.
     * @param dialogueUI The UI system this NPC can use to show text or choices.
     * @param player The player object, to affect things like kindness.
     */
    public void interact(GameState gameState, DialogueUI dialogueUI, Player player) {
        // Each NPC has its own interaction logic tree.
        switch (this.name) {
            case "Beggar":
                // This NPC only offers one choice, one time.
                dialogueUI.showChoice("A beggar approaches you.",
                    new String[]{"Give money", "Refuse"},
                    (choice) -> {
                        // This code runs AFTER the player makes a choice.
                        gameState.makeChoice("beggar_encounter", choice);
                        if (choice == 1) player.adjustKindness(5); else player.adjustKindness(-5);
                        gameState.completeObjective("beggar_encounter");
                        this.setInteractable(false); // Make the beggar non-interactable after this.
                    });
                break;

            case "Lena":
                // Lena has different dialogue based on game state.
                if (gameState.getFlag("found_first_limiter")) {
                    dialogueUI.showNarration("What is that strange device you found on my pipes?");
                } else {
                    dialogueUI.showNarration(this.dialogue);
                    // This is where you would add logic for the "discovery" moment.
                }
                break;
                
            default:
                // Any other NPC will just say their default line.
                dialogueUI.showNarration(this.name + ": " + this.dialogue);
                break;
        }
    }

    private static ClothesContainer getAppearanceForNPCType(String npcType) {
        // ... (this method remains the same)
        switch (npcType.toLowerCase()) {
            case "resident_woman": return new ClothesContainer(5, 1);
            default: return new ClothesContainer(3, 5);
        }
    }
    
    public String getDialogue() { return dialogue; }
    public void setDialogue(String dialogue) { this.dialogue = dialogue; }
}