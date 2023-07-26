package de.m_marvin.voxelengine.screens.elements;

import java.awt.Color;

import de.m_marvin.openui.dep.UIPlane;
import de.m_marvin.renderengine.buffers.BufferBuilder;
import de.m_marvin.renderengine.translation.PoseStack;
import de.m_marvin.univec.impl.Vec2i;
import de.m_marvin.voxelengine.VoxelEngine;
import de.m_marvin.voxelengine.rendering.BufferSource;
import de.m_marvin.voxelengine.rendering.RenderType;

public class UISquarePlane extends UIPlane {

	public UISquarePlane(Vec2i position, Vec2i size, Color color) {
		super(position, size, color);
	}

	@Override
	public void draw(PoseStack poseStack) {
		float xl = position.x;
		float yl = position.y;
		float xh = xl + size.x;
		float yh = yl + size.y;
		float r = color.getRed() / 255F;
		float g = color.getGreen() / 255F;
		float b = color.getBlue() / 255F;
		float a = color.getAlpha() / 255F;
		
		BufferSource bufferSource = VoxelEngine.getInstance().getGameRenderer().getBufferSource();
		BufferBuilder buffer = bufferSource.startBuffer(RenderType.screen());
		
		buffer.vertex(poseStack, xl, yl).color(r, g, b, a).uv(0, 0).endVertex();
		buffer.vertex(poseStack, xh, yl).color(r, g, b, a).uv(1, 0).endVertex();
		buffer.vertex(poseStack, xh, yh).color(r, g, b, a).uv(1, 1).endVertex();
		buffer.vertex(poseStack, xl, yh).color(r, g, b, a).uv(0, 1).endVertex();
		buffer.end();
	}

}
