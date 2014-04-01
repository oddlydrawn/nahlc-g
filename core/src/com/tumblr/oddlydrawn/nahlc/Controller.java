/*
 *   Copyright 2014 oddlydrawn
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.tumblr.oddlydrawn.nahlc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/** @author oddlydrawn */
public class Controller {
	private final float LEVEL_UP_MINUS_MINUS = 0.027f; // 0.027f
	private final float TIME_TO_DROP = 0.65f; // 0.65f, 0.6
	private final float TIME_TO_HOLD = 0.2f; // 0.2f
	private final float TIME_TO_MOVE = 0.09f; // 0.09f
	// It seems as though a speed of around 0.125 for the 20th level is a good jumping off point
	// However, TIME_TO_HOLD needs to be shorter. Play around with it.
	// timeToDrop=0.055999853 is excessive
	private OrthographicCamera camera;
	private Floater floater;
	// TODO make bounding circles for circle controls?
	private Rectangle leftRect;
	private Rectangle rightRect;
	private Rectangle downRect;
	private Rectangle rotateLeftRect;
	private Rectangle rotateRightRect;
	private Rectangle dropDownRect;
	private Rectangle pauseRect;
	private Rectangle pausedNoticeRect;
	private Vector2 touchCoords;
	private Vector3 unprojectedTouchCoords;
	private SavedStuff savedStuff;
	private float timeToDrop;
	private float timeToMove;
	private float timer;
	private float heldTimer;
	private boolean released;
	private boolean pauseReleased;
	private boolean dropAllTheWayReleased;

	/** Constructor that forces the boolean released = true. Because it is so. */
	public Controller () {
		touchCoords = new Vector2();
		unprojectedTouchCoords = new Vector3();
		released = true;
		timeToDrop = TIME_TO_DROP;
	}

	private void projectTouchCoords () {
		unprojectedTouchCoords.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(unprojectedTouchCoords);
		touchCoords.x = unprojectedTouchCoords.x;
		touchCoords.y = unprojectedTouchCoords.y;
	}

	/** Checks for input and applies input to floater.
	 * @param delta */
	public void update (float delta) {
		timer += delta;
		timeToMove += delta;
// touchCoords.x = Gdx.input.getX();
// touchCoords.y = Gdx.input.getY();

		projectTouchCoords();

		if (pressedPause()) {
			// And you released the button
			if (pauseReleased) {
				// Move the floater.
				togglePause();
				pauseReleased = false;
			}
		} else {
			pauseReleased = true;
		}

		if (floater.getPaused() == false) {
			processInput(delta);

			// Auto drop down after a certain amount of time has passed.
			if ((timer > timeToDrop)) { // && (timeToMove > TIME_TO_MOVE)) {
				floater.moveDown();
				timer = 0;
			}
		}

	}

	private void processInput (float delta) {
		// If there is input
		if (pressedLeft()) {
			// And you released the button
			if (released) {
				// Move the floater.
				moveLeft();
			} else { // Or you're holding the button down to move across great distances.
				heldTimer += delta;
				// Once you've held the button down after enough time that isn't confused with a long tap...
				if (heldTimer > TIME_TO_HOLD) {
					// et cetera
					moveLeft();
				}
			}
			released = false;
		} else if (pressedRight()) {
			if (released) {
				moveRight();
			} else {
				heldTimer += delta;
				if (heldTimer > TIME_TO_HOLD) {
					moveRight();
				}
			}
			released = false;
		} else if (pressedDown()) {
			if (released) {
				moveDown();
			} else {
				heldTimer += delta;
				if (heldTimer > TIME_TO_HOLD) {
					moveDown();
				}
			}
			released = false;
		} else if (pressedDropDown()) {
			if (released && dropAllTheWayReleased) {
				moveAllTheWayDown();
			}
			released = false;
		} else if (pressedRotateLeft()) {
			if (released) {
				rotateLeft();
			}
			released = false;
		} else if (pressedRotateRight()) {
			if (released) {
				rotateRight();
			}
			released = false;
		} else { // You've stopped input.
			// So I assume you've let go of the button.
			released = true;
			dropAllTheWayReleased = true;
			heldTimer = 0;
		}
	}

	private void moveLeft () {
		if (timeToMove > TIME_TO_MOVE) {
			floater.moveLeft();
			timeToMove = 0;
		}
	}

