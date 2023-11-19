package de.m_marvin.openui.flatmono.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

import de.m_marvin.openui.core.UIRenderMode;
import de.m_marvin.openui.core.components.Component;
import de.m_marvin.openui.flatmono.TextRenderer;
import de.m_marvin.openui.flatmono.UtilRenderer;
import de.m_marvin.renderengine.buffers.defimpl.SimpleBufferSource;
import de.m_marvin.renderengine.fontrendering.FontRenderer;
import de.m_marvin.renderengine.inputbinding.KeyCodes;
import de.m_marvin.renderengine.inputbinding.UserInput.FunctionalKey;
import de.m_marvin.renderengine.resources.defimpl.ResourceLocation;
import de.m_marvin.renderengine.translation.PoseStack;
import de.m_marvin.univec.impl.Vec2i;

public class TextFieldComponent extends Component<ResourceLocation> {
	
	public static final int FRAME_WIDTH = 1;
	public static final int TEXT_BORDER_GAP = 2;
	public static final int TEXT_AUTO_SCROLL_AREA = 20;
	
	protected Color color;
	protected Color textColor;
	protected Font font = DEFAULT_FONT;
	protected String text = "";
	protected int maxLength = 64;
	protected Consumer<String> changeCallback = s -> {};
	
	protected boolean textOverride = false;
	protected boolean cursorState = false;
	protected int cursorPosition = 0;
	protected int textOffset = 0;
	protected int selectionStart = -1;
	protected int selectionEnd = -1;
	
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
	
	public void setChangeCallback(Consumer<String> changeCallback) {
		this.changeCallback = changeCallback;
	}
	
	public Consumer<String> getChangeCallback() {
		return changeCallback;
	}
	
	public int getMaxLength() {
		return maxLength;
	}
	
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
		if (this.text.length() > maxLength) {
			this.text = this.text.substring(0, maxLength);
			this.redraw();
		}
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
	
	public void setCursorPosition(int charIndex) {
		this.cursorPosition = Math.max(0, Math.min(this.text.length(), charIndex));
		if (this.textOffset > this.cursorPosition - 1) {
			this.textOffset = Math.max(this.cursorPosition - 1, 0);
		} else {
			int lastIndex = this.textOffset + FontRenderer.limitStringWidth(this.text.substring(this.textOffset), this.font, this.size.x - TEXT_BORDER_GAP * 2).length();
			if (lastIndex - 1 < this.cursorPosition && lastIndex < this.text.length()) this.textOffset += Math.min(this.cursorPosition - (lastIndex - 1), this.text.length() - this.textOffset);
		}
	}
	
	public void moveCursorNear(int xPos) {
		this.cursorPosition = this.textOffset + FontRenderer.limitStringWidth(this.text.substring(this.textOffset), this.font, Math.min(this.size.x - TEXT_BORDER_GAP * 2, xPos)).length();
	}
	
	public void clearSelection() {
		this.selectionStart = this.selectionEnd = -1;
	}
	
	public void setSelection(int from, int to) {
		this.selectionStart = from;
		this.selectionEnd = to;
		this.redraw();
	}
	
	public boolean hasSelection() {
		return this.selectionStart != this.selectionEnd;
	}
	
	public String getSelection() {
		if (!hasSelection()) return "";
		int startIndex = Math.min(this.selectionStart, this.selectionEnd);
		int endIndex = Math.max(this.selectionStart, this.selectionEnd);
		return this.text.substring(startIndex, endIndex);
	}
	
	@Override
	public void setup() {
		super.setup();
		this.getContainer().getUserInput().addTextInputListener(this::textInput);
		this.getContainer().getUserInput().addKeyboardListener(this::keyTyped);
	}
	
	@Override
	public void cleanup() {
		super.cleanup();
		this.getContainer().getUserInput().removeTextInputListener(this::textInput);
		this.getContainer().getUserInput().removeKeyboardListener(this::keyTyped);
	}
	
	@Override
	protected void onClicked(int button, boolean pressed, boolean repeated) {
		
		if (pressed && button == KeyCodes.MOUSE_BUTTON_1) {
			
			moveCursorNear((int) this.getContainer().getCursorPosition().x - this.getAbsoluteOffset().x);
			this.selectionStart = this.selectionEnd = this.cursorPosition;
			this.redraw();
			
		}
		
	}
	
