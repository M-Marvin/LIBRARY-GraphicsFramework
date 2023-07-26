package de.m_marvin.openui.dep;

import java.awt.Color;

import de.m_marvin.univec.impl.Vec2i;

public abstract class UIPlane extends UISizeableElement {
	
	protected Color color;
	
	public UIPlane(Vec2i position, Vec2i size, Color color) {
		super(position, size);
		this.color = color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
	
}
