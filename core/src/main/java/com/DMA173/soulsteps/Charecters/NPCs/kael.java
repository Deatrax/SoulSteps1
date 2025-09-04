package com.DMA173.soulsteps.Charecters.NPCs;

import com.DMA173.soulsteps.Charecters.CharecterAssets;
import com.DMA173.soulsteps.Charecters.NPC;
import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.story.GameStateManager;
import com.DMA173.soulsteps.ui.UIManager;

public class kael extends NPC {

    public kael(CharecterAssets assets, int characterType, float startX, float startY, String name, String npcType) {
        super(assets, characterType, startX, startY, name, npcType);
        //TODO Auto-generated constructor stub
    }

    @Override
    public void interact(Player player, GameStateManager gsm, UIManager uiManager){

        if(!gsm.hasCompletedObjective("interact_with_kael")){
            gsm.completeObjective("interact_with_kael");

            uiManager.showChoice(
                    "Hurt Man", // Speaker
                    "Please help a man in need, will you?", // Prompt
                    new String[] { "Here help yourself", "No, I'm very busy now" }, // Choices
                    (choice) -> {
                        // This code runs AFTER the player makes a choice from the dialogue box.
                        if (choice == 1) { // Chose to help
                            player.adjustKindness(10);
                            gsm.completeObjective("given_alms_to_begger");
                            this.setDialogue("You are a kind person, sir!");
                            
                            uiManager.showNarration("Poor begger", "Thank you very much sir, may Almighty bless you.");
                            
                            
                            
                        } else { // Chose to refuse
                            player.adjustKindness(-5);
                            gsm.completeObjective("denied_alms_to_begger");
                            this.setDialogue("");
                            
                            uiManager.showNarration("Poor Begger", "Oh... alright then.");
                        }
                    }
                );
        }
    }
    
}
