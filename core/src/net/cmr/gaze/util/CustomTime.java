package net.cmr.gaze.util;

public class CustomTime {

	public static double timeToSeconds(long nanoTime) {
		double milliseconds = (double) nanoTime / 1e9;
	    return milliseconds;
	}
	
}
