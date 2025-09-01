package com.DMA173.soulsteps.world;

import com.DMA173.soulsteps.Charecters.CharecterAssets;
import com.DMA173.soulsteps.Charecters.NPC;
import com.DMA173.soulsteps.Charecters.NPCManager;
import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.story.GameStateManager;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

/**
 * Manages loading game zones (maps) and their specific content like NPCs.
 * It acts as the bridge between the GameStateManager and the visible world.
 */
public class WorldManager {

    private TiledMap currentMap;
    private NPCManager currentNpcManager; // Each zone gets its own NPC manager
    private CharecterAssets characterAssets;
    private GameStateManager gsm;

    public WorldManager(CharecterAssets assets) {
        this.characterAssets = assets;
        this.gsm = GameStateManager.getInstance();
    }

    /**
     * Loads a new zone, including its map and NPCs.
     */
    public void loadZone(String zoneId) {
        if (currentMap != null) {
            currentMap.dispose();
        }

        // Load the map file corresponding to the zone ID
        try {
            currentMap = new TmxMapLoader().load("maps/" + zoneId + ".tmx");
            System.out.println("Loaded zone: " + zoneId);
        } catch (Exception e) {
            System.err.println("Could not load map: maps/" + zoneId + ".tmx. Loading fallback.");
            currentMap = new TmxMapLoader().load("Tile_City.tmx");
        }

        // Create a new NPCManager and load the NPCs for this specific zone
        currentNpcManager = new NPCManager(characterAssets);
        currentNpcManager.loadNpcsForZone(zoneId);
    }

    public void update(float delta) {
        if (currentNpcManager != null) {
            currentNpcManager.update(delta);
        }
    }

    /**
     * Delegates interaction to the current zone's NPCManager.
     */
    public boolean handleInteraction(Player player) {
        if (currentNpcManager != null) {
            return currentNpcManager.handleInteraction(player, gsm);
        }
        return false;
    }

    /**
     * Gets the interaction hint from the current zone's NPCManager.
     */
    public String getInteractionHint(Player player) {
        if (currentNpcManager != null) {
            NPC target = currentNpcManager.getNearbyInteractableNPC(player);
            if (target != null) {
                return "Press E to talk to " + target.getName();
            }
        }
        return null;
    }

    public TiledMap getCurrentMap() {
        return currentMap;
    }

    public NPCManager getCurrentNpcManager() {
        return currentNpcManager;
    }

    public void dispose() {
        if (currentMap != null) {
            currentMap.dispose();
        }
    }
}