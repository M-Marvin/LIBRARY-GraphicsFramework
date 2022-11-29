package de.m_marvin.voxelengine.screens;

import java.awt.Color;

import de.m_marvin.openui.ScreenAligment;
import de.m_marvin.openui.ScreenUI;
import de.m_marvin.openui.elements.UIButtonElement;
import de.m_marvin.univec.impl.Vec2i;
import de.m_marvin.voxelengine.screens.elements.UIButton;
import de.m_marvin.voxelengine.screens.elements.UISquarePlane;

public class MainMenuScreen extends ScreenUI {
	
	protected UIButtonElement buttonTest;
	
	public MainMenuScreen() {
		super(new Vec2i(300, 200), ScreenAligment.LEFT);
		
		addElement(new UISquarePlane(new Vec2i(0, 0), new Vec2i(300, 200), new Color(128, 128, 128, 150)));
		
		this.buttonTest = addElement(new UIButton(new Vec2i(0, 0), new Vec2i(100, 200), "TEST", UIButton.BUTTON_COLOR_BLACK, UIButton.TEXT_COLOR_WHITE));
		
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
