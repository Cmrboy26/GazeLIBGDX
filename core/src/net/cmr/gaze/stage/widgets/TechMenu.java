package net.cmr.gaze.stage.widgets;

import java.util.LinkedList;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import net.cmr.gaze.Gaze;

public class TechMenu extends WidgetGroup {
    
    Gaze game;

    ButtonGroup<ImageButton> categoryButtonGroup;
    ImageButton confirmButton;

    public TechMenu(Gaze game) {
        this.game = game;
        setBounds(0, 0, 640, 360);

        Image image = new Image(game.getSprite("techBackground"));
        image.setBounds(57*2, 36*2, 206*2, 108*2);
        addActor(image);

        categoryButtonGroup = new ButtonGroup<>();
        categoryButtonGroup.setMinCheckCount(1);
        categoryButtonGroup.setMaxCheckCount(1);

        for(int i = 0; i < 7; i++) {
            TextureRegionDrawable icon = new TextureRegionDrawable(game.getSprite("techCategoryIcon"+(i+1)));
            icon.setMinSize(11*2, 11*2);
            ImageButton button = new ImageButton(icon);
            final int touchIndex = i;
            button.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    System.out.println(touchIndex);
                    return true;
                }
            });
            button.setBounds(57*2+6*2, 36*2+108*2-17*2-(i*14*2), 11*2, 11*2);
            categoryButtonGroup.add(button);
            addActor(button);
        }

        TextureRegionDrawable researchButton = new TextureRegionDrawable(game.getSprite("researchConfirm"));
        researchButton.setMinSize(16, 16);
        confirmButton = new ImageButton(researchButton);
        confirmButton.setBounds(57*2+184*2, 36*2+8*2, 8*2, 8*2);
        confirmButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("CONFIRM TECH");
                   return true;
            }
        });
        addActor(confirmButton);

        ScrollPaneStyle style = new ScrollPaneStyle();
        ScrollPane researchPanel = new ScrollPane(null, style);
        researchPanel.setBounds(57*2+24*2, 36*2+21*2, 168*2, 81*2);

        addActor(researchPanel);
    } 

    public static class ResearchTree {

        ResearchNode rootNode;

        private ResearchTree() {

        }
        
    }

    public static class ResearchTreeBuilder {
        ResearchNode root;
        ResearchNode current;

    }
    
    private static class ResearchNode {
        
        String name;
        LinkedList<ResearchNode> children;

        public ResearchNode(String name) {
            this.name = name;
            children = new LinkedList<>();
        }

        public void addChild(ResearchNode child) {
            children.add(child);
        }

    }
}
