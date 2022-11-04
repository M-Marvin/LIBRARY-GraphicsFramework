package de.m_marvin.renderengine.vertecies;

import java.util.ArrayList;
import java.util.List;

import de.m_marvin.renderengine.utility.NumberFormat;

/**
 * Describes the order and format of the vertex attributes in some sort of buffer and the configuration of the in-values of the vertex shader program.
 * 
 * @author Marvin Köhler
 */
public class VertexFormat {
	
	protected List<VertexElement> elements = new ArrayList<>();
	protected int size;
	
	/**
	 * Represents one attribute in the format.
	 * 
	 * @author Marvin Köhler
	 */
	public record VertexElement(int index, int offset, boolean normalize, String name, NumberFormat format, int count) {}
	
	/**
	 * Append a new attribute element to this format.
	 * 
	 * @param name The name of the element in the vertex shader program
	 * @param format The number format used for this attribute
	 * @param count The number of values required for this attribute
	 * @param normalize If the values should be normalized
	 * @return This vertex format to add more attributes
	 */
	public VertexFormat appand(String name, NumberFormat format, int count, boolean normalize) {
		elements.add(new VertexElement(elements.size(), this.size, normalize, name, format, count));
		this.size += count * format.size();
		return this;
	}
	
	/**
	 * Returns a list containing all attributes in the correct order of this format.
	 * @return A {@link List} containg all elements in correct order
	 */
	public List<VertexElement> getElements() {
		return elements;
	}
	
	/**
	 * Returns the attribute element with the given index
	 * @param index The index of the required element
	 * @return The element with the given index
	 * @throws IndexOutOfBoundsException If the index is out of bounds
	 */
	public VertexElement elementWithIndex(int index) {
		return this.elements.get(index);
	}
	
	/**
	 * Returns the total number bytes required to represent one vertex with this format.
	 * @return The required bytes for one vertex if all attributes are filled with data
	 */
	public int getSize() {
		return this.size;
	}
	
	/**
	 * Returns the number of attributes that are required to describe one vertex.
	 * @return The number of attribute elements for one vertex
	 */
	public int getElementCount() {
		return this.elements.size();
	}
	
}
