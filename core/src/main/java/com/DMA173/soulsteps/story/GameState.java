package com.DMA173.soulsteps.story;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * GameState manages the overall story progression, tracks player choices,
 * and controls game flow. This is a singleton class.
 */
public class GameState {
    private static GameState instance;

    // Story progression
    private Act currentAct;
    private Chapter currentChapter;
    private Set<String> completedObjectives;
    private Map<String, Boolean> gameFlags;
    private Map<String, Integer> playerChoices;

    // Map access control
    private Set<String> unlockedZones;
    private Set<String> blockedZones;

    public enum Act {
        ACT_1_FADING_TOWN,
        ACT_2_CONSPIRACY_UNFOLDS,
        ACT_3_RESORT_LAB
    }

    public enum Chapter {
        CH1_INTRODUCTION, CH2_FIRST_JOBS, CH3_DISCOVERY,
        CH4_THE_THREAT, CH5_HURT_MAN, CH6_INVESTIGATION,
        CH7_INFILTRATION, CH8_THE_CORE, CH9_FINAL_CHOICE
    }

    private GameState() {
        // Initialize all collections
        completedObjectives = new HashSet<>();
        gameFlags = new HashMap<>();
        playerChoices = new HashMap<>();
        unlockedZones = new HashSet<>();
        blockedZones = new HashSet<>();
        
        // Call reset to set default values
        resetGame();
    }
    
    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    private void initializeGameFlags() {
        gameFlags.put("met_lena", false);
        gameFlags.put("found_first_limiter", false);
        gameFlags.put("reported_to_veridia", false);
        gameFlags.put("helped_kael", false);
        gameFlags.put("has_lab_blueprints", false);
        gameFlags.put("alarm_triggered", false);
        gameFlags.put("gave_money_to_beggar", false);
        gameFlags.put("exploited_lena", false);
        gameFlags.put("entered_danger_zone", false);
        gameFlags.put("very_low_kindness", false);
    }

    public void checkStoryProgression() {
        // This method will contain the logic to move between chapters
        // based on completed objectives and flags.
        // Example:
        if (currentChapter == Chapter.CH1_INTRODUCTION && hasCompletedObjective("tutorial_complete")) {
            progressToChapter(Chapter.CH2_FIRST_JOBS);
        }
        // ... add more progression logic here as you build quests
    }

    public void makeChoice(String choiceId, int optionSelected) {
        playerChoices.put(choiceId, optionSelected);
        System.out.println("CHOICE MADE: " + choiceId + " -> Option " + optionSelected);

        switch (choiceId) {
            case "beggar_encounter":
                if (optionSelected == 1) { // Gave money
                    setFlag("gave_money_to_beggar", true);
                }
                break;
            case "limiter_decision":
                if (optionSelected == 1) { // Investigate
                    setFlag("found_first_limiter", true);
                    completeObjective("discovered_conspiracy");
                } else { // Exploit
                    setFlag("exploited_lena", true);
                }
                break;
             // ... add more choice handling here
        }
        checkStoryProgression();
    }
    
    public void completeObjective(String objectiveId) {
        if (completedObjectives.add(objectiveId)) {
            System.out.println("Objective completed: " + objectiveId);
            checkStoryProgression();
        }
    }

    private void progressToChapter(Chapter newChapter) {
        if (currentChapter != newChapter) {
            System.out.println("Story Progress: " + currentChapter + " -> " + newChapter);
            currentChapter = newChapter;
        }
    }
    
    // --- Getters and Setters ---
    public void unlockZone(String zoneId) { unlockedZones.add(zoneId); blockedZones.remove(zoneId); }
    public void blockZone(String zoneId) { blockedZones.add(zoneId); }
    public boolean isZoneAccessible(String zoneId) { return unlockedZones.contains(zoneId) && !blockedZones.contains(zoneId); }
    public Act getCurrentAct() { return currentAct; }
    public Chapter getCurrentChapter() { return currentChapter; }
    public boolean hasCompletedObjective(String objective) { return completedObjectives.contains(objective); }
    public boolean getFlag(String flag) { return gameFlags.getOrDefault(flag, false); }
    public void setFlag(String flag, boolean value) { gameFlags.put(flag, value); }

    public void resetGame() {
        currentAct = Act.ACT_1_FADING_TOWN;
        currentChapter = Chapter.CH1_INTRODUCTION;
        completedObjectives.clear();
        gameFlags.clear();
        playerChoices.clear();
        unlockedZones.clear();
        blockedZones.clear();
        
        // Initialize starting state
        unlockedZones.add("town_square");
        blockedZones.add("veridia_tower");
        blockedZones.add("resort_area");
        initializeGameFlags();
        System.out.println("--- Game State Reset ---");
    }
}