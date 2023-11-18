package net.cmr.gaze.stage;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import net.cmr.gaze.Gaze;

public class SetupScreen implements Screen {

	Stages stages;
	Gaze game;
	TextField password, passwordConfirm, pin, pinConfirm, username;
	TextButton create;
	InputMultiplexer multi;
	
	String errorMessage = "";
	
	public SetupScreen(final Gaze game) {
		this.game = game;
		this.stages = new Stages();
		multi = stages.getInputMultiplexer();
		stages.get(Align.center).getRoot().addCaptureListener(new InputListener() {
		    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		        if (!(event.getTarget() instanceof TextField)) stages.get(Align.center).setKeyboardFocus(null);
		        return false;
		    }
		});
		
		int top = 120;
		int spacing = 40;
		
		final int height = 36;
		
		TextFieldStyle style = new TextFieldStyle(game.getSkin().get("textFieldLarge", TextFieldStyle.class));
		style.font = game.getFont(14);
		
		username = new TextField("", style);
		username.setMessageText("Username (changeable whenever)");
		username.setMaxLength(16);
		username.setAlignment(Align.center);
		username.setBounds(320-height*3*1.25F, 360-top, height*6*1.25F, height*1.25F);
		username.setTextFieldFilter(new TextFieldFilter() {

			@Override
			public boolean acceptChar(TextField textField, char c) {
				if(Character.isAlphabetic(c)) {
					return true;
				}
				if(Character.isDigit(c)) {
					return true;
				}
				
				return false;
			}
			
		});
		stages.get(Align.center).addActor(username);
		
		password = new TextField("", game.getSkin(), "textFieldLarge");
		password.setMessageText("Password");
		password.setMaxLength(32);
		password.setAlignment(Align.center);
		password.setBounds(320-height*3, 360-top-spacing, height*6, height);
		password.setPasswordMode(true);
		password.setPasswordCharacter('*');
		stages.get(Align.center).addActor(password);
		
		passwordConfirm = new TextField("", game.getSkin(), "textFieldLarge");
		passwordConfirm.setMessageText("Confirm Password");
		passwordConfirm.setMaxLength(32);
		passwordConfirm.setAlignment(Align.center);
		passwordConfirm.setBounds(320-height*3, 360-top-spacing-spacing, height*6, height);
		passwordConfirm.setPasswordMode(true);
		passwordConfirm.setPasswordCharacter('*');
		stages.get(Align.center).addActor(passwordConfirm);
		
		pin = new TextField("", game.getSkin(), "textField");
		pin.setMaxLength(8);
		pin.setMessageText("Pin");
		pin.setAlignment(Align.center);
		pin.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
		pin.setBounds(320-height*2, 360-top-spacing-spacing-spacing, height*4, height);
		pin.setPasswordMode(true);
		pin.setPasswordCharacter('*');
		stages.get(Align.center).addActor(pin);
		
		pinConfirm = new TextField("", game.getSkin(), "textField");
		pinConfirm.setMaxLength(8);
		pinConfirm.setMessageText("Confirm Pin");
		pinConfirm.setAlignment(Align.center);
		pinConfirm.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
		pinConfirm.setBounds(320-height*2, 360-top-spacing-spacing-spacing-spacing, height*4, height);
		pinConfirm.setPasswordMode(true);
		pinConfirm.setPasswordCharacter('*');
		stages.get(Align.center).addActor(pinConfirm);
		
		create = new TextButton("Create", game.getSkin(), "button");
		create.setBounds(320-45*2, 10, 45*4, 45);
		create.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y) {
		    	
		    	if(createLogin()) {
					game.playSound("trueSelect", 1f);
					game.setScreen(new StartupScreen(game));
		    	} else {
					game.playSound("falseSelect", 1f);
		    	}
		    	
				//game.playSound("trueSelect", 1f);
		    	//game.setScreen(new GameScreen(game));
		    }
		});
		stages.get(Align.center).addActor(create);
		
		Gdx.input.setInputProcessor(multi);
	}
	
	@Override
	public void show() {
		
	}

	@Override
	public void render(float delta) {
		
		game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
		game.batch.begin();
		
		game.viewport.apply();
		
		GlyphLayout layout = new GlyphLayout(game.getFont(40), "Account Setup");
		float fontX = 320 - layout.width/2;
		float fontY = 35 - layout.height/2;
		
		game.getFont(40).draw(game.batch, "Account Setup", fontX, 360-fontY);
		
		layout = new GlyphLayout(game.getFont(16), errorMessage);
		fontX = 320 - layout.width/2;
		fontY = 35+30 - layout.height/2;
		
		game.getFont(16).draw(game.batch, errorMessage, fontX, 360-fontY);

		stages.act(delta);
		stages.render(game.batch, false);
		
		game.batch.end();
	}

	@Override
	public void resize(int width, int height) {
		stages.resize(width, height);
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void dispose() {
		stages.dispose();
	}
	
	public boolean createLogin() {
		
		if(!password.getText().equals(passwordConfirm.getText())) {
			errorMessage = "Passwords do not equal each other.";
			return false;
		}
		if(!pin.getText().equals(pinConfirm.getText())) {
			errorMessage = "Pins do not equal each other.";
			return false;
		}
		
		if(password.getText().length() < 6) {
			errorMessage = "Password is not long enough. Minimum length: 6";
			return false;
		}
		if(pin.getText().length() < 4) {
			errorMessage = "Pin is not long enough. Minimum length: 4";
			return false;
		}
		
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			String toEncode = password+":"+pin;
			byte[] output = digest.digest(toEncode.getBytes(StandardCharsets.UTF_8));
			Preferences login = Gdx.app.getPreferences("LoginData");
			UUID id = UUID.nameUUIDFromBytes(output);
			login.putString("credentials", id.toString());
			login.putString("username", username.getText());
			login.flush();
		} catch (NoSuchAlgorithmException e) {
			//e.printStackTrace();
			errorMessage = e.getMessage();
			return false;
		}
		
		
		return true;
	}

}
