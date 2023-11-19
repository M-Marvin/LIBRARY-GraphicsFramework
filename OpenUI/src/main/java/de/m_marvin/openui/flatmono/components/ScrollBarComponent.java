package de.m_marvin.openui.flatmono.components;

import java.awt.Color;
import java.util.Optional;
import java.util.function.Consumer;

import de.m_marvin.openui.core.UIRenderMode;
import de.m_marvin.openui.core.components.Component;
import de.m_marvin.openui.flatmono.UIRenderModes;
import de.m_marvin.openui.flatmono.UtilRenderer;
import de.m_marvin.renderengine.buffers.BufferBuilder;
import de.m_marvin.renderengine.buffers.defimpl.SimpleBufferSource;
import de.m_marvin.renderengine.resources.defimpl.ResourceLocation;
import de.m_marvin.renderengine.translation.PoseStack;
import de.m_marvin.univec.impl.Vec2d;
import de.m_marvin.univec.impl.Vec2i;

public class ScrollBarComponent extends Component<ResourceLocation> {
	
	public static final int RAIL_WIDTH = 1;
	public static final int BAR_RAIL_GAP = 5;
	
	protected Color color;
	protected int barSize;
	protected int scrollLength;
	protected boolean horizontal;
	protected int barPosition = 0;
	protected int scrollSensitivity = 10;
	protected Consumer<Integer> changeCallback = i -> {};
	
	protected int grabOffset = 0;
	protected boolean pressed = false;
	
	public ScrollBarComponent(boolean horizontal, int scrollLength, int barSize, Color color) {
		assert this.barSize <= this.scrollLength : "Scrollbar can not be longer than the scroll-space!";
		this.color = color;
		this.barSize = barSize;
		this.scrollLength = scrollLength;
		this.horizontal = horizontal;
		
		setMargin(5, 5, 5, 5);
		setSize(horizontal ? new Vec2i(120, 20) : new Vec2i(20, 120));
		fixSize();
	}
	
	public ScrollBarComponent(boolean horizontal, int scrollLength, int barSize) {
		this(horizontal, scrollLength, barSize, Color.WHITE);
	}
	
	public boolean isHorizontal() {
		return horizontal;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		assert color != null : "Argument can not be null!";
		this.color = color;
		this.redraw();
	}
	
	public int getBarSize() {
		return barSize;
	}
	
	public void setBarSize(int barSize) {
		this.barSize = barSize;
		this.redraw();
	}
	
	public int getScrollLength() {
		return scrollLength;
	}
	
	public void setScrollLength(int scrollLength) {
		this.scrollLength = scrollLength;
		this.redraw();
	}
	
	public void setScrollSensitivity(int scrollSensitivity) {
		this.scrollSensitivity = scrollSensitivity;
	}
	
	public int getScrollSensitivity() {
		return scrollSensitivity;
	}
	
	public void setChangeCallback(Consumer<Integer> changeCallback) {
		this.changeCallback = changeCallback;
	}
	
	public Consumer<Integer> getChangeCallback() {
		return changeCallback;
	}
	
	@Override
	protected void mouseEvent(Optional<Vec2d> scroll, int button, boolean pressed, boolean repeated) {
		super.mouseEvent(scroll, button, pressed, repeated);
		
		if (button == 0 && !pressed) {
			
			this.pressed = false;
			this.redraw();
			
		}
	}

