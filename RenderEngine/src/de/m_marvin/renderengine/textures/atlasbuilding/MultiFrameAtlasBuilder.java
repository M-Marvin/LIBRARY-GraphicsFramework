package de.m_marvin.renderengine.textures.atlasbuilding;

import java.util.List;

public class MultiFrameAtlasBuilder {
	
	public static record AtlasMultiFrameImage(int width, int height, int[] frames, int[] pixels, boolean interpolated, int frametime) {}
	
	protected List<AtlasMultiFrameImage> atlasImages;
	
	public void addAtlasImage(AtlasMultiFrameImage image) {
		this.atlasImages.add(image);
	}
	
	public void addAtlasImage(int width, int height, int[] frames, int[] pixels, boolean interpolated, int frametime) {
		this.atlasImages.add(new AtlasMultiFrameImage(width, height, frames, pixels, interpolated, frametime));
	}
	
	public void buildLayout(boolean prioritizeHeight) {
		
		
		
	}
	
}
