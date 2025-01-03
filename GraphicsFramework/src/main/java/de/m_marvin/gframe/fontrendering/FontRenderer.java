package de.m_marvin.gframe.fontrendering;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.function.Function;

import de.m_marvin.gframe.buffers.BufferBuilder;
import de.m_marvin.gframe.buffers.IBufferSource;
import de.m_marvin.gframe.buffers.defimpl.IRenderMode;
import de.m_marvin.gframe.resources.IResourceProvider;
import de.m_marvin.gframe.resources.ISourceFolder;
import de.m_marvin.gframe.textures.TextureLoader;
import de.m_marvin.gframe.textures.maps.AbstractTextureMap;
import de.m_marvin.gframe.translation.PoseStack;
import de.m_marvin.gframe.utility.Utility;
import de.m_marvin.simplelogging.Log;
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
	
	public static String fitText(String string, Font font, int maxWidth) {
		
		int nc = 0;
		int tw = 0;
//		int[] cw = new int[string.length()];
		FontMetrics metrics = fontMetricsFactory.apply(font);
		for (int i = 0; i < string.length(); i++) {
			int w = metrics.charWidth(string.charAt(i));
			if (tw + w > maxWidth) break;
//			cw[i] = w;
			tw += w;
			nc++;
		}
		
		if (nc == string.length()) return string;
		return string.substring(0, nc);
		
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
			Log.defaultLogger().error("The font atlas for the font '%s' is cached under the wrong format!", fontToString(font));
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
		
		Log.defaultLogger().info("Loaded and cached font %s!", fontToString(font));
		
	}
	
	private static <R extends IResourceProvider<R>> void loadCharacters(Font font, FontMetrics metrics, FontAtlasMap<R> fontAtlas, int first, int last, boolean antialiasing) {
		
		int characterHeight = metrics.getHeight();
		
		for (int i = first; i <= last; i++) {
			
			int characterWidth = metrics.charWidth((char) i);
			
			if (characterWidth <= 0) {
				Log.defaultLogger().error("Character width for char '%s' in font %s is <= 0!", String.valueOf((char) i), fontToString(font));
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
