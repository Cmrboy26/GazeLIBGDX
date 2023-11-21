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
        CLEAR(0, new Color(1f, 1f, 1f, 1f), Ambiance.SILENT),
        RAIN(1, new Color(0.6784f, 0.6784f, 1f, 1f), Ambiance.RAIN),
        THUNDER(2, new Color(0.4980f, 0.4980f, 0.7294f, 1f), Ambiance.THUNDER);

        int id;
        Color ambientColor;
        Ambiance ambiance;

        WeatherType(int id, Color ambientColor, Ambiance ambiance) {
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
        public Ambiance getAmbiance() {
            return ambiance;
        }

        public void write(DataBuffer buffer) throws IOException {
            buffer.writeInt(id);
        }
        public static WeatherType read(DataInputStream input) throws IOException {
            return weatherTypeMap.get(input.readInt());
        }
    }

    public static WeatherType getWeather(EnvironmentController env) {

        double time = env.getTime()/60;

        time = Math.floor(time);
        time*=60;

		double noise = SimplexNoise.noise(1, .0003, 30, .3, 1, time, env.getSeed());

		noise++;
		noise/=2d;

		WeatherType type;
		if(noise > env.getThunderThreshold()) {
			type = WeatherType.THUNDER;
		} else if(noise > env.getRainThreshold()) {
			type = WeatherType.RAIN;
		} else {
			type = WeatherType.CLEAR;
		}
        return type;
    }

}
