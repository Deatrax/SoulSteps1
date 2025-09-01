package com.DMA173.soulsteps;

import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.story.StoryProgressionManager;
import com.DMA173.soulsteps.ui.UIManager;
import com.DMA173.soulsteps.world.WorldManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * UPDATED INPUT HANDLER WITH STORY INTEGRATION
 * 
 * Now supports map transitions and story progression.
 * The story manager handles all the complex logic, this just triggers it.
 */
public class InputHandler {
    private OrthographicCamera camera;
    private Player player;
    private UIManager uiManager;
    private WorldManager worldManager;
    private StoryProgressionManager storyManager;
    private Boolean debugMode = true;

    public InputHandler(OrthographicCamera camera, Player player, UIManager uiManager, WorldManager worldManager) {
        this.camera = camera;
        this.player = player;
        this.uiManager = uiManager;
        this.worldManager = worldManager;
    }
    
    /**
     * Set the story manager (called from FirstScreen after initialization)
     */
    public void setStoryManager(StoryProgressionManager storyManager) {
        this.storyManager = storyManager;
    }

    public void handleInput(float delta) {
        // Check if any menu is active - if so, don't handle game input
        if (uiManager.isAnyMenuActive()) {
            return; // Let the menu system handle all input
        }

        handleUIInput();
        handlePlayerMovement(delta);
        handleCameraControls(delta);
        handleInteractions();

        if(debugMode){
            handleDebugControls();
        }
    }

    private void handleInteractions() {
        // Interaction press - NOW HANDLES BOTH NPCs AND MAP TRANSITIONS
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            boolean interactionHandled = false;
            
            // First try NPC interaction
            boolean npcInteracted = worldManager.handleInteraction(player);
            if (npcInteracted) {
                uiManager.setInteractionHint("");
                interactionHandled = true;
            }
            
            // If no NPC interaction, try map transition
            if (!interactionHandled && storyManager != null) {
                boolean transitionHandled = storyManager.handleMapTransition(player);
                if (transitionHandled) {
                    uiManager.clearInteractionHint();
                    interactionHandled = true;
                }
            }
            
            // If nothing was interacted with
            if (!interactionHandled) {
                uiManager.showNotification("Nothing to interact with here.");
            }
        }

        // Update interaction hints (both NPCs and map transitions)
        updateInteractionHints();

        // Inventory key
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            handleInventoryInput();
        }

        // Pause/Menu key - Now properly shows pause menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            uiManager.showPauseMenu();
        }

        // Toggle debug mode
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            debugMode = !debugMode;
            uiManager.toggleDebugMode();
        }
    }
    
    /**
     * Update interaction hints for both NPCs and map transitions
     */
    private void updateInteractionHints() {
        // First check for NPC interactions
        String npcHint = worldManager.getInteractionHint(player);
        if (npcHint != null) {
            uiManager.setInteractionHint(npcHint);
            return;
        }
        
        // If no NPC nearby, check for map transitions (handled in story manager update)
        // The story manager sets interaction hints for map transitions automatically
        
        // Only clear hint if neither NPC nor transition is available
        if (npcHint == null) {
            // Note: Don't clear here, let story manager handle transition hints
             uiManager.clearInteractionHint();
        }
    }

    /**
     * Handle inventory input (I key)
     */
    private void handleInventoryInput() {
        System.out.println("Inventory key pressed - opening inventory");
        // TODO: Implement inventory system
        // - Show inventory UI
        // - Display collected items and evidence
    }

    /**
     * Debug method to print all player statistics
     */
    private void printPlayerStats() {
        System.out.println("=== PLAYER STATS ===");
        System.out.println("Name: " + player.getName());
        System.out.println("Position: (" + player.getPosition().x + ", " + player.getPosition().y + ")");
        System.out.println("Kindness: " + player.getKindnessLevel() + " (" +
                String.format("%.1f", player.getKindnessPercentage() * 100) + "%)");
        System.out.println("Evidence Count: " + player.getEvidenceCount());
        System.out.println("Has Water Limiter: " + player.hasWaterLimiter());
        System.out.println("Danger Zone Active: " + player.isDangerZoneActive());
        System.out.println("Speed: " + player.getSpeed());
        System.out.println("==================");
    }

    private void handleUIInput() {
        // Additional UI controls can go here
    }

    private void handlePlayerMovement(float delta) {
        // Player movement is now handled in the Player class itself
        // This method is kept for potential future movement restrictions or special cases
    }

    private void handleCameraControls(float delta) {
        // Zoom controls
        if (Gdx.input.isKeyPressed(Input.Keys.PLUS) || Gdx.input.isKeyPressed(Input.Keys.EQUALS)) {
            camera.zoom -= 0.01f;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
            camera.zoom += 0.01f;
        }

        // Clamp zoom to reasonable values
        camera.zoom = Math.max(0.2f, Math.min(2.0f, camera.zoom));
    }

    public boolean isPaused() {
        return uiManager.isAnyMenuActive();
    }

    /**
     * Handles debug-only input (remove in final version)
     */
    private void handleDebugControls() {
        // Debug: Test kindness adjustment
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            player.adjustKindness(10);
            System.out.println("DEBUG: Kindness increased! Current: " + player.getKindnessLevel());

            if (player.isDangerZoneActive()) {
                System.out.println("DEBUG: DANGER ZONE ACTIVE!");
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.J)) {
            player.adjustKindness(-10);
            System.out.println("DEBUG: Kindness decreased! Current: " + player.getKindnessLevel());

            if (player.isDangerZoneActive()) {
                System.out.println("DEBUG: DANGER ZONE ACTIVE!");
            }
        }

        // Debug: Test evidence collection
        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            player.collectEvidence();
            System.out.println("DEBUG: Evidence collected! Total: " + player.getEvidenceCount());
        }

        // Debug: Test water limiter discovery
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            player.findWaterLimiter();
        }

        // Debug: Print player stats
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            printPlayerStats();
        }

        // Debug: Show pause menu (alternative to ESC)
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            uiManager.showPauseMenu();
        }
        
        // === STORY SYSTEM DEBUG CONTROLS ===
        if (storyManager != null) {
            // Debug: Force complete current objective
            if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
                storyManager.forceCompleteCurrentObjective();
                System.out.println("DEBUG: Forced objective completion");
            }
            
            // Debug: Jump to specific objectives for testing
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
                storyManager.jumpToObjective("talk_to_lena_first_time");
                System.out.println("DEBUG: Jumped to objective 1");
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
                storyManager.jumpToObjective("investigate_water_system");
                System.out.println("DEBUG: Jumped to objective 2");
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
                storyManager.jumpToObjective("find_first_evidence");
                System.out.println("DEBUG: Jumped to objective 3");
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
                storyManager.jumpToObjective("enter_veridia_building");
                System.out.println("DEBUG: Jumped to objective 4");
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
                storyManager.jumpToObjective("talk_to_receptionist");
                System.out.println("DEBUG: Jumped to objective 5");
            }
        }
    }
}