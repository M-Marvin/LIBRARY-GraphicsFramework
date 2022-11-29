package de.m_marvin.voxelengine.rendering;

import java.util.Objects;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;

import com.google.common.base.Function;

import de.m_marvin.renderengine.GLStateManager;
import de.m_marvin.renderengine.resources.locationtemplates.ResourceLocation;
import de.m_marvin.renderengine.vertices.RenderPrimitive;
import de.m_marvin.renderengine.vertices.VertexFormat;
import de.m_marvin.voxelengine.Utility;

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
		return Objects.hash(this.getName(), this.textureMap());
	}
	
	public static RenderType[] voxelRenderLayers() {
		return new RenderType[] {
				RenderType.voxelSolid()
		};
	}
	
	protected static RenderType voxelSolid = new RenderType() {
		
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
	public static RenderType voxelSolid() {
		return voxelSolid;
	}
	
	protected static RenderType screen = new RenderType() {
		
		@Override
		public VertexFormat vertexFormat() {
			return DefaultVertexFormat.SCREEN;
		}
		
		@Override
		public ResourceLocation textureMap() {
			return null;
		}
		
		@Override
		public void setState() {
			GLStateManager.enable(GL11.GL_BLEND);
		}
		
		@Override
		public void resetState() {
			GLStateManager.disable(GL11.GL_BLEND);
		}
		
		@Override
		public RenderPrimitive primitive() {
			return RenderPrimitive.QUADS;
		}
		
		@Override
		public String getName() {
			return "screen";
		}
	};	
	public static RenderType screen() {
		return screen;
	}

	protected static Function<ResourceLocation, RenderType> screenTextured = Utility.memorize((texture) -> {
		return new RenderType() {
			
			@Override
			public VertexFormat vertexFormat() {
				return DefaultVertexFormat.SCREEN;
			}
			
			@Override
			public ResourceLocation textureMap() {
				return texture;
			}
			
			@Override
			public void setState() {
				GLStateManager.enable(GL11.GL_BLEND);
			}
			
			@Override
			public void resetState() {
				GLStateManager.disable(GL11.GL_BLEND);
			}
			
			@Override
			public RenderPrimitive primitive() {
				return RenderPrimitive.QUADS;
			}
			
			@Override
			public String getName() {
				return "screenTextured";
			}
		};
	});
	public static RenderType screenTextured(ResourceLocation texture) {
		return screenTextured.apply(texture);
	}
	
}
