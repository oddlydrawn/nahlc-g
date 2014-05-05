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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.tumblr.oddlydrawn.nahlc.Assets;
import com.tumblr.oddlydrawn.nahlc.SavedStuff;

/** @author oddlydrawn */
public class SetupScreen implements Screen {
	final float PAD = 20f;
	Game game;
	Assets assets;
	Stage stage;
	Table table;
	Skin skin;
	private boolean soundOn;
	private boolean musicOn;
	private boolean upsideDown = false;

	public SetupScreen (Game g) {
		game = g;

		assets = new Assets();
		stage = new Stage(new ExtendViewport(320, 480));
		Gdx.input.setInputProcessor(stage);
		skin = new Skin();
		assets.initSetupScreen();

		table = new Table();
		table.setFillParent(true);
		table.align(Align.left);
		stage.addActor(table);

		Table buttonsTable = new Table();

		skin.add("default", new BitmapFont(Gdx.files.internal("data/fonts/deja.fnt")));

		ImageTextButtonStyle imageTextButtonStyle = new ImageTextButtonStyle();
		imageTextButtonStyle.checked = new TextureRegionDrawable(assets.getSelectedSprite());
		imageTextButtonStyle.up = new TextureRegionDrawable(assets.getUnselectedSprite());
		imageTextButtonStyle.font = skin.getFont("default");
		skin.add("default", imageTextButtonStyle);

		CheckBoxStyle checkBoxStyle = new CheckBoxStyle();
		checkBoxStyle.checkboxOff = new TextureRegionDrawable(assets.getUncheckedSprite());
		checkBoxStyle.checkboxOn = new TextureRegionDrawable(assets.getCheckedSprite());
		checkBoxStyle.font = skin.getFont("default");
		skin.add("default", checkBoxStyle);

		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.font = skin.getFont("default");
		textButtonStyle.up = new NinePatchDrawable(assets.getBoxPatch());
		skin.add("default", textButtonStyle);

		final CheckBox soundOnBox = new CheckBox("Sound?", skin);
		table.add(soundOnBox);
		table.row();

		final CheckBox musicOnBox = new CheckBox("Music?", skin);
		table.add(musicOnBox);
		table.row();

		final CheckBox upsideDownBox = new CheckBox("Upside Down?", skin);
		table.add(upsideDownBox);
		table.row();

		final ImageTextButton zero = new ImageTextButton("0", skin);
		buttonsTable.add(zero);
		final ImageTextButton one = new ImageTextButton("1", skin);
		buttonsTable.add(one);
		final ImageTextButton two = new ImageTextButton("2", skin);
		buttonsTable.add(two);
		final ImageTextButton three = new ImageTextButton("3", skin);
		buttonsTable.add(three);
		final ImageTextButton four = new ImageTextButton("4", skin);
		buttonsTable.add(four);
		buttonsTable.row();
		final ImageTextButton five = new ImageTextButton("5", skin);
		buttonsTable.add(five);
		final ImageTextButton six = new ImageTextButton("6", skin);
		buttonsTable.add(six);
		final ImageTextButton seven = new ImageTextButton("7", skin);
		buttonsTable.add(seven);
		final ImageTextButton eight = new ImageTextButton("8", skin);
		buttonsTable.add(eight);
		final ImageTextButton nine = new ImageTextButton("9", skin);
		buttonsTable.add(nine);

		ButtonGroup groupButtons = new ButtonGroup();
		groupButtons.add(zero);
		groupButtons.add(one);
		groupButtons.add(two);
		groupButtons.add(three);
		groupButtons.add(four);
		groupButtons.add(five);
		groupButtons.add(six);
		groupButtons.add(seven);
		groupButtons.add(eight);
		groupButtons.add(nine);
		groupButtons.setMaxCheckCount(1);
		groupButtons.uncheckAll();
		zero.setChecked(true);

		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = skin.getFont("default");
		skin.add("default", labelStyle);

		Label levelLabel = new Label("Level:", skin);
		table.add(levelLabel).padTop(PAD).align(Align.left);
		table.row();

		table.add(buttonsTable).padBottom(PAD);
		table.row();

		Label bagSize = new Label("Shape bag size: ", skin);
		table.add(bagSize).align(Align.left);
		table.row();

		Table bagTable = new Table();

		final ImageTextButton sizeRandom = new ImageTextButton("Rnd", skin);
		bagTable.add(sizeRandom);
		final ImageTextButton sizeSeven = new ImageTextButton("7", skin);
		bagTable.add(sizeSeven);
		final ImageTextButton sizeFourteen = new ImageTextButton("14", skin);
		bagTable.add(sizeFourteen);
		final ImageTextButton sizeTwentyOne = new ImageTextButton("21", skin);
		bagTable.add(sizeTwentyOne);
		table.add(bagTable);
		table.row();

		ButtonGroup bagButtons = new ButtonGroup();
		bagButtons.add(sizeRandom);
		bagButtons.add(sizeSeven);
		bagButtons.add(sizeFourteen);
		bagButtons.add(sizeTwentyOne);
		bagButtons.setMaxCheckCount(1);
		bagButtons.uncheckAll();
		sizeSeven.setChecked(true);

		TextButton startButton = new TextButton("Start", skin);
		table.add(startButton).padTop(PAD);
		table.debugTable();

		SavedStuff savedStuff = new SavedStuff();
		savedStuff.loadPreferences();
		soundOn = savedStuff.isSoundOn();
		musicOn = savedStuff.isMusicOn();
		upsideDown = savedStuff.isUpsideDown();

		soundOnBox.setChecked(soundOn);
		musicOnBox.setChecked(musicOn);
		upsideDownBox.setChecked(upsideDown);
		System.out.println("sound: " + soundOn + "  music: " + musicOn + "  upside: " + upsideDown);

		startButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				boolean sound;
				boolean music;
				boolean upside;
				int bag = 0;
				int level = 0;

				if (zero.isChecked()) {
					level = 0;
				} else if (one.isChecked()) {
					level = 1;
				} else if (two.isChecked()) {
					level = 2;
				} else if (three.isChecked()) {
					level = 3;
				} else if (four.isChecked()) {
					level = 4;
				} else if (five.isChecked()) {
					level = 5;
				} else if (six.isChecked()) {
					level = 6;
				} else if (seven.isChecked()) {
					level = 7;
				} else if (eight.isChecked()) {
					level = 8;
				} else if (nine.isChecked()) {
					level = 9;
				}

				if (sizeRandom.isChecked()) {
					bag = 0;
				} else if (sizeSeven.isChecked()) {
					bag = 1;
				} else if (sizeFourteen.isChecked()) {
					bag = 2;
				} else if (sizeTwentyOne.isChecked()) {
					bag = 3;
				}

				sound = soundOnBox.isChecked();
				music = musicOnBox.isChecked();
				upside = upsideDownBox.isChecked();

				SavedStuff stuff = new SavedStuff();
				stuff.saveAll(sound, music, upside, bag);

				dispose();
				game.setScreen(new GameScreen(game, level));
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
		assets.disposeSetupScreen();
	}
}
