package de.m_marvin.renderengine.buffers;

import java.nio.ByteBuffer;

import de.m_marvin.renderengine.vertecies.RenderPrimitive;
import de.m_marvin.renderengine.vertecies.VertexFormat;

/**
 * Represents a BufferBuilder which accepts vertex-data via {@link #begin(RenderPrimitive, VertexFormat)} and {@link #end()} 
 * and stores it until the data is processed by calling {@link #popNext()}.
 * The actual draw methods are provided by a different interface.
 * @author Marvin Köhler
 *
 */
public interface IBufferBuilder {
	
	/**
	 * Represents a call to {@link IBufferBuilder#begin()} and {@link IBufferBuilder#end()} and stores the amount of vertecies, indices and the format and primitive type provided to the begin-call.
	 * @author Marvin Köhler
	 */
	public static record DrawState(int vertecies, int indecies, VertexFormat format, RenderPrimitive type) {}
	/**
	 * Packs the ByteBuffer and the DrawState returned by the {@link IBufferBuilder#popNext()} method into one item.
	 * @author Marvin Köhler
	 */
	public static record BufferPair(ByteBuffer buffer, DrawState drawState) {}

	/**
	 * Starts a new vertex data-buffer to draw to.
	 * The buffer is able to process draw calls like {@link #vertex(float, float, float)} until {@link #end()} is called.
	 * 
	 * @param type The primitive type of the vertecies.
	 * @param format The vertex/shader-attribute format used.
	 * @throws IllegalStateException If {@link #begin(RenderPrimitive, VertexFormat) was already called and not finished via {@link #end()}
	 **/
	public void begin(RenderPrimitive type, VertexFormat format);

	/**
	 * Finishes a vertex data-buffer started with {@link #begin(RenderPrimitive, VertexFormat).
	 * The vertex-data gets cached in the buffer of the BufferBuilder and can be copied into a own buffer by calling {@link #popNext()}.
	 */
	public void end();

	/**
	 * Returns the number of completed ({@link #end()} called) and cached vertex data-buffers are available.
	 * 
	 * @return The number of available vertex data-buffers
	 **/
	public int completedBuffers();

	/**
	 * Pops the first of the completed vertex data-buffers.
	 * 
	 * @return A ByteBuffer containing the vertex-data and a {@link DrawState} containing necessary informations about the data in the buffer.
	 * @throws IllegalStateException if no data is available
	 **/
	public BufferPair popNext();
	
	/**
	 * Clears all cached vertex data-buffers.
	 */
	public void discardStored();
	
}
