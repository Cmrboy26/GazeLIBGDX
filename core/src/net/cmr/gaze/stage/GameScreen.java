package net.cmr.gaze.stage;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.Deflater;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.crafting.CategoryButton;
import net.cmr.gaze.crafting.CraftDisplay;
import net.cmr.gaze.crafting.Crafting;
import net.cmr.gaze.crafting.Crafting.CraftingStation;
import net.cmr.gaze.crafting.RecipeCategory;
import net.cmr.gaze.crafting.RecipeDisplay;
import net.cmr.gaze.debug.RateCalculator;
import net.cmr.gaze.game.ChatManager;
import net.cmr.gaze.game.ChatMessage;
import net.cmr.gaze.inventory.InventorySlot;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Placeable;
import net.cmr.gaze.inventory.Tool;
import net.cmr.gaze.leveling.SkillDisplay;
import net.cmr.gaze.leveling.Skills.Skill;
import net.cmr.gaze.leveling.SkillsPacket;
import net.cmr.gaze.networking.GameServer;
import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketBuilder;
import net.cmr.gaze.networking.PacketSender;
import net.cmr.gaze.networking.packets.AudioPacket;
import net.cmr.gaze.networking.packets.ChatPacket;
import net.cmr.gaze.networking.packets.ChestInventoryPacket;
import net.cmr.gaze.networking.packets.ChunkDataPacket;
import net.cmr.gaze.networking.packets.ChunkUnloadPacket;
import net.cmr.gaze.networking.packets.CraftingStationPacket;
import net.cmr.gaze.networking.packets.DespawnEntity;
import net.cmr.gaze.networking.packets.DisconnectPacket;
import net.cmr.gaze.networking.packets.EntityPositionsPacket;
import net.cmr.gaze.networking.packets.FoodPacket;
import net.cmr.gaze.networking.packets.HealthPacket;
import net.cmr.gaze.networking.packets.HotbarUpdatePacket;
import net.cmr.gaze.networking.packets.InventoryClickPacket;
import net.cmr.gaze.networking.packets.InventoryUpdatePacket;
import net.cmr.gaze.networking.packets.PingPacket;
import net.cmr.gaze.networking.packets.PlayerConnectionStatusPacket;
import net.cmr.gaze.networking.packets.PlayerInputPacket;
import net.cmr.gaze.networking.packets.PlayerInteractPacket;
import net.cmr.gaze.networking.packets.PositionPacket;
import net.cmr.gaze.networking.packets.QuestDataPacket;
import net.cmr.gaze.networking.packets.SpawnEntity;
import net.cmr.gaze.networking.packets.TileUpdatePacket;
import net.cmr.gaze.networking.packets.UIEventPacket;
import net.cmr.gaze.networking.packets.WorldChangePacket;
import net.cmr.gaze.stage.widgets.BarsWidget;
import net.cmr.gaze.stage.widgets.ChatWidget;
import net.cmr.gaze.stage.widgets.ChestInventoryWidget;
import net.cmr.gaze.stage.widgets.GameMenuIcon;
import net.cmr.gaze.stage.widgets.HintMenu;
import net.cmr.gaze.stage.widgets.HintMenu.HintMenuType;
import net.cmr.gaze.stage.widgets.Notification;
import net.cmr.gaze.stage.widgets.PauseMenu;
import net.cmr.gaze.stage.widgets.PlayerInventoryWidget;
import net.cmr.gaze.stage.widgets.QuestBook;
import net.cmr.gaze.stage.widgets.ResearchMenu;
import net.cmr.gaze.util.ClosestValueMap;
import net.cmr.gaze.util.CustomMath;
import net.cmr.gaze.util.CustomTime;
import net.cmr.gaze.util.Vector2Double;
import net.cmr.gaze.world.BaseTile;
import net.cmr.gaze.world.CeilingTile;
import net.cmr.gaze.world.Chunk;
import net.cmr.gaze.world.LightSource;
import net.cmr.gaze.world.Lights;
import net.cmr.gaze.world.RenderRule;
import net.cmr.gaze.world.Rotatable;
import net.cmr.gaze.world.SeeThroughTile;
import net.cmr.gaze.world.StructureTile;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileData;
import net.cmr.gaze.world.Tiles;
import net.cmr.gaze.world.TransitionTile;
import net.cmr.gaze.world.WallTile;
import net.cmr.gaze.world.WorldGenerator.WorldGeneratorType;
import net.cmr.gaze.world.entities.Entity;
import net.cmr.gaze.world.entities.HealthEntity;
import net.cmr.gaze.world.entities.Particle;
import net.cmr.gaze.world.entities.Player;

@SuppressWarnings("deprecation")
public class GameScreen implements Screen {

	public final Gaze game;
	Viewport uiViewport, topViewport, bottomViewport, worldViewport, rightTopViewport, leftTopViewport, leftBottomViewport;
	Stage bottomStage, centerStage, topStage, rightTopStage, leftTopStage, leftBottomStage;
	
	ShapeRenderer shapeRenderer;
	GameServer server;
	
	Socket socket;
	DataOutputStream dataOut;
	DataInputStream dataIn;
	
	public PacketBuilder builder;
	public PacketSender sender;
	boolean serverSinglePlayer;
	
	final String username;
	
	HashMap<Point, Tile[][][]> tileData; // SHOULD NOT BE SET TO NULL, otherwise TileData object will be out of sync
	public TileData tileDataObject;
	
	Table hotbarTable;
	WidgetGroup crafting;
	
	PlayerInventoryWidget inventory;
	ChestInventoryWidget chestInventory;
	
	ButtonGroup<InventorySlot> hotbarButtonGroup;
	RecipeDisplay recipeDisplay;
	CraftDisplay craftDisplay;
	ScrollPane categoryScrollPane;
	Image craftingLeft, craftingRight;
	ButtonGroup<CategoryButton> categoryButtonGroup;
	Table categoryTable;
	
	PauseMenu pauseMenu;
	SkillDisplay skillDisplay;
	QuestBook quests;
	BarsWidget barsWidget;
	ResearchMenu tech;
	ChatWidget chatWidget;

	ConcurrentHashMap<UUID, Entity> entities;
	
	FrameBuffer frameBuffer;
	Lights lights;
	
	long latency = 0;
	double worldTime = 0;
	boolean showUI = true;
	int rotation = 0;
	boolean gammaOverride = false;
	
	WorldGeneratorType currentWorldType;
	
	ClosestValueMap<Long, Vector2Double> cvm = new ClosestValueMap<Long, Vector2Double>();
	public RateCalculator downloadSpeed = new RateCalculator(100), uploadSpeed = new RateCalculator(100);
	
	ChatManager chat;
	
