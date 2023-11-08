package de.m_marvin.openui.design1;

import org.lwjgl.opengl.GL33;

import de.m_marvin.openui.core.UIRenderMode;
import de.m_marvin.renderengine.GLStateManager;
import de.m_marvin.renderengine.resources.defimpl.ResourcePath;
import de.m_marvin.renderengine.utility.NumberFormat;
import de.m_marvin.renderengine.vertices.RenderPrimitive;
import de.m_marvin.renderengine.vertices.VertexFormat;

public class UIRenderModes {
	
	public static final ResourcePath SHADER_SOLID_PLANE = new ResourcePath("ui/solidPlane");
	public static final ResourcePath SHADER_CLICKABLE_HOVERABLE = new ResourcePath("ui/clickableHoverable");
	
	private static boolean test = false;
	
	public static UIRenderMode<ResourcePath> clickableHoverable(ResourcePath texture) {
		return new UIRenderMode<ResourcePath>(
				RenderPrimitive.TRIANGLES, 
				new VertexFormat().appand("position", NumberFormat.FLOAT, 3, false).appand("uv", NumberFormat.FLOAT, 2, false).appand("color", NumberFormat.FLOAT, 4, false).appand("pressed", NumberFormat.BYTE, 1, false), 
				SHADER_CLICKABLE_HOVERABLE, 
				(shader, tl, container) -> {
					

					if (!test) {
						test = true;
						// FIXME
						tl.buildAtlasMapFromTextures(new ResourcePath("ui"), new ResourcePath("ui_atkas"), false, false);
					}
					
					
					shader.getUniform("ProjMat").setMatrix4f(container.getProjectionMatrix());
					shader.getUniform("CursorPos").setVec2f(container.getCursorPosition());
					shader.getUniform("Texture").setTextureSampler(tl.getTexture(texture));
					GLStateManager.enable(GL33.GL_DEPTH_TEST);
					GLStateManager.enable(GL33.GL_BLEND);
				}
		);
	}
	
	public static UIRenderMode<ResourcePath> solidPlane() {
		return new UIRenderMode<ResourcePath>(
				RenderPrimitive.TRIANGLES, 
				new VertexFormat().appand("position", NumberFormat.FLOAT, 3, false).appand("color", NumberFormat.FLOAT, 4, false),
				SHADER_SOLID_PLANE, 
				(shader, tl, container) -> {
					shader.getUniform("ProjMat").setMatrix4f(container.getProjectionMatrix());
					GLStateManager.enable(GL33.GL_DEPTH_TEST);
					GLStateManager.enable(GL33.GL_BLEND);
				}
		);
	}
	
	
	
}
