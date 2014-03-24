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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.tumblr.oddlydrawn.nahlc.Assets;
import com.tumblr.oddlydrawn.nahlc.Renderer;
import com.tumblr.oddlydrawn.nahlc.SavedStuff;

/** @author oddlydrawn */
public class GameOverScreen implements Screen {
	private Game game;
	private Stage stage;
	private Table rootTable;
	private Table topScoreTable;
	private Skin skin;
	private SavedStuff savedStuff;
	private int tmpX;
	private int tmpY;
	Assets assets;
	Group groupTen;

	public GameOverScreen (Game g, int level, int score) {
		game = g;
		savedStuff = new SavedStuff();
		assets = new Assets();
		assets.initGameOver();
		stage = new Stage(new StretchViewport(Renderer.WIDTH, Renderer.HEIGHT));
		Gdx.input.setInputProcessor(stage);
		skin = new Skin();

		savedStuff.setLevel(level);
		savedStuff.setScore(score);

		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(Color.LIGHT_GRAY);
		pixmap.fill();

		// The following defines the defaults for Scene2D's skin
		skin.add("grey", new Texture(pixmap));
		skin.add("default", new BitmapFont(Gdx.files.internal("data/fonts/deja.fnt")));

		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = skin.getFont("default");
		skin.add("default", labelStyle);

		ButtonStyle buttonStyle = new ButtonStyle();
		skin.add("default", buttonStyle);

		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.font = skin.getFont("default");
		skin.add("default", textButtonStyle);

		Image boxPatchImage = new Image(assets.getBoxPatch());
		boxPatchImage.setSize(Renderer.WIDTH - 40, Renderer.HEIGHT - 5);
		boxPatchImage.setPosition(20, 2);
		stage.addActor(boxPatchImage);

		Image boxPatchImageTwo = new Image(assets.getBoxPatch());
		boxPatchImageTwo.setSize(220, 295);
		boxPatchImageTwo.setPosition(50, 90);
		stage.addActor(boxPatchImageTwo);

		rootTable = new Table();
		rootTable.setFillParent(true);
		stage.addActor(rootTable);
		rootTable.debug();

		topScoreTable = new Table();
		topScoreTable.debug();
		topScoreTable.columnDefaults(0).width(73);
		topScoreTable.columnDefaults(1).width(50);
		topScoreTable.columnDefaults(1).align(Align.center);

		Label scoreLabel = new Label("Score: " + score, skin);
		Label topScoresLabel = new Label("Top Scores", skin);

		Image mainMenuImage = new Image(assets.getMainMenuSprite());
		Image newGameImage = new Image(assets.getNewGameSprite());

		Button mainMenuButton = new Button(skin);
		mainMenuButton.add(mainMenuImage);

		Button newGameButton = new Button(skin);
		newGameButton.add(newGameImage);

		Image newRecordImage = new Image(assets.getNewRecordSprite());
		Image gameOverImage = new Image(assets.getGameOverSprite());

		log("height" + assets.getNewRecordSprite().getHeight());
		Image titleImage;
		if (savedStuff.isPreviousScoreInTopScore()) {
			titleImage = newRecordImage;
		} else {
			titleImage = gameOverImage;
		}

		Label[][] allTheScores = new Label[3][11];

		for (int y = 0; y < 10; y++) {
			for (int x = 0; x < 3; x++) {
				switch (x) {
				case 0:
					if (y == 9) {
						allTheScores[x][y] = new Label(String.valueOf(y + 1) + ".", skin);
					} else {
						allTheScores[x][y] = new Label(" " + String.valueOf(y + 1) + ".", skin);
					}
					topScoreTable.add(allTheScores[x][y]);
					break;
				case 1:
					System.out.println("case 1: " + x + " and " + y);
					allTheScores[x][y] = new Label(savedStuff.getLevel(y, x - 1), skin);
					topScoreTable.add(allTheScores[x][y]);

					if (savedStuff.isPreviousScoreInTopScore()) {
						if (y >= savedStuff.getScoreToReplace()) {
							if (y == 9) {
// allTheScores[x][y].addAction(Actions.sequence(Actions.delay(2f), Actions.delay(0.5f),
// Actions.moveBy(0, -26, 2, Interpolation.bounceOut), Actions.delay(2f),
// Actions.rotateBy(90f, 2f, Interpolation.bounceOut), Actions.delay(0.5f),
// Actions.moveBy(0, -500, 0.5f, Interpolation.bounceOut)));
								allTheScores[x][y].addAction(Actions.sequence(Actions.delay(2f), Actions.delay(0.5f),
									Actions.moveBy(0, -26, 2, Interpolation.bounceOut),
									Actions.moveBy(0, -500, 2, Interpolation.bounceOut)));
							} else {
								allTheScores[x][y].addAction(Actions.sequence(Actions.delay(2f), Actions.delay(0.5f),
									Actions.moveBy(0, -26, 2, Interpolation.bounceOut)));
							}
						}

					}

					break;
				case 2:
					System.out.println("case 2: " + x + " and " + y);
					allTheScores[x][y] = new Label(savedStuff.getScore(y, x - 1), skin);
					topScoreTable.add(allTheScores[x][y]).right();

					if (savedStuff.isPreviousScoreInTopScore()) {
						if (y >= savedStuff.getScoreToReplace()) {
							if (y == 9) {
// allTheScores[x][y].addAction(Actions.sequence(Actions.delay(2f), Actions.delay(0.5f),
// Actions.moveBy(0, -26, 2, Interpolation.bounceOut), Actions.delay(2f),
// Actions.rotateBy(90f, 2f, Interpolation.bounceOut), Actions.delay(0.5f),
// Actions.moveBy(0, -500, 0.5f, Interpolation.bounceOut)));
								allTheScores[x][y].addAction(Actions.sequence(Actions.delay(2f), Actions.delay(0.5f),
									Actions.moveBy(0, -26, 2, Interpolation.bounceOut),
									Actions.moveBy(0, -500, 2, Interpolation.bounceOut)));
							} else {
								allTheScores[x][y].addAction(Actions.sequence(Actions.delay(2f), Actions.delay(0.5f),
									Actions.moveBy(0, -26, 2, Interpolation.bounceOut)));
							}
						}
					}
					break;
				default:
					System.out.println("He's dead, jim");
					break;
				}
			}
			topScoreTable.row();
		}

		rootTable.add(titleImage);

		rootTable.row();
		rootTable.add(scoreLabel).expand();

		rootTable.row();

		rootTable.add(topScoresLabel);
		rootTable.row();
		topScoreTable.setWidth(rootTable.getWidth());
		topScoreTable.setTransform(true);
		rootTable.setTransform(true);
		rootTable.add(topScoreTable);
		rootTable.row();

		rootTable.add(newGameButton).expand().bottom();
		rootTable.row();
		rootTable.add(mainMenuButton).expand();

		rootTable.padLeft(35f);
		rootTable.padRight(35f);
		rootTable.padTop(9f);
		rootTable.padBottom(9f);

		newGameButton.toBack();
		mainMenuButton.toBack();

		TextButton previousLevelButton = new TextButton(String.valueOf(level), skin);
		TextButton previousScoreButton = new TextButton(String.valueOf(score), skin);

		stage.addActor(previousLevelButton);
		stage.addActor(previousScoreButton);

		Group group = new Group();
		group.addActor(previousScoreButton);
		group.addActor(previousLevelButton);
		group.setOrigin(110, 6);

// previousLevelButton.setPosition(20, 30);
// // previousLevelButton.setOrigin(55, 30);
		previousScoreButton.setPosition(100, 00);
		previousScoreButton.align(Align.left);
// previousScoreButton.setOrigin(55, 30);

// previousLevelButton.addAction(Actions.rotateBy(90, 3f, Interpolation.bounceOut));
// previousScoreButton.addAction(Actions.rotateBy(90, 3f, Interpolation.bounceOut));
		previousLevelButton.setTransform(true);
		previousScoreButton.setTransform(true);

		stage.addActor(group);
		group.setPosition(130, 97);

		group.setTransform(true);
// group.addAction(Actions.sequence(Actions.delay(5f), Actions.rotateBy(90, 3f, Interpolation.bounceOut)));
		log("testtingggsdf " + topScoreTable.getCell(allTheScores[1][0]).getWidgetX() + ", "
			+ topScoreTable.getCell(allTheScores[2][0]).getPadLeft());

// allTheScores[1][0].addAction(Actions.sequence(Actions.delay(0.5f), Actions.delay(0.5f),
// Actions.moveBy(0, -26, 2, Interpolation.bounceOut)));
		mainMenuButton.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				log("mainMenu changed");

// game.setScreen(new MainMenuScreen(game));

			}
		});

		newGameButton.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				log("newgame changed");
				dispose();
				game.setScreen(new GameScreen(game));
			}
		});

	}

	@Override
	public void render (float delta) {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(delta);
		stage.draw();
		rootTable.drawDebug(stage);
		topScoreTable.drawDebug(stage);
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
		log("pause() called");
	}

	@Override
	public void resume () {
	}

	@Override
	public void dispose () {
		log("dispose() called");
		stage.dispose();
		skin.dispose();
		assets.disposeGameOver();
	}

	public void log (String s) {
		System.out.println(s);
	}
}
