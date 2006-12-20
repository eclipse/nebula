/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.nebula.nebface.viewers;

import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.nebula.widgets.grid.Grid;
import org.eclipse.swt.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.nebula.widgets.grid.GridItem;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * This is the JFace' implementation of SWT's {@link TableColumn} like
 * {@link TableViewer} is JFace' implementation of {@link Table}
 * 
 * @since 3.3
 */
public final class GridViewerColumn extends ViewerColumn {

    private GridColumn column;
    private CheckEditingSupport checkEditingSupport;
    private GridViewer viewer;

    /**
     * Create a new column in the {@link TableViewer}
     * 
     * @param viewer
     *            the viewer the column belongs to
     * @param style
     *            the style used to create the column for style bits see
     *            {@link TableColumn}
     * @see TableColumn#TableColumn(Table, int)
     */
    public GridViewerColumn(GridViewer viewer, int style) {
        this(viewer, style, -1);
        this.viewer = viewer;
    }

    /**
     * Create a new column in the {@link TableViewer}
     * 
     * @param viewer
     *            the viewer the column belongs to
     * @param style
     *            the style used to create the column for style bits see
     *            {@link TableColumn}
     * @param index
     *            the index of the newly created column
     * @see TableColumn#TableColumn(Table, int, int)
     */
    public GridViewerColumn(GridViewer viewer, int style, int index) {
        this(viewer, createColumn((Grid) viewer.getControl(), style, index));
        this.viewer = viewer;
    }

    /**
     * 
     * @param viewer
     *            the viewer the column belongs to
     * @param column
     *            the column the viewer is attached to
     */
    public GridViewerColumn(GridViewer viewer, GridColumn column) {
        super(viewer, column);
        this.column = column;
        this.viewer = viewer;
    }
    
    private static GridColumn createColumn(Grid grid, int style, int index) {
        if (index >= 0) {
            return new GridColumn(grid, style, index);
        }

        return new GridColumn(grid, style);
    }

    /**
     * @return the underlying SWT column
     */
    public GridColumn getColumn() {
        return column;
    }
    
    public void setEditingSupport(EditingSupport editingSupport)
    {
        if (editingSupport instanceof CheckEditingSupport)
        {
            if (checkEditingSupport == null)
            {
                final int colIndex = getColumn().getParent().indexOf(getColumn());
                
                getColumn().getParent().addListener(SWT.Selection, new Listener()
                {                
                    public void handleEvent(Event event)
                    {                         
                        if (event.detail == SWT.CHECK && event.index == colIndex)
                        {
                            GridItem item = (GridItem)event.item;
                            Object element = viewer.getElementAt(viewer.getGrid().indexOf(item));
                            checkEditingSupport.setValue(element, new Boolean(item.getChecked(colIndex)));
                        }
                    }                
                });
            }
            checkEditingSupport = (CheckEditingSupport)editingSupport;
        }
        else
        {
            super.setEditingSupport(editingSupport);
        }        
    }
}
