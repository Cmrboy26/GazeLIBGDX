package net.cmr.gaze;

import com.badlogic.gdx.Gdx;

public class Logger {
	
	public static void log(String tag, String message) {
		if(Gdx.app != null) {
			Gdx.app.log(tag, message);
		} else {
			System.out.println("["+tag+"] "+message);
		}
	}
	public static void error(String tag, String message) {
		if(Gdx.app != null) {
			Gdx.app.error(tag, message);
		} else {
			System.out.println("["+tag+"] "+message);
		}
	}
	
}
