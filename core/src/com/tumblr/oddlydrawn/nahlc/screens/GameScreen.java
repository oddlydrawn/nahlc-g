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

package com.tumblr.oddlydrawn.nahlc.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.tumblr.oddlydrawn.nahlc.Assets;
import com.tumblr.oddlydrawn.nahlc.Audio;
import com.tumblr.oddlydrawn.nahlc.Board;
import com.tumblr.oddlydrawn.nahlc.Controller;
import com.tumblr.oddlydrawn.nahlc.Floater;
import com.tumblr.oddlydrawn.nahlc.Renderer;
import com.tumblr.oddlydrawn.nahlc.SavedStuff;

/** @author oddlydrawn */
public class GameScreen implements Screen {
	public final static String FLOATER_JSON = "floater.json";
	public final static String BOARD_JSON = "board.json";
	private final float TIME_TO_DISPOSE = 1.1f;
	private final boolean DONT_APPEND = false;
	private final boolean MAKE_NEW = true;
	private Game game;
	private Board board;
	private Controller controller;
	private Floater floater;
	private Renderer renderer;
	private Assets assets;
	private Audio audio;
	private SavedStuff savedStuff;
	private float timer;
	private boolean playedHurt;
	private Json json;

	public GameScreen (Game g) {
		game = g;
		// New all the objects.
		board = new Board();
		controller = new Controller();
		floater = new Floater(MAKE_NEW);
		renderer = new Renderer();
		assets = new Assets();
		audio = new Audio();
		savedStuff = new SavedStuff();
		json = new Json();

		savedStuff.loadScores();
		savedStuff.loadPreferences();
		assets.initGame();

		shareObjects(true);
		fillBoardWithBlocks();
	}

	public GameScreen (Game g, boolean loadSavedGame) {
		game = g;
		json = new Json();
		// load old objects
		loadGameState();
		floater.initObjectsAfterSerialization();

		// New some the objects.
		controller = new Controller();
		renderer = new Renderer();
		assets = new Assets();
		audio = new Audio();
		savedStuff = new SavedStuff();

		savedStuff.loadScores();
		savedStuff.loadPreferences();
		assets.initGame();

		shareObjects(false);

	}

	public void loadGameState () {
		FileHandle fileHandle = Gdx.files.local(FLOATER_JSON);
		String floaterJsonString = fileHandle.readString();
		floater = json.fromJson(Floater.class, floaterJsonString);

		FileHandle fileHandle2 = Gdx.files.local(BOARD_JSON);
		String boardJsonString = fileHandle2.readString();
		board = json.fromJson(Board.class, boardJsonString);
	}

	public void saveGameState () {
// json.setIgnoreUnknownFields(false);
// Gdx.app.log("mew", json.toJson(floater, Object.class));
// Gdx.app.log("mew", json.toJson(board, Object.class));
// // System.out.println(json.prettyPrint(board));
// Gdx.app.log("mew", json.toJson(controller, Object.class));
// System.out.println(json.prettyPrint(controller));

		String floaterJsonString = json.toJson(floater, Object.class);
		String boardJsonString = json.toJson(board, Object.class);

		FileHandle fileHandle = Gdx.files.local(FLOATER_JSON);
		fileHandle.writeString(floaterJsonString, DONT_APPEND);

		FileHandle fileHandle2 = Gdx.files.local(BOARD_JSON);
		fileHandle2.writeString(boardJsonString, DONT_APPEND);
		Gdx.app.log("nahlc", "saved game thingy");
		Gdx.app.log("nahlc", "floater exists: " + Gdx.files.local(FLOATER_JSON).exists());
		Gdx.app.log("nahlc", "board exists: " + Gdx.files.local(BOARD_JSON).exists());
	}

	public void shareObjects (boolean makeNew) {
		// All the objects are best friends forever. Order probably matters, almost definitely.
		renderer.setSavedStuff(savedStuff);
		controller.setFloater(floater);
		floater.setBoard(board);
		if (makeNew) board.createBoard();
		renderer.setFloater(floater);
		if (makeNew) floater.createNew();
		renderer.setBoard(board);
		board.setFloater(floater);
		renderer.setAssets(assets);
		board.setAssets(assets);
		board.setController(controller);
		board.setAudio(audio);
		floater.setAudio(audio);
		controller.setAssets(assets);
		controller.setCamera(renderer.getCam());
		renderer.setController(controller);
		renderer.setAudio(audio);
	}

	public void fillBoardWithBlocks () {
		board.fillWithBlocks(savedStuff.getLevelSize());
	}

	@Override
	public void render (float delta) {
		// Process those inputs.
		delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());
		try {
			if (floater.isGameOver() == false) {
				controller.update(delta);
			} else {
				timer += delta;
				if (playedHurt == false) {
					audio.playHurt();
					playedHurt = true;
				}

				if (timer > TIME_TO_DISPOSE) {
					dispose();
					game.setScreen(new GameOverScreen(game, board.getCurrentLevel(), board.getCurrentScore()));
				}
			}
			// Update board after inputs.
			board.updateBoard();

			// What do you think this is? O.o
			renderer.render(delta);
		} catch (RuntimeException ex) {
			ex.printStackTrace(System.out);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

	}

	@Override
	public void resize (int width, int height) {
	}

	@Override
	public void show () {
	}

	@Override
	public void hide () {
	}

	@Override
	public void pause () {
		floater.setPausedTrue();
		savedStuff.setSavedGameExists(true);
		savedStuff.savePreferences();
		saveGameState();
	}

	@Override
	public void resume () {
	}

	@Override
	public void dispose () {
		audio.dispose();
		assets.disposeGame();
		renderer.dispose();
	}
}
