package de.m_marvin.renderengine.vertecies;

import java.util.ArrayList;
import java.util.List;

public class VertexFormat {
	
	protected List<VertexElement> elements = new ArrayList<>();
	protected int size;
	
	public record VertexElement(int index, int position, boolean normalize, String name, NumberFormat format, int size) {}
	
	public VertexFormat appand(String name, NumberFormat format, int count, boolean normalize) {
		int size = count * format.size();
		elements.add(new VertexElement(elements.size(), this.size, normalize, name, format, size));
		this.size += size;
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
	
}
