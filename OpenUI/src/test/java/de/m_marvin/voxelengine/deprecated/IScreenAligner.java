package de.m_marvin.voxelengine.deprecated;

import de.m_marvin.univec.impl.Vec2f;
import de.m_marvin.univec.impl.Vec2i;

@FunctionalInterface
public interface IScreenAligner {
	
	public Vec2f getOffset(Vec2i screenSize, Vec2f windowSize);
	
}