	@Override
	protected void onCursorMoveOver(Vec2i position, boolean leaved) {
		
		if (this.selectionStart != -1 && this.selectionEnd != -1 && this.getContainer().getUserInput().isMouseButtonPressed(KeyCodes.MOUSE_BUTTON_1)) {
			
			int cursorPos = (int) this.getContainer().getCursorPosition().x - this.getAbsoluteOffset().x;
			if (cursorPos > this.size.x - TEXT_BORDER_GAP * 2 - TEXT_AUTO_SCROLL_AREA) {
				setCursorPosition(this.cursorPosition + 1);
			} else if (cursorPos < TEXT_AUTO_SCROLL_AREA) {
				setCursorPosition(this.cursorPosition - 1);
			}
			moveCursorNear(cursorPos);
			this.selectionEnd = this.cursorPosition;
			this.redraw();
			
		}
		
	}
	
	protected void keyTyped(int keycode, int scancode, boolean pressed, boolean released) {
		
		if (this.isFocused() && pressed) {
			
			boolean ctrlDown = this.getContainer().getUserInput().isKeyPressed(KeyCodes.KEY_LEFT_CONTROL) || this.getContainer().getUserInput().isKeyPressed(KeyCodes.KEY_RIGHT_CONTROL);
			
			if (ctrlDown) {
				if (keycode == KeyCodes.KEY_C && hasSelection()) {
					StringSelection data = new StringSelection(getSelection());
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(data, data);
				} else if (keycode == KeyCodes.KEY_V) {
					try {
						Transferable data = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
						if (data.isDataFlavorSupported(DataFlavor.stringFlavor)) {
							String pasteText = data.getTransferData(DataFlavor.stringFlavor).toString();
							pasteText = pasteText.replaceAll("[\n,\r\t]", "");
							
							if (hasSelection()) {
								int pasted = textInput(this.selectionStart, this.selectionEnd, pasteText);
								setCursorPosition(Math.min(this.selectionStart, this.selectionEnd) + pasted);
							} else {
								int pasted = textInput(this.cursorPosition, this.cursorPosition, pasteText);
								setCursorPosition(this.cursorPosition + pasted);
							}
							clearSelection();
						}
					} catch (UnsupportedFlavorException | IOException e) {
						clearSelection();
					}
				} else if (keycode == KeyCodes.KEY_X && hasSelection()) {
					StringSelection data = new StringSelection(getSelection());
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(data, data);
					
					deleteText(this.selectionStart, this.selectionEnd - 1);
					setCursorPosition(Math.min(this.selectionStart, this.selectionEnd));
					clearSelection();
				}
				this.redraw();
			}
			
		}
		
	}
	
	protected void textInput(int codepoint, Optional<FunctionalKey> functionalKey) {
		
		if (this.isFocused()) {
			
			if (functionalKey.isPresent()) {

				boolean shiftDown = this.getContainer().getUserInput().isKeyPressed(KeyCodes.KEY_LEFT_SHIFT) || this.getContainer().getUserInput().isKeyPressed(KeyCodes.KEY_RIGHT_SHIFT);
				
				switch (functionalKey.get()) {
				
				// Move cursor
				case INSERT: this.textOverride = !this.textOverride; break;
				case POS1: setCursorPosition(0); clearSelection(); break;
				case END: setCursorPosition(this.text.length()); clearSelection(); break;
				case KEY_LEFT: 
					if (shiftDown) {
						if (this.selectionStart == -1 || this.selectionEnd == -1) {
							this.selectionStart = this.selectionEnd = this.cursorPosition;
						}
						setCursorPosition(this.cursorPosition - 1);
						this.selectionEnd = this.cursorPosition;
					} else {
						setCursorPosition(this.cursorPosition - 1);
						clearSelection();
					}
					break;
				case KEY_RIGHT:
					if (shiftDown) {
						if (this.selectionStart == -1 || this.selectionEnd == -1) {
							this.selectionStart = this.selectionEnd = this.cursorPosition;
						}
						setCursorPosition(this.cursorPosition + 1);
						this.selectionEnd = this.cursorPosition;
					} else {
						setCursorPosition(this.cursorPosition + 1);
						clearSelection();
					}
					break;
				
				// Delete characters
				case BACKSPACE: 
					if (this.hasSelection()) {
						deleteText(this.selectionStart, this.selectionEnd - 1);
						setCursorPosition(Math.min(this.selectionStart, this.selectionEnd));
					} else {
						deleteText(this.cursorPosition - 1, this.cursorPosition - 1);
						setCursorPosition(this.cursorPosition - 1);
					}
					clearSelection();
					break;
				case DEL: 
					if (this.hasSelection()) {
						deleteText(this.selectionStart, this.selectionEnd - 1);
						setCursorPosition(Math.min(this.selectionStart, this.selectionEnd));
					} else {
						deleteText(this.cursorPosition, this.cursorPosition);
					}
					clearSelection();
					break;
					
				default: break;
				}
				
			} else {
				
				if (hasSelection()) {
					int pasted = textInput(this.selectionStart, this.selectionEnd, String.valueOf((char) codepoint));
					setCursorPosition(Math.min(this.selectionStart, this.selectionEnd) + pasted);
				} else {
					int pasted = textInput(this.cursorPosition, this.cursorPosition, String.valueOf((char) codepoint));
					setCursorPosition(this.cursorPosition + pasted);
				}
				clearSelection();
				
			}
			
			this.cursorState = true;
			this.redraw();
			
		}
		
	}
	
