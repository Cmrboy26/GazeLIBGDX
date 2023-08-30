package net.cmr.gaze.world;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.math.Rectangle;

import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Placeable;
import net.cmr.gaze.networking.ConnectionPredicates.ConnectionPredicate;
import net.cmr.gaze.networking.GameServer;
import net.cmr.gaze.networking.PlayerConnection;
import net.cmr.gaze.networking.packets.AudioPacket;
import net.cmr.gaze.networking.packets.ChunkDataPacket;
import net.cmr.gaze.networking.packets.ChunkUnloadPacket;
import net.cmr.gaze.networking.packets.DespawnEntity;
import net.cmr.gaze.networking.packets.EntityPositionsPacket;
import net.cmr.gaze.networking.packets.HotbarUpdatePacket;
import net.cmr.gaze.networking.packets.InventoryUpdatePacket;
import net.cmr.gaze.networking.packets.PlayerInteractPacket;
import net.cmr.gaze.networking.packets.SpawnEntity;
import net.cmr.gaze.networking.packets.TileUpdatePacket;
import net.cmr.gaze.networking.packets.WorldChangePacket;
import net.cmr.gaze.util.ArrayUtil;
import net.cmr.gaze.util.CustomMath;
import net.cmr.gaze.util.Normalize;
import net.cmr.gaze.util.Vector2Double;
import net.cmr.gaze.world.TileType.Replaceable;
import net.cmr.gaze.world.entities.Entity;
import net.cmr.gaze.world.entities.NPC;
import net.cmr.gaze.world.entities.Player;

public class World {

	public static final int SIMULATION_DISTANCE = 2;
	
	public ConcurrentHashMap<Point, Chunk> chunkList;
	public TileData tileData;
	
	double seed;
	double worldTime;
	
	private String worldName;
	private ArrayList<PlayerConnection> players;
	private GameServer server;
	private WorldGenerator generator;
	
	
	public World(WorldGenerator generator, GameServer server, String worldName, double seed) {
		this(server, worldName, seed);
		this.generator = generator;
	}
	
	public World(GameServer server, String worldName, double seed) {
		this.players = new ArrayList<>();
		this.seed = new Random((long) seed).nextDouble()*Short.MAX_VALUE;
		this.worldName = worldName;
		this.chunkList = new ConcurrentHashMap<>();
		this.server = server;
		this.tileData = new TileData(this);
	}
	
	public Chunk getChunk(Point chunkCoordinate) {
		return getChunk(chunkCoordinate, false);
	}
	
	public Chunk getChunk(Point chunkCoordinate, boolean disableGeneration) {
		Chunk at = chunkList.get(chunkCoordinate);
		boolean add = false;
		if(at == null) {
			at = new Chunk(this, chunkCoordinate);
			add = true;
			chunkList.put(chunkCoordinate, at);
		}
		if(disableGeneration) {
			return at;
		}
		if(!at.isGenerated()) {
			at.generate();
		}
		return at;
	}

	double playerTime;
	double updateTime;
	double updateTileTile;
	
	int debugIteration;
	
