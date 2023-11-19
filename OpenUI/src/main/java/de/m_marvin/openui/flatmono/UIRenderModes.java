package de.m_marvin.openui.flatmono;

import java.util.function.Function;

import org.lwjgl.opengl.GL33;

import de.m_marvin.openui.core.UIRenderMode;
import de.m_marvin.openui.core.UITextureHandler;
import de.m_marvin.renderengine.GLStateManager;
import de.m_marvin.renderengine.resources.defimpl.ResourceLocation;
import de.m_marvin.renderengine.utility.NumberFormat;
import de.m_marvin.renderengine.utility.Utility;
import de.m_marvin.renderengine.vertices.RenderPrimitive;
import de.m_marvin.renderengine.vertices.VertexFormat;

public class UIRenderModes {
	
	public static final ResourceLocation SHADER_PLAIN_SOLID = new ResourceLocation(UIResourceFolders.NAMESPACE, "ui/plain_solid");
	public static final ResourceLocation SHADER_TEXTURED_SOLID = new ResourceLocation(UIResourceFolders.NAMESPACE, "ui/textured_solid");
	public static final ResourceLocation SHADER_PLAIN_CLICKABLE = new ResourceLocation(UIResourceFolders.NAMESPACE, "ui/plain_clickable");
	public static final ResourceLocation SHADER_PLAIN_CIRCLE = new ResourceLocation(UIResourceFolders.NAMESPACE, "ui/plain_circle");
	
	public static final int RENDER_ORDER_SOLID = 0;
	public static final int RENDER_ORDER_TRANSPARENT_0 = 1;
	public static final int RENDER_ORDER_TRANSPARENT_1 = 2;
	
	public static UIRenderMode<ResourceLocation> plainSolid() {
		return plainSolid;
	}
	public static final UIRenderMode<ResourceLocation> plainSolid = new UIRenderMode<ResourceLocation>(
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

	public static UIRenderMode<ResourceLocation> texturedSolid(ResourceLocation texture) {
		return texturedSolid.apply(texture);
	}
	private static final Function<ResourceLocation, UIRenderMode<ResourceLocation>> texturedSolid = Utility.memorize((texture) -> {
		return new UIRenderMode<ResourceLocation>(
				RenderPrimitive.TRIANGLES,
				new VertexFormat()
					.appand("position", NumberFormat.FLOAT, 3, false)
					.appand("uv", NumberFormat.FLOAT, 2, false)
					.appand("color", NumberFormat.FLOAT, 4, false),
				SHADER_TEXTURED_SOLID,
				(shader, container) -> {
					UITextureHandler.ensureSingleTexturesLoaded(container.getActiveTextureLoader(), texture);
					
					shader.getUniform("ProjMat").setMatrix4f(container.getProjectionMatrix());
					shader.getUniform("Texture").setTextureSampler(container.getActiveTextureLoader().getTexture(texture));
					GLStateManager.enable(GL33.GL_DEPTH_TEST);
					GLStateManager.enable(GL33.GL_BLEND);
					GLStateManager.blendFunc(GL33.GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA);
				}
		);
	});
	
	public static UIRenderMode<ResourceLocation> plainClickable() {
		return plainClickable;
	}
	private static final UIRenderMode<ResourceLocation> plainClickable = new UIRenderMode<ResourceLocation>(
			RenderPrimitive.TRIANGLES, 
			new VertexFormat()
				.appand("position", NumberFormat.FLOAT, 3, false)
				.appand("pxpos", NumberFormat.FLOAT, 2, false)
				.appand("pxsize", NumberFormat.INT, 2, false)
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
	
	public static UIRenderMode<ResourceLocation> plainCircle() {
		return plainCircle;
	}
	private static final UIRenderMode<ResourceLocation> plainCircle = new UIRenderMode<ResourceLocation>(
			RenderPrimitive.TRIANGLES,
			new VertexFormat()
				.appand("position", NumberFormat.FLOAT, 3, false)
				.appand("px_pos", NumberFormat.FLOAT, 2, false)
				.appand("px_center", NumberFormat.INT, 2, false)
				.appand("inner_rad", NumberFormat.INT, 1, false)
				.appand("outer_rad", NumberFormat.INT, 1, false)
				.appand("color", NumberFormat.FLOAT, 4, false),
			SHADER_PLAIN_CIRCLE,
			(shader, container) -> {
				shader.getUniform("ProjMat").setMatrix4f(container.getProjectionMatrix());
				GLStateManager.enable(GL33.GL_DEPTH_TEST);
				GLStateManager.enable(GL33.GL_BLEND);
				GLStateManager.blendFunc(GL33.GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA);
			}
	);
	
}
