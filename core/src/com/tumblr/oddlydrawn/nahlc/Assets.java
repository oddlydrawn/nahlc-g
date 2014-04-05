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
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/** @author oddlydrawn */
public class Assets {
	public static final int BLOCK_WIDTH = 16;
	public static final int BLOCK_HEIGHT = 16;
	private final String PACKED_ATLAS_PATH = "data/gfx/packed.atlas";
	private final String BLOCKS_ATLAS_REGION = "blocksSheetSmall";
	private final String LEFT_ATLAS_REGION = "left";
	private final String RIGHT_ATLAS_REGION = "right";
	private final String DOWN_ATLAS_REGION = "down";
	private final String ROTATE_LEFT_ATLAS_REGION = "rotateLeft";
	private final String ROTATE_RIGHT_ATLAS_REGION = "rotateRight";
	private final String DROP_DOWN_REGION = "dropDown";
	private final String PAUSE_REGION = "pauseButton";
	private final String PAUSE_NOTICE_REGION = "pausedNotice";
	private final String BOX_REGION = "box";
	private final String MAIN_MENU_REGION = "mainMenu";
	private final String NEW_GAME_REGION = "newGame";
	private final String NEW_RECORD_LARGE_REGION = "newRecordLarge";
	private final String GAME_OVER_REGION = "gameOverLarge";
	private final String TITLE_REGION = "title";
	// The number of different color strips in the image.
	private final int MAX_NUMBER_COLOR = 5; // 5
	private TextureRegion[][] textureRegions;
	private TextureAtlas atlas;
	private AtlasRegion blocksAtlas;
	private Sprite leftSprite;
	private Sprite rightSprite;
	private Sprite downSprite;
	private Sprite rotateLeftSprite;
	private Sprite rotateRightSprite;
	private Sprite dropDownSprite;
	private Sprite pauseSprite;
	private Sprite pauseNoticeSprite;
	private Sprite mainMenuSprite;
	private Sprite newGameSprite;
	private Sprite newRecordSprite;
	private Sprite gameOverSprite;
	private Sprite titleSprite;
	private NinePatch boxPatch;
	private int colorIndex;
	private int tmpX;
	private int tmpY;
	private int x;
	private int y;

	public Assets () {

	}

	public void initMainMenu () {
		atlas = new TextureAtlas(Gdx.files.internal(PACKED_ATLAS_PATH));
		boxPatch = new NinePatch(atlas.createPatch(BOX_REGION));
		titleSprite = new Sprite(atlas.findRegion(TITLE_REGION));
	}

