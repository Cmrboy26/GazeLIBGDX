package net.cmr.gaze;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Music.OnCompletionListener;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.Hinting;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.cmr.gaze.crafting.Crafting;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.stage.IntroScreen;
import net.cmr.gaze.stage.SettingScreen;
import net.cmr.gaze.stage.widgets.HintMenu.HintMenuType;
import net.cmr.gaze.util.Normalize;
import net.cmr.gaze.world.Tiles;
import net.cmr.gaze.world.entities.Player;

public class Gaze extends Game {
	
	static Gaze singleton;
	
	public static Gaze get() {
		if(singleton!=null) {
			return singleton;
		}
		throw new RuntimeException("Attempted to access client side objects from server side.");
	}
	public static boolean singletonExists() {
		return singleton!=null; 
	}
	
	public static final boolean SKIP = true;
	public static final boolean HITBOXES = false;
	
	FreeTypeFontGenerator fontgenerator;
	Skin defaultSkin;
	
	public static int version = 1;
	
	public SpriteBatch batch;
	public HashMap<Float, BitmapFont> fontmap;
	public HashMap<String, Sprite> sprites;
	public HashMap<String, Animation<TextureRegion>> animations;
	public HashMap<String, Sound> sounds;
	public HashMap<String, Music> music;
	
	TextureAtlas UIAtlas;
	TextureAtlas TilesAtlas;
	TextureAtlas ItemsAtlas;
	TextureAtlas PlayerAtlas;
	
	public ExtendViewport backgroundViewport;
	
	public float horizontalGutter;
	public float verticalGutter;
	
	public Viewport viewport;
	
	public Preferences settings;
	
	public Gaze() {
 		singleton = this;
	}
	
	public NinePatch healthbar, healthbarBackground, helpBox, questBoxNine;
	
