package net.cmr.gaze.networking.packets;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.networking.CompressedPacket;
import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;
import net.cmr.gaze.world.Chunk;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.entities.Entity;

@PacketID(id = 6)
public class ChunkDataPacket extends CompressedPacket {

	Tile[][][] tileData;
	Point chunkCoordinate;
	ArrayList<Entity> entities;
	
	/*public ChunkDataPacket(Tile[][][] tileData, Point chunkCoordinate, ArrayList<Entity> entities) {
		this.tileData = tileData;
		this.chunkCoordinate = chunkCoordinate;
		this.entities = entities;
	}*/
	
	public ChunkDataPacket(Chunk c) {
		
		if(c == null) {
			throw new NullPointerException();
		}
		
		this.tileData = c.getTiles();
		this.chunkCoordinate = c.getCoordinate();
		this.entities = c.getEntities();
	}
	
	public ChunkDataPacket(DataInputStream input, int packetSize) throws IOException {
		super(input, packetSize);
	}
	
	public Tile[][][] getTiles() {
		return tileData;
	}
	public Point getChunkCoordinate() {
		return chunkCoordinate;
	}
	public ArrayList<Entity> getChunkEntities() {
		return entities;
	}

	@Override
	protected void writePacketData(DataBuffer buffer) throws IOException {
		buffer.writeInt(chunkCoordinate.x);
		buffer.writeInt(chunkCoordinate.y);
		for(int x = 0; x < Chunk.CHUNK_SIZE; x++) {
			for(int y = 0; y < Chunk.CHUNK_SIZE; y++) {
				for(int z = 0; z < Chunk.LAYERS; z++) {
					Tile toAdd = tileData[x][y][z];
					Tile.writeOutgoingTile(toAdd, buffer);
				}
			}
		}
		buffer.writeInt(entities.size());
		for(int i = 0; i < entities.size(); i++) {
			entities.get(i).writeEntity(buffer, false, false);
		}
		/*for(int z = 2; z >= 0; z--) {
			for(int x = 0; x < 16; x++) {
				System.out.print(getTiles()[x][0][z]+"\t");
			}
			System.out.println();
		}*/
		
	}

	@Override
	public void readDecompressedPacketData(DataInputStream input, int packetSize) throws IOException {
		chunkCoordinate = new Point(input.readInt(), input.readInt());
		tileData = new Tile[Chunk.CHUNK_SIZE][Chunk.CHUNK_SIZE][Chunk.LAYERS];
		for(int x = 0; x < Chunk.CHUNK_SIZE; x++) {
			for(int y = 0; y < Chunk.CHUNK_SIZE; y++) {
				for(int z = 0; z < Chunk.LAYERS; z++) {
					Tile in = Tile.readIncomingTile(input);
					tileData[x][y][z] = in;
				}
			}
		}
		entities = new ArrayList<>();
		int entityLength = input.readInt();
		for(int i = 0; i < entityLength; i++) {
			entities.add(Entity.readEntity(input, false));
		}
		/*for(int z = 2; z >= 0; z--) {
			for(int x = 0; x < 16; x++) {
				System.out.print(getTiles()[x][0][z]+"\t");
			}
			System.out.println();
		}*/
	}

}
