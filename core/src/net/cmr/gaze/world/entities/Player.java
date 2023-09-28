package net.cmr.gaze.world.entities;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.FoodItem;
import net.cmr.gaze.inventory.Inventory;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Tool;
import net.cmr.gaze.leveling.Skills;
import net.cmr.gaze.leveling.Skills.Skill;
import net.cmr.gaze.leveling.SkillsPacket;
import net.cmr.gaze.networking.ConnectionPredicates.ConnectionPredicate;
import net.cmr.gaze.networking.PlayerConnection;
import net.cmr.gaze.networking.packets.FoodPacket;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.stage.widgets.QuestData;
import net.cmr.gaze.util.ArrayUtil;
import net.cmr.gaze.util.CustomMath;
import net.cmr.gaze.world.HealthEntityListener;
import net.cmr.gaze.world.HousingDoor;
import net.cmr.gaze.world.LightSource;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileData;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.World;

// TODO: Player Inventory data should only be sent when the player is within range

public class Player extends HealthEntity implements LightSource {
	
	//Player Health/Damage Data
	final float iFrameDefault = .5f;
	float invincibilityFrames = 0;

	// FOOD
	public static final float MAX_HUNGER = 20, MAX_SATURATION = 20;
	float hunger = MAX_HUNGER, saturation = MAX_SATURATION;

	// Player Game Data
	String username;
	Inventory inventory;
	int selectedHotbarSlot;
	boolean sprinting;
	Skills skills;
	QuestData questData;
	public long lastBreakInteraction;

	// Player Visual Data
	public static int AVAILABLE_PLAYER_TYPES = 0;
	int playerType = 0;
	
	// TODO: add functionality
	double spawnPointX, spawnPointY;
	String spawnPointWorld;
	
	/**
	 * This constructor should only be used in Entity reading and writing
	 */
	protected Player(HealthEntityListener listener) {
		super(EntityType.Player, listener);
		inventory = new Inventory(5*7);
		skills = new Skills();
	}
	
	/**
	 * This constructor should only be used in PlayerDisplayWidget rendering
	 * @param playerType the type of the player
	 */
	public Player(int playerType, HealthEntityListener listener) {
		super(EntityType.Player, listener);
		this.playerType = playerType;
	}
	
	public Player(String username, HealthEntityListener listener) {
		super(EntityType.Player, listener);
		this.username = username;
		inventory = new Inventory(5*7);
		//inventory.put(4, Items.getItem(ItemType.Stone, 6));
		skills = new Skills();
		this.questData = new QuestData();
	}

	public Player(String string, double x, double y, HealthEntityListener listener) {
		super(EntityType.Player, listener);
		this.username = string;
		inventory = new Inventory(5*7);
		setPosition(x, y);
		skills = new Skills();
		this.questData = new QuestData();
	}

	public String getUsername() {
		return username;
	}
	public Inventory getInventory() {
		return inventory;
	}
	
	// NOTE: player velocity will be constantly set to what the input is before every update method, so its up to the
	// update method to stop the player if they're supposed to be stopped
	@Override
	public void update(double deltaTime, TileData data) {
		super.update(deltaTime, data);

		if(data.isClient()) {
			Tile lastUnder = data.getTile(getLastTileX(), getLastTileY(), 0);
			Tile nowUnder = data.getTile(getTileX(), getTileY(), 0);
			if(lastUnder != null && nowUnder != null) {
				if(lastUnder.getType()==TileType.WATER ^ nowUnder.getType()==TileType.WATER) {
					if(lastUnder.getType()==TileType.WATER) {
						Gaze.get().playSoundCooldown("splash", 1f, .25f);
					} else {
						Gaze.get().playSoundCooldown("splash", .75f, .25f);
					}
				}
			}
			Tile lastAt = data.getTile(getLastTileX(), getLastTileY(), 1);
			Tile at = data.getTile(getTileX(), getTileY(), 1);
			if(at instanceof HousingDoor ^ lastAt instanceof HousingDoor) {
				Gaze.get().playSoundCooldown("tick", 1f, .25f);
			}
		} else {
			invincibilityFrames-=deltaTime;
			Tile nowUnder = data.getTile(getTileX(), getTileY(), 0);
			if(nowUnder != null && nowUnder.getType()==TileType.LAVA) {
				if(invincibilityFrames <= 0) {
					invincibilityFrames = iFrameDefault;
					damage(10);
					data.getServerData().playSound("hurt", 1, getTileX(), getTileY());
				}
			}
			hungerTick((float) deltaTime);
		}
		
	}
	
