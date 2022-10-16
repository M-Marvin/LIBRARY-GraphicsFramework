package de.m_marvin.univec;

import de.m_marvin.univec.impl.Vec3f;

public class Rotation {
	
	private Rotation() {}
	
	public static final Vec3f XP = new Vec3f(+1F, 0F, 0F);
	public static final Vec3f XN = new Vec3f(-1F, 0F, 0F);
	public static final Vec3f YP = new Vec3f(0F, +1F, 0F);
	public static final Vec3f YN = new Vec3f(0F, -1F, 0F);
	public static final Vec3f ZP = new Vec3f(0F, 0F, +1F);
	public static final Vec3f ZN = new Vec3f(0F, 0F, -1F);
	
}
