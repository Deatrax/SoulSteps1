package com.DMA173.soulsteps.world;

import com.DMA173.soulsteps.Charecters.CharecterAssets;
import com.DMA173.soulsteps.Charecters.NPC;
import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.story.GameState;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldManager {
    private GameState gameState;
    private Player player;
    private CharecterAssets characterAssets;

    private TiledMap currentMap;
    private String currentZoneId;
    private boolean justChangedZones = false;
    
    private Map<String, Rectangle> zoneTransitions;
    private List<NPC> currentNPCs;
    
    public WorldManager(Player player, CharecterAssets assets) {
        this.player = player;
        this.characterAssets = assets;
        this.gameState = GameState.getInstance();
        this.zoneTransitions = new HashMap<>();
        this.currentNPCs = new ArrayList<>();
    }
    
    public void loadZone(String zoneId) {
        if (!gameState.isZoneAccessible(zoneId)) return;
        if (currentMap != null) currentMap.dispose();

        currentZoneId = zoneId;
        try {
            currentMap = new TmxMapLoader().load("maps/" + zoneId + ".tmx");
        } catch (Exception e) {
            System.err.println("CRITICAL: Could not load map: maps/" + zoneId + ".tmx. Loading fallback.");
            currentMap = new TmxMapLoader().load("maps/Tile_City.tmx");
        }
        
        justChangedZones = true;
        loadZoneContent();
    }

    private void loadZoneContent() {
        currentNPCs.clear();
        zoneTransitions.clear();
        
        switch (currentZoneId) {
            case "town_square":
                player.setPosition(400, 300);
                if (!gameState.hasCompletedObjective("beggar_encounter")) {
                    currentNPCs.add(createNPC("Beggar", 350, 280, "resident_casual", "Spare some change?"));
                }
                zoneTransitions.put("residential_area", new Rectangle(0, 250, 32, 100));
                break;
            case "residential_area":
                player.setPosition(600, 300);
                currentNPCs.add(createNPC("Lena", 250, 300, "resident_woman", "Oh Elian, my water pressure is so low..."));
                zoneTransitions.put("town_square", new Rectangle(768, 250, 32, 100));
                break;
        }
    }
    
    // NOTE: The @Override was incorrect here because WorldManager doesn't extend any class with an update method.
    public void update(float delta) {
        // This method is now only responsible for things that move or change over time in the world.
        // Interaction logic has been moved out.
        checkZoneTransition();
        justChangedZones = false;
    }

    public String checkProximityForPrompt() {
        NPC nearbyNpc = findNearbyNpc(player.getPosition(), 60f);
        if (nearbyNpc != null) {
            return "[E] Talk to " + nearbyNpc.getName();
        }
        return null;
    }

    public NPC findNearbyNpc(Vector2 position, float radius) {
        for (NPC npc : currentNPCs) {
            if (position.dst(npc.getPosition()) < radius) {
                return npc;
            }
        }
        return null;
    }

    private void checkZoneTransition() {
        for (Map.Entry<String, Rectangle> entry : zoneTransitions.entrySet()) {
            if (entry.getValue().contains(player.getPosition())) {
                loadZone(entry.getKey());
                return;
            }
        }
    }

    private NPC createNPC(String id, float x, float y, String type, String dialogue) {
        NPC npc = new NPC(this.characterAssets, 1, x, y, id, type);
        npc.setDialogue(dialogue);
        return npc;
    }

    public TiledMap getCurrentMap() { return currentMap; }
    public List<NPC> getCurrentNPCs() { return currentNPCs; }
    public boolean justChangedZones() { return justChangedZones; }
}