package de.m_marvin.openui;

import java.util.ArrayList;
import java.util.List;

import de.m_marvin.renderengine.translation.PoseStack;
import de.m_marvin.univec.impl.Vec2f;
import de.m_marvin.univec.impl.Vec2i;

public abstract class ScreenUI {
	
	protected IScreenAligner aligment;
	protected Vec2i size;
	protected Vec2f windowSize;
	protected List<ScreenUI> subScreens = new ArrayList<>();
	protected List<IUIElement> uiElements = new ArrayList<>();
	
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
	
	public void onOpen() {
		this.subScreens.forEach(ScreenUI::onOpen);
	}
	public void onClose() {
		this.subScreens.forEach(ScreenUI::onClose);
	}

	public abstract void drawAdditionalContent(PoseStack poseStack, float partialTick);

	public void tick() {}
	
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
	
}
