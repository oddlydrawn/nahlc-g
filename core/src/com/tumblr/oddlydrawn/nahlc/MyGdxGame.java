
package com.tumblr.oddlydrawn.nahlc;

import com.badlogic.gdx.Game;
import com.tumblr.oddlydrawn.nahlc.screens.GameScreen;

public class MyGdxGame extends Game {

	@Override
	public void create () {
		setScreen(new GameScreen(this));
	}
}
