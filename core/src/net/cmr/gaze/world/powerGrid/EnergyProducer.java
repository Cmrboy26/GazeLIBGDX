package net.cmr.gaze.world.powerGrid;

public interface EnergyProducer extends EnergyUser  {

    public double getEnergyProduced();
    public default boolean isMachineProducing() {
        return getEnergyProduced() > 0;
    }
}
