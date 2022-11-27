package de.m_marvin.openui.elements;

import de.m_marvin.univec.impl.Vec2i;

public abstract class UISizeableElement extends UIPositionableElement {
	
	protected Vec2i size;

	public UISizeableElement(Vec2i position, Vec2i size) {
		super(position);
		this.size = size;
	}
	
	public void setSize(Vec2i size) {
		this.size = size;
	}
	
	public Vec2i getSize() {
		return size;
	}
	
}
