package de.m_marvin.renderengine.textures;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import de.m_marvin.renderengine.resources.IResourceProvider;

public class SingleTextureMap<R extends IResourceProvider<R>> extends AbstractTextureMap<R> {
	
	public SingleTextureMap(int width, int height, int[] frames, int frametime, int[] pixels, boolean interpolate) {
		this.width = width;
		this.height = height;
		this.frames = frames;
		this.frameHeight = this.height / (IntStream.of(this.frames).max().getAsInt() + 1);
		this.frametime = frametime;
		this.pixels = pixels;
		this.interpolate = interpolate;
		updateMatrix();
		init();
	}
	public SingleTextureMap(BufferedImage imageSource) {
		this(imageSource.getWidth(), imageSource.getHeight(), new int[] {0}, 1, imageSource.getRGB(0, 0, imageSource.getWidth(), imageSource.getHeight(), null, 0, imageSource.getWidth()), false);
	}
	public SingleTextureMap(InputStream imageStrean) throws IOException {
		this(ImageIO.read(imageStrean));
	}
	public SingleTextureMap(BufferedImage imageSource, int[] frames, int frametime, boolean interpolate) {
		this(imageSource.getWidth(), imageSource.getHeight(), frames, frametime, imageSource.getRGB(0, 0, imageSource.getWidth(), imageSource.getHeight(), null, 0, imageSource.getWidth()), interpolate);
	}
	public SingleTextureMap(InputStream imageStream, int[] frames, int frametime, boolean interpolate) throws IOException {
		this(ImageIO.read(imageStream), frames, frametime, interpolate);
	}

	@Override
	public void activateTexture(R textureLoc) {}
	
	@Override
	public float mapU(float u) {
		return u;
	}
	@Override
	public float mapV(float v) {
		return v;
	}
	
}
