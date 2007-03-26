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
package org.eclipse.nebula.widgets.grid;

import org.eclipse.nebula.widgets.grid.internal.DefaultCellRenderer;
import org.eclipse.nebula.widgets.grid.internal.DefaultColumnHeaderRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TypedListener;

/**
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p> 
 * Instances of this class represent a column in a grid widget.
 * <p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SWT.LEFT, SWT.RIGHT, SWT.CENTER, SWT.CHECK</dd>
 * <dt><b>Events:</b></dt>
 * <dd> Move, Resize, Selection, Show, Hide</dd>
 * </dl>
 * 
 * @author chris.gross@us.ibm.com
 */
public class GridColumn extends Item
{

    /**
     * Default width of the column.
     */
    private static final int DEFAULT_WIDTH = 10;

    /**
     * Parent table.
     */
    private Grid parent;

    /**
     * Header renderer.
     */
    private GridHeaderRenderer headerRenderer = new DefaultColumnHeaderRenderer();

    /**
     * Cell renderer.
     */
    private GridCellRenderer cellRenderer = new DefaultCellRenderer();

    /**
     * Width of column.
     */
    private int width = DEFAULT_WIDTH;

    /**
     * Sort style of column. Only used to draw indicator, does not actually sort
     * data.
     */
    private int sortStyle = SWT.NONE;

    /**
     * Determines if this column shows toggles.
     */
    private boolean tree = false;

    /**
     * Does this column contain check boxes?  Did the user specify SWT.CHECK in the constructor
     * of the column.
     */
    private boolean check = false;
    
    /**
     * Specifies if this column should display a checkbox because SWT.CHECK was passed to the parent
     * table (not necessarily the column).
     */
    private boolean tableCheck = false;

    /**
     * Is this column resizable?
     */
    private boolean resizeable = true;

    /**
     * Is this column moveable?
     */
    private boolean moveable = false;

    /**
     * Is a summary column in a column group. Not applicable if this column is
     * not in a group.
     */
    private boolean summary = true;

    /**
     * Is a detail column in a column group. Not applicable if this column is
     * not in a group.
     */
    private boolean detail = true;

    private boolean visible = true;
    
    private boolean cellSelectionEnabled = true;

    private GridColumnGroup group;
    
    private boolean checkable = true;
    
    private boolean wordWrap = false;

    /**
     * Constructs a new instance of this class given its parent (which must be a
     * <code>Grid</code>) and a style value describing its behavior and
     * appearance. The item is added to the end of the items maintained by its
     * parent.
     * 
     * @param parent an Grid control which will be the parent of the new
     * instance (cannot be null)
     * @param style the style of control to construct
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the parent</li>
     * <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
     * </ul>
     */
    public GridColumn(Grid parent, int style)
    {
        this(parent, style, -1);
    }

    /**
     * Constructs a new instance of this class given its parent (which must be a
     * <code>Grid</code>), a style value describing its behavior and
     * appearance, and the index at which to place it in the items maintained by
     * its parent.
     * 
     * @param parent an Grid control which will be the parent of the new
     * instance (cannot be null)
     * @param style the style of control to construct
     * @param index the index to store the receiver in its parent
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the parent</li>
     * <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
     * </ul>
     */
    public GridColumn(Grid parent, int style, int index)
    {
        super(parent, style, index);

        init(parent, style, index);
    }

    /**
     * Constructs a new instance of this class given its parent column group (which must be a
     * <code>GridColumnGroup</code>), a style value describing its behavior and
     * appearance, and the index at which to place it in the items maintained by
     * its parent.
     * 
     * @param parent an Grid control which will be the parent of the new
     * instance (cannot be null)
     * @param style the style of control to construct
     * @param index the index to store the receiver in its parent
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the parent</li>
     * <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
     * </ul>
     */
    public GridColumn(GridColumnGroup parent, int style)
    {
        super(parent.getParent(), style, parent.getNewColumnIndex());

        init(parent.getParent(), style, parent.getNewColumnIndex());

        group = parent;

        group.newColumn(this, -1);
    }

