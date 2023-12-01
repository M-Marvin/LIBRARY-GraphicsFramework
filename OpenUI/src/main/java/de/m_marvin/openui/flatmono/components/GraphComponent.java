package de.m_marvin.openui.flatmono.components;

import java.awt.Color;
import java.awt.Font;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.m_marvin.openui.core.UIRenderMode;
import de.m_marvin.openui.core.components.Component;
import de.m_marvin.openui.flatmono.TextRenderer;
import de.m_marvin.openui.flatmono.UIRenderModes;
import de.m_marvin.openui.flatmono.UtilRenderer;
import de.m_marvin.renderengine.buffers.BufferBuilder;
import de.m_marvin.renderengine.buffers.defimpl.SimpleBufferSource;
import de.m_marvin.renderengine.fontrendering.FontRenderer;
import de.m_marvin.renderengine.resources.defimpl.ResourceLocation;
import de.m_marvin.renderengine.translation.PoseStack;
import de.m_marvin.univec.impl.Vec2d;
import de.m_marvin.univec.impl.Vec2f;
import de.m_marvin.univec.impl.Vec2i;

public class GraphComponent extends Component<ResourceLocation> {
	
	protected Color color;
	protected Color textColor;
	protected Font font = DEFAULT_FONT;
	protected int scalaNumberScaleX = 10;
	protected int scalaNumberScaleY = 10;
	protected int minValueX;
	protected int maxValueX;
	protected int minValueY;
	protected int maxValueY;
	protected boolean showDigits = true;
	protected Set<Graph> graphs = new HashSet<>();
	
	public GraphComponent(int minX, int maxX, int minY, int maxY) {
		this(minX, maxX, minY, maxY, Color.WHITE);
	}
	
	public GraphComponent(int minX, int maxX, int minY, int maxY, Color color) {
		this(minX, maxX, minY, maxY, color, Color.WHITE);
	}
	
	public GraphComponent(int minX, int maxX, int minY, int maxY, Color color, Color textColor) {
		this.minValueX = minX;
		this.maxValueX = maxX;
		this.minValueY = minY;
		this.maxValueY = maxY;
		this.color = color;
		this.textColor = textColor;
		
		this.setMargin(5, 5, 5, 5);
		this.setSize(new Vec2i(200, 200));
		this.fixSize();
	}
	
	public void setShowDigits(boolean showDigits) {
		this.showDigits = showDigits;
		this.redraw();
	}
	
	public boolean getShowDigits() {
		return showDigits;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
		this.redraw();
	}
	
	public Color getTextColor() {
		return textColor;
	}
	
	public void setTextColor(Color textColor) {
		this.textColor = textColor;
		this.redraw();
	}
	
	public Font getFont() {
		return font;
	}
	
	public void setFont(Font font) {
		this.font = font;
		this.redraw();
	}
	
	public void setValueLimits(int minX, int maxX, int minY, int maxY) {
		this.minValueX = minX;
		this.maxValueX = maxX;
		this.minValueY = minY;
		this.maxValueY = maxY;
		this.redraw();
	}
	
	public void stepValueLimit(int stepX, int stepY) {
		this.minValueX += stepX;
		this.maxValueX += stepX;
		this.minValueY += stepY;
		this.maxValueY += stepY;
		this.redraw();
	}
	
	public int getMinValueX() {
		return minValueX;
	}
	
	public int getMinValueY() {
		return minValueY;
	}
	
	public int getMaxValueX() {
		return maxValueX;
	}
	
	public int getMaxValueY() {
		return maxValueY;
	}
	
	public int getScalaNumberScaleX() {
		return scalaNumberScaleX;
	}
	
	public void setScalaNumberScaleX(int scalaNumberScale) {
		this.scalaNumberScaleX = scalaNumberScale;
		this.redraw();
	}

	public int getScalaNumberScaleY() {
		return scalaNumberScaleY;
	}
	
	public void setScalaNumberScaleY(int scalaNumberScale) {
		this.scalaNumberScaleY = scalaNumberScale;
		this.redraw();
	}
	
	public Set<Graph> getGraphs() {
		return graphs;
	}
	
	public void addGraph(Graph graph) {
		this.graphs.add(graph);
		this.redraw();
	}
	
	public void removeGraph(Graph graph) {
		this.graphs.remove(graph);
		this.redraw();
	}
	
