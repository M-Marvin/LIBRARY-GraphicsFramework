package de.m_marvin.openui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.m_marvin.renderengine.buffers.BufferBuilder;
import de.m_marvin.renderengine.inputbinding.UserInput;
import de.m_marvin.renderengine.inputbinding.UserInput.FunctionalKey;
import de.m_marvin.renderengine.translation.PoseStack;
import de.m_marvin.univec.impl.Vec2d;
import de.m_marvin.univec.impl.Vec2f;
import de.m_marvin.univec.impl.Vec2i;

public abstract class ScreenUI {
	
	protected IScreenAligner aligment;
	protected Vec2i size;
	protected Vec2f windowSize;
	protected List<ScreenUI> subScreens = new ArrayList<>();
	protected List<IUIElement> uiElements = new ArrayList<>();
	protected UserInput input;
	
	public ScreenUI(Vec2i size, IScreenAligner aligment) {
		this.size = size;
		this.aligment = aligment;
	}
	
	public void setSize(Vec2i size) {
		this.size = size;
	}
	
	public Vec2i getSize() {
		return size;
	}
	
	public Vec2f getWindowSize() {
		return windowSize;
	}
	
	public IScreenAligner getAligment() {
		return aligment;
	}
	
	public void setAligment(IScreenAligner aligment) {
		this.aligment = aligment;
	}
	
	public void applyScreenTransformation(PoseStack poseStack, int windowWidth, int windowHeight) {
		
		float screenRatioX = this.size.x / (float) this.size.y;
		float screenRatioY = this.size.y / (float) this.size.x;
		float windowRatioX = windowWidth / (float) windowHeight;
		float windowRatioY = windowHeight / (float) windowWidth;
		float uiXscale = Math.max(1F, screenRatioX);
		float uiYscale = Math.max(1F, screenRatioY);
		float screenXscale = Math.min(1F * (1F / uiXscale), 1F / (windowRatioX * uiYscale));
		float screenYscale = Math.min(1F * (1F / uiYscale), 1F / (windowRatioY * uiXscale));
		float scaleX = screenXscale * (uiXscale / (this.size.x));
		float scaleY = screenYscale * (uiYscale / (this.size.y));
		
		Vec2f aligningOffst = this.aligment.getOffset(this.size, this.windowSize);
		
		poseStack.scale(scaleX * 2, -scaleY * 2, 1);
		poseStack.translate(- size.x / 2 + aligningOffst.x, -size.y / 2 + aligningOffst.y, 0);
		
	}
	
	public <T extends IUIElement> T addElement(T element) {
		if (this.uiElements.contains(element)) return null;
		this.uiElements.add(element);
		return element;
	}
	
	public void removeElement(IUIElement element) {
		this.uiElements.remove(element);
	}
	
	public <T extends ScreenUI> T addSubScreen(T screen) {
		if (this.subScreens.contains(screen)) return null;
		this.subScreens.add(screen);
		return screen;
	}
	
	public void removeSubScreen(ScreenUI screen) {
		this.subScreens.remove(screen);
	}
	
	public void onOpen(UserInput inputHandler) {
		this.input = inputHandler;
		inputHandler.addKeyboardListener(this::keyPressed);
		inputHandler.addTextInputListener(this::textInput);
		inputHandler.addMouseListener(this::mousePressed);
		inputHandler.addCursorListener(this::mouseMove);
		this.subScreens.forEach((s) -> s.onOpen(inputHandler));
	}
	public void onClose(UserInput inputHandler) {
		this.input = null;
		inputHandler.removeKeyboardListener(this::keyPressed);
		inputHandler.removeTextInputListener(this::textInput);
		inputHandler.removeMouseListener(this::mousePressed);
		inputHandler.removeCursorListener(this::mouseMove);
		this.subScreens.forEach((s) -> s.onClose(inputHandler));
	}
	
	public UserInput getInput() {
		return input;
	}

	public void drawScreen(PoseStack poseStack, int windowWidth, int windowHeight, float partialTick) {

		poseStack.push();
		
		float windowSizeX = Math.max(1F, (windowWidth / (float) windowHeight) / (this.size.x / (float) this.size.y)) * this.size.x;
		float windowSizeY = Math.max(1F, (windowHeight / (float) windowWidth) / (this.size.y / (float) this.size.x)) * this.size.y;
		this.windowSize = new Vec2f(windowSizeX, windowSizeY);
		
		applyScreenTransformation(poseStack, windowWidth, windowHeight);
		
		drawAdditionalContent(poseStack, partialTick);
		
		this.uiElements.forEach((element) -> {
			poseStack.push();
			element.draw(poseStack);
			poseStack.pop();
		});
		
		poseStack.pop();
		
		this.subScreens.forEach(screen -> screen.drawScreen(poseStack, windowWidth, windowHeight, partialTick));
		
	}

