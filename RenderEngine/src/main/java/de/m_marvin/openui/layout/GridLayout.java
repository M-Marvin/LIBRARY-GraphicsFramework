package de.m_marvin.openui.layout;

import java.util.List;

import de.m_marvin.openui.components.Compound;
import de.m_marvin.renderengine.resources.IResourceProvider;

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
	
	protected boolean columndsEqualSize;
	protected boolean rowsEquealSize;
	
	public GridLayout() {
		this(false, false);
	}
	
	public GridLayout(boolean columndsEqualSize, boolean rowsEquealSize) {
		this.columndsEqualSize = columndsEqualSize;
		this.rowsEquealSize = rowsEquealSize;
	}

	@Override
	public Class<GridLayoutData> getDataClass() {
		return GridLayoutData.class;
	}
	
	@Override
	public <R extends IResourceProvider<R>> void rearange(Compound<R> compound, List<Compound<R>> childComponents) {
		
		
		
	}

}
