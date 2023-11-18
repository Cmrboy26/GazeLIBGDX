package net.cmr.gaze.inventory;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.Logger;
import net.cmr.gaze.inventory.custom.AcornItem;
import net.cmr.gaze.inventory.custom.AnvilItem;
import net.cmr.gaze.inventory.custom.Apple;
import net.cmr.gaze.inventory.custom.BasicCircuit;
import net.cmr.gaze.inventory.custom.BasicGenerator;
import net.cmr.gaze.inventory.custom.BasicMotor;
import net.cmr.gaze.inventory.custom.BedItem;
import net.cmr.gaze.inventory.custom.BlastFurnaceItem;
import net.cmr.gaze.inventory.custom.Bread;
import net.cmr.gaze.inventory.custom.BrickCeilingItem;
import net.cmr.gaze.inventory.custom.BrickItem;
import net.cmr.gaze.inventory.custom.CampfireItem;
import net.cmr.gaze.inventory.custom.ChestItem;
import net.cmr.gaze.inventory.custom.ChuteItem;
import net.cmr.gaze.inventory.custom.ClayItem;
import net.cmr.gaze.inventory.custom.CoalGeneratorItem;
import net.cmr.gaze.inventory.custom.CoalItem;
import net.cmr.gaze.inventory.custom.CopperIngot;
import net.cmr.gaze.inventory.custom.CopperOreItem;
import net.cmr.gaze.inventory.custom.CopperWire;
import net.cmr.gaze.inventory.custom.Cotton;
import net.cmr.gaze.inventory.custom.CottonSeeds;
import net.cmr.gaze.inventory.custom.FurnaceItem;
import net.cmr.gaze.inventory.custom.Glass;
import net.cmr.gaze.inventory.custom.GrassSeeds;
import net.cmr.gaze.inventory.custom.IlmeniteOre;
import net.cmr.gaze.inventory.custom.IronAxe;
import net.cmr.gaze.inventory.custom.IronGear;
import net.cmr.gaze.inventory.custom.IronIngotItem;
import net.cmr.gaze.inventory.custom.IronOre;
import net.cmr.gaze.inventory.custom.IronPickaxe;
import net.cmr.gaze.inventory.custom.Magnet;
import net.cmr.gaze.inventory.custom.SandItem;
import net.cmr.gaze.inventory.custom.Silicon;
import net.cmr.gaze.inventory.custom.SolarPanelItem;
import net.cmr.gaze.inventory.custom.SteelIngot;
import net.cmr.gaze.inventory.custom.Stone;
import net.cmr.gaze.inventory.custom.StoneAxe;
import net.cmr.gaze.inventory.custom.StoneBrickCeilingItem;
import net.cmr.gaze.inventory.custom.StoneBrickFloorItem;
import net.cmr.gaze.inventory.custom.StoneBrickWallItem;
import net.cmr.gaze.inventory.custom.StoneHoe;
import net.cmr.gaze.inventory.custom.StonePathFloorItem;
import net.cmr.gaze.inventory.custom.StonePickaxe;
import net.cmr.gaze.inventory.custom.StoneShovel;
import net.cmr.gaze.inventory.custom.TableItem;
import net.cmr.gaze.inventory.custom.TechnologyTableItem;
import net.cmr.gaze.inventory.custom.TitaniumIngot;
import net.cmr.gaze.inventory.custom.TorchItem;
import net.cmr.gaze.inventory.custom.Wheat;
import net.cmr.gaze.inventory.custom.WheatSeeds;
import net.cmr.gaze.inventory.custom.Wood;
import net.cmr.gaze.inventory.custom.WoodAxe;
import net.cmr.gaze.inventory.custom.WoodCeilingItem;
import net.cmr.gaze.inventory.custom.WoodDoorItem;
import net.cmr.gaze.inventory.custom.WoodElectricityPoleItem;
import net.cmr.gaze.inventory.custom.WoodFloorItem;
import net.cmr.gaze.inventory.custom.WoodHoe;
import net.cmr.gaze.inventory.custom.WoodPickaxe;
import net.cmr.gaze.inventory.custom.WoodShovel;
import net.cmr.gaze.inventory.custom.WoodWallItem;
import net.cmr.gaze.inventory.custom.WoodWateringCan;

public class Items {

	private static HashMap<Integer, ItemType> identifierStorage = new HashMap<>();
	
