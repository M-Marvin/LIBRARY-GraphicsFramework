package de.m_marvin.voxelengine.world;

import java.util.ArrayList;
import java.util.List;

import de.m_marvin.unimat.impl.Quaternion;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;
import de.m_marvin.voxelengine.VoxelEngine;
import de.m_marvin.voxelengine.physicengine.d3.physic.IRigidObject;
import de.m_marvin.voxelengine.physicengine.d3.physic.RigidPhysicWorld;
import de.m_marvin.voxelengine.physicengine.d3.util.BroadphaseAlgorithm;

public class ClientLevel {
	
	protected RigidPhysicWorld<IRigidObject> dynamicWorld;
	
	public ClientLevel() {
		this.dynamicWorld = new RigidPhysicWorld<IRigidObject>(BroadphaseAlgorithm.SIMPLE);
	}
	
	public void setGravity(Vec3f gravityVec) {
		this.dynamicWorld.setGravity(gravityVec);
	}
	
	public List<VoxelStructure> getStructures() {
		List<VoxelStructure> s = new ArrayList<>();
		for (IRigidObject o : this.dynamicWorld.getObjectList()) {
			if (o instanceof VoxelStructure i) s.add(i);
		}
		return s;
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

	public void debug() {

		WorldObject plate = new GroundPlateObject();
		dynamicWorld.addObject(plate);
		plate.getRigidBody().setOrientation(new Quaternion(new Vec3i(1, 0, 0), 0));
		plate.getRigidBody().setPosition(new Vec3f(0F, -30F, 0F));
	}
	
}
