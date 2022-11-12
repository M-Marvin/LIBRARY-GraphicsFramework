package de.m_marvin.enginetest.world;

import de.m_marvin.enginetest.world.objects.WorldObject;
import de.m_marvin.physicengine.d3.physic.RigidPhysicWorld;
import de.m_marvin.physicengine.d3.util.BroadphaseAlgorithm;
import de.m_marvin.univec.impl.Vec3f;

public class ClientLevel {
	
	protected RigidPhysicWorld<WorldObject> physicalObjectContainer;
	
	public ClientLevel() {
		this.physicalObjectContainer = new RigidPhysicWorld<WorldObject>(new Vec3f(-1000F, -1000F, -1000F), new Vec3f(1000F, 1000F, 1000F), BroadphaseAlgorithm.SIMPLE);
	}
	
	public void addObject(WorldObject object) {
		if (this.physicalObjectContainer.containsObject(object)) return;
		this.physicalObjectContainer.addObject(object);
	}
	
	public void removeObject(WorldObject object) {
		if (!this.physicalObjectContainer.containsObject(object)) return;
		this.physicalObjectContainer.removeObject(object);
	}
	
}
