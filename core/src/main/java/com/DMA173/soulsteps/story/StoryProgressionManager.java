package com.DMA173.soulsteps.story;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.DMA173.soulsteps.Charecters.NPC;
import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.story.StoryProgressionManager.MapTransition;
import com.DMA173.soulsteps.ui.UIManager;
import com.DMA173.soulsteps.world.WorldManager;
import com.badlogic.gdx.math.Vector2;

/**
 * STORY PROGRESSION SYSTEM
 * 
 * This system manages the entire story flow, objectives, and map transitions.
 * It's designed to be easily extendable without modifying core game files.
 * 
 * HOW TO ADD NEW STORY CONTENT:
 * 
 * 1. ADD NEW OBJECTIVES:
 *    - Add objective ID to initializeObjectives()
 *    - Add objective text to getObjectiveText()
 *    - Add completion logic to checkObjectiveCompletion()
 * 
 * 2. ADD NEW MAPS:
 *    - Create your .tmx map file in assets/maps/
 *    - Add map transition zones in initializeMapTransitions()
 *    - Add NPCs for the new map in WorldManager.loadNpcsForZone()
 * 
 * 3. ADD NPC INTERACTIONS:
 *    - Modify NPC.interact() method to check story state
 *    - Use GameStateManager flags to track conversation progress
 * 
 * EXAMPLE STORY FLOW:
 * Start → Talk to Lena → Find Evidence → Enter Building → Talk to Suspect → Confront Villain
 */
public class StoryProgressionManager {
    private GameStateManager gameState;
    private UIManager uiManager;
    private WorldManager worldManager;
    
    // Current story state
    private String currentObjective;
    private List<String> objectiveOrder;
    private Map<String, String> objectiveTexts;
    private Map<String, MapTransition> mapTransitions;

    // --- NEW: A map to store the world coordinates for each objective ---
    private Map<String, Vector2> objectiveLocations;
    
    //flag for if the game was loaded from a saved state or file
    private Boolean isContinuedStory;

    // --- NEW: A map to store all our programmatic dialogue triggers ---
    private Map<String, DialogueTrigger> dialogueTriggers;

    // --- NEW: A map to store all our automatic dialogue triggers ---
    private Map<String, AutoDialogueTrigger> autoDialogueTriggers;
    
    public Boolean getIsContinuedStory() {
        return isContinuedStory;
    }

    public void setIsContinuedStory(Boolean isContinuedStory) {
        this.isContinuedStory = isContinuedStory;
    }
    

    // --- MODIFY THE CONSTRUCTOR ---
    public StoryProgressionManager(UIManager uiManager, WorldManager worldManager) {
        this.gameState = GameStateManager.getInstance();
        this.uiManager = uiManager;
        this.worldManager = worldManager;
        
        initializeStorySystem();

        // --- NEW: Add the initialization for objective locations ---
        this.objectiveLocations = new HashMap<>();
        initializeObjectiveLocations();

        // --- NEW: Initialize the dialogue triggers ---
        this.dialogueTriggers = new HashMap<>();
        initializeDialogueTriggers();

        this.autoDialogueTriggers = new HashMap<>(); // Initialize the new map
        initializeAutoDialogueTriggers();
    }


    public StoryProgressionManager(UIManager uiManager, WorldManager worldManager, Boolean continued) {
        this.gameState = GameStateManager.getInstance();
        this.uiManager = uiManager;
        this.worldManager = worldManager;
        isContinuedStory = continued;
        initializeStorySystem();
    }
    
    private void initializeStorySystem() {
        initializeObjectives();
        initializeMapTransitions();
        startStory();
    }
    
