package de.m_marvin.voxelengine.world;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.linearmath.Transform;

import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;
import de.m_marvin.voxelengine.physicengine.d3.univec.UniVecHelper;

public class VoxelComponent {
	
	protected List<VoxelMaterial> materials;
	protected List<int[][][]> voxels;
	protected Vec3i[] aabb;
	protected Vec3f centerOfShape;
	
	public VoxelComponent(List<int[][][]> voxels, List<VoxelMaterial> materials) {
		this.materials = materials;
		this.voxels = voxels;
		this.aabb = calculateAABB();
		this.centerOfShape = calculateCenterOfShape();
	}

	protected Vec3i[] calculateAABB() {
		Vec3i min = new Vec3i(voxels.get(0).length, voxels.get(0)[0].length, voxels.get(0)[0][0].length);
		Vec3i max = new Vec3i(0, 0, 0);
		for (int[][][] voxelArr : voxels) {
			
			for (int x = 0; x < voxelArr.length; x++) {
				for (int y = 0; y < voxelArr[x].length; y++) {
					for (int z = 0; z < voxelArr[x][y].length; z++) {
						
						if (voxelArr[x][y][z] > 0) {
							
							if (x < min.x) min.x = x;
							if (y < min.y) min.y = y;
							if (z < min.z) min.z = z;
							
							if (x > max.x) max.x = x;
							if (y > max.y) max.y = y;
							if (z > max.z) max.z = z;
							
						}
						
					}
				}
			}
		}
		return new Vec3i[] {min, max};
	}
	
	public Vec3f calculateCenterOfShape() {
		Vec3i[] aabb = getAabb();
		return new Vec3f(aabb[1].sub(aabb[0])).div(2F).add(aabb[0]);
	}
	
	public Vec3f getCenterOfShape() {
		return centerOfShape;
	}
	
	public Vec3i[] getAabb() {
		return aabb;
	}
	
	public VoxelComponent() {
		this.materials = new ArrayList<>();
		this.voxels = new ArrayList<>();
	}
	
	public List<VoxelMaterial> getMaterials() {
		return materials;
	}
	
	public List<int[][][]> getVoxels() {
		return voxels;
	}
	
	public VoxelMaterial getMaterial(int id) {
		return this.materials.get(id - 1);
	}
	
	public CollisionShape buildShape() {
		CompoundShape shape = new CompoundShape();
		Vec3f centerOfMassShape = getCenterOfShape();
		
		for (int[][][] voxelArr : voxels) {
			Vec3i min = new Vec3i(voxelArr.length, voxelArr[0].length, voxelArr[0][0].length);
			Vec3i max = new Vec3i(0, 0, 0);
			
			for (int x = 0; x < voxelArr.length; x++) {
				for (int y = 0; y < voxelArr[x].length; y++) {
					for (int z = 0; z < voxelArr[x][y].length; z++) {
						
						if (voxelArr[x][y][z] > 0) {
							
							if (x < min.x) min.x = x;
							if (y < min.y) min.y = y;
							if (z < min.z) min.z = z;
							
							if (x > max.x) max.x = x;
							if (y > max.y) max.y = y;
							if (z > max.z) max.z = z;
							
						}
						
					}
				}
			}
			
			Vec3f halfBoxSize = new Vec3f(max.sub(min)).div(2F);
			Vec3f centerOffset = new Vec3f(min).add(halfBoxSize).sub(centerOfMassShape).div(VoxelStructure.PHYSICS_2_VOXEL_FACTOR);
			halfBoxSize.divI(VoxelStructure.PHYSICS_2_VOXEL_FACTOR);
			BoxShape box = new BoxShape(new Vector3f(halfBoxSize.x, halfBoxSize.y, halfBoxSize.z));
			Transform transform = UniVecHelper.transform(centerOffset);
			
			shape.addChildShape(transform, box);
		}
		return shape;
	}
	
}