	@Override
	public void create () {
		this.viewport = new ExtendViewport(640, 360);
		this.viewport.getCamera().position.set(640f/2f, 360f/2f, 0);
		backgroundViewport = new ExtendViewport(640, 360);
		batch = new SpriteBatch();
		//Gdx.files.internal("seymourfont.ttf").file().exists();
		//System.out.println(Gdx.files.internal("seymourfont.ttf").file().exists());
		fontgenerator = new FreeTypeFontGenerator(Gdx.files.internal("seymourfont.ttf"));
		fontmap = new HashMap<>();
		sprites = new HashMap<>();
		sounds = new HashMap<>();
		music = new HashMap<>();
		
		
		UIAtlas = new TextureAtlas(Gdx.files.internal("atlas/UI.atlas"));
		TilesAtlas = new TextureAtlas(Gdx.files.internal("tiles/tilesSprites/Tiles.atlas"));
		ItemsAtlas = new TextureAtlas(Gdx.files.internal("atlas/Items.atlas"));  
		PlayerAtlas = new TextureAtlas(Gdx.files.internal("entities/atlases/Player.atlas"));  
		
		int size = UIAtlas.getRegions().size;
		size += TilesAtlas.getRegions().size;
		size += ItemsAtlas.getRegions().size;
		size += PlayerAtlas.getRegions().size;
		int counter = 1;
		
		for(int i = 0; i < UIAtlas.getRegions().size; i++) {
			AtlasRegion region = UIAtlas.getRegions().get(i);
			sprites.put(region.name, new Sprite(region));
			Logger.log("INFO", "["+(counter++)+"/"+size+"]\t"+" Initializing Texture... "+region.name);
		}
		for(int i = 0; i < TilesAtlas.getRegions().size; i++) {
			AtlasRegion region = TilesAtlas.getRegions().get(i);
			Sprite s = new Sprite(region);
			sprites.put(region.name, new Sprite(region));
			Logger.log("INFO", "["+(counter++)+"/"+size+"]\t"+" Initializing Texture... "+region.name);
		}
		for(int i = 0; i < ItemsAtlas.getRegions().size; i++) {
			AtlasRegion region = ItemsAtlas.getRegions().get(i);
			sprites.put(region.name, new Sprite(region));
			Logger.log("INFO", "["+(counter++)+"/"+size+"]\t"+" Initializing Texture... "+region.name);
		}
		for(int i = 0; i < PlayerAtlas.getRegions().size; i++) {
			AtlasRegion region = PlayerAtlas.getRegions().get(i);
			sprites.put(region.name, new Sprite(region));
			Logger.log("INFO", "["+(counter++)+"/"+size+"]\t"+" Initializing Texture... "+region.name);
		}
		
		healthbar = new NinePatch(sprites.get("healthbarNine"), 2, 2, 3, 4);
		healthbar.scale(2, 2);
		healthbarBackground = new NinePatch(sprites.get("healthbarNineBackground"), 2, 2, 3, 4);
		healthbarBackground.scale(2, 2);
		
		helpBox = new NinePatch(sprites.get("helpBoxNine"), 1, 1, 1, 1);
		helpBox.scale(2, 2);
		questBoxNine = new NinePatch(sprites.get("questBoxNine"), 1, 1, 1, 1);
		
		settings = SettingScreen.initializePreferences();
		setFPS();
		setWindowMode();
		
		initAnimations();

		initSound("intro");
		initSound("outro");
		initSound("select");
		initSound("trueSelect");
		initSound("falseSelect");
		initSound("tick");
		initSound("chute");
		initSound("error");
		initSound("water0");
		initSound("water1");
		initSound("woodHit");
		initSound("stoneHit");
		initSound("stoneBreak");
		initSound("grassBreak");
		initSound("pickup");
		initSound("forestAmbience0");
		initSound("forestAmbience1");
		initSound("forestAmbience2");
		initSound("forestAmbience3");
		initSound("forestAmbience4");
		initSound("splash");
		initSound("place");
		initSound("hurt");
		initSound("craftSuccess");
		initSound("craftFail");
		initSound("caveAmbience0");
		
		initMusic("peacefulOne");
		
		initializeGameContent();
		
		/*Preferences login = Gdx.app.getPreferences("LoginData");
		if(login.getString("credentials", null) == null) {
			this.setScreen(new SetupScreen(this));
		} else {
			this.setScreen(new StartupScreen(this));
		}*/
		this.setScreen(new IntroScreen(this));
		
		FileHandle handle = Gdx.files.external("/Gaze/");
		handle.file().mkdir();
		handle = Gdx.files.external("/Gaze/saves");
		handle.file().mkdir();
		
		playMusic("peacefulOne");
		
		HintMenuType.loadViewedHints();
		
		//System.out.println(getAverageColor(getSprite("itemSlotBackgroundCheckedFilled")));
	}

	public static Color getAverageColor(TextureRegion region) {
		return getAverageColor(new Sprite(region));
	}
	
	public static Color getAverageColor(Sprite sprite) {
		if(!sprite.getTexture().getTextureData().isPrepared()) {
			sprite.getTexture().getTextureData().prepare();
		}
		Pixmap pixels = sprite.getTexture().getTextureData().consumePixmap();
		
		int redSum = 0;
		int greenSum = 0;
		int blueSum = 0;
		int totalPixel = 0;
		
		for(int x = sprite.getRegionX(); x < sprite.getRegionX()+sprite.getWidth(); x++) {
			for(int y = sprite.getRegionY(); y < sprite.getRegionY()+sprite.getHeight(); y++) {
				Color pixelColor = new Color(pixels.getPixel(x, y));
				float alpha = pixelColor.a;
				if(alpha > .9f) {
					totalPixel++;
					redSum += (int) (255*pixelColor.r*alpha);
					greenSum += (int) (255*pixelColor.g*alpha);
					blueSum += (int) (255*pixelColor.b*alpha);
				}
			}
		}
		Color output = new Color((redSum/totalPixel)/255f, (greenSum/totalPixel)/255f, (blueSum/totalPixel)/255f, 1f);
		//System.out.println(output);
		return output;
	}
	
