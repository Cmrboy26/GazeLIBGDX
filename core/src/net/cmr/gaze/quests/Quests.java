package net.cmr.gaze.quests;

import java.util.ArrayList;
import java.util.HashMap;

import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.leveling.Skills.Skill;
import net.cmr.gaze.networking.PlayerConnection;
import net.cmr.gaze.networking.PlayerConnection.QuestCheckType;
import net.cmr.gaze.quests.QuestCategory.QuestPage;
import net.cmr.gaze.quests.QuestCategory.QuestPage.QuestObject;
import net.cmr.gaze.util.Identifier;

public class Quests {

	public static final Identifier GETTING_STARTED = new Identifier("gaze:getting_started");
	public static final Identifier COLLECTING_RESOURCES = new Identifier("collecting_resources");
	public static final Identifier MINING = new Identifier("mining");

	public static final Identifier FARMING = new Identifier("gaze:farming");
	public static final Identifier PREPARATION = new Identifier("preparation");
	public static final Identifier BASIC_FARMING = new Identifier("basic_farming");
	public static final Identifier AUTOMATIC_FARMING = new Identifier("automatic_farming");

	
	private static HashMap<Identifier, QuestCategory> questMap;
	private static boolean initialized = false;
	
	public static QuestCategory getQuestCategory(Identifier id) {
		if(!initialized) {
			initialize();
		}
		return questMap.getOrDefault(id, null);
	}
	
	public enum QuestTier {
		BRONZE(0),
		SILVER(1),
		GOLD(2);

		public final int tier;
		QuestTier(int tier) {
			this.tier = tier;
		}
		public int getTier() {
			return tier;
		}
	}

	public static ArrayList<QuestObject> questCheck(PlayerConnection connection, QuestCheckType type, Object... args) {
		if(!initialized) {
			initialize();
		}
		// iterate through all quest objects, run questCheck, and if it returns true, add it to the return arraylist
		ArrayList<QuestObject> completedQuests = new ArrayList<>();
		for(QuestCategory category : questMap.values()) {
			for(QuestPage page : category.questPages.values()) {
				for(QuestObject quest : page.quests) {
					if(quest.questCheck(connection, type, args)) {
						completedQuests.add(quest);
					}
				}
			}
		}
		return completedQuests;
	}
	
	public static void initialize() {
		questMap = new HashMap<>();
		initialized = true;
		
		// GETTING STARTED

		QuestCategory gettingStarted = new QuestCategory(GETTING_STARTED, "Getting Started");
		Quests.registerQuestCategory(gettingStarted);

		QuestPage collectingResources = gettingStarted.new QuestPage(COLLECTING_RESOURCES, "Collecting Resources");
		collectingResources.setDescription("Welcome to the world!\nCollect some resources and get started!");
		collectingResources.new QuestObject(QuestTier.BRONZE, "Gather Wood from Trees", QuestCheckType.PICKUP) {
			@Override
			public boolean questCheck(PlayerConnection connection, QuestCheckType type, Object... args) {
				return QuestUtil.pickup(type, ItemType.WOOD, args);
			}
		};
		collectingResources.new QuestObject(QuestTier.SILVER, "Craft a Table", QuestCheckType.CRAFT) {
			@Override
			public boolean questCheck(PlayerConnection connection, QuestCheckType type, Object... args) {
				return QuestUtil.craft(type, ItemType.TABLE, args);
			}
		};
		collectingResources.new QuestObject(QuestTier.GOLD, "Craft a Chute", QuestCheckType.CRAFT) {
			@Override
			public boolean questCheck(PlayerConnection connection, QuestCheckType type, Object... args) {
				return QuestUtil.craft(type, ItemType.CHUTE, args);
			}
		};

		QuestPage mining = gettingStarted.new QuestPage(MINING, "Mining");
		mining.setDescription("Those wood tools won't cut it in space!");
		mining.new QuestObject(QuestTier.BRONZE, "Reach Level Two Mining", QuestCheckType.LEVELUP) {
			@Override
			public boolean questCheck(PlayerConnection connection, QuestCheckType type, Object... args) {
				return QuestUtil.levelUp(type, Skill.MINING, 2, args);
			}
		};
		mining.new QuestObject(QuestTier.SILVER, "Craft a Furnace", QuestCheckType.CRAFT) {
			@Override
			public boolean questCheck(PlayerConnection connection, QuestCheckType type, Object... args) {
				return QuestUtil.craft(type, ItemType.FURNACE, args);
			}
		};
		mining.new QuestObject(QuestTier.GOLD, "Gather Iron Ore\nForge an Iron Bar", QuestCheckType.CRAFT) {
			@Override
			public boolean questCheck(PlayerConnection connection, QuestCheckType type, Object... args) {
				return QuestUtil.craft(type, ItemType.CHUTE, args);
			}
		};

		// FARMING

		QuestCategory farming = new QuestCategory(FARMING, "Farming");
		Quests.registerQuestCategory(farming);

		QuestPage preparation = gettingStarted.new QuestPage(PREPARATION, "Preparation");
		preparation.setDescription("Prepare the tools to make yourself a farm!");
		preparation.new QuestObject(QuestTier.BRONZE, "Craft a Wood Shovel", QuestCheckType.CRAFT) {
			@Override
			public boolean questCheck(PlayerConnection connection, QuestCheckType type, Object... args) {
				return QuestUtil.craft(type, ItemType.WOOD_SHOVEL, args);
			}
		};
		preparation.new QuestObject(QuestTier.SILVER, "Craft a Wood Hoe", QuestCheckType.CRAFT) {
			@Override
			public boolean questCheck(PlayerConnection connection, QuestCheckType type, Object... args) {
				return QuestUtil.craft(type, ItemType.WOOD_HOE, args);
			}
		};
		preparation.new QuestObject(QuestTier.GOLD, "Craft a Wood Watering Can", QuestCheckType.CRAFT) {
			@Override
			public boolean questCheck(PlayerConnection connection, QuestCheckType type, Object... args) {
				return QuestUtil.craft(type, ItemType.WOOD_WATERING_CAN, args);
			}
		};

		QuestPage basicFarming = gettingStarted.new QuestPage(BASIC_FARMING, "Now We're Farming!");
		basicFarming.setDescription("Use your shovel to soften the ground,\n and use the hoe totill the soil!\n Make sure your farm is properly watered!");
		basicFarming.new QuestObject(QuestTier.BRONZE, "Reach Level Two Foraging", QuestCheckType.LEVELUP) {
			@Override
			public boolean questCheck(PlayerConnection connection, QuestCheckType type, Object... args) {
				return QuestUtil.levelUp(type, Skill.FORAGING, 2, args);
			}
		};
		basicFarming.new QuestObject(QuestTier.SILVER, "Harvest Wheat", QuestCheckType.PICKUP) {
			@Override
			public boolean questCheck(PlayerConnection connection, QuestCheckType type, Object... args) {
				return QuestUtil.pickup(type, ItemType.WHEAT, args);
			}
		};
		basicFarming.new QuestObject(QuestTier.GOLD, "Craft a Stone Shovel", QuestCheckType.CRAFT) {
			@Override
			public boolean questCheck(PlayerConnection connection, QuestCheckType type, Object... args) {
				return QuestUtil.craft(type, ItemType.STONE_SHOVEL, args);
			}
		};

	}

	public static void registerQuestCategory(QuestCategory category) {
		questMap.put(category.id, category);
	}
	
	
}