	public void update(double delta) {
		long now = System.nanoTime();
		HashSet<Point> loadedChunks = new HashSet<>();
		
		updateTime+=delta;
		updateTileTile+=delta;
		playerTime+=delta;
		worldTime+=delta;
		
		//int loop = (int) (playerTime/Entity.DELTA_TIME);
		
		for(int i = 0; i < players.size(); i++) {
			PlayerConnection connection = players.get(i);
			connection.update();
			Player player = connection.getPlayer();
			Point chunk = player.getChunk();
			for(int x = -World.SIMULATION_DISTANCE; x <= World.SIMULATION_DISTANCE; x++) {
				for(int y = -World.SIMULATION_DISTANCE; y <= World.SIMULATION_DISTANCE; y++) {
					loadedChunks.add(new Point(x+chunk.x, y+chunk.y));
				}
			}
			//for(int y = 0; y < loop; y++) {
				connection.setPlayerMovement();
				player.update(delta, tileData);
				sendNeededChunks(connection);
				connection.processPositionPacket();
				sendNeededChunks(connection);
			//}
			//int amount = 0;
			if(connection.getInteractEvents().size()>0) {
				for(PlayerInteractPacket interact = connection.getInteractEvents().get(0);
						connection.getInteractEvents().size() > 0; 
						connection.getInteractEvents().remove(0)) {
					
					/*
					 * TODO: make an effective rate limiter 
					amount++;
					if(amount >= 5) {
						connection.disconnect(GameServer.DISCONNECT_RATE_LIMIT);
					}*/
					
					int x = (int) Math.floor(interact.getWorldX()/Tile.TILE_SIZE);
					int y = (int) Math.floor(interact.getWorldY()/Tile.TILE_SIZE);
					
					if(player.getDistanceToTile(x, y) < player.getInteractRadius()) {
						
						if(interact.getClickType()==2) {
							Item held = player.getInventory().get(player.getHotbarSlot());
							if(held instanceof Placeable) {
								Placeable placeable = (Placeable) held;
								int rotation = CustomMath.minMax(0,interact.getModifier(),3);
								Tile tile = placeable.getPlaceTile(rotation);
								Tile at = getTile(x, y, tile.getType().layer);
								if(at!=null && at.getType()==tile.getType()) {
									continue;
								}
								
								Rectangle tileBoundingBox = tile.getBoundingBox(x, y);
								boolean doNotPlace = false;
								if(tileBoundingBox!=null) {
									for(PlayerConnection c : players) {
										Entity e = c.getPlayer();
										Rectangle bounds = e.getBoundingBox(e, e.getX(), e.getY());
										if(bounds == null) {
											continue;
										}
										
										if(bounds.overlaps(tileBoundingBox)) {
											doNotPlace = true;
											break;
										}
									}
									for(int vx = -1; vx <= 1 && !doNotPlace; vx++) {
										for(int vy = -1; vy <= 1 && !doNotPlace; vy++) {
											Point chunkCoordinate = new Point(Chunk.getChunk(vx, vy));
											chunkCoordinate.translate(vx, vy);
											Chunk c = getChunk(chunkCoordinate, true);
											for(Entity e : c.getEntities()) {
												Rectangle bounds = e.getBoundingBox(e, e.getX(), e.getY());
												if(bounds == null) {
													continue;
												}
												if(bounds.overlaps(tileBoundingBox)) {
													doNotPlace = true;
													break;
												}
											}
										}
									}
								}
								if(!doNotPlace) {
									boolean placed = addTile(tile, x, y, true);
									if(placed) {
										tile.onPlace(this, x, y, player);
										player.getInventory().remove(Items.getItem(held.getType(), 1), player.getHotbarSlot());
										playSound(placeable.getPlaceAudio(), 1f, x, y);
										connection.inventoryChanged(true);
										continue;
									}
								}
							}
						}
						if(getChunk(Chunk.getChunk(x, y), true) != null) {
							for(int z = Chunk.LAYERS-1; z >= 0; z--) {
								Tile at = getTile(x, y, z);
								if(at == null) {
									continue;
								}
								
								boolean successfulInteract = at.onInteract(connection, this, x, y, interact.getClickType());
								if(successfulInteract) {
									break;
								}
							}
						}
					}
					
					
				}
			
				
			}
		}
		
		//playerTime-=Entity.DELTA_TIME*loop;
		
		/*for(PlayerConnection changedChunkConnection : newChunkConnections) {
			Player player = changedChunkConnection.getPlayer();
			Point beforeMovement = player.getLastChunk();
			Point afterMovement = player.getChunk();
			InventoryUpdatePacket sendToNewNearbyPlayers = new InventoryUpdatePacket(player);
			for(PlayerConnection temp : players) {
				if(temp.equals(changedChunkConnection)) {
					continue;
				}
				boolean evaluation = server.evaluatePredicate(temp, ConnectionPredicate.PLAYER_NOW_IN_BOUNDS, afterMovement, beforeMovement);
				if(evaluation) {
					temp.getSender().addPacket(sendToNewNearbyPlayers);
				}
			}
		}
		newChunkConnections.clear();
		*/
		
		
		
		
		while(updateTime>=Entity.DELTA_TIME) {
			updateTime-=Entity.DELTA_TIME;
			boolean b = updateTileTile>Tile.DELTA_TIME;
			for(Point chunkCoordinate : chunkList.keySet()) {
				Chunk chunk = chunkList.get(chunkCoordinate);
				
				
				chunk.update(loadedChunks.contains(chunkCoordinate), b);
			}
			if(b) {
				updateTileTile = 0;
			}
			for(PlayerConnection playerConnection : players) {
				ArrayList<Entity> endEntities = new ArrayList<>();
				ArrayList<Chunk> playerChunks = getPlayerLoadedChunks(playerConnection);
				
				for(PlayerConnection tempConnection : players) {
					if(server.evaluatePredicate(playerConnection, ConnectionPredicate.PLAYER_IN_BOUNDS, tempConnection.getPlayer().getChunk(), this)) {
						endEntities.add(tempConnection.getPlayer());
					}
				}
				
				for(Chunk c : playerChunks) {
					if(c!=null) {
						endEntities.addAll(c.getEntities());
					}
				}
				playerConnection.getSender().addPacket(new EntityPositionsPacket(endEntities));
			}
		}
		
		loadedChunks.clear();
		loadedChunks = null;
	}
	
