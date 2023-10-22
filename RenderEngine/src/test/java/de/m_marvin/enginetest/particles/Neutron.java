package de.m_marvin.enginetest.particles;

import de.m_marvin.enginetest.ParticleType;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec4f;

public class Neutron extends Particle {

	public Neutron(Vec3d position, Vec3d velocity) {
		super(ParticleType.NEUTRON, position, velocity);
	}

	@Override
	public Vec4f getColor() {
		return new Vec4f(1, 1, 1, 1);
	}

	@Override
	public double getMass() {
		return 1.67492749804E-27;
	}

	@Override
	public double getCharge() {
		return 0;
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
