package net.cmr.gaze.world.abstractTiles;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.entities.Player;
import net.cmr.gaze.world.interfaceTiles.SpeedChangeTile;

public abstract class FloorTile extends TransitionTile implements SpeedChangeTile {
	
	Tile underTile;
	
	public FloorTile(TileType tileType) {
		super(tileType);
	}
	
	public void readFloorTileData(DataInputStream input) throws IOException {
		underTile = Tile.readIncomingTile(input);
	}

	@Override
	protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
		Tile.writeOutgoingTile(underTile, buffer);
	}
	
	@Override
	public void onBreak(World world, Player player, int x, int y) {
		world.addTile(underTile, x, y);
	}

	protected void renderBelowTile(Gaze game, GameScreen screen, int x, int y) {
		if(underTile!=null) {
			underTile.render(game, screen, x, y);
		}
	}
	
	@Override
	public String[] getTransitionSprite() {
		return null;
	}
	@Override
	public TileType[] getTransitionTiles() {
		return null;
	}

	public void setUnderTile(Tile at) {
		this.underTile = at;
	}

	public Tile getUnderTile() {
		return underTile;
	}
	
}
