package de.m_marvin.gframe.translation;

import java.util.ArrayList;
import java.util.List;

import de.m_marvin.gframe.vertices.IVertexConsumer;
import de.m_marvin.unimat.api.IQuaternionMath.EulerOrder;
import de.m_marvin.unimat.impl.Matrix3f;
import de.m_marvin.unimat.impl.Matrix4f;
import de.m_marvin.unimat.impl.Quaternionf;
import de.m_marvin.unimat.MatUtil;
import de.m_marvin.univec.api.IVector3;
import de.m_marvin.univec.impl.Vec3f;

/**
 * Used to apply translations to draw calls of the {@link IVertexConsumer}.
 * 
 * @author Marvin Köhler
 *
 */
public class PoseStack {
	
	protected List<Pose> poseStack = new ArrayList<>();
	public static record Pose(Matrix3f normal, Matrix4f pose) {}
	
	public PoseStack() {
		this.poseStack.add(new Pose(new Matrix3f(), new Matrix4f()));
	}
	
	public Pose last() {
		return poseStack.get(this.poseStack.size() - 1);
	}
	
	public void push() {
		Matrix3f normal = last().normal().copy();
		Matrix4f pose = last().pose().copy();
		poseStack.add(new Pose(normal, pose));
	}
	
	public void pop() {
		if (cleared()) throw new IllegalStateException("Cant pop state, stack already cleared!");
		poseStack.remove(poseStack.size() - 1);
	}
	
	public void translate(IVector3<?> vec) {
		translate(vec.x().floatValue(), vec.y().floatValue(), vec.z().floatValue());
	}
	public void translate(float x, float y, float z) {
		this.last().pose.mulI(MatUtil.translateMatrixF(x, y, z));
	}
	
	public void scale(float sx, float sy, float sz) {
		PoseStack.Pose posestack$pose = last();
		posestack$pose.pose.mulI(MatUtil.scaleMatrixF(sx, sy, sz));
		if (sx == sy && sy == sz) {
			if (sx > 0.0F) {
				return;
			}
			posestack$pose.normal.scalarI(-1.0F);
		}

		float f = 1.0F / sx;
		float f1 = 1.0F / sy;
		float f2 = 1.0F / sz;
		float f3 = fastInvCubeRoot(f * f1 * f2);
		posestack$pose.normal.mulI(MatUtil.createScaleMatrixF(f3 * f, f3 * f1, f3 * f2));
	}
	
	private static float fastInvCubeRoot(float p_14200_) {
		int i = Float.floatToIntBits(p_14200_);
		i = 1419967116 - i / 3;
		float f = Float.intBitsToFloat(i);
		f = 0.6666667F * f + 1.0F / (3.0F * f * f * p_14200_);
		return 0.6666667F * f + 1.0F / (3.0F * f * f * p_14200_);
	}
	
	public void rotateRadians(float x, float y, float z) {
		rotate(new Quaternionf(new Vec3f(x, y, z), EulerOrder.XYZ, false));
	}
	public void rotateDegrees(float x, float y, float z) {
		rotate(new Quaternionf(new Vec3f(x, y, z), EulerOrder.XYZ, true));
	}
	public void rotate(Quaternionf quat) {
		Pose pose = last();
		pose.normal().mulI(quat);
		pose.pose().mulI(quat);
	}
	
	public boolean cleared() {
		return this.poseStack.size() == 1;
	}
	
	public void assertCleared() {
		if (!cleared())
			throw new TranslationStackException("PoseStack not cleared!");
	}
	
}
