package de.m_marvin.renderengine.textures.atlasbuilding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.m_marvin.renderengine.textures.atlasbuilding.AtlasLayoutBuilder.AtlasImageLayout;
import de.m_marvin.renderengine.textures.atlasbuilding.AtlasLayoutBuilder.AtlasLayout;

public class MultiFrameAtlasLayoutBuilder<T> {
	
	public static record AtlasMultiFrameImage<T>(int width, int height, int frameCount, int[] frames, int frametime, boolean interpolate, T image) {}
	public static record AtlasFrameLayout<T>(int x, int y, int width, int frameHeight, int frame, int nextFrame, float subframe, boolean interpolate, T image) {}
	public static record AtlasMultiFrameLayout<T>(int width, int height, int frames, List<List<AtlasFrameLayout<T>>> frameLayouts) {}
	
	protected List<AtlasMultiFrameImage<T>> atlasImages = new ArrayList<>();
	
	public void addAtlasImage(AtlasMultiFrameImage<T> image) {
		this.atlasImages.add(image);
	}
	public void addAtlasImage(int width, int height, int[] frames, int frametime, boolean interpolate, T image) {
		int frameCount = IntStream.of(frames).max().getAsInt() + 1;
		this.atlasImages.add(new AtlasMultiFrameImage<T>(width, height, frameCount, frames, frametime, interpolate, image));
	}
	
	public AtlasMultiFrameLayout<T> buildLayout(boolean prioritizeHeight) {
		
		if (this.atlasImages.isEmpty()) throw new IllegalStateException("No images have ban added to the builder!");
		
		// Calculate required frame time for atlas
		List<List<Integer>> divs = this.atlasImages
			.stream()
			.filter((image) -> image.frames().length > 1)
			.map((image) -> 
				IntStream.range(1, image.frametime() + 1)
				.filter((div) -> image.frametime() % div == 0)
				.boxed()
				.toList()
			)
			.toList();
		System.out.println(divs);
		int atlasFrametime = IntStream.range(
				1, 
				divs.stream().mapToInt((divStream) -> 
					divStream.stream().mapToInt(Integer::intValue).max().getAsInt()
				).max().getAsInt() + 1
			)
			.filter((i) -> divs.stream().filter((list) -> list.contains(i)).count() == divs.size())
			.max().getAsInt();
		System.out.println("Required frame time for atlas texture: " + atlasFrametime);
		
		// Calculate atlas frames per image frame
		Map<AtlasMultiFrameImage<T>, Integer> atlasFramesPerImageFrame = this.atlasImages.stream().collect(Collectors.toMap((image) -> image, (image) -> image.frames().length > 1 ? image.frametime / atlasFrametime : 1));
		
		// Calculate frames required for all images to complete a full number of cycles
		int[] imageFrames = this.atlasImages.stream().mapToInt((image) -> image.frames().length * atlasFramesPerImageFrame.get(image)).toArray();
		int atlasFrameCount = this.atlasImages.stream().mapToInt((image) -> image.frames().length * atlasFramesPerImageFrame.get(image)).max().getAsInt();
		boolean  modified = true;
		while (modified) {
			modified = false;
			atlasFrameCount = IntStream.of(imageFrames).max().getAsInt();
			for (int i = 0; i < imageFrames.length; i++) {
				if (imageFrames[i] < atlasFrameCount && this.atlasImages.get(i).frames().length > 1) {
					imageFrames[i] += this.atlasImages.get(i).frames().length * atlasFramesPerImageFrame.get(this.atlasImages.get(i));
					modified = true;
				}
			}
		}
		System.out.println("Required frames for atlas texture: " + atlasFrameCount);
		
		// Build atlas frame layout
		AtlasLayoutBuilder<AtlasMultiFrameImage<T>> layoutBuilder = new AtlasLayoutBuilder<>();
		for (AtlasMultiFrameImage<T> image : this.atlasImages) {
			layoutBuilder.addAtlasImage(image.width, image.height / image.frameCount, image);
		}
		AtlasLayout<AtlasMultiFrameImage<T>> frameLayout = layoutBuilder.buildLayout(prioritizeHeight);

		// Calculate atlas format
		int atlasWidth = frameLayout.width();
		int atlasHeight = frameLayout.height() * atlasFrameCount;
		System.out.println("Atlas texture size: " + atlasWidth + " x " + atlasHeight);
		
		// Build frame layouts
		List<List<AtlasFrameLayout<T>>> multiFrameLayouts = new ArrayList<>();
		for (int atlasFrameIndex = 0; atlasFrameIndex < atlasFrameCount; atlasFrameIndex++) {
			List<AtlasFrameLayout<T>> frameImages = new ArrayList<>();
			for (AtlasImageLayout<AtlasMultiFrameImage<T>> layout : frameLayout.imageLayouts()) {
				int framesPerImageFrame = atlasFramesPerImageFrame.get(layout.image().image());
				int imageFrameIndex = (atlasFrameIndex / framesPerImageFrame) % layout.image().image().frames().length;
				int nextImageFrameIngex = (imageFrameIndex + 1) % layout.image().image().frames().length;
				frameImages.add(
						new AtlasFrameLayout<T>(
								layout.x(), 
								layout.y() + frameLayout.height() * atlasFrameIndex,
								layout.image().width(),
								layout.image().height(),
								layout.image().image().frames()[imageFrameIndex],
								layout.image().image().frames()[nextImageFrameIngex],
								((atlasFrameIndex % framesPerImageFrame) / (float) framesPerImageFrame),
								layout.image().image().interpolate(),
								layout.image().image().image()
							));
			}
			multiFrameLayouts.add(frameImages);
		}
		
		// Complete, cleanup
		AtlasMultiFrameLayout<T> layout = new AtlasMultiFrameLayout<>(atlasWidth, atlasHeight, atlasFrameCount, multiFrameLayouts);
		this.atlasImages.clear();
		return layout;
		
	}
	
}
