package de.m_marvin.voxelengine.deprecated;

import de.m_marvin.univec.impl.Vec2i;

public abstract class UIPositionableElement implements IUIElement {
	
	protected Vec2i position;
	
	public UIPositionableElement(Vec2i position) {
		this.position = position;
	}
	
	public void setPosition(Vec2i position) {
		this.position = position;
	}
	
	public Vec2i getPosition() {
		return this.position;
	}
	
}