	public enum ItemType {
		WOOD(Wood.class),
		STONE(Stone.class),
		IRON_INGOT(IronIngotItem.class),
		COPPER_INGOT(CopperIngot.class),
		STEEL_INGOT(SteelIngot.class),
		TITANIUM_INGOT(TitaniumIngot.class),

		IRON_GEAR(IronGear.class),
		COPPER_WIRE(CopperWire.class),
		MAGNET(Magnet.class),
		BASIC_CIRCUIT(BasicCircuit.class),
		BASIC_MOTOR(BasicMotor.class),
		BASIC_GENERATOR(BasicGenerator.class),
		
		COAL(CoalItem.class),
		IRON_ORE(IronOre.class),
		COPPER_ORE(CopperOreItem.class),
		ILEMITE_ORE(IlmeniteOre.class),

		SAND(SandItem.class),
		GLASS(Glass.class),
		CLAY(ClayItem.class),
		BRICK(BrickItem.class),
		SILICON(Silicon.class),

		WOOD_AXE(WoodAxe.class, 1),
		WOOD_PICKAXE(WoodPickaxe.class, 1),
		WOOD_SHOVEL(WoodShovel.class, 1),
		WOOD_HOE(WoodHoe.class, 1),
		WOOD_WATERING_CAN(WoodWateringCan.class, 1),
		STONE_PICKAXE(StonePickaxe.class, 1),
		STONE_AXE(StoneAxe.class, 1),
		STONE_SHOVEL(StoneShovel.class, 1),
		STONE_HOE(StoneHoe.class, 1),
		IRON_PICKAXE(IronPickaxe.class), 
		IRON_AXE(IronAxe.class),

		STEEL_DRILL(null),
		TITANIUM_DRILL(null),
		
		TABLE(TableItem.class, 4), 
		CHUTE(ChuteItem.class, 4), 
		FURNACE(FurnaceItem.class, 4), 
		CAMPFIRE(CampfireItem.class, 4), 
		CHEST(ChestItem.class, 4), 
		ANVIL(AnvilItem.class, 4),
		TECHNOLOGY_TABLE(TechnologyTableItem.class, 4),
		AGRICULTURE_TABLE(null),

		BLAST_FURNACE(BlastFurnaceItem.class, 4), 
		SOLAR_PANEL(SolarPanelItem.class, 4),
		COAL_GENERATOR(CoalGeneratorItem.class, 4),
		BASIC_MINING_DRILL(null),
		BASIC_STOVE(null),
		BASIC_CROP_BREEDER(null),
		MODERN_CROP_BREEDER(null),
		
		TORCH(TorchItem.class), 
		
		GRASS_SEEDS(GrassSeeds.class), 
		WHEAT_SEEDS(WheatSeeds.class), 
		COTTON_SEEDS(CottonSeeds.class),
		WHEAT(Wheat.class), 
		COTTON(Cotton.class), 
		BASIC_FERTILIZER(null),
		BREAD(Bread.class), 
		ACORN(AcornItem.class),
		APPLE(Apple.class),
		APPLE_PIE(null),
		
		WOOD_WALL(WoodWallItem.class), 
		WOOD_FLOOR(WoodFloorItem.class), 
		WOOD_DOOR(WoodDoorItem.class),
		WOOD_CEILING(WoodCeilingItem.class),
		STONE_PATH_FLOOR(StonePathFloorItem.class), 
		STONE_BRICK_WALL(StoneBrickWallItem.class), 
		STONE_BRICK_FLOOR(StoneBrickFloorItem.class), 
		STONE_BRICK_CEILING(StoneBrickCeilingItem.class),
		BRICK_CEILING(BrickCeilingItem.class), 
		BED(BedItem.class),
		
		WOOD_ELECTRICITY_POLE(WoodElectricityPoleItem.class);
		
		final int maxSize;
		final Class<? extends Item> clazz;
		private ItemType(Class<? extends Item> clazz, int maxSize) {
			addIdentifier();
			this.clazz = clazz;
			this.maxSize = maxSize;
		}
		private ItemType(Class<? extends Item> clazz) {
			addIdentifier();
			this.clazz = clazz;
			maxSize = 64;
		}
		private void addIdentifier() {
			if(identifierStorage.getOrDefault(getID(), null)!=null) {
				throw new NullPointerException("ID Conflict! Either the same name was used for a Item or there's a name().hashCode() conflict!");
			}
			identifierStorage.put(getID(), this);
		}
		public static ItemType getItemTypeFromID(int identifier) {
			ItemType end = identifierStorage.get(identifier);
			if(end == null) {
				throw new NullPointerException("Could not find ItemType for identifier "+identifier);
			}
			return end;
		}
		public static boolean itemIDExists(int identifier) {
			return identifierStorage.containsKey(identifier);
		}
		public int getID() {
			return name().hashCode();
		}
		public Class<? extends Item> getItemClass() {
			return clazz;
		}
		public int getMaxStackSize() {
			return maxSize;
		}
	}
	