	public boolean addTile(Tile t, int x, int y, boolean countReplacables) {
		return addTile(t, x, y, false, countReplacables);
	}
	
	public boolean addTile(Tile t, int x, int y) {
		return addTile(t, x, y, false, true);
	}
	
	public boolean generateTile(Chunk c, Tile t, int x, int y) {
		return addTile(t, x, y, true, true);
	}
	
	/**
	 * NOTE: this should be used SPARINGLY, as it only directly changes the tile and doesn't have any structure functionality
	 */
	public void setTile(Tile t, int tilex, int tiley) {
		getChunk(Chunk.getChunk(tilex, tiley), true).setTile(t, Chunk.getInsideChunkCoordinates(tilex, tiley), t.getType().layer);
	}
	
	public boolean addTile(Tile t, int tilex, int tiley, boolean disableGenerate, boolean countReplacables) {
		getChunk(Chunk.getChunk(tilex, tiley), disableGenerate);
		if(t instanceof BaseTile) {
			BaseTile base = (BaseTile) t;
			int width = base.width;
			int height = base.height;
			for(int x = 0; x<width; x++) {
				for(int y = 0; y<height; y++) {
					if(!isValidPlacement(base, tilex+x, tiley+y, countReplacables)) {
						return false;
					} else if(countReplacables) {
						Tile at = getTile(tilex+x, tiley+y, t.getType().layer);
						if(at!=null&&at.getReplaceability()==Replaceable.ALWAYS) {
							removeTile(tilex, tiley, at.getType().layer);
						}
					}
				}
			}
			for(int x = 0; x<width; x++) {
				for(int y = 0; y<height; y++) {
					if((x==0&&y==0)) {
						getChunk(Chunk.getChunk(tilex, tiley), disableGenerate).setTile(t, Chunk.getInsideChunkCoordinates(tilex, tiley), t.getType().layer);
						if(!disableGenerate) onTileChange(tilex, tiley, t.getType().layer);
					} else {
						addTile(new StructureTile(base.getType(), x, y), tilex+x, tiley+y, disableGenerate, countReplacables);
					}
				}
			}
		} else if(t instanceof FloorTile) { 
			Tile at = getTile(tilex, tiley, t.getType().layer);
			Tile above = getTile(tilex, tiley, t.getType().layer+1);
			if(at instanceof FloorTile) {
				return false;
			}
			if(above != null && ((ArrayUtil.contains(above.belowBlacklist(), t)||!ArrayUtil.contains(above.belowWhitelist(), t)))) {
				
				if(above.belowBlacklist()!=null&&ArrayUtil.contains(above.belowBlacklist(), t.getType())) {
					return false;
				}
				if(above.belowWhitelist()!=null&&!ArrayUtil.contains(above.belowWhitelist(), t.getType())) {
					return false;
				}
			}
			
			if(t.belowBlacklist()!=null&&ArrayUtil.contains(t.belowBlacklist(), at.getType())) {
				// below tile is blacklisted
				return false;
			}
			if(t.belowWhitelist()!=null&&!ArrayUtil.contains(t.belowWhitelist(), at.getType())) {
				// below tile is not whitelisted
				return false;
			}
			
			((FloorTile)t).setUnderTile(at);
			getChunk(Chunk.getChunk(tilex, tiley), disableGenerate).setTile(t, Chunk.getInsideChunkCoordinates(tilex, tiley), t.getType().layer);
			if(!disableGenerate) onTileChange(tilex, tiley, t.getType().layer);
		} else {
			Tile at = getTile(tilex, tiley, t.getType().layer);
			
			if(isValidPlacement(t, tilex, tiley, countReplacables)) {
				
				if(at!=null&&at.getReplaceability()==Replaceable.ALWAYS&&countReplacables) {
					removeTile(tilex, tiley, at.getType().layer);
				}
				
				if(at!=null&&at.getType()==t.getType()) {
					return false;
				}				
				
				getChunk(Chunk.getChunk(tilex, tiley), disableGenerate).setTile(t, Chunk.getInsideChunkCoordinates(tilex, tiley), t.getType().layer);
				if(!disableGenerate) onTileChange(tilex, tiley, t.getType().layer);
				
				if(t.getType().layer == 0) {
					Tile above = getTile(tilex, tiley, 1);
					if(above != null) {
						
						if(above instanceof StructureTile) {
							above = ((StructureTile)above).getBaseTile(this, tilex, tiley);
						}
						
						if(!isValidPlacement(above, tilex, tiley, countReplacables)) {
							removeTile(tilex, tiley, 1);
						}
					}
				}
				
			} else {
				return false;
			}
		}
		return true; 
	}
	
