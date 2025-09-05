package com.DMA173.soulsteps.world;

import com.DMA173.soulsteps.Charecters.CharecterAssets;
import com.DMA173.soulsteps.Charecters.NPC;
import com.DMA173.soulsteps.Charecters.NPCManager;
import com.DMA173.soulsteps.Charecters.NPCs.KaelNPC;
import com.DMA173.soulsteps.Charecters.NPCs.beggerNPC;
import com.DMA173.soulsteps.Charecters.NPCs.manager;
import com.DMA173.soulsteps.Charecters.NPCs.receptionist;
import com.DMA173.soulsteps.Charecters.NPCs.vandalTeenNPC;
import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.story.GameStateManager;
import com.DMA173.soulsteps.story.StoryProgressionManager;
import com.DMA173.soulsteps.ui.UIManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;

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
    KaelNPC kael;
    Game gam;
    StoryProgressionManager story;
    ScreenAdapter screen;
    // --- NEW: A flag to track if an in-game cutscene is active ---
    private boolean isCutsceneActive = false;

    // --- NEW: Public methods to control the cutscene state ---
    public void startCutscene() {
        this.isCutsceneActive = true;
        System.out.println("[WORLD] Cutscene started.");
    }

    public void endCutscene() {
        this.isCutsceneActive = false;
        System.out.println("[WORLD] Cutscene ended.");
    }

    public boolean isCutsceneActive() {
        return isCutsceneActive;
    }


    public void setStory(StoryProgressionManager story) {
        this.story = story;
    }

    private String currentZoneName; // Track which zone we're in

    // MERGED: Add a flag to signal that the map has changed
    private boolean mapChanged = false;

    // --- NEW: Define the name of the layer characters will be on ---
    private final String characterLayerName = "PlayerLayer";
     
    // --- NEW: A variable to remember the last zone we were in ---
    private String previousZoneName;

    public WorldManager(CharecterAssets assets) {
        this.characterAssets = assets;
        this.gsm = GameStateManager.getInstance();
        this.currentZoneName = "";
        this.previousZoneName = ""; // Initialize as empty
    }

    /**
     * Loads a new zone, including its map and NPCs.
     * UPDATED: Now tracks zone name for story system
     */
    public void loadZone(String zoneId) {

        // --- NEW: Remember the current zone before changing it ---
        this.previousZoneName = this.currentZoneName;

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
                
                beggerNPC begger = new beggerNPC(characterAssets, 5, 435, 300, "begger", "begger");
                begger.setDialogue("Please help a man in need");
                currentNpcManager.addNPC(begger);

                // The NPC is created at position (200, 150)
                /*  NPC receptionist = new NPC(characterAssets, 2, 200, 150, "Ms. Chen", "veridia_employee");

                    // Define the path for the NPC to walk
                    receptionist.walkPath(
                        50f,  // Speed
                        true, // Tell the NPC to loop this path
                        
                        // --- THIS IS THE FIX ---
                        // Point 1: Walk from the start (200, 150) to the destination (50, 150)
                        new Vector2(50, 150),  
                        
                        // Point 2: Walk from (50, 150) back to the original starting point (200, 150)
                        new Vector2(200, 150)  
                        // --- END OF FIX ---
                    );
                receptionist.setDialogue("Welcome to Veridia Corporation. How may I help you?");
                currentNpcManager.addNPC(receptionist);*/

                NPC deliveryman1 = new NPC(characterAssets, 3, 610, 127, "Delivery Man", "delivery_person", false);
                deliveryman1.walkPath(
                    50f, // Speed
                    false,
                    new Vector2(50, 127)  // Then back to the start (to loop, you'd need more logic)
                );
                currentNpcManager.addNPC(deliveryman1);

                if (/*gsm.hasCompletedObjective("goToDanHouse")*/ true) {
                    vandalTeenNPC teen1 = new vandalTeenNPC(characterAssets, 4, 146, 249, "Vandal", "resident_casual");
                    
                    // --- USE THE NEW METHOD ---
                    teen1.performEffect("spray_effect"); // The NPC will now show its idle animation with the spray effect on top.
                    
                    // You can even set their facing direction
                    teen1.setCurrentDir(CharecterAssets.Direction.LEFT); // Make them face a wall
                    
                    currentNpcManager.addNPC(teen1);
                }

                if(gsm.hasCompletedObjective("talked_to_rep")){
                    kael =new KaelNPC(characterAssets, 920, 1152, gam, this, story);
                    
                    currentNpcManager.addNPC(kael);
                }

                
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
                
            case "office/office":
                receptionist receptionist = new receptionist(characterAssets, 2, 380, 340, "Ms. Chen", "veridia_employee");

                // Define the path for the NPC to walk
                receptionist.walkPath(
                    50f,  // Speed
                    true, // Tell the NPC to loop this path
                    
                    // --- THIS IS THE FIX ---
                    // Point 1: Walk from the start (200, 150) to the destination (50, 150)
                    new Vector2(960, 340),  
                    
                    // Point 2: Walk from (50, 150) back to the original starting point (200, 150)
                    new Vector2(380, 340)  
                    // --- END OF FIX ---
                );
               // receptionist.setDialogue("Welcome to Veridia Corporation. How may I help you?");
                currentNpcManager.addNPC(receptionist);
                // EXAMPLE: Building interior NPCs
              /*   NPC receptionist = new NPC(characterAssets, 2, 200, 150, "Ms. Chen", "veridia_employee");
                receptionist.setDialogue("Welcome to Veridia Corporation. How may I help you?");
                currentNpcManager.addNPC(receptionist);*/
                
                // EXAMPLE: Add security guard
                /*
                NPC security = new NPC(characterAssets, 1, 350, 180, "Security Chief", "police");
                security.setDialogue("This is a restricted area. Please state your business.");
                currentNpcManager.addNPC(security);
                */
                

            manager Manager = new manager(characterAssets, 5, 951, 894, "Manager", "veridia_employee");
                Manager.setDialogue("Please help a man in need");
                currentNpcManager.addNPC(Manager);
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

      // --- NEW: A public getter for the previous zone name ---
    public String getPreviousZoneName() {
        return previousZoneName;
    }
    

    public void confirmMapChange() {
        this.mapChanged = false;
    }


    public void update(float delta) {
         handleCutsceneEvents();
        if (currentNpcManager != null) {
            currentNpcManager.update(delta);
        }
    }

    // --- NEW: Add this entire new method to manage cutscene logic ---
    private void handleCutsceneEvents() {
        // --- Kael's Abduction Cutscene ---
        // This checks if the flag for the cutscene has been set and the cutscene isn't already running.
        if (gsm.getFlag("player_ignored_kael") && !gsm.getFlag("kael_abduction_cutscene_started")) {
            
            // Set a new flag to ensure this block only runs once.
            gsm.setFlag("kael_abduction_cutscene_started", true);
            startCutscene();

            // Find Kael in the current zone.
            NPC kael = currentNpcManager.getNPCByName("Kael");
            if (kael == null) {
                endCutscene(); // Can't run the cutscene if Kael isn't there.
                return;
            }

            // 1. Spawn two guards off-screen.
            System.out.println("[CUTSCENE] Spawning guards for abduction.");
            NPC guard1 = new NPC(characterAssets, 1, -50, kael.getPosition().y, "Guard", "police");
            NPC guard2 = new NPC(characterAssets, 1, -50, kael.getPosition().y + 20, "Guard", "police");
            currentNpcManager.addNPC(guard1);
            currentNpcManager.addNPC(guard2);

            // 2. Command the guards to walk to Kael.
            float guardSpeed = 80f; // Guards move with purpose.
            guard1.walkPath(guardSpeed, false, new Vector2(kael.getPosition().x - 20, kael.getPosition().y));
            guard2.walkPath(guardSpeed, false, new Vector2(kael.getPosition().x + 20, kael.getPosition().y));
            
            // 3. Command Kael to play an idle animation (he's cornered).
            kael.stopAction(); // Ensure he's not doing anything else.

            // Set a flag that the next stage of the cutscene is ready.
            gsm.setFlag("guards_approaching_kael", true);
        }

        // --- Second stage of the cutscene: The guards have arrived ---
        if (gsm.getFlag("guards_approaching_kael") && !gsm.getFlag("kael_being_escorted")) {
            NPC guard1 = currentNpcManager.getNPCByName("Guard"); // Just gets the first one
            
            // We check if the guards have finished their path (i.e., they are idle).
            if (guard1 != null && guard1.getCurrentState() == NPC.NpcState.IDLE) {
                
                // Set a new flag to prevent this from re-triggering.
                gsm.setFlag("kael_being_escorted", true);

                // Find all the relevant NPCs again.
                NPC kael = currentNpcManager.getNPCByName("Kael");
                NPC guard2 = currentNpcManager.getNPCByName("Guard", 1); // Helper method needed to get the second guard

                // 4. Command all three to walk off-screen to the left.
                System.out.println("[CUTSCENE] Escorting Kael off-screen.");
                float escortSpeed = 60f;
                kael.walkPath(escortSpeed, false, new Vector2(-100, kael.getPosition().y));
                guard1.walkPath(escortSpeed, false, new Vector2(-120, guard1.getPosition().y));
                if (guard2 != null) {
                     guard2.walkPath(escortSpeed, false, new Vector2(-80, guard2.getPosition().y));
                }

                // Set a flag for the final stage.
                gsm.setFlag("kael_escort_finished_check", true);
            }
        }

        // --- Final stage of the cutscene: They have walked off-screen ---
        if (gsm.getFlag("kael_escort_finished_check")) {
            NPC kael = currentNpcManager.getNPCByName("Kael");
            
            // When Kael is off-screen and has finished his path, the cutscene is over.
            if (kael == null || kael.getCurrentState() == NPC.NpcState.IDLE) {
                // Remove the NPCs from the world.
                currentNpcManager.removeNPC("Kael");
                currentNpcManager.removeNPC("Guard");
                currentNpcManager.removeNPC("Guard"); // Remove both
                
                // End the cutscene and reset the flags.
                endCutscene();
                gsm.setFlag("kael_escort_finished_check", false); // Prevent re-triggering
            }
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

    public void completeObjective(String str){
        gsm.completeObjective(str);
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

    public GameStateManager getGsm() {
        return gsm;
    }

    public void setGsm(GameStateManager gsm) {
        this.gsm = gsm;
    }


    public void dispose() {
        if (currentMap != null) {
            currentMap.dispose();
        }
        if (currentNpcManager != null) {
            currentNpcManager.dispose();
        }
    }

    public void setGam(Game gam) {
        this.gam = gam;
    }

    public ScreenAdapter getScreen() {
        return screen;
    }

    public void setScreen(ScreenAdapter screen) {
        this.screen = screen;
    }
}