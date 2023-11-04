package de.m_marvin.openui.design1;

import de.m_marvin.openui.core.window.UIWindow;
import de.m_marvin.renderengine.resources.defimpl.ResourcePath;

public abstract class Window extends UIWindow<ResourcePath, ResourceFolders> {

	public Window(String windowName) {
		super(ResourceFolders.SHADERS, ResourceFolders.TEXTURES, windowName);
	}
	
}
