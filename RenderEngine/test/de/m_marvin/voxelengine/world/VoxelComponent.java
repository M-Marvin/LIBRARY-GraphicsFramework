package de.m_marvin.voxelengine.world;

import java.util.ArrayList;
import java.util.List;

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
	
}
