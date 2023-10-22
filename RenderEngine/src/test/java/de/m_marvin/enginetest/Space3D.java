package de.m_marvin.enginetest;

import java.util.HashSet;
import java.util.Set;

import de.m_marvin.univec.impl.Vec3d;

public class Space3D {
	
	private final double gravitationalConstant = 0.67430E-11;
	private Set<Particle> particles = new HashSet<>();
	
	public Set<Particle> getParticles() {
		return particles;
	}

	public void stepPhysic(double partialSecond) {
		// TODO Auto-generated method stub
		
		for (Particle p : particles) {
			p.getPosition().addI(p.getVelocity().mul(partialSecond));
		}
		
		for (Particle p : particles) {
			double mass = p.getMass();
			
			for (Particle p2 : particles) {
				if (p2 == p) continue;
				
				Vec3d distance = p2.getPosition().sub(p.getPosition());
				double mass2 = p2.getMass();
				
				Vec3d force = distance.mul(this.gravitationalConstant * ((mass * mass2) / Math.pow(distance.length(), 3)));
				Vec3d accelleration = force.div(mass);
				
				p.getVelocity().addI(accelleration.mul(partialSecond));
				
			}
			
		}
		
	}
	
	
	
}
