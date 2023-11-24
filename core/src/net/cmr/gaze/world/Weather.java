package net.cmr.gaze.world;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.util.SimplexNoise;

public class Weather {
    
    private static HashMap<Integer, WeatherType> weatherTypeMap = new HashMap<>();
    public static final Color CLEAR_COLOR = new Color(1f, 1f, 1f, 1f);
    
    public enum WeatherType {
        CLEAR(0, CLEAR_COLOR, Ambience.SILENT),
        RAIN(1, new Color(0.6784f, 0.6784f, 1f, 1f), Ambience.RAIN),
        THUNDER(2, new Color(0.3980f, 0.3980f, 0.9294f, 1f), Ambience.THUNDER);

        int id;
        Color ambientColor;
        Ambience ambience;

        WeatherType(int id, Color ambientColor, Ambience ambience) {
            this.id = id;
            this.ambientColor = ambientColor;
            this.ambience = ambience;
            weatherTypeMap.put(id, this);
        }

        public int getId() {
            return id;
        }
        public Color getAmbientColor() {
            return ambientColor;
        }
        public Ambience getAmbience() {
            return ambience;
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
