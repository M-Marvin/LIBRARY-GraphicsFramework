package de.m_marvin.openui;

import de.m_marvin.univec.impl.Vec2f;
import de.m_marvin.univec.impl.Vec2i;

public enum ScreenAligment implements IScreenAligner {
	
	LEFT((screenSize, windowSize) -> new Vec2f(-(windowSize.x - screenSize.x) / 2F, 0F)),
	RIGHT((screenSize, windowSize) -> new Vec2f(+(windowSize.x - screenSize.x) / 2F, 0F)),
	TOP((screenSize, windowSize) -> new Vec2f(0, +(windowSize.y - screenSize.y) / 2F)),
	BOTTOM((screenSize, windowSize) -> new Vec2f(0, -(windowSize.y - screenSize.y) / 2F)),
	
	TOP_LEFT((screenSize, windowSize) -> new Vec2f(-(windowSize.x - screenSize.x) / 2F, +(windowSize.y - screenSize.y) / 2F)),
	TOP_RIGHT((screenSize, windowSize) -> new Vec2f(+(windowSize.x - screenSize.x) / 2F, +(windowSize.y - screenSize.y) / 2F)),
	BOTTOM_LEFT((screenSize, windowSize) -> new Vec2f(-(windowSize.x - screenSize.x) / 2F, -(windowSize.y - screenSize.y) / 2F)),
	BOTTOM_RIGHT((screenSize, windowSize) -> new Vec2f(+(windowSize.x - screenSize.x) / 2F, -(windowSize.y - screenSize.y) / 2F)),
	
	CENTERED((screenSize, windowSize) -> new Vec2f(0F, 0F));
	
	private final IScreenAligner offsetSupplier;
	
	private ScreenAligment(IScreenAligner offsetSupplier) {
		this.offsetSupplier = offsetSupplier;
	}
	
	@Override
	public Vec2f getOffset(Vec2i screenSize, Vec2f windowSize) {
		return this.offsetSupplier.getOffset(screenSize, windowSize);
	}
	
}
