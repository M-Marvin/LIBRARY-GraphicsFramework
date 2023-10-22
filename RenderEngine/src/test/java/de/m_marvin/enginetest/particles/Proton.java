package de.m_marvin.enginetest.particles;

import de.m_marvin.enginetest.ParticleType;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec4f;

public class Proton extends Particle {

	public Proton(Vec3d position, Vec3d velocity) {
		super(ParticleType.PROTON, position, velocity);
	}

	@Override
	public Vec4f getColor() {
		return new Vec4f(1, 0, 0, 1);
	}

	@Override
	public double getMass() {
		return 1.67262192369E-27;
	}

	@Override
	public double getCharge() {
		return +Electron.ELEMENTAL_CHARGE;
	}

	@Override
	public Vec3d getColorCharge() {
		return COLOR_CHARGE_WHITE;
	}

	@Override
	public double getWeakCharge() {
		return WEAK_CHARGE_HP;
	}

}
