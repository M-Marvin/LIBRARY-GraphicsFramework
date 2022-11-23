package de.m_marvin.voxelengine.rendering;

import org.lwjgl.opengl.GL33;

import de.m_marvin.renderengine.GLStateManager;
import de.m_marvin.renderengine.resources.locationtemplates.ResourceLocation;
import de.m_marvin.renderengine.vertices.RenderPrimitive;
import de.m_marvin.renderengine.vertices.VertexFormat;

public abstract class RenderType {
	
	public abstract void setState();
	public abstract void resetState();
	public abstract VertexFormat vertexFormat();
	public abstract ResourceLocation textureMap();
	public abstract RenderPrimitive primitive();
	public abstract String getName();
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RenderType type) {
			return type.getName().equals(this.getName());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.getName().hashCode();
	}
	
	public static RenderType[] voxelRenderLayers() {
		return new RenderType[] {
				RenderType.voxelSolid()
		};
	}
	
	public static RenderType voxelSolid() {
		return new RenderType() {
			
			@Override
			public VertexFormat vertexFormat() {
				return DefaultVertexFormat.VOXELS;
			}
			
			@Override
			public ResourceLocation textureMap() {
				return null;
			}
			
			@Override
			public void setState() {
				//GLStateManager.enable(GL33.GL_CULL_FACE);
				GLStateManager.enable(GL33.GL_DEPTH_TEST);
			}
			
			@Override
			public void resetState() {
				//GLStateManager.disable(GL33.GL_CULL_FACE);
				GLStateManager.disable(GL33.GL_DEPTH_TEST);
			}

			@Override
			public RenderPrimitive primitive() {
				return RenderPrimitive.POINTS;
			}

			@Override
			public String getName() {
				return "voxel_solid";
			}
		};
	}
	
}
