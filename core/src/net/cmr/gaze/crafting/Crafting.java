package net.cmr.gaze.crafting;

import java.util.HashMap;

import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.leveling.Skills.Skill;
import net.cmr.gaze.world.entities.Player;

public class Crafting {

	private static boolean initialized;
	private static HashMap<String, RecipeCategory> categories;
	
	public enum CraftingStation {
		NONE("craftingLeft", "craftingRight"),
		TABLE("craftingLeft", "craftingRight"), 
		FURNACE("craftingLeft", "craftingRight"), 
		CAMPFIRE("craftingLeft", "craftingRight"), 
		ANVIL("craftingLeft", "craftingRight"),
		TECHNOLOGY_TABLE("craftingLeft", "craftingRight", true), 
		BLAST_FURNACE("craftingLeft", "craftingRight", true);
		
		public String leftDisplayName, rightDisplayName;
		public boolean onlyShowThisStation = false; 
		
		private CraftingStation() {
			this.leftDisplayName = "crafting"+name()+"Left";
			this.rightDisplayName = "crafting"+name()+"Right";
			this.onlyShowThisStation = false;
		}
		private CraftingStation(String leftDisplayName, String rightDisplayName) {
			this.leftDisplayName = leftDisplayName;
			this.rightDisplayName = rightDisplayName;
			this.onlyShowThisStation = false;
		}
		private CraftingStation(String leftDisplayName, String rightDisplayName, boolean onlyShowThisStation) {
			this.leftDisplayName = leftDisplayName;
			this.rightDisplayName = rightDisplayName;
			this.onlyShowThisStation = onlyShowThisStation;
		}
		
	}
	
	public static abstract class CraftingRequirement {
		
		public abstract boolean canCraft(Recipe recipe, Player player);
		
	}

	public static class LevelRequirement extends CraftingRequirement {
		
		Skill skill;
		int requiredLevel;
		
		public LevelRequirement(Skill skill, int requiredLevel) {
			this.skill = skill;
			this.requiredLevel = requiredLevel;
		}

		@Override
		public boolean canCraft(Recipe recipe, Player player) {
			int level = player.getSkills().getLevel(skill);
			if(level<requiredLevel) {
				return false;
			}
			return true;
		}
		
	}

	public static class ResearchRequirement extends CraftingRequirement {
		
		String universalID;
		
		public ResearchRequirement(String universalID) {
			this.universalID = universalID;
		}

		@Override
		public boolean canCraft(Recipe recipe, Player player) {
			return player.getResearchData().isResearched(universalID);
		}
		
	}
	
	private static LevelRequirement level(Skill skill, int req) {
		return new LevelRequirement(skill, req);
	}
	private static ResearchRequirement research(String universalID) {
		return new ResearchRequirement(universalID);
	}
	
