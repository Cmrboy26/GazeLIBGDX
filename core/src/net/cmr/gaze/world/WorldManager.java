package net.cmr.gaze.world;

import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.networking.CompressedPacket;
import net.cmr.gaze.networking.GameServer;
import net.cmr.gaze.world.WorldGenerator.WorldGeneratorType;
import net.cmr.gaze.world.powerGrid.EnergyDistributor;

public class WorldManager {

	private HashMap<String, World> worldMap;
	private final GameServer server;
	public long universalSeed;
	public static final String DEFAULT_WORLD_NAME = "default";

	
	public WorldManager(GameServer server) {
		this.worldMap = new HashMap<>();
		this.server = server;
		loadAllWorlds();
		
		if(getWorld(DEFAULT_WORLD_NAME)==null) {
			createWorld(DEFAULT_WORLD_NAME, WorldGenerator.getGenerator(WorldGeneratorType.DEFAULT_OVERWORLD));
			saveWorld(DEFAULT_WORLD_NAME);
		}
	}
	
	public World getWorld(String world) {
		return worldMap.get(world);
	}
	public void saveWorld(String worldName) {
		
		World world = getWorld(worldName);
		if(world == null) {
			return;
		}
		
		File saveFolder = server.getFile("/worlds/"+worldName+"/");
		saveFolder.mkdirs();
		
		HashMap<Point, ArrayList<Chunk>> regions = new HashMap<>();
		
		// puts all the chunks into their specified 16x16 region
		for(Chunk chunk : world.chunkList.values()) {
			Point region = Chunk.getChunk(chunk.getCoordinate());
			ArrayList<Chunk> regionChunks;
			if(regions.containsKey(region)) {
				regionChunks = regions.get(region);
			} else {
				regionChunks = new ArrayList<>();
				regions.put(region, regionChunks);
			}
			regionChunks.add(chunk);
		}
		
		File worldData = server.getFile("/worlds/"+worldName+"/world.data");
		File electricityData = server.getFile("/worlds/"+worldName+"/electricity.data");
		
		try {
			worldData.delete();
			FileOutputStream outputStream = new FileOutputStream(worldData);
			
			DataBuffer buffer = new DataBuffer();
			
			buffer.writeInt(world.getGenerator().getGeneratorType().getID());
			buffer.writeDouble(world.getSeed());
			buffer.writeDouble(world.getWorldTime());
			
			outputStream.write(buffer.toArray());
			buffer.close();
			
			outputStream.flush();
			outputStream.close();
			
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		DataBuffer electricityBuffer = new DataBuffer();
		for(Point point : regions.keySet()) {
			ArrayList<Chunk> chunks = regions.get(point);
			String fileName = point.x+"~"+point.y+".region";
			
			File regionFile = server.getFile("/worlds/"+worldName+"/"+fileName);

			try {
				
				DataBuffer buffer = new DataBuffer();
				for(Chunk c : chunks) {
					c.writeChunk(buffer, electricityBuffer);
				}
				
				byte[] result = CompressedPacket.compress(buffer.toArray());
				buffer.close();
				FileOutputStream outputStream = new FileOutputStream(regionFile);
				
				outputStream.write(result);
				
				outputStream.flush();
				outputStream.close();
				
			} catch(IOException e) {
				e.printStackTrace();
			}
			
		}
		
		try {
			electricityData.delete();
			FileOutputStream outputStream = new FileOutputStream(electricityData);
			
			outputStream.write(electricityBuffer.toArray());
			electricityBuffer.close();
			
			outputStream.flush();
			outputStream.close();
			
		} catch(IOException e) {
			e.printStackTrace();
		}

	}
	public void saveAllWorlds() {
		for(String worldName : worldMap.keySet()) {
			saveWorld(worldName);
		}
	}
	public void loadWorld(String worldName) {
		
		File worldFolder = server.getFile("/worlds/"+worldName+"/");
		
		if(!worldFolder.exists()) {
			// TODO: remove this in the future probably
			throw new NullPointerException("Tried to load a world not saved to file! Name: "+worldName);
		}
		
		// TODO: read the world.data file and implement all the data into the world object
		File worldData = server.getFile("/worlds/"+worldName+"/world.data");
		
		if(!worldFolder.exists()) {
			// TODO: remove this in the future probably
			throw new NullPointerException("world.data does not exist for world name "+worldName);
		}

		File electricityData = server.getFile("/worlds/"+worldName+"/electricity.data");
		if(!electricityData.exists()) {
			throw new NullPointerException();
		}
		
		WorldGeneratorType type = null;
		double seed = 0, worldTime = 0;
		try {
			
			FileInputStream fileInput = new FileInputStream(worldData);
			DataInputStream input = new DataInputStream(fileInput);
			
			type = WorldGeneratorType.getTypeFromID(input.readInt()); 
			seed = input.readDouble();
			worldTime = input.readDouble();
			 
			//System.out.println("READ SEED WORLD "+worldName+" : "+seed);
			
			input.close();
			fileInput.close();
			
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		World world = new World(WorldGenerator.getGenerator(type), server, worldName, seed);
		world.seed = seed;
		world.worldTime = worldTime;
		
		for(File region : worldFolder.listFiles()) {
			
			if(!region.getName().contains("~")) {
				continue;
			}
			
			//String strx = region.getName().substring(0, region.getName().indexOf("~"));
			//String stry = region.getName().substring(region.getName().indexOf("~")+1, region.getName().indexOf("."));
			//int x = Integer.parseInt(strx), y = Integer.parseInt(stry);
			
			try {
				FileInputStream fileInput = new FileInputStream(region);
				
				byte[] all = new byte[fileInput.available()];
				fileInput.read(all);
				fileInput.close();
				
				byte[] decompressed = CompressedPacket.decompress(all);
				all = null;
				
				ByteArrayInputStream in = new ByteArrayInputStream(decompressed);
		        DataInputStream input = new DataInputStream(in);
		        
		        while(input.available() > 0) {
		        	Chunk c = Chunk.readChunk(input, world);
		        	world.chunkList.put(c.getCoordinate(), c);
		        }
		        
		        input.close();
		        in.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			
			FileInputStream fileInput = new FileInputStream(electricityData);
			DataInputStream input = new DataInputStream(fileInput);
			
			// READ CONNECTIONS AND SET THEM

			while(input.available() > 0) {
				ElectricityPole.readConnections(input, world);
			}
			
			input.close();
			fileInput.close();
			
		} catch(IOException e) {
			e.printStackTrace();
		}

		worldMap.put(worldName, world);
	}
	public void loadAllWorlds() {
		File worldsFolder = server.getFile("/worlds/");
		
		if(!worldsFolder.exists()) {
			return;
		}
		
		for(File f : worldsFolder.listFiles()) {
			if(f.isDirectory()) {
				loadWorld(f.getName());
			}
		}
		
	}
	
	public void createWorld(String name, WorldGenerator generator) {
		if(worldMap.containsKey(name)) {
			return;
		}
		World world = new World(generator, server, name, (new Random(getSeedFromWorld(name, generator)).nextDouble()-.5)*(Short.MAX_VALUE*2));
		worldMap.put(name, world);
	}
	
	private long getSeedFromWorld(String name, WorldGenerator generator) {
		String worldSeedString = name+server.getUniversalSeed()+generator.getGeneratorType().id;
    	long hash = UUID.nameUUIDFromBytes(worldSeedString.getBytes()).getMostSignificantBits();
    	return hash;
	}
	
	public void updateWorlds(double delta) {
		for(World world : worldMap.values()) {
			world.update(delta);
		}
	}
	
	public World getUndergroundWorld(World input) {
		Objects.requireNonNull(input);
		
		final String undergroundName = "Underground";
		
		String oppositeName = input.getWorldName().contains(undergroundName)?input.getWorldName().substring(0, input.getWorldName().length()-undergroundName.length()):input.getWorldName()+undergroundName;

		World target = getWorld(oppositeName);

		if(target != null) {
			return target;
		}
		
		createWorld(oppositeName, input.getGenerator().invertUnderground());
		return getWorld(oppositeName);
	}

	public World getDefaultWorld() {
		return getWorld(DEFAULT_WORLD_NAME);
	}
}
