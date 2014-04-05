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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.badlogic.gdx.math.MathUtils;

/** @author oddlydrawn */
public class Floater {
	public static final int MAX_NEXT_SHAPES = 7; // 14
	public static final int NUM_TYPES = 7; // 7
	public static final int NUM_NEXT_SHAPES = 1; // 2
	private final int I = 0; // 0
	private final int O = 1; // 1
	private final int T = 2; // 2
	private final int J = 3; // 3
	private final int L = 4; // 4
	private final int S = 5; // 5
	private final int Z = 6; // 6
	// Collection of the next MAX_NEXT_SHAPES to make shape distribution fair..ish.
	private ArrayList<Integer> listNextShapes;
	private Board board;
	// Actual factual block positions.
	private Coords posOne;
	private Coords posTwo;
	private Coords posThree;
	private Coords posFour;
	// Test positions for each block.
	private Coords posOneTest;
	private Coords posTwoTest;
	private Coords posThreeTest;
	private Coords posFourTest;
	private Coords rotatedCoords;
	private Coords tmpCoords;
	private Random random;
	private Audio audio;
	// mids are distance from origin.
	private float midX;
	private float midY;
	// Coordinates after origin is taken from block positions.
	private float xxx;
	private float yyy;
	// Rotated coordinates.
	private float newX;
	private float newY;
	private int shapeType;
	private int nextShapeCounter;
	private int x;
	private int y;
	private int bagSize;
	private int nextRandomShapeType;
	private boolean grounded;
	private boolean gameOver;
	private boolean paused;
	private boolean randomNextShapes;

	/** Constructor that makes all the new objects. */
	public Floater () {
		// New all the objects.
		random = new Random();
		posOne = new Coords();
		posTwo = new Coords();
		posThree = new Coords();
		posFour = new Coords();

		posOneTest = new Coords();
		posTwoTest = new Coords();
		posThreeTest = new Coords();
		posFourTest = new Coords();

		rotatedCoords = new Coords();
		tmpCoords = new Coords();

		listNextShapes = new ArrayList<Integer>();

	}

	/** After being called by Controller, tries to move left, if no collisions are found it will applyCoordChange() */
	public void moveLeft () {
		posOneTest.x--;
		posTwoTest.x--;
		posThreeTest.x--;
		posFourTest.x--;
		if ((collisionToLeft() == false) && (thereIsCollision() == false)) {
			applyCoordChange();
		} else {
			revertCoordChange();
		}
	}

	/** After being called by Controller, tries to move right, if no collisions are found it will applyCoordChange() */
	public void moveRight () {
		posOneTest.x++;
		posTwoTest.x++;
		posThreeTest.x++;
		posFourTest.x++;
		if ((collisionToRight() == false) && (thereIsCollision() == false)) {
			applyCoordChange();
		} else {
			revertCoordChange();
		}
	}

	/** After being called by Controller, tries to move down, if no collisions are found it will applyCoordChange() */
	public void moveDown () {
		posOneTest.y++;
		posTwoTest.y++;
		posThreeTest.y++;
		posFourTest.y++;
		if ((collisionDown() == false) && (thereIsCollision() == false)) {
			applyCoordChange();
		} else {
			revertCoordChange();
			// XXX hacky
			// / FIXME
			grounded = true;
			audio.playCollides();
		}
	}

	/** Called by Controller to rotate the floater in a clockwise direction. */
	public void rotateRight () {
		findMidPoint();

		// Rotates a blocks coordinates.
		rotatedCoords = rotateCoordsRight(posOneTest.x, posOneTest.y);
		// Sets rotated coordinates.
		posOneTest.set(rotatedCoords.x, rotatedCoords.y);

		rotatedCoords = rotateCoordsRight(posTwoTest.x, posTwoTest.y);
		posTwoTest.set(rotatedCoords.x, rotatedCoords.y);

		rotatedCoords = rotateCoordsRight(posThreeTest.x, posThreeTest.y);
		posThreeTest.set(rotatedCoords.x, rotatedCoords.y);

		rotatedCoords = rotateCoordsRight(posFourTest.x, posFourTest.y);
		posFourTest.set(rotatedCoords.x, rotatedCoords.y);

		// Tests rotated coordinates for collisions.
		if (checkAllTheCollisions() == false) {
			applyCoordChange();
		} else {
			tryFriendlyRotation();
			if (checkAllTheCollisions() == false) {
				applyCoordChange();
			} else {
				tryFriendlyRotation();
				if (checkAllTheCollisions() == false) {
					applyCoordChange();
				} else {
					revertCoordChange();
				}
			}
		}
	}

