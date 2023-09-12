package net.cmr.gaze.crafting;

import net.cmr.gaze.crafting.Crafting.CraftingStation;
import net.cmr.gaze.crafting.Crafting.LevelRequirement;
import net.cmr.gaze.inventory.Inventory;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.leveling.Skills;

public class Recipe {

	String recipeName, categoryName;
	ItemType[] ingredients;
	int[] ingredientsQuantity;
	ItemType[] results;
	int[] resultsQuantity;
	CraftingStation station;
	LevelRequirement[] levelRequirements;
	
	public Recipe(String categoryName, String recipeName, CraftingStation station, ItemType[] ingredients, int[] ingredientsQuantity, ItemType[] results, int[] resultsQuantity, LevelRequirement...levelRequirements) {
		this.recipeName = recipeName;
		this.categoryName = categoryName;
		this.ingredients = ingredients;
		this.results = results;
		this.ingredientsQuantity = ingredientsQuantity;
		this.resultsQuantity = resultsQuantity;
		this.station = station;
		this.levelRequirements = levelRequirements;
	}
	
	public boolean canCraft(Inventory inventory, Skills skills) {
		return canCraft(inventory, 1, skills);
	}
	public boolean canCraft(Inventory inventory, int times, Skills skills) {
		for(int i = 0; i < ingredients.length; i++) {
			ItemType type = ingredients[i];
			int amountInInv = inventory.getQuantityOfItem(type);
			if(amountInInv < ingredientsQuantity[i]*times) {
				return false;
			}
		}
		return correctLevel(skills);
	}
	
	public boolean correctLevel(Skills skills) {
		for(LevelRequirement levelRequirement : levelRequirements) {
			int level = skills.getLevel(levelRequirement.skill);
			if(level<levelRequirement.requiredLevel) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @param inventory
	 * @param times
	 * @return null if craft was unsuccessful, otherwise returns leftover items from the craft that do not fit in the inventory
	 */
	public Item[] craft(Inventory inventory, int times, CraftingStation station, Skills skills) {
		if(!canCraft(inventory, times, skills)) {
			return null;
		}
		if(this.station != station) {
			return null;
		}
		
		for(int i = 0; i < ingredients.length; i++) {
			inventory.remove(Items.getItem(ingredients[i], ingredientsQuantity[i]*times));
		}
		Item[] returns = new Item[results.length];
		for(int i = 0; i < results.length; i++) {
			for(int time = 0; time<times; time++) {
				returns[i] = inventory.add(Items.getItem(results[i], resultsQuantity[i]));
			}
		}
		return returns;
	}
	
	public String getName() {
		return recipeName;
	}
	public String getCategory() {
		return categoryName;
	}
	public ItemType[] getResults() {
		return results;
	}
	public ItemType[] getIngredients() {
		return ingredients;
	}



}
