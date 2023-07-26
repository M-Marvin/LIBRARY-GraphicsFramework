package de.m_marvin.openui.layout;

import java.util.List;

import de.m_marvin.openui.components.Compound;
import de.m_marvin.renderengine.resources.IResourceProvider;

public class BorderLayout extends Layout<BorderLayout.BorderLayoutData> {
	
	public static enum BorderSection {
		LEFT,RIGHT,TOP,BOTTOM,CENTERED;
	}
	
	public static class BorderLayoutData extends Layout.LayoutData {
		public final BorderSection section;
		public BorderLayoutData() {
			this.section = BorderSection.CENTERED;
		}
		public BorderLayoutData(BorderSection section) {
			this.section = section;
		}
	}
	
	@Override
	public Class<BorderLayoutData> getDataClass() {
		return BorderLayoutData.class;
	}
	
	@Override
	public <R extends IResourceProvider<R>> void rearange(Compound<R> compound, List<Compound<R>> childComponents) {
		
		
		
	}

}
