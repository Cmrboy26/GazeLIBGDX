package net.cmr.gaze.world;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.world.Weather.WeatherType;

/**
 * The EnvironmentController class is responsible for managing the environment of a world.
 * This includes the time, weather, ambient lighting, etc.
 */
public class EnvironmentController {

    public static double NO_WEATHER = Double.MAX_VALUE;

    double seed;
    double time;

    public enum EnvironmentControllerType {

        DEFAULT(0),
        UNDERGROUND(1);

        int id;

        EnvironmentControllerType(int id) {
            this.id = id;
        }

        public int getID() {
            return id;
        }

        public static EnvironmentControllerType getTypeFromID(int id) {
            for(EnvironmentControllerType type : EnvironmentControllerType.values()) {
                if(type.getID()==id) {
                    return type;
                }
            }
            return null;
        }
    }
    public static EnvironmentController getEnvironmentController(EnvironmentControllerType type, double seed) {
        switch(type) {
            case DEFAULT:
                return new EnvironmentController(type, seed);
            case UNDERGROUND:  
                return new EnvironmentController(type, seed) {
                    @Override
                    public double getRainThreshold() {
                        return NO_WEATHER;
                    }
                    @Override
                    public double getThunderThreshold() {
                        return NO_WEATHER;
                    }
                    @Override
                    public float getAmbientBrightness() {
                        return 0;
                    }
                };
            default:
                return null;
        }
    }
    
    EnvironmentControllerType type;

    public EnvironmentController(EnvironmentControllerType type, double seed, double time) {
        this(type, seed);
        this.time = time;
    }
    public EnvironmentController(EnvironmentControllerType type, double seed) {
        this.type = type;
        this.seed = seed;
    }

    public void update(double delta) {
        time += delta;
    }

    public float getAmbientBrightness() {
        return (float) calculateAmbience(.1f, .8f, .025f, 60f);
    }

    public boolean isNight() {
        return getAmbientBrightness() <= .2f;
    }

    /**
     * Calculates the ambient lighting based on the time of day.
     * @param min is the minimum value (typically .1)
     * @param max is the maximum value (typically .8)
     * @param rate is the rate at which the ambient lighting transitions between day and night (.025 is a somewhat good number?)
     * @param duration is the duration that either day or night lasts before transitioning (60 is a good number)
     * @return
     */
    private double calculateAmbience(double min, double max, double rate, double duration) {
    	double transitionTime = (max-min)/rate;
    	double x = getTime()%(2*duration+2*transitionTime);
    	
    	if(x < duration) {
    		return max;
    	}
    	if(x < duration+transitionTime) {
    		return (-rate*(x-duration)+max);
    	}
    	if(x < 2*duration+transitionTime) {
    		return min;
    	}
    	return (rate*(x-2*duration-transitionTime)+min);
    }

    public void write(DataBuffer buffer) throws IOException {
        buffer.writeInt(type.getID());
        buffer.writeDouble(seed);
        buffer.writeDouble(time);
    }

    public static EnvironmentController read(DataInputStream input) throws IOException {
        int id = input.readInt();
        double seed = input.readDouble();
        double time = input.readDouble();
        EnvironmentController controller = getEnvironmentController(EnvironmentControllerType.getTypeFromID(id), seed);
        controller.time = time;
        return controller;
    }

    public double getSeed() {
        return seed;
    }
    public double getTime() {
        return time;
    }

    public double getThunderThreshold() {
        return 0.925;
    }
    public double getRainThreshold() {
        return 0.7;
    }

    public Color getAmbientColor() {
        WeatherType type = Weather.getWeather(this);
        Color color = new Color(type.getAmbientColor());
        color.mul(getAmbientBrightness());
        color.a = 1f;
        return color;
    }

    public String toString() {
        return "EnvironmentController[seed="+seed+", time="+time+", type="+type+
        "\nrainThreshold="+getRainThreshold()+", thunderThreshold="+getThunderThreshold()+", currentAmbientBrightness="+getAmbientBrightness()+"]";
    }

}