	public Tile getTile(int x, int y, int layer) {
		Chunk chunk = getChunk(Chunk.getChunk(x,y), true);
		return chunk.getTile(Chunk.getInsideChunkCoordinates(x,y), layer);
	}
	
	public void removeTile(int tilex, int tiley, int layer) {
		removeTile(tilex, tiley, layer, false);
	}
	
	public void removeTile(int tilex, int tiley, int layer, boolean breakTile) {
		Tile at = getTile(tilex, tiley, layer);
		
		if(at instanceof BaseTile || at instanceof StructureTile) {
			
			BaseTile base;
			int baseX, baseY;
			
			if(at instanceof StructureTile) {
				StructureTile struct = (StructureTile) at;
				Tile temp = getTile(tilex-struct.x, tiley-struct.y, layer);
				if(temp instanceof BaseTile) {
					base = (BaseTile) temp;
				} else {
					return;
				}
				baseX = tilex-struct.x;
				baseY = tiley-struct.y;
			} else {
				base = (BaseTile) at;
				baseX = tilex;
				baseY = tiley;
			}
			
			for(int x = 0; x < base.width; x++) {
				for(int y = 0; y < base.height; y++) {
					if(breakTile) {
						if(x == 0 && y == 0) {
							if(at != null) {
								base.onBreak(this, null, x, y);
							}
						}
					}
					getChunk(Chunk.getChunk(baseX+x, baseY+y), true).setTile(null, Chunk.getInsideChunkCoordinates(baseX+x, baseY+y), layer);
					onTileChange(baseX+x, baseY+y, layer);
				}
			}
			//addEntity(new DroppedItem(Items.getItem(ItemType.Wood, 3), tilex, tiley));
		} else {
			if(breakTile) {
				if(at != null) {
					at.onBreak(this, null, tilex, tiley);
				}
			}
			getChunk(Chunk.getChunk(tilex, tiley), true).setTile(null, Chunk.getInsideChunkCoordinates(tilex, tiley), layer);
			onTileChange(tilex, tiley, layer);
		}
	}
	
	public boolean isValidPlacement(Tile t, int x, int y, boolean countReplacables) {
		if(t.getType().layer == 0) {
			return true;
		}
		Tile at = getTile(x, y, t.getType().layer);
		Tile below = getTile(x, y, t.getType().layer-1);
		if(below == null) {
			return false;
		}
		if(at != null) {
			if(!(at.getReplaceability()==Replaceable.ALWAYS&&countReplacables)) {
				return false;
			}
		}
		if(t.belowBlacklist()!=null&&ArrayUtil.contains(t.belowBlacklist(), below.getType())) {
			// below tile is blacklisted
			return false;
		}
		if(t.belowWhitelist()!=null&&!ArrayUtil.contains(t.belowWhitelist(), below.getType())) {
			// below tile is not whitelisted
			return false;
		}
		return true;
	}
	
