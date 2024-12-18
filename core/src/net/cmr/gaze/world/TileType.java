package net.cmr.gaze.world;

import net.cmr.gaze.world.tile.Air;
import net.cmr.gaze.world.tile.Anvil;
import net.cmr.gaze.world.tile.BasicConveyorTile;
import net.cmr.gaze.world.tile.BasicMiningDrill;
import net.cmr.gaze.world.tile.BasicPumpTile;
import net.cmr.gaze.world.tile.BedTile;
import net.cmr.gaze.world.tile.BlastFurnace;
import net.cmr.gaze.world.tile.Boiler;
import net.cmr.gaze.world.tile.BrickCeilingTile;
import net.cmr.gaze.world.tile.CampfireTile;
import net.cmr.gaze.world.tile.ChestTile;
import net.cmr.gaze.world.tile.ChuteTile;
import net.cmr.gaze.world.tile.ClayTile;
import net.cmr.gaze.world.tile.CoalGenerator;
import net.cmr.gaze.world.tile.CoalOreWall;
import net.cmr.gaze.world.tile.CopperOreWall;
import net.cmr.gaze.world.tile.CottonTile;
import net.cmr.gaze.world.tile.CrudeWindmill;
import net.cmr.gaze.world.tile.DirtTile;
import net.cmr.gaze.world.tile.FarmlandTile;
import net.cmr.gaze.world.tile.FurnaceTile;
import net.cmr.gaze.world.tile.GrassTile;
import net.cmr.gaze.world.tile.IronOreWall;
import net.cmr.gaze.world.tile.LavaTile;
import net.cmr.gaze.world.tile.SandTile;
import net.cmr.gaze.world.tile.SandstoneTile;
import net.cmr.gaze.world.tile.SolarPanel;
import net.cmr.gaze.world.tile.SteamEngine;
import net.cmr.gaze.world.tile.StoneBrickCeilingTile;
import net.cmr.gaze.world.tile.StoneBrickFloorTile;
import net.cmr.gaze.world.tile.StoneBrickWallTile;
import net.cmr.gaze.world.tile.StonePathFloorTile;
import net.cmr.gaze.world.tile.StoneTile;
import net.cmr.gaze.world.tile.StoneWall;
import net.cmr.gaze.world.tile.TableTile;
import net.cmr.gaze.world.tile.TallGrassTile;
import net.cmr.gaze.world.tile.TechnologyTableTile;
import net.cmr.gaze.world.tile.TorchTile;
import net.cmr.gaze.world.tile.Tree;
import net.cmr.gaze.world.tile.TreeSapling;
import net.cmr.gaze.world.tile.WaterTile;
import net.cmr.gaze.world.tile.WheatTile;
import net.cmr.gaze.world.tile.WoodCeilingTile;
import net.cmr.gaze.world.tile.WoodDoorTile;
import net.cmr.gaze.world.tile.WoodElectricityPole;
import net.cmr.gaze.world.tile.WoodFloorTile;
import net.cmr.gaze.world.tile.WoodWallTile;

// NOTE: cannot store the below white/black lists here as it will cause errors.
// (referencing enums before they're constructed inside of an enum)

public enum TileType {
	
