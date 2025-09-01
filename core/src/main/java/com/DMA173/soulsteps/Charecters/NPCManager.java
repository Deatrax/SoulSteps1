package com.DMA173.soulsteps.Charecters;

import com.DMA173.soulsteps.story.GameStateManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages NPCs for a SPECIFIC ZONE. It is created and controlled by the WorldManager.
 */
public class NPCManager {
    private List<NPC> npcs;
    private CharecterAssets assets;

    public NPCManager(CharecterAssets assets) {
        this.assets = assets;
        this.npcs = new ArrayList<>();
    }

    /**
     * Loads the NPCs required for a given zone based on its ID.
     */
    public void loadNpcsForZone(String zoneId) {
        npcs.clear(); // Clear out NPCs from the previous zone

        switch (zoneId) {
            case "town_square":
                // Create Lena only, as requested.
                NPC lena = new NPC(assets, 1, 350, 250, "Lena", "ally");
                lena.setDialogue("Elian! I've been looking for you. The water pressure is terrible!");
                npcs.add(lena);
                break;
            
            // You can add more cases for other zones later
            // case "residential_area":
            //     //...
            //     break;
        }
        System.out.println("Loaded " + npcs.size() + " NPCs for zone '" + zoneId + "'.");
    }

    public void update(float delta) {
        for (NPC npc : npcs) {
            npc.update(delta);
        }
    }

    public void render(Batch batch) {
        for (NPC npc : npcs) {
            npc.render(batch);
        }
    }
    
    /**
     * REFACTORED to pass the GameStateManager down to the NPC.
     */
    public boolean handleInteraction(Player player, GameStateManager gsm) {
        NPC target = getNearbyInteractableNPC(player);
        if (target != null) {
            target.interact(player, gsm);
            return true;
        }
        return false;
    }
    
    /**
     * Finds the closest interactable NPC to the player.
     */
    public NPC getNearbyInteractableNPC(Player player) {
        Vector2 playerPos = player.getPosition();
        final float INTERACTION_DISTANCE = 50f;

        for (NPC npc : npcs) {
            if (npc.isInteractable() && npc.getPosition().dst(playerPos) <= INTERACTION_DISTANCE) {
                return npc;
            }
        }
        return null; // No NPC found in range
    }

    public List<NPC> getAllNPCs() {
        return npcs;
    }
}