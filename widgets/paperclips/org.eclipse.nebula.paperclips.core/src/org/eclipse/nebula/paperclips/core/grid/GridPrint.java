/*
 * Copyright (c) 2005 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.core.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.nebula.paperclips.core.PrintIterator;
import org.eclipse.nebula.paperclips.core.grid.internal.GridCellImpl;
import org.eclipse.nebula.paperclips.core.grid.internal.GridIterator;
import org.eclipse.nebula.paperclips.core.internal.util.PaperClipsUtil;
import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;

/**
 * A Print which arranges child prints into a grid. A grid is initialized with a
 * series of GridColumns, and child prints are laid out into those columns by
 * invoking the add(...) methods.
 * <p>
 * GridPrint uses a column sizing algorithm based on the <a
 * href=http://www.w3.org/TR/html4/appendix/notes.html#h-B.5.2.2>W3C
 * recommendation</a> for automatic layout of tables. GridPrint deviates from
 * the recommendation on one important point: if there is less width available
 * on the print device than the calculated "minimum" size of the grid, the
 * columns will be scaled down to <em>less</em> than their calculated minimum
 * widths. Only when one of the columns goes below its "absolute minimum" will
 * the grid fail to print ( {@link PrintIterator#next(int, int)} returns null).
 * <p>
 * GridPrint offers three basic methods of specifying column size.
 * <ol>
 * <li>Default size. The column will be somewhere between it's minimum and
 * preferred width. GridPrint will determine the optimum widths for all default
 * size columns, using the modified W3C recommendation described above. This is
 * the recommended option for most cases.
 * <li>Preferred size. The column will be sized to it's preferred width. This
 * option is sometimes appropriate, for example when certain portions of text
 * should not be allowed to line-wrap. In cases where only a few cells in a
 * column need to be prevented from line wrapping, consider wrapping them in a
 * NoBreakPrint instead.
 * <li>Explicit size. The column will be the size you specify, expressed in
 * points. 72 points = 1".
 * </ol>
 * Example: GridPrint grid = new GridPrint("d, p, 72pts");
 * <p>
 * In addition, any column can be given a grow attribute. In the event a grid is
 * not as wide as the page, those columns with the grow attribute set will be
 * widened to fill the extra space.
 * <p>
 * Because GridPrint scales columns according to their minimum sizes in the
 * worst-case scenario, the absolute minimum size of a GridPrint is dependant on
 * its child prints and is not clearly defined.
 * <p>
 * If a grid has one of more columns with the grow attribute set, the grid is
 * horizontally greedy. Greedy prints take up all the available space on the
 * page.
 * 
 * @author Matthew Hall
 * @see GridColumn
 * @see PrintIterator#minimumSize()
 * @see PrintIterator#preferredSize()
 */
public final class GridPrint implements Print {
	/**
	 * Constant colspan value indicating that all remaining columns in the row
	 * should be used.
	 */
	public static final int REMAINDER = -1;

	/**
	 * Constant column size value indicating that the column should be given its
	 * preferred size. (In the context of W3C's autolayout recommendation, this
	 * has the effect of setting the columns minimum width to its preferred
	 * width. This value is used in the GridColumn constructor.
	 */
	public static final int PREFERRED = 0;

	/**
	 * Constant cell spacing value indicating that the borders of adjacent cells
	 * should overlap.
	 */
	public static final int BORDER_OVERLAP = -1;

	private GridLook look;

	/** The columns for this grid. */
	final List columns; // List<GridColumn>

	/** Array of column groups. */
	int[][] columnGroups = new int[0][];

	/**
	 * Two-dimension list of all header cells. Each element of this list
	 * represents a row in the header. Each element of a row represents a
	 * cellspan in that row.
	 */
	final List header = new ArrayList(); // List <List <GridCell>>

	/** Column cursor - the column that the next added header cell will go into. */
	private int headerCol = 0;

	/**
	 * Two-dimensional list of all body cells. Each element of this list
	 * represents a row in the body. Each element of a row represents a cellspan
	 * in that row.
	 */

	final List body = new ArrayList(); // List <List <GridCell>>

	/** Column cursor - the column that the next added print will go into. */
	private int bodyCol = 0;

	boolean cellClippingEnabled = true;

	/**
	 * Two-dimension list of all footer cells. Each element of this list
	 * represents a row in the footer. Each element of a row represents a
	 * cellspan in that row.
	 */
	// List <List <GridCell>>
	final List footer = new ArrayList();

