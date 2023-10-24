package net.cmr.gaze.world.powerGrid;

import java.util.ArrayList;
import java.util.List;

public class EnergySubnet {
    
    private List<EnergyProducer> producers;
    private List<EnergyConsumer> consumers;
    private List<EnergyBatteries> batteries;

    public EnergySubnet() {
        this.producers = new ArrayList<>();
        this.consumers = new ArrayList<>();
        this.batteries = new ArrayList<>();
    }

    /**
     * Returns the net energy of the subnet.
     */
    public double getNetEnergy() {
        double loss = 0;
        for(EnergyConsumer consumer : consumers) {
            loss += consumer.getEnergyConsumption();
        }
        for(EnergyProducer producer : producers) {
            loss -= producer.getEnergyProduced();
        }
        return loss;
    }

    /**
     * Returns the total energy stored in the subnet.
     */
    public double getBatteryCharge() {
        double charge = 0;
        for(EnergyBatteries battery : batteries) {
            charge += battery.getEnergyStored();
        }
        return charge;
    }

    public int getSize() {
        return producers.size() + consumers.size() + batteries.size();
    }

    public List<EnergyProducer> getProducers() {
        return producers;
    }
    public List<EnergyConsumer> getConsumers() {
        return consumers;
    }
    public List<EnergyBatteries> getBatteries() {
        return batteries;
    }

    public void addUser(EnergyUser user) {
        if(user instanceof EnergyProducer) {
            EnergyProducer producer = (EnergyProducer) user;
            if(producers.contains(producer)) {
                return;
            }
            producers.add(producer);
        } else if(user instanceof EnergyConsumer) {
            EnergyConsumer consumer = (EnergyConsumer) user;
            if(consumers.contains(consumer)) {
                return;
            }
            consumers.add(consumer);
        } else if(user instanceof EnergyBatteries) {
            EnergyBatteries battery = (EnergyBatteries) user;
            if(batteries.contains(battery)) {
                return;
            }
            batteries.add(battery);
        }
    }

    public void removeUser(EnergyUser user) {
        if(user instanceof EnergyProducer) {
            EnergyProducer producer = (EnergyProducer) user;
            producers.remove(producer);
        } else if(user instanceof EnergyConsumer) {
            EnergyConsumer consumer = (EnergyConsumer) user;
            consumers.remove(consumer);
        } else if(user instanceof EnergyBatteries) {
            EnergyBatteries battery = (EnergyBatteries) user;
            batteries.remove(battery);
        }
    }

}
