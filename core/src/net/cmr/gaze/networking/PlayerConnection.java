package net.cmr.gaze.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

import com.badlogic.gdx.math.Vector2;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.crafting.Crafting;
import net.cmr.gaze.crafting.Crafting.CraftingStation;
import net.cmr.gaze.crafting.Recipe;
import net.cmr.gaze.inventory.Inventory;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.networking.ConnectionPredicates.ConnectionPredicate;
import net.cmr.gaze.networking.GameServer.ServerType;
import net.cmr.gaze.networking.packets.AuthenticationPacket;
import net.cmr.gaze.networking.packets.ChestInventoryPacket;
import net.cmr.gaze.networking.packets.CraftPacket;
import net.cmr.gaze.networking.packets.CraftingStationPacket;
import net.cmr.gaze.networking.packets.DisconnectPacket;
import net.cmr.gaze.networking.packets.HotbarUpdatePacket;
import net.cmr.gaze.networking.packets.InventoryClickPacket;
import net.cmr.gaze.networking.packets.InventoryUpdatePacket;
import net.cmr.gaze.networking.packets.PingPacket;
import net.cmr.gaze.networking.packets.PlayerInputPacket;
import net.cmr.gaze.networking.packets.PlayerInteractPacket;
import net.cmr.gaze.networking.packets.PositionPacket;
import net.cmr.gaze.networking.packets.UIEventPacket;
import net.cmr.gaze.util.CustomMath;
import net.cmr.gaze.world.CraftingStationTile;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.entities.DroppedItem;
import net.cmr.gaze.world.entities.Player;
import net.cmr.gaze.world.tile.ChestTile;

public class PlayerConnection {

	boolean initialized = false, connected = true, disconnect = false;
	String disconnectMessage = "";
	
	Socket socket;
	PacketBuilder builder;
	PacketSender sender;
	String username;
	DataInputStream dataInput;
	DataOutputStream dataOutput;
	public GameServer server;
	Player player;
	UUID playerUUID;
	
	float latency;
	
	public String loadedWorld = "default";
	
	ArrayList<PlayerInteractPacket> interactPackets;
	PositionPacket lastPacket;
	
	private double movementX = 0, movementY = 0;
	private double lastDifference;
	private boolean sprinting = false;
	
	int targetX, targetY;
	Tile targetTile;
	
