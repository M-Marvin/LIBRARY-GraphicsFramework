package de.m_marvin.renderengine.fontrendering;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.function.Function;

import de.m_marvin.renderengine.buffers.BufferBuilder;
import de.m_marvin.renderengine.buffers.IBufferSource;
import de.m_marvin.renderengine.buffers.defimpl.IRenderMode;
import de.m_marvin.renderengine.resources.IResourceProvider;
import de.m_marvin.renderengine.resources.ISourceFolder;
import de.m_marvin.renderengine.textures.AbstractTextureMap;
import de.m_marvin.renderengine.textures.utility.TextureLoader;
import de.m_marvin.renderengine.translation.PoseStack;
import de.m_marvin.renderengine.utility.Utility;
import de.m_marvin.simplelogging.printing.Logger;
import de.m_marvin.univec.impl.Vec2i;

public class FontRenderer {
	
	public static <R extends IResourceProvider<R>, T extends IRenderMode> void renderString(String string, Color color, Font font, R fontAtlasesLocation, Function<R, T> renderModeSupplier, TextureLoader<R, ?> textureLoader, IBufferSource<T> bufferSource, PoseStack matrixStack) {
		FontAtlasMap<R> fontAtlas = getFontAtlas(textureLoader, font, fontAtlasesLocation);
		renderString(string, color, fontAtlas, renderModeSupplier, bufferSource, matrixStack);
	}
	
	public static <R extends IResourceProvider<R>, T extends IRenderMode> void renderString(String string, Color color, FontAtlasMap<R> fontAtlas, Function<R, T> renderModeSupplier, IBufferSource<T> bufferSource, PoseStack matrixStack) {
		
		int wo = 0;
		
		float r = color.getRed() / 255F;
		float g = color.getGreen() / 255F;
		float b = color.getBlue() / 255F;
		float a = color.getAlpha() / 255F;
		
		for (int i = 0; i < string.length(); i++) {
			char character = string.charAt(i);
			fontAtlas.activateCharacter(character);
			
			Vec2i characterSize = fontAtlas.getCharacterSize();
			float fxl = wo;
			float fyl = 0;
			float fxh = wo + characterSize.x;
			float fyh = characterSize.y;
			wo +=  characterSize.x;
			
			BufferBuilder vertexBuffer = bufferSource.startBuffer(renderModeSupplier.apply(fontAtlas.getAtlasLocation()));
			
			vertexBuffer.vertex(matrixStack, fxl, fyl, 0).uv(fontAtlas, 0, 0).color(r, g, b, a).endVertex();
			vertexBuffer.vertex(matrixStack, fxh, fyl, 0).uv(fontAtlas, 1, 0).color(r, g, b, a).endVertex();
			vertexBuffer.vertex(matrixStack, fxl, fyh, 0).uv(fontAtlas, 0, 1).color(r, g, b, a).endVertex();
			vertexBuffer.vertex(matrixStack, fxl, fyh, 0).uv(fontAtlas, 0, 1).color(r, g, b, a).endVertex();
			vertexBuffer.vertex(matrixStack, fxh, fyl, 0).uv(fontAtlas, 1, 0).color(r, g, b, a).endVertex();
			vertexBuffer.vertex(matrixStack, fxh, fyh, 0).uv(fontAtlas, 1, 1).color(r, g, b, a).endVertex();
			
			vertexBuffer.end();
			
		}
		
	}
	
	public static String limitStringWidth(String string, Font font, int maxWidth) {
		int width = 0;
		FontMetrics metrics = fontMetricsFactory.apply(font);
		for (int i = 0; i < string.length(); i++) {
			width += metrics.charWidth(string.charAt(i));
			if (width > maxWidth) return string.substring(0, i);
		}
		return string;
	}
	
	public static int calculateStringWidth(String string, Font font) {
		int width = 0;
		FontMetrics metrics = fontMetricsFactory.apply(font);
		for (int i = 0; i < string.length(); i++) {
			width += metrics.charWidth(string.charAt(i));
		}
		return width;
	}
	
	public static int getFontHeight(Font font) {
		FontMetrics metrics = fontMetricsFactory.apply(font);
		return metrics.getHeight();
	}
	
	public static String getFontAtlasName(Font font) {
		return font.getFamily() + "_" + font.getStyle() + "_" + font.getSize();
	}
	
	public static <R extends IResourceProvider<R>, S extends ISourceFolder> FontAtlasMap<R> getFontAtlas(TextureLoader<R, S> textureLoader, Font font, R fontAtlasesLocation) {
		
		R fontAtlasLocation = fontAtlasesLocation.locationOfFile(getFontAtlasName(font).toLowerCase());
		
		if (!textureLoader.getTextureMapNames().contains(fontAtlasLocation)) {
			loadFont(textureLoader, font, false, fontAtlasLocation);
		}
		
		AbstractTextureMap<R> textureMap = textureLoader.getTextureMap(fontAtlasLocation);
		if (textureMap instanceof FontAtlasMap<R> fontMap) {
			return fontMap;
		} else {
			Logger.defaultLogger().logError("The font atlas for the font '" + fontToString(font) + "' is cached under the wrong format!");
			return null;
		}
		
	}
	
	private static Function<Font, FontMetrics> fontMetricsFactory = Utility.memorize(font -> {
		
		// Get font metrics, only accessible trough an graphics object
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics();
		g.dispose();
		return metrics;
		
	});
	
	private static <R extends IResourceProvider<R>, S extends ISourceFolder> void loadFont(TextureLoader<R, S> textureLoader, Font font, boolean antialiasing, R fontAtlasName) {
		
		FontMetrics metrics = fontMetricsFactory.apply(font);
		
		// Make atlas by printing each character onto the image
		FontAtlasMap<R> fontAtlas = new FontAtlasMap<R>(fontAtlasName);
		loadCharacters(font, metrics, fontAtlas, 31, 256, false);		
		fontAtlas.buildAtlas(false);
		
		// Cache the fonts texture atlas in the texture loader
		textureLoader.cacheTextureMap(fontAtlasName, fontAtlas);
		
		Logger.defaultLogger().logInfo("Loaded and cached font " + fontToString(font) + "!");
		
	}
	
	private static <R extends IResourceProvider<R>> void loadCharacters(Font font, FontMetrics metrics, FontAtlasMap<R> fontAtlas, int first, int last, boolean antialiasing) {
		
		int characterHeight = metrics.getHeight();
		
		for (int i = first; i <= last; i++) {
			
			int characterWidth = metrics.charWidth((char) i);
			
			if (characterWidth <= 0) {
				Logger.defaultLogger().logError("Character width for char '" + String.valueOf((char) i) + "' in font " + fontToString(font) + " is <= 0!");
				continue;
			}
			
			BufferedImage charImage = new BufferedImage(characterWidth, characterHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = charImage.createGraphics();
			if (antialiasing) graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			graphics.setFont(font);
			graphics.setPaint(Color.white);
			graphics.drawString(String.valueOf((char) i), 0, metrics.getAscent());
			graphics.dispose();
			
			int[] pixels = charImage.getRGB(0, 0, characterWidth, characterHeight, null, 0, characterWidth);
			
			fontAtlas.addCharacter((char) i, characterWidth, characterHeight, pixels);
			
		}
		
	}
	
	public static String fontToString(Font font) {
		return font.toString().replace("java.awt.", "");
	}
	
}
