package de.m_marvin.renderengine.buffers;

import java.nio.ByteBuffer;
import java.util.Queue;

import org.lwjgl.system.MemoryUtil;

import com.google.common.collect.Queues;

import de.m_marvin.renderengine.utility.NumberFormat;
import de.m_marvin.renderengine.vertices.IVertexConsumer;
import de.m_marvin.renderengine.vertices.RenderPrimitive;
import de.m_marvin.renderengine.vertices.VertexFormat;
import de.m_marvin.renderengine.vertices.VertexFormat.VertexElement;

/**
 * The BufferBuilder is used to create {@link VertexBuffers} by calling necessary draw methods like {@link #vertex(float, float, float)}.
 * 
 * It can store multiple data-streams for multiple VertexBuffers at the same time.
 * It can be reused after building all VertexBuffers via {@link #popNext()} or by calling {@link #discardStored()}.
 * @author Marvin Koehler
 *
 */
public class BufferBuilder implements IBufferBuilder, IVertexConsumer {
	
	protected ByteBuffer buffer;
	protected Queue<DrawState> drawStates;
	protected int uploadedBytes;
	protected int writtenBytes;

	protected VertexFormat format;
	protected RenderPrimitive type;
	protected boolean building;
	protected boolean buildingIndecies;
	protected int vertexCount;
	protected int indexCount;
	protected int currentElementIndex;
	protected VertexElement currentElement;
	
	/**
	 * Allocates a new empty BufferBuilder with the given size in bytes.
	 * 
	 * @param bufferSize The size of the buffer in bytes
	 **/
	public BufferBuilder(int bufferSize) {
		this.buffer = MemoryUtil.memAlloc(bufferSize);
		this.drawStates = Queues.newArrayDeque();
	}
	
	private void ensureCapacity(int size) {
		if (buffer.position() + size > buffer.capacity()) {
			int currentSize = this.buffer.capacity();
			int addedSize = roundUp(size);
			this.buffer = MemoryUtil.memRealloc(buffer, currentSize + addedSize);
		}
	}
	
	private static int roundUp(int size) {
		int i = 2097152;
		if (size == 0) {
			return i;
		} else {
			if (size < 0) {
				i *= -1;
			}
			
			int j = size % i;
			return j == 0 ? size : size + i - j;
		}
	}
	
	@Override
	public int completedBuffers() {
		return this.drawStates.size();
	}
	
	@Override
	public BufferPair popNext() {
		if (this.drawStates.isEmpty()) throw new IllegalStateException("Nothing has ben drawn to the buffer!");
		DrawState drawState = this.drawStates.poll();
		this.buffer.position(uploadedBytes);
		this.uploadedBytes += drawState.format().getSize() * drawState.vertices() + drawState.indecies() * NumberFormat.UINT.size();
		this.buffer.limit(uploadedBytes);
		ByteBuffer drawBuffer = this.buffer.slice();
		drawBuffer.order(this.buffer.order());
		this.buffer.clear();
		if (this.drawStates.isEmpty()) {
			this.writtenBytes = 0;
			this.uploadedBytes = 0;
		}
		return new BufferPair(drawBuffer, drawState);	
	}
	
	@Override
	public void discardStored() {
		this.buffer.clear();
		this.uploadedBytes = 0;
		this.drawStates.clear();
		this.vertexCount = 0;
		this.indexCount = 0;
		this.building = false;
		this.currentElementIndex = -1;
	}

	/**
	 * Destroys this BufferBuilder and frees its allocated memory.
	 */
	public void freeMemory() {
		discardStored();
		MemoryUtil.memFree(buffer);
	}
	
	@Override
	public void begin(RenderPrimitive type, VertexFormat format) {
		if (this.building) {
			throw new IllegalStateException("BufferBuilder already building!");
		} else {
			this.building = true;
			this.format = format;
			this.type = type;
			this.vertexCount = 0;
			this.indexCount = 0;
			this.currentElementIndex = -1;
			this.buffer.position(this.writtenBytes);
		}
	}
	
	@Override
	public void end() {
		if (this.building) {
			if (!this.buildingIndecies) {
				this.type.buildDefaultIndecies(this.vertexCount, this::index);
			}
			this.drawStates.add(new DrawState(this.vertexCount, this.indexCount, this.format, this.type));
			this.vertexCount = 0;
			this.indexCount = 0;
			this.building = false;
			this.buildingIndecies = false;
			this.writtenBytes = buffer.position();
		}
	}

	/**
	 * Returns the vertex-data element that is required next in the order of the format specified in the {@link #begin(RenderPrimitive, VertexFormat)} call.
	 * @return The next required vertex data element of the specified attribute format
	 */
	public VertexElement getCurrentElement() {
		return currentElement;
	}
	
	@Override
	public IVertexConsumer nextElement() {
		if (!this.building) throw new IllegalStateException("Buffer not building!");
		this.currentElementIndex++;
		if (this.currentElementIndex == this.format.getElementCount()) throw new IllegalStateException("The current VertexFormat does not have more than " + this.format.getElementCount() + " elements!");
		this.currentElement = this.format.getElements().get(currentElementIndex);
		return this;
	}

