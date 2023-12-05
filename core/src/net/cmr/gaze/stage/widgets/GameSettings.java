package net.cmr.gaze.stage.widgets;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Callable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.DataBuffer;
import com.badlogic.gdx.utils.DelayedRemovalArray;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.PlayerDisplayWidget;
import net.cmr.gaze.util.Pair;
import net.cmr.gaze.world.entities.Player;

public class GameSettings extends ScrollPane {
    
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

    public enum InputType {
        NONE,
        KEYBOARD, 
        MOUSE, 
        CONTROLLER;
    }

    public enum Controls {

        // Any Input Controls
        MOVE_UP(0),
        MOVE_DOWN(1),
        MOVE_LEFT(2),
        MOVE_RIGHT(3),
        SPRINT(4),
        INTERACT(5), // used for interacting in the world and special clicking in the inventory, Right click by default
        SELECT(6), // used for attacking, breaking, clicking inventory, Right click by default
        CLOSE(7), // used for closing the menu
        INVENTORY(8),
        RESEARCH(9),
        CRAFTING(10);

        // Keyboard Only Controls

        int defaultControlCode;
        InputType defaultInputType;
        int id;
        Controls(int id) {
            this(id, -1, InputType.NONE);
        }
        Controls(int id, int defaultControlCode, InputType defaultInputType) {
            this.id = id;
            this.defaultControlCode = defaultControlCode;
            this.defaultInputType = defaultInputType;
        }

        public void write(DataBuffer buffer) throws IOException {
            buffer.writeInt(id);
        } 
        public static Controls read(DataInputStream input) throws IOException {
            return controls.get(input.readInt());
        }

        public int getDefaultControlCode() {
            return defaultControlCode;
        }
        public InputType getDefaultInputType() {
            return defaultInputType;
        }

        public boolean isDown() {
            return GameSettings.isDown(this);
        }
        public boolean isJustDown() {
            return GameSettings.isJustDown(this);
        }

    }

    static boolean initialized = false;
    static HashMap<Integer, Controls> controls = new HashMap<Integer, Controls>();
    static HashMap<Controls, Pair<Integer, InputType>> controlSettings = new HashMap<Controls, Pair<Integer, InputType>>();

    public static void initialize() {
        initialized = true;
        controls = new HashMap<Integer, Controls>();
        controlSettings = new HashMap<Controls, Pair<Integer, InputType>>();
        for(Controls control : Controls.values()) {
            controls.put(control.id, control);
        }
        if(Gaze.singletonExists()) {
            Gaze game = Gaze.get();
            for(Controls control : Controls.values()) {
                if(!game.settings.get().containsKey("control_"+control.name().toLowerCase()) || !game.settings.get().containsKey("control_"+control.name().toLowerCase()+"_inputType")) {
                    game.settings.putInteger("control_"+control.name().toLowerCase(), control.getDefaultControlCode());
                    game.settings.putInteger("control_"+control.name().toLowerCase()+"_inputType", control.getDefaultInputType().ordinal());
                    game.settings.flush();
                }
                int controlCode = game.settings.getInteger("control_"+control.name().toLowerCase());
                int inputTypeIndex = game.settings.getInteger("control_"+control.name().toLowerCase()+"_inputType");
                InputType inputType = null;
                if(controlCode==-1 || inputTypeIndex<=0 || controlCode>=Controls.values().length) {
                    controlCode = control.getDefaultControlCode();
                    inputType = control.getDefaultInputType();
                } else {
                    inputType = InputType.values()[inputTypeIndex];
                }
                setControl(game, control, controlCode, inputType);
            }
        }
    }

    public static void setControl(Gaze game, Controls control, int controlCode, InputType inputType) {
        controlSettings.put(control, new Pair<Integer, InputType>(controlCode, inputType));
        game.settings.putInteger("control_"+control.name().toLowerCase(), controlCode);
        game.settings.putInteger("control_"+control.name().toLowerCase()+"_inputType", inputType.ordinal());
        System.out.println("Set control "+control.name().toLowerCase()+" to "+controlCode+" with input type "+inputType.name());
        System.out.println(game.settings.getInteger("control_"+control.name().toLowerCase())+" "+game.settings.getInteger("control_"+control.name().toLowerCase()+"_inputType"));
        game.settings.flush();
    }

    private static boolean isDown(Controls controls) {
        // Get the control from the static list
        Pair<Integer, InputType> control = controlSettings.get(controls);

        if(control.getFirst()==-1 || control.getSecond()==InputType.NONE) {
            control = new Pair<Integer, InputType>(controls.getDefaultControlCode(), controls.getDefaultInputType());
            if(control.getFirst()==-1 || control.getSecond()==InputType.NONE) {
                return false;
            }
        }

        switch (control.getSecond()) {
            case KEYBOARD:
                return Gdx.input.isKeyPressed(control.getFirst());
            case MOUSE:
                return Gdx.input.isButtonPressed(control.getFirst());
            case CONTROLLER:
                return false;
            default:
                return false;
        }
    }

    private static boolean isJustDown(Controls controls) {
        // Get the control from the static list
        Pair<Integer, InputType> control = controlSettings.get(controls);

        switch (control.getSecond()) {
            case KEYBOARD:
                return Gdx.input.isKeyJustPressed(control.getFirst());
            case MOUSE:
                return Gdx.input.isButtonJustPressed(control.getFirst());
            case CONTROLLER:
                return false;
            default:
                return false;
        }
    }

    public final static int SPACING = 10;
    private final static int ELEMENT_SPACING_Y = 10, ELEMENT_SPACING_X = 5;
    Gaze game;
    Setting setting;

    public GameSettings(Gaze game, Setting setting) {
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
                TextField moveUp = getControlButton(game, Controls.MOVE_UP);
                TextField moveDown = getControlButton(game, Controls.MOVE_DOWN);
                insert(table, moveUp, moveDown);
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

    public static TextField getControlButton(Gaze game, final Controls controls) {
        TextField textField = new TextField("", game.getSkin(), "textField") {
            int displayKeyCode = -1;
            boolean addedInputListener = false;
            @Override
            public void act(float delta) {
                super.act(delta);
                if(!addedInputListener) {
                    Pair<Integer, InputType> control = controlSettings.get(controls);
                    displayKeyCode = control.getFirst();
                    addCaptureListener(new InputListener() {
                        @Override
                        public boolean keyDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, int keycode) {
                            if(keycode==Input.Keys.ESCAPE) {
                                setControl(game, controls, -1, InputType.NONE);
                                displayKeyCode = -1;
                            } else {
                                System.out.println("SETTING CONTROL TO "+keycode);
                                setControl(game, controls, keycode, InputType.KEYBOARD);
                                displayKeyCode = keycode;
                            }
                            return true;
                        }
                    });
                    addedInputListener = true;
                }
                if(displayKeyCode!=-1) {
                    setText(Input.Keys.toString(displayKeyCode));
                } else {
                    setText("None");
                }
            }
        };
        textField.setMessageText("Press a key...");
        return textField;
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
