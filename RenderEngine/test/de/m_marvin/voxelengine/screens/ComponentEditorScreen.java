package de.m_marvin.voxelengine.screens;

import org.lwjgl.opengl.GL11;

import de.m_marvin.openui.ScreenAligment;
import de.m_marvin.openui.ScreenUI;
import de.m_marvin.renderengine.GLStateManager;
import de.m_marvin.renderengine.buffers.BufferBuilder;
import de.m_marvin.renderengine.buffers.BufferUsage;
import de.m_marvin.renderengine.buffers.VertexBuffer;
import de.m_marvin.renderengine.resources.locationtemplates.ResourceLocation;
import de.m_marvin.renderengine.shaders.ShaderInstance;
import de.m_marvin.renderengine.textures.AbstractTextureMap;
import de.m_marvin.renderengine.translation.PoseStack;
import de.m_marvin.unimat.impl.Matrix4f;
import de.m_marvin.unimat.impl.Quaternion;
import de.m_marvin.univec.impl.Vec2f;
import de.m_marvin.univec.impl.Vec2i;
import de.m_marvin.univec.impl.Vec3i;
import de.m_marvin.voxelengine.VoxelEngine;
import de.m_marvin.voxelengine.rendering.BufferSource;
import de.m_marvin.voxelengine.rendering.GameRenderer;
import de.m_marvin.voxelengine.rendering.RenderStage;
import de.m_marvin.voxelengine.rendering.RenderType;
import de.m_marvin.voxelengine.world.VoxelComponent;

public class ComponentEditorScreen extends ScreenBase {
	
	protected Matrix4f viewMatrix = new Matrix4f();
	protected Vec2f rotationInput = new Vec2f(0, 0);
	protected float scaleInput = 0;
	
	protected VoxelComponent component;
	
	protected TopBar topBar;
	protected class TopBar extends ScreenUI {
		
		public TopBar() {
			super(new Vec2i(1000, 40), ScreenAligment.TOP_LEFT);
		}
		
		@Override
		public void drawAdditionalContent(PoseStack poseStack, float partialTick) {
			
			BufferBuilder buffer = VoxelEngine.getInstance().getGameRenderer().getBufferSource().startBuffer(RenderType.screen());
			drawRectangle(buffer, poseStack, 0, 0, windowSize.x, 40, 0, 0, 0, 0.6F);
			buffer.end();
		}
		
	}
	
	public ComponentEditorScreen(VoxelComponent comp) {
		super(new Vec2i(1000, 600), ScreenAligment.CENTERED);
		
		this.topBar = addSubScreen(new TopBar());
		
		this.component = comp;
	}
	
	@Override
	public void drawAdditionalContent(PoseStack poseStack, float partialTicks) {
		
		GameRenderer.executeOnRenderStage(RenderStage.UI, true, () -> {
			
			// Additional Voxel-Components
			ShaderInstance shader = VoxelEngine.getInstance().getShaderLoader().getShader(VoxelEngine.getInstance().getGameRenderer().getLevelShader());
			
			if (shader != null) {
				
				AbstractTextureMap<ResourceLocation> materialAtlas = VoxelEngine.getInstance().getTextureLoader().getTextureMap(GameRenderer.MATERIAL_ATLAS);
				
				shader.useShader();
				shader.getUniform("ProjMat").setMatrix4f(VoxelEngine.getInstance().getGameRenderer().getProjectionMatrix());
				shader.getUniform("ViewMat").setMatrix4f(viewMatrix);
				shader.getUniform("HalfVoxelSize").setFloat(0.5F);
				shader.getUniform("Texture").setTextureSampler(materialAtlas);
				shader.getUniform("AnimMat").setMatrix3f(materialAtlas.frameMatrix());
				shader.getUniform("AnimMatLast").setMatrix3f(materialAtlas.lastFrameMatrix());
				shader.getUniform("Interpolation").setFloat(partialTicks);
				
				VertexBuffer vao = new VertexBuffer();
				
				if (component != null) {
					BufferSource bufferSource = VoxelEngine.getInstance().getGameRenderer().getBufferSource();
					
					poseStack.push();
					poseStack.translate(0, -2, -100);
					poseStack.scale(1F, 1F, 1F);
					poseStack.rotate(new Quaternion(new Vec3i(1, 0, 0), (float) Math.toRadians(10)));
					
					for (RenderType renderLayer : RenderType.voxelRenderLayers()) {	
						BufferBuilder buffer = bufferSource.getBuffer(renderLayer);
						buffer.begin(renderLayer.primitive(), renderLayer.vertexFormat());
						GameRenderer.drawComponent(buffer, poseStack, renderLayer, component, 1, 1, 1, 1);
						buffer.end();
						
						renderLayer.setState();
						GLStateManager.disable(GL11.GL_DEPTH_TEST);
						vao.upload(buffer, BufferUsage.DYNAMIC);
						vao.bind();
						vao.drawAll(renderLayer.primitive());
						vao.unbind();
						renderLayer.resetState();
					}
					
					poseStack.pop();
					
				}
				
				vao.discard();
				
				shader.unbindShader();
				
			}
			
		});
		
	}
	
}
