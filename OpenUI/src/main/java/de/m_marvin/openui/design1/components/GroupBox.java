package de.m_marvin.openui.design1.components;

import java.awt.Color;

import de.m_marvin.openui.core.UIRenderMode;
import de.m_marvin.openui.core.components.Compound;
import de.m_marvin.openui.design1.UtilRenderer;
import de.m_marvin.renderengine.buffers.defimpl.SimpleBufferSource;
import de.m_marvin.renderengine.resources.defimpl.ResourcePath;
import de.m_marvin.renderengine.translation.PoseStack;

public class GroupBox extends Compound<ResourcePath> {
	
	protected Color backgroundColor;
	protected Color frameColor;
	protected int frameWidth = 1;
	
	public GroupBox() {
		this(Color.WHITE);
	}
	
	public GroupBox(Color frameColor) {
		this(frameColor, Color.BLACK);
	}
	
	public GroupBox(Color frameColor, Color backgroundColor) {
		this.frameColor = frameColor;
		this.backgroundColor = backgroundColor;
	}
	
	public Color getFrameColor() {
		return frameColor;
	}
	
	public Color getBackgroundColor() {
		return backgroundColor;
	}
	
	public void setFrameWidth(int frameWidth) {
		this.frameWidth = frameWidth;
	}
	
	public int getFrameWidth() {
		return frameWidth;
	}
	
	@Override
	public void drawBackground(SimpleBufferSource<ResourcePath, UIRenderMode<ResourcePath>> bufferSource, PoseStack matrixStack) {
		
		UtilRenderer.renderRectangle(this.size.x, this.size.y, this.backgroundColor, bufferSource, matrixStack);
		
	}
	
	@Override
	public void drawForeground(SimpleBufferSource<ResourcePath, UIRenderMode<ResourcePath>> bufferSource, PoseStack matrixStack) {
		
		UtilRenderer.renderFrame(this.size.x, this.size.y, this.frameWidth, this.frameColor, bufferSource, matrixStack);
		
	}
	
}
