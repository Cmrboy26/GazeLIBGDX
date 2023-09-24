package net.cmr.gaze.world;

import net.cmr.gaze.world.entities.HealthEntity;

public interface HealthEntityListener {

	public void healthChanged(HealthEntity entity, int damageAmount);
	
}
