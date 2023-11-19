package de.m_marvin.openui.flatmono;

import de.m_marvin.openui.core.window.UIWindow;
import de.m_marvin.renderengine.resources.defimpl.ResourceLocation;

public abstract class Window extends UIWindow<ResourceLocation, UIResourceFolders> {

	public Window(String windowName) {
		super(UIResourceFolders.SHADERS, UIResourceFolders.TEXTURES, windowName);
	}
	
}