	/** Column cursor - the column that the next added footer cell will go into. */
	private int footerCol = 0;

	/**
	 * Constructs a GridPrint with no columns and a default look.
	 */
	public GridPrint() {
		this(new GridColumn[0]);
	}

	/**
	 * Constructs a GridPrint with no columns and the given look.
	 * 
	 * @param look
	 *            the look to apply to the constructed grid.
	 */
	public GridPrint(GridLook look) {
		this(new GridColumn[0], look);
	}

	/**
	 * Constructs a GridPrint with the given columns and a default look.
	 * 
	 * @param columns
	 *            a comma-separated list of parseable column specs.
	 * @see GridColumn#parse(String)
	 */
	public GridPrint(String columns) {
		this(parseColumns(columns));
	}

	/**
	 * Constructs a GridPrint with the given columns and look.
	 * 
	 * @param columns
	 *            a comma-separated list of parseable column specs.
	 * @param look
	 *            the look to apply to the constructed grid.
	 * @see GridColumn#parse(String)
	 */
	public GridPrint(String columns, GridLook look) {
		this(parseColumns(columns), look);
	}

	/**
	 * Constructs a GridPrint with the given columns and a default look.
	 * 
	 * @param columns
	 *            the columns for the new grid.
	 */
	public GridPrint(GridColumn[] columns) {
		Util.noNulls(columns);

		this.columns = new ArrayList();
		for (int i = 0; i < columns.length; i++)
			this.columns.add(columns[i]);
		this.look = new DefaultGridLook();
	}

	/**
	 * Constructs a GridPrint with the given columns and look.
	 * 
	 * @param columns
	 *            the columns for the new grid.
	 * @param look
	 *            the look to apply to the constructed grid.
	 */
	public GridPrint(GridColumn[] columns, GridLook look) {
		this(columns);
		setLook(look);
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + bodyCol;
		result = prime * result + (cellClippingEnabled ? 1231 : 1237);
		result = prime * result + GridPrint.hashCode(columnGroups);
		result = prime * result + ((columns == null) ? 0 : columns.hashCode());
		result = prime * result + ((footer == null) ? 0 : footer.hashCode());
		result = prime * result + footerCol;
		result = prime * result + ((header == null) ? 0 : header.hashCode());
		result = prime * result + headerCol;
		result = prime * result + ((look == null) ? 0 : look.hashCode());
		return result;
	}

	private static int hashCode(int[][] array) {
		int prime = 31;
		if (array == null)
			return 0;
		int result = 1;
		for (int index = 0; index < array.length; index++) {
			result = prime * result
					+ (array[index] == null ? 0 : hashCode(array[index]));
		}
		return result;
	}

	private static int hashCode(int[] array) {
		int prime = 31;
		if (array == null)
			return 0;
		int result = 1;
		for (int index = 0; index < array.length; index++) {
			result = prime * result + array[index];
		}
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GridPrint other = (GridPrint) obj;
		if (body == null) {
			if (other.body != null)
				return false;
		} else if (!body.equals(other.body))
			return false;
		if (bodyCol != other.bodyCol)
			return false;
		if (cellClippingEnabled != other.cellClippingEnabled)
			return false;
		if (!Util.equal(columnGroups, other.columnGroups))
			return false;
		if (columns == null) {
			if (other.columns != null)
				return false;
		} else if (!columns.equals(other.columns))
			return false;
		if (footer == null) {
			if (other.footer != null)
				return false;
		} else if (!footer.equals(other.footer))
			return false;
		if (footerCol != other.footerCol)
			return false;
		if (header == null) {
			if (other.header != null)
				return false;
		} else if (!header.equals(other.header))
			return false;
		if (headerCol != other.headerCol)
			return false;
		if (look == null) {
			if (other.look != null)
				return false;
		} else if (!look.equals(other.look))
			return false;
		return true;
	}

	/**
	 * Adds the column on the right edge of the grid. Any cells which have been
	 * added to the grid prior to adding the column will be adjusted as follows:
	 * the right-hand cell of each completed row will have it's colspan expanded
	 * to fill the added column.
	 * 
	 * @param column
	 *            the column to add to the grid.
	 * @see GridColumn#parse(String)
	 */
	public void addColumn(String column) {
		addColumn(columns.size(), GridColumn.parse(column));
	}

