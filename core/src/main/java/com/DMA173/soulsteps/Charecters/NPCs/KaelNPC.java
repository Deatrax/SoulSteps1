package com.DMA173.soulsteps.Charecters.NPCs;

import com.DMA173.soulsteps.Charecters.CharecterAssets;
import com.DMA173.soulsteps.Charecters.NPC;
import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.story.GameStateManager;
import com.DMA173.soulsteps.ui.UIManager;
import com.badlogic.gdx.Game;

public class KaelNPC extends NPC {

    public KaelNPC(CharecterAssets assets, int characterType, float startX, float startY) {
        super(assets, characterType, startX, startY, "Kael", "ally");
        this.setDialogue("You need to be more careful. They're watching.");
    }

    @Override
    public void interact(Player player, GameStateManager gsm, UIManager uiManager, Game game) {
        if (!gsm.getFlag("kael_encounter_initiated")) {
            gsm.setFlag("kael_encounter_initiated", true);

            uiManager.showChoice(
                "Hurt Man", // Speaker
                "Hey, you! Please, you have to help me. They're after me... I dropped my keys somewhere around here.", // Prompt
                new String[]{"Help him look for the keys.", "Ignore him and walk away."}, // Choices (Path A, Path B)
                (choice) -> {
                    if (choice == 1) {
                        handlePathA(player, gsm, uiManager, game);
                    } else {
                        handlePathB(player, gsm, uiManager, game);
                    }
                }
            );
        } else {
            // Default dialogue if the player talks to Kael again later.
            uiManager.showNarration(this.getName(), this.getDialogue());
        }
    }

    private void handlePathA(Player player, GameStateManager gsm, UIManager uiManager, Game game) {
        // --- PATH A: HELP THE MAN ---
        player.adjustKindness(10);
        gsm.setFlag("player_helped_kael", true);
        
        // Use showChoice with one option to act like a narration box that waits for input.
        uiManager.showChoice(
            "Elian",
            "Alright, stay here. I'll help you find them.",
            new String[]{"[1] Start looking..."},
            (choice) -> {
                // This code runs AFTER the player presses '1'.
                // --- TRIGGER THE OBJECT FINDER MINIGAME ---
                System.out.println("[STORY] Triggering Object Finder Minigame to find Kael's keys.");
                // game.setScreen(new ObjectFinder(game)); // TODO: Launch the minigame screen
            }
        );
    }

    private void handlePathB(Player player, GameStateManager gsm, UIManager uiManager, Game game) {
        // --- PATH B: IGNORE THE MAN ---
        player.adjustKindness(-15);
        gsm.setFlag("player_ignored_kael", true);
        
        uiManager.showChoice(
            "Elian",
            "Sorry, I can't get involved.",
            new String[]{"[1] Walk away..."},
            (choice) -> {
                uiManager.showChoice(
                    "Narrator",
                    "The man is quickly apprehended by Veridia security. As they drag him away, you notice a file fall from his jacket near a trash bin.",
                    new String[]{"[1] Investigate the file..."},
                    (choice2) -> {
                        // --- TRIGGER THE PAPER PUZZLE MINIGAME ---
                        System.out.println("[STORY] Triggering Paper Puzzle minigame from the discarded file.");
                        // game.setScreen(new PagePuzzle(game, ...)); // TODO: Launch the paper puzzle minigame
                    }
                );
            }
        );
    }
}