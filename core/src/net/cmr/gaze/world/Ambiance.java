package net.cmr.gaze.world;

import com.badlogic.gdx.graphics.g3d.environment.AmbientCubemap;

public enum Ambiance {
    
    SILENT,
    
    FOREST,
    WATER,
    FACTORY,
    
    RAIN,
    THUNDER;

    Ambiance() {

    }

    public String getAmbientSound(EnvironmentController environmentController) {
        String[] sounds = getAmbientSounds(environmentController);
        return sounds[(int) (Math.random() * sounds.length)];
    }
    public static String getAmbientSound(Ambiance ambience, EnvironmentController controller) {
        return ambience.getAmbientSound(controller);
    }

    private String[] getAmbientSounds(EnvironmentController environmentController) {
        switch (this) {
            case FOREST:
                if(environmentController.isNight()) {
                    return new String[] {"outro.wav"};
                }
                return new String[] {"forestAmbiance0.wav","forestAmbiance1.wav","forestAmbiance2.wav","forestAmbiance3.wav","forestAmbiance4.wav"};
            case WATER:
                return new String[] {"water0.wav","water1.wav"};
            case FACTORY:
                return new String[] {"machineworking.wav"};
            case RAIN:
                return new String[] {"rain.wav"};
            case THUNDER:
                return new String[] {"thunder.wav"};
            default:
                return new String[] {};
        }
    }

    public static Ambiance getAmbiance(Ambiance ambience, EnvironmentController environmentController) {
        
    }
    
}
