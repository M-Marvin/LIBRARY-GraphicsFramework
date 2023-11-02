package de.m_marvin.openui.components;

import de.m_marvin.openui.rendering.UIRenderModes;
import de.m_marvin.renderengine.buffers.BufferBuilder;
import de.m_marvin.renderengine.buffers.defimpl.RenderMode;
import de.m_marvin.renderengine.buffers.defimpl.SimpleBufferSource;
import de.m_marvin.renderengine.resources.IResourceProvider;
import de.m_marvin.renderengine.resources.defimpl.ResourceLocation;
import de.m_marvin.renderengine.translation.PoseStack;

public class GroupBox<R extends IResourceProvider<R>> extends Compound<R> {
	
	@Override
	public void drawBackground(SimpleBufferSource<R> bufferSource, PoseStack matrixStack) {

		@SuppressWarnings("unchecked")
		BufferBuilder buffer = bufferSource.startBuffer((RenderMode<R>) UIRenderModes.solidPlane(new ResourceLocation("uitest:openui/solidPlane"), new ResourceLocation("example:glsl")));
		
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
