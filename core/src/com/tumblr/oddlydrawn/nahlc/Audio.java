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
	Sound soundCollides;
	Sound soundLaserish;

	public Audio () {
		soundCollides = Gdx.audio.newSound(Gdx.files.internal("data/sound/wallCollision2.wav"));
		soundLaserish = Gdx.audio.newSound(Gdx.files.internal("data/sound/laserish.wav"));
	}

	public void playCollides () {
		soundCollides.play(1.0f);
	}

	public void playLaserish () {
		soundLaserish.play(1.0f);
	}

	public void dispose () {
		soundCollides.dispose();
		soundLaserish.dispose();
	}
}
