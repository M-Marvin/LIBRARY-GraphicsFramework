package de.m_marvin.renderengine.textures;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import de.m_marvin.renderengine.resources.IResourceProvider;
import de.m_marvin.renderengine.textures.atlasbuilding.MultiFrameAtlasLayoutBuilder;
import de.m_marvin.renderengine.textures.atlasbuilding.MultiFrameAtlasLayoutBuilder.AtlasFrameLayout;
import de.m_marvin.renderengine.textures.atlasbuilding.MultiFrameAtlasLayoutBuilder.AtlasMultiFrameLayout;
import de.m_marvin.univec.impl.Vec4f;

/**
 * The multi texture implementation of the {@link AbstractTextureMap}.
 * Contains multiple textures and reduces the number of texture uploads to the GPU.
 * Can not contains mixed interpolated and non interpolated textures.
 * 
 * @author Marivn Köhler
 *
 * @param <R> The type of the resource locations
 */
public class AtlasTextureMap<R extends IResourceProvider<R>> extends AbstractTextureMap<R> {
	
	protected Map<R, Vec4f> location2uv = new HashMap<>();
	protected R activeTexture = null;
	
	protected record LayoutPair<R>(R location, int[] pixels) {};
	protected MultiFrameAtlasLayoutBuilder<LayoutPair<R>> layoutBuilder;
	protected boolean building;
	
	/**
	 * Creates a new atlas map, ready for adding textures to it.
	 * Before it can be used {@link #buildAtlas(boolean, boolean)} has to be called.
	 */
	public AtlasTextureMap() {
		layoutBuilder = new MultiFrameAtlasLayoutBuilder<>();
		building = true;
	}

	/**
	 * Adds a texture to the atlas.
	 * @param location The name under which the texture should be stored in the atlas
	 * @param width The width of the texture in pixels
	 * @param height The complete height of the texture in pixels (not the height of one frame)
	 * @param frames The frames id array describing the order of animation frames
	 * @param frametime The number of ticks a frame lasts
	 * @param pixels The RGBA pixel data array
	 */
	public void addTexture(R location, int width, int height, int[] frames, int frametime, int[] pixels) {
		if (!building) throw new IllegalStateException("The atlas is already compiled, no more textures can be added!");
		layoutBuilder.addAtlasImage(
			width, 
			height, 
			frames, 
			frametime, 
			new LayoutPair<R>(location, pixels)
		);
	}
	
	/**
	 * Tries to place all textures in the atlas and makes the texture ready for use.
	 * After this method is called, no more textures can be added.
	 * @param prioritizeAtlasHeight Determines the if the textures are aligned on the x or y axis
	 * @param interpolate True if the textures of the atlas need to be interpolated
	 */
	public void buildAtlas(boolean prioritizeAtlasHeight, boolean interpolate) {
		if (!building) throw new IllegalStateException("The atlas is already compiled!");

		AtlasMultiFrameLayout<LayoutPair<R>> layout = layoutBuilder.buildLayout(prioritizeAtlasHeight);
		BufferedImage atlasImage = new BufferedImage(layout.width(), layout.height(), BufferedImage.TYPE_4BYTE_ABGR);
		
		for (List<AtlasFrameLayout<LayoutPair<R>>> frameLayout : layout.frameLayouts()) {
			for (AtlasFrameLayout<LayoutPair<R>> imageLayout : frameLayout) {
				
				int pixels[] = framePixels(imageLayout.image().pixels(), imageLayout.frame(), imageLayout.frameHeight(), imageLayout.width());
				
				if (interpolate) {
					int[] nextPixels = framePixels(imageLayout.image().pixels, imageLayout.nextFrame(), imageLayout.frameHeight(), imageLayout.width());
					pixels = interpolatePixels(pixels, nextPixels, imageLayout.subframe());
				}
				
				atlasImage.setRGB(imageLayout.x(), imageLayout.y(), imageLayout.width(), imageLayout.frameHeight(), pixels, 0, imageLayout.width());
				if (!location2uv.containsKey(imageLayout.image().location())) {
					location2uv.put(
							imageLayout.image().location(), 
							new Vec4f(
								imageLayout.x() / (float) layout.width(), 
								imageLayout.framey() / (float) (layout.height() / layout.frames()),
								imageLayout.width() / (float) layout.width(), 
								imageLayout.frameHeight() / (float) (layout.height() / layout.frames())
							)
					);
				}
				
			}
		}
		
		building = false;
		
		this.width = layout.width();
		this.height = layout.height();
		this.frames = IntStream.range(0, layout.frames()).toArray();
		this.frameHeight = this.height / (IntStream.of(this.frames).max().getAsInt() + 1);
		this.frametime = layout.frametime();
		this.pixels = atlasImage.getRGB(0, 0, this.width, this.height, null, 0, this.width);
		this.interpolate = interpolate;
		updateMatrix();
		init();
	}

	/**
	 * Helper method to interpolate between two frames.
	 * @param pixels1 Pixel data array of frame 1
	 * @param pixels2 Pixel data array of frame 2
	 * @param interpolation Float value between 0 and 1 to describe the interpolation
	 * @return The interpolated pixel data array
	 */
	protected static int[] interpolatePixels(int[] pixels1, int[] pixels2, float interpolation) {
		int[] pixels = new int[pixels1.length];
		for (int i = 0; i < pixels1.length; i++) {
			byte a1 = (byte) (pixels1[i] >> 24);
			byte r1 = (byte) (pixels1[i] >> 16);
			byte g1 = (byte) (pixels1[i] >> 8);
			byte b1 = (byte) (pixels1[i] >> 0);
			byte a2 = (byte) (pixels2[i] >> 24);
			byte r2 = (byte) (pixels2[i] >> 16);
			byte g2 = (byte) (pixels2[i] >> 8);
			byte b2 = (byte) (pixels2[i] >> 0);
			byte a = (byte) (a1 * (1F - interpolation) + a2 * interpolation);
			byte r = (byte) (r1 * (1F - interpolation) + r2 * interpolation);
			byte g = (byte) (g1 * (1F - interpolation) + g2 * interpolation);
			byte b = (byte) (b1 * (1F - interpolation) + b2 * interpolation);
			pixels[i] = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0);
		}
		return pixels;
	}
	
	/**
	 * Helper method to get a array with pixel data of a specific frame copied from the pixel data array of the complete texture.
	 * @param pixels The pixel data array of the texture
	 * @param frame The id of the frame, 0 is the first top frame
	 * @param frameHeight The height of one frame in the texture
	 * @param width The width of the texture
	 * @return The pixel data array of the frame
	 */
	protected static int[] framePixels(int[] pixels, int frame, int frameHeight, int width) {
		
		int begin = frame * frameHeight * width;
		int end = begin + frameHeight * width;
		return Arrays.copyOfRange(pixels, begin, end);
		
	}
	
	@Override
	public void activateTexture(R textureLoc) {
		if (location2uv.containsKey(textureLoc)) activeTexture = textureLoc;
	}

	@Override
	public float mapU(float u) {
		if (activeTexture != null) {
			Vec4f texUV = location2uv.get(activeTexture);
			return texUV.x() + texUV.z() * u;
		}
		return u;
	}

	@Override
	public float mapV(float v) {
		if (activeTexture != null) {
			Vec4f texUV = location2uv.get(activeTexture);
			return texUV.y() + texUV.w() * v;
		}
		return v;
	}
	
}
