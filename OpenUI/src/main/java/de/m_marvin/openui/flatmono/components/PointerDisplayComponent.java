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
	protected Font titleFont = DEFAULT_FONT;
	protected Font font = DEFAULT_FONT;
	protected Function<Float, String> titleSupplier;
	protected int scalaNumberScale = 1;
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

		this.title = this.titleSupplier.apply(this.value * this.scalaNumberScale);
		
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
	
	public Font getFont() {
		return font;
	}
	
	public void setFont(Font font) {
		assert font != null : "Argument can not be null!";
		this.font = font;
		this.redraw();
	}
	
	public Font getTitleFont() {
		return titleFont;
	}
	
	public void setTitleFont(Font titleFont) {
		assert titleFont != null : "Argument can not be null!";
		this.titleFont = titleFont;
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
	
	public int getScalaNumberScale() {
		return scalaNumberScale;
	}
	
	public void setScalaNumberScale(int scalaNumberScale) {
		this.scalaNumberScale = scalaNumberScale;
		this.title = this.titleSupplier.apply(this.value * this.scalaNumberScale);
		this.redraw();
	}
	
	public float getValue() {
		return value;
	}
	
	public void setValue(float value) {
		this.value = Math.max(this.minValue, Math.min(this.maxValue, value));
		this.title = this.titleSupplier.apply(this.value * this.scalaNumberScale);
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
		
		int scalaCount = this.maxValue - this.minValue;
		float angleSteps = 270 / (float) scalaCount;
		
		for (int i = 0; i <= scalaCount; i++) {
			
			int value = this.minValue + i;
			int sl = (value % 10 == 0) ? SCALA_LINE_LENGTH_10 : (value % 5 == 0) ? SCALA_LINE_LENGTH_5 : SCALA_LINE_LENGTH_1;
			
			UtilRenderer.renderRectangle(0, ri - sl - FRAME_GAP, 1, sl, this.textColor, bufferSource, matrixStack);

			matrixStack.rotateDegrees(0, 0, angleSteps);
			
		}
		
		matrixStack.pop();
		
	}
	
	@Override
	public void drawForeground(SimpleBufferSource<ResourceLocation, UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {
		
		int ro = this.size.x / 2;
		int ri = ro - FRAME_GAP;
		int rt = ri - FRAME_GAP - SCALA_LINE_LENGTH_10 - this.font.getSize();
		
		int scalaCount = this.maxValue - this.minValue;
		float angleSteps = 270 / (float) scalaCount;
		
		for (int i = 0; i <= scalaCount; i++) {
			
			int value = this.minValue + i;
			
			if (value % 10 != 0) continue;
			
			int x = (int) (Math.cos(Math.toRadians(i * angleSteps + 137)) * rt) + this.size.x / 2;
			int y = (int) (Math.sin(Math.toRadians(i * angleSteps + 137)) * rt) + this.size.y / 2;
			
			TextRenderer.renderTextCentered(x, y, String.valueOf(value * this.scalaNumberScale), this.font, this.textColor, this.container.getActiveTextureLoader(), bufferSource, matrixStack);
			
		}

		TextRenderer.renderTextCentered(this.size.x / 2, this.size.y * 7/8, this.title, this.titleFont, this.textColor, this.getContainer().getActiveTextureLoader(), bufferSource, matrixStack);
		
		shiftRenderLayer();
		
		matrixStack.push();
		matrixStack.translate(this.size.x / 2, this.size.y / 2, 0);
		matrixStack.rotateDegrees(0, 0, -135);
		
		matrixStack.rotateDegrees(0, 0, 270 * (this.value - this.minValue) / (float) (this.maxValue - this.minValue));
		
		UtilRenderer.renderRectangle(-POINTER_WIDTH / 2, -ri + POINTER_FRAME_GAP, POINTER_WIDTH, ri + POINTER_WIDTH / 2 - POINTER_FRAME_GAP, this.color, bufferSource, matrixStack);
		UtilRenderer.renderTriangle(-POINTER_WIDTH / 2, -ri + POINTER_FRAME_GAP - POINTER_TIP_LENGTH, POINTER_WIDTH, POINTER_TIP_LENGTH, this.color, bufferSource, matrixStack);
		
		UtilRenderer.renderCircle(-10, -10, 20, 20, 10, 10, 0, 10, this.color, bufferSource, matrixStack);
		
		matrixStack.pop();
		
	}
	
}
