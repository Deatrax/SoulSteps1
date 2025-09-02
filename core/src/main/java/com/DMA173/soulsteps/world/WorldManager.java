package com.DMA173.soulsteps.world;

import com.DMA173.soulsteps.Charecters.CharecterAssets;
import com.DMA173.soulsteps.Charecters.NPC;
import com.DMA173.soulsteps.Charecters.NPCManager;
import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.story.GameStateManager;
import com.DMA173.soulsteps.ui.UIManager;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

/**
 * UPDATED WORLD MANAGER
 * 
 * Now supports the story progression system with zone tracking and easier map management.
 * 
 * HOW TO ADD NEW MAPS:
 * 1. Create your .tmx file in assets/[zonename].tmx
 * 2. Add NPCs for the zone in loadNpcsForZone() method below
 * 3. Add map transitions in StoryProgressionManager
 * 
 * EXAMPLE MAP STRUCTURE:
 * - town_square.tmx (main town area)
 * - veridia_interior.tmx (inside Veridia Corp building)
 * - residential_area.tmx (housing district)
 * - police_station.tmx (police station interior)
 * - warehouse.tmx (investigation location)
 */
public class WorldManager {

    private TiledMap currentMap;
    private NPCManager currentNpcManager; // Each zone gets its own NPC manager
    private CharecterAssets characterAssets;
    private GameStateManager gsm;
    private String currentZoneName; // Track which zone we're in

    // MERGED: Add a flag to signal that the map has changed
    private boolean mapChanged = false;

    // --- NEW: Define the name of the layer characters will be on ---
    private final String characterLayerName = "PlayerLayer";

    public WorldManager(CharecterAssets assets) {
        this.characterAssets = assets;
        this.gsm = GameStateManager.getInstance();
        this.currentZoneName = "";
    }

    /**
     * Loads a new zone, including its map and NPCs.
     * UPDATED: Now tracks zone name for story system
     */
    public void loadZone(String zoneId) {
        // Dispose previous map
        if (currentMap != null) {
            currentMap.dispose();
        }

        // Load the map file corresponding to the zone ID
        try {
            currentMap = new TmxMapLoader().load(zoneId + ".tmx");
            currentZoneName = zoneId;
            System.out.println("[WORLD] Loaded zone: " + zoneId);
        } catch (Exception e) {
            System.err.println("[WORLD] Could not load map: " + zoneId + ".tmx. Loading fallback.");
            try {
                currentMap = new TmxMapLoader().load("Tile_City.tmx");
                currentZoneName = "town_square"; // Default fallback
            } catch (Exception fallbackError) {
                System.err.println("[WORLD] Fallback map also failed to load!");
                return;
            }
        }

        // Create a new NPCManager and load the NPCs for this specific zone
        currentNpcManager = new NPCManager(characterAssets);
        loadNpcsForZone(zoneId);

        for (NPC npc : currentNpcManager.getAllNPCs()) {
            npc.setCurrentMapName(zoneId);
        }
        this.mapChanged = true;
    }
    
    /**
     * STEP 6: Define NPCs for each zone
     * 
     * TO ADD NPCs TO NEW MAPS:
     * 1. Add a case for your zone ID
     * 2. Create NPCs with appropriate positions and types
     * 3. Set their dialogue and interaction behavior
     */
    private void loadNpcsForZone(String zoneId) {
        if (currentNpcManager == null) return;
        
        currentNpcManager.clearNPCs(); // Clear previous zone's NPCs
        
        switch (zoneId) {
            case "Tile_City":
                // EXAMPLE: Main town NPCs
                NPC lena = new NPC(characterAssets, 1, 350, 250, "Lena", "ally");
                lena.setDialogue("Elian! I've been looking for you. The water pressure is terrible!");
                currentNpcManager.addNPC(lena);
                
                // EXAMPLE: Add more town NPCs
                /*
                NPC townGuard = new NPC(characterAssets, 2, 600, 400, "Guard Thompson", "police");
                townGuard.setDialogue("Keep an eye out for anything suspicious, citizen.");
                currentNpcManager.addNPC(townGuard);
                
                NPC shopkeeper = new NPC(characterAssets, 3, 200, 350, "Maria", "resident_woman");
                shopkeeper.setDialogue("The water bills have been outrageous lately!");
                currentNpcManager.addNPC(shopkeeper);
                */
                break;
                
            case "veridia_interior":
                // EXAMPLE: Building interior NPCs
                NPC receptionist = new NPC(characterAssets, 2, 200, 150, "Ms. Chen", "veridia_employee");
                receptionist.setDialogue("Welcome to Veridia Corporation. How may I help you?");
                currentNpcManager.addNPC(receptionist);
                
                // EXAMPLE: Add security guard
                /*
                NPC security = new NPC(characterAssets, 1, 350, 180, "Security Chief", "police");
                security.setDialogue("This is a restricted area. Please state your business.");
                currentNpcManager.addNPC(security);
                */
                break;
                
            // EXAMPLE: How to add more zones
            /*
            case "residential_area":
                NPC resident1 = new NPC(characterAssets, 3, 100, 200, "Mrs. Johnson", "resident_formal");
                resident1.setDialogue("The water pressure has been so low lately!");
                currentNpcManager.addNPC(resident1);
                
                NPC resident2 = new NPC(characterAssets, 4, 300, 180, "Bob", "resident_casual");
                resident2.setDialogue("I heard Veridia Corp controls our water supply now.");
                currentNpcManager.addNPC(resident2);
                break;
                
            case "police_station":
                NPC chiefOfPolice = new NPC(characterAssets, 1, 250, 200, "Chief Rodriguez", "police");
                chiefOfPolice.setDialogue("What evidence do you have about this water situation?");
                currentNpcManager.addNPC(chiefOfPolice);
                break;
                
            case "warehouse":
                // Hidden area with key evidence
                NPC whistleblower = new NPC(characterAssets, 5, 400, 300, "Anonymous Worker", "veridia_employee");
                whistleblower.setDialogue("I shouldn't be telling you this, but...");
                currentNpcManager.addNPC(whistleblower);
                break;
            */
                
            default:
                System.out.println("[WORLD] No NPCs defined for zone: " + zoneId);
                break;
        }
        
        System.out.println("[WORLD] Loaded " + currentNpcManager.getAllNPCs().size() + " NPCs for zone '" + zoneId + "'");
    }

    // MERGED: Add these two new methods
    public boolean hasMapChanged() {
        return mapChanged;
    }

    public void confirmMapChange() {
        this.mapChanged = false;
    }


    public void update(float delta) {
        if (currentNpcManager != null) {
            currentNpcManager.update(delta);
        }
    }

    /**
     * Delegates interaction to the current zone's NPCManager.
     */
    public boolean handleInteraction(Player player, UIManager uiManager) { // <-- Add UIManager here
        if (currentNpcManager != null) {
            return currentNpcManager.handleInteraction(player, gsm, uiManager); // <-- Pass it down
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

    // GETTERS
    public TiledMap getCurrentMap() {
        return currentMap;
    }

    public NPCManager getCurrentNpcManager() {
        return currentNpcManager;
    }
    
    /**
     * NEW: Get current zone name for story system
     */
    public String getCurrentZoneName() {
        return currentZoneName;
    }

    public String getCharacterLayerName() {
        return characterLayerName;
    }


    public void dispose() {
        if (currentMap != null) {
            currentMap.dispose();
        }
        if (currentNpcManager != null) {
            currentNpcManager.dispose();
        }
    }
}