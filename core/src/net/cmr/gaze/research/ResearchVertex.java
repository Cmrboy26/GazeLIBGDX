package net.cmr.gaze.research;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.JsonValue;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.InventorySlot.SimpleItemSlot;
import net.cmr.gaze.inventory.Items.ItemType;

public class ResearchVertex {
        
        public ResearchVertex parent;
        public ArrayList<ResearchVertex> children;
        public ResearchTree tree;
        public Requirement[] requirements = null;
        public String name, description, icon, ID, parentID;
        public Vector2 position;

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

        public RequirementWidget getRequirementWidget(Gaze game) {
            return new RequirementWidget(this, game);
        }

        public static class Requirement {
            enum RequirementType {
                ITEM, RESEARCH, SKILL;
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
            // SKILL    |MINING   | 10

            public Requirement(String requirementString) {
                String[] split = requirementString.split("/");
                // print everything in split
                category = RequirementType.get(split[0]);
                type = split[1];
                if(split.length > 2) {
                    value = split[2];
                }
            }

            public String toString() {
                return category + "/" + type + "/" + value;
            }
        }

        public static class RequirementWidget extends HorizontalGroup {

            public RequirementWidget(ResearchVertex vertex, Gaze game) {
                setHeight(18);
                setWidth(300);
                align(Align.left);
                if(vertex.requirements==null) {
                    return;
                }
                Sprite sprite = new Sprite(game.getSprite("translucent"));
                Image empty = new Image(sprite)  {
                    @Override
                    public float getPrefWidth() {
                        return 8;
                    }
                    @Override
                    public float getPrefHeight() {
                        return 8;
                    }
                };
                empty.setVisible(false);
                empty.setWidth(8);
                empty.setHeight(8);
                for (Requirement requirement : vertex.requirements) {
                    //System.out.println(requirement);
                    try {
                        switch (requirement.category) {
                            case ITEM:
                                ItemType type = ItemType.getItemTypeFromID(requirement.type.hashCode());
                                SimpleItemSlot slot = new SimpleItemSlot(game, type, Integer.valueOf(requirement.value.toString())) {
                                    @Override
                                    public float getPrefWidth() {
                                        return 18;
                                    }
                                    @Override
                                    public float getPrefHeight() {
                                        return 18;
                                    }
                                };
                                slot.setWidth(18);
                                slot.setHeight(18);
                                addActor(slot);
                                break;
                            case RESEARCH:
                                break;
                            case SKILL:
                                Image smallSpacing = new Image(game.getSprite("smallNumberSpacing")) {
                                    @Override
                                    public float getPrefWidth() {
                                        return 2;
                                    }
                                    @Override
                                    public float getPrefHeight() {
                                        return 8;
                                    }
                                };
                                smallSpacing.setBounds(0, 0, 2, 8);

                                Image levelImage = new Image(game.getSprite("LV")) {
                                    @Override
                                    public float getPrefWidth() {
                                        return 12;
                                    }
                                    @Override
                                    public float getPrefHeight() {
                                        return 8;
                                    }
                                };
                                addActor(levelImage);
                                addActor(smallSpacing);

                                TextureRegionDrawable skillSprite = new TextureRegionDrawable(game.getSprite(requirement.type.toString()));
                                Image skillImage = new Image(skillSprite) {
                                    @Override
                                    public float getPrefWidth() {
                                        return 10;
                                    }
                                    @Override
                                    public float getPrefHeight() {
                                        return 12.5f;
                                    }
                                };
                                int level = Integer.valueOf(requirement.value.toString());
                                int tens = ((int) (level%100)/10);
                                if(level >= 10) {
                                    Image tensImage = new Image(game.getSprite("smallNumber"+tens))  {
                                        @Override
                                        public float getPrefWidth() {
                                            return 6;
                                        }
                                        @Override
                                        public float getPrefHeight() {
                                            return 7.5f;
                                        }
                                    };
                                    tensImage.setWidth(8);
                                    tensImage.setHeight(7.5f);
                                    addActor(tensImage);
                                    smallSpacing = new Image(game.getSprite("smallNumberSpacing")) {
                                        @Override
                                        public float getPrefWidth() {
                                            return 2;
                                        }
                                        @Override
                                        public float getPrefHeight() {
                                            return 8;
                                        }
                                    };
                                    smallSpacing.setBounds(0, 0, 2, 8);
                                    addActor(smallSpacing);
                                }
                                int ones = ((int) (level)%10);
                                Image onesImage = new Image(game.getSprite("smallNumber"+ones))  {
                                    @Override
                                    public float getPrefWidth() {
                                        return 6;
                                    }
                                    @Override
                                    public float getPrefHeight() {
                                        return 7.5f;
                                    }
                                };
                                onesImage.setWidth(6);
                                onesImage.setHeight(7.5f);
                                addActor(onesImage);
                                smallSpacing = new Image(game.getSprite("smallNumberSpacing")) {
                                    @Override
                                    public float getPrefWidth() {
                                        return 2;
                                    }
                                    @Override
                                    public float getPrefHeight() {
                                        return 8;
                                    }
                                };
                                smallSpacing.setBounds(0, 0, 2, 8);
                                addActor(smallSpacing);

                                skillImage.setWidth(10);
                                skillImage.setHeight(12.5f);
                                addActor(skillImage);
                                break;
                            default:
                                break;
                        }
                    } catch(Exception e) {
                        System.err.println("Error loading requirement: "+requirement+" | "+e.getMessage());
                    }
                    addActor(empty);
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
