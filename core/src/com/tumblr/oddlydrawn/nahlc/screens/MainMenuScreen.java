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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.tumblr.oddlydrawn.nahlc.Assets;
import com.tumblr.oddlydrawn.nahlc.Renderer;

/** @author oddlydrawn */
public class MainMenuScreen implements Screen {
	private Game game;
	private Stage stage;
	private Table table;
	private Assets assets;
	private Skin skin;

	public MainMenuScreen (Game g) {
		game = g;
		assets = new Assets();
		assets.initMainMenu();
		stage = new Stage(new StretchViewport(Renderer.WIDTH, Renderer.HEIGHT));
		Gdx.input.setInputProcessor(stage);
		skin = new Skin();

		skin.add("default", new BitmapFont(Gdx.files.internal("data/fonts/deja.fnt")));

		skin.add("patch", new NinePatch(assets.getBoxPatch()));

		Image boxPatchImage = new Image(assets.getBoxPatch());
		boxPatchImage.setSize(Renderer.WIDTH - 40, Renderer.HEIGHT - 40);
		boxPatchImage.setPosition(20, 22);
		stage.addActor(boxPatchImage);

		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.font = skin.getFont("default");
		textButtonStyle.up = new NinePatchDrawable(assets.getBoxPatch());
		skin.add("default", textButtonStyle);

		table = new Table();
		table.setFillParent(true);
		table.setPosition(0, 0);
		stage.addActor(table);
		table.debug();
		table.debugTable();

		Image titleImage = new Image(assets.getTitleSprite());
		table.add(titleImage).padBottom(200f);
		table.row();

		TextButton newGameButton = new TextButton("New Game", skin);
		table.add(newGameButton).padBottom(20f);
		table.row();

		TextButton licenseButton = new TextButton("License", skin);
		table.add(licenseButton).padBottom(10f);
		table.row();

		newGameButton.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				dispose();
				game.setScreen(new SetupScreen(game));
			}
		});

		licenseButton.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				dispose();
				game.setScreen(new LicenseScreen(game));
			}
		});
	}

	@Override
	public void render (float delta) {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize (int width, int height) {
		stage.getViewport().update(width, height, true);
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
		stage.dispose();
		skin.dispose();
		assets.disposeGame();
	}
}
