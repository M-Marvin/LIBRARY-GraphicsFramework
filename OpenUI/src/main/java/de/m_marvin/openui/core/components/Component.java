package de.m_marvin.openui.core.components;

import java.awt.Font;
import java.util.Optional;

import de.m_marvin.renderengine.resources.IResourceProvider;
import de.m_marvin.univec.impl.Vec2d;
import de.m_marvin.univec.impl.Vec2i;

public class Component<R extends IResourceProvider<R>> extends Compound<R> {
	
	public static final Font DEFAULT_FONT = new Font("arial", Font.BOLD, 16);
	
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
	
	protected void cursorMove(Vec2d position, boolean entered, boolean leaved) {
		boolean overComponent = this.container.getTopComponentUnderCursor() == this;
		if (overComponent != this.cursorOverComponent || this.cursorOverComponent) {
			if (overComponent != this.cursorOverComponent) {
				this.cursorOverComponent = overComponent;
				this.redraw();
			}
			this.onCursorMoveOver(new Vec2i(position).sub(this.getParentOffset()), !overComponent);
		}
	}
	
	protected void mouseEvent(Optional<Vec2d> scroll, int button, boolean pressed, boolean repeated) {
		if (this.cursorOverComponent) {
			if (scroll.isPresent()) {
				this.onScroll(scroll.get());
			} else {
				if (pressed) this.setFocused(true);
				this.onClicked(button, pressed, repeated);
			}
		}
	}
	
	protected void onCursorMoveOver(Vec2i position, boolean leaved) {}
	protected void onClicked(int button, boolean pressed, boolean repeated) {}
	protected void onScroll(Vec2d scroll) {}
	public void onChangeFocus() {}
	public void scheduledTick(int arg) {}
	
	public void scheduleTick(int delay, int arg) {
		this.container.scheduleTick(delay, this, arg);
	}
	
	public boolean isFocused() {
		return this.container.getFocusedComponent() == this;
	}
	
	public void setFocused(boolean focus) {
		this.container.setFocusedComponent(focus ? this : null);
	}
	
	public boolean isCursorOverComponent() {
		return cursorOverComponent;
	}
	
}
