package de.m_marvin.voxelengine.screens;

import de.m_marvin.renderengine.buffers.BufferBuilder;
import de.m_marvin.renderengine.translation.PoseStack;
import de.m_marvin.univec.impl.Vec2i;
import de.m_marvin.voxelengine.VoxelEngine;
import de.m_marvin.voxelengine.deprecated.ScreenAligment;
import de.m_marvin.voxelengine.deprecated.ScreenUI;
import de.m_marvin.voxelengine.deprecated.UIButtonElement;
import de.m_marvin.voxelengine.rendering.RenderType;

public class MainMenuScreen extends ScreenUI {
	
	protected UIButtonElement buttonTest;
	
	public MainMenuScreen() {
		super(new Vec2i(100, 200), ScreenAligment.TOP_LEFT);
		
		//this.buttonTest = addElement(new UIButton(new Vec2i(0, 0), new Vec2i(100, 100), "TEST", UIButton.BUTTON_COLOR_BLACK, UIButton.TEXT_COLOR_WHITE));
		
	}
	
	@Override
	public void drawAdditionalContent(PoseStack poseStack, float partialTick) {
		
		float lx = 0;
		float by = -this.windowSize.y + this.size.y;
		float rx1 = 80;
		float rx2 = 100;
		float ty = size.y;
		
		BufferBuilder buffer = VoxelEngine.getInstance().getGameRenderer().getBufferSource().startBuffer(RenderType.screen());
		buffer.vertex(poseStack, lx, by).color(0, 0, 0, 0.6F).uv(0, 0).endVertex();
		buffer.vertex(poseStack, rx1, by).color(0, 0, 0, 0.6F).uv(0, 0).endVertex();
		buffer.vertex(poseStack, rx1, ty).color(0, 0, 0, 0.6F).uv(0, 0).endVertex();
		buffer.vertex(poseStack, lx, ty).color(0, 0, 0, 0.6F).uv(0, 0).endVertex();
		
		buffer.vertex(poseStack, rx1, by).color(0, 0, 0, 0.6F).uv(0, 0).endVertex();
		buffer.vertex(poseStack, rx2, by).color(0, 0, 0, 0.0F).uv(0, 0).endVertex();
		buffer.vertex(poseStack, rx2, ty).color(0, 0, 0, 0.0F).uv(0, 0).endVertex();
		buffer.vertex(poseStack, rx1, ty).color(0, 0, 0, 0.6F).uv(0, 0).endVertex();
		buffer.end();
	}
	
}
