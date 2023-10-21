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
import net.cmr.gaze.leveling.Skills.Skill;

public class ResearchVertex {
        
        public ResearchVertex parent;
        public ArrayList<ResearchVertex> children;
        public ResearchTree tree;
        public ResearchRequirement[] requirements = null;
        public ResearchReward[] rewards = null;
        public String name, description, icon, ID, parentID;
        public Vector2 position;

        // JSON needs to contain: requirements for research, research name, research description, research icon, PARENTS of the graph, and the position of the vertex in the graph
        public ResearchVertex(ResearchTree tree, JsonValue json) {
            this.tree = tree;
            this.children = new ArrayList<>();
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
                // PARENTS (mandatory)
                String parentID = json.getString("parent-id");
                parent = tree.getVertex(parentID);
                tree.addChild(parent, this);
                // REQUIREMENTS (mandatory)
                JsonValue requirements = json.get("requirements");
                this.requirements = new ResearchRequirement[requirements.size];
                for(int i = 0; i < requirements.size; i++) {
                    this.requirements[i] = new ResearchRequirement(requirements.getString(i));
                }
                // REWARDS (optional)
                JsonValue rewards = json.get("rewards");
                if(rewards != null) {
                    this.rewards = new ResearchReward[rewards.size];
                    for(int i = 0; i < rewards.size; i++) {
                        this.rewards[i] = new ResearchReward(rewards.getString(i));
                    }
                }
            }
        }

        public static class ResearchReward {
            public enum ResearchRewardType {
                ITEM, XP;
                public static ResearchRewardType get(String name) {
                    for(ResearchRewardType type : values()) {
                        if(type.name().equals(name))
                            return type;
                    }
                    return null;
                }
            }

            public ResearchRewardType category;
            public Object type, value;
            // category |type     | value
            // ITEM     |WOOD_AXE | 1
            // XP       |MINING   | 10
            public ResearchReward(String rewardString) {
                String[] split = rewardString.split("/");
                category = ResearchRewardType.get(split[0]);
                type = split[1];
                if(split.length > 2) {
                    value = split[2];
                }
            }
            public ItemType getItemType() {
                if(category == ResearchRewardType.ITEM) {
                    return ItemType.getItemTypeFromID(type.hashCode());
                }
                throw new RuntimeException("Reward is not an item reward!");
            }
            public int getItemQuantity() {
                if(category == ResearchRewardType.ITEM) {
                    return Integer.valueOf(value.toString());
                }
                throw new RuntimeException("Reward is not an item reward!");
            }
            public int getSkillXP() {
                if(category == ResearchRewardType.XP) {
                    return Integer.valueOf(value.toString());
                }
                throw new RuntimeException("Reward is not an XP reward!");
            }
            public Skill getSkill() {
                if(category == ResearchRewardType.XP) {
                    for(Skill skill : Skill.values()) {
                        if(skill.name().equals(type.toString())) {
                            return skill;
                        }
                    }
                }
                throw new RuntimeException("Reward is not an XP reward!");
            }
            public String toString() {
                return category + "/" + type + "/" + value;
            }
        }

        public RequirementWidget getRequirementWidget(Gaze game) {
            return new RequirementWidget(this, game);
        }

        public static class ResearchRequirement {
            public enum ResearchRequirementType {
                ITEM, RESEARCH, SKILL;
                public static ResearchRequirementType get(String name) {
                    for(ResearchRequirementType type : values()) {
                        if(type.name().equals(name))
                            return type;
                    }
                    return null;
                }
            } 
            public ResearchRequirementType category;
            public Object type, value;
            // category |type     | value
            // ITEM     |WOOD_AXE | 1
            // RESEARCH |gaze:machinery.electricity 
            // SKILL    |MINING   | 10

            public ResearchRequirement(String requirementString) {
                String[] split = requirementString.split("/");
                // print everything in split
                category = ResearchRequirementType.get(split[0]);
                type = split[1];
                if(split.length > 2) {
                    value = split[2];
                }
            }

            public ItemType getItemType() {
                if(category == ResearchRequirementType.ITEM) {
                    return ItemType.getItemTypeFromID(type.hashCode());
                }
                throw new RuntimeException("Requirement is not an item requirement!");
            }
            public int getSkillLevel() {
                if(category == ResearchRequirementType.SKILL) {
                    return Integer.valueOf(value.toString());
                }
                throw new RuntimeException("Requirement is not a skill requirement!");
            }
            public int getItemQuantity() {
                if(category == ResearchRequirementType.ITEM) {
                    return Integer.valueOf(value.toString());
                }
                throw new RuntimeException("Requirement is not an item requirement!");
            }
            public Skill getSkill() {
                if(category == ResearchRequirementType.SKILL) {
                    for(Skill skill : Skill.values()) {
                        if(skill.name().equals(type.toString())) {
                            return skill;
                        }
                    }
                }
                throw new RuntimeException("Requirement is not a skill requirement!");
            }
            public String getResearchID() {
                if(category == ResearchRequirementType.RESEARCH) {
                    return type.toString();
                }
                throw new RuntimeException("Requirement is not a research requirement!");
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
                for (ResearchRequirement requirement : vertex.requirements) {
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
