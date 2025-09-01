package com.DMA173.soulsteps.Charecters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * A concrete implementation of a Character used for testing player control.
 * It extends the abstract Character class and implements the `update` method
 * to read keyboard input and control the character's movement and state.
 */
public class TestCharacter extends Character {

    /**
     * Constructor for the TestCharacter.
     *
     * @param assets        A pre-initialized CharecterAssets instance.
     * @param characterType The visual style to use for this character (e.g., 0 for the first one).
     * @param startX        The initial X position.
     * @param startY        The initial Y position.
     * @param speed         The movement speed.
     */
    public TestCharacter(CharecterAssets assets, int characterType, float startX, float startY, float speed) {
        // Call the parent constructor to set up all the common properties
        super(assets, characterType, startX, startY, speed);
    }

    /**
     * Implements the character's specific update logic: handling keyboard input.
     *
     * @param delta The time elapsed since the last frame.
     */
    @Override
    public void update(float delta) {
        // First, update the animation timer by calling the method from the parent class.
        updateStateTime(delta);

        // Assume the character is not moving until a key is pressed.
        boolean moved = false;

        // Check for keyboard input and update position and direction.
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            position.y += speed * delta;
            setCurrentDir(CharecterAssets.Direction.UP);
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            position.y -= speed * delta;
            setCurrentDir(CharecterAssets.Direction.DOWN);
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            position.x -= speed * delta;
            setCurrentDir(CharecterAssets.Direction.LEFT);
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            position.x += speed * delta;
            setCurrentDir(CharecterAssets.Direction.RIGHT);
            moved = true;
        }

        // Set the final movement state for this frame.
        // The render() method in the parent class will use this to decide which animation to show.
        this.setMoving(moved);
    }
}
