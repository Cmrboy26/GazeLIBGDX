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
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

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
    
    /*
        
        {
            "comment": "the id will be appened to the namespace and all research nodes will append 'parent-id' to that",
            "namespace": "gaze",
            "root": {
                "name": "Machinery",
                "description": "The basis of all industry",
                "id": "machinery"
            },
            "comment2": "the parent research is ALWAYS a requirement",
            "researchNodes": [{
                "name": "Electricity",
                "description": "POWER",
                "icon": "electricSymbol",
                "id": "electricity",
                "parent-id": "root",
                "requirements": [
                    "ITEM|WOOD_PICKAXE:1",
                    "RESEARCH|gaze:machinery.electricity"
                ],
                "rewards": [
                    "XP|MINING|10",
                    "ITEM|WOOD_PICKAXE:1"
                ]
            }, {
                "name": "Power Transportation",
                "description": "Move around electricity",
                "icon": "powerPole",
                "id": "powerTransport",
                "parent-id": "electricity",
                "requirements": [
                    "ITEM|COPPER_WIRE:10"
                ],
                "rewards": [
                    "XP|MINING:15",
                    "ITEM|POWER_POLE:10"
                ]
            }]
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
                graph.createVertex(value);
            }

            return graph;
        }

        private ResearchVertex createVertex(JsonValue value) {
            ResearchVertex vertex = new ResearchVertex(this, value);
            researchNodes.put(vertex.getID(), vertex);
            return vertex;
        }

        public ResearchVertex getVertex(String id) {
            return researchNodes.get(id);
        }

        public void addChild(ResearchVertex parent, ResearchVertex child) {
            parent.children.add(child);
            child.parent = parent;
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
                    "ITEM|WOOD_PICKAXE:1",
                    "RESEARCH|gaze:machinery.electricity"
                ],
                "rewards": [
                    "XP|MINING|10",
                    "ITEM|WOOD_PICKAXE:1"
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
            String parentID = json.getString("parent-id");
            if(tree.root == null) {
                tree.root = this;
                parent = null;
            } else {
                parent = tree.getVertex(parentID);
                tree.addChild(parent, this);
            }
            // read requirements
            JsonValue requirements = json.get("requirements");
            this.requirements = new Requirement[requirements.size];
            for(int i = 0; i < requirements.size; i++) {
                this.requirements[i] = new Requirement(requirements.getString(i));
            }
        }

        static class Requirement {
            enum RequirementType {
                ITEM, RESEARCH, LEVEL;
            } 
            RequirementType category;
            // category |type     | value
            // ITEM     |WOOD_AXE | 1
            // RESEARCH |gaze:machinery.electricity | 1
            // LEVEL    |MINING   | 10
            Object type, value;

            public Requirement(String requirementString) {
                String[] split = requirementString.split("|");
                category = RequirementType.valueOf(split[0]);
                type = split[1];
                value = split[2];
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
