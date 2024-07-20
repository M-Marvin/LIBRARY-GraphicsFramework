package de.m_marvin.gframe.textures.atlasbuilding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.m_marvin.simplelogging.printing.Logger;
import de.m_marvin.univec.impl.Vec2i;

/**
 * Provides an algorithm that tries to place the images into a single texture atlas.
 * Sometimes this can fail if the sizes of the textures are to different.
 * 
 * @author Marvin Köhler
 *
 * @param <T> Type of the textures
 */
public class AtlasLayoutBuilder<T> {
	
	public static record AtlasImage<T>(int width, int height, T image) {}
	public static record AtlasImageLayout<T>(int x, int y, AtlasImage<T> image) {}
	public static record AtlasLayout<T>(List<AtlasImageLayout<T>> imageLayouts, int width, int height) {}
	
	protected List<AtlasImage<T>> atlasImages = new ArrayList<>();
	protected List<AtlasImageLayout<T>> atlasLayout = new ArrayList<>();
	protected List<Vec2i> pastePoints = new ArrayList<>();
	
	/**
	 * Adds the image to the list of images to place in the atlas.
	 * @param image The image represented by an {@link AtlasImage}
	 */
	public void addAtlasImage(AtlasImage<T> image) {
		this.atlasImages.add(image);
	}
	
	/**
	 * Adds the image to the list of images to place in the atlas.
	 * @param width The image width
	 * @param height The image height
	 * @param image The image data
	 */
	public void addAtlasImage(int width, int height, T image) {
		this.atlasImages.add(new AtlasImage<T>(width, height, image));
	}
	
