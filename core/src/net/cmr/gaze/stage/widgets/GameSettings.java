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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.PlayerDisplayWidget;
import net.cmr.gaze.stage.widgets.HintMenu.HintMenuType;
import net.cmr.gaze.util.Pair;
import net.cmr.gaze.world.entities.Player;

public class GameSettings extends ScrollPane {
    
    public enum Setting {
        GRAPHICS("Graphics"), PLAYER("Player"), GAME("Game"), CONTROLS("Controls"), AUDIO("Audio");
        String displayName;
        Setting(String displayName) {
            this.displayName = displayName;
        }
        public String getDisplayName() {
            return displayName;
        }
    }

    public enum InputType {
        NONE(0),
        KEYBOARD(1), 
        MOUSE(2), 
        CONTROLLER(3);
        
        int id;
        InputType(int id) {
            this.id = id;
        }

        public int getID() {
            return id;
        }
    }

    public enum Controls {

        // Any Input Controls
        MOVE_UP(0, Input.Keys.W, InputType.KEYBOARD),
        MOVE_DOWN(1, Input.Keys.S, InputType.KEYBOARD),
        MOVE_LEFT(2, Input.Keys.A, InputType.KEYBOARD),
        MOVE_RIGHT(3, Input.Keys.D, InputType.KEYBOARD),
        SPRINT(4, Input.Keys.SHIFT_LEFT, InputType.KEYBOARD),
        INTERACT(5, Input.Buttons.RIGHT, InputType.MOUSE), // used for interacting in the world and special clicking in the inventory, Right click by default
        SELECT(6, Input.Buttons.LEFT, InputType.MOUSE), // used for attacking, breaking, clicking inventory, Left click by default
        CLOSE(7, Input.Keys.ESCAPE, InputType.KEYBOARD), // used for closing the menu
        INVENTORY(8, Input.Keys.E, InputType.KEYBOARD),
        RESEARCH(9, Input.Keys.R, InputType.KEYBOARD),
        CRAFTING(10, Input.Keys.C, InputType.KEYBOARD);

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
        public String toString() {
            String enumName = name().toLowerCase();
            String[] words = enumName.split("_");
            StringBuilder result = new StringBuilder();
            for (String word : words) {
                result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
            return result.toString().trim();
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
                    game.settings.putInteger("control_"+control.name().toLowerCase()+"_inputType", control.getDefaultInputType().getID());
                    game.settings.flush();
                }
                int controlCode = Integer.parseInt((String) game.settings.get().get("control_"+control.name().toLowerCase()));
                int inputTypeIndex = Integer.parseInt((String) game.settings.get().get("control_"+control.name().toLowerCase()+"_inputType"));
                InputType inputType = null;
                if(inputTypeIndex<=0 || inputTypeIndex>=InputType.values().length) {
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
        game.settings.putInteger("control_"+control.name().toLowerCase()+"_inputType", inputType.getID());
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
                Label fpsLabel = getAdaptiveLabel(game, new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        int value = (int) fpsSlider.getValue();
                        if(value<0) {
                            return "Unlimited";
                        }
                        if(value==0) {
                            return "VSync";
                        }
                        return "FPS: "+value+"";
                    } 
                });
                insert(table, fpsLabel, fpsSlider);

                TextButton fullscreenButton = getToggleButton(game, "fullscreen", "Fullscreen", "On", "Off");
                TextButton particlesButton = getToggleButton(game, "displayParticles", "Particles", "On", "Off");
                insert(table, fullscreenButton, particlesButton);

