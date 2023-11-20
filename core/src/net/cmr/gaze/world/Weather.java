package net.cmr.gaze.world;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.util.SimplexNoise;

public class Weather {
    
    private static HashMap<Integer, WeatherType> weatherTypeMap = new HashMap<>();
    
    public enum WeatherType {
        CLEAR(0, new Color(1f, 1f, 1f, 1f)),
        RAIN(1, new Color(0.6784f, 0.6784f, 1f, 1f)),
        THUNDER(2, new Color(0.4980f, 0.4980f, 0.7294f, 1f));

        int id;
        Color ambientColor;

        WeatherType(int id, Color ambientColor) {
            this.id = id;
            this.ambientColor = ambientColor;
            weatherTypeMap.put(id, this);
        }

        public int getId() {
            return id;
        }
        public Color getAmbientColor() {
            return ambientColor;
        }

        public void write(DataBuffer buffer) throws IOException {
            buffer.writeInt(id);
        }
        public static WeatherType read(DataInputStream input) throws IOException {
            return weatherTypeMap.get(input.readInt());
        }
    }

    public static WeatherType getWeather(EnvironmentController environmentController) {
        // Use a perlin noise function to determine the weather based on the time and seed.
        double noise = SimplexNoise.noise(environmentController.getTime(), environmentController.getSeed());
        return null;
    }

}
