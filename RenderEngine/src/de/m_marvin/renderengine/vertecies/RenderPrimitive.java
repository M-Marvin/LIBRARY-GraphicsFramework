package de.m_marvin.renderengine.vertecies;

import java.util.function.BiConsumer;
import java.util.function.IntConsumer;

import org.lwjgl.opengl.GL33;

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
	
	public int getGlType() {
		return glType;
	}
	
	public void buildDefaultIndecies(int vertexCount, IntConsumer indexconsumer) {
		this.defaultIndexBuilder.accept(vertexCount, indexconsumer);
	}
	
}
