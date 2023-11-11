package net.cmr.gaze.stage.menus;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
import net.cmr.gaze.networking.packets.ResearchPacket;
import net.cmr.gaze.research.ResearchData;
import net.cmr.gaze.research.ResearchTree;
import net.cmr.gaze.research.ResearchVertex;
import net.cmr.gaze.research.ResearchVertex.RequirementWidget;
import net.cmr.gaze.research.ResearchVertex.ResearchRequirement;
import net.cmr.gaze.research.ResearchVertex.ResearchRequirement.ResearchRequirementType;
import net.cmr.gaze.stage.GameScreen;

public class ResearchMenu extends GameMenu {
    
    Gaze game;
    GameScreen screen;

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

    public static ResearchVertex getVertex(String universalID) {
        for(ResearchTree tree : researchTrees) {
            String treeTest = tree.getUniversalID(tree.root);
            if(universalID.indexOf(treeTest) != 0) {
                continue;
            }
            if(universalID.equals(treeTest)) {
                return tree.root;
            }
            String internalID = universalID.substring(treeTest.length()+1);
            ResearchVertex vertex = tree.getResearchNodes().get(internalID);
            if(vertex != null) {
                return vertex;
            }
        }
        return null;
    }

    @Override
    public int getOpenKey() {
        return Input.Keys.G;
    }

    public ResearchMenu(Gaze game, GameScreen screen) {
        super(MenuAlignment.CENTER);
        this.game = game;
        this.screen = screen;
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
                    String univString = vertex.tree.getUniversalID(vertex);
                    screen.sender.addPacket(new ResearchPacket(univString));

                    //data.setResearched(vertex, true);
                    //researchButtonGroup.uncheckAll();
                    //game.playSound("intro", 1f);
                    //refreshResearchPanel(false);

                    return true;
                }
                return false;
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

    public void refreshResearchPanel(boolean resetScroll) {
        WidgetGroup group = new WidgetGroup();
        group.setBounds(0, 0, 168*2, 81*2*5);

        researchButtonGroup = new ButtonGroup<>();
        researchButtonGroup.setMinCheckCount(0);
        researchButtonGroup.setMaxCheckCount(1);
        if(researchTree == null) {return;}
        for(ResearchVertex vertex : researchTree.getResearchNodes().values()) {
            String icon = vertex.icon.replaceAll(">", "");
            TextureRegion tr = null;
            if(!game.hasSprite(icon) || vertex.icon.contains(">")) {
                Animation<TextureRegion> anim = game.getAnimation(icon);
                if(anim != null) {
                     tr = anim.getKeyFrame(0f);
                } else {
                    tr = game.getSprite(icon);
                }
            } else {
                tr = game.getSprite(icon);
            }
            TextureRegionDrawable drawable = new TextureRegionDrawable(tr);
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

    public ResearchTree getTree() {
        return researchTree;
    }
    public void setResearchTree(int index) {
        researchTree = researchTrees.get(index);
    }

    public ResearchData getData() {
        return data;
    }
    public void setResearchData(ResearchData data) {
        this.data = data;
    }

    public static class ResearchWidget extends ImageButton {
        private static TextureRegionDrawable background;
        public ResearchVertex vertex;
        boolean hit = false;
        Gaze game;
        
        public ResearchWidget(ResearchMenu menu, ResearchVertex vertex, TextureRegionDrawable icon, Gaze game) {
            super(new ImageButtonStyle(icon, icon, icon, null, getBackground(game), getBackground(game)));
            this.vertex = vertex;
            this.game = game;
            boolean canBeResearched = false;
            if(vertex.parent != null) {
                boolean cannotBeResearched = false;
                cannotBeResearched = cannotBeResearched || !menu.data.isResearched(vertex.parent.tree.getUniversalID(vertex.parent));
                for(ResearchRequirement requirement : vertex.requirements) {
                    if(requirement.category != ResearchRequirementType.RESEARCH) {
                        continue;
                    }
                    cannotBeResearched = cannotBeResearched || !menu.data.isResearched(requirement.getResearchID());
                }
                canBeResearched = !cannotBeResearched;
            } else {
                canBeResearched = true;
            }

            if(!canBeResearched) {
                setColor(.75f, .75f, .75f, .25f);
                setDisabled(true);
            } else if(!menu.data.isResearched(vertex)) {
                setColor(1, 1, 1f, 1);
            } else {
                setColor(148/255f, 255/255f, 159/255f, 1);
            }
            addListener(new InputListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    hit = true;
                    tdim = ResearchWidget.super.getWidth();
                    tname = vertex.name;
                    tdisc = vertex.description;
                }
            });
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            if(hit) {
                Vector2 mouseScreenPosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
                Vector2 mouseLocalPosition = screenToLocalCoordinates(mouseScreenPosition);
                if(hit(mouseLocalPosition.x, mouseLocalPosition.y, false) != null) {
                    Vector2 position = localToStageCoordinates(new Vector2(mouseLocalPosition.x, mouseLocalPosition.y));
                    tx = position.x;
                    ty = position.y;
                } else {
                    hit = false;
                    tname = null;
                    tdisc = null;
                }
            }
        }

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
            } else {
                tname = null;
                tdisc = null;
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

    static float tx, ty, tdim;
    static String tname, tdisc;

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        BitmapFont font = game.getFont(7);
        if(tname != null) {
            font.draw(batch, tname, tx+5, ty+5+7/2f);
            font.draw(batch, tdisc, tx+5, ty-5+7/2f);
        }
    }
    
}
