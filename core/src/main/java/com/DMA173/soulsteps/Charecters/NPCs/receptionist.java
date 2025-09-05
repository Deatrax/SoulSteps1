package com.DMA173.soulsteps.Charecters.NPCs;

import com.DMA173.soulsteps.Charecters.CharecterAssets;
import com.DMA173.soulsteps.Charecters.NPC;
import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.story.GameStateManager;
import com.DMA173.soulsteps.ui.UIManager; // 👈 add this import

public class receptionist extends NPC {

    public receptionist(CharecterAssets assets, int characterType, float startX, float startY, String name,
            String npcType) {
        super(assets, characterType, startX, startY, name, npcType);
    }

    @Override
    public void interact(Player player, GameStateManager gsm, UIManager uiManager) {
        // --- Start of Necessary Change ---
        stopWalking(); // Stop the receptionist from moving when interacted with.
        
        if (!gsm.hasCompletedObjective("talked_to_rep")) {
            gsm.completeObjective("talked_to_rep");

            // This is the sequence of nested callbacks.
            // Each line of dialogue triggers the next one when the player dismisses it.
            uiManager.showNarration("Receptionist", "Welcome to Veridia Corporation. How may I help you?", () -> {
                uiManager.showNarration("Elian", "I would like to inquire about this device?", () -> {
                    uiManager.showNarration("Receptionist", "Okay! You can talk to our manager over there.");
                    // The last line has no callback, so the conversation ends here.
                });
            });

        } else {
            // This is the dialogue that will be shown if the player talks to the receptionist again.
            uiManager.showNarration("Receptionist", "The manager is over there if you wish to speak with him.");
        }
        // --- End of Necessary Change ---
    }
}