	public void initAnimations() {
		animations = new HashMap<>();
		
		Sprite player = getSprite("player");
		
		TextureRegion[][] playerRegions = player.split((int) player.getWidth()/16, 64);
		
		int character = 1;
		
		//Logger.log("INFO", "Initializing animations...");
		
		FileHandle animationData = Gdx.files.internal("animations.ani");
		JsonValue values = new JsonReader().parse(animationData);
		
		for(int i = 0; i < values.size; i++) {
			JsonValue value = values.get(i);
			
			if(sprites.get(value.name)==null) {
				Gdx.app.error("WARNING", "File/Sprite not found for animation \""+value.name+"\" in animations.ani");
			}
			
			Gdx.app.log("INFO", "["+(i+1)+"/"+values.size+"]\t Initializing Animation...\t "+value.name);
			
			int tileX = value.getInt("tileX");
			int tileY = value.getInt("tileY");
			TextureRegion[][] region = getSprite(value.name).split(tileX, tileY);
			
			JsonValue animationsJson = value.get("animations");
			for(JsonValue animation : animationsJson) {
				String name = animation.getString("name", value.name);
				int x = animation.getInt("x", 0);
				int y = animation.getInt("y", 0);
				int w = animation.getInt("w", region[0].length);
				float time = animation.getFloat("time", .2f);
				boolean loop = animation.getBoolean("loop", true);
				TextureRegion[] animRegion = getTextureArrayFromJSONData(region, x, y, w);
				
				Animation<TextureRegion> animationObject = new Animation<TextureRegion>(time, animRegion);
				animationObject.setPlayMode(loop?PlayMode.LOOP:PlayMode.NORMAL);
				
				animations.put(name, animationObject);
				//System.out.println(name+","+x+","+y+","+w+","+time);
			}
		}
		
		
		/*int v = 0;
		Animation<TextureRegion> walkDown = new Animation<TextureRegion>(.2f, playerRegions[character][v], playerRegions[character][v+1], playerRegions[character][v+2], playerRegions[character][v+3]);
		walkDown.setPlayMode(PlayMode.LOOP);
		animations.put("playerWalkDown", walkDown);
		Animation<TextureRegion> idleDown = new Animation<TextureRegion>(10f, playerRegions[character][v]);
		idleDown.setPlayMode(PlayMode.NORMAL);
		animations.put("playerIdleDown", idleDown);
		v+=4;
		
		Animation<TextureRegion> walkUp = new Animation<TextureRegion>(.2f, playerRegions[character][v], playerRegions[character][v+1], playerRegions[character][v+2], playerRegions[character][v+3]);
		walkUp.setPlayMode(PlayMode.LOOP);
		animations.put("playerWalkUp", walkUp);
		Animation<TextureRegion> idleUp = new Animation<TextureRegion>(10f, playerRegions[character][v]);
		idleUp.setPlayMode(PlayMode.NORMAL);
		animations.put("playerIdleUp", idleUp);
		v+=4;
		
		Animation<TextureRegion> walkRight = new Animation<TextureRegion>(.2f, playerRegions[character][v], playerRegions[character][v+1], playerRegions[character][v+2], playerRegions[character][v+3]);
		walkRight.setPlayMode(PlayMode.LOOP);
		animations.put("playerWalkRight", walkRight);
		Animation<TextureRegion> idleRight = new Animation<TextureRegion>(10f, playerRegions[character][v]);
		idleRight.setPlayMode(PlayMode.NORMAL);
		animations.put("playerIdleRight", idleRight);
		v+=4;
		
		Animation<TextureRegion> walkLeft = new Animation<TextureRegion>(.2f, playerRegions[character][v], playerRegions[character][v+1], playerRegions[character][v+2], playerRegions[character][v+3]);
		walkLeft.setPlayMode(PlayMode.LOOP);
		animations.put("playerWalkLeft", walkLeft);
		Animation<TextureRegion> idleLeft = new Animation<TextureRegion>(10f, playerRegions[character][v]);
		idleLeft.setPlayMode(PlayMode.NORMAL);
		animations.put("playerIdleLeft", idleLeft);
		v+=4;*/
		
		/*TextureRegion[][] lavaRegion = getSprite("lava").split(32, 32);
		Animation<TextureRegion> lava = new Animation<TextureRegion>(.2f, lavaRegion[0]);
		lava.setPlayMode(PlayMode.LOOP);
		animations.put("lava", lava);*/
		
		/*TextureRegion[][] waterRegion = getSprite("water").split(32, 32);
		Animation<TextureRegion> water = new Animation<TextureRegion>(.2f, waterRegion[0]);
		water.setPlayMode(PlayMode.LOOP);
		animations.put("water", water);
		
		TextureRegion[][] torchRegion = getSprite("torch").split(32, 32);
		Animation<TextureRegion> torch = new Animation<TextureRegion>(.2f, torchRegion[0]);
		torch.setPlayMode(PlayMode.LOOP);
		animations.put("torch", torch);
		
		TextureRegion[][] arrowRegion = getSprite("upArrow").split(16, 16);
		Animation<TextureRegion> arrow = new Animation<TextureRegion>(.1f, arrowRegion[0]);
		arrow.setPlayMode(PlayMode.LOOP);
		animations.put("upArrow", arrow);*/
		
		Player.AVAILABLE_PLAYER_TYPES = 0;
		while(true) {
			if(animations.get("playerWalkDown"+Player.AVAILABLE_PLAYER_TYPES)!=null) {
				Player.AVAILABLE_PLAYER_TYPES++;
			} else {
				Player.AVAILABLE_PLAYER_TYPES--;
				break;
			}
		}
	}
	
