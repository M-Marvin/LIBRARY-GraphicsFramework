package de.m_marvin.enginetest;

import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec4f;

public class Particle {
	
	// Mass in kg
	protected double mass = 10;
	// Velocity in m/s
	protected Vec3d velocity = new Vec3d();
	// Position in m
	protected Vec3d position = new Vec3d();
	
	protected Vec4f color = new Vec4f(1, 1, 1, 1);

	public Particle(Vec3d position, double mass, Vec3d velocity, Vec4f color) {
		this.position = position;
		this.mass = mass;
		this.velocity = velocity;
		this.color = color;
	}
	
	public Vec3d getPosition() {
		return position;
	}
	
	public Vec3d getVelocity() {
		return velocity;
	}
	
	public double getMass() {
		return mass;
	}
	
	public Vec4f getColor() {
		return color;
	}

	public float getSize() {
		// TODO Auto-generated method stub
		return 1000;
	}
	
}
