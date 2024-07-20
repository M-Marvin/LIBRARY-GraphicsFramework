package de.m_marvin.gframe.buffers.defimpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.m_marvin.gframe.buffers.BufferBuilder;
import de.m_marvin.gframe.buffers.IBufferSource;
import de.m_marvin.gframe.resources.IResourceProvider;

public class SimpleBufferSource<R extends IResourceProvider<R>, RM extends IRenderMode> implements IBufferSource<RM> {
	
	protected final int initialBufferSize;
	protected Map<RM, BufferBuilder> buffers;
	
	public SimpleBufferSource(int initialBufferSize) {
		this.initialBufferSize = initialBufferSize;
		this.buffers = new HashMap<>();
	}
	
	@Override
	public BufferBuilder getBuffer(RM renderLayer) {
		BufferBuilder buffer = buffers.get(renderLayer);
		if (buffer == null) {
			buffer = new BufferBuilder(initialBufferSize);
			buffers.put(renderLayer, buffer);
		}
		return buffer;
	}
	
	@Override
	public BufferBuilder startBuffer(RM renderLayer) {
		BufferBuilder buffer = getBuffer(renderLayer);
		buffer.begin(renderLayer.primitive(), renderLayer.vertexFormat());
		return buffer;
	}
	
	@Override
	public Set<RM> getBufferTypes() {
		return this.buffers.keySet();
	}
	
	@Override
	public void freeAllMemory() {
		this.buffers.forEach((renderLayer, buffer) -> buffer.freeMemory());
	}

	@Override
	public void discardAll() {
		this.buffers.forEach((renderLayer, buffer) -> buffer.discardStored());
	}
	
}
