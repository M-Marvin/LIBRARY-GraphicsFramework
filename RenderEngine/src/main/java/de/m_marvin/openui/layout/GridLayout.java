package de.m_marvin.openui.layout;

import java.util.List;
import java.util.stream.IntStream;

import de.m_marvin.openui.components.Compound;
import de.m_marvin.renderengine.resources.IResourceProvider;
import de.m_marvin.univec.impl.Vec2i;

public class GridLayout extends Layout<GridLayout.GridLayoutData> {
	
	public static class GridLayoutData extends Layout.LayoutData {
		public final int column;
		public final int row;
		public GridLayoutData(int column, int row) {
			this.column = column;
			this.row = row;
		}
		public GridLayoutData() {
			this.column = -1;
			this.row = -1;
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
	
	@SuppressWarnings("unchecked")
	@Override
	public <R extends IResourceProvider<R>> void rearange(Compound<R> compound, List<Compound<R>> childComponents) {
		
		Vec2i gridSize = countRowsAndColumns(childComponents);
		
		Compound<R>[][] grid = (Compound<R>[][]) new Object[gridSize.x][gridSize.y];
		for (int i = 0; i < grid.length; i++) grid[i] = (Compound<R>[]) new Object[gridSize.x];
		
		for (Compound<R> c : childComponents) {
			GridLayoutData data = c.getLayoutData(this);
			grid[data.column][data.row] = c;
		}
		
		if (columnsEqualSize) {
			int[] columnSizes = 
					fitSizes(
						compound.getSize().x,
						IntStream.range(0, gridSize.x)
						.mapToObj(column -> 
							totalMinAndMax(
									IntStream.range(0, gridSize.y)
									.mapToObj(row -> grid[row][column])
									.map(Layout::widthMinMax)
									.toArray(i -> new int[2][i])
							)
						)
						.toArray(i -> new int[2][i])
					);					

			for (Compound<R> c : childComponents) {
				Vec2i newSize = c.getSizeMargin();
				newSize.setX(columnSizes[c.getLayoutData(this).column]);
				c.setSizeMargin(newSize);
			}
		} else {
			for (int row = 0; row < gridSize.y; row++) {
				int rowF = row;
				
				int[] columnSizes =
						fitSizes(
							compound.getSize().x,
							IntStream.range(0, gridSize.x)
							.mapToObj(column -> grid[column][rowF])
							.map(Layout::widthMinMax)
							.toArray(i -> new int[2][i])
						);
				
				for (int column = 0; column < gridSize.x; column++) {
					Vec2i newSize = grid[row][column].getSizeMargin();
					newSize.setX(columnSizes[column]);
					grid[row][column].setSizeMargin(newSize);
				}
			}
		}
		
		if (rowsEquealSize) {
			int[] rowSizes = 
					fitSizes(
						compound.getSize().y,	
						IntStream.range(0, gridSize.y)
						.mapToObj(row -> 
							totalMinAndMax(
									IntStream.range(0, gridSize.x)
									.mapToObj(column -> grid[column][row])
									.map(Layout::heightMinMax)
									.toArray(i -> new int[2][i])
							)
						)
						.toArray(i -> new int[2][i])
					);					

			for (Compound<R> c : childComponents) {
				Vec2i newSize = c.getSizeMargin();
				newSize.setY(rowSizes[c.getLayoutData(this).row]);
				c.setSizeMargin(newSize);
			}
		} else {
			for (int column = 0; column < gridSize.y; column++) {
				int columnF = column;
				
				int[] rowSizes =
						fitSizes(
							compound.getSize().y,
							IntStream.range(0, gridSize.y)
							.mapToObj(row -> grid[row][columnF])
							.map(Layout::heightMinMax)
							.toArray(i -> new int[2][i])
						);
				
				for (int row = 0; row < gridSize.y; row++) {
					Vec2i newSize = grid[row][column].getSizeMargin();
					newSize.setX(rowSizes[row]);
					grid[row][column].setSizeMargin(newSize);
				}
			}
		}
		
	}

}