	/**
	 * Adds the column on the right edge of the grid. Any cells which have been
	 * added to the grid prior to adding the column will be adjusted as follows:
	 * the right-hand cell of each completed row will have it's colspan expanded
	 * to fill the added column.
	 * 
	 * @param column
	 *            the column to add to the grid.
	 */
	public void addColumn(GridColumn column) {
		addColumn(columns.size(), column);
	}

	/**
	 * Inserts the column at the specified position in the grid. Any cells which
	 * have been added to the grid prior to adding the column will be adjusted
	 * as follows: on each row, the cell which overlaps or whose right edge
	 * touches the insert position will be expanded to fill the added column.
	 * 
	 * @param index
	 *            the insert position.
	 * @param column
	 *            the column to be inserted.
	 * @see GridColumn#parse(String)
	 */
	public void addColumn(int index, String column) {
		addColumn(index, GridColumn.parse(column));
	}

	/**
	 * Inserts the column at the specified position in the grid. Any cells which
	 * have been added to the grid prior to adding the column will be adjusted
	 * as follows: on each row, the cell which overlaps or whose right edge
	 * touches the insert position will be expanded to fill the added column.
	 * 
	 * @param index
	 *            the insert position.
	 * @param column
	 *            the column to be inserted.
	 */
	public void addColumn(int index, GridColumn column) {
		checkColumnInsert(index);
		Util.notNull(column);

		this.columns.add(index, column);
		adjustForColumnInsert(index, 1);
	}

	/**
	 * Adds the columns on the right edge of the grid. Any cells which have been
	 * added to the grid prior to adding the columns will be adjusted as
	 * follows: the right-hand cell of each completed row will have it's colspan
	 * expanded to fill the added columns.
	 * 
	 * @param columns
	 *            the columns to add to the grid.
	 * @see GridColumn#parse(String)
	 */
	public void addColumns(String columns) {
		addColumns(this.columns.size(), parseColumns(columns));
	}

	/**
	 * Adds the columns on the right edge of the grid. Any cells which have been
	 * added to the grid prior to adding the columns will be adjusted as
	 * follows: the right-hand cell of each completed row will have it's colspan
	 * expanded to fill the added columns.
	 * 
	 * @param columns
	 *            the columns to add to the grid.
	 */
	public void addColumns(GridColumn[] columns) {
		addColumns(this.columns.size(), columns);
	}

	/**
	 * Inserts the columns at the specified position in the grid. Any cells
	 * which have been added to the grid prior to adding the columns will be
	 * adjusted as follows: on each row, the cell which overlaps or whose right
	 * edge touches the insert position will be expanded to fill the added
	 * columns.
	 * 
	 * @param index
	 *            the insert position.
	 * @param columns
	 *            the columns to be inserted.
	 * @see GridColumn#parse(String)
	 */
	public void addColumns(int index, String columns) {
		addColumns(index, parseColumns(columns));
	}

	/**
	 * Inserts the columns at the specified position in the grid. Any cells
	 * which have been added to the grid prior to adding the columns will be
	 * adjusted as follows: on each row, the cell which overlaps or whose right
	 * edge touches the insert position will be expanded to fill the added
	 * columns.
	 * 
	 * @param index
	 *            the insert position.
	 * @param columns
	 *            the columns to be inserted.
	 * @see GridColumn#parse(String)
	 */
	public void addColumns(int index, GridColumn[] columns) {
		checkColumnInsert(index);
		Util.noNulls(columns);

		this.columns.addAll(index, Arrays.asList(columns));

		adjustForColumnInsert(index, columns.length);
	}

