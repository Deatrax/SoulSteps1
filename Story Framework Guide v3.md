You are absolutely right. The guide was incomplete. It explained how to create the *quest steps* but not how to create the *characters* who drive those quest steps forward. My apologies. A guide is useless if it doesn't show the full picture.

Let's fix that right now with a complete, detailed recipe that covers exactly what you asked for: how to add a new NPC from scratch and wire them into your story to trigger objectives and effects.

Here is the new, complete version of the guide. I have added a brand new **"Recipe D"** which is the most important part you were missing.

---

### `story_system_guide.md` (Final, Complete Version)

# SoulSteps Story Progression System - A Developer's Guide

## 1. Overview

Welcome to your story system! This guide explains how to add new quests, maps, characters, and choices to *SoulSteps*. The system is designed to be data-driven and modular, so you can add new content by following simple, repeatable patterns without breaking existing code.

The system is composed of four key components:
*   **`StoryProgressionManager`**: The "Quest Director." It knows the order of objectives, the text for each quest, and how maps connect to each other.
*   **`GameStateManager`**: The "Story's Memory." A simple class that uses flags and a list of completed objectives to track every choice and major event in the game.
*   **`WorldManager`**: The "Stage Manager." It physically builds the world, loading the correct map and placing the correct NPCs based on the current zone.
*   **`NPC.java`**: The "Actor." Each important NPC contains its own unique dialogue and story logic in its `interact()` method, which reads from and writes to the `GameStateManager`.

---

## 2. Core Recipes: How to Add New Content

Follow these recipes for the most common development tasks.

### Recipe A: How to Add a New Story Quest (Objective)
This is the process for creating a new step in your quest log.

1.  **Open `StoryProgressionManager.java`** and find the `initializeObjectives()` method.
2.  **Add the Objective ID and Text:** Add your new objective to the `objectiveOrder` list and its description to the `objectiveTexts` map.
    ```java
    // In initializeObjectives()
    // ... after existing objectives
    objectiveOrder.add("find_veridia_keycard");
    objectiveTexts.put("find_veridia_keycard", "Find a keycard to access the Veridia building.");
    ```
3.  **(Optional) Add a Waypoint:** If the objective has a specific target, open `initializeObjectiveLocations()` and add an entry.
    ```java
    // In initializeObjectiveLocations()
    objectiveLocations.put("find_veridia_keycard", new Vector2(450, 320)); // The location of the NPC who has the keycard
    ```
4.  **Define Completion Logic:** Go to `checkObjectiveCompletion()` and add a `case` for your new objective. This tells the game when the quest is finished.
    ```java
    // In checkObjectiveCompletion()
    case "find_veridia_keycard":
        // This objective completes when the player gets the "has_keycard" flag.
        if (gameState.getFlag("has_keycard")) {
            completeCurrentObjective();
        }
        break;
    ```

---

### Recipe B: How to Add a New Map (Zone)
This is the process for creating a new level.

1.  **Create the Map File:** In Tiled, create your map and save it as `assets/maps/your_zone_id.tmx`. Remember to add a "Collision" layer.
2.  **Add NPCs for the Zone:** Open `WorldManager.java` -> `loadNpcsForZone()`. Add a `case` for your new zone and create the NPCs that will live there.
    ```java
    // In WorldManager.loadNpcsForZone()
    case "police_station":
        NPC chief = new NPC(characterAssets, 2, 250, 200, "Police Chief", "police");
        chief.setDialogue("What can I do for you, citizen?");
        currentNpcManager.addNPC(chief);
        break;
    ```
3.  **Create the Map Transition:** Open `StoryProgressionManager.java` -> `initializeMapTransitions()`. Add a new `MapTransition` object to connect an existing map to your new one.
    ```java
    // In initializeMapTransitions()
    MapTransition toPoliceStation = new MapTransition();
    toPoliceStation.fromZone = "town_square";
    toPoliceStation.toZone = "police_station";
    // ... set triggerArea, spawnPosition, etc.
    mapTransitions.put("town_to_police", toPoliceStation);
    ```

---

### Recipe C: How to Trigger a Story Choice from an Object
Use this for non-NPC interactions, like solving a puzzle or examining a clue.

