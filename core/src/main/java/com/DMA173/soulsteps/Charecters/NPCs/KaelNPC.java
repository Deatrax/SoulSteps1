package com.DMA173.soulsteps.Charecters.NPCs;

import com.DMA173.soulsteps.Charecters.CharecterAssets;
import com.DMA173.soulsteps.Charecters.NPC;
import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.story.GameStateManager;
import com.DMA173.soulsteps.ui.UIManager;
import com.badlogic.gdx.Game;

// KaelNPC extends the base NPC class, inheriting all its basic properties.
public class KaelNPC extends NPC {

    public KaelNPC(CharecterAssets assets, float startX, float startY) {
        // We call the parent constructor with Kael's specific details.
        // We can use character model '4' for him, for example.
        super(assets, 4, startX, startY, "Kael", "ally");
        this.setDialogue("You need to be more careful. They're watching.");
    }

    /**
     * This is Kael's unique "brain". It OVERRIDES the default, simple interact method
     * from the parent NPC class. This is where all the logic from your story outline goes.
     */
    @Override
    public void interact(Player player, GameStateManager gsm, UIManager uiManager, Game game) {
        
        // --- The "Hurt Man" Encounter ---
        // This entire block of logic only happens once. We use a flag to track it.
        if (!gsm.getFlag("kael_encounter_initiated")) {
            gsm.setFlag("kael_encounter_initiated", true);

            uiManager.showChoice(
                "Hurt Man", // Speaker
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
        
        // uiManager.showChoice("Elian", "Alright, stay calm. I'll help you look.",  new String[] {
        //     // This code runs AFTER the first narration box is closed.
        //     uiManager.showChoice("Kael", "Thank you! I think they're buried under all this junk.",  new String[] {
        //         // This runs after the second narration, triggering the minigame.
        //         System.out.println("[STORY] Triggering Object Finder Minigame to find Kael's keys.");
        //         // --- MINIGAME TRIGGER ---
        //         // game.setScreen(new ObjectFinder(game, thisScreen, ...)); // TODO: Launch the ObjectFinder screen
        //     });
        // });
    }

    /**
     * Handles the story progression if the player chooses to ignore Kael.
     */
    private void handlePathB(Player player, GameStateManager gsm, UIManager uiManager, Game game) {
        player.adjustKindness(-15);
        gsm.setFlag("player_ignored_kael", true);
        
        // uiManager.showChoice("Elian", "Sorry, I can't get involved.",  new String[] {
        //     uiManager.showChoice("Narrator", "The man is quickly apprehended by security. As they drag him away, you notice a shredded file fall from his jacket.",  new String[] {
        //         System.out.println("[STORY] Player can now interact with the file to trigger the Paper Puzzle.");
        //         // Here, you would typically spawn a new interactable "object" in the world.
        //         // For now, we can launch the puzzle directly for testing.
        //         // --- MINIGAME TRIGGER ---
        //         // game.setScreen(new PagePuzzle(game, thisScreen, ...)); // TODO: Launch the PagePuzzle screen
        //     });
        // });
    }
}