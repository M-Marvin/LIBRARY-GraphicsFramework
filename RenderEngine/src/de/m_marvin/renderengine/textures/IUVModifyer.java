package de.m_marvin.renderengine.textures;

import de.m_marvin.unimat.impl.Matrix3f;

public interface IUVModifyer {
	
	public float mapU(float u);
	public float mapV(float v);
	public Matrix3f frameMatrix();
	public Matrix3f lastFrameMatrix();
	public boolean doFrameInterpolation();
	
}
