package de.m_marvin.voxelengine.world;

import java.util.ArrayList;
import java.util.List;

import com.bulletphysics.collision.shapes.CompoundShape;

import de.m_marvin.physicengine.d3.physic.IRigidObject;
import de.m_marvin.physicengine.d3.physic.PropertyFlag;
import de.m_marvin.physicengine.d3.univec.SimplifiedRigidBody;
import de.m_marvin.physicengine.d3.univec.UniVecHelper;
import de.m_marvin.unimat.impl.Quaternion;
import de.m_marvin.univec.impl.Vec3f;

public class VoxelStructure implements IRigidObject {
	
	public static final float PHYSICS_2_VOXEL_FACTOR = 10F;
	
	public class StructureComponent {
		
		public VoxelComponent component;
		public Vec3f position;
		public Quaternion orientation;
		
		public StructureComponent(VoxelComponent component, Vec3f position, Quaternion orientation) {
			this.component = component;
			this.position = position;
			this.orientation = orientation;
		}
		
		public void buildShape(CompoundShape shape) {
			shape.addChildShape(UniVecHelper.transform(position.div(PHYSICS_2_VOXEL_FACTOR), orientation), component.buildShape());
		}
		
	}
	
	protected List<StructureComponent> components = new ArrayList<>();
	protected CompoundShape collisionShape;
	protected SimplifiedRigidBody rigidBody;
	
	public void addComponent(VoxelComponent component, Vec3f position, Quaternion orientation) {
		this.components.add(new StructureComponent(component, position, orientation));
		rebuildShape();
	}
	
	public List<StructureComponent> getComponents() {
		return components;
	}
	
	public void rebuildShape() {
		this.collisionShape = new CompoundShape();
		this.components.forEach((component) -> component.buildShape(this.collisionShape));
	}
	
	public CompoundShape getCollisionShape() {
		return collisionShape;
	}
	
	@Override
	public void createRigidBody() {
		Vec3f inertia = UniVecHelper.calculateInertia(collisionShape, 1);
		this.rigidBody = new SimplifiedRigidBody(1, null, collisionShape, inertia);
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
	
	public Vec3f getPosition() {
		return this.rigidBody.getPosition().mul(PHYSICS_2_VOXEL_FACTOR);
	}
	
	public void setPosition(Vec3f position) {
		this.rigidBody.setPosition(position.div(PHYSICS_2_VOXEL_FACTOR));
	}
	
	public Quaternion getOrientation() {
		return this.rigidBody.getRotation();
	}
	
	public void setOrientation(Quaternion rotation) {
		this.rigidBody.setOrientation(rotation);
	}
	
	public void setStatic(boolean isStatic) {
		if (this.rigidBody == null) throw new IllegalStateException("The structure has not been placed yet!");
		if (isStatic) {
			this.rigidBody.addFlag(PropertyFlag.STATIC);
		} else {
			this.rigidBody.removeFlag(PropertyFlag.STATIC);
		}
	}
	
}
