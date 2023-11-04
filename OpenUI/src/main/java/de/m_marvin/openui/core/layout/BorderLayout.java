package de.m_marvin.openui.core.layout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.m_marvin.openui.core.components.Compound;
import de.m_marvin.renderengine.resources.IResourceProvider;
import de.m_marvin.univec.impl.Vec2i;

public class BorderLayout extends Layout<BorderLayout.BorderLayoutData> {
	
	public static enum BorderSection {
		LEFT,RIGHT,TOP,BOTTOM,
		BOTTOM_LEFT,TOP_LEFT,BOTTOM_RIGHT,TOP_RIGHT,
		CENTERED;
	}
	
	public static enum CornerStretch {
		NONE,VERTICAL,HORIZONTAL;
	}
	
	public static class BorderLayoutData extends Layout.LayoutData {
		public final BorderSection section;
		public BorderLayoutData() {
			this.section = BorderSection.CENTERED;
		}
		public BorderLayoutData(BorderSection section) {
			this.section = section;
		}
	}
	
	protected CornerStretch cornerStretchMode;
	
	public BorderLayout() {
		this(CornerStretch.NONE);
	}
	
	public BorderLayout(CornerStretch cornerStretchMode) {
		this.cornerStretchMode = cornerStretchMode;
	}
	
	@Override
	public Class<BorderLayoutData> getDataClass() {
		return BorderLayoutData.class;
	}

	protected int setIfHigherX(int minSize, Compound<?> component) {
		if (component == null) return minSize;
		return minSize < component.getSizeMinMargin().x ? component.getSizeMinMargin().x : minSize;
	}

	protected int setIfHigherSizeX(int size, Compound<?> component) {
		if (component == null) return size;
		return size < component.getSizeMargin().x ? component.getSizeMargin().x : size;
	}

	protected int setIfLowerX(int maxSize, Compound<?> component) {
		if (component == null) return maxSize;
		return maxSize > component.getSizeMaxMargin().x || maxSize == -1 ? component.getSizeMaxMargin().x : maxSize;
	}

	protected int setIfHigherY(int minSize, Compound<?> component) {
		if (component == null) return minSize;
		return minSize < component.getSizeMinMargin().y ? component.getSizeMinMargin().y : minSize;
	}

	protected int setIfHigherSizeY(int size, Compound<?> component) {
		if (component == null) return size;
		return size < component.getSizeMargin().y ? component.getSizeMargin().y : size;
	}

	protected int setIfLowerY(int maxSize, Compound<?> component) {
		if (component == null) return maxSize;
		return maxSize > component.getSizeMaxMargin().y || maxSize == -1 ? component.getSizeMaxMargin().y : maxSize;
	}
	
	protected Vec2i minSizeRequired = new Vec2i();
	protected Vec2i maxSizeRequired = new Vec2i();
	
