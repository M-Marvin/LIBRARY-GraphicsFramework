package de.m_marvin.voxelengine.rendering;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import com.google.common.collect.Queues;

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
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;
import de.m_marvin.univec.impl.Vec4f;
import de.m_marvin.voxelengine.VoxelEngine;
import de.m_marvin.voxelengine.rendering.LevelRender.StructureRender;
import de.m_marvin.voxelengine.world.ClientLevel;
import de.m_marvin.voxelengine.world.VoxelComponent;
import de.m_marvin.voxelengine.world.VoxelMaterial;
import de.m_marvin.voxelengine.world.VoxelStructure;

public class GameRenderer {

	public static final ResourceLocation VOXEL_SHADER = new ResourceLocation("example:world/voxelShader");
	
	public float fov;
	protected Matrix4f projectionMatrix;
	protected BufferSource bufferSource;
	protected Queue<Runnable> renderCallQueue;
	
	protected LevelRender levelRender;
	
	public GameRenderer(int initialBufferSize) {
		this.renderCallQueue = Queues.newArrayDeque();
		this.bufferSource = new BufferSource(initialBufferSize);
		this.levelRender = new LevelRender();
	}

	public BufferSource getBufferSource() {
		return this.bufferSource;
	}
	
	public void updatePerspective() {
		int[] windowSize = VoxelEngine.getInstance().getMainWindow().getSize();
		this.projectionMatrix = Matrix4f.perspective(this.fov, windowSize[0] / (float) windowSize[1], 1, 1000);
		executeOnRenderThread(() -> GLStateManager.resizeViewport(0, 0, windowSize[0], windowSize[1]));
	}
	
	public void resetRenderCache() {
		this.levelRender.discard();
		this.shaderStreamBuffer.discard();
	}
	
	public void executeOnRenderThread(Runnable runnable) {
		this.renderCallQueue.add(runnable);
	}
	
	public void frameStart() {
		while (this.renderCallQueue.size() > 0) this.renderCallQueue.poll().run();
	}
	
	public ResourceLocation getScreenShader() {
		return new ResourceLocation("example:misc/screen");
	}
	
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
	
	protected VertexBuffer shaderStreamBuffer = new VertexBuffer();
	
	public void renderScreen(ScreenUI screen, float partialTick) {
		
		ShaderInstance shader = VoxelEngine.getInstance().getShaderLoader().getShader(getScreenShader());
		
		if (shader != null) {
			
			int[] size = VoxelEngine.getInstance().getMainWindow().getSize();
			PoseStack poseStack = new PoseStack();
			screen.drawScreen(poseStack, size[0], size[1], partialTick);
			
			shader.useShader();
			shader.getUniform("Interpolation").setFloat(partialTick);
			
			bufferSource.getBufferTypes().forEach((type) -> {
				
				if (Arrays.asList(RenderType.screenLayers()).contains(type)) {
					
					type.setState();
					BufferBuilder buffer = bufferSource.getBuffer(type);
					for (int i = 0; i < buffer.completedBuffers(); i++) {
						shaderStreamBuffer.upload(buffer, BufferUsage.DYNAMIC);
						shaderStreamBuffer.bind();
						
						if (type.textureMap() != null) {
							AbstractTextureMap<ResourceLocation> textureMap = VoxelEngine.getInstance().getTextureLoader().getTexture(type.textureMap());
							shader.getUniform("AnimMat").setMatrix3f(textureMap.frameMatrix());
							shader.getUniform("AnimMatLast").setMatrix3f(textureMap.lastFrameMatrix());
							shader.getUniform("Texture").setTextureSampler(textureMap);
						} else {
							shader.getUniform("Texture").setTextureSampler(null);
						}
						
						shaderStreamBuffer.drawAll(type.primitive());
					}
					type.resetState();
					
				}
				
			});
			
			shaderStreamBuffer.unbind();
			
			shader.unbindShader();
			
		}
		
	}
	
	public ResourceLocation getLevelShader() {
		return new ResourceLocation(VoxelEngine.NAMESPACE, "world/voxel");
	}
	
	public static final ResourceLocation MATERIAL_ATLAS = new ResourceLocation(VoxelEngine.NAMESPACE, "materials");
	