	@Override
	protected void onClicked(int button, boolean pressed, boolean repeated) {
		
		if (button == 0) {
			
			if (this.pressed == false && pressed == true) {

				if (!this.horizontal) {
					
					int grabPos = (int) (this.getContainer().getCursorPosition().y - this.getAbsoluteOffset().y);
					int barSize = (int) ((this.barSize / (float) this.scrollLength) * this.size.y);
					int barPos = (int) (this.barPosition / (float) (this.scrollLength - this.barSize) * (this.size.y - barSize));
					
					if (grabPos >= barPos && grabPos <= barPos + barSize) {
						this.grabOffset = grabPos - barPos;
					} else {
						this.grabOffset = barSize / 2;
						this.barPosition = (int) (((grabPos - this.grabOffset) / (float) (this.size.y - barSize)) * (this.scrollLength - this.barSize));
						this.barPosition = Math.min(this.scrollLength - this.barSize, Math.max(0, this.barPosition));
						this.changeCallback.accept(this.barPosition);
					}
					
				} else {
					
					int grabPos = (int) (this.getContainer().getCursorPosition().x - this.getAbsoluteOffset().x);
					int barSize = (int) ((this.barSize / (float) this.scrollLength) * this.size.x);
					int barPos = (int) (this.barPosition / (float) (this.scrollLength - this.barSize) * (this.size.x - barSize));
					
					if (grabPos >= barPos && grabPos <= barPos + barSize) {
						this.grabOffset = grabPos - barPos;
					} else {
						this.grabOffset = barSize / 2;
						this.barPosition = (int) (((grabPos - this.grabOffset) / (float) (this.size.x - barSize)) * (this.scrollLength - this.barSize));
						this.barPosition = Math.min(this.scrollLength - this.barSize, Math.max(0, this.barPosition));
						this.changeCallback.accept(this.barPosition);
					}
					
				}
				
			}
			
			this.pressed = pressed;
			this.redraw();
			
		}
		
	}
	
	@Override
	protected void onScroll(Vec2d scroll) {
		
		if (!this.horizontal) {
			this.barPosition = (int) Math.max(0, Math.min(this.scrollLength - this.barSize, this.barPosition - scroll.y * this.scrollSensitivity));
		} else {
			this.barPosition = (int) Math.max(0, Math.min(this.scrollLength - this.barSize, this.barPosition - scroll.y * this.scrollSensitivity));
		}

		this.redraw();
		this.changeCallback.accept(this.barPosition);
		
	}
	
	@Override
	protected void cursorMove(Vec2d position, boolean entered, boolean leaved) {
		super.cursorMove(position, entered, leaved);
		
		if (this.pressed) {
			
			if (!this.horizontal) {
				
				int grabPos = (int) (position.y - this.getAbsoluteOffset().y);
				int barSize = (int) ((this.barSize / (float) this.scrollLength) * this.size.y);
				
				this.barPosition = (int) (((grabPos - this.grabOffset) / (float) (this.size.y - barSize)) * (this.scrollLength - this.barSize));
				this.barPosition = Math.min(this.scrollLength - this.barSize, Math.max(0, this.barPosition));
				this.changeCallback.accept(this.barPosition);
				this.redraw();
				
			} else {
				
				int grabPos = (int) (position.x - this.getAbsoluteOffset().x);
				int barSize = (int) ((this.barSize / (float) this.scrollLength) * this.size.x);
				
				this.barPosition = (int) (((grabPos - this.grabOffset) / (float) (this.size.x - barSize)) * (this.scrollLength - this.barSize));
				this.barPosition = Math.min(this.scrollLength - this.barSize, Math.max(0, this.barPosition));
				this.changeCallback.accept(this.barPosition);
				this.redraw();
				
			}
			
		}
	}
	
