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

public class PointerDisplayComponent extends Component<ResourceLocation> {
	
	protected Color color;
	protected Color textColor;
	protected Font font = DEFAULT_FONT;
	protected Function<Float, String> titleSupplier;
	protected int minValue;
	protected int maxValue;
	protected float value;
	
	protected String title;
	
	public PointerDisplayComponent(int min, int max, Function<Float, String> titleSupplier, Color color, Color textColor) {
		this.titleSupplier = titleSupplier;
		this.color = color;
		this.textColor = textColor;
		this.maxValue = max;
		this.minValue = min;
		this.value = this.minValue;

		this.title = this.titleSupplier.apply(this.value);
		
		this.setMargin(5, 5, 5, 5);
		this.setSize(new Vec2i(200, 200));
		this.fixSize();
	}
	
	public PointerDisplayComponent(int min, int max, Function<Float, String> titleSupplier, Color color) {
		this(min, max, titleSupplier, color, Color.WHITE);
	}
	
	public PointerDisplayComponent(int min, int max, Function<Float, String> titleSupplier) {
		this(min, max, titleSupplier, Color.WHITE);
	}
	
	public PointerDisplayComponent() {
		this(0, 100, (f) -> String.format("%.0f%%", f));
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
		this.redraw();
	}

	public Color getTextColor() {
		return textColor;
	}
	
	public void setTextColor(Color textColor) {
		this.textColor = textColor;
		this.redraw();
	}
	
	public Font getFont() {
		return font;
	}
	
	public void setFont(Font font) {
		this.font = font;
		this.redraw();
	}
	
	public int getMinValue() {
		return minValue;
	}
	
	public void setMinValue(int minValue) {
		this.minValue = minValue;
		this.redraw();
	}
	
	public float getMaxValue() {
		return maxValue;
	}
	
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
		this.redraw();
	}
	
	public float getValue() {
		return value;
	}
	
	public void setValue(float value) {
		this.value = Math.max(this.minValue, Math.max(this.minValue, value));
		this.title = this.titleSupplier.apply(this.value);
		this.redraw();
	}

	protected static final int FRAME_WIDTH = 2;
	protected static final int FRAME_GAP = 3;
	protected static final int SCALA_LINE_LENGTH_1 = 8;
	protected static final int SCALA_LINE_LENGTH_5 = 16;
	protected static final int SCALA_LINE_LENGTH_10 = 20;
	protected static final int POINTER_WIDTH = 8;
	protected static final int POINTER_FRAME_GAP = 40;
	protected static final int POINTER_TIP_LENGTH = 20;
	
	@Override
	public void drawBackground(SimpleBufferSource<ResourceLocation, UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {
		
		int ro = this.size.x / 2;
		int ri = ro - FRAME_WIDTH;
		
		UtilRenderer.renderCircle(this.size.x, this.size.y, this.size.x / 2, this.size.y / 2, ri, ro, this.color, bufferSource, matrixStack);
		
		shiftRenderLayer();
		
		matrixStack.push();
		matrixStack.translate(this.size.x / 2, this.size.y / 2, 0);
		matrixStack.rotateDegrees(0, 0, 45);
		
		int scalaCount = this.maxValue - this.minValue + 1;
		float angleSteps = 270 / (float) scalaCount;
		
		for (int i = 0; i < scalaCount; i++) {
			
			matrixStack.rotateDegrees(0, 0, angleSteps);
			
			int sl = (i % 10 == 0) ? SCALA_LINE_LENGTH_10 : (i % 5 == 0) ? SCALA_LINE_LENGTH_5 : SCALA_LINE_LENGTH_1;
			
			UtilRenderer.renderRectangle(0, ri - sl - FRAME_GAP, 1, sl, this.textColor, bufferSource, matrixStack);
			
		}
		
		matrixStack.pop();
		
	}
	
	@Override
	public void drawForeground(SimpleBufferSource<ResourceLocation, UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {
		
		int ro = this.size.x / 2;
		int ri = ro - FRAME_WIDTH;
		
		matrixStack.push();
		matrixStack.translate(this.size.x / 2, this.size.y / 2, 0);
		matrixStack.rotateDegrees(0, 0, -135);
		
		matrixStack.rotateDegrees(0, 0, 270 * (this.value - this.minValue) / (float) (this.maxValue - this.minValue));
		
		UtilRenderer.renderRectangle(-POINTER_WIDTH / 2, - ri + POINTER_FRAME_GAP, POINTER_WIDTH, ri + POINTER_WIDTH / 2 - POINTER_FRAME_GAP, this.color, bufferSource, matrixStack);
		UtilRenderer.renderTriangle(-POINTER_WIDTH / 2, -ri + POINTER_FRAME_GAP - POINTER_TIP_LENGTH, POINTER_WIDTH, POINTER_TIP_LENGTH, this.color, bufferSource, matrixStack);
		
		UtilRenderer.renderCircle(-10, -10, 20, 20, 10, 10, 0, 10, this.color, bufferSource, matrixStack);
		
		matrixStack.pop();
		
		TextRenderer.renderTextCentered(this.size.x / 2, this.size.y * 3/4, this.title, this.font, this.textColor, this.getContainer().getActiveTextureLoader(), bufferSource, matrixStack);
		
	}
	
}
