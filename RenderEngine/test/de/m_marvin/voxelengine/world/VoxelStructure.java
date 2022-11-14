package de.m_marvin.voxelengine.world;

import java.util.List;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;

import de.m_marvin.physicengine.d3.physic.IRigidObject;
import de.m_marvin.unimat.impl.Quaternion;
import de.m_marvin.univec.impl.Vec3f;

public class VoxelStructure implements IRigidObject {
	
	public class StructureComponent {
		
		protected VoxelComponent component;
		protected Vec3f position;
		protected Quaternion orientation;
		
		public StructureComponent(VoxelComponent component, Vec3f position, Quaternion orientation) {
			this.component = component;
			this.position = position;
			this.orientation = orientation;
		}
		
	}
	
	protected List<StructureComponent> components;
	protected CollisionShape collisionShape;
	
	public void addComponent(VoxelComponent component, Vec3f position, Quaternion orientation) {
		this.components.add(new StructureComponent(component, position, orientation));
	}
	
	public void rebuildShape() {
		
		
		
	}
	
	@Override
	public void createRigidBody() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearRigidBody() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RigidBody getRigidBody() {
		// TODO Auto-generated method stub
		return null;
	}

}
