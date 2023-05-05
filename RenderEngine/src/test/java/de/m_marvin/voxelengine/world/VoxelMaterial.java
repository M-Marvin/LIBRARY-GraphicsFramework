package de.m_marvin.voxelengine.world;

import de.m_marvin.renderengine.resources.locationtemplates.ResourceLocation;
import de.m_marvin.voxelengine.rendering.RenderType;

public class VoxelMaterial {
	
	protected ResourceLocation texture;
	protected float pixelScale;
	protected RenderType renderLayer;
	
	public VoxelMaterial(RenderType renderLayer, ResourceLocation texture, float pixelScale) {
		this.texture = texture;
		this.pixelScale = pixelScale;
		this.renderLayer = renderLayer;
	}
	
	public ResourceLocation texture() {
		return this.texture;
	}
	
	public float pixelScale() {
		return this.pixelScale;
	}

	public RenderType renderLayer() {
		return this.renderLayer;
	}
	
}
