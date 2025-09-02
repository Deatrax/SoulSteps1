package com.DMA173.soulsteps.story;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    //flag for if the game was loaded from a saved state or file
    private Boolean isContinuedStory;
    
    public Boolean getIsContinuedStory() {
        return isContinuedStory;
    }

    public void setIsContinuedStory(Boolean isContinuedStory) {
        this.isContinuedStory = isContinuedStory;
    }

    public StoryProgressionManager(UIManager uiManager, WorldManager worldManager) {
        this.gameState = GameStateManager.getInstance();
        this.uiManager = uiManager;
        this.worldManager = worldManager;
        
        initializeStorySystem();
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
        objectiveOrder.add("talk_to_lena_first_time");
        objectiveTexts.put("talk_to_lena_first_time", "Find Lena and learn about the water crisis");
        
        // Objective 2: Investigation begins
        objectiveOrder.add("investigate_water_system");
        objectiveTexts.put("investigate_water_system", "Investigate the town's water supply system");
        
        // Objective 3: First clue
        objectiveOrder.add("find_first_evidence");
        objectiveTexts.put("find_first_evidence", "Search for evidence of tampering (Press L to collect debug evidence)");
        
        // Objective 4: Map transition example
        objectiveOrder.add("enter_veridia_building");
        objectiveTexts.put("enter_veridia_building", "Enter the Veridia Corporation building");
        
        // Objective 5: Inside building investigation
        objectiveOrder.add("talk_to_receptionist");
        objectiveTexts.put("talk_to_receptionist", "Talk to the receptionist inside Veridia Corp");
        
        // Objective 6: Find the truth
        objectiveOrder.add("discover_water_limiter");
        objectiveTexts.put("discover_water_limiter", "Find evidence of the water limiting device");
        
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
        
        // EXAMPLE TRANSITIONS - MODIFY/ADD YOUR OWN:
        
        // From town_square to veridia_interior (entering building)
        MapTransition enterBuilding = new MapTransition();
        enterBuilding.fromZone = "town_square";
        enterBuilding.toZone = "office";
        enterBuilding.triggerArea = new Vector2(500, 300); // Position of building door
        enterBuilding.triggerRadius = 30f;
        enterBuilding.spawnPosition = new Vector2(100, 100); // Where player spawns in new map
        enterBuilding.requiredObjective = "enter_veridia_building"; // Must have this objective active
        enterBuilding.interactionText = "Press E to enter Veridia Corporation";
        mapTransitions.put("town_to_veridia", enterBuilding);
        
        // From veridia_interior back to town_square (exiting building)
        MapTransition exitBuilding = new MapTransition();
        exitBuilding.fromZone = "office";
        exitBuilding.toZone = "town_square";
        exitBuilding.triggerArea = new Vector2(100, 50); // Position of exit door inside
        exitBuilding.triggerRadius = 30f;
        exitBuilding.spawnPosition = new Vector2(500, 250); // Back outside the building
        exitBuilding.requiredObjective = null; // No objective required to exit
        exitBuilding.interactionText = "Press E to exit building";
        mapTransitions.put("veridia_to_town", exitBuilding);
        
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
        
        // From residential_area to police_station
        MapTransition toPoliceStation = new MapTransition();
        toPoliceStation.fromZone = "residential_area";
        toPoliceStation.toZone = "police_station";
        toPoliceStation.triggerArea = new Vector2(300, 150);
        toPoliceStation.triggerRadius = 35f;
        toPoliceStation.spawnPosition = new Vector2(200, 100);
        toPoliceStation.requiredObjective = "report_to_authorities";
        toPoliceStation.interactionText = "Press E to enter police station";
        mapTransitions.put("residential_to_police", toPoliceStation);
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
        checkMapTransitions(player);
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
            case "talk_to_lena_first_time":
                // This gets completed in NPC.interact() when talking to Lena
                // No additional check needed here
                break;
                
            case "investigate_water_system":
                // Auto-complete after talking to Lena (could add timer or other conditions)
                if (gameState.hasCompletedObjective("talked_to_lena_first_time")) {
                    completeCurrentObjective();
                }
                break;
                
            case "find_first_evidence":
                // Complete when player collects evidence (using debug key L or real evidence)
                if (player.getEvidenceCount() > 0) {
                    completeCurrentObjective();
                }
                break;
                
            case "enter_veridia_building":
                // This gets completed by map transition system
                // No additional check needed here
                break;
                
            case "talk_to_receptionist":
                // This would be completed by NPC interaction in the new map
                if (gameState.hasCompletedObjective("talked_to_receptionist_veridia")) {
                    completeCurrentObjective();
                }
                break;
                
            case "discover_water_limiter":
                // Complete when player finds the water limiter device
                if (player.hasWaterLimiter()) {
                    completeCurrentObjective();
                }
                break;
                
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
    
    /**
     * STEP 5: Check for map transitions (doors, zone changes)
     * 
     * This automatically handles map transitions when player approaches trigger areas
     */
    private void checkMapTransitions(Player player) {
        String currentZone = worldManager.getCurrentZoneName();
        Vector2 playerPos = player.getPosition();
        
        for (Map.Entry<String, MapTransition> entry : mapTransitions.entrySet()) {
            MapTransition transition = entry.getValue();
            
            // Check if this transition applies to current zone
            if (!transition.fromZone.equals(currentZone)) continue;
            
            // Check if player is near the transition trigger
            float distance = playerPos.dst(transition.triggerArea);
            if (distance <= transition.triggerRadius) {
                // Check if objective requirement is met (if any)
                if (transition.requiredObjective != null && 
                    !currentObjective.equals(transition.requiredObjective)) {
                    continue; // Objective not active
                }
                
                // Show interaction hint
                uiManager.setInteractionHint(transition.interactionText);
                return; // Only show one hint at a time
            }
        }
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
}