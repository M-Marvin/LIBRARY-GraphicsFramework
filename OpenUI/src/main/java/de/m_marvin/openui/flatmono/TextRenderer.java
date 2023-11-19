package de.m_marvin.openui.flatmono;

import java.awt.Color;
import java.awt.Font;

import de.m_marvin.openui.core.UIRenderMode;
import de.m_marvin.renderengine.buffers.IBufferSource;
import de.m_marvin.renderengine.fontrendering.FontRenderer;
import de.m_marvin.renderengine.resources.defimpl.ResourceLocation;
import de.m_marvin.renderengine.textures.utility.TextureLoader;
import de.m_marvin.renderengine.translation.PoseStack;

public class TextRenderer {
	
	public static final ResourceLocation FONT_ATLASES_LOCATION = new ResourceLocation(UIResourceFolders.NAMESPACE, "ui/font");
	
	public static void renderText(String text, Font font, Color color, TextureLoader<ResourceLocation, ?> textureLoader, IBufferSource<UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {
		FontRenderer.renderString(text, color, font, FONT_ATLASES_LOCATION, UIRenderModes::texturedSolid, textureLoader, bufferSource, matrixStack);
	}
	
	public static void renderTextCentered(String text, Font font, Color color, TextureLoader<ResourceLocation, ?> textureLoader, IBufferSource<UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {
		int width = FontRenderer.calculateStringWidth(text, font);
		int height = FontRenderer.getFontHeight(font);
		matrixStack.push();
		matrixStack.translate(-width / 2, -height / 2, 0);
		renderText(text, font, color, textureLoader, bufferSource, matrixStack);
		matrixStack.pop();
	}

	public static void renderText(int x, int y, String text, Font font, Color color, TextureLoader<ResourceLocation, ?> textureLoader, IBufferSource<UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {
		matrixStack.push();
		matrixStack.translate(x, y, 0);
		renderText(text, font, color, textureLoader, bufferSource, matrixStack);
		matrixStack.pop();
	}
	
	public static void renderTextCentered(int x, int y, String text, Font font, Color color, TextureLoader<ResourceLocation, ?> textureLoader, IBufferSource<UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {
		matrixStack.push();
		matrixStack.translate(x, y, 0);
		renderTextCentered(text, font, color, textureLoader, bufferSource, matrixStack);
		matrixStack.pop();
	}
		
}
