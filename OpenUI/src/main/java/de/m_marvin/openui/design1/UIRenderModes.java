package de.m_marvin.openui.design1;

import org.lwjgl.opengl.GL33;

import de.m_marvin.renderengine.GLStateManager;
import de.m_marvin.renderengine.buffers.defimpl.RenderMode;
import de.m_marvin.renderengine.resources.defimpl.ResourcePath;
import de.m_marvin.renderengine.utility.NumberFormat;
import de.m_marvin.renderengine.vertices.RenderPrimitive;
import de.m_marvin.renderengine.vertices.VertexFormat;

public class UIRenderModes {
	
	public static final ResourcePath UI_SHADER_LOCATION = new ResourcePath("ui/solidPlane");

	public static RenderMode<ResourcePath> solidPlane(ResourcePath shaderLocation) {
		return new RenderMode<ResourcePath>(RenderPrimitive.TRIANGLES, new VertexFormat().appand("position", NumberFormat.FLOAT, 3, false).appand("color", NumberFormat.FLOAT, 4, false), shaderLocation, (shader, tl) -> {
			GLStateManager.enable(GL33.GL_DEPTH_TEST);
			GLStateManager.enable(GL33.GL_BLEND);
		});
	}
	
}
