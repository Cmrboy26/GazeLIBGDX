package net.cmr.gaze.stage.widgets;

import java.util.concurrent.Callable;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.PlayerDisplayWidget;
import net.cmr.gaze.world.entities.Player;

public class SettingsWidget extends ScrollPane {
    
    public enum Setting {
        GRAPHICS("Graphics"), PLAYER("Player"), ONLINE("Online"), CONTROLS("Controls"), AUDIO("Audio");
        String displayName;
        Setting(String displayName) {
            this.displayName = displayName;
        }
        public String getDisplayName() {
            return displayName;
        }
    }

    public final static int SPACING = 10;
    private final static int ELEMENT_SPACING_Y = 10, ELEMENT_SPACING_X = 5;
    Gaze game;
    Setting setting;

    public SettingsWidget(Gaze game, Setting setting) {
        super(getTable(game, setting));
        this.game = game;
        this.setting = setting;
        this.setWidth(640/2-SPACING);
        this.setHeight(360-SPACING);
        this.setScrollPercentY(100f);
        setOverscroll(false, false);
    }

    public Setting getSetting() {
        return setting;
    }

    public static Table getTable(Gaze game, Setting setting) {
        Table table = new Table();
        table.setBackground(new NinePatchDrawable(game.getNinePatch("helpBox")));
        table.setWidth(640/2-SPACING);
        table.setHeight(360-SPACING);
        switch (setting) {
            case GRAPHICS:
                Slider fpsSlider = getSlider(game, "FPS", -10, 150, 10, Integer.class);
                Label fpsLabel = getAdaptiveLabel(game, "FPS", new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        int value = (int) fpsSlider.getValue();
                        if(value<0) {
                            return "Unlimited";
                        }
                        if(value==0) {
                            return "VSync";
                        }
                        return value+"";
                    } 
                });
                insert(table, fpsLabel, fpsSlider);

                TextButton fullscreenButton = getToggleButton(game, "fullscreen", "Fullscreen", "On", "Off");
                TextButton particlesButton = getToggleButton(game, "displayParticles", "Particles", "On", "Off");
                insert(table, fullscreenButton, particlesButton);

