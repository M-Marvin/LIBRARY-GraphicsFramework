package de.m_marvin.voxelengine.world;

import java.util.List;

import de.m_marvin.physicengine.d3.physic.RigidPhysicWorld;
import de.m_marvin.physicengine.d3.util.BroadphaseAlgorithm;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.voxelengine.VoxelEngine;

public class ClientLevel {
	
	protected RigidPhysicWorld<VoxelStructure> dynamicWorld;
	
	public ClientLevel() {
		this.dynamicWorld = new RigidPhysicWorld<VoxelStructure>(BroadphaseAlgorithm.DBVT);
	}
	
	public void setGravity(Vec3f gravityVec) {
		this.dynamicWorld.setGravity(gravityVec);
	}
	
	public List<VoxelStructure> getStructures() {
		return this.dynamicWorld.getObjectList();
	}
	
	public boolean addStructure(VoxelStructure structure) {
		if (structure.getRigidBody() != null) return false;
		dynamicWorld.addObject(structure);
		return true;
	}
	
	public boolean removeStructure(VoxelStructure structure) {
		if (!dynamicWorld.containsObject(structure)) return false;
		dynamicWorld.removeObject(structure);
		return true;
	}
	
	public void tick() {
		
		this.dynamicWorld.stepPhysic(VoxelEngine.getInstance().getTickTime() / 1000F, 0, 0);
		
	}
	
}