	public void onTileChange(int x, int y, int layer) {
		TileUpdatePacket packet = new TileUpdatePacket(getTile(x, y, layer), x, y, layer);
		for(PlayerConnection player : players) {
			server.sendAllPacketIf(packet, ConnectionPredicate.PLAYER_IN_BOUNDS, Chunk.getChunk(x, y), this);
			//player.getSender().addPacket(new TileUpdatePacket(getTile(new Point(x, y), layer), x, y, layer));
		}
	}
	
	public double getSeed() {
		return seed;
	}
	
	public void addPlayer(PlayerConnection connection) {
		if(connection.getPlayer().getWorld()==this) {
			return;
		} else {
			if(connection.getPlayer().getWorld()!=null) {
				connection.getPlayer().getWorld().removePlayer(connection);
			}
		}
		
		if(!players.contains(connection)) {
			players.add(connection);
			sendWorldData(connection);
		}
		connection.getPlayer().setWorld(this);
		for(PlayerConnection temp : players) {
			if(temp.equals(connection)) {
				continue;
			}
			boolean inBounds = server.evaluatePredicate(temp, ConnectionPredicate.PLAYER_IN_BOUNDS, connection.getPlayer().getChunk(), this);
			// will obfuscate the position of the player if it is out of range (so people cant tell where everyone is at any given time)
			temp.getSender().addPacket(new SpawnEntity(connection.getPlayer(), inBounds));
			if(inBounds) {
				temp.getSender().addPacket(new HotbarUpdatePacket(connection.getPlayer().getUUID(), (byte) connection.getPlayer().getHotbarSlot()));
			}
		}
	}
	
	public void removePlayer(PlayerConnection connection) {
		players.remove(connection);
		for(PlayerConnection temp : players) {
			if(temp.equals(connection)) {
				continue;
			}
			temp.getSender().addPacket(new DespawnEntity(connection.getPlayer()));
		}
		connection.getPlayer().setWorld(null);
	}
	
	/**
	 * Will clear the connection's current world data on the player's end and replace it
	 * with the current world's content. Similar to Minecraft's F3+A. Should be called to 
	 * initially send over player data on join as well.
	 * @param connection
	 */
	public void sendWorldData(PlayerConnection connection) {
		connection.getSender().addPacket(new WorldChangePacket(this));
		for(int x = connection.getPlayer().getChunk().x-1; x <= connection.getPlayer().getChunk().x+1; x++) {
			for(int y = connection.getPlayer().getChunk().y-1; y <= connection.getPlayer().getChunk().y+1; y++) {
				Point temp = new Point(x, y);
				Chunk at = getChunk(temp);
				connection.getSender().addPacket(new ChunkDataPacket(at));
				temp = null;
			}
		}
		for(PlayerConnection tconnection : players) {
			Player player = tconnection.getPlayer();
			connection.getSender().addPacket(new SpawnEntity(player));
		}
	}

	public void updateEntityChunk(Entity entity) {
		updateEntityChunk(entity, false);
	}
	
	public void updateEntityChunk(Entity entity, boolean add) {
		//System.out.println("changed entity thing");
		Point newChunk = entity.getChunk();
		getChunk(newChunk, true).addEntity(entity);
		// if the entity's current chunk in loading distance of player AND its previous position was not in the loading distance of the player, send spawnEntity
		SpawnEntity packet = new SpawnEntity(entity);
		
		for(PlayerConnection conneciton : players) {
			Player player = conneciton.getPlayer();
			boolean inLoading = server.evaluatePredicate(conneciton, ConnectionPredicate.PLAYER_IN_BOUNDS, entity.getChunk(), this);
			boolean previouslyInLoading = new Vector2Double(player.getLastChunk()).chebyshev(new Vector2Double(entity.getLastChunk())).intValue()<=1;
			if(inLoading && (!previouslyInLoading||add)) {
				conneciton.getSender().addPacket(packet);
			}
		}
		
	}
	
	public void playSound(String noise, float volume, int x, int y) {
		server.sendAllPacketIf(new AudioPacket(noise, 1f), ConnectionPredicate.PLAYER_IN_BOUNDS, Chunk.getChunk(x, y), this);
	}
	
