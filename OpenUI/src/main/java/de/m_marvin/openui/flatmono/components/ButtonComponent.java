package de.m_marvin.openui.flatmono.components;

import java.awt.Color;
import java.awt.Font;

import de.m_marvin.openui.core.UIRenderMode;
import de.m_marvin.openui.core.components.Component;
import de.m_marvin.openui.flatmono.TextRenderer;
import de.m_marvin.openui.flatmono.UtilRenderer;
import de.m_marvin.renderengine.buffers.defimpl.SimpleBufferSource;
import de.m_marvin.renderengine.fontrendering.FontRenderer;
import de.m_marvin.renderengine.resources.defimpl.ResourceLocation;
import de.m_marvin.renderengine.translation.PoseStack;
import de.m_marvin.univec.impl.Vec2i;

public class ButtonComponent extends Component<ResourceLocation> {
	
	protected Color color;
	protected Color textColor;
	protected String title;
	protected Font font = DEFAULT_FONT;
	protected Runnable action = () -> {};
	
	protected boolean pressed = false;
	
	public ButtonComponent(String title, Color color, Color invertColor) {
		this.color = color;
		this.textColor = invertColor;
		this.title = title;
		
		setMargin(5, 5, 5, 5);
		setSize(new Vec2i(80, FontRenderer.getFontHeight(this.font) + 2));
		fixSize();
	}

	public ButtonComponent(String title, Color color) {
		this(title, color, Color.BLACK);
	}

	public ButtonComponent(String title) {
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
	
	public void setAction(Runnable action) {
		assert action != null : "Argument can not be null!";
		this.action = action;
	}
	
	public Runnable getAction() {
		return action;
	}
	
	@Override
	protected void onClicked(int button, boolean pressed, boolean repeated) {
		if (button == 0) {
			if (this.pressed == true && pressed == false) {
				this.action.run();
			}
			this.pressed = pressed;
			this.redraw();
		}
	}
	
	@Override
	protected void onCursorMoveOver(Vec2i position, boolean leaved) {
		if (leaved) {
			this.pressed = false;
			this.redraw();
		}
	}
	
	@Override
	public void drawBackground(SimpleBufferSource<ResourceLocation, UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {
		
		int p = this.pressed ? 2 : (this.cursorOverComponent ? 1 : 0);
		
		UtilRenderer.renderClickableRectangle(this.size.x, this.size.y, 0, 0, this.size.x, this.size.y, p, this.color, bufferSource, matrixStack);
		
	}
	
	@Override
	public void drawForeground(SimpleBufferSource<ResourceLocation, UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {
		
		if (this.title != null) {
			
			String title = FontRenderer.limitStringWidth(this.title, this.font, this.size.x - 2);
			TextRenderer.renderTextCentered(this.size.x / 2, this.size.y / 2, title, this.font, this.pressed ? this.color : this.textColor, container.getActiveTextureLoader(), bufferSource, matrixStack);
			
		}
		
	}
	
}