	public PlayerConnection(GameServer server, Socket socket) {
		this.socket = socket;
		this.builder = new PacketBuilder(true) {
			@Override
			public void processPacket(Packet packet) {
				processIncomingPacket(packet);
			}
		};
		this.sender = new PacketSender();
		this.server = server;
		try {
			this.dataInput = new DataInputStream(socket.getInputStream());
			this.dataOutput = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.interactPackets = new ArrayList<>();
	}
	
	public void processConnectionIO() throws Exception {
		builder.build(dataInput);
		sender.sendAll(dataOutput);
	}
	
	public void update() {
		if(targetTile!=null) {
			if(getPlayer().getDistanceToTile(targetX, targetY)>getPlayer().getInteractRadius()) {
				getSender().addPacket(new CraftingStationPacket(0, 0, CraftingStation.NONE));
				this.targetTile = null;
				this.targetX = 0;
				this.targetY = 0;
			}
		}
	}
	
	public void initialize(AuthenticationPacket packet) {
		if(!isInitialized()) {
			//this.username = packet.getUsername();
			this.playerUUID = UUID.nameUUIDFromBytes(packet.getServerSpecificID());
			this.username = packet.getUsername();
			packet.getUsername().chars().forEach(c -> {
				if(!(Character.isAlphabetic(c) || Character.isDigit(c))) {
					throw new NetworkException("Invalid username characters recieved. (don't hack)");
				}
			});
			
			while(server.connections.containsKey(username)) {
				//this.username+="-";
				throw new NetworkException("Player with same username already online.");
			}
			if(packet.getVersion()==Gaze.version) {
				initialized = true;
				this.player = server.loadPlayer(this);
				this.player.setPlayerType(packet.getPlayerType());
				return;
			}
			throw new InvalidVersionException("Server set to version "+Gaze.version+", recieved connection version "+packet.getVersion());
		}
	}
	
	public void disconnect(String message) {
		disconnect = true;
		disconnectMessage = message;
	}
	
	public void onDisconnect() {
		if(initialized) {
			server.savePlayer(this);
			player.getWorld().removePlayer(this);
		}
		try {
			this.dataInput.close();
			this.dataOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	
	public boolean isConnected() {
		return !(System.currentTimeMillis()-builder.millisTimeSinceDataRecieved > GameServer.TIMEOUT_TIME || socket.isClosed()) && !disconnect;
	}
	
	public String getUsername() {
		if(isInitialized()) {
			return username;
		} else {
			throw new NullPointerException("Connection not initialized, couldn't get name.");
		}
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	
	public Player getPlayer() {
		return player;
	}
	
	public PacketSender getSender() {
		return sender;
	}
	
	public void processIncomingPacket(Packet packet) {
		if(packet instanceof AuthenticationPacket) {
			initialize((AuthenticationPacket) packet);
		}
		if(packet instanceof PingPacket) {
			if(!((PingPacket)packet).isServerPing()) {
				sender.sendPacketInstant(dataOutput, packet);
			} else {
				latency = (System.currentTimeMillis()-((PingPacket)packet).getTime())/2f;
			}
		}
		if(packet instanceof PlayerInputPacket) {
			PlayerInputPacket inputPacket = (PlayerInputPacket) packet;
			movementX = CustomMath.minMax(-1f, inputPacket.getX(), 1f);
			movementY = CustomMath.minMax(-1f, inputPacket.getY(), 1f);
			
			Vector2 end = CustomMath.clampCircle((float) movementX, (float) movementY, 1, .1f);
			
			movementY = end.y;
			movementX = end.x;
			
			sprinting = inputPacket.getSprinting();
			setPlayerMovement();
		}
		if(packet instanceof DisconnectPacket) {
			disconnect(((DisconnectPacket)packet).getMessage());
		}
		if(packet instanceof PlayerInteractPacket) {
			PlayerInteractPacket interact = (PlayerInteractPacket) packet;
			interactPackets.add(interact);
		}
		if(packet instanceof PositionPacket) {
			PositionPacket pos = (PositionPacket) packet;
			lastPacket = pos;
		}
		if(packet instanceof HotbarUpdatePacket) {
			HotbarUpdatePacket name = (HotbarUpdatePacket) packet;
			if(name.getSlot() < 0 || name.getSlot() >= 7) {
				disconnect("Invalid hotbar slot "+name.getSlot());
			}
			player.setHotbarSlot(name.getSlot());
			name = new HotbarUpdatePacket( player.getUUID(), (byte) name.getSlot());
			for(PlayerConnection connection : player.getWorld().getPlayers()) {
				if(!connection.equals(this)) {
					server.sendPacketIf(connection, name, ConnectionPredicate.PLAYER_IN_BOUNDS, player.getChunk(), player.getWorld());
				}
			}
		}
		if(packet instanceof InventoryClickPacket) {
			InventoryClickPacket click = (InventoryClickPacket) packet;

			Inventory selectedInventory, clickedInventory = null;
			
			if(click.getSelectedIsPlayerInventory()) {
				selectedInventory = getPlayer().getInventory();
			} else {
				if(targetTile instanceof ChestTile) {
					selectedInventory = ((ChestTile)targetTile).getInventory();
				} else {
					return;
				}
			}
			
			if(click.getClickedIsPlayerInventory()) {
				clickedInventory = getPlayer().getInventory();
			} else {
				if(targetTile!=null) {
					if(targetTile instanceof ChestTile) {
						clickedInventory = ((ChestTile)targetTile).getInventory();
					} else {
						return;
					}
				}
			}
			
			if(click.getClickedSlot()==-1) {
				// Drop Item
				Item drop = selectedInventory.get(click.getSelectedSlot());
				player.getWorld().addEntity(new DroppedItem(drop, getPlayer().getTileX(), getPlayer().getTileY()));
				selectedInventory.put(click.getSelectedSlot(), null);
			} else {
				clickedInventory.inventoryAction(selectedInventory, click.getSelectedSlot(), click.getClickedSlot(), click.getModifiers());
			}
			inventoryChanged(false);
			
			if(!click.getClickedIsPlayerInventory() || !click.getSelectedIsPlayerInventory()) {
				if(targetTile != null) {
					getPlayer().getWorld().onTileChange(targetX, targetY, targetTile.getType().layer);
				}
			}
			
		}
		if(packet instanceof CraftPacket) {
			CraftPacket craft = (CraftPacket) packet;
			Recipe recipe = Crafting.getRecipe(craft.getCategory(), craft.getRecipe());
			if(recipe != null) {
				
				// TODO: BUG - when crafting something while your inventory is full with items on the ground,
				// the client is desynced from the server because the dropped items get picked up while the packet is in transit
				
				Item[] results = recipe.craft(getPlayer().getInventory(), craft.getTimes(), getCraftingStation(), getPlayer().getSkills());
				inventoryChanged(false);
				if(results != null) {
					for(int i = 0; i < results.length; i++) {
						if(results[i] == null) {
							continue;
						}
						getPlayer().getWorld().addEntity(new DroppedItem(results[i], getPlayer().getTileX(), getPlayer().getTileY()));
					}
				}
			}
		}
		if(packet instanceof UIEventPacket) {
			UIEventPacket ui = (UIEventPacket) packet;
			if(ui.getOpenState()==false && ui.getContainerID()==1) {
				this.targetTile = null;
				this.targetX = 0;
				this.targetY = 0;
			}
		}
	}
	
	public CraftingStation getCraftingStation() {
		if(targetTile!=null) {
			if(targetTile instanceof CraftingStationTile) {
				CraftingStationTile cst = (CraftingStationTile) targetTile;
				return cst.getStation();
			}
		}
		return CraftingStation.NONE;
	}

	public void inventoryChanged(boolean sendUpdateToSelf) {
		if(sendUpdateToSelf) {
			getSender().addPacket(new InventoryUpdatePacket(player));
		}
		server.sendAllPacketExcludeSelfIf(this, new InventoryUpdatePacket(player), ConnectionPredicate.PLAYER_IN_BOUNDS, player.getChunk(), player.getWorld());
	}
	public void inventoryChanged() {
		inventoryChanged(true);
	}
	
	public ArrayList<PlayerInteractPacket> getInteractEvents() {
		return interactPackets;
	}
	
	public void processPositionPacket() {
		if(lastPacket == null) {
			return;
		}
		
		double difference = Math.hypot((getPlayer().getX()-lastPacket.getX()), (getPlayer().getY()-lastPacket.getY()));
		
		//System.out.println(difference);
		
		if(server.serverType==ServerType.SingleplayerPrivate) {
			//getPlayer().setPosition(lastPacket.getX(), lastPacket.getY());
		}
		
		//if(difference != 0 && difference == lastDifference) {
			// TODO LOTS OF POTENTIAL FOR VULNERABILITIES
			//System.out.println(username+":"+difference);
			/*if(difference < Tile.TILE_SIZE/10) {
				getPlayer().setPosition(lastPacket.getX(), lastPacket.getY());
			} else {
				System.out.println("Player position incorrect "+username+" : off by a significant amount, correcting... "+difference);
				sender.addPacket(new PositionPacket(new Vector2Double(getPlayer().getX(), getPlayer().getY())));
				difference = 0;
				lastDifference = 0;
			}*/
		//}
		
		
		
		lastDifference = difference;
	}
	
	public void setPlayerMovement() {
		player.setSprinting(sprinting);
		player.setVelocity(movementX*player.getSpeed(), movementY*player.getSpeed());
	}
	
	public void setCraftingStation(Tile tile, int x, int y) {
		if(tile instanceof CraftingStationTile) {
			CraftingStationTile csTile = (CraftingStationTile) tile;
			CraftingStation station = csTile.getStation();
			getSender().addPacket(new CraftingStationPacket(x, y, station));
			this.targetX = x;
			this.targetY = y;
			this.targetTile = tile;
		}
	}
	
	public void setOpenContainer(ChestTile tile, int x, int y) {
		if(tile instanceof ChestTile) {
			getSender().addPacket(new ChestInventoryPacket(x, y));
			this.targetX = x;
			this.targetY = y;
			this.targetTile = tile;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof PlayerConnection) {
			PlayerConnection in = (PlayerConnection) obj;
			
			if(!in.isInitialized() && !isInitialized()) {
				return obj==in;
			}
			
			if(in.getUsername().equals(getUsername())) {
				return true;
			}
		}
		return false;
	}
	
}
