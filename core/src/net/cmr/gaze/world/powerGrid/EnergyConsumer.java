package net.cmr.gaze.world.powerGrid;

public interface EnergyConsumer extends EnergyUser {
    
    public static final double MIN_EFFICIENCY = .5f;

    public double getEnergyConsumption();
    public default boolean isMachineFunctioning() {
        return getMachineEfficiency() >= MIN_EFFICIENCY;
    }
    public default double getMachineEfficiency() {
        if(getPowerGrid() == null) return 0;
        return getPowerGrid().getMachineEfficiency();
    }

}
