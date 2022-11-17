package de.m_marvin.physicengine.d3.univec;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.MotionState;

import de.m_marvin.univec.impl.Vec3f;

public class UniVecHelper {
	
	public static Vec3f calculateInertia(CollisionShape shape, float mass) {
		Vector3f v = new Vector3f();
		shape.calculateLocalInertia(mass, v);
		return new Vec3f(v.x, v.y, v.z);
	}
	
	public static RigidBody rigidBody(float mass, MotionState motionState, CollisionShape shape, Vec3f inertia) {
		return new RigidBody(mass, motionState, shape, new Vector3f(inertia.x, inertia.y, inertia.z));
	}
	
}
