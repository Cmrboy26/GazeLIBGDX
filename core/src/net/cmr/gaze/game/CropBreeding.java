package net.cmr.gaze.game;

import java.util.Objects;

import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.SeedItem;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.inventory.custom.CottonSeeds;
import net.cmr.gaze.inventory.custom.WheatSeeds;

public class CropBreeding {

    public static final float RARE_CHANCE = 0.05f;
    public static final float UNCOMMON_CHANCE = 0.15f;

    public enum CropType {
        WHEAT(ItemType.WHEAT_SEEDS),
        COTTON(ItemType.COTTON_SEEDS),
        BARLEY(ItemType.ANVIL),
        RICE(ItemType.BREAD),
        POTATO(ItemType.AGRICULTURE_TABLE),
        TOMATO(ItemType.BASIC_CIRCUIT),
        CORN(ItemType.BLAST_FURNACE),
        SUGAR(ItemType.IRON_PICKAXE),
        CARROT(ItemType.WOOD_HOE);

        ItemType type;

        private CropType(ItemType type) {
            this.type = type;
        }

        public ItemType getItemType() {
            return type;
        }
    }

    public Item breedSeeds(SeedItem seed1, SeedItem seed2) {
        Objects.requireNonNull(seed1);
        Objects.requireNonNull(seed2);
        CropType type1 = seed1.getCropType();
        CropType type2 = seed2.getCropType();
        if(seed1.getSize() < 1 || seed2.getSize() < 1) {
            return null;
        } else {
            seed1.subtract(1);
            seed2.subtract(1);
        }
        return Items.getItem(getBreedResult(type1, type2).getItemType(), 1);
    }   

    /*
    Wheat + Wheat = Wheat, (Rare) Barley
    Barley + Barley = Rice
    Barley + Wheat = Barley, Wheat, (Uncommon) Potato
    Potato + Potato = Tomato (because why not)
    Tomato + Tomato = Potato
    Cotton + Cotton = Cotton, (Rare) Corn
    Corn + Tomato OR Potato = Carrot
    Rice + Wheat = Sugar
     */

    public CropType getBreedResult(CropType type1, CropType type2) {
        if(type1==CropType.WHEAT && type2==CropType.WHEAT) {
            if(rareChance()) {
                return CropType.BARLEY;
            } else {
                return CropType.WHEAT;
            }
        }
        if(type1==CropType.BARLEY && type2==CropType.BARLEY) {
            return CropType.RICE;
        }
        if(eitherEqual(type1, type2, CropType.BARLEY) && eitherEqual(type1, type2, CropType.WHEAT)) {
            if(uncommonChance()) {
                return CropType.POTATO;
            } else {
                return random(type1, type2);
            }
        }
        if(type1==CropType.POTATO && type2==CropType.POTATO) {
            return CropType.TOMATO;
        }
        if(type1==CropType.TOMATO && type2==CropType.TOMATO) {
            return CropType.POTATO;
        }
        if(type1==CropType.COTTON && type2==CropType.COTTON) {
            if(rareChance()) {
                return CropType.CORN;
            } else {
                return CropType.COTTON;
            }
        }
        if(eitherEqual(type1, type2, CropType.CORN) && (eitherEqual(type1, type2, CropType.TOMATO) || eitherEqual(type1, type2, CropType.POTATO))) {
            return CropType.CARROT;
        }
        if(eitherEqual(type1, type2, CropType.RICE) && eitherEqual(type1, type2, CropType.WHEAT)) {
            return CropType.SUGAR;
        }
        return type1;
    }

    private boolean rareChance() {
        return Math.random() < RARE_CHANCE;
    }
    private boolean uncommonChance() {
        return Math.random() < UNCOMMON_CHANCE;
    }

    private boolean eitherEqual(CropType type1, CropType type2, CropType compare) {
        return type1 == compare || type2 == compare;
    }
    private CropType random(CropType... types) {
        return types[(int)(Math.random()*types.length)];
    }

}
