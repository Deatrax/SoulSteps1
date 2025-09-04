package com.DMA173.soulsteps.Charecters.NPCs;

import com.DMA173.soulsteps.Charecters.CharecterAssets;
import com.DMA173.soulsteps.Charecters.NPC;
import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.story.GameStateManager;
import com.DMA173.soulsteps.story.StoryProgressionManager; // 👈 add this import
import com.DMA173.soulsteps.ui.UIManager;

public class receptionist extends NPC {

    private final StoryProgressionManager story; // 👈 hold a reference

    public receptionist(
            CharecterAssets assets,
            int characterType,
            float startX,
            float startY,
            String name,
            String npcType,
            StoryProgressionManager story // 👈 inject here
    ) {
        super(assets, characterType, startX, startY, name, npcType);
        this.story = story;
    }

    @Override
    public void interact(Player player, GameStateManager gsm, UIManager uiManager) {

        // Only run the receptionist dialog when that objective is active.
        if (story.isObjectiveActive("talked_to_rep")) {

            uiManager.showChoice(
                "Receptionist",
                "Welcome to Verdia HQ. How can I help you?",
                new String[] { "I'm here to investigate.", "Just looking around." },
                (choice) -> {
                    if (choice == 1) {
                        player.adjustKindness(5);

                        // Sync both systems:
                        // 1) Mark the objective complete in GameStateManager (your save/flags layer)
                        gsm.completeObjective("talked_to_rep");
                        // 2) Advance the story to the next objective ("talked_to_man")
                        story.forceCompleteCurrentObjective();

                        this.setDialogue("Please speak with the manager upstairs.");
                        uiManager.showNarration(null, "New Objective: Talk to the manager.");
                    } else {
                        player.adjustKindness(-5);
                        uiManager.showNarration("Receptionist", "Alright, but don’t waste our time.");
                    }
                }
            );

        } else {
            // Fallback dialog when this isn't the active objective
            uiManager.showNarration("Receptionist", "Good day!");
        }
    }
}
