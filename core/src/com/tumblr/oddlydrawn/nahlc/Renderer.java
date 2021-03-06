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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/** @author oddlydrawn */
public class Renderer {
	final public static float WIDTH = 320; // 320
	final public static float HEIGHT = 480; // 480
	private final String FONT_LOC = "data/fonts/d.fnt";
	private final String SCORE = "Score:";
	private final String LEVEL = "Level:";
	private final String HI_SCORE = "HiScore:";
	private final float GLYPH_WIDTH = 11; // 11
	private final float RIGHT_ORIGIN = 300; // 300
	public static final float LOVELY_GRAY = 0.18f; // 0.18f
	private final int NEXT_SHAPE_ORIGIN_X = 15; // 15
	private final int NEXT_SHAPE_ORIGIN_Y = 1; // 1
	private final int NEXT_SHAPE_BG_X = 15; // 15
	private final int NEXT_SHAPE_BG_Y = 1; // 1
	private final int PAD_HORIZONTAL = 16; // 16
	private final int PAD_VERTICAL = 0; // 0
	private final int BACKGROUND = 7; // 7
	private final int PAD_THREE = 3; // 3
	private final int PAD_TWO = 2; // 2
	private final int I = 0; // 0
	private final int O = 1; // 1
	private final int T = 2; // 2
	private final int J = 3; // 3
	private final int L = 4; // 4
	private final int S = 5; // 5
	private final int Z = 6; // 6
	private BitmapFont font;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Board board;
	private Floater floater;
	private Assets assets;
	private TextureRegion region;
	private Texture distanceFontTexture;
	// Coords for next shape.
	private Coords posOne;
	private Coords posTwo;
	private Coords posThree;
	private Coords posFour;
	private String tmpString;
	private String highScoreString;
	private SavedStuff savedStuff;
	private ShaderProgram fontShader;
	private int highScore;
	private int color;
	private int x;
	private int y;
	private int tmpX;
	private int tmpY;
	private int nextShape;

	/** Creates new objects and sets camera up. */
	public Renderer () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera(WIDTH, HEIGHT);
		camera.setToOrtho(true, WIDTH, HEIGHT);

		distanceFontTexture = new Texture(Gdx.files.internal("data/fonts/d.png"), true); // true enables mipmaps
		distanceFontTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear); // linear filtering in nearest
