package de.m_marvin.openui.design1.components;

import java.awt.Color;
import java.awt.Font;
import java.util.function.Consumer;

import de.m_marvin.openui.core.UIRenderMode;
import de.m_marvin.openui.core.components.Component;
import de.m_marvin.openui.design1.TextRenderer;
import de.m_marvin.openui.design1.UIRenderModes;
import de.m_marvin.renderengine.buffers.BufferBuilder;
import de.m_marvin.renderengine.buffers.defimpl.SimpleBufferSource;
import de.m_marvin.renderengine.fontrendering.FontRenderer;
import de.m_marvin.renderengine.resources.defimpl.ResourcePath;
import de.m_marvin.renderengine.translation.PoseStack;
import de.m_marvin.univec.impl.Vec2i;

public class ToggleButtonComponent extends Component<ResourcePath> {
	
	protected static final int TOGGLE_WIDTH = 5;
	protected static final int BUTTON_MARGIN = 10;
	
	protected Color color;
	protected Color textColor;
	protected String title;
	protected Font font = new Font("arial", Font.BOLD, 16);
	protected Consumer<Boolean> toggleAction = (state) -> {};
	
	protected boolean state = false;
	protected boolean pressed = false;
	
	public ToggleButtonComponent(String title, Color color, Color invertColor) {
		this.color = color;
		this.textColor = invertColor;
		this.title = title;
		
		setMargin(5, 5, 5, 5);
		setSize(new Vec2i(80, FontRenderer.getFontHeight(this.font) + 2));
		fixSize();
	}

	public ToggleButtonComponent(String title, Color color) {
		this(title, color, Color.BLACK);
	}

