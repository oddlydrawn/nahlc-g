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
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;

/** @author oddlydrawn */
public class SavedStuff {
	private final String SCORES_UPSIDE_DOWN = "scoresUpside.txt"; // "scoresUpside.txt"
	private final String SCORES_NORMAL = "scores.txt"; // "scores.txt"
	private final String DELIMITER_STRING = ","; // ","
	private final String PREFERENCES_STRING = "Preferences";
	private final String KEY_BAG_SIZE = "bagSize";
	private final String KEY_UPSIDE_DOWN = "upsideDown";
	private final String KEY_SOUND_ON = "soundOn";
	private final String KEY_MUSIC_ON = "soundOff";
	private final String KEY_LEVEL_SIZE = "levelSize";
	private final String KEY_LOAD_SAVED_GAME = "loadGame";
	private final String ERROR_TAG = "ERROR";
	private final int SCORES_HEIGHT = 10; // 10
	private final int SCORES_WIDTH = 2; // 2
	private final int LEVEL = 0; // 0
	private final int SCORE = 1; // 1
	private String[][] allTheScoresStrings = { {"9", "10000"}, {"8", "9000"}, {"7", "8000"}, {"6", "7000"}, {"5", "6000"},
		{"4", "5000"}, {"3", "4000"}, {"2", "3000"}, {"1", "2000"}, {"0", "1000"}};
	private String scoresFilename;
	private Preferences prefs;
	private int previousScore;
	private int scoreToReplace;
	private int bagSize;
	private int levelSize;
	private boolean soundOn;
	private boolean musicOn;
	private boolean newRecord;
	private boolean upsideDown;
	private boolean loadSavedGame;

	public SavedStuff () {
		prefs = Gdx.app.getPreferences(PREFERENCES_STRING);
	}

	public void loadPreferences () {
		bagSize = prefs.getInteger(KEY_BAG_SIZE, 0);
		upsideDown = prefs.getBoolean(KEY_UPSIDE_DOWN, false);
		soundOn = prefs.getBoolean(KEY_SOUND_ON, true);
		musicOn = prefs.getBoolean(KEY_MUSIC_ON, true);
		levelSize = prefs.getInteger(KEY_LEVEL_SIZE, 0);
		loadSavedGame = prefs.getBoolean(KEY_LOAD_SAVED_GAME, false);
		setScoresFilename();
	}

	public void savePreferences () {
		prefs.flush();
	}

	public void loadScores () {
		loadPreferences();
		try {
			String scoresString;
			FileHandle scoresHandle;
			if (Gdx.files.local(scoresFilename).exists()) {
				scoresHandle = Gdx.files.local(scoresFilename);
				scoresString = scoresHandle.readString();
				String scoresArray[] = scoresString.split(DELIMITER_STRING);
				int counter = 0;
				for (int x = 0; x < SCORES_HEIGHT; x++) {
					for (int y = 0; y < SCORES_WIDTH; y++) {
						allTheScoresStrings[x][y] = scoresArray[counter];
						counter++;
					}
				}
			}
		} catch (RuntimeException ex) {
			Gdx.app.log(ERROR_TAG, ex.getMessage());
			ex.printStackTrace();
		} catch (Exception e) {
			Gdx.app.log(ERROR_TAG, "Something terrible happened while trying to load the scores file " + scoresFilename);
			Gdx.app.log(ERROR_TAG, e.getMessage());
			e.printStackTrace();
		}
	}

	public void updateLevelAndScore (int level, int score) {
		newRecord = false;
		previousScore = score;
		for (int x = 0; x < SCORES_HEIGHT; x++) {
			if (score > getScoreStringToInt(x)) {
				newRecord = true;

				moveScoresDown(x);
				allTheScoresStrings[x][LEVEL] = String.valueOf(level);
				allTheScoresStrings[x][SCORE] = String.valueOf(score);
				return;
			}
		}

	}

	public void saveScoresToFile () {
		loadPreferences();
		String scoresString = "";
		FileHandle scoresHandle = Gdx.files.local(scoresFilename);
		for (int x = 0; x < SCORES_HEIGHT; x++) {
			for (int y = 0; y < SCORES_WIDTH; y++) {
				scoresString += allTheScoresStrings[x][y];
				scoresString += ",";
			}
		}
		scoresHandle.writeString(scoresString, false);
	}

	private void checkScoreToTable () {
		newRecord = false;
		for (int x = 0; x < SCORES_HEIGHT; x++) {
			if (previousScore > getScoreStringToInt(x)) {
				newRecord = true;
				scoreToReplace = x;
				return;
			}
		}
	}

	private void moveScoresDown (int index) {
		for (int x = SCORES_HEIGHT - 1; x > index; x--) {
			allTheScoresStrings[x][LEVEL] = allTheScoresStrings[x - 1][LEVEL];
			allTheScoresStrings[x][SCORE] = allTheScoresStrings[x - 1][SCORE];
		}
	}

	private int getScoreStringToInt (int index) {
		return Integer.valueOf(allTheScoresStrings[index][SCORE]);
	}

	private void setScoresFilename () {
		if (isUpsideDown()) {
			scoresFilename = SCORES_UPSIDE_DOWN;
		} else {
			scoresFilename = SCORES_NORMAL;
		}
	}

	public void setScore (int score) {
		previousScore = score;
		checkScoreToTable();
	}

	String getHighScore () {
		return allTheScoresStrings[0][SCORE];
	}

	public String getLevel (int x, int y) {
		return allTheScoresStrings[x][y];
	}

	public String getScore (int x, int y) {
		return allTheScoresStrings[x][y];
	}

	public int getScoreToReplace () {
		return scoreToReplace;
	}

	int getBagSize () {
		return bagSize;
	}

	public boolean isSoundOn () {
		return soundOn;
	}

	public boolean isMusicOn () {
		return musicOn;
	}

	public boolean isUpsideDown () {
		return upsideDown;
	}

	public void saveAll (Boolean sound, Boolean music, Boolean upside, int bag, int level) {
		prefs = Gdx.app.getPreferences(PREFERENCES_STRING);
		prefs.putInteger(KEY_BAG_SIZE, bag);
		prefs.putBoolean(KEY_SOUND_ON, sound);
		prefs.putBoolean(KEY_MUSIC_ON, music);
		prefs.putBoolean(KEY_UPSIDE_DOWN, upside);
		prefs.putInteger(KEY_LEVEL_SIZE, level);
		prefs.putBoolean(KEY_LOAD_SAVED_GAME, false);
		savePreferences();
	}

	public int getLevelSize () {
		return levelSize;
	}

	public boolean isPreviousScoreInTopScore () {
		return newRecord;
	}

	public boolean savedGameExists () {
		return loadSavedGame;
	}

	public void setSavedGameExists (boolean loadSavedGame) {
		prefs = Gdx.app.getPreferences(PREFERENCES_STRING);
		prefs.putBoolean(KEY_LOAD_SAVED_GAME, loadSavedGame);
	}
}