	/**
	 * Tries to build the atlas layout from the added images.
	 * @param prioritizeHeight Determines the arrangement of the textures (x or y axis).
	 * @return If successful the layout for the atlas
	 * @throws IllegalStateException if the building of the layout fails
	 */
	public AtlasLayout<T> buildLayout(boolean prioritizeHeight) {
		
		if (this.atlasImages.size() == 0) throw new IllegalStateException("Can't build layout with zero images! (Would not make any sense ...)");
		
		// Relay simple layout ...
		if (this.atlasImages.size() == 1) {
			AtlasImage<T> image = this.atlasImages.get(0);
			AtlasImageLayout<T> imageLayout = new AtlasImageLayout<T>(0, 0, image);
			AtlasLayout<T> layout = new AtlasLayout<T>(Arrays.asList(imageLayout), image.width, image.height);
			this.atlasImages = new ArrayList<>();
			return layout;
		}
		
		// Calculate the plane size of all images combined
		int planeSize = 0;
		for (AtlasImage<T> image : this.atlasImages) planeSize += image.width * image.height;
		
		// Calculate the minimum required size of the atlas, assuming 100% efficiency when combine the images
		int minWidth = (int) Math.sqrt(planeSize);
		int minHeight = minWidth;
		
		// Set the starting point of the algorithm
		pastePoints.add(new Vec2i(0, 0));
		 
		// Sort all images after width and height into a new list
		this.atlasImages.sort((img2, img1) -> {
			int c = prioritizeHeight ? Integer.compare(img1.width, img2.width) : Integer.compare(img1.height, img2.height);
			if (c == 0) return prioritizeHeight ? Integer.compare(img1.height, img2.height) : Integer.compare(img1.width, img2.width);
			return c;
		});
		
		// Begin layout algorithm and continue until all images are placed
		while (this.atlasImages.size() > 0) {
			
			if (pastePoints.size() == 0) break; // An error occurred and no paste points could be found anymore!
			
			// Find next available paste point for the next image
			Vec2i lowestPoint = this.pastePoints.stream()
					.sorted((vec1, vec2) -> prioritizeHeight ? Integer.compare(vec1.x(), vec2.x()) : Integer.compare(vec1.y(), vec2.y()))
					.findFirst().get();
			int lowest = prioritizeHeight ? lowestPoint.x() : lowestPoint.y();
			Vec2i nextPoint = this.pastePoints.stream()
					.sorted((vec1, vec2) -> prioritizeHeight ? Integer.compare(vec1.x(), vec2.x()) : Integer.compare(vec1.y(), vec2.y()))
					.filter((vec) -> prioritizeHeight ? vec.x() == lowest : vec.y() == lowest)
					.sorted((vec1, vec2) -> prioritizeHeight ? Integer.compare(vec1.y(), vec2.y()) : Integer.compare(vec1.x(), vec2.x()))
					.findFirst().get();
			this.pastePoints.remove(nextPoint);
			
			AtlasImage<T> fittingImage = null;
			if (prioritizeHeight) {
				
				// Calculate maximum height of the image for that point
				int maxY = nextPoint.y();
				for (; maxY < minHeight; maxY++) {
					boolean occupied = false;
					for (AtlasImageLayout<T> layout : this.atlasLayout) {
						
						// Check if layout occupies the position
						float y = maxY;
						float x = nextPoint.x();
						if (layout.y <= y && layout.y + layout.image.height > y && layout.x <= x && layout.x + layout.image.width > x)  {
							occupied = true;
							break;
						}
						
					}
					if (occupied) break;
				}
				int maxHeight = maxY - nextPoint.y();
				
				// Try to find a image that fits at this position
				for (AtlasImage<T> image : this.atlasImages) {
					if (image.height <= maxHeight) {
						fittingImage = image;
						break;
					}
				}
								
			} else {
				
				// Calculate maximum width of the image for that point
				int maxX = nextPoint.x();
				for (; maxX < minWidth; maxX++) {
					boolean occupied = false;
					for (AtlasImageLayout<T> layout : this.atlasLayout) {
						
						// Check if layout occupies the position
						float x = maxX;
						float y = nextPoint.y();
						if (layout.x <= x && layout.x + layout.image.width > x && layout.y <= y && layout.y + layout.image.height > y)  {
							occupied = true;
							break;
						}
						
					}
					if (occupied) break;
				}
				int maxWidth = maxX - nextPoint.x();

				// Try to find a image that fits at this position
				for (AtlasImage<T> image : this.atlasImages) {
					if (image.width <= maxWidth) {
						fittingImage = image;
						break;
					}
				}
								
			}
			
			// If found, place image layout
			if (fittingImage != null) {
				
				this.atlasLayout.add(new AtlasImageLayout<T>(nextPoint.x(), nextPoint.y(), fittingImage));

				// Modify height/size if required
				if (prioritizeHeight) {
					int x = nextPoint.x() + fittingImage.width;
					if (x > minWidth) minWidth = x;
				} else {
					int y = nextPoint.y() + fittingImage.height;
					if (y > minHeight) minHeight = y;
				}
				
				// Calculate new paste points resulting from the placed image
				if (prioritizeHeight) {

					// If this image is the first in the column, add bottom right corner of this image as paste point
					if (nextPoint.y() == 0) {
						pastePoints.add(nextPoint.add(fittingImage.width, 0));
					} else {
						
						// Find image behind this image
						AtlasImageLayout<T> image = this.atlasLayout.stream()
								.filter((layout) -> layout.x <= nextPoint.x() && layout.x + layout.image.width > nextPoint.x())
								.filter((layout) -> layout.y + layout.image.height <= nextPoint.y())
								.sorted((layout1, layout2) -> Integer.compare(layout1.y(), layout2.y()))
								.reduce((f, s) -> s).get();
						
						// If image is taller than this one, add bottom right corner of this image as paste point
						if (image.x + image.image.width > nextPoint.x() + fittingImage.width) {
							this.pastePoints.add(nextPoint.add(fittingImage.width, 0));
						}
						
					}
					
					// Add top left corner of this image as paste point
					pastePoints.add(nextPoint.add(0, fittingImage.height));
					
				} else {
					
					// If this image is the first in the row, add top left corner of this image as paste point
					if (nextPoint.x() == 0) {
						pastePoints.add(nextPoint.add(0, fittingImage.height));
					} else {
						
						// Find image behind this image
						AtlasImageLayout<T> image = this.atlasLayout.stream()
								.filter((layout) -> layout.y <= nextPoint.y() && layout.y + layout.image.height > nextPoint.y())
								.filter((layout) -> layout.x + layout.image.width <= nextPoint.x())
								.sorted((layout1, layout2) -> Integer.compare(layout1.x(), layout2.x()))
								.reduce((f, s) -> s).get();
						
						// If image is taller than this one, add top left corner of this image as paste point
						if (image.y + image.image.height > nextPoint.y() + fittingImage.height) {
							this.pastePoints.add(nextPoint.add(0, fittingImage.height));
						}
						
					}
					
					// Add bottom right corner of this image as paste point
					pastePoints.add(nextPoint.add(fittingImage.width, 0));
					
				}
				
				// Remove image from list
				this.atlasImages.remove(fittingImage);
				
			}
			
			// Continue with next point
			
		}
 		
		if (this.atlasImages.size() == 0) {
			
			// Everything placed, cleanup
			this.pastePoints.clear();
			List<AtlasImageLayout<T>> layout = this.atlasLayout;
			this.atlasLayout = new ArrayList<>();
			
			return new AtlasLayout<T>(layout, minWidth, minHeight);
			
		} else {

			// Could not place all images, something must be wrong!
			System.err.println("Remaining images " + this.atlasImages.size() + ", Paste-Points " + pastePoints.size());
			this.atlasImages.forEach((image) -> Logger.defaultLogger().logWarn("Remaining image >  " + image));
			throw new IllegalStateException("Could not place all images in the layout, maybe to large differences in the image formats?");
			
		}
		
	}
	
}
