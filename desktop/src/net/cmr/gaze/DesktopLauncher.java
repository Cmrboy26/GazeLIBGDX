package net.cmr.gaze;

import java.awt.Point;
import java.io.InvalidObjectException;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import net.cmr.gaze.world.powerGrid.EnergyDistributor;
import net.cmr.gaze.world.powerGrid.PowerGrid;
import net.cmr.gaze.world.tile.WoodElectricityPole;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) throws InvalidObjectException {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		//config.setForegroundFPS(60);
		config.useVsync(false);
		config.setTitle("Gaze");
		config.setWindowedMode(640, 360);
		//new Lwjgl3Application(new Gaze() {}, config);

		WoodElectricityPole pole1 = new WoodElectricityPole();
		WoodElectricityPole pole2 = new WoodElectricityPole();
		WoodElectricityPole pole3 = new WoodElectricityPole();
		WoodElectricityPole pole4 = new WoodElectricityPole();
		WoodElectricityPole pole5 = new WoodElectricityPole();
		WoodElectricityPole pole6 = new WoodElectricityPole();
		pole1.worldCoordinates = new Point(0, 0);
		pole2.worldCoordinates = new Point(1, 0);
		pole3.worldCoordinates = new Point(2, 0);
		pole4.worldCoordinates = new Point(3, 0);
		pole5.worldCoordinates = new Point(2, 1);
		pole6.worldCoordinates = new Point(-1, 1);
		
		EnergyDistributor.connectNodes(pole1, pole2);
		EnergyDistributor.connectNodes(pole2, pole3);
		EnergyDistributor.connectNodes(pole3, pole4);
		EnergyDistributor.connectNodes(pole4, pole5);
		EnergyDistributor.connectNodes(pole5, pole6);
		EnergyDistributor.connectNodes(pole1, pole6);

		//PowerGrid.adaptiveSetGrid(pole1);
		pole3.getPowerGrid().remove(pole3, true);
		pole6.getPowerGrid().remove(pole6, true);

		System.out.println(pole1.getPowerGrid());
		System.out.println(pole2.getPowerGrid());
		System.out.println(pole3.getPowerGrid());
		System.out.println(pole4.getPowerGrid());
		System.out.println(pole5.getPowerGrid());
		System.out.println(pole6.getPowerGrid());



	}
}
