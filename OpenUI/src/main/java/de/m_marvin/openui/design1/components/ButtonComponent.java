package de.m_marvin.openui.design1.components;

import java.awt.Color;

import de.m_marvin.openui.core.UIRenderMode;
import de.m_marvin.openui.core.components.Component;
import de.m_marvin.openui.design1.UIRenderModes;
import de.m_marvin.renderengine.buffers.BufferBuilder;
import de.m_marvin.renderengine.buffers.defimpl.SimpleBufferSource;
import de.m_marvin.renderengine.resources.defimpl.ResourcePath;
import de.m_marvin.renderengine.translation.PoseStack;
import de.m_marvin.univec.impl.Vec2i;

public class ButtonComponent extends Component<ResourcePath> {
	
	public static final Color BUTTON_COLOR_GRAY = new Color(0, 0, 0);
	 
	protected Color color;
	protected boolean pressed = false;
	
	public ButtonComponent(Color color) {
		this.marginLeft = this.marginRight = this.marginTop = this.marginBottom = 5;
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
	
	@Override
	public void onClicked(int button, boolean pressed, boolean repeated) {
		if (button == 0) {
			if (this.pressed == true && pressed == false) {
				System.out.println("Clicked");
			}
			this.pressed = pressed;
			this.redraw();
		}
	}
	
	@Override
	public void onCursorMoveOver(Vec2i position, boolean leaved) {
		if (leaved) {
			this.pressed = false;
			this.redraw();
		}
	}
	
	@Override
	public void drawBackground(SimpleBufferSource<ResourcePath, UIRenderMode<ResourcePath>> bufferSource, PoseStack matrixStack) {
		
		BufferBuilder buffer = bufferSource.startBuffer(UIRenderModes.clickableHoverable(new ResourcePath("ui/test")));
		
		matrixStack.push();
		matrixStack.translate(this.offset.x, this.offset.y, 0);
		
		float r = this.color.getRed() / 255F;
		float g = this.color.getGreen() / 255F;
		float b = this.color.getBlue() / 255F;
		float a = this.color.getAlpha() / 255F;
		byte p = (byte) (this.pressed ? 1 : 0);
		
		buffer.vertex(matrixStack, this.size.x, 0, 0)			.uv(1, 0).color(r, g, b, a).putByte(p).nextElement().endVertex();
		buffer.vertex(matrixStack, 0, 0, 0)						.uv(0, 0).color(r, g, b, a).putByte(p).nextElement().endVertex();
		buffer.vertex(matrixStack, this.size.x, this.size.y, 0)	.uv(1, 1).color(r, g, b, a).putByte(p).nextElement().endVertex();
		buffer.vertex(matrixStack, this.size.x, this.size.y, 0)	.uv(1, 1).color(r, g, b, a).putByte(p).nextElement().endVertex();
		buffer.vertex(matrixStack, 0, 0, 0)						.uv(0, 0).color(r, g, b, a).putByte(p).nextElement().endVertex();
		buffer.vertex(matrixStack, 0, this.size.y, 0)			.uv(0, 1).color(r, g, b, a).putByte(p).nextElement().endVertex();
		
		buffer.end();
		
		matrixStack.pop();
		
	}
	
}
