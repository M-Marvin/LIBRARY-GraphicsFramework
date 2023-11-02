package de.m_marvin.enginetest;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import de.m_marvin.enginetest.particles.Particle;
import de.m_marvin.univec.impl.Vec3d;

public class Space3D {
	
	private final double gravitationalConstant = 0.67430E-11; // m^3/(kg*s^2)
	private final double electricalFieldConstant = 8.8541878128E-12; // As/Vm
	
	private final Function<Double, Double> couplingParameterStrongForce = r -> 0.1 + Math.min(0.4, Math.max(0.0, (r - 0.001E-15) / (0.2E-15 - 0.001E-15)));
	private final double couplingParameterWeakForce = 1.0 / 30.0;
	private final double rangeWeakForce = 0.002E-15 * 10000E100;
	
	private final double h = 6.6260755E-34;
	private final double lightSpeed = 299792458;
	
	
	private Set<Particle> particles = new HashSet<>();
	
	public Set<Particle> getParticles() {
		return particles;
	}
	
	public Vec3d calculateGravitationalForce(Particle p1, Particle p2) {
		double d = (p1.getMass() * p2.getMass()) / p1.getPosition().distSqr(p2.getPosition());
		double a = this.gravitationalConstant;
		return p2.getPosition().sub(p1.getPosition()).normalize().mul(a * d);
	}
	
	public Vec3d calculateElectricalForce(Particle p1, Particle p2) {
		double d = (p1.getCharge() * p2.getCharge()) / p1.getPosition().distSqr(p2.getPosition());
		double a = 1 / (4 * Math.PI * this.electricalFieldConstant);
		return p2.getPosition().sub(p1.getPosition()).normalize().mul(a * d);
	}
	
	public Vec3d calculateStrongNuclearForce(Particle p1, Particle p2) {
		double r = p1.getPosition().dist(p2.getPosition());
		double d = p1.getColorCharge().dot(p2.getColorCharge()) / r;
		double a = (this.h / (2 * Math.PI)) * this.lightSpeed * this.couplingParameterStrongForce.apply(r);
		return p2.getPosition().sub(p1.getPosition()).normalize().mul(a * d);
	}
	
	public Vec3d calculateWeakNuclearForce(Particle p1, Particle p2) {
		double r = p1.getPosition().dist(p2.getPosition());
		if (r > this.rangeWeakForce) return new Vec3d();
		double d = (p1.getWeakCharge() * p2.getWeakCharge()) / r;
		double a = (this.h / (2 * Math.PI)) * this.lightSpeed * this.couplingParameterWeakForce;
		return p2.getPosition().sub(p1.getPosition()).normalize().mul(a * d);
	}
	
	public void stepPhysic(double partialSecond) {
		
		for (Particle p : particles) {
			p.getPosition().addI(p.getVelocity().mul(partialSecond));
		}
		
		for (Particle p : particles) {
			for (Particle p2 : particles) {
				if (p2 == p) continue;
				
				Vec3d forceGravitational = 	calculateGravitationalForce(p, p2);
				Vec3d forceElectric = 		calculateElectricalForce(p, p2);
				Vec3d forceStrongNuclear = 	calculateStrongNuclearForce(p, p2);
				Vec3d forceWeakNucler = 	calculateWeakNuclearForce(p, p2);
				Vec3d accelleration = forceGravitational.add(forceElectric).add(forceStrongNuclear).add(forceWeakNucler).div(p.getMass());
				
				p.getVelocity().addI(accelleration.mul(partialSecond));
			}
		}
		
	}
	
}
