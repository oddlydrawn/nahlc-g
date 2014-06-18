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
import com.badlogic.gdx.graphics.GL20;
import com.tumblr.oddlydrawn.nahlc.Renderer;
import com.tumblr.oddlydrawn.nahlc.SavedStuff;

/** @author oddlydrawn */
public class LoadingScreen implements Screen {
	Game g;
	SavedStuff savedStuff;
	float LOVELY_GRAY;

	public LoadingScreen (Game g) {
		this.g = g;
		savedStuff = new SavedStuff();

		savedStuff.loadPreferences();

		LOVELY_GRAY = Renderer.LOVELY_GRAY;
	}

	@Override
	public void render (float delta) {
		Gdx.gl.glClearColor(LOVELY_GRAY, LOVELY_GRAY, LOVELY_GRAY, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.app.log("nahlc", "loading screen render");

		if (savedStuff.savedGameExists()) {
			Gdx.app.log("nahlc", "saved game exists");
			g.setScreen(new GameScreen(g, true));
		} else {
			Gdx.app.log("nahlc", "saved game does not exist");
			g.setScreen(new MainMenuScreen(g));
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
	}

	@Override
	public void resume () {
	}

	@Override
	public void dispose () {
	}

}