	public ToggleButtonComponent(String title) {
		this(title, Color.WHITE);
	}

	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		assert color != null : "Argument can not be null!";
		this.color = color;
		this.redraw();
	}
	
	public Color getTextColor() {
		return textColor;
	}
	
	public void setTextColor(Color textColor) {
		assert textColor != null : "Argument can not be null!";
		this.textColor = textColor;
		this.redraw();
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
		this.redraw();
	}
	
	public Font getFont() {
		return font;
	}
	
	public void setFont(Font font) {
		assert font != null : "Argument can not be null!";
		this.font = font;
		this.redraw();
	}
	
	public void setAction(Consumer<Boolean> toggleAction) {
		assert toggleAction != null : "Argument can not be null!";
		this.toggleAction = toggleAction;
	}
	
	public Consumer<Boolean> getAction() {
		return toggleAction;
	}
	
	public void setState(boolean state) {
		this.state = state;
	}
	
	public boolean getState() {
		return this.state;
	}
	
	@Override
	public void onClicked(int button, boolean pressed, boolean repeated) {
		if (button == 0) {
			if (this.pressed == true && pressed == false) {
				this.state = !this.state;
				this.toggleAction.accept(this.state);
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
		
		matrixStack.push();
		
		float r = this.color.getRed() / 255F;
		float g = this.color.getGreen() / 255F;
		float b = this.color.getBlue() / 255F;
		float a = this.color.getAlpha() / 255F;
		byte pb = (byte) (this.pressed ? 2 : (this.cursorOverComponent ? 1 : 0));
		byte pa = (byte) (!this.state ? 2 : (this.cursorOverComponent ? 1 : 0));
		
		float fla = TOGGLE_WIDTH;
		float flb = BUTTON_MARGIN;
		float frb = this.size.x - BUTTON_MARGIN;
		float fra = this.size.x - TOGGLE_WIDTH;
		
		BufferBuilder buffer = bufferSource.startBuffer(UIRenderModes.plainClickable());
		
		buffer.vertex(matrixStack, frb, 0, 0)						.vec2f(frb, 0)						.vec2i(this.size.x, this.size.y).color(r, g, b, a).putInt(pb).nextElement().endVertex();
		buffer.vertex(matrixStack, flb, 0, 0)						.vec2f(flb, 0)						.vec2i(this.size.x, this.size.y).color(r, g, b, a).putInt(pb).nextElement().endVertex();
		buffer.vertex(matrixStack, frb, this.size.y, 0)				.vec2f(frb, this.size.y)			.vec2i(this.size.x, this.size.y).color(r, g, b, a).putInt(pb).nextElement().endVertex();
		buffer.vertex(matrixStack, frb, this.size.y, 0)				.vec2f(frb, this.size.y)			.vec2i(this.size.x, this.size.y).color(r, g, b, a).putInt(pb).nextElement().endVertex();
		buffer.vertex(matrixStack, flb, 0, 0)						.vec2f(flb, 0)						.vec2i(this.size.x, this.size.y).color(r, g, b, a).putInt(pb).nextElement().endVertex();
		buffer.vertex(matrixStack, flb, this.size.y, 0)				.vec2f(flb, this.size.y)			.vec2i(this.size.x, this.size.y).color(r, g, b, a).putInt(pb).nextElement().endVertex();
		
		buffer.vertex(matrixStack, fla, 0, 0)						.vec2f(fla, 0)						.vec2i(this.size.x, this.size.y).color(r, g, b, a).putInt(pa).nextElement().endVertex();
		buffer.vertex(matrixStack, 0, 0, 0)							.vec2f(0, 0)						.vec2i(this.size.x, this.size.y).color(r, g, b, a).putInt(pa).nextElement().endVertex();
		buffer.vertex(matrixStack, fla, this.size.y, 0)				.vec2f(fla, this.size.y)			.vec2i(this.size.x, this.size.y).color(r, g, b, a).putInt(pa).nextElement().endVertex();
		buffer.vertex(matrixStack, fla, this.size.y, 0)				.vec2f(fla, this.size.y)			.vec2i(this.size.x, this.size.y).color(r, g, b, a).putInt(pa).nextElement().endVertex();
		buffer.vertex(matrixStack, 0, 0, 0)							.vec2f(0, 0)						.vec2i(this.size.x, this.size.y).color(r, g, b, a).putInt(pa).nextElement().endVertex();
		buffer.vertex(matrixStack, 0, this.size.y, 0)				.vec2f(0, this.size.y)				.vec2i(this.size.x, this.size.y).color(r, g, b, a).putInt(pa).nextElement().endVertex();

		buffer.vertex(matrixStack, this.size.x, 0, 0)				.vec2f(this.size.x, 0)				.vec2i(this.size.x, this.size.y).color(r, g, b, a).putInt(pa).nextElement().endVertex();
		buffer.vertex(matrixStack, fra, 0, 0)						.vec2f(fra, 0)						.vec2i(this.size.x, this.size.y).color(r, g, b, a).putInt(pa).nextElement().endVertex();
		buffer.vertex(matrixStack, this.size.x, this.size.y, 0)		.vec2f(this.size.x, this.size.y)	.vec2i(this.size.x, this.size.y).color(r, g, b, a).putInt(pa).nextElement().endVertex();
		buffer.vertex(matrixStack, this.size.x, this.size.y, 0)		.vec2f(this.size.x, this.size.y)	.vec2i(this.size.x, this.size.y).color(r, g, b, a).putInt(pa).nextElement().endVertex();
		buffer.vertex(matrixStack, fra, 0, 0)						.vec2f(fra, 0)						.vec2i(this.size.x, this.size.y).color(r, g, b, a).putInt(pa).nextElement().endVertex();
		buffer.vertex(matrixStack, fra, this.size.y, 0)				.vec2f(fra, this.size.y)			.vec2i(this.size.x, this.size.y).color(r, g, b, a).putInt(pa).nextElement().endVertex();
		
		buffer.end();
		
		matrixStack.pop();
		
	}
	
	@Override
	public void drawForeground(SimpleBufferSource<ResourcePath, UIRenderMode<ResourcePath>> bufferSource, PoseStack matrixStack) {
		
		if (this.title != null) {
			
			String title = FontRenderer.limitStringWidth(this.title, this.font, this.size.x - BUTTON_MARGIN * 2 - 2);
			TextRenderer.renderTextCentered(this.size.x / 2, this.size.y / 2, title, this.font, this.pressed ? this.color : this.textColor, container.getActiveTexureLoader(), bufferSource, matrixStack);
			
		}
		
	}
	
}
