package net.cmr.gaze.stage;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
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
import net.cmr.gaze.networking.packets.ChestInventoryPacket;
import net.cmr.gaze.networking.packets.ChunkDataPacket;
import net.cmr.gaze.networking.packets.ChunkUnloadPacket;
import net.cmr.gaze.networking.packets.CraftingStationPacket;
import net.cmr.gaze.networking.packets.DespawnEntity;
import net.cmr.gaze.networking.packets.DisconnectPacket;
import net.cmr.gaze.networking.packets.EntityPositionsPacket;
import net.cmr.gaze.networking.packets.HotbarUpdatePacket;
import net.cmr.gaze.networking.packets.InventoryClickPacket;
import net.cmr.gaze.networking.packets.InventoryUpdatePacket;
import net.cmr.gaze.networking.packets.PingPacket;
import net.cmr.gaze.networking.packets.PlayerConnectionStatusPacket;
import net.cmr.gaze.networking.packets.PlayerInputPacket;
import net.cmr.gaze.networking.packets.PlayerInteractPacket;
import net.cmr.gaze.networking.packets.PositionPacket;
import net.cmr.gaze.networking.packets.SpawnEntity;
import net.cmr.gaze.networking.packets.TileUpdatePacket;
import net.cmr.gaze.networking.packets.UIEventPacket;
import net.cmr.gaze.networking.packets.WorldChangePacket;
import net.cmr.gaze.stage.widgets.ChestInventoryWidget;
import net.cmr.gaze.stage.widgets.HintMenu;
import net.cmr.gaze.stage.widgets.HintMenu.HintMenuType;
import net.cmr.gaze.stage.widgets.Notification;
import net.cmr.gaze.stage.widgets.PauseMenu;
import net.cmr.gaze.stage.widgets.PlayerInventoryWidget;
import net.cmr.gaze.util.ClosestValueMap;
import net.cmr.gaze.util.CustomMath;
import net.cmr.gaze.util.CustomTime;
import net.cmr.gaze.util.Pair;
import net.cmr.gaze.util.Vector2Double;
import net.cmr.gaze.world.BaseTile;
import net.cmr.gaze.world.Chunk;
import net.cmr.gaze.world.LightSource;
import net.cmr.gaze.world.Lights;
import net.cmr.gaze.world.Rotatable;
import net.cmr.gaze.world.SeeThroughTile;
import net.cmr.gaze.world.StructureTile;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileData;
import net.cmr.gaze.world.TransitionTile;
import net.cmr.gaze.world.WallTile;
import net.cmr.gaze.world.WorldGenerator.WorldGeneratorType;
import net.cmr.gaze.world.entities.Entity;
import net.cmr.gaze.world.entities.Player;

@SuppressWarnings("deprecation")
public class GameScreen implements Screen {

	public final Gaze game;
	Viewport uiViewport, topViewport, bottomViewport, worldViewport, rightTopViewport;
	Stage bottomStage, centerStage, topStage, rightTopStage;
	
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
	
	ConcurrentHashMap<UUID, Entity> entities;
	
	FrameBuffer frameBuffer;
	Lights lights;
	
	long latency = 0;
	double worldTime = 0;
	boolean showUI = true;
	int rotation = 0;
	
	WorldGeneratorType currentWorldType;
	
	//HashMap<Long, Vector2Double> previousPlayerPositions = new HashMap<>();
	ClosestValueMap<Long, Vector2Double> cvm = new ClosestValueMap<Long, Vector2Double>();
	
