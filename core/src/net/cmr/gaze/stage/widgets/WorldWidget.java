package net.cmr.gaze.stage.widgets;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.stage.SaveSelectScreen;

public class WorldWidget extends WidgetGroup {

	Gaze game;
	boolean selected;
	String worldName;
	LabelStyle style;
	
	Label name, time;
	public Label confirmDelete;
	Image unselectedImg, selectedImg;
	WorldWidgetGroup wwg;
	SaveSelectScreen screen;
	
	public WorldWidget(Gaze game, SaveSelectScreen screen, String worldName, String playTime, WorldWidgetGroup wwg) {
		this.game = game;
		this.screen = screen;
		this.worldName = worldName;
		this.wwg = wwg;
		this.setBounds(0, 0, 320, 60);
		style = new LabelStyle();
		style.font = game.getFont(12f);
		
		selectedImg = new Image(game.getSprite("worldWidgetSelected"));
		selectedImg.setBounds(0, 0, 160*2, 30*2);
		selectedImg.setSize(160*2, 30*2);
		selectedImg.setVisible(isSelected());
		addActor(selectedImg);
		
		unselectedImg = new Image(game.getSprite("worldWidget"));
		unselectedImg.setBounds(0, 0, 160*2, 30*2);
		unselectedImg.setSize(160*2, 30*2);
		unselectedImg.setVisible(!isSelected());
		addActor(unselectedImg);
		
		name = addLabel(worldName, 45, 30, 160-45, 30, Align.left);
		time = addLabel(playTime, 45, 5, 160-45, 30, Align.left);
		confirmDelete = addLabel("", 160, 5, 140, 30, Align.right);
		
		
		addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				wwg.click(WorldWidget.this);
				game.playSound(isSelected()+"Select", 1f);
				return true;
			}
		});
	}
	
	public boolean clickedOnce;
	
	public void attemptDelete() {
		if(clickedOnce) {
			wwg.click(this);
			deleteWorldFolder();
			game.setScreen(new SaveSelectScreen(game, screen.creationType));
			return;
		}
		clickedOnce=true;
		confirmDelete.setText("Confirm delete?");
		wwg.click(this);
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
		selectedImg.setVisible(isSelected());
		unselectedImg.setVisible(!isSelected());
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	private Label addLabel(String str, int x, int y, int w, int h, int align) {
		Label label = new Label(str, style);
		label.setBounds(x, y, w, h);
		label.setAlignment(align);
		addActor(label);
		return label;
	}
	
	public void deleteWorldFolder() {
		FileHandle handle = Gdx.files.external("/Gaze/saves/"+worldName+"/");
		handle.deleteDirectory();
	}
	
	public static WorldWidgetGroup createWorldWidgets(Gaze game, SaveSelectScreen screen) {
		FileHandle handle = Gdx.files.external("/Gaze/saves/");
		File folder = handle.file();
		WorldWidgetGroup wwg = new WorldWidgetGroup();
		for(File directory : folder.listFiles()) {
			String worldName = directory.getName();
			File saveData = new File(directory.getAbsolutePath()+"/saveData.data");
			String playTime = "New Save";
			
			if(saveData.exists()) {
				try {
					FileInputStream fin = new FileInputStream(saveData);
					DataInputStream inputStream = new DataInputStream(fin);
					
					inputStream.readLong();
					long time = inputStream.readLong();
					
					int minutes = (int) Math.floor(time/60f);
					int hours = (int) Math.floor(minutes/60f);
					
					if(time != 0) {
						playTime = hours+":"+(minutes%60)+":"+(time%60);
					}
					
					inputStream.close();
					fin.close();
				} catch(IOException e) {
					e.printStackTrace();
					continue;
				}
			}
			
			WorldWidget world = new WorldWidget(game, screen, worldName, playTime, wwg);
			wwg.addWidget(world);
		}
		
		return wwg;
	}
	
	@Override
	public String toString() {
		return worldName;
	}
}
