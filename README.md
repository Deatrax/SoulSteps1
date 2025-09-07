# SoulSteps

![SoulSteps Banner](httpss://raw.githubusercontent.com/Sadman-Shaharier/SoulSteps/master/assets/UI/backgrounds/MainMenuBackgroundGemini1.png)

*A 2D story-driven adventure game about courage, conscience, and change.*

[![Java](https://img.shields.io/badge/Language-Java-blue.svg)](https://www.java.com)
[![License: CC BY 4.0](https://img.shields.io/badge/License-CC%20BY%204.0-lightgrey.svg)](https://creativecommons.org/licenses/by/4.0/)
[![Framework](https://img.shields.io/badge/Framework-LibGDX-red.svg)](https://libgdx.com/)

---

## ► About The Game

**SoulSteps** is a top-down 2D adventure game built with the LibGDX framework in Java. Inspired by the UN's Sustainable Development Goal 16 (Peace, Justice, and Strong Institutions), the game places players in a fictional, corrupt world where doing the right thing comes at a cost.

Instead of traditional combat, players engage in non-lethal **Confrontations**, using logic, empathy, and collected evidence to win arguments and change the minds of others. The narrative is driven by player choice, with a dynamic story system that reacts to your decisions and a world that evolves as the plot unfolds.

## ► Story Synopsis

The town of Aethelgard is slowly being choked by mysterious "resource shortages." Public gardens are wilting, water pressure is failing, and the community's spirit is drying up. The powerful **Veridia Dynamics** corporation, once hailed as the town's savior, now manages all infrastructure with an iron grip.

You play as **Elian**, a quiet but skilled technician just trying to do his job. When a routine repair job uncovers a strange device on a water main, Elian is pulled into a conspiracy that goes to the very top. Your choices will determine whether you expose the rot in Aethelgard or become a part of the system you sought to change.

## ► Core Features

This project serves as a showcase of robust game architecture and modern Java practices.

*   **Dynamic Story Progression:** A powerful, data-driven `StoryProgressionManager` controls the entire questline, making it easy to add new objectives, branches, and narrative content.
*   **Modular World System:** The `WorldManager` dynamically loads maps and populates them with the correct NPCs and triggers based on the player's progress in the story.
*   **Event & Trigger System:** The world is filled with interactive triggers for seamless map transitions, object examinations, and automatic "cutscenes" that interrupt the player to drive the story forward.
*   **Interactive Dialogue & Choice System:** An on-screen UI handles both narrative dialogue and multi-choice ethical dilemmas that have real consequences on the story and gameplay.
*   **Waypoint Guidance System:** An intelligent on-screen arrow guides the player towards their current objective's location, automatically hiding when the target is in view.
*   **Non-Lethal Confrontation System:** A unique, turn-based "combat" system where players use dialogue, empathy, and evidence to deplete an opponent's "Resolve" rather than their health.
*   **Dynamic NPC Behavior:** NPCs feature custom action/effect animations (e.g., spray-painting) and can follow complex, looping patrol paths. Their dialogue and presence in the world change based on story events.
*   **Layered Character Customization:** A flexible `ClothesContainer` and rendering system allows characters to wear multiple layers of clothing (pants, shirts, jackets) for diverse appearances.
*   **Tile-Based Collision & Dynamic Rendering:** Features robust collision detection against a Tiled map layer and a dynamic rendering system that correctly draws the player at any depth between map layers.

## ► Technology Stack

*   **Language:** Java 8+
*   **Framework:** LibGDX
*   **Build Tool:** Gradle
*   **Font Rendering:** gdx-freetype extension

## ► Getting Started

To run this project on your local machine, follow these steps.

### Prerequisites

*   **Java Development Kit (JDK):** Version 8 or higher.
*   **IDE:** IntelliJ IDEA (recommended) or another IDE with Gradle support.

### Setup & Run

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/Sadman-Shaharier/SoulSteps.git
    cd SoulSteps
    ```

2.  **Import into IntelliJ IDEA:**
    *   Open IntelliJ IDEA.
    *   Select `File > Open...` (or `Open` on the welcome screen).
    *   Navigate to and select the `build.gradle` file in the project's root directory.
    *   Choose "Open as Project".
    *   IntelliJ will automatically sync the Gradle project. This may take a few minutes.

3.  **Run the Game:**
    *   In the project file browser, navigate to `lwjgl3/src/main/java/com/DMA173/soulsteps/lwjgl3/Lwjgl3Launcher.java`.
    *   Right-click on the `Lwjgl3Launcher.java` file and select "Run 'Lwjgl3Launcher.main()'".

## ► Project Architecture

The project follows a clean, separated architecture to ensure maintainability and scalability.

*   `core/src/main/java/com/DMA173/soulsteps/`
    *   `assets/`: Contains all game assets (fonts, maps, character sprites, UI elements).
    *   `Charecters/`: The base `Character` class, `Player`, `NPC`, and the `CharecterAssets` manager.
    *   `combat/`: Contains the `ConfrontationManager` for the non-lethal conflict system.
    *   `story/`: The "brain" of the game. Contains the `GameStateManager` (memory) and `StoryProgressionManager` (quest director).
    *   `ui/`: All UI components, including the in-game `UIManager`, the `MainMenu`, and `PauseMenu`.
    *   `world/`: The `WorldManager`, responsible for loading maps and their content.

## ► How to Extend the Game

The story system is designed to be easily extended without AI help. Refer to the detailed `story_system_guide.md` file for in-depth examples.

### Quick Recipes:

#### To Add a New Quest Step:

1.  **Open `StoryProgressionManager.java`:**
2.  Add a new objective ID and text to `initializeObjectives()`.
3.  (Optional) Add a waypoint for the objective in `initializeObjectiveLocations()`.
4.  Add the completion logic for the objective in a `case` block inside `checkObjectiveCompletion()`.

#### To Add a New Map and Connect It:

1.  **Create your map** and save it to `assets/maps/`.
2.  **Open `WorldManager.java`:** In `loadNpcsForZone()`, add a `case` for your new map's ID and create the NPCs that will be there.
3.  **Open `StoryProgressionManager.java`:** In `initializeMapTransitions()`, create a new `MapTransition` object to link an existing map to your new one.

#### To Add a New Story-Critical NPC:

1.  **Open `WorldManager.java`:** In `loadNpcsForZone()`, add logic to spawn your new NPC, potentially based on a `GameStateManager` flag (e.g., only spawn after a certain quest is complete).
2.  **Open `NPC.java`:** In the `interact()` method, add a `case` for your new NPC's name. Inside, write the custom dialogue logic, which can read flags from and write updates to the `GameStateManager` to progress the story.

## ► Future Plans

*   Implement a full save/load system.
*   Expand the Confrontation system with more argument types and status effects.
*   Integrate sound effects and background music.
*   Build out the complete story with all planned zones and characters.

## ► License

This project is licensed under the **Creative Commons Attribution 4.0 International License**. See the [LICENSE.md](LICENSE.md) file for details.

## ► Acknowledgments

*   All character and map assets are placeholders sourced from various open-source platforms. Credit to the original artists.
*   Built with the incredible LibGDX framework.
