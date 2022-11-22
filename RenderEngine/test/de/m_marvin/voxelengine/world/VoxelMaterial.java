package de.m_marvin.voxelengine.world;

import de.m_marvin.renderengine.resources.locationtemplates.ResourceLocation;
import de.m_marvin.voxelengine.rendering.RenderType;

public class VoxelMaterial {

	public ResourceLocation texture() {
		return new ResourceLocation("example:materials/ground");
	}
	
	public float pixelScale() {
		return 0.5F;
	}

	public RenderType renderLayer() {
		return RenderType.voxelSolid();
	}
	
	
	
}