	public GameScreen(final Gaze game, String username, Socket socket, DataInputStream dataIn, DataOutputStream dataOut, GameServer server, boolean singlePlayer) {
		this.game = game;
		this.serverSinglePlayer = singlePlayer;
		this.entities = new ConcurrentHashMap<>();
		Preferences prefs = SettingScreen.initializePreferences();
		lights = new Lights();
		chat = new ChatManager();
		
		this.uiViewport = new FitViewport(640, 360);
		this.uiViewport.getCamera().position.set(640f/2f, 360f/2f, 0);
		((OrthographicCamera)uiViewport.getCamera()).zoom = prefs.getFloat("uiZoom");
		
		this.bottomViewport = new FitViewport(640, 360);
		((OrthographicCamera)bottomViewport.getCamera()).zoom = prefs.getFloat("uiZoom");
		
		topViewport = new FitViewport(640, 360);
		topViewport.getCamera().position.set(320, 180, 0);
		
		rightTopViewport = new FitViewport(640, 360);
		//((OrthographicCamera)rightTopViewport.getCamera()).zoom = prefs.getFloat("uiZoom");
		rightTopViewport.getCamera().position.set(320, 180, 0);
		
		leftTopViewport = new FitViewport(640, 360);
		//((OrthographicCamera)leftTopViewport.getCamera()).zoom = prefs.getFloat("uiZoom");
		leftTopViewport.getCamera().position.set(320, 180, 0);

		leftBottomViewport = new FitViewport(640, 360);
		//((OrthographicCamera)leftTopViewport.getCamera()).zoom = prefs.getFloat("uiZoom");
		leftBottomViewport.getCamera().position.set(320, 180, 0);

		this.worldViewport = new ExtendViewport(64, 36);
		this.worldViewport.getCamera().position.set(64f/2f, 36f/2f, 0);
		((OrthographicCamera)worldViewport.getCamera()).zoom = prefs.getFloat("worldZoom");
		this.tileData = new HashMap<>();
		this.tileDataObject = new TileData(tileData);
		shapeRenderer = new ShapeRenderer();
		this.username = username;
		this.server = server;
		this.builder = new PacketBuilder(false) {
			@Override
			public void processPacket(Packet packet) {
				processIncomingPacket(packet);
			}
		};
		this.sender = new PacketSender();
		this.builder.attatchCalculator(downloadSpeed);
		this.sender.attatchCalculator(uploadSpeed);
		
		this.socket = socket;
		this.dataIn = dataIn;
		this.dataOut = dataOut;
		
		InputMultiplexer multiInput = new InputMultiplexer();
		
		bottomStage = new Stage(bottomViewport);
		centerStage = new Stage(uiViewport);
		topStage = new Stage(topViewport);
		rightTopStage = new Stage(rightTopViewport);
		leftTopStage = new Stage(leftTopViewport);
		leftBottomStage = new Stage(leftBottomViewport);
		
		barsWidget = new BarsWidget(game);
		leftTopStage.addActor(barsWidget);

		int width = 30;
		int spacing = 10;
		
		int totalWidth = (width*7)+(spacing*6);
		
		hotbarTable = new Table();
		hotbarTable.setWidth(totalWidth);
		hotbarTable.setHeight(width);
		hotbarButtonGroup = new ButtonGroup<InventorySlot>();
		hotbarButtonGroup.setMinCheckCount(1);
		hotbarButtonGroup.setMaxCheckCount(1);
		
		float gmicwidth = 24;
		float gmicspacing = 4;
		float gmicmargin = 6;
		float top = 260;

		GameMenuIcon gmicInventory = new GameMenuIcon(this, GameMenuIcon.INVENTORY_ICON, 640-gmicwidth-gmicmargin, top, gmicwidth) {
			@Override
			public void onClick() {
				toggleMenu(MenuType.INVENTORY);
			}
		};
		rightTopStage.addActor(gmicInventory);

		GameMenuIcon gmicCrafting = new GameMenuIcon(this, GameMenuIcon.CRAFTING_ICON, 640-gmicwidth-gmicmargin, top-(gmicwidth+gmicspacing), gmicwidth) {
			@Override
			public void onClick() {
				toggleMenu(MenuType.CRAFTING);
			}
		};
		rightTopStage.addActor(gmicCrafting);

		GameMenuIcon gmicQuest = new GameMenuIcon(this, GameMenuIcon.QUESTS_ICON, 640-gmicwidth-gmicmargin, top-(gmicwidth+gmicspacing)*2, gmicwidth) {
			@Override
			public void onClick() {
				toggleMenu(MenuType.QUESTS);
			}
		};
		rightTopStage.addActor(gmicQuest);

		GameMenuIcon gmicTech = new GameMenuIcon(this, GameMenuIcon.TECH_ICON, 640-gmicwidth-gmicmargin, top-(gmicwidth+gmicspacing)*3, gmicwidth) {
			@Override
			public void onClick() {
				toggleMenu(MenuType.TECH);
			}
		};
		rightTopStage.addActor(gmicTech);

		for(int i = 0; i < 7; i++) {
			InventorySlot button = new InventorySlot(game, this, i, false);
			hotbarButtonGroup.add(button);
			final int it = i;
			if(i == 0) {
				button.setChecked(true);
			}
			button.addListener(new ActorGestureListener() {
				@Override
				public void touchUp(InputEvent event, float x, float y, int pointer, int b) {
					super.touchUp(event, x, y, pointer, b);
					if(button.hit(x, y, false) != null) {
						sender.addPacket(new HotbarUpdatePacket((byte) (it)));
						game.playSound("tick", .5f);
						getLocalPlayer().setHotbarSlot(it);
						openHelpMenu(HintMenuType.ROTATION, true);
					}
				}
			});
			
			hotbarTable.add(button).width(width).height(width).spaceRight(spacing);
		}

		hotbarTable.setBounds(320-totalWidth/2, 26.5f*(8/10f), totalWidth, width);
		
		bottomStage.addActor(hotbarTable);
		
		inventory = new PlayerInventoryWidget(game, this);
		chestInventory = new ChestInventoryWidget(game, this);
		crafting = new WidgetGroup();
		quests = new QuestBook(game);
		tech = new ResearchMenu(game);
		chatWidget = new ChatWidget(game, this, chat);

		craftingLeft = new Image(game.getSprite("craftingLeft"));
		craftingLeft.setBounds(0, (360-256)/2, 80*2, 128*2);
		crafting.addActor(craftingLeft);
		
		craftingRight = new Image(game.getSprite("craftingRight")) {
			@Override
			public void draw(Batch batch, float parentAlpha) {
				if(recipeDisplay.getSelectedRecipe()!=null) {
					super.draw(batch, parentAlpha);
				}
			}
		};
		craftingRight.setBounds(640-160, (360-256)/2, 80*2, 128*2);
		crafting.addActor(craftingRight);

		categoryTable = new Table();
		categoryScrollPane = new ScrollPane(categoryTable);
		categoryButtonGroup = new ButtonGroup<>();
		
		categoryButtonGroup.setMaxCheckCount(1);
		categoryButtonGroup.setMinCheckCount(1);
		
		categoryScrollPane.setPosition(6+28, 319, Align.left);
		categoryScrollPane.setWidth(22*4+2*3);
		categoryScrollPane.setHeight(15);
		categoryScrollPane.setSmoothScrolling(false);
		categoryScrollPane.setOverscroll(false, false);
		
		for(String key : Crafting.getAllCategories().keySet()) {
			RecipeCategory category = Crafting.getAllCategories().get(key);
			CategoryButton button = new CategoryButton(game, category, true);
			categoryButtonGroup.add(button);
			categoryTable.add(button).width(15).height(15).spaceRight(2);
		}
		
		ScrollPaneStyle scrollStyle = new ScrollPaneStyle();
		recipeDisplay = new RecipeDisplay(game, this, new Table(), scrollStyle, categoryButtonGroup);

		craftDisplay = new CraftDisplay(game, this, recipeDisplay);
		
		inventory.setVisible(false);
		chestInventory.setVisible(false);
		
		crafting.setVisible(false);
		recipeDisplay.setVisible(false);
		categoryScrollPane.setVisible(false);
		craftDisplay.setVisible(false);
		skillDisplay = new SkillDisplay(game, this);
		skillDisplay.setVisible(true);
		pauseMenu = new PauseMenu(game, this);
		
		centerStage.addActor(chestInventory);
		centerStage.addActor(inventory);
		centerStage.addActor(crafting);
		centerStage.addActor(recipeDisplay);
		centerStage.addActor(categoryScrollPane);
		centerStage.addActor(craftDisplay);
		centerStage.addActor(quests);
		centerStage.addActor(tech);
		centerStage.addActor(pauseMenu);
		rightTopStage.addActor(skillDisplay);
		leftBottomStage.addActor(chatWidget);
		
		openHelpMenu(HintMenuType.FIRST_JOIN);
		
		multiInput.addProcessor(bottomStage);
		multiInput.addProcessor(centerStage);
		multiInput.addProcessor(topStage);
		multiInput.addProcessor(rightTopStage);
		multiInput.addProcessor(leftTopStage);
		multiInput.addProcessor(leftBottomStage);
		multiInput.addProcessor(new InputAdapter() {
			@Override
			public boolean scrolled(float amountX, float amountY) {
				if(Math.abs(amountY) > 0) {
					int currentSlot = hotbarButtonGroup.getChecked().slot;
					int newSlot = currentSlot + (int) (Math.abs(amountY)/amountY*(prefs.getBoolean("invertScroll")?1:-1));
					if(newSlot > 6) {
						newSlot = 0;
					}
					if(newSlot < 0) {
						newSlot = 6;
					}
					InventorySlot button = (InventorySlot) hotbarTable.getCells().get(newSlot).getActor();
					button.setChecked(true);
					sender.addPacket(new HotbarUpdatePacket((byte) (newSlot)));
					if(getLocalPlayer()!=null) {
						getLocalPlayer().setHotbarSlot(newSlot);
						openHelpMenu(HintMenuType.ROTATION, true);
					}
					return true;
				}
				return false;
			}
			@Override
			public boolean keyDown(int character) {
				if(character >= Input.Keys.NUM_1 && character <= Input.Keys.NUM_7) {
					InventorySlot button = (InventorySlot) hotbarTable.getCells().get(character-Input.Keys.NUM_1).getActor();
					//deselectAllButtons();
					button.setChecked(true);
					sender.addPacket(new HotbarUpdatePacket((byte) (character-Input.Keys.NUM_1)));
					if(getLocalPlayer()!=null) {
						getLocalPlayer().setHotbarSlot(character-Input.Keys.NUM_1);
						openHelpMenu(HintMenuType.ROTATION, true);
					}
					return true;
				}
				if(character == Input.Keys.E) {
					if(chestInventory.isVisible()) {
						MenuGroup.CENTER.closeGroup(GameScreen.this);
					} else {
						toggleMenu(MenuType.INVENTORY);
						openHelpMenu(HintMenuType.INVENTORY);
						sender.addPacket(new UIEventPacket(inventory.isVisible(), 2));
					}
				}
				if(character == Input.Keys.C) {
					setCraftingStation(CraftingStation.NONE);
					toggleMenu(MenuType.CRAFTING);
					openHelpMenu(HintMenuType.CRAFTING);
					sender.addPacket(new UIEventPacket(craftingMenuVisible, 1));
				}
				if(character == Input.Keys.F) {
					toggleMenu(MenuType.QUESTS);
					sender.addPacket(new UIEventPacket(craftingMenuVisible, 3));
				}
				if(character == Input.Keys.ESCAPE) {
					pauseMenu.setVisible(!pauseMenu.isVisible());
					game.playSound("select", 1f);
				}
				if(character == Input.Keys.F12) {
					gammaOverride = !gammaOverride;
				}
				if(character == Input.Keys.F3) {
					Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
					ByteBuffer pixels = pixmap.getPixels();

					int size = Gdx.graphics.getBackBufferWidth() * Gdx.graphics.getBackBufferHeight() * 4;
					for (int i = 3; i < size; i += 4) {
						pixels.put(i, (byte) 255);
					}
					
					String name = System.currentTimeMillis()+".png";
					FileHandle external = Gdx.files.external("/Gaze/screenshots/"+name);
					FileHandle folder = Gdx.files.external("/Gaze/screenshots/");
					folder.mkdirs();
					PixmapIO.writePNG(external, pixmap, Deflater.DEFAULT_COMPRESSION, true);
					System.out.println("[INFO] Took screenshot and saved to "+external.file().getPath());
					pixmap.dispose();
				}
				if(character == Input.Keys.F4) {
					Gdx.graphics.setWindowedMode(1920, 720);
				}
				return false;
			}
			@SuppressWarnings("unchecked")
			public void deselectAllButtons() {
				for(Cell<InventorySlot> c : hotbarTable.getCells()) {
					InventorySlot temp = c.getActor();
					temp.setChecked(false);
				}
			}
		});
		
		Gdx.input.setInputProcessor(multiInput);
		
		game.stopMusic();
		
	}
	
