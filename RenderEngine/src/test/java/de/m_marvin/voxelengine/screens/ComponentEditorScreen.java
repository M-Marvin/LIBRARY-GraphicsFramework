package de.m_marvin.voxelengine.screens;

import java.util.Optional;

import org.lwjgl.opengl.GL11;

import de.m_marvin.openui.dep.ScreenAligment;
import de.m_marvin.openui.dep.ScreenUI;
import de.m_marvin.renderengine.GLStateManager;
import de.m_marvin.renderengine.buffers.BufferBuilder;
import de.m_marvin.renderengine.buffers.BufferUsage;
import de.m_marvin.renderengine.buffers.VertexBuffer;
import de.m_marvin.renderengine.resources.defimpl.ResourceLocation;
import de.m_marvin.renderengine.shaders.ShaderInstance;
import de.m_marvin.renderengine.textures.AbstractTextureMap;
import de.m_marvin.renderengine.translation.PoseStack;
import de.m_marvin.unimat.impl.Matrix4f;
import de.m_marvin.unimat.impl.Quaternion;
import de.m_marvin.univec.impl.Vec2d;
import de.m_marvin.univec.impl.Vec2f;
import de.m_marvin.univec.impl.Vec2i;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;
import de.m_marvin.voxelengine.VoxelEngine;
import de.m_marvin.voxelengine.rendering.BufferSource;
import de.m_marvin.voxelengine.rendering.GameRenderer;
import de.m_marvin.voxelengine.rendering.RenderStage;
import de.m_marvin.voxelengine.rendering.RenderType;
import de.m_marvin.voxelengine.utility.Raytracer;
import de.m_marvin.voxelengine.world.VoxelComponent;

public class ComponentEditorScreen extends ScreenBase {
	
	protected Matrix4f viewMatrix = new Matrix4f();
	protected Vec2f rotation = new Vec2f(0, 0);
	protected Vec2f rotationMotion = new Vec2f(0, 0);
	protected float rotationDirection = 0F;
	protected float zoom = 1F;
	protected float zoomMotion = 0F;
	protected Vec3f offset = new Vec3f(0, 0, -100);
	protected boolean cameraButtonPressed = false;
	protected Vec2d lastMousePos = null;
	protected Vec3f ray2 = new Vec3f(0, 0, 0);
	protected Vec3f ray1 = new Vec3f(0, 0, 0);
	
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
	public void tick() {
		
		this.rotation.addI(this.rotationMotion);
		this.rotationMotion.mulI(0.9F);
		
		if (getInput().isBindingActive("misc.editor.zoom.in")) {
			this.zoomMotion = 1F;
		} else if (getInput().isBindingActive("misc.editor.zoom.out")) {
			this.zoomMotion = -1F;
		}
		
		this.zoomMotion = this.zoomMotion * 0.9F;
		this.zoom += this.zoomMotion * this.zoom;
		this.zoom = Math.min(4, Math.max(0.8F, this.zoom));
		
	}
	
	@Override
	public void mouseMove(Vec2d position, boolean entered, boolean leaved) {
		
		if (this.getInput().isBindingActive("misc.editor.camera")) {
			
			if (this.rotationDirection == 0F) {
				this.rotationDirection = (rotation.y > 0.5 * Math.PI || rotation.y < -0.5 * Math.PI) ? -1F : 1F;
			}
			
			if (lastMousePos == null) {
				lastMousePos = position;
			} else {
				Vec2d movement = position.sub(lastMousePos);
				lastMousePos = position;
				if (rotation.y > Math.PI) rotation.y = (float) -(2 * Math.PI - rotation.y);
				if (rotation.y < -Math.PI) rotation.y = (float) (2 * Math.PI + rotation.y);
				if (rotation.x > Math.PI) rotation.x = (float) -(2 * Math.PI - rotation.x);
				if (rotation.x < -Math.PI) rotation.x = (float) (2 * Math.PI + rotation.x);
				rotationMotion.y = (float) (movement.y * 0.01F);
				rotationMotion.x = (float) (movement.x * 0.01F * this.rotationDirection);
			}
			
		} else if (lastMousePos != null) {
			lastMousePos = null;
			this.rotationDirection = 0F;
		} else {
			
			Vec3f rayOrigin = offset.add(0.0F, 0.0F, 0.0F).mul(-1F);
			Vec3f camPoint = Raytracer.getCameraRay(position, 0.5F, this.getWindowSize(), this.viewMatrix, VoxelEngine.getInstance().getGameRenderer().getProjectionMatrix());
			Vec3f mouseRay = camPoint.sub(rayOrigin);
			
			System.out.println(mouseRay);
			
			double angle = new Vec3f(0, 0, -1).angle(mouseRay);
			System.out.println(Math.toDegrees(angle));
			
			Raytracer raytracer = new Raytracer(rayOrigin, mouseRay);
			Optional<Vec3i> hitVoxel = raytracer.raytraceComponent(component, 0.25F, 150F);
			
			mouseRay.z *= -1;
			
			this.ray1 = rayOrigin.sub(0F, 0F, 100f);
			this.ray2 = mouseRay;
			
			//System.out.println(rayOrigin.add(mouseRay.mul(100F)));
			System.out.println(hitVoxel.isPresent() ? hitVoxel.get().toString() : "MISSED");
			
		}
		
	}
	
