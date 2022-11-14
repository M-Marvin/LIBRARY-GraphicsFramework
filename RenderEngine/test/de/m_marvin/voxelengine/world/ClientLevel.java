package de.m_marvin.voxelengine.world;

import de.m_marvin.physicengine.d3.physic.RigidPhysicWorld;
import de.m_marvin.physicengine.d3.util.BroadphaseAlgorithm;
import de.m_marvin.univec.impl.Vec3f;

public class ClientLevel {
	
	protected RigidPhysicWorld<VoxelStructure> dynamicWorld;
	
	public ClientLevel() {
		this.dynamicWorld = new RigidPhysicWorld<VoxelStructure>(BroadphaseAlgorithm.DBVT);
	}
	
	public void setGravity(Vec3f gravityVec) {
		this.dynamicWorld.setGravity(gravityVec);
	}
	
}