	public GameScreen(final Gaze game, String username, Socket socket, DataInputStream dataIn, DataOutputStream dataOut, GameServer server, boolean singlePlayer) {
		this.game = game;
		this.serverSinglePlayer = singlePlayer;
		this.entities = new ConcurrentHashMap<>();
		Preferences prefs = SettingScreen.initializePreferences();
		lights = new Lights();
		
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
		
		this.socket = socket;
		this.dataIn = dataIn;
		this.dataOut = dataOut;
		
		InputMultiplexer multiInput = new InputMultiplexer();
		
		bottomStage = new Stage(bottomViewport);
		centerStage = new Stage(uiViewport);
		topStage = new Stage(topViewport);
		rightTopStage = new Stage(rightTopViewport);
		
		int width = 30;
		int spacing = 10;
		
		int totalWidth = (width*7)+(spacing*6);
		
		hotbarTable = new Table();
		hotbarTable.setWidth(totalWidth);
		hotbarTable.setHeight(width);
		hotbarButtonGroup = new ButtonGroup<InventorySlot>();
		hotbarButtonGroup.setMinCheckCount(1);
		hotbarButtonGroup.setMaxCheckCount(1);
		
		
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
					}
				}
			});
			
			hotbarTable.add(button).width(width).height(width).spaceRight(spacing);
		}

		hotbarTable.setBounds(320-totalWidth/2, 26.5f, totalWidth, width);
		
		bottomStage.addActor(hotbarTable);
		
		//inventory = new PlayerInventoryWidget(game, this);
		inventory = new PlayerInventoryWidget(game, this);
		chestInventory = new ChestInventoryWidget(game, this);
		crafting = new WidgetGroup();
		
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
		
		pauseMenu = new PauseMenu(game, this);
		skillDisplay = new SkillDisplay(game, this);
		
		centerStage.addActor(chestInventory);
		centerStage.addActor(inventory);
		centerStage.addActor(crafting);
		centerStage.addActor(recipeDisplay);
		centerStage.addActor(categoryScrollPane);
		centerStage.addActor(craftDisplay);
		centerStage.addActor(pauseMenu);
		rightTopStage.addActor(skillDisplay);
		
		openHelpMenu(HintMenuType.FIRST_JOIN);
		
		/*String[] text = new String[] {"MINING LEVEL UP!\n2 -> 3", "New recipes unlocked!"};
		String[] sprites = new String[] {"upArrow"};
		boolean[] animation = new boolean[] {true};
		addNotification(text, sprites, animation, 3f, 5f, "intro");*/
		//addNotification(text, sprites, animation, 3f, 5f, "intro");
		//addNotification(text, sprites, animation, 3f, 5f, "intro");
		/*
		 
		  DESCRIPTION OF THE BARS:
		  Red: health
		  Green: hunger
		  Yellow: stamina (or hit cooldown)
		  Blue: break progress on current tile
		  
		  
		Image bars = new Image(new TextureRegionDrawable(game.getSprite("energyBars")));
		topStage.addActor(bars);
		bars.setAlign(Align.top);
		bars.setPosition(320, 360);
		
		ProgressBarStyle style = new ProgressBarStyle(new TextureRegionDrawable(game.getSprite("itemSlotBackground")), null);
		style.knob = new TextureRegionDrawable(game.getSprite("energyBars"));
		ProgressBar healthBar = new ProgressBar(0, 1, (float) .01, false, style);
		topStage.addActor(healthBar);
		*/
		
		multiInput.addProcessor(bottomStage);
		multiInput.addProcessor(centerStage);
		multiInput.addProcessor(topStage);
		multiInput.addProcessor(rightTopStage);
		multiInput.addProcessor(new InputAdapter() {
			@Override
			public boolean keyDown(int character) {
				if(character >= Input.Keys.NUM_1 && character <= Input.Keys.NUM_7) {
					InventorySlot button = (InventorySlot) hotbarTable.getCells().get(character-Input.Keys.NUM_1).getActor();
					//deselectAllButtons();
					button.setChecked(true);
					sender.addPacket(new HotbarUpdatePacket((byte) (character-Input.Keys.NUM_1)));
					if(getLocalPlayer()!=null) {
						getLocalPlayer().setHotbarSlot(character-Input.Keys.NUM_1);
					}
					return true;
				}
				if(character == Input.Keys.E) {
					if(chestInventory.isVisible()) {
						chestInventory.setVisible(false);
					} else {
						inventory.setVisible(!inventory.isVisible());
						
						openHelpMenu(HintMenuType.INVENTORY);
						sender.addPacket(new UIEventPacket(inventory.isVisible(), 2));
					}
				}
				if(character == Input.Keys.C) {
					setCraftingStation(CraftingStation.NONE);
					setCraftingVisibility(!craftingMenuVisible);
					sender.addPacket(new UIEventPacket(craftingMenuVisible, 1));
				}
				if(character == Input.Keys.ESCAPE) {
					pauseMenu.setVisible(!pauseMenu.isVisible());
					game.playSound("select", 1f);
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
		
		worldTime+=delta;
		stepDelta+=Math.min(delta, .25f);
		logPositionDelta+=delta;
		
		float speed = 1;
		float ix = 0, iy = 0;
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
		
		Vector2 clamp = CustomMath.clampCircle(ix, iy, 1, .1f);
		ix = clamp.x;
		iy = clamp.y;
		
		boolean sprint = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);

		Vector2 mouseScreenPosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
		Vector2 mouseLocalPosition = inventory.screenToLocalCoordinates(mouseScreenPosition);
		
		if(!overMenus(mouseLocalPosition)) {
			if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
				
				if(inventory.inventoryGroup.selectedSlot!=null) {
					// drop item
					boolean selectedIsPlayerInventory = getLocalPlayer()!=null&&getLocalPlayer().getInventory().equals(inventory.inventoryGroup.selectedSlot.getInventory());
					sender.addPacket(new InventoryClickPacket(selectedIsPlayerInventory, false, inventory.inventoryGroup.selectedSlot.slot, -1, null));
					inventory.inventoryGroup.selectedSlot.getInventory().put(inventory.inventoryGroup.selectedSlot.slot, null);
					inventory.inventoryGroup.selectedSlot = null;
					
				} else {
					
					leftClickDelta+=delta;
					if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
						leftClickDelta = Float.MAX_VALUE;
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
						
						if(at instanceof WallTile) {
							if(below instanceof WallTile) {
								output.add(0, -Tile.TILE_SIZE, 0);
							}
						} else if(below instanceof WallTile) {
							output.add(0, -Tile.TILE_SIZE, 0);
						}

						if(below instanceof WallTile && getLocalPlayer().getTileCoordinates().equals(new Point(x, y))) {
							if(at!=null) {
								output.add(0, Tile.TILE_SIZE, 0);
							}
						}
						
						sender.addPacket(new PlayerInteractPacket(0, (int) output.x, (int) output.y, -1));
					}
				}
			} else {
				leftClickDelta = 0;
			}
			
			if(Gdx.input.isButtonPressed(Input.Buttons.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.V)) {
				
				if(!(Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.V))) {
					if(getLocalPlayer()!=null&&(getLocalPlayer().getHeldItem()instanceof Placeable||getLocalPlayer().getHeldItem() instanceof Tool)) {
						rightClickDelta+=delta;
					}
				} else {
					rightClickDelta = Integer.MAX_VALUE;
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
					
					sender.addPacket(new PlayerInteractPacket(2, (int) output.x, (int) output.y, modifier));
					
					int x = (int) Math.floor(output.x/Tile.TILE_SIZE);
					int y = (int) Math.floor(output.y/Tile.TILE_SIZE);
					
					clientSideTileInteraction(x, y, 2);
				}
			} else {
				rightClickDelta = 0;
			}
		}
		
		if(lastX !=ix || lastY != iy || lastSprint != sprint) {
			//this.previousPlayerPositions.put(System.currentTimeMillis(), new Vector2Double(getLocalPlayer().getX(), getLocalPlayer().getY()));
			sender.addPacket(new PlayerInputPacket(ix, iy, sprint));
		}
		if(logPositionDelta>1/10f) {
			cvm.put(System.currentTimeMillis(), new Vector2Double(getLocalPlayer().getX(), getLocalPlayer().getY()));
			logPositionDelta = 0;
		}
		
		lastX = ix;
		lastY = iy;
		lastSprint = sprint;

		if(Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
			openHelpMenu(null, false);
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.F2)) {
			showUI = !showUI;
		}
		
		//updateDelta+=delta;
		//while(updateDelta>=Entity.DELTA_TIME) {
			//updateDelta-=Entity.DELTA_TIME;
			for(UUID uuid : entities.keySet()) {
				Entity entity = entities.get(uuid);
				if(entity.equals(getLocalPlayer())) {
					Player p = (Player) entity;
					p.setSprinting(sprint);
					entity.setVelocity(ix*p.getSpeed(), iy*p.getSpeed());
				}
				entity.update(delta, tileDataObject);
				//entity.update(Entity.DELTA_TIME, tileDataObject);
			}
		//}
		
		recipeDisplay.update();
		
		Point centerChunk = null;
		if(getLocalPlayer()!=null) {
			Vector2Double pos = new Vector2Double(getLocalPlayer().getX(), getLocalPlayer().getY());
			if(CustomTime.timeToSeconds(System.nanoTime()-lastUpdate)>=Entity.DELTA_TIME) {
				sender.addPacket(new PositionPacket(pos));
				lastUpdate = System.nanoTime();
			}
			
			worldViewport.getCamera().position.set((float) pos.getX(), (float) pos.getY() + Tile.TILE_SIZE / 2, 0);

			Vector3 temp = new Vector3(), temp2 = new Vector3();
			worldViewport.getCamera().getPickRay(0, 1).getEndPoint(temp, 10);
			worldViewport.getCamera().getPickRay(1, 1).getEndPoint(temp2, 10);
			float dist = temp.dst(temp2);

			worldViewport.getCamera().position.x = roundToNearest(worldViewport.getCamera().position.x, dist);
			worldViewport.getCamera().position.y = roundToNearest(worldViewport.getCamera().position.y, dist);
			centerChunk = Entity.getChunk(pos.getX(), pos.getY());
		}
		
		if(centerChunk != null) {

			frameBuffer.begin();
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
				return (int) -(e1.getRenderYOffset()+e1.getY());
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
			
			Format lightingFormat = Format.Alpha;
			
			Tile.tileRenderDelta += Gdx.graphics.getDeltaTime();
			
			int translucentX = Integer.MAX_VALUE, translucentY = Integer.MAX_VALUE;
			if(getLocalPlayer()!=null) {
				translucentX = getLocalPlayer().getTileX();
				translucentY = getLocalPlayer().getTileY()-1;
			}
			
			for(int z = 0; z < Chunk.LAYERS; z++) {
				for(int y = (centerChunk.y+1)*Chunk.CHUNK_SIZE+Chunk.CHUNK_SIZE; y >= (centerChunk.y-1)*Chunk.CHUNK_SIZE; y--) {
					ArrayList<Pair<Integer, Tile>> xStrip = new ArrayList<>();
					for(int x = (centerChunk.x-1)*Chunk.CHUNK_SIZE; x <= (centerChunk.x+1)*Chunk.CHUNK_SIZE+Chunk.CHUNK_SIZE; x++) {
						Tile[][][] data = tileData.get(Chunk.getChunk(x, y));
						if(data == null) {
							continue;
						}
						if(data[Math.floorMod(x, Chunk.CHUNK_SIZE)][Math.floorMod(y, Chunk.CHUNK_SIZE)][z] != null) {
							xStrip.add(new Pair<>(x, data[Math.floorMod(x, Chunk.CHUNK_SIZE)][Math.floorMod(y, Chunk.CHUNK_SIZE)][z]));
						}
					}
					xStrip.sort(Comparator.comparing(pair -> {
						Tile t = ((Pair<Integer, Tile>) pair).getSecond();
						return t.getRenderYOffset();
					}));
					while(xStrip.size() > 0) {
						Pair<Integer, Tile> pair = xStrip.get(0);
						
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
							if(z==e.getRenderLayer()&&-pair.getSecond().getRenderYOffset()+(y*Tile.TILE_SIZE)<(-e.getRenderYOffset()+e.getY())) {
								if(e instanceof LightSource) {
									LightSource light = (LightSource) e;
									
									lights.addLight((float) e.getX()+light.offsetX(), (float) e.getY()+light.offsetY(), light.getIntensity()*Tile.TILE_SIZE);
								}
								
								e.render(game, this);
								entities.remove(0);
								continue;
							}
							break;
						}
						
						if(pair.getSecond() instanceof LightSource) {
							lights.addLight(pair.getFirst()*Tile.TILE_SIZE+Tile.TILE_SIZE/2, y*Tile.TILE_SIZE+Tile.TILE_SIZE/2, ((LightSource)pair.getSecond()).getIntensity()*Tile.TILE_SIZE);
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
						if(translucent) {
							game.batch.setColor(new Color(1f, 1f, 1f, .5f));
						}
						
						pair.getSecond().render(game, tileData, pair.getFirst(), y);
						
						if(translucent) {
							game.batch.setColor(Color.WHITE);
						}
						xStrip.remove(0);
					}
				}
			}
			while(entities.size() > 0) {
				entities.get(0).render(game, this);
				entities.remove(0);
			}
			
			
			if(getLocalPlayer()!=null) {
				if(getLocalPlayer().getHeldItem() instanceof Placeable) {
					
					if(Gdx.input.isKeyJustPressed(Input.Keys.R)) {
						rotation++;
						if(rotation>3) {
							rotation = 0;
						}
					}
					if(Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
						rotation--;
						if(rotation<0) {
							rotation = 3;
						}
					}
					
					Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
					Vector3 output = worldViewport.getCamera().unproject(new Vector3(mouse, 0));
					Point targetTile = Entity.getTileCoordinates(output.x, output.y);
					
					//TileType type = ((Placeable)getLocalPlayer().getHeldItem()).getTileToPlace();
					
					/*if(Placeable.temporaryPlaceTile==null || Placeable.temporaryPlaceTile.getType()!=type) {
						Placeable.temporaryPlaceTile = Tiles.getTile(type);
					}*/
					
					double time = CustomTime.timeToSeconds(System.nanoTime());
					float sin = MathUtils.sin((float) ((time*2)%MathUtils.PI2));
					float random = (sin*sin)/4+(2.5f/4f);
					
					game.batch.setColor(1, 1, 1, random);
					((Placeable)getLocalPlayer().getHeldItem()).getTempPlaceTile(rotation).render(game, tileData, targetTile.x, targetTile.y);
					
					game.batch.setColor(Color.WHITE);
				}
			}
			
			game.batch.end();
			
			frameBuffer.end();
			
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
		
		
		

		game.batch.setBlendFunction(GL20.GL_SRC_ALPHA,  GL20.GL_ONE_MINUS_SRC_ALPHA);
		game.batch.setProjectionMatrix(uiViewport.getCamera().combined);
		game.batch.begin();
		
		uiViewport.apply();

		if(showUI) {
			centerStage.act(delta);
			centerStage.draw();
		}
		
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
		
		/*if(socket!=null) {game.getFont(25).draw(game.batch, "Connected: "+isConnected(), 30, 360-30);}
		if(socket!=null) {game.getFont(15).draw(game.batch, "Latency: "+latency+"ms", 30, 360-30-25);}
		if(server != null) {game.getFont(15).draw(game.batch, "Connected: "+server.currentActivePlayers(), 30, 360-30-25-15);}
		 
		i = 0;
		for(String user : playerPositions.keySet()) {
			game.getFont(15).draw(game.batch, user+"'s data = "+playerPositions.get(user).toString(), 30, 360-30-25-15-(20*(i+1)));
			i++;
		}*/
		
		//game.getFont(15).draw(game.batch, "E:"+entities.size(), 30, 360-30);
		
		game.batch.setProjectionMatrix(bottomViewport.getCamera().combined);
		bottomViewport.apply();
		

		if(showUI) {
			int width = 320;
			game.batch.draw(game.getSprite("hotbar"), 320-width/2f, 10, width, width/5f);
		}
		
		game.batch.end();
		game.batch.begin();
		if(showUI) {
			bottomStage.act();
			bottomStage.draw();
		}
		game.batch.end();
		
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
		rightTopViewport.setScreenY(Gdx.graphics.getHeight()-topViewport.getScreenHeight());
		rightTopViewport.setScreenX(Gdx.graphics.getWidth()-topViewport.getScreenWidth());
		
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
					((TransitionTile)upd.getTile()).updateSprites(game, tileData, upd.getX(), upd.getY());
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
					((TransitionTile) at).updateSprites(game, tileData, x+tx, y+ty);
				}
			}
		} else if(packet instanceof SpawnEntity) {
			SpawnEntity spawn = (SpawnEntity) packet;
			Entity entity = spawn.getEntity();
			entities.put(entity.getUUID(), entity);
			if(entity instanceof Player) {
				Player player = (Player) entity;
				//System.out.println("RECEIVED PLAYER, OBF: "+player.getX()+","+player.getY());
				if(player.getUsername().equals(username)) {
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
							
							final double threshold = Tile.TILE_SIZE/2f;
							
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
				setCraftingVisibility(false);
			} else {
				setCraftingStation(cspack.getStation());
				setCraftingVisibility(true);
			}
			inventory.setVisible(false);
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
		} else if(packet instanceof ChestInventoryPacket) {
			ChestInventoryPacket cip = (ChestInventoryPacket) packet;
			inventory.setVisible(false);
			chestInventory.setVisible(true);
			chestInventory.setChestInventory(cip.getX(), cip.getY());
			setCraftingStation(CraftingStation.NONE);
			setCraftingVisibility(false);
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
			
			if(crafting.isVisible()) {
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
		default:
			break;
		}
	}
	
	public void openHelpMenu(HintMenuType type) {
		openHelpMenu(type, true);
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
    
}
