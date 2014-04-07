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
	private boolean soundOn;
	private boolean musicOn;
	private boolean newRecord;
	private boolean upsideDown = false;

	public SavedStuff () {
	}

	public void loadPreferences () {
		prefs = Gdx.app.getPreferences(PREFERENCES_STRING);
		bagSize = prefs.getInteger(KEY_BAG_SIZE, 0);
		upsideDown = prefs.getBoolean(KEY_UPSIDE_DOWN, false);
		soundOn = prefs.getBoolean(KEY_SOUND_ON, true);
		musicOn = prefs.getBoolean(KEY_MUSIC_ON, true);
		// FIXME debug, remove later
		bagSize = 2;
		soundOn = false;
		upsideDown = false;
	}

	public void savePreferences () {
		prefs.flush();
	}

	public void loadScores () {
		setScoresFilename();

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
		} catch (Exception e) {
			Gdx.app.log("DEBUG", "Something terrible happened while trying to load the scores file " + scoresFilename);
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

	public void checkScoreToTable () {
		newRecord = false;
		for (int x = 0; x < SCORES_HEIGHT; x++) {
			if (previousScore > getScoreStringToInt(x)) {
				newRecord = true;
				scoreToReplace = x;
				return;
			}
		}
	}

	public void moveScoresDown (int index) {
		for (int x = SCORES_HEIGHT - 1; x > index; x--) {
			allTheScoresStrings[x][LEVEL] = allTheScoresStrings[x - 1][LEVEL];
			allTheScoresStrings[x][SCORE] = allTheScoresStrings[x - 1][SCORE];
		}
	}

	public int getScoreStringToInt (int index) {
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

	public String getHighScore () {
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

	public String getPreviousScore () {
		return String.valueOf(previousScore);
	}

	public int getBagSize () {
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

	public boolean isPreviousScoreInTopScore () {
		return newRecord;
	}
}
