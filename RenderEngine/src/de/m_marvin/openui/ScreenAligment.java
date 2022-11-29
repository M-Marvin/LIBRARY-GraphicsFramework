package de.m_marvin.openui;

import de.m_marvin.univec.impl.Vec2f;
import de.m_marvin.univec.impl.Vec2i;

public enum ScreenAligment implements IScreenAligner {
	
	LEFT((screenSize, windowSize) -> windowSize.sub(screenSize).div(2F)),
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