	@Override
	public void show() {
		
	}

	float stepDelta = 0;
	
	float lastX, lastY;
	boolean lastSprint = false;
	
	float pingDelta = 0;
	final float pingTime = 0.1f;
	
	float updateDelta;
	float logPositionDelta;
	float rightClickDelta = 0;
	float leftClickDelta = 0;
	
	long lastUpdate = 0;
	long lastSoundAttempt = 0;
	int soundChance = 500;
	
	@Override
	public void render(float delta) {
		
		if(!isConnected()) {
			game.setScreen(new MessageScreen(game, "Disconnected"));
			return;
		}
		
		processConnection(delta);
		
		worldTime+=delta;
		stepDelta+=Math.min(delta, .25f);
		logPositionDelta+=delta;
		
		currentRenderRule = deriveRenderRule();

		processEntitiesAndPlayerMovement(delta);

		Vector2 mouseScreenPosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
		Vector2 mouseLocalPosition = inventory.screenToLocalCoordinates(mouseScreenPosition);
		
		processMouseInputs(delta, mouseLocalPosition);

		if(Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
			openHelpMenu(null, false);
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.F2)) {
			showUI = !showUI;
		}
		
		recipeDisplay.update();
		
		Point centerChunk = null;
		if(getLocalPlayer()!=null) {
			Vector2Double pos = new Vector2Double(getLocalPlayer().getX(), getLocalPlayer().getY());
			
			snapCamera(pos);
			centerChunk = Entity.getChunk(pos.getX(), pos.getY());
			
			// sends the position of the player on the client side to the server
			// no current uses for this yet, but if the server wanted to sync the player better, and
			// help reduce snaping back, this position packet would be the way to do it
			if(CustomTime.timeToSeconds(System.nanoTime()-lastUpdate)>=Entity.DELTA_TIME) {
				sender.addPacket(new PositionPacket(pos));
				lastUpdate = System.nanoTime();
			}
			
		}
		
		if(centerChunk != null) {

			frameBuffer.begin();
			
			renderWorld(centerChunk);
			renderPlaceableHologram();

			frameBuffer.end();
			
			drawWorldToScreen();
		}
		
		renderUI(delta, mouseLocalPosition);