    /**
     * STEP 1: Define all story objectives in order
     * 
     * TO ADD NEW OBJECTIVES:
     * 1. Add the objective ID to this list
     * 2. Add the display text in getObjectiveText()
     * 3. Add completion logic in checkObjectiveCompletion()
     */
    private void initializeObjectives() {
        objectiveOrder = new ArrayList<>();
        objectiveTexts = new HashMap<>();
        
        // EXAMPLE STORY FLOW - MODIFY THIS TO ADD YOUR STORY:

        //Objective 0: monologue
        objectiveOrder.add("introMonologue");
        objectiveTexts.put("introMonologue", "");
        
        // Objective 1: Tutorial/Introduction
        objectiveOrder.add("goToDanHouse");
        objectiveTexts.put("goToDanHouse", "Go to Dan's house to check his plumbing");

        // //Objective 2: Fix dan's plumbing
        // objectiveOrder.add("fixDanPlumbing");
        // objectiveTexts.put("fixDanPlumbing", "Go to Dan's house to check his plumbing");

        objectiveOrder.add("gotolenahouse");
        objectiveTexts.put("gotolenahouse","Now go to fix Lena's kitchen plumbing");
        
        
        
        // EXAMPLE: How to add more objectives
        /*
        objectiveOrder.add("confront_ceo");
        objectiveTexts.put("confront_ceo", "Confront the CEO with evidence");
        
        objectiveOrder.add("escape_building");
        objectiveTexts.put("escape_building", "Escape the Veridia Corporation building");
        
        objectiveOrder.add("report_to_authorities");
        objectiveTexts.put("report_to_authorities", "Report your findings to the authorities");
        */
        
        System.out.println("[STORY] Initialized " + objectiveOrder.size() + " objectives");
    }


    // --- NEW: Add this entire new method ---
    private void initializeObjectiveLocations() {
        System.out.println("[STORY] Initializing objective waypoint locations...");

        // Define the X, Y world coordinates for the target of each objective.
        // These should match the locations in your Tiled maps.

        // Objective: "talk_to_lena_first_time" -> The location of Lena in the 'town_square' map.
        //objectiveLocations.put("talk_to_lena_first_time", new Vector2(350, 250));
        
        objectiveLocations.put("goToDanHouse", new Vector2(750,550));
        // As you add new objectives, you can add their target locations here.
        // If an objective doesn't have a specific location (e.g., "collect 3 items"),
        // you simply don't add an entry for it.
    }


    // --- NEW: Add this entire new method to define your triggers ---
    private void initializeDialogueTriggers() {
        System.out.println("[STORY] Initializing programmatic dialogue triggers...");

        // EXAMPLE: A trigger to find the limiter at the main water valve.
        DialogueTrigger findLimiterTrigger = new DialogueTrigger();
        findLimiterTrigger.zone = "town_square"; // This trigger is in the main town
        findLimiterTrigger.eventId = "found_limiter_device"; // This MUST match a case in triggerStoryEvent()
        findLimiterTrigger.triggerArea = new Vector2(800, 400); // The coordinates of the water valve
        findLimiterTrigger.triggerRadius = 35f;
        findLimiterTrigger.interactionText = "Press E to Examine Valve";
        findLimiterTrigger.isOneTime = true; // It should only be findable once
        findLimiterTrigger.requiredObjective = "investigate_water_system"; // Only appears during this quest
        
        // Add it to our map with a unique key
        dialogueTriggers.put("valve_investigation", findLimiterTrigger);

        // --- FUTURE: Add more triggers here ---
        /*
        DialogueTrigger loreNoteTrigger = new DialogueTrigger();
        loreNoteTrigger.zone = "interior";
        loreNoteTrigger.eventId = "read_lore_note";
        loreNoteTrigger.triggerArea = new Vector2(250, 180); // A desk inside the house
        loreNoteTrigger.triggerRadius = 30f;
        loreNoteTrigger.interactionText = "Press E to Read Note";
        loreNoteTrigger.isOneTime = false; // Player can re-read it
        loreNoteTrigger.requiredObjective = null; // Always available
        dialogueTriggers.put("dan_house_lore_note", loreNoteTrigger);
        */
    }

