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

/** @author oddlydrawn */
public class Board {
	public static final int SCORE_INCREASE_GROUNDED = 1; // 1
	public static final int SCORE_INCREASE_SINGLE = 2; // 20
	public static final int SCORE_INCREASE_DOUBLE = 5; // 50
	public static final int SCORE_INCREASE_TRIPLE = 8; // 80
	public static final int SCORE_INCREASE_NAHLC = 12; // 125
	public static final int NUM_ROWS_TO_LEVEL_UP = 10; // 10
	public static final int NUM_LEVEL_TO_FINISH = 20; // 20
	public static final short BOARD_WIDTH = 12; // 10
	public static final short BOARD_HEIGHT = 21; // 21
	private final int SINGLE = 1;
	private final int DOUBLE = 2;
	private final int TRIPLE = 3;
	private final int NAHLC = 4;
	private final int EMPTY = 7;
	private Controller controller;
	private Floater floater;
	private Assets assets;
	private Audio audio;
	private int[][] board;
	private int[][] combinedBoard;
	private int color;
	private int x;
	private int y;
	private int row;
	private int numTotalCompletedRows;
	private int currentLevel;
	private int currentScore;
	private int completedLines;
	private int numTotalBlocks;
	private short counter;

	/** Creates a blank board. Usually blank. */
	public void createBoard () {
		board = new int[BOARD_WIDTH][BOARD_HEIGHT];
		combinedBoard = new int[BOARD_WIDTH][BOARD_HEIGHT];

		// Empties out the entire board.
		for (x = 0; x < BOARD_WIDTH; x++) {
			for (y = 0; y < BOARD_HEIGHT; y++) {
				board[x][y] = EMPTY;
			}
		}

		// Empties out the entire combinedBoard.
		for (x = 0; x < Board.BOARD_WIDTH; x++) {
			for (y = 0; y < Board.BOARD_HEIGHT; y++) {
				board[x][y] = EMPTY;
			}
		}
		numTotalCompletedRows = 0;
	}

	/** Updates all blocks of the combinedBoard which is a combination of Board's board and the Floater. This might belong in Board. */
	public void updateCombinedBoard () {
		// Goes through the board, copying all color fields.
		for (x = 0; x < BOARD_WIDTH; x++) {
			for (y = 0; y < BOARD_HEIGHT; y++) {
				color = getColor(x, y);
				combinedBoard[x][y] = color;
			}
		}

		// Ugly but gets (x, y) of each floater block and sets the Color on the combinedBoard.
		combinedBoard[floater.getPosOne().x][floater.getPosOne().y] = floater.getShapeColor();
		combinedBoard[floater.getPosTwo().x][floater.getPosTwo().y] = floater.getShapeColor();
		combinedBoard[floater.getPosThree().x][floater.getPosThree().y] = floater.getShapeColor();
		combinedBoard[floater.getPosFour().x][floater.getPosFour().y] = floater.getShapeColor();

		// If the floater has reached the bottom of the board of stopped on top of a block.
		if (floater.getGrounded() == true) {
			// Add the floater blocks to the board.
			board[floater.getPosOne().x][floater.getPosOne().y] = floater.getShapeColor();
			board[floater.getPosTwo().x][floater.getPosTwo().y] = floater.getShapeColor();
			board[floater.getPosThree().x][floater.getPosThree().y] = floater.getShapeColor();
			board[floater.getPosFour().x][floater.getPosFour().y] = floater.getShapeColor();

			// Keeps track of the total number of blocks currently on the board.
			// TODO C-c-c-combo multiplier. But how many max digits should the score be? Who knows. Not me.
			numTotalBlocks += 4;

			// TODO probably where we want to play an animation

			// Checks for completed lines.
			completedLines = getNumCompleteLines();
			if (completedLines > 0) {

				// Keeps track of the total number of blocks currently on the board.
				numTotalBlocks -= completedLines * BOARD_WIDTH;

				switch (completedLines) {
				case SINGLE:
					increaseScoreSingle();
					break;
				case DOUBLE:
					increaseScoreDouble();
					break;
				case TRIPLE:
					increaseScoreTriple();
					break;
				case NAHLC:
					increaseScoreNahlc();
					break;
				}
				clearAllCompleteLines();
			}

			controller.resetTimer();
			controller.resetHeldTimer();
			// Spawn a 'new' floater.
			floater.createNew();

			currentScore += SCORE_INCREASE_GROUNDED;
		}
	}

