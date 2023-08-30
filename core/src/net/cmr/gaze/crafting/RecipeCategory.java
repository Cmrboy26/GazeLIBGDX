package net.cmr.gaze.crafting;

import java.util.ArrayList;

import net.cmr.gaze.inventory.Item;

public class RecipeCategory {

	String name;
	Item categoryItem;
	ArrayList<Recipe> recipes;
	
	public RecipeCategory(String name, Item categoryItem) {
		this.name = name;
		this.categoryItem = categoryItem;
		this.recipes = new ArrayList<>();
	}
	
	public String getName() {
		return name;
	}
	
	public Item getCategoryItem() {
		return categoryItem;
	}
	
	public void addRecipe(Recipe recipe) {
		recipes.add(recipe);
	}
	
}
