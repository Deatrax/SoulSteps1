package com.DMA173.soulsteps.Charecters.NPCs;

import com.DMA173.soulsteps.Charecters.CharecterAssets;
import com.DMA173.soulsteps.Charecters.NPC;
import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.story.GameStateManager;
import com.DMA173.soulsteps.ui.UIManager;

public class receptionist extends NPC{

    public receptionist(CharecterAssets assets, int characterType, float startX, float startY, String name,
            String npcType) {
        super(assets, characterType, startX, startY, name, npcType);
        //TODO Auto-generated constructor stub
    }

    @Override
    public void interact(Player player, GameStateManager gsm, UIManager uiManager){
        if (!gsm.hasCompletedObjective("talked_to_rep")){
                gsm.completeObjective("talked_to_rep");
                uiManager.showChoice(
                "Receptionist", // Speaker
                "tumi pochaaaaaaaa", // Prompt
                new String[] { "Of course. I'll look into it.", "I'm busy with other jobs right now." }, // Choices
                (choice) -> {
                    // This code runs AFTER the player makes a choice from the dialogue box.
                    if (choice == 1) { // Chose to help
                        player.adjustKindness(5);
                        gsm.completeObjective("talked_to_rep");
                        this.setDialogue("Did you find anything about the water system yet?");
                        
                        // Give the player their next objective via a narration box
                        uiManager.showNarration(null, "New Objective: Investigate the water system.");
                        uiManager.setObjective("Investigate the water system");
                        
                    } else { // Chose to refuse
                        player.adjustKindness(-5);
                        gsm.setFlag("refused_to_help_lena", true);
                        this.setDialogue("If you change your mind, I'll be here.");
                        
                        uiManager.showNarration("receptionist", "Oh... alright then.");
                    }
                }
            );
        }
         else if(gsm.hasCompletedObjective("talked_to_rep") && !gsm.hasCompletedObjective("denied")){
            uiManager.showChoice(
                    "Poor begger", // Speaker
                    "Will you still help me?", // Prompt
                    new String[] { "Ok, Here help yourself", "No, I'm very busy now" }, // Choices
                    (choice) -> {
                        // This code runs AFTER the player makes a choice from the dialogue box.
                        if (choice == 1) { // Chose to help
                            player.adjustKindness(5);
                            gsm.completeObjective("given_alms_to_begger");
                            this.setDialogue("You are a kind person, sir!");
                            
                            uiManager.showNarration("Poor begger", "Thank you very much sir, may Almighty bless you.");
                        }else { // Chose to refuse
                            player.adjustKindness(-10);
                            gsm.completeObjective("denied");
                            this.setDialogue("");
                            
                            uiManager.showNarration("Poor Begger", "Oh... alright then.");
                        }

                    }
                );
        }
        else{
            uiManager.showNarration("Poor begger", "Oh... alright then.");
        }
        
    }
 }

