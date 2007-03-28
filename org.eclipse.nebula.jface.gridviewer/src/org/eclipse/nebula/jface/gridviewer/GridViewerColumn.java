/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    rmcamara@us.ibm.com - initial API and implementation
 *******************************************************************************/ 

package org.eclipse.nebula.jface.gridviewer;

import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * The concrete implementation of the ColumnViewer for the grid.
 *
 * @author rmcamara@us.ibm.com
 * @since 3.3
 */
public final class GridViewerColumn extends ViewerColumn 
{
    /** The concrete grid column that is being represented by the {@code ViewerColumn}.*/
    private GridColumn column;
    
    /** Editor support for handling check events. */
    private CheckEditingSupport checkEditingSupport;
    
    /** The parent grid viewer. */
    private GridViewer viewer;

    /**
     * Create a new column in the {@link GridViewer}
     * 
     * @param viewer
     *            the viewer the column belongs to
     * @param style
     *            the style used to create the column for style bits see
     *            {@link GridColumn}
     * @see GridColumn#GridColumn(Grid, int)
     */
    public GridViewerColumn(GridViewer viewer, int style) 
    {
        this(viewer, style, -1);
        this.viewer = viewer;
    }

    /**
     * Create a new column in the {@link GridViewer}
     * 
     * @param viewer
     *            the viewer the column belongs to
     * @param style
     *            the style used to create the column for style bits see
     *            {@link GridColumn}
     * @param index
     *            the index of the newly created column
     * @see GridColumn#GridColumn(Grid, int, int)
     */
    public GridViewerColumn(GridViewer viewer, int style, int index) 
    {
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
    public GridViewerColumn(GridViewer viewer, GridColumn column) 
    {
        super(viewer, column);
        this.column = column;
        this.viewer = viewer;
    }
    
    private static GridColumn createColumn(Grid table, int style, int index) 
    {
        if (index >= 0) 
        {
            return new GridColumn(table, style, index);
        }

        return new GridColumn(table, style);
    }

    /**
     * Returns the underlying column.
     * 
     * @return the underlying Nebula column
     */
    public GridColumn getColumn() 
    {
        return column;
    }
    
    /** {@inheritDoc} */
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
