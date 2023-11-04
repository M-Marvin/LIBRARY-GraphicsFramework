package de.m_marvin.uitest;

import java.awt.Color;

import de.m_marvin.openui.core.components.Compound;
import de.m_marvin.openui.core.layout.BorderLayout;
import de.m_marvin.openui.core.layout.BorderLayout.BorderSection;
import de.m_marvin.openui.core.layout.BorderLayout.CornerStretch;
import de.m_marvin.openui.design1.Window;
import de.m_marvin.openui.design1.components.ButtonComponent;
import de.m_marvin.openui.core.layout.GridLayout;
import de.m_marvin.renderengine.resources.defimpl.ResourcePath;
import de.m_marvin.univec.impl.Vec2i;

public class TestWindow extends Window {
	
	public TestWindow() {
		super("Test Window");
	}
	
	@Override
	protected void initUI() {
		this.uiContainer.getRootCompound().setLayout(new BorderLayout(CornerStretch.VERTICAL));
		
		Compound<ResourcePath> b = new ButtonComponent(Color.CYAN);
		b.setSizeMin(new Vec2i(100, 100));
		b.setSizeMax(new Vec2i(150, 0));
		b.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.LEFT));
		
		ButtonComponent b1 = new ButtonComponent(Color.RED);
		b1.setSizeMin(new Vec2i(100, 100));
		b1.setSizeMax(new Vec2i(0, 150));
		b1.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.TOP));
		
		ButtonComponent b2 = new ButtonComponent(Color.GREEN);
		b2.setSize(new Vec2i(100, 0));
		b2.fixSize();
		b2.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.RIGHT));
		
		ButtonComponent b3 = new ButtonComponent(Color.BLUE);
		b3.setSizeMin(new Vec2i(100, 100));
		b3.setSizeMax(new Vec2i(0, 150));
		b3.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.BOTTOM));
		
		ButtonComponent b5 = new ButtonComponent(new Color(1, 0, 1));
		b5.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.CENTERED));
		b5.setLayout(new GridLayout());
		b5.setSizeMin(new Vec2i(10 * 60, 10 * 20));
		
		for (int i = 0; i < 10; i++) {
			for (int t = 0; t < 10; t++) {
				
				Color color = Color.GRAY;
				if (!(i == 0 || i == 9 || t == 0 || t == 9)) color = Color.WHITE;
				
				ButtonComponent b6 = new ButtonComponent(color);
				b6.setLayoutData(new GridLayout.GridLayoutData(i, t));
				//b6.setMargin(0, 0, 0, 0);
				b6.setSize(new Vec2i(60, 20));
				b6.fixSize();
				b5.addComponent(b6);
				
			}
		}
		
		b5.autoSetMinSize();
		
		this.uiContainer.getRootCompound().addComponent(b);
		this.uiContainer.getRootCompound().addComponent(b1);
		this.uiContainer.getRootCompound().addComponent(b2);
		this.uiContainer.getRootCompound().addComponent(b3);
		this.uiContainer.getRootCompound().addComponent(b5);
		this.uiContainer.getRootCompound().autoSetMinSize();
		
	}
	
}
