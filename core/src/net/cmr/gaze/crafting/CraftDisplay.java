package net.cmr.gaze.crafting;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.networking.packets.CraftPacket;
import net.cmr.gaze.networking.packets.PositionPacket;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.util.Vector2Double;
import net.cmr.gaze.world.Tile;

public class CraftDisplay extends WidgetGroup {

	RecipeDisplay recipeDisplay;
	TextButton craftButton;
	ScrollPane ingredientsScrollPane;
	Table ingredientsTable;
	Gaze game;
	GameScreen screen;
	
	public CraftDisplay(Gaze game, GameScreen screen, RecipeDisplay recipeDisplay) {
		this.recipeDisplay = recipeDisplay;
		this.game = game;
		this.screen = screen;
		recipeDisplay.getSelectedRecipe();
		
		craftButton = new TextButton("Craft", game.getSkin(), "smallButton");
		craftButton.align(Align.center);
		craftButton.setBounds(640-130, 95, 100, 25);
		craftButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				
				if(recipeDisplay.getSelectedRecipe()==null) {
					return true;
				}
				
				Recipe recipe = recipeDisplay.getSelectedRecipe().recipe;
				
				int times = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)?5:1;
				
				Object result = recipe.craft(screen.getLocalPlayer().getInventory(), times, recipeDisplay.station, screen.getLocalPlayer().getSkills());
				
				screen.sender.addPacket(new CraftPacket(recipe.getCategory(), recipe.getName(), times));
				
				if(result != null) {
					game.playSoundCooldown(recipeDisplay.getSelectedRecipe().displayItem.getCraftSound(), 1f, .5f);
				} else {
					game.playSoundCooldown("craftFail", 1f, .5f);
				}
				return true;
			}
		});
		addActor(craftButton);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		if(recipeDisplay.getSelectedRecipe()!=null) {
			super.draw(batch, parentAlpha);
			RecipeSlot recipeSlot = recipeDisplay.getSelectedRecipe();
			Recipe recipe = recipeSlot.recipe;
			
			batch.draw(game.getSprite("itemSlotBackgroundFilled"), 640-120, 190, 78, 78);
			Item.draw(game, getStage().getViewport(), recipeSlot.displayItem, batch, 640-120, 190, 78, 78);
			
			int width = 22*recipe.ingredients.length+2*(recipe.ingredients.length-1);
			
			for(int i = 0; i < recipe.ingredients.length; i++) {
				batch.draw(game.getSprite("itemSlotBackgroundFilled"), 640-80-(width/2)+(i*24), 158, 22, 22);
				Item.draw(game, getStage().getViewport(), Items.getItem(recipe.ingredients[i], recipe.ingredientsQuantity[i]), batch, 640-80-(width/2)+(i*24), 158, 22, 22);
			}
		}
	}
	@Override
	public void act(float delta) {
		if(recipeDisplay.getSelectedRecipe()!=null) {
			super.act(delta);
		}
	}
	
}
