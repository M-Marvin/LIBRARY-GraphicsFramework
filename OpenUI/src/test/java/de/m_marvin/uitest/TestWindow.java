package de.m_marvin.uitest;

import java.awt.Color;
import java.util.Random;

import de.m_marvin.openui.core.layout.BorderLayout;
import de.m_marvin.openui.core.layout.BorderLayout.BorderSection;
import de.m_marvin.openui.core.layout.BorderLayout.CornerStretch;
import de.m_marvin.openui.flatmono.Window;
import de.m_marvin.openui.flatmono.components.BarComponent;
import de.m_marvin.openui.flatmono.components.ButtonComponent;
import de.m_marvin.openui.flatmono.components.GraphComponent;
import de.m_marvin.openui.flatmono.components.GraphComponent.Graph;
import de.m_marvin.openui.flatmono.components.GroupBox;
import de.m_marvin.openui.flatmono.components.ScrollBarComponent;
import de.m_marvin.openui.flatmono.components.TextFieldComponent;
import de.m_marvin.openui.flatmono.components.ToggleButtonComponent;
import de.m_marvin.openui.core.layout.GridLayout;
import de.m_marvin.univec.impl.Vec2d;
import de.m_marvin.univec.impl.Vec2i;

public class TestWindow extends Window {
	
	public TestWindow() {
		super("Test Window");
	}
	
	@Override
	protected void initUI() {
		this.getRootComponent().setLayout(new BorderLayout(CornerStretch.VERTICAL));
		
		GroupBox b = new GroupBox();
		b.setSizeMin(new Vec2i(100, 100));
		b.setSizeMax(new Vec2i(150, 0));
		b.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.LEFT));
		
		ScrollBarComponent scrollbar1 = new ScrollBarComponent(false, 200, 60);
		scrollbar1.setOffset(new Vec2i(40, 40));
		b.addComponent(scrollbar1);
		
		GroupBox b1 = new GroupBox();
		b1.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.TOP));
		b1.setLayout(new GridLayout());

		String[] titles = new String[] {"Test", "Button", "12345", "!?\\@Ä", "ABCDEFGHIJKLMNOP"};
		Random r = new Random();
		
		for (int i = 0; i < 4; i++) {
			for (int t = 0; t < 2; t++) {
				
				TextFieldComponent b6 = new TextFieldComponent();
				b6.setText(titles[r.nextInt(titles.length)]);
				b6.setLayoutData(new GridLayout.GridLayoutData(i, t + 10));
				b1.addComponent(b6);
				
			}
		}
		
		GraphComponent gc = new GraphComponent(0, 100, -50, 50);
		gc.setLayoutData(new GridLayout.GridLayoutData(4, 0));
		gc.setSize(new Vec2i(600, 300));
		gc.fixSize();
		b1.addComponent(gc);
		
		Graph graph = new GraphComponent.Graph("Test", new Color(0, 255, 0, 255));
		gc.addGraph(graph);
		graph.getDataPoints().add(new Vec2d(0, 25));
		graph.getDataPoints().add(new Vec2d(40, -25));
		graph.getDataPoints().add(new Vec2d(60, -40));
		graph.getDataPoints().add(new Vec2d(80, 40));
		graph.getDataPoints().add(new Vec2d(100, 45));

		Graph graph2 = new GraphComponent.Graph("Test", new Color(255, 0, 0, 255));
		gc.addGraph(graph2);
		graph2.getDataPoints().add(new Vec2d(0, 20));
		graph2.getDataPoints().add(new Vec2d(20, -20));
		graph2.getDataPoints().add(new Vec2d(40, -30));
		graph2.getDataPoints().add(new Vec2d(65, 00));
		graph2.getDataPoints().add(new Vec2d(70, 23));
		
		b1.autoSetMaxAndMinSize();
		b1.getSizeMax().setX(0);
		
		GroupBox b2 = new GroupBox();
		b2.setSize(new Vec2i(100, 0));
		b2.fixSize();
		b2.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.RIGHT));
		
		BarComponent bar2 = new BarComponent(false);
		bar2.setValue(70);
		bar2.setSize(new Vec2i(20, 300));
		bar2.setOffset(new Vec2i(30, 40));
		b2.addComponent(bar2);
		
		GroupBox b3 = new GroupBox();
		b3.setSizeMin(new Vec2i(100, 100));
		b3.setSizeMax(new Vec2i(0, 150));
		b3.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.BOTTOM));

		ScrollBarComponent scrollbar2 = new ScrollBarComponent(true, 200, 60);
		scrollbar2.setOffset(new Vec2i(40, 40));
		b3.addComponent(scrollbar2);
		
		BarComponent bar1 = new BarComponent();
		bar1.setValue(50);
		bar1.setSize(new Vec2i(300, 20));
		bar1.setOffset(new Vec2i(240, 40));
		b3.addComponent(bar1);
		
		GroupBox b5 = new GroupBox();
		b5.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.CENTERED));
		b5.setLayout(new GridLayout());
		b5.setSizeMin(new Vec2i(10 * 60, 10 * 20));
		
		for (int i = 0; i < 10; i++) {
			for (int t = 0; t < 10; t++) {
				
				Color color = Color.WHITE;
				if (!(i == 0 || i == 9 || t == 0 || t == 9)) color = Color.RED;
				
				ButtonComponent b6 = new ButtonComponent(titles[r.nextInt(titles.length)], color);
				b6.setLayoutData(new GridLayout.GridLayoutData(i, t));
				int a = i;
				int c = t;
				b6.setAction(() -> System.out.println("Clicked on " + a + "/" + c));
				b5.addComponent(b6);
				
				b6.setAction(() -> gc.stepValueLimit(5, 0));
				
			}
		}
		
		for (int i = 0; i < 10; i++) {
			for (int t = 0; t < 2; t++) {
				
				ToggleButtonComponent b6 = new ToggleButtonComponent(titles[r.nextInt(titles.length)]);
				b6.setLayoutData(new GridLayout.GridLayoutData(i, t + 10));
				int a = i;
				int c = t;
				b6.setAction((state) -> System.out.println("Toggle " + (state ? "on" : "off") + " " + a + "/" + c));
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