	public static void initialize() {
		if(initialized) {
			return;
		}
		
		categories = new HashMap<>();
		initializeRecipeCategory("Tools", Items.getItem(ItemType.STONE_AXE, 1));
		initializeRecipeCategory("Materials", Items.getItem(ItemType.IRON_INGOT, 1));
		initializeRecipeCategory("Appliances", Items.getItem(ItemType.TABLE, 1));
		initializeRecipeCategory("Farming", Items.getItem(ItemType.WOOD_WATERING_CAN, 1));
		initializeRecipeCategory("Housing", Items.getItem(ItemType.BED, 1));
		
		initializeRecipe("Materials", "brick", CraftingStation.FURNACE, new ItemType[] {ItemType.CLAY}, new int[] {1}, new ItemType[] {ItemType.BRICK}, new int[] {1}, research("gaze:resources.clayProcessing"));
		initializeRecipe("Materials", "glass", CraftingStation.FURNACE, new ItemType[] {ItemType.SAND}, new int[] {2}, new ItemType[] {ItemType.GLASS}, new int[] {1}, research("gaze:resources.sandProcessing"));
		initializeRecipe("Materials", "silicon", CraftingStation.BLAST_FURNACE, new ItemType[] {ItemType.STONE, ItemType.SAND}, new int[] {2, 2}, new ItemType[] {ItemType.SILICON}, new int[] {1}, research("gaze:resources.siliconProcessing"));
		initializeRecipe("Materials", "ironIngot", CraftingStation.FURNACE, new ItemType[] {ItemType.IRON_ORE}, new int[] {1}, new ItemType[] {ItemType.IRON_INGOT}, new int[] {1});
		initializeRecipe("Materials", "ironIngotBlast", CraftingStation.BLAST_FURNACE, new ItemType[] {ItemType.IRON_ORE}, new int[] {2}, new ItemType[] {ItemType.IRON_INGOT}, new int[] {3});
		initializeRecipe("Materials", "copperIngot", CraftingStation.FURNACE, new ItemType[] {ItemType.COPPER_ORE}, new int[] {1}, new ItemType[] {ItemType.COPPER_INGOT}, new int[] {1});
		initializeRecipe("Materials", "copperIngotBlast", CraftingStation.BLAST_FURNACE, new ItemType[] {ItemType.COPPER_ORE}, new int[] {2}, new ItemType[] {ItemType.COPPER_INGOT}, new int[] {3});
		initializeRecipe("Materials", "ironGear", CraftingStation.TECHNOLOGY_TABLE, new ItemType[] {ItemType.IRON_INGOT}, new int[] {1}, new ItemType[] {ItemType.IRON_GEAR}, new int[] {3}, research("gaze:machinery.gears"));
		initializeRecipe("Materials", "copperWire", CraftingStation.TECHNOLOGY_TABLE, new ItemType[] {ItemType.COPPER_INGOT}, new int[] {1}, new ItemType[] {ItemType.COPPER_WIRE}, new int[] {4}, research("gaze:machinery.electricity"));
		initializeRecipe("Materials", "magnet", CraftingStation.TECHNOLOGY_TABLE, new ItemType[] {ItemType.IRON_INGOT}, new int[] {1}, new ItemType[] {ItemType.MAGNET}, new int[] {4}, research("gaze:machinery.electricity"));
		initializeRecipe("Materials", "basicMotor", CraftingStation.TECHNOLOGY_TABLE, new ItemType[] {ItemType.IRON_INGOT, ItemType.MAGNET, ItemType.IRON_GEAR}, new int[] {1, 2, 1}, new ItemType[] {ItemType.BASIC_MOTOR}, new int[] {1}, research("gaze:machinery.motors1"));
		initializeRecipe("Materials", "basicGenerator", CraftingStation.TECHNOLOGY_TABLE, new ItemType[] {ItemType.IRON_INGOT, ItemType.MAGNET, ItemType.COPPER_WIRE}, new int[] {1, 2, 2}, new ItemType[] {ItemType.BASIC_GENERATOR}, new int[] {1}, research("gaze:machinery.generators1"));
		initializeRecipe("Materials", "basicCircuit", CraftingStation.TECHNOLOGY_TABLE, new ItemType[] {ItemType.COPPER_WIRE, ItemType.SILICON}, new int[] {2, 2}, new ItemType[] {ItemType.BASIC_CIRCUIT}, new int[] {1}, research("gaze:machinery.circuits1"));

		initializeRecipe("Tools", "woodAxe", CraftingStation.NONE, new ItemType[] {ItemType.WOOD}, new int[] {10}, new ItemType[] {ItemType.WOOD_AXE}, new int[] {1}, research("gaze:resources.woodAge"));
		initializeRecipe("Tools", "woodPickaxe", CraftingStation.NONE, new ItemType[] {ItemType.WOOD}, new int[] {10}, new ItemType[] {ItemType.WOOD_PICKAXE}, new int[] {1}, research("gaze:resources.woodAge"));
		initializeRecipe("Tools", "woodShovel", CraftingStation.NONE, new ItemType[] {ItemType.WOOD}, new int[] {10}, new ItemType[] {ItemType.WOOD_SHOVEL}, new int[] {1}, research("gaze:resources.woodAge"));
		initializeRecipe("Tools", "woodHoe", CraftingStation.NONE, new ItemType[] {ItemType.WOOD}, new int[] {10}, new ItemType[] {ItemType.WOOD_HOE}, new int[] {1}, research("gaze:resources.woodAge"));
		initializeRecipe("Tools", "stoneAxe", CraftingStation.TABLE, new ItemType[] {ItemType.WOOD_AXE, ItemType.STONE}, new int[] {1, 10}, new ItemType[] {ItemType.STONE_AXE}, new int[] {1}, research("gaze:resources.stoneAge"));
		initializeRecipe("Tools", "stonePickaxe", CraftingStation.TABLE, new ItemType[] {ItemType.WOOD_PICKAXE, ItemType.STONE}, new int[] {1, 10}, new ItemType[] {ItemType.STONE_PICKAXE}, new int[] {1}, research("gaze:resources.stoneAge"));
		initializeRecipe("Tools", "stoneShovel", CraftingStation.TABLE, new ItemType[] {ItemType.WOOD_SHOVEL, ItemType.STONE}, new int[] {1, 10}, new ItemType[] {ItemType.STONE_SHOVEL}, new int[] {1}, research("gaze:resources.stoneAge"));
		initializeRecipe("Tools", "stoneHoe", CraftingStation.TABLE, new ItemType[] {ItemType.WOOD_HOE, ItemType.STONE}, new int[] {1, 10}, new ItemType[] {ItemType.STONE_HOE}, new int[] {1}, research("gaze:resources.stoneAge"));
		initializeRecipe("Tools", "ironAxe", CraftingStation.ANVIL, new ItemType[] {ItemType.STONE_AXE, ItemType.IRON_INGOT}, new int[] {1, 8}, new ItemType[] {ItemType.IRON_AXE}, new int[] {1}, research("gaze:resources.forging"));
		initializeRecipe("Tools", "ironPickaxe", CraftingStation.ANVIL, new ItemType[] {ItemType.STONE_PICKAXE, ItemType.IRON_INGOT}, new int[] {1, 8}, new ItemType[] {ItemType.IRON_PICKAXE}, new int[] {1}, research("gaze:resources.forging"));
		initializeRecipe("Tools", "ironShovel", CraftingStation.ANVIL, new ItemType[] {ItemType.STONE_SHOVEL, ItemType.IRON_INGOT}, new int[] {1, 8}, new ItemType[] {ItemType.IRON_SHOVEL}, new int[] {1}, research("gaze:resources.forging"));
		initializeRecipe("Tools", "ironHoe", CraftingStation.ANVIL, new ItemType[] {ItemType.STONE_HOE, ItemType.IRON_INGOT}, new int[] {1, 8}, new ItemType[] {ItemType.IRON_HOE}, new int[] {1}, research("gaze:resources.forging"));
		
		initializeRecipe("Appliances", "table", CraftingStation.NONE, new ItemType[] {ItemType.WOOD}, new int[] {8}, new ItemType[] {ItemType.TABLE}, new int[] {1});
		initializeRecipe("Appliances", "campfire", CraftingStation.NONE, new ItemType[] {ItemType.WOOD}, new int[] {8}, new ItemType[] {ItemType.CAMPFIRE}, new int[] {1});
		initializeRecipe("Appliances", "torch", CraftingStation.NONE, new ItemType[] {ItemType.WOOD}, new int[] {3}, new ItemType[] {ItemType.TORCH}, new int[] {1});
		initializeRecipe("Appliances", "torchCheap", CraftingStation.NONE, new ItemType[] {ItemType.WOOD, ItemType.COAL}, new int[] {1, 2}, new ItemType[] {ItemType.TORCH}, new int[] {4}, level(Skill.MINING, 3));
		initializeRecipe("Appliances", "chute", CraftingStation.TABLE, new ItemType[] {ItemType.WOOD}, new int[] {10}, new ItemType[] {ItemType.CHUTE}, new int[] {1}, research("gaze:resources.woodAge"));
		initializeRecipe("Appliances", "chest", CraftingStation.TABLE, new ItemType[] {ItemType.WOOD}, new int[] {12}, new ItemType[] {ItemType.CHEST}, new int[] {1});
		initializeRecipe("Appliances", "furnace", CraftingStation.TABLE, new ItemType[] {ItemType.STONE}, new int[] {8}, new ItemType[] {ItemType.FURNACE}, new int[] {1}, research("gaze:resources.stoneAge"));
		initializeRecipe("Appliances", "anvil", CraftingStation.FURNACE, new ItemType[] {ItemType.IRON_INGOT}, new int[] {6}, new ItemType[] {ItemType.ANVIL}, new int[] {1}, research("gaze:resources.forging"));
		initializeRecipe("Appliances", "technologyTable", CraftingStation.ANVIL, new ItemType[] {ItemType.IRON_INGOT, ItemType.COPPER_INGOT}, new int[] {10, 5}, new ItemType[] {ItemType.TECHNOLOGY_TABLE}, new int[] {1}, research("gaze:machinery.technology"));
		
		initializeRecipe("Appliances", "woodElectricityPole", CraftingStation.TECHNOLOGY_TABLE, new ItemType[] {ItemType.WOOD, ItemType.COPPER_WIRE}, new int[] {3, 2}, new ItemType[] {ItemType.WOOD_ELECTRICITY_POLE}, new int[] {2}, research("gaze:machinery.electricity"));
		initializeRecipe("Appliances", "blastFurnace", CraftingStation.TECHNOLOGY_TABLE, new ItemType[] {ItemType.IRON_INGOT, ItemType.IRON_GEAR, ItemType.COPPER_WIRE}, new int[] {4, 4, 6}, new ItemType[] {ItemType.BLAST_FURNACE}, new int[] {1}, research("gaze:resources.blast_furnace"));
		initializeRecipe("Appliances", "basicPump", CraftingStation.TECHNOLOGY_TABLE, new ItemType[] {ItemType.IRON_INGOT, ItemType.IRON_GEAR, ItemType.BASIC_MOTOR}, new int[] {2, 4, 2}, new ItemType[] {ItemType.BASIC_PUMP}, new int[] {1}, research("gaze:machinery.pumps1"));
		initializeRecipe("Appliances", "basicMiningDrill", CraftingStation.TECHNOLOGY_TABLE, new ItemType[] {ItemType.IRON_INGOT, ItemType.IRON_GEAR, ItemType.BASIC_MOTOR, ItemType.BASIC_CIRCUIT}, new int[] {4, 6, 4, 4}, new ItemType[] {ItemType.BASIC_MINING_DRILL}, new int[] {1}, research("gaze:machinery.miningDrills1"));
		
		initializeRecipe("Appliances", "crudeWindmill", CraftingStation.TECHNOLOGY_TABLE, new ItemType[] {ItemType.WOOD, ItemType.IRON_INGOT, ItemType.BASIC_GENERATOR}, new int[] {8, 4, 2}, new ItemType[] {ItemType.CRUDE_WINDMILL}, new int[] {1}, research("gaze:machinery.windmill1"));
		initializeRecipe("Appliances", "steamEngine", CraftingStation.TECHNOLOGY_TABLE, new ItemType[] {ItemType.IRON_INGOT, ItemType.BASIC_GENERATOR}, new int[] {4, 4}, new ItemType[] {ItemType.STEAM_ENGINE}, new int[] {1}, research("gaze:machinery.steam_power"));
		initializeRecipe("Appliances", "boiler", CraftingStation.TECHNOLOGY_TABLE, new ItemType[] {ItemType.IRON_INGOT, ItemType.BASIC_GENERATOR, ItemType.COPPER_WIRE}, new int[] {4, 4, 8}, new ItemType[] {ItemType.BOILER}, new int[] {1}, research("gaze:machinery.steam_power"));
		initializeRecipe("Appliances", "solarPanel", CraftingStation.TECHNOLOGY_TABLE, new ItemType[] {ItemType.IRON_INGOT, ItemType.IRON_GEAR, ItemType.SILICON, ItemType.GLASS}, new int[] {4, 4, 4, 6}, new ItemType[] {ItemType.SOLAR_PANEL}, new int[] {1}, research("gaze:machinery.solar1"));
		initializeRecipe("Appliances", "basicConveyor", CraftingStation.TECHNOLOGY_TABLE, new ItemType[] {ItemType.IRON_INGOT, ItemType.IRON_GEAR}, new int[] {1, 2}, new ItemType[] {ItemType.BASIC_CONVEYOR}, new int[] {6}, research("gaze:machinery.motors1"));

		initializeRecipe("Farming", "woodWateringCan", CraftingStation.NONE, new ItemType[] {ItemType.WOOD}, new int[] {10}, new ItemType[] {ItemType.WOOD_WATERING_CAN}, new int[] {1}, research("gaze:farming.watering"));
		initializeRecipe("Farming", "bread", CraftingStation.NONE, new ItemType[] {ItemType.WHEAT}, new int[] {4}, new ItemType[] {ItemType.BREAD}, new int[] {1}, research("gaze:farming.bread"));
		
		initializeRecipe("Housing", "woodWall", CraftingStation.TABLE, new ItemType[] {ItemType.WOOD}, new int[] {2}, new ItemType[] {ItemType.WOOD_WALL}, new int[] {4});
		initializeRecipe("Housing", "woodFloor", CraftingStation.TABLE, new ItemType[] {ItemType.WOOD}, new int[] {2}, new ItemType[] {ItemType.WOOD_FLOOR}, new int[] {4});
		initializeRecipe("Housing", "woodDoor", CraftingStation.TABLE, new ItemType[] {ItemType.WOOD}, new int[] {2}, new ItemType[] {ItemType.WOOD_DOOR}, new int[] {1});
		initializeRecipe("Housing", "woodCeiling", CraftingStation.TABLE, new ItemType[] {ItemType.WOOD}, new int[] {2}, new ItemType[] {ItemType.WOOD_CEILING}, new int[] {4});
		initializeRecipe("Housing", "stoneBrickWall", CraftingStation.TABLE, new ItemType[] {ItemType.STONE}, new int[] {2}, new ItemType[] {ItemType.STONE_BRICK_WALL}, new int[] {4});
		initializeRecipe("Housing", "stoneBrickFloor", CraftingStation.TABLE, new ItemType[] {ItemType.STONE}, new int[] {2}, new ItemType[] {ItemType.STONE_BRICK_FLOOR}, new int[] {4});
		initializeRecipe("Housing", "stoneBrickCeiling", CraftingStation.TABLE, new ItemType[] {ItemType.STONE}, new int[] {2}, new ItemType[] {ItemType.STONE_BRICK_CEILING}, new int[] {4});
		initializeRecipe("Housing", "brickCeiling", CraftingStation.TABLE, new ItemType[] {ItemType.STONE, ItemType.WOOD}, new int[] {2, 1}, new ItemType[] {ItemType.BRICK_CEILING}, new int[] {6});
		initializeRecipe("Housing", "stonePathFloor", CraftingStation.TABLE, new ItemType[] {ItemType.STONE}, new int[] {2}, new ItemType[] {ItemType.STONE_PATH_FLOOR}, new int[] {4});
		
		initialized = true;
	}
	
	public static HashMap<String, RecipeCategory> getAllCategories() {
		return categories;
	}
	
	public static RecipeCategory getCategory(String recipeCategory) {
		return categories.get(recipeCategory);
	}
	
	public static Recipe getRecipe(String category, String recipeName) {
		for(Recipe recipe : getCategory(category).recipes) {
			if(recipe.getName().equals(recipeName)) {
				return recipe;
			}
		}
		return null;
	}
	
	private static void initializeRecipeCategory(String name, Item icon) {
		categories.put(name, new RecipeCategory(name, icon));
	}
	
	private static void initializeRecipe(String recipeCategory, String recipeName, CraftingStation station, ItemType[] ingredients, int[] ingredientsQuantity, ItemType[] results, int[] resultsQuantity, CraftingRequirement...requirements) {
		getCategory(recipeCategory).addRecipe(new Recipe(recipeCategory, recipeName, station, ingredients, ingredientsQuantity, results, resultsQuantity, requirements));
	}
	
}
