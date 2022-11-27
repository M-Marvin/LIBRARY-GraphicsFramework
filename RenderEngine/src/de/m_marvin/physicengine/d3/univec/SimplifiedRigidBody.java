package de.m_marvin.physicengine.d3.univec;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import de.m_marvin.physicengine.d3.physic.PropertyFlag;
import de.m_marvin.unimat.impl.Matrix4f;
import de.m_marvin.unimat.impl.Quaternion;
import de.m_marvin.univec.impl.Vec3f;

public class SimplifiedRigidBody extends RigidBody {
	
	public SimplifiedRigidBody(float mass, MotionState motionState, CollisionShape collisionShape, Vec3f localInertia) {
		super(mass, motionState, collisionShape, new Vector3f(localInertia.x, localInertia.y, localInertia.z));
	}

	public SimplifiedRigidBody(RigidBodyConstructionInfo constructionInfo) {
		super(constructionInfo);
	}

	public Quaternion getRotation() {
		Quat4f quat = this.getOrientation(new Quat4f());
		return new Quaternion(quat.x, quat.y, quat.z, quat.w);
	}
	
	public Vec3f getPosition() {
		Vector3f vec = this.getCenterOfMassPosition(new Vector3f());
		return new Vec3f(vec.x, vec.y, vec.z);
	}
	
	public Vec3f getGraphicalPosition() {
		Vector3f minVec = new Vector3f();
		Vector3f maxVec = new Vector3f();
		this.getAabb(minVec, maxVec);
		return new Vec3f(minVec.x, minVec.y, minVec.z);
	}
	
	public void setPosition(Vec3f position) {
		Transform t = this.getCenterOfMassTransform(new Transform());
		t.origin.set(position.x, position.y, position.z);
		this.setCenterOfMassTransform(t);
	}
	
	public void setOrientation(Quaternion rotation) {
		Transform t = this.getCenterOfMassTransform(new Transform());
		t.setRotation(new Quat4f(rotation.i(), rotation.j(), rotation.k(), rotation.r()));
		this.setCenterOfMassTransform(t);
	}

	public Matrix4f getTranslation() {
		Transform t = this.getCenterOfMassTransform(new Transform());
		javax.vecmath.Matrix4f m = t.getMatrix(new javax.vecmath.Matrix4f());
		return new Matrix4f(
				m.m00, m.m01, m.m02, m.m03,
				m.m10, m.m11, m.m12, m.m13,
				m.m20, m.m21, m.m22, m.m23,
				m.m30, m.m31, m.m32, m.m33
		);
	}
	
	public void addFlag(PropertyFlag flag) {
		setCollisionFlags(getCollisionFlags() | flag.getFlag());
		flag.getConfigurator().accept(this);
	}
	
	public void removeFlag(PropertyFlag flag) {
		setCollisionFlags(getCollisionFlags() & ~flag.getFlag());
	}
	
}
