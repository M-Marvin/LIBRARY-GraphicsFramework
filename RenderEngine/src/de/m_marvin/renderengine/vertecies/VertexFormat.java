package de.m_marvin.renderengine.vertecies;

import java.util.ArrayList;
import java.util.List;

public class VertexFormat {
	
	protected List<VertexElement> elements = new ArrayList<>();
	protected int size;
	
	public record VertexElement(String name, Format format, int size) {}
	public enum Format {
		INT(Integer.BYTES),SHORT(Short.BYTES),FLOAT(Float.BYTES),BYTE(Byte.BYTES);
		private final int bytes;
		Format(int bytes) { this.bytes = bytes; }
		public int size() {
			return this.bytes;
		}
	}
	
	public VertexFormat appand(String name, Format format, int count) {
		this.size += count * format.size();
		elements.add(new VertexElement(name, format, size));
		return this;
	}
	
	public List<VertexElement> getElements() {
		return elements;
	}
	
	public VertexElement getElementWithIndex(int index) {
		return this.elements.get(index);
	}
	
	public int getSize() {
		return this.size;
	}
	
	public int getElementCount() {
		return this.elements.size();
	}
	
}
