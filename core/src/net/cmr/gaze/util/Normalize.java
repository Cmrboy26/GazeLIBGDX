package net.cmr.gaze.util;

import java.text.DecimalFormat;
import java.util.stream.Stream;

public class Normalize {

	/**
	 * Converts the number into a value of 1, -1, or 0
	 * @param x The number to normalize
	 * @return
	 */
	public static double norm(double x) {
		return Math.signum(x);
		/*try {return Math.abs(x)/x;
		} catch(Exception e) {return 0;}*/
	}
	
	/**
	 * Clamps the corresponding value to a circle
	 * @param x Value between -1 and 1
	 * @param y Value between -1 and 1
	 * @return
	 */
	/*public static Vector2 equidistant(double x, double y) {
		
		// TODO if the values are inside of the circle, do nothing
		
		Vector2 p = new Vector2();
		
		double m = y/x;
		double xint = Math.sqrt(1/(m*m+1))*(Math.abs(x)/x);
		
		p.set(xint, m*xint);
		
		if(Double.isNaN(xint)) {
			p.setX(x);
			p.setY(Math.abs(y)/y);
		}
		if(Double.isNaN(p.getY())) {
			if(y == 0) {
				p.setY(0);
			}
		}
		
		Circle c = new Circle(0, 0, 1);
		if(c.includes((float) p.getX(), (float) p.getY())) {
			return new Vector2(x, y);
		}
		
		return p;
	}*/
	
	/*public static Vector2 equidistant(Vector2 vector) {
		return equidistant(vector.getX(), vector.getY());
	}*/

	public static double invert(double d) {
		return ((d-0.5d)*-1)+0.5d;
	}
	
	public static String truncateDouble(double input, int tensPlaces) {
		String tensCounter = "#";
		for(int i = 0; i < tensPlaces-1; i++) {
			tensCounter+="#";
		}
		return new DecimalFormat("#."+tensCounter).format(input);
	}
	
}