	@Override
	public void drawBackground(SimpleBufferSource<ResourceLocation, UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {
		
		int gw = this.size.x / (this.maxValueX - this.minValueX);
		int gh = this.size.y / (this.maxValueY - this.minValueY);
		Color rc = new Color(this.color.getRed(), this.color.getBlue(), this.color.getGreen(), 128);
		
		for (int ix = this.minValueX; ix < this.maxValueX; ix++) {
			
			if (ix % this.scalaNumberScaleX != 0) continue;
			
			UtilRenderer.renderRectangle((ix - this.minValueX) * gw, 0, ix == 0 ? 2 : 1, this.size.y, ix == 0 ? this.color : rc, bufferSource, matrixStack);
			
		}
		for (int iy = this.minValueY; iy < this.maxValueY; iy++) {

			if (iy % this.scalaNumberScaleY != 0) continue;
			
			UtilRenderer.renderRectangle(0, this.size.y - (iy - this.minValueY) * gh, this.size.x, iy == 0 ? 2 : 1, iy == 0 ? this.color : rc, bufferSource, matrixStack);
			
		}
		
	}
	
	@Override
	public void drawForeground(SimpleBufferSource<ResourceLocation, UIRenderMode<ResourceLocation>> bufferSource, PoseStack matrixStack) {
		
		BufferBuilder vertexBuffer = bufferSource.startBuffer(UIRenderModes.lines(2));

		Vec2i mult = new Vec2i(this.size.x / (this.maxValueX - this.minValueX), -this.size.y / (this.maxValueY - this.minValueY));
		
		for (Graph graph : this.graphs) {
			
			List<Vec2d> points = graph.dataPoints.stream().toList();
			
			float r = graph.color.getRed() / 255F;
			float g = graph.color.getGreen() / 255F;
			float b = graph.color.getBlue() / 255F;
			float a = graph.color.getAlpha() / 255F;
			
			for (int i = 1; i < points.size(); i++) {
				Vec2f p1 = new Vec2f(points.get(i - 1)).sub(new Vec2f(this.minValueX, this.minValueY)).mul(mult).add(0.0F, (float) this.size.y).clamp(new Vec2f(0, 0), this.size);
				Vec2f p2 = new Vec2f(points.get(i)).sub(new Vec2f(this.minValueX, this.minValueY)).mul(mult).add(0.0F, (float) this.size.y).clamp(new Vec2f(0, 0), this.size);
				
				vertexBuffer.vertex(matrixStack, p1.x, p1.y, 0).color(r, g, b, a).endVertex();
				vertexBuffer.vertex(matrixStack, p2.x, p2.y, 0).color(r, g, b, a).endVertex();
			}
			
		}
		
		vertexBuffer.end();

		this.shiftRenderLayer();

		UtilRenderer.renderFrame(this.size.x, this.size.y, 1, this.color, bufferSource, matrixStack);
		
		if (this.showDigits) {
			int gw = this.size.x / (this.maxValueX - this.minValueX);
			int gh = this.size.y / (this.maxValueY - this.minValueY);
			int th = FontRenderer.getFontHeight(this.font);
			
			for (int ix = this.minValueX; ix < this.maxValueX; ix++) {
				
				if (ix % this.scalaNumberScaleX != 0) continue;
				
				if (ix > this.minValueX) {
					String text = String.valueOf(ix);
					int tw = FontRenderer.calculateStringWidth(text, this.font);
					TextRenderer.renderTextCentered((ix - this.minValueX) * gw - tw / 2, this.size.y - th / 2 - 5, text, this.font, this.color, this.container.getActiveTextureLoader(), bufferSource, matrixStack);
				}
				
			}
			for (int iy = this.minValueY; iy < this.maxValueY; iy++) {

				if (iy % this.scalaNumberScaleY != 0) continue;
				
				if (iy > this.minValueY) {
					String text = String.valueOf(iy);
					int tw = FontRenderer.calculateStringWidth(text, this.font);
					TextRenderer.renderTextCentered(tw / 2 + 5, this.size.y - (iy - this.minValueY) * gh - th / 2, text, this.font, this.color, this.container.getActiveTextureLoader(), bufferSource, matrixStack);
				}
				
			}
			
			shiftRenderLayer();
		}
		
		for (Graph graph : this.graphs) {
			
			Vec2d end = graph.getDataPoints().getLast();
			if (end == null) continue;
			
			Vec2i pos = new Vec2i(end).sub(new Vec2i(this.minValueX, this.minValueY)).mul(mult).add(0, this.size.y).clamp(new Vec2i(0, 0), this.size);
			int xo = FontRenderer.calculateStringWidth(graph.title, this.font) / 2 + 5;
			TextRenderer.renderTextCentered((int) pos.x - xo, (int) pos.y, graph.title, this.font, graph.color, this.getContainer().getActiveTextureLoader(), bufferSource, matrixStack);			
			
		}
		
	}
	
	public static class Graph {
		
		protected Color color;
		protected Deque<Vec2d> dataPoints = new LinkedList<>();
		protected String title;
		
		public Graph(String title, Color color) {
			this.color = color;
			this.title = title;
		}
		
		public String getTitle() {
			return title;
		}
		
		public void setTitle(String title) {
			this.title = title;
		}
		
		public Color getColor() {
			return color;
		}
		
		public void setColor(Color color) {
			this.color = color;
		}
		
		public Deque<Vec2d> getDataPoints() {
			return dataPoints;
		}
		
	}
	
	
}
