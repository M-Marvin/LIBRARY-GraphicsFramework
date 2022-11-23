package de.m_marvin.voxelengine.world;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.linearmath.Transform;

import de.m_marvin.physicengine.d3.univec.UniVecHelper;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;

public class VoxelComponent {
	
	protected List<VoxelMaterial> materials;
	protected List<int[][][]> voxels;
	
	public VoxelComponent(List<int[][][]> voxels, List<VoxelMaterial> materials) {
		this.materials = materials;
		this.voxels = voxels;
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
			
			Vec3f halfBoxSize = new Vec3f(max.sub(min).addI(1, 1, 1)).divI(2F);
			Vec3f centerOffset = new Vec3f(min).addI(halfBoxSize);
			BoxShape box = new BoxShape(new Vector3f(halfBoxSize.x, halfBoxSize.y, halfBoxSize.z));
			Transform transform = UniVecHelper.transform(centerOffset);
			
			shape.addChildShape(transform, box);
		}
		return shape;
	}
	
}
