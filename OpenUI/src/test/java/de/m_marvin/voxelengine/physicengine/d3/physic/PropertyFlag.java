package de.m_marvin.voxelengine.physicengine.d3.physic;

import java.util.function.Consumer;

import javax.vecmath.Vector3f;

import com.bulletphysics.dynamics.RigidBody;

import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.voxelengine.physicengine.d3.univec.UniVecHelper;

public enum PropertyFlag {
	
	STATIC(com.bulletphysics.collision.dispatch.CollisionFlags.STATIC_OBJECT, (rigidbody) -> {
		Vec3f inertia = UniVecHelper.calculateInertia(rigidbody.getCollisionShape(), 0);
		rigidbody.setMassProps(0, new Vector3f(inertia.x, inertia.y, inertia.z));
	}),
	KINEMATIC(com.bulletphysics.collision.dispatch.CollisionFlags.KINEMATIC_OBJECT, (rigidbody) -> {
		Vec3f inertia = UniVecHelper.calculateInertia(rigidbody.getCollisionShape(), 0);
		rigidbody.setMassProps(0, new Vector3f(inertia.x, inertia.y, inertia.z));
	}),
	NO_CONTACT_RESPONSE(com.bulletphysics.collision.dispatch.CollisionFlags.NO_CONTACT_RESPONSE),
	CUSTOM_MATERIAL_CALLBACK(com.bulletphysics.collision.dispatch.CollisionFlags.CUSTOM_MATERIAL_CALLBACK),
	CHARACTER(com.bulletphysics.collision.dispatch.CollisionFlags.CHARACTER_OBJECT);
	
	private PropertyFlag(int flag) {
		this(flag, (rigidbody) -> {});
	}
	
	private PropertyFlag(int flag, Consumer<RigidBody> configurator) {
		this.flag = flag;
		this.configurator = configurator;
	}
	
	private int flag;
	private Consumer<RigidBody> configurator;
	
	public int getFlag() {
		return flag;
	}
	
	public Consumer<RigidBody> getConfigurator() {
		return configurator;
	}
	
}
