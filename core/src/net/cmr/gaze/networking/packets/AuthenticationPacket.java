package net.cmr.gaze.networking.packets;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;

@PacketID(id = 0)
public class AuthenticationPacket extends Packet {

	String username, serverEncryption;
	int version, playerType;
	byte[] serverSpecificID;
	
	/*
	 * AUTH PROCESS:
	 * 
	 * S->C: new AuthenticationPacket(serverEncryptionString)
	 * C: initialize(desiredUsername, unencryptedUUID)
	 * 
	 * 
	 * 
	 */
	
	
	
	// sent from the server to the player
	public AuthenticationPacket(String serverEncryption) {
		this.serverEncryption = serverEncryption;
		this.username = "";
		this.serverEncryption = "";
		this.version = 0;
		this.playerType = 0;
		this.serverSpecificID = new byte[0];
	}
	
	public AuthenticationPacket(DataInputStream input, int packetSize) throws IOException {
		super(input, packetSize);
	}

	public void initialize(String username, UUID privateUUID, int playerVersion) {
		this.version = Gaze.version;
		this.username = username;
		this.playerType = playerVersion;
		
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] end = digest.digest((privateUUID.toString()+username+serverEncryption).getBytes());
			this.serverSpecificID = end;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public String getUsername() {
		return username;
	}
	public int getVersion() {
		return version;
	}
	public String getServerEncryption() {
		return serverEncryption;
	}
	public byte[] getServerSpecificID() {
		return serverSpecificID;
	}
	public int getPlayerType() {
		return playerType;
	}
	
	@Override
	protected void writePacketData(DataBuffer buffer) throws IOException{
		buffer.writeUTF(serverEncryption);
		buffer.writeUTF(username);
		buffer.writeInt(version);
		buffer.writeInt(serverSpecificID.length);
		if(serverSpecificID.length != 0) {
			buffer.write(serverSpecificID);
		}
		buffer.writeInt(playerType);
	}
	
	@Override
	public void readPacketData(DataInputStream input, int packetSize) throws IOException {
		this.serverEncryption = input.readUTF();
		this.username = input.readUTF();
		this.version = input.readInt();
		int idLen = input.readInt();
		if(idLen != 0) {
			serverSpecificID = new byte[idLen];
			input.read(serverSpecificID);
		} else {
			serverSpecificID = new byte[0];
		}
		this.playerType = input.readInt();
	}
	
	/*@Override
	protected DataBuffer getPacketData() {
		DataBuffer buffer = new DataBuffer();
		return null;
	}
	
	public static void writePacket(DataOutputStream out, String username) throws IOException {
		DataBuffer buffer = new DataBuffer();
		
		buffer.writeUTF(username);
		buffer.writeInt(Gaze.version);
		
		sendPacketData(out, getIdentifier(), buffer);
	}
	
	public static AuthenticationPacket readPacket(DataInputStream input) throws IOException{
		String username = input.readUTF();
		int version = input.readInt();
		return new AuthenticationPacket(username, version);
	}

	@Override
	public int getPacketIdentifier() {
		return getIdentifier();
	}
	
	public static short getIdentifier() {
		return 0;
	}*/

}