	private TextureRegion[] getTextureArrayFromJSONData(TextureRegion[][] region, int x, int y, int w) {
		TextureRegion[] end = new TextureRegion[w];
		
		for(int i = 0; i < w; i++) {
			end[i] = region[y][x+i];
		}
		
		return end;
	}
	
	public void setFPS() {
		int targetFPS = settings.getInteger("FPS");
		
		if(targetFPS>0) {
			Gdx.graphics.setForegroundFPS(targetFPS);
			Gdx.graphics.setVSync(false);
		} else {
			Gdx.graphics.setForegroundFPS(targetFPS==0?Gdx.graphics.getDisplayMode().refreshRate:-1);
			Gdx.graphics.setVSync(targetFPS==0);
		}
	}
	public void setWindowMode() {
		boolean fullscreen = settings.getBoolean("fullscreen");
		
		if(fullscreen) {
			Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		} else {
			Gdx.graphics.setWindowedMode(640, 360);
		}
	}
	
	
	@Override
	public void render () {
		
		if(playingMusic!=null) {
			music.get(playingMusic).setVolume(settings.getFloat("musicVolume")*settings.getFloat("masterVolume"));
		}
		
		ScreenUtils.clear(Color.BLACK);
		
		super.render();
		
		batch.setProjectionMatrix(this.viewport.getCamera().combined);
		this.viewport.apply();
		batch.begin();
		
		// Debug display
		
		if(settings.getBoolean("displayFPS")) {
			getFont(15).draw(batch, "FPS:"+Gdx.graphics.getFramesPerSecond(), 0, 360);
			if(getScreen() instanceof GameScreen) {
				GameScreen gs = (GameScreen) getScreen();
				getFont(15).draw(batch, "Download Speed: "+Normalize.truncateDouble(gs.downloadSpeed.ratePerSecond()/1000d, 1)+" (kB/S)", 0, 360-20);
				getFont(15).draw(batch, "Upload Speed: "+Normalize.truncateDouble(gs.uploadSpeed.ratePerSecond()/1000d, 1)+" (kB/S)", 0, 360-40);
				
			}
		}
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		if(getScreen()!=null) {
			getScreen().dispose();
		}
		fontgenerator.dispose();
		for(BitmapFont font : fontmap.values()) {
			font.dispose();
		}
		if(defaultSkin!=null) {
			defaultSkin.dispose();
		}
		for(String string : sprites.keySet()) {
			sprites.get(string).getTexture().dispose();
		}
		for(String string : sounds.keySet()) {
			sounds.get(string).dispose();
		}
		for(String music : music.keySet()) {
			this.music.get(music).dispose();
		}
		TilesAtlas.dispose();
		UIAtlas.dispose();
		ItemsAtlas.dispose();
		PlayerAtlas.dispose();
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		
		float targetRatio = 640f/360f;
		
		horizontalGutter=Math.max(0f,(width-height*targetRatio)/2f);
		verticalGutter=Math.max(0f,(height-width/targetRatio)/2f);
		
		viewport.update(width, height);
		backgroundViewport.update(width, height);
	}
	
	BitmapFont masterFont;
	
