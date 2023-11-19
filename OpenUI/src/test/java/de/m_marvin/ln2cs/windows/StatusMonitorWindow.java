package de.m_marvin.ln2cs.windows;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

import de.m_marvin.openui.core.layout.BorderLayout;
import de.m_marvin.openui.core.layout.BorderLayout.BorderSection;
import de.m_marvin.openui.flatmono.Window;
import de.m_marvin.openui.flatmono.components.ButtonComponent;
import de.m_marvin.openui.flatmono.components.GroupBox;
import de.m_marvin.openui.flatmono.components.PointerDisplayComponent;
import de.m_marvin.univec.impl.Vec2i;

public class StatusMonitorWindow extends Window {

	public StatusMonitorWindow() {
		super("LN2_CS Status Monitor");
	}
	
	PointerDisplayComponent pd1;
	PointerDisplayComponent pd2;
	PointerDisplayComponent pd3;
	
	@Override
	protected void initUI() {
		
		GroupBox background = new GroupBox(Color.BLACK);
		background.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.CENTERED));
		this.getRootComponent().setLayout(new BorderLayout());
		this.getRootComponent().addComponent(background);
		
		ButtonComponent b1 = new ButtonComponent("Test Button 1");
		b1.setOffset(new Vec2i(100, 100));
		b1.setSize(new Vec2i(100, b1.getSize().y));
		background.addComponent(b1);
		
		pd1 = new PointerDisplayComponent();
		pd1.setOffset(new Vec2i(100, 130));
		pd1.setTextColor(new Color(0, 255, 255, 255));
		background.addComponent(pd1);

		pd2 = new PointerDisplayComponent();
		pd2.setOffset(new Vec2i(300, 130));
		background.addComponent(pd2);

		pd3 = new PointerDisplayComponent();
		pd3.setOffset(new Vec2i(500, 130));
		pd3.setSize(new Vec2i(300, 300));
		pd3.setTextColor(new Color(255, 0, 0, 255));
		pd3.setFont(new Font("arial", Font.BOLD ,32));
		background.addComponent(pd3);
		
	}
	
	float v[] = { 0, 0, 0 };
	int c;
	
	public void test() {
		// TODO Auto-generated method stub
		
		c++;
		if (c >= 100) {
			c = 0;
			for (int i = 0; i < v.length; i++) v[i] = new Random().nextFloat();
		}
		
		if (pd1 != null) pd1.setValue(pd1.getValue() + ((v[0] * 100) - pd1.getValue()) * 0.1F);
		if (pd2 != null) pd2.setValue(pd2.getValue() + ((v[1] * 100) - pd2.getValue()) * 0.1F);
		if (pd3 != null) pd3.setValue(pd3.getValue() + ((v[2] * 100) - pd3.getValue()) * 0.1F);
		
	}
	
}
