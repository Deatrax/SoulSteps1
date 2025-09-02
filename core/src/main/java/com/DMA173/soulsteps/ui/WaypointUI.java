package com.DMA173.soulsteps.ui;

import com.DMA173.soulsteps.Charecters.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * A UI component that displays an arrow at the edge of the screen,
 * pointing towards an off-screen objective target.
 */
public class WaypointUI {

    private Texture arrowTexture;
    private TextureRegion arrowRegion;
    private boolean isVisible = false;
    private float arrowRotation = 0f;
    private Vector2 arrowPosition = new Vector2();

    public WaypointUI() {
        try {
            // You will need to create a simple arrow image pointing UPWARDS.
            // Save it as 'assets/ui/waypoint_arrow.png'
            arrowTexture = new Texture(Gdx.files.internal("ui/waypoint_arrow.png"));
            arrowRegion = new TextureRegion(arrowTexture);
        } catch (Exception e) {
            System.out.println("[waypoint UI] Could not load 'ui/waypoint_arrow.png'. Waypoint will not be visible. Exception = "+e.getLocalizedMessage());
            arrowTexture = null;
        }
    }

    /**
     * Main update method called every frame from UIManager.
     * It calculates if the arrow should be visible and where it should point.
     * @param gameCamera The camera viewing the game world.
     * @param player The player character.
     * @param targetWorldPos The world coordinates of the objective target.
     */
    public void update(OrthographicCamera gameCamera, Player player, Vector2 targetWorldPos) {
        if (arrowTexture == null || targetWorldPos == null) {
            isVisible = false;
            return;
        }

        // Check if the target is on-screen
        if (gameCamera.frustum.pointInFrustum(targetWorldPos.x, targetWorldPos.y, 0)) {
            isVisible = false;
            return;
        }
        
        isVisible = true;

        // --- Calculation Logic ---
        Vector2 playerScreenPos = new Vector2(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        
        // Convert world target position to screen position relative to the player
        Vector2 targetScreenPos = new Vector2(targetWorldPos.x - player.getPosition().x, targetWorldPos.y - player.getPosition().y);
        targetScreenPos.scl(1 / gameCamera.zoom); // Account for camera zoom
        targetScreenPos.add(playerScreenPos);

        // Calculate the angle from the center of the screen to the target
        arrowRotation = targetScreenPos.sub(playerScreenPos).angleDeg() - 90; // -90 because our arrow texture points up

        // Clamp the arrow's position to the edges of the screen
        float screenMargin = 50f;
        arrowPosition.x = Math.max(screenMargin, Math.min(Gdx.graphics.getWidth() - screenMargin, targetScreenPos.add(playerScreenPos).x));
        arrowPosition.y = Math.max(screenMargin, Math.min(Gdx.graphics.getHeight() - screenMargin, targetScreenPos.y));
    }

    public void render(SpriteBatch batch) {
        if (!isVisible || arrowRegion == null) {
            return;
        }
        
        batch.draw(
            arrowRegion,
            arrowPosition.x - arrowRegion.getRegionWidth() / 2f,
            arrowPosition.y - arrowRegion.getRegionHeight() / 2f,
            arrowRegion.getRegionWidth() / 2f,  // originX
            arrowRegion.getRegionHeight() / 2f, // originY
            arrowRegion.getRegionWidth(),       // width
            arrowRegion.getRegionHeight(),      // height
            0.1f,                                 // scaleX
            0.1f,                                 // scaleY
            arrowRotation                       // rotation
        );
    }
    
    public void dispose() {
        if (arrowTexture != null) {
            arrowTexture.dispose();
        }
    }
}