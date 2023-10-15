package net.cmr.gaze.crafting;

import java.util.HashMap;

import net.cmr.gaze.inventory.Inventory;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.leveling.Skills;
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
		ANVIL("craftingLeft", "craftingRight");
		
		public String leftDisplayName, rightDisplayName;
		
		private CraftingStation() {
			this.leftDisplayName = "crafting"+name()+"Left";
			this.rightDisplayName = "crafting"+name()+"Right";
		}
		private CraftingStation(String leftDisplayName, String rightDisplayName) {
			this.leftDisplayName = leftDisplayName;
			this.rightDisplayName = rightDisplayName;
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
		initializeRecipeCategory("Materials", Items.getItem(ItemType.WOOD, 1));
		initializeRecipeCategory("Appliances", Items.getItem(ItemType.FURNACE, 1));
		initializeRecipeCategory("Farming", Items.getItem(ItemType.WHEAT_SEEDS, 1));
		initializeRecipeCategory("Housing", Items.getItem(ItemType.STONE_BRICK_WALL, 1));
		
		//initializeRecipe("Materials", "DebugItems", CraftingStation.NONE, new ItemType[] {ItemType.STONE}, new int[] {0}, new ItemType[] {ItemType.WOOD, ItemType.WOOD}, new int[] {64, 64});
		//initializeRecipe("Materials", "DebugItems2", CraftingStation.NONE, new ItemType[] {ItemType.STONE}, new int[] {0}, new ItemType[] {ItemType.STONE, ItemType.STONE}, new int[] {64, 64});
		//initializeRecipe("Materials", "DebugItems3", CraftingStation.NONE, new ItemType[] {ItemType.STONE}, new int[] {0}, new ItemType[] {ItemType.WHEAT, ItemType.BREAD}, new int[] {64, 64});
		initializeRecipe("Materials", "ironIngot", CraftingStation.FURNACE, new ItemType[] {ItemType.IRON_ORE}, new int[] {2}, new ItemType[] {ItemType.IRON_INGOT}, new int[] {1});
		
		initializeRecipe("Tools", "woodAxe", CraftingStation.NONE, new ItemType[] {ItemType.WOOD}, new int[] {10}, new ItemType[] {ItemType.WOOD_AXE}, new int[] {1}, level(Skill.FORAGING, 1));
		initializeRecipe("Tools", "woodPickaxe", CraftingStation.NONE, new ItemType[] {ItemType.WOOD}, new int[] {10}, new ItemType[] {ItemType.WOOD_PICKAXE}, new int[] {1}, level(Skill.FORAGING, 1));
		initializeRecipe("Tools", "woodShovel", CraftingStation.NONE, new ItemType[] {ItemType.WOOD}, new int[] {10}, new ItemType[] {ItemType.WOOD_SHOVEL}, new int[] {1}, level(Skill.FORAGING, 1));
		initializeRecipe("Tools", "woodHoe", CraftingStation.NONE, new ItemType[] {ItemType.WOOD}, new int[] {10}, new ItemType[] {ItemType.WOOD_HOE}, new int[] {1}, level(Skill.FORAGING, 1));
		initializeRecipe("Tools", "stoneAxe", CraftingStation.TABLE, new ItemType[] {ItemType.WOOD_AXE, ItemType.STONE}, new int[] {1, 10}, new ItemType[] {ItemType.STONE_AXE}, new int[] {1}, level(Skill.MINING, 2));
		initializeRecipe("Tools", "stonePickaxe", CraftingStation.TABLE, new ItemType[] {ItemType.WOOD_PICKAXE, ItemType.STONE}, new int[] {1, 10}, new ItemType[] {ItemType.STONE_PICKAXE}, new int[] {1}, level(Skill.MINING, 2));
		initializeRecipe("Tools", "stoneShovel", CraftingStation.TABLE, new ItemType[] {ItemType.WOOD_SHOVEL, ItemType.STONE}, new int[] {1, 10}, new ItemType[] {ItemType.STONE_SHOVEL}, new int[] {1}, level(Skill.MINING, 2));
		initializeRecipe("Tools", "stoneHoe", CraftingStation.TABLE, new ItemType[] {ItemType.WOOD_HOE, ItemType.STONE}, new int[] {1, 10}, new ItemType[] {ItemType.STONE_HOE}, new int[] {1}, level(Skill.MINING, 2));
		initializeRecipe("Tools", "ironAxe", CraftingStation.ANVIL, new ItemType[] {ItemType.STONE_AXE, ItemType.IRON_INGOT}, new int[] {1, 8}, new ItemType[] {ItemType.IRON_AXE}, new int[] {1}, level(Skill.MINING, 3));
		initializeRecipe("Tools", "ironPickaxe", CraftingStation.ANVIL, new ItemType[] {ItemType.STONE_PICKAXE, ItemType.IRON_INGOT}, new int[] {1, 8}, new ItemType[] {ItemType.IRON_PICKAXE}, new int[] {1}, level(Skill.MINING, 3));
		
		initializeRecipe("Appliances", "table", CraftingStation.NONE, new ItemType[] {ItemType.WOOD}, new int[] {8}, new ItemType[] {ItemType.TABLE}, new int[] {1});
		initializeRecipe("Appliances", "campfire", CraftingStation.NONE, new ItemType[] {ItemType.WOOD}, new int[] {8}, new ItemType[] {ItemType.CAMPFIRE}, new int[] {1});
		initializeRecipe("Appliances", "torch", CraftingStation.NONE, new ItemType[] {ItemType.WOOD}, new int[] {3}, new ItemType[] {ItemType.TORCH}, new int[] {1});
		initializeRecipe("Appliances", "chute", CraftingStation.TABLE, new ItemType[] {ItemType.WOOD}, new int[] {10}, new ItemType[] {ItemType.CHUTE}, new int[] {1}, level(Skill.FORAGING, 1));
		initializeRecipe("Appliances", "chest", CraftingStation.TABLE, new ItemType[] {ItemType.WOOD}, new int[] {12}, new ItemType[] {ItemType.CHEST}, new int[] {1});
		initializeRecipe("Appliances", "furnace", CraftingStation.TABLE, new ItemType[] {ItemType.STONE}, new int[] {8}, new ItemType[] {ItemType.FURNACE}, new int[] {1}, level(Skill.MINING, 2));
		initializeRecipe("Appliances", "anvil", CraftingStation.TABLE, new ItemType[] {ItemType.IRON_INGOT}, new int[] {6}, new ItemType[] {ItemType.ANVIL}, new int[] {1});
		
		initializeRecipe("Farming", "woodWateringCan", CraftingStation.NONE, new ItemType[] {ItemType.WOOD}, new int[] {10}, new ItemType[] {ItemType.WOOD_WATERING_CAN}, new int[] {1}, research("gaze:machinery.electricity"));
		initializeRecipe("Farming", "bread", CraftingStation.NONE, new ItemType[] {ItemType.WHEAT}, new int[] {4}, new ItemType[] {ItemType.BREAD}, new int[] {1});
		
		initializeRecipe("Housing", "woodWall", CraftingStation.TABLE, new ItemType[] {ItemType.WOOD}, new int[] {2}, new ItemType[] {ItemType.WOOD_WALL}, new int[] {4});
		initializeRecipe("Housing", "woodFloor", CraftingStation.TABLE, new ItemType[] {ItemType.WOOD}, new int[] {2}, new ItemType[] {ItemType.WOOD_FLOOR}, new int[] {6});
		initializeRecipe("Housing", "woodDoor", CraftingStation.TABLE, new ItemType[] {ItemType.WOOD}, new int[] {2}, new ItemType[] {ItemType.WOOD_DOOR}, new int[] {1});
		initializeRecipe("Housing", "woodCeiling", CraftingStation.TABLE, new ItemType[] {ItemType.WOOD}, new int[] {2}, new ItemType[] {ItemType.WOOD_CEILING}, new int[] {6});
		initializeRecipe("Housing", "stoneBrickWall", CraftingStation.TABLE, new ItemType[] {ItemType.STONE}, new int[] {2}, new ItemType[] {ItemType.STONE_BRICK_WALL}, new int[] {4});
		initializeRecipe("Housing", "stoneBrickFloor", CraftingStation.TABLE, new ItemType[] {ItemType.STONE}, new int[] {2}, new ItemType[] {ItemType.STONE_BRICK_FLOOR}, new int[] {6});
		initializeRecipe("Housing", "stoneBrickCeiling", CraftingStation.TABLE, new ItemType[] {ItemType.STONE}, new int[] {2}, new ItemType[] {ItemType.STONE_BRICK_CEILING}, new int[] {6});
		initializeRecipe("Housing", "brickCeiling", CraftingStation.TABLE, new ItemType[] {ItemType.STONE, ItemType.WOOD}, new int[] {2, 1}, new ItemType[] {ItemType.BRICK_CEILING}, new int[] {8});
		initializeRecipe("Housing", "stonePathFloor", CraftingStation.TABLE, new ItemType[] {ItemType.STONE}, new int[] {2}, new ItemType[] {ItemType.STONE_PATH_FLOOR}, new int[] {6});
		
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