                Slider uiScaleSlider = getSlider(game, "uiZoom", 1f, 3f, .5f, Float.class);
                Label uiScaleLabel = getAdaptiveLabel(game, new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return "UI Scale: "+((int) (1/uiScaleSlider.getValue()*100))+"%";
                    } 
                });
                insert(table, uiScaleLabel, uiScaleSlider);

                Slider worldScaleSlider = getSlider(game, "worldZoom", 1f, 4f, 1, Float.class);
                Label worldScaleLabel = getAdaptiveLabel(game, new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return "World Zoom: "+((int) (2/worldScaleSlider.getValue()*100))+"%";
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
                insert(table, playerDisplay, null);
                insert(table, playerTypeButton, null);
                break;
            case GAME:
                TextButton invertScrollingButton = getToggleButton(game, "invertScroll", "Invert Scroll", "On", "Off");
                TextButton showFPSButton = getToggleButton(game, "displayFPS", "Show FPS", "On", "Off");
                insert(table, invertScrollingButton, showFPSButton);
                
                TextButton showHintsButton = getToggleButton(game, "showHints", "Hints", "On", "Off");
                TextButton resetHintsButton = getActionButton(game, "Reset Hints", new Runnable() {
                    @Override
                    public void run() {
                        HintMenuType.resetHints();
                        HintMenuType.saveViewedHints();
                    }
                });
                insert(table, showHintsButton, resetHintsButton);
                break;
            case CONTROLS:
                for(final Controls control : Controls.values()) {
                    Label label = getAdaptiveLabel(game, new Callable<String>() {
                        @Override
                        public String call() throws Exception {
                            return control.toString();
                        } 
                    });
                    TextField textField = getControlButton(game, control);
                    insert(table, label, textField);
                }
                break;
            case AUDIO:
                Slider masterVolume = getSlider(game, "masterVolume", 0, 1f, .05f, Float.class);
                Label masterVolumeLabel = getAdaptiveLabel(game, new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return "Master: "+((int) (masterVolume.getValue()*100))+"%";
                    } 
                });
                insert(table, masterVolumeLabel, masterVolume);

                Slider musicVolume = getSlider(game, "musicVolume", 0, 1f, .05f, Float.class);
                Label musicVolumeLabel = getAdaptiveLabel(game, new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return "Music: "+((int) (musicVolume.getValue()*100))+"%";
                    } 
                });
                insert(table, musicVolumeLabel, musicVolume);

                Slider sfxVolume = getSlider(game, "sfxVolume", 0, 1f, .05f, Float.class);
                Label sfxVolumeLabel = getAdaptiveLabel(game, new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return "SFX: "+((int) (sfxVolume.getValue()*100))+"%";
                    } 
                });
                insert(table, sfxVolumeLabel, sfxVolume);

                Slider ambientVolume = getSlider(game, "ambientVolume", 0, 1f, .05f, Float.class);
                Label ambientVolumeLabel = getAdaptiveLabel(game, new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return  "Ambient: "+((int) (ambientVolume.getValue()*100))+"%";
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
       
        if(actorLeft != null) {
            Cell<Actor> left = table.add(actorLeft).padLeft(ELEMENT_SPACING_X).padBottom(ELEMENT_SPACING_Y/2f).padTop(ELEMENT_SPACING_Y/2f);
            if(left.getActor() instanceof Label) {
                left.expandX();
            }
        }
        if(actorRight != null) {
            Cell<Actor> right = table.add(actorRight).padRight(ELEMENT_SPACING_X).padBottom(ELEMENT_SPACING_Y/2f).padTop(ELEMENT_SPACING_Y/2f);
            if(right.getActor() instanceof Label) {
                right.expandX();
            }
        }
        table.row();
    }

    public static TextField getControlButton(Gaze game, final Controls controls) {
        TextField textField = new TextField("", game.getSkin(), "textField") {
            int displayKeyCode = -1;
            InputType displayInputType = InputType.NONE;
            boolean addedInputListener = false, readNextInput = false;
            boolean leftSelected = false;
            @Override
            public void act(float delta) {
                super.act(delta);
                if(leftSelected) {
                    getStage().setKeyboardFocus(null);
                    leftSelected = false;
                }
                if(!addedInputListener) {
                    Pair<Integer, InputType> control = controlSettings.get(controls);
                    displayKeyCode = control.getFirst();
                    displayInputType = control.getSecond();
                    setTouchable(Touchable.enabled);
                    addCaptureListener(new InputListener() {
                        @Override
                        public boolean keyDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, int keycode) {
                            if(keycode==Input.Keys.BACKSPACE) {
                                setControl(game, controls, -1, InputType.NONE);
                                displayKeyCode = -1;
                                displayInputType = InputType.KEYBOARD;
                            } else {
                                setControl(game, controls, keycode, InputType.KEYBOARD);
                                displayKeyCode = keycode;
                                displayInputType = InputType.KEYBOARD;
                            }
                            getStage().setKeyboardFocus(null);
                            readNextInput = false;
                            return true;
                        }
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            if(!readNextInput) {
                                readNextInput = true;
                                return true;
                            } else {
                                if(button==Input.Buttons.LEFT) {
                                    setControl(game, controls, Input.Buttons.LEFT, InputType.MOUSE);
                                    displayKeyCode = Input.Buttons.LEFT;
                                    leftSelected = true;
                                } else if(button==Input.Buttons.RIGHT) {
                                    setControl(game, controls, Input.Buttons.RIGHT, InputType.MOUSE);
                                    displayKeyCode = Input.Buttons.RIGHT;
                                } else if(button==Input.Buttons.MIDDLE) {
                                    setControl(game, controls, Input.Buttons.MIDDLE, InputType.MOUSE);
                                    displayKeyCode = Input.Buttons.MIDDLE;
                                } else if(button==Input.Buttons.FORWARD) {
                                    setControl(game, controls, Input.Buttons.FORWARD, InputType.MOUSE);
                                    displayKeyCode = Input.Buttons.FORWARD;
                                } else if(button==Input.Buttons.BACK) {
                                    setControl(game, controls, Input.Buttons.BACK, InputType.MOUSE);
                                    displayKeyCode = Input.Buttons.BACK;
                                }
                                displayInputType = InputType.MOUSE;
                                readNextInput = false;
                                getStage().setKeyboardFocus(null);
                            }
                            return true;
                        }
                    });
                    addedInputListener = true;
                }
                if(displayKeyCode!=-1) {
                    switch(displayInputType) {
                        case KEYBOARD:
                            setText(Input.Keys.toString(displayKeyCode));
                            break;
                        case MOUSE:
                            switch(displayKeyCode) {
                                case Input.Buttons.LEFT:
                                    setText("Left Mouse");
                                    break;
                                case Input.Buttons.RIGHT:
                                    setText("Right Mouse");
                                    break;
                                case Input.Buttons.MIDDLE:
                                    setText("Middle Mouse");
                                    break;
                                case Input.Buttons.FORWARD:
                                    setText("Forward Mouse");
                                    break;
                                case Input.Buttons.BACK:
                                    setText("Back Mouse");
                                    break;
                                default:
                                    setText("Mouse Button "+displayKeyCode);
                                    break;
                            }
                            break;
                        case CONTROLLER:
                            setText("Controller "+displayKeyCode);
                            break;
                        default:
                            setText("None");
                            break;
                    }
                } else {
                    setText("None");
                }
            }
        };
        return textField;
    }

    public static Label getAdaptiveLabel(Gaze game, final Callable<String> adaptiveStringRetriever) {
        LabelStyle style = new LabelStyle(game.getFont(15), Color.WHITE);
        String text = "";
        try {
            text = adaptiveStringRetriever.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Label label = new Label(text, style) {
            @Override
            public void act(float delta) {
                super.act(delta);
                try {
                    setText(adaptiveStringRetriever.call());
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

    public static TextButton getActionButton(Gaze game, final String text, Runnable action) {
        TextButtonStyle style = new TextButtonStyle(game.getSkin().get("toggle", TextButtonStyle.class));
        style.font = game.getFont(15);
        style.downFontColor = style.fontColor;
        TextButton button = new TextButton(text, style);
        button.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int buttonIndex) {
                action.run();
                return true;
            }
        });
        return button;
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
