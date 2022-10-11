package de.m_marvin.renderengine.vertecies;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL33;

public class VertexFormat {
	
	protected List<VertexElement> elements = new ArrayList<>();
	protected int size;
	
	public record VertexElement(int index, int position, boolean normalize, String name, Format format, int size) {}
	public enum Format {
		INT(Integer.BYTES, GL33.GL_INT),SHORT(Short.BYTES, GL33.GL_SHORT),FLOAT(Float.BYTES, GL33.GL_FLOAT),BYTE(Byte.BYTES, GL33.GL_BYTE);
		private final int bytes;
		private final int glType;
		Format(int bytes, int glType) {
			this.bytes = bytes;
			this.glType = glType;
		}
		public int size() {
			return this.bytes;
		}
		public int gltype() {
			return this.glType;
		}
	}
	
	public VertexFormat appand(String name, Format format, int count, boolean normalize) {
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