	/** Called by Controller to rotate the floater in a counter-clockwise direction. */
	public void rotateLeft () {
		findMidPoint();
		rotatedCoords = rotateCoordsLeft(posOneTest.x, posOneTest.y);
		posOneTest.set(rotatedCoords.x, rotatedCoords.y);

		rotatedCoords = rotateCoordsLeft(posTwoTest.x, posTwoTest.y);
		posTwoTest.set(rotatedCoords.x, rotatedCoords.y);

		rotatedCoords = rotateCoordsLeft(posThreeTest.x, posThreeTest.y);
		posThreeTest.set(rotatedCoords.x, rotatedCoords.y);

		rotatedCoords = rotateCoordsLeft(posFourTest.x, posFourTest.y);
		posFourTest.set(rotatedCoords.x, rotatedCoords.y);

		// Tests rotated coordinates for collisions.
		if (checkAllTheCollisions() == false) {
			applyCoordChange();
		} else {
			tryFriendlyRotation();
			if (checkAllTheCollisions() == false) {
				applyCoordChange();
			} else {
				tryFriendlyRotation();
				if (checkAllTheCollisions() == false) {
					applyCoordChange();
				} else {
					revertCoordChange();
				}
			}
		}
	}

	/** Rotates the coordinates for the floater relative to posTwoTest's position in a clockwise direction. Processes one block.
	 * @param x pos{...}Test.x that needs to be rotated
	 * @param y pos{...}Test.y that needs to be rotated
	 * @return Returns the rotated Coords tmpCoords */
	private Coords rotateCoordsRight (int x, int y) {
		// (x, y) is block's current position.
		// mids are the distance to origin of all the blocks's middle--the point all blocks pivot on.
		// (xxx, yyy) are a block's position relative to origin.
		xxx = x - midX;
		yyy = y - midY;

		// The rotation, being done on board's origin. (newX, newY) are the rotated coordinates.
		newX = -yyy;
		newY = xxx;

		tmpCoords.set(MathUtils.round(newX), MathUtils.round(newY));
		// Adds back the blocks' distance from origin to place blocks in correct relative position after rotation.
		tmpCoords.x += midX;
		tmpCoords.y += midY;

		// XXX Do I really need to return, set to rotated coords, then set the coords? Can't I just skip the middle step?...
		return tmpCoords;
	}

	/** Rotates the coordinates for the floater relative to posTwoTest's position in a counter-clockwise direction.
	 * @param x pos{...}Test.x that needs to be rotated
	 * @param y pos{...}Test.y that needs to be rotated
	 * @return Returns the rotated Coords tmpCoords */
	private Coords rotateCoordsLeft (int x, int y) {
		xxx = x - midX;
		yyy = y - midY;

		newX = yyy;
		newY = -xxx;

		tmpCoords.set(MathUtils.round(newX), MathUtils.round(newY));
		tmpCoords.x += midX;
		tmpCoords.y += midY;

		return tmpCoords;
	}

	/** Applies the pos{...}Test coordinates to their respective pos{...} objects. */
	private void applyCoordChange () {
		posOne.set(posOneTest.getX(), posOneTest.getY());
		posTwo.set(posTwoTest.getX(), posTwoTest.getY());
		posThree.set(posThreeTest.getX(), posThreeTest.getY());
		posFour.set(posFourTest.getX(), posFourTest.getY());
	}

	/** Applies the pos{...} coordinates to their respective pos{...}Test objects. */
	private void revertCoordChange () {
		posOneTest.set(posOne.getX(), posOne.getY());
		posTwoTest.set(posTwo.getX(), posTwo.getY());
		posThreeTest.set(posThree.getX(), posThree.getY());
		posFourTest.set(posFour.getX(), posFour.getY());
	}

	private boolean checkAllTheCollisions () {
		if ((collisionToRight() == true) || (collisionToLeft() == true)) return true;
		if ((collisionDown() == true) || (thereIsCollision() == true)) return true;
		return false;

	}

	private void tryFriendlyRotation () {
		tmpCoords.set(posTwoTest.x + 1, posTwoTest.y);
		if ((stuffToTheRight(tmpCoords))) {
			posOneTest.x -= 1;
			posTwoTest.x -= 1;
			posThreeTest.x -= 1;
			posFourTest.x -= 1;
		} else {
			tmpCoords.set(posTwoTest.x - 1, posTwoTest.y);
			if (stuffToTheLeft(tmpCoords)) {
				posOneTest.x += 1;
				posTwoTest.x += 1;
				posThreeTest.x += 1;
				posFourTest.x += 1;
			}
		}
	}

