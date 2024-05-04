package de.m_marvin.renderengine.fontrendering;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import de.m_marvin.renderengine.resources.IResourceProvider;
import de.m_marvin.renderengine.textures.atlasbuilding.AtlasLayoutBuilder;
import de.m_marvin.renderengine.textures.atlasbuilding.AtlasLayoutBuilder.AtlasImageLayout;
import de.m_marvin.renderengine.textures.atlasbuilding.AtlasLayoutBuilder.AtlasLayout;
import de.m_marvin.renderengine.textures.maps.AbstractTextureMap;
import de.m_marvin.renderengine.textures.utility.TextureDataFormat;
import de.m_marvin.renderengine.textures.utility.TextureFormat;
import de.m_marvin.simplelogging.printing.Logger;
import de.m_marvin.univec.impl.Vec2i;
import de.m_marvin.univec.impl.Vec4f;

public class FontAtlasMap<R extends IResourceProvider<R>> extends AbstractTextureMap<R> {
	
	protected R atlasLocation;
	protected Map<Character, Vec4f> character2uv = new HashMap<>();
	protected char activeCharacter = 0;
	
	protected record LayoutPair<R>(char character, int[] pixels) {};
	protected AtlasLayoutBuilder<LayoutPair<R>> layoutBuilder;
	protected boolean building;
	
	public FontAtlasMap(R atlasLocation) {
		super(TextureFormat.RED_GREEN_BLUE_ALPHA);
		this.layoutBuilder = new AtlasLayoutBuilder<>();
		this.building = true;
		this.atlasLocation = atlasLocation;
	}

	public R getAtlasLocation() {
		return atlasLocation;
	}
	
	public void addCharacter(char character, int width, int height, int[] pixels) {
		if (!building) throw new IllegalStateException("The atlas is already compiled, no more textures can be added!");
		layoutBuilder.addAtlasImage(
				width,
				height,
				new LayoutPair<>(character, pixels)
		);
	}
	
	/**
	 * Tries to place all textures in the atlas and makes the texture ready for use.
	 * After this method is called, no more textures can be added.
	 * @param prioritizeAtlasHeight Determines the if the textures are aligned on the x or y axis
	 * @param interpolate True if the textures of the atlas need to be interpolated
	 */
	public void buildAtlas(boolean prioritizeAtlasHeight) {
		if (!building) throw new IllegalStateException("The atlas is already compiled!");

		AtlasLayout<LayoutPair<R>> layout = layoutBuilder.buildLayout(prioritizeAtlasHeight);
		BufferedImage atlasImage = new BufferedImage(layout.width(), layout.height(), BufferedImage.TYPE_4BYTE_ABGR);
		
		for (AtlasImageLayout<LayoutPair<R>> imageLayout : layout.imageLayouts()) {
			
			char character = imageLayout.image().image().character();
			int pixels[] = imageLayout.image().image().pixels();
			int width = imageLayout.image().width();
			int height = imageLayout.image().height();
			
			atlasImage.setRGB(imageLayout.x(), imageLayout.y(), width, height, pixels, 0, width);
			if (!character2uv.containsKey(character)) {
				character2uv.put(
						character, 
						new Vec4f(
							imageLayout.x() / (float) layout.width(), 
							imageLayout.y() / (float) layout.height(),
							width / (float) layout.width(), 
							height / (float) layout.height()
						)
				);
			}
			
		}
		
		building = false;
		
		this.frames = new int[] {0};
		this.frameHeight = this.height;
		this.frametime = 0;
		int width = layout.width();
		int height = layout.height();
		int[] pixels = atlasImage.getRGB(0, 0, this.width, this.height, null, 0, this.width);
		upload(width, height, TextureDataFormat.INT_RGBA_8_8_8_8, pixels);
	}
	
	@Override
	public void activateTexture(R textureLoc) {}
	
	public void activateCharacter(char character) {
		if (character2uv.containsKey(character)) {
			activeCharacter = character;
		} else {
			Logger.defaultLogger().logWarn("The character-texture for '" + character + "' does not exist in the atlas!");
			activeCharacter = 0;
		}
	}
	
	public Vec2i getCharacterSize() {
		Vec4f uv = getUV();
		return new Vec2i((int) (uv.z * width), (int) (uv.w * height));
	}
	
	@Override
	public float mapU(float u) {
		Vec4f texUV = getUV();
		return texUV.x() + texUV.z() * u;
	}

	@Override
	public float mapV(float v) {
		Vec4f texUV = getUV();
		return texUV.y() + texUV.w() * v;
	}

	@Override
	public Vec4f getUV() {
		if (!character2uv.containsKey(activeCharacter)) return new Vec4f(0, 0, 0, 0);
		return character2uv.get(activeCharacter);
	}
	
	@Override
	public int getImageWidth() {
		return Math.round((mapV(1) - mapV(0)) * height);
	}

	@Override
	public int getImageHeight() {
		return Math.round((mapU(1) - mapU(0)) * width);
	}
	
	@Override
	public int getMapWidth() {
		return width;
	}

	@Override
	public int getMapHeight() {
		return frameHeight;
	}
	
}
