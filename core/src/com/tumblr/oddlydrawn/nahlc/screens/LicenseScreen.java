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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.tumblr.oddlydrawn.nahlc.Assets;

/** @author oddlydrawn */
public class LicenseScreen implements Screen {
	private final String LICENSE_PATH = "data/LICENSE-2.0.txt";
	private final String FONT_PATH = "data/fonts/deja.fnt";
	private final float WIDTH = 320;
	private final float HEIGHT = 480;
	final float TABLE_PAD = 10f;
	private String licenseString;
	private Assets assets;
	private Stage stage;
	private Skin skin;
	Game game;

	public LicenseScreen (Game g) {
		game = g;
		stage = new Stage(new FitViewport(WIDTH, HEIGHT));
		skin = new Skin();
		assets = new Assets();
		assets.initMainMenu();
		Gdx.input.setInputProcessor(stage);

		FileHandle handle;
		handle = Gdx.files.internal(LICENSE_PATH);
		licenseString = handle.readString();

		Table table = new Table();
		table.setFillParent(true);
		stage.addActor(table);

		skin.add("default", new BitmapFont(Gdx.files.internal(FONT_PATH)));

		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = skin.getFont("default");
		skin.add("default", labelStyle);

		ScrollPaneStyle scrollPaneStyle = new ScrollPaneStyle();
		skin.add("default", scrollPaneStyle);

		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.font = skin.getFont("default");
		textButtonStyle.up = new NinePatchDrawable(assets.getBoxPatch());
		skin.add("default", textButtonStyle);

		Label license = new Label(licenseString, skin);
		ScrollPane scrollPane = new ScrollPane(license, skin);
		scrollPane.setFlickScroll(true);
		table.add(scrollPane);
		table.row();

		TextButton backButton = new TextButton("Back", skin);
		table.add(backButton).padTop(TABLE_PAD);
		table.padTop(TABLE_PAD);
		table.padBottom(TABLE_PAD);
		table.row();

		backButton.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				dispose();
				game.setScreen(new MainMenuScreen(game));
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
		assets.disposeMainMenu();
	}

}
