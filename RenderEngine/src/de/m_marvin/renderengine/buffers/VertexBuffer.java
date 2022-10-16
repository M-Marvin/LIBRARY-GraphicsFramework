package de.m_marvin.renderengine.buffers;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL33;

import de.m_marvin.renderengine.GLStateManager;
import de.m_marvin.renderengine.buffers.BufferBuilder.DrawState;
import de.m_marvin.renderengine.utility.NumberFormat;
import de.m_marvin.renderengine.vertecies.VertexFormat.VertexElement;

public class VertexBuffer {
	
	protected int arrayObjectId;
	protected int vertexBufferId;
	protected int indexBufferId;
	protected int indecies;
	protected int vertecies;
	
	public VertexBuffer() {
		GLStateManager.genVertexArray((id) -> this.arrayObjectId = id);
		GLStateManager.genBufferObject((id) -> this.indexBufferId = id);
		GLStateManager.genBufferObject((id) -> this.vertexBufferId = id);
	}
	
	public void discard() {
		GLStateManager.deleteVertexArray(this.vertexBufferId);
		GLStateManager.deleteBufferObject(vertexBufferId);
		GLStateManager.deleteBufferObject(indexBufferId);
	}
	
	public void upload(BufferBuilder bufferBuilder) {
		GLStateManager.assertOnRenderThread();
		BufferBuilder.BufferPair pair = bufferBuilder.popNext();
		ByteBuffer buffer = pair.buffer();
		DrawState drawState = pair.drawState();
		bindBuffers();
		buffer.clear();
		buffer.limit(drawState.vertecies() * drawState.format().getSize());
		GLStateManager.bufferData(GL33.GL_ARRAY_BUFFER, buffer, GL33.GL_STATIC_DRAW);
		buffer.position(buffer.limit());
		buffer.limit(buffer.limit() + drawState.indecies() * Integer.BYTES);
		GLStateManager.bufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, buffer, GL33.GL_STATIC_DRAW);
		unbindBuffers();
	}
	
	public void bind() {
		GLStateManager.bindVertexArray(arrayObjectId);
	}
	
	public void unbind() {
		GLStateManager.bindVertexArray(0);
	}
	
	public void bindBuffers() {
		bind();
		GLStateManager.bindBufferObject(GL33.GL_ARRAY_BUFFER, vertexBufferId);
		GLStateManager.bindBufferObject(GL33.GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
	}
	
	public void unbindBuffers() {
		unbind();
		GLStateManager.bindBufferObject(GL33.GL_ARRAY_BUFFER, 0);
		GLStateManager.bindBufferObject(GL33.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	public int indecieFormat() {
		return NumberFormat.UINT.gltype();
	}
	
}
