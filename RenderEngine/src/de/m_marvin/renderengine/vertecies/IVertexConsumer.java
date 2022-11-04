package de.m_marvin.renderengine.vertecies;

import de.m_marvin.renderengine.textures.IUVModifyer;
import de.m_marvin.renderengine.translation.PoseStack;
import de.m_marvin.univec.api.IVector3;
import de.m_marvin.univec.api.IVector4;
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
	
	/* Predefined layouts for common vertex attributes */
	
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
	 * Predefined layout for the vertex position attribute
	 * @param x The vertex x position
	 * @param y The vertex y position
	 * @param z The vertex z position
	 * @return This consumer to apply more draw calls
	 */
	public IVertexConsumer vertex(float x, float y, float z);
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
	public IVertexConsumer normal(float x, float y, float z);
	/**
	 * Predefined layout for the color attribute
	 * @param r The color red value
	 * @param g The color green value
	 * @param b The color blue value
	 * @param a The color alpha value
	 * @return This consumer to apply more draw calls
	 */
	public IVertexConsumer color(float r, float g, float b, float a);
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
	public IVertexConsumer uv(float u, float v);
	
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
	 * Accepts a float value for the current attribute.
	 * @param f The value
	 * @return This consumer to apply more draw calls
	 */
	public IVertexConsumer putFloat(float f);
	/**
	 * Accepts a integer value for the current attribute.
	 * @param i The value
	 * @return This consumer to apply more draw calls
	 */
	public IVertexConsumer putInt(int i);
	/**
	 * Accepts a float short for the current attribute.
	 * @param s The value
	 * @return This consumer to apply more draw calls
	 */
	public IVertexConsumer putShort(short s);
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
	 * Accepts a short array value for the current attribute.
	 * @param shortArr The value
	 * @return This consumer to apply more draw calls
	 */
	public IVertexConsumer putShortArr(short... shortArr);
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
