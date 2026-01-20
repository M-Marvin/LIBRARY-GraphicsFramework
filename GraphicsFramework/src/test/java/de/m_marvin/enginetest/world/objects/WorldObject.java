package de.m_marvin.enginetest.world.objects;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.linearmath.Transform;

import de.m_marvin.enginetest.physicengine.d3.physic.IRigidObject;
import de.m_marvin.enginetest.physicengine.d3.univec.SimplifiedRigidBody;
import de.m_marvin.enginetest.physicengine.d3.univec.UniVecHelper;
import de.m_marvin.gframe.resources.defimpl.ResourceLocation;
import de.m_marvin.unimat.impl.Matrix4f;
import de.m_marvin.univec.impl.Vec3f;

public abstract class WorldObject implements IRigidObject {
	
	Transform worldTransform = new Transform();
	SimplifiedRigidBody rigidBody;

	public abstract CollisionShape getShape();
	
	public abstract float getMass();
	
	public abstract ResourceLocation getModel();
	
	public abstract Vec3f getModelOffset();
	
	@Override
	public void createRigidBody() {
		this.rigidBody = new SimplifiedRigidBody(getMass(), null, getShape(), UniVecHelper.calculateInertia(getShape(), getMass()));
		this.rigidBody.setSleepingThresholds(8F, 1F);
	}

	@Override
	public void clearRigidBody() {
		this.rigidBody.destroy();
		this.rigidBody = null;
	}
	
	@Override
	public SimplifiedRigidBody getRigidBody() {
		return this.rigidBody;
	}

	public Matrix4f getModelTranslation() {
		return this.rigidBody.getTranslation().mul(Matrix4f.translate(getModelOffset()));
	}
	
}
