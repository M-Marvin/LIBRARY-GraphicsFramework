package de.m_marvin.openui.components;

import java.util.ArrayList;
import java.util.List;

import de.m_marvin.openui.UIContainer;
import de.m_marvin.openui.layout.Layout;
import de.m_marvin.openui.layout.Layout.LayoutData;
import de.m_marvin.renderengine.buffers.defimpl.SimpleBufferSource;
import de.m_marvin.renderengine.resources.IResourceProvider;
import de.m_marvin.renderengine.translation.PoseStack;
import de.m_marvin.univec.impl.Vec2i;

public class Compound<R extends IResourceProvider<R>> {
	
	protected Vec2i sizeMin;
	protected Vec2i sizeMax;
	protected Vec2i size;
	protected Vec2i offset;
	protected int marginLeft;
	protected int marginRight;
	protected int marginTop;
	protected int marginBottom;
	
	protected UIContainer<R> container;
	protected Layout<?> layout;
	protected LayoutData layoutData;
	protected List<Compound<R>> childComponents;
	protected boolean needsRedraw;
	
	public Compound() {
		this.sizeMin = new Vec2i(20, 20);
		this.sizeMax = new Vec2i(1000, 1000);
		this.size = new Vec2i(30, 30);
		this.offset = new Vec2i(0, 0);
		this.marginLeft = 0;
		this.marginRight = 0;
		this.marginTop = 0;
		this.marginBottom = 0;
		this.layout = null;
		this.childComponents = new ArrayList<>();
		this.needsRedraw = true;
	}
	
	public void updateLayout() {
		for (Compound<R> c : this.childComponents) c.updateLayout();
		if (this.layout != null) {
			this.layout.rearange(this, this.childComponents);
		} else {
			this.size = this.sizeMin;
		}
		this.redraw();
	}
	
	public void redraw() {
		this.needsRedraw = true;
	}
	
	protected void setContainer(UIContainer<R> container) {
		this.container = container;
		for (Compound<R> c : this.childComponents) c.setContainer(container);
	}
	
	public void addComponent(Compound<R> childComponent) {
		this.childComponents.add(childComponent);
		childComponent.setContainer(container);
	}
	
	public void removeComponent(Compound<R> childComponent) {
		this.container.deleteVAOs(childComponent);
		childComponent.setContainer(null);
		this.childComponents.remove(childComponent);
	}
	
	public void setLayout(Layout<?> layout) {
		this.layout = layout;
	}
	
	public Layout<?> getLayout() {
		return layout;
	}
	
	public void setLayoutData(LayoutData layoutData) {
		this.layoutData = layoutData;
	}
	
	public LayoutData getLayoutData() {
		return layoutData;
	}
	
	public <D extends LayoutData, T extends Layout<D>> D getLayoutData(T layout) {
		if (this.layoutData != null && this.layoutData.getClass() == layout.getDataClass()) {
			return layout.getDataClass().cast(this.layoutData);
		}
		return null;
	}
	
	public List<Compound<R>> getChildComponents() {
		return childComponents;
	}
	
	public void setSizeMax(Vec2i sizeMax) {
		this.sizeMax = sizeMax;
		this.sizeMax.minI(sizeMax);
	}
	
	public Vec2i getSizeMax() {
		return sizeMax;
	}
	
	public Vec2i getSizeMaxMargin() {
		return sizeMax.add(new Vec2i(marginLeft + marginRight, marginTop + marginBottom));
	}
	
	public void setSizeMin(Vec2i sizeMin) {
		this.sizeMin = sizeMin;
		this.sizeMax.maxI(sizeMin);
	}
	
	public Vec2i getSizeMin() {
		return sizeMin;
	}

	public Vec2i getSizeMinMargin() {
		return sizeMin.add(new Vec2i(marginLeft + marginRight, marginTop + marginBottom));
	}
	
	public void setOffset(Vec2i offset) {
		this.offset = offset;
	}

	public void setOffsetMargin(Vec2i offset) {
		this.offset = offset.add(new Vec2i(marginLeft, marginTop));
	}
	
	public Vec2i getOffset() {
		return offset;
	}
	
	public void setSize(Vec2i size) {
		this.size = size;
	}

	public void setSizeMargin(Vec2i size) {
		setSize(size.sub(new Vec2i(marginLeft + marginRight, marginTop + marginBottom)));
	}
	
	public void fixSize() {
		setSizeMin(getSize());
		setSizeMax(getSize());
	}
	
	public Vec2i getSize() {
		return size;
	}

	public Vec2i getSizeMargin() {
		return size.add(new Vec2i(marginLeft + marginRight, marginTop + marginBottom));
	}
	
	public void setMargin(int marginLeft, int marginRight, int marginUp, int marginDown) {
		this.marginLeft = marginLeft;
		this.marginRight = marginRight;
		this.marginTop = marginUp;
		this.marginBottom = marginDown;
	}
	
	public float getMarginLeft() {
		return marginLeft;
	}
	
	public float getMarginRight() {
		return marginRight;
	}
	
	public float getMarginUp() {
		return marginTop;
	}
	
	public float getMarginDown() {
		return marginBottom;
	}
	
	public void drawBackground(SimpleBufferSource<R> bufferSource, PoseStack matrixStack) {}
	public void drawForeground(SimpleBufferSource<R> bufferSource, PoseStack matrixStack) {}
	
	public void updateOutdatedVAOs(UIContainer<R> container) {
		if (this.needsRedraw) {
			this.needsRedraw = false;
			container.updateVAOs(this);
		}
		for (Compound<R> c : this.childComponents) c.updateOutdatedVAOs(container);
	}
	
}
