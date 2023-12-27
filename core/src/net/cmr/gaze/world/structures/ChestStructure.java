package net.cmr.gaze.world.structures;

import java.awt.Point;
import java.util.Random;
import java.util.stream.IntStream;

import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.Tiles;
import net.cmr.gaze.world.tile.ChestTile;

public class ChestStructure extends Structure {

	public ChestStructure(Point worldCoordinate) {
		super(worldCoordinate, 3, 4);
	}

	static Tile[][][] thing;
	
	@Override
	public Tile[][][] getTiles() {
		if(thing == null) {
			thing = new Tile[getWidth()][getHeight()][2];
			for(int x = 0; x < 3; x++) {
				for(int y = 0; y < 4; y++) {
					thing[x][y][0] = Tiles.getTile(TileType.STONE);
					thing[x][y][1] = Tiles.getTile(TileType.AIR);
				}
			}
			thing[1][2][1] = Tiles.getTile(TileType.CHEST); 
		}
		return thing;
	}
	
	@Override
	public void generateChestLoot(ChestTile tile, int x, int y) {
		Random random = new Random(x*37*37*37+y*23);
		/*for(int i = 0; i < tile.getInventory().getSize(); i++) {
			int input = (int) Math.floor(random.nextFloat()*ItemType.values().length);
			ItemType type = ItemType.values()[input];
			tile.getInventory().put(i,Items.getItem(type, 1));
		}*/
		for(int i = 0; i < tile.getInventory().getSize(); i++) {
			if(random.nextInt(3)==0) {
				switch(random.nextInt(4)) {
				case 3: {
					tile.getInventory().put(i, Items.getItem(ItemType.STONE, random.nextInt(5)+1));
					break;
				}
				case 2: {
					if(random.nextInt(2)==0) {
						tile.getInventory().put(i, Items.getItem(ItemType.COPPER_ORE, random.nextInt(2)+1));
					} else {
						tile.getInventory().put(i, Items.getItem(ItemType.IRON_ORE, random.nextInt(2)+1));
					}
					break;
				}
				case 1: {
					tile.getInventory().put(i, Items.getItem(ItemType.TORCH, random.nextInt(3)+1));
					break;
				}
				case 0: {
					if(random.nextInt(5)==4) {
						tile.getInventory().put(i, Items.getItem(ItemType.STONE_PICKAXE, 1));
					}
					break;
				}
				}
			}
		}
	}

}