	@Override
	public void drawBackground(SimpleBufferSource<ResourceLocation, UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {
		
		float r = this.color.getRed() / 255F;
		float g = this.color.getGreen() / 255F;
		float b = this.color.getBlue() / 255F;
		float a = this.color.getAlpha() / 255F;

		int p = this.pressed ? 2 : (this.cursorOverComponent ? 1 : 0);
		
		if (!this.horizontal) {

			int barSize = (int) ((this.barSize / (float) this.scrollLength) * this.size.y);
			int barPos = (int) (this.barPosition / (float) (this.scrollLength - this.barSize) * (this.size.y - barSize));
			
			int fxl = 0;
			int fxr = this.size.x;
			int fyt = barPos;
			int fyb = barPos + barSize;
			
			int ryt = 0;
			int rht = Math.max(0, barPos - BAR_RAIL_GAP);
			int ryb = Math.min(this.size.y, barPos + barSize + BAR_RAIL_GAP);
			int rhb = this.size.y - ryb;
			
			UtilRenderer.renderRectangle(0, ryt, RAIL_WIDTH, rht, this.color, bufferSource, matrixStack);
			UtilRenderer.renderRectangle(0, ryb, RAIL_WIDTH, rhb, this.color, bufferSource, matrixStack);

			UtilRenderer.renderRectangle(this.size.x - RAIL_WIDTH, ryt, RAIL_WIDTH, rht, this.color, bufferSource, matrixStack);
			UtilRenderer.renderRectangle(this.size.x - RAIL_WIDTH, ryb, RAIL_WIDTH, rhb, this.color, bufferSource, matrixStack);
			
			BufferBuilder buffer = bufferSource.startBuffer(UIRenderModes.plainClickable());
			
			buffer.vertex(matrixStack, fxl, fyt, 0)	.vec2f(0, 0)					.vec2i(this.size.x, barSize).color(r, g, b, a).putInt(p).nextElement().endVertex();
			buffer.vertex(matrixStack, fxr, fyt, 0)	.vec2f(this.size.x, 0)			.vec2i(this.size.x, barSize).color(r, g, b, a).putInt(p).nextElement().endVertex();
			buffer.vertex(matrixStack, fxr, fyb, 0)	.vec2f(this.size.x, barSize)	.vec2i(this.size.x, barSize).color(r, g, b, a).putInt(p).nextElement().endVertex();
			buffer.vertex(matrixStack, fxr, fyb, 0)	.vec2f(this.size.x, barSize)	.vec2i(this.size.x, barSize).color(r, g, b, a).putInt(p).nextElement().endVertex();
			buffer.vertex(matrixStack, fxl, fyb, 0)	.vec2f(0, barSize)				.vec2i(this.size.x, barSize).color(r, g, b, a).putInt(p).nextElement().endVertex();
			buffer.vertex(matrixStack, fxl, fyt, 0)	.vec2f(0, 0)					.vec2i(this.size.x, barSize).color(r, g, b, a).putInt(p).nextElement().endVertex();
			
			buffer.end();
			
		} else {

			int barSize = (int) ((this.barSize / (float) this.scrollLength) * this.size.x);
			int barPos = (int) (this.barPosition / (float) (this.scrollLength - this.barSize) * (this.size.x - barSize));
			
			int fxl = barPos;
			int fxr = barPos + barSize;
			int fyt = 0;
			int fyb = this.size.y;
			
			int rxr = Math.min(this.size.x, barPos + barSize + BAR_RAIL_GAP);
			int rwr = this.size.x - rxr;
			int rxl = 0;
			int rwl = Math.max(0, barPos - BAR_RAIL_GAP);
			
			UtilRenderer.renderRectangle(rxl, 0, rwl, RAIL_WIDTH, this.color, bufferSource, matrixStack);
			UtilRenderer.renderRectangle(rxr, 0, rwr, RAIL_WIDTH, this.color, bufferSource, matrixStack);

			UtilRenderer.renderRectangle(rxl, this.size.y - RAIL_WIDTH, rwl, RAIL_WIDTH, this.color, bufferSource, matrixStack);
			UtilRenderer.renderRectangle(rxr, this.size.y - RAIL_WIDTH, rwr, RAIL_WIDTH, this.color, bufferSource, matrixStack);
			
			BufferBuilder buffer = bufferSource.startBuffer(UIRenderModes.plainClickable());
			
			buffer.vertex(matrixStack, fxl, fyt, 0)	.vec2f(0, 0)					.vec2i(barSize, this.size.y).color(r, g, b, a).putInt(p).nextElement().endVertex();
			buffer.vertex(matrixStack, fxr, fyt, 0)	.vec2f(barSize, 0)				.vec2i(barSize, this.size.y).color(r, g, b, a).putInt(p).nextElement().endVertex();
			buffer.vertex(matrixStack, fxr, fyb, 0)	.vec2f(barSize, this.size.y)	.vec2i(barSize, this.size.y).color(r, g, b, a).putInt(p).nextElement().endVertex();
			buffer.vertex(matrixStack, fxr, fyb, 0)	.vec2f(barSize, this.size.y)	.vec2i(barSize, this.size.y).color(r, g, b, a).putInt(p).nextElement().endVertex();
			buffer.vertex(matrixStack, fxl, fyb, 0)	.vec2f(0, this.size.y)			.vec2i(barSize, this.size.y).color(r, g, b, a).putInt(p).nextElement().endVertex();
			buffer.vertex(matrixStack, fxl, fyt, 0)	.vec2f(0, 0)					.vec2i(barSize, this.size.y).color(r, g, b, a).putInt(p).nextElement().endVertex();
			
			buffer.end();
			
		}
		
		
	}
	
}