	public void initGame () {
		atlas = new TextureAtlas(Gdx.files.internal(PACKED_ATLAS_PATH));
		blocksAtlas = atlas.findRegion(BLOCKS_ATLAS_REGION);
		leftSprite = new Sprite(atlas.findRegion(LEFT_ATLAS_REGION));
		rightSprite = new Sprite(atlas.findRegion(RIGHT_ATLAS_REGION));
		downSprite = new Sprite(atlas.findRegion(DOWN_ATLAS_REGION));
		rotateLeftSprite = new Sprite(atlas.findRegion(ROTATE_LEFT_ATLAS_REGION));
		rotateRightSprite = new Sprite(atlas.findRegion(ROTATE_RIGHT_ATLAS_REGION));
		dropDownSprite = new Sprite(atlas.findRegion(DROP_DOWN_REGION));
		pauseSprite = new Sprite(atlas.findRegion(PAUSE_REGION));
		pauseNoticeSprite = new Sprite(atlas.findRegion(PAUSE_NOTICE_REGION));
		boxPatch = new NinePatch(atlas.createPatch(BOX_REGION));
		mainMenuSprite = new Sprite(atlas.findRegion(MAIN_MENU_REGION));
		newGameSprite = new Sprite(atlas.findRegion(NEW_GAME_REGION));

		downSprite.flip(false, true);
		rotateLeftSprite.flip(false, true);
		rotateRightSprite.flip(false, true);
		dropDownSprite.flip(false, true);
		pauseNoticeSprite.flip(false, true);
		mainMenuSprite.flip(false, true);
		newGameSprite.flip(false, true);

		leftSprite.setPosition(10, 342);
		rightSprite.setPosition(leftSprite.getX() + 79, 342);
		downSprite.setPosition(49, 406);
		rotateLeftSprite.setPosition(172, 342);
		rotateRightSprite.setPosition(246, 342);
		dropDownSprite.setPosition(217, 416);
		pauseSprite.setPosition(262, 280);
		pauseNoticeSprite.setPosition(48, 160);

		// Floater.NUM_TYPES + 1 because there are 7 block shape types, the 8th is empty.
		textureRegions = new TextureRegion[MAX_NUMBER_COLOR][Floater.NUM_TYPES + 1];

		// There are 7 types of shapes, + 1 is the empty background shape to use.
		for (y = 0; y < Floater.NUM_TYPES + 1; y++) {
			// Makes sure we only get the number of block sets that were created.
			for (x = 0; x < MAX_NUMBER_COLOR; x++) {
				// Converts from int to pixels.
				tmpX = x * BLOCK_WIDTH;
				tmpY = y * BLOCK_HEIGHT;
				// Sets one TextureRegion for each element in the array, correct color in correct place including background.
				textureRegions[x][y] = new TextureRegion(blocksAtlas, tmpX, tmpY, BLOCK_WIDTH, BLOCK_HEIGHT);
			}
		}
	}

	public void initGameOver () {
		atlas = new TextureAtlas(Gdx.files.internal(PACKED_ATLAS_PATH));
		boxPatch = new NinePatch(atlas.createPatch(BOX_REGION));
		mainMenuSprite = new Sprite(atlas.findRegion(MAIN_MENU_REGION));
		newGameSprite = new Sprite(atlas.findRegion(NEW_GAME_REGION));
		newRecordSprite = new Sprite(atlas.findRegion(NEW_RECORD_LARGE_REGION));
		gameOverSprite = new Sprite(atlas.findRegion(GAME_OVER_REGION));
	}

	public void disposeGameOver () {
		atlas.dispose();
	}

	public void disposeGame () {
		atlas.dispose();
	}

	public void disposeMainMenu () {
		atlas.dispose();
	}

	/** Get the correct TextureRegion for the correct color/color type.
	 * @param color The color/shape Renderer wants.
	 * @return */
	public TextureRegion getBlock (int color) {
		return textureRegions[colorIndex][color];
	}

	/** Changes to a different column set of blocks, preferably after a certain number of levels. */
	public void changeColors () {
		colorIndex++;
		if (colorIndex >= MAX_NUMBER_COLOR) colorIndex = 0;
	}

	public Sprite getLeftSprite () {
		return leftSprite;
	}

	public Sprite getRightSprite () {
		return rightSprite;
	}

	public Sprite getDownSprite () {
		return downSprite;
	}

	public Sprite getRotateLeftSprite () {
		return rotateLeftSprite;
	}

	public Sprite getRotateRightSprite () {
		return rotateRightSprite;
	}

	public Sprite getDropDownSprite () {
		return dropDownSprite;
	}

	public Sprite getPauseSprite () {
		return pauseSprite;
	}

	public Sprite getPauseNoticeSprite () {
		return pauseNoticeSprite;
	}

	public Sprite getMainMenuSprite () {
		return mainMenuSprite;
	}

	public Sprite getNewGameSprite () {
		return newGameSprite;
	}

	public Sprite getNewRecordSprite () {
		return newRecordSprite;
	}

	public Sprite getGameOverSprite () {
		return gameOverSprite;
	}

	public NinePatch getBoxPatch () {
		return boxPatch;
	}

	public Sprite getTitleSprite () {
		return titleSprite;
	}
}
