package de.m_marvin.enginetest.particles;

import de.m_marvin.enginetest.ParticleType;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec4f;

public abstract class Particle {

	public static final Vec3d COLOR_CHARGE_RED = new Vec3d(+1, 0, 0);
	public static final Vec3d COLOR_CHARGE_GREEN = new Vec3d(0, +1, 0);
	public static final Vec3d COLOR_CHARGE_BLUE = new Vec3d(0, 0, +1);
	public static final Vec3d COLOR_CHARGE_ANTI_RED = new Vec3d(-1, 0, 0);
	public static final Vec3d COLOR_CHARGE_ANTI_GREEN = new Vec3d(0, -1, 0);
	public static final Vec3d COLOR_CHARGE_ANTI_BLUE = new Vec3d(0, 0, -1);
	public static final Vec3d COLOR_CHARGE_WHITE = new Vec3d(0, 0, 0);
	
	public static final double WEAK_CHARGE_HP = +0.5;
	public static final double WEAK_CHARGE_HN = -0.5;
	public static final double WEAK_CHARGE_P = +1.0;
	public static final double WEAK_CHARGE_N = -1.0;
	public static final double WEAK_CHARGE_Z = 0.0;
	
	// Velocity in m/s
	protected Vec3d velocity = new Vec3d();
	// Position in m
	protected Vec3d position = new Vec3d();
	// Type of this particle
	protected final ParticleType type;
	
	public Particle(ParticleType type, Vec3d position, Vec3d velocity) {
		this.type = type;
		this.position = position;
		this.velocity = velocity;
	}
	
	public ParticleType getType() {
		return type;
	}
	
	public Vec3d getPosition() {
		return position;
	}
	
	public Vec3d getVelocity() {
		return velocity;
	}

	/**
	 * Mass in kg
	 */
	public abstract double getMass();

	/**
	 * Charge in C
	 */
	public abstract double getCharge();
	
	/**
	 * Color charge
	 */
	public abstract Vec3d getColorCharge();
	
	/**
	 * Weak charge
	 */
	public abstract double getWeakCharge();

	public abstract Vec4f getColor();
	
	public float getSize() {
		// TODO Auto-generated method stub
		return 1000;
	}
	
}
