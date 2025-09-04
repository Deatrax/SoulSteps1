Of course. It's an excellent idea to update the documentation to match the final, working architecture. A good guide is crucial for making the system truly yours and enabling you to build out the rest of your story without assistance.

Here is the updated, comprehensive `story_system_guide.md`. It is tailored specifically to the classes and methods in the project files you provided, including the programmatic object interactions.

---

### `story_system_guide.md` (Updated Version)

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

Let's add a quest to "Investigate the strange noises from the warehouse."

**Step 1: Define the Objective ID**
Open `StoryProgressionManager.java` and navigate to the `initializeObjectives()` method.

**Step 2: Add the Objective to the Story Order**
Add your new objective ID to the `objectiveOrder` list.
```java
// In initializeObjectives()
// ... after existing objectives
objectiveOrder.add("investigate_warehouse_noises");
objectiveTexts.put("investigate_warehouse_noises", "Investigate the strange noises coming from the old warehouse.");
```

**Step 3: (Optional) Add a Waypoint Location**
If the objective has a specific target location, open `initializeObjectiveLocations()` and add an entry.
```java
// In initializeObjectiveLocations()
objectiveLocations.put("investigate_warehouse_noises", new Vector2(1200, 850)); // The X,Y coords of the warehouse
```

**Step 4: Define the Completion Logic**
Go to the `checkObjectiveCompletion()` method and add a `case` for your new objective ID. This tells the game when the quest is finished.
```java
// In checkObjectiveCompletion()
case "investigate_warehouse_noises":
    // This objective is complete when the player enters the "warehouse" zone.
    if (worldManager.getCurrentZoneName().equals("warehouse")) {
        completeCurrentObjective();
    }
    break;```

### Recipe B: How to Add a New Map (Zone)

Let's add a "Warehouse" map that is accessible from the `town_square`.

**Step 1: Create the Map File**
Using the Tiled editor, create your new map and save it as `assets/maps/warehouse.tmx`. Remember to add a "Collision" layer for walls.

**Step 2: Add NPCs to the New Map**
Open `WorldManager.java` and find the `loadNpcsForZone()` method. Add a `case` for your new map's ID.
```java
// In WorldManager.loadNpcsForZone()
case "warehouse":
    NPC guard = new NPC(characterAssets, 2, 150, 200, "Warehouse Guard", "veridia_employee");
    guard.setDialogue("This is private property. You're not supposed to be here.");
    currentNpcManager.addNPC(guard);
    break;
```

**Step 3: Create the Map Transition**
Open `StoryProgressionManager.java` and find the `initializeMapTransitions()` method. Add a new `MapTransition` object.
```java
// In initializeMapTransitions()
MapTransition toWarehouse = new MapTransition();
toWarehouse.fromZone = "town_square";            // Where the transition starts
toWarehouse.toZone = "warehouse";                // The new map to load
toWarehouse.triggerArea = new Vector2(1200, 850); // The X,Y of the warehouse door in town_square
toWarehouse.triggerRadius = 40f;                 // How close the player needs to be
toWarehouse.spawnPosition = new Vector2(100, 120); // Where the player appears inside the warehouse
toWarehouse.requiredObjective = "investigate_warehouse_noises"; // Player needs this quest active
toWarehouse.interactionText = "Press E to enter Warehouse";
mapTransitions.put("town_to_warehouse", toWarehouse);
```

### Recipe C: How to Trigger a Story Choice from an Object

This is for events that aren't tied to an NPC or a map transition, like finding an item or solving a puzzle.

**Step 1: Identify the Triggering Action**
Find the place in your code where the action happens (e.g., inside your `pipepuzzle.java` after a puzzle is solved, or inside `FirstScreen.checkTriggers()` for a Tiled object).

**Step 2: Add an Event Case**
Open `StoryProgressionManager.java` and find the `triggerStoryEvent()` method. Add a `case` for your new event ID.
```java
// In triggerStoryEvent()
case "found_suspicious_invoice":
    uiManager.showChoice(
        "Discovery",
        "Tucked away in a filing cabinet, you find an invoice for an industrial-grade 'water flow regulator'. It's addressed to Veridia Corp.",
        new String[]{"Take it as evidence.", "Leave it."},
        (choice) -> {
            if (choice == 1) {
                player.collectEvidence();
                uiManager.showNarration(null, "You pocket the invoice. This could be the proof you need.");
            } else {
                player.adjustKindness(-2); // Ignoring potential evidence is a choice
                uiManager.showNarration(null, "You leave the invoice. It's too risky to take.");
            }
        }
    );
    break;
```

**Step 3: Call the Trigger**
From your other code (e.g., `FirstScreen.checkTriggers`), call the new event.
```java
// Example from FirstScreen.checkTriggers()
if ("invoice_cabinet".equals(type)) {
    // When the player interacts with an object of type "invoice_cabinet" in Tiled...
    storyManager.triggerStoryEvent("found_suspicious_invoice", elian);
}
```

---

## 3. The Complete Story Flow: A Walkthrough

This is how all the pieces work together, using your existing first quest as an example.

1.  **Game Starts:** `StoryProgressionManager.startStory()` is called. It sets the current objective to `"talk_to_lena_first_time"` and tells the `UIManager` to display its text. The `objectiveLocations` map tells the waypoint system where Lena is.

2.  **Player Interacts with Lena:** The `InputHandler` detects the 'E' key press and calls `worldManager.handleInteraction()`. The `WorldManager` finds Lena and calls `lena.interact()`.

3.  **NPC Logic Runs:** Inside `NPC.java`, Lena's `interact()` method checks the `GameStateManager`. Since `"talk_to_lena_first_time"` has not been completed, it shows a choice dialogue via the `UIManager`.

4.  **Player Makes a Choice:** The player presses '1'. The `UIManager`'s dialogue box executes the code provided in the `interact` method.

5.  **State is Updated:** The code inside the choice callback runs. It adjusts the player's kindness, and most importantly, it calls `gsm.completeObjective("talk_to_lena_first_time")`. Lena's default dialogue is also changed for future conversations.

6.  **Story Progresses:** On the next game frame, `StoryProgressionManager.update()` runs. Its `checkObjectiveCompletion()` method sees that the previous objective is done and moves to the next one in the `objectiveOrder` list: `"investigate_water_system"`. It tells the `UIManager` to display the new objective text.

**The cycle is now complete and ready for the next quest step.**