	public void renderLevel(ClientLevel level, float partialTick) {
		
		ShaderInstance shader = VoxelEngine.getInstance().getShaderLoader().getShader(getLevelShader());
		
		if (shader != null) {

			Matrix4f viewMatrix = VoxelEngine.getInstance().getMainCamera().getViewMatrix();
			
			AbstractTextureMap<ResourceLocation> materialAtlas = VoxelEngine.getInstance().getTextureLoader().getTextureMap(MATERIAL_ATLAS);
			
			shader.useShader();
			shader.getUniform("ProjMat").setMatrix4f(projectionMatrix);
			shader.getUniform("ViewMat").setMatrix4f(viewMatrix);
			shader.getUniform("HalfVoxelSize").setFloat(0.5F);
			shader.getUniform("Texture").setTextureSampler(materialAtlas);
			shader.getUniform("AnimMat").setMatrix3f(materialAtlas.frameMatrix());
			shader.getUniform("AnimMatLast").setMatrix3f(materialAtlas.lastFrameMatrix());
			shader.getUniform("Interpolation").setFloat(partialTick);
			
			PoseStack poseStack = new PoseStack();
			
			for (VoxelStructure structure : level.getStructures()) {
				
				StructureRender compiledStructure = levelRender.getOrCreateRender(structure);
				
				if (compiledStructure.isDirty()) {
					
					drawStructure(shader, bufferSource, poseStack, structure, 1F, 1F, 1F, 1F);
					
					for (RenderType renderLayer : RenderType.voxelRenderLayers()) {
						compiledStructure.getBuffer(renderLayer).upload(bufferSource.getBuffer(renderLayer), BufferUsage.DYNAMIC);
					}
					compiledStructure.setDirty(false);
					break;
					
				}
				
			}
			
			for (RenderType renderLayer : RenderType.voxelRenderLayers()) {
				
				renderLayer.setState();
				
				level.getStructures().forEach((structure) -> {
					
					StructureRender compiledStructure = levelRender.getOrCreateRender(structure);
					
					if (!compiledStructure.isDirty()) {
						
						VertexBuffer buffer = compiledStructure.getBuffer(renderLayer);
						
						Vec3f position = structure.getPosition();
						Quaternion rotation = structure.getRigidBody().getRotation();
						Matrix4f translationMatrix = Matrix4f.translateMatrix(position.x, position.y, position.z).mul(rotation);
						shader.getUniform("TranMat").setMatrix4f(translationMatrix);
						
						buffer.bind();
						buffer.drawAll(renderLayer.primitive());
						buffer.unbind();
						
					}
					
				});
				
				renderLayer.resetState();
				
			}

			shader.unbindShader();
			
		}
		
	}
	
	public void renderAdditionalBufferedObjects(float partialTick) {
		
	}
	
	public static void drawStructure(ShaderInstance shader, BufferSource bufferSource, PoseStack poseStack, VoxelStructure structure, float r, float g, float b, float a) {
		
		for (RenderType renderLayer : RenderType.voxelRenderLayers()) {
			
			BufferBuilder buffer = bufferSource.getBuffer(renderLayer);
			buffer.begin(renderLayer.primitive(), renderLayer.vertexFormat());
			
			structure.getComponents().forEach((structureComponent) -> {
				
				poseStack.push();
				poseStack.translate(structureComponent.position.x, structureComponent.position.y, structureComponent.position.z);
				poseStack.rotate(structureComponent.orientation);
				Vec3i[] aabb = structureComponent.component.getAabb();
				Vec3i offset = aabb[0];//.sub(aabb[0]).div(2).add(aabb[0]);
				poseStack.translate(offset);
				
				shader.getUniform("VoxelMat").setMatrix4f(poseStack.last().pose());
				
				drawComponent(buffer, renderLayer, structureComponent.component, r, g, b, a);
				
				poseStack.pop();
				
			});
			
			buffer.end();
			
		}
		
	}
	
	public static void drawComponent(BufferBuilder buffer, RenderType renderLayer, VoxelComponent component, float r, float g, float b, float a) {
		
		AbstractTextureMap<ResourceLocation> texture = VoxelEngine.getInstance().getTextureLoader().getTextureMap(MATERIAL_ATLAS);
		List<int[][][]> voxelGroups = component.getVoxels();
		
		for (int[][][] voxels : voxelGroups) {
			for (int x = 0; x < voxels.length; x++) {
				int [][] voxelSlice = voxels[x];
				for (int y = 0; y < voxelSlice.length; y++) {
					int [] voxelRow = voxelSlice[y];
					for (int z = 0; z < voxelRow.length; z++) {
						int voxelId = voxelRow[z];
						
						if (voxelId > 0) {

							byte sideState = 0;
							if (z > 0 ? voxels[x][y][z - 1] == 0 : true)						sideState += 1; // North
							if (z < voxelRow.length - 1 ? voxels[x][y][z + 1] == 0 : true)		sideState += 2; // South
							if (x > 0 ? voxels[x - 1][y][z] == 0 : true)						sideState += 4; // East
							if (x < voxels.length - 1 ? voxels[x + 1][y][z] == 0 : true)		sideState += 8; // West
							if (y < voxelSlice.length - 1 ? voxels[x][y + 1][z] == 0 : true)	sideState += 16; // Up
							if (y > 0 ? voxels[x][y - 1][z] == 0 : true)						sideState += 32; // Down
							
							VoxelMaterial material = component.getMaterial(voxelId);
							
							if (sideState > 0 && material.renderLayer().equals(renderLayer)) {
								
								ResourceLocation textureName = material.texture();
								texture.activateTexture(textureName);
								Vec4f texUV = texture.getUV();
								int texWidth = (int) (texture.getImageWidth() * material.pixelScale());
								int texHeight = (int) (texture.getImageHeight() * material.pixelScale());
								
								buffer.vertex(x, y, z).vec3i(x, y, z).nextElement().putInt(sideState).color(r, g, b, a).vec4f(texUV.x, texUV.y, texUV.z, texUV.w).vec2i(texWidth, texHeight).endVertex();
								
							}
							
						}
						
					}
				}
			}
		}
		
	}
	
}
