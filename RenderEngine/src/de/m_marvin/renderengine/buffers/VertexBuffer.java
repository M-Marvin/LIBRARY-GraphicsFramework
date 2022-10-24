package de.m_marvin.renderengine.buffers;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL33;

import de.m_marvin.renderengine.GLStateManager;
import de.m_marvin.renderengine.utility.NumberFormat;
import de.m_marvin.renderengine.vertecies.RenderPrimitive;

public class VertexBuffer {
	
	protected int arrayObjectId;
	protected int vertexBufferId;
	protected int indexBufferId;
	protected int indecies;
	protected int vertecies;
	
	public boolean initialised() {
		return arrayObjectId > 0;
	}
	
	public void discard() {
		GLStateManager.assertOnRenderThread();
		GLStateManager.deleteVertexArray(this.arrayObjectId);
		GLStateManager.deleteBufferObject(this.indexBufferId);
		GLStateManager.deleteBufferObject(vertexBufferId);
		this.vertexBufferId = 0;
		this.arrayObjectId = 0;
		this.indexBufferId = 0;
	}
	
	protected void initialise() {
		this.arrayObjectId = GLStateManager.genVertexArray();
		this.indexBufferId = GLStateManager.genBufferObject();
		this.vertexBufferId = GLStateManager.genBufferObject();
	}
	
	public void upload(IBufferBuilder bufferBuilder, BufferUsage usage) {
		GLStateManager.assertOnRenderThread();
		
		initialise();
		IBufferBuilder.BufferPair pair = bufferBuilder.popNext();
		IBufferBuilder.DrawState drawState = pair.drawState();
		this.indecies = drawState.indecies();
		this.vertecies = drawState.vertecies();
		
		bind();
		
		ByteBuffer buffer = pair.buffer();
		buffer.clear();
		buffer.limit(this.vertecies * drawState.format().getSize());
		GLStateManager.bindBufferObject(GL33.GL_ARRAY_BUFFER, vertexBufferId);
		GLStateManager.bufferData(GL33.GL_ARRAY_BUFFER, buffer, usage.gltype());
		drawState.format().getElements().forEach((element) -> {
			GLStateManager.attributePointer(element.index(), element.count(), element.format().gltype(), element.normalize(), drawState.format().getSize(), element.offset());
			GLStateManager.enableAttributeArray(element.index());
		});
		
		buffer.position(buffer.limit());
		buffer.limit(buffer.limit() + drawState.indecies() * indecieFormat().size());
		GLStateManager.bindBufferObject(GL33.GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
		GLStateManager.bufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, buffer, usage.gltype());
		
		GLStateManager.bindBufferObject(GL33.GL_ARRAY_BUFFER, 0);
		
		unbind();
		
	}
	
	public void bind() {
		GLStateManager.bindVertexArray(arrayObjectId);
	}
	
	public void unbind() {
		GLStateManager.bindVertexArray(0);
	}
	
	public NumberFormat indecieFormat() {
		return NumberFormat.UINT;
	}
	
	public void drawAll(RenderPrimitive mode) {
		GLStateManager.drawElements(mode.getgltype(), indecies, indecieFormat().gltype());
	}
	
}
