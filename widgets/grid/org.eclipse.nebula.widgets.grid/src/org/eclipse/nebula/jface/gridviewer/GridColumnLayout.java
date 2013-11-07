/*******************************************************************************
 * Copyright (c) 2007, 2009, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	   Tom Schindl <tom.schindl@bestsolution.at> - initial API and implementation (TableColumnLayout)
 *                                               - fix for bug 178280 (TableColumnLayout)                                         
 *     IBM Corporation - API refactoring and general maintenance (TableColumnLayout)
 *     Kristine Jetzke - initial creation of file and adaption to Grid     
 *******************************************************************************/

package org.eclipse.nebula.jface.gridviewer;

import org.eclipse.jface.layout.AbstractColumnLayout;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Widget;

/**
 * The GridColumnLayout is the {@link Layout} used to maintain
 * {@link GridColumn} sizes in a {@link Grid}.
 * 
 * <p>
 * <b>You can only add the {@link Layout} to a container whose <i>only</i> child
 * is the {@link Grid} control you want the {@link Layout} applied to. Don't
 * assign the layout directly the {@link Grid}</b>
 * </p>
 * 
 * <p>
 * This class was copied from {@link TableColumnLayout} and adapted to {@link Grid}.
 * </p>
 * 
 */
public class GridColumnLayout extends AbstractColumnLayout {

	private static final boolean IS_GTK = Util.isGtk();
	
	/**
	 * {@inheritDoc}
	 */
	protected int getColumnCount(Scrollable tableTree) {
		return ((Grid) tableTree).getColumnCount();
	}

	/**
	 * {@inheritDoc}
	 */
	protected void setColumnWidths(Scrollable tableTree, int[] widths) {
		GridColumn[] columns = ((Grid) tableTree).getColumns();
		for (int i = 0; i < widths.length; i++) {
			columns[i].setWidth(widths[i]);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected ColumnLayoutData getLayoutData(Scrollable tableTree,
			int columnIndex) {
		GridColumn column = ((Grid) tableTree).getColumn(columnIndex);
		return (ColumnLayoutData) column.getData(LAYOUT_DATA);
	}

	Composite getComposite(Widget column) {
		return ((GridColumn) column).getParent().getParent();
	}


	/**
	 * {@inheritDoc}
	 */
	protected void updateColumnData(Widget column) {
		GridColumn gColumn = (GridColumn) column;
		Grid g = gColumn.getParent();

		if (!IS_GTK || g.getColumn(g.getColumnCount() - 1) != gColumn) {
			gColumn.setData(LAYOUT_DATA,
					new ColumnPixelData(gColumn.getWidth()));
			layout(g.getParent(), true);
		}
	}
}