    // --- NEW: Add this entire new method to define your auto-triggers ---
    private void initializeAutoDialogueTriggers() {
        System.out.println("[STORY] Initializing automatic dialogue triggers...");

        // EXAMPLE: A trigger where Lena calls out to the player as they walk by.
        AutoDialogueTrigger lenaCallout = new AutoDialogueTrigger();
        lenaCallout.zone = "town_square";
        lenaCallout.npcName = "Lena"; // The NPC who will initiate the dialogue
        lenaCallout.triggerArea = new Vector2(400, 280); // An area near Lena
        lenaCallout.triggerRadius = 60f;
        lenaCallout.isOneTime = true;
        // This trigger will only fire if the player has NOT yet talked to Lena.
        lenaCallout.requiredObjective = "talk_to_lena_first_time"; 
        
        autoDialogueTriggers.put("lena_initial_callout", lenaCallout);

        // --- FUTURE EXAMPLE ---
        /*
        AutoDialogueTrigger guardWarning = new AutoDialogueTrigger();
        guardWarning.zone = "veridia_interior";
        guardWarning.npcName = "Security Chief";
        guardWarning.triggerArea = new Vector2(300, 150);
        guardWarning.triggerRadius = 50f;
        guardWarning.isOneTime = false; // The guard can warn you every time you get too close
        guardWarning.requiredObjective = "discover_water_limiter"; // Only happens after you've found evidence
        autoDialogueTriggers.put("veridia_guard_warning", guardWarning);
        */
    }
    
    


    // --- NEW: Add this public getter method ---
    /**
     * Returns the world coordinates of the current objective's target.
     * Returns null if the current objective has no specific location.
     */
    public Vector2 getCurrentObjectiveLocation() {
        if (currentObjective != null) {
            return objectiveLocations.get(currentObjective);
        }
        return null;
    }
    
    /**
     * STEP 2: Define map transitions (doors, exits, zone changes)
     * 
     * TO ADD NEW MAP TRANSITIONS:
     * 1. Create your map file in assets/maps/[mapname].tmx
     * 2. Add the transition here with source zone, target zone, and trigger area
     * 3. Set the spawn position for the new map
     */
    private void initializeMapTransitions() {
        mapTransitions = new HashMap<>();
        

        MapTransition enterDansHouse = new MapTransition();
        enterDansHouse.fromZone = "Tile_City";
        enterDansHouse.toZone = "interior";
        enterDansHouse.triggerArea = new Vector2(750, 550); // Position of building door
        enterDansHouse.triggerRadius = 30f;
        enterDansHouse.spawnPosition = new Vector2(100, 100); // Where player spawns in new map
        enterDansHouse.requiredObjective = "goToDanHouse"; // Must have this objective active
        enterDansHouse.interactionText = "Press E to enter Dan's House";
        mapTransitions.put("town_to_Dans_house", enterDansHouse);


        MapTransition exitDanHouse = new MapTransition();
        exitDanHouse.fromZone = "interior";
        exitDanHouse.toZone = "Tile_City";
        exitDanHouse.triggerArea = new Vector2(80, 93);
        exitDanHouse.triggerRadius = 100f;
        exitDanHouse.spawnPosition = new Vector2(740, 545);
        exitDanHouse.requiredObjective = "gotolenahouse";
        exitDanHouse.interactionText = "Press E to exit Dan's House";
        mapTransitions.put("sdfhkshjdfsdf", exitDanHouse);



       



        // EXAMPLE TRANSITIONS - MODIFY/ADD YOUR OWN:
        
        // From town_square to veridia_interior (entering building)
        // MapTransition enterBuilding = new MapTransition();
        // enterBuilding.fromZone = "town_square";
        // enterBuilding.toZone = "office";
        // enterBuilding.triggerArea = new Vector2(500, 300); // Position of building door
        // enterBuilding.triggerRadius = 30f;
        // enterBuilding.spawnPosition = new Vector2(100, 100); // Where player spawns in new map
        // enterBuilding.requiredObjective = "enter_veridia_building"; // Must have this objective active
        // enterBuilding.interactionText = "Press E to enter Veridia Corporation";
        // mapTransitions.put("town_to_veridia", enterBuilding);
        
        
        // EXAMPLE: How to add more map transitions
        /*
        // From town_square to residential_area
        MapTransition toResidential = new MapTransition();
        toResidential.fromZone = "town_square";
        toResidential.toZone = "residential_area";
        toResidential.triggerArea = new Vector2(800, 400);
        toResidential.triggerRadius = 40f;
        toResidential.spawnPosition = new Vector2(50, 200);
        toResidential.requiredObjective = null; // Always accessible
        toResidential.interactionText = "Press E to enter residential district";
        mapTransitions.put("town_to_residential", toResidential);
        
    
        */
        
        System.out.println("[STORY] Initialized " + mapTransitions.size() + " map transitions");
    }
    