                Slider uiScaleSlider = getSlider(game, "uiZoom", 1f, 3f, .5f, Float.class);
                Label uiScaleLabel = getAdaptiveLabel(game, "UI Scale", new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return ((int) (1/uiScaleSlider.getValue()*100))+"%";
                    } 
                });
                insert(table, uiScaleLabel, uiScaleSlider);

                Slider worldScaleSlider = getSlider(game, "worldZoom", 1f, 4f, 1, Float.class);
                Label worldScaleLabel = getAdaptiveLabel(game, "World Zoom", new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return ((int) (2/worldScaleSlider.getValue()*100))+"%";
                    } 
                });
                insert(table, worldScaleLabel, worldScaleSlider);

                break;
            case PLAYER:
                final Player displayPlayer = new Player(game.settings.getInteger("playerType"), null);
                PlayerDisplayWidget playerDisplay = new PlayerDisplayWidget(game, displayPlayer) {
                    double deltaTime;
                    @Override
                    public void act(float delta) {
                        super.act(delta);
                        deltaTime+=delta;
                        double vX = 0, vY = 0;
                        int v = (int) Math.floor((deltaTime/1f)%4f);
                        if(v == 0) {vX = 0; vY = -1;}
                        if(v == 1) {vX = 1; vY = 0;}
                        if(v == 2) {vX = 0; vY = 1;}
                        if(v == 3) {vX = -1; vY = 0;}
                        displayPlayer.setVelocity(vX, vY);
                    }
                };
                TextButtonStyle style = new TextButtonStyle(game.getSkin().get("toggle", TextButtonStyle.class));
                TextButton playerTypeButton = new TextButton("Change Player", style);
                playerDisplay.setHeight(200);
                playerDisplay.setWidth(200);
                playerTypeButton.setWidth(100);
                playerTypeButton.setHeight(25f);
                playerTypeButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        game.settings.putInteger("playerType", (game.settings.getInteger("playerType")+1)%(Player.AVAILABLE_PLAYER_TYPES+1));
                        displayPlayer.setPlayerType(game.settings.getInteger("playerType"));
                    }
                });
                insert(table, playerDisplay, playerTypeButton);

                break;
            case ONLINE:
                break;
            case CONTROLS:
                break;
            case AUDIO:
                Slider masterVolume = getSlider(game, "masterVolume", 0, 1f, .05f, Float.class);
                Label masterVolumeLabel = getAdaptiveLabel(game, "Master", new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return ((int) (masterVolume.getValue()*100))+"%";
                    } 
                });
                insert(table, masterVolumeLabel, masterVolume);
                Slider musicVolume = getSlider(game, "musicVolume", 0, 1f, .05f, Float.class);
                Label musicVolumeLabel = getAdaptiveLabel(game, "Music", new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return ((int) (musicVolume.getValue()*100))+"%";
                    } 
                });
                insert(table, musicVolumeLabel, musicVolume);
                Slider sfxVolume = getSlider(game, "sfxVolume", 0, 1f, .05f, Float.class);
                Label sfxVolumeLabel = getAdaptiveLabel(game, "SFX", new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return ((int) (sfxVolume.getValue()*100))+"%";
                    } 
                });
                insert(table, sfxVolumeLabel, sfxVolume);
                Slider ambientVolume = getSlider(game, "ambientVolume", 0, 1f, .05f, Float.class);
                Label ambientVolumeLabel = getAdaptiveLabel(game, "Ambient", new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return ((int) (ambientVolume.getValue()*100))+"%";
                    } 
                });
                insert(table, ambientVolumeLabel, ambientVolume);
                break;
            default:
                break;
        }
        return table;
    }

    public static Preferences getPreferences(Gaze game) {
        return game.settings;
    }

    public static void insert(Table table, Actor actorLeft, Actor actorRight) {
        Cell<Actor> left = table.add(actorLeft).padLeft(ELEMENT_SPACING_X).padBottom(ELEMENT_SPACING_Y/2f).padTop(ELEMENT_SPACING_Y/2f);
        if(left.getActor() instanceof Label) {
            left.expandX();
        }
        Cell<Actor> right = table.add(actorRight).padRight(ELEMENT_SPACING_X).padBottom(ELEMENT_SPACING_Y/2f).padTop(ELEMENT_SPACING_Y/2f);
        if(right.getActor() instanceof Label) {
            right.expandX();
        }
        right.row();
    }

    public static Label getAdaptiveLabel(Gaze game, final String text, final Callable<String> adaptiveStringRetriever) {
        LabelStyle style = new LabelStyle(game.getFont(15), Color.WHITE);
        Label label = new Label(text, style) {
            @Override
            public void act(float delta) {
                super.act(delta);
                try {
                    setText(text+": "+adaptiveStringRetriever.call());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        label.setHeight(20);
        return label;
    }

    public static <T extends Number> Slider getSlider(Gaze game, String settingsLocation, float min, float max, float step, Class<T> valueType) {
        Slider slider = new Slider(min, max, step, false, game.getSkin(), "slider") {
            float lastValue = Integer.MIN_VALUE;
            @Override
            public void act(float delta) {
                super.act(delta);
                if(lastValue==Integer.MIN_VALUE) {
                    lastValue = getValue();
                }
                if(getValue()!=lastValue) {
                    lastValue = getValue();
                    if(valueType==Integer.class) {
                        getPreferences(game).putInteger(settingsLocation, (int) lastValue);
                    } else if(valueType==Float.class) {
                        getPreferences(game).putFloat(settingsLocation, lastValue);
                    }
                    getPreferences(game).flush();
                }
            }
        };
        float value = getPreferences(game).getFloat(settingsLocation);
        slider.setValue(value);

        return slider;
    }

    public static TextButton getToggleButton(Gaze game, String settingsLocation, final String text, String onText, String offText) {
        TextButtonStyle style = new TextButtonStyle(game.getSkin().get("toggle", TextButtonStyle.class));
        style.font = game.getFont(15);
        TextButton button = new TextButton("", style) {
            boolean lastChecked = false;
            boolean lastValueInitialized = false;
            @Override
            public void act(float delta) {
                super.act(delta);
                boolean value = isChecked();
                setText(text + ": " + (value ? onText : offText));
                if(lastValueInitialized) {
                    if(value!=lastChecked) {
                        lastChecked = value;
                        getPreferences(game).putBoolean(settingsLocation, value);
                        getPreferences(game).flush();
                    }
                } else {
                    lastValueInitialized = true;
                }
            }
        };
        boolean value = getPreferences(game).getBoolean(settingsLocation);
        button.setChecked(value);
        return button;
    }

}
