/****************************************************************************
 * Copyright (c) 2008, 2009 Jeremy Dowdall
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/

package org.eclipse.nebula.cwt.v;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;

public class VGridLayout extends VLayout {

	/**
	 * numColumns specifies the number of cell columns in the layout. If
	 * numColumns has a value less than 1, the layout will not set the size and
	 * position of any controls.
	 * 
	 * The default value is 1.
	 */
	public int numColumns = 1;

	/**
	 * makeColumnsEqualWidth specifies whether all columns in the layout will be
	 * forced to have the same width.
	 * 
	 * The default value is false.
	 */
	public boolean makeColumnsEqualWidth = false;

	/**
	 * marginWidth specifies the number of pixels of horizontal margin that will
	 * be placed along the left and right edges of the layout.
	 * 
	 * The default value is 5.
	 */
	public int marginWidth = 5;

	/**
	 * marginHeight specifies the number of pixels of vertical margin that will
	 * be placed along the top and bottom edges of the layout.
	 * 
	 * The default value is 5.
	 */
	public int marginHeight = 5;

	/**
	 * marginLeft specifies the number of pixels of horizontal margin that will
	 * be placed along the left edge of the layout.
	 * 
	 * The default value is 0.
	 * 
	 * @since 3.1
	 */
	public int marginLeft = 0;

	/**
	 * marginTop specifies the number of pixels of vertical margin that will be
	 * placed along the top edge of the layout.
	 * 
	 * The default value is 0.
	 * 
	 * @since 3.1
	 */
	public int marginTop = 0;

	/**
	 * marginRight specifies the number of pixels of horizontal margin that will
	 * be placed along the right edge of the layout.
	 * 
	 * The default value is 0.
	 * 
	 * @since 3.1
	 */
	public int marginRight = 0;

	/**
	 * marginBottom specifies the number of pixels of vertical margin that will
	 * be placed along the bottom edge of the layout.
	 * 
	 * The default value is 0.
	 * 
	 * @since 3.1
	 */
	public int marginBottom = 0;

	/**
	 * horizontalSpacing specifies the number of pixels between the right edge
	 * of one cell and the left edge of its neighboring cell to the right.
	 * 
	 * The default value is 5.
	 */
	public int horizontalSpacing = 5;

	/**
	 * verticalSpacing specifies the number of pixels between the bottom edge of
	 * one cell and the top edge of its neighboring cell underneath.
	 * 
	 * The default value is 5.
	 */
	public int verticalSpacing = 5;

	private Map<Object, Point> sizes;
	
	public VGridLayout() {
	}
	
	public VGridLayout(int numColumns, boolean makeColumnsEqualWidth) {
		this.numColumns = numColumns;
		this.makeColumnsEqualWidth = makeColumnsEqualWidth;
	}

	@Override
	protected Point computeSize(VPanel panel, int wHint, int hHint, boolean flushCache) {
		VControl[] children = getChildren(panel);

		if(flushCache || (sizes == null)) {
			loadChildSizes(children);
		}
		
		Point size = new Point(0, 0);

		int col = 0;
		int row = 0;
		int rowWidth = 0;
		int rowHeight = 0;
		
		Set<Integer> taken = new HashSet<Integer>(children.length);
		for(VControl child : children) {
			while(taken.contains((row * numColumns) + col)) {
				col++;
			}
			Point p = sizes.get(child);
			GridData data = child.getLayoutData();
			int w = (data.widthHint != SWT.DEFAULT) ? data.widthHint : (p.x + data.horizontalIndent);
			int h = (data.heightHint != SWT.DEFAULT) ? data.heightHint : (p.y + data.verticalIndent);
			rowWidth += w;
			rowHeight = Math.max(rowHeight, h);
			for(int i = 1; i < data.verticalSpan; i++) {
				taken.add(((row + i) * numColumns) + col);
			}
			col += data.horizontalSpan;
			if(col >= numColumns) {
				size.x = Math.max(size.x, rowWidth);
				size.y += rowHeight;
				col = 0;
				row++;
				rowWidth = 0;
				rowHeight = 0;
			}
		}
		if(col != 0) {
			size.x = Math.max(size.x, rowWidth);
			size.y += rowHeight;
		}

		int numRows = getNumRows(children);

		size.x += (marginLeft + (2 * marginWidth) + marginRight + (horizontalSpacing * (numColumns - 1)));
		size.y += (marginTop + (2 * marginHeight) + marginBottom + (verticalSpacing * (numRows - 1)));

		return size;
	}

	private void doLayout(VPanel parent, VControl[] children) {
		Point size = parent.getClientSize();
		int border = 0; // TODO

		if(size.x == 0 || size.y == 0) {
			return;
		}

		int[] widths = new int[numColumns];
		int[] heights = new int[getNumRows(children)];

		Arrays.fill(widths, 0);
		Arrays.fill(heights, 0);

		int col = 0;
		int row = 0;

		Set<Integer> takenSet = new HashSet<Integer>(children.length);

		for(VControl child : children) {
			while(takenSet.contains((row * numColumns) + col)) {
				col++;
			}
			Point p = sizes.get(child);
			GridData data = child.getLayoutData();
			if(widths[col] > -1) {
				if(makeColumnsEqualWidth || data.grabExcessHorizontalSpace) {
					widths[col] = -1;
				} else {
					int w = ((data.widthHint != SWT.DEFAULT) ? data.widthHint : p.x) + data.horizontalIndent;
					widths[col] = Math.max(widths[col], w);
				}
			}
			if(heights[row] > -1) {
				if(data.grabExcessVerticalSpace) {
					heights[row] = -1;
				} else {
					int h = ((data.heightHint != SWT.DEFAULT) ? data.heightHint : p.y) + data.verticalIndent;
					heights[row] = Math.max(heights[row], h);
				}
			}
			for(int i = 1; i < data.verticalSpan; i++) {
				takenSet.add(((row + i) * numColumns) + col);
			}
			col += data.horizontalSpan;
			if(col >= numColumns) {
				col = 0;
				row++;
			}
		}

		int xconsumed = marginLeft + (2 * marginWidth) + marginRight + (horizontalSpacing * (widths.length - 1));
		int yconsumed = marginTop + (2 * marginHeight) + marginBottom + (verticalSpacing * (heights.length - 1));
		int xgrabbers = 0;
		int ygrabbers = 0;

		for(int i : widths) {
			if(i == -1) {
				xgrabbers++;
			} else {
				xconsumed += i;
			}
		}
		for(int i : heights) {
			if(i == -1) {
				ygrabbers++;
			} else {
				yconsumed += i;
			}
		}

		int grabWidth = (xgrabbers > 0) ? (size.x - xconsumed) / xgrabbers : 0;
		int grabHeight = (ygrabbers > 0) ? (size.y - yconsumed) / ygrabbers : 0;

		col = 0;
		row = 0;

		int xslop = makeColumnsEqualWidth ? (int) Math.ceil((size.x - border - xconsumed - (grabWidth * xgrabbers)) / 2) + 1 : 0;

		int xoffset = (parent instanceof VPanel) ? ((VPanel) parent).getBounds().x : 0;
		int yoffset = (parent instanceof VPanel) ? ((VPanel) parent).getBounds().y : 0;
		int initX = marginLeft + marginWidth;
		int cellX = initX;
		int cellY = marginTop + marginHeight;
		int cellWidth = 0;
		int cellHeight = 0;

		int[] taken = new int[(heights.length * widths.length) + 1];
		Arrays.fill(taken, -1);

		for(VControl child : children) {
			while(taken[(row*numColumns)+col] > 0) {
				cellX += taken[(row*numColumns)+col] + horizontalSpacing;
				col++;
			}
			Point p = sizes.get(child);
			GridData data = child.getLayoutData();
			cellWidth = 0;
			for(int i = 0; i < data.horizontalSpan && (col + i) < widths.length; i++) {
				if((col + i) == 0) {
					cellWidth += xslop;
				} else if((col + i) == widths.length - 1) {
					cellWidth = size.x - border - cellX - marginWidth - marginRight - 1;
					break;
				}
				cellWidth += (widths[col + i] == -1) ? grabWidth : widths[col + i];
			}
			cellHeight = 0;
			for(int i = 0; i < data.verticalSpan && (row + i) < heights.length; i++) {
				if((row + i) == heights.length - 1) {
					cellHeight = size.y - border - cellY - marginTop - marginBottom - 1;
				} else {
					cellHeight += (heights[row + i] == -1) ? grabHeight : heights[row + i];
				}
				if(i > 0) {
					taken[((row+i)*numColumns)+col] = cellWidth;
				}
			}
			int w1 = (SWT.FILL == data.horizontalAlignment) ? (cellWidth - data.horizontalIndent) : p.x;
			int h1 = (SWT.FILL == data.verticalAlignment) ? (cellHeight - data.verticalIndent) : p.y;
			child.setBounds( 
					xoffset + getX(data, cellX, cellWidth, w1), 
					yoffset + getY(data, cellY, cellHeight, h1), 
					w1,
					h1
				);
			col += data.horizontalSpan;
			if(col >= numColumns) {
				row++;
				col = 0;
				cellX = initX;
				cellY += (cellHeight + verticalSpacing);
			} else {
				cellX += (cellWidth + horizontalSpacing);
			}
		}
	}

	private VControl[] getChildren(VPanel parent) {
		VControl[] ca = parent.getChildren();
		
		if(ca != null) {
			List<VControl> children = new ArrayList<VControl>();
			for(VControl child : ca) {
				GridData data = child.getLayoutData();
				if(data != null && !data.exclude) {
					children.add(child);
				}
			}
			return children.toArray(new VControl[children.size()]);
		} else {
			return new VControl[0];
		}
	}

	private int getNumRows(VControl[] children) {
		int c = 0;
		int r = 0;
		int xc = 0;
		
		Set<Integer> taken = new HashSet<Integer>(children.length);

		for(VControl child : children) {
			while(taken.contains((r * numColumns) + c)) {
				c++;
			}
			GridData data = child.getLayoutData();
			xc = data.horizontalSpan;
			for(int i = 1; i < data.verticalSpan; i++) {
				taken.add(((r + i) * numColumns) + c);
			}
			c += xc;
			if(c >= numColumns) {
				c = 0;
				r++;
			}
		}
		if(c != 0) {
			r++;
		}

		return r;
	}

	private int getX(GridData data, int cellStart, int cellSpan, int controlSpan) {
		switch(data.horizontalAlignment) {
		case SWT.FILL:
		case SWT.LEFT:
		case SWT.BEGINNING:
			return cellStart + data.horizontalIndent;
		case SWT.RIGHT:
		case SWT.END:
			return cellStart + cellSpan - controlSpan;
		case SWT.CENTER:
		default:
			return cellStart + ((cellSpan - controlSpan) / 2);
		}
	}

	private int getY(GridData data, int cellStart, int cellSpan, int controlSpan) {
		switch(data.verticalAlignment) {
		case SWT.FILL:
		case SWT.TOP:
		case SWT.BEGINNING:
			return cellStart + data.verticalIndent;
		case SWT.BOTTOM:
		case SWT.END:
			return cellStart + cellSpan - controlSpan;
		case SWT.CENTER:
		default:
			return cellStart + ((cellSpan - controlSpan) / 2);
		}
	}
	
	@Override
	protected void layout(VPanel panel, boolean flushCache) {
		VControl[] children = getChildren(panel);

		if(flushCache || (sizes == null)) {
			loadChildSizes(children);
		}

		if(children.length > 0) {
			doLayout(panel, children);
			panel.redraw();
		}
	}

	private void loadChildSizes(VControl[] children) {
		if(sizes != null) {
			sizes.clear();
		}
		sizes = new HashMap<Object, Point>();
		for(VControl child : children) {
			sizes.put(child, child.computeSize(-1, -1));
		}
	}
	
}