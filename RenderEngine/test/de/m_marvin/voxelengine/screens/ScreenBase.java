package de.m_marvin.voxelengine.screens;

import de.m_marvin.openui.IScreenAligner;
import de.m_marvin.openui.ScreenUI;
import de.m_marvin.renderengine.buffers.BufferBuilder;
import de.m_marvin.renderengine.translation.PoseStack;
import de.m_marvin.univec.impl.Vec2i;

public abstract class ScreenBase extends ScreenUI {

	public ScreenBase(Vec2i size, IScreenAligner aligment) {
		super(size, aligment);
		// TODO Auto-generated constructor stub
	}
	
	public void drawRectangle(BufferBuilder buffer, PoseStack poseStack, float topLeftX, float topLeftY, float width, float height, float r, float g, float b, float a) {
		float lx = topLeftX;
		float ty = topLeftY;
		float rx = lx + width;
		float by = ty + height;
		buffer.vertex(poseStack, lx, ty).color(r, g, b, a).uv(0, 0).endVertex();
		buffer.vertex(poseStack, rx, ty).color(r, g, b, a).uv(0, 0).endVertex();
		buffer.vertex(poseStack, rx, by).color(r, g, b, a).uv(0, 0).endVertex();
		buffer.vertex(poseStack, lx, by).color(r, g, b, a).uv(0, 0).endVertex();
	}

	public void drawRectangleTransitionH(BufferBuilder buffer, PoseStack poseStack, float topLeftX, float topLeftY, float width, float height, float r1, float g1, float b1, float a1, float r2, float g2, float b2, float a2) {
		float lx = topLeftX;
		float ty = topLeftY;
		float rx = lx + width;
		float by = ty + height;
		buffer.vertex(poseStack, lx, ty).color(r2, g2, b2, a2).uv(0, 0).endVertex();
		buffer.vertex(poseStack, rx, ty).color(r2, g2, b2, a2).uv(0, 0).endVertex();
		buffer.vertex(poseStack, rx, by).color(r1, g1, b1, a1).uv(0, 0).endVertex();
		buffer.vertex(poseStack, lx, by).color(r1, g1, b1, a1).uv(0, 0).endVertex();
	}

	public void drawRectangleTransitionV(BufferBuilder buffer, PoseStack poseStack, float topLeftX, float topLeftY, float width, float height, float r1, float g1, float b1, float a1, float r2, float g2, float b2, float a2) {
		float lx = topLeftX;
		float ty = topLeftY;
		float rx = lx + width;
		float by = ty + height;
		buffer.vertex(poseStack, lx, ty).color(r1, g1, b1, a1).uv(0, 0).endVertex();
		buffer.vertex(poseStack, rx, ty).color(r2, g2, b2, a2).uv(0, 0).endVertex();
		buffer.vertex(poseStack, rx, by).color(r2, g2, b2, a2).uv(0, 0).endVertex();
		buffer.vertex(poseStack, lx, by).color(r1, g1, b1, a1).uv(0, 0).endVertex();
	}
	
}