	long lastRender;
	
	float stateTime = 0;
	String lastAnimation;
	
	@Override
	public void render(Gaze game, GameScreen screen) {
		if(!getAnimationString().equals(lastAnimation)) {
			lastAnimation = getAnimationString();
			stateTime = 0;
		}
		if(!Objects.equals(screen.getLocalPlayer(), this)) {
			game.getFont(1.25f).draw(game.batch, getUsername(), (float) (getX()-Tile.TILE_SIZE/2), (float) (getY()+Tile.TILE_SIZE*1.15), Tile.TILE_SIZE, Align.center, false);
		}
		stateTime += Gdx.graphics.getDeltaTime();
		game.batch.draw(game.getAnimation(getAnimationString()).getKeyFrame(stateTime, true), (float) (getX()-Tile.TILE_SIZE/2-Tile.TILE_SIZE/2), (float) (getY()-Tile.TILE_SIZE/2), Tile.TILE_SIZE*2, Tile.TILE_SIZE*2);
		Item.draw(game, null, inventory.get(selectedHotbarSlot), game.batch, (float) (getX()-Tile.TILE_SIZE/4), (float) (getY()+Tile.TILE_SIZE)-Tile.TILE_SIZE/9, Tile.TILE_SIZE/2, Tile.TILE_SIZE/2, false);
		super.render(game, screen);
	}
	
	String lastDirection;
	
	public String getAnimationString() {
		
		float threshold = .25f;
		
		boolean moving = false;

		if(getVelocityY() >= threshold) {
			moving = true;
			lastDirection = "Up";
		} else if(getVelocityY() <= -threshold) {
			moving = true;
			lastDirection = "Down";
		}
		if(getVelocityX() >= threshold) {
			moving = true;
			lastDirection = "Right"; 
		} else if(getVelocityX() <= -threshold) {
			moving = true;
			lastDirection = "Left";
		}
		
		if(lastDirection==null) {
			lastDirection = "Down";
		}
		return "player"+(moving?"Walk":"Idle")+lastDirection+CustomMath.minMax(0, playerType, AVAILABLE_PLAYER_TYPES);
		
	}
	
	final int VERSION = 2;
	
	@Override
	public HealthEntity readHealthEntityData(DataInputStream input, boolean fromFile) throws IOException {
		int readVersion = input.readInt();
		boolean obfuscate = input.readBoolean();
		username = input.readUTF();
		if(!obfuscate) {
			inventory = Inventory.readInventory(input);
		}
		skills = Skills.readSkills(input);
		selectedHotbarSlot = input.readInt();
		playerType = input.readInt();
		if(readVersion >= 1) {
			questData = QuestData.read(input);
		}
		if(readVersion >= 2) {
			hunger = input.readFloat();
			saturation = input.readFloat();
		}
		return this;
	}
	
	@Override
	public void writeHealthEntity(DataBuffer buffer, boolean obfuscatePosition, boolean toFile) throws IOException {
		buffer.writeInt(VERSION);
		buffer.writeBoolean(obfuscatePosition);
		buffer.writeUTF(username);
		if(!obfuscatePosition) {
			inventory.writeInventory(buffer);
		}
		skills.writeSkills(buffer);
		buffer.writeInt(selectedHotbarSlot);
		buffer.writeInt(playerType);
		QuestData.write(questData, buffer);
		buffer.writeFloat(hunger);
		buffer.writeFloat(saturation);
	}

	public void setHotbarSlot(int slot) {
		selectedHotbarSlot = slot;
	}

	public int getHotbarSlot() {
		return selectedHotbarSlot;
	}
	
	public float getPickupRadius() {
		return 1.5f*Tile.TILE_SIZE;
	}
	public double getInteractRadius() {
		return 3;
	}
	
	public Rectangle getBoundingBox() {
		return new Rectangle((float) (getX()-Tile.TILE_SIZE/2+Tile.TILE_SIZE/6), (float) (getY()), Tile.TILE_SIZE-Tile.TILE_SIZE/3, Tile.TILE_SIZE/4);
	}
	
	@Override
	public Rectangle getBoundingBox(Entity entity, double x, double y) {
		Rectangle entityBox = entity.getBoundingBox();
		if(entityBox == null) {
			return entityBox;
		}
		entityBox.setPosition((float) x-Tile.TILE_SIZE/2f+Tile.TILE_SIZE/6, (float) y);
		return entityBox;
	} 
	