    private void startStory() {
        // Set the first objective
        if (!objectiveOrder.isEmpty()) {
            currentObjective = objectiveOrder.get(0);
            uiManager.updateObjective(getObjectiveText(currentObjective));
            //uiManager.showNarration("Narrator...", "Athelgard, once a thriving town with jolly people is now slowly\ntaking a dark turn. Captured into capitalistic greed the town is now seeing\na case of resource scarcity. You are elian, a plumber trying to live life without mich ruckus.\nHowever your choices will now seal the face of the town forever");
            uiManager.showChoice(
                "Narrator...", 
                "Athelgard, once a thriving town with jolly people is now slowly\ntaking a dark turn. Captured into capitalistic greed the town is now seeing\na case of resource scarcity. You are elian, a plumber trying to live life without mich ruckus.\nHowever your choices will now seal the face of the town forever", 
                new String[] { "Press [1] to continue" }, // Choices
                (choice) -> {
                    // This code runs AFTER the player makes a choice from the dialogue box.
                    if (choice == 1) { 
                        
                        gameState.completeObjective("introMonologue");
                        
                        System.err.println("[narrator] Player pressed [1] ");
                        // Give the player their next objective via a narration box
                        uiManager.showNarration(null, "Press aswd / arrow keys to move, E to interact. You now will go to dan's house to check his kitchen plumbing");
                        
                    }
                }
            );
        }
        System.out.println("[STORY] Story started with objective: " + currentObjective);
    }
    
    /**
     * STEP 3: Define what each objective's display text should be
     * 
     * TO ADD OBJECTIVE TEXT:
     * Add a case for your objective ID and return the text to display
     */
    private String getObjectiveText(String objectiveId) {
        return objectiveTexts.getOrDefault(objectiveId, "Unknown objective");
    }
    
    /**
     * MAIN UPDATE METHOD
     * Call this every frame to check for story progression
     */
    public void update(float delta, Player player) {
        checkObjectiveCompletion(player);
        //checkMapTransitions(player);

        checkAutoDialogueTriggers(player);
    }
    
    /**
     * STEP 4: Define how each objective gets completed
     * 
     * TO ADD OBJECTIVE COMPLETION LOGIC:
     * 1. Add a case for your objective ID
     * 2. Check the condition for completion
     * 3. Call completeCurrentObjective() when condition is met
     */
    private void checkObjectiveCompletion(Player player) {
        if (currentObjective == null) return;
        
        switch (currentObjective) {
            case "introMonologue":
                if(gameState.hasCompletedObjective("introMonologue")){
                    completeCurrentObjective();
                }
                break;

            // case "talk_to_lena_first_time":
            //     // This gets completed in NPC.interact() when talking to Lena
            //     // No additional check needed here
            //     break;
                
            
            // EXAMPLE for player
            // case "find_first_evidence":
            //     // Complete when player collects evidence (using debug key L or real evidence)
            //     if (player.getEvidenceCount() > 0) {
            //         completeCurrentObjective();
            //     }
            //     break;
                
            // case "enter_veridia_building":
            //     // This gets completed by map transition system
            //     // No additional check needed here
            //     break;
                
                
            // case "discover_water_limiter":
            //     // Complete when player finds the water limiter device
            //     if (player.hasWaterLimiter()) {
            //         completeCurrentObjective();
            //     }
            //     break;
                
            // EXAMPLE: How to add more objective completion conditions
            /*
            case "confront_ceo":
                // Complete when specific flag is set after CEO conversation
                if (gameState.getFlag("ceo_confronted")) {
                    completeCurrentObjective();
                }
                break;
                
            case "escape_building":
                // Complete when player exits building after confrontation
                if (gameState.getFlag("ceo_confronted") && 
                    worldManager.getCurrentZone().equals("town_square")) {
                    completeCurrentObjective();
                }
                break;
                
            case "report_to_authorities":
                // Complete when talking to police NPC
                if (gameState.hasCompletedObjective("reported_to_police")) {
                    completeCurrentObjective();
                }
                break;
            */
        }
    }


