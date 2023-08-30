package net.cmr.gaze.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class SaveData {
	
	public static void write(GameServer server) {
		File file = server.getFile("/saveData.data");
		try {
			
			FileOutputStream fout = new FileOutputStream(file);
			DataOutputStream out = new DataOutputStream(fout);
			
			out.writeLong(server.universalSeed); // Universal seed
			out.writeLong(server.serverRunningTime); // Running time in seconds
			
			out.flush();
			fout.flush();
			out.close();
			fout.close();
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeLocal(String worldName, long seed, long runningTime) {
		FileHandle folder = Gdx.files.external("/Gaze/saves/"+worldName+"/");
		File folderFile = folder.file();
		folderFile.mkdirs();
		FileHandle handle = Gdx.files.external("/Gaze/saves/"+worldName+"/saveData.data");
		File file = handle.file();
		try {
			
			FileOutputStream fout = new FileOutputStream(file);
			DataOutputStream out = new DataOutputStream(fout);
			
			out.writeLong(seed); // Universal seed
			out.writeLong(runningTime); // Running time in seconds
			
			out.flush();
			fout.flush();
			out.close();
			fout.close();
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void read(GameServer server) {
		File file = server.getFile("/saveData.data");
		
		if(!file.exists()) {
			server.serverRunningTime = 0;
			server.universalSeed = new Random().nextLong();
			return;
		}
		
		try {
			
			FileInputStream fout = new FileInputStream(file);
			DataInputStream out = new DataInputStream(fout);
			
			server.universalSeed = out.readLong(); // Universal seed
			server.serverRunningTime = out.readLong(); // Running time in seconds
			
			out.close();
			fout.close();
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
}