		currentRenderRule = null;
	}

	private void renderUI(float delta, Vector2 mouseLocalPosition) {
		game.batch.setBlendFunction(GL20.GL_SRC_ALPHA,  GL20.GL_ONE_MINUS_SRC_ALPHA);
		game.batch.begin();
		
		game.batch.setProjectionMatrix(topViewport.getCamera().combined);
		
		topViewport.apply();

		if(showUI) {
			topStage.act(delta);
			topStage.draw();
		}
		
		game.batch.setProjectionMatrix(rightTopViewport.getCamera().combined);
		
		if((activeNotification==null || activeNotification.finished()) && notificationQueue.size()>0) {
			Notification notification = notificationQueue.poll();
			activeNotification = notification;
			rightTopStage.addActor(activeNotification);
		}
		
		rightTopViewport.apply();
		if(showUI) {
			rightTopStage.act(delta);
			rightTopStage.draw();
		}
		
		game.batch.setProjectionMatrix(leftTopViewport.getCamera().combined);

		leftTopViewport.apply();
		if(showUI) {
			leftTopStage.act(delta);
			leftTopStage.draw();
		}
		
		game.batch.setProjectionMatrix(bottomViewport.getCamera().combined);

		bottomViewport.apply();
		

		if(showUI) {
			int width = 320;
			game.batch.draw(game.getSprite("hotbar"), 320-width/2f, 4, width, width/5f);
		}
		
		game.batch.end();
		game.batch.begin();
		if(showUI) {
			bottomStage.act();
			bottomStage.draw();
		}
		game.batch.end();

		game.batch.setProjectionMatrix(leftBottomViewport.getCamera().combined);
		game.batch.begin();
		leftBottomViewport.apply();
		if(showUI) {
			leftBottomStage.act();
			leftBottomStage.draw();
		}
		game.batch.end();

		game.batch.setProjectionMatrix(uiViewport.getCamera().combined);
		
		uiViewport.apply();

		if(showUI) {
			centerStage.act(delta);
			centerStage.draw();
		}
		
		if(GameScreen.hoveredItemViewport!=null) {
			game.batch.setProjectionMatrix(GameScreen.hoveredItemViewport.getCamera().combined);
			game.batch.begin();
			GameScreen.hoveredItemViewport.apply();
			
			BitmapFont font = game.getFont(5f);
			
			float x = mouseLocalPosition.x+5;
			x = CustomMath.minMax(hoveredItemViewport.getWorldWidth()/6, x, hoveredItemViewport.getWorldWidth()-hoveredItemViewport.getWorldWidth()/6);
			
			Item item = hoveredItem;
			font.draw(game.batch, Item.getName(item)+"\n"+Item.getDescription(item), x, mouseLocalPosition.y);
			
			game.batch.end();
		}
		GameScreen.hoveredItemViewport = null;
		GameScreen.hoveredItem = null;
		GameScreen.hoveredItemLocalViewportCoordinates = null;
	}

	private void drawWorldToScreen() {
		shapeRenderer.setProjectionMatrix(game.batch.getProjectionMatrix().idt());
		shapeRenderer.begin(ShapeType.Filled);
		float ambience = getAmbientLight();
		
		shapeRenderer.setColor(ambience, ambience, ambience, 1);
		shapeRenderer.rect(-1, 1, 2, -2);
		shapeRenderer.end();

		
		game.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		game.batch.begin();

		game.batch.setProjectionMatrix(worldViewport.getCamera().combined);
		lights.renderLights(game.batch);
		
		game.batch.end();
		game.batch.setProjectionMatrix(game.batch.getProjectionMatrix().idt());
		game.batch.setBlendFunction(GL20.GL_ZERO,  GL20.GL_SRC_COLOR);
		game.batch.begin();
		
		game.batch.draw(frameBuffer.getColorBufferTexture(), -1, 1, 2, -2);
		game.batch.setColor(Color.WHITE);
		game.batch.end();
	}

	private void renderPlaceableHologram() {
		if(getLocalPlayer()!=null) {
			if(getLocalPlayer().getHeldItem() instanceof Placeable) {
				Placeable placeable = (Placeable) getLocalPlayer().getHeldItem();
				Tile temp = Tiles.getTile(placeable.getTileToPlace());
				if(temp instanceof Rotatable) {
					Rotatable rotatable = (Rotatable) temp;
					if(Gdx.input.isKeyJustPressed(Input.Keys.R)) {
						rotation++;
						if(rotation>rotatable.maxDirection()) {
							rotation = 0;
						}
					}
					if(Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
						rotation--;
						if(rotation<0) {
							rotation = rotatable.maxDirection();
						}
					}
					if(rotation > rotatable.maxDirection()) {
						rotation = 0;
					}
				} else {
					rotation = 0;
				}

				Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
				Vector3 output = worldViewport.getCamera().unproject(new Vector3(mouse, 0));
				Point targetTile = Entity.getTileCoordinates(output.x, output.y);
				
				double time = CustomTime.timeToSeconds(System.nanoTime());
				float sin = MathUtils.sin((float) ((time*2)%MathUtils.PI2));
				float random = (sin*sin)/4+(2.5f/4f);
				
				game.batch.setColor(1, 1, 1, random);
				placeable.getTempPlaceTile(rotation).render(game, this, targetTile.x, targetTile.y);
				
				game.batch.setColor(Color.WHITE);
			}
		}
		game.batch.end();
	}

	/**
	 * Renders the game world on the screen.
	 * 
	 * @param centerChunk the center chunk of the world to be rendered
	 */
	private void renderWorld(Point centerChunk) {
		Gdx.gl.glClearColor(.2f, .2f, .2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		worldViewport.apply();
		game.batch.setProjectionMatrix(worldViewport.getCamera().combined);
		game.batch.setBlendFunction(GL20.GL_SRC_ALPHA,  GL20.GL_ONE_MINUS_SRC_ALPHA);
		game.batch.begin();
		
		ArrayList<Entity> entities = new ArrayList<>();
		
		for(UUID id : this.entities.keySet()) {
			Entity entity = this.entities.get(id);
			entities.add(entity);
		}
		
		Comparator<Entity> compare = Comparator.comparing(Entity::getRenderLayer).thenComparing(e1 -> {
			return (int) -(-e1.getRenderYOffset()*Tile.TILE_SIZE+e1.getY());
		});
		entities.sort(compare);
		
		boolean attemptSound = false;
		if(System.currentTimeMillis()-lastSoundAttempt>1000) {
			attemptSound = true;
			soundChance-=10;
			lastSoundAttempt = System.currentTimeMillis();
		}
		Random r = new Random();
		
		
		int dimension = (int) (Chunk.CHUNK_SIZE*3*Tile.TILE_SIZE);
		boolean renderParticles = game.settings.getBoolean("displayParticles");
		
		Format lightingFormat = Format.Alpha;
		
		Tile.tileRenderDelta += Gdx.graphics.getDeltaTime();
		
		int translucentX = Integer.MAX_VALUE, translucentY = Integer.MAX_VALUE;
		if(getLocalPlayer()!=null) {
			if(!currentRenderRule.equals(RenderRule.HOUSE_RULE)) {
				translucentX = getLocalPlayer().getTileX();
				translucentY = getLocalPlayer().getTileY()-1;
			}
		}
		ArrayList<LightSource> tileLights = new ArrayList<>();
		ArrayList<Vector2> tileLightsCoordinates = new ArrayList<>();

		int z = 0;

		for(int y = (centerChunk.y+1)*Chunk.CHUNK_SIZE+Chunk.CHUNK_SIZE; y >= (centerChunk.y-1)*Chunk.CHUNK_SIZE; y--) {
			attemptSound = getXStrip(centerChunk, entities, attemptSound, r, renderParticles, translucentX,
					translucentY, tileLights, tileLightsCoordinates, z, y);
		}

		for(int y = (centerChunk.y+1)*Chunk.CHUNK_SIZE+Chunk.CHUNK_SIZE; y >= (centerChunk.y-1)*Chunk.CHUNK_SIZE; y--) {
			for(z = 1; z < Chunk.LAYERS; z++) {
				attemptSound = getXStrip(centerChunk, entities, attemptSound, r, renderParticles, translucentX,
						translucentY, tileLights, tileLightsCoordinates, z, y);
			}
		}

		while(tileLights.size() > 0) {
			LightSource light = tileLights.get(0);
			lights.addLight(tileLightsCoordinates.get(0).x, tileLightsCoordinates.get(0).y, light.getIntensity()*Tile.TILE_SIZE, light.getColor());
			tileLights.remove(0);
			tileLightsCoordinates.remove(0);
		}

		while(entities.size() > 0) {
			entities.get(0).render(game, this);
			entities.remove(0);
		}
	}

	private static class RenderBlock {
		public int x;
		public Tile tile;
		public float alpha;
		public RenderBlock(int x, Tile tile, float alpha) {
			this.x = x;
			this.tile = tile;
			this.alpha = alpha;
		}
		public int getFirst() {
			return x;
		} 
		public Tile getSecond() {
			return tile;
		}
		public float getAlpha() {
			return alpha;
		}
	}

	private boolean getXStrip(Point centerChunk, ArrayList<Entity> entities, boolean attemptSound, Random r,
			boolean renderParticles, int translucentX, int translucentY, ArrayList<LightSource> tileLights,
			ArrayList<Vector2> tileLightsCoordinates, int z, int y) {
		ArrayList<RenderBlock> xStrip = new ArrayList<>();
		for(int x = (centerChunk.x-1)*Chunk.CHUNK_SIZE; x <= (centerChunk.x+1)*Chunk.CHUNK_SIZE+Chunk.CHUNK_SIZE; x++) {
			Tile[][][] data = tileData.get(Chunk.getChunk(x, y));
			if(data == null) {
				continue;
			}
			// implement render rule here
			int tx = Math.floorMod(x, Chunk.CHUNK_SIZE);
			int ty = Math.floorMod(y, Chunk.CHUNK_SIZE);

			float alpha = currentRenderRule.renderTile(data, tx, ty);
			if(alpha != 0) {
				if(data[tx][ty][z] != null) {
					xStrip.add(new RenderBlock(x, data[tx][ty][z], alpha));
				}
			}
			
			//if(currentRenderRule.renderTile(data, tx, ty)) {
			//	if(data[tx][ty][z] != null) {
			//		xStrip.add(new Pair<>(x, data[tx][ty][z]));
			//	}
			//}
		}
		xStrip.sort(Comparator.comparing(pair -> {
			Tile t = ((RenderBlock) pair).getSecond();
			return -t.getRenderYOffset()*Tile.TILE_SIZE;
		}));
		attemptSound = renderXStrip(entities, attemptSound, r, renderParticles, translucentX, translucentY,
			tileLights, tileLightsCoordinates, z, y, xStrip);
		return attemptSound;
	}

	private boolean renderXStrip(ArrayList<Entity> entities, boolean attemptSound, Random r, boolean renderParticles,
			int translucentX, int translucentY, ArrayList<LightSource> tileLights,
			ArrayList<Vector2> tileLightsCoordinates, int z, int y, ArrayList<RenderBlock> xStrip) {
		while(xStrip.size() > 0) {
			RenderBlock pair = xStrip.get(0);
			
			if(attemptSound) {
				if(pair.getSecond().getAmbientNoise(this)!=null) {
					if(soundChance <= 0 || r.nextInt(soundChance)==1) {
						soundChance = 600;
						double distance = getLocalPlayer().getDistanceToTile(pair.getFirst(), y);
						double pow = 1.2;
						
						float volume = (float) CustomMath.minMax(0, ((-1d/Math.pow(Chunk.CHUNK_SIZE*1.5d, pow))*Math.pow(distance, pow))+1d, 1);
						
						game.playSoundCooldown(pair.getSecond().getAmbientNoise(this), 
								volume*pair.getSecond().getAmbientNoiseVolume()*game.settings.getFloat("ambientVolume"),
								pair.getSecond().getAmbientNoisePitch(), 7f);
						attemptSound = false;
					}
				}
			}
			
			while(entities.size() > 0) {
				Entity e = entities.get(0);
				if(z==e.getRenderLayer()&&pair.getSecond().getRenderYOffset()*Tile.TILE_SIZE+(y*Tile.TILE_SIZE)<(e.getRenderYOffset()*Tile.TILE_SIZE+e.getY())) {
					if(e instanceof LightSource) {
						LightSource light = (LightSource) e;
						//tileLights.add(light);
						//tileLightsCoordinates.add(new Vector2((float) e.getX()+light.offsetX(), (float) e.getY()+light.offsetY()));
						lights.addLight((float) e.getX()+light.offsetX(), (float) e.getY()+light.offsetY(), light.getIntensity()*Tile.TILE_SIZE, light.getColor());
					}
					game.batch.setColor(new Color(1f, 1f, 1f, 1f));
					if(e instanceof Particle) {
						if(renderParticles) {
							e.render(game, this);
						}
					} else {
						e.render(game, this);
					}
					entities.remove(0);
					continue;
				}
				break;
			}
			
			if(pair.getSecond() instanceof LightSource) {
				LightSource lightz = (LightSource) pair.getSecond();
				tileLights.add(lightz);
				tileLightsCoordinates.add(new Vector2(pair.getFirst()*Tile.TILE_SIZE+Tile.TILE_SIZE/2, y*Tile.TILE_SIZE+Tile.TILE_SIZE/2));
				//lights.addLight(pair.getFirst()*Tile.TILE_SIZE+Tile.TILE_SIZE/2, y*Tile.TILE_SIZE+Tile.TILE_SIZE/2, lightz.getIntensity()*Tile.TILE_SIZE, lightz.getColor());
			}
			
			boolean translucent = false;
			if(pair.getSecond() instanceof SeeThroughTile) {
				if(pair.getSecond() instanceof BaseTile) {
					BaseTile tile = ((BaseTile)pair.getSecond());
					for(int width = 0; width < tile.getWidth(); width++) {
						for(int height = 0; height < tile.getHeight(); height++) {
							if(z == 1 
									&& (pair.getFirst()+width==translucentX)
									&& (y+height == translucentY || y+height == translucentY+1)
									&& (pair.getSecond() instanceof SeeThroughTile)) {
								translucent = true;
								break;
							}
						}
						if(translucent) {
							break;
						}
					}
				} else {
					if(z == 1 
							&& (pair.getFirst()==translucentX)
							&& (y == translucentY || y == translucentY+1)
							&& (pair.getSecond() instanceof SeeThroughTile)) {
						translucent = true;
					}
				}
			}
			float alpha = pair.getAlpha();
			if(translucent) {
				alpha = .5f;
			}
			game.batch.setColor(new Color(1f, 1f, 1f, alpha));
			
			pair.getSecond().render(game, this, pair.getFirst(), y);
			
			if(translucent) {
				game.batch.setColor(Color.WHITE);
			}
			xStrip.remove(0);
		}
		return attemptSound;
	}

	/**
	 * Snaps the game camera to align with the pixels on the screen
	 * Prevents some weird camera issues
	 * @param playerPosition position of the player
	 */
	private void snapCamera(Vector2Double playerPosition) {
		worldViewport.getCamera().position.set((float) playerPosition.getX(), (float) playerPosition.getY() + Tile.TILE_SIZE / 2, 0);

		Vector3 temp = new Vector3(), temp2 = new Vector3();
		worldViewport.getCamera().getPickRay(0, 1).getEndPoint(temp, 10);
		worldViewport.getCamera().getPickRay(1, 1).getEndPoint(temp2, 10);
		float dist = temp.dst(temp2);

		worldViewport.getCamera().position.x = roundToNearest(worldViewport.getCamera().position.x, dist);
		worldViewport.getCamera().position.y = roundToNearest(worldViewport.getCamera().position.y, dist);
	}

	/**
	 * Processes mouse inputs for the game screen.
	 * 
	 * @param delta the time in seconds since the last frame
	 * @param mouseLocalPosition the local position of the mouse
	 */
	private void processMouseInputs(float delta, Vector2 mouseLocalPosition) {
		if(!overMenus(mouseLocalPosition)) {
			if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
				
				if(inventory.inventoryGroup.selectedSlot!=null) {
					// drop item
					boolean selectedIsPlayerInventory = getLocalPlayer()!=null&&getLocalPlayer().getInventory().equals(inventory.inventoryGroup.selectedSlot.getInventory());
					sender.addPacket(new InventoryClickPacket(selectedIsPlayerInventory, false, inventory.inventoryGroup.selectedSlot.slot, -1, null));
					inventory.inventoryGroup.selectedSlot.getInventory().put(inventory.inventoryGroup.selectedSlot.slot, null);
					inventory.inventoryGroup.selectedSlot = null;
					
				} else {
					
					boolean automaticClick = true;
					leftClickDelta+=delta;
					if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
						leftClickDelta = Float.MAX_VALUE;
						automaticClick = false;
					}
					
					if(leftClickDelta > .1) {
						leftClickDelta = 0;
						Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
						Vector3 output = worldViewport.getCamera().unproject(new Vector3(mouse, 0));
						
						int x = (int) Math.floor(output.x/Tile.TILE_SIZE);
						int y = (int) Math.floor(output.y/Tile.TILE_SIZE);
						
						clientSideTileInteraction(x, y, 0);
						
						Tile at = tileDataObject.getTile(x, y, 1);
						Tile below = tileDataObject.getTile(x, y-1, 1);
						
						int ignoreCeiling = Objects.equals(currentRenderRule, RenderRule.HOUSE_RULE)?1:0;

						boolean skipWall = false;

						int downwardShift = 0;

						if(ignoreCeiling==0) {
							Tile ceiling = tileDataObject.getTile(x, y, 2);
							Tile belowceiling = tileDataObject.getTile(x, y-1, 2);
							boolean atIsCeiling = ceiling instanceof CeilingTile;
							boolean belowIsCeiling = belowceiling instanceof CeilingTile;

							if(belowIsCeiling) {
								downwardShift = 1;
								skipWall = true;
							}
							if(atIsCeiling && !belowIsCeiling && at != null) {
								ignoreCeiling = 1;
								skipWall = true;
							}
						}

						skipWall = Objects.equals(currentRenderRule, RenderRule.HOUSE_RULE) || skipWall;

						if(!skipWall) {
							if(at instanceof WallTile) {
								if(below instanceof WallTile) {
									downwardShift = 1;
								}
							} else if(below instanceof WallTile) {
								downwardShift = 1;
							}

							if(below instanceof WallTile && getLocalPlayer().getTileCoordinates().equals(new Point(x, y))) {
								if(at!=null) {
									downwardShift = 0;
								}
							}
						}
						
						output.add(0, Tile.TILE_SIZE*-downwardShift, 0);
						
						sender.addPacket(new PlayerInteractPacket(automaticClick, 0, (int) output.x, (int) output.y, -1, ignoreCeiling));
					}
				}
			} else {
				leftClickDelta = 0;
			}
			
			if(Gdx.input.isButtonPressed(Input.Buttons.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.V)) {
				
				boolean automaticClick = true;
				
				/*if(!(Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.V))) {
					if(getLocalPlayer()!=null&&(getLocalPlayer().getHeldItem()instanceof Placeable||getLocalPlayer().getHeldItem() instanceof Tool)) {
						rightClickDelta+=delta;
					}
				} else {
					//rightClickDelta = Integer.MAX_VALUE;
					automaticClick = false;
				}*/
				
				if((Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.V))) {
					rightClickDelta = Integer.MAX_VALUE/2;
					automaticClick = false;
				}
				if(getLocalPlayer()!=null&&(getLocalPlayer().getHeldItem()instanceof Placeable||getLocalPlayer().getHeldItem() instanceof Tool)) {
					rightClickDelta+=delta;
				}
				
				if(rightClickDelta > .2) {
					rightClickDelta = 0;
					Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
					Vector3 output = worldViewport.getCamera().unproject(new Vector3(mouse, 0));
					
					int modifier = -1;
					
					Item held;
					if(getLocalPlayer()!=null&&(held=getLocalPlayer().getHeldItem())instanceof Placeable) {
						Tile toBePlaced = ((Placeable)held).getTempPlaceTile(rotation);
						if(toBePlaced instanceof Rotatable) {
							modifier = rotation;
						}
					}
					
					sender.addPacket(new PlayerInteractPacket(automaticClick, 2, (int) output.x, (int) output.y, modifier, Objects.equals(currentRenderRule, RenderRule.HOUSE_RULE)?1:0));
					
					int x = (int) Math.floor(output.x/Tile.TILE_SIZE);
					int y = (int) Math.floor(output.y/Tile.TILE_SIZE);
					
					clientSideTileInteraction(x, y, 2);
				}
			} else {
				rightClickDelta = 0;
			}
		}
	}

	/**
	 * Processes player movement based on user input and updates the player and local client entities accordingly.
	 * @param delta The time elapsed since the last frame.
	 */
	private void processEntitiesAndPlayerMovement(float delta) {
		float speed = 1;
		float ix = 0, iy = 0;


		if(chatWidget.getStage().getKeyboardFocus()==null) {
			if(Gdx.input.isKeyPressed(Input.Keys.W)) {
				iy += speed;
			}
			if(Gdx.input.isKeyPressed(Input.Keys.S)) {
				iy -= speed;
			}
			if(Gdx.input.isKeyPressed(Input.Keys.D)) {
				ix += speed;
			}
			if(Gdx.input.isKeyPressed(Input.Keys.A)) {
				ix -= speed;
			}
		}
		
		Vector2 clamp = CustomMath.clampCircle(ix, iy, 1, .1f);
		ix = clamp.x;
		iy = clamp.y;
		
		boolean sprint = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);
		
		if(lastX !=ix || lastY != iy || lastSprint != sprint) {
			//this.previousPlayerPositions.put(System.currentTimeMillis(), new Vector2Double(getLocalPlayer().getX(), getLocalPlayer().getY()));
			sender.addPacket(new PlayerInputPacket(ix, iy, sprint));
		}
		if(logPositionDelta>1/10f) {
			if(getLocalPlayer()!=null) {
				cvm.put(System.currentTimeMillis(), new Vector2Double(getLocalPlayer().getX(), getLocalPlayer().getY()));
				logPositionDelta = 0;
			}
		}
		
		lastX = ix;
		lastY = iy;
		lastSprint = sprint;
		
		for(UUID uuid : entities.keySet()) {
			Entity entity = entities.get(uuid);
			if(entity.equals(getLocalPlayer())) {
				Player p = (Player) entity;
				p.setSprinting(sprint);
				entity.setVelocity(ix*p.getSpeed(), iy*p.getSpeed());
			}
			entity.update(delta, tileDataObject);
		}
	}

	/**
	 * Processes the network connection by building incoming packets, sending ping packets, and sending queued packets out to the server.
	 * 
	 * @param delta the time in seconds since the last frame
	 */
	private void processConnection(float delta) {
		try {
			builder.build(dataIn);
			pingDelta+=delta;
			if(pingDelta>=pingTime) {
				sender.addPacket(new PingPacket(System.currentTimeMillis(), false));
				pingDelta = 0;
			}
			
			sender.sendAll(dataOut);
		} catch(IOException e) {
			if(!e.getMessage().contains("closed")) {
				e.printStackTrace();
				game.setScreen(new MessageScreen(game, e.getMessage()));
			}
		} catch(Exception e) {
			e.printStackTrace();
			game.setScreen(new MessageScreen(game, e.getMessage()));
		}
	}

	private void clientSideTileInteraction(int x, int y, int clickType) {
		if(getLocalPlayer()!=null && getLocalPlayer().getDistanceToTile(x, y) < getLocalPlayer().getInteractRadius()) {
			for(int i = Chunk.LAYERS-1; i >= 0; i--) {
				Tile at = tileDataObject.getTile(x, y, i);
				
				if(at instanceof StructureTile) {
					at = ((StructureTile)at).getBaseTile(tileDataObject, x, y);
				}
				
				if(at != null) {
					boolean action = at.onInteractClient(this, clickType);
					if(action) {
						break;
					}
				}
			}
		}
	}
	
	@Override
	public void resize(int width, int height) {
		uiViewport.update(width, height);
		bottomViewport.update(width, height);
		bottomViewport.setScreenY(0);
		bottomViewport.getCamera().position.set(640f/2f, 360f/2f*((OrthographicCamera)bottomViewport.getCamera()).zoom, 0);
		worldViewport.update(width, height);
		topStage.getViewport().update(width, height);
		topViewport.setScreenY(Gdx.graphics.getHeight()-topViewport.getScreenHeight());

		rightTopViewport.update(width, height);
		rightTopViewport.setScreenY(Gdx.graphics.getHeight()-rightTopViewport.getScreenHeight());
		rightTopViewport.setScreenX(Gdx.graphics.getWidth()-rightTopViewport.getScreenWidth());

		leftTopViewport.update(width, height);
		leftTopViewport.setScreenX(0);
		leftTopViewport.setScreenY(Gdx.graphics.getHeight()-leftTopViewport.getScreenHeight());

		leftBottomViewport.update(width, height);
		leftBottomViewport.setScreenX(0);
		leftBottomViewport.setScreenY(0);
		
		if (frameBuffer != null && (frameBuffer.getWidth() != width || frameBuffer.getHeight() != height)) {
			frameBuffer.dispose();
			frameBuffer = null;
		}

		if(width==0||height==0) {
			frameBuffer = new FrameBuffer(Pixmap.Format.RGB565, 1, 1, false);
			return;
		}
		
		if (frameBuffer == null) {
			try {
				frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
			} catch (Exception e) {
				frameBuffer = new FrameBuffer(Pixmap.Format.RGB565, width, height, false);
			}
		}
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		//closeNetworkFeatures();
	}

	@Override
	public void dispose() {
		closeNetworkFeatures();
		HintMenuType.saveViewedHints();
		shapeRenderer.dispose();
		bottomStage.dispose();
		centerStage.dispose();
		topStage.dispose();
		rightTopStage.dispose();
		leftTopStage.dispose();
		frameBuffer.dispose();
	}

	
	public void closeNetworkFeatures() {
		sender.sendPacketInstant(dataOut, new DisconnectPacket(GameServer.DISCONNECT_GENERIC));
		try {
			// allows for the disconnect packet to go through
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(server != null) {server.stopServer();}
		try {
			dataOut.close();
			dataIn.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isConnected() {
		return !(System.currentTimeMillis()-builder.millisTimeSinceDataRecieved > GameServer.TIMEOUT_TIME || socket.isClosed());
	}
	
	public void processIncomingPacket(Packet packet) {
		if(packet instanceof PingPacket) {
			PingPacket ping = (PingPacket) packet;
			if(ping.isServerPing()) {
				sender.sendPacketInstant(dataOut, ping);
			} else {
				latency = (System.currentTimeMillis()-ping.getTime())/2;
			}
		} else if(packet instanceof DisconnectPacket) {
			closeNetworkFeatures();
			game.setScreen(new MessageScreen(game, (((DisconnectPacket)packet).getMessage())));
		/*} else if(packet instanceof PositionUpdatesPacket) {
			PositionUpdatesPacket posPacket = (PositionUpdatesPacket) packet;
		*/} else if(packet instanceof PlayerConnectionStatusPacket) {
			PlayerConnectionStatusPacket status = (PlayerConnectionStatusPacket) packet;
			chat.addMessage(new ChatMessage("", status.getUsername()+" "+status.getStatus()));
		} else if(packet instanceof ChunkDataPacket) {
			ChunkDataPacket chunkD = (ChunkDataPacket) packet;
			tileData.put(chunkD.getChunkCoordinate(), chunkD.getTiles());
			for(Entity ent : chunkD.getChunkEntities()) {
				entities.put(ent.getUUID(), ent);
			}
		} else if(packet instanceof ChunkUnloadPacket) {
			ChunkUnloadPacket unload = (ChunkUnloadPacket) packet;
			tileData.remove(unload.getCoordinate());
			
			for(UUID id : entities.keySet()) {
				Entity at = entities.get(id);
				// commented out for debug purposes
				if(!(at instanceof Player) && at.getChunk().equals(unload.getCoordinate())) {
					entities.remove(id);
				}
			}
			
		} else if(packet instanceof TileUpdatePacket) {
			TileUpdatePacket upd = (TileUpdatePacket) packet;
			Point chunk = Chunk.getChunk(upd.getX(), upd.getY());
			if(tileData.containsKey(chunk)) {
				Tile[][][] data = tileData.get(chunk);
				Point inside = Chunk.getInsideChunkCoordinates(upd.getX(), upd.getY());
				data[inside.x][inside.y][upd.getLayer()] = upd.getTile();
				if(upd.getTile() instanceof TransitionTile) {
					((TransitionTile)upd.getTile()).updateSprites(game, this, upd.getX(), upd.getY());
				}
			}
			int x = upd.getX();
			int y = upd.getY();
			for (int v = 0; v < 4; v++) {
				
				int tx = 0, ty = 0;
				
				switch (v) {
				case(0): {
					tx = 1;
					ty = 0;
					break;
				}
				case(1): {
					tx = 0;
					ty = 1;
					break;
				}
				case(2): {
					tx = -1;
					ty = 0;
					break;
				}
				case(3): {
					tx = 0;
					ty = -1;
					break;
				}
				}
				Point relative = Chunk.getInsideChunkCoordinates(x+tx, y+ty);
				Tile[][][] c = tileData.get(Chunk.getChunk(x+tx, y+ty));
				
				if (c == null) {
					continue;
				}

				Tile at = c[relative.x][relative.y][upd.getLayer()];

				if (at instanceof TransitionTile) {
					((TransitionTile) at).updateSprites(game, this, x+tx, y+ty);
				}
			}
		} else if(packet instanceof SpawnEntity) {
			SpawnEntity spawn = (SpawnEntity) packet;
			Entity entity = spawn.getEntity();
			entities.put(entity.getUUID(), entity);
			if(entity instanceof Player) {
				Player player = (Player) entity;
				//System.out.println("RECEIVED PLAYER, OBF: "+player.getX()+","+player.getY());
				if(player.equals(getLocalPlayer())) {
					quests.setQuestData(player.getQuestData());
					barsWidget.setHealth(((HealthEntity)player).getHealth(), ((HealthEntity)player).getMaxHealth());
					barsWidget.setFood(player.getHunger()/Player.MAX_HUNGER);
					getLocalPlayer().setHunger(player.getHunger());
					getLocalPlayer().setHealth(player.getHealth());
					for(InventorySlot slot : hotbarButtonGroup.getButtons()) {
						if(slot.getSlot()==player.getHotbarSlot()) {
							slot.setChecked(true);
							break;
						}
					}
				}
			}
		} else if(packet instanceof DespawnEntity) {
			DespawnEntity despawn = (DespawnEntity) packet;
			UUID despawnID = despawn.getUUID();
			entities.remove(despawnID);
		} else if(packet instanceof EntityPositionsPacket) {
			EntityPositionsPacket entityPositions = (EntityPositionsPacket) packet;
			for(UUID uuid : entities.keySet()) {
				Vector2Double incomingData = entityPositions.getPositions().get(uuid);
				Vector2Double incomingVelocity = entityPositions.getVelocities().get(uuid);
				Entity entity = entities.get(uuid);
				
				if(entity == null) {
					continue;
				}
				
				if(incomingData==null) {
					if(entity instanceof Player) {
						entity.setPosition(Double.MAX_VALUE, Double.MAX_VALUE);
						entity.setVelocity(0, 0);
					}
				} else {
					// do position correction if positions are out of sync
					if(entity instanceof Player) {
						if(!entity.equals(getLocalPlayer())) {
							entity.setVelocity(incomingVelocity.getX(), incomingVelocity.getY());
							entity.setPosition(incomingData.getX(), incomingData.getY());
						} else /*if(!serverSinglePlayer) */{
							entity.setVelocity(incomingVelocity.getX(), incomingVelocity.getY());
							
							long millisClosestLast = System.currentTimeMillis()-(latency/2l);
							
							int index = cvm.getClosestIndexOf(millisClosestLast);
							Vector2Double oldPosition = cvm.get(millisClosestLast);
							
							final double threshold = Tile.TILE_SIZE/(2f*(game.settings.getBoolean("connectionThreshold")?.5f:1));
							
							if(oldPosition!=null) {
								//System.out.println(cvm.size()+","+oldPosition);
								
								if(Math.hypot(oldPosition.getX()-incomingData.getX(), oldPosition.getY()-incomingData.getY()) > threshold) {
									entity.setPosition(incomingData.getX(), incomingData.getY());
									//System.out.println("SET THE POSITION");
								}
							}
							
							for(int i = 0; i < index; i++) {
								cvm.getInternalList().remove(0);
							}
							
							/*if(entity.getVelocityX()==incomingVelocity.getX() && entity.getVelocityY()==incomingVelocity.getY()) {
								if((Math.abs(entity.getX()-incomingData.getX()) > (Tile.TILE_SIZE)*(latency/500d) || Math.abs(entity.getY()-incomingData.getY()) > (Tile.TILE_SIZE)*(latency/500d))) {
									//entity.setPosition(incomingData.getX(), incomingData.getY());
									
									long millisClosestLast = System.currentTimeMillis()-(latency/2l);
									
									int index = cvm.getClosestIndexOf(millisClosestLast);
									Vector2Double oldPosition = cvm.get(millisClosestLast);
									if(oldPosition!=null) {
										System.out.println(cvm.size()+","+oldPosition);
									}
									
									for(int i = 0; i < index; i++) {
										cvm.getInternalList().remove(0);
									}
								}
							}*/
						}
						
					} else {
						entity.setVelocity(incomingVelocity.getX(), incomingVelocity.getY());
						entity.setPosition(incomingData.getX(), incomingData.getY());
					}
					entity.setLastServerData(incomingData.getX(), incomingData.getY(), incomingVelocity.getX(), incomingVelocity.getY());
					
				}
				
				// DEBUG
				if(entity instanceof Player) {
					//System.out.println("ENTPOS:"+((Player)entity).getUsername()+":"+entity.getX()+","+entity.getY());
				}
				
			}
		} else if(packet instanceof AudioPacket) {
			AudioPacket audio = (AudioPacket) packet;
			game.playSound(audio.getAudio(), audio.getVolume());
			//System.out.println(audio.getAudio()+":"+audio.getVolume());
		} else if(packet instanceof InventoryUpdatePacket) {
			InventoryUpdatePacket inv = (InventoryUpdatePacket) packet;
			Player target = (Player) entities.get(inv.getUUID());
			target.getInventory().set(inv.getInventory().getAll());
		} else if(packet instanceof HotbarUpdatePacket) {
			HotbarUpdatePacket hotbar = (HotbarUpdatePacket) packet;
			Entity entity = entities.get(hotbar.getEntityID());
			if(entity != null && entity instanceof Player) {
				Player player = (Player) entity;
				player.setHotbarSlot(hotbar.getSlot());
				openHelpMenu(HintMenuType.ROTATION, true);
			}
		} else if(packet instanceof PositionPacket) {
			PositionPacket pos = (PositionPacket) packet;
			//System.out.println("SETTING PLAYER POSITION ON CLIENT SIDE: "+pos.getX()+","+pos.getY());
			getLocalPlayer().setPosition(pos.getX(), pos.getY());
			getLocalPlayer().setLastServerData(pos.getX(), pos.getY(), getLocalPlayer().lastServerVelX(), getLocalPlayer().lastServerVelY());
		} else if(packet instanceof WorldChangePacket) {
			WorldChangePacket change = (WorldChangePacket) packet;
			currentWorldType = change.getType();
			worldTime = change.getTime();
			tileData.clear();
			entities.clear();
			
		} else if(packet instanceof CraftingStationPacket) {
			CraftingStationPacket cspack = (CraftingStationPacket) packet;
			if(cspack.getStation()==CraftingStation.NONE) {
				setCraftingStation(CraftingStation.NONE);
				setMenu(MenuType.CRAFTING, false);
			} else {
				setCraftingStation(cspack.getStation());
				//setCraftingVisibility(true);
				setMenu(MenuType.CRAFTING, true);
			}
			
			//setMenu(MenuType.CRAFTING, false);
			chestInventory.setVisible(false);
			chestInventory.setChestInventory(0, 0);
		} else if(packet instanceof SkillsPacket) {
			SkillsPacket sklz = (SkillsPacket) packet;
			
			Entity entity = entities.get(sklz.getUUID());
			if(entity instanceof Player) {
				Player player = (Player) entity;
				
				int[] array = new int[Skill.values().length];
				
				for(int i = 0; i < Skill.values().length; i++) {
					array[i] = player.getSkills().getLevel(Skill.values()[i]);
				}
				
				player.overrideSkills(sklz.getSkills());
				
				if(player.equals(getLocalPlayer())) {
					for(int i = 0; i < Skill.values().length; i++) {
						int newLevel = player.getSkills().getLevel(Skill.values()[i]);
						if(newLevel>array[i]) {
							String[] text = new String[] {Skill.values()[i].name()+" LEVEL UP!\n"+array[i]+" -> "+newLevel, "New recipes unlocked!"};
							String[] sprites = new String[] {"upArrow"};
							boolean[] animation = new boolean[] {true};
							addNotification(text, sprites, animation, 3f, 5f, "intro");
							if(Skill.values()[i]==Skill.FORAGING) {
								openHelpMenu(HintMenuType.LEVEL_UP);
							}
						}
					}
					skillDisplay.updateValues();
				}
				
			}
		} else if(packet instanceof HealthPacket) {
			HealthPacket healths = (HealthPacket) packet;
			Entity entity = entities.get(healths.getEntityUUID());
			if(entity instanceof HealthEntity) {
				HealthEntity hent = ((HealthEntity)entity);
				float incomingHealth = hent.getHealth();
				hent.setHealth(healths.getHealth());
				if(entity.equals(getLocalPlayer())) {
					barsWidget.setHealth(hent.getHealth(), hent.getMaxHealth());
					float difference = healths.getHealth()-incomingHealth;
					float threshold = 1;
					if(difference <= -threshold) {
						game.playSoundCooldown("hurt", .5f, .4f);
					} else if(difference >= threshold) {
						game.playSoundCooldown("heal", .5f, 1f);
					}
				}
			}
		} else if(packet instanceof FoodPacket) {
			FoodPacket foods = (FoodPacket) packet;
			if(getLocalPlayer()!=null) {
				barsWidget.setFood(foods.getHunger()/Player.MAX_HUNGER);
				getLocalPlayer().setHunger(foods.getHunger());
			}
		} else if(packet instanceof ChestInventoryPacket) {
			ChestInventoryPacket cip = (ChestInventoryPacket) packet;
			chestInventory.setChestInventory(cip.getX(), cip.getY());
			setCraftingStation(CraftingStation.NONE);
			openMenu(MenuType.CHEST);
		} else if(packet instanceof QuestDataPacket) {
			QuestDataPacket qdata = (QuestDataPacket) packet;
			quests.updateQuestData(qdata);
			if(qdata.getValue()) {
				addNotification(
						new String[] {"Quest COMPLETED!",
								""+qdata.getQuest().getPreReq(qdata.getQuestTier(), qdata.getQuestNumber())}, 
						new String[] {"upArrow"}, 
						new boolean[] {true},
						2f, 4f, "trueSelect");
			}
		} else if(packet instanceof ChatPacket) {
			ChatPacket chat = (ChatPacket) packet;
			this.chat.addMessage(chat.getMessage());
		}
	}
	
	boolean craftingMenuVisible;
	
	public void setCraftingVisibility(boolean visible) {
		craftingMenuVisible = visible;
		crafting.setVisible(visible);
		recipeDisplay.setVisible(visible);
		craftDisplay.setVisible(visible);
		categoryScrollPane.setVisible(visible);
	}
	public void setCraftingStation(CraftingStation station) { 
		recipeDisplay.setCraftingStation(station);
		
		Sprite left = game.getSprite(station.leftDisplayName);
		Drawable leftDraw;
		if(left != null) {
			leftDraw = new TextureRegionDrawable(left);
		} else {
			leftDraw = new TextureRegionDrawable(game.getSprite("craftingLeft"));
		}
		craftingLeft.setDrawable(leftDraw);
		
		Sprite right = game.getSprite(station.rightDisplayName);
		Drawable rightDraw;
		if(right != null) {
			rightDraw = new TextureRegionDrawable(right);
		} else {
			rightDraw = new TextureRegionDrawable(game.getSprite("craftingRight"));
		}
		craftingRight.setDrawable(rightDraw);
	}
	
	UUID localPlayerUUID;
	public Player getLocalPlayer() {
		if(localPlayerUUID != null) {
			Player player = (Player) entities.get(localPlayerUUID);
			if(player == null) {
				localPlayerUUID = null;
			} else {
				return player;
			}
		}
		for(Entity entity : entities.values()) {
			if(entity instanceof Player) {
				Player player = (Player) entity;
				if(player.getUsername().equals(username)) {
					localPlayerUUID = player.getUUID();
					break;
				}
			}
		}
		if(localPlayerUUID == null) {
			return null;
		}
		return (Player) entities.get(localPlayerUUID);
	}
	
	public boolean overMenus(Vector2 mouseLocalPosition) {
		boolean end = false;
		
		end = end || inventory.hit(mouseLocalPosition.x, mouseLocalPosition.y, false) != null;
		end = end || crafting.hit(mouseLocalPosition.x, mouseLocalPosition.y, false) != null;
		end = end || hotbarTable.hit(mouseLocalPosition.x, mouseLocalPosition.y, false) != null;
		end = end || pauseMenu.hit(mouseLocalPosition.x, mouseLocalPosition.y, false) != null;
		end = end || chestInventory.hit(mouseLocalPosition.x, mouseLocalPosition.y, false) != null;
		end = end || quests.hit(mouseLocalPosition.x, mouseLocalPosition.y, false) != null;
		end = end || tech.hit(mouseLocalPosition.x, mouseLocalPosition.y, false) != null;
		
		return end;
	}
	
    public static float roundToNearest(float input, float snapValue) {
    	return input;
    }
    
    private static Viewport hoveredItemViewport;
    private static Item hoveredItem;
    private static Vector2 hoveredItemLocalViewportCoordinates;
    
    public static void setHoveredItem(Viewport viewport, Item item, Vector2 localViewportCoordinates) {
    	hoveredItemViewport = viewport;
    	hoveredItem = item;
    	hoveredItemLocalViewportCoordinates = localViewportCoordinates;
    }
    
    public float getAmbientLight() {
    	//return 1;
    	
    	if(gammaOverride) {
    		return 1;
    	}
		if(Objects.equals(currentRenderRule, RenderRule.HOUSE_RULE)) {
			return 0;
		}
    	
    	float value = 0;
    	if(currentWorldType==null) {
    		value = 0;
    	} else {
    		if(currentWorldType.isUnderground()) {
    			value = 0;
    		} else {
    			value = calculateAmbience(.1f, .8f, .025f, 60f, (float) worldTime);
    		}
    	}
    	return value;
    }
    
    public float calculateAmbience(float min, float max, float rate, float duration, float time) {
    	float transitionTime = (max-min)/rate;
    	float x = time%(2*duration+2*transitionTime);
    	
    	if(x < duration) {
    		return max;
    	}
    	if(x < duration+transitionTime) {
    		return (-rate*(x-duration)+max);
    	}
    	if(x < 2*duration+transitionTime) {
    		return min;
    	}
    	return (rate*(x-2*duration-transitionTime)+min);
    }
    
	/**
	 * This method is contextually aware and will open the correct help menu depending on the player's situation.
	 * 
	 * @param type MAY BE NULL, will provide context to the method.
	 * 
	 */
	public void openHelpMenu(HintMenuType type, boolean dontShowAgain) {
		
		if(type == null) {
			
			if(getLocalPlayer()!=null
					&&getLocalPlayer().getHeldItem() instanceof Placeable
					&&Tiles.getTile(((Placeable)getLocalPlayer().getHeldItem()).getTileToPlace()) instanceof Rotatable) {
				type = HintMenuType.ROTATION;
			} else if(crafting.isVisible()) {
				type = HintMenuType.CRAFTING;
			} else if(inventory.isVisible()) {
				type = HintMenuType.INVENTORY;
			} else if(getLocalPlayer()!=null&&getLocalPlayer().getSkills().getLevel(Skill.FORAGING)>1) {
				type = HintMenuType.LEVEL_UP;
			} else {
				type = HintMenuType.FIRST_JOIN;
			}
			
		}
		
		Objects.requireNonNull(type);
		
		if(dontShowAgain && type.viewed) {
			return;
		}
		if(dontShowAgain && !game.settings.getBoolean("showHints")) {
			return;
		}
		
		switch(type) {
		case FIRST_JOIN:
			centerStage.addActor(new HintMenu(game, type, 10f, 360-10-100, 230, 100, 8f));
			break;
		case INVENTORY:
			centerStage.addActor(new HintMenu(game, type, 10f, 360-10-120, 250, 120, 8f));
			break;
		case CRAFTING:
			centerStage.addActor(new HintMenu(game, type, (640-270f)/2f, 360-10-120, 270, 120, 8f));
			break;
		case LEVEL_UP:
			centerStage.addActor(new HintMenu(game, type, (640-270f)/2f, (360-200)/2f, 270, 200, 8f));
			break;
		case ROTATION:
			centerStage.addActor(new HintMenu(game, type, 10f, 360-10-100, 230, 100, 8f));
			break;
		default:
			break;
		}
	}
	
	public void openHelpMenu(HintMenuType type) {
		openHelpMenu(type, true);
	}
	
	/*public void closeCentralMenu() {
		for(MenuType type : MenuType.values()) {
			type.setVisible(this, false);
		}
	}*/

	static enum MenuGroup {
		CENTER {

		},
		SIDES {

		};

		public void closeGroup(GameScreen screen) {
			for(MenuType type : MenuType.values()) {
				if(type.group == this) {
					type.setVisible(screen, false);
				}
			}
		}
	}

	public static enum MenuType {
		INVENTORY(MenuGroup.CENTER) {
			@Override
			public void setVisible(GameScreen screen, boolean visible) {
				screen.inventory.setVisible(visible);
				screen.inventory.inventoryGroup.uncheckAll();
				screen.inventory.inventoryGroup.selectedSlot = null;
			}
		},
		CRAFTING(MenuGroup.SIDES) {
			@Override
			public void setVisible(GameScreen screen, boolean visible) {
				screen.setCraftingVisibility(visible);
			}
		},
		QUESTS(MenuGroup.CENTER) {
			@Override
			public void setVisible(GameScreen screen, boolean visible) {
				screen.quests.setVisible(visible);
			}
		},
		CHEST(MenuGroup.CENTER) {
			@Override
			public void setVisible(GameScreen screen, boolean visible) {
				screen.chestInventory.setVisible(visible);
			}
		}, 
		TECH(MenuGroup.CENTER) {
			@Override
			public void setVisible(GameScreen screen, boolean visible) {
				screen.tech.setVisible(visible);
			}
		};

		MenuGroup group;
		MenuType(MenuGroup group) {
			this.group = group;
		}

		public abstract void setVisible(GameScreen screen, boolean visible);
	}

	public void toggleMenu(MenuType type) {
		boolean visible = false;
		switch(type) {
		case INVENTORY:
			visible = inventory.isVisible();
			break;
		case CRAFTING:
			visible = crafting.isVisible();
			break;
		case QUESTS:
			visible = quests.isVisible();
			break;
		case CHEST:
			visible = chestInventory.isVisible();
			break;
		case TECH:
			visible = tech.isVisible();
			break;
		default:
			break;
		}
		type.group.closeGroup(this);
		type.setVisible(this, !visible);
	}

	public void openMenu(MenuType type) {
		type.group.closeGroup(this);
		type.setVisible(this, true);
	}
	public void setMenu(MenuType type, boolean state) {
		if(state) {
			type.group.closeGroup(this);
		}
		type.setVisible(this, state);
	}

	public RenderRule currentRenderRule;

	public RenderRule deriveRenderRule() {
		Player localPlayer = getLocalPlayer();
		RenderRule.delta += Gdx.graphics.getDeltaTime();
		RenderRule.delta = Math.min(RenderRule.delta, 1);
		if(localPlayer!=null) {
			int tx = localPlayer.getTileX();
			int ty = localPlayer.getTileY();
			Tile[][][] data = tileData.get(Chunk.getChunk(tx, ty));
			if(data == null) {
				return RenderRule.DEFAULT_RULE;
			}
			int rx = Math.floorMod(tx, Chunk.CHUNK_SIZE);
			int ry = Math.floorMod(ty, Chunk.CHUNK_SIZE);
			if(data[rx][ry][2] instanceof CeilingTile) {
				return RenderRule.HOUSE_RULE;
			}
		}
		return RenderRule.DEFAULT_RULE;
	}
    
    public void addLevelUpNotification() {
    	String[] text = new String[] {"MINING LEVEL UP!\n2 -> 3", "New recipes unlocked!"};
		String[] sprites = new String[] {"furnace", "chest", "chute", "anvil"};
		addNotification(text, sprites, 3f, 5f, "intro");
    }
    
    Queue<Notification> notificationQueue = new LinkedList<>();
    Notification activeNotification = null;
    
    public void addNotification(String[] text, String[] sprites, float textTime, float duration, String soundEffect) {
    	Notification notif = new Notification(game, text, sprites, textTime, Notification.getEvenlySpacedTime(sprites, duration), duration, soundEffect);
		notif.setDirection(true);
		notif.setPosition(640, 180);
		
		notificationQueue.add(notif);
    }
    public void addNotification(String[] text, String[] sprites, boolean[] animations, float textTime, float duration, String soundEffect) {
    	Notification notif = new Notification(game, text, sprites, animations, textTime, Notification.getEvenlySpacedTime(sprites, duration), duration, soundEffect);
		notif.setDirection(true);
		notif.setPosition(640, 180);
		
		notificationQueue.add(notif);
    }

	public void sendChatMessage(String message) {
		sender.addPacket(new ChatPacket(new ChatMessage("", message)));
	}
    
}
