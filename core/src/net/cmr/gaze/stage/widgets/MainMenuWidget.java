package net.cmr.gaze.stage.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.stage.CreationType;
import net.cmr.gaze.stage.MainMenuScreen;
import net.cmr.gaze.stage.MultiplayerSelectScreen;
import net.cmr.gaze.stage.SaveSelectScreen;
import net.cmr.gaze.stage.SettingScreen;
import net.cmr.gaze.stage.widgets.GameSettings.Setting;

public class MainMenuWidget extends WidgetGroup implements Disposable {

    Gaze game;
    MainMenuScreen screen;
    Texture lineTexture;
    
    Table mainTable, playTable, settingsTable;
	TextButton play, playSingleplayer, playMultiplayer, hostMultiplayer, settings, credits, exit;
    GameSettings settingsWidget;
    final float topDistance = 140;
	final float spacing = 0;
	final float height = 35;
    final float widthScale = 3.5f;
    final float smallHeightScale = .75f;

    public MainMenuWidget(Gaze game, MainMenuScreen screen) {
        this.game = game;
        this.screen = screen;
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.drawPixel(0, 0, Color.WHITE.toIntBits());
        lineTexture = new Texture(pixmap);
        pixmap.dispose();

        mainTable = new Table() {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                super.draw(batch, parentAlpha);
                batch.draw(lineTexture, getX()-12, super.getY(), 2, super.getHeight());
            }
        };

