package de.m_marvin.openui.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.m_marvin.openui.core.components.Compound;
import de.m_marvin.renderengine.buffers.BufferBuilder;
import de.m_marvin.renderengine.buffers.BufferUsage;
import de.m_marvin.renderengine.buffers.VertexBuffer;
import de.m_marvin.renderengine.buffers.defimpl.SimpleBufferSource;
import de.m_marvin.renderengine.inputbinding.UserInput;
import de.m_marvin.renderengine.resources.IResourceProvider;
import de.m_marvin.renderengine.resources.ISourceFolder;
import de.m_marvin.renderengine.shaders.ShaderInstance;
import de.m_marvin.renderengine.shaders.ShaderLoader;
import de.m_marvin.renderengine.textures.utility.TextureLoader;
import de.m_marvin.renderengine.translation.PoseStack;
import de.m_marvin.unimat.impl.Matrix4f;
import de.m_marvin.univec.impl.Vec2f;
import de.m_marvin.univec.impl.Vec2i;

public class UIContainer<R extends IResourceProvider<R>> {
	
	public static final int DEFAULT_INITIAL_BUFFER_SIZE = 3600;
	
	private final Compound<R> compound;
	private final SimpleBufferSource<R, UIRenderMode<R>> bufferSource;
	private final Map<UIRenderMode<R>, Map<Compound<R>, List<VertexBuffer>>> vertexBuffers;
	private final List<VertexBuffer> emptyVAOs = new ArrayList<>();
	private final UserInput userInput;
	private Compound<R> focused;
	private Matrix4f projectionMatrix;
	private PoseStack matrixStack;
	private Vec2f cursorPosition = new Vec2f(-1, -1);
	private Compound<R> topComponentUnderCursor = null;
	
	public UIContainer(UserInput userInput) {
		this(DEFAULT_INITIAL_BUFFER_SIZE, userInput);
	}
	
	public UIContainer(int initalBufferSize, UserInput userInput) {
		this.userInput = userInput;
		this.bufferSource = new SimpleBufferSource<R, UIRenderMode<R>>(initalBufferSize);
		this.vertexBuffers = new HashMap<>();
		this.compound = new Compound<R>();
		this.compound.setContainer(this);
		this.compound.setOffset(new Vec2i(0, 0));
		this.compound.setMargin(0, 0, 0, 0);
		this.compound.setLayout(null);
		screenResize(new Vec2i(1000, 600));
		
		this.userInput.addCursorListener((position, entered, leaved) -> {
			this.cursorPosition = new Vec2f(position);
			this.topComponentUnderCursor = findTopComponentUnder(new Vec2i(position));
		});
	}
	
	public Compound<R> findTopComponentUnder(Vec2i position) {
		return findTopComponentUnder0(this.getRootCompound(), position);
	}

	private Compound<R> findTopComponentUnder0(Compound<R> c, Vec2i position) {
		for (Compound<R> cc : c.getChildComponents()) {
			if (cc.isInComponent(position.sub(c.getOffset()))) return findTopComponentUnder0(cc, position);
		}
		return c;
	}
	
	/**
	 * Change screen size of the UI container and update layout.
	 * 
	 * @param size Screen size in pixels
	 */
	public void screenResize(Vec2i size) {
		this.projectionMatrix = Matrix4f.orthographic(0, size.x, 0, size.y, -1F, 1F);
		this.compound.setSize(size);
		this.compound.updateLayout();
	}
	
	public Compound<R> getRootCompound() {
		return compound;
	}
	
	public Vec2i calculateMinScreenSize() {
		return compound.calculateMinSize();
	}
	
	public void setFocusedComponent(Compound<R> component) {
		this.focused = component;
	}
	
	public Compound<R> getFocusedComponent() {
		return this.focused;
	}
	
	public UserInput getUserInput() {
		return userInput;
	}
	
	public Vec2f getCursorPosition() {
		return cursorPosition;
	}
	
	public Compound<R> getTopComponentUnderCursor() {
		return topComponentUnderCursor;
	}
	
	/* Rendering */
	
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
	
