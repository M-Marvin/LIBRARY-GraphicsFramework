package de.m_marvin.gframe.textures.atlasbuilding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.m_marvin.gframe.textures.atlasbuilding.AtlasLayoutBuilder.AtlasImageLayout;
import de.m_marvin.gframe.textures.atlasbuilding.AtlasLayoutBuilder.AtlasLayout;

/**
 * Provides an algorithm to build an animated atlas from animated textures.
 * Uses the {@link AtlasLayoutBuilder} for the frame layout and extends its layout to multiple frames.
 * 
 * @author Marvin Köhler
 *
 * @param <T> The type of the image
 */
public class MultiFrameAtlasLayoutBuilder<T> {
	
	public static record AtlasMultiFrameImage<T>(int width, int height, int frameCount, int[] frames, int frametime, T image) {}
	public static record AtlasFrameLayout<T>(int x, int y, int framey, int width, int frameHeight, int frame, int nextFrame, float subframe, T image) {}
	public static record AtlasMultiFrameLayout<T>(int width, int height, int frames, int frametime, List<List<AtlasFrameLayout<T>>> frameLayouts) {}
	
	protected List<AtlasMultiFrameImage<T>> atlasImages = new ArrayList<>();
	
	/**
	 * Adds the image to the list of images to place in the atlas.
	 * @param image The image represented by an {@link AtlasMultiFrameImage}
	 */
	public void addAtlasImage(AtlasMultiFrameImage<T> image) {
		this.atlasImages.add(image);
	}
	
	/**
	 * Adds the image to the list of images to place in the atlas.
	 * @param width The image width
	 * @param height The image height
	 * @param frames The frames of the animation
	 * @param frametime The tick-count one frame lasts
	 * @param interpolate If the texture interpolates between the frames
	 * @param image The image data
	 */
	public void addAtlasImage(int width, int height, int[] frames, int frametime, T image) {
		int frameCount = IntStream.of(frames).max().getAsInt() + 1;
		this.atlasImages.add(new AtlasMultiFrameImage<T>(width, height, frameCount, frames, frametime, image));
	}
	
	/**
	 * Tries to build the atlas layout from the added images.
	 * @param prioritizeHeight Determines the arrangement of the textures (x or y axis).
	 * @return If successful the layout for the atlas
	 * @throws IllegalStateException if the building of the layout fails
	 */
	public AtlasMultiFrameLayout<T> buildLayout(boolean prioritizeHeight) {
		
		if (this.atlasImages.isEmpty()) throw new IllegalStateException("No images have ban added to the builder!");
		
		// Calculate required frame time for atlas
		List<List<Integer>> divs = this.atlasImages
			.stream()
			.map((image) -> 
				IntStream.range(1, image.frametime() + 1)
				.filter((div) -> image.frametime() % div == 0)
				.boxed()
				.toList()
			)
			.toList();
		int atlasFrametime = IntStream.range(
				1, 
				divs.stream().mapToInt((divStream) -> 
					divStream.stream().mapToInt(Integer::intValue).max().getAsInt()
				).max().getAsInt() + 1
			)
			.filter((i) -> divs.stream().filter((list) -> list.contains(i)).count() == divs.size())
			.max().getAsInt();
		
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
		
		// Build atlas frame layout
		AtlasLayoutBuilder<AtlasMultiFrameImage<T>> layoutBuilder = new AtlasLayoutBuilder<>();
		for (AtlasMultiFrameImage<T> image : this.atlasImages) {
			layoutBuilder.addAtlasImage(image.width, image.height / image.frameCount, image);
		}
		AtlasLayout<AtlasMultiFrameImage<T>> frameLayout = layoutBuilder.buildLayout(prioritizeHeight);

		// Calculate atlas format
		int atlasWidth = frameLayout.width();
		int atlasHeight = frameLayout.height() * atlasFrameCount;
		
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
								layout.y(),
								layout.image().width(),
								layout.image().height(),
								layout.image().image().frames()[imageFrameIndex],
								layout.image().image().frames()[nextImageFrameIngex],
								((atlasFrameIndex % framesPerImageFrame) / (float) framesPerImageFrame),
								layout.image().image().image()
							));
			}
			multiFrameLayouts.add(frameImages);
		}
		
		// Complete, cleanup
		AtlasMultiFrameLayout<T> layout = new AtlasMultiFrameLayout<>(atlasWidth, atlasHeight, atlasFrameCount, atlasFrametime, multiFrameLayouts);
		this.atlasImages.clear();
		return layout;
		
	}
	
}
