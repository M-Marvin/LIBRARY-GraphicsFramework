package de.m_marvin.openui.layout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	
	@Override
	public <R extends IResourceProvider<R>> void rearange(Compound<R> compound, List<Compound<R>> childComponents) {
		
		Vec2i gridSize = countRowsAndColumns(childComponents);
		
		int[] rowWidthsMin = null;
		int[] rowWidthsMax = null;
		int[] columnHeightsMin = null;
		int[] columnHeightsMax = null;
		
		if (rowsEquealSize) {
			rowWidthsMin = new int[gridSize.x];
			rowWidthsMax = new int[gridSize.x];
			
			for (int i = 0; i < gridSize.x; i++) {
				for (Compound<R> c : childComponents) {
					if (c.getLayoutData(this).row == i) {
						if (c.getSizeMin().x > rowWidthsMin[i]) rowWidthsMin[i] = c.getSizeMin().x;
						if (c.getSizeMax().x < rowWidthsMax[i] || rowWidthsMax[i] == 0) rowWidthsMax[i] = c.getSizeMax().x;
					}
				}
			}
		}
		
		if (columnsEqualSize) {
			columnHeightsMin = new int[gridSize.y];
			columnHeightsMax = new int[gridSize.y];
			
			for (int i = 0; i < gridSize.y; i++) {
				for (Compound<R> c : childComponents) {
					if (c.getLayoutData(this).column == i) {
						if (c.getSizeMin().y > columnHeightsMin[i]) columnHeightsMin[i] = c.getSizeMin().y;
						if (c.getSizeMax().y < columnHeightsMax[i] || columnHeightsMax[i] == 0) columnHeightsMax[i] = c.getSizeMax().y;
					}
				}
			}
		}
		
		for (int ix = 0; ix < gridSize.x; ix++) {
			
			
			
		}
		
		System.out.println(columnHeightsMax.length + " " + rowWidthsMax.length);
		
	}

}
