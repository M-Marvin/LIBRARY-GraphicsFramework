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
	protected float marginLeft;
	protected float marginRight;
	protected float marginUp;
	protected float marginDown;
	
	protected UIContainer<R> container;
	protected Layout<?> layout;
	protected LayoutData layoutData;
	protected List<Compound<R>> childComponents;
	protected boolean needsRedraw;
	
	public Compound() {
		this.sizeMin = new Vec2i(-1, -1);
		this.sizeMax = new Vec2i(-1, -1);
		this.size = new Vec2i(30, 30);
		this.offset = new Vec2i(0, 0);
		this.marginLeft = 5;
		this.marginRight = 5;
		this.marginUp = 5;
		this.marginDown = 5;
		this.layout = null;
		this.childComponents = new ArrayList<>();
		this.needsRedraw = true;
	}
	
	public void updateLayout() {
		if (this.layout != null) this.layout.rearange(this, this.childComponents);
		this.redraw();
		for (Compound<R> c : this.childComponents) c.updateLayout();
	}
	
	public void redraw() {
		this.needsRedraw = true;
	}
	
	protected void setContainer(UIContainer<R> container) {
		this.container = container;
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
	
	public List<Compound<R>> getChildComponents() {
		return childComponents;
	}
	
	public void setSizeMax(Vec2i sizeMax) {
		this.sizeMax = sizeMax;
	}
	
	public Vec2i getSizeMax() {
		return sizeMax;
	}
	
	public void setSizeMin(Vec2i sizeMin) {
		this.sizeMin = sizeMin;
	}
	
	public Vec2i getSizeMin() {
		return sizeMin;
	}
	
	public void setOffset(Vec2i offset) {
		this.offset = offset;
	}
	
	public Vec2i getOffset() {
		return offset;
	}
	
	public void setSize(Vec2i size) {
		this.size = size;
	}
	
	public Vec2i getSize() {
		return size;
	}
	
	public void setMargin(float marginLeft, float marginRight, float marginUp, float marginDown) {
		this.marginLeft = marginLeft;
		this.marginRight = marginRight;
		this.marginUp = marginUp;
		this.marginDown = marginDown;
	}
	
	public float getMarginLeft() {
		return marginLeft;
	}
	
	public float getMarginRight() {
		return marginRight;
	}
	
	public float getMarginUp() {
		return marginUp;
	}
	
	public float getMarginDown() {
		return marginDown;
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
