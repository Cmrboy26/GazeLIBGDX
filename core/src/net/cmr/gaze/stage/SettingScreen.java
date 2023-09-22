package net.cmr.gaze.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.PlayerDisplayWidget;
import net.cmr.gaze.stage.widgets.Background;
import net.cmr.gaze.stage.widgets.HintMenu.HintMenuType;
import net.cmr.gaze.world.entities.Player;

public class SettingScreen implements Screen {

	public static final String settingsPreferences = "GazeSettings.xml";
	
	final Gaze game;
	Stage stage;
	TextButton back;
	Slider masterVolume, musicVolume, sfxVolume, ambientVolume, worldZoom, uiZoom, fpsSlider;
	final Label masterLabel, musicLabel, sfxLabel, ambientLabel, worldLabel, uiLabel, fpsLabel;
	
	final TextButton fpsButton, hintButton, hintResetButton, fullscreenButton, playerTypeButton, invertScrollButton, connectionThreshold;
	
	final int millisSoundCooldown = 250;
	Interpolation sliderInterpolation = Interpolation.smoother;
	
	Player displayPlayer;
	
	Preferences prefs;
	
	public static Preferences initializePreferences() {
		Preferences prefs = Gdx.app.getPreferences(settingsPreferences);
        
        if(prefs.getFloat("masterVolume", -1)==-1) {
        	prefs.putFloat("masterVolume", .5f);
        }
        if(prefs.getFloat("musicVolume", -1)==-1) {
        	prefs.putFloat("musicVolume", 1f);
        }
        if(prefs.getFloat("sfxVolume", -1)==-1) {
        	prefs.putFloat("sfxVolume", 1f);
        }
        if(prefs.getFloat("ambientVolume", -1)==-1) {
        	prefs.putFloat("ambientVolume", 1f);
        }
        if(prefs.getFloat("worldZoom", -1)==-1) {
        	prefs.putFloat("worldZoom", 3);
        }
        if(prefs.getFloat("uiZoom", -1)==-1) {
        	prefs.putFloat("uiZoom", 1);
        }
        if(prefs.get().getOrDefault("displayFPS", null)==null) {
        	prefs.putBoolean("displayFPS", false);
        }
        if(prefs.getInteger("FPS", -2)==-2) {
        	prefs.putInteger("FPS", -10);
        }
        if(prefs.get().getOrDefault("fullscreen", null)==null) {
        	prefs.putBoolean("fullscreen", true);
        }
        if(prefs.getInteger("playerType", -1)==-1) {
        	prefs.putInteger("playerType", 0);
        }
        if(prefs.get().getOrDefault("invertScroll", null)==null) {
        	prefs.putBoolean("invertScroll", false);
        }
        if(prefs.get().getOrDefault("connectionThreshold", null)==null) {
        	prefs.putBoolean("connectionThreshold", false);
        }
        
        prefs.flush();
        return prefs;
	}
	
	public SettingScreen(final Gaze game) {
		this.game = game;
        stage = new Stage();
        stage.setViewport(game.viewport);
        
        prefs = initializePreferences();
        this.displayPlayer = new Player(prefs.getInteger("playerType"));
        
        // 3 by X rows/collumns of settings
        Table contentTable = new Table();
        contentTable.setBounds(0, 50, 640, 260);
        
        LabelStyle labelStyle = new LabelStyle();
        labelStyle.font = game.getFont(10f);
        masterLabel = new Label("Master Volume:", labelStyle);
        masterLabel.setAlignment(Align.right);
        
        masterVolume = new Slider(0f, 1f, .1f, false, game.getSkin(), "slider");
        masterVolume.setPosition(640/2-200, 360/2-25, Align.center);
        masterVolume.setWidth(200f);
        masterVolume.setHeight(25f);
        masterVolume.setAnimateInterpolation(sliderInterpolation);
        masterVolume.setAnimateDuration(.1f);
        masterVolume.setValue(prefs.getFloat("masterVolume"));
        masterVolume.addListener(new ChangeListener() {
        	float lastVal = 0;
			long lastSound = 0;
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(lastVal != masterVolume.getPercent()) {
					if(System.currentTimeMillis()-lastSound>millisSoundCooldown) {
						game.playSound("tick", .8f);
						lastSound = System.currentTimeMillis();
					}
					lastVal = masterVolume.getPercent();
					prefs.putFloat("masterVolume", masterVolume.getValue());
				}
			}
		});
        