    // --- NEW: Add this entire new method to handle the checking logic ---
    private void checkAutoDialogueTriggers(Player player) {
        // We cannot fire a new auto-dialogue if one is already active.
        if (uiManager.isDialogueActive()) {
            return;
        }

        String currentZone = worldManager.getCurrentZoneName();
        Vector2 playerPos = player.getPosition();

        for (AutoDialogueTrigger trigger : autoDialogueTriggers.values()) {
            // Check if the trigger is for the current zone and the player is inside its radius
            if (trigger.zone.equals(currentZone) && playerPos.dst(trigger.triggerArea) <= trigger.triggerRadius) {
                
                // Check if objective requirement is met
                if (trigger.requiredObjective != null && !isObjectiveActive(trigger.requiredObjective)) {
                    continue; // Skip if the wrong quest is active
                }

                // Check if it's a one-time trigger that has already been used
                String triggerFlag = "auto_triggered_" + trigger.npcName;
                if (trigger.isOneTime && gameState.getFlag(triggerFlag)) {
                    continue; // Skip if already fired
                }

                // --- FIRE THE DIALOGUE ---
                // Find the NPC in the world who is supposed to speak.
                NPC speaker = worldManager.getCurrentNpcManager().getNPCByName(trigger.npcName);
                if (speaker != null) {
                    System.out.println("[STORY] Firing auto-dialogue for NPC: " + speaker.getName());
                    
                    // Use the NPC's own interact method to show their dialogue.
                    // This reuses your existing story logic perfectly!
                    speaker.interact(player, gameState, uiManager);

                    // If it's a one-time event, set the flag so it doesn't fire again.
                    if (trigger.isOneTime) {
                        gameState.setFlag(triggerFlag, true);
                    }
                }
                
                // We only fire one auto-trigger per frame to avoid conflicts.
                return;
            }
        }
    }
    
    /**
     * STEP 5: Check for map transitions (doors, zone changes)
     * 
     * This automatically handles map transitions when player approaches trigger areas
     */
    /**
     * DEPRECATED: This method is being replaced by the new getInteractionHint().
     * You can delete this old method.
     */
    // private void checkMapTransitions(Player player) { ... }
    
    /**
     * NEW, UNIFIED HINT SYSTEM
     * This is now the single source of truth for all interaction hints.
     * It prioritizes map transitions over NPC dialogue.
     * @param player The player character.
     * @return The text for the interaction hint, or null if nothing is in range.
     * Now checks for Dialogue Triggers, then Map Transitions, then NPCs.
     */
    public String getInteractionHint(Player player) {
        String currentZone = worldManager.getCurrentZoneName();
        Vector2 playerPos = player.getPosition();

        // 1. Check for Dialogue Triggers (highest priority)
        for (DialogueTrigger trigger : dialogueTriggers.values()) {
            // Check if the trigger is in the current zone and in range
            if (trigger.zone.equals(currentZone) && playerPos.dst(trigger.triggerArea) <= trigger.triggerRadius) {
                // Check if the objective requirement is met
                if (trigger.requiredObjective == null || isObjectiveActive(trigger.requiredObjective)) {
                    // Check if it's a one-time trigger that has already been used
                    if (trigger.isOneTime && gameState.getFlag("triggered_" + trigger.eventId)) {
                        continue; // Skip this one, it's been used
                    }
                    return trigger.interactionText;
                }
            }
        }

        // 2. Check for Map Transitions
        for (MapTransition transition : mapTransitions.values()) {
            if (transition.fromZone.equals(currentZone) && playerPos.dst(transition.triggerArea) <= transition.triggerRadius) {
                if (transition.requiredObjective == null || isObjectiveActive(transition.requiredObjective)) {
                    return transition.interactionText;
                }
            }
        }

        // 3. If nothing else, check for NPCs.
        return worldManager.getInteractionHint(player);
    }

    
    
    
    /**
     * Handle map transition when player presses E near a door/exit
     * Call this from InputHandler when E is pressed
     */
    public boolean handleMapTransition(Player player) {
        String currentZone = worldManager.getCurrentZoneName();
        Vector2 playerPos = player.getPosition();
        
        for (Map.Entry<String, MapTransition> entry : mapTransitions.entrySet()) {
            MapTransition transition = entry.getValue();
            
            // Check if this transition applies
            if (!transition.fromZone.equals(currentZone)) continue;
            
            float distance = playerPos.dst(transition.triggerArea);
            if (distance <= transition.triggerRadius) {
                // Check objective requirement
                if (transition.requiredObjective != null && 
                    !currentObjective.equals(transition.requiredObjective)) {
                    uiManager.showNotification("You need to complete your current objective first.");
                    return false;
                }
                
                // Execute the transition
                executeMapTransition(transition, player);
                return true;
            }
        }
        
        return false; // No transition found
    }
    