	private static HashMap<ItemType, Item> map;
	public static HashMap<ItemType, String> nameMap, descriptionMap;
	private static boolean initialized = false;
	
	public static Item getItem(ItemType type, int size) {
		if(size>type.maxSize) {
			throw new RuntimeException("Max item size for item type "+type.name()+" cannot exceed "+type.maxSize+", attempted value: "+size);
		}
		Item result = map.getOrDefault(type, null);
		Item end = result.clone();
		end.set(size);
		return end;
	}
	
	public static Item getItem(ItemType type, DataInputStream input) throws IOException {
		int size = input.readInt();
		Item end = map.getOrDefault(type, null).readItem(input, type, size);
		return end;
	}
	
	public static void initialize() {
		if(initialized) {
			return;
		}
		map = new HashMap<>();
		nameMap = new HashMap<>();
		descriptionMap = new HashMap<>();
		
		for(int i = 0; i < ItemType.values().length; i++) {
			ItemType type = ItemType.values()[i];
			Logger.log("INFO", "["+(i+1)+"/"+ItemType.values().length+"] \tInitializing Item... "+type.name());
			Class<? extends Item> itemClass = type.getItemClass();
			try {
				for(Constructor<?> construct : itemClass.getConstructors()) {
					Parameter[] paramaters = construct.getParameters();
					if(paramaters.length==1) {
						Item item = (Item) construct.newInstance(1);
						map.put(type, item);
						break;
					} else {
						Item item = (Item) construct.newInstance();
						map.put(type, item);
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			writeEmptyLangFile();
			readLangFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(ItemType type : ItemType.values()) {
			if(!map.containsKey(type)) {
				Logger.error("DEVELOPMENT", "Items singleton does not contain an entry for ItemType "+type);
				//Gdx.app.exit();
			}
		}
		
		initialized = true;
	}
	
	public static void writeEmptyLangFile() throws IOException{
		File file = new File("items.txt");
		file.delete();
		file.createNewFile();
		FileWriter writer = new FileWriter(file);
		writer.write("DELETE THIS WHOLE LINE WHEN YOU SEE IT!! The first line under the ItemType is the name of the item, and the next line is the description of the line.\n");
		
		for(int i = 0; i < ItemType.values().length; i++) {
			ItemType type = ItemType.values()[i];
			writer.write(type.name()+"\n\t\n\t"+(i!=ItemType.values().length-1?"\n":""));
		}
		writer.flush();
		writer.close();
	}
	public static void readLangFile() throws IOException {
		if(!Gaze.singletonExists()) {
			return;
		}
		FileHandle handle = Gdx.files.internal("lang/items.txt");
		String string = handle.readString();
		Reader freader = new StringReader(string);
		BufferedReader reader = new BufferedReader(freader);

		String[] lines = string.split("\n");
		int index = 0;
		int lineLength = lines.length;
		System.out.println(index+","+lineLength);
		while(index<lineLength) {
			String itemTypeName = reader.readLine();
			String name = reader.readLine();
			String description = reader.readLine();
			if(itemTypeName==null||name==null||description==null) {
				break;
			}
			name = name.substring(1);
			description = description.substring(1);
			
			if(!ItemType.itemIDExists(itemTypeName.hashCode())) {
				Logger.log("ERROR", "["+((index+1)/3)+"/"+(lineLength/3)+"] ItemType with name \""+itemTypeName+"\" does not exist.");
			} else {
				Logger.log("INFO", "["+((index+1)/3)+"/"+(lineLength/3)+"] \tCreating Item Descriptions... "+itemTypeName);
				ItemType type = ItemType.getItemTypeFromID(itemTypeName.hashCode());
				nameMap.put(type, name);
				descriptionMap.put(type, description);
			}
			index+=3;
		}
		
		reader.close();
	}
	
}
