package de.m_marvin.openui.design1;

import java.util.function.Function;

import org.lwjgl.opengl.GL33;

import de.m_marvin.openui.core.UIRenderMode;
import de.m_marvin.openui.core.UITextureHandler;
import de.m_marvin.renderengine.GLStateManager;
import de.m_marvin.renderengine.resources.defimpl.ResourcePath;
import de.m_marvin.renderengine.utility.NumberFormat;
import de.m_marvin.renderengine.utility.Utility;
import de.m_marvin.renderengine.vertices.RenderPrimitive;
import de.m_marvin.renderengine.vertices.VertexFormat;

public class UIRenderModes {
	
	public static final ResourcePath SHADER_PLAIN_SOLID = new ResourcePath("ui/plain_solid");
	public static final ResourcePath SHADER_TEXTURED_SOLID = new ResourcePath("ui/textured_solid");
	public static final ResourcePath SHADER_PLAIN_CLICKABLE = new ResourcePath("ui/plain_clickable");
	
	public static final int RENDER_ORDER_SOLID = 0;
	public static final int RENDER_ORDER_TRANSPARENT_0 = 1;
	public static final int RENDER_ORDER_TRANSPARENT_1 = 2;
	
	public static UIRenderMode<ResourcePath> plainSolid() {
		return plainSolid;
	}
	public static final UIRenderMode<ResourcePath> plainSolid = new UIRenderMode<ResourcePath>(
			RenderPrimitive.TRIANGLES, 
			new VertexFormat()
				.appand("position", NumberFormat.FLOAT, 3, false)
				.appand("color", NumberFormat.FLOAT, 4, false),
			SHADER_PLAIN_SOLID, 
			(shader, container) -> {
				shader.getUniform("ProjMat").setMatrix4f(container.getProjectionMatrix());
				GLStateManager.enable(GL33.GL_DEPTH_TEST);
				GLStateManager.enable(GL33.GL_BLEND);
				GLStateManager.blendFunc(GL33.GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA);
			}
	);

	public static UIRenderMode<ResourcePath> texturedSolid(ResourcePath texture) {
		return texturedSolid.apply(texture);
	}
	private static final Function<ResourcePath, UIRenderMode<ResourcePath>> texturedSolid = Utility.memorize((texture) -> {
		return new UIRenderMode<ResourcePath>(
				RenderPrimitive.TRIANGLES,
				new VertexFormat()
					.appand("position", NumberFormat.FLOAT, 3, false)
					.appand("uv", NumberFormat.FLOAT, 2, false)
					.appand("color", NumberFormat.FLOAT, 4, false),
				SHADER_TEXTURED_SOLID,
				(shader, container) -> {
					UITextureHandler.ensureSingleTexturesLoaded(container.getActiveTexureLoader(), texture);
					
					shader.getUniform("ProjMat").setMatrix4f(container.getProjectionMatrix());
					shader.getUniform("Texture").setTextureSampler(container.getActiveTexureLoader().getTexture(texture));
					GLStateManager.enable(GL33.GL_DEPTH_TEST);
					GLStateManager.enable(GL33.GL_BLEND);
					GLStateManager.blendFunc(GL33.GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA);
				}
		);
	});
	
	public static UIRenderMode<ResourcePath> plainClickable() {
		return plainClickable;
	}
	private static final UIRenderMode<ResourcePath> plainClickable = new UIRenderMode<ResourcePath>(
			RenderPrimitive.TRIANGLES, 
			new VertexFormat()
				.appand("position", NumberFormat.FLOAT, 3, false)
				.appand("pxpos", NumberFormat.FLOAT, 2, false)
				.appand("pxsize", NumberFormat.UINT, 2, false)
				.appand("color", NumberFormat.FLOAT, 4, false)
				.appand("pressed", NumberFormat.INT, 1, false), 
			SHADER_PLAIN_CLICKABLE, 
			(shader, container) -> {
				shader.getUniform("ProjMat").setMatrix4f(container.getProjectionMatrix());
				shader.getUniform("BorderWidth").setInt(2);
				GLStateManager.enable(GL33.GL_DEPTH_TEST);
				GLStateManager.enable(GL33.GL_BLEND);
				GLStateManager.blendFunc(GL33.GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA);
			}
	);
	
}
