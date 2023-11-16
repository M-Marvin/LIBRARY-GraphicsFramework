package de.m_marvin.openui.core;

import de.m_marvin.renderengine.buffers.defimpl.IRenderMode;
import de.m_marvin.renderengine.resources.IResourceProvider;
import de.m_marvin.renderengine.shaders.ShaderInstance;
import de.m_marvin.renderengine.vertices.RenderPrimitive;
import de.m_marvin.renderengine.vertices.VertexFormat;

public record UIRenderMode<R extends IResourceProvider<R>>(
		RenderPrimitive primitive, 
		VertexFormat vertexFormat, 
		R shader,
		IUIRenderModeSetup<R> setupRenderMode
) implements IRenderMode {
	
	public void setupRenderMode(ShaderInstance shader, UIContainer<R> container) {
		this.setupRenderMode().setup(shader, container);
	}
	
	@FunctionalInterface
	public static interface IUIRenderModeSetup<R extends IResourceProvider<R>> {
		public void setup(ShaderInstance shader, UIContainer<R> container);
	}
	
}
