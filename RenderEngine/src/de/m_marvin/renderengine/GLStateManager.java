package de.m_marvin.renderengine;

import java.nio.ByteBuffer;
import java.util.function.IntConsumer;

import org.lwjgl.opengl.GL33;

public class GLStateManager {
	
	public static boolean isOnRenderThread() {
		return true; // TODO
	}
	
	public static void assertOnRenderThread() {
		if (!isOnRenderThread()) throw new IllegalStateException("GL operations have to be performed on the render thread!");
	}
	
	public static void genVertexArray(IntConsumer idConsumer) {
		idConsumer.accept(GL33.glGenVertexArrays());
	}
	
	public static void genBufferObject(IntConsumer idConsumer) {
		idConsumer.accept(GL33.glGenBuffers());
	}

	public static void deleteVertexArray(int vertexArrayId) {
		GL33.glDeleteVertexArrays(vertexArrayId);
	}
	
	public static void deleteBufferObject(int bufferObjectId) {
		GL33.glDeleteBuffers(bufferObjectId);
	}

	public static void bufferData(int target, ByteBuffer data, int usage) {
		GL33.glBufferData(target, data, usage);
	}

	public static void bindBufferObject(int target, int vertexBufferId) {
		GL33.glBindBuffer(target, vertexBufferId);
	}

	public static void bindVertexArray(int arrayObjectId) {
		GL33.glBindVertexArray(arrayObjectId);
	}
	
	public static void attributePointer(int attributeId, int size, int position, int format, boolean normalize, long bufferOffset) {
		GL33.glVertexAttribPointer(attributeId, size, format, normalize, position, bufferOffset);
	}
	
	public static void d() {
		GL33.
	}
	
}