1.  **Open `StoryProgressionManager.java` -> `triggerStoryEvent()`**.
2.  **Add a `case` for your event.** This is where you define the dialogue and the consequences of the choice.
    ```java
    // In triggerStoryEvent()
    case "found_limiter_device":
        uiManager.showChoice("Discovery", "You found a strange device attached to the pipes...",
            new String[]{"Take it as evidence.", "Leave it."},
            (choice) -> {
                if (choice == 1) {
                    player.findWaterLimiter(); // This method is in Player.java
                    gsm.completeObjective("discover_water_limiter");
                }
            }
        );
        break;
    ```
3.  **Call the Event from Your Game Logic:** From another part of your code (like `FirstScreen.checkTriggers()` or your puzzle class), call the method.
    ```java
    // In FirstScreen, after detecting interaction with a Tiled object of type "main_valve"
    if ("main_valve".equals(type)) {
        storyManager.triggerStoryEvent("found_limiter_device", elian);
    }
    ```---

### **Recipe D: How to Add a New Story-Critical NPC (The Missing Piece)**
This is the complete process for adding a new character who progresses the story.

**Goal:** We will add **"Kael,"** an ex-Veridia engineer. Talking to him will complete the objective `"find_first_evidence"` and give the player a new objective, `"enter_veridia_building"`.

**Step 1: Place the NPC in the World (Conditionally)**
Kael should only appear *after* you've talked to Lena. We will add logic to `WorldManager` to handle this.

Open `WorldManager.java` -> `loadNpcsForZone()`.
```java
// In WorldManager.loadNpcsForZone(), inside the "town_square" case

case "town_square":
    // Lena is always here (until you decide otherwise)
    NPC lena = new NPC(characterAssets, 1, 350, 250, "Lena", "ally");
    lena.setDialogue("Elian! The water pressure is terrible!");
    currentNpcManager.addNPC(lena);

    // --- ADD THIS BLOCK ---
    // Kael only appears AFTER the player has started the investigation with Lena.
    if (gsm.hasCompletedObjective("talk_to_lena_first_time")) {
        NPC kael = new NPC(characterAssets, 4, 600, 200, "Kael", "ally");
        kael.setDialogue("Psst... over here. I heard you're looking into Veridia. I might be able to help.");
        currentNpcManager.addNPC(kael);
    }
    // ----------------------
    break;```

**Step 2: Give the NPC Its Story Logic**
Now, we give Kael his "brain." Open `NPC.java` -> `interact()`. Add a new `case` for him in the `switch` statement.

```java
// In NPC.java -> interact()

switch (this.name) {
    case "Lena":
        handleLenaInteraction(player, gsm, uiManager);
        break;

    // --- ADD THIS NEW CASE ---
    case "Kael":
        // This is Kael's specific interaction logic
        if (gsm.isObjectiveActive("find_first_evidence")) {
            uiManager.showChoice(
                "Kael",
                "I used to work for Veridia. I know what they're doing. Here, this keycard will get you inside the main building.",
                new String[]{"Thank you. I'll use it.", "This is too dangerous. I'm out."},
                (choice) -> {
                    if (choice == 1) {
                        gsm.setFlag("has_keycard", true);
                        uiManager.showNarration(null, "You received the Veridia Keycard.");
                        // --- THIS IS THE CAUSE/EFFECT ---
                        // Completing this objective triggers the next one.
                        gsm.completeObjective("find_first_evidence");
                    } else {
                        player.adjustKindness(-10);
                        uiManager.showNarration("Kael", "I understand. But someone has to stop them.");
                    }
                }
            );
        } else {
            // Default dialogue for Kael if you talk to him at any other time
            uiManager.showNarration("Kael", "Be careful in that building. They're watching everyone.");
        }
        break;
    // -------------------------

    default:
        uiManager.showNarration(this.name, this.dialogue);
        this.hasBeenTalkedTo = true;
        break;
}
```

You have now created a new, dynamic NPC. He only appears when the story needs him, and talking to him correctly progresses the questline by calling `gsm.completeObjective()`, which automatically activates the next objective in your `StoryProgressionManager`.