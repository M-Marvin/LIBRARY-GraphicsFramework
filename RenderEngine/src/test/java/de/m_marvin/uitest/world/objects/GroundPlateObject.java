package de.m_marvin.uitest.world.objects;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;

import de.m_marvin.renderengine.resources.defimpl.ResourceLocation;
import de.m_marvin.univec.impl.Vec3f;

public class GroundPlateObject extends WorldObject {

	@Override
	public CollisionShape getShape() {
		return new BoxShape(new Vector3f(35, 0.5F, 35));
	}
	
	@Override
	public void createRigidBody() {
		super.createRigidBody();
		this.rigidBody.setCollisionFlags(CollisionFlags.STATIC_OBJECT);
	}

	@Override
	public float getMass() {
		return 0;
	}

	@Override
	public ResourceLocation getModel() {
		return new ResourceLocation("example:objects/ground_plate_test");
	}
	
	@Override
	public Vec3f getModelOffset() {
		return new Vec3f(-0F, -0.5F, 0F);
	}
	
}
