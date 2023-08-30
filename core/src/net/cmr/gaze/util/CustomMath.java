package net.cmr.gaze.util;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class CustomMath {

	public static int minMax(int low, int value, int high) {
		return Math.max(Math.min(high, value), low);
	}

	public static double minMax(double low, double value, double high) {
		return Math.max(Math.min(high, value), low);
	}
	
	public static float minMax(float low, float value, float high) {
		return Math.max(Math.min(high, value), low);
	}
	
	public static Vector2 clampCircle(float x, float y, float radius, float threshold) {
		
		if(Math.hypot(x, y)<threshold) {
			return Vector2.Zero;
		}
		
		float angle = (MathUtils.atan2((float) x, (float) y)*MathUtils.radiansToDegrees)%360f;
		
		angle = Math.round(angle);
		
		float cx = MathUtils.sinDeg(angle);
		float cy = MathUtils.cosDeg(angle);
		
		return new Vector2(cx*radius, cy*radius);
	}
	
}
