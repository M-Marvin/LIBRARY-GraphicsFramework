package de.m_marvin.voxelengine.deprecated;

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

	@Override
	public boolean isOverElement(Vec2i coursorPosition) {
		return	coursorPosition.x >= this.position.x &&
				coursorPosition.y >= this.position.y &&
				coursorPosition.x < this.position.x + this.size.x &&
				coursorPosition.y < this.position.y + this.size.y;
	}
	
}
