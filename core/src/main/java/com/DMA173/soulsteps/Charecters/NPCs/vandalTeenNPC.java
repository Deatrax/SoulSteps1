package com.DMA173.soulsteps.Charecters.NPCs;

import com.DMA173.soulsteps.Charecters.CharecterAssets;
import com.DMA173.soulsteps.Charecters.NPC;
import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.story.GameStateManager;
import com.DMA173.soulsteps.ui.UIManager;

public class vandalTeenNPC extends  NPC{
    public vandalTeenNPC(CharecterAssets assets, int characterType, float startX, float startY, String name, String npcType){
        super( assets,  characterType,  startX,  startY,  name,  npcType);
    }

    @Override
    public void interact(Player player, GameStateManager gsm, UIManager uiManager) {
        
        if(!gsm.hasCompletedObjective("Talked_with_vandal")){

            gsm.completeObjective("Talked_with_vandal");
            
            uiManager.showChoice(
                    "Teen doing vandalism", // Speaker
                    "What do you want?", // Prompt
                    new String[] { "What are you doing?"}, // Choices
                    (choice) -> {
                        // This code runs AFTER the player makes a choice from the dialogue box.
                        if (true) { // Chose to help
                            
                            
                            // uiManager.showNarration("Teen doing vandalism", "What ever the heck I wish to do!!");
                            uiManager.showChoice(
                                "Teen doing vandalism", 
                                "What ever the heck I wish to do!!", 
                                new String[] { "[Stop the kid] You should not do that!!", "[let him be and mind your own] ...alright"}, // Choices
                                (choice2) -> {
                                    if(choice2 == 1){
                                        player.adjustKindness(10);
                                        gsm.completeObjective("tried_to_stop_teen");
                                        this.setDialogue("FFFINNEEE....you are no fun.. *hmph*");
                                    }
                                    else if(choice2 == 2){
                                        player.adjustKindness(-10);
                                        gsm.completeObjective("tried_to_stop_teen");
                                        this.setDialogue("Yeah right, get lost boomer");
                                    }

                                }
                            );
                            
                            
                        } 
                    }
                );
        }
        
        else{
            uiManager.showNarration("Teen doing vandalism", this.dialogue);
        }
        
    }
}
