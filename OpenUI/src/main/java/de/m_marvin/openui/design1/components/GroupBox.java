package de.m_marvin.openui.design1.components;

import de.m_marvin.openui.core.components.Compound;
import de.m_marvin.openui.design1.UIRenderModes;
import de.m_marvin.renderengine.buffers.BufferBuilder;
import de.m_marvin.renderengine.buffers.defimpl.SimpleBufferSource;
import de.m_marvin.renderengine.resources.defimpl.ResourcePath;
import de.m_marvin.renderengine.translation.PoseStack;

public class GroupBox extends Compound<ResourcePath> {
	
	@Override
	public void drawBackground(SimpleBufferSource<ResourcePath> bufferSource, PoseStack matrixStack) {
		
		BufferBuilder buffer = bufferSource.startBuffer(UIRenderModes.solidPlane(UIRenderModes.UI_SHADER_LOCATION));
		
		matrixStack.push();
		matrixStack.translate(this.offset.x, this.offset.y, 0);
		
		float r = 1;
		float g = 1;
		float b = 1;
		float a = 0.5F;
		
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
