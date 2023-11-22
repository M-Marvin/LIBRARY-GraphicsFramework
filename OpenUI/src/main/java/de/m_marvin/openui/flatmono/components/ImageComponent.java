package de.m_marvin.openui.flatmono.components;

import java.awt.Color;

import de.m_marvin.openui.core.UIRenderMode;
import de.m_marvin.openui.core.components.Component;
import de.m_marvin.openui.flatmono.UtilRenderer;
import de.m_marvin.renderengine.buffers.defimpl.SimpleBufferSource;
import de.m_marvin.renderengine.resources.defimpl.ResourceLocation;
import de.m_marvin.renderengine.translation.PoseStack;

public class ImageComponent extends Component<ResourceLocation> {
	
	protected ResourceLocation imageLocation;
	protected Color color;
	protected Color backgroundColor;
	protected ImageAdjust imageAdjust;
	
	public static enum ImageAdjust {
		CENTERED,LEFT_TOP,STRETCHED;
	}
	
	public ImageComponent(ResourceLocation image, ImageAdjust adjust, Color color, Color backgroundColor) {
		this.imageLocation = image;
		this.imageAdjust = adjust;
		this.color = color;
		this.backgroundColor = backgroundColor;
		
		this.setMargin(5, 5, 5, 5);
	}
	
	public ImageComponent(ResourceLocation image, ImageAdjust adjust, Color color) {
		this(image, adjust, color, Color.BLACK);
	}
	
	public ImageComponent(ResourceLocation image, ImageAdjust adjust) {
		this(image, adjust, Color.WHITE);
	}
	
	public ImageComponent(ResourceLocation image) {
		this(image, ImageAdjust.CENTERED);
	}
	
	public ResourceLocation getImageLocation() {
		return imageLocation;
	}
	
	public void setImageLocation(ResourceLocation imageLocation) {
		this.imageLocation = imageLocation;
		this.redraw();
	}
	
	public ImageAdjust getImageAdjust() {
		return imageAdjust;
	}
	
	public void setImageAdjust(ImageAdjust imageAdjust) {
		assert imageAdjust != null : "Argument can not be null!";
		this.imageAdjust = imageAdjust;
		this.redraw();
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		assert color != null : "Argument can not be null!";
		this.color = color;
		this.redraw();
	}
	
	public Color getBackgroundColor() {
		return backgroundColor;
	}
	
	public void setBackgroundColor(Color backgroundColor) {
		assert backgroundColor != null : "Argument can not be null!";
		this.backgroundColor = backgroundColor;
		this.redraw();
	}
	
	@Override
	public void drawBackground(SimpleBufferSource<ResourceLocation, UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {
		
		UtilRenderer.renderRectangle(this.size.x, this.size.y, this.backgroundColor, bufferSource, matrixStack);
		
	}
	
	@Override
	public void drawForeground(SimpleBufferSource<ResourceLocation, UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {

		// TODO Adjustment
		
		UtilRenderer.renderTexture(this.size.x, this.size.y, this.imageLocation, this.color, bufferSource, matrixStack);
		
	}
	
}
