package de.m_marvin.renderengine.vertices;

import de.m_marvin.renderengine.textures.IUVModifyer;
import de.m_marvin.renderengine.translation.PoseStack;
import de.m_marvin.univec.api.IVector3;
import de.m_marvin.univec.api.IVector4;
import de.m_marvin.univec.impl.Vec2f;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec4f;

/**
 * Contains all drawing methods required to fill a {@link BufferBuilder} with the neccessary data for the geometry to draw.
 * For some important attributes are predefined layout available (vertex, normal, color, uv).
 * Other attribute layouts have the be implemented or build manually with the {@link #nextElement()} and primitive put methods.
 * Its important to strictly follow the attribute order of the used vertex format.
 * 
 * @author Marvin Köhler
 *
 */
public interface IVertexConsumer {
	
	/* Predefined names and layouts for common vertex attributes */
	
	/**
	 * Predefined layout for the vertex position attribute
	 * @param poseStack PoseStack containing transformations that have to be applied to the vertex
	 * @param x The vertex x position
	 * @param y The vertex y position
	 * @param z The vertex z position
	 * @return This consumer to apply more draw calls
	 */
	public default IVertexConsumer vertex(PoseStack poseStack, float x, float y, float z) {
		IVector4<Float> vec = poseStack.last().pose().translate(new Vec4f(x, y, z, 1));
		return vertex(vec.x(), vec.y(), vec.z());
	}

	/**
	 * Predefined layout for the vertex position attribute with only x and y
	 * @param poseStack PoseStack containing transformations that have to be applied to the vertex
	 * @param x The vertex x position
	 * @param y The vertex y position
	 * @return This consumer to apply more draw calls
	 */
	public default IVertexConsumer vertex(PoseStack poseStack, float x, float y) {
		IVector4<Float> vec = poseStack.last().pose().translate(new Vec4f(x, y, 0, 1));
		return vec2f(vec.x(), vec.y());
	}
	
	/**
	 * Predefined layout for the vertex position attribute with only x and y
	 * @param x The vertex x position
	 * @param y The vertex y position
	 * @return This consumer to apply more draw calls
	 */
	public default IVertexConsumer vertex(float x, float y) {
		return vec2f(x, y);
	}

	/**
	 * Predefined layout for the vertex position attribute
	 * @param x The vertex x position
	 * @param y The vertex y position
	 * @param z The vertex z position
	 * @return This consumer to apply more draw calls
	 */
	public default IVertexConsumer vertex(float x, float y, float z) {
		return vec3f(x, y, z);
	}
	
	/**
	 * Predefined layout for the normal attribute
	 * @param poseStack Pose stack contains transformations that have to be applied to the vertex
	 * @param x The normal x value
	 * @param y The normal y value
	 * @param z The normal z value
	 * @return This consumer to apply more draw calls
	 */
	public default IVertexConsumer normal(PoseStack poseStack, float x, float y, float z) {
		IVector3<Float> vec = poseStack.last().normal().translate(new Vec3f(x, y, z));
		return normal(vec.x(), vec.y(), vec.z());
	}
	
	/**
	 * Predefined layout for the normal attribute
	 * @param poseStack Pose stack contains transformations that have to be applied to the vertex
	 * @param x The normal x value
	 * @param y The normal y value
	 * @param z The normal z value
	 * @return This consumer to apply more draw calls
	 */
	public default IVertexConsumer normal(float x, float y, float z) {
		return vec3f(x, y, z);
	}
	
	/**
	 * Predefined layout for the color attribute
	 * @param r The color red value
	 * @param g The color green value
	 * @param b The color blue value
	 * @param a The color alpha value
	 * @return This consumer to apply more draw calls
	 */
	public default IVertexConsumer color(float r, float g, float b, float a) {
		return vec4f(r, g, b, a);
	}
	
	/**
	 * Predefined layout for the UV attribute
	 * @param modifier An UV modifier (mostly a atlas texture map) modifying the provided UV positions.
	 * @param u The texture u position
	 * @param b The texture v position
	 * @return This consumer to apply more draw calls
	 */
	public default IVertexConsumer uv(IUVModifyer modifier, float u, float v) {
		return uv(modifier.mapU(u), modifier.mapV(v));
	}
	
	/**
	 * Predefined layout for the UV attribute
	 * @param u The texture u position
	 * @param b The texture v position
	 * @return This consumer to apply more draw calls
	 */
	public default IVertexConsumer uv(float u, float v) {
		return vec2f(u, v);
	}
		
	/* Predefined layouts for common data types attributes */
	
	/**
	 * Predefined layout for a vec2 float attribute.
	 * @param x The vec2 x value
	 * @param y The vec2 y value
	 * @return This consumer to apply more draw calls
	 */
	public IVertexConsumer vec2f(float x, float y);

