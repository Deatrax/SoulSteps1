package com.DMA173.soulsteps.Charecters;

import java.util.ArrayList;
import java.util.List;

import com.DMA173.soulsteps.story.GameStateManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

/**
 * UPDATED NPC MANAGER
 * 
 * Now supports dynamic NPC loading and better integration with story system.
 * Each zone can have different NPCs loaded dynamically.
 */
public class NPCManager {
    private List<NPC> npcs;
    private CharecterAssets assets;

    public NPCManager(CharecterAssets assets) {
        this.assets = assets;
        this.npcs = new ArrayList<>();
    }

    /**
     * DEPRECATED: This method is now replaced by WorldManager.loadNpcsForZone()
     * Keeping for backward compatibility, but NPCs should be loaded via WorldManager
     */
    @Deprecated
    public void loadNpcsForZone(String zoneId) {
        // This method is now handled by WorldManager
        // See WorldManager.loadNpcsForZone() for NPC definitions
    }
    
    /**
     * Add a single NPC to this manager
     * Used by WorldManager when setting up zones
     */
    public void addNPC(NPC npc) {
        npcs.add(npc);
    }
    
    /**
     * Clear all NPCs (used when switching zones)
     */
    public void clearNPCs() {
        npcs.clear();
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
     * Handle interaction with NPCs in this zone
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
    
    /**
     * Find NPC by name (useful for story-specific interactions)
     */
    public NPC getNPCByName(String name) {
        for (NPC npc : npcs) {
            if (npc.getName().equals(name)) {
                return npc;
            }
        }
        return null;
    }
    
    /**
     * Check if a specific NPC exists in this zone
     */
    public boolean hasNPC(String name) {
        return getNPCByName(name) != null;
    }
    
    /**
     * Remove an NPC by name (useful for story events)
     */
    public boolean removeNPC(String name) {
        NPC npc = getNPCByName(name);
        if (npc != null) {
            npcs.remove(npc);
            System.out.println("[NPC] Removed NPC: " + name);
            return true;
        }
        return false;
    }
    
    /**
     * Get count of NPCs in this zone
     */
    public int getNPCCount() {
        return npcs.size();
    }
    
    public void dispose() {
        // NPCs don't need disposal, but keeping method for future use
        npcs.clear();
    }
}