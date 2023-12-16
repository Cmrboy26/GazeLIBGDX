package net.cmr.gaze.world.interfaceTiles;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

public interface Rotatable {

	public int getDirection(); // down = 0, left = 1, up = 2, right = 3
	public void setDirection(int v);
	public default int maxDirection() {
		return 3;
	};

	public default int getComponentX() {
		return getDirection() == 1 ? -1 : getDirection() == 3 ? 1 : 0;
	}
	public default int getComponentY() {
		return getDirection() == 0 ? -1 : getDirection() == 2 ? 1 : 0;
	}
	
	public default void writeRotatableData(DataBuffer buffer) throws IOException {
		buffer.writeInt(getDirection());
	}
	public static void readRotatableData(DataInputStream input, Rotatable tile) throws IOException {
		tile.setDirection(input.readInt());
	}
	
}