	/** Identifies the lines that are completely solid, call dropBlocks(row) for all cleared rows */
	private void clearAllCompleteLines () {
		for (row = 0; row < BOARD_HEIGHT; row++) {
			if (isLineComplete(row)) {
				dropBlocks(row);
				numTotalCompletedRows++;
				if (numTotalCompletedRows % NUM_ROWS_TO_LEVEL_UP == 0) {
					currentLevel++;
					assets.changeColors();
					controller.levelUp();
				}
			}
		}
		audio.playLaserish();
	}

	/** After clearing a row, this makes all blocks drop down to their correct positions.
	 * @param row The cleared row. */
	private void dropBlocks (int row) {
		// Starts on the cleared row (bottom). Ends at the top of the board.
		for (y = row; y > 0; y--) {
			// Applies the drop to each block across the width of the board.
			for (x = 0; x < BOARD_WIDTH; x++) {
				// Gets the values from the block directly above.
				color = board[x][y - 1];

				// Applies the values to the empty block. This is the drop.
				board[x][y] = color;
			}
		}
	}

	private void increaseScoreSingle () {
		currentScore += SCORE_INCREASE_SINGLE * numTotalBlocks;
	}

	private void increaseScoreDouble () {
		currentScore += SCORE_INCREASE_DOUBLE * numTotalBlocks;
	}

	private void increaseScoreTriple () {
		currentScore += SCORE_INCREASE_TRIPLE * numTotalBlocks;
	}

	private void increaseScoreNahlc () {
		currentScore += SCORE_INCREASE_NAHLC * numTotalBlocks;
	}

	/** Checks if block in a row is solid.
	 * @param y The row to check.
	 * @return false if one block is empty. */
	private boolean isLineComplete (int y) {
		for (x = 0; x < BOARD_WIDTH; x++) {
			if (isFilled(x, y) == false) return false;
		}
		return true;
	}

	/** Checks if the coordinate on the board is filled using Coords.
	 * @param coords
	 * @return False if empty. True if not empty. */
	public boolean isFilled (Coords coords) {
		if (board[coords.x][coords.y] != EMPTY) return true;
		return false;
	}

	/** Checks if the coordinate on the board is filled using ints.
	 * @param x
	 * @param y
	 * @return False if empty. True if not empty. */
	public boolean isFilled (int x, int y) {
		if (board[x][y] != EMPTY) return true;
		return false;
	}

	/** Identifies and counts the number of lines that are completely solid and are ready to be cleared */
	private short getNumCompleteLines () {
		counter = 0;
		// Goes through the board's rows.
		for (y = 0; y < BOARD_HEIGHT; y++) {
			// Checks if the current row is complete, increases counter if it is.
			if (isLineComplete(y)) counter++;
		}
		return counter;
	}

	/** Checks if the coordinate on the combinedBoard is filled using ints.
	 * @param x
	 * @param y
	 * @return False if empty. True if filled. */
	public int getCombinedBoardColor (int x, int y) {
		return combinedBoard[x][y];
	}

	public int getCurrentLevel () {
		return currentLevel;
	}

	/** Gets the color that is in a block. Simultaneously tells if if it's empty based on the color.
	 * @param x
	 * @param y
	 * @return */
	private int getColor (int x, int y) {
		return board[x][y];
	}

	public int getCurrentScore () {
		return currentScore;
	}

	public void setAudio (Audio audio) {
		this.audio = audio;
	}

	/** Board needs Assets to change color after (numTotalCompletedRows % NUM_TO_CHANGE_COLOR == 0)
	 * @param assets */
	public void setAssets (Assets assets) {
		this.assets = assets;
	}

	/** Board needs Controller to reset drop timer after floater is grounded.
	 * @param controller */
	public void setController (Controller controller) {
		this.controller = controller;
	}

	/** We need the floater because of reasons.
	 * @param floater */
	public void setFloater (Floater floater) {
		this.floater = floater;
	}
}