	/*public BitmapFont getFont(int size) {
		return getFont(size, 2);
	}*/
	
	public BitmapFont getFont(float size) {
		BitmapFont font;
		if(((font = fontmap.get(size))!=null)) {
			return font;
		}
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 25;
		parameter.hinting = Hinting.AutoFull;
		parameter.shadowColor = Color.BLACK;
		parameter.shadowOffsetX = 2;
		parameter.shadowOffsetY = 2;
		parameter.genMipMaps = true;
		parameter.borderStraight = false;
		parameter.mono = true;
		parameter.incremental = true;
		parameter.minFilter = TextureFilter.Nearest;
		parameter.magFilter = TextureFilter.Nearest;
		font = fontgenerator.generateFont(parameter);
		font.getData().setScale(size/25f);
		font.setUseIntegerPositions(false);
		fontmap.put(size, font);
		return font;
		
		/*if(masterFont == null) {
			FreeTypeFontParameter parameter = new FreeTypeFontParameter();
			parameter.size = 25;
			parameter.hinting = Hinting.AutoFull;
			parameter.shadowColor = Color.BLACK;
			parameter.shadowOffsetX = 1;
			parameter.shadowOffsetY = 1;
			parameter.genMipMaps = true;
			parameter.borderStraight = false;
			parameter.mono = true;
			parameter.incremental = true;
			parameter.minFilter = TextureFilter.Nearest;
			parameter.magFilter = TextureFilter.Nearest;
			masterFont = fontgenerator.generateFont(parameter);
		}
		masterFont.getData().setScale(size/25f);
		return masterFont;*/
		
		/*BitmapFont font;
		if(((font = fontmap.get(size))!=null)) {
			return font;
		}
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = size;
		parameter.hinting = Hinting.AutoFull;
		parameter.shadowColor = Color.BLACK;
		parameter.shadowOffsetX = shadow;
		parameter.shadowOffsetY = shadow;
		parameter.genMipMaps = true;
		parameter.borderStraight = false;
		parameter.mono = true;
		parameter.incremental = true;
		parameter.minFilter = TextureFilter.Nearest;
		parameter.magFilter = TextureFilter.Nearest;
		font = fontgenerator.generateFont(parameter);
		font.setUseIntegerPositions(false);
		fontmap.put(size, font);
		return font;*/
	}
	
	public Sprite getSprite(String name) {
		Sprite sprite = sprites.get(name);
		if(sprite !=null) {
			return sprite;
		}
		System.out.println("COULD NOT FIND SPRITE "+name);
		return sprites.get("logo");
	}
	public Animation<TextureRegion> getAnimation(String animation) {
		return animations.get(animation);
	}
	
	private void initSound(String sound) {
		sounds.put(sound, Gdx.audio.newSound(Gdx.files.internal("sfx/"+sound+".wav")));
	}
	private void initMusic(String music) {
		this.music.put(music, Gdx.audio.newMusic(Gdx.files.internal("mus/"+music+".wav")));
	}
	
	boolean musicPlaying = false;
	String playingMusic = null;
	OnCompletionListener listener = new OnCompletionListener() {
		@Override
		public void onCompletion(Music music) {
			musicPlaying = false;
			playingMusic = null;
		}
	};
	
	public void playMusic(String music) {
		if(!musicPlaying) {
			musicPlaying = true;
			playingMusic = music;
			this.music.get(music).setVolume(settings.getFloat("musicVolume")*settings.getFloat("masterVolume"));
			this.music.get(music).play();
			this.music.get(music).setOnCompletionListener(listener);
		}
	}
	
	public void stopMusic() {
		
	}
	
	public long playSound(String sound, float volume, float pitch) {
		
		if(sounds.get(sound) == null) {
			return -1;
		}
		
		long v = sounds.get(sound).play(volume*settings.getFloat("sfxVolume")*settings.getFloat("masterVolume"), pitch, 0);
		return v;
	}
	
	public long playSound(String sound, float volume) {
		return playSound(sound, volume, 1f);
	}
	
	public HashMap<String, Long> playSoundCooldownMap = new HashMap<>();
	
