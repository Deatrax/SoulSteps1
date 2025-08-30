package com.DMA173.soulsteps.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.DMA173.soulsteps.Charecters.CharecterAssets; // We will create this placeholder
import com.DMA173.soulsteps.Charecters.NPC;
import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.story.GameState;
import com.DMA173.soulsteps.ui.DialogueUI;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * WorldManager handles the game world, including zones, NPCs, objectives,
 * and environmental changes based on story progression.
 */
public class WorldManager {

    private GameState gameState;
    private Player player;
    private CharecterAssets characterAssets;
    private DialogueUI dialogueUI;

    private TiledMap currentMap;
    private String currentZoneId;
    private boolean justChangedZones = false;
    
    private Map<String, Rectangle> zoneTransitions;
    private List<NPC> currentNPCs;
    
    public WorldManager(Player player, CharecterAssets assets, DialogueUI dialogueUI) {
        this.player = player;
        this.characterAssets = assets;
        this.dialogueUI = dialogueUI;
        this.gameState = GameState.getInstance();
        
        this.zoneTransitions = new HashMap<>();
        this.currentNPCs = new ArrayList<>();
    }
    
    public void loadZone(String zoneId) {
        if (!gameState.isZoneAccessible(zoneId)) {
            System.out.println("Zone " + zoneId + " is not accessible!");
            return;
        }

        if (currentMap != null) {
            currentMap.dispose();
        }

        currentZoneId = zoneId;
        // IMPORTANT: Make sure you have these TMX files in your assets/maps/ folder
        try {
            currentMap = new TmxMapLoader().load("maps/" + zoneId + ".tmx");
        } catch (Exception e) {
            System.err.println("Could not load map: maps/" + zoneId + ".tmx");
            // Load a default map to prevent crashing
            currentMap = new TmxMapLoader().load("maps/Tile_City.tmx");
        }
        
        System.out.println("Loaded zone: " + zoneId);
        justChangedZones = true;
        
        // Position player at a default spawn point
        player.setPosition(150, 200);

        // Load content for the new zone
        loadZoneContent();
    }

    private void loadZoneContent() {
        currentNPCs.clear();
        zoneTransitions.clear();
        
        // This is where you define what exists in each zone based on the story
        switch (currentZoneId) {
            case "town_square":
                player.setPosition(400, 300);
                // Add beggar NPC only if the choice hasn't been made
                if (!gameState.getFlag("gave_money_to_beggar")) {
                     currentNPCs.add(createNPC("beggar", 350, 280, "resident_casual", "Spare some change?"));
                }
                // Define a transition area to the residential zone
                zoneTransitions.put("residential_area", new Rectangle(0, 250, 32, 100));
                break;

            case "residential_area":
                player.setPosition(600, 300);
                // Add Lena
                String lenaDialogue = gameState.getFlag("found_first_limiter") ?
                    "Thank you for your help, Elian!" : "My water pressure is so low...";
                currentNPCs.add(createNPC("lena", 250, 300, "resident_woman", lenaDialogue));
                
                // Transition back to town square
                zoneTransitions.put("town_square", new Rectangle(768, 250, 32, 100));
                break;
        }
    }
    
    public void update(float delta) {
        // Update all NPCs in the current zone
        for (NPC npc : currentNPCs) {
            npc.update(delta);
        }
        
        // Check for zone transitions after updating positions
        checkZoneTransition();
        
        // Reset the zone change flag after one frame
        justChangedZones = false;
    }

    public void handleInteraction() {
        // Find the closest interactable NPC
        NPC targetNpc = findNearbyNpc(player.getPosition(), 60f); // 60f is interaction radius

        if (targetNpc != null) {
            System.out.println("Interacting with: " + targetNpc.getName());
            
            // Handle specific story interactions
            if (targetNpc.getName().equals("beggar")) {
                dialogueUI.showChoice("A beggar approaches you.",
                    new String[]{"Give money", "Refuse"},
                    (choice) -> {
                        gameState.makeChoice("beggar_encounter", choice);
                        if (choice == 1) player.adjustKindness(5); else player.adjustKindness(-5);
                        targetNpc.setInteractable(false); // Can't interact again
                    });
            } else {
                 // For all other NPCs, just show their dialogue
                dialogueUI.showNarration(targetNpc.getName() + ": " + targetNpc.getDialogue());
            }
        }
    }

    private void checkZoneTransition() {
        for (Map.Entry<String, Rectangle> entry : zoneTransitions.entrySet()) {
            if (entry.getValue().contains(player.getPosition().x, player.getPosition().y)) {
                String targetZone = entry.getKey();
                System.out.println("Player entered transition to: " + targetZone);
                loadZone(targetZone);
                return; // Exit after first transition to prevent multiple loads
            }
        }
    }
    
    private NPC findNearbyNpc(Vector2 position, float radius) {
        NPC closestNpc = null;
        float closestDist = Float.MAX_VALUE;

        for (NPC npc : currentNPCs) {
            float dist = position.dst(npc.getPosition());
            if (dist < radius && dist < closestDist) {
                closestDist = dist;
                closestNpc = npc;
            }
        }
        return closestNpc;
    }

    private NPC createNPC(String id, float x, float y, String type, String dialogue) {
        NPC npc = new NPC(this.characterAssets, 1, x, y, id, type); // Using characterType 1 as default
        npc.setDialogue(dialogue);
        return npc;
    }

    // --- Getters ---
    public TiledMap getCurrentMap() { return currentMap; }
    public List<NPC> getCurrentNPCs() { return currentNPCs; }
    public boolean justChangedZones() { return justChangedZones; }
}