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
		
		float screenRatio = this.size.x / (float) this.size.y;
		float windowRatio = windowWidth / (float) windowHeight;
		float offset = windowRatio - screenRatio;
		float offsetY = offset < 0 ? -offset : 0;
		float offsetX = offset > 0 ? offset : 0;
		
		//Matrix3f.createTranslationMatrix(windowRatio, offset)
		//return Matrix3f.createScaleMatrix(1 / this.size.x, 1 / this.size.y);
		
		System.out.println(windowRatio);
		poseStack.scale(2F / (this.size.x * windowRatio), 2F / (this.size.y), 1);
		//poseStack.scale(0.25F, this.size.y / (float) windowHeight, 1);
		//poseStack.translate(-1 + offsetX / 2, -1 + offsetY / 2, 0);
		//poseStack.scale(1F / offsetX, 1, 1);
		
		// 0 -> 1.0
		// 1 -> 0.5
		// 1.5 -> 0.25
		
		//System.out.println(offsetX);
		
		this.uiElements.forEach((element) -> {
			poseStack.push();
			element.draw(poseStack);
			poseStack.pop();
		});
		
		poseStack.pop();
		
	}
	
}
