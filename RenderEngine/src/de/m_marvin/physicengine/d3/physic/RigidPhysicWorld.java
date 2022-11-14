package de.m_marvin.physicengine.d3.physic;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;

import de.m_marvin.enginetest.world.objects.WorldObject;
import de.m_marvin.physicengine.d3.util.BroadphaseAlgorithm;
import de.m_marvin.univec.impl.Vec3f;

public class RigidPhysicWorld<T extends IRigidObject> {

	protected final DynamicsWorld dynamicWorld;
	protected final Vec3f worldMin;
	protected final Vec3f worldMax;
	protected final boolean hasBoarders;
	
	protected List<T> rigidBodies = new ArrayList<>();
	
	public RigidPhysicWorld(BroadphaseAlgorithm broadphaseAlgorithm) {
		this(null, null, broadphaseAlgorithm);
	}
	
	public RigidPhysicWorld(Vec3f worldMin, Vec3f worldMax, BroadphaseAlgorithm broadphaseAlgorithm) {
		
		this.hasBoarders = worldMax != null && worldMax != null;
		this.worldMin = worldMin;
		this.worldMax = worldMax;
		
		if (broadphaseAlgorithm.needsBoarders() && !this.hasBoarders) throw new IllegalArgumentException("The selected broadphase algorithm needs a specified maximum world size!");
		
		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher collisionDispatcher = new CollisionDispatcher(collisionConfiguration);
		BroadphaseInterface broadphaseInterface = broadphaseAlgorithm.getInterface(worldMin, worldMax);
		SequentialImpulseConstraintSolver constrainSolver = new SequentialImpulseConstraintSolver();
		
		this.dynamicWorld = new DiscreteDynamicsWorld(collisionDispatcher, broadphaseInterface, constrainSolver, collisionConfiguration);
		
	}
	
	public DynamicsWorld getDynamicWorld() {
		return dynamicWorld;
	}
	
	public void stepPhysic(float timeStep, int maxSubSteps, float fixedTimeStep) {
		this.dynamicWorld.stepSimulation(timeStep, maxSubSteps, fixedTimeStep);
	}
	
	public void setGravity(Vec3f gravityVec) {
		this.dynamicWorld.setGravity(new Vector3f(gravityVec.x, gravityVec.y, gravityVec.z));
	}
	
	public Vec3f getWorldMax() {
		if (!this.hasBoarders) throw new UnsupportedOperationException("This world instance has no limits!");
		return worldMax;
	}
	
	public Vec3f getWorldMin() {
		if (!this.hasBoarders) throw new UnsupportedOperationException("This world instance has no limits!");
		return worldMin;
	}
	
	public boolean hasBoarders() {
		return hasBoarders;
	}
	
	public Vec3f getGravity() {
		Vector3f gravityVec = new Vector3f();
		this.dynamicWorld.getGravity(gravityVec);
		return new Vec3f(gravityVec.x, gravityVec.y, gravityVec.z);
	}
	
	public void addObject(T rigidBodyObject) {
		if (!this.rigidBodies.contains(rigidBodyObject)) {
			rigidBodyObject.createRigidBody();
			this.dynamicWorld.addRigidBody(rigidBodyObject.getRigidBody());
			this.rigidBodies.add(rigidBodyObject);
		}
	}
	
	public void removeObject(T rigidBodyObject) {
		if (this.rigidBodies.contains(rigidBodyObject)) {
			this.dynamicWorld.removeRigidBody(rigidBodyObject.getRigidBody());
			rigidBodyObject.clearRigidBody();
			this.rigidBodies.remove(rigidBodyObject);
		}
	}
	
	public boolean containsObject(WorldObject rigidBodyObject) {
		return this.rigidBodies.contains(rigidBodyObject);
	}
	
	public List<T> getObjectList() {
		return this.rigidBodies;
	}
	
}
