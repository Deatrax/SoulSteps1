package com.DMA173.soulsteps.Charecters;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

/**
 * Manages all NPCs in the game world.
 * Handles NPC creation, updates, rendering, and interactions.
 */
public class NPCManager {
    private List<NPC> npcs;
    private CharecterAssets assets;
    private Random random;
    
    // NPC spawn configuration
    private static final int MAX_NPCS = 15;
    private static final float MIN_SPAWN_DISTANCE = 100f; // Minimum distance from player
    private static final float MAX_SPAWN_DISTANCE = 500f; // Maximum distance from player
    
    public NPCManager(CharecterAssets assets) {
        this.assets = assets;
        this.npcs = new ArrayList<>();
        this.random = new Random();
    }
    
    /**
     * Initialize NPCs in the world
     */
    public void initializeNPCs(float mapWidth, float mapHeight, Vector2 playerPosition) {
        createNamedNPCs(mapWidth, mapHeight, playerPosition);
        createRandomResidents(mapWidth, mapHeight, playerPosition);
        
        System.out.println("Initialized " + npcs.size() + " NPCs");
    }
    
    /**
     * Create important named NPCs for the story
     */
    private void createNamedNPCs(float mapWidth, float mapHeight, Vector2 playerPosition) {
        // Lena - Ally character
        Vector2 lenaPos = getValidSpawnPosition(mapWidth, mapHeight, playerPosition);
        NPC lena = new NPC(assets, 1, lenaPos.x, lenaPos.y, "Lena", "ally");
        lena.setDialogue("Elian! I've been looking for you. There's something strange happening with the water supply...");
        npcs.add(lena);
        
        // Police Officer Johnson
        Vector2 officerPos = getValidSpawnPosition(mapWidth, mapHeight, playerPosition);
        NPC officer = new NPC(assets, 2, officerPos.x, officerPos.y, "Officer Johnson", "police");
        officer.setDialogue("Evening, citizen. We've had reports of unusual activity. Stay vigilant.");
        npcs.add(officer);
        
        // Veridia Corporation Employee
        Vector2 veridiaPos = getValidSpawnPosition(mapWidth, mapHeight, playerPosition);
        NPC veridiaEmployee = new NPC(assets, 4, veridiaPos.x, veridiaPos.y, "Dr. Mitchell", "veridia_employee");
        veridiaEmployee.setDialogue("Veridia Corporation is committed to the city's wellbeing. Nothing to worry about here.");
        npcs.add(veridiaEmployee);
        
        // Firefighter
        Vector2 firePos = getValidSpawnPosition(mapWidth, mapHeight, playerPosition);
        NPC firefighter = new NPC(assets, 3, firePos.x, firePos.y, "Captain Rodriguez", "firefighter");
        firefighter.setDialogue("We've been getting more emergency calls lately. Something's not right in this city.");
        npcs.add(firefighter);
        
        // Delivery Person
        Vector2 deliveryPos = getValidSpawnPosition(mapWidth, mapHeight, playerPosition);
        NPC deliveryPerson = new NPC(assets, 0, deliveryPos.x, deliveryPos.y, "Sam", "delivery_person");
        deliveryPerson.setDialogue("I deliver packages all over the city. I've seen some weird things lately...");
        npcs.add(deliveryPerson);
    }
    
    /**
     * Create random resident NPCs
     */
    private void createRandomResidents(float mapWidth, float mapHeight, Vector2 playerPosition) {
        String[] maleNames = {"Marcus", "David", "James", "Robert", "William", "Thomas", "Daniel"};
        String[] femaleNames = {"Sarah", "Emma", "Lisa", "Maria", "Jennifer", "Amanda", "Jessica"};
        String[] residentTypes = {"resident_casual", "resident_formal", "resident_woman", "cold_weather", "winter_resident"};
        String[] casualDialogues = {
            "Beautiful day, isn't it?",
            "I love living in this neighborhood.",
            "Have you tried the coffee shop on Main Street?",
            "The weather has been strange lately...",
            "I'm just heading to the market.",
            "This city used to be so peaceful.",
            "Have you noticed anything odd about the water lately?"
        };
        
        int remainingSlots = MAX_NPCS - npcs.size();
        
        for (int i = 0; i < remainingSlots; i++) {
            Vector2 spawnPos = getValidSpawnPosition(mapWidth, mapHeight, playerPosition);
            String residentType = residentTypes[random.nextInt(residentTypes.length)];
            
            // Choose name based on type
            String name;
            if (residentType.equals("resident_woman")) {
                name = femaleNames[random.nextInt(femaleNames.length)];
            } else {
                name = maleNames[random.nextInt(maleNames.length)];
            }
            
            int characterType = random.nextInt(6); // Random character appearance
            NPC resident = new NPC(assets, characterType, spawnPos.x, spawnPos.y, name, residentType);
            resident.setDialogue(casualDialogues[random.nextInt(casualDialogues.length)]);
            
            npcs.add(resident);
        }
    }
    
    /**
     * Get a valid spawn position that's not too close or too far from player
     */
    private Vector2 getValidSpawnPosition(float mapWidth, float mapHeight, Vector2 playerPosition) {
        Vector2 spawnPos = new Vector2();
        int attempts = 0;
        final int MAX_ATTEMPTS = 50;
        
        do {
            // Generate random position within map bounds
            spawnPos.x = random.nextFloat() * (mapWidth - 100) + 50; // Leave some border
            spawnPos.y = random.nextFloat() * (mapHeight - 100) + 50;
            
            attempts++;
            if (attempts > MAX_ATTEMPTS) {
                // Fallback: just use a random position
                break;
            }
        } while (!isValidSpawnDistance(spawnPos, playerPosition));
        
        return spawnPos;
    }
    
    /**
     * Check if spawn position is at appropriate distance from player
     */
    private boolean isValidSpawnDistance(Vector2 spawnPos, Vector2 playerPos) {
        float distance = spawnPos.dst(playerPos);
        return distance >= MIN_SPAWN_DISTANCE && distance <= MAX_SPAWN_DISTANCE;
    }
    
    /**
     * Update all NPCs
     */
    public void update(float delta) {
        for (NPC npc : npcs) {
            npc.update(delta);
        }
    }
    
    /**
     * Render all NPCs
     */
    public void render(Batch batch) {
        for (NPC npc : npcs) {
            npc.render(batch);
        }
    }
    
    /**
     * Handle interaction with nearby NPCs
     */
    public boolean handleInteraction(Player player) {
        Vector2 playerPos = player.getPosition();
        final float INTERACTION_DISTANCE = 50f;
        
        for (NPC npc : npcs) {
            float distance = npc.getPosition().dst(playerPos);
            if (distance <= INTERACTION_DISTANCE && npc.isInteractable()) {
                npc.interact(player);
                return true; // Interaction handled
            }
        }
        return false; // No NPC to interact with
    }
    
    /**
     * Get NPC by name (for story events)
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
     * Add a new NPC dynamically
     */
    public void addNPC(NPC npc) {
        npcs.add(npc);
    }
    
    /**
     * Remove an NPC
     */
    public void removeNPC(NPC npc) {
        npcs.remove(npc);
    }
    
    /**
     * Get all NPCs of a specific type
     */
    public List<NPC> getNPCsByType(String npcType) {
        List<NPC> result = new ArrayList<>();
        for (NPC npc : npcs) {
            if (npc.getNpcType().equals(npcType)) {
                result.add(npc);
            }
        }
        return result;
    }
    
    public List<NPC> getAllNPCs() {
        return new ArrayList<>(npcs);
    }
}