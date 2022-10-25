package de.m_marvin.renderengine.textures;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import de.m_marvin.unimat.impl.Matrix3f;

public class SingleTextureMap extends AbstractTextureMap {

	protected Matrix3f animationMatrixLast;
	protected Matrix3f animationMatrix;
	protected int frameHeight;
	protected int[] frames;
	protected int activeFrame;
	protected int frameTicks;
	protected boolean interpolate;
		
	public SingleTextureMap(int width, int height, int[] frames, int[] pixels, boolean interpolate) {
		this.width = width;
		this.height = height;
		this.frameHeight = this.height / frames.length;
		this.frames = frames;
		this.pixels = pixels;
		this.interpolate = interpolate;
		updateMatrix();
		init();
	}
	public SingleTextureMap(BufferedImage imageSource) {
		this(imageSource.getWidth(), imageSource.getHeight(), new int[] {0}, imageSource.getRGB(0, 0, imageSource.getWidth(), imageSource.getHeight(), null, 0, imageSource.getWidth()), false);
	}
	public SingleTextureMap(InputStream imageStrean) throws IOException {
		this(ImageIO.read(imageStrean));
	}
	public SingleTextureMap(BufferedImage imageSource, int[] frames, boolean interpolate) {
		this(imageSource.getWidth(), imageSource.getHeight(), frames, imageSource.getRGB(0, 0, imageSource.getWidth(), imageSource.getHeight(), null, 0, imageSource.getWidth()), interpolate);
	}
	public SingleTextureMap(InputStream imageStream, int[] frames, boolean interpolate) throws IOException {
		this(ImageIO.read(imageStream), frames, interpolate);
	}
	
	public void updateMatrix() {
		this.animationMatrixLast = this.animationMatrix;
		this.animationMatrix = Matrix3f.createScaleMatrix(1, (float) frameHeight / height).mul(Matrix3f.createTranslationMatrix(0, frames[activeFrame]));
		if (this.animationMatrixLast == null) this.animationMatrixLast = this.animationMatrix;
	}
	
	public void nextFrame() {
		this.activeFrame++;
		if (this.activeFrame >= this.frames.length) this.activeFrame = 0;
		updateMatrix();
	}
	
	public void setFrame(int frame) {
		if (frame < 0) throw new IndexOutOfBoundsException("The frame id can not be smaler than 0!");
		if (this.frames.length <= frame) throw new IndexOutOfBoundsException("The texture has only " + this.frames.length + " frames!");
		this.activeFrame = frame;
		updateMatrix();
	}
	
	@Override
	public float mapU(float u) {
		return u;
	}
	@Override
	public float mapV(float v) {
		return v;
	}
	
	@Override
	public Matrix3f frameMatrix() {
		return this.animationMatrix;
	}
	@Override
	public Matrix3f lastFrameMatrix() {
		return this.interpolate ? this.animationMatrixLast : this.animationMatrix;
	}
	@Override
	public boolean doFrameInterpolation() {
		return this.interpolate;
	}
	
}
