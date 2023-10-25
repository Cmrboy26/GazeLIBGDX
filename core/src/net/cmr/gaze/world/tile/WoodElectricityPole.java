package net.cmr.gaze.world.tile;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.ElectricityPole;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.TileUtils;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.entities.Player;

public class WoodElectricityPole extends ElectricityPole {

    public WoodElectricityPole() {
        super(TileType.WOOD_ELECTRICITY_POLE);
    }

    @Override
	public TileType[] belowWhitelist() {
		return null;
	}

	@Override
	public TileType[] belowBlacklist() {
		return getDefaultBlacklist();
	}
	
	@Override
	public void render(Gaze game, GameScreen screen, int x, int y) {
		game.batch.draw(game.getSprite("woodPowerPole"), x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE, TILE_SIZE*2);
		super.render(game, screen, x, y);
	}

    @Override
    public void onBreak(World world, Player player, int x, int y) {
        TileUtils.dropItem(world, x, y, Items.getItem(ItemType.WOOD_ELECTRICITY_POLE, 1));
    }

    @Override
    public String getBreakNoise() {
        return "woodHit";
    }
    @Override
    public String getHitNoise() {
        return "outro";
    }

    @Override
    public void writePole(TileType type, DataBuffer buffer) throws IOException {

    }

    @Override
    public ElectricityPole readPole(DataInputStream input, TileType type) throws IOException {
        return new WoodElectricityPole();
    }
    

}
