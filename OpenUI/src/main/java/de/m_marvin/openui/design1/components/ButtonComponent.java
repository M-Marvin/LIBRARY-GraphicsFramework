package de.m_marvin.openui.design1.components;

import java.awt.Color;
import java.awt.Font;

import de.m_marvin.openui.core.UIRenderMode;
import de.m_marvin.openui.core.components.Component;
import de.m_marvin.openui.design1.TextRenderer;
import de.m_marvin.openui.design1.UIRenderModes;
import de.m_marvin.renderengine.buffers.BufferBuilder;
import de.m_marvin.renderengine.buffers.defimpl.SimpleBufferSource;
import de.m_marvin.renderengine.resources.defimpl.ResourcePath;
import de.m_marvin.renderengine.translation.PoseStack;
import de.m_marvin.univec.impl.Vec2i;

public class ButtonComponent extends Component<ResourcePath> {
	
	protected Color color;
	protected Color textColor;
	protected String title = null;
	
	protected boolean pressed = false;
	
	public ButtonComponent(String title, Color color, Color invertColor) {
		this.marginLeft = this.marginRight = this.marginTop = this.marginBottom = 5;
		this.color = color;
		this.textColor = invertColor;
		this.title = title;
	}

	public ButtonComponent(String title, Color color) {
		this(title, color, Color.BLACK);
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
		this.redraw();
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
		this.redraw();
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
	
	public static final Font FONT = new Font("arial", Font.PLAIN, 16);
	
	@Override
	public void drawBackground(SimpleBufferSource<ResourcePath, UIRenderMode<ResourcePath>> bufferSource, PoseStack matrixStack) {
		
		matrixStack.push();
		
		float r = this.color.getRed() / 255F;
		float g = this.color.getGreen() / 255F;
		float b = this.color.getBlue() / 255F;
		float a = this.color.getAlpha() / 255F;
		byte p = (byte) (this.pressed ? 2 : (this.cursorOverComponent ? 1 : 0));
		
		BufferBuilder buffer = bufferSource.startBuffer(UIRenderModes.plainClickable());
		buffer.vertex(matrixStack, this.size.x, 0, 0)			.vec2f(this.size.x, 0)				.vec2i(this.size.x, this.size.y).color(r, g, b, a).putInt(p).nextElement().endVertex();
		buffer.vertex(matrixStack, 0, 0, 0)						.vec2f(0, 0)						.vec2i(this.size.x, this.size.y).color(r, g, b, a).putInt(p).nextElement().endVertex();
		buffer.vertex(matrixStack, this.size.x, this.size.y, 0)	.vec2f(this.size.x, this.size.y)	.vec2i(this.size.x, this.size.y).color(r, g, b, a).putInt(p).nextElement().endVertex();
		buffer.vertex(matrixStack, this.size.x, this.size.y, 0)	.vec2f(this.size.x, this.size.y)	.vec2i(this.size.x, this.size.y).color(r, g, b, a).putInt(p).nextElement().endVertex();
		buffer.vertex(matrixStack, 0, 0, 0)						.vec2f(0, 0)						.vec2i(this.size.x, this.size.y).color(r, g, b, a).putInt(p).nextElement().endVertex();
		buffer.vertex(matrixStack, 0, this.size.y, 0)			.vec2f(0, this.size.y)				.vec2i(this.size.x, this.size.y).color(r, g, b, a).putInt(p).nextElement().endVertex();
		
		buffer.end();
		
		matrixStack.pop();
		
	}
	
	@Override
	public void drawForeground(SimpleBufferSource<ResourcePath, UIRenderMode<ResourcePath>> bufferSource, PoseStack matrixStack) {
		
		if (this.title != null) {
			
			TextRenderer.renderTextCentered(this.size.x / 2, this.size.y / 2, title, FONT, this.pressed ? this.color : this.textColor, container.getActiveTexureLoader(), bufferSource, matrixStack);
			
		}
		
	}
	
}