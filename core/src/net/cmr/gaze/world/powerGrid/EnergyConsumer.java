package net.cmr.gaze.world.powerGrid;

public interface EnergyConsumer extends EnergyUser {
    
    public static final double MIN_EFFICIENCY = .5f;

    public double getEnergyConsumption();

}
