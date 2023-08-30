package net.cmr.gaze.world.pathfind;

import java.util.Objects;

public class AStarNode {

	public int x;
	public int y;
	public AStarNode parent;
	
	public AStarNode(int x, int y, AStarNode parent) {
		this.x = x;
		this.y = y;
		this.parent = parent;
	}
	
	@Override
    public boolean equals(Object o) {
		AStarNode point = (AStarNode) o;
        return x == point.x && y == point.y;
    }
	
	public AStarNode offset(int ox, int oy) {
		return new AStarNode(x + ox, y + oy, this);
	}
	
	@Override
    public String toString() { return String.format("(%d, %d)", x, y); }
	@Override
    public int hashCode() { return Objects.hash(x, y); }
	
}
