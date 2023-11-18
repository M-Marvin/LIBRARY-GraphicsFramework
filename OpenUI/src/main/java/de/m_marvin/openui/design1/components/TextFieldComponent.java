package de.m_marvin.openui.design1.components;

import java.awt.Color;
import java.awt.Font;

import de.m_marvin.openui.core.KeyCodes;
import de.m_marvin.openui.core.UIRenderMode;
import de.m_marvin.openui.core.components.Component;
import de.m_marvin.openui.design1.TextRenderer;
import de.m_marvin.openui.design1.UtilRenderer;
import de.m_marvin.renderengine.buffers.defimpl.SimpleBufferSource;
import de.m_marvin.renderengine.fontrendering.FontRenderer;
import de.m_marvin.renderengine.inputbinding.UserInput;
import de.m_marvin.renderengine.resources.defimpl.ResourcePath;
import de.m_marvin.renderengine.translation.PoseStack;
import de.m_marvin.univec.impl.Vec2i;

public class TextFieldComponent extends Component<ResourcePath> {
	
	protected Color color;
	protected Color textColor;
	protected Font font = new Font("arial", Font.BOLD, 16);
	protected String text = "";
	
	protected boolean textOverride = false;
	protected boolean cursorState;
	protected int cursorPosition = 0;
	protected int textOffset = 0;

	public TextFieldComponent(Color textColor, Color color) {
		this.color = color;
		this.textColor = textColor;

		setMargin(5, 5, 5, 5);
		setSize(new Vec2i(120, FontRenderer.getFontHeight(this.font) + 2));
		fixSize();
	}
	
	public TextFieldComponent(Color textColor) {
		this(textColor, Color.BLACK);
	}
	
	public TextFieldComponent() {
		this(Color.WHITE);
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		assert color != null : "Argument can not be null!";
		this.color = color;
		this.redraw();
	}
	
	public Color getTextColor() {
		return textColor;
	}
	
	public void setTextColor(Color textColor) {
		assert textColor != null : "Argument can not be null!";
		this.textColor = textColor;
		this.redraw();
	}
	
	public Font getFont() {
		return font;
	}
	
	public void setFont(Font font) {
		assert font != null : "Argument can not be null!";
		this.font = font;
		this.redraw();
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		assert text != null : "Argument can not be null!";
		this.text = text;
		this.redraw();
	}
	
	public void setCursorPosition(int cursorPosition) {
		this.cursorPosition = Math.max(0, Math.min(this.text.length(), cursorPosition));
		if (this.textOffset > this.cursorPosition) {
			this.textOffset = this.cursorPosition;
		} else {
			int lastIndex = this.textOffset + FontRenderer.limitStringWidth(this.text.substring(this.textOffset), this.font, this.size.x - 22).length();
			if (lastIndex < this.cursorPosition && lastIndex < this.text.length()) this.textOffset += Math.min(this.cursorPosition - (lastIndex), this.text.length() - this.textOffset);
		}
	}
	
	@Override
	public void setup() {
		super.setup();
		this.getContainer().getUserInput().addKeyboardListener(this::keyTyped);
	}
	
	@Override
	public void cleanup() {
		super.cleanup();
		this.getContainer().getUserInput().removeKeyboardListener(this::keyTyped);
	}
	
	public void keyTyped(int key, int scancode, boolean pressed, boolean repeated) {
		if (this.isFocused() && (pressed || repeated)) {
			
			switch (key) {
			case KeyCodes.KEY_INSERT: if (!repeated) this.textOverride = !this.textOverride; break;
			case KeyCodes.KEY_POS1: if (!repeated) setCursorPosition(0); break;
			case KeyCodes.KEY_END: if (!repeated) setCursorPosition(this.text.length()); break;
			case KeyCodes.KEY_LEFT: setCursorPosition(this.cursorPosition - 1); break;
			case KeyCodes.KEY_RIGHT: setCursorPosition(this.cursorPosition + 1); break;
			
			case KeyCodes.KEY_BACKSPACE: 
				deleteText(this.cursorPosition - 1);
				setCursorPosition(this.cursorPosition - 1);
				break;
			case KeyCodes.KEY_DELETE: deleteText(this.cursorPosition); break;
			default: if (KeyCodes.isPrintable(key)) textInput(key);
			}
			
			this.cursorState = true;
			this.redraw();
			
		}
	}
	
	protected void deleteText(int pos) {
		if (pos >= 0 && pos < this.text.length()) {
			this.text = this.text.substring(0, pos) + this.text.substring(pos + 1);
		}
	}
	
	protected void textInput(int keycode) {
		String s = String.valueOf((char) keycode);
		UserInput userInput = this.getContainer().getUserInput();
		if (!userInput.isCapsLockOn() && !userInput.isKeyPressed(KeyCodes.KEY_LEFT_SHIFT) && !userInput.isKeyPressed(KeyCodes.KEY_RIGHT_SHIFT))
			s = s.toLowerCase();
		this.text = this.text.substring(0, this.cursorPosition) + String.valueOf(s.charAt(0)) + this.text.substring(this.cursorPosition);
		this.setCursorPosition(this.cursorPosition + 1);
	}
	
	@Override
	public void onChangeFocus() {
		if (this.isFocused()) {
			this.cursorState = false;
			this.tick(1);
		} else {
			this.cursorState = false;
			this.redraw();
		}
	}
	
	@Override
	public void tick(int arg) {
		if (arg == 1 && this.isFocused()) {
			this.cursorState = !this.cursorState;
			this.scheduleTick(400, 1);
			this.redraw();
		}
	}
	
	@Override
	public void drawBackground(SimpleBufferSource<ResourcePath, UIRenderMode<ResourcePath>> bufferSource, PoseStack matrixStack) {
		
		String offsetText = this.text.substring(this.textOffset);
		String renderableText = FontRenderer.limitStringWidth(offsetText, this.font, this.size.x - 2);
		
		TextRenderer.renderText(2, 1, renderableText, this.font, this.textColor, this.container.getActiveTexureLoader(), bufferSource, matrixStack);
		
	}
	
	@Override
	public void drawForeground(SimpleBufferSource<ResourcePath, UIRenderMode<ResourcePath>> bufferSource, PoseStack matrixStack) {
		
		UtilRenderer.renderFrame(this.size.x, this.size.y, 1, this.textColor, bufferSource, matrixStack);
		
		if (this.isFocused() && this.cursorState) {

			String offsetText = this.text.substring(this.textOffset).substring(0, this.cursorPosition - this.textOffset);
			int cursorPos = FontRenderer.calculateStringWidth(offsetText, this.font);
			
			int cursorWidth = this.textOverride ? FontRenderer.calculateStringWidth(String.valueOf(this.text.charAt(cursorPosition)), this.font) : 2;
			int cursorHeight = FontRenderer.getFontHeight(this.font) - 2;
			
			UtilRenderer.renderRectangle(cursorPos + 2, 2, cursorWidth, cursorHeight, this.textColor, bufferSource, matrixStack);
			
		}
		
	}
	
}