        play = new TextButton("Play", game.getSkin(), "default");
        play.setWidth(height*widthScale);
        play.setHeight(height);
        play.getLabel().setAlignment(Align.left);
        play.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                game.playSound(!playTable.isVisible()+"Select", 1f);
                selectTable(playTable);
            }
        });
        mainTable.add(play).width(height*widthScale).height(height).spaceBottom(spacing/2f).spaceTop(spacing/2f).row();

        playTable = new Table() {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                super.draw(batch, parentAlpha);
                batch.draw(lineTexture, getX()-12, super.getY(), 2, super.getHeight());
                drawPointer(batch, parentAlpha, playTable);
            }
        };

        playSingleplayer = new TextButton("Singleplayer", game.getSkin(), "defaultSmall");
		playSingleplayer.setWidth(height*widthScale);
		playSingleplayer.setHeight(height*smallHeightScale);
        playSingleplayer.getLabel().setAlignment(Align.left);
		playSingleplayer.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y)
		    {
				game.playSound("trueSelect", 1f);
				game.setScreen(new SaveSelectScreen(game, CreationType.Singleplayer));
		    }
		});
        playTable.add(playSingleplayer).width(height*widthScale).height(height*smallHeightScale).spaceBottom(spacing/2f).spaceTop(spacing/2f).row();

		playMultiplayer = new TextButton("Multiplayer", game.getSkin(), "defaultSmall");
		playMultiplayer.setWidth(height*widthScale);
		playMultiplayer.setHeight(height*smallHeightScale);
        playMultiplayer.getLabel().setAlignment(Align.left);
		playMultiplayer.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y)
		    {
				game.playSound("trueSelect", 1f);
				game.setScreen(new MultiplayerSelectScreen(game));
		    }
		});
        playTable.add(playMultiplayer).width(height*widthScale).height(height*smallHeightScale).spaceBottom(spacing/2f).spaceTop(spacing/2f).row();
        
        hostMultiplayer = new TextButton("Host Multiplayer", game.getSkin(), "defaultSmall");
		hostMultiplayer.setWidth(height*widthScale);
		hostMultiplayer.setHeight(height*smallHeightScale);
        hostMultiplayer.getLabel().setAlignment(Align.left);
		hostMultiplayer.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y)
		    {
				game.playSound("trueSelect", 1f);
				game.setScreen(new SaveSelectScreen(game, CreationType.Hosting));
		    }
		});
        playTable.add(hostMultiplayer).width(height*widthScale).height(height*smallHeightScale).spaceBottom(spacing/2f).spaceTop(spacing/2f).row();
        

        settings = new TextButton("Settings", game.getSkin(), "default");
		settings.setWidth(height*widthScale);
		settings.setHeight(height);
        settings.getLabel().setAlignment(Align.left);
		settings.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y)
		    {
                game.playSound(!settingsTable.isVisible()+"Select", 1f);
                selectTable(settingsTable);
				//game.playSound("select", 1f);
		    	//game.setScreen(new SettingScreen(game));
		    }
		});
        mainTable.add(settings).width(height*widthScale).height(height).spaceBottom(spacing/2f).spaceTop(spacing/2f).row();
        
        settingsTable = new Table() {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                super.draw(batch, parentAlpha);
                batch.draw(lineTexture, getX()-12, super.getY(), 2, super.getHeight());
                drawPointer(batch, parentAlpha, settingsTable);
            }
        };

        for(Setting setting : Setting.values()) {
            TextButton button = new TextButton(setting.getDisplayName(), game.getSkin(), "defaultSmall");
            button.setWidth(height*widthScale);
            button.setHeight(height*smallHeightScale);
            button.getLabel().setAlignment(Align.left);
            button.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y)
                {
                    game.playSound("trueSelect", 1f);
                    openSettings(setting);
                }
            });
            settingsTable.add(button).width(height*widthScale).height(height*smallHeightScale).spaceBottom(spacing/2f).spaceTop(spacing/2f).row();    
        }

        
		/*TextButton gameCustomization = new TextButton("In-Game", game.getSkin(), "defaultSmall");
		gameCustomization.setWidth(height*widthScale);
		gameCustomization.setHeight(height*smallHeightScale);
        gameCustomization.getLabel().setAlignment(Align.left);
		gameCustomization.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y)
		    {
				game.playSound("trueSelect", 1f);
                openSettings(Setting.CUSTOMIZATION);
		    }
		});
        settingsTable.add(gameCustomization).width(height*widthScale).height(height*smallHeightScale).spaceBottom(spacing/2f).spaceTop(spacing/2f).row();*/

        /*TextButton playerCustomization = new TextButton("Player", game.getSkin(), "defaultSmall");
		playerCustomization.setWidth(height*widthScale);
		playerCustomization.setHeight(height*smallHeightScale);
        playerCustomization.getLabel().setAlignment(Align.left);
		playerCustomization.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y)
		    {
				game.playSound("trueSelect", 1f);
                openSettings(Setting.PLAYER);
		    }
		});
        settingsTable.add(playerCustomization).width(height*widthScale).height(height*smallHeightScale).spaceBottom(spacing/2f).spaceTop(spacing/2f).row();

        TextButton online = new TextButton("Online", game.getSkin(), "defaultSmall");
		online.setWidth(height*widthScale);
		online.setHeight(height*smallHeightScale);
        online.getLabel().setAlignment(Align.left);
		online.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y)
		    {
				game.playSound("trueSelect", 1f);
                openSettings(Setting.ONLINE);
		    }
		});
        settingsTable.add(online).width(height*widthScale).height(height*smallHeightScale).spaceBottom(spacing/2f).spaceTop(spacing/2f).row();

        TextButton controls = new TextButton("Controls", game.getSkin(), "defaultSmall");
		controls.setWidth(height*widthScale);
		controls.setHeight(height*smallHeightScale);
        controls.getLabel().setAlignment(Align.left);
		controls.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y)
		    {
				game.playSound("trueSelect", 1f);
                openSettings(Setting.CONTROLS);
		    }
		});
        settingsTable.add(controls).width(height*widthScale).height(height*smallHeightScale).spaceBottom(spacing/2f).spaceTop(spacing/2f).row();
        */

        credits = new TextButton("Credits", game.getSkin(), "default");
		credits.setWidth(height*widthScale);
		credits.setHeight(height);
        credits.getLabel().setAlignment(Align.left);
		credits.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y)
		    {
				game.playSound("select", 1f);
		    	game.setScreen(new SettingScreen(game));
		    }
		});
        mainTable.add(credits).width(height*widthScale).height(height).spaceBottom(spacing/2f).spaceTop(spacing/2f).row();

        exit = new TextButton("Exit", game.getSkin(), "default");
		exit.setWidth(height*widthScale);
		exit.setHeight(height);
        exit.getLabel().setAlignment(Align.left);
		exit.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y)
		    {
				game.playSound("falseSelect", 1f);
		    	Gdx.app.exit();
		    }
		});
		mainTable.add(exit).width(height*widthScale).height(height).spaceBottom(spacing/2f).spaceTop(spacing/2f).row();
        
        mainTable.pack();
        mainTable.setPosition(14, -mainTable.getHeight()/2f);
        addActor(mainTable);
                
        playTable.pack();
        playTable.setVisible(false);
        playTable.setPosition(14+mainTable.getWidth(), -playTable.getHeight()/2f+mainTable.getHeight()/mainTable.getRows()+(mainTable.getHeight()/(mainTable.getRows()*2)));
        addActor(playTable);

        settingsTable.pack();
        settingsTable.setVisible(false);
        settingsTable.setPosition(14+mainTable.getWidth(), -settingsTable.getHeight()/2f+0*mainTable.getHeight()/(mainTable.getRows())+(mainTable.getHeight()/(mainTable.getRows()*2)));
        addActor(settingsTable);
    }

    public void drawPointer(Batch batch, float alpha, Actor actor) {
        batch.draw(game.getSprite("pointer"), actor.getX()-12-10, actor.getY()+actor.getHeight()/2f-5, 10, 10);
    }

    public void openSettings(Setting setting) {
        if(settingsWidget!=null) {
            settingsWidget.remove();
            if(settingsWidget.getSetting()==setting) {
                settingsWidget = null;
                return;
            }
            settingsWidget = null;
        }
        settingsWidget = new GameSettings(game, setting);
        settingsWidget.setPosition(640/2+GameSettings.SPACING/2f, GameSettings.SPACING/2f);
        screen.stages.get(Align.right).addActor(settingsWidget);
    }

    public void selectTable(Table table) {
        if(settingsWidget!=null) {
            settingsWidget.remove();
            settingsWidget = null;
        }
        boolean visible = table.isVisible();
        playTable.setVisible(false);
        settingsTable.setVisible(false);

        table.setVisible(!visible);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public void dispose() {
        lineTexture.dispose();
    }

}
