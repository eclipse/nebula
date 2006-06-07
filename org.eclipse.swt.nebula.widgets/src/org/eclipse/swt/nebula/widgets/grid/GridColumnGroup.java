/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    chris.gross@us.ibm.com - initial API and implementation
 *******************************************************************************/ 
package org.eclipse.swt.nebula.widgets.grid;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.nebula.widgets.grid.internal.DefaultColumnGroupHeaderRenderer;
import org.eclipse.swt.widgets.Item;

/**
 * Instances of this class represent a column group in a grid widget.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SWT.TOGGLE</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection</dd>
 * </dl>
 * 
 * @author chris.gross@us.ibm.com
 */
public class GridColumnGroup extends Item
{

    private Grid parent;

    private GridColumn[] columns = new GridColumn[] {};

    private boolean expanded = true;

    /**
     * Header renderer.
     */
    private IInternalWidget headerRenderer = new DefaultColumnGroupHeaderRenderer();

    /**
     * Constructs a new instance of this class given its parent (which must be a Table) and a style 
     * value describing its behavior and appearance. 
     * 
     * @param parent the parent table
     * @param style the style of the group
     */
    public GridColumnGroup(Grid parent, int style)
    {
        super(parent, style);
        this.parent = parent;

        headerRenderer.setDisplay(getDisplay());
        parent.newColumnGroup(this);
    }

    Grid getParent()
    {
        return parent;
    }

    int getNewColumnIndex()
    {
        if (columns.length == 0)
        {
            return -1;
        }

        GridColumn lastCol = columns[columns.length - 1];
        return parent.indexOf(lastCol) + 1;
    }

    void newColumn(GridColumn column, int index)
    {
        GridColumn[] newAllColumns = new GridColumn[columns.length + 1];
        System.arraycopy(columns, 0, newAllColumns, 0, columns.length);
        newAllColumns[newAllColumns.length - 1] = column;
        columns = newAllColumns;
    }

    /**
     * Returns the columns within this group.
     * 
     * @return the columns
     */
    public GridColumn[] getColumns()
    {
        return columns;
    }

    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        super.dispose();

        for (int i = 0; i < columns.length; i++)
        {
            columns[i].dispose();
        }

        parent.removeColumnGroup(this);
    }

    /**
     * @return the headerRenderer
     */
    public IInternalWidget getHeaderRenderer()
    {
        return headerRenderer;
    }

    /**
     * @param headerRenderer the headerRenderer to set
     */
    public void setHeaderRenderer(IInternalWidget headerRenderer)
    {
        this.headerRenderer = headerRenderer;
        headerRenderer.setDisplay(getDisplay());
    }

    /**
     * Returns true if the receiver is expanded, false otherwise.
     * 
     * @return the expanded attribute
     */
    public boolean getExpanded()
    {
        return expanded;
    }

    /**
     * Sets the expanded state of the receiver.
     * 
     * @param expanded the expanded to set
     */
    public void setExpanded(boolean expanded)
    {
        this.expanded = expanded;

        parent.refreshHoverState();
        parent.setScrollValuesObsolete();
        parent.redraw();
    }

    /**
     * Returns the first visible column in this column group.
     * 
     * @return first visible column
     */
    GridColumn getFirstVisibleColumn()
    {
        GridColumn[] cols = parent.getColumnsInOrder();
        for (int i = 0; i < cols.length; i++)
        {
            if (cols[i].getColumnGroup() == this && cols[i].isVisible())
            {
                return cols[i];
            }
        }
        return null;
    }

    /**
     * Returns the last visible column in this column group.
     * 
     * @return last visible column
     */
    GridColumn getLastVisibleColumn()
    {
        GridColumn[] cols = parent.getColumnsInOrder();
        GridColumn lastVisible = null;
        for (int i = 0; i < cols.length; i++)
        {
            if (cols[i].getColumnGroup() == this && cols[i].isVisible())
            {
                lastVisible = cols[i];
            }
        }
        return lastVisible;
    }

    Rectangle getBounds()
    {
        Rectangle bounds = new Rectangle(0, 0, 0, 0);

        bounds.height = parent.getGroupHeaderHeight();

        boolean foundFirstColumnInGroup = false;

        GridColumn[] cols = parent.getColumnsInOrder();
        for (int i = 0; i < cols.length; i++)
        {
            if (cols[i].getColumnGroup() == this && cols[i].isVisible())
            {
                if (!foundFirstColumnInGroup)
                {
                    bounds.x = parent.getOrigin(cols[i], null).x;
                    foundFirstColumnInGroup = true;
                }
                bounds.width += cols[i].getWidth();
            }
            else
            {
                if (foundFirstColumnInGroup)
                {
                    break;
                }
            }
        }

        return bounds;
    }

}
