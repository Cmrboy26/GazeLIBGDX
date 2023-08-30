package net.cmr.gaze.stage.widgets;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.stage.SettingScreen;

public class HintMenu extends WidgetGroup {

	Gaze game;
	Label text;
	Image background;
	public static final float exitButtonHeight = 20;
	
	public enum HintMenuType {
		
		FIRST_JOIN {
			@Override
			public String getText() {
				return "Welcome to Gaze!\r\n"
						+ "\r\n"
						+ "These boxes will help to guide you on\r\n"
						+ "your adventure in this new universe.\r\n"
						+ "(If you need to see them again, press \"F1\".)\r\n"
						+ "To move around, use the \"WASD\" keys to move, and\r\n"
						+ "use your \"LEFT\" and \"RIGHT\" click to interact\r\n"
						+ "with the world around you.\r\n"
						+ "Try opening up your inventory with the \"E\" key!";
			}
		},
		INVENTORY {
			public String getText() {
				return "Here is your character's inventory!\r\n"
						+ "\r\n"
						+ "When you obtain an item, you can \"LEFT CLICK\"\r\n"
						+ "it once to highlight it. Once it's selected,\r\n"
						+ "you can \"LEFT CLICK\" again to transfer it over\r\n"
						+ "to that new spot.\r\n"
						+ "Items on the lowest row of your inventory\r\n"
						+ "will be available for use in your hotbar.\r\n"
						+ "Use numbers \"1-7\" on your number row to\r\n"
						+ "select items in your hotbar!\r\n"
						+ "Try pressing \"C\" to open your crafting menu!";
			}
		},
		CRAFTING {
			public String getText() {
				return "Here is the crafting menu!\r\n"
						+ "\r\n"
						+ "The top row is the category row.\r\n"
						+ "You can select any recipe, and a new menu will appear.\r\n"
						+ "The items below the large item in this menu are the ingredients. \r\n"
						+ "Once you have enough ingredients, you can craft the item!\r\n"
						+ "New recipes will be unlocked whenyour character levels up!\r\n"
						+ "Try breaking a few trees until you level up!\r\n"
						+ "(You can view progress in the right top of your screen)";
			}
		},
		LEVEL_UP {
			@Override
			public String getText() {
				return "Congrats on leveling up for the first time!\r\n"
						+ "\r\n"
						+ "You've unlocked a few new recipes, including\r\n"
						+ "wood tools, a chute (to access the underground),\r\n"
						+ "and a workbench.\r\n"
						+ "The underground is full of exploitable resources\r\n"
						+ "for you to discover and utilize in your adventures.\r\n"
						+ "Wood tools can be used to speed up resource\r\n"
						+ "collection, and the shovel/hoe pair allow you\r\n"
						+ "to create farms after collecting seeds from \r\n"
						+ "tall grass.\r\n"
						+ "Now that you have a moderate amount of wood,\r\n"
						+ "consider crafting some of these recipes.";
			}
		};
		
		public boolean viewed;
		
		public static void loadViewedHints() {
			Preferences preferences = SettingScreen.initializePreferences();
			for(HintMenuType type : HintMenuType.values()) {
				type.viewed = preferences.getBoolean("HELP_"+type.name(), false);
			}
		}
		
		public static void saveViewedHints() {
			Preferences preferences = SettingScreen.initializePreferences();
			for(HintMenuType type : HintMenuType.values()) {
				preferences.putBoolean("HELP_"+type.name(), type.viewed);
			}
			preferences.flush();
		}
		
		public static void resetHints() {
			Preferences preferences = SettingScreen.initializePreferences();
			for(HintMenuType type : HintMenuType.values()) {
				preferences.putBoolean("HELP_"+type.name(), false);
				type.viewed = false;
			}
			preferences.flush();
		}
		
		public void setViewed(boolean viewed) {
			this.viewed = viewed;
		}
		
		public abstract String getText();
		
	}
	
	static HintMenu openMenu;
	
	public HintMenu(Gaze game, HintMenuType type, float x, float y, float w, float h, float textSize) {
		this.game = game;
		setBounds(x, y-exitButtonHeight, w, h+exitButtonHeight);
		
		background = new Image(game.helpBox);
		background.setBounds(0, 0, w, h+exitButtonHeight);
		addActor(background);
		
		LabelStyle style = new LabelStyle();
		style.font = game.getFont(textSize);
		
		text = new Label(type.getText(), style);
		text.setBounds(0, exitButtonHeight, w, h);
		text.setAlignment(Align.center);
		addActor(text);
		
		TextButtonStyle button = new TextButtonStyle(game.getSkin().get("button", TextButtonStyle.class));
		button.font = game.getFont(15f);
		
		TextButton exit = new TextButton("Back", button);
		exit.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				HintMenu.this.addAction(Actions.removeActor());
			}
		});
		exit.setBounds((w-exitButtonHeight*4)/2f, exitButtonHeight*(1/6f), exitButtonHeight*4, exitButtonHeight);
		addActor(exit);
		
		if(openMenu!=null) {
			openMenu.addAction(Actions.removeActor());
			openMenu = null;
		}
		openMenu = this;
		type.setViewed(true);
		
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
	}
	
}
