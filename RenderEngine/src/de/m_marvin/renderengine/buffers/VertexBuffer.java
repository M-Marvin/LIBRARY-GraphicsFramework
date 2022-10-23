package de.m_marvin.renderengine.buffers;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL33;

import de.m_marvin.renderengine.GLStateManager;
import de.m_marvin.renderengine.utility.NumberFormat;
import de.m_marvin.renderengine.vertecies.RenderPrimitive;
import de.m_marvin.renderengine.vertecies.VertexFormat.VertexElement;

public class VertexBuffer {
	
	protected int arrayObjectId;
	protected int[] vertexBufferIds;
	protected int indexBufferId;
	protected int indecies;
	protected int vertecies;
	
	public int parelelDataBuffers() {
		return this.vertexBufferIds == null ? 0 : this.vertexBufferIds.length;
	}
	
	public boolean initialised() {
		return arrayObjectId > 0;
	}
	
	public void discard() {
		GLStateManager.assertOnRenderThread();
		GLStateManager.deleteVertexArray(this.arrayObjectId);
		GLStateManager.deleteBufferObject(this.indexBufferId);
		for (int i = 0; i < this.vertexBufferIds.length; i++) {
			GLStateManager.deleteBufferObject(vertexBufferIds[i]);
			vertexBufferIds[i] = 0;
		}
		this.vertexBufferIds = null;
		this.arrayObjectId = 0;
		this.indexBufferId = 0;
	}
	
	protected void initialise(int paralelVAOs) {
		this.arrayObjectId = GLStateManager.genVertexArray();
		this.indexBufferId = GLStateManager.genBufferObject();
		this.vertexBufferIds = new int[paralelVAOs];
		for (int i = 0; i < paralelVAOs; i++)
			this.vertexBufferIds[i] = GLStateManager.genBufferObject();
	}
	
	public void upload(IBufferBuilder bufferBuilder) {
		GLStateManager.assertOnRenderThread();
		
		initialise(bufferBuilder.paralelDataVAOs());
		IBufferBuilder.BufferPair pair = bufferBuilder.popNext();
		IBufferBuilder.DrawState drawState = pair.drawState();
		this.indecies = drawState.indecies();
		this.vertecies = drawState.vertecies();
		
		bind();
		
		if (bufferBuilder.paralelDataVAOs() > 1) {
			
			for (int i = 1; i < drawState.format().getElementCount(); i++) {
				VertexElement element = drawState.format().elementWithIndex(i - 1);
				ByteBuffer buffer = pair.buffer()[i];
				if (buffer != null) {
					buffer.clear();
					buffer.limit(this.vertecies * element.count() * element.format().size());
					GLStateManager.bindBufferObject(GL33.GL_ARRAY_BUFFER, vertexBufferIds[i]);
					GLStateManager.bufferData(GL33.GL_ARRAY_BUFFER, buffer, GL33.GL_STATIC_DRAW);
					GLStateManager.attributePointer(element.index(), element.count(), element.format().gltype(), element.normalize(), element.count() * element.format().size(), 0);
				}
			}

			ByteBuffer indecieBuffer = pair.buffer()[0];
			if (indecieBuffer != null) {
				indecieBuffer.clear();
				indecieBuffer.limit(this.indecies * indecieFormat().size());
				GLStateManager.bindBufferObject(GL33.GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
				GLStateManager.bufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, indecieBuffer, GL33.GL_STATIC_DRAW);
			}
			
			GLStateManager.bindBufferObject(GL33.GL_ARRAY_BUFFER, 0);
			
		} else {
			
			ByteBuffer buffer = pair.buffer()[0];
			buffer.clear();
			buffer.limit(this.vertecies * drawState.format().getSize());
			GLStateManager.bindBufferObject(GL33.GL_ARRAY_BUFFER, vertexBufferIds[0]);
			GLStateManager.bufferData(GL33.GL_ARRAY_BUFFER, buffer, GL33.GL_STATIC_DRAW);
			drawState.format().getElements().forEach((element) -> GLStateManager.attributePointer(element.index(), element.count(), element.format().gltype(), element.normalize(), drawState.format().getSize(), element.offset()));

			buffer.position(buffer.limit());
			buffer.limit(buffer.limit() + drawState.indecies() * indecieFormat().size());
			GLStateManager.bindBufferObject(GL33.GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
			GLStateManager.bufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, buffer, GL33.GL_STATIC_DRAW);
			
			GLStateManager.bindBufferObject(GL33.GL_ARRAY_BUFFER, 0);
			
		}
		
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
