package net.cmr.gaze.networking;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.networking.ConnectionPredicates.ConnectionPredicate;
import net.cmr.gaze.networking.packets.AuthenticationPacket;
import net.cmr.gaze.networking.packets.ChatPacket;
import net.cmr.gaze.networking.packets.DisconnectPacket;
import net.cmr.gaze.networking.packets.PingPacket;
import net.cmr.gaze.networking.packets.PlayerConnectionStatusPacket;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.WorldManager;
import net.cmr.gaze.world.entities.Entity;
import net.cmr.gaze.world.entities.Player;

public class GameServer {
	
	ServerSocket serverSocket;
	ServerProperties properties;
	
	boolean serverRunning = false;
	
	public final String saveName;
	private final int port;
	
	public ConcurrentHashMap<String, PlayerConnection> connections;
	ArrayList<PlayerConnection> connectionInitializeQueue;
	
	ConnectionPredicates connectionPredicates;
	
	public static final String DISCONNECT_GENERIC = "Disconnected";
	public static final String DISCONNECT_SERVER_CLOSE = "Server closed";
	public static final String DISCONNECT_INVALID_VERSION = "Invalid game version!";
	public static final String DISCONNECT_QUIT = "Quit the game";
	public static final String DISCONNECT_RATE_LIMIT = "Sending too many packets!";
	public static final int DEFAULT_PORT = 1126;
	
	public static final int TIMEOUT_TIME = 5000;
	
	public long serverRunningTime;
	public float serverRunningDelta;
	
	private WorldManager worldManager;
	long universalSeed;
	
	String serverEncryptionKey = "awej;lwjerklj543lkjLJjlkjslkjejroi3jr0925JDSFJKSDFl;kewa";
	
	public enum ServerType {
		
		SingleplayerPrivate,
		MultiplayerLocal,
		MultiplayerPublic,
		DedicatedMultiplayer;
		
	}
	
	public ServerType serverType;
	
	public GameServer(ServerType serverType, String saveName) throws IOException {
		this.serverType = serverType;
		this.saveName = saveName;
		this.properties = ServerProperties.get(this);
		this.port = Integer.parseInt(properties.get("port"));
		
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(1000);
		
		connections = new ConcurrentHashMap<>();
		connectionInitializeQueue = new ArrayList<>();
		connectionPredicates = new ConnectionPredicates(this);
	}
	
