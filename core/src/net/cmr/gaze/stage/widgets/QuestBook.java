package net.cmr.gaze.stage.widgets;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.networking.packets.QuestDataPacket;

public class QuestBook extends WidgetGroup {

	Gaze game;
	
	Label bronzeCondition, silverCondition, goldCondition, title, description, pageNumber;
	Image bronzeCheck, silverCheck, goldCheck;
	ScrollPane scrollPane;
	ImageButton leftButton, rightButton;
	
	int index;
	ButtonGroup<QuestButton> group;
	
	HashMap<Quest, Boolean[][]> map;
	
	public QuestBook(Gaze game) {
		this.game = game;
		this.setPosition(320/2f, (360-256)/2);
		this.map = new HashMap<>();
		/*map.put(Quests.STARTING_OFF, new Boolean[][] {
			{true, false, true},
			{false, true, false},
		});
		map.put(Quests.FARMING, new Boolean[][] {
			{true, true, true}
		});*/
		
		Image image = new Image(game.getSprite("questBook"));
		image.setBounds(0, (0)/2, 320, 256);
		addActor(image);
		
		bronzeCheck = new Image(game.getSprite("checkMark"));
		bronzeCheck.setBounds((49*2), (16*2), 31*2, 14*2);
		addActor(bronzeCheck);
		
		goldCheck = new Image(game.getSprite("checkMark"));
		goldCheck.setBounds((49*2)+(30*2), (16*2), 31*2, 14*2);
		addActor(goldCheck);
		
		silverCheck = new Image(game.getSprite("checkMark"));
		silverCheck.setBounds((49*2)+(30*2)+(30*2), (16*2), 31*2, 14*2);
		addActor(silverCheck);
		
		LabelStyle conditionStyle = new LabelStyle(game.getFont(6), Color.WHITE);
		
		goldCondition = new Label("do this action to get the bronze condition.\ndo it now.", conditionStyle);
		goldCondition.setBounds((64*2), 32*2, 75*2, 8*2);
		addActor(goldCondition);
		
		silverCondition = new Label("do this action to get the silver condition", conditionStyle);
		silverCondition.setBounds((64*2), 32*2+((8+3)*2)*1, 75*2, 8*2);
		addActor(silverCondition);
		
		bronzeCondition = new Label("do this action to get the bronze condition", conditionStyle);
		bronzeCondition.setBounds((64*2), 32*2+((8+3)*2)*2, 75*2, 8*2);
		addActor(bronzeCondition);
		
		LabelStyle titleStyle = new LabelStyle(game.getFont(12), Color.WHITE);
		
		title = new Label("QUEST TITLE", titleStyle);
		title.setBounds(52*2, 101*2, 87*2, 8*2);
		title.setAlignment(Align.center);
		addActor(title);

		LabelStyle desciptionStyle = new LabelStyle(game.getFont(8), Color.WHITE);
		
		description = new Label("This is a test description\nit can be multiple lines\n kinda awesome", desciptionStyle);
		description.setBounds(52*2, (65+9)*2, 87*2, (36-9)*2);
		description.setAlignment(Align.center);
		addActor(description);
		
		pageNumber = new Label("Page", desciptionStyle);
		pageNumber.setBounds(61*2, 65*2, 69*2, 7*2);
		pageNumber.setAlignment(Align.center);
		addActor(pageNumber);
		
		ScrollPaneStyle scrollStyle = new ScrollPaneStyle();
		
		scrollPane = new ScrollPane(null, scrollStyle);
		scrollPane.setBounds(17*2, 17*2, 32*2, 94*2);
		scrollPane.setScrollbarsVisible(false);
		scrollPane.setOverscroll(false, false);
		setScrollContent();
		addActor(scrollPane);
		
		ImageButtonStyle leftStyle = new ImageButtonStyle();
		leftStyle.up = new TextureRegionDrawable(game.getSprite("smallArrow")).tint(new Color(0.141f, 0.075f, 0.02f, 1f));
		leftStyle.down = new TextureRegionDrawable(game.getSprite("smallArrow")).tint(Color.YELLOW);
		
		leftButton = new ImageButton(leftStyle);
		leftButton.setBounds(52*2, 65*2, 7*2, 7*2);
		leftButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(leftButton.isPressed()) {
					index = Math.max(0, index-1);
					setVisibleData();
				}
			}
		});
		addActor(leftButton);

		ImageButtonStyle rightStyle = new ImageButtonStyle();
		Sprite sprite = new Sprite(game.getSprite("smallArrow"));
		sprite.flip(true, false);
		rightStyle.up = new TextureRegionDrawable(sprite).tint(new Color(0.141f, 0.075f, 0.02f, 1f));
		rightStyle.down = new TextureRegionDrawable(sprite).tint(Color.YELLOW);
		
		rightButton = new ImageButton(rightStyle);
		rightButton.setBounds(132*2, 65*2, 7*2, 7*2);
		rightButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(rightButton.isPressed()) {
					index = Math.min(index+1, group.getChecked().quest.getSize()-1);
					setVisibleData();
				}
			}
		});
		addActor(rightButton);
	}
	
	public void setCompletedQuests(HashMap<Quest, Boolean[][]> map) {
		this.map = map;
	}
	
	public void setScrollContent() {
		Table table = new Table();
		group = new ButtonGroup<>();
		group.setMaxCheckCount(1);
		group.setMinCheckCount(1);
		
		TextButtonStyle style = new TextButtonStyle();
		style.up = new NinePatchDrawable(game.questBoxNine).tint(new Color(0.141f, 0.075f, 0.02f, 1f));
		style.checked = new NinePatchDrawable(game.questBoxNine).tint(Color.YELLOW);
		style.font = game.getFont(7);
		style.checkedFontColor = Color.YELLOW;
		style.fontColor = Color.WHITE;

		appendButton(table, group, Quest.STARTING_OFF, style);
		appendButton(table, group, Quest.FARMING, style);
		setVisibleData();
		
		scrollPane.setActor(table);
	}
	
	public void setVisibleData() {
		QuestButton checked = group.getChecked();
		title.setText(checked.quest.getTitle(index));
		description.setText(checked.quest.getDescription(index));
		bronzeCondition.setText(checked.quest.getBronzePreReq(index));
		silverCondition.setText(checked.quest.getSilverPreReq(index));
		goldCondition.setText(checked.quest.getGoldPreReq(index));
		pageNumber.setText((index+1)+"/"+checked.quest.getSize());
		
		bronzeCheck.setVisible(getQuestCompleted(checked.quest, index, 0));
		silverCheck.setVisible(getQuestCompleted(checked.quest, index, 1));
		goldCheck.setVisible(getQuestCompleted(checked.quest, index, 2));
	}
	
	public void setQuestData(QuestData qdata) {
		this.map = qdata.getData();
		setVisibleData();
	}
	
	public void updateQuestData(QuestDataPacket packet) {
		this.map.get(packet.getQuest())[packet.getQuestNumber()][packet.getQuestTier()] = packet.getValue();
		setVisibleData();
	}
	
	public boolean getQuestCompleted(Quest quest, int index, int tier) {
		Boolean[][] array = map.getOrDefault(quest, null);
		if(array == null) {
			return false;
		}
		return array[index][tier];
	}
	
	private void appendButton(Table table, ButtonGroup<QuestButton> group, Quest quest, TextButtonStyle style) {
		QuestButton button = new QuestButton(quest, style);
		button.align(Align.center);
		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(button.isChecked()) {
					index = 0;
					setVisibleData();
				}
			}
		});
		group.add(button);
		table.add(button).width(32*2).height(10*2).row();
	}
	
	class QuestButton extends TextButton {
		Quest quest;
		public QuestButton(Quest quest, TextButtonStyle style) {
			super(quest.getCategoryName(), style);
			this.quest = quest;
		}
	}
	
	public enum QuestTier {
		BRONZE(0),
		SILVER(1),
		GOLD(2);

		final int tier;
		QuestTier(int tier) {
			this.tier = tier;
		}
	}

	public enum Quest {
		
		STARTING_OFF(0, "Starting Off", new String[][] {
			{"Collecting Resources", "collect resource desc", "Gather Wood from Trees", "Craft a Table\nCraft a Wood Axe", "Craft a Chute"},
			{"Mining", "get some ores man", "Get to Level One Mining", "Craft a Furnace", "Gather Iron Ore\nForge an Iron Bar"}
		}),
		FARMING(1, "Farming", new String[][] {
			{"Basic Farm", "hey make a farm", "Craft a Wood Shovel", "Craft a Wood Hoe", "Craft a Wood Watering Can"},
		});
		
		public final int id;
		String questCategoryName;
		String[][] data;
		
		Quest(int id, String name, String[][] data) {
			this.id = id;
			this.questCategoryName = name;
			this.data = data;
			
			if(QuestData.questsMap==null) {
				QuestData.questsMap = new HashMap<>();
			}
			QuestData.questsMap.put(id, this);
		}
		
		public static Quest getQuestFromID(int id) {
			return QuestData.questsMap.get(id);
		}
		
		public String getCategoryName() {
			return questCategoryName;
		}
		public String getTitle(int questNumber) {
			return data[questNumber][0];
		}
		public String getDescription(int questNumber) {
			return data[questNumber][1];
		}
		public String getPreReq(int tier, int questNumber) {
			if(tier == 0) {
				return getBronzePreReq(questNumber);
			}
			if(tier == 1) {
				return getSilverPreReq(questNumber);
			}
			if(tier == 2) {
				return getGoldPreReq(questNumber);
			}
			return "";
		}
		public String getPreReq(QuestTier qtier, int questNumber) {
			int tier = qtier.tier;
			if(tier == 0) {
				return getBronzePreReq(questNumber);
			}
			if(tier == 1) {
				return getSilverPreReq(questNumber);
			}
			if(tier == 2) {
				return getGoldPreReq(questNumber);
			}
			return "";
		}
		public String getBronzePreReq(int questNumber) {
			return data[questNumber][2];
		}
		public String getSilverPreReq(int questNumber) {
			return data[questNumber][3];
		}
		public String getGoldPreReq(int questNumber) {
			return data[questNumber][4];
		}
		public int getSize() {
			return data.length;
		}
	}
	
}
