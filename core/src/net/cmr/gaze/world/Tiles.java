package net.cmr.gaze.world;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import net.cmr.gaze.Logger;

public class Tiles {

	static HashMap<Integer, TileType> identifierStorage = new HashMap<>();
	private static boolean initialized;
	private static HashMap<TileType, Tile> map;
	private static HashMap<TileType, Color> averageColorMap;
	
	public static Tile getTile(TileType type) {
		if(map==null) {
			initialize();
		}
		Tile result = map.getOrDefault(type, null);
		Tile clone = result.clone();
		return clone;
	}
	
	public static Tile getTile(TileType type, DataInputStream input) throws IOException{
		if(map==null) {
			initialize();
		}
		return map.getOrDefault(type, null).readTile(input, type);
	}
	
	public static void initialize() {
		if(initialized) {
			return;
		}
		map = new HashMap<>();
		
		for(int i = 0; i < TileType.values().length; i++) {
			TileType type = TileType.values()[i];
			Logger.log("INFO", "["+(i+1)+"/"+TileType.values().length+"] \tInitializing Tile... "+type.name());
			Class<? extends Tile> itemClass = type.clazz;
			try {
				for(Constructor<?> construct : itemClass.getConstructors()) {
					Tile item = (Tile) construct.newInstance();
					map.put(type, item);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		averageColorMap = new HashMap<>(map.size());
		
		for(TileType type : TileType.values()) {
			if(!map.containsKey(type)) {
				Logger.error("DEVELOPMENT", "Tiles singleton does not contain an entry for TileType "+type);
				Gdx.app.exit();
			}
		}
		
		initialized = true;
	}
	
	public static Color getAverageColor(TileType type) {
		return averageColorMap.getOrDefault(type, null);
	}
	public static void setAverageColor(TileType type, Color color) {
		averageColorMap.put(type, color);
	}
	
}
