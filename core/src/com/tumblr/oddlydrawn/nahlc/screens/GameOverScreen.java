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
	private Assets assets;

	public GameOverScreen (Game g, int level, int score) {
		game = g;
		savedStuff = new SavedStuff();
		assets = new Assets();
		assets.initGameOver();
		stage = new Stage(new StretchViewport(Renderer.WIDTH, Renderer.HEIGHT));
		skin = new Skin();

		savedStuff.loadScores();
		savedStuff.setScore(score);

		skin.add("default", new BitmapFont(Gdx.files.internal("data/fonts/deja.fnt")));

		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = skin.getFont("default");
		skin.add("default", labelStyle);

		ButtonStyle buttonStyle = new ButtonStyle();
		skin.add("default", buttonStyle);

		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.font = skin.getFont("default");
		skin.add("default", textButtonStyle);

		// Background NinePatch image
		Image boxPatchImage = new Image(assets.getBoxPatch());
		boxPatchImage.setSize(Renderer.WIDTH - 40, Renderer.HEIGHT - 5);
		boxPatchImage.setPosition(20, 2);
		stage.addActor(boxPatchImage);

		// Top Score's background NinePatch image
		Image boxPatchImageTwo = new Image(assets.getBoxPatch());
		boxPatchImageTwo.setSize(220, 295);
		boxPatchImageTwo.setPosition(50, 90);
		stage.addActor(boxPatchImageTwo);

		// Single-column table that holds all the things.
		rootTable = new Table();
		rootTable.setFillParent(true);
		stage.addActor(rootTable);
		rootTable.debug();

		// Three-column table that holds the top scores.
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

		Image titleImage;
		if (savedStuff.isPreviousScoreInTopScore()) {
			titleImage = newRecordImage;
		} else {
			titleImage = gameOverImage;
		}

		Label[][] allTheScores = new Label[3][11];

		// Creates the labels for all the scores, populates them, and adds animations.
		for (int y = 0; y < 10; y++) {
			for (int x = 0; x < 3; x++) {
				switch (x) {
				case 0: // The labels from 1 - 10, left most to show score rank.
					if (y == 9) {
						allTheScores[x][y] = new Label(String.valueOf(y + 1) + ".", skin);
					} else {
						// Adds a space, for correct padding to numbers 1-9 (or the array indexes 0-8)
						allTheScores[x][y] = new Label(" " + String.valueOf(y + 1) + ".", skin);
					}
					topScoreTable.add(allTheScores[x][y]);
					break;
				case 1: // The labels with the Level associated with each score
					allTheScores[x][y] = new Label(savedStuff.getLevel(y, x - 1), skin);
					topScoreTable.add(allTheScores[x][y]);

					if (savedStuff.isPreviousScoreInTopScore()) {
						// Adds the drop animation to scores below the score to replace
						if (y >= savedStuff.getScoreToReplace()) {
							allTheScores[x][y].addAction(Actions.sequence(Actions.delay(2f), Actions.delay(0.5f),
								Actions.moveBy(0, -26, 2, Interpolation.bounceOut)));
						}
					}
					break;
				case 2: // The labels with the Score associated with each rank and level
					allTheScores[x][y] = new Label(savedStuff.getScore(y, x - 1), skin);
					topScoreTable.add(allTheScores[x][y]).right();

					if (savedStuff.isPreviousScoreInTopScore()) {
						// Adds the drop animation to scores below the score to replace
						if (y >= savedStuff.getScoreToReplace()) {
							allTheScores[x][y].addAction(Actions.sequence(Actions.delay(2f), Actions.delay(0.5f),
								Actions.moveBy(0, -26, 2, Interpolation.bounceOut)));
						}
					}
					break;
				default:
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

		// Adds padding to Level and 10th rank strings for correct padding
		String levelButtonString;
		int mew = Integer.valueOf(String.valueOf(allTheScores[1][9].getText()));
		if (mew < 10) {
			levelButtonString = String.valueOf(allTheScores[1][9].getText()) + " ";
		} else {
			levelButtonString = String.valueOf(allTheScores[1][9].getText());
		}
		String previousLevelButtonString;
		if (level < 10) {
			previousLevelButtonString = String.valueOf(level + " ");
		} else {
			previousLevelButtonString = String.valueOf(level);
		}
		String previousScoreString = String.valueOf(score);

		TextButton previousLevelButton = new TextButton(previousLevelButtonString, skin);
		TextButton previousScoreButton = new TextButton(previousScoreString, skin);
		TextButton tenthLevelButton = new TextButton(levelButtonString, skin);
		TextButton tenthScoreButton = new TextButton(String.valueOf(allTheScores[2][9].getText()), skin);

		// Pads Button text with spaces for correct alignment
		tenthLevelButton.setText(fillStringWithSpaces(String.valueOf(tenthLevelButton.getText())));
		previousLevelButton.setText(fillStringWithSpaces(previousLevelButtonString));
		stage.addActor(tenthLevelButton);
		stage.addActor(tenthScoreButton);

		// Groups to link the scores and levels for 10th-rank scores and the score just obtained
		// Needed to apply transforms to buttons since transforming text isn't possible, I think.
		Group group = new Group();
		group.addActor(tenthScoreButton);
		group.addActor(tenthLevelButton);

		Group groupPrevious = new Group();
		groupPrevious.addActor(previousLevelButton);
		groupPrevious.addActor(previousScoreButton);

		// Modifies score buttons positions for correct alignment with the rest of the table
		tenthScoreButton.setPosition(144 - subPosFromLength(tenthScoreButton.getText().length()), 00);
		previousScoreButton.setPosition(144 - subPosFromLength(previousScoreString.length()), 00);
		tenthScoreButton.align(Align.left);
		previousScoreButton.align(Align.left);

		tenthLevelButton.setTransform(true);
		tenthScoreButton.setTransform(true);

		stage.addActor(group);
		group.setPosition(105, 97);

		allTheScores[1][9].setVisible(false);
		allTheScores[2][9].setVisible(false);

		group.setTransform(true);

		// Adds animation for drop and bounce then rotate and drop to 10th rank score
		if (savedStuff.isPreviousScoreInTopScore()) {
			group.setOrigin(group.getX() + group.getWidth() + 50, 10);
			group.addAction(Actions.sequence(Actions.delay(2f), Actions.delay(0.5f),
				Actions.moveBy(0, -26, 2, Interpolation.bounceOut), Actions.delay(0.1f),
				Actions.rotateBy(90f, 2f, Interpolation.bounceOut), Actions.delay(0.1f),
				Actions.moveBy(0, -120, 2, Interpolation.bounceOut)));
		}

		// Adds spaces to first score in top score table to have the same column width regardless of score size
		allTheScores[2][0].setText(fillCSWithSpaces(allTheScores[2][0].getText()));

		mainMenuButton.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				dispose();
				game.setScreen(new MainMenuScreen(game));
			}
		});

		newGameButton.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				dispose();
				game.setScreen(new GameScreen(game));
			}
		});

		// Sets correct vertical position for score just obtained
		groupPrevious.setPosition(group.getX(), group.getY() + getVerticalPosSub(savedStuff.getScoreToReplace()));
		groupPrevious.setTransform(true);

		// Score is now in position, this makes it invisible
		groupPrevious.addAction(Actions.fadeOut(0.1f));

		// Fades the score in after the bottom-most score rotates and drops
		if (savedStuff.isPreviousScoreInTopScore()) {
			groupPrevious.addAction(Actions.sequence(Actions.delay(7.5f), Actions.fadeIn(1f)));
		}
		stage.addActor(groupPrevious);

		savedStuff.setSavedGameExists(false);
		savedStuff.savePreferences();

		savedStuff.updateLevelAndScore(level, score);
		try {
			savedStuff.saveScoresToFile();
		} catch (RuntimeException ex) {
			Gdx.app.log("ERROR NAHLC", ex.getMessage());
			ex.printStackTrace();
		} catch (Exception e) {
			Gdx.app.log("ERROR NAHLC", e.getMessage());
			e.printStackTrace();
		}
		Gdx.app.log("NAHLC", "leaving create()");
	}

	private float subPosFromLength (int length) {
		return 11 * (length - 1);
	}

	private float getVerticalPosSub (int level) {
		int diff = 9 - level;
		return diff * 26;
	}

	public String fillStringWithSpaces (String s) {
		int optimum = 7;
		int missing = optimum - s.length();
		for (int i = 0; i < missing; i++) {
			s = " " + s;
		}
		return s;
	}

	public CharSequence fillCSWithSpaces (CharSequence charSequence) {
		int optimum = 7;
		int missing = optimum - charSequence.length();
		for (int i = 0; i < missing; i++) {
			charSequence = " " + charSequence;
		}
		return charSequence;
	}

	@Override
	public void render (float delta) {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		try {
			stage.act(delta);
		} catch (RuntimeException ex) {
			Gdx.app.log("NAHLC", "RUNTIME EXCEPTION while acting on stage");
			Gdx.app.log("ERROR NAHLC", ex.getMessage());
			ex.printStackTrace();
		} catch (Exception e) {
			Gdx.app.log("NAHLC", "EXCEPTION while acting on stage");
			Gdx.app.log("ERROR NAHLC", e.getMessage());
			e.printStackTrace();
		}

		try {
			stage.draw();
		} catch (RuntimeException ex) {
			Gdx.app.log("NAHLC", "RUNTIME EXCEPTION while drawing stage");
			Gdx.app.log("ERROR NAHLC", ex.getMessage());
			ex.printStackTrace();
		} catch (Exception e) {
			Gdx.app.log("NAHLC", "EXCEPTION while drawing stage");
			Gdx.app.log("ERROR NAHLC", e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void resize (int width, int height) {
		stage.getViewport().update(width, height, true);
		Gdx.app.log("NAHLC", "leaving resize()");
	}

	@Override
	public void show () {
		Gdx.input.setInputProcessor(stage);
		Gdx.app.log("NAHLC", "leaving show");
	}

	@Override
	public void hide () {
		Gdx.app.log("NAHLC", "leaving hide()");
	}

	@Override
	public void pause () {
		Gdx.app.log("NAHLC", "leaving pause()");
	}

	@Override
	public void resume () {
		Gdx.app.log("NAHLC", "leaving resume()");
	}

	@Override
	public void dispose () {
		stage.dispose();
		skin.dispose();
		assets.disposeGameOver();
		Gdx.app.log("NAHLC", "leaving dispose()");
	}

	public void log (String s) {
		System.out.println(s);
	}
}
