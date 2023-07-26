package de.m_marvin.voxelengine.screens;

import de.m_marvin.openui.dep.IScreenAligner;
import de.m_marvin.openui.dep.ScreenUI;
import de.m_marvin.univec.impl.Vec2i;

public abstract class ScreenBase extends ScreenUI {

	public ScreenBase(Vec2i size, IScreenAligner aligment) {
		super(size, aligment);
	}
	
	
	// TODO Currently not used, just a base class of all ui's
	
}