	private void checkColumnInsert(int index) {
		if (index < 0 || index > this.columns.size())
			PaperClips.error(SWT.ERROR_INVALID_RANGE,
					"index = " + index + ", size = " + this.columns.size()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void adjustForColumnInsert(int index, int count) {
		adjustCellsForColumnInsert(header, index, count);
		adjustCellsForColumnInsert(body, index, count);
		adjustCellsForColumnInsert(footer, index, count);

		adjustColumnGroupsForColumnInsert(index, count);

		if (bodyCol > index)
			bodyCol += count;
		if (headerCol > index)
			headerCol += count;
		if (footerCol > index)
			footerCol += count;
	}

	private void adjustCellsForColumnInsert(List rows, int index, int count) {
		for (int rowI = 0; rowI < rows.size(); rowI++) {
			List row = (List) rows.get(rowI);
			int col = 0;
			for (int cellI = 0; cellI < row.size(); cellI++) {
				GridCell cell = (GridCell) row.get(cellI);
				col += cell.getColSpan();

				// Adjust the cell which extends through the insert point, or
				// whose right side touches the insert
				// point. Except on the last row, don't adjust the final cell if
				// it only touches the insert point
				// (the user may be adding columns right before s/he adds column
				// headers).
				if ( // cell overlaps insert point, or
				(col > index) ||
				// right side touches insert point but is not the final cell.
						(col == index && (rowI + 1 < rows.size() || cellI + 1 < row
								.size()))) {
					row.set(cellI,
							new GridCellImpl(cell.getHorizontalAlignment(),
									cell.getVerticalAlignment(), cell
											.getContent(), cell.getColSpan()
											+ count));
					break;
				}
			}
		}
	}

	private void adjustColumnGroupsForColumnInsert(int index, int count) {
		for (int groupI = 0; groupI < columnGroups.length; groupI++) {
			int[] group = columnGroups[groupI];
			for (int i = 0; i < group.length; i++)
				if (group[i] >= index)
					group[i] += count;
		}
	}

	/**
	 * Separates the comma-separated argument and parses each piece to obtain an
	 * array of GridColumns.
	 * 
	 * @param columns
	 *            the comma-separated list of column specs.
	 * @return GridColumn array with the requested columns.
	 */
	private static GridColumn[] parseColumns(String columns) {
		Util.notNull(columns);
		String[] cols = columns.split("\\s*,\\s*"); //$NON-NLS-1$

		GridColumn[] result = new GridColumn[cols.length];
		for (int i = 0; i < cols.length; i++)
			result[i] = GridColumn.parse(cols[i]);

		return result;
	}

	/**
	 * Returns an array of <code>GridColumn</code>s which are the columns in the
	 * receiver.
	 * 
	 * @return an array of <code>GridColumn</code>s which are the columns in the
	 *         receiver.
	 */
	public GridColumn[] getColumns() {
		return (GridColumn[]) columns.toArray(new GridColumn[columns.size()]);
	}

	/**
	 * Adds the Print to the grid header, with default alignment and a colspan
	 * of 1.
	 * 
	 * @param cell
	 *            the print to add.
	 */
	public void addHeader(Print cell) {
		headerCol = add(header, headerCol, SWT.DEFAULT, SWT.DEFAULT, cell, 1);
	}

	/**
	 * Adds the Print to the grid header, using the given alignment.
	 * 
	 * @param hAlignment
	 *            the horizontal alignment of the print within the grid cell.
	 *            One of {@link SWT#DEFAULT} , {@link SWT#LEFT},
	 *            {@link SWT#CENTER} or {@link SWT#RIGHT}.
	 * @param cell
	 *            the print to add.
	 */
	public void addHeader(int hAlignment, Print cell) {
		headerCol = add(header, headerCol, hAlignment, SWT.DEFAULT, cell, 1);
	}

	/**
	 * Adds the Print to the grid header, using the given alignment.
	 * 
	 * @param hAlignment
	 *            the horizontal alignment of the print within the grid cell.
	 *            One of {@link SWT#DEFAULT} , {@link SWT#LEFT},
	 *            {@link SWT#CENTER} or {@link SWT#RIGHT}.
	 * @param vAlignment
	 *            the vertical alignment of the print within the grid cell. One
	 *            of {@link SWT#DEFAULT}, {@link SWT#TOP}, {@link SWT#CENTER},
	 *            {@link SWT#BOTTOM}, or {@link SWT#FILL}. A value of FILL
	 *            indicates that the cell is vertically greedy, so GridPrint
	 *            will limit the cell's height to the tallest non-FILL cell in
	 *            the row.
	 * @param cell
	 *            the print to add.
	 */
	public void addHeader(int hAlignment, int vAlignment, Print cell) {
		headerCol = add(header, headerCol, hAlignment, vAlignment, cell, 1);
	}

	/**
	 * Adds the Print to the grid header, with the given colspan and the default
	 * alignment.
	 * 
	 * @param cell
	 *            the print to add.
	 * @param colspan
	 *            the number of columns to span, or {@link GridPrint#REMAINDER}
	 *            to span the rest of the row.
	 */
	public void addHeader(Print cell, int colspan) {
		headerCol = add(header, headerCol, SWT.DEFAULT, SWT.DEFAULT, cell,
				colspan);
	}

	/**
	 * Adds the Print to the grid header, using the given colspan and alignment.
	 * 
	 * @param cell
	 *            the print to add.
	 * @param colspan
	 *            the number of columns to span, or {@link GridPrint#REMAINDER}
	 *            to span the rest of the row.
	 * @param hAlignment
	 *            the horizontal alignment of the print within the grid cell.
	 *            One of {@link SWT#DEFAULT} , {@link SWT#LEFT},
	 *            {@link SWT#CENTER} or {@link SWT#RIGHT}.
	 */
	public void addHeader(int hAlignment, Print cell, int colspan) {
		headerCol = add(header, headerCol, hAlignment, SWT.DEFAULT, cell,
				colspan);
	}

	/**
	 * Adds the Print to the grid header, using the given colspan and alignment.
	 * 
	 * @param hAlignment
	 *            the horizontal alignment of the print within the grid cell.
	 *            One of {@link SWT#DEFAULT} , {@link SWT#LEFT},
	 *            {@link SWT#CENTER} or {@link SWT#RIGHT}.
	 * @param vAlignment
	 *            the vertical alignment of the print within the grid cell. One
	 *            of {@link SWT#DEFAULT}, {@link SWT#TOP}, {@link SWT#CENTER},
	 *            {@link SWT#BOTTOM}, or {@link SWT#FILL}. A value of FILL
	 *            indicates that the cell is vertically greedy, so GridPrint
	 *            will limit the cell's height to the tallest non-FILL cell in
	 *            the row.
	 * @param cell
	 *            the print to add.
	 * @param colspan
	 *            the number of columns to span, or {@link GridPrint#REMAINDER}
	 *            to span the rest of the row.
	 */
	public void addHeader(int hAlignment, int vAlignment, Print cell,
			int colspan) {
		headerCol = add(header, headerCol, hAlignment, vAlignment, cell,
				colspan);
	}

	/**
	 * Returns an array containing the header cells in this grid. Each inner
	 * array represents one row in the header.
	 * 
	 * @return an array containing the header cells in this grid.
	 */
	public GridCell[][] getHeaderCells() {
		return getGridCellArray(header);
	}

	/**
	 * Returns an array containing the body cells in the grid. Each inner array
	 * represents one row in the body.
	 * 
	 * @return an array containing the body cells in the grid.
	 */
	public GridCell[][] getBodyCells() {
		return getGridCellArray(body);
	}

	/**
	 * Returns an array containing the footer cells in the grid. Each inner
	 * array represents one row in the footer.
	 * 
	 * @return an array containing the footer cells in the grid.
	 */
	public GridCell[][] getFooterCells() {
		return getGridCellArray(footer);
	}

	private static GridCell[][] getGridCellArray(List list) {
		GridCell[][] cells = new GridCell[list.size()][];
		for (int rowIndex = 0; rowIndex < cells.length; rowIndex++) {
			List row = (List) list.get(rowIndex);
			GridCell[] rowCells = new GridCell[row.size()];
			for (int cellIndex = 0; cellIndex < rowCells.length; cellIndex++)
				rowCells[cellIndex] = (GridCell) row.get(cellIndex);
			cells[rowIndex] = rowCells;
		}
		return cells;
	}

	/**
	 * Adds the Print to the grid body, with the default alignment and a colspan
	 * of 1.
	 * 
	 * @param cell
	 *            the print to add.
	 */
	public void add(Print cell) {
		bodyCol = add(body, bodyCol, SWT.DEFAULT, SWT.DEFAULT, cell, 1);
	}

	/**
	 * Adds the Print to the grid body, using the given colspan and alignment.
	 * 
	 * @param hAlignment
	 *            the horizontal alignment of the print within the grid cell.
	 *            One of {@link SWT#DEFAULT} , {@link SWT#LEFT},
	 *            {@link SWT#CENTER} or {@link SWT#RIGHT}.
	 * @param cell
	 *            the print to add.
	 */
	public void add(int hAlignment, Print cell) {
		bodyCol = add(body, bodyCol, hAlignment, SWT.DEFAULT, cell, 1);
	}

	/**
	 * Adds the Print to the grid body, using the given colspan and alignment.
	 * 
	 * @param hAlignment
	 *            the horizontal alignment of the print within the grid cell.
	 *            One of {@link SWT#DEFAULT} , {@link SWT#LEFT},
	 *            {@link SWT#CENTER} or {@link SWT#RIGHT}.
	 * @param vAlignment
	 *            the vertical alignment of the print within the grid cell. One
	 *            of {@link SWT#DEFAULT}, {@link SWT#TOP}, {@link SWT#CENTER},
	 *            {@link SWT#BOTTOM}, or {@link SWT#FILL}. A value of FILL
	 *            indicates that the cell is vertically greedy, so GridPrint
	 *            will limit the cell's height to the tallest non-FILL cell in
	 *            the row.
	 * @param cell
	 *            the print to add.
	 */
	public void add(int hAlignment, int vAlignment, Print cell) {
		bodyCol = add(body, bodyCol, hAlignment, vAlignment, cell, 1);
	}

	/**
	 * Adds the Print to the grid body, with the given colspan and the default
	 * alignment.
	 * 
	 * @param cell
	 *            the print to add.
	 * @param colspan
	 *            the number of columns to span, or {@link GridPrint#REMAINDER}
	 *            to span the rest of the row.
	 */
	public void add(Print cell, int colspan) {
		bodyCol = add(body, bodyCol, SWT.DEFAULT, SWT.DEFAULT, cell, colspan);
	}

	/**
	 * Adds the Print to the grid body, using the given colspan and alignment.
	 * 
	 * @param hAlignment
	 *            the horizontal alignment of the print within the grid cell.
	 *            One of {@link SWT#DEFAULT} , {@link SWT#LEFT},
	 *            {@link SWT#CENTER} or {@link SWT#RIGHT}.
	 * @param cell
	 *            the print to add.
	 * @param colspan
	 *            the number of columns to span, or {@link GridPrint#REMAINDER}
	 *            to span the rest of the row.
	 */
	public void add(int hAlignment, Print cell, int colspan) {
		bodyCol = add(body, bodyCol, hAlignment, SWT.DEFAULT, cell, colspan);
	}

	/**
	 * Adds the Print to the grid body, using the given colspan and alignment.
	 * 
	 * @param hAlignment
	 *            the horizontal alignment of the print within the grid cell.
	 *            One of {@link SWT#DEFAULT} , {@link SWT#LEFT},
	 *            {@link SWT#CENTER} or {@link SWT#RIGHT}.
	 * @param vAlignment
	 *            the vertical alignment of the print within the grid cell. One
	 *            of {@link SWT#DEFAULT}, {@link SWT#TOP}, {@link SWT#CENTER},
	 *            {@link SWT#BOTTOM}, or {@link SWT#FILL}. A value of FILL
	 *            indicates that the cell is vertically greedy, so GridPrint
	 *            will limit the cell's height to the tallest non-FILL cell in
	 *            the row.
	 * @param cell
	 *            the print to add.
	 * @param colspan
	 *            the number of columns to span, or {@link GridPrint#REMAINDER}
	 *            to span the rest of the row.
	 */
	public void add(int hAlignment, int vAlignment, Print cell, int colspan) {
		bodyCol = add(body, bodyCol, hAlignment, vAlignment, cell, colspan);
	}

	/**
	 * Returns whether individual body cells in the grid may be broken across
	 * pages. Defaults to true.
	 * 
	 * @return whether individual body cells in the grid may be broken across
	 *         pages.
	 */
	public boolean isCellClippingEnabled() {
		return cellClippingEnabled;
	}

	/**
	 * Sets whether individual body cells in the grid may be broken across
	 * pages.
	 * 
	 * @param cellClippingEnabled
	 *            whether to enabled cell clipping.
	 */
	public void setCellClippingEnabled(boolean cellClippingEnabled) {
		this.cellClippingEnabled = cellClippingEnabled;
	}

	/**
	 * Adds the Print to the grid footer, with the default alignment and a
	 * colspan of 1.
	 * 
	 * @param cell
	 *            the print to add.
	 */
	public void addFooter(Print cell) {
		footerCol = add(footer, footerCol, SWT.DEFAULT, SWT.DEFAULT, cell, 1);
	}

	/**
	 * Adds the Print to the grid footer, using the given colspan and alignment.
	 * 
	 * @param hAlignment
	 *            the horizontal alignment of the print within the grid cell.
	 *            One of {@link SWT#DEFAULT} , {@link SWT#LEFT},
	 *            {@link SWT#CENTER} or {@link SWT#RIGHT}.
	 * @param cell
	 *            the print to add.
	 */
	public void addFooter(int hAlignment, Print cell) {
		footerCol = add(footer, footerCol, hAlignment, SWT.DEFAULT, cell, 1);
	}

	/**
	 * Adds the Print to the grid footer, using the given colspan and alignment.
	 * 
	 * @param hAlignment
	 *            the horizontal alignment of the print within the grid cell.
	 *            One of {@link SWT#DEFAULT} , {@link SWT#LEFT},
	 *            {@link SWT#CENTER} or {@link SWT#RIGHT}.
	 * @param vAlignment
	 *            the vertical alignment of the print within the grid cell. One
	 *            of {@link SWT#DEFAULT}, {@link SWT#TOP}, {@link SWT#CENTER},
	 *            {@link SWT#BOTTOM}, or {@link SWT#FILL}. A value of FILL
	 *            indicates that the cell is vertically greedy, so GridPrint
	 *            will limit the cell's height to the tallest non-FILL cell in
	 *            the row.
	 * @param cell
	 *            the print to add.
	 */
	public void addFooter(int hAlignment, int vAlignment, Print cell) {
		footerCol = add(footer, footerCol, hAlignment, vAlignment, cell, 1);
	}

	/**
	 * Adds the Print to the grid footer, with the given colspan and the default
	 * alignment.
	 * 
	 * @param cell
	 *            the print to add.
	 * @param colspan
	 *            the number of columns to span, or {@link GridPrint#REMAINDER}
	 *            to span the rest of the row.
	 */
	public void addFooter(Print cell, int colspan) {
		footerCol = add(footer, footerCol, SWT.DEFAULT, SWT.DEFAULT, cell,
				colspan);
	}

	/**
	 * Adds the Print to the grid footer, using the given colspan and alignment.
	 * 
	 * @param hAlignment
	 *            the horizontal alignment of the print within the grid cell.
	 *            One of {@link SWT#DEFAULT} , {@link SWT#LEFT},
	 *            {@link SWT#CENTER} or {@link SWT#RIGHT}.
	 * @param cell
	 *            the print to add.
	 * @param colspan
	 *            the number of columns to span, or {@link GridPrint#REMAINDER}
	 *            to span the rest of the row.
	 */
	public void addFooter(int hAlignment, Print cell, int colspan) {
		footerCol = add(footer, footerCol, hAlignment, SWT.DEFAULT, cell,
				colspan);
	}

	/**
	 * Adds the Print to the grid footer, using the given colspan and alignment.
	 * 
	 * @param hAlignment
	 *            the horizontal alignment of the print within the grid cell.
	 *            One of {@link SWT#DEFAULT} , {@link SWT#LEFT},
	 *            {@link SWT#CENTER} or {@link SWT#RIGHT}.
	 * @param vAlignment
	 *            the vertical alignment of the print within the grid cell. One
	 *            of {@link SWT#DEFAULT}, {@link SWT#TOP}, {@link SWT#CENTER},
	 *            {@link SWT#BOTTOM}, or {@link SWT#FILL}. A value of FILL
	 *            indicates that the cell is vertically greedy, so GridPrint
	 *            will limit the cell's height to the tallest non-FILL cell in
	 *            the row.
	 * @param cell
	 *            the print to add.
	 * @param colspan
	 *            the number of columns to span, or {@link GridPrint#REMAINDER}
	 *            to span the rest of the row.
	 */
	public void addFooter(int hAlignment, int vAlignment, Print cell,
			int colspan) {
		footerCol = add(footer, footerCol, hAlignment, vAlignment, cell,
				colspan);
	}

	/*
	 * Returns the column number that we've advanced to, after adding the new
	 * cell.
	 */
	private int add(
			List rows, // List of List of GridCell
			int startColumn, int hAlignment, int vAlignment,
			Print cellContents, int colspan) {
		startColumn = startNewRowIfCurrentRowFull(startColumn);
		checkColumnSpan(startColumn, colspan);
		List row = getOpenRow(rows, startColumn);
		colspan = convertRemainderToExplicitColSpan(startColumn, colspan);

		GridCell cell = new GridCellImpl(hAlignment, vAlignment, cellContents,
				colspan);
		row.add(cell);
		startColumn += colspan;

		// Make sure column number is valid.
		if (startColumn > columns.size()) {
			// THIS SHOULD NOT HAPPEN--ABOVE LOGIC SHOULD PREVENT THIS CASE
			// ..but just in case.

			row.remove(row.size() - 1);
			if (row.size() == 0)
				rows.remove(row);

			PaperClips.error(SWT.ERROR_INVALID_ARGUMENT, "Colspan " + colspan //$NON-NLS-1$
					+ " too wide at column " + startColumn + " (" //$NON-NLS-1$ //$NON-NLS-2$
					+ columns.size() + " columns total)"); //$NON-NLS-1$
		}

		return startColumn;
	}

	private int convertRemainderToExplicitColSpan(int startColumn, int colspan) {
		if (colspan == REMAINDER)
			colspan = columns.size() - startColumn;
		return colspan;
	}

	private int startNewRowIfCurrentRowFull(int startColumn) {
		// If we're at the end of a row, start a new row.
		if (startColumn == columns.size())
			startColumn = 0;
		return startColumn;
	}

	private void checkColumnSpan(int startColumn, int colspan) {
		if (startColumn + colspan > columns.size())
			PaperClips.error(SWT.ERROR_INVALID_ARGUMENT, "Colspan " + colspan //$NON-NLS-1$
					+ " too wide at column " + startColumn + " (" //$NON-NLS-1$ //$NON-NLS-2$
					+ columns.size() + " columns total)"); //$NON-NLS-1$
	}

	private List getOpenRow(List rows, int startColumn) {
		List row; // the row we will add the cell to.
		if (startColumn == 0)
			// Start a new row if back at column 0.
			rows.add(row = new ArrayList(columns.size()));
		else
			// Get the incomplete row.
			row = (List) rows.get(rows.size() - 1); // List of GridCell
		return row;
	}

	/**
	 * Returns current column groups. The returned array may be modified without
	 * affecting this GridPrint.
	 * 
	 * @return the column groups.
	 */
	public int[][] getColumnGroups() {
		return PaperClipsUtil.copy(columnGroups);
	}

	/**
	 * Sets the column groups to the given two-dimension array. Each int[] array
	 * is a group. Columns in a group will be the same size when laid out on the
	 * print device.
	 * <p>
	 * The following statement causes columns 0 and 2 to be the same size, and
	 * columns 1 and 3 to be the same size.
	 * 
	 * <pre>
	 * grid.setColumnGroups(new int[][] { { 0, 2 }, { 1, 3 } });
	 * </pre>
	 * 
	 * <p>
	 * The behavior of this property is undefined when a column belongs to more
	 * than one group.
	 * <p>
	 * <b>Note:</b> Column grouping is enforced <i>before</i> column weights.
	 * Therefore, columns in the same group should be given the same weight to
	 * ensure they are laid out at the same width.
	 * 
	 * @param columnGroups
	 *            the new column groups.
	 */
	public void setColumnGroups(int[][] columnGroups) {
		checkColumnGroups(columnGroups);
		this.columnGroups = PaperClipsUtil.copy(columnGroups);
	}

	private void checkColumnGroups(int[][] columnGroups) {
		Util.notNull(columnGroups);
		for (int groupIndex = 0; groupIndex < columnGroups.length; groupIndex++)
			checkColumnGroup(columnGroups[groupIndex]);
	}

	private void checkColumnGroup(int[] columnGroup) {
		Util.notNull(columnGroup);
		for (int columnInGroupIndex = 0; columnInGroupIndex < columnGroup.length; columnInGroupIndex++)
			checkColumnIndex(columnGroup[columnInGroupIndex]);
	}

	private void checkColumnIndex(int columnIndex) {
		if (columnIndex < 0 || columnIndex >= columns.size())
			PaperClips.error(SWT.ERROR_INVALID_RANGE,
					"Column index in column group must be " + "0 <= " //$NON-NLS-1$ //$NON-NLS-2$ 
							+ columnIndex + " < " + columns.size()); //$NON-NLS-1$
	}

	/**
	 * Returns the grid's look. A GridLook determines what decorations will
	 * appear around the grid's contents. Default is a DefaultGridLook with no
	 * cell spacing, no cell borders, and no background colors.
	 * 
	 * @return the look of this grid.
	 */
	public GridLook getLook() {
		return look;
	}

	/**
	 * Sets the grid's look.
	 * 
	 * @param look
	 *            the new look.
	 */
	public void setLook(GridLook look) {
		Util.notNull(look);
		this.look = look;
	}

	public PrintIterator iterator(Device device, GC gc) {
		return new GridIterator(this, device, gc);
	}
}
