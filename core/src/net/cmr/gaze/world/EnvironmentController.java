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
        return 0;
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

}
