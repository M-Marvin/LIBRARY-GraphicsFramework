package de.m_marvin.enginetest;

import java.util.function.BiFunction;

import de.m_marvin.enginetest.particles.Electron;
import de.m_marvin.enginetest.particles.Neutron;
import de.m_marvin.enginetest.particles.Particle;
import de.m_marvin.enginetest.particles.Proton;
import de.m_marvin.univec.impl.Vec3d;

public enum ParticleType {
	ELECTRON(Electron::new),
	NEUTRON(Neutron::new),
	PROTON(Proton::new);
	
	private final BiFunction<Vec3d, Vec3d, Particle> factory;
	
	private ParticleType(BiFunction<Vec3d, Vec3d, Particle> factory) {
		this.factory = factory;
	}
	
	public Particle create(Vec3d position, Vec3d velocity) {
		return this.factory.apply(position, velocity);
	}
	
}