	protected void invert(Matrix4f m) {
		Matrix4f nm = new Matrix4f(
				1 / m.m00(), m.m01(), m.m02(), -m.m03(),
				m.m10(), 1 / m.m11(), m.m12(), -m.m13(),
				m.m20(), m.m21(), 1 / m.m22(), -m.m23(),
				m.m30(), m.m31(), m.m32(), m.m33()
			);
		m.setI(nm);
	}
	
	@Override
	public void mousePressed(Optional<Vec2d> scroll, int button, boolean pressed, boolean repeated) {
		
		if (scroll.isPresent()) {
			
			this.zoomMotion = (float) (scroll.get().y * 0.05F);
			
		} else {
			
			
			
		}
		
	}
	
	@Override
	public void drawAdditionalContent(PoseStack poseStack, float partialTicks) {
		
		GameRenderer.executeOnRenderStage(RenderStage.UI, false, () -> {
			
			BufferSource bufferSource = VoxelEngine.getInstance().getGameRenderer().getBufferSource();

			VertexBuffer vao = new VertexBuffer();
			
			// Additional UI-Components
			ShaderInstance uiShader = VoxelEngine.getInstance().getShaderLoader().getShader(VoxelEngine.getInstance().getGameRenderer().getScreenShader());
			
			if (uiShader != null) {
				
				uiShader.useShader();
				uiShader.getUniform("Interpolation").setFloat(partialTicks);
				
				for (RenderType renderLayer : RenderType.screenLayers()) {	
					BufferBuilder buffer = bufferSource.getBuffer(renderLayer);
					buffer.begin(renderLayer.primitive(), renderLayer.vertexFormat());
					
					drawRectangle(buffer, poseStack, -1, -1, 2, 2, 0, 0, 1, 0.5F);
					
					poseStack.push();
					int rasterWidth = 100;
					int countWidth = Math.floorDiv((int) windowSize.x / 2, rasterWidth) + 1;
					int countHeight = Math.floorDiv((int) windowSize.y / 2, rasterWidth) + 1;
					int ty = (int) (- (windowSize.y - getSize().y) / 2);
					int by = (int) (getSize().y + (windowSize.y - getSize().y) / 2);
					int lx = (int) (- (windowSize.x - getSize().x) / 2);
					int rx = (int) (getSize().x + (windowSize.x - getSize().x) / 2);
					
					int[] size = VoxelEngine.getInstance().getMainWindow().getSize();
					applyScreenTransformation(poseStack, size[0], size[1]);
					for (int x = -countWidth; x <= countWidth; x++) {
						int cx = getSize().x / 2 + x * rasterWidth;
						drawLine(buffer, poseStack, cx, by, cx, ty, 3, 0, 0, 0, 0.1F);
					}
					for (int y = -countHeight; y <= countHeight; y++) {
						int ry = getSize().y / 2 + y * rasterWidth;
						drawLine(buffer, poseStack, lx, ry, rx, ry, 3, 0, 0, 0, 0.1F);
					}
					poseStack.pop();
					
					buffer.end();
					
					renderLayer.setState();
					GLStateManager.disable(GL11.GL_DEPTH_TEST);
					vao.upload(buffer, BufferUsage.DYNAMIC);
					vao.bind();
					vao.drawAll(renderLayer.primitive());
					vao.unbind();
					renderLayer.resetState();
				}
				
				uiShader.unbindShader();
				
			}
			
			// Additional Voxel-Components
			ShaderInstance vxlShader = VoxelEngine.getInstance().getShaderLoader().getShader(VoxelEngine.getInstance().getGameRenderer().getVoxelShader());
			
			if (vxlShader != null) {
				
				AbstractTextureMap<ResourceLocation> materialAtlas = VoxelEngine.getInstance().getTextureLoader().getTextureMap(GameRenderer.MATERIAL_ATLAS);
				
				vxlShader.useShader();
				vxlShader.getUniform("ProjMat").setMatrix4f(VoxelEngine.getInstance().getGameRenderer().getProjectionMatrix());
				vxlShader.getUniform("HalfVoxelSize").setFloat(this.zoom * 0.51F);
				vxlShader.getUniform("Texture").setTextureSampler(materialAtlas);
				vxlShader.getUniform("AnimMat").setMatrix3f(materialAtlas.frameMatrix());
				vxlShader.getUniform("AnimMatLast").setMatrix3f(materialAtlas.lastFrameMatrix());
				vxlShader.getUniform("Interpolation").setFloat(partialTicks);
				
				if (component != null) {
					
					poseStack.push();
					poseStack.translate(this.offset);
					poseStack.scale(this.zoom, this.zoom, this.zoom);
					poseStack.rotate(new Quaternion(new Vec3i(1, 0, 0), (float) this.rotation.y));
					poseStack.rotate(new Quaternion(new Vec3i(0, 1, 0), (float) this.rotation.x));
					
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
					
					viewMatrix = poseStack.last().pose();
					
					poseStack.pop();
					
				}
				
				vxlShader.unbindShader();
				
			}
			
			// TEST
			ShaderInstance levelShader = VoxelEngine.getInstance().getShaderLoader().getShader(VoxelEngine.getInstance().getGameRenderer().getLevelShader());
			
			if (levelShader != null) {
				
				AbstractTextureMap<ResourceLocation> materialAtlas = VoxelEngine.getInstance().getTextureLoader().getTextureMap(GameRenderer.MATERIAL_ATLAS);
				
				levelShader.useShader();
				levelShader.getUniform("ProjMat").setMatrix4f(VoxelEngine.getInstance().getGameRenderer().getProjectionMatrix());
				levelShader.getUniform("Texture").setTextureSampler(materialAtlas);
				levelShader.getUniform("AnimMat").setMatrix3f(materialAtlas.frameMatrix());
				levelShader.getUniform("AnimMatLast").setMatrix3f(materialAtlas.lastFrameMatrix());
				levelShader.getUniform("Interpolation").setFloat(partialTicks);
				
				if (component != null) {
					
					poseStack.push();
					poseStack.translate(this.offset);
					poseStack.scale(this.zoom, this.zoom, this.zoom);
					poseStack.rotate(new Quaternion(new Vec3i(1, 0, 0), (float) this.rotation.y));
					poseStack.rotate(new Quaternion(new Vec3i(0, 1, 0), (float) this.rotation.x));
					
					RenderType renderLayer = RenderType.solid();
					
					BufferBuilder buffer = bufferSource.getBuffer(renderLayer);
					buffer.begin(renderLayer.primitive(), renderLayer.vertexFormat());
					GameRenderer.drawLine(buffer, poseStack, this.ray1.x, this.ray1.y, this.ray1.z, this.ray2.x, this.ray2.y, this.ray2.z, 0.01F, 1, 1, 1, 1);
					buffer.end();
					
					GLStateManager.disable(GL11.GL_DEPTH_TEST);
					vao.upload(buffer, BufferUsage.DYNAMIC);
					vao.bind();
					vao.drawAll(renderLayer.primitive());
					vao.unbind();
					
					//viewMatrix = poseStack.last().pose();
					
					poseStack.pop();
					
				}
				
				levelShader.unbindShader();
				
			}
			
			vao.discard();
			
		});
		
	}
	
}
