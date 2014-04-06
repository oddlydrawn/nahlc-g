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
import com.tumblr.oddlydrawn.nahlc.Assets;
import com.tumblr.oddlydrawn.nahlc.Audio;
import com.tumblr.oddlydrawn.nahlc.Board;
import com.tumblr.oddlydrawn.nahlc.Controller;
import com.tumblr.oddlydrawn.nahlc.Floater;
import com.tumblr.oddlydrawn.nahlc.Renderer;

/** @author oddlydrawn */
public class GameScreen implements Screen {
	private final float TIME_TO_DISPOSE = 1.1f;
	private Game game;
	private Board board;
	private Controller controller;
	private Floater floater;
	private Renderer renderer;
	private Assets assets;
	private Audio audio;
	private float timer;
	private boolean playedHurt;

	public GameScreen (Game g) {
		game = g;
		// New all the objects.
		board = new Board();
		controller = new Controller();
		floater = new Floater();
		renderer = new Renderer();
		assets = new Assets();
		audio = new Audio();

		assets.initGame();

		// All the objects are best friends forever. Order probably matters, almost definitely.
		controller.setFloater(floater);
		floater.setBoard(board);
		board.createBoard();
		renderer.setFloater(floater);
		floater.createNew();
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

	@Override
	public void render (float delta) {
		// Process those inputs.
		delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());
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
	}

	@Override
	public void resume () {
	}

	@Override
	public void dispose () {
		audio.dispose();
		assets.disposeGame();
	}
}
