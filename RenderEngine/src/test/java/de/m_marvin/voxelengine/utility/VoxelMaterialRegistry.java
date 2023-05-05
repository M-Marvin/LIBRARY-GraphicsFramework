package de.m_marvin.voxelengine.utility;

import java.util.HashMap;
import java.util.Map.Entry;

import de.m_marvin.renderengine.resources.locationtemplates.ResourceLocation;
import de.m_marvin.voxelengine.world.VoxelMaterial;

public class VoxelMaterialRegistry {
	
	protected HashMap<ResourceLocation, VoxelMaterial> materials = new HashMap<>();
	
	public VoxelMaterial registerMaterial(ResourceLocation name, VoxelMaterial material) {
		materials.put(name, material);
		return material;
	}
	
	public ResourceLocation nameOfMaterial(VoxelMaterial material) {
		for (Entry<ResourceLocation, VoxelMaterial> entry : materials.entrySet()) {
			if (entry.getValue() == material) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	public VoxelMaterial getMaterial(ResourceLocation name) {
		return this.materials.get(name);
	}
	
}
