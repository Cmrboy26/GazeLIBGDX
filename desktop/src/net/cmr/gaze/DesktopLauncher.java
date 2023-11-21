package net.cmr.gaze;

import java.io.InvalidObjectException;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {

	public static void main (String[] arg) throws InvalidObjectException {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		//config.setForegroundFPS(60);
		config.useVsync(false);
		config.setTitle("Gaze");
		config.setWindowedMode(640, 360);
		config.setWindowIcon(FileType.Internal, "sprites/logo.png");
		new Lwjgl3Application(new Gaze() {}, config);

		/*int size = 52;
		WoodElectricityPole[] array = new WoodElectricityPole[size];
		for(int i = 0; i < size; i++) {
			array[i] = new WoodElectricityPole();
			array[i].worldCoordinates = new Point(i, i);
		}
		for(int i = 1; i < size; i++) {
			EnergyDistributor.connectNodes(array[i-1], array[i]);
		}
		
		array[42].removeFromGrid();
		array[49].removeFromGrid();
		array[2].removeFromGrid();
		array[4].removeFromGrid();
		array[7].removeFromGrid();

		for(int i = 0; i < size; i++) {
			if(array[i].getPowerGrid()==null) {
				System.out.println(i+" : null");
				continue;
			} else {
				System.out.println(i+" : "+array[i].getPowerGrid().hashCode());
			}
		}*/


		/*WoodElectricityPole pole1 = new WoodElectricityPole();
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
		System.out.println(pole6.getPowerGrid());*/



	}
}
