package de.m_marvin.renderengine.buffers;

import java.util.Set;

/**
 * An optional interface for handling the creation and reusing of multiple {@link BufferBuilders}.
 * No implementation is provided because this depends of on the application.
 * 
 * @author Marvin Koehler
 *
 * @param <T> The buffer map key type to use for identifying the different buffers.
 */
public interface IBufferSource<T> {
	
	/**
	 * Returns (or creates if not already existing) a new buffer builder for the given key.
	 * The initial size of the buffer is determined by the implementation of the {@link IBufferSource} interface.
	 * 
	 * @param bufferKey The key to which the required buffer should be mapped
	 * @return The buffer mapped to the given key
	 */
	public BufferBuilder getBuffer(T bufferKey);

	/**
	 * Returns (or creates if not already existing) a new buffer builder for the given key and starts a draw.
	 * The initial size of the buffer is determined by the implementation of the {@link IBufferSource} interface.
	 * 
	 * @param bufferKey The key to which the required buffer should be mapped
	 * @return The buffer mapped to the given key
	 */
	public BufferBuilder startBuffer(T bufferKey);
	
	/**
	 * Returns a list of all buffer types used with this buffer source.
	 * @return A list of all buffer types used with this buffer source.
	 */
	public Set<T> getBufferTypes();
	
	/**
	 * Clears all buffers content and deletes the buffers from memory.
	 */
	public void freeAllMemory();
	
	/**
	 * Clears all buffers content but does not delete the buffers itself.
	 */
	public void discardAll();
	
}
