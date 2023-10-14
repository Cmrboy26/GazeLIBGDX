package net.cmr.gaze.stage.widgets;

import java.awt.Color;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.research.ResearchData;
import net.cmr.gaze.research.ResearchTree;
import net.cmr.gaze.research.ResearchVertex;
import net.cmr.gaze.research.ResearchVertex.RequirementWidget;

public class ResearchMenu extends WidgetGroup {
    
    Gaze game;

    ButtonGroup<ImageButton> categoryButtonGroup;
    ButtonGroup<ResearchWidget> researchButtonGroup;
    ImageButton confirmButton;
    ResearchTree researchTree;
    ScrollPane researchPanel;
    ResearchData data;

    public static ArrayList<ResearchTree> researchTrees;
    private static boolean initialized;
    public static void initialize() {
        if(initialized) {return;}
        researchTrees = new ArrayList<>();
        for(int i = 0; true; i++) {
            FileHandle handle = Gdx.files.internal("content/tech"+i+".json");
            if(!handle.exists()) {
                break;
            }
            Gdx.app.log("INFO", "Loading research... tech"+i+".json...");
            try {
                String string = handle.readString();
                researchTrees.add(ResearchTree.deriveResearchGraph(string));
            } catch (InvalidObjectException e) {
                e.printStackTrace();
            }
        }
        initialized = true;
    }

