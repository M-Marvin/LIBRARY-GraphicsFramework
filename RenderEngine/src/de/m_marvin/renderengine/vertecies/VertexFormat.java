package de.m_marvin.renderengine.vertecies;

import java.util.ArrayList;
import java.util.List;

import de.m_marvin.renderengine.GLStateManager;
import de.m_marvin.renderengine.utility.NumberFormat;

public class VertexFormat {
	
	protected List<VertexElement> elements = new ArrayList<>();
	protected int size;
	
	public record VertexElement(int index, int offset, boolean normalize, String name, NumberFormat format, int count) {}
	
	public VertexFormat appand(String name, NumberFormat format, int count, boolean normalize) {
		elements.add(new VertexElement(elements.size(), this.size, normalize, name, format, count));
		this.size += count * format.size();
		return this;
	}
	
	public List<VertexElement> getElements() {
		return elements;
	}
	
	public VertexElement elementWithIndex(int index) {
		return this.elements.get(index);
	}
	
	public int getSize() {
		return this.size;
	}
	
	public int getElementCount() {
		return this.elements.size();
	}
	
	public void enableAttributes() {
		for (VertexElement element : getElements()) {
			GLStateManager.enableAttributeArray(element.index());
		}
	}
	
	public void disableAttributes() {
		for (VertexElement element : getElements()) {
			GLStateManager.disableAttributeArray(element.index());
		}
	}
	
}