	AIR(Air.class, 1, TickType.NONE),
	GRASS(GrassTile.class, 0, TickType.NONE),
	TREE(Tree.class, 1, TickType.NEARBY, 7, Replaceable.GENERATION),
	SAND(SandTile.class, 0, TickType.NONE), 
	WATER(WaterTile.class, 0, TickType.NONE),
	TABLE(TableTile.class, 1, TickType.NONE, 2), 
	DIRT(DirtTile.class, 0, TickType.NEARBY),
	TALL_GRASS(TallGrassTile.class, 1, TickType.NONE, Replaceable.ALWAYS), 
	STONE(StoneTile.class, 0, TickType.NONE), 
	LAVA(LavaTile.class, 0, TickType.NONE), 
	STONE_WALL(StoneWall.class, 1, TickType.NONE, 3, Replaceable.GENERATION), 
	CHUTE(ChuteTile.class, 1, TickType.NONE, 3), 
	TORCH(TorchTile.class, 1, TickType.NONE), 
	IRON_ORE_WALL(IronOreWall.class, 1, TickType.NONE, 4, Replaceable.GENERATION), 
	FURNACE(FurnaceTile.class, 1, TickType.NONE, 2),
	FARMLAND(FarmlandTile.class, 0, TickType.CONSTANT), 
	WHEAT(WheatTile.class, 1, TickType.NONE), 
	CAMPFIRE(CampfireTile.class, 1, TickType.NEARBY, 3),
	CHEST(ChestTile.class, 1, TickType.NONE, 3, Replaceable.NEVER), 
	WOOD_WALL(WoodWallTile.class, 1, TickType.NONE, 3),
	WOOD_FLOOR(WoodFloorTile.class, 0, TickType.NONE, 3),
	WOOD_DOOR(WoodDoorTile.class, 1, TickType.NONE, 3), 
	STONE_PATH_FLOOR(StonePathFloorTile.class, 0, TickType.NONE, 3),
	STONE_BRICK_WALL(StoneBrickWallTile.class, 1, TickType.NONE, 3), 
	STONE_BRICK_FLOOR(StoneBrickFloorTile.class, 0, TickType.NONE, 3), 
	ANVIL(Anvil.class, 1, TickType.NONE, 3), 
	TREE_SAPLING(TreeSapling.class, 1, TickType.NEARBY, 3, Replaceable.GENERATION), 
	WOOD_CEILING(WoodCeilingTile.class, 2, TickType.NONE, 2), 
	STONE_BRICK_CEILING(StoneBrickCeilingTile.class, 2, TickType.NONE, 2), 
	BRICK_CEILING(BrickCeilingTile.class, 2, TickType.NONE, 2),
	COPPER_ORE_WALL(CopperOreWall.class, 1, TickType.NONE, 4, Replaceable.GENERATION), 
	TECHNOLOGY_TABLE(TechnologyTableTile.class, 1, TickType.NONE, 3),
	COAL_ORE_WALL(CoalOreWall.class, 1, TickType.NONE, 4, Replaceable.GENERATION),
	WOOD_ELECTRICITY_POLE(WoodElectricityPole.class, 1, TickType.NONE, 3, Replaceable.NEVER), 
	BLAST_FURNACE(BlastFurnace.class, 1, TickType.CONSTANT, 3, Replaceable.NEVER), 
	SOLAR_PANEL(SolarPanel.class, 1, TickType.CONSTANT, 3, Replaceable.NEVER), 
	COAL_GENERATOR(CoalGenerator.class, 1, TickType.CONSTANT, 3, Replaceable.NEVER), 
	BED(BedTile.class, 1, TickType.NONE, 3, Replaceable.NEVER), 
	COTTON(CottonTile.class, 1, TickType.NONE), 
	SANDSTONE(SandstoneTile.class, 0, TickType.NEARBY), 
	CLAY(ClayTile.class, 0, TickType.NONE),
	STEAM_ENGINE(SteamEngine.class, 1, TickType.CONSTANT, 3, Replaceable.NEVER), 
	BASIC_CONVEYOR(BasicConveyorTile.class, 1, TickType.CONSTANT, 3, Replaceable.NEVER), 
	BASIC_PUMP(BasicPumpTile.class, 1, TickType.CONSTANT, 3, Replaceable.NEVER),
	BOILER(Boiler.class, 1, TickType.CONSTANT, 3, Replaceable.NEVER), 
	BASIC_MINING_DRILL(BasicMiningDrill.class, 1, TickType.CONSTANT, 3, Replaceable.NEVER), 
	CRUDE_WINDMILL(CrudeWindmill.class, 1, TickType.CONSTANT, 3, Replaceable.NEVER);
	
	public enum TickType {
		CONSTANT,
		NEARBY,
		NONE
	}
	
	public enum Replaceable {
		ALWAYS,
		GENERATION,
		NEVER;
	}
	
	public int layer;
	public TickType tickType;
	public float breakAmount;
	public Replaceable replaceable = Replaceable.NEVER;
	public Class<? extends Tile> clazz;
	
	private TileType(Class<? extends Tile> clazz, int layer, TickType type) {
		addIdentifier();
		this.clazz = clazz;
		this.layer = layer;
		this.tickType = type;
		this.breakAmount = 0;
	}
	
	private TileType(Class<? extends Tile> clazz, int layer, TickType type, int breakAmount) {
		addIdentifier();
		this.clazz = clazz;
		this.layer = layer;
		this.tickType = type;
		this.breakAmount = breakAmount;
	}
	
	private TileType(Class<? extends Tile> clazz, int layer, TickType type, Replaceable replaceable) {
		addIdentifier();
		this.clazz = clazz;
		this.layer = layer;
		this.tickType = type;
		this.breakAmount = 0;
		this.replaceable = replaceable;
	}
	
	private TileType(Class<? extends Tile> clazz, int layer, TickType type, float breakAmount, Replaceable replaceable) {
		addIdentifier();
		this.clazz = clazz;
		this.layer = layer;
		this.tickType = type;
		this.breakAmount = breakAmount;
		this.replaceable = replaceable;
	}
	
	public int getID() {
		return name().hashCode();
	}
	public static TileType getItemTypeFromID(int identifier) {
		TileType end = Tiles.identifierStorage.get(identifier);
		if(end == null) {
			throw new NullPointerException("Could not find ItemType for identifier "+identifier);
		}
		return end;
	}
	private void addIdentifier() {
		if(Tiles.identifierStorage.getOrDefault(getID(), null)!=null) {
			throw new NullPointerException("ID Conflict! Either the same name was used for a Tile or there's a name().hashCode() conflict!");
		}
		Tiles.identifierStorage.put(getID(), this);
	}
	
	
}