// mipmap image

		font = new BitmapFont(Gdx.files.internal(FONT_LOC), new TextureRegion(distanceFontTexture), true);

		fontShader = new ShaderProgram(Gdx.files.internal("data/shaders/distancefield.vert"),
			Gdx.files.internal("data/shaders/distancefield.frag"));
		if (!fontShader.isCompiled()) {
			Gdx.app.error("fontShader", "compilation failed:\n" + fontShader.getLog());
		}

		posOne = new Coords();
		posTwo = new Coords();
		posThree = new Coords();
		posFour = new Coords();

	}

	/** render() renders stuff, clears the screen. */
	public void render (float delta) {
		Gdx.gl.glClearColor(LOVELY_GRAY, LOVELY_GRAY, LOVELY_GRAY, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (floater.isGameOver()) {
			System.out.println("game over");
		}
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		batch.setShader(null);
		batch.disableBlending();

		// Next shape isn't a board so it doesn't have empty squares to serve as a background.
		drawNextShapeBackground();

		// Order matters. Board's empty squares serve as background.
		if (savedStuff.isUpsideDown() == true) {
			drawBoardUpsideDown();
			nextShapePosUpdate();
			vertInvertNextShape();
		} else {
			drawBoard(batch);
			nextShapePosUpdate();
		}
		drawNextShape();

		batch.enableBlending();

		assets.getLeftSprite().draw(batch);
		assets.getRightSprite().draw(batch);
		assets.getDownSprite().draw(batch);
		assets.getRotateLeftSprite().draw(batch);
		assets.getRotateRightSprite().draw(batch);
		assets.getDropDownSprite().draw(batch);
		assets.getPauseSprite().draw(batch);

		batch.setShader(fontShader);
		// "Level:"
		font.draw(batch, LEVEL, 235, 120);
		// Level number
		tmpString = Integer.toString(board.getCurrentLevel());
		font.draw(batch, tmpString, rightJustify(tmpString), 142);

		// "Score:"
		font.draw(batch, SCORE, 235, 174);
		// Score number from board
		tmpString = Integer.toString(board.getCurrentScore());
		font.draw(batch, tmpString, rightJustify(tmpString), 196);

		// "HiScore:"
		font.draw(batch, HI_SCORE, 235, 228);
		if (board.getCurrentScore() < highScore) {
			font.draw(batch, highScoreString, rightJustify(highScoreString), 250);
		} else {
			tmpString = Integer.toString(board.getCurrentScore());
			font.draw(batch, tmpString, rightJustify(tmpString), 250);
		}

		batch.setShader(null);

		if (floater.isPaused() == true) {
			assets.getBoxPatch().draw(batch, Assets.BLOCK_WIDTH * PAD_TWO, Assets.BLOCK_HEIGHT * PAD_TWO,
				(Board.BOARD_WIDTH - PAD_TWO) * Assets.BLOCK_WIDTH, (Board.BOARD_HEIGHT - PAD_THREE) * Assets.BLOCK_HEIGHT);
			assets.getPauseNoticeSprite().draw(batch);
		}
		batch.end();
	}

	private void drawBoard (SpriteBatch batch) {
		// Since board's y=0 is just padding for correct, simple drops, we omit that.
		for (y = 1; y < Board.BOARD_HEIGHT; y++) {
			for (x = 0; x < Board.BOARD_WIDTH; x++) {
				// Converts for() coordinates to pixels.
				tmpX = x * Assets.BLOCK_WIDTH;
				tmpY = y * Assets.BLOCK_HEIGHT;

				// Adds the padding for pretty and even borders.
				tmpX += PAD_HORIZONTAL;
				tmpY += PAD_VERTICAL;

				// Gets the color of the block at combinedBoard's (x, y)
				color = board.getColor(x, y);

				// Gets the corresponding textureRegion from assets.
				region = assets.getBlock(color);

				// Then we paint it to the screen. Isn't it pretty?
				batch.draw(region, tmpX, tmpY, Assets.BLOCK_WIDTH, Assets.BLOCK_HEIGHT);
			}
		}
		drawFloaterBlock(floater.getPosOne());
		drawFloaterBlock(floater.getPosTwo());
		drawFloaterBlock(floater.getPosThree());
		drawFloaterBlock(floater.getPosFour());
	}

	private void drawFloaterBlock (Coords coords) {
		tmpX = coords.getX() * Assets.BLOCK_WIDTH;
		tmpY = coords.getY() * Assets.BLOCK_HEIGHT;
		tmpX += PAD_HORIZONTAL;
		tmpY += PAD_VERTICAL;
		color = floater.getShapeColor();
		region = assets.getBlock(color);
		batch.draw(region, tmpX, tmpY, Assets.BLOCK_WIDTH, Assets.BLOCK_HEIGHT);
	}

	private void drawBoardUpsideDown () {
		// Since board's y=0 is just padding for correct, simple drops, we omit that.
		for (y = 0; y < Board.BOARD_HEIGHT - 1; y++) {
			for (x = 0; x < Board.BOARD_WIDTH; x++) {
				// Converts for() coordinates to pixels.
				tmpX = x * Assets.BLOCK_WIDTH;
				tmpY = y * Assets.BLOCK_HEIGHT;

				// Adds the padding for pretty and even borders.
				tmpX += PAD_HORIZONTAL;
				tmpY += PAD_HORIZONTAL;

				// Gets the color of the block at combinedBoard's (x, y)
				color = board.getColor(x, Board.BOARD_HEIGHT - 1 - y);

				// Gets the corresponding textureRegion from assets.
				region = assets.getBlock(color);

				// Then we paint it to the screen. Isn't it pretty?
				batch.draw(region, tmpX, tmpY, Assets.BLOCK_WIDTH, Assets.BLOCK_HEIGHT);
			}
		}
		drawFloaterBlockUpsideDown(floater.getPosOne());
		drawFloaterBlockUpsideDown(floater.getPosTwo());
		drawFloaterBlockUpsideDown(floater.getPosThree());
		drawFloaterBlockUpsideDown(floater.getPosFour());
	}

	private void drawFloaterBlockUpsideDown (Coords coords) {
		tmpX = coords.getX() * Assets.BLOCK_WIDTH;
		// Upside down the vertical
		tmpY = Board.BOARD_HEIGHT - 1 - coords.getY();
		tmpY *= Assets.BLOCK_HEIGHT;

		// Adds the padding for pretty and even borders.
		tmpX += PAD_HORIZONTAL;
		tmpY += PAD_HORIZONTAL;

		color = floater.getShapeColor();

		// Gets the corresponding textureRegion from assets.
		region = assets.getBlock(color);

		// Then we paint it to the screen. Isn't it pretty?
		batch.draw(region, tmpX, tmpY, Assets.BLOCK_WIDTH, Assets.BLOCK_HEIGHT);
	}

	private void drawNextShapeBackground () {
		region = assets.getBlock(BACKGROUND);
		for (x = 0; x < 4; x++) {
			for (y = 0; y < 6; y++) {
				// Converts for() coordinates to pixels.
				tmpX = x + NEXT_SHAPE_BG_X;
				tmpX *= Assets.BLOCK_WIDTH;

				tmpY = y + NEXT_SHAPE_BG_Y;
				tmpY *= Assets.BLOCK_HEIGHT;

				// Pretty, pretty.
				batch.draw(region, tmpX, tmpY, Assets.BLOCK_WIDTH, Assets.BLOCK_HEIGHT);
			}
		}
	}

	private void nextShapePosUpdate () {
		// Gets next shape and corresponding region.
		nextShape = floater.getNextShape();
		region = assets.getBlock(nextShape);

		// Determines the basic coordinates needed to draw the right shape.
		switch (nextShape) {
		case I:
			posOne.set(1, 1);
			posTwo.set(1, 2);
			posThree.set(1, 3);
			posFour.set(1, 4);
			break;
		case O:
			posOne.set(1, 2);
			posTwo.set(2, 2);
			posThree.set(1, 3);
			posFour.set(2, 3);
			break;
		case T:
			posOne.set(1, 2);
			posTwo.set(1, 3);
			posThree.set(2, 3);
			posFour.set(1, 4);
			break;
		case J:
			posOne.set(1, 2);
			posTwo.set(1, 3);
			posThree.set(1, 4);
			posFour.set(2, 2);
			break;
		case L:
			posOne.set(2, 2);
			posTwo.set(2, 3);
			posThree.set(2, 4);
			posFour.set(1, 2);
			break;
		case S:
			posOne.set(1, 2);
			posTwo.set(1, 3);
			posThree.set(2, 3);
			posFour.set(2, 4);
			break;
		case Z:
			posOne.set(2, 2);
			posTwo.set(2, 3);
			posThree.set(1, 3);
			posFour.set(1, 4);
			break;
		}
	}

	private void vertInvertNextShape () {
		posOne.y = 5 - posOne.y;
		posTwo.y = 5 - posTwo.y;
		posThree.y = 5 - posThree.y;
		posFour.y = 5 - posFour.y;
	}

	private void drawNextShape () {
		// Adds the distance from origin to block's coordinates.
		posOne.x += NEXT_SHAPE_ORIGIN_X;
		// Converts to pixels.
		posOne.x *= Assets.BLOCK_WIDTH;

		// Does the same with y coordinates.
		posOne.y += NEXT_SHAPE_ORIGIN_Y;
		posOne.y *= Assets.BLOCK_HEIGHT;

		// Draws the single block.
		batch.draw(region, posOne.x, posOne.y, Assets.BLOCK_WIDTH, Assets.BLOCK_HEIGHT);

		// Et cetera, et cetera.
		posTwo.x += NEXT_SHAPE_ORIGIN_X;
		posTwo.x *= Assets.BLOCK_WIDTH;
		posTwo.y += NEXT_SHAPE_ORIGIN_Y;
		posTwo.y *= Assets.BLOCK_HEIGHT;
		batch.draw(region, posTwo.x, posTwo.y, Assets.BLOCK_WIDTH, Assets.BLOCK_HEIGHT);

		posThree.x += NEXT_SHAPE_ORIGIN_X;
		posThree.x *= Assets.BLOCK_WIDTH;
		posThree.y += NEXT_SHAPE_ORIGIN_Y;
		posThree.y *= Assets.BLOCK_HEIGHT;
		batch.draw(region, posThree.x, posThree.y, Assets.BLOCK_WIDTH, Assets.BLOCK_HEIGHT);

		posFour.x += NEXT_SHAPE_ORIGIN_X;
		posFour.x *= Assets.BLOCK_WIDTH;
		posFour.y += NEXT_SHAPE_ORIGIN_Y;
		posFour.y *= Assets.BLOCK_HEIGHT;
		batch.draw(region, posFour.x, posFour.y, Assets.BLOCK_WIDTH, Assets.BLOCK_HEIGHT);
	}

	/** Numbers should be right justified, I think. This does that.
	 * @return The X position needed to correctly right justify text. */
	private float rightJustify (String s) {
		x = s.length() - 1;
		x *= GLYPH_WIDTH;
		return RIGHT_ORIGIN - x;
	}

	public void setFloater (Floater floater) {
		this.floater = floater;
		int bagSize = savedStuff.getBagSize();
		floater.setBagSize(bagSize);
	}

	public void setAssets (Assets assets) {
		this.assets = assets;
	}

	public void setBoard (Board board) {
		this.board = board;
	}

	public void setAudio (Audio audio) {
		boolean soundOn = savedStuff.isSoundOn();
		boolean musicOn = savedStuff.isMusicOn();
		audio.setSoundTo(soundOn);
		audio.setMusicTo(musicOn);
	}

	public OrthographicCamera getCam () {
		return camera;
	}

	public void resize (int width, int height) {
		if (camera != null) {
			camera.setToOrtho(true, WIDTH, HEIGHT);
		}
	}

	public void setController (Controller controller) {
		controller.setSavedStuff(savedStuff);
	}

	public void setSavedStuff (SavedStuff savedStuff) {
		this.savedStuff = savedStuff;

		highScoreString = savedStuff.getHighScore();
		highScore = Integer.parseInt(savedStuff.getHighScore());
	}

	public void dispose () {
		fontShader.dispose();
		distanceFontTexture.dispose();
		font.dispose();
		batch.dispose();
	}
}
