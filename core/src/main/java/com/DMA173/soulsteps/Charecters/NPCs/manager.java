package com.DMA173.soulsteps.Charecters.NPCs;

import com.DMA173.soulsteps.Charecters.CharecterAssets;
import com.DMA173.soulsteps.Charecters.NPC;
import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.story.GameStateManager;
import com.DMA173.soulsteps.ui.UIManager;

public class manager extends NPC {

    public manager(CharecterAssets assets, int characterType, float startX, float startY, String name,
            String npcType) {
        super(assets, characterType, startX, startY, name, npcType);
    }

    @Override
    public void interact(Player player, GameStateManager gsm, UIManager uiManager) {
        stopWalking(); // Stop the manager from moving when talked to.
        
        if (!gsm.hasCompletedObjective("talked_to_man")) {
            gsm.completeObjective("talked_to_man");

            uiManager.showNarration("Elian", "Hello Sir, I would like to know about this device.", () -> {
                uiManager.showNarration("Manager", "This device isn’t for public concern, just leave it.", () -> {
                    
                    uiManager.showChoice(
                        "System", // Speaker
                        "Would you like to try to persuade him anymore?", // Prompt
                        new String[] { "Yes", "No" }, // Choices
                        (choice) -> {
                            if (choice == 1) { // Player chose "Yes"
                                
                                uiManager.showNarration("Elian", "I would still like to inquire about this device?", () -> {
                                    // --- Start of Necessary Change ---
                                    // This is the final dialogue in this branch.
                                    // After it shows, we need to hide the dialogue box.
                                    uiManager.showNarration("Manager", "I said it’s none of your business. \nNow get out of here before I lose my patience!", () -> {
                                        uiManager.hideDialogue();
                                    });
                                    // --- End of Necessary Change ---
                                });

                            } else { // Player chose "No"
                                
                                // --- Start of Necessary Change ---
                                // This is also a final dialogue in its branch.
                                // After it shows, we need to hide the dialogue box.
                                player.adjustKindness(-5);
                                uiManager.showNarration("Elian", "Oh... alright then.", () -> {
                                    uiManager.hideDialogue();
                                });
                                // --- End of Necessary Change ---
                            }
                        }
                    );
                });
            });

        } else if (gsm.hasCompletedObjective("talked_to_rep") && !gsm.hasCompletedObjective("denied")) {
            uiManager.showNarration("Manager", "I have told you to leave the place!!");

        } else {
            uiManager.showNarration("Manager", "Is there anything else I can help you with?");
        }
    }
}