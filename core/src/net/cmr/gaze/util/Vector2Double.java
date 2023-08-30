package net.cmr.gaze.util;

import java.awt.Point;
import java.io.Serializable;


public class Vector2Double implements Serializable {
	
	private static final long serialVersionUID = -6885214421266275116L;
	private double x, y;
	
	public Vector2Double() {
		
	}
	/*
	public Vector2Double(Direction d) {
		this.x = d.getX();
		this.y = d.getY();
	}
	
	public Vector2Double(Direction d, double scale) {
		this.x = d.getX()*scale;
		this.y = d.getY()*scale;
	}*/
	
	public Vector2Double(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2Double(Vector2Double copy) {
		if(copy != null) {
			this.x = copy.getX();
			this.y = copy.getY();
		}
	}
	
	public Vector2Double(Point location) {
		if(location != null) {
			this.x = location.getX();
			this.y = location.getY();
		}
	}

	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	/*public Vector2Double resize() {
		this.x = Resize.AccurateResizeX(x);
		this.y = Resize.AccurateResizeY(y);
		return this;
	}*/
	
	public Vector2Double scale(double scale) {
		this.x *= scale;
		this.y *= scale;
		return this;
	}
	
	public Vector2Double multiply(Vector2Double in) {
		this.x *= in.getX();
		this.y *= in.getY();
		return this;
	}
	
	public Vector2Double add(Vector2Double in) {
		return translate(in);
	}
	
	public Vector2Double subtract(Vector2Double in) {
		return translate(new Vector2Double(in).scale(-1));
	}
	
	public Vector2Double translate(double x, double y) {
		this.x += x;
		this.y += y;
		return this;
	}
	
	public Vector2Double translate(Vector2Double in) {
		this.x += in.getX();
		this.y += in.getY();
		return this;
	}
	
	public void set(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void set(Vector2Double in) {
		this.x = in.getX();
		this.y = in.getY();
	}
	
	/*public void normalize() {
		this.x = Normalize.norm(x);
		this.y = Normalize.norm(y);
	}*/
	
	public double dotProduct(Vector2Double other) {
	    return x * other.x + y * other.y;
	}
	
	public double distance(Vector2Double other) {
		return Math.hypot(x-other.x, y-other.y);
	}
	
	public String toString() {
		return "<"+x+", "+y+">";
	}

	public Point toPoint() {
		return new Point((int) x, (int) y);
	}
	
	public Number manhattan(Vector2Double other) {
		return (Math.abs(getY()-other.getY())+Math.abs(getX()-other.getX()));
	}
	public Number chebyshev(Vector2Double other) {
		return Math.max(Math.abs(getY()-other.getY()), Math.abs(getX()-other.getX()));
	}

	public void reset() {
		this.x = 0;
		this.y = 0;
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof Vector2Double) {
			Vector2Double compare = (Vector2Double) obj;
			return compare.getX() == getX() && compare.getY() == getY();
		}
		return false;
	}
	
	
}