	/**
	 * Check if any components need to be redrawn, and update the VAOs.<br>
	 * <b>NEEDS TO BE CALLED ON RENDER THREAD</b>
	 */
	public void updateOutdatedVAOs() {
		if (this.matrixStack == null || !this.matrixStack.cleared()) this.matrixStack = new PoseStack();
		this.matrixStack.push();
		this.compound.updateOutdatedVAOs(this, new Vec2i(0, 0), this.matrixStack);
		this.matrixStack.pop();
		this.matrixStack.assertCleared();	
	}
	
	public void updateVAOs(Compound<R> component, Vec2i offset) {
		
		matrixStack.push();
		matrixStack.translate(offset.x, offset.y, 0);
		component.drawBackground(this.bufferSource, this.matrixStack);
		component.drawForeground(this.bufferSource, this.matrixStack);
		matrixStack.pop();
		
		removeOutdatedVAOs(component);
		uploadNewVAOs(bufferSource, component);
		
		Iterator<UIRenderMode<R>> it = this.vertexBuffers.keySet().iterator();
		while (it.hasNext()) {
			if (this.vertexBuffers.get(it.next()).isEmpty()) it.remove();
		}
		
	}

	protected VertexBuffer getEmptyVAO() {
		if (this.emptyVAOs.size() > 0) return this.emptyVAOs.remove(0);
		return new VertexBuffer();
	}
	
	protected void removeOutdatedVAOs(Compound<R> component) {
		for (Map<Compound<R>, List<VertexBuffer>> map : vertexBuffers.values()) {
			if (map.containsKey(component)) {
				for (VertexBuffer buffer : map.get(component)) {
					this.emptyVAOs.add(buffer);
				}
				map.get(component).clear();
			}
		}
	}
	
	public void deleteVAOs(Compound<R> component) {
		for (Map<Compound<R>, List<VertexBuffer>> map : vertexBuffers.values()) {
			if (map.containsKey(component)) {
				for (VertexBuffer buffer : map.get(component)) {
					this.emptyVAOs.add(buffer);
				}
				map.remove(component);
			}
		}
	}
	
	protected void uploadNewVAOs(SimpleBufferSource<R, UIRenderMode<R>> bufferSource, Compound<R> component) {
		for (UIRenderMode<R> renderMode : bufferSource.getBufferTypes()) {
			if (!this.vertexBuffers.containsKey(renderMode)) this.vertexBuffers.put(renderMode, new HashMap<>());
			Map<Compound<R>, List<VertexBuffer>> componentMap = this.vertexBuffers.get(renderMode);
			
			if (!componentMap.containsKey(component)) componentMap.put(component, new ArrayList<>());
			List<VertexBuffer> bufferList = componentMap.get(component);
			
			BufferBuilder bufferBuilder = bufferSource.getBuffer(renderMode);
			for (int i = 0; i < bufferBuilder.completedBuffers(); i++) {
				VertexBuffer buffer = getEmptyVAO();
				buffer.upload(bufferBuilder, BufferUsage.DYNAMIC);
				bufferList.add(buffer);
			}
		}
	}
	
	/**
	 * Render the cached VAOs.<br>
	 * <b>NEEDS TO BE CALLED ON RENDER THREAD</b>
	 * 
	 * @param shaderLoader Shader loader
	 * @param textureLoader Texture loader
	 */
	public void renderVAOs(ShaderLoader<R, ? extends ISourceFolder> shaderLoader, TextureLoader<R, ? extends ISourceFolder> textureLoader) {
		
		for (UIRenderMode<R> renderMode : this.vertexBuffers.keySet()) {
			
			Map<Compound<R>, List<VertexBuffer>> bufferMap = this.vertexBuffers.get(renderMode);
			
			ShaderInstance shader = shaderLoader.getOrLoadShader(renderMode.shader(), Optional.of(renderMode.vertexFormat()));
			shader.useShader();
			renderMode.setupRenderMode(shader, textureLoader, this);
			
			for (List<VertexBuffer> bufferList : bufferMap.values()) {
				for (VertexBuffer buffer : bufferList) {
					
					buffer.bind();
					buffer.drawAll(renderMode.primitive());
					
				}
			}
			
		}
		
	}
	
}
