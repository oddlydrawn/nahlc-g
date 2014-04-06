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
import com.badlogic.gdx.audio.Sound;

/** @author oddlydrawn */
public class Audio {
	private final String COLLIDES_PATH = "data/sound/wallCollision2.wav";
	private final String LASERISH_PATH = "data/sound/laserish.wav";
	private final String HURT_PATH = "data/sound/hurt.wav";
	private final float LOUD_VOLUME = 1.0f;
	private final float SOFTER_VOLUME = 0.5f;
	private Sound soundCollides;
	private Sound soundLaserish;
	private Sound soundHurt;
	private boolean soundOn;
	private boolean musicOn;

	public Audio () {
		soundCollides = Gdx.audio.newSound(Gdx.files.internal(COLLIDES_PATH));
		soundLaserish = Gdx.audio.newSound(Gdx.files.internal(LASERISH_PATH));
		soundHurt = Gdx.audio.newSound(Gdx.files.internal(HURT_PATH));
	}

	public void playCollides () {
		if (soundOn) soundCollides.play(LOUD_VOLUME);
	}

	public void playLaserish () {
		if (soundOn) soundLaserish.play(SOFTER_VOLUME);
	}

	public void playHurt () {
		if (soundOn) soundHurt.play(SOFTER_VOLUME);
	}

	public void dispose () {
		soundCollides.dispose();
		soundLaserish.dispose();
		soundHurt.dispose();
	}

	public void setSoundTo (boolean soundOn) {
		this.soundOn = soundOn;
	}

	public void setMusicTo (boolean musicOn) {
		this.musicOn = musicOn;
	}
}
