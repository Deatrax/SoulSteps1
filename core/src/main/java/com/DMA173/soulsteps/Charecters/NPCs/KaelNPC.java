package com.DMA173.soulsteps.Charecters.NPCs;

import com.DMA173.soulsteps.Charecters.CharecterAssets;
import com.DMA173.soulsteps.Charecters.NPC;
import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.story.GameStateManager;
import com.DMA173.soulsteps.ui.UIManager;
import com.badlogic.gdx.Game;

// KaelNPC extends the base NPC class, inheriting all its basic properties.
public class KaelNPC extends NPC {

    private Game game;

    public KaelNPC(CharecterAssets assets, float startX, float startY) {
        // We call the parent constructor with Kael's specific details.
        // We can use character model '4' for him, for example.
        super(assets, 4, startX, startY, "Kael", "ally");
        this.setDialogue("You need to be more careful. They're watching.");
    }

    public void setGame(Game game){
        this.game = game;
    }

    @Override
    public void interact(Player player, GameStateManager gsm, UIManager uiManager) {
        interact2( player,  gsm,  uiManager,  game);
    }


    /**
     * This is Kael's unique "brain". It OVERRIDES the default, simple interact method
     * from the parent NPC class. This is where all the logic from your story outline goes.
     */
    public void interact2(Player player, GameStateManager gsm, UIManager uiManager, Game game) {
        
        // --- The "Hurt Man" Encounter ---
        // This entire block of logic only happens once. We use a flag to track it.
        if (!gsm.getFlag("kael_encounter_initiated")) {
            gsm.setFlag("kael_encounter_initiated", true);

            uiManager.showChoice(
                "Mysterious Hurt Man", // Speaker
                "Hey, you! Please, help me. They're after me... I dropped my car keys somewhere in this trash.", // Prompt
                new String[]{"Help him look for the keys.", "Ignore him and walk away."}, // Choices for Path A and Path B
                (choice) -> {
                    if (choice == 1) {
                        // --- Player chose PATH A ---
                        handlePathA(player, gsm, uiManager, game);
                    } else {
                        // --- Player chose PATH B ---
                        handlePathB(player, gsm, uiManager, game);
                    }
                }
            );

        } else {
            // This is the default dialogue if the player interacts with Kael at any other time.
            uiManager.showNarration(this.getName(), this.getDialogue());
        }
    }

    /**
     * Handles the story progression if the player chooses to help Kael.
     */
    private void handlePathA(Player player, GameStateManager gsm, UIManager uiManager, Game game) {
        player.adjustKindness(10);
        gsm.setFlag("player_helped_kael", true);
        
       uiManager.showNarration("system", "you will now enter the obj game");
    }

    /**
     * Handles the story progression if the player chooses to ignore Kael.
     */
    private void handlePathB(Player player, GameStateManager gsm, UIManager uiManager, Game game) {
        player.adjustKindness(-15);
        gsm.setFlag("player_ignored_kael", true);
        
       uiManager.showNarration("system", "you will now see him get abducted");
        
    }
}