	protected void deleteText(int from, int to) {
		int startIndex = Math.min(from, to);
		int endIndex = Math.max(from, to);
		if (startIndex >= 0 && endIndex < this.text.length()) {
			this.text = 
					this.text.substring(0, startIndex) + 
					this.text.substring(endIndex + 1);
		}
		this.changeCallback.accept(this.text);
	}
	
	protected int textInput(int from, int to, String text) {
		int startIndex = Math.min(from, to);
		int endIndex = Math.max(from, to);
		int pasteLength = Math.min(text.length(), this.maxLength - this.text.length() + (endIndex - startIndex));
		if (startIndex >= 0 && endIndex <= this.text.length() && pasteLength > 0) {
			this.text = 
					this.text.substring(0, startIndex) + 
					text.substring(0, pasteLength) + 
					this.text.substring(this.textOverride && endIndex < this.text.length() ? endIndex + 1 : endIndex);
			this.changeCallback.accept(this.text);
			return pasteLength;
		}
		return 0;
	}
	
	@Override
	public void onChangeFocus() {
		if (this.isFocused()) {
			this.cursorState = false;
			this.scheduledTick(1);
		} else {
			this.textOverride = false;
			this.cursorState = false;
			this.redraw();
		}
	}
	
	@Override
	public void scheduledTick(int arg) {
		if (arg == 1 && this.isFocused()) {
			this.cursorState = !this.cursorState;
			this.scheduleTick(400, 1);
			this.redraw();
		}
	}
	
	@Override
	public void drawBackground(SimpleBufferSource<ResourceLocation, UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {
		
		String offsetText = this.text.substring(this.textOffset);
		String renderableText = FontRenderer.limitStringWidth(offsetText, this.font, this.size.x - TEXT_BORDER_GAP * 2);
		
		TextRenderer.renderText(TEXT_BORDER_GAP, FRAME_WIDTH, renderableText, this.font, this.textColor, this.container.getActiveTextureLoader(), bufferSource, matrixStack);
		
	}
	
	@Override
	public void drawForeground(SimpleBufferSource<ResourceLocation, UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {
		
		UtilRenderer.renderFrame(this.size.x, this.size.y, FRAME_WIDTH, this.textColor, bufferSource, matrixStack);
		
		if (this.isFocused() && this.cursorState) {
			
			// Draw Cursor
			String offsetText = this.text.substring(this.textOffset).substring(0, this.cursorPosition - this.textOffset);
			int cursorPos = FontRenderer.calculateStringWidth(offsetText, this.font);
			
			int cursorWidth = this.textOverride && this.cursorPosition < this.text.length() ? FontRenderer.calculateStringWidth(String.valueOf(this.text.charAt(cursorPosition)), this.font) : TEXT_BORDER_GAP;
			int cursorHeight = FontRenderer.getFontHeight(this.font) - TEXT_BORDER_GAP;
			
			UtilRenderer.renderRectangle(cursorPos + TEXT_BORDER_GAP, TEXT_BORDER_GAP, cursorWidth, cursorHeight, this.textColor, bufferSource, matrixStack);
			
			if (this.selectionStart != this.selectionEnd) {
				
				// Draw selection
				int startIndex = Math.min(this.selectionStart, this.selectionEnd);
				int endIndex = Math.max(this.selectionStart , this.selectionEnd);
				
				int startPos = startIndex < this.textOffset ? 0 : FontRenderer.calculateStringWidth(this.text.substring(this.textOffset, startIndex), this.font);
				int endPos = endIndex < this.textOffset ? 0 : FontRenderer.calculateStringWidth(this.text.substring(this.textOffset, endIndex), this.font);
				
				if (startPos > this.size.x - 2) startPos = this.size.x - TEXT_BORDER_GAP;
				if (endPos > this.size.x - 2) endPos = this.size.x - TEXT_BORDER_GAP;
				
				UtilRenderer.renderRectangle(startPos + TEXT_BORDER_GAP, TEXT_BORDER_GAP, endPos - startPos, cursorHeight, this.textColor, bufferSource, matrixStack);
				
			}
			
		}
		
	}
	
}