	@Override
	public <R extends IResourceProvider<R>> void rearange(Compound<R> compound, List<Compound<R>> childComponents) {
		
		Map<BorderSection, Compound<R>> compounds = new HashMap<>();
		for (Compound<R> c : childComponents) {
			BorderSection section = c.getLayoutData(this).section;
			if (!compounds.containsKey(section)) compounds.put(section, c);
		}
		
		int[][] widthsMinMax = {
				totalMinAndMax(
						widthMinMax(compounds.get(BorderSection.BOTTOM_LEFT)),
						widthMinMax(compounds.get(BorderSection.LEFT)),
						widthMinMax(compounds.get(BorderSection.TOP_LEFT))
				),
				totalMinAndMax(
						widthMinMax(compounds.get(BorderSection.TOP)),
						widthMinMax(compounds.get(BorderSection.CENTERED)),
						widthMinMax(compounds.get(BorderSection.BOTTOM))
				),
				totalMinAndMax(
						widthMinMax(compounds.get(BorderSection.BOTTOM_RIGHT)),
						widthMinMax(compounds.get(BorderSection.RIGHT)),
						widthMinMax(compounds.get(BorderSection.TOP_RIGHT))
				)
		};
		int[] widths = fitSizes(compound.getSize().x, widthsMinMax);

		int[][] heightsMinMax = {
				totalMinAndMax(
						heightMinMax(compounds.get(BorderSection.TOP_RIGHT)),
						heightMinMax(compounds.get(BorderSection.TOP)),
						heightMinMax(compounds.get(BorderSection.TOP_LEFT))
				),
				totalMinAndMax(
						heightMinMax(compounds.get(BorderSection.LEFT)),
						heightMinMax(compounds.get(BorderSection.CENTERED)),
						heightMinMax(compounds.get(BorderSection.RIGHT))
				),
				totalMinAndMax(
						heightMinMax(compounds.get(BorderSection.BOTTOM_RIGHT)),
						heightMinMax(compounds.get(BorderSection.BOTTOM)),
						heightMinMax(compounds.get(BorderSection.BOTTOM_LEFT))
				)
		};
		int[] heights = fitSizes(compound.getSize().y, heightsMinMax);

		this.minSizeRequired = new Vec2i(minSizeRequired(widthsMinMax), minSizeRequired(heightsMinMax));
		this.maxSizeRequired = new Vec2i(maxSizeRequired(widthsMinMax), maxSizeRequired(heightsMinMax));
		
		boolean leftTop = false;
		boolean leftBottom = false;
		boolean rightTop = false;
		boolean rightBottom = false;
		
		if (compounds.get(BorderSection.TOP_LEFT) != null) {
			compounds.get(BorderSection.TOP_LEFT).setSizeMargin(new Vec2i(widths[0], heights[0]));
			compounds.get(BorderSection.TOP_LEFT).setOffsetMargin(new Vec2i(0, 0));
			leftTop = true;
		}
		if (compounds.get(BorderSection.TOP_RIGHT) != null) {
			compounds.get(BorderSection.TOP_RIGHT).setSizeMargin(new Vec2i(widths[2], heights[0]));
			compounds.get(BorderSection.TOP_RIGHT).setOffsetMargin(new Vec2i(widths[0] + widths[1], 0));
			rightTop = true;
		}
		if (compounds.get(BorderSection.BOTTOM_LEFT) != null) {
			compounds.get(BorderSection.BOTTOM_LEFT).setSizeMargin(new Vec2i(widths[0], heights[2]));
			compounds.get(BorderSection.BOTTOM_LEFT).setOffsetMargin(new Vec2i(0, heights[0] + heights[1]));
			leftBottom = true;
		}
		if (compounds.get(BorderSection.BOTTOM_RIGHT) != null) {
			compounds.get(BorderSection.BOTTOM_RIGHT).setSizeMargin(new Vec2i(widths[2], heights[2]));
			compounds.get(BorderSection.BOTTOM_RIGHT).setOffsetMargin(new Vec2i(widths[0] + widths[1], heights[0] + heights[1]));
			rightBottom = true;
		}
		if (compounds.get(BorderSection.LEFT) != null) {
			boolean stretchUp = !leftTop & cornerStretchMode == CornerStretch.VERTICAL;
			boolean stretchDown = !leftBottom & cornerStretchMode == CornerStretch.VERTICAL;
			compounds.get(BorderSection.LEFT).setSizeMargin(new Vec2i(widths[0], heights[1] + (stretchUp ? heights[0] : 0) + (stretchDown ? heights[2] : 0)));
			compounds.get(BorderSection.LEFT).setOffsetMargin(new Vec2i(0, stretchUp ? 0 : heights[0]));
		}
		if (compounds.get(BorderSection.RIGHT) != null) {
			boolean stretchUp = !rightTop & cornerStretchMode == CornerStretch.VERTICAL;
			boolean stretchDown = !rightBottom & cornerStretchMode == CornerStretch.VERTICAL;
			compounds.get(BorderSection.RIGHT).setSizeMargin(new Vec2i(widths[2], heights[1] + (stretchUp ? heights[0] : 0) + (stretchDown ? heights[2] : 0)));
			compounds.get(BorderSection.RIGHT).setOffsetMargin(new Vec2i(widths[0] + widths[1], stretchUp ? 0 : heights[0]));
		}
		if (compounds.get(BorderSection.TOP) != null) {
			boolean stretchLeft = !leftTop & cornerStretchMode == CornerStretch.HORIZONTAL;
			boolean stretchRight = !rightTop & cornerStretchMode == CornerStretch.HORIZONTAL;
			compounds.get(BorderSection.TOP).setSizeMargin(new Vec2i(widths[1] + (stretchLeft ? widths[0] : 0) + (stretchRight ? widths[2] : 0), heights[0]));
			compounds.get(BorderSection.TOP).setOffsetMargin(new Vec2i(stretchLeft ? 0 : widths[0], 0));
		}
		if (compounds.get(BorderSection.BOTTOM) != null) {
			boolean stretchLeft = !leftBottom & cornerStretchMode == CornerStretch.HORIZONTAL;
			boolean stretchRight = !rightBottom & cornerStretchMode == CornerStretch.HORIZONTAL;
			compounds.get(BorderSection.BOTTOM).setSizeMargin(new Vec2i(widths[1] + (stretchLeft ? widths[0] : 0) + (stretchRight ? widths[2] : 0), heights[2]));
			compounds.get(BorderSection.BOTTOM).setOffsetMargin(new Vec2i(stretchLeft ? 0 : widths[0], heights[0] + heights[1]));
		}
		if (compounds.get(BorderSection.CENTERED) != null) {
			compounds.get(BorderSection.CENTERED).setSizeMargin(new Vec2i(widths[1], heights[1]));
			compounds.get(BorderSection.CENTERED).setOffsetMargin(new Vec2i(widths[0], heights[0]));
		}
		
	}

	@Override
	public Vec2i getMinSizeRequired() {
		return this.minSizeRequired;
	}

	@Override
	public Vec2i getMaxSizeRequired() {
		return this.maxSizeRequired;
	}

}
