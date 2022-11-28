package de.m_marvin.openui;

import java.util.ArrayList;
import java.util.List;

import de.m_marvin.renderengine.translation.PoseStack;
import de.m_marvin.unimat.impl.Matrix3f;
import de.m_marvin.unimat.impl.Matrix4f;
import de.m_marvin.univec.impl.Vec2i;

public abstract class ScreenUI {
	
	protected Vec2i size;
	protected List<IUIElement> uiElements = new ArrayList<>();
	
	public ScreenUI(Vec2i size) {
		this.size = size;
	}
	
	public void setSize(Vec2i size) {
		this.size = size;
	}
	
	public Vec2i getSize() {
		return size;
	}
	
	public Matrix3f getScreenTransformation(int windowWidth, int windowHeight) {
		return new Matrix3f();
	}
	
	public <T extends IUIElement> T addElement(T element) {
		if (this.uiElements.contains(element)) return null;
		this.uiElements.add(element);
		return element;
	}
	
	public void removeElement(IUIElement element) {
		this.uiElements.remove(element);
	}

	public abstract void onOpen();
	public abstract void onClose();
	
	/* Rendering related stuff that has to be executed on the render thread */
	
	public void drawScreen(PoseStack poseStack, int windowWidth, int windowHeight) {
		
		poseStack.push();
		
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
		
		float offsetX = -Math.min(0, windowRatioY - screenRatioY); // offset > 0 ? offset : 0;
		float offsetY = -Math.min(0, windowRatioX - screenRatioX); // offset < 0 ? -offset : 0;
		
		//Matrix3f.createTranslationMatrix(windowRatio, offset)
		//return Matrix3f.createScaleMatrix(1 / this.size.x, 1 / this.size.y);
		
		System.out.println(windowRatioY + " " + screenRatioY);
		
		poseStack.scale(scaleX * 2, scaleY * 2, 1);
		poseStack.translate(-1 + offsetX * 0.5F, -1 + offsetY * 0.5F, 0);
		
		System.out.println((scaleX * offsetX * size.x));
		
		this.uiElements.forEach((element) -> {
			poseStack.push();
			element.draw(poseStack);
			poseStack.pop();
		});
		
		poseStack.pop();
		
	}
	
}
