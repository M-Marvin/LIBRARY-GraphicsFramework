package de.m_marvin.renderengine.buffers;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL33;

import de.m_marvin.renderengine.GLStateManager;
import de.m_marvin.renderengine.utility.NumberFormat;
import de.m_marvin.renderengine.vertecies.RenderPrimitive;

/**
 * Represents a VAO on the GPUs memory.
 * Contains vertex and index data about geometry that can be drawn using {@link #drawAll(RenderPrimitive)}.
 * 
 * Can be used multiple times after its creation.
 * Can also be rewritten with new vertex and index data using {@link #upload(IBufferBuilder, BufferUsage)}.
 * 
 * @author Marvin Köhler
 *
 */
public class VertexBuffer {
	
	protected int arrayObjectId;
	protected int vertexBufferId;
	protected int indexBufferId;
	protected int indecies;
	protected int vertecies;
	
	/**
	 * Returns true if the VAO is initialized (VAO and VBOs created on GPU).
	 * Draw calls to uninitialized VertexBuffers could result in JVM crashes caused by LWJGL.
	 * @implNote Only checks the id of the VAO, but since VBOs and VAO always get initialized together, this is enough to check if the VertexBuffer is ready to be used.
	 * 
	 * @return True if the VAO and VBOs are initialized on the GPU
	 */
	public boolean initialized() {
		return arrayObjectId > 0;
	}
	
	/**
	 * Discards all VBOs and VAO from the GPU.
	 * This resets all data stored in the VertexBuffer.
	 * Reinitialization with {@link #initialize()} is possible
	 **/
	public void discard() {
		GLStateManager.assertOnRenderThread();
		GLStateManager.deleteVertexArray(this.arrayObjectId);
		GLStateManager.deleteBufferObject(this.indexBufferId);
		GLStateManager.deleteBufferObject(vertexBufferId);
		this.vertexBufferId = 0;
		this.arrayObjectId = 0;
		this.indexBufferId = 0;
	}
	
	/**
	 * Initializes the VBOs and VAO on the GPU.
	 * 
	 * @throws IllegalStateException If the VertexBuffer is already initialized
	 */
	protected void initialize() {
		if (initialized()) throw new IllegalStateException("VertexBuffer is already initialized!");
		this.arrayObjectId = GLStateManager.genVertexArray();
		this.indexBufferId = GLStateManager.genBufferObject();
		this.vertexBufferId = GLStateManager.genBufferObject();
	}
	
	/**
	 * Pulls vertex data from the provided {@link BufferBuilder} and loads it up into the GPU.
	 * The usage parameter is passed to the OpenGL functions to decide where on the GPU the best place to store the data is.
	 * 
	 * @param bufferBuilder The source of the vertex data, if the provided buffer is empty, a IllegalStateException might be thrown.
	 * @param usage The usage of the data, passed to the OpenGL functions
	 */
	public void upload(IBufferBuilder bufferBuilder, BufferUsage usage) {
		GLStateManager.assertOnRenderThread();
		
		initialize();
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
	
	/**
	 * Binds the VAO of this Buffer on the GPU for rendering.
	 **/
	public void bind() {
		GLStateManager.bindVertexArray(arrayObjectId);
	}
	
	/**
	 * Binds VAO id 0 on the GPU.
	 * Effectively unbinds this VAO.
	 **/
	public void unbind() {
		GLStateManager.bindVertexArray(0);
	}
	
	/**
	 * Returns the supported index-data format.
	 * The format of this implementation is {@link NumberFormat.UINT}.
	 **/
	public NumberFormat indecieFormat() {
		return NumberFormat.UINT;
	}
	
	/**
	 * Draws the content of the currently bound VertexBuffer.
	 * @implNote It does not matter on which VertexBuffer instance the method is called, only the buffer currently bound will be drawn.
	 * @param The geometry primitive drawn using the data in the currently bound VAO.
	 */
	public void drawAll(RenderPrimitive mode) {
		GLStateManager.drawElements(mode.getgltype(), indecies, indecieFormat().gltype());
	}
	
}
