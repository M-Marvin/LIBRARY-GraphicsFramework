package de.m_marvin.uitest;

import java.awt.Color;

import de.m_marvin.openui.core.layout.BorderLayout;
import de.m_marvin.openui.core.layout.BorderLayout.BorderSection;
import de.m_marvin.openui.core.layout.BorderLayout.CornerStretch;
import de.m_marvin.openui.core.layout.GridLayout;
import de.m_marvin.openui.design1.Window;
import de.m_marvin.openui.design1.components.ButtonComponent;
import de.m_marvin.openui.design1.components.GroupBox;
import de.m_marvin.univec.impl.Vec2i;

public class TestWindow extends Window {
	
	public TestWindow() {
		super("Test Window");
	}
	
	@Override
	protected void initUI() {
		this.getRootComponent().setLayout(new BorderLayout(CornerStretch.VERTICAL));
		
		GroupBox b = new GroupBox(Color.CYAN);
		b.setSizeMin(new Vec2i(100, 100));
		b.setSizeMax(new Vec2i(150, 0));
		b.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.LEFT));
		
		GroupBox b1 = new GroupBox(Color.RED);
		b1.setSizeMin(new Vec2i(100, 100));
		b1.setSizeMax(new Vec2i(0, 150));
		b1.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.TOP));
		
		GroupBox b2 = new GroupBox(Color.GREEN);
		b2.setSize(new Vec2i(100, 0));
		b2.fixSize();
		b2.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.RIGHT));
		
		GroupBox b3 = new GroupBox(Color.BLUE);
		b3.setSizeMin(new Vec2i(100, 100));
		b3.setSizeMax(new Vec2i(0, 150));
		b3.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.BOTTOM));
		
		GroupBox b5 = new GroupBox(new Color(1, 0, 1));
		b5.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.CENTERED));
		b5.setLayout(new GridLayout());
		b5.setSizeMin(new Vec2i(10 * 60, 10 * 20));
		
		for (int i = 0; i < 1; i++) {
			for (int t = 0; t < 1; t++) {
				
				Color color = Color.WHITE;
				//if (!(i == 0 || i == 9 || t == 0 || t == 9)) color = Color.GREEN;
				
				ButtonComponent b6 = new ButtonComponent("Test", color);
				b6.setLayoutData(new GridLayout.GridLayoutData(i, t));
				//b6.setMargin(0, 0, 0, 0);
				b6.setSize(new Vec2i(60, 20));
				b6.fixSize();
				b5.addComponent(b6);
				
			}
		}
		
		b5.autoSetMinSize();
		
		this.getRootComponent().addComponent(b);
		this.getRootComponent().addComponent(b1);
		this.getRootComponent().addComponent(b2);
		this.getRootComponent().addComponent(b3);
		this.getRootComponent().addComponent(b5);
		this.getRootComponent().autoSetMinSize();
		
	}
	
}
