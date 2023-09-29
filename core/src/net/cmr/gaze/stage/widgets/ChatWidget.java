package net.cmr.gaze.stage.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.game.ChatManager;
import net.cmr.gaze.game.ChatManager.ChatListener;
import net.cmr.gaze.game.ChatMessage;
import net.cmr.gaze.stage.GameScreen;

public class ChatWidget extends WidgetGroup {
    
    Gaze game;
    GameScreen screen;
    ChatManager manager;
    Label chatLabel;
    ScrollPane scrollPane;
    TextField textField;
    public ChatWidget(Gaze game, GameScreen screen, ChatManager manager) {
        this.game = game;
        this.manager = manager;
        this.screen = screen;
        manager.addListener(new ChatListener() {
            @Override
            public void onMessageRecieved(ChatMessage message) {
                if(getStage().getKeyboardFocus()==null) {
                    scrollPane.setScrollY(10000);
                }
                String chat = "";
                for(int i = 0; i < manager.count; i++) {
                    chat += manager.getMessage(i) + "\n";
                }
                chatLabel.setText(chat);
            }
        });

        TextFieldStyle style = new TextFieldStyle();
        style.font = game.getFont(8);
        style.fontColor = Color.WHITE;
        NinePatch smallButton = new NinePatch(game.buttonNine);
        smallButton.scale(.5f, .5f);
        style.background = new NinePatchDrawable(smallButton);
        style.focusedBackground = new NinePatchDrawable(smallButton);

        textField = new TextField("", style);
        textField.setBounds(0, 0, 160, 20);
        textField.setOnlyFontChars(true);
        textField.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if(Input.Keys.ENTER == keycode) {
                    if(textField.getText().length() > 0) {
                        screen.sendChatMessage(textField.getText());
                        textField.setText("");
                        getStage().setKeyboardFocus(null);
                    }
                    return true;
                }
                return false;
            }
        });
        addActor(textField);

        LabelStyle labelStyle = new LabelStyle();
        labelStyle.font = game.getFont(8);

        chatLabel = new Label("", labelStyle);
        chatLabel.setAlignment(Align.bottomLeft);
        chatLabel.setBounds(0, 0, 160, 80);
        chatLabel.setWrap(true);

        ScrollPaneStyle scrollStyle = new ScrollPaneStyle();

        scrollPane = new ScrollPane(chatLabel, scrollStyle);
        scrollPane.setBounds(2, 22, 160, 80);
        scrollPane.setOverscroll(false, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollbarsVisible(true);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFlingTime(0.2f);
        addActor(scrollPane);
        

        setBounds(0, 0, 160, 100);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        textField.setVisible(getStage().getKeyboardFocus()!=null);
        if(Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            if(getStage().getKeyboardFocus()==null) {
                getStage().setKeyboardFocus(textField);
            }
        }
    }

}
