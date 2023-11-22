package de.m_marvin.openui.flatmono.components;

import java.awt.Color;
import java.awt.Font;

import de.m_marvin.openui.core.UIRenderMode;
import de.m_marvin.openui.core.components.Component;
import de.m_marvin.openui.flatmono.TextRenderer;
import de.m_marvin.renderengine.buffers.defimpl.SimpleBufferSource;
import de.m_marvin.renderengine.fontrendering.FontRenderer;
import de.m_marvin.renderengine.resources.defimpl.ResourceLocation;
import de.m_marvin.renderengine.translation.PoseStack;
import de.m_marvin.univec.impl.Vec2i;

public class LabelComponent extends Component<ResourceLocation> {
	
	protected String title;
	protected Font font = DEFAULT_FONT;
	protected Color textColor;
	
	public LabelComponent(String title, Color textColor) {
		this.title = title;
		this.textColor = textColor;
		
		this.setMargin(5, 5, 5, 5);
		this.setSize(new Vec2i(FontRenderer.calculateStringWidth(title, font), FontRenderer.getFontHeight(font)));
		this.fixSize();
	}
	
	public LabelComponent(String title) {
		this(title, Color.WHITE);
	}
	
	public Font getFont() {
		return font;
	}
	
	public void setFont(Font font) {
		assert font != null : "Argument can not be null!";
		this.font = font;
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
	
	@Override
	public void drawForeground(SimpleBufferSource<ResourceLocation, UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {
		
		TextRenderer.renderTextCentered(this.size.x / 2, this.size.y / 2, this.title, this.font, this.textColor, this.container.getActiveTextureLoader(), bufferSource, matrixStack);
		
	}
	
}
