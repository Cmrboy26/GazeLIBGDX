package net.cmr.gaze;

import java.io.InvalidObjectException;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import net.cmr.gaze.game.CropBreeding;
import net.cmr.gaze.inventory.Inventory;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {

	public static void main (String[] arg) throws InvalidObjectException {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		//config.useVsync(false);
		//config.setTitle("Gaze");
		//config.setWindowedMode(640, 360);
		//config.setWindowIcon(FileType.Internal, "sprites/logo.png");
		//new Lwjgl3Application(new Gaze() {}, config);

		Items.initialize();
		Inventory inventory = new Inventory(6);
		for(int i = 0; i < 100; i++) {
			Item result = CropBreeding.breedSeeds(Items.getItem(ItemType.WHEAT_SEEDS, 1), Items.getItem(ItemType.WHEAT_SEEDS, 1));
			inventory.add(result);
		}
		System.out.println(inventory);
	}
}
