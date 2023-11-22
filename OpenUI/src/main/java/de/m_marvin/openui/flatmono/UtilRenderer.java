package de.m_marvin.openui.flatmono;

import java.awt.Color;

import de.m_marvin.openui.core.UIRenderMode;
import de.m_marvin.renderengine.buffers.BufferBuilder;
import de.m_marvin.renderengine.buffers.IBufferSource;
import de.m_marvin.renderengine.buffers.defimpl.SimpleBufferSource;
import de.m_marvin.renderengine.resources.defimpl.ResourceLocation;
import de.m_marvin.renderengine.translation.PoseStack;

public class UtilRenderer {
	
	public static void renderRectangle(int x, int y, int w, int h, Color color, IBufferSource<UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {
		matrixStack.push();
		matrixStack.translate(x, y, 0);
		renderRectangle(w, h, color, bufferSource, matrixStack);
		matrixStack.pop();
	}
	
	public static void renderRectangle(int w, int h, Color color, IBufferSource<UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {

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
	
	public static void renderFrame(int x, int y, int w, int h, int frameWidth, Color frameColor, IBufferSource<UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {
		matrixStack.push();
		matrixStack.translate(x, y, 0);
		renderFrame(w, h, frameWidth, frameColor, bufferSource, matrixStack);
		matrixStack.pop();
	}
	
	public static void renderFrame(int w, int h, int frameWidth, Color frameColor, IBufferSource<UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {

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
	
	public static void renderTriangleFrame(int x, int y, int w, int h, int frameWidth, Color frameColor, IBufferSource<UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {
		matrixStack.push();
		matrixStack.translate(x, y, 0);
		renderTriangleFrame(w, h, frameWidth, frameColor, bufferSource, matrixStack);
		matrixStack.pop();
	}
	
	public static void renderTriangleFrame(int w, int h, int frameWidth, Color frameColor, IBufferSource<UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {

		float r = frameColor.getRed() / 255F;
		float g = frameColor.getGreen() / 255F;
		float b = frameColor.getBlue() / 255F;
		float a = frameColor.getAlpha() / 255F;
		
		double angle = Math.atan(h / (w / 2));
		
		float f = frameWidth;
		float fxm = w / 2;
		float fyit = f / (float) Math.cos(angle);
		float fxol = 0;
		float fxil = f + f / (float) Math.tan(angle);
		float fxor = w;
		float fxir = w - f - f / (float) Math.tan(angle);
		float fyot = 0;
		float fyob = h;
		float fyib = h - f;
		
		BufferBuilder buffer = bufferSource.startBuffer(UIRenderModes.plainSolid());
		
		// Bottom
		buffer.vertex(matrixStack, fxol, fyob, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxor, fyob, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxir, fyib, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxir, fyib, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxil, fyib, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxol, fyob, 0).color(r, g, b, a).endVertex();
		
		// Left
		buffer.vertex(matrixStack, fxm, fyot, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxm, fyit, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxil, fyib, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxil, fyib, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxol, fyob, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxm, fyot, 0).color(r, g, b, a).endVertex();

		// Right
		buffer.vertex(matrixStack, fxm, fyot, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxm, fyit, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxir, fyib, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxir, fyib, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxor, fyob, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxm, fyot, 0).color(r, g, b, a).endVertex();
		
		buffer.end();
		
	}

	public static void renderTriangle(int x, int y, int w, int h, Color frameColor, IBufferSource<UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {
		matrixStack.push();
		matrixStack.translate(x, y, 0);
		renderTriangle(w, h, frameColor, bufferSource, matrixStack);
		matrixStack.pop();
	}
	
	public static void renderTriangle(int w, int h, Color frameColor, IBufferSource<UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {

		float r = frameColor.getRed() / 255F;
		float g = frameColor.getGreen() / 255F;
		float b = frameColor.getBlue() / 255F;
		float a = frameColor.getAlpha() / 255F;
		
		float fxm = w / 2;
		float fxl = 0;
		float fxr = w;
		float fyt = 0;
		float fyb = h;
		
		BufferBuilder buffer = bufferSource.startBuffer(UIRenderModes.plainSolid());
		
		buffer.vertex(matrixStack, fxr, fyb, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxl, fyb, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxm, fyt, 0).color(r, g, b, a).endVertex();
		
		buffer.end();
		
	}
	
	public static void renderClickableRectangle(int x, int y, int w, int h, int pxx, int pxy, int pxw, int pxh, int p, Color color, IBufferSource<UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {
		matrixStack.push();
		matrixStack.translate(x, y, 0);
		renderClickableRectangle(w, h, pxx, pxy, pxw, pxh, p, color, bufferSource, matrixStack);
		matrixStack.pop();
	}
	
	public static void renderClickableRectangle(int w, int h, int pxx, int pxy, int pxw, int pxh, int p, Color color, IBufferSource<UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {

		float r = color.getRed() / 255F;
		float g = color.getGreen() / 255F;
		float b = color.getBlue() / 255F;
		float a = color.getAlpha() / 255F;
		
		BufferBuilder buffer = bufferSource.startBuffer(UIRenderModes.plainClickable());
		
		buffer.vertex(matrixStack, w, 0, 0).vec2f(pxx + w, pxy + 0).vec2i(pxw, pxh).color(r, g, b, a).putInt(p).nextElement().endVertex();
		buffer.vertex(matrixStack, 0, 0, 0).vec2f(pxx + 0, pxy + 0).vec2i(pxw, pxh).color(r, g, b, a).putInt(p).nextElement().endVertex();
		buffer.vertex(matrixStack, w, h, 0).vec2f(pxx + w, pxy + h).vec2i(pxw, pxh).color(r, g, b, a).putInt(p).nextElement().endVertex();
		buffer.vertex(matrixStack, w, h, 0).vec2f(pxx + w, pxy + h).vec2i(pxw, pxh).color(r, g, b, a).putInt(p).nextElement().endVertex();
		buffer.vertex(matrixStack, 0, 0, 0).vec2f(pxx + 0, pxy + 0).vec2i(pxw, pxh).color(r, g, b, a).putInt(p).nextElement().endVertex();
		buffer.vertex(matrixStack, 0, h, 0).vec2f(pxx + 0, pxy + h).vec2i(pxw, pxh).color(r, g, b, a).putInt(p).nextElement().endVertex();
		
		buffer.end();
		
	}
	
	public static void renderCircle(int x, int y, int w, int h, int pxcx, int pxcy, int pxri, int pxro, Color color, IBufferSource<UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {
		matrixStack.push();
		matrixStack.translate(x, y, 0);
		renderCircle(w, h, pxcx, pxcy, pxri, pxro, color, bufferSource, matrixStack);
		matrixStack.pop();
	}
	
	public static void renderCircle(int w, int h, int pxcx, int pxcy, int pxri, int pxro, Color color, IBufferSource<UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {

		float r = color.getRed() / 255F;
		float g = color.getGreen() / 255F;
		float b = color.getBlue() / 255F;
		float a = color.getAlpha() / 255F;
		
		BufferBuilder buffer = bufferSource.startBuffer(UIRenderModes.plainCircle());
		
		buffer.vertex(matrixStack, 0, 0, 0).vec2f(0, 0).vec2i(pxcx, pxcy).putInt(pxri).nextElement().putInt(pxro).nextElement().color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, w, 0, 0).vec2f(w, 0).vec2i(pxcx, pxcy).putInt(pxri).nextElement().putInt(pxro).nextElement().color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, w, h, 0).vec2f(w, h).vec2i(pxcx, pxcy).putInt(pxri).nextElement().putInt(pxro).nextElement().color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, w, h, 0).vec2f(w, h).vec2i(pxcx, pxcy).putInt(pxri).nextElement().putInt(pxro).nextElement().color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, 0, h, 0).vec2f(0, h).vec2i(pxcx, pxcy).putInt(pxri).nextElement().putInt(pxro).nextElement().color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, 0, 0, 0).vec2f(0, 0).vec2i(pxcx, pxcy).putInt(pxri).nextElement().putInt(pxro).nextElement().color(r, g, b, a).endVertex();
		
		buffer.end();
		
	}

	public static void renderTexture(int x, int y, int w, int h, ResourceLocation texture, Color color, SimpleBufferSource<ResourceLocation, UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {
		matrixStack.push();
		matrixStack.translate(x, y, 0);
		renderTexture(w, h, texture, color, bufferSource, matrixStack);
		matrixStack.pop();
	}
	
	public static void renderTexture(int w, int h, ResourceLocation texture, Color color, SimpleBufferSource<ResourceLocation, UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {

		float r = color.getRed() / 255F;
		float g = color.getGreen() / 255F;
		float b = color.getBlue() / 255F;
		float a = color.getAlpha() / 255F;
		
		float fxl = 0;
		float fxr = w;
		float fyt = 0;
		float fyb = h;
		
		BufferBuilder buffer = bufferSource.startBuffer(UIRenderModes.texturedSolid(texture));
		
		buffer.vertex(matrixStack, fxl, fyt, 0).uv(0, 1).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxr, fyt, 0).uv(1, 1).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxr, fyb, 0).uv(1, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxr, fyb, 0).uv(1, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxl, fyb, 0).uv(0, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, fxl, fyt, 0).uv(0, 1).color(r, g, b, a).endVertex();
		
		buffer.end();
		
	}
	
}
