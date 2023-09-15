package net.cmr.gaze.quests;

import java.util.HashMap;

import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.networking.PlayerConnection;
import net.cmr.gaze.networking.PlayerConnection.QuestCheckType;
import net.cmr.gaze.quests.QuestCategory.QuestPage;
import net.cmr.gaze.util.Identifier;

public class Quests {

	public static final Identifier GETTING_STARTED = new Identifier("gaze:getting_started");
	public static final Identifier COLLECTING_RESOURCES = new Identifier("collecting_resources");
	public static final Identifier FARMING = new Identifier("gaze:farming");
	
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
	
	public static void initialize() {
		questMap = new HashMap<>();
		initialized = true;
		
		QuestCategory gettingStarted = new QuestCategory(GETTING_STARTED, "Getting Started");
		
		QuestPage collectingResources = gettingStarted.new QuestPage(COLLECTING_RESOURCES, "Collecting Resources");
		collectingResources.setDescription("Welcome to the world!\nCollect some resources and get started!");
		collectingResources.new QuestObject(QuestTier.BRONZE, "Gather Wood from Trees", QuestCheckType.PICKUP) {
			@Override
			public boolean questCheck(PlayerConnection connection, QuestCheckType type, Object... args) {
				return QuestUtil.pickup(type, ItemType.WOOD, args);
			}
		};
		collectingResources.new QuestObject(QuestTier.SILVER, "Craft a Table") {
			@Override
			public boolean questCheck(PlayerConnection connection, QuestCheckType type, Object... args) {
				return QuestUtil.craft(type, ItemType.TABLE, args);
			}
		};
		collectingResources.new QuestObject(QuestTier.GOLD, "Craft a Chute") {
			@Override
			public boolean questCheck(PlayerConnection connection, QuestCheckType type, Object... args) {
				return QuestUtil.craft(type, ItemType.CHUTE, args);
			}
		};
	}
	
	
}