	private boolean stuffToTheRight (Coords c) {
		if (c.getX() >= Board.BOARD_WIDTH) return true;
		if (c.getX() + 1 >= Board.BOARD_WIDTH) return true;
		if (board.isFilled(c)) return true;
		if (board.isFilled(c.x + 1, c.y)) return true;
		if (board.isFilled(c.x, c.y + 1)) return true;
		if (board.isFilled(c.x + 1, c.y + 1)) return true;
		return false;
	}

	private boolean stuffToTheLeft (Coords c) {
		if (c.getX() < 0) return true;
		if (c.getX() - 1 < 0) return true;
		if (board.isFilled(c)) return true;
		if (board.isFilled(c.x - 1, c.y)) return true;
		if (board.isFilled(c.x, c.y - 1)) return true;
		if (board.isFilled(c.x - 1, c.y - 1)) return true;
		return false;
	}

	/** Midpoint is just posTwoTest's position. */
	private void findMidPoint () {
		// TODO find the correct block to rotate on instead of just posTwoTest,
		// possibly per shape.
		midX = posTwoTest.x;
		midY = posTwoTest.y;
	}

	/** Spawns a block at the top of the board when a new piece is needed. */
	public void createNew () {
		System.out.println("randomNextShapes = " + randomNextShapes);
		if (randomNextShapes == true) {
			shapeType = nextRandomShapeType;
			nextRandomShapeType = random.nextInt(NUM_TYPES);
		} else {
			// Makes sure we don't exceed MAX_NEXT_SHAPES before setting shapeType.
			if (nextShapeCounter >= MAX_NEXT_SHAPES) {
				nextShapeCounter = 0;
				Collections.shuffle(listNextShapes);
			}

			shapeType = listNextShapes.get(nextShapeCounter);
			nextShapeCounter++;

			// Pre and post checks needed to actually use shapes Zero through last shape in collection.
			if (nextShapeCounter >= MAX_NEXT_SHAPES) {
				nextShapeCounter = 0;
				Collections.shuffle(listNextShapes);
			}
		}

		// Can't very well stop before we've begun.
		grounded = false;

		// Creates the shape, using the random shape type.
		switch (shapeType) {
		case I:
			posOne.set(5, 1);
			posTwo.set(5, 2);
			posThree.set(5, 3);
			posFour.set(5, 4);
			break;
		case O:
			posOne.set(5, 1);
			posTwo.set(6, 1);
			posThree.set(5, 2);
			posFour.set(6, 2);
			break;
		case T:
			posOne.set(5, 1);
			posTwo.set(5, 2);
			posThree.set(6, 2);
			posFour.set(5, 3);
			break;
		case J:
			posOne.set(5, 1);
			posTwo.set(5, 2);
			posThree.set(5, 3);
			posFour.set(6, 1);
			break;
		case L:
			posOne.set(6, 1);
			posTwo.set(6, 2);
			posThree.set(6, 3);
			posFour.set(5, 1);
			break;
		case S:
			posOne.set(5, 1);
			posTwo.set(5, 2);
			posThree.set(6, 2);
			posFour.set(6, 3);
			break;
		case Z:
			posOne.set(6, 1);
			posTwo.set(6, 2);
			posThree.set(5, 2);
			posFour.set(5, 3);
			break;
		}

		// I think bad things happened once when we didn't initially set the test values.
		// TODO check again.
		posOneTest.set(posOne.getX(), posOne.getY());
		posTwoTest.set(posTwo.getX(), posTwo.getY());
		posThreeTest.set(posThree.getX(), posThree.getY());
		posFourTest.set(posFour.getX(), posFour.getY());

		if (thereIsCollision()) {
			gameOver = true;
		}
	}

	/** Checks for a collision against the right boundary of the board, also calls thereIsCollision().
	 * @return Returns true if there is a collision. */
	private boolean collisionToRight () {
		// Checks if the floater is at the board right edge boundary.
		if (posOneTest.getX() >= Board.BOARD_WIDTH) return true;
		if (posTwoTest.getX() >= Board.BOARD_WIDTH) return true;
		if (posThreeTest.getX() >= Board.BOARD_WIDTH) return true;
		if (posFourTest.getX() >= Board.BOARD_WIDTH) return true;
		return false;
	}

	/** Checks for a collision against the left boundary of the board, also calls thereIsCollision().
	 * @return Returns true if there is a collision. */
	private boolean collisionToLeft () {
		// Checks if the floater is at the board left edge boundary.
		if (posOneTest.getX() < 0) return true;
		if (posTwoTest.getX() < 0) return true;
		if (posThreeTest.getX() < 0) return true;
		if (posFourTest.getX() < 0) return true;
		return false;
	}