	@Override
	public IVertexConsumer putFloat(float f) {
		this.buffer.putFloat(f);
		return this;
	}
	@Override
	public IVertexConsumer putInt(int i) {
		this.buffer.putInt(i);
		return this;
	}
	@Override
	public IVertexConsumer putByte(byte b) {
		this.buffer.put(b);
		return this;
	}
	@Override
	public IVertexConsumer putIntArr(int... intArr) {
		for (int i : intArr) this.buffer.putInt(i);
		return this;
	}
	@Override
	public IVertexConsumer putFloatArr(float... floatArr) {
		for (float f : floatArr) this.buffer.putFloat(f);
		return this;
	}
	@Override
	public IVertexConsumer putByteArr(byte... floatArr) {
		for (byte b : floatArr) this.buffer.put(b);
		return this;
	}

	@Override
	public IVertexConsumer vec2f(float x, float y) {
		nextElement();
		if (getCurrentElement().format() != NumberFormat.FLOAT) throw new IllegalStateException("VertexFormat requires diffrent number format!");
		putFloat(x);
		putFloat(y);
		return this;
	}
	
	@Override
	public IVertexConsumer vec3f(float x, float y, float z) {
		nextElement();
		if (getCurrentElement().format() != NumberFormat.FLOAT) throw new IllegalStateException("VertexFormat requires diffrent number format!");
		putFloat(x);
		putFloat(y);
		putFloat(z);
		return this;
	}
	
	@Override
	public IVertexConsumer vec4f(float x, float y, float z, float w) {
		nextElement();
		if (getCurrentElement().format() != NumberFormat.FLOAT) throw new IllegalStateException("VertexFormat requires diffrent number format!");
		putFloat(x);
		putFloat(y);
		putFloat(z);
		putFloat(w);
		return this;
	}

	@Override
	public IVertexConsumer vec2i(int x, int y) {
		nextElement();
		if (getCurrentElement().format() != NumberFormat.INT && getCurrentElement().format() != NumberFormat.UINT) throw new IllegalStateException("VertexFormat requires diffrent number format!");
		putInt(x);
		putInt(y);
		return this;
	}
	
	@Override
	public IVertexConsumer vec3i(int x, int y, int z) {
		nextElement();
		if (getCurrentElement().format() != NumberFormat.INT && getCurrentElement().format() != NumberFormat.UINT) throw new IllegalStateException("VertexFormat requires diffrent number format!");
		putInt(x);
		putInt(y);
		putInt(z);
		return this;
	}
	
	@Override
	public IVertexConsumer vec4i(int x, int y, int z, int w) {
		nextElement();
		if (getCurrentElement().format() != NumberFormat.INT && getCurrentElement().format() != NumberFormat.UINT) throw new IllegalStateException("VertexFormat requires diffrent number format!");
		putInt(x);
		putInt(y);
		putInt(z);
		putInt(w);
		return this;
	}

	@Override
	public IVertexConsumer vec2b(byte x, byte y) {
		nextElement();
		if (getCurrentElement().format() != NumberFormat.BYTE && getCurrentElement().format() != NumberFormat.UBYTE) throw new IllegalStateException("VertexFormat requires diffrent number format!");
		putByte(x);
		putByte(y);
		return this;
	}
	
	@Override
	public IVertexConsumer vec3b(byte x, byte y, byte z) {
		nextElement();
		if (getCurrentElement().format() != NumberFormat.BYTE && getCurrentElement().format() != NumberFormat.UBYTE) throw new IllegalStateException("VertexFormat requires diffrent number format!");
		putByte(x);
		putByte(y);
		putByte(z);
		return this;
	}
	
	@Override
	public IVertexConsumer vec4b(byte x, byte y, byte z, byte w) {
		nextElement();
		if (getCurrentElement().format() != NumberFormat.BYTE && getCurrentElement().format() != NumberFormat.UBYTE) throw new IllegalStateException("VertexFormat requires diffrent number format!");
		putByte(x);
		putByte(y);
		putByte(z);
		putByte(w);
		return this;
	}

	@Override
	public void endVertex() {
		if (this.currentElementIndex != this.format.getElementCount() - 1) {
			throw new IllegalStateException("Not all elements filled!");
		} else if (this.buildingIndecies) {
			throw new IllegalStateException("Cant put vertices after indecies!");
		} else {
			this.currentElementIndex = -1;
			this.vertexCount++;
		}
		ensureCapacity(this.format.getSize());
	}

	@Override
	public IVertexConsumer index(int i) {
		if (!this.building) throw new IllegalStateException("Buffer not building!");
		if (this.currentElementIndex != -1) {
			throw new IllegalStateException("Not all elements filled!");
		} else {
			this.buildingIndecies = true;
			putInt(i);
			this.indexCount++;
		}
		return this;
	}

	@Override
	public IVertexConsumer indecies(int... i) {
		if (!this.building) throw new IllegalStateException("Buffer not building!");
		if (this.currentElementIndex != -1) {
			throw new IllegalStateException("Not all elements filled!");
		} else {
			this.buildingIndecies = true;
			putIntArr(i);
			this.indexCount += i.length;
		}
		return this;
	}
	
}
