package de.m_marvin.openui.rendering;

import org.lwjgl.opengl.GL33;

import de.m_marvin.renderengine.GLStateManager;
import de.m_marvin.renderengine.buffers.defimpl.RenderMode;
import de.m_marvin.renderengine.resources.IResourceProvider;
import de.m_marvin.renderengine.utility.NumberFormat;
import de.m_marvin.renderengine.vertices.RenderPrimitive;
import de.m_marvin.renderengine.vertices.VertexFormat;

public class UIRenderModes<R extends IResourceProvider<R>> {
	
	public static <R extends IResourceProvider<R>> RenderMode<R> solidPlane(R shaderLocation) {
		return new RenderMode<R>(RenderPrimitive.TRIANGLES, new VertexFormat().appand("position", NumberFormat.FLOAT, 3, false).appand("color", NumberFormat.FLOAT, 4, false), shaderLocation, (shader, tl) -> {
			GLStateManager.enable(GL33.GL_DEPTH_TEST);
			GLStateManager.enable(GL33.GL_BLEND);
		});
	}
	
	
	
}
