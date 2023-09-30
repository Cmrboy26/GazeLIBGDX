package net.cmr.gaze.world.tile;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.inventory.Tool.Material;
import net.cmr.gaze.inventory.Tool.ToolType;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.BaseTile;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileData;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.TileUtils;
import net.cmr.gaze.world.Tiles;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.entities.Particle.ParticleEffectType;
import net.cmr.gaze.world.entities.Player;

public class TreeSapling extends BaseTile {

    int stage;
	float elapsedTime = 0;

    public TreeSapling() {
        super(TileType.TREE_SAPLING, 2, 1);
    }

	@Override
	public float getRenderYOffset() {
		return .1f;
	}

	@Override
	public void generateInitialize(int x, int y, double seed) {
		stage = getRandomizedInt(2, x, y);
        //stage = (int) Math.floor(Math.random()*3);
	}

	@Override
	public void update(TileData data, Point worldCoordinates) {
		super.update(data, worldCoordinates);
		if(data.isServer()) {
			if(stage<=2) {
				elapsedTime+=DELTA_TIME;
				if(elapsedTime > 80 && new Random().nextInt(300)==0) {
					elapsedTime=0;
					stage++;
					if(stage <= 2) {
						data.getServerData().onTileChange(worldCoordinates.x, worldCoordinates.y, 1);
					}
				}
			}
			if(stage == 3) {
				TileUtils.spawnParticleOffset(data.getServerData(), ParticleEffectType.LEAVES, this, worldCoordinates.x+.3f, worldCoordinates.y, .9f, 25);
				data.getServerData().setTile(Tiles.getTile(TileType.TREE), worldCoordinates.x, worldCoordinates.y);
				data.getServerData().onTileChange(worldCoordinates.x, worldCoordinates.y, 1);
			}
		}
	}
    @Override
    public void render(Gaze game, HashMap<Point, Tile[][][]> chunks, int x, int y) {
        draw(game.batch, game.getSprite("treeStage"+stage), x, y, 2, 1);
        super.render(game, chunks, x, y);
    }

	@Override
	public void onBreak(World world, Player player, int x, int y) {
		TileUtils.dropItem(world, x, y, Items.getItem(ItemType.ACORN, 1));
	}

    @Override
	public TileType[] belowWhitelist() {
		return new TileType[] {TileType.GRASS};
	}

	@Override
	public TileType[] belowBlacklist() {
		return null;
	}
	
	@Override
	public Material getMaterial() {
		return Material.WOOD;
	}
	public ToolType getToolType() {
		return ToolType.AXE;
	}
	
	@Override
	public String getHitNoise() {
		return "woodHit";
	}
	@Override
	public String getBreakNoise() {
		return "woodHit";
	}

    @Override
    public Tile readTile(DataInputStream input, TileType type) throws IOException {
        TreeSapling sapling = new TreeSapling();
        Tile.readBreakData(input, sapling);
        sapling.stage = input.readInt();
		sapling.elapsedTime = input.readFloat();
        return sapling;
    }

    @Override
    protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
        buffer.writeInt(stage);
		buffer.writeFloat(elapsedTime);
    }

    @Override
	public String getAmbientNoise(GameScreen game) {
		return "forestAmbience"+new Random().nextInt(5);
	}
	@Override
	public float getAmbientNoiseVolume() {
		return .25f;
	}
	
	@Override
	public float getAmbientNoisePitch() {
		return 1f+new Random().nextFloat()/5f-1/10f;
	}
    
}