	public void playSoundCooldown(String sound, float volume, float cooldown) {
		long at = playSoundCooldownMap.getOrDefault(sound, -1L);
		if(at!=-1 && System.currentTimeMillis()-at >= cooldown*1000f) {
			playSoundCooldownMap.put(sound, -1L);
			at = -1;
		}
		
		if(at == -1) {
			playSound(sound, volume);
			playSoundCooldownMap.put(sound, System.currentTimeMillis());
		}
	}
	public void playSoundCooldown(String sound, float volume, float pitch, float cooldown) {
		long at = playSoundCooldownMap.getOrDefault(sound, -1L);
		if(at!=-1 && System.currentTimeMillis()-at >= cooldown*1000f) {
			playSoundCooldownMap.put(sound, -1L);
			at = -1;
		}
		
		if(at == -1) {
			playSound(sound, volume, pitch);
			playSoundCooldownMap.put(sound, System.currentTimeMillis());
		}
	}
	
	public Skin getSkin() {
		
		if(defaultSkin != null) {
			return defaultSkin;
		}
		
		defaultSkin = new Skin();
		
		TextButtonStyle style = new TextButtonStyle();
		
		style.down = new TextureRegionDrawable(getSprite("buttonSelected"));
		style.over = new TextureRegionDrawable(getSprite("buttonSelected"));
		style.up = new TextureRegionDrawable(getSprite("button"));
		
		style.font = getFont(25);
		style.fontColor = Color.WHITE;
		style.overFontColor = Color.YELLOW;
		defaultSkin.add("button", style);
		
		TextButtonStyle largeStyle = new TextButtonStyle();
		
		largeStyle.down = new TextureRegionDrawable(getSprite("buttonSelectedLarge"));
		largeStyle.over = new TextureRegionDrawable(getSprite("buttonSelectedLarge"));
		largeStyle.up = new TextureRegionDrawable(getSprite("buttonLarge"));
		
		largeStyle.font = getFont(25);
		largeStyle.fontColor = Color.WHITE;
		largeStyle.overFontColor = Color.YELLOW;
		defaultSkin.add("buttonLarge", largeStyle);
		
		TextButtonStyle smallstyle = new TextButtonStyle();
		
		smallstyle.down = new TextureRegionDrawable(getSprite("buttonSelected"));
		smallstyle.over = new TextureRegionDrawable(getSprite("buttonSelected"));
		smallstyle.up = new TextureRegionDrawable(getSprite("button"));
		
		smallstyle.font = getFont(20);
		smallstyle.fontColor = Color.WHITE;
		smallstyle.overFontColor = Color.YELLOW;
		smallstyle.unpressedOffsetY = 0;
		smallstyle.pressedOffsetY = 0;
		defaultSkin.add("smallButton", smallstyle);
		
		TextButtonStyle toggle = new TextButtonStyle();
		
		toggle.down = new TextureRegionDrawable(getSprite("buttonSelected"));
		toggle.over = new TextureRegionDrawable(getSprite("buttonSelected"));
		toggle.checked = new TextureRegionDrawable(getSprite("buttonSelected"));
		toggle.up = new TextureRegionDrawable(getSprite("button"));

		toggle.unpressedOffsetY = 3;
		toggle.pressedOffsetY = 0;
		toggle.font = getFont(25);
		toggle.fontColor = Color.WHITE;
		toggle.overFontColor = Color.YELLOW;
		toggle.checkedFontColor = Color.YELLOW;
		defaultSkin.add("toggle", toggle);
		
		TextButtonStyle largeToggle = new TextButtonStyle();
		
		largeToggle.down = new TextureRegionDrawable(getSprite("buttonSelectedLarge"));
		largeToggle.over = new TextureRegionDrawable(getSprite("buttonSelectedLarge"));
		largeToggle.checked = new TextureRegionDrawable(getSprite("buttonSelectedLarge"));
		largeToggle.up = new TextureRegionDrawable(getSprite("buttonLarge"));

		largeToggle.unpressedOffsetY = 3;
		largeToggle.pressedOffsetY = 0;
		largeToggle.font = getFont(25);
		largeToggle.fontColor = Color.WHITE;
		largeToggle.overFontColor = Color.YELLOW;
		largeToggle.checkedFontColor = Color.YELLOW;
		defaultSkin.add("toggleLarge", largeToggle);
		
		SliderStyle sliderStyle = new SliderStyle();
		sliderStyle.background = new TextureRegionDrawable(getSprite("button"));
		sliderStyle.backgroundOver = new TextureRegionDrawable(getSprite("buttonSelected"));
		sliderStyle.background.setMinHeight(25);
		sliderStyle.backgroundOver.setMinHeight(25);
		sliderStyle.knob = new TextureRegionDrawable(getSprite("sliderKnob"));
		sliderStyle.knobBefore = new TextureRegionDrawable(getSprite("translucent"));
		defaultSkin.add("slider", sliderStyle);
		
		SliderStyle invertedSliderStyle = new SliderStyle();
		invertedSliderStyle.background = new TextureRegionDrawable(getSprite("button"));
		invertedSliderStyle.backgroundOver = new TextureRegionDrawable(getSprite("buttonSelected"));
		invertedSliderStyle.background.setMinHeight(25);
		invertedSliderStyle.backgroundOver.setMinHeight(25);
		invertedSliderStyle.knob = new TextureRegionDrawable(getSprite("sliderKnob"));
		invertedSliderStyle.knobAfter = new TextureRegionDrawable(getSprite("translucent"));
		defaultSkin.add("sliderInverted", invertedSliderStyle);
		
		SliderStyle sliderNoFill = new SliderStyle();
		sliderNoFill.background = new TextureRegionDrawable(getSprite("button"));
		sliderNoFill.backgroundOver = new TextureRegionDrawable(getSprite("buttonSelected"));
		sliderNoFill.background.setMinHeight(25);
		sliderNoFill.backgroundOver.setMinHeight(25);
		sliderNoFill.knob = new TextureRegionDrawable(getSprite("sliderKnob"));
		defaultSkin.add("sliderNoFill", sliderNoFill);
		
		SliderStyle sliderLargeStyle = new SliderStyle();
		sliderLargeStyle.backgroundOver = new TextureRegionDrawable(getSprite("buttonSelectedLarge"));
		sliderLargeStyle.background = new TextureRegionDrawable(getSprite("buttonLarge"));
		sliderLargeStyle.background.setMinHeight(25);
		sliderLargeStyle.backgroundOver.setMinHeight(25);
		sliderLargeStyle.knob = new TextureRegionDrawable(getSprite("sliderKnob"));
		defaultSkin.add("sliderLarge", sliderLargeStyle);
		
		TextFieldStyle textFieldStyle = new TextFieldStyle();
		textFieldStyle.background = new TextureRegionDrawable(getSprite("button"));
		textFieldStyle.focusedBackground = new TextureRegionDrawable(getSprite("buttonSelected"));
		textFieldStyle.font = getFont(15);
		textFieldStyle.focusedFontColor = Color.YELLOW;
		textFieldStyle.fontColor = Color.WHITE;
		defaultSkin.add("textField", textFieldStyle);
		
		TextFieldStyle textFieldLargeStyle = new TextFieldStyle();
		textFieldLargeStyle.background = new TextureRegionDrawable(getSprite("buttonLarge"));
		textFieldLargeStyle.focusedBackground = new TextureRegionDrawable(getSprite("buttonSelectedLarge"));
		textFieldLargeStyle.font = getFont(20);
		textFieldLargeStyle.focusedFontColor = Color.YELLOW;
		textFieldLargeStyle.fontColor = Color.WHITE;
		
		defaultSkin.add("textFieldLarge", textFieldLargeStyle);
		
		return defaultSkin;
	}
	
	@Override
	public void setScreen(Screen screen) {
		Screen current = getScreen();
		super.setScreen(screen);
		if(current!=null) {
			current.dispose();
		}
	}
	
	private static String externalIP = null;
	public static String getExternalIp() {
		if(externalIP!=null) {
			return externalIP;
		}
		try {
			URL whatismyip = new URL("https://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			String ip = in.readLine();
			externalIP = ip;
			return ip;
		} catch (MalformedURLException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		externalIP = "";
		return externalIP;
	}
	
	public static void initializeGameContent() {
		Items.initialize();
		Tiles.initialize();
		Crafting.initialize();
	}
	
	
	
	
}
