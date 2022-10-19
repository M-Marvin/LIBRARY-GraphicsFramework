package de.m_marvin.renderengine.buffers;

import de.m_marvin.renderengine.translation.PoseStack;
import de.m_marvin.univec.api.IVector3;
import de.m_marvin.univec.api.IVector4;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec4f;

public interface IVertexConsumer {
	
	/* Predefined layouts for common vertex attributes */
	
	public default IVertexConsumer vertex(PoseStack poseStack, float x, float y, float z) {
		IVector4<Float> vec = poseStack.last().pose().translate(new Vec4f(x, y, z, 1));
		return vertex(vec.x(), vec.y(), vec.z());
	}
	public IVertexConsumer vertex(float x, float y, float z);
	public default IVertexConsumer normal(PoseStack poseStack, float x, float y, float z) {
		IVector3<Float> vec = poseStack.last().normal().translate(new Vec3f(x, y, z));
		return normal(vec.x(), vec.y(), vec.z());
	}
	public IVertexConsumer normal(float x, float y, float z);
	public IVertexConsumer color(float r, float g, float b, float a);
	public IVertexConsumer uv(float u, float v);
	
	/* End of predefined layouts */
	
	public IVertexConsumer nextElement();
	
	public IVertexConsumer putFloat(float f);
	public IVertexConsumer putInt(int i);
	public IVertexConsumer putShort(short s);
	public IVertexConsumer putByte(byte b);
	public IVertexConsumer putIntArr(int... intArr);
	public IVertexConsumer putFloatArr(float... floatArr);
	public IVertexConsumer putShortArr(short... shortArr);
	public IVertexConsumer putByteArr(byte... floatArr);
	
	public IVertexConsumer index(int i);
	public IVertexConsumer indecies(int... i);
	public void endVertex();
	
}
