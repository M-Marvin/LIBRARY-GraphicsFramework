package de.m_marvin.renderengine.vertecies;

import java.util.function.BiConsumer;
import java.util.function.IntConsumer;

import org.lwjgl.opengl.GL33;

import de.m_marvin.renderengine.buffers.VertexBuffer;

/**
 * Represents the different primitives supported by this render engine.
 * @implNote The quads implementation just provides a special index-algorithm to convert the vertex data to triangle primitives, so it does not mater if {@link QUADS} or {@link TRIANGLES} is passes to the drawing function of {@link VertexBuffer#drawAll(RenderPrimitive)}.
 * 
 * @author Marvin Köhler
 *
 */
public enum RenderPrimitive {
	
	TRIANGLES(GL33.GL_TRIANGLES, (vertecies, indexconsumer) -> {
		for (int i = 0; i < vertecies; i++) indexconsumer.accept(i);
	}),
	TRIANGLES_STRIP(GL33.GL_TRIANGLE_STRIP, (vertecies, indexconsumer) -> {
		for (int i = 0; i < vertecies; i++) indexconsumer.accept(i);
	}),
	TRIANGLES_FAN(GL33.GL_TRIANGLE_FAN, (vertecies, indexconsumer) -> {
		for (int i = 0; i < vertecies; i++) indexconsumer.accept(i);
	}),
	QUADS(GL33.GL_TRIANGLES, (vertecies, indexconsumer) -> {
		for (int i = 0; i < vertecies; i += 4) {
			indexconsumer.accept(i + 0);
			indexconsumer.accept(i + 1);
			indexconsumer.accept(i + 2);
			indexconsumer.accept(i + 2);
			indexconsumer.accept(i + 3);
			indexconsumer.accept(i + 0);
		}
	});
	
	private final int glType;
	private final BiConsumer<Integer, IntConsumer> defaultIndexBuilder;
	
	private RenderPrimitive(int glType, BiConsumer<Integer, IntConsumer> defaultIndexBuilder) {
		this.glType = glType;
		this.defaultIndexBuilder = defaultIndexBuilder;
	}
	
	public int getgltype() {
		return glType;
	}
	
	/**
	 * Builds the default indecies required for rendering geometry with this primitive type.
	 * This method can be used to auto generate the indecies if the used vertex data is in the correct standard order for this primitive.
	 * 
	 * @param vertexCount The number of vertecies to draw
	 * @param indexconsumer An consumer to receive the index values
	 */
	public void buildDefaultIndecies(int vertexCount, IntConsumer indexconsumer) {
		this.defaultIndexBuilder.accept(vertexCount, indexconsumer);
	}
	
}
