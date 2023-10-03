package de.m_marvin.openui.layout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.border.Border;

import de.m_marvin.openui.components.Compound;
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
	
	@Override
	public <R extends IResourceProvider<R>> void rearange(Compound<R> compound, List<Compound<R>> childComponents) {
		
		Map<BorderSection, Compound<R>> compounds = new HashMap<>();
		for (Compound<R> c : childComponents) {
			BorderSection section = c.getLayoutData(this).section;
			if (!compounds.containsKey(section)) compounds.put(section, c);
		}
		
		int[] widths = fitSizes(compound.getSize().x, 
				totalMinAndMax(
						widthMinMax(compounds.get(BorderSection.BOTTOM_LEFT)),
						widthMinMax(compounds.get(BorderSection.LEFT)),
						widthMinMax(compounds.get(BorderSection.TOP_LEFT))
				),
				totalMinAndMax(
						widthMinMax(compounds.get(BorderSection.TOP)),
						widthMinMax(compounds.get(BorderSection.TOP))
				),
				totalMinAndMax(
						widthMinMax(compounds.get(BorderSection.BOTTOM_RIGHT)),
						widthMinMax(compounds.get(BorderSection.RIGHT)),
						widthMinMax(compounds.get(BorderSection.TOP_RIGHT))
				)
		);

		int[] heights = fitSizes(compound.getSize().y, 
				totalMinAndMax(
						heightMinMax(compounds.get(BorderSection.BOTTOM_LEFT)),
						heightMinMax(compounds.get(BorderSection.LEFT)),
						heightMinMax(compounds.get(BorderSection.TOP_LEFT))
				),
				totalMinAndMax(
						heightMinMax(compounds.get(BorderSection.TOP)),
						heightMinMax(compounds.get(BorderSection.TOP))
				),
				totalMinAndMax(
						heightMinMax(compounds.get(BorderSection.BOTTOM_RIGHT)),
						heightMinMax(compounds.get(BorderSection.RIGHT)),
						heightMinMax(compounds.get(BorderSection.TOP_RIGHT))
				)
		);
		
		if (compounds.get(BorderSection.TOP_LEFT) != null) {
			compounds.get(BorderSection.TOP_LEFT).setSize(new Vec2i(widths[0], heights[0]));
			compounds.get(BorderSection.TOP_LEFT).setOffset(new Vec2i(0, 0));
		}
		if (compounds.get(BorderSection.TOP) != null) {
			compounds.get(BorderSection.TOP).setSize(new Vec2i(widths[1], heights[0]));
			compounds.get(BorderSection.TOP).setOffset(new Vec2i(widths[0], 0));
		}
		if (compounds.get(BorderSection.TOP_RIGHT) != null) {
			compounds.get(BorderSection.TOP_RIGHT).setSize(new Vec2i(widths[2], heights[0]));
			compounds.get(BorderSection.TOP_RIGHT).setOffset(new Vec2i(widths[0] + widths[1], 0));
		}
		if (compounds.get(BorderSection.LEFT) != null) {
			compounds.get(BorderSection.LEFT).setSize(new Vec2i(widths[0], heights[1]));
			compounds.get(BorderSection.LEFT).setOffset(new Vec2i(0, heights[0]));
		}
		if (compounds.get(BorderSection.RIGHT) != null) {
			compounds.get(BorderSection.RIGHT).setSize(new Vec2i(widths[2], heights[1]));
			compounds.get(BorderSection.RIGHT).setOffset(new Vec2i(widths[0] + widths[1], heights[0]));
		}
		if (compounds.get(BorderSection.BOTTOM_LEFT) != null) {
			compounds.get(BorderSection.BOTTOM_LEFT).setSize(new Vec2i(widths[0], heights[2]));
			compounds.get(BorderSection.BOTTOM_LEFT).setOffset(new Vec2i(0, heights[0] + heights[1]));
		}
		if (compounds.get(BorderSection.BOTTOM) != null) {
			compounds.get(BorderSection.BOTTOM).setSize(new Vec2i(widths[1], heights[2]));
			compounds.get(BorderSection.BOTTOM).setOffset(new Vec2i(widths[0], heights[0] + heights[1]));
		}
		if (compounds.get(BorderSection.BOTTOM_RIGHT) != null) {
			compounds.get(BorderSection.BOTTOM_RIGHT).setSize(new Vec2i(widths[2], heights[2]));
			compounds.get(BorderSection.BOTTOM_RIGHT).setOffset(new Vec2i(widths[0] + widths[1], heights[0] + heights[1]));
		}
		 
//		int minSizeLeft = setIfHigherX(-1, compounds.get(BorderSection.LEFT));
//		minSizeLeft = setIfHigherX(minSizeLeft, compounds.get(BorderSection.TOP_LEFT));
//		minSizeLeft = setIfHigherX(minSizeLeft, compounds.get(BorderSection.BOTTOM_LEFT));
//
//		int maxSizeLeft = setIfLowerX(-1, compounds.get(BorderSection.LEFT));
//		maxSizeLeft = setIfLowerX(maxSizeLeft, compounds.get(BorderSection.TOP_LEFT));
//		maxSizeLeft = setIfLowerX(maxSizeLeft, compounds.get(BorderSection.BOTTOM_LEFT));
//
//		int minSizeRight = setIfHigherX(-1, compounds.get(BorderSection.RIGHT));
//		minSizeRight = setIfHigherX(minSizeRight, compounds.get(BorderSection.TOP_RIGHT));
//		minSizeRight = setIfHigherX(minSizeRight, compounds.get(BorderSection.BOTTOM_RIGHT));
//
//		int maxSizeRight = setIfLowerX(-1, compounds.get(BorderSection.RIGHT));
//		maxSizeRight = setIfLowerX(maxSizeRight, compounds.get(BorderSection.TOP_RIGHT));
//		maxSizeRight = setIfLowerX(maxSizeRight, compounds.get(BorderSection.BOTTOM_RIGHT));
//		
//		int minSizeTop = setIfHigherY(-1, compounds.get(BorderSection.TOP));
//		minSizeTop = setIfHigherY(minSizeTop, compounds.get(BorderSection.TOP_LEFT));
//		minSizeTop = setIfHigherY(minSizeTop, compounds.get(BorderSection.TOP_RIGHT));
//
//		int maxSizeTop = setIfLowerY(-1, compounds.get(BorderSection.TOP));
//		maxSizeTop = setIfLowerY(maxSizeTop, compounds.get(BorderSection.TOP_LEFT));
//		maxSizeTop = setIfLowerY(maxSizeTop, compounds.get(BorderSection.TOP_RIGHT));
//
//		int minSizeBottom = setIfHigherY(-1, compounds.get(BorderSection.BOTTOM));
//		minSizeBottom = setIfHigherY(minSizeBottom, compounds.get(BorderSection.BOTTOM_LEFT));
//		minSizeBottom = setIfHigherY(minSizeBottom, compounds.get(BorderSection.BOTTOM_RIGHT));
//
//		int maxSizeBottom = setIfLowerY(-1, compounds.get(BorderSection.BOTTOM));
//		maxSizeBottom = setIfLowerY(maxSizeBottom, compounds.get(BorderSection.BOTTOM_LEFT));
//		maxSizeBottom = setIfLowerY(maxSizeBottom, compounds.get(BorderSection.BOTTOM_RIGHT));
//		
//		
//		int sizeLeft = setIfHigherSizeX(0, compounds.get(BorderSection.LEFT));
//		sizeLeft = setIfHigherSizeX(sizeLeft, compounds.get(BorderSection.TOP_LEFT));
//		sizeLeft = setIfHigherSizeX(sizeLeft, compounds.get(BorderSection.BOTTOM_LEFT));
//		sizeLeft = Math.min(maxSizeLeft, Math.max(minSizeLeft, sizeLeft));
//		
//		int sizeRight = setIfHigherSizeX(0, compounds.get(BorderSection.RIGHT));
//		sizeRight = setIfHigherSizeX(sizeRight, compounds.get(BorderSection.TOP_RIGHT));
//		sizeRight = setIfHigherSizeX(sizeRight, compounds.get(BorderSection.BOTTOM_RIGHT));
//		sizeRight = Math.min(maxSizeRight, Math.max(minSizeRight, sizeRight));
//		
//		int sizeTop = setIfHigherSizeY(0, compounds.get(BorderSection.TOP));
//		sizeTop = setIfHigherSizeY(sizeTop, compounds.get(BorderSection.TOP_LEFT));
//		sizeTop = setIfHigherSizeY(sizeTop, compounds.get(BorderSection.TOP_RIGHT));
//		sizeTop = Math.min(maxSizeTop, Math.max(minSizeTop, sizeTop));
//		
//		int sizeBottom = setIfHigherSizeY(0, compounds.get(BorderSection.BOTTOM));
//		sizeBottom = setIfHigherSizeY(sizeBottom, compounds.get(BorderSection.BOTTOM_LEFT));
//		sizeBottom = setIfHigherSizeY(sizeBottom, compounds.get(BorderSection.BOTTOM_RIGHT));
//		sizeBottom = Math.min(maxSizeBottom, Math.max(minSizeBottom, sizeBottom));
//		
//		int sizeHorizontal = setIfHigherSizeX(0, compounds.get(BorderSection.TOP));
//		sizeHorizontal = setIfHigherSizeX(sizeHorizontal, compounds.get(BorderSection.BOTTOM));
//		sizeHorizontal = Math.min(compound.getSizeMax().x, Math.max(compound.getSizeMin().x, sizeLeft + sizeHorizontal + sizeRight));
//		
//		int sizeVertical = setIfHigherSizeY(0, compounds.get(BorderSection.LEFT));
//		sizeVertical = setIfHigherSizeY(sizeVertical, compounds.get(BorderSection.RIGHT));
//		sizeVertical = Math.min(compound.getSizeMax().y, Math.max(compound.getSizeMin().y, sizeTop + sizeVertical + sizeBottom));
//		
//		int sizeCenterX = sizeHorizontal - (sizeLeft + sizeRight);
//		int sizeCenterY = sizeVertical - (sizeTop + sizeBottom);
//		
// 		
//		if (compounds.containsKey(BorderSection.LEFT)) {
//			int i1 = this.cornerStretchMode == CornerStretch.VERTICAL ? (compounds.containsKey(BorderSection.TOP_LEFT) ? 0: sizeTop) : 0;
//			int i2 = this.cornerStretchMode == CornerStretch.VERTICAL ? (compounds.containsKey(BorderSection.BOTTOM_LEFT) ? 0 : sizeBottom) : 0;
//			compounds.get(BorderSection.LEFT).setSizeMargin(new Vec2i(sizeLeft, sizeCenterY + i1 + i2));
//			compounds.get(BorderSection.LEFT).setOffsetMargin(new Vec2i(0, sizeTop - i1));
//		}
//		if (compounds.containsKey(BorderSection.RIGHT)) {
//			int i1 = this.cornerStretchMode == CornerStretch.VERTICAL ? (compounds.containsKey(BorderSection.TOP_RIGHT) ? 0: sizeTop) : 0;
//			int i2 = this.cornerStretchMode == CornerStretch.VERTICAL ? (compounds.containsKey(BorderSection.BOTTOM_RIGHT) ? 0 : sizeBottom) : 0;
//			compounds.get(BorderSection.RIGHT).setSizeMargin(new Vec2i(sizeRight, sizeCenterY + i1 + i2));
//			compounds.get(BorderSection.RIGHT).setOffsetMargin(new Vec2i(sizeLeft + sizeCenterX, sizeTop - i1));
//		}
//		if (compounds.containsKey(BorderSection.TOP)) {
//			int i1 = this.cornerStretchMode == CornerStretch.HORIZONTAL ? (compounds.containsKey(BorderSection.TOP_LEFT) ? 0: sizeLeft) : 0;
//			int i2 = this.cornerStretchMode == CornerStretch.HORIZONTAL ? (compounds.containsKey(BorderSection.TOP_RIGHT) ? 0 : sizeRight) : 0;
//			compounds.get(BorderSection.TOP).setSizeMargin(new Vec2i(sizeCenterX + i1 + i2, sizeTop));
//			compounds.get(BorderSection.TOP).setOffsetMargin(new Vec2i(sizeLeft - i1, 0));
//		}
//		if (compounds.containsKey(BorderSection.BOTTOM)) {
//			int i1 = this.cornerStretchMode == CornerStretch.HORIZONTAL ? (compounds.containsKey(BorderSection.BOTTOM_LEFT) ? 0: sizeLeft) : 0;
//			int i2 = this.cornerStretchMode == CornerStretch.HORIZONTAL ? (compounds.containsKey(BorderSection.BOTTOM_RIGHT) ? 0 : sizeRight) : 0;
//			compounds.get(BorderSection.BOTTOM).setSizeMargin(new Vec2i(sizeCenterX + i1 + i2, sizeBottom));
//			compounds.get(BorderSection.BOTTOM).setOffsetMargin(new Vec2i(sizeLeft - i1, sizeTop + sizeCenterY));
//		}
//		if (compounds.containsKey(BorderSection.TOP_LEFT)) {
//			compounds.get(BorderSection.TOP_LEFT).setSizeMargin(new Vec2i(sizeLeft, sizeTop));
//			compounds.get(BorderSection.TOP_LEFT).setOffsetMargin(new Vec2i(0, 0));
//		}
//		if (compounds.containsKey(BorderSection.TOP_RIGHT)) {
//			compounds.get(BorderSection.TOP_RIGHT).setSizeMargin(new Vec2i(sizeRight, sizeTop));
//			compounds.get(BorderSection.TOP_RIGHT).setOffsetMargin(new Vec2i(sizeLeft + sizeCenterX, 0));
//		}
//		if (compounds.containsKey(BorderSection.BOTTOM_LEFT)) {
//			compounds.get(BorderSection.BOTTOM_LEFT).setSizeMargin(new Vec2i(sizeLeft, sizeBottom));
//			compounds.get(BorderSection.BOTTOM_LEFT).setOffsetMargin(new Vec2i(0, sizeTop + sizeCenterY));
//		}
//		if (compounds.containsKey(BorderSection.BOTTOM_RIGHT)) {
//			compounds.get(BorderSection.BOTTOM_RIGHT).setSizeMargin(new Vec2i(sizeRight, sizeBottom));
//			compounds.get(BorderSection.BOTTOM_RIGHT).setOffsetMargin(new Vec2i(sizeLeft + sizeCenterX, sizeTop + sizeCenterY));
//		}
//		if (compounds.containsKey(BorderSection.CENTERED)) {
//			compounds.get(BorderSection.CENTERED).setSizeMargin(new Vec2i(sizeCenterX, sizeCenterY));
//			compounds.get(BorderSection.CENTERED).setOffsetMargin(new Vec2i(sizeLeft, sizeTop));
//		}
//		
//		compound.setSize(new Vec2i(sizeHorizontal, sizeVertical));
		
	}

}
