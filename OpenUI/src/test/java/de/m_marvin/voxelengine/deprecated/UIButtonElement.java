package de.m_marvin.voxelengine.deprecated;

import java.awt.Color;

import de.m_marvin.univec.impl.Vec2i;

public abstract class UIButtonElement extends UISizeableElement {
	
	protected String title;
	protected Color colorBack;
	protected Color colorFront;
	
	public UIButtonElement(Vec2i position, Vec2i size, String title, Color colorBack, Color colorFront) {
		super(position, size);
		this.title = title;
		this.colorBack = colorBack;
		this.colorFront = colorFront;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setColorBack(Color colorBack) {
		this.colorBack = colorBack;
	}
	
	public void setColorFront(Color colorFront) {
		this.colorFront = colorFront;
	}
	
	public Color getColorBack() {
		return colorBack;
	}
	
	public Color getColorFront() {
		return colorFront;
	}
	
}
