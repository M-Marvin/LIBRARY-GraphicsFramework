package de.m_marvin.openui.core.layout;

import java.util.List;
import java.util.stream.IntStream;

import de.m_marvin.openui.core.components.Compound;
import de.m_marvin.renderengine.resources.IResourceProvider;
import de.m_marvin.univec.impl.Vec2i;

public class GridLayout extends Layout<GridLayout.GridLayoutData> {
	
	public static class GridLayoutData extends Layout.LayoutData {
		public final int column;
		public final int row;
		public GridLayoutData(int column, int row) {
			assert column > 0 && row > 0 : "Row and column index negative!";
			this.column = column;
			this.row = row;
		}
		public GridLayoutData() {
			this.column = 0;
			this.row = 0;
		}
	}
	
	protected boolean columnsEqualSize;
	protected boolean rowsEquealSize;
	
	public GridLayout() {
		this(false, false);
	}
	
	public GridLayout(boolean columnsEqualSize, boolean rowsEquealSize) {
		assert columnsEqualSize || rowsEquealSize : "One of rows or columns must have equeal size!";
		
		this.columnsEqualSize = columnsEqualSize;
		this.rowsEquealSize = rowsEquealSize;
	}

	@Override
	public Class<GridLayoutData> getDataClass() {
		return GridLayoutData.class;
	}
	
	public <R extends IResourceProvider<R>> Vec2i countRowsAndColumns(List<Compound<R>> components) {
		int row = -1, column = -1;
		for (Compound<R> c : components) {
			GridLayoutData data = c.getLayoutData(this);
			if (data == null) continue;
			if (data.row > row) row = data.row;
			if (data.column > column) column = data.column;
		}
		return new Vec2i(row + 1, column + 1);
	}
	
	protected Vec2i minSizeRquired = new Vec2i();
	protected Vec2i maxSizeRquired = new Vec2i();
	
	@Override
	public <R extends IResourceProvider<R>> void rearange(Compound<R> compound, List<Compound<R>> childComponents) {
		
		Vec2i gridSize = new Vec2i(
				childComponents.stream().mapToInt(c -> c.getLayoutData(this).column	).max().getAsInt() + 1,
				childComponents.stream().mapToInt(c -> c.getLayoutData(this).row	).max().getAsInt() + 1
		);
		
		int[][] widthsMinMax = IntStream
			.range(0, gridSize.x)
			.mapToObj(column ->
				totalMinAndMax(
					childComponents
					.stream()
					.filter(c -> c.getLayoutData(this).column == column)
					.map(Layout::widthMinMax)
					.toArray(i -> new int[i][i])
				)
			)
			.toArray(i -> new int[i][i]);
		int[] widths = fitSizes(compound.getSize().x, widthsMinMax);
		
		int[][] heightsMinMax = IntStream
				.range(0, gridSize.y)
				.mapToObj(row ->
					totalMinAndMax(
						childComponents
						.stream()
						.filter(c -> c.getLayoutData(this).row == row)
						.map(Layout::heightMinMax)
						.toArray(i -> new int[i][i])
					)
				)
				.toArray(i -> new int[i][i]);
		int[] heights = fitSizes(compound.getSize().y, heightsMinMax);
		
		this.minSizeRquired = new Vec2i(minSizeRequired(widthsMinMax), minSizeRequired(heightsMinMax));
		this.maxSizeRquired = new Vec2i(maxSizeRequired(widthsMinMax), maxSizeRequired(heightsMinMax));
		
		for (Compound<R> component : childComponents) {
			GridLayoutData data = component.getLayoutData(this);
			int x = 0;
			for (int i = 0; i < data.column; i++) x += widths[i];
			int y = 0;
			for (int i = 0; i < data.row; i++) y += heights[i];
			component.setOffsetMargin(new Vec2i(x, y));
			component.setSizeMargin(new Vec2i(widths[data.column], heights[data.row]));
		}
		
	}

	@Override
	public Vec2i getMinSizeRequired() {
		return this.minSizeRquired;
	}
	
	@Override
	public Vec2i getMaxSizeRequired() {
		return this.maxSizeRquired;
	}

}
