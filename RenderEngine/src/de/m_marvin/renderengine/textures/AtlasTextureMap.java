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

public class AtlasTextureMap<R extends IResourceProvider<R>> extends AbstractTextureMap<R> {
	
	protected Map<R, Vec4f> location2uv = new HashMap<>();
	protected R activeTexture = null;
	
	protected record LayoutPair<R>(R location, int[] pixels) {};
	protected MultiFrameAtlasLayoutBuilder<LayoutPair<R>> layoutBuilder;
	protected boolean building;
	
	public AtlasTextureMap() {
		layoutBuilder = new MultiFrameAtlasLayoutBuilder<>();
		building = true;
	}

	public void addTexture(R location, int width, int height, int[] frames, int frametime, boolean interpolate, int[] pixels) {
		if (!building) throw new IllegalStateException("The atlas is already compiled, no more textures can be added!");
		layoutBuilder.addAtlasImage(
			width, 
			height, 
			frames, 
			frametime, 
			interpolate, 
			new LayoutPair<R>(location, pixels)
		);
	}
	
	public void buildAtlas(boolean prioritizeAtlasHeight, boolean interpolate) {
		if (!building) throw new IllegalStateException("The atlas is already compiled!");

		AtlasMultiFrameLayout<LayoutPair<R>> layout = layoutBuilder.buildLayout(prioritizeAtlasHeight);
		BufferedImage atlasImage = new BufferedImage(layout.width(), layout.height(), BufferedImage.TYPE_4BYTE_ABGR);
		
		for (List<AtlasFrameLayout<LayoutPair<R>>> frameLayout : layout.frameLayouts()) {
			for (AtlasFrameLayout<LayoutPair<R>> imageLayout : frameLayout) {
				
				int pixels[] = framePixels(imageLayout.image().pixels(), imageLayout.frame(), imageLayout.frameHeight(), imageLayout.width());
				
				if (imageLayout.interpolate()) {
					
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

	protected static int[] interpolatePixels(int[] pixels1, int[] pixels2, float interpolation) {
		int[] pixels = new int[pixels1.length];
		for (int i = 0; i < pixels1.length; i++) {
			pixels[i] = (int) (pixels1[i] * (1F - interpolation) + pixels2[i] * interpolation); // TODO: Incorrect interpolation, only for testing!
		}
		return pixels;
	}
	
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