        contentTable.add(masterLabel).width(105).height(25).spaceRight(5);
        contentTable.add(masterVolume).width(100).height(25).spaceBottom(5);
        
        musicLabel = new Label("Music Volume:", labelStyle);
        musicLabel.setAlignment(Align.right);
        
        musicVolume = new Slider(0f, 1f, .1f, false, game.getSkin(), "slider");
        musicVolume.setPosition(640/2-200, 360/2-25, Align.center);
        musicVolume.setWidth(100f);
        musicVolume.setHeight(25f);
        musicVolume.setAnimateInterpolation(sliderInterpolation);
        musicVolume.setAnimateDuration(.1f);
        musicVolume.setValue(prefs.getFloat("musicVolume"));
        musicVolume.addListener(new ChangeListener() {
			float lastVal = 0;
			long lastSound = 0;
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(lastVal != musicVolume.getPercent()) {
					if(System.currentTimeMillis()-lastSound>millisSoundCooldown) {
						game.playSound("tick", .8f);
						lastSound = System.currentTimeMillis();
					}
					lastVal = musicVolume.getPercent();
					prefs.putFloat("musicVolume", musicVolume.getValue());
				}
			}
		});
        
        contentTable.add(musicLabel).width(105).height(25).spaceRight(5);
        contentTable.add(musicVolume).width(100).height(25).spaceBottom(5);
        
        sfxLabel = new Label("SFX Volume:", labelStyle);
        sfxLabel.setAlignment(Align.right);
        
        sfxVolume = new Slider(0f, 1f, .1f, false, game.getSkin(), "slider");
        sfxVolume.setPosition(640/2-200, 360/2-25, Align.center);
        sfxVolume.setWidth(100f);
        sfxVolume.setHeight(25f);
        sfxVolume.setAnimateInterpolation(sliderInterpolation);
        sfxVolume.setAnimateDuration(.1f);
        sfxVolume.setValue(prefs.getFloat("sfxVolume"));
        sfxVolume.addListener(new ChangeListener() {
			float lastVal = 0;
			long lastSound = 0;
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(lastVal != sfxVolume.getPercent()) {
					if(System.currentTimeMillis()-lastSound>millisSoundCooldown) {
						game.playSound("tick", .8f);
						lastSound = System.currentTimeMillis();
					}
					lastVal = sfxVolume.getPercent();
					prefs.putFloat("sfxVolume", sfxVolume.getValue());
				}
			}
		});
        
        contentTable.add(sfxLabel).width(105).height(25).spaceRight(5);
        contentTable.add(sfxVolume).width(100).height(25).spaceBottom(5);
        contentTable.row();
        
        ambientLabel = new Label("Ambient Volume:", labelStyle);
        ambientLabel.setAlignment(Align.right);
        
        ambientVolume = new Slider(0f, 1f, .1f, false, game.getSkin(), "slider");
        ambientVolume.setPosition(640/2-200, 360/2-25, Align.center);
        ambientVolume.setWidth(100f);
        ambientVolume.setHeight(25f);
        ambientVolume.setAnimateInterpolation(sliderInterpolation);
        ambientVolume.setAnimateDuration(.1f);
        ambientVolume.setValue(prefs.getFloat("ambientVolume"));
        ambientVolume.addListener(new ChangeListener() {
			float lastVal = 0;
			long lastSound = 0;
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(lastVal != ambientVolume.getPercent()) {
					if(System.currentTimeMillis()-lastSound>millisSoundCooldown) {
						game.playSound("tick", .8f);
						lastSound = System.currentTimeMillis();
					}
					lastVal = ambientVolume.getPercent();
					prefs.putFloat("ambientVolume", ambientVolume.getValue());
				}
			}
		});
        
        contentTable.add(ambientLabel).width(105).height(25).spaceRight(5);
        contentTable.add(ambientVolume).width(100).height(25).spaceBottom(5);
        
        worldZoom = new Slider(1f, 4f, 1, false, game.getSkin(), "sliderNoFill");
        worldZoom.setPosition(640/2-200, 360/2-25, Align.center);
        worldZoom.setWidth(100f);
        worldZoom.setHeight(25f);
        worldZoom.setAnimateInterpolation(sliderInterpolation);
        worldZoom.setAnimateDuration(.1f);
        worldZoom.setValue(prefs.getFloat("worldZoom"));
        worldZoom.addListener(new ChangeListener() {
			float lastVal = 0;
			long lastSound = 0;
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(lastVal != worldZoom.getPercent()) {
					if(System.currentTimeMillis()-lastSound>millisSoundCooldown) {
						game.playSound("tick", .8f);
						lastSound = System.currentTimeMillis();
					}
					lastVal = worldZoom.getPercent();
					worldLabel.setText("World Zoom: "+((int) (2/worldZoom.getValue()*100))+"%");
					prefs.putFloat("worldZoom", worldZoom.getValue());
				}
			}
		});
        worldLabel = new Label("World Zoom: "+((int) (2/worldZoom.getValue()*100))+"%", labelStyle);
        worldLabel.setAlignment(Align.right);
        
        contentTable.add(worldLabel).width(105).height(25).spaceRight(5);
        contentTable.add(worldZoom).width(100).height(25).spaceBottom(5);
        
        uiZoom = new Slider(1f, 3f, .5f, false, game.getSkin(), "sliderNoFill");
        uiZoom.setPosition(640/2-200, 360/2-25, Align.center);
        uiZoom.setWidth(100f);
        uiZoom.setHeight(25f);
        uiZoom.setAnimateInterpolation(sliderInterpolation);
        uiZoom.setAnimateDuration(.1f);
        uiZoom.setValue(prefs.getFloat("uiZoom"));
        uiZoom.addListener(new ChangeListener() {
			float lastVal = 0;
			long lastSound = 0;
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(lastVal != uiZoom.getPercent()) {
					if(System.currentTimeMillis()-lastSound>millisSoundCooldown) {
						game.playSound("tick", .8f);
						lastSound = System.currentTimeMillis();
					}
					lastVal = uiZoom.getPercent();
					uiLabel.setText("UI Zoom: "+((int) (1/uiZoom.getValue()*100))+"%");
					prefs.putFloat("uiZoom", uiZoom.getValue());
				}
			}
		});
        uiLabel = new Label("UI Zoom: "+((int) (1/uiZoom.getValue()*100))+"%", labelStyle);
        uiLabel.setAlignment(Align.right);
        
        contentTable.add(uiLabel).width(105).height(25).spaceRight(5);
        contentTable.add(uiZoom).width(100).height(25).spaceBottom(5);
        contentTable.row();
        

        fpsSlider = new Slider(-10, 150, 10, false, game.getSkin(), "sliderNoFill");
        fpsSlider.setPosition(640/2-200, 360/2-25, Align.center);
        fpsSlider.setWidth(100f);
        fpsSlider.setHeight(25f);
        fpsSlider.setAnimateInterpolation(sliderInterpolation);
        fpsSlider.setAnimateDuration(.1f);
        fpsSlider.setValue(prefs.getInteger("FPS"));
        fpsSlider.addListener(new ChangeListener() {
			float lastVal = 0;
			long lastSound = 0;
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(lastVal != fpsSlider.getPercent()) {
					if(System.currentTimeMillis()-lastSound>millisSoundCooldown) {
						game.playSound("tick", .8f);
						lastSound = System.currentTimeMillis();
					}
					lastVal = fpsSlider.getPercent();
					fpsLabel.setText("FPS: "+getFPSString((int) fpsSlider.getValue()));
					prefs.putInteger("FPS", (int) fpsSlider.getValue());
					game.setFPS();
				}
			}
		});
        fpsLabel = new Label("FPS: "+getFPSString((int) fpsSlider.getValue()), labelStyle);
        fpsLabel.setAlignment(Align.right);
        
        contentTable.add(fpsLabel).width(105).height(25).spaceRight(5);
        contentTable.add(fpsSlider).width(100).height(25).spaceBottom(5);        
        
        contentTable.row();
        
        TextButtonStyle toggleStyle = new TextButtonStyle(game.getSkin().get("toggle", TextButtonStyle.class));
        toggleStyle.font = game.getFont(10);
        
        TextButtonStyle toggleStyleSmaller = new TextButtonStyle(game.getSkin().get("toggle", TextButtonStyle.class));
        toggleStyleSmaller.font = game.getFont(8);
        
        TextButtonStyle clickStyle = new TextButtonStyle(game.getSkin().get("button", TextButtonStyle.class));
        clickStyle.font = game.getFont(10);
        
        fpsButton = new TextButton("Debug/FPS", toggleStyle);
        fpsButton.setWidth(100);
        fpsButton.setHeight(25f);
        fpsButton.setChecked(prefs.getBoolean("displayFPS"));
        fpsButton.addListener(new ChangeListener() {
			boolean lastVal = false;
			long lastSound = 0;
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(System.currentTimeMillis()-lastSound>millisSoundCooldown) {
					game.playSound("tick", .8f);
					lastSound = System.currentTimeMillis();
				}
				lastVal = fpsButton.isChecked();
				prefs.putBoolean("displayFPS", lastVal);
			}
		});
        contentTable.add(fpsButton).width(100).height(25).spaceRight(5).spaceBottom(5);
        
        hintButton = new TextButton("Display Hints", toggleStyle);
        hintButton.setWidth(100);
        hintButton.setHeight(25f);
        hintButton.setChecked(prefs.getBoolean("showHints"));
        hintButton.addListener(new ChangeListener() {
			boolean lastVal = false;
			long lastSound = 0;
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(System.currentTimeMillis()-lastSound>millisSoundCooldown) {
					game.playSound("tick", .8f);
					lastSound = System.currentTimeMillis();
				}
				lastVal = hintButton.isChecked();
				prefs.putBoolean("showHints", lastVal);
			}
		});
        contentTable.add(hintButton).width(100).height(25).spaceRight(5).spaceBottom(5);
        
        hintResetButton = new TextButton("Reset Hints", clickStyle);
        hintResetButton.setWidth(100);
        hintResetButton.setHeight(25f);
        hintResetButton.addListener(new ChangeListener() {
			long lastSound = 0;
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(System.currentTimeMillis()-lastSound>millisSoundCooldown) {
					game.playSound("tick", .8f);
					lastSound = System.currentTimeMillis();
				}
				HintMenuType.resetHints();
				HintMenuType.saveViewedHints();
			}
		});
        contentTable.add(hintResetButton).width(100).height(25).spaceRight(5).spaceBottom(5);
        
        fullscreenButton = new TextButton("Fullscreen", toggleStyle);
        fullscreenButton.setWidth(100);
        fullscreenButton.setHeight(25f);
        fullscreenButton.setChecked(prefs.getBoolean("fullscreen"));
        fullscreenButton.addListener(new ChangeListener() {
			boolean lastVal = false;
			long lastSound = 0;
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(System.currentTimeMillis()-lastSound>millisSoundCooldown) {
					game.playSound("tick", .8f);
					lastSound = System.currentTimeMillis();
				}
				lastVal = fullscreenButton.isChecked();
				prefs.putBoolean("fullscreen", lastVal);
				game.setWindowMode();
			}
		});
        contentTable.add(fullscreenButton).width(100).height(25).spaceRight(5).spaceBottom(5);
        
        invertScrollButton = new TextButton("Invert Scroll", toggleStyle);
        invertScrollButton.setWidth(100);
        invertScrollButton.setHeight(25f);
        invertScrollButton.setChecked(prefs.getBoolean("invertScroll"));
        invertScrollButton.addListener(new ChangeListener() {
			boolean lastVal = false;
			long lastSound = 0;
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(System.currentTimeMillis()-lastSound>millisSoundCooldown) {
					game.playSound("tick", .8f);
					lastSound = System.currentTimeMillis();
				}
				lastVal = invertScrollButton.isChecked();
				prefs.putBoolean("invertScroll", lastVal);
			}
		});
        contentTable.add(invertScrollButton).width(100).height(25).spaceRight(5).spaceBottom(5);
        
        connectionThreshold = new TextButton("Laxed Movement\nCorrection", toggleStyleSmaller);
        connectionThreshold.setWidth(100);
        connectionThreshold.setHeight(25f);
        connectionThreshold.setChecked(prefs.getBoolean("connectionThreshold"));
        connectionThreshold.addListener(new ChangeListener() {
			boolean lastVal = false;
			long lastSound = 0;
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(System.currentTimeMillis()-lastSound>millisSoundCooldown) {
					game.playSound("tick", .8f);
					lastSound = System.currentTimeMillis();
				}
				lastVal = connectionThreshold.isChecked();
				prefs.putBoolean("connectionThreshold", lastVal);
			}
		});
        contentTable.add(connectionThreshold).width(100).height(25).spaceLeft(deltaTime).spaceRight(5).spaceBottom(5);
        contentTable.row();
        
        PlayerDisplayWidget pdw = new PlayerDisplayWidget(game, displayPlayer);
        contentTable.add(pdw).width(70).height(70);
        
        playerTypeButton = new TextButton("Change Player", clickStyle);
        playerTypeButton.setWidth(100);
        playerTypeButton.setHeight(25f);
        playerTypeButton.addListener(new ChangeListener() {
			long lastSound = 0;
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(System.currentTimeMillis()-lastSound>millisSoundCooldown) {
					game.playSound("tick", .8f);
					lastSound = System.currentTimeMillis();
				}
				prefs.putInteger("playerType", (prefs.getInteger("playerType")+1)%(Player.AVAILABLE_PLAYER_TYPES+1));
				displayPlayer.setPlayerType(prefs.getInteger("playerType"));
			}
		});
        contentTable.add(playerTypeButton).width(100).height(25).spaceRight(5).spaceBottom(5);

        
        back = new TextButton("Back", game.getSkin(), "button");
		back.setPosition(20f, 30, Align.left);
		back.setWidth(150f);
		back.setHeight(37.5f);
		back.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y)
		    {
				game.playSound("falseSelect", 1f);
		    	game.setScreen(new MainMenuScreen(game));
		    }
		});
		stage.addActor(back);
		
		
		
		stage.addActor(contentTable);
		
		/*TextField input = new TextField(null, game.getSkin(), "textFieldLarge");
		input.setPosition(640/2, 360/2, Align.center);
		input.setWidth(200);
		input.setHeight(25);
		input.setOrigin(Align.center);
		input.setMaxLength(16);
		input.setOnlyFontChars(true);
		input.setFocusTraversal(false);
		stage.addActor(input);
		*/


		Gdx.input.setInputProcessor(stage);
	}
	
	private String getFPSString(int value) {
		if(value<0) {
			return "Unlimited";
		}
		if(value==0) {
			return "VSync";
		}
		return value+"";
	}
	
	@Override
	public void show() {
		
	}

	float deltaTime;
	
	@Override
	public void render(float delta) {
		game.batch.setProjectionMatrix(game.backgroundViewport.getCamera().combined);
		game.batch.begin();
		Background.draw(game.batch, game.backgroundViewport);
		game.batch.end();
		
		deltaTime+=delta;
		double vX = 0, vY = 0;
		int v = (int) Math.floor((deltaTime/1f)%4f);
		if(v == 0) {vX = 0; vY = -1;}
		if(v == 1) {vX = 1; vY = 0;}
		if(v == 2) {vX = 0; vY = 1;}
		if(v == 3) {vX = -1; vY = 0;}
		displayPlayer.setVelocity(vX, vY);
		
		game.viewport.apply();
		game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
		game.batch.begin();
		game.getFont(45).draw(game.batch, "Settings", 30, 360-30);
		stage.act();
		stage.draw();
		game.batch.end();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height);
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		prefs.flush();
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

}
