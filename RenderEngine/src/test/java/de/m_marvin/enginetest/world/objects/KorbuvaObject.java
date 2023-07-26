package de.m_marvin.enginetest.world.objects;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;

import de.m_marvin.renderengine.resources.defimpl.ResourceLocation;
import de.m_marvin.univec.impl.Vec3f;

public class KorbuvaObject extends WorldObject {

	@Override
	public CollisionShape getShape() {
		return new BoxShape(new Vector3f(1.5F, 1F, 0.03125F));
	}

	@Override
	public float getMass() {
		return 5;
	}

	@Override
	public ResourceLocation getModel() {
		return new ResourceLocation("example:objects/korbuva");
	}

	@Override
	public Vec3f getModelOffset() {
		return new Vec3f(-1.5F, -0.5F, -0.03125F);
	}

}
