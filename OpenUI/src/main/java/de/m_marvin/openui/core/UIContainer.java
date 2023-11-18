package de.m_marvin.openui.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.m_marvin.openui.core.components.Component;
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
	public static final float LAYER_Z_SHIFT = -0.001F;
	
	private final Compound<R> compound;
	private final SimpleBufferSource<R, UIRenderMode<R>> bufferSource;
	private final Map<VertexBuffer, UIRenderMode<R>> vertexBuffers;
	private final Map<Compound<R>, List<VertexBuffer>> component2bufferMap;
	private final List<VertexBuffer> emptyVAOs = new ArrayList<>();
	private final UserInput userInput;
	private Compound<R> focused;
	private Matrix4f projectionMatrix;
	private PoseStack matrixStack;
	private Vec2f cursorPosition = new Vec2f(-1, -1);
	private Compound<R> topComponentUnderCursor = null;
	private Map<Long, List<ScheduleComponentTick<R>>> scheduleTicks = new LinkedHashMap<>();	
	protected record ScheduleComponentTick<R extends IResourceProvider<R>>(Component<R> component, int arg) {}
	
	public UIContainer(UserInput userInput) {
		this(DEFAULT_INITIAL_BUFFER_SIZE, userInput);
	}
	
	public UIContainer(int initalBufferSize, UserInput userInput) {
		this.userInput = userInput;
		this.bufferSource = new SimpleBufferSource<R, UIRenderMode<R>>(initalBufferSize);
		this.vertexBuffers = new LinkedHashMap<>();
		this.component2bufferMap = new HashMap<>();
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
		if (component == this.focused) return;
		Compound<R> unfocused = this.focused;
		this.focused = component;
		if (unfocused instanceof Component<R> c) c.onChangeFocus();
		if (this.focused instanceof Component<R> c) c.onChangeFocus();
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
	
	public void scheduleTick(long delay, Component<R> component, int arg) {
		long timeOfExecution = System.currentTimeMillis() + delay;
		List<ScheduleComponentTick<R>> tasks = this.scheduleTicks.getOrDefault(timeOfExecution, new ArrayList<>());
		tasks.add(new ScheduleComponentTick<>(component, arg));
		this.scheduleTicks.put(timeOfExecution, tasks);	
	}
	
	/* Rendering */
	
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
	
	/**
	 * Check if any components need to be redrawn, and update the VAOs.<br>
	 * <b>NEEDS TO BE CALLED ON RENDER THREAD</b>
	 */
	public void updateComponentVAOs(TextureLoader<R, ? extends ISourceFolder> textureLoader) {
		this.activeTexureLoader = textureLoader;
		if (this.matrixStack == null || !this.matrixStack.cleared()) this.matrixStack = new PoseStack();
		this.matrixStack.push();
		
		List<Compound<R>> nextLayerComponents = new ArrayList<>();
		nextLayerComponents.add(this.compound);
		
		boolean redrawAbove = false;
		while (nextLayerComponents.size() > 0) {
			List<Compound<R>> nextLayer = new ArrayList<>();
			boolean redrawAll = redrawAbove;
			for (Compound<R> component : nextLayerComponents) {
				if (component.checkRedraw() || redrawAll) {
					updateVAOs(component);
					redrawAbove = true;
				}
				
				nextLayer.addAll(component.getChildComponents());
			}
			nextLayerComponents = nextLayer;
			matrixStack.translate(0, 0, -0.1F);
		}
		
		this.matrixStack.pop();
		this.matrixStack.assertCleared();	
	}
	
	protected void updateVAOs(Compound<R> component) {
		
		matrixStack.push();
		matrixStack.translate(component.getAbsoluteOffset().x, component.getAbsoluteOffset().y, 0);
		removeOutdatedVAOs(component);
		component.drawBackground(this.bufferSource, this.matrixStack);
		uploadNewVAOs(bufferSource, component);
		matrixStack.translate(0, 0, LAYER_Z_SHIFT);
		component.drawForeground(this.bufferSource, this.matrixStack);
		uploadNewVAOs(bufferSource, component);
		matrixStack.pop();
		
	}

	protected VertexBuffer getEmptyVAO() {
		if (this.emptyVAOs.size() > 0) return this.emptyVAOs.remove(0);
		return new VertexBuffer();
	}
	
	protected void removeOutdatedVAOs(Compound<R> component) {
		List<VertexBuffer> removed = this.component2bufferMap.get(component);
		if (removed != null) {
			for (VertexBuffer r : removed) this.vertexBuffers.remove(r);
			this.emptyVAOs.addAll(removed);
			removed.clear();
		}
	}
	
	public void deleteVAOs(Compound<R> component) {
		List<VertexBuffer> removed = this.component2bufferMap.remove(component);
		for (VertexBuffer r : removed) this.vertexBuffers.remove(r);
		this.emptyVAOs.addAll(removed);
	}
	
	protected void uploadNewVAOs(SimpleBufferSource<R, UIRenderMode<R>> bufferSource, Compound<R> component) {
		
		for (UIRenderMode<R> renderMode : bufferSource.getBufferTypes()) {
			if (!this.component2bufferMap.containsKey(component)) this.component2bufferMap.put(component, new ArrayList<>());
			
			BufferBuilder bufferBuilder = bufferSource.getBuffer(renderMode);
			while (bufferBuilder.completedBuffers() > 0) {
				VertexBuffer buffer = getEmptyVAO();
				buffer.upload(bufferBuilder, BufferUsage.STATIC);
				this.component2bufferMap.get(component).add(buffer);
				this.vertexBuffers.put(buffer, renderMode);
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
		
		this.activeTexureLoader = textureLoader;
		
		UIRenderMode<R> activeMode = null;
		
		for (VertexBuffer buffer : this.vertexBuffers.keySet()) {

			UIRenderMode<R> renderMode = this.vertexBuffers.get(buffer);
			
			if (activeMode != renderMode) {
				activeMode = renderMode;
				
				ShaderInstance shader = shaderLoader.getOrLoadShader(renderMode.shader(), Optional.of(renderMode.vertexFormat()));
				shader.useShader();
				renderMode.setupRenderMode(shader, this);
				
			}

			buffer.bind();
			buffer.drawAll(renderMode.primitive());
			
		}
		
		// Trigger schedule ticks
		long time = System.currentTimeMillis();
		Optional<Long> timeToExecute = this.scheduleTicks.keySet().stream().filter(t -> t < time).findAny();
		if (timeToExecute.isPresent()) {
			this.scheduleTicks.remove(timeToExecute.get()).forEach(s -> s.component().tick(s.arg()));
		}
		
	}
	
	private TextureLoader<R, ? extends ISourceFolder> activeTexureLoader;
	
	public TextureLoader<R, ? extends ISourceFolder> getActiveTexureLoader() {
		return activeTexureLoader;
	}
	
}
