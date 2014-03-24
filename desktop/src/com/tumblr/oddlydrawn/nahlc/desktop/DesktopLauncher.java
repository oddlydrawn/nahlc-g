
package com.tumblr.oddlydrawn.nahlc.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.tumblr.oddlydrawn.nahlc.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "NAHLC";
		config.width = 320;
		config.height = 480;
		new LwjglApplication(new MyGdxGame(), config);
	}
}
