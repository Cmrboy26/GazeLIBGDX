package net.cmr.gaze.world.entities;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.networking.PlayerConnection;
import net.cmr.gaze.networking.packets.AudioPacket;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileData;

public class DroppedItem extends Entity {

	Item item;
	long spawnTime = 0;
	float renderTime = 0;
	
	protected DroppedItem() {
		super(EntityType.DroppedItem);
	}
	
	public DroppedItem(Item item, int x, int y) {
		super(EntityType.DroppedItem, x*Tile.TILE_SIZE, y*Tile.TILE_SIZE);
		this.item = item;
	}

	@Override
	public void writeEntity(DataBuffer buffer, boolean obfuscatePosition, boolean toFile) throws IOException {
		super.writeEntity(buffer, obfuscatePosition, toFile);
		Item.writeOutgoingItem(item, buffer);
	}
	
	@Override
	protected Entity readEntityData(DataInputStream input, boolean fromFile) throws IOException {
		item = Item.readIncomingItem(input);
		return this;
	}
	
	@Override
	public void render(Gaze game, GameScreen screen) { 
		renderTime += Gdx.graphics.getDeltaTime();
		float startOffset = (float) Math.pow(2d, -Math.pow(6f*renderTime, 2d));
		//float startOffset = (float) Math.max(0, -Math.pow(renderTime*2, 2f)+1f);
		Item.draw(game, null, item, game.batch, (float) getX(), (float) (getY()+Math.sin((double) (System.nanoTime()+this.hashCode())/5e8))+(Tile.TILE_SIZE/2f)*startOffset, Tile.TILE_SIZE/1.5f, Tile.TILE_SIZE/1.5f, false);
	}

	@Override
	public void update(double deltaTime, TileData data) {
		super.update(deltaTime, data);
		if(!data.isServer()) {
			return;
		}
		if(spawnTime == 0) {
			spawnTime = System.currentTimeMillis();
		}
		if(System.currentTimeMillis()-spawnTime > 1000) {
			for(PlayerConnection connection : getWorld().getPlayers()) {
				Player player = connection.getPlayer();
				if(Vector2.dst((float) player.getX(), (float) player.getY(), (float) getX(), (float) getY()) <= player.getPickupRadius()) {
					Item temp = player.getInventory().add(item);
						connection.inventoryChanged();
						connection.getSender().addPacket(new AudioPacket("pickup", 1f));
					if(temp==null) {
						deleteEntity();
						return;
					} else if(temp.getSize() != item.getSize()) {
						item = temp;
					}
				}
				spawnTime+=250;
			}
		}
	}
	
}