    public ResearchMenu(Gaze game) {
        this.game = game;
        this.data = new ResearchData();
        setVisible(false);

        setBounds(0, 0, 640, 360);

        Image image = new Image(game.getSprite("techBackground"));
        image.setBounds(57*2, 36*2, 206*2, 108*2);
        addActor(image);

        categoryButtonGroup = new ButtonGroup<>();
        categoryButtonGroup.setMinCheckCount(1);
        categoryButtonGroup.setMaxCheckCount(1);

        TextureRegionDrawable background = new TextureRegionDrawable(game.getSprite("itemSlotBackgroundChecked"));
        for(int i = 0; i < 7; i++) {
            TextureRegionDrawable icon = new TextureRegionDrawable(game.getSprite("techCategoryIcon"+(i+1)));
            icon.setMinSize(11*2, 11*2);
            
            ImageButtonStyle categoryStyle = new ImageButtonStyle();
            categoryStyle.imageUp = null;
            categoryStyle.imageDown = background;
            categoryStyle.imageChecked = background;
            categoryStyle.up = icon;
            categoryStyle.down = icon;
            categoryStyle.checked = icon;

            ImageButton button = new ImageButton(categoryStyle);
            final int touchIndex = i;
            button.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    setResearchTree(touchIndex);
                    refreshResearchPanel(true);
                    return true;
                }
            });
            button.setBounds(57*2+6*2, 36*2+108*2-17*2-(i*14*2), 11*2, 11*2);
            categoryButtonGroup.add(button);
            addActor(button);
        }

        TextureRegionDrawable researchButton = new TextureRegionDrawable(game.getSprite("researchConfirm"));
        researchButton.setMinSize(16, 16);
        
        TextureRegionDrawable itemSlotBackgroundChecked = new TextureRegionDrawable(game.getSprite("itemSlotBackgroundChecked"));
        itemSlotBackgroundChecked.setMinSize(16, 16);

        ImageButtonStyle researchButtonStyle = new ImageButtonStyle();
        researchButtonStyle.imageUp = null;
        researchButtonStyle.imageDown = null;
        researchButtonStyle.imageChecked = null;
        researchButtonStyle.up = researchButton;
        researchButtonStyle.down = researchButton.tint(com.badlogic.gdx.graphics.Color.GREEN);
        researchButtonStyle.checked = researchButton;

        confirmButton = new ImageButton(researchButtonStyle);
        confirmButton.setBounds(57*2+184*2, 36*2+8*2, 8*2, 8*2);
        confirmButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(researchButtonGroup.getChecked() == null) {
                    return false;
                } else if(!data.isResearched(researchButtonGroup.getChecked().vertex)) {
                    ResearchVertex vertex = researchButtonGroup.getChecked().vertex;
                    data.setResearched(vertex, true);
                    researchButtonGroup.uncheckAll();
                    game.playSound("intro", 1f);
                    refreshResearchPanel(false);
                }
                return true;
            }
        });
        addActor(confirmButton);

        ScrollPaneStyle style = new ScrollPaneStyle();
        researchPanel = new ScrollPane(null, style);
        researchPanel.setBounds(57*2+24*2, 36*2+21*2, 168*2, 81*2);
        researchPanel.setScrollPercentY(1f);
        addActor(researchPanel);
        setResearchTree(categoryButtonGroup.getCheckedIndex());
        refreshResearchPanel(true);
    }

    private void refreshResearchPanel(boolean resetScroll) {
        WidgetGroup group = new WidgetGroup();
        group.setBounds(0, 0, 168*2, 81*2*5);

        researchButtonGroup = new ButtonGroup<>();
        researchButtonGroup.setMinCheckCount(0);
        researchButtonGroup.setMaxCheckCount(1);
        if(researchTree == null) {return;}
        for(ResearchVertex vertex : researchTree.getResearchNodes().values()) {
            TextureRegionDrawable drawable = new TextureRegionDrawable(game.getSprite(vertex.icon));
            drawable.setMinSize(14*2, 14*2);
            ResearchWidget widget = new ResearchWidget(this, vertex, drawable, game);
            widget.setBounds(vertex.position.x*3f+168-16*.75f, vertex.position.y*3f+16*.75f, 16*1.5f, 16*1.5f);
            group.addActor(widget);
            researchButtonGroup.add(widget);
        }

        Table table = new Table();
        table.add(group).width(168*2).height(81*4);
        researchPanel.setActor(table);
        researchPanel.layout();
        if(resetScroll) {
            researchPanel.setScrollPercentY(1f);
        }
    } 

    public static class ResearchWidget extends ImageButton {
        public ResearchVertex vertex;
        public ResearchWidget(ResearchMenu menu, ResearchVertex vertex, TextureRegionDrawable icon, Gaze game) {
            super(new ImageButtonStyle(icon, icon, icon, null, getBackground(game), getBackground(game)));
            this.vertex = vertex;
            if(vertex.parent != null && !menu.data.isResearched(vertex.parent.tree.getUniversalID(vertex.parent))) {
                setColor(.75f, .75f, .75f, .5f);
                setDisabled(true);
            } else if(!menu.data.isResearched(vertex)) {
                setColor(.75f, .75f, .75f, 1);
            } else {
                setColor(com.badlogic.gdx.graphics.Color.WHITE);
            }
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
        }

        private static TextureRegionDrawable background;
        private static TextureRegionDrawable getBackground(Gaze game) {
            if(background!=null) {
                return background;
            }
            return new TextureRegionDrawable(game.getSprite("itemSlotBackgroundChecked"));
        }
    }

    boolean lastVisibility;
    ResearchWidget lastResearchVertex;
    RequirementWidget requirementWidget;

    @Override
    public void act(float delta) {
        super.act(delta);
        if(isVisible() != lastVisibility) {
            lastVisibility = isVisible();
            getStage().setScrollFocus(researchPanel);
            if(lastVisibility) {
                refreshResearchPanel(false);
            }
        }
        if(!isVisible() && getStage().getScrollFocus()!=null) {
            getStage().setScrollFocus(null);
        }
        ResearchWidget newOne = researchButtonGroup.getChecked();
        if(!Objects.equals(lastResearchVertex, newOne)) {
            lastResearchVertex = newOne;
            if(requirementWidget!=null) {
                requirementWidget.remove();
            }
            if(lastResearchVertex == null) {
                return;
            }
            if(!data.isResearched(lastResearchVertex.vertex)) {
                RequirementWidget widget = lastResearchVertex.vertex.getRequirementWidget(game);
                widget.setPosition(57*2+(28)*2, 36*2+(7.5f)*2);
                requirementWidget = widget;
                addActor(widget);
            }
        }
    }

    public void setResearchTree(int index) {
        researchTree = researchTrees.get(index);
    }
    
}
