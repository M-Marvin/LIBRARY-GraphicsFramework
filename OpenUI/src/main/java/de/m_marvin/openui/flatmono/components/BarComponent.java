package de.m_marvin.openui.flatmono.components;

import java.awt.Color;
import java.awt.Font;
import java.util.function.Function;

import de.m_marvin.openui.core.UIRenderMode;
import de.m_marvin.openui.core.components.Component;
import de.m_marvin.openui.flatmono.TextRenderer;
import de.m_marvin.openui.flatmono.UtilRenderer;
import de.m_marvin.renderengine.buffers.defimpl.SimpleBufferSource;
import de.m_marvin.renderengine.resources.defimpl.ResourceLocation;
import de.m_marvin.renderengine.translation.PoseStack;
import de.m_marvin.univec.impl.Vec2i;

public class BarComponent extends Component<ResourceLocation> {
	
	public static final int FRAME_WIDTH = 1;
	public static final int BAR_FRAME_GAP = 3;
	
	protected Color color;
	protected Color textColor;
	protected Font font = DEFAULT_FONT;
	protected Function<Integer, String> titleSupplier;
	protected int minValue;
	protected int maxValue;
	protected int value;
	protected boolean horizontal;
	
	protected String title;
	
	public BarComponent(boolean horizontal, int min, int max, Function<Integer, String> titleSupplier, Color color, Color textColor) {
		this.titleSupplier = titleSupplier;
		this.color = color;
		this.textColor = textColor;
		this.minValue = min;
		this.maxValue = max;
		this.value = minValue;
		this.horizontal = horizontal;

		this.title = this.titleSupplier.apply(this.value);
		
		setMargin(5, 5, 5, 5);
		setSize(horizontal ? new Vec2i(120, 20) : new Vec2i(20, 120));
		fixSize();
	}
	
	public BarComponent(boolean horizontal, int min, int max, Function<Integer, String> titleSupplier, Color color) {
		this(horizontal, min, max, titleSupplier, color, Color.GRAY);
	}

	public BarComponent(boolean horizontal, int min, int max, Function<Integer, String> titleSupplier) {
		this(horizontal, min, max, titleSupplier, Color.WHITE);
	}
	
	public BarComponent(boolean horizontal) {
		this(horizontal, 0, 100, p -> String.valueOf(p) + "%");
	}

	public BarComponent() {
		this(true);
	}

	public boolean isHorizontal() {
		return horizontal;
	}
	
	public Font getFont() {
		return font;
	}
	
	public void setFont(Font font) {
		assert font != null : "Argument can not be null!";
		this.font = font;
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
	
	public Color getTextColor() {
		return textColor;
	}
	
	public void setTextColor(Color textColor) {
		assert textColor != null : "Argument can not be null!";
		this.textColor = textColor;
		this.redraw();
	}
	
	public Function<Integer, String> getTitleSupplier() {
		return titleSupplier;
	}
	
	public void setTitleSupplier(Function<Integer, String> titleSupplier) {
		assert titleSupplier != null : "Argument can not be null!";
		this.titleSupplier = titleSupplier;
		this.redraw();
	}
	
	public int getMinValue() {
		return minValue;
	}
	
	public void setMinValue(int minValue) {
		this.minValue = minValue;
		this.redraw();
	}
	
	public int getMaxValue() {
		return maxValue;
	}
	
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
		this.redraw();
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = Math.max(this.minValue, Math.min(this.maxValue, value));
		this.title = this.titleSupplier.apply(this.value);
		this.redraw();
	}
	
	@Override
	public void drawBackground(SimpleBufferSource<ResourceLocation, UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {

		if (!this.horizontal) {

			UtilRenderer.renderRectangle(0, 0, FRAME_WIDTH, this.size.y, this.color, bufferSource, matrixStack);
			UtilRenderer.renderRectangle(this.size.x - FRAME_WIDTH, 0, FRAME_WIDTH, this.size.y, this.color, bufferSource, matrixStack);
			
			int barHeight = (int) (((this.value - this.minValue) / (float) (this.maxValue - this.minValue)) * (this.size.y - BAR_FRAME_GAP * 2));
			int barOffset = this.size.y - BAR_FRAME_GAP - barHeight;
			
			UtilRenderer.renderRectangle(BAR_FRAME_GAP, barOffset, this.size.x - BAR_FRAME_GAP * 2, barHeight, this.color, bufferSource, matrixStack);
			
		} else {

			UtilRenderer.renderRectangle(0, 0, this.size.x, FRAME_WIDTH, this.color, bufferSource, matrixStack);
			UtilRenderer.renderRectangle(0, this.size.y - FRAME_WIDTH, this.size.x, FRAME_WIDTH, this.color, bufferSource, matrixStack);
			
			int barWidth = (int) (((this.value - this.minValue) / (float) (this.maxValue - this.minValue)) * (this.size.x - BAR_FRAME_GAP * 2));
			
			UtilRenderer.renderRectangle(BAR_FRAME_GAP, BAR_FRAME_GAP, barWidth, this.size.y - BAR_FRAME_GAP * 2, this.color, bufferSource, matrixStack);
			
		}
		
	}
	
	@Override
	public void drawForeground(SimpleBufferSource<ResourceLocation, UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {
		
		if (this.title != null) {
			
			matrixStack.push();
			matrixStack.translate(this.size.x / 2, this.size.y / 2, 0F);
			if (!this.horizontal) matrixStack.rotateDegrees(0, 0, 90);
			
			TextRenderer.renderTextCentered(0, 0, this.title, this.font, this.textColor, this.container.getActiveTextureLoader(), bufferSource, matrixStack);
			
			matrixStack.pop();
			
		}
		
	}
	
}
