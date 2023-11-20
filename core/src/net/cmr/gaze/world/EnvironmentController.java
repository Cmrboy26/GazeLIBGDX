package net.cmr.gaze.world;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

/**
 * The EnvironmentController class is responsible for managing the environment of a world.
 * This includes the time, weather, ambient lighting, etc.
 */
public class EnvironmentController {

    double seed;
    double time;

    public EnvironmentController(double seed, double time) {
        this(seed);
        this.time = time;
    }
    public EnvironmentController(double seed) {
        this.seed = seed;
    }

    public void update(double delta) {
        time += delta;
    }

    public float getAmbientBrightness() {
        return (float) calculateAmbience(.1f, .8f, .025f, 60f);
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
        buffer.writeDouble(seed);
        buffer.writeDouble(time);
    }

    public static EnvironmentController read(DataInputStream input) throws IOException {
        double seed = input.readDouble();
        double time = input.readDouble();
        return new EnvironmentController(seed, time);
    }

    public double getSeed() {
        return seed;
    }
    public double getTime() {
        return time;
    }

    public double getThunderThreshold() {
        return 0.975;
    }
    public double getRainThreshold() {
        return 0.7;
    }

}
