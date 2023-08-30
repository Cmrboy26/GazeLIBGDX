package net.cmr.gaze.world;

import java.util.Random;

import net.cmr.gaze.world.tile.FarmlandTile;

public abstract class CropTile extends Tile {

	protected int stage = 1;
	protected int progress = 0;
	
	public CropTile(TileType tileType) {
		super(tileType);
	}
	
	@Override
	public TileType[] belowBlacklist() {
		return null;
	}
	
	@Override
	public TileType[] belowWhitelist() {
		return new TileType[] {TileType.FARMLAND};
	}
	
	@Override
	public boolean isInstantBreak() {
		return true;
	}
	
	public abstract int getGrowthStages();
	// in seconds
	public abstract int getAverageTimePerStage();

	private static Random cropRandomizer;
	
	public void updateCrop(float moisture, TileData tileData, int x, int y) {
		if(stage >= getGrowthStages()) {
			return;
		}
		if(moisture > 0) {
			if(cropRandomizer == null) {
				cropRandomizer = new Random();
			}
			int chance = cropRandomizer.nextInt((int) Math.max(1,getAverageTimePerStage()/FarmlandTile.CHECK_DURATION/3));
			if(chance==0) {
				progress++;
			}
			if(progress>=3) {
				stage++;
				progress = 0;
				if(tileData.isServer()) {
					World world = tileData.getServerData();
					world.onTileChange(x, y, 1);
				}
			}
		}
	}
	
}
