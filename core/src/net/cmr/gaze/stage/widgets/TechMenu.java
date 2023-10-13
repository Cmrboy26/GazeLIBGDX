package net.cmr.gaze.stage.widgets;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
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
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import net.cmr.gaze.Gaze;

public class TechMenu extends WidgetGroup {
    
    Gaze game;

    ButtonGroup<ImageButton> categoryButtonGroup;
    ImageButton confirmButton;
    ResearchTree researchTree;
    ScrollPane researchPanel;

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
            Gdx.app.log("TechMenu", "Loading tech"+i+".json...");
            try {
                String string = handle.readString();
                researchTrees.add(ResearchTree.deriveResearchGraph(string));
            } catch (InvalidObjectException e) {
                e.printStackTrace();
            }
        }
        initialized = true;
    }

    public TechMenu(Gaze game) {
        this.game = game;

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
                System.out.println("CONFIRM TECH");
                refreshResearchPanel(false);
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

        ButtonGroup<ResearchWidget> researchButtonGroup = new ButtonGroup<>();
        researchButtonGroup.setMinCheckCount(0);
        researchButtonGroup.setMaxCheckCount(1);
        if(researchTree == null) {return;}
        for(ResearchVertex vertex : researchTree.getResearchNodes().values()) {
            TextureRegionDrawable drawable = new TextureRegionDrawable(game.getSprite(vertex.icon));
            drawable.setMinSize(14*2, 14*2);
            ResearchWidget widget = new ResearchWidget(drawable, game);
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
        public ResearchWidget(TextureRegionDrawable icon, Gaze game) {
            super(new ImageButtonStyle(icon, icon, icon, null, getBackground(game), getBackground(game)));
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
    }

    public void setResearchTree(int index) {
        researchTree = researchTrees.get(index);
    }

    public static class ResearchTree {  

        private ResearchVertex root;
        private HashMap<String, ResearchVertex> researchNodes;
        private String namespace;

        public ResearchTree() {
            researchNodes = new HashMap<>();
        }

        public HashMap<String, ResearchVertex> getResearchNodes() {
            return researchNodes;
        }

        public static ResearchTree deriveResearchGraph(String inputString) throws InvalidObjectException {
            ResearchTree graph = new ResearchTree();

            JsonReader reader = new JsonReader();
            JsonValue mainJSONObject = reader.parse(inputString);

            graph.namespace = mainJSONObject.getString("namespace");
            graph.root = new ResearchVertex(graph, mainJSONObject.get("root"));
            graph.researchNodes.put(graph.root.getID(), graph.root);

            for(JsonValue value : mainJSONObject.get("researchNodes")) {
                ResearchVertex vertex = graph.createVertex(value);
                //System.out.println(graph.getUniversalID(vertex.parent) + "\t->\t" +graph.getUniversalID(vertex));
            }

            return graph;
        }

        private ResearchVertex createVertex(JsonValue value) {
            ResearchVertex vertex = new ResearchVertex(this, value);
            researchNodes.put(vertex.getID(), vertex);
            return vertex;
        }

        public ResearchVertex getVertex(String id) {
            if(id.equals("root"))
                return root;
            return researchNodes.get(id);
        }

        public void addChild(ResearchVertex parent, ResearchVertex child) {
            parent.children.add(child);
            child.parent = parent;
        }

        public String getUniversalID(ResearchVertex vertex) {
            if(vertex.equals(root)) {
                return namespace + ":" + root.getID();
            }
            return namespace + ":" + root.getID() + "." + vertex.getID();
        }

    }
    
    private static class ResearchVertex {
        
        ResearchVertex parent;
        ArrayList<ResearchVertex> children;
        ResearchTree tree;
        Requirement[] requirements;
        String name, description, icon, ID, parentID;
        Vector2 position;

        // JSON needs to contain: requirements for research, research name, research description, research icon, PARENTS of the graph, and the position of the vertex in the graph
        public ResearchVertex(ResearchTree tree, JsonValue json) {
            this.tree = tree;
            this.children = new ArrayList<>();
            // read from json
            this.name = json.getString("name");
            this.description = json.getString("description");
            this.icon = json.getString("icon");
            this.ID = json.getString("id");
            JsonValue position = json.get("position");
            this.position = new Vector2(position.getInt(0), position.getInt(1));
            if(tree.root == null) {
                tree.root = this;
                parent = null;
            } else {
                String parentID = json.getString("parent-id");
                parent = tree.getVertex(parentID);
                tree.addChild(parent, this);
                // read requirements
                JsonValue requirements = json.get("requirements");
                this.requirements = new Requirement[requirements.size];
                for(int i = 0; i < requirements.size; i++) {
                    this.requirements[i] = new Requirement(requirements.getString(i));
                }
                // read position
            }
        }



        static class Requirement {
            enum RequirementType {
                ITEM, RESEARCH, LEVEL;
                public static RequirementType get(String name) {
                    for(RequirementType type : values()) {
                        if(type.name().equals(name))
                            return type;
                    }
                    return null;
                }
            } 
            RequirementType category;
            Object type, value;
            // category |type     | value
            // ITEM     |WOOD_AXE | 1
            // RESEARCH |gaze:machinery.electricity 
            // LEVEL    |MINING   | 10

            public Requirement(String requirementString) {
                String[] split = requirementString.split("/");
                // print everything in split
                category = RequirementType.get(split[0]);
                type = split[1];
                if(split.length > 2) {
                    value = split[2];
                }
            }
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof ResearchVertex) {
                return ((ResearchVertex) obj).getID().equals(getID());
            }
            return false;
        }

        public String getID() {
            return ID;
        }

        @Override
        public int hashCode() {
            return getID().hashCode();
        }

    }
}