	public void acceptIncomingSockets() {
		while(serverRunning) {
			try {
				Socket incomingSocket = serverSocket.accept();
				
				if(serverType==ServerType.SingleplayerPrivate) {
					if(connectionInitializeQueue.size()>0 || connections.size()>0) {
						incomingSocket.close();
						continue;
					}
					if(!incomingSocket.getInetAddress().isLoopbackAddress()) {
						incomingSocket.close();
						continue;
					}
				}
				
				//System.out.println("[SERVER] New connection recieved from "+incomingSocket.getRemoteSocketAddress());
				PlayerConnection connection = new PlayerConnection(this, incomingSocket);
				connectionInitializeQueue.add(connection);
				connection.getSender().addPacket(new AuthenticationPacket(this.serverEncryptionKey));
			} catch(SocketTimeoutException e) { 
				// accept timeout
			} catch(SocketException e) {
				if(!serverSocket.isClosed()) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void startServer() {
		if(serverRunning) {
			return;
		}
		
		Gaze.initializeGameContent();
		System.out.println("[SERVER] Starting server...");
		System.out.println("[SERVER] Server type: "+serverType.name());
		serverRunning = true;
		
		loadAll();
		
		Thread thread = new Thread() {
			@Override
			public void run() {
				acceptIncomingSockets();
			}
		};
		thread.start();
		
		Thread loop = new Thread() {
			public void run() {
				serverLoop();
			}
		};
		loop.start();
		
		System.out.println("[SERVER] Server started!");
	}
	
	public void stopServer() {
		if(!serverRunning) {
			return;
		}
		System.out.println("[SERVER] Stopping server...");
		serverRunning = false;
		
		for(String str : connections.keySet()) {
			PlayerConnection connection = connections.get(str);
			disconnect(connection, DISCONNECT_SERVER_CLOSE);
		}
		connections.clear();
		for(int i = 0; i < connectionInitializeQueue.size(); i++) {
			PlayerConnection connection = connectionInitializeQueue.get(i);
			disconnect(connection, DISCONNECT_SERVER_CLOSE);
		}
		
		saveAll();
		
		connectionInitializeQueue.clear();
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void serverLoop() {
		long lastTime = System.nanoTime();
		float deltaTime = -1;
		
		while(serverRunning) {
			if(deltaTime>=0) {
				//long now = System.nanoTime();
				serverRunningDelta+=deltaTime;
				while(serverRunningDelta>1) {
					serverRunningDelta-=1;
					serverRunningTime++;
				}
				
				if(deltaTime < 1/1000f) {
					// NOTE: this sleep block somehow synchronizes the update rate of the client's world data and the server's world updating
					// just dont remove it ig??
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				processPacketData(deltaTime);
				getWorldManager().updateWorlds(deltaTime);
				//double time = CustomTime.timeToSeconds(System.nanoTime()-now);
				//if(time > .001) {
				//	System.out.println(time);
				//}
			}
			
			deltaTime = (System.nanoTime()-lastTime)/1000000000f;
			lastTime = System.nanoTime();
		}
		stopServer();
	}
	
	private void disconnect(PlayerConnection connection) {
		if(connection.getSocket().isClosed()) {
			return;
		}
		if(connection.getPlayer()!=null) {
			System.out.println("[SERVER] Disconnected connection "+connection.getSocket().getRemoteSocketAddress().toString()+" "+connection.disconnectMessage);
		}
		connection.sender.sendPacketInstant(connection.dataOutput, new DisconnectPacket(connection.disconnectMessage==null?DISCONNECT_GENERIC:connection.disconnectMessage));
		try {
			connection.getSocket().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		connection.onDisconnect();
		if(this.serverType==ServerType.SingleplayerPrivate) {
			stopServer();
		}
	}
	
	private void disconnect(PlayerConnection connection, String disconnectMessage) {
		if(connection.getSocket().isClosed()) {
			return;
		}
		connection.disconnectMessage = disconnectMessage;
		System.out.println("[SERVER] Disconnected connection "+connection.getSocket().getRemoteSocketAddress().toString()+" "+connection.disconnectMessage);
		connection.sender.sendPacketInstant(connection.dataOutput, new DisconnectPacket(disconnectMessage));
		try {
			connection.getSocket().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		connection.onDisconnect();
		sendAllPacketIf(new PlayerConnectionStatusPacket(connection.username, ConnectionStatus.DISCONNECTED), ConnectionPredicate.SEND_ALL);
	}
	
	float pingDelta = 0;
	final float pingTime = 0.5f;
	float positionUpdateDelta = 0;
	final float positionUpdateTime = 1/120f;
	
	public void processPacketData(float deltaTime) {
		pingDelta+=deltaTime;
		positionUpdateDelta+=deltaTime;
		ArrayList<PlayerConnection> removeList = new ArrayList<>();
		for(int i = 0; i < connectionInitializeQueue.size(); i++) {
			PlayerConnection connection = connectionInitializeQueue.get(i);
			if(!connection.isConnected()) {
				removeList.add(connection);
				disconnect(connection);
				continue;
			}
			
			try {
				connection.processConnectionIO();
			} catch(InvalidVersionException e) {
				removeList.add(connection);
				disconnect(connection, DISCONNECT_INVALID_VERSION);
				continue;
			} catch(NetworkException e) {
				System.out.println("[SERVER] [ERROR] "+e.getMessage());
				removeList.add(connection);
				disconnect(connection, e.getMessage());
				continue;
			} catch (IOException e) {
				e.printStackTrace();
				removeList.add(connection);
				disconnect(connection, e.getMessage());
				continue;
			} catch(Exception e) {
				e.printStackTrace();
				removeList.add(connection);
				disconnect(connection, e.getMessage());
				continue;
			}
			
			if(connection.isInitialized()) {
				removeList.add(connection);
				connections.put(connection.getUsername(), connection);
				sendAllPacketIf(new PlayerConnectionStatusPacket(connection.username, ConnectionStatus.CONNECTED), ConnectionPredicate.SEND_ALL);
				System.out.println("[SERVER] Connection from "+connection.getSocket().getRemoteSocketAddress()+" successfully logged in as user "+connection.getUsername()+ " : "+connection.playerUUID.toString());
				
				getWorldManager().getWorld(connection.loadedWorld).addPlayer(connection);
			}
		}
		while(removeList.size()>0) {
			connectionInitializeQueue.remove(removeList.get(0));
			removeList.remove(0);
		}
		
		//
		
		for(String username : connections.keySet()) {
			PlayerConnection connection = connections.get(username);
			if(!connection.isConnected()) {
				removeList.add(connection);
				disconnect(connection);
				continue;
			}
			
			try {
				connection.processConnectionIO();
			} catch(InvalidVersionException e) {
				removeList.add(connection);
				disconnect(connection, DISCONNECT_INVALID_VERSION);
				continue;
			} catch(NetworkException e) {
				System.out.println("[SERVER] [ERROR] "+e.getMessage());
				removeList.add(connection);
				disconnect(connection, e.getMessage());
				e.printStackTrace();
				continue;
			} catch (IOException e) {
				removeList.add(connection);
				disconnect(connection, e.getMessage());
				e.printStackTrace();
				continue;
			} catch(Exception e) {
				removeList.add(connection);
				e.printStackTrace();
				disconnect(connection, e.getMessage());
				continue;
			}
			
			if(pingDelta > pingTime) {
				connection.sender.addPacket(new PingPacket(System.currentTimeMillis(), true));
			}
			
			if(positionUpdateDelta > positionUpdateTime) {
				/*String[] usernames = new String[connections.size()];
				Vector2Double[] movements = new Vector2Double[connections.size()];
				Vector2Double[] positions = new Vector2Double[connections.size()];
				
				int i = 0;
				for(String str : connections.keySet()) {
					PlayerConnection connectionTemp = connections.get(str);
					usernames[i] = str;
					movements[i] = new Vector2Double(connectionTemp.getPlayer().getVelocityX(), connectionTemp.getPlayer().getVelocityY());
					positions[i] = new Vector2Double(connectionTemp.getPlayer().getX(), connectionTemp.getPlayer().getY());
					i++;
				}
				
				connection.sender.addPacket(new PositionUpdatesPacket(usernames, positions, movements));*/
			}
			
		}
		while(removeList.size()>0) {
			connections.remove(removeList.get(0).getUsername());
			removeList.remove(0);
		}
		if(pingDelta > pingTime) {
			pingDelta = 0;
		}
		if(positionUpdateDelta > positionUpdateTime) {
			positionUpdateDelta = 0;
		}
		
	}
	
	public boolean evaluatePredicate(PlayerConnection connection, ConnectionPredicate predicate, Object...objects) {
		return connectionPredicates.evaluate(predicate, new Object[] {connection.username, objects});
	}
	
	public void sendAllPacketIf(Packet packet, ConnectionPredicate predicate, Object...objectss) {
		
		if(predicate == ConnectionPredicate.PLAYER_IN_BOUNDS) {
			World world = (World) objectss[1];
			world.getPlayers().forEach(connection-> {
				sendPacketIf(connection, packet, predicate, objectss);
			});
			return;
		} else if(predicate == ConnectionPredicate.PLAYER_NOW_IN_BOUNDS) {
			World world = (World) objectss[2];
			world.getPlayers().forEach(connection-> {
				sendPacketIf(connection, packet, predicate, objectss);
			});
			return;
		}
		
		for(String username : connections.keySet()) {
			if(connectionPredicates.evaluate(predicate, new Object[] {username, objectss})) {
				connections.get(username).sender.addPacket(packet);
			}
		}
	}
	
	public void sendAllPacketExcludeSelfIf(PlayerConnection exclude, Packet packet, ConnectionPredicate predicate, Object...objectss) {
		
		if(predicate == ConnectionPredicate.PLAYER_IN_BOUNDS) {
			World world = (World) objectss[1];
			for(PlayerConnection connection : world.getPlayers()) {
				if(connection.equals(exclude)) {
					continue;
				}
				sendPacketIf(connection, packet, predicate, objectss);
			}
			return;
		} else if(predicate == ConnectionPredicate.PLAYER_NOW_IN_BOUNDS) {
			World world = (World) objectss[2];
			for(PlayerConnection connection : world.getPlayers()) {
				if(connection.equals(exclude)) {
					continue;
				}
				sendPacketIf(connection, packet, predicate, objectss);
			}
			return;
		}
		
		for(String username : connections.keySet()) {
			if(connectionPredicates.evaluate(predicate, new Object[] {username, objectss})) {
				connections.get(username).sender.addPacket(packet);
			}
		}
	}
	
	public void sendPacketIf(PlayerConnection connection, Packet packet, ConnectionPredicate predicate, Object...objectss) {
		String username = connection.getUsername();
		if(connectionPredicates.evaluate(predicate, new Object[] {username, objectss})) {
			connections.get(username).sender.addPacket(packet);
		}
	}
	
	public void saveAll() {
		if(serverType==ServerType.DedicatedMultiplayer) {
			//handle = Gdx.files.absolute(System.getProperty("user.dir")+"/data/"+worldName+"/");
			File saveFolder = new File(System.getProperty("user.dir")+"/serverdata/");
			saveFolder.mkdirs();
			
		} else {
			FileHandle handle = Gdx.files.external("/Gaze/saves/"+saveName+"/");
			handle.mkdirs();
		}
		getWorldManager().saveAllWorlds();
		saveSaveData();
	}
	
	public void loadAll() {
		loadSaveData();
		setWorldManager(new WorldManager(this));
	}
	
	public void saveSaveData() {
		SaveData.write(this);
		
		/*File file = getFile("/saveData.data");
		try {
			
			FileOutputStream fout = new FileOutputStream(file);
			DataOutputStream out = new DataOutputStream(fout);
			
			out.writeLong(universalSeed); // Universal seed
			out.writeLong(serverRunningTime); // Running time in seconds
			
			out.flush();
			fout.flush();
			out.close();
			fout.close();
			
		} catch(IOException e) {
			e.printStackTrace();
		}*/
	}
	
	public void loadSaveData() {
		SaveData.read(this);
		
		/*File file = getFile("/saveData.dat");
		
		if(!file.exists()) {
			serverRunningTime = 0;
			return;
		}
		
		try {
			
			FileInputStream fout = new FileInputStream(file);
			DataInputStream out = new DataInputStream(fout);
			
			universalSeed = out.readLong(); // Universal seed
			serverRunningTime = out.readLong(); // Running time in seconds
			
			out.close();
			fout.close();
			
		} catch(IOException e) {
			e.printStackTrace();
		}*/
	}
	
	public void savePlayer(PlayerConnection connection) {
		File playerFile;
		String fileName = connection.playerUUID+".player";
		
		if(serverType==ServerType.DedicatedMultiplayer) {
			File saveFolder = new File(System.getProperty("user.dir")+"/serverdata/players/");
			saveFolder.mkdirs();
			playerFile = new File(System.getProperty("user.dir")+"/serverdata/players/"+fileName);
		} else {
			FileHandle handle = Gdx.files.external("/Gaze/saves/"+saveName+"/players/");
			handle.mkdirs();
			playerFile = Gdx.files.external("/Gaze/saves/"+saveName+"/players/"+fileName).file();
		}
		
		try {
			playerFile.delete();
			playerFile.createNewFile();
			
			DataBuffer buffer = new DataBuffer();

			buffer.writeUTF(connection.getPlayer().getWorld().getWorldName());
			connection.getPlayer().writeEntity(buffer, false, true);
			
			FileOutputStream outputStream = new FileOutputStream(playerFile);
			outputStream.write(buffer.toArray());
			outputStream.flush();
			outputStream.close();
			buffer.flush();
			buffer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public Player loadPlayer(PlayerConnection connection) {
		File playerFile;
		String fileName = connection.playerUUID+".player";
		
		if(serverType==ServerType.DedicatedMultiplayer) {
			playerFile = new File(System.getProperty("user.dir")+"/serverdata/players/"+fileName);
		} else {
			playerFile = Gdx.files.external("/Gaze/saves/"+saveName+"/players/"+fileName).file();
		}
		
		if(playerFile.exists()) {
			try {
				FileInputStream fileInput = new FileInputStream(playerFile);
				DataInputStream inputStream = new DataInputStream(fileInput);
				
				connection.loadedWorld = inputStream.readUTF();
				Player player = (Player) Entity.readEntity(inputStream, true);
				
				inputStream.close();
				fileInput.close();
				
				return player;
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		return new Player(connection.getUsername(), null);
	}
	
	public int currentActivePlayers() {
		return connections.size();
	}

	public boolean running() {
		return serverRunning;
	}
	
	public File getFile(String internalDirectory) {
		File file;
		
		if(serverType==ServerType.DedicatedMultiplayer) {
			file = new File(System.getProperty("user.dir")+"/serverdata"+internalDirectory);
		} else {
			FileHandle handle = Gdx.files.external("/Gaze/saves/"+saveName+internalDirectory);
			file = handle.file();
		}
		
		return file;
	}

	public WorldManager getWorldManager() {
		return worldManager;
	}

	public void setWorldManager(WorldManager manager) {
		this.worldManager = manager;
	}

	public int getPort() {
		return port;
	}

	public long getUniversalSeed() {
		return universalSeed;
	}
	
}
