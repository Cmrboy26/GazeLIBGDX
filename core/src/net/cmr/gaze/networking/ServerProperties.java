package net.cmr.gaze.networking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class ServerProperties {

	public static String INTERNAL_DIRECTORY = "/serverProperties.data";
	
	private HashMap<String, String> data;
	
	private ServerProperties() {
		data = new HashMap<>();
	}
	
	public String get(String key) {
		return data.get(key);
	}
	
	public static ServerProperties get(GameServer server) {
		ServerProperties properties = new ServerProperties();
		
		File file = server.getFile(INTERNAL_DIRECTORY);
		if(!file.exists()) {
			properties = getDefault(server);
		} else {
			
			try {
				FileReader reader = new FileReader(file);
				BufferedReader dataIn = new BufferedReader(reader);
				
				String s;
				while((s = dataIn.readLine())!=null) {
					String key = s.substring(0, s.indexOf('='));
					String value = s.substring(s.indexOf('=')+1, s.length());
					properties.data.put(key, value);
				}
				
				dataIn.close();
				reader.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		}
		
		return properties;
	} 
	
	private static ServerProperties getDefault(GameServer server) {
		ServerProperties properties = new ServerProperties();
		
		File file = server.getFile(INTERNAL_DIRECTORY);
		
		properties.data.put("port", GameServer.DEFAULT_PORT+"");
		properties.data.put("maxPlayerCount", 8+"");
		
		try {
			server.getFile("").mkdir();
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			FileWriter output = new FileWriter(file);
			BufferedWriter dataOut = new BufferedWriter(output);
			
			for(String key : properties.data.keySet()) {
				String value = properties.data.get(key);
				dataOut.write(key);
				dataOut.write('=');
				dataOut.write(value);
				dataOut.write('\n');
			}
			
			dataOut.flush();
			output.flush();
			dataOut.close();
			output.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return properties;
	}
	
}
