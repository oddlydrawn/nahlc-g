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
import com.badlogic.gdx.files.FileHandle;

/** @author oddlydrawn */
public class SavedStuff {
	private final int LEVEL = 0;
	private final int SCORE = 1;
	private String[][] allTheScoresStrings = { {"20", "100"}, {"19", "90"}, {"17", "80"}, {"18", "70"}, {"16", "60"},
		{"15", "50"}, {"14", "40"}, {"13", "30"}, {"15", "25"}, {"15", "20"}};
	private int previousScore;
	private int previousLevel;
	private int scoreToReplace;
	private boolean newRecord;

	public SavedStuff () {
		try {
			String scoresString;
			FileHandle scoresHandle;
			if (Gdx.files.local("scores.txt").exists()) {
				scoresHandle = Gdx.files.local("scores.txt");
				scoresString = scoresHandle.readString();
				String scoresArray[] = scoresString.split(",");
				int counter = 0;
				for (int x = 0; x < 10; x++) {
					for (int y = 0; y < 2; y++) {
						allTheScoresStrings[x][y] = scoresArray[counter];
						counter++;
					}
				}
			}
		} catch (Exception e) {
		}
	}

	public String getHighScore () {
		return allTheScoresStrings[0][SCORE];
	}

	public void setLevel (int level) {
		previousLevel = level;
	}

	public void setScore (int score) {
		previousScore = score;
		checkScoreToTable();
	}

	public void saveLevelAndScore (int level, int score) {
		newRecord = false;
		previousScore = score;
		for (int x = 0; x < 10; x++) {
			if (score > scoreStringToInt(x)) {
				newRecord = true;

				moveScoresDown(x);
				allTheScoresStrings[x][LEVEL] = String.valueOf(level);
				allTheScoresStrings[x][SCORE] = String.valueOf(score);
				System.out.println("saveLevelAndScore x=" + x + " allScores[][]" + allTheScoresStrings[x][LEVEL] + " "
					+ allTheScoresStrings[x][SCORE]);
				return;
			}
		}

	}

	public void saveToFile () {
		String scoresString = "";
		FileHandle scoresHandle = Gdx.files.local("scores.txt");
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 2; y++) {
				scoresString += allTheScoresStrings[x][y];
				scoresString += ",";
			}
		}
		scoresHandle.writeString(scoresString, false);
	}

	public void checkScoreToTable () {
		newRecord = false;
		for (int x = 0; x < 10; x++) {
			if (previousScore > scoreStringToInt(x)) {
				newRecord = true;
				scoreToReplace = x;
				return;
			}
		}
	}

	public int scoreStringToInt (int index) {
		return Integer.valueOf(allTheScoresStrings[index][SCORE]);
	}

	public void moveScoresDown (int index) {
		for (int x = 9; x > index; x--) {
			allTheScoresStrings[x][LEVEL] = allTheScoresStrings[x - 1][LEVEL];
			allTheScoresStrings[x][SCORE] = allTheScoresStrings[x - 1][SCORE];
		}
	}

	public String getLevel (int x, int y) {
		return allTheScoresStrings[x][y];
	}

	public String getScore (int x, int y) {
		return allTheScoresStrings[x][y];
	}

	public boolean isPreviousScoreInTopScore () {
		return newRecord;
	}

	public int getScoreToReplace () {
		return scoreToReplace;
	}

	public String getPreviousScore () {
		return String.valueOf(previousScore);
	}
}
