package net.cmr.gaze.networking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

import com.badlogic.gdx.utils.DataBuffer;

@PacketID(id = -1)
public abstract class CompressedPacket extends Packet {

	public CompressedPacket() {
		
	}
	
	public CompressedPacket(DataInputStream input, int packetSize) throws IOException {
		readPacketData(input, packetSize);
	}

	@Override
	public int sendPacket(DataOutputStream output) throws IOException {
		DataBuffer buffer = new DataBuffer();
		writePacketData(buffer);
		output.writeInt(getPacketIdentifier());
		
		byte[] compressedArray = compress(buffer.toArray());
		
		output.writeInt(compressedArray.length);
		output.write(compressedArray);
		output.flush();
		buffer.close();
		buffer = null;
		return compressedArray.length;
	}
	
	@Override
	protected abstract void writePacketData(DataBuffer buffer) throws IOException;
	@Override
	public void readPacketData(DataInputStream originalInput, int packetSize) throws IOException {
		byte[] compressedData = new byte[packetSize];
		originalInput.read(compressedData);
		byte[] decompressedData = decompress(compressedData);
        ByteArrayInputStream in = new ByteArrayInputStream(decompressedData);
        DataInputStream input = new DataInputStream(in);
        readDecompressedPacketData(input, decompressedData.length);
        input.close();
        in.close();
	}
	
	public abstract void readDecompressedPacketData(DataInputStream input, int packetSize) throws IOException;

	public static byte[] compress(byte[] in) {
	    try {
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        DeflaterOutputStream defl = new DeflaterOutputStream(out);
	        defl.write(in);
	        defl.flush();
	        defl.close();

	        return out.toByteArray();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	public static byte[] decompress(byte[] in) {
	    try {
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        InflaterOutputStream infl = new InflaterOutputStream(out);
	        infl.write(in);
	        infl.flush();
	        infl.close();

	        return out.toByteArray();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	
}
