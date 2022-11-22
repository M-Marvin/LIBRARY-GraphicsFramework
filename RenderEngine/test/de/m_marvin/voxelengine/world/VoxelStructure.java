package de.m_marvin.voxelengine.world;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;

import de.m_marvin.physicengine.d3.physic.IRigidObject;
import de.m_marvin.physicengine.d3.univec.SimplifiedRigidBody;
import de.m_marvin.physicengine.d3.univec.UniVecHelper;
import de.m_marvin.unimat.impl.Quaternion;
import de.m_marvin.univec.impl.Vec3f;

public class VoxelStructure implements IRigidObject {
	
	public class StructureComponent {
		
		public VoxelComponent component;
		public Vec3f position;
		public Quaternion orientation;
		
		public StructureComponent(VoxelComponent component, Vec3f position, Quaternion orientation) {
			this.component = component;
			this.position = position;
			this.orientation = orientation;
		}
		
	}
	
	protected List<StructureComponent> components = new ArrayList<>();
	protected CollisionShape collisionShape;
	protected SimplifiedRigidBody rigidBody;
	
	public void addComponent(VoxelComponent component, Vec3f position, Quaternion orientation) {
		this.components.add(new StructureComponent(component, position, orientation));
		rebuildShape();
	}
	
	public List<StructureComponent> getComponents() {
		return components;
	}
	
	public void rebuildShape() {
		
		this.collisionShape = new BoxShape(new Vector3f(1, 1, 1));
		
	}
	
	@Override
	public void createRigidBody() {
		
		Vec3f inertia = UniVecHelper.calculateInertia(collisionShape, 1);
		this.rigidBody = UniVecHelper.rigidBody(1, new DefaultMotionState(), collisionShape, inertia);
		
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
	
}
