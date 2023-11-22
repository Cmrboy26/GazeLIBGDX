package net.cmr.gaze.world;

import com.badlogic.gdx.graphics.g3d.environment.AmbientCubemap;

public enum Ambience {
    
    SILENT,
    
    FOREST,
    WATER,
    FACTORY,
    
    RAIN,
    THUNDER;

    Ambience() {

    }

    public String getAmbientSound(EnvironmentController environmentController) {
        String[] sounds = getAmbientSounds(environmentController);
        if(sounds == null || sounds.length == 0) {
            return null;
        }
        return sounds[(int) (Math.random() * sounds.length)];
    }
    public static String getAmbientSound(Ambience ambience, EnvironmentController controller) {
        return ambience.getAmbientSound(controller);
    }

    private String[] getAmbientSounds(EnvironmentController environmentController) {
        switch (this) {
            case SILENT:
                return new String[] {};
            case FOREST:
                if(environmentController.isNight()) {
                    return new String[] {"crickets0", "crickets1"};
                }
                return new String[] {"forestAmbience0","forestAmbience1","forestAmbience2","forestAmbience3","forestAmbience4"};
            case WATER:
                return new String[] {"water0","water1"};
            case FACTORY:
                return new String[] {"machineworking"};
            case RAIN:
                return new String[] {"outro"};
            case THUNDER:
                return new String[] {"intro"};
            default:
                return new String[] {};
        }
    }
    
    public static Ambience getWeatherAmbience(EnvironmentController controller) {
        Weather.WeatherType weather = Weather.getWeather(controller);
        return weather.getAmbience();
    }

}
