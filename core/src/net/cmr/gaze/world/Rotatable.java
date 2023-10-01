package net.cmr.gaze.world;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

public interface Rotatable {

	public int getDirection(); // down = 0, left = 1, up = 2, right = 3
	public void setDirection(int v);
	public default int maxDirection() {
		return 3;
	};
	
	public default void writeRotatableData(DataBuffer buffer) throws IOException {
		buffer.writeInt(getDirection());
	}
	public static void readRotatableData(DataInputStream input, Rotatable tile) throws IOException {
		tile.setDirection(input.readInt());
	}
	
}