    private void init(Grid table, int style, int index)
    {
        this.parent = table;

        table.newColumn(this, index);

        if ((style & SWT.CHECK) == SWT.CHECK)
        {
            check = true;
        }

        initHeaderRenderer();
        initCellRenderer();
    }

    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        parent.removeColumn(this);
        if (group != null)
            group.removeColumn(this);
        super.dispose();
    }

    /**
     * Initialize header renderer.
     */
    private void initHeaderRenderer()
    {
        headerRenderer.setDisplay(getDisplay());
    }

    /**
     * Initialize cell renderer.
     */
    private void initCellRenderer()
    {
        cellRenderer.setDisplay(getDisplay());

        cellRenderer.setCheck(check);
        cellRenderer.setTree(tree);
        cellRenderer.setColumn(parent.indexOf(this));

        if ((getStyle() & SWT.RIGHT) == SWT.RIGHT)
        {
            cellRenderer.setAlignment(SWT.RIGHT);
        }

        if ((getStyle() & SWT.CENTER) == SWT.CENTER)
        {
            cellRenderer.setAlignment(SWT.CENTER);
        }

    }

    /**
     * Returns the header renderer.
     * 
     * @return header renderer
     */
    GridHeaderRenderer getHeaderRenderer()
    {
        return headerRenderer;
    }

    /**
     * Returns the cell renderer.
     * 
     * @return cell renderer.
     */
    GridCellRenderer getCellRenderer()
    {
        return cellRenderer;
    }

    /**
     * Returns the width of the column.
     * 
     * @return width of column
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public int getWidth()
    {
        checkWidget();
        return width;
    }

    /**
     * Sets the width of the column.
     * 
     * @param width new width
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setWidth(int width)
    {
        checkWidget();
        setWidth(width,true);
    }
    
    void setWidth(int width, boolean redraw)
    {
        this.width = width;
        if (redraw)
        {
            parent.setScrollValuesObsolete();
            parent.redraw();
        }        
    }

    /**
     * Sets the sort indicator style for the column. This method does not actual
     * sort the data in the table. Valid values include: SWT.UP, SWT.DOWN,
     * SWT.NONE.
     * 
     * @param style SWT.UP, SWT.DOWN, SWT.NONE
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setSort(int style)
    {
        checkWidget();
        sortStyle = style;
        parent.redraw();
    }

    /**
     * Returns the sort indicator value.
     * 
     * @return SWT.UP, SWT.DOWN, SWT.NONE
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public int getSort()
    {
        checkWidget();
        return sortStyle;
    }

    /**
     * Adds the listener to the collection of listeners who will be notified
     * when the receiver's is pushed, by sending it one of the messages defined
     * in the <code>SelectionListener</code> interface.
     * 
     * @param listener the listener which should be notified
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void addSelectionListener(SelectionListener listener)
    {
        checkWidget();
        if (listener == null)
        {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        this.addListener(SWT.Selection, new TypedListener(listener));
    }

    /**
     * Removes the listener from the collection of listeners who will be
     * notified when the receiver's selection changes.
     * 
     * @param listener the listener which should no longer be notified
     * @see SelectionListener
     * @see #addSelectionListener(SelectionListener)
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void removeSelectionListener(SelectionListener listener)
    {
        checkWidget();
        this.removeListener(SWT.Selection, listener);
    }

    /**
     * Fires selection listeners.
     */
    void fireListeners()
    {
        Event e = new Event();
        e.display = this.getDisplay();
        e.item = this;
        e.widget = parent;

        this.notifyListeners(SWT.Selection, e);
    }

    /**
     * Returns true if the column is visible, false otherwise. If the column is
     * in a group and the group is not expanded and this is a detail column,
     * returns false (and vice versa).
     * 
     * @return true if visible, false otherwise
     * 
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public boolean isVisible()
    {
        checkWidget();
        if (group != null)
        {
            if ((group.getExpanded() && !isDetail()) || (!group.getExpanded() && !isSummary()))
            {
                return false;
            }
        }
        return visible;
    }

    /**
     * Returns the visibility state as set with {@code setVisible}.
     * 
     * @return the visible
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public boolean getVisible()
    {
        checkWidget();
        return visible;
    }

    /**
     * Sets the column's visibility.
     * 
     * @param visible the visible to set
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setVisible(boolean visible)
    {
        checkWidget();
        
        boolean before = isVisible();
        
        this.visible = visible;
        
        if (isVisible() != before)
        {
            if (visible)
            {
                notifyListeners(SWT.Show, new Event());                
            }
            else
            {
                notifyListeners(SWT.Hide, new Event());
            }
            
            GridColumn[] colsOrdered = parent.getColumnsInOrder();
            boolean fire = false;
            for (int i = 0; i < colsOrdered.length; i++)
            {
                GridColumn column = colsOrdered[i];
                if (column == this)
                {
                    fire = true;
                }
                else
                {
                    if (column.isVisible()) column.fireMoved();
                }
            }
            
            parent.redraw();            
        }
    }

    /**
     * Causes the receiver to be resized to its preferred size.
     * 
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void pack()
    {
        checkWidget();

        GC gc = new GC(parent);
        int newWidth = getHeaderRenderer().computeSize(gc, SWT.DEFAULT, SWT.DEFAULT, this).x;
        for (int i = 0; i < parent.getItems().length; i++)
        {
            GridItem item = parent.getItems()[i];
            if (item.isVisible())
            {
                newWidth = Math.max(newWidth, getCellRenderer().computeSize(gc, SWT.DEFAULT,
                                                                            SWT.DEFAULT, item).x);
            }
        }
        gc.dispose();
        setWidth(newWidth);
        parent.redraw();
    }

    /**
     * Returns true if this column includes a tree toggle.
     * 
     * @return true if the column includes the tree toggle.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public boolean isTree()
    {
        checkWidget();
        return tree;
    }

    /**
     * Returns true if the column includes a check box.
     * 
     * @return true if the column includes a check box.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public boolean isCheck()
    {
        checkWidget();

        return check || tableCheck;
    }

    /**
     * Sets the cell renderer.
     * 
     * @param cellRenderer The cellRenderer to set.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setCellRenderer(GridCellRenderer cellRenderer)
    {
        checkWidget();

        this.cellRenderer = cellRenderer;
        initCellRenderer();
    }

    /**
     * Sets the header renderer.
     * 
     * @param headerRenderer The headerRenderer to set.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setHeaderRenderer(GridHeaderRenderer headerRenderer)
    {
        checkWidget();
        this.headerRenderer = headerRenderer;
        initHeaderRenderer();
    }

    /**
     * Adds a listener to the list of listeners notified when the column is
     * moved or resized.
     * 
     * @param listener listener
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void addControlListener(ControlListener listener)
    {
        checkWidget();
        if (listener == null)
        {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        TypedListener typedListener = new TypedListener(listener);
        addListener(SWT.Resize, typedListener);
        addListener(SWT.Move, typedListener);
    }

    /**
     * Removes the given control listener.
     * 
     * @param listener listener.
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void removeControlListener(ControlListener listener)
    {
        checkWidget();
        if (listener == null)
        {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        removeListener(SWT.Resize, listener);
        removeListener(SWT.Move, listener);
    }

    /**
     * Fires moved event.
     */
    void fireMoved()
    {
        Event e = new Event();
        e.display = this.getDisplay();
        e.item = this;
        e.widget = parent;

        this.notifyListeners(SWT.Move, e);
    }

    /**
     * Fires resized event.
     */
    void fireResized()
    {
        Event e = new Event();
        e.display = this.getDisplay();
        e.item = this;
        e.widget = parent;

        this.notifyListeners(SWT.Resize, e);
    }

    /**
     * Adds or removes the columns tree toggle.
     * 
     * @param tree true to add toggle.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setTree(boolean tree)
    {
        checkWidget();

        this.tree = tree;
        cellRenderer.setTree(tree);
        parent.redraw();
    }

    /**
     * Returns the column alignment.
     * 
     * @return SWT.LEFT, SWT.RIGHT, SWT.CENTER
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public int getAlignment()
    {
        checkWidget();
        return cellRenderer.getAlignment();
    }

    /**
     * Sets the column alignment.
     * 
     * @param alignment SWT.LEFT, SWT.RIGHT, SWT.CENTER
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setAlignment(int alignment)
    {
        checkWidget();
        cellRenderer.setAlignment(alignment);
    }

    /**
     * Returns true if this column is moveable.
     * 
     * @return true if moveable.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public boolean getMoveable()
    {
        checkWidget();
        return moveable;
    }

    /**
     * Sets the column moveable or fixed.
     * 
     * @param moveable true to enable column moving
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setMoveable(boolean moveable)
    {
        checkWidget();
        this.moveable = moveable;
        parent.redraw();
    }

    /**
     * Returns true if the column is resizeable.
     * 
     * @return true if the column is resizeable.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public boolean getResizeable()
    {
        checkWidget();
        return resizeable;
    }

    /**
     * Sets the column resizeable.
     * 
     * @param resizeable true to make the column resizeable
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setResizeable(boolean resizeable)
    {
        checkWidget();
        this.resizeable = resizeable;
    }

    /**
     * Returns the column group if this column was created inside a group, or
     * {@code null} otherwise.
     * 
     * @return the column group.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public GridColumnGroup getColumnGroup()
    {
        checkWidget();
        return group;
    }

    /**
     * Returns true if this column is set as a detail column in a column group.
     * Detail columns are shown when the group is expanded.
     * 
     * @return true if the column is a detail column.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public boolean isDetail()
    {
        checkWidget();
        return detail;
    }

    /**
     * Sets the column as a detail column in a column group. Detail columns are
     * shown when a column group is expanded. If this column was not created in
     * a column group, this method has no effect.
     * 
     * @param detail true to show this column when the group is expanded.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setDetail(boolean detail)
    {
        checkWidget();
        this.detail = detail;
    }

    /**
     * Returns true if this column is set as a summary column in a column group.
     * Summary columns are shown when the group is collapsed.
     * 
     * @return true if the column is a summary column.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public boolean isSummary()
    {
        checkWidget();
        return summary;
    }

    /**
     * Sets the column as a summary column in a column group. Summary columns
     * are shown when a column group is collapsed. If this column was not
     * created in a column group, this method has no effect.
     * 
     * @param summary true to show this column when the group is collapsed.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setSummary(boolean summary)
    {
        checkWidget();
        this.summary = summary;
    }

    /**
     * Returns the bounds of this column's header.
     * 
     * @return bounds of the column header
     */
    Rectangle getBounds()
    {
        Rectangle bounds = new Rectangle(0, 0, 0, 0);

        if (!isVisible())
        {
            return bounds;
        }

        Point loc = parent.getOrigin(this, null);
        bounds.x = loc.x;
        bounds.y = loc.y;
        bounds.width = getWidth();
        bounds.height = parent.getHeaderHeight();
        if (getColumnGroup() != null)
        {
            bounds.height -= parent.getGroupHeaderHeight();
        }

        return bounds;
    }

    /**
     * @return the tableCheck
     */
    protected boolean isTableCheck()
    {
        return tableCheck;
    }

    /**
     * @param tableCheck the tableCheck to set
     */
    protected void setTableCheck(boolean tableCheck)
    {
        this.tableCheck = tableCheck;
        
        cellRenderer.setCheck(tableCheck || check);
    }

    /**
     * Returns true if cells in the receiver can be selected.
     * 
     * @return the cellSelectionEnabled
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public boolean getCellSelectionEnabled()
    {
        checkWidget();
        return cellSelectionEnabled;
    }

    /**
     * Sets whether cells in the receiver can be selected.
     * 
     * @param cellSelectionEnabled the cellSelectionEnabled to set
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setCellSelectionEnabled(boolean cellSelectionEnabled)
    {
        checkWidget();
        this.cellSelectionEnabled = cellSelectionEnabled;
    }

    /**
     * Returns the parent grid.
     * 
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public Grid getParent()
    {
        checkWidget();
        return parent;
    }

    /**
     * Returns the checkable state.  If false the checkboxes in the column cannot be checked.
     * 
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public boolean getCheckable()
    {
        checkWidget();
        return checkable;
    }

    /**
     * Sets the checkable state.  If false the checkboxes in the column cannot be checked.
     * 
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setCheckable(boolean checkable)
    {
        checkWidget();
        this.checkable = checkable;
    }
    
    void setColumnIndex(int newIndex)
    {
        cellRenderer.setColumn(newIndex);
    }

    /**
     * Returns the true if the cells in receiver wrap their text.
     * 
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public boolean getWordWrap()
    {
        checkWidget();
        return cellRenderer.isWordWrap();
    }

    /**
     * If the argument is true, wraps the text in the receiver's cells.  This feature will not cause
     * the row height to expand to accommodate the wrapped text.  Please use 
     * <code>Grid#setItemHeight</code> to change the height of each row.
     * 
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setWordWrap(boolean wordWrap)
    {
        checkWidget();
        cellRenderer.setWordWrap(wordWrap);
        parent.redraw();
    }
}
