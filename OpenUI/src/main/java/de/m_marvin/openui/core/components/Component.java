package de.m_marvin.openui.core.components;

import java.util.Optional;

import de.m_marvin.renderengine.resources.IResourceProvider;
import de.m_marvin.univec.impl.Vec2d;
import de.m_marvin.univec.impl.Vec2i;

public class Component<R extends IResourceProvider<R>> extends Compound<R> {
	
	protected boolean cursorOverComponent = false;
	
	@Override
	public void setup() {
		super.setup();
		this.getContainer().getUserInput().addCursorListener(this::cursorMove);
		this.getContainer().getUserInput().addMouseListener(this::mouseEvent);
	}
	
	@Override
	public void cleanup() {
		super.cleanup();
		this.getContainer().getUserInput().removeCursorListener(this::cursorMove);
		this.getContainer().getUserInput().removeMouseListener(this::mouseEvent);
	}
	
	public void cursorMove(Vec2d position, boolean entered, boolean leaved) {
		this.cursorOverComponent = isInComponent(new Vec2i(position));
		System.out.println("tEST");
	}
	
	public void mouseEvent(Optional<Vec2d> scroll, int button, boolean pressed, boolean repeated) {}
	
	public boolean isFocused() {
		return this.container.getFocusedComponent() == this;
	}
	
	public void setFocused(boolean focus) {
		this.container.setFocusedComponent(focus ? this : null);
	}
	
	public boolean isInComponent(Vec2i position) {
		return	this.offset.x <= position.x && this.offset.y <= position.y &&
				this.offset.x + this.size.x >= position.x && this.offset.y + this.size.y >= position.y; 
	}
	
	public boolean isCursorOverComponent() {
		return cursorOverComponent;
	}
	
}
