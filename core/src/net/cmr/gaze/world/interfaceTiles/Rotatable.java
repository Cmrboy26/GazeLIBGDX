package net.cmr.gaze.world.interfaceTiles;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

public interface Rotatable {

	public static class Direction {
		public static final int DOWN = 0;
		public static final int LEFT = 1;
		public static final int UP = 2;
		public static final int RIGHT = 3;
	}

	public int getDirection(); // down = 0, left = 1, up = 2, right = 3
	public void setDirection(int v);
	public default int maxDirection() {
		return 3;
	};

	public default float getComponentX() {
		return getDirection() == 1 ? -1 : getDirection() == 3 ? 1 : 0;
	}
	public default float getComponentY() {
		return getDirection() == 0 ? -1 : getDirection() == 2 ? 1 : 0;
	}
	
	public default void writeRotatableData(DataBuffer buffer) throws IOException {
		buffer.writeInt(getDirection());
	}
	public static void readRotatableData(DataInputStream input, Rotatable tile) throws IOException {
		tile.setDirection(input.readInt());
	}
	
}
