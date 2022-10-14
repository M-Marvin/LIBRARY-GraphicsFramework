package de.m_marvin.renderengine.vertecies;

import java.util.ArrayList;
import java.util.List;

import de.m_marvin.unimat.impl.Matrix3f;
import de.m_marvin.unimat.impl.Matrix4f;
import de.m_marvin.unimat.impl.Quaternion;
import de.m_marvin.univec.api.IVector3;
import de.m_marvin.univec.impl.Vec3f;

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
		translate((float) vec.x(), (float) vec.y(), (float) vec.z());
	}
	public void translate(float x, float y, float z) {
		this.last().pose.mul(Matrix4f.createTranslateMatrix(x, y, z));
	}
	
	public void scale(float sx, float sy, float sz) {
		PoseStack.Pose posestack$pose = last();
		posestack$pose.pose.mulI(Matrix4f.createScaleMatrix(sx, sy, sz));
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
		posestack$pose.normal.mul(Matrix3f.createScaleMatrix(f3 * f, f3 * f1, f3 * f2));
	}
	
	private static float fastInvCubeRoot(float p_14200_) {
		int i = Float.floatToIntBits(p_14200_);
		i = 1419967116 - i / 3;
		float f = Float.intBitsToFloat(i);
		f = 0.6666667F * f + 1.0F / (3.0F * f * f * p_14200_);
		return 0.6666667F * f + 1.0F / (3.0F * f * f * p_14200_);
	}
	
	public void rotateRadians(float x, float y, float z, float angle) {
		multiplyTransformation(new Quaternion(new Vec3f(x, y, z), angle));
	}
	public void rotateDegrees(float x, float y, float z, float angle) {
		multiplyTransformation(new Quaternion(new Vec3f(x, y, z), (float) Math.toRadians(angle)));
	}
	
	public void multiplyTransformation(Quaternion quat) {
		Pose pose = last();
		pose.normal().mul(quat);
		pose.pose().mul(quat);
	}
	
	public boolean cleared() {
		return this.poseStack.size() == 1;
	}
	
}
