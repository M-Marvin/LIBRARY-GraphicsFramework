package de.m_marvin.voxelengine.rendering;

import java.util.List;

import org.lwjgl.opengl.GL33;

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
import de.m_marvin.univec.impl.Vec4f;
import de.m_marvin.voxelengine.VoxelEngine;
import de.m_marvin.voxelengine.rendering.LevelRender.StructureRender;
import de.m_marvin.voxelengine.world.ClientLevel;
import de.m_marvin.voxelengine.world.VoxelComponent;
import de.m_marvin.voxelengine.world.VoxelMaterial;
import de.m_marvin.voxelengine.world.VoxelStructure;

public class LevelRenderer {

	public static final ResourceLocation VOXEL_SHADER = new ResourceLocation("example:world/voxelShader");
	
	public float fov;
	protected Matrix4f projectionMatrix;
	protected boolean resized;
	
	protected BufferSource bufferSource;
	protected LevelRender levelRender;
	
	public LevelRenderer(int initialBufferSize) {
		this.bufferSource = new BufferSource(initialBufferSize);
		this.levelRender = new LevelRender();
	}
	
	public void updatePerspective() {
		int[] windowSize = VoxelEngine.getInstance().getMainWindow().getSize();
		this.projectionMatrix = Matrix4f.perspective(this.fov, windowSize[0] / (float) windowSize[1], 1, 1000);
		this.resized = true;
	}
	
	public void resetRenderCache() {
		this.levelRender.discard();
	}
	
	public void renderLevel(ClientLevel level, float partialTick) {
		
		if (resized) {
			int[] windowSize = VoxelEngine.getInstance().getMainWindow().getSize();
			GLStateManager.resizeViewport(0, 0, windowSize[0], windowSize[1]);
			this.resized = false;
		}
		
		ShaderInstance shader = VoxelEngine.getInstance().getShaderLoader().getShader(new ResourceLocation("example:world/voxelShader"));
		
		if (shader != null) {

			Matrix4f viewMatrix = VoxelEngine.getInstance().getMainCamera().getViewMatrix();
			
			AbstractTextureMap<ResourceLocation> materialAtlas = VoxelEngine.getInstance().getTextureLoader().getTextureMap(VoxelEngine.MATERIAL_ATLAS);
			
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
					
					drawStructure(bufferSource, poseStack, structure, 1F, 1F, 1F, 1F);
					
					for (RenderType renderLayer : RenderType.voxelRenderLayers()) {
						compiledStructure.getBuffer(renderLayer).upload(bufferSource.getBuffer(renderLayer), BufferUsage.DYNAMIC);
					}
					compiledStructure.setDirty(false);
					break;
					
				}
				
			}
			
			for (RenderType renderLayer : RenderType.voxelRenderLayers()) {
				
				renderLayer.setState();
				GL33.glPointSize(10);
				GL33.glEnable(GL33.GL_POINT_SIZE);
				
				level.getStructures().forEach((structure) -> {
					
					StructureRender compiledStructure = levelRender.getOrCreateRender(structure);
					
					if (!compiledStructure.isDirty()) {
						
						VertexBuffer buffer = compiledStructure.getBuffer(renderLayer);
						
						Vec3f position = structure.getRigidBody().getPosition();
						Quaternion rotation = structure.getRigidBody().getRotation();
						Matrix4f translationMatrix = Matrix4f.translateMatrix(position.x, position.y, position.z).mul(rotation);
						System.out.println(position);
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
	
	public static void drawStructure(BufferSource bufferSource, PoseStack poseStack, VoxelStructure structure, float r, float g, float b, float a) {
		
		for (RenderType renderLayer : RenderType.voxelRenderLayers()) {
			
			BufferBuilder buffer = bufferSource.getBuffer(renderLayer);
			buffer.begin(renderLayer.primitive(), renderLayer.vertexFormat());
			
			structure.getComponents().forEach((structureComponent) -> {
				
				poseStack.push();
				poseStack.translate(structureComponent.position.x, structureComponent.position.y, structureComponent.position.z);
				poseStack.rotate(structureComponent.orientation);
				
				drawComponent(buffer, renderLayer, poseStack, structureComponent.component, r, g, b, a);
				
				poseStack.pop();
				
			});
			
			buffer.end();
			
		}
		
	}
	
	public static void drawComponent(BufferBuilder buffer, RenderType renderLayer, PoseStack poseStack, VoxelComponent component, float r, float g, float b, float a) {
		
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
								AbstractTextureMap<ResourceLocation> texture = VoxelEngine.getInstance().getTextureLoader().getTexture(textureName);
								Vec4f texUV = texture.getUV();
								int texWidth = (int) (texture.getImageWidth() * material.pixelScale());
								int texHeight = (int) (texture.getImageHeight() * material.pixelScale());
								
								buffer.vertex(poseStack, x, y, z).nextElement().putInt(x).putInt(y).putInt(z).nextElement().putInt(sideState).color(r, g, b, a).nextElement().putFloat(texUV.x).putFloat(texUV.y).putFloat(texUV.z).putFloat(texUV.w).nextElement().putInt(texWidth).putInt(texHeight).endVertex();
								
							}
						}
						
					}
				}
			}
		}
		
	}
	
}
