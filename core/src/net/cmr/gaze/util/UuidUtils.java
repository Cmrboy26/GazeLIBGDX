package net.cmr.gaze.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

import com.badlogic.gdx.utils.DataBuffer;

public class UuidUtils {
	public static UUID asUuid(byte[] bytes) {
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		long firstLong = bb.getLong();
		long secondLong = bb.getLong();
		return new UUID(firstLong, secondLong);
	}

	public static byte[] asBytes(UUID uuid) {
		ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		bb.putLong(uuid.getMostSignificantBits());
		bb.putLong(uuid.getLeastSignificantBits());
		return bb.array();
	}
	
	public static void writeUUID(DataBuffer buffer, UUID id) throws IOException {
		
		if(id == null) {
			buffer.writeInt(-1);
			return;
		}
		
		byte[] bytes = asBytes(id);
		buffer.writeInt(bytes.length);
		buffer.write(bytes);
	}
	public static UUID readUUID(DataInputStream input) throws IOException {
		int length = input.readInt();
		if(length == -1) {
			return null;
		}
		byte[] array = new byte[length];
		input.read(array);
		return asUuid(array);
	}
}
