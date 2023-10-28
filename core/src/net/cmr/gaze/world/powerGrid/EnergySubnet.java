package net.cmr.gaze.world.powerGrid;

import java.util.ArrayList;
import java.util.List;

import net.cmr.gaze.util.CustomMath;

public class EnergySubnet {
    
    private List<EnergyProducer> producers;
    private List<EnergyConsumer> consumers;
    private List<EnergyBatteries> batteries;
    private final EnergyDistributor distributor;

    public EnergySubnet(EnergyDistributor distributor) {
        this.distributor = distributor;
        this.producers = new ArrayList<>();
        this.consumers = new ArrayList<>();
        this.batteries = new ArrayList<>();
    }

    /**
     * Returns the net energy of the subnet.
     */
    public double getNetEnergy() {
        return getEnergyProduced() - getEnergyConsumed();
    }

    public double getEnergyProduced() {
        double produced = 0;
        for(EnergyProducer producer : producers) {
            produced += producer.getEnergyProduced();
        }
        return produced;
    }

    public double getEnergyConsumed() {
        double consumed = 0;
        for(EnergyConsumer consumer : consumers) {
            consumed += consumer.getEnergyConsumption();
        }
        return consumed;
    }

    public double getMachineEfficiency() {
        double loss = getEnergyConsumed();
        double gained = getEnergyProduced();
        
        if(loss == 0) {
            return 1;
        }

        double percent = (gained / loss);
        percent = CustomMath.minMax(0, percent, 1);
        return percent;
    }
    public double getGenerationEfficiency() {
        double loss = getEnergyProduced();
        double gained = getEnergyConsumed();
        
        if(gained == 0) {
            return 1;
        }
        double percent = (loss / gained);
        percent = CustomMath.minMax(0, percent, 1);
        return percent;
    }

    public ArrayList<EnergyUser> releaseEnergyUsers() {
        ArrayList<EnergyUser> users = new ArrayList<>();
        users.addAll(producers);
        users.addAll(consumers);
        users.addAll(batteries);
        producers.clear();
        consumers.clear();
        batteries.clear();
        return users;
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
