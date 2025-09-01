# SoulSteps Story Progression System - Complete Guide

## Overview
This system allows you to easily add new story objectives, maps, NPCs, and transitions without modifying core game files. Everything is data-driven and modular.

## Quick Start - Adding New Content

### 1. Adding New Story Objectives

**Step 1:** Open `StoryProgressionManager.java` and find `initializeObjectives()`

**Step 2:** Add your objective to the list:
```java
// Add after existing objectives
objectiveOrder.add("your_new_objective_id");
objectiveTexts.put("your_new_objective_id", "Your objective description text");
```

**Step 3:** Add completion logic in `checkObjectiveCompletion()`:
```java
case "your_new_objective_id":
    // Define when this objective should complete
    if (someCondition) {
        completeCurrentObjective();
    }
    break;
```

### 2. Adding New Maps

**Step 1:** Create your map file: `assets/maps/yourmap.tmx`

**Step 2:** Add NPCs for the map in `WorldManager.loadNpcsForZone()`:
```java
case "yourmap":
    NPC newNPC = new NPC(characterAssets, 1, 300, 200, "NPC Name", "npc_type");
    newNPC.setDialogue("Hello! Welcome to this new area.");
    currentNpcManager.addNPC(newNPC);
    break;
```

**Step 3:** Add map transitions in `StoryProgressionManager.initializeMapTransitions()`:
```java
MapTransition toYourMap = new MapTransition();
toYourMap.fromZone = "town_square";          // Where transition starts
toYourMap.toZone = "yourmap";                // Your new map
toYourMap.triggerArea = new Vector2(400, 300); // Door/exit position
toYourMap.triggerRadius = 30f;               // How close player needs to be
toYourMap.spawnPosition = new Vector2(100, 100); // Where player appears in new map
toYourMap.requiredObjective = "enter_yourmap";    // Optional: objective required
toYourMap.interactionText = "Press E to enter Your Location";
mapTransitions.put("town_to_yourmap", toYourMap);
```

## Detailed Examples

### Example 1: Creating a Police Station Investigation

```java
// In initializeObjectives()
objectiveOrder.add("go_to_police_station");
objectiveTexts.put("go_to_police_station", "Report your findings to the police");

objectiveOrder.add("talk_to_police_chief");
objectiveTexts.put("talk_to_police_chief", "Speak with the police chief about Veridia Corp");

// In initializeMapTransitions()
MapTransition toPoliceStation = new MapTransition();
toPoliceStation.fromZone = "town_square";
toPoliceStation.toZone = "police_station";
toPoliceStation.triggerArea = new Vector2(700, 500);
toPoliceStation.triggerRadius = 35f;
toPoliceStation.spawnPosition = new Vector2(150, 100);
toPoliceStation.requiredObjective = "go_to_police_station";
toPoliceStation.interactionText = "Press E to enter police station";
mapTransitions.put("town_to_police", toPoliceStation);

// In WorldManager.loadNpcsForZone()
case "police_station":
    NPC policeChief = new NPC(characterAssets, 1, 250, 200, "Chief Rodriguez", "police");
    policeChief.setDialogue("What evidence do you have about this water situation?");
    currentNpcManager.addNPC(policeChief);
    
    NPC officer = new NPC(characterAssets, 2, 400, 180, "Officer Blake", "police");
    officer.setDialogue("We've had several complaints about water issues lately.");
    currentNpcManager.addNPC(officer);
    break;
```

### Example 2: Creating a Multi-Stage Investigation

```java
// Progressive objectives that build on each other
objectiveOrder.add("find_warehouse_location");
objectiveTexts.put("find_warehouse_location", "Find the location of Veridia's water processing facility");

objectiveOrder.add("infiltrate_warehouse");
objectiveTexts.put("infiltrate_warehouse", "Sneak into the water processing warehouse");

objectiveOrder.add("photograph_evidence");
objectiveTexts.put("photograph_evidence", "Document the illegal water limiting equipment");

objectiveOrder.add("escape_warehouse");
objectiveTexts.put("escape_warehouse", "Escape the warehouse without being caught");

// Completion logic example
case "find_warehouse_location":
    // Complete when player talks to specific NPC who gives location
    if (gameState.getFlag("warehouse_location_revealed")) {
        completeCurrentObjective();
    }
    break;

case "infiltrate_warehouse":
    // Complete when player enters warehouse map
    if (worldManager.getCurrentZoneName().equals("warehouse")) {
        completeCurrentObjective();
    }
    break;
```

### Example 3: Creating Conditional Map Access

```java
// Map that's only accessible after certain story progress
MapTransition toSecretArea = new MapTransition();
toSecretArea.fromZone = "warehouse";
toSec