    private void executeMapTransition(MapTransition transition, Player player) {
        System.out.println("[STORY] Transitioning from " + transition.fromZone + " to " + transition.toZone);
        
        // Load new map
        worldManager.loadZone(transition.toZone);
        
        // Move player to spawn position
        player.setPosition(transition.spawnPosition.x, transition.spawnPosition.y);
        player.setCurrentMapName(worldManager.getCurrentZoneName());
        
        // If this transition completes an objective, do it
        if (transition.requiredObjective != null && 
            transition.requiredObjective.equals(currentObjective)) {
            completeCurrentObjective();
        }
        
        // Clear interaction hint
        uiManager.clearInteractionHint();
        
        System.out.println("[STORY] Map transition completed");
    }
    
    /**
     * Complete the current objective and move to the next one
     */
    private void completeCurrentObjective() {
        if (currentObjective == null) return;
        
        System.out.println("[STORY] Objective completed: " + currentObjective);
        gameState.completeObjective(currentObjective);
        
        // Find next objective
        int currentIndex = objectiveOrder.indexOf(currentObjective);
        if (currentIndex >= 0 && currentIndex + 1 < objectiveOrder.size()) {
            currentObjective = objectiveOrder.get(currentIndex + 1);
            uiManager.updateObjective(getObjectiveText(currentObjective));
            System.out.println("[STORY] New objective: " + currentObjective);
        } else {
            // Story completed!
            currentObjective = null;
            uiManager.updateObjective("Story completed! You saved the town's water supply!");
            System.out.println("[STORY] All objectives completed! Story finished!");
        }
    }
    
    /**
     * Force complete current objective (useful for debugging or special events)
     */
    public void forceCompleteCurrentObjective() {
        completeCurrentObjective();
    }
    
    /**
     * Jump to a specific objective (useful for testing different story parts)
     */
    public void jumpToObjective(String objectiveId) {
        if (objectiveTexts.containsKey(objectiveId)) {
            currentObjective = objectiveId;
            uiManager.updateObjective(getObjectiveText(currentObjective));
            System.out.println("[STORY] Jumped to objective: " + objectiveId);
        }
    }
    
    /**
     * Get current objective for external systems
     */
    public String getCurrentObjective() {
        return currentObjective;
    }
    
    /**
     * Check if a specific objective is currently active
     */
    public boolean isObjectiveActive(String objectiveId) {
        return objectiveId.equals(currentObjective);
    }


    /**
     * NEW METHOD for programmatic, object-based interactions.
     * Call this from other game systems (like after a puzzle is solved) to trigger a story event.
     * @param eventId A unique string identifying the event, e.g., "found_limiter".
     */
    public void triggerStoryEvent(String eventId, Player player) {
        System.out.println("[STORY] Programmatic event triggered: " + eventId);

        // Use a switch to handle different programmed events
        switch (eventId) {
            
            case "solved_lena_pipes_puzzle":
                // This event is called after the player solves the pipe puzzle at Lena's house.
                // It now presents the critical choice about the limiter.
                uiManager.showChoice(
                    "Investigation", // Speaker
                    "While fixing the pipes, you discover a strange, non-standard device attached to the water main. It seems to be restricting the flow.", // Prompt
                    new String[]{"Remove it and investigate.", "Just fix the pipes and ignore it."}, // Choices
                    (choice) -> {
                        // This code runs AFTER the player makes a choice.
                        if (choice == 1) {
                            player.findWaterLimiter(); // Method from Player.java
                            uiManager.showNarration(null, "You carefully remove the device and stash it in your bag. This could be important evidence.");
                            // You could also complete an objective here if you want
                            // completeCurrentObjective(); 
                        } else {
                            gameState.setFlag("ignored_first_limiter", true);
                            player.adjustKindness(-10);
                            uiManager.showNarration(null, "It's not your business. You fix the pipes around the device and leave.");
                        }
                    }
                );
                break;

            // --- FUTURE EXAMPLE: Interacting with a computer terminal ---
            /*
            case "hacked_veridia_computer":
                uiManager.showNarration(
                    "System Log",
                    "Accessing encrypted files... \n\nProject Aquila: Water diversion to sector 7 approved. \nStatus: ACTIVE."
                );
                gameState.setFlag("knows_about_project_aquila", true);
                player.collectEvidence();
                break;
            */
            
            // Add more cases for other programmatic events here.
        }
    }


    public boolean handleInteraction(Player player) {
        String currentZone = worldManager.getCurrentZoneName();
        Vector2 playerPos = player.getPosition();

        // 1. Check for Dialogue Triggers first
        for (DialogueTrigger trigger : dialogueTriggers.values()) {
            if (trigger.zone.equals(currentZone) && playerPos.dst(trigger.triggerArea) <= trigger.triggerRadius) {
                if (trigger.requiredObjective == null || isObjectiveActive(trigger.requiredObjective)) {
                    // Check if it's a one-time trigger that has already been used
                    if (trigger.isOneTime && gameState.getFlag("triggered_" + trigger.eventId)) {
                        continue;
                    }

                    // --- FIRE THE EVENT ---
                    triggerStoryEvent(trigger.eventId, player);
                    
                    // If it's a one-time trigger, set a flag so it can't be used again
                    if (trigger.isOneTime) {
                        gameState.setFlag("triggered_" + trigger.eventId, true);
                    }
                    return true; // Interaction was handled
                }
            }
        }

        // 2. If no dialogue trigger was fired, try for a map transition
        if (handleMapTransition(player)) {
            return true;
        }

        // 3. If still nothing, try for an NPC interaction
        if (worldManager.handleInteraction(player, uiManager)) {
            return true;
        }

        return false; // No interaction was found
    }



    
    /**
     * INNER CLASS: Represents a map transition (door, exit, etc.)
     */
    public static class MapTransition {
        public String fromZone;           // Source map name
        public String toZone;             // Destination map name
        public Vector2 triggerArea;       // Position where transition can be triggered
        public float triggerRadius;       // How close player needs to be
        public Vector2 spawnPosition;     // Where player spawns in new map
        public String requiredObjective;  // Objective that must be active (null = always available)
        public String interactionText;    // Text to show when near transition
    }

    /**
     * NEW INNER CLASS: Represents a trigger that automatically starts a dialogue
     * when the player walks into its radius, without needing to press 'E'.
     */
    public static class AutoDialogueTrigger {
        public String zone;                 // The map this trigger exists on.
        public String npcName;              // The name of the NPC who will speak.
        public Vector2 triggerArea;         // The center of the trigger zone.
        public float triggerRadius;         // The size of the trigger zone.
        public String requiredObjective;    // (Optional) An objective that must be active for this to fire.
        public boolean isOneTime = true;    // If true, this trigger only fires once per game.
    }


    /**
     * NEW INNER CLASS: Represents an invisible, programmatic trigger on a map
     * that fires a dialogue event when the player interacts with it.
     */
    public static class DialogueTrigger {
        public String zone;                 // The map this trigger exists on (e.g., "town_square").
        public String eventId;              // A unique ID that connects to the triggerStoryEvent method.
        public Vector2 triggerArea;         // The X, Y coordinates of the trigger.
        public float triggerRadius;         // How close the player needs to be.
        public String interactionText;      // The hint to show (e.g., "[E] Examine").
        public boolean isOneTime = true;    // If true, the trigger disappears after being used once.
        public String requiredObjective;    // (Optional) An objective that must be active for this to appear.
    }
}