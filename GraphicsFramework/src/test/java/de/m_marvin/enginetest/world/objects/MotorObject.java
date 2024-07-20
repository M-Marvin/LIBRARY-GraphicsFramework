package de.m_marvin.enginetest.world.objects;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;

import de.m_marvin.gframe.resources.defimpl.ResourceLocation;
import de.m_marvin.univec.impl.Vec3f;

public class MotorObject extends WorldObject {
	
	@Override
	public CollisionShape getShape() {
		return new BoxShape(new Vector3f(0.5F, 0.5F, 0.5F));
	}

	@Override
	public float getMass() {
		return 6F;
	}

	@Override
	public ResourceLocation getModel() {
		return new ResourceLocation("example:objects/motor_item");
	}

	@Override
	public Vec3f getModelOffset() {
		return new Vec3f(0F, -0.0F, 0F);
	}
	
}
