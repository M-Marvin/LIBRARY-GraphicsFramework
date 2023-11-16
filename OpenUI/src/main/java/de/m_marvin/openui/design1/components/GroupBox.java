package de.m_marvin.openui.design1.components;

import java.awt.Color;

import de.m_marvin.openui.core.UIRenderMode;
import de.m_marvin.openui.core.components.Compound;
import de.m_marvin.openui.design1.UIRenderModes;
import de.m_marvin.renderengine.buffers.BufferBuilder;
import de.m_marvin.renderengine.buffers.defimpl.SimpleBufferSource;
import de.m_marvin.renderengine.resources.defimpl.ResourcePath;
import de.m_marvin.renderengine.translation.PoseStack;

public class GroupBox extends Compound<ResourcePath> {
	
	protected Color color;
	
	public GroupBox(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
	
	@Override
	public void drawBackground(SimpleBufferSource<ResourcePath, UIRenderMode<ResourcePath>> bufferSource, PoseStack matrixStack) {
		
		BufferBuilder buffer = bufferSource.startBuffer(UIRenderModes.plainSolid());
		
		matrixStack.push();
		matrixStack.translate(this.offset.x, this.offset.y, 0);

		float r = this.color.getRed() / 255F;
		float g = this.color.getGreen() / 255F;
		float b = this.color.getBlue() / 255F;
		float a = this.color.getAlpha() / 255F;
		
		buffer.vertex(matrixStack, this.size.x, 0, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, 0, 0, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, this.size.x, this.size.y, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, this.size.x, this.size.y, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, 0, 0, 0).color(r, g, b, a).endVertex();
		buffer.vertex(matrixStack, 0, this.size.y, 0).color(r, g, b, a).endVertex();
		buffer.end();
		
		matrixStack.pop();
		
		this.redraw();
		
	}
	
}
