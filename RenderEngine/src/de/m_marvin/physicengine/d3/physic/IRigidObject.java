package de.m_marvin.physicengine.d3.physic;

import com.bulletphysics.dynamics.RigidBody;

public interface IRigidObject {
	
	public void createRigidBody();
	public void clearRigidBody();
	public RigidBody getRigidBody();
		
}
