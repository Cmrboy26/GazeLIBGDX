package net.cmr.gaze.crafting;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.crafting.Crafting.CraftingStation;
import net.cmr.gaze.stage.GameScreen;

public class RecipeDisplay extends ScrollPane {
	
	Gaze game;
	GameScreen screen;
	Table table;
	ButtonGroup<RecipeSlot> group;
	ButtonGroup<CategoryButton> categoryButtons;
	CategoryButton currentlyClicked;
	CraftingStation station;
	
	public RecipeDisplay(Gaze game, GameScreen screen, Table table, ScrollPaneStyle scrollStyle, ButtonGroup<CategoryButton> categoryButtons) {
		super(table, scrollStyle);
		this.game = game;
		this.screen = screen;
		this.table = table;
		this.station = CraftingStation.NONE;
		this.categoryButtons = categoryButtons;
		group = new ButtonGroup<>();
		setPosition(6, 340/2, Align.left);
		setHeight(22*6+2*6);
		setSmoothScrolling(false);
		setOverscroll(false, false);
	}
	
	public void update() {
		CategoryButton category = categoryButtons.getChecked();
		if(!category.equals(currentlyClicked)) {
			currentlyClicked = category;
			setDisplayContent(category.category);
		}
	}
	
	public void toggleVisibility() {
		setVisible(!isVisible());
	}
	
	public void setCraftingStation(CraftingStation station) {
		this.station = station;
		setDisplayContent(currentlyClicked.category);
	}
	
	public void setDisplayContent(RecipeCategory category) {
		
		if(category == null) {
			return;
		}
		
		table.clearChildren();
		group = new ButtonGroup<>();
		group.setMaxCheckCount(1);
		group.setMinCheckCount(0);
		
		ArrayList<Recipe> validRecipes = new ArrayList<>();
		for(Recipe recipe : category.recipes) {
			if(recipe.station == station || recipe.station == CraftingStation.NONE) {
				if(screen.getLocalPlayer()!=null && recipe.requirementsMet(screen.getLocalPlayer())) {
					validRecipes.add(recipe);
				}
			}
		}

		/*ArrayList<Recipe> validRecipes = category.recipes.stream().filter(s -> {
			if(s.station == station) {
				if(screen.getLocalPlayer()!=null && s.requirementsMet(screen.getLocalPlayer())) {
					return true;
				}
			}
			return false;
		}).collect(Collectors.toCollection(ArrayList::new));*/
		
		for(int i = 0; i < validRecipes.size(); i++) {
			if(i%4==0) {
				table.row();
			}
			Recipe recipe = validRecipes.get(i);
			
			RecipeSlot slot = new RecipeSlot(game, recipe, true);
			group.add(slot);
			table.add(slot).width(22).height(22).spaceRight(2).spaceBottom(2);
		}
	}

	public RecipeSlot getSelectedRecipe() {
		return group.getChecked();
	}
	
}