	public void update() {
		
		tick();
		this.uiElements.forEach((element) -> element.tick());
		this.subScreens.forEach(screen -> screen.update());
		
	}
	
	
	
	
	public void keyPressed(int key, int scancode, boolean pressed, boolean repeated) {}
	public void textInput(char character, Optional<FunctionalKey> functionalKey) {}
	public void mousePressed(Optional<Vec2d> scroll, int button, boolean pressed, boolean repeated) {}
	public void mouseMove(Vec2d position, boolean entered, boolean leaved) {}
	
	public abstract void drawAdditionalContent(PoseStack poseStack, float partialTick);
	public void tick() {}
	
	
	
	
	public static void drawLine(BufferBuilder buffer, PoseStack poseStack, float x1, float y1, float x2, float y2, float w, float r, float g, float b, float a) {
		float hlength = (float) Math.sqrt(Math.pow(Math.abs(x1 - x2), 2) + Math.pow(Math.abs(y1 - y2), 2));
		float xlength = x1 - x2;
		float ylength = y1 - y2;
		float ox = ylength / hlength;
		float oy = xlength / hlength;
		buffer.vertex(poseStack, x1 + ox * w, y1 - oy * w).color(r, g, b, a).uv(0, 0).endVertex();
		buffer.vertex(poseStack, x1 - ox * w, y1 + oy * w).color(r, g, b, a).uv(0, 0).endVertex();
		buffer.vertex(poseStack, x2 - ox * w, y2 + oy * w).color(r, g, b, a).uv(0, 0).endVertex();
		buffer.vertex(poseStack, x2 + ox * w, y2 - oy * w).color(r, g, b, a).uv(0, 0).endVertex();
	}
	
	public static void drawRectangle(BufferBuilder buffer, PoseStack poseStack, float topLeftX, float topLeftY, float width, float height, float r, float g, float b, float a) {
		float lx = topLeftX;
		float ty = topLeftY;
		float rx = lx + width;
		float by = ty + height;
		buffer.vertex(poseStack, lx, ty).color(r, g, b, a).uv(0, 0).endVertex();
		buffer.vertex(poseStack, rx, ty).color(r, g, b, a).uv(0, 0).endVertex();
		buffer.vertex(poseStack, rx, by).color(r, g, b, a).uv(0, 0).endVertex();
		buffer.vertex(poseStack, lx, by).color(r, g, b, a).uv(0, 0).endVertex();
	}

	public static void drawRectangleTransitionH(BufferBuilder buffer, PoseStack poseStack, float topLeftX, float topLeftY, float width, float height, float r1, float g1, float b1, float a1, float r2, float g2, float b2, float a2) {
		float lx = topLeftX;
		float ty = topLeftY;
		float rx = lx + width;
		float by = ty + height;
		buffer.vertex(poseStack, lx, ty).color(r2, g2, b2, a2).uv(0, 0).endVertex();
		buffer.vertex(poseStack, rx, ty).color(r2, g2, b2, a2).uv(0, 0).endVertex();
		buffer.vertex(poseStack, rx, by).color(r1, g1, b1, a1).uv(0, 0).endVertex();
		buffer.vertex(poseStack, lx, by).color(r1, g1, b1, a1).uv(0, 0).endVertex();
	}

	public static void drawRectangleTransitionV(BufferBuilder buffer, PoseStack poseStack, float topLeftX, float topLeftY, float width, float height, float r1, float g1, float b1, float a1, float r2, float g2, float b2, float a2) {
		float lx = topLeftX;
		float ty = topLeftY;
		float rx = lx + width;
		float by = ty + height;
		buffer.vertex(poseStack, lx, ty).color(r1, g1, b1, a1).uv(0, 0).endVertex();
		buffer.vertex(poseStack, rx, ty).color(r2, g2, b2, a2).uv(0, 0).endVertex();
		buffer.vertex(poseStack, rx, by).color(r2, g2, b2, a2).uv(0, 0).endVertex();
		buffer.vertex(poseStack, lx, by).color(r1, g1, b1, a1).uv(0, 0).endVertex();
	}
	
}
