package com.DMA173.soulsteps;

import com.DMA173.soulsteps.Charecters.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * InputHandler class manages all user input for the game.
 * Separates input logic from screen rendering logic.
 * Demonstrates separation of concerns in OOP design.
 */
public class InputHandler {
    
    // Dependencies injected through constructor
    private OrthographicCamera camera;
    private Player player;
    
    // Input state tracking
    private boolean debugMode;
    
    /**
     * Constructor - Dependency Injection pattern
     * @param camera The game camera for zoom controls
     * @param player The player character for game actions
     */
    public InputHandler(OrthographicCamera camera, Player player) {
        this.camera = camera;
        this.player = player;
        this.debugMode = true; // Enable debug mode for development
    }
    
    /**
     * Main input handling method called every frame
     * @param delta Time elapsed since last frame
     */
    public void handleInput(float delta) {
        handleCameraControls();
        handleGameControls();
        
        if (debugMode) {
            handleDebugControls();
        }
    }
    
    /**
     * Handles camera-related input (zoom)
     */
    private void handleCameraControls() {
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
    
    /**
     * Handles main game controls (interaction, inventory, menu)
     */
    private void handleGameControls() {
        // Interaction key - will be used for talking to NPCs, examining objects
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            handleInteractionInput();
        }
        
        // Inventory key
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            handleInventoryInput();
        }
        
        // Pause/Menu key
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            handlePauseInput();
        }
        
        // Toggle debug mode
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            debugMode = !debugMode;
            System.out.println("Debug mode: " + (debugMode ? "ON" : "OFF"));
        }
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
    }
    
    /**
     * Handle interaction input (E key)
     */
    private void handleInteractionInput() {
        System.out.println("Interaction key pressed - looking for nearby objects/NPCs");
        // TODO: Implement interaction system
        // - Check for nearby NPCs
        // - Check for interactable objects
        // - Trigger dialogue or object interaction
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
     * Handle pause/menu input (Escape key)
     */
    private void handlePauseInput() {
        System.out.println("Escape pressed - opening pause menu");
        // TODO: Implement pause menu
        // - Show pause menu UI
        // - Pause game logic
        // - Provide save/load options
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
    
    // Getters for external access if needed
    public boolean isDebugMode() {
        return debugMode;
    }
    
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
}
