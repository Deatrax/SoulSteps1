package com.DMA173.soulsteps.ui;

import com.DMA173.soulsteps.Charecters.Player;
import com.DMA173.soulsteps.MainMenuScreen;
import com.DMA173.soulsteps.story.GameStateManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class UIManager {
    private Game game;
    private Stage stage;
    private Skin skin;

    // HUD Actors
    private Label healthLabel;
    private Label kindnessLabel;
    private Label objectiveLabel;
    private Label interactionLabel;
    private Label debugLabel;
    
    // Pause Menu Actors
    private Window pauseWindow;
    private boolean isPaused = false;
    
    public UIManager(Game game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        createSkin();
        buildHud();
        buildPauseMenu();
    }
    
    private void createSkin() {
        skin = new Skin(Gdx.files.internal("ui/uiskin.json")); // Using the default skin
    }
    
    private void buildHud() {
        Table hudTable = new Table();
        hudTable.setFillParent(true);
        hudTable.top().left(); // Anchor to the top-left

        healthLabel = new Label("Health:", skin);
        kindnessLabel = new Label("Kindness:", skin);
        objectiveLabel = new Label("Objective:", skin);
        interactionLabel = new Label("", skin);
        debugLabel = new Label("", skin);

        hudTable.add(healthLabel).left().pad(10);
        hudTable.row();
        hudTable.add(kindnessLabel).left().pad(10);
        hudTable.row();
        hudTable.add(objectiveLabel).left().pad(10).width(300); // Give it width for wrapping
        
        // Another table for centered/bottom elements
        Table bottomTable = new Table();
        bottomTable.setFillParent(true);
        bottomTable.bottom();
        bottomTable.add(interactionLabel).pad(20);

        stage.addActor(hudTable);
        stage.addActor(bottomTable);
    }
    
    private void buildPauseMenu() {
        pauseWindow = new Window("PAUSED", skin);
        pauseWindow.setSize(300, 250);
        pauseWindow.setMovable(false);

        TextButton resumeButton = new TextButton("Resume", skin);
        TextButton mainMenuButton = new TextButton("Main Menu", skin);
        TextButton exitButton = new TextButton("Exit Game", skin);
        
        pauseWindow.add(resumeButton).fillX().pad(5).row();
        pauseWindow.add(mainMenuButton).fillX().pad(5).row();
        pauseWindow.add(exitButton).fillX().pad(5);
        
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hideAllMenus();
            }
        });
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        pauseWindow.setVisible(false); // Start hidden
        stage.addActor(pauseWindow);
    }
    
    public void render(Player player) {
        updateHud(player);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        
    }
    
    private void updateHud(Player player) {
        healthLabel.setText("Health: " + player.getHealth() + " / 100");
        kindnessLabel.setText("Kindness: " + player.getKindnessLevel() + " / 100");
        
        GameStateManager gsm = GameStateManager.getInstance();
        if (gsm.hasCompletedObjective(GameStateManager.OBJ_TALK_TO_LENA)) {
            objectiveLabel.setText("Objective: Find the main water valve.");
        } else {
            objectiveLabel.setText("Objective: Find and talk to Lena.");
        }
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        pauseWindow.setPosition(width / 2f - pauseWindow.getWidth() / 2, height / 2f - pauseWindow.getHeight() / 2);
    }
    
    public void showPauseMenu() {
        isPaused = true;
        pauseWindow.setVisible(true);
        Gdx.input.setInputProcessor(stage); // Let the stage handle clicks
    }
    
    public void hideAllMenus() {
        isPaused = false;
        pauseWindow.setVisible(false);
        // We will set the input processor back in FirstScreen or InputHandler
    }

    public boolean isAnyMenuActive() {
        return isPaused;
    }
    
    public void setInteractionHint(String hint) {
        interactionLabel.setText(hint != null ? hint : "");
    }
    
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}