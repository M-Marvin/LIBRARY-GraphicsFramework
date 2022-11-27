package de.m_marvin.voxelengine.screens.elements;

import java.awt.Color;

import de.m_marvin.openui.elements.UIButtonElement;
import de.m_marvin.renderengine.buffers.BufferBuilder;
import de.m_marvin.renderengine.resources.locationtemplates.ResourceLocation;
import de.m_marvin.renderengine.textures.AbstractTextureMap;
import de.m_marvin.renderengine.translation.PoseStack;
import de.m_marvin.univec.impl.Vec2i;
import de.m_marvin.voxelengine.VoxelEngine;
import de.m_marvin.voxelengine.rendering.BufferSource;
import de.m_marvin.voxelengine.rendering.RenderType;

public class UIButton extends UIButtonElement {

	public UIButton(Vec2i position, Vec2i size, String title, Color colorBack, Color colorFront) {
		super(position, size, title, colorBack, colorFront);
	}
	
	@Override
	public boolean isOverElement(Vec2i coursorPosition) {
		return	coursorPosition.x >= this.position.x &&
				coursorPosition.y >= this.position.y &&
				coursorPosition.x < this.position.x + this.size.x &&
				coursorPosition.y < this.position.y + this.size.y;
	}
	
	@Override
	public void onHover(HoverEvent event, Vec2i coursorPosition) {
		
		if (VoxelEngine.getInstance().getInputHandler().isBindingTyped("misc.click.primary")) {
			
			System.out.println("CLICK");
			
		}
		
	}
	
	@Override
	public void draw(PoseStack poseStack) {
				
		float xl = position.x;
		float yl = position.y;
		float xh = xl + size.x;
		float yh = yl + size.y;
		float r = colorBack.getRed() / 255F;
		float g = colorBack.getGreen() / 255F;
		float b = colorBack.getBlue() / 255F;
		float a = colorBack.getAlpha() / 255F;
		
		BufferSource bufferSource = VoxelEngine.getInstance().getGameRenderer().getBufferSource();
		
		ResourceLocation textureLoc = new ResourceLocation(VoxelEngine.NAMESPACE, "screen/test");
		AbstractTextureMap<ResourceLocation> texture = VoxelEngine.getInstance().getTextureLoader().getTexture(textureLoc);
		
		BufferBuilder buffer = bufferSource.startBuffer(RenderType.screenTextured(textureLoc));
				
		buffer.vertex(poseStack, xl, yl).color(r, g, b, a).uv(texture, 0, 0).endVertex();
		buffer.vertex(poseStack, xh, yl).color(r, g, b, a).uv(texture, 1, 0).endVertex();
		buffer.vertex(poseStack, xh, yh).color(r, g, b, a).uv(texture, 1, 1).endVertex();
		buffer.vertex(poseStack, xl, yh).color(r, g, b, a).uv(texture, 0, 1).endVertex();
		buffer.end();
	}

}