	public void addEntity(Entity entity) {
		updateEntityChunk(entity, true);
		entity.setWorld(this);
	}
	public void removeEntity(Entity entity) {
		getChunk(entity.getChunk(), false).removeEntity(entity);
		server.sendAllPacketIf(new DespawnEntity(entity), ConnectionPredicate.PLAYER_IN_BOUNDS, entity.getChunk(), this);
	}
	
	public ArrayList<Chunk> getPlayerLoadedChunks(PlayerConnection connection) {
		ArrayList<Chunk> chunks = new ArrayList<>();
		for(int x = connection.getPlayer().getChunk().x-1; x <= connection.getPlayer().getChunk().x+1; x++) {
			for(int y = connection.getPlayer().getChunk().y-1; y <= connection.getPlayer().getChunk().y+1; y++) {
				chunks.add(this.chunkList.get(new Point(x, y)));
			}
		}
		return chunks;
	}
	
	public void sendNeededChunks(PlayerConnection connection) {
		Player player = connection.getPlayer();
		Point beforeMovement = player.getLastChunk();
		Point afterMovement = player.getChunk();
		if(!player.getLastChunk().equals(player.getChunk())) {
			
			Point chunkChange = new Point(afterMovement.x-beforeMovement.x,afterMovement.y-beforeMovement.y);
			//connection.sendOutgoingObject(new SetCenterChunkPacket(currentChunk));
			
			InventoryUpdatePacket sendToNewNearbyPlayers = new InventoryUpdatePacket(player);
			HotbarUpdatePacket sendHotbar = new HotbarUpdatePacket(player.getUUID(), (byte) player.getHotbarSlot());
			//newChunkConnections.add(connection);
			for(PlayerConnection tempConnection : players) {
				
				boolean evaluation = server.evaluatePredicate(tempConnection, ConnectionPredicate.PLAYER_NOW_IN_BOUNDS, afterMovement, beforeMovement, this);
				if(evaluation) {
					tempConnection.getSender().addPacket(sendToNewNearbyPlayers);
					tempConnection.getSender().addPacket(sendHotbar);
					connection.getSender().addPacket(new InventoryUpdatePacket(tempConnection.getPlayer()));
					connection.getSender().addPacket(new HotbarUpdatePacket(tempConnection.getPlayer().getUUID(), (byte) tempConnection.getPlayer().getHotbarSlot()));
				}
			}
			
			int renderDistance = 1;
			if(chunkChange.x != 0) {
				for(int y = -renderDistance; y <= renderDistance; y++) {
					Chunk loadChunk = getChunk(new Point((int) (afterMovement.x+renderDistance*Normalize.norm(chunkChange.x)), afterMovement.y+y));
					connection.getSender().addPacket(new ChunkDataPacket(loadChunk));
					Point unloadPoint = new Point((int) (afterMovement.x-Normalize.norm(chunkChange.x)-renderDistance*Normalize.norm(chunkChange.x)), afterMovement.y+y);
					connection.getSender().addPacket(new ChunkUnloadPacket(unloadPoint));
				}
			}
			if(chunkChange.y != 0) {
				for(int x = -renderDistance; x <= renderDistance; x++) {
					connection.getSender().addPacket(new ChunkDataPacket(getChunk(new Point(afterMovement.x+x, (int) (afterMovement.y+renderDistance*Normalize.norm(chunkChange.y))))));
					connection.getSender().addPacket(new ChunkUnloadPacket(new Point(afterMovement.x+x, (int) (afterMovement.y-Normalize.norm(chunkChange.y)-renderDistance*Normalize.norm(chunkChange.y)))));
				}
			}
		}
	}
	
	public String getWorldName() {
		return worldName;
	}
	/*public UUID getWorldUUID() {
		return worldID;
	}*/
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof World) {
			World world = (World) obj;
			if(world.getWorldName().equals(getWorldName())) {
				return true;
			}
		}
		return super.equals(obj);
	}

	public ArrayList<PlayerConnection> getPlayers() {
		return players;
	}
	public double getWorldTime() {
		return worldTime;
	}
	
	public int hashCode() {
		return Objects.hash(worldName);
	}

	public WorldGenerator getGenerator() {
		return generator;
	}
	public void setGenerator(WorldGenerator generator) {
		this.generator = generator;
	}
	public GameServer getServer() {
		return server;
	}
	
	
}
