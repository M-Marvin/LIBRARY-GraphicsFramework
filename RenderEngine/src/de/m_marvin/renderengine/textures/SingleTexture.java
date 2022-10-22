package de.m_marvin.renderengine.textures;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;

import de.m_marvin.renderengine.GLStateManager;

public class SingleTexture implements ITextureSampler {
	
	protected int textureId;
	protected IntBuffer pixels;
	protected final int width;
	protected final int height;
	
	public SingleTexture() {
		this.width = 2;
		this.height = 2;
		this.pixels = BufferUtils.createIntBuffer(4);
		this.pixels.put(new Color(255, 0, 0).getRGB());
		this.pixels.put(new Color(0, 255, 0).getRGB());
		this.pixels.put(new Color(0, 0, 255).getRGB());
		this.pixels.put(new Color(255, 0, 255).getRGB());
		this.pixels.flip();
		init();
	}
	
	public SingleTexture(InputStream imageStream) throws IOException {
		
		BufferedImage imageBuffer = ImageIO.read(imageStream);
		this.width = imageBuffer.getWidth();
		this.height = imageBuffer.getHeight();
		int[] pixelArr = imageBuffer.getRGB(0, 0, width, height, null, 0, width);
		this.pixels = BufferUtils.createIntBuffer(pixelArr.length);
		this.pixels.put(pixelArr);
		this.pixels.flip();
		init();
		
	}
	
	public void init() {
		this.textureId = GLStateManager.genTexture();
		GLStateManager.bindTexture(GL33.GL_TEXTURE_2D, textureId);
		GLStateManager.textureParameter(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_NEAREST);
		GLStateManager.textureParameter(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_NEAREST);
		GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGBA8, width, height, 0, GL33.GL_BGRA, GL33.GL_UNSIGNED_INT_8_8_8_8_REV, pixels);
		//GLStateManager.loadTexture(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGBA8, GL33.GL_BGRA, width, height, 0, GL33.GL_UNSIGNED_INT_8_8_8_8_REV, pixels);
		GLStateManager.bindTexture(GL33.GL_TEXTURE_2D, 0);
	}
	
	@Override
	public void bindTexture(int samplerId) {
		GLStateManager.activeTexture(samplerId);
		GLStateManager.bindTexture(GL33.GL_TEXTURE_2D, textureId);
	}
	
}
