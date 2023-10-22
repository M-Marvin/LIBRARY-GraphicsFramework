package de.m_marvin.enginetest.particles;

import de.m_marvin.enginetest.ParticleType;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec4f;

public class Electron extends Particle {

	public static final double ELEMENTAL_CHARGE = 1.602176634E-19;
	
	public Electron(Vec3d position, Vec3d velocity) {
		super(ParticleType.ELECTRON, position, velocity);
	}

	@Override
	public Vec4f getColor() {
		return new Vec4f(0, 0, 1, 1);
	}

	@Override
	public double getMass() {
		return 9.1093837015E-31;
	}

	@Override
	public double getCharge() {
		return -ELEMENTAL_CHARGE;
	}

	@Override
	public Vec3d getColorCharge() {
		return COLOR_CHARGE_WHITE;
	}

	@Override
	public double getWeakCharge() {
		return WEAK_CHARGE_HN;
	}
	
}
