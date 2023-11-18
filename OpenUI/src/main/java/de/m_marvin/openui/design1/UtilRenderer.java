package de.m_marvin.openui.design1;

import java.awt.Color;

import de.m_marvin.openui.core.UIRenderMode;
import de.m_marvin.renderengine.buffers.BufferBuilder;
import de.m_marvin.renderengine.buffers.IBufferSource;
import de.m_marvin.renderengine.resources.defimpl.ResourcePath;
import de.m_marvin.renderengine.translation.PoseStack;
import de.m_marvin.univec.impl.Vec2i;

public class UtilRenderer {

	public static void renderRectangle(int x, int y, int w, int h, Color color, IBufferSource<UIRenderMode<ResourcePath>> bufferSource, PoseStack matrixStack) {
		matrixStack.push();
		matrixStack.translate(x, y, 0);
		renderRectangle(w, h, color, bufferSource, matrixStack);
		matrixStack.pop();
	}
	
	public static void renderRectangle(int w, int h, Color color, IBufferSource<UIRenderMode<ResourcePath>> bufferSource, PoseStack matrixStack) {

		float r = color.getRed() / 255F;
		float g = color.getGreen() / 255F;
		float b = color.getBlue() / 255F;
		float a = color.getAlpha() / 255F;
		
		float fxl = 0;
		float fxr = w;
		float fyt = 0;
		float fyb = h;
		
		BufferBuilder buffer = bufferSource.startBuffer(UIRenderModes.plainSolid());
		
		// Background
		buffer.vertex(matrixStack, fxl, fyt, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxr, fyt, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxr, fyb, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxr, fyb, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxl, fyb, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxl, fyt, 0).color(r, g, b, a).endVertex();
		
		buffer.end();
		
	}
	
	public static void renderFrame(int x, int y, int w, int h, int frameWidth, Color frameColor, IBufferSource<UIRenderMode<ResourcePath>> bufferSource, PoseStack matrixStack) {
		matrixStack.push();
		matrixStack.translate(x, y, 0);
		renderFrame(w, h, frameWidth, frameColor, bufferSource, matrixStack);
		matrixStack.pop();
	}
	
	public static void renderFrame(int w, int h, int frameWidth, Color frameColor, IBufferSource<UIRenderMode<ResourcePath>> bufferSource, PoseStack matrixStack) {

		float r = frameColor.getRed() / 255F;
		float g = frameColor.getGreen() / 255F;
		float b = frameColor.getBlue() / 255F;
		float a = frameColor.getAlpha() / 255F;
		
		float f = frameWidth;
		float fxol = 0;
		float fxil = f;
		float fxor = w;
		float fxir = w - f;
		float fyot = 0;
		float fyit = f;
		float fyob = h;
		float fyib = h - f;
		
		BufferBuilder buffer = bufferSource.startBuffer(UIRenderModes.plainSolid());
		
		// Top
		buffer.vertex(matrixStack, fxol, fyot, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxor, fyot, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxir, fyit, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxir, fyit, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxil, fyit, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxol, fyot, 0).color(r, g, b, a).endVertex();
		
		// Bottom
		buffer.vertex(matrixStack, fxol, fyob, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxor, fyob, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxir, fyib, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxir, fyib, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxil, fyib, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxol, fyob, 0).color(r, g, b, a).endVertex();
		
		// Left
		buffer.vertex(matrixStack, fxol, fyot, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxil, fyit, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxil, fyib, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxil, fyib, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxol, fyob, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxol, fyot, 0).color(r, g, b, a).endVertex();

		// Right
		buffer.vertex(matrixStack, fxor, fyot, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxir, fyit, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxir, fyib, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxir, fyib, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxor, fyob, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxor, fyot, 0).color(r, g, b, a).endVertex();
		
		buffer.end();
		
	}
	
}