	/**
	 * Predefined layout for a vec3 float attribute.
	 * @param x The vec2 x value
	 * @param y The vec2 y value
	 * @param z The vec2 z value
	 * @return This consumer to apply more draw calls
	 */
	public IVertexConsumer vec3f(float x, float y, float z);

	/**
	 * Predefined layout for a vec4 float attribute.
	 * @param x The vec2 x value
	 * @param y The vec2 y value
	 * @param z The vec2 z value
	 * @param w The vec2 w value
	 * @return This consumer to apply more draw calls
	 */
	public IVertexConsumer vec4f(float x, float y, float z, float w);

	/**
	 * Predefined layout for a vec2 integer attribute.
	 * @param x The vec2 x value
	 * @param y The vec2 y value
	 * @return This consumer to apply more draw calls
	 */
	public IVertexConsumer vec2i(int x, int y);

	/**
	 * Predefined layout for a vec3 integer attribute.
	 * @param x The vec2 x value
	 * @param y The vec2 y value
	 * @param z The vec2 z value
	 * @return This consumer to apply more draw calls
	 */
	public IVertexConsumer vec3i(int x, int y, int z);

	/**
	 * Predefined layout for a vec4 integer attribute.
	 * @param x The vec2 x value
	 * @param y The vec2 y value
	 * @param z The vec2 z value
	 * @param w The vec2 w value
	 * @return This consumer to apply more draw calls
	 */
	public IVertexConsumer vec4i(int x, int y, int z, int w);

	/**
	 * Predefined layout for a vec2 byte attribute.
	 * @param x The vec2 x value
	 * @param y The vec2 y value
	 * @return This consumer to apply more draw calls
	 */
	public IVertexConsumer vec2b(byte x, byte y);

	/**
	 * Predefined layout for a vec3 byte attribute.
	 * @param x The vec2 x value
	 * @param y The vec2 y value
	 * @param z The vec2 z value
	 * @return This consumer to apply more draw calls
	 */
	public IVertexConsumer vec3b(byte x, byte y, byte z);

	/**
	 * Predefined layout for a vec4 byte attribute.
	 * @param x The vec2 x value
	 * @param y The vec2 y value
	 * @param z The vec2 z value
	 * @param w The vec2 w value
	 * @return This consumer to apply more draw calls
	 */
	public IVertexConsumer vec4b(byte x, byte y, byte z, byte w);
	
	/* End of predefined layouts */
	
	/**
	 * Loads the first or next attribute from the current vertex attribute format ({@link VertexFormat}).
	 * The order to add data for an attribute is always: nextElement() -> series of put()-methods -> continue with next element.
	 * The standard implementation of {@link #vertex(float, float, float)} for example would be: {@link #nextElement()}.{@link #putFloat(x)}.{@link #putFloat(y)}.{@link #putFloat(z)}
	 * 
	 * @return This consumer to apply more draw calls
	 */
	public IVertexConsumer nextElement();
	
	/**
	 * Accepts a integer value for the current attribute.
	 * @param i The value
	 * @return This consumer to apply more draw calls
	 */
	public IVertexConsumer putInt(int i);

	/**
	 * Accepts a float value for the current attribute.
	 * @param f The value
	 * @return This consumer to apply more draw calls
	 */
	public IVertexConsumer putFloat(float f);
	
	/**
	 * Accepts a float byte for the current attribute.
	 * @param b The value
	 * @return This consumer to apply more draw calls
	 */
	public IVertexConsumer putByte(byte b);
	
	/**
	 * Accepts a integer array value for the current attribute.
	 * @param intArr The value
	 * @return This consumer to apply more draw calls
	 */
	public IVertexConsumer putIntArr(int... intArr);
	
	/**
	 * Accepts a float array value for the current attribute.
	 * @param floatArr The value
	 * @return This consumer to apply more draw calls
	 */
	public IVertexConsumer putFloatArr(float... floatArr);
	
	/**
	 * Accepts a byte array value for the current attribute.
	 * @param byteArr The value
	 * @return This consumer to apply more draw calls
	 */
	public IVertexConsumer putByteArr(byte... byteArr);
	
	/**
	 * Accepts an index value.
	 * Index values are stored after all vertex values are accepted.
	 * Index values always have the integer format in this implementation.
	 * 
	 * @param i The index value
	 * @return This consumer to apply more draw calls
	 */
	public IVertexConsumer index(int i);
	
	/**
	 * Accepts multiple index values.
	 * Index values are stored after all vertex values are accepted.
	 * Index values always have the integer format in this implementation.
	 * 
	 * @param i The index value
	 * @return This consumer to apply more draw calls
	 */
	public IVertexConsumer indecies(int... i);
	
	/**
	 * Completes a vertex with its attributes.
	 * After this method is called, the first attribute of the next element can be started with {@link #nextElement()}.
	 */
	public void endVertex();
	
}