	/** Checks for a collision against the bottom boundary of the board, also calls thereIsCollision().
	 * @return Returns true if there is a collision. */
	private boolean collisionDown () {
		// Checks if the floater is at the board bottom edge boundary.
		if (posOneTest.getY() >= Board.BOARD_HEIGHT) return true;
		if (posTwoTest.getY() >= Board.BOARD_HEIGHT) return true;
		if (posThreeTest.getY() >= Board.BOARD_HEIGHT) return true;
		if (posFourTest.getY() >= Board.BOARD_HEIGHT) return true;
		return false;
	}

	/** Checks for a collision against blocks.
	 * @return Returns true if there is a collision. */
	private boolean thereIsCollision () {
		// Checks if there's a block in the way when moving in any direction.
		if (board.isFilled(posOneTest)) return true;
		if (board.isFilled(posTwoTest)) return true;
		if (board.isFilled(posThreeTest)) return true;
		if (board.isFilled(posFourTest)) return true;
		return false;
	}

	/** Prints the coordinates of each pos{...} and pos{...}Test using System.out. */
	@SuppressWarnings("unused")
	private void printCoords () {
		System.out.println("posOneTest = ( " + posOneTest.x + ", " + posOneTest.y + ") and posOne = ( " + posOne.x + ", "
			+ posOne.y + ")");
		System.out.println("posTwoTest = ( " + posTwoTest.x + ", " + posTwoTest.y + ") and posTwo = ( " + posTwo.x + ", "
			+ posTwo.y + ")");
		System.out.println("posThreeTest = ( " + posThreeTest.x + ", " + posThreeTest.y + ") and posThree = ( " + posThree.x + ", "
			+ posThree.y + ")");
		System.out.println("posFourTest = ( " + posFourTest.x + ", " + posFourTest.y + ") and posFour = ( " + posFour.x + ", "
			+ posFour.y + ")");
	}

	public void togglePaused () {
		if (paused) {
			paused = false;
		} else {
			paused = true;
		}
	}

	public boolean isPaused () {
		return paused;
	}

	public boolean isGameOver () {
		return gameOver;
	}

	/** Board needs to know when floater is grounded to permanently place floater blocks on combinedBoard & to createNew() floater.
	 * @return */
	public boolean isGrounded () {
		return grounded;
	}

	/** Get's current shape color, useful for Renderer.
	 * @return */
	public int getShapeColor () {
		return shapeType;
	}

	/** Get's next shape to give that hint to player because everyone was doing it.
	 * @return */
	public int getNextShape () {
		if (randomNextShapes == true) {
			return nextRandomShapeType;
		}
		return listNextShapes.get(nextShapeCounter);
	}

	/** Renderer needs pos{...} to draw it and board needs it when the floater can no longer be moved
	 * @return pos{...} */
	public Coords getPosOne () {
		return posOne;
	}

	/** Renderer needs pos{...} to draw it and board needs it when the floater can no longer be moved
	 * @return pos{...} */
	public Coords getPosTwo () {
		return posTwo;
	}

	/** Renderer needs pos{...} to draw it and board needs it when the floater can no longer be moved
	 * @return pos{...} */
	public Coords getPosThree () {
		return posThree;
	}

	/** Renderer needs pos{...} to draw it and board needs it when the floater can no longer be moved
	 * @return pos{...} */
	public Coords getPosFour () {
		return posFour;
	}

	public void setAudio (Audio audio) {
		this.audio = audio;
	}

	/** Sets the board for collisions.
	 * @param board */
	public void setBoard (Board board) {
		this.board = board;
	}

	public void setBagSize (int bagSize) {
		this.bagSize = bagSize;
		if (bagSize == 0) setUseRandomNextShapes();
		initBag();
	}

	public void initBag () {
		// Creates NUM_NEXT_SHAPES sets of NUM_TYPES (2 set of 7, currently).
		for (x = 0; x < bagSize; x++) {
			for (y = 0; y < NUM_TYPES; y++) {
				listNextShapes.add(y);
			}
		}
		System.out.println("listNextShapes size=" + listNextShapes.size());

		// TODO I would have preferred an array and have a min distance apart for shapes... but that's extra work for now.
		Collections.shuffle(listNextShapes);

		// Renderer needs to draw the next shape.
		nextShapeCounter = 0;
	}

	public void setUseRandomNextShapes () {
		randomNextShapes = true;
		nextRandomShapeType = random.nextInt(NUM_TYPES);
	}
}