	private void moveRight () {
		if (timeToMove > TIME_TO_MOVE) {
			floater.moveRight();
			timeToMove = 0;
		}
	}

	private void moveDown () {
		if (timeToMove > TIME_TO_MOVE) {
			floater.moveDown();
			timeToMove = 0;
		}
	}

	private void moveAllTheWayDown () {
		if (timeToMove > TIME_TO_MOVE) {
			while (floater.getGrounded() == false) {
				floater.moveDown();
				timeToMove = 0;
			}
		}
	}

	private void rotateRight () {
		if (timeToMove > TIME_TO_MOVE) {
			if (savedStuff.getUpsideDown() == true) {
				floater.rotateLeft();
			} else {
				floater.rotateRight();
			}

			timeToMove = 0;
		}
	}

	private void rotateLeft () {
		if (timeToMove > TIME_TO_MOVE) {
			if (savedStuff.getUpsideDown() == true) {
				floater.rotateRight();
			} else {
				floater.rotateLeft();
			}
			timeToMove = 0;
		}
	}

	private void togglePause () {
		floater.togglePaused();
		timeToMove = 0;
	}

	// All the left input
	private boolean pressedLeft () {
		if (Gdx.input.isKeyPressed(Keys.LEFT)) return true;
		if (Gdx.input.isTouched() && leftRect.contains(touchCoords)) return true;
		return false;
	}

	// All the right input
	private boolean pressedRight () {
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) return true;
		if (Gdx.input.isTouched() && rightRect.contains(touchCoords)) return true;
		return false;
	}

	// All the down input
	private boolean pressedDown () {
		if (Gdx.input.isKeyPressed(Keys.DOWN)) return true;
		if (Gdx.input.isTouched() && downRect.contains(touchCoords)) return true;
		return false;
	}

	private boolean pressedDropDown () {
		if (Gdx.input.isKeyPressed(Keys.SPACE)) return true;
		if (Gdx.input.isTouched() && dropDownRect.contains(touchCoords)) return true;
		return false;
	}

	// All the rotate counter-clockwise input
	private boolean pressedRotateLeft () {
		if (Gdx.input.isKeyPressed(Keys.Z)) return true;
		if (Gdx.input.isTouched() && rotateLeftRect.contains(touchCoords)) return true;
		return false;
	}

	// All the rotate clockwise input
	private boolean pressedRotateRight () {
		if (Gdx.input.isKeyPressed(Keys.X)) return true;
		if (Gdx.input.isTouched() && rotateRightRect.contains(touchCoords)) return true;
		return false;
	}

	private boolean pressedPause () {
		if (Gdx.input.isKeyPressed(Keys.P)) return true;
		if (Gdx.input.isTouched() && pauseRect.contains(touchCoords)) return true;
		if (floater.getPaused() == true) {
			if (Gdx.input.isTouched() && pausedNoticeRect.contains(touchCoords)) return true;
		}
		return false;
	}

	private boolean pressedNewGame () {

		return false;
	}

	private boolean pressedMainMenu () {

		return false;
	}

	// Resets timer.
	public void resetTimer () {
		timer = 0;
	}

	// Resets held timer, useful after floater has been grounded.
	public void resetHeldTimer () {
		heldTimer = 0;
	}

	public void levelUp () {
		timeToDrop -= LEVEL_UP_MINUS_MINUS;
		timeToMove -= LEVEL_UP_MINUS_MINUS;
	}

	public void setAssets (Assets assets) {
		leftRect = assets.getLeftSprite().getBoundingRectangle();
		rightRect = assets.getRightSprite().getBoundingRectangle();
		downRect = assets.getDownSprite().getBoundingRectangle();
		rotateLeftRect = assets.getRotateLeftSprite().getBoundingRectangle();
		rotateRightRect = assets.getRotateRightSprite().getBoundingRectangle();
		dropDownRect = assets.getDropDownSprite().getBoundingRectangle();
		pauseRect = assets.getPauseSprite().getBoundingRectangle();
		pausedNoticeRect = assets.getPauseNoticeSprite().getBoundingRectangle();
	}

	/** Sets... the Floater... since Controller controls the Floater...
	 * @param floater */
	public void setFloater (Floater floater) {
		this.floater = floater;
	}

	public void setCamera (OrthographicCamera camera) {
		this.camera = camera;
	}

	public void setSavedStuff (SavedStuff savedStuff) {
		this.savedStuff = savedStuff;
	}
}
