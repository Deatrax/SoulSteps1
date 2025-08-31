package com.DMA173.soulsteps.story;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The "brain" of the story. A singleton that holds all story flags,
 * completed objectives, and overall game progress.
 */
public class GameStateManager {
    private static GameStateManager instance;

    private Set<String> completedObjectives;
    private Map<String, Boolean> storyFlags;

    // Use public static final strings for objective IDs to avoid typos
    public static final String OBJ_TALK_TO_LENA = "talked_to_lena_first_time";
    public static final String OBJ_FIND_LIMITER = "find_the_water_limiter";

    private GameStateManager() {
        completedObjectives = new HashSet<>();
        storyFlags = new HashMap<>();
        reset();
    }

    public static GameStateManager getInstance() {
        if (instance == null) {
            instance = new GameStateManager();
        }
        return instance;
    }

    /**
     * Call this method to mark a story beat or quest step as complete.
     */
    public void completeObjective(String objectiveId) {
        if (completedObjectives.add(objectiveId)) {
            System.out.println("[STORY] Objective Completed: " + objectiveId);
        }
    }

    public boolean hasCompletedObjective(String objectiveId) {
        return completedObjectives.contains(objectiveId);
    }

    /**
     * Use flags for smaller states or choices within the story.
     */
    public void setFlag(String flag, boolean value) {
        System.out.println("[STORY] Flag set: " + flag + " = " + value);
        storyFlags.put(flag, value);
    }

    public boolean getFlag(String flag) {
        return storyFlags.getOrDefault(flag, false);
    }

    /**
     * Resets the game state for a new game.
     */
    public void reset() {
        completedObjectives.clear();
        storyFlags.clear();
        System.out.println("[STORY] Game State Reset.");
        setFlag("limiter_found", false); // Add a new flag
    }
}