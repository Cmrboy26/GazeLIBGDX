package net.cmr.gaze.stage.widgets;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
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

        WidgetGroup group = new WidgetGroup();
        group.setBounds(0, 0, 168*2, 81*2*5);
        Image icon = new Image(new TextureRegionDrawable(game.getSprite(GameMenuIcon.CRAFTING_ICON)));
        icon.setBounds(168-16, 10, 16*2, 16*2);
        group.addActor(icon);
        Image icon2 = new Image(new TextureRegionDrawable(game.getSprite(GameMenuIcon.INVENTORY_ICON)));
        icon2.setBounds(168-16, 10+50, 16*2, 16*2);
        group.addActor(icon2);
        Image icon3 = new Image(new TextureRegionDrawable(game.getSprite(GameMenuIcon.QUESTS_ICON)));
        icon3.setBounds(168-16, 10+50+130, 16*2, 16*2);
        group.addActor(icon3);

        Table table = new Table();
        table.add(group).width(168*2).height(81*4);

        ScrollPaneStyle style = new ScrollPaneStyle();
        researchPanel = new ScrollPane(table, style);
        researchPanel.setBounds(57*2+24*2, 36*2+21*2, 168*2, 81*2);
        researchPanel.layout();
        researchPanel.setScrollPercentY(1f);
        setResearchTree("{\r\n" + //
						"\t\"comment\":\"the id will be appened to the namespace and all research nodes will append 'parent-id' to that\",\r\n" + //
						"\t\"namespace\":\"gaze\",\r\n" + //
						"\t\"root\":{\r\n" + //
						"\t\t\"name\":\"Machinery\",\r\n" + //
						"\t\t\"description\":\"The basis of all industry\",\r\n" + //
						"\t\t\"icon\":\"machinery\",\r\n" + //
						"\t\t\"id\":\"machinery\"\r\n" + //
						"\t},\r\n" + //
						"\t\"comment2\":\"the parent research is ALWAYS a requirement\",\r\n" + //
						"\t\"researchNodes\":[\r\n" + //
						"\t\t{\t\r\n" + //
						"\t\t\t\"name\":\"Gears\",\r\n" + //
						"\t\t\t\"description\":\"Gears and gadgets\",\r\n" + //
						"\t\t\t\"icon\":\"gearSymbol\",\r\n" + //
						"\t\t\t\"id\":\"gears\",\r\n" + //
						"\t\t\t\"parent-id\":\"root\",\r\n" + //
						"\t\t\t\"requirements\": [\r\n" + //
						"\t\t\t\t\"ITEM/IRON_INGOT/5\"\r\n" + //
						"\t\t\t],\r\n" + //
						"\t\t\t\"rewards\": []\r\n" + //
						"\t\t},\r\n" + //
						"\t\t{\r\n" + //
						"\t\t\t\"name\":\"Electricity\",\r\n" + //
						"\t\t\t\"description\":\"POWER\",\r\n" + //
						"\t\t\t\"icon\":\"electricSymbol\",\r\n" + //
						"\t\t\t\"id\":\"electricity\",\r\n" + //
						"\t\t\t\"parent-id\":\"gears\",\r\n" + //
						"\t\t\t\"requirements\":[\r\n" + //
						"\t\t\t\t\"ITEM/WOOD_PICKAXE/1\",\r\n" + //
						"\t\t\t\t\"RESEARCH/gaze:machinery.electricity\"\r\n" + //
						"\t\t\t],\r\n" + //
						"\t\t\t\"rewards\":[\r\n" + //
						"\t\t\t\t\"XP/MINING/10\",\r\n" + //
						"\t\t\t\t\"ITEM/WOOD_PICKAXE/1\"\r\n" + //
						"\t\t\t]\r\n" + //
						"\t\t},\r\n" + //
						"\t\t{\r\n" + //
						"\t\t\t\"name\":\"Power Transportation\",\r\n" + //
						"\t\t\t\"description\":\"Move around electricity\",\r\n" + //
						"\t\t\t\"icon\":\"powerPole\",\r\n" + //
						"\t\t\t\"id\":\"powerTransport\",\r\n" + //
						"\t\t\t\"parent-id\":\"electricity\",\r\n" + //
						"\t\t\t\"requirements\":[\r\n" + //
						"\t\t\t\t\"ITEM/COPPER_WIRE/10\"\r\n" + //
						"\t\t\t],\r\n" + //
						"\t\t\t\"rewards\":[\r\n" + //
						"\t\t\t\t\"XP/MINING/15\",\r\n" + //
						"\t\t\t\t\"ITEM/POWER_POLE/10\"\r\n" + //
						"\t\t\t]\r\n" + //
						"\t\t}\r\n" + //
						"\t]\r\n" + //
						"}");
        addActor(researchPanel);
    } 

    @Override
    public void act(float delta) {
        super.act(delta);
        if(!isVisible()) {
            getStage().setScrollFocus(null);
        }
    }

    public void setResearchTree(String string) {
        try {
            researchTree = ResearchTree.deriveResearchGraph(string);
		} catch (InvalidObjectException e) {
			e.printStackTrace();
		}
    }
    
    /*
        
        {
            "comment":"the id will be appened to the namespace and all research nodes will append 'parent-id' to that",
            "namespace":"gaze",
            "root":{
                "name":"Machinery",
                "description":"The basis of all industry",
                "id":"machinery"
            },
            "comment2":"the parent research is ALWAYS a requirement",
            "researchNodes":[
                {
                    "name":"Electricity",
                    "description":"POWER",
                    "icon":"electricSymbol",
                    "id":"electricity",
                    "parent-id":"root",
                    "requirements":[
                        "ITEM/WOOD_PICKAXE/1",
                        "RESEARCH/gaze:machinery.electricity"
                    ],
                    "rewards":[
                        "XP/MINING/10",
                        "ITEM/WOOD_PICKAXE/1"
                    ]
                },
                {
                    "name":"Power Transportation",
                    "description":"Move around electricity",
                    "icon":"powerPole",
                    "id":"powerTransport",
                    "parent-id":"electricity",
                    "requirements":[
                        "ITEM/COPPER_WIRE/10"
                    ],
                    "rewards":[
                        "XP/MINING/15",
                        "ITEM/POWER_POLE/10"
                    ]
                }
            ]
        }

         */

    public static class ResearchTree {  

        private ResearchVertex root;
        private HashMap<String, ResearchVertex> researchNodes;
        private String namespace;

        public ResearchTree() {
            researchNodes = new HashMap<>();
        }

        public static ResearchTree deriveResearchGraph(String inputString) throws InvalidObjectException {
            ResearchTree graph = new ResearchTree();

            JsonReader reader = new JsonReader();
            JsonValue mainJSONObject = reader.parse(inputString);

            graph.namespace = mainJSONObject.getString("namespace");
            graph.root = new ResearchVertex(graph, mainJSONObject.get("root"));

            for(JsonValue value : mainJSONObject.get("researchNodes")) {
                ResearchVertex vertex = graph.createVertex(value);
                System.out.println(graph.getUniversalID(vertex.parent) + "\t->\t" +graph.getUniversalID(vertex));
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

        /*
        
            {
                "name": "Electricity",
                "description": "POWER",
                "icon": "electricSymbol",
                "id": "electricity",
                "parent-id": "root",
                "requirements": [
                    "ITEM/WOOD_PICKAXE/1",
                    "RESEARCH/gaze:machinery.electricity"
                ],
                "rewards": [
                    "XP/MINING/10",
                    "ITEM/WOOD_PICKAXE/1"
                ]
            }

         */

        // JSON needs to contain: requirements for research, research name, research description, research icon, PARENTS of the graph, and the position of the vertex in the graph
        public ResearchVertex(ResearchTree tree, JsonValue json) {
            this.tree = tree;
            this.children = new ArrayList<>();
            // read from json
            this.name = json.getString("name");
            this.description = json.getString("description");
            this.icon = json.getString("icon");
            this.ID = json.getString("id");
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
