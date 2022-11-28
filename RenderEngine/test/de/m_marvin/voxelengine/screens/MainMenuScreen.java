package de.m_marvin.voxelengine.screens;

import java.awt.Color;

import de.m_marvin.openui.ScreenUI;
import de.m_marvin.openui.elements.UIButtonElement;
import de.m_marvin.univec.impl.Vec2i;
import de.m_marvin.voxelengine.screens.elements.UIButton;

public class MainMenuScreen extends ScreenUI {
	
	protected UIButtonElement buttonTest;
	
	public MainMenuScreen() {
		super(new Vec2i(200, 100));
		this.buttonTest = addElement(new UIButton(new Vec2i(0, 0), new Vec2i(200, 100), "TEST", new Color(128, 128, 128, 128), new Color(255, 255, 255, 255)));
		
	}
	
	@Override
	public void onOpen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClose() {
		// TODO Auto-generated method stub
		
	}

}
