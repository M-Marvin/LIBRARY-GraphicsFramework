package de.m_marvin.unimat.impl;

import de.m_marvin.unimat.api.IQuaternion;
import de.m_marvin.univec.api.IVector3;
import de.m_marvin.univec.impl.Vec3f;

public class Quaternion implements IQuaternion<Quaternion> {
	
	private float i;
	private float j;
	private float k;
	private float r;

	public Quaternion(float i, float j, float k, float r) {
		this.i = i;
		this.j = j;
		this.k = k;
		this.r = r;
	}

	public Quaternion(IVector3<?> axisVec, float deg) {
		float f = (float) Math.sin(deg / 2.0F);
		this.i = (float) axisVec.x() * f;
		this.j = (float) axisVec.y() * f;
		this.k = (float) axisVec.z() * f;
		this.r = (float) Math.cos(deg / 2.0F);
	}

	public static Quaternion fromXYZDegrees(IVector3<?> vec) {
		return fromXYZRadians(
				(float)Math.toRadians((double)vec.x()), 
				(float)Math.toRadians((double)vec.y()), 
				(float)Math.toRadians((double)vec.z())
			);
	}

	public IVector3<Float> toXYZDegrees() {
		IVector3<Float> vector3f = this.toXYZRadians();
		return new Vec3f(
				(float)Math.toDegrees((double)vector3f.x()), 
				(float)Math.toDegrees((double)vector3f.y()), 
				(float)Math.toDegrees((double)vector3f.z())
			);
	}

	public static Quaternion fromXYZRadians(float x, float y, float z) {
		Quaternion quaternion = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
		quaternion.mulI(new Quaternion((float)Math.sin((double)(x / 2.0F)), 0.0F, 0.0F, (float)Math.cos((double)(x / 2.0F))));
		quaternion.mulI(new Quaternion(0.0F, (float)Math.sin((double)(y / 2.0F)), 0.0F, (float)Math.cos((double)(y / 2.0F))));
		quaternion.mulI(new Quaternion(0.0F, 0.0F, (float)Math.sin((double)(z / 2.0F)), (float)Math.cos((double)(z / 2.0F))));
		return quaternion;
	}
	
	public IVector3<Float> toXYZRadians() {
		float f = this.r() * this.r();
		float f1 = this.i() * this.i();
		float f2 = this.j() * this.j();
		float f3 = this.k() * this.k();
		float f4 = f + f1 + f2 + f3;
		float f5 = 2.0F * this.r() * this.i() - 2.0F * this.j() * this.k();
		float f6 = (float)Math.asin((double)(f5 / f4));
		return Math.abs(f5) > 0.999F * f4 ? new Vec3f(2.0F * (float)Math.atan2((double)this.i(), (double)this.r()), f6, 0.0F) : new Vec3f((float)Math.atan2((double)(2.0F * this.j() * this.k() + 2.0F * this.i() * this.r()), (double)(f - f1 - f2 + f3)), f6, (float)Math.atan2((double)(2.0F * this.i() * this.j() + 2.0F * this.r() * this.k()), (double)(f + f1 - f2 - f3)));
	}

	@Override
	public float i() {
		return i;
	}

	@Override
	public float j() {
		return j;
	}

	@Override
	public float k() {
		return k;
	}

	@Override
	public float r() {
		return r;
	}

	@Override
	public Quaternion setI(float i, float j, float k, float r) {
		this.i = i;
		this.j = j;
		this.k = k;
		this.r = r;
		return this;
	}

	@Override
	public Quaternion mul(Quaternion quat) {
			float f = this.i();
			float f1 = this.j();
			float f2 = this.k();
			float f3 = this.r();
			float f4 = quat.i();
			float f5 = quat.j();
			float f6 = quat.k();
			float f7 = quat.r();
			float i = f3 * f4 + f * f7 + f1 * f6 - f2 * f5;
			float j = f3 * f5 - f * f6 + f1 * f7 + f2 * f4;
			float k = f3 * f6 + f * f5 - f1 * f4 + f2 * f7;
			float r = f3 * f7 - f * f4 - f1 * f5 - f2 * f6;
			return new Quaternion(i, j, k, r);
	}

	@Override
	public Quaternion mul(float f) {
		return new Quaternion(i * f, j * f, k, r);
	}

	@Override
	public Quaternion copy() {
		return new Quaternion(i, j, k, r);
	}

}