	public double getSpeed() {
		return 2*(sprinting?1.5d:1);
	}
	public void setSprinting(boolean sprinting) {
		this.sprinting = sprinting;
	}
	public double getBreakAmount(Tile tile) {
		Item held = getInventory().get(selectedHotbarSlot);
		if(!tile.isBreakable()) {
			return 0;
		}
		if(held != null) {
			if(held instanceof Tool) {
				Tool tool = (Tool) held;
				if(tool.toolType()==tile.getToolType()) {
					if(ArrayUtil.contains(tool.breakMaterials(), tile.getMaterial())) {
						if(tile.getBreakLevel() <= tool.breakLevel()) {
							return tool.breakStrength();
						}
						return 0;
					}
				}
			}
		}
		if(tile.getBreakLevel() > 0 && tile.getToolType()!=null) {
			return 0;
		}
		return 1;
	}
	public double getBreakSpeed() {
		Item held = getInventory().get(selectedHotbarSlot);
		if(held instanceof Tool) {
			return ((Tool)held).breakSpeed();
		}
		return 1;
	}

	public Item getHeldItem() {
		return inventory.get(getHotbarSlot());
	}

	public Skills getSkills() {
		return skills;
	}
	
	public void overrideSkills(Skills skills2) {
		this.skills = skills2;
	}
	
	public void addXP(World world, Skill skill, double xp) {
		skills.addXP(skill, xp);
		SkillsPacket packet = new SkillsPacket(skills, this);
		world.getServer().sendAllPacketIf(packet, ConnectionPredicate.PLAYER_IN_BOUNDS, getChunk(), world);
	}

	public QuestData getQuestData() {
		return questData;
	}
	
	float tick = 1;
	int lastTick = 0;
	int lastHunger = 0;

	public void hungerTick(float delta) {
		if((tick-=delta) <= 0) {
			tick = 1;
			float decreaseAmount = (new Vector2((float) getVelocityX(), (float)getVelocityY()).len()>.8f?.1f:0f)*(sprinting?1.75f:1f);
			System.out.println(decreaseAmount);
			if(saturation <= 0) {
				hunger-=decreaseAmount;
			}

			saturation-=decreaseAmount;

			hunger = CustomMath.minMax(0, hunger, MAX_HUNGER);
			saturation = CustomMath.minMax(0, saturation, MAX_SATURATION);
			System.out.println("Hunger: "+hunger+" Saturation: "+saturation);
			int tempHunger = (int) Math.floor(getHunger());
			if(lastHunger!=tempHunger) {
				lastHunger = tempHunger;
				PlayerConnection correspondingConnection = searchForPlayer(getUsername());
				if(correspondingConnection!=null) {
					correspondingConnection.getSender().addPacket(new FoodPacket(hunger, saturation));
				}
			}
		}
	}
	
	public void eatFood(FoodItem food) {
		hunger += food.getFoodPoints();
		saturation += food.getSaturationPoints();
		hunger = CustomMath.minMax(0, hunger, MAX_HUNGER);
		saturation = CustomMath.minMax(0, saturation, MAX_SATURATION);
		PlayerConnection correspondingConnection = searchForPlayer(getUsername());
		if(correspondingConnection!=null) {
			correspondingConnection.getSender().addPacket(new FoodPacket(hunger, saturation));
		}
		onEat(food);
	}

	private void onEat(FoodItem food) {
		System.out.println("Eating "+food.toString());
		System.out.println("Hunger: "+hunger);
		System.out.println("Saturation: "+saturation);
	}

	public PlayerConnection searchForPlayer(String username) {
		if(world==null) {
			return null;
		}
		for(PlayerConnection connection : world.getServer().connections.values()) {
			if(connection.getPlayer().getUsername().equals(username)) {
				return connection;
			}
		}
		return null;
	}

	public float getHunger() {
		return hunger;
	}

	public float getSaturation() {
		return saturation;
	}

	@Override
	public float getIntensity() {
		
		if(getHeldItem() instanceof LightSource) {
			return ((LightSource)getHeldItem()).getIntensity();
		}
		
		return 2;
	}
	
	@Override
	public void onDeath() {
		getInventory().clear();
		if(getWorld()!=null) {
			getWorld().getServer().connections.get(getUsername()).inventoryChanged(true);
		}
		setHealth(getMaxHealth());
	}
	
	@Override
	public float offsetY() {
		return Tile.TILE_SIZE/2;
	}
	
	public double getSpawnPointX() {
		return spawnPointX;
	}
	public double getSpawnPointY() {
		return spawnPointY;
	}
	
	public void setPlayerType(int playerType) {
		this.playerType = playerType;
	}

	
}
