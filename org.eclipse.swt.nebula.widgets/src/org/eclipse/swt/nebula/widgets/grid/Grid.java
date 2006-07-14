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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.nebula.widgets.grid.internal.DefaultDropPointRenderer;
import org.eclipse.swt.nebula.widgets.grid.internal.DefaultEmptyCellRenderer;
import org.eclipse.swt.nebula.widgets.grid.internal.DefaultEmptyColumnHeaderRenderer;
import org.eclipse.swt.nebula.widgets.grid.internal.DefaultEmptyRowHeaderRenderer;
import org.eclipse.swt.nebula.widgets.grid.internal.DefaultFocusRenderer;
import org.eclipse.swt.nebula.widgets.grid.internal.DefaultRowHeaderRenderer;
import org.eclipse.swt.nebula.widgets.grid.internal.DefaultTopLeftRenderer;
import org.eclipse.swt.nebula.widgets.grid.internal.IScrollBarProxy;
import org.eclipse.swt.nebula.widgets.grid.internal.NullScrollBarProxy;
import org.eclipse.swt.nebula.widgets.grid.internal.ScrollBarProxyAdapter;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TypedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p> 
 * Instances of this class implement a selectable user interface object that
 * displays a list of images and strings and issue notification when selected.
 * <p>
 * The item children that may be added to instances of this class must be of
 * type {@code GridItem}.
 * </p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SWT.SINGLE, SWT.MULTI, SWT.NO_FOCUS, SWT.CHECK</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection, DefaultSelection</dd>
 * </dl>
 * 
 * @author chris.gross@us.ibm.com
 */
public class Grid extends Canvas
{
    //TODO: figure out better way to allow renderers to trigger events
    //TODO: scroll as necessary when performing drag select (current strategy ok)
    //TODO: need to refactor the way the range select remembers older selection
    //TODO: remember why i decided i needed to refactor the way the range select remembers older selection
    //TODO: need to alter how column drag selection works to allow selection of spanned cells 
    //TODO: row header renderer changes need to be recorded somewhere, the value passed in is now 
    //the item rather than the row number, but the computeSize method still takes the row number
    //this all needs to be rethought    
    //TODO:  JAVADOC!
    //TODO: put alpha warnings in class comment
    //TODO: record internal widget event name changes
    //TODO: column freezing
    
    /**
     * Alpha blending value used when drawing the dragged column header.
     */
    private static final int COLUMN_DRAG_ALPHA = 128;

    /**
     * Number of pixels below the header to draw the drop point.
     */
    private static final int DROP_POINT_LOWER_OFFSET = 3;

    /**
     * Horizontal scrolling increment, in pixels.
     */
    private static final int HORZ_SCROLL_INCREMENT = 5;

    /**
     * The area to the left and right of the column boundary/resizer that is
     * still considered the resizer area. This prevents a user from having to be
     * *exactly* over the resizer.
     */
    private static final int COLUMN_RESIZER_THRESHOLD = 4;

    /**
     * The minimum width of a column header.
     */
    private static final int MIN_COLUMN_HEADER_WIDTH = 20;

    /**
     * The number used when sizing the row header (i.e. size it for '1000')
     * initially.
     */
    private static final int INITIAL_ROW_HEADER_SIZING_VALUE = 1000;

    /**
     * The factor to multiply the current row header sizing value by when
     * determining the next sizing value. Used for performance reasons.
     */
    private static final int ROW_HEADER_SIZING_MULTIPLIER = 10;

    /**
     * Tracks whether the scroll values are correct. If not they will be
     * recomputed in onPaint. This allows us to get a free ride on top of the
     * OS's paint event merging to assure that we don't perform this expensive
     * operation when unnecessary.
     */
    private boolean scrollValuesObsolete = false;

    /**
     * All items in the table, not just root items.
     */
    private Vector items = new Vector();

    /**
     * List of selected items.
     */
    private Vector selectedItems = new Vector();

    /**
     * Reference to the item in focus.
     */
    private GridItem focusItem;
    
    private boolean cellSelectionEnabled = false;
    
    private Vector selectedCells = new Vector();
    private Vector selectedCellsBeforeRangeSelect = new Vector();
    
    private boolean cellDragSelectionOccuring = false;
    private boolean cellRowDragSelectionOccuring = false;
    private boolean cellColumnDragSelectionOccuring = false;
    private boolean cellDragCTRL = false;
    private boolean followupCellSelectionEventOwed = false;
    
    private boolean cellSelectedOnLastMouseDown;
    private boolean cellRowSelectedOnLastMouseDown;
    private boolean cellColumnSelectedOnLastMouseDown;
    
    private GridColumn shiftSelectionAnchorColumn;
    
    private GridColumn focusColumn;
    
    private Vector selectedColumns = new Vector();
    
    /**
     * This is the column that the user last navigated to, but may not be the focusColumn because
     * that column may be spanned in the current row.  This is only used in situations where the user
     * has used the keyboard to navigate up or down in the table and the focusColumn has switched to
     * a new column because the intended column (was maintained in this var) was spanned.  The table
     * will attempt to set focus back to the intended column during subsequent up/down navigations.
     */
    private GridColumn intendedFocusColumn;
    

    /**
     * List of table columns in creation/index order.
     */
    private Vector columns = new Vector();

    /**
     * List of the table columns in the order they are displayed.
     */
    private Vector displayOrderedColumns = new Vector();

    private GridColumnGroup[] columnGroups = new GridColumnGroup[0];

    /**
     * Renderer to paint the top left area when both column and row headers are
     * shown.
     */
    private IRenderer topLeftRenderer = new DefaultTopLeftRenderer();

    /**
     * Renderer used to paint row headers.
     */
    private IRenderer rowHeaderRenderer = new DefaultRowHeaderRenderer();

    /**
     * Renderer used to paint empty column headers, used when the columns don't
     * fill the horz space.
     */
    private IRenderer emptyColumnHeaderRenderer = new DefaultEmptyColumnHeaderRenderer();

    /**
     * Renderer used to paint empty cells to fill horz and vert space.
     */
    private GridCellRenderer emptyCellRenderer = new DefaultEmptyCellRenderer();

    /**
     * Renderer used to paint empty row headers when the rows don't fill the
     * vertical space.
     */
    private IRenderer emptyRowHeaderRenderer = new DefaultEmptyRowHeaderRenderer();

    /**
     * Renderers the UI affordance identifying where the dragged column will be
     * dropped.
     */
    private IRenderer dropPointRenderer = new DefaultDropPointRenderer();

    /**
     * Renderer used to paint on top of an already painted row to denote focus.
     */
    private IRenderer focusRenderer = new DefaultFocusRenderer();

    /**
     * Are row headers visible?
     */
    private boolean rowHeaderVisible = false;

    /**
     * Are column headers visible?
     */
    private boolean columnHeadersVisible = false;

    /**
     * Type of selection behavior. Valid values are SWT.SINGLE and SWT.MULTI.
     */
    private int selectionType = SWT.SINGLE;

    /**
     * True if selection highlighting is enabled.
     */
    private boolean selectionEnabled = true;
    
    /**
     * Height of each row.
     */
    private int rowHeight = 0;

    /**
     * Width of each row header.
     */
    private int rowHeaderWidth = 30;

    /**
     * The row header width is variable. The row header width gets larger as
     * more rows are added to the table to ensure that the row header has enough
     * room to display the longest string of numbers that display in the row
     * header. This determination of how wide to make the row header is rather
     * slow and therefore is only done at every 1000 items (or so). This
     * variable remembers how many items were last computed and therefore when
     * the number of items is greater than this value, we need to recalculate
     * the row header width. See newItem().
     */
    private int lastRowHeaderWidthCalculationAt = 0;

    /**
     * Height of each column header.
     */
    private int headerHeight = 0;

    /**
     * True if mouse is hover on a column boundary and can resize the column.
     */
    private boolean hoveringOnColumnResizer = false;

    /**
     * Reference to the column being resized.
     */
    private GridColumn columnBeingResized;

    /**
     * Is the user currently resizing a column?
     */
    private boolean resizingColumn = false;

    /**
     * The mouse X position when the user starts the resize.
     */
    private int resizingStartX = 0;

    /**
     * The width of the column when the user starts the resize. This, together
     * with the resizingStartX determines the current width during resize.
     */
    private int resizingColumnStartWidth = 0;

    /**
     * Reference to the column whose header is currently in a pushed state.
     */
    private GridColumn columnBeingPushed;

    /**
     * Is the user currently pushing a column header?
     */
    private boolean pushingColumn = false;

    /**
     * Is the user currently pushing a column header and hovering over that same
     * header?
     */
    private boolean pushingAndHovering = false;

    /**
     * X position of the mouse when the user first pushes a column header.
     */
    private int startHeaderPushX = 0;

    /**
     * X position of the mouse when the user has initiated a drag. This is
     * different than startHeaderPushX because the mouse is allowed some
     * 'wiggle-room' until the header is put into drag mode.
     */
    private int startHeaderDragX = 0;

    /**
     * The current X position of the mouse during a header drag.
     */
    private int currentHeaderDragX = 0;

    /**
     * Are we currently dragging a column header?
     */
    private boolean draggingColumn = false;

    private GridColumn dragDropBeforeColumn = null;

    private GridColumn dragDropAfterColumn = null;

    /**
     * True if the current dragDropPoint is a valid drop point for the dragged
     * column. This is false if the column groups are involved and a column is
     * being dropped into or out of its column group.
     */
    private boolean dragDropPointValid = true;

    /**
     * Reference to the currently item that the mouse is currently hovering
     * over.
     */
    private GridItem hoveringItem;

    /**
     * Reference to the column that the mouse is currently hovering over.
     * Includes the header and all cells (all rows) in this column.
     */
    private GridColumn hoveringColumn;

    private GridColumn hoveringColumnHeader;

    private GridColumnGroup hoverColumnGroupHeader;

    /**
     * String-based detail of what is being hovered over in a cell. This allows
     * a renderer to differentiate between hovering over different parts of the
     * cell. For example, hovering over a checkbox in the cell or hovering over
     * a tree node in the cell. The table does nothing with this string except
     * to set it back in the renderer when its painted. The renderer sets this
     * during its notify method (InternalWidget.HOVER) and the table pulls it
     * back and maintains it so it can be set back when the cell is painted. The
     * renderer determines what the hover detail means and how it affects
     * painting.
     */
    private String hoveringDetail = "";

    /**
     * Are the grid lines visible?
     */
    private boolean linesVisible = true;

    /**
     * Grid line color.
     */
    private Color lineColor;

    /**
     * Vertical scrollbar proxy.
     */
    private IScrollBarProxy vScroll;

    /**
     * Horizontal scrollbar proxy.
     */
    private IScrollBarProxy hScroll;

    /**
     * The number of GridItems whose visible = true. Maintained for
     * performance reasons (rather than iterating over all items).
     */
    private int currentVisibleItems = 0;

    /**
     * Item selected when a multiple selection using shift+click first occurs.
     * This item anchors all further shift+click selections.
     */
    private GridItem shiftSelectionAnchorItem;

    private boolean columnScrolling = false;

    private int groupHeaderHeight;

    private Color cellHeaderSelectionBackground;
    
    /**
     * Filters out unnecessary styles, adds mandatory styles and generally
     * manages the style to pass to the super class.
     * 
     * @param style user specified style.
     * @return style to pass to the super class.
     */
    private static int checkStyle(int style)
    {
        int mask = SWT.BORDER | SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT | SWT.H_SCROLL | SWT.V_SCROLL
                   | SWT.SINGLE | SWT.MULTI | SWT.NO_FOCUS | SWT.CHECK;
        int newStyle = style & mask;
        newStyle |= SWT.DOUBLE_BUFFERED;
        return newStyle;
    }

    /**
     * Constructs a new instance of this class given its parent and a style
     * value describing its behavior and appearance.
     * <p>
     * 
     * @param parent a composite control which will be the parent of the new
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
     * </ul>
     * @see SWT#SINGLE
     * @see SWT#MULTI
     */
    public Grid(Composite parent, int style)
    {
        super(parent, checkStyle(style));

        topLeftRenderer.setDisplay(getDisplay());
        rowHeaderRenderer.setDisplay(getDisplay());
        emptyColumnHeaderRenderer.setDisplay(getDisplay());
        emptyCellRenderer.setDisplay(getDisplay());
        dropPointRenderer.setDisplay(getDisplay());
        focusRenderer.setDisplay(getDisplay());
        emptyRowHeaderRenderer.setDisplay(getDisplay());

        setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        setForeground(getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
        setLineColor(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

        if ((style & SWT.MULTI) != 0)
        {
            selectionType = SWT.MULTI;
        }
        
        if (getVerticalBar() != null)
        {
            getVerticalBar().setVisible(false);
            vScroll = new ScrollBarProxyAdapter(getVerticalBar());
        }
        else
        {
            vScroll = new NullScrollBarProxy();
        }

        if (getHorizontalBar() != null)
        {
            getHorizontalBar().setVisible(false);            
            hScroll = new ScrollBarProxyAdapter(getHorizontalBar());
        }
        else
        {
            hScroll = new NullScrollBarProxy();
        }

        scrollValuesObsolete = true;

        initListeners();

        GC gc = new GC(this);
        rowHeight = gc.getFontMetrics().getHeight() + 2;
        gc.dispose();
        
        selectedCells.add(new Point(1,1));
        
        RGB sel = getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION).getRGB();
        RGB white = getDisplay().getSystemColor(SWT.COLOR_WHITE).getRGB();
        
        RGB cellSel = blend(sel,white,50);
        
        cellHeaderSelectionBackground = new Color(getDisplay(),cellSel);
    }

    
    
    /** 
     * {@inheritDoc}
     */
    public void dispose()
    {
        cellHeaderSelectionBackground.dispose();
        super.dispose();
    }

    
    
    /**
     * Returns the background color of column and row headers when a cell in 
     * the row or header is selected.
     * 
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public Color getCellHeaderSelectionBackground()
    {
        checkWidget();
        return cellHeaderSelectionBackground;
    }

    /**
     * Sets the background color of column and row headers displayed when a cell in 
     * the row or header is selected.
     * 
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setCellHeaderSelectionBackground(Color cellSelectionBackground)
    {
        checkWidget();
        this.cellHeaderSelectionBackground = cellSelectionBackground;
    }

    /**
     * Adds the listener to the collection of listeners who will be notified
     * when the receiver's selection changes, by sending it one of the messages
     * defined in the {@code SelectionListener} interface.
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
        addListener(SWT.Selection, new TypedListener(listener));
        addListener(SWT.DefaultSelection, new TypedListener(listener));
    }

    /**
     * Adds the listener to the collection of listeners who will be notified
     * when the receiver's items changes, by sending it one of the messages
     * defined in the {@code TreeListener} interface.
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
     * @see TreeListener
     * @see #removeTreeListener
     * @see org.eclipse.swt.events.TreeEvent
     */
    public void addTreeListener(TreeListener listener)
    {
        checkWidget();
        if (listener == null)
        {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }

        addListener(SWT.Expand, new TypedListener(listener));
        addListener(SWT.Collapse, new TypedListener(listener));
    }

    /**
     * {@inheritDoc}
     */
    public Point computeSize(int wHint, int hHint, boolean changed)
    {
        checkWidget();

        Point prefSize = null;
        if (wHint == SWT.DEFAULT || hHint == SWT.DEFAULT)
        {
            prefSize = getTableSize();
            prefSize.x += 2 * getBorderWidth();
            prefSize.y += 2 * getBorderWidth();
        }

        int x = 0;
        int y = 0;

        if (wHint == SWT.DEFAULT)
        {
            x += prefSize.x;
            if (getVerticalBar() != null)
            {
                x += getVerticalBar().getSize().x;
            }
        }
        else
        {
            x = wHint;
        }

        if (hHint == SWT.DEFAULT)
        {
            y += prefSize.y;
            if (getHorizontalBar() != null)
            {
                y += getHorizontalBar().getSize().y;
            }
        }
        else
        {
            y = hHint;
        }

        return new Point(x, y);
    }

    /**
     * Deselects the item at the given zero-relative index in the receiver. If
     * the item at the index was already deselected, it remains deselected.
     * Indices that are out of range are ignored.
     * 
     * @param index the index of the item to deselect
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void deselect(int index)
    {
        checkWidget();

        if (index < 0 || index > items.size() - 1)
        {
            return;
        }

        GridItem item = (GridItem)items.get(index);
        if (selectedItems.contains(item))
        {
            selectedItems.remove(item);
        }

        redraw();
    }

    /**
     * Deselects the items at the given zero-relative indices in the receiver.
     * If the item at the given zero-relative index in the receiver is selected,
     * it is deselected. If the item at the index was not selected, it remains
     * deselected. The range of the indices is inclusive. Indices that are out
     * of range are ignored.
     * 
     * @param start the start index of the items to deselect
     * @param end the end index of the items to deselect
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void deselect(int start, int end)
    {
        checkWidget();

        for (int i = start; i <= end; i++)
        {
            if (i < 0)
            {
                continue;
            }
            if (i > items.size() - 1)
            {
                break;
            }

            GridItem item = (GridItem)items.get(i);
            if (selectedItems.contains(item))
            {
                selectedItems.remove(item);
            }
        }
        redraw();
    }

    /**
     * Deselects the items at the given zero-relative indices in the receiver.
     * If the item at the given zero-relative index in the receiver is selected,
     * it is deselected. If the item at the index was not selected, it remains
     * deselected. Indices that are out of range and duplicate indices are
     * ignored.
     * 
     * @param indices the array of indices for the items to deselect
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_NULL_ARGUMENT - if the set of indices is null</li>
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void deselect(int[] indices)
    {
        checkWidget();
        if (indices == null)
        {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }

        for (int i = 0; i < indices.length; i++)
        {
            int j = indices[i];

            if (j >= 0 && j < items.size())
            {
                GridItem item = (GridItem)items.get(j);
                if (selectedItems.contains(item))
                {
                    selectedItems.remove(item);
                }
            }
        }
        redraw();
    }

    /**
     * Deselects all selected items in the receiver.
     * 
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void deselectAll()
    {
        checkWidget();
        selectedItems.clear();
        redraw();
    }

    /**
     * Returns the column at the given, zero-relative index in the receiver.
     * Throws an exception if the index is out of range. If no
     * {@code GridColumn}s were created by the programmer, this method will
     * throw {@code ERROR_INVALID_RANGE} despite the fact that a single column
     * of data may be visible in the table. This occurs when the programmer uses
     * the table like a list, adding items but never creating a column.
     * 
     * @param index the index of the column to return
     * @return the column at the given index
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number
     * of elements in the list minus 1 (inclusive)</li>
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public GridColumn getColumn(int index)
    {
        checkWidget();

        if (index < 0 || index > getColumnCount() - 1)
        {
            SWT.error(SWT.ERROR_INVALID_RANGE);
        }

        return (GridColumn)columns.get(index);
    }

    /**
     * Returns the column at the given point in the receiver or null if no such
     * column exists. The point is in the coordinate system of the receiver.
     * 
     * @param point the point used to locate the column
     * @return the column at the given point
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_NULL_ARGUMENT - if the point is null</li>
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public GridColumn getColumn(Point point)
    {
        checkWidget();
        if (point == null)
        {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }

        GridColumn overThis = null;

        int x2 = 0;

        if (rowHeaderVisible)
        {
            if (point.x <= rowHeaderWidth)
            {
                return null;
            }

            x2 += rowHeaderWidth;
        }

        x2 -= getHScrollSelectionInPixels();

        for (Iterator columnIterator = displayOrderedColumns.iterator(); columnIterator.hasNext(); )
        {
            GridColumn column = (GridColumn) columnIterator.next();

            if (!column.isVisible())
            {
                continue;
            }

            if (point.x >= x2 && point.x <= x2 + column.getWidth())
            {
                overThis = column;
                break;
            }

            x2 += column.getWidth();
        }

        if (overThis == null)
        {
            return null;
        }

        // special logic for column spanning
        GridItem item = getItem(point);
        if (item != null)
        {
            int displayColIndex = displayOrderedColumns.indexOf(overThis);

            // track back all previous columns and check their spanning
            for (int i = 0; i < displayColIndex; i++)
            {
                if (!((GridColumn)displayOrderedColumns.get(i)).isVisible())
                {
                    continue;
                }

                int colIndex = indexOf((GridColumn)displayOrderedColumns.get(i));
                int span = item.getColumnSpan(colIndex);

                if (i + span >= displayColIndex)
                {
                    overThis = (GridColumn)displayOrderedColumns.get(i);
                    break;
                }
            }
        }

        return overThis;
    }

    /**
     * Returns the number of columns contained in the receiver. If no
     * {@code GridColumn}s were created by the programmer, this value is
     * zero, despite the fact that visually, one column of items may be visible.
     * This occurs when the programmer uses the table like a list, adding items
     * but never creating a column.
     * 
     * @return the number of columns
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public int getColumnCount()
    {
        checkWidget();
        return columns.size();
    }

    /**
     * Returns an array of zero-relative integers that map the creation order of
     * the receiver's items to the order in which they are currently being
     * displayed.
     * <p>
     * Specifically, the indices of the returned array represent the current
     * visual order of the items, and the contents of the array represent the
     * creation order of the items.
     * </p>
     * <p>
     * Note: This is not the actual structure used by the receiver to maintain
     * its list of items, so modifying the array will not affect the receiver.
     * </p>
     * 
     * @return the current visual order of the receiver's items
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public int[] getColumnOrder()
    {
        checkWidget();

        int[] order = new int[columns.size()];
        int i = 0;
        for (Iterator colIterator = displayOrderedColumns.iterator(); colIterator.hasNext(); )
        {
            GridColumn col = (GridColumn) colIterator.next();
            order[i] = columns.indexOf(col);
            i++;
        }
        return order;
    }
    
    /**
     * Returns the number of column groups contained in the receiver. 
     * 
     * @return the number of column groups
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public int getColumnGroupCount()
    {
        checkWidget();
        return columnGroups.length;
    }
    
    /**
     * Returns an array of {@code GridColumnGroup}s which are the column groups in the
     * receiver. 
     * <p>
     * Note: This is not the actual structure used by the receiver to maintain
     * its list of items, so modifying the array will not affect the receiver.
     * </p>
     * 
     * @return the column groups in the receiver
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public GridColumnGroup[] getColumnGroups()
    {
        checkWidget();
        GridColumnGroup[] newArray = new GridColumnGroup[columnGroups.length];
        System.arraycopy (columnGroups, 0, newArray, 0, columnGroups.length);
        return newArray;
    }
    
    /**
     * Returns the column group at the given, zero-relative index in the receiver.
     * Throws an exception if the index is out of range. 
     * 
     * @param index the index of the column group to return
     * @return the column group at the given index
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number
     * of elements in the list minus 1 (inclusive)</li>
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public GridColumnGroup getColumnGroup(int index)
    {
        checkWidget();
        
        if (index < 0 || index >= columnGroups.length) 
            SWT.error(SWT.ERROR_INVALID_RANGE);
        
        return columnGroups[index];
    }

    /**
     * Sets the order that the items in the receiver should be displayed in to
     * the given argument which is described in terms of the zero-relative
     * ordering of when the items were added.
     * 
     * @param order the new order to display the items
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS -if not called from the thread that
     * created the receiver</li>
     * </ul>
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_NULL_ARGUMENT - if the item order is null</li>
     * <li>ERROR_INVALID_ARGUMENT - if the order is not the same length as the
     * number of items, or if an item is listed twice, or if the order splits a
     * column group</li>
     * </ul>
     */
    public void setColumnOrder(int[] order)
    {
        checkWidget();

        if (order == null)
        {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }

        if (order.length != displayOrderedColumns.size())
        {
            SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        }

        boolean[] seen = new boolean[displayOrderedColumns.size()];

        for (int i = 0; i < order.length; i++)
        {
            if (order[i] < 0 || order[i] >= displayOrderedColumns.size())
            {
                SWT.error(SWT.ERROR_INVALID_ARGUMENT);
            }
            if (seen[order[i]])
            {
                SWT.error(SWT.ERROR_INVALID_ARGUMENT);
            }
            seen[order[i]] = true;
        }

        if (columnGroups.length != 0)
        {
            GridColumnGroup currentGroup = null;
            int colsInGroup = 0;

            for (int i = 0; i < order.length; i++)
            {
                GridColumn col = getColumn(order[i]);

                if (currentGroup != null)
                {
                    if (col.getColumnGroup() != currentGroup)
                    {
                        SWT.error(SWT.ERROR_INVALID_ARGUMENT);
                    }
                    else
                    {
                        colsInGroup--;
                        if (colsInGroup == 0)
                        {
                            currentGroup = null;
                        }
                    }
                }
                else if (col.getColumnGroup() != null)
                {
                    currentGroup = col.getColumnGroup();
                    colsInGroup = currentGroup.getColumns().length - 1;
                }

            }
        }

        GridColumn[] cols = getColumns();

        displayOrderedColumns.clear();

        for (int i = 0; i < order.length; i++)
        {
            displayOrderedColumns.add(cols[order[i]]);
        }
    }

    /**
     * Returns an array of {@code GridColumn}s which are the columns in the
     * receiver. If no {@code GridColumn}s were created by the programmer,
     * the array is empty, despite the fact that visually, one column of items
     * may be visible. This occurs when the programmer uses the table like a
     * list, adding items but never creating a column.
     * <p>
     * Note: This is not the actual structure used by the receiver to maintain
     * its list of items, so modifying the array will not affect the receiver.
     * </p>
     * 
     * @return the items in the receiver
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public GridColumn[] getColumns()
    {
        checkWidget();
        return (GridColumn[])columns.toArray(new GridColumn[columns.size()]);
    }

    /**
     * Returns the empty cell renderer.
     * 
     * @return Returns the emptyCellRenderer.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public GridCellRenderer getEmptyCellRenderer()
    {
        checkWidget();
        return emptyCellRenderer;
    }

    /**
     * Returns the empty column header renderer.
     * 
     * @return Returns the emptyColumnHeaderRenderer.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public IRenderer getEmptyColumnHeaderRenderer()
    {
        checkWidget();
        return emptyColumnHeaderRenderer;
    }

    /**
     * Returns the empty row header renderer.
     * 
     * @return Returns the emptyRowHeaderRenderer.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public IRenderer getEmptyRowHeaderRenderer()
    {
        checkWidget();
        return emptyRowHeaderRenderer;
    }

    /**
     * Returns the externally managed horizontal scrollbar.
     * 
     * @return the external horizontal scrollbar.
     * @see #setHorizontalScrollBarProxy(IScrollBarProxy)
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    protected IScrollBarProxy getExternalHorizontalScrollBar()
    {
        checkWidget();
        return hScroll;
    }

    /**
     * Returns the externally managed vertical scrollbar.
     * 
     * @return the external vertical scrollbar.
     * @see #setlVerticalScrollBarProxy(IScrollBarProxy)
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    protected IScrollBarProxy getExternalVerticalScrollBar()
    {
        checkWidget();
        return vScroll;
    }

    /**
     * Gets the focus renderer.
     * 
     * @return Returns the focusRenderer.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public IRenderer getFocusRenderer()
    {
        checkWidget();
        return focusRenderer;
    }

    /**
     * Returns the height of the column headers. If this table has column
     * groups, the returned value includes the height of group headers.
     * 
     * @return height of the column header row
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public int getHeaderHeight()
    {
        checkWidget();
        return headerHeight;
    }

    /**
     * Returns the height of the column group headers.
     * 
     * @return height of column group headers
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public int getGroupHeaderHeight()
    {
        checkWidget();
        return groupHeaderHeight;
    }

    /**
     * Returns {@code true} if the receiver's header is visible, and
     * {@code false} otherwise.
     * 
     * @return the receiver's header's visibility state
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public boolean getHeaderVisible()
    {
        checkWidget();
        return columnHeadersVisible;
    }

    /**
     * Returns the item at the given, zero-relative index in the receiver.
     * Throws an exception if the index is out of range.
     * 
     * @param index the index of the item to return
     * @return the item at the given index
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the 
     * list minus 1 (inclusive) </li>     * 
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public GridItem getItem(int index)
    {
        checkWidget();
        
        if (index < 0 || index >= items.size())
        {
            SWT.error(SWT.ERROR_INVALID_RANGE);
        }
        
        return (GridItem)items.get(index);
    }

    /**
     * Returns the item at the given point in the receiver or null if no such
     * item exists. The point is in the coordinate system of the receiver.
     * 
     * @param point the point used to locate the item
     * @return the item at the given point
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_NULL_ARGUMENT - if the point is null</li> 
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public GridItem getItem(Point point)
    {
        checkWidget();
        
        if (point == null)
            SWT.error(SWT.ERROR_NULL_ARGUMENT);

        Point p = new Point(point.x, point.y);

        if (columnHeadersVisible)
        {
            if (p.y <= headerHeight)
            {
                return null;
            }
            p.y -= headerHeight;
        }

        int row = p.y / (rowHeight + 1);

        // /CLOVER:OFF
        if (p.y % (rowHeight + 1) != 0)
        {
            row++;
        }
        // /CLOVER:ON

        row += vScroll.getSelection();

        if (row > items.size())
        {
            return null;
        }

        int index = row - 1;

        Iterator iter = items.iterator();
        while (row > 0)
        {
            if (!iter.hasNext())
            {
                return null;
            }

            GridItem item = (GridItem)iter.next();
            if (item.isVisible())
            {
                row--;
            }
            else
            {
                index++;
            }
        }

        // /CLOVER:OFF
        if (index < 0)
        {
            return null;
        }
        // /CLOVER:ON

        return (GridItem)items.get(index);
    }

    /**
     * Returns the number of items contained in the receiver.
     * 
     * @return the number of items
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public int getItemCount()
    {
        checkWidget();
        return getItems().length;
    }

    /**
     * Returns the height of each row.
     * 
     * @return height of each row
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public int getItemHeight()
    {
        checkWidget();
        return rowHeight;
    }

    /**
     * Returns a (possibly empty) List of {@code NGridItem}s which are the
     * items in the receiver.
     * <p>
     * Note: This is not the actual structure used by the receiver to maintain
     * its list of items, so modifying the array will not affect the receiver.
     * </p>
     * 
     * @return the items in the receiver
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public GridItem[] getItems()
    {
        checkWidget();
        return (GridItem[])items.toArray(new GridItem[items.size()]);
    }

    /**
     * Returns the line color.
     * 
     * @return Returns the lineColor.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public Color getLineColor()
    {
        checkWidget();
        return lineColor;
    }

    /**
     * Returns true if the lines are visible.
     * 
     * @return Returns the linesVisible.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public boolean getLinesVisible()
    {
        checkWidget();
        return linesVisible;
    }

    /**
     * Returns the next visible item in the table.
     * 
     * @param item item
     * @return next visible item or null
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public GridItem getNextVisibleItem(GridItem item)
    {
        checkWidget();

        int index = items.indexOf(item);
        if (items.size() == index + 1)
        {
            return null;
        }

        GridItem nextItem = (GridItem)items.get(index + 1);

        while (!nextItem.isVisible())
        {
            index++;
            if (items.size() == index + 1)
            {
                return null;
            }

            nextItem = (GridItem)items.get(index + 1);
        }

        return nextItem;
    }

    /**
     * Returns the previous visible item in the table. Passing null for the item
     * will return the last visible item in the table.
     * 
     * @param item item or null
     * @return previous visible item or if item==null last visible item
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public GridItem getPreviousVisibleItem(GridItem item)
    {
        checkWidget();

        int index = 0;
        if (item == null)
        {
            index = items.size();
        }
        else
        {
            index = items.indexOf(item);
            if (index == 0)
            {
                return null;
            }
        }

        GridItem prevItem = (GridItem)items.get(index - 1);

        while (!prevItem.isVisible())
        {
            index--;
            if (index == 0)
            {
                return null;
            }

            prevItem = (GridItem)items.get(index - 1);
        }

        return prevItem;
    }
    
    /**
     * Returns the previous visible column in the table.
     * 
     * @param column column
     * @return previous visible column or null
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public GridColumn getPreviousVisibleColumn(GridColumn column)
    {        
        checkWidget();
        
        int index = displayOrderedColumns.indexOf(column);
        
        if (index == 0)
            return null;
        
        index --;
        
        GridColumn previous = (GridColumn)displayOrderedColumns.get(index);
        
        while (!previous.isVisible())
        {
            if (index == 0)
                return null;
            
            index --;
            previous = (GridColumn)displayOrderedColumns.get(index);
        }
        
        return previous;
    }
    
    /**
     * Returns the next visible column in the table.
     * 
     * @param column column
     * @return next visible column or null
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public GridColumn getNextVisibleColumn(GridColumn column)
    {        
        checkWidget();
        
        int index = displayOrderedColumns.indexOf(column);
        
        if (index == displayOrderedColumns.size() - 1)
            return null;
        
        index ++;
        
        GridColumn next = (GridColumn)displayOrderedColumns.get(index);
        
        while (!next.isVisible())
        {
            if (index == displayOrderedColumns.size() - 1)
                return null;
            
            index ++;
            next = (GridColumn)displayOrderedColumns.get(index);
        }
        
        return next;
    }

    /**
     * Returns the number of root items contained in the receiver.
     * 
     * @return the number of items
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public int getRootItemCount()
    {
        checkWidget();
        return getRootItems().size();
    }

    /**
     * Returns a (possibly empty) List of {@code GridItem}s which are
     * the root items in the receiver.
     * <p>
     * Note: This is not the actual structure used by the receiver to maintain
     * its list of items, so modifying the array will not affect the receiver.
     * </p>
     * 
     * @return the items in the receiver
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public List getRootItems()
    {
        checkWidget();

        ArrayList children = new ArrayList();

        for (int i = 0; i < items.size(); i++)
        {
            GridItem child = (GridItem)items.get(i);
            if (child.getParentItem() == null)
            {
                children.add((GridItem)items.get(i));
            }
        }

        return children;
    }

    /**
     * Gets the row header renderer.
     * 
     * @return Returns the rowHeaderRenderer.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public IRenderer getRowHeaderRenderer()
    {
        checkWidget();
        return rowHeaderRenderer;
    }

    /**
     * Returns a arrray of {@code GridItem}s that are currently selected in the
     * receiver. The order of the items is unspecified. An empty array indicates
     * that no items are selected.
     * <p>
     * Note: This is not the actual structure used by the receiver to maintain
     * its selection, so modifying the array will not affect the receiver.
     * </p>
     * 
     * @return an array representing the selection
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public GridItem[] getSelection()
    {
        checkWidget();
        return (GridItem[])selectedItems.toArray(new GridItem[selectedItems.size()]);
    }

    /**
     * Returns the number of selected items contained in the receiver.
     * 
     * @return the number of selected items
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public int getSelectionCount()
    {
        checkWidget();
        return selectedItems.size();
    }
    
    /**
     * Returns the number of selected cells contained in the receiver.
     * 
     * @return the number of selected cells
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public int getCellSelectionCount()
    {
        checkWidget();
        return selectedCells.size();
    }

    /**
     * Returns the zero-relative index of the item which is currently selected
     * in the receiver, or -1 if no item is selected.
     * 
     * @return the index of the selected item
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public int getSelectionIndex()
    {
        checkWidget();

        if (selectedItems.size() == 0)
        {
            return -1;
        }

        return items.indexOf(selectedItems.get(0));
    }

    /**
     * Returns the zero-relative indices of the items which are currently
     * selected in the receiver. The order of the indices is unspecified. The
     * array is empty if no items are selected.
     * <p>
     * Note: This is not the actual structure used by the receiver to maintain
     * its selection, so modifying the array will not affect the receiver.
     * </p>
     * 
     * @return the array of indices of the selected items
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public int[] getSelectionIndices()
    {
        checkWidget();

        int[] indices = new int[selectedItems.size()];
        int i = 0;
        for (Iterator itemIterator = selectedItems.iterator(); itemIterator.hasNext(); )
        {
            GridItem item = (GridItem) itemIterator.next();
            indices[i] = items.indexOf(item);
            i++;
        }
        return indices;
    }

    /**
     * Returns the zero-relative index of the item which is currently at the top
     * of the receiver. This index can change when items are scrolled or new
     * items are added or removed.
     * 
     * @return the index of the top item
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public int getTopIndex()
    {
        checkWidget();

        int firstVisibleIndex = 0;
        if (vScroll.getVisible())
        {
            // figure out first visible row and last visible row
            firstVisibleIndex = vScroll.getSelection();

            int row = firstVisibleIndex + 1;

            Iterator itemsIter = items.iterator();
            while (row > 0 && itemsIter.hasNext())
            {
                GridItem item = (GridItem)itemsIter.next();

                if (item.isVisible())
                {
                    row--;
                    if (row == 0)
                    {
                        firstVisibleIndex = items.indexOf(item);
                    }
                }
            }
        }
        return firstVisibleIndex;
    }

    /**
     * Gets the top left renderer.
     * 
     * @return Returns the topLeftRenderer.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public IRenderer getTopLeftRenderer()
    {
        checkWidget();
        return topLeftRenderer;
    }

    /**
     * Searches the receiver's list starting at the first column (index 0) until
     * a column is found that is equal to the argument, and returns the index of
     * that column. If no column is found, returns -1.
     * 
     * @param column the search column
     * @return the index of the column
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_NULL_ARGUMENT - if the column is null</li>
     * </ul>      
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public int indexOf(GridColumn column)
    {
        checkWidget();
        
        if (column == null)
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        
        if (column.getParent() != this) return -1;
        
        return columns.indexOf(column);
    }

    /**
     * Searches the receiver's list starting at the first item (index 0) until
     * an item is found that is equal to the argument, and returns the index of
     * that item. If no item is found, returns -1.
     * 
     * @param item the search item
     * @return the index of the item
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_NULL_ARGUMENT - if the item is null</li>
     * </ul>  
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public int indexOf(GridItem item)
    {
        checkWidget();
        
        if (item == null)
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        
        if (item.getParent() != this) return -1;
        
        return items.indexOf(item);
    }

    /**
     * Returns {@code true} if the receiver's row header is visible, and
     * {@code false} otherwise.
     * <p>
     * 
     * @return the receiver's row header's visibility state
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public boolean isRowHeaderVisible()
    {
        checkWidget();
        return rowHeaderVisible;
    }

    /**
     * Returns {@code true} if the item is selected, and {@code false}
     * otherwise. Indices out of range are ignored.
     * 
     * @param index the index of the item
     * @return the visibility state of the item at the index
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public boolean isSelected(int index)
    {
        checkWidget();
        
        if (index < 0 || index >= items.size()) return false;
        
        return isSelected((GridItem)items.get(index));
    }

    /**
     * Returns true if the given item is selected.
     * 
     * @param item item
     * @return true if the item is selected.
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_NULL_ARGUMENT - if the item is null</li> 
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public boolean isSelected(GridItem item)
    {
        checkWidget();
        return selectedItems.contains(item);
    }

    /**
     * Returns true if the given cell is selected.
     * 
     * @param cell cell
     * @return true if the cell is selected.
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_NULL_ARGUMENT - if the cell is null</li> 
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public boolean isCellSelected(Point cell)
    {
        checkWidget();
        
        if (cell == null)
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        
        return selectedCells.contains(cell);
    }
    
    /**
     * Removes the item from the receiver at the given zero-relative index.
     * 
     * @param index the index for the item
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number
     * of elements in the list minus 1 (inclusive)</li>
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void remove(int index)
    {
        checkWidget();
        if (index < 0 || index > items.size() - 1)
        {
            SWT.error(SWT.ERROR_INVALID_RANGE);
        }
        GridItem item = (GridItem)items.get(index);
        item.dispose();
        redraw();
    }

    /**
     * Removes the items from the receiver which are between the given
     * zero-relative start and end indices (inclusive).
     * 
     * @param start the start of the range
     * @param end the end of the range
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_INVALID_RANGE - if either the start or end are not between 0
     * and the number of elements in the list minus 1 (inclusive)</li>
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void remove(int start, int end)
    {
        checkWidget();

        for (int i = end; i >= start; i--)
        {
            if (i < 0 || i > items.size() - 1)
            {
                SWT.error(SWT.ERROR_INVALID_RANGE);
            }
            GridItem item = (GridItem)items.get(i);
            item.dispose();
        }
        redraw();
    }

    /**
     * Removes the items from the receiver's list at the given zero-relative
     * indices.
     * 
     * @param indices the array of indices of the items
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number
     * of elements in the list minus 1 (inclusive)</li>
     * <li>ERROR_NULL_ARGUMENT - if the indices array is null</li>
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void remove(int[] indices)
    {
        checkWidget();

        if (indices == null)
        {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }

        GridItem[] removeThese = new GridItem[indices.length];
        for (int i = 0; i < indices.length; i++)
        {
            int j = indices[i];
            if (j < items.size() && j >= 0)
            {
                removeThese[i] = (GridItem)items.get(j);
            }
            else
            {
                SWT.error(SWT.ERROR_INVALID_RANGE);
            }

        }
        for (int i = 0; i < removeThese.length; i++)
        {
            GridItem item = removeThese[i];
            item.dispose();
        }
        redraw();
    }

    /**
     * Removes all of the items from the receiver.
     * 
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void removeAll()
    {
        checkWidget();
        items.clear();
        selectedItems.clear();
        focusItem = null;
        redraw();
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
        removeListener(SWT.Selection, listener);
        removeListener(SWT.DefaultSelection, listener);
    }

    /**
     * Removes the listener from the collection of listeners who will be
     * notified when the receiver's items changes.
     * 
     * @param listener the listener which should no longer be notified
     * @see TreeListener
     * @see #addTreeListener(TreeListener)
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void removeTreeListener(TreeListener listener)
    {
        checkWidget();
        removeListener(SWT.Expand, listener);
        removeListener(SWT.Collapse, listener);
    }

    /**
     * Selects the item at the given zero-relative index in the receiver. If the
     * item at the index was already selected, it remains selected. Indices that
     * are out of range are ignored.
     * 
     * @param index the index of the item to select
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void select(int index)
    {
        checkWidget();

        if (!selectionEnabled) return;
        
        if (index < 0 || index >= items.size()) return;
        
        GridItem item = (GridItem)items.get(index);
        
        if (selectionType == SWT.MULTI && selectedItems.contains(item)) return;

        if (selectionType == SWT.SINGLE) selectedItems.clear();
        
        selectedItems.add(item);

        redraw();
    }

    /**
     * Selects the items in the range specified by the given zero-relative
     * indices in the receiver. The range of indices is inclusive. The current
     * selection is not cleared before the new items are selected.
     * <p>
     * If an item in the given range is not selected, it is selected. If an item
     * in the given range was already selected, it remains selected. Indices
     * that are out of range are ignored and no items will be selected if start
     * is greater than end. If the receiver is single-select and there is more
     * than one item in the given range, then all indices are ignored.
     * 
     * @param start the start of the range
     * @param end the end of the range
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     * @see Grid#setSelection(int,int)
     */
    public void select(int start, int end)
    {
        checkWidget();
        
        if (!selectionEnabled) return;
        
        if (selectionType == SWT.SINGLE && start != end) return;
        
        if (selectionType == SWT.SINGLE) selectedItems.clear();
        
        for (int i = start; i <= end; i++)
        {
            if (i < 0)
            {
                continue;
            }
            if (i > items.size() - 1)
            {
                break;
            }

            GridItem item = (GridItem)items.get(i);
            
            if (!selectedItems.contains(item))
                selectedItems.add(item);
        }
        redraw();
    }

    /**
     * Selects the items at the given zero-relative indices in the receiver. The
     * current selection is not cleared before the new items are selected.
     * <p>
     * If the item at a given index is not selected, it is selected. If the item
     * at a given index was already selected, it remains selected. Indices that
     * are out of range and duplicate indices are ignored. If the receiver is
     * single-select and multiple indices are specified, then all indices are
     * ignored.
     * 
     * @param indices the array of indices for the items to select
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_NULL_ARGUMENT - if the array of indices is null</li>
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     * @see Grid#setSelection(int[])
     */
    public void select(int[] indices)
    {
        checkWidget();

        if (indices == null)
        {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        
        if (!selectionEnabled) return;
        
        if (selectionType == SWT.SINGLE && indices.length > 1) return;
        
        if (selectionType == SWT.SINGLE) selectedItems.clear();

        for (int i = 0; i < indices.length; i++)
        {
            int j = indices[i];

            if (j >= 0 && j < items.size())
            {
                GridItem item = (GridItem)items.get(j);
                if (!selectedItems.contains(item))
                    selectedItems.add(item);
            }
        }
        redraw();
    }

    /**
     * Selects all of the items in the receiver.
     * <p>
     * If the receiver is single-select, do nothing.
     * 
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void selectAll()
    {
        checkWidget();
        
        if (!selectionEnabled) return;
        
        if (selectionType == SWT.SINGLE) return;
        
        selectedItems.clear();
        selectedItems.addAll(items);
        redraw();
    }

    /**
     * Sets the empty cell renderer.
     * 
     * @param emptyCellRenderer The emptyCellRenderer to set.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setEmptyCellRenderer(GridCellRenderer emptyCellRenderer)
    {
        checkWidget();
        emptyCellRenderer.setDisplay(getDisplay());
        this.emptyCellRenderer = emptyCellRenderer;
    }

    /**
     * Sets the empty column header renderer.
     * 
     * @param emptyColumnHeaderRenderer The emptyColumnHeaderRenderer to set.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setEmptyColumnHeaderRenderer(IRenderer emptyColumnHeaderRenderer)
    {
        checkWidget();
        emptyColumnHeaderRenderer.setDisplay(getDisplay());
        this.emptyColumnHeaderRenderer = emptyColumnHeaderRenderer;
    }

    /**
     * Sets the empty row header renderer.
     * 
     * @param emptyRowHeaderRenderer The emptyRowHeaderRenderer to set.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setEmptyRowHeaderRenderer(IRenderer emptyRowHeaderRenderer)
    {
        checkWidget();
        emptyRowHeaderRenderer.setDisplay(getDisplay());
        this.emptyRowHeaderRenderer = emptyRowHeaderRenderer;
    }

    /**
     * Sets the external horizontal scrollbar. Allows the scrolling to be
     * managed externally from the table. This functionality is only intended
     * when SWT.H_SCROLL is not given.
     * <p>
     * Using this feature, a ScrollBar could be instantiated outside the table,
     * wrapped in IScrollBar and thus be 'connected' to the table.
     * 
     * @param scroll The horizontal scrollbar to set.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    protected void setHorizontalScrollBarProxy(IScrollBarProxy scroll)
    {
        checkWidget();
        if (getHorizontalBar() != null)
        {
            return;
        }
        hScroll = scroll;
    }

    /**
     * Sets the external vertical scrollbar. Allows the scrolling to be managed
     * externally from the table. This functionality is only intended when
     * SWT.V_SCROLL is not given.
     * <p>
     * Using this feature, a ScrollBar could be instantiated outside the table,
     * wrapped in IScrollBar and thus be 'connected' to the table.
     * 
     * @param scroll The vertical scrollbar to set.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    protected void setlVerticalScrollBarProxy(IScrollBarProxy scroll)
    {
        checkWidget();
        if (getVerticalBar() != null)
        {
            return;
        }
        vScroll = scroll;
    }

    /**
     * Sets the focus renderer.
     * 
     * @param focusRenderer The focusRenderer to set.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setFocusRenderer(IRenderer focusRenderer)
    {
        checkWidget();
        this.focusRenderer = focusRenderer;
    }

    /**
     * Marks the receiver's header as visible if the argument is {@code true},
     * and marks it invisible otherwise.
     * 
     * @param show the new visibility state
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setHeaderVisible(boolean show)
    {
        checkWidget();
        this.columnHeadersVisible = show;
        redraw();
    }

    /**
     * Sets the line color.
     * 
     * @param lineColor The lineColor to set.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setLineColor(Color lineColor)
    {
        checkWidget();
        this.lineColor = lineColor;
    }

    /**
     * Sets the line visibility.
     * 
     * @param linesVisible Te linesVisible to set.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setLinesVisible(boolean linesVisible)
    {
        checkWidget();
        this.linesVisible = linesVisible;
        redraw();
    }

    /**
     * Sets the row header renderer.
     * 
     * @param rowHeaderRenderer The rowHeaderRenderer to set.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setRowHeaderRenderer(IRenderer rowHeaderRenderer)
    {
        checkWidget();
        rowHeaderRenderer.setDisplay(getDisplay());
        this.rowHeaderRenderer = rowHeaderRenderer;
    }

    /**
     * Marks the receiver's row header as visible if the argument is
     * {@code true}, and marks it invisible otherwise. When row headers are
     * visible, horizontal scrolling is always done by column rather than by
     * pixel.
     * 
     * @param show the new visibility state
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setRowHeaderVisible(boolean show)
    {
        checkWidget();
        this.rowHeaderVisible = show;
        setColumnScrolling(true);
        redraw();
    }

    /**
     * Selects the item at the given zero-relative index in the receiver. The
     * current selection is first cleared, then the new item is selected.
     * 
     * @param index the index of the item to select
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setSelection(int index)
    {
        checkWidget();        
        
        if (!selectionEnabled) return;
        
        if (index >= 0 && index < items.size())
        {
            selectedItems.clear();
            selectedItems.add((GridItem)items.get(index));
            redraw();
        }
    }

    /**
     * Selects the items in the range specified by the given zero-relative
     * indices in the receiver. The range of indices is inclusive. The current
     * selection is cleared before the new items are selected.
     * <p>
     * Indices that are out of range are ignored and no items will be selected
     * if start is greater than end. If the receiver is single-select and there
     * is more than one item in the given range, then all indices are ignored.
     * 
     * @param start the start index of the items to select
     * @param end the end index of the items to select
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     * @see Grid#deselectAll()
     * @see Grid#select(int,int)
     */
    public void setSelection(int start, int end)
    {
        checkWidget();
        
        if (!selectionEnabled) return;
        
        if (selectionType == SWT.SINGLE && start != end) return;

        selectedItems.clear();
        for (int i = start; i <= end; i++)
        {
            if (i < 0)
            {
                continue;
            }
            if (i > items.size() - 1)
            {
                break;
            }

            GridItem item = (GridItem)items.get(i);
            selectedItems.add(item);
        }
        redraw();
    }

    /**
     * Selects the items at the given zero-relative indices in the receiver. The
     * current selection is cleared before the new items are selected.
     * <p>
     * Indices that are out of range and duplicate indices are ignored. If the
     * receiver is single-select and multiple indices are specified, then all
     * indices are ignored.
     * 
     * @param indices the indices of the items to select
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_NULL_ARGUMENT - if the array of indices is null</li>
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     * @see Grid#deselectAll()
     * @see Grid#select(int[])
     */
    public void setSelection(int[] indices)
    {
        checkWidget();
        
        if (!selectionEnabled) return;
        
        if (selectionType == SWT.SINGLE && indices.length > 1) return;

        selectedItems.clear();
        for (int i = 0; i < indices.length; i++)
        {
            int j = indices[i];

            if (j < 0)
            {
                continue;
            }
            if (j > items.size() - 1)
            {
                break;
            }

            GridItem item = (GridItem)items.get(j);
            selectedItems.add(item);

        }
        redraw();
    }

    /**
     * Sets the receiver's selection to be the given List of items. The current
     * selection is cleared before the new items are selected.
     * <p>
     * Items that are not in the receiver are ignored. If the receiver is
     * single-select and multiple items are specified, then all items are
     * ignored.
     * 
     * @param selection the List of items
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    private void setSelection(List selection)
    {
        checkWidget();
        selectedItems.clear();
        selectedItems.addAll(selection);
        redraw();
    }

    /**
     * Sets the receiver's selection to be the given array of items. The current
     * selection is cleared before the new items are selected.
     * <p>
     * Items that are not in the receiver are ignored. If the receiver is
     * single-select and multiple items are specified, then all items are
     * ignored.
     * 
     * @param _items the array of items
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_NULL_ARGUMENT - if the array of items is null</li>
     * <li>ERROR_INVALID_ARGUMENT - if one of the items has been disposed</li>
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     * @see Grid#deselectAll()
     * @see Grid#select(int[])
     * @see Grid#setSelection(int[])
     */
    public void setSelection(GridItem[] _items)
    {
        checkWidget();
        
        if (!selectionEnabled) return;
        
        if (selectionType == SWT.SINGLE && _items.length > 1) return;
        
        setSelection(Arrays.asList(_items));
    }

    /**
     * Sets the zero-relative index of the item which is currently at the top of
     * the receiver. This index can change when items are scrolled or new items
     * are added and removed.
     * 
     * @param index the index of the top item
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setTopIndex(int index)
    {
        checkWidget();
        if (index < 0 || index >= items.size())
        {
            return;
        }

        GridItem item = (GridItem)items.get(index);
        if (!item.isVisible())
        {
            return;
        }

        if (!vScroll.getVisible())
        {
            return;
        }

        int vScrollAmount = 0;

        for (int i = 0; i < index; i++)
        {
            if (((GridItem)items.get(i)).isVisible())
            {
                vScrollAmount++;
            }
        }

        vScroll.setSelection(vScrollAmount);
        redraw();
    }

    /**
     * Sets the top left renderer.
     * 
     * @param topLeftRenderer The topLeftRenderer to set.
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setTopLeftRenderer(IRenderer topLeftRenderer)
    {
        checkWidget();
        topLeftRenderer.setDisplay(getDisplay());
        this.topLeftRenderer = topLeftRenderer;
    }

    /**
     * Shows the column. If the column is already showing in the receiver, this
     * method simply returns. Otherwise, the columns are scrolled until the
     * column is visible.
     * 
     * @param col the column to be shown
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void showColumn(GridColumn col)
    {
        checkWidget();

        if (!hScroll.getVisible())
        {
            return;
        }

        if (!col.isVisible())
        {
            return;
        }

        int x = getColumnHeaderXPosition(col);

        int firstVisibleX = 0;
        if (rowHeaderVisible)
        {
            firstVisibleX = rowHeaderWidth;
        }

        // if its visible just return
        if (x >= firstVisibleX
            && (x + col.getWidth()) <= (firstVisibleX + (getClientArea().width - firstVisibleX)))
        {
            return;
        }

        if (x < firstVisibleX)
        {
            hScroll.setSelection(getHScrollSelectionInPixels() - (firstVisibleX - x));
        }
        else
        {
            if (col.getWidth() > getClientArea().width - firstVisibleX)
            {
                hScroll.setSelection(getHScrollSelectionInPixels() + (x - firstVisibleX));
            }
            else
            {
                x -= getClientArea().width - firstVisibleX - col.getWidth();
                hScroll.setSelection(getHScrollSelectionInPixels() + (x - firstVisibleX));
            }
        }

        redraw();
    }

    /**
     * Shows the item. If the item is already showing in the receiver, this
     * method simply returns. Otherwise, the items are scrolled until the item
     * is visible.
     * 
     * @param item the item to be shown
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void showItem(GridItem item)
    {
        checkWidget();

        if (!item.isVisible())
        {
            GridItem parent = item.getParentItem();
            do
            {
                if (!parent.isExpanded())
                {
                    parent.setExpanded(true);
                }
                parent = parent.getParentItem();
            }
            while (parent != null);

            updateScrollbars();
        }

        int index = items.indexOf(item);

        index = 0;
        for (Iterator itemIterIterator = items.iterator(); itemIterIterator.hasNext(); )
        {
            GridItem itemIter = (GridItem) itemIterIterator.next();
            if (itemIter == item)
            {
                break;
            }

            if (itemIter.isVisible())
            {
                index++;
            }
        }

        int visibleRows = getPotentiallyPaintedRows();

        // if its visible just return
        if (index >= vScroll.getSelection() && index < vScroll.getSelection() + visibleRows)
        {
            return;
        }

        if (index < vScroll.getSelection())
        {
            vScroll.setSelection(index);
        }
        else
        {
            vScroll.setSelection(index - (visibleRows - 1));
        }

        redraw();
    }

    /**
     * Shows the selection. If the selection is already showing in the receiver,
     * this method simply returns. Otherwise, the items are scrolled until the
     * selection is visible.
     * 
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void showSelection()
    {
        checkWidget();

        if (selectedItems.size() == 0)
        {
            return;
        }

        GridItem item = (GridItem)selectedItems.get(0);

        showItem(item);
    }
    
    /**
     * Enables selection highlighting if the argument is <code>true</code>.  
     * 
     * @param selectionEnabled the selection enabled state
     * 
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setSelectionEnabled(boolean selectionEnabled)
    {
        checkWidget();
        
        if (!selectionEnabled)
        {
            selectedItems.clear();
            redraw();
        }
        
        this.selectionEnabled = selectionEnabled;
    }
    
    /**
     * Returns <code>true</code> if selection is enabled, false otherwise.
     * 
     * @return the selection enabled state
     * 
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public boolean getSelectionEnabled()
    {
        checkWidget();
        return selectionEnabled;
    }
    

    /**
     * Computes and sets the height of the header row. This method will ask for
     * the preferred size of all the column headers and use the max.
     * 
     * @param gc GC for font metrics, etc.
     */
    private void computeHeaderHeight(GC gc)
    {

        int colHeaderHeight = 0;
        for (Iterator columnsIterator = columns.iterator(); columnsIterator.hasNext(); )
        {
            GridColumn column = (GridColumn) columnsIterator.next();
            colHeaderHeight = Math
                .max(column.getHeaderRenderer().computeSize(gc, column.getWidth(), SWT.DEFAULT,
                                                            column).y, colHeaderHeight);
        }

        int groupHeight = 0;
        for (int groupIndex = 0; groupIndex < columnGroups.length; groupIndex++)
        {
            GridColumnGroup group = (GridColumnGroup) columnGroups[groupIndex];
            groupHeight = Math.max(group.getHeaderRenderer().computeSize(gc, SWT.DEFAULT,
                                                                         SWT.DEFAULT, group).y,
                                   groupHeight);
        }

        headerHeight = colHeaderHeight + groupHeight;
        groupHeaderHeight = groupHeight;
    }

    /**
     * Returns the computed row height. Currently this method just gets the
     * preferred size of all the cells in the first row and returns that (it is
     * then used as the height of all rows). Future versions of this method
     * could be more sophisticated.
     * 
     * @param gc GC used to perform font metrics,etc.
     * @return the row height
     */
    private int computeRowHeight(GC gc)
    {
        // row height is currently determined by the height of the first row
        // This could eventually be changed to compute the max height for all
        // cells and
        // potentially even variable heights for different rows (though that
        // would require
        // changing more than just this method)

        int height = -1;

        if (columns.size() == 0 || items.size() == 0)
        {
            return height;
        }

        for (Iterator columnsIterator = columns.iterator(); columnsIterator.hasNext(); )
        {
            GridColumn column = (GridColumn) columnsIterator.next();
            height = Math.max(height, column.getCellRenderer().computeSize(gc, SWT.DEFAULT,
                                                                           SWT.DEFAULT,
                                                                           (GridItem)items.get(0)).y);
        }

        return height <= 0 ? 16 : height;
    }

    /**
     * Fires the selection listeners.
     * 
     * @param item selected item
     */
    void fireSelectionListeners(GridItem item)
    {
        Event e = new Event();
        e.display = getDisplay();
        e.widget = this;
        e.type = SWT.Selection;

        this.notifyListeners(SWT.Selection, e);
    }

    /**
     * Returns the x position of the given column. Takes into account scroll
     * position.
     * 
     * @param column given column
     * @return x position
     */
    private int getColumnHeaderXPosition(GridColumn column)
    {
        if (!column.isVisible())
        {
            return -1;
        }

        int x = 0;

        x -= getHScrollSelectionInPixels();

        if (rowHeaderVisible)
        {
            x += rowHeaderWidth;
        }
        for (Iterator column2Iterator = displayOrderedColumns.iterator(); column2Iterator.hasNext(); )
        {
            GridColumn column2 = (GridColumn) column2Iterator.next();

            if (!column2.isVisible())
            {
                continue;
            }

            if (column2 == column)
            {
                break;
            }

            x += column2.getWidth();
        }

        return x;
    }

    /**
     * Returns the hscroll selection in pixels. This method abstracts away the
     * differences between column by column scrolling and pixel based scrolling.
     * 
     * @return the horizontal scroll selection in pixels
     */
    private int getHScrollSelectionInPixels()
    {
        int selection = hScroll.getSelection();
        if (columnScrolling)
        {
            int pixels = 0;
            for (int i = 0; i < selection; i++)
            {
                pixels += ((GridColumn)displayOrderedColumns.get(i)).getWidth();
            }
            selection = pixels;
        }
        return selection;
    }

    /**
     * Returns the size of the preferred size of the inner table.
     * 
     * @return the preferred size of the table.
     */
    private Point getTableSize()
    {
        int x = 0;
        int y = 0;

        if (columnHeadersVisible)
        {
            y += headerHeight;
        }

        for (Iterator itemIterator = items.iterator(); itemIterator.hasNext(); )
        {
            GridItem item = (GridItem) itemIterator.next();
            if (item.isVisible())
            {
                y += rowHeight + 1;
            }
        }

        if (rowHeaderVisible)
        {
            x += rowHeaderWidth;
        }

        for (Iterator columnIterator = columns.iterator(); columnIterator.hasNext(); )
        {
            GridColumn column = (GridColumn) columnIterator.next();
            if (column.isVisible())
            {
                x += column.getWidth();
            }
        }

        return new Point(x, y);
    }

    /**
     * Returns the number of rows that could possibly be visible in the current
     * client area.
     * 
     * @return the number of potentially visible rows
     */
    private int getPotentiallyPaintedRows()
    {
        int visibleRows = getClientArea().height;
        if (columnHeadersVisible)
        {
            visibleRows -= headerHeight;
        }
        visibleRows = visibleRows / (rowHeight + 1);

        return visibleRows;
    }

    /**
     * Manages the header column dragging and calculates the drop point,
     * triggers a redraw.
     * 
     * @param x mouse x
     * @return true if this event has been consumed.
     */
    private boolean handleColumnDragging(int x)
    {

        GridColumn local_dragDropBeforeColumn = null;
        GridColumn local_dragDropAfterColumn = null;

        int x2 = 1;

        if (rowHeaderVisible)
        {
            x2 += rowHeaderWidth + 1;
        }

        x2 -= getHScrollSelectionInPixels();

        int i = 0;
        GridColumn previousVisibleCol = null;
        boolean nextVisibleColumnIsBeforeCol = false;
        GridColumn firstVisibleCol = null;
        GridColumn lastVisibleCol = null;

        if (x < x2)
        {
            for (Iterator columnIterator = displayOrderedColumns.iterator(); columnIterator.hasNext(); )
            {
                GridColumn column = (GridColumn) columnIterator.next();
                if (!column.isVisible())
                {
                    continue;
                }
                local_dragDropBeforeColumn = column;
                break;
            }
            local_dragDropAfterColumn = null;
        }
        else
        {
            for (Iterator columnIterator = displayOrderedColumns.iterator(); columnIterator.hasNext(); )
            {
                GridColumn column = (GridColumn) columnIterator.next();
                if (!column.isVisible())
                {
                    continue;
                }

                i++;

                if (firstVisibleCol == null)
                {
                    firstVisibleCol = column;
                }
                lastVisibleCol = column;

                if (nextVisibleColumnIsBeforeCol)
                {
                    local_dragDropBeforeColumn = column;
                    nextVisibleColumnIsBeforeCol = false;
                }

                if (x >= x2 && x <= (x2 + column.getWidth()))
                {
                    if (x <= (x2 + column.getWidth() / 2))
                    {
                        local_dragDropBeforeColumn = column;
                        local_dragDropAfterColumn = previousVisibleCol;
                    }
                    else
                    {
                        local_dragDropAfterColumn = column;

                        // the next visible column is the before col
                        nextVisibleColumnIsBeforeCol = true;
                    }
                }

                x2 += column.getWidth();
                previousVisibleCol = column;
            }

            if (local_dragDropBeforeColumn == null)
            {
                local_dragDropAfterColumn = lastVisibleCol;
            }
        }

        currentHeaderDragX = x;

        if (local_dragDropBeforeColumn != dragDropBeforeColumn
            || (dragDropBeforeColumn == null && dragDropAfterColumn == null))
        {
            dragDropPointValid = true;

            // Determine if valid drop point
            if (columnGroups.length != 0)
            {

                if (columnBeingPushed.getColumnGroup() == null)
                {
                    if (local_dragDropBeforeColumn != null
                        && local_dragDropAfterColumn != null
                        && local_dragDropBeforeColumn.getColumnGroup() != null
                        && local_dragDropBeforeColumn.getColumnGroup() == local_dragDropAfterColumn
                            .getColumnGroup())
                    {
                        // Dont move a column w/o a group in between two columns
                        // in the same group
                        dragDropPointValid = false;
                    }
                }
                else
                {
                    if (!(local_dragDropBeforeColumn != null && local_dragDropBeforeColumn
                        .getColumnGroup() == columnBeingPushed.getColumnGroup())
                        && !(local_dragDropAfterColumn != null && local_dragDropAfterColumn
                            .getColumnGroup() == columnBeingPushed.getColumnGroup()))
                    {
                        // Dont move a column with a group
                        dragDropPointValid = false;
                    }
                }
            }
            else
            {
                dragDropPointValid = true;
            }
        }

        dragDropBeforeColumn = local_dragDropBeforeColumn;
        dragDropAfterColumn = local_dragDropAfterColumn;

        redraw();
        return true;
    }

    /**
     * Handles the moving of columns after a column is dropped.
     */
    private void handleColumnDrop()
    {
        draggingColumn = false;

        if ((dragDropBeforeColumn != columnBeingPushed && dragDropAfterColumn != columnBeingPushed)
            && (columnGroups.length == 0 || dragDropPointValid))
        {

            displayOrderedColumns.remove(columnBeingPushed);

            int notifyFrom = 0;

            if (dragDropBeforeColumn == null)
            {
                displayOrderedColumns.add(columnBeingPushed);
                notifyFrom = displayOrderedColumns.size();
            }
            else if (dragDropAfterColumn == null)
            {
                displayOrderedColumns.add(0, columnBeingPushed);
                notifyFrom = 1;
            }
            else
            {
                int insertAtIndex = 0;

                if (columnGroups.length != 0)
                {
                    // ensure that we aren't putting this column into a group,
                    // this is possible if
                    // there are invisible columns between the after and before
                    // cols

                    if (dragDropBeforeColumn.getColumnGroup() == columnBeingPushed.getColumnGroup())
                    {
                        insertAtIndex = displayOrderedColumns.indexOf(dragDropBeforeColumn);
                    }
                    else if (dragDropAfterColumn.getColumnGroup() == columnBeingPushed
                        .getColumnGroup())
                    {
                        insertAtIndex = displayOrderedColumns.indexOf(dragDropAfterColumn) + 1;
                    }
                    else
                    {
                        if (dragDropBeforeColumn.getColumnGroup() == null)
                        {
                            insertAtIndex = displayOrderedColumns.indexOf(dragDropBeforeColumn);
                        }
                        else
                        {
                            GridColumnGroup beforeGroup = dragDropBeforeColumn.getColumnGroup();
                            insertAtIndex = displayOrderedColumns.indexOf(dragDropBeforeColumn);
                            while (insertAtIndex > 0
                                   && ((GridColumn)displayOrderedColumns.get(insertAtIndex -1)).getColumnGroup() == beforeGroup)
                            {
                                insertAtIndex--;
                            }

                        }
                    }
                }
                else
                {
                    insertAtIndex = displayOrderedColumns.indexOf(dragDropBeforeColumn);
                }
                displayOrderedColumns.add(insertAtIndex, columnBeingPushed);
                notifyFrom = insertAtIndex + 1;
            }

            for (int i = notifyFrom; i < displayOrderedColumns.size(); i++)
            {
                ((GridColumn)displayOrderedColumns.get(i)).fireMoved();
            }
        }

        redraw();
    }

    /**
     * Determines if the mouse is pushing the header but has since move out of
     * the header bounds and therefore should be drawn unpushed. Also initiates
     * a column header drag when appropriate.
     * 
     * @param x mouse x
     * @param y mouse y
     * @return true if this event has been consumed.
     */
    private boolean handleColumnHeaderHoverWhilePushing(int x, int y)
    {
        GridColumn overThis = overColumnHeader(x, y);

        if ((overThis == columnBeingPushed) != pushingAndHovering)
        {
            pushingAndHovering = (overThis == columnBeingPushed);
            redraw();
        }
        if (columnBeingPushed.getMoveable())
        {

            // CHECKSTYLE:OFF
            if (pushingAndHovering && Math.abs(startHeaderPushX - x) > 3)
            // CHECKSTYLE:ON
            {

                // stop pushing
                pushingColumn = false;
                columnBeingPushed.getHeaderRenderer().setMouseDown(false);
                columnBeingPushed.getHeaderRenderer().setHover(false);

                // now dragging
                draggingColumn = true;
                columnBeingPushed.getHeaderRenderer().setMouseDown(false);

                startHeaderDragX = x;

                dragDropAfterColumn = null;
                dragDropBeforeColumn = null;
                dragDropPointValid = true;

                handleColumnDragging(x);
            }
        }

        return true;
    }

    /**
     * Determines if a column group header has been clicked and forwards the
     * event to the header renderer.
     * 
     * @param x mouse x
     * @param y mouse y
     * @return true if this event has been consumed.
     */
    private boolean handleColumnGroupHeaderClick(int x, int y)
    {

        if (!columnHeadersVisible)
        {
            return false;
        }

        GridColumnGroup overThis = overColumnGroupHeader(x, y);

        if (overThis == null)
        {
            return false;
        }

        int headerX = 0;
        if (rowHeaderVisible)
        {
            headerX += rowHeaderWidth;
        }

        int width = 0;
        boolean firstCol = false;

        for (Iterator colIterator = displayOrderedColumns.iterator(); colIterator.hasNext(); )
        {
            GridColumn col = (GridColumn) colIterator.next();
            if (col.getColumnGroup() == overThis && col.isVisible())
            {
                firstCol = true;
                width += col.getWidth();
            }
            if (!firstCol && col.isVisible())
            {
                headerX += col.getWidth();
            }
        }

        overThis.getHeaderRenderer().setBounds(headerX - getHScrollSelectionInPixels(), 0, width,
                                               groupHeaderHeight);
        return overThis.getHeaderRenderer()
            .notify(IInternalWidget.LeftMouseButtonDown, new Point(x, y), overThis);
    }

    /**
     * Determines if a column header has been clicked, updates the renderer
     * state and triggers a redraw if necesary.
     * 
     * @param x mouse x
     * @param y mouse y
     * @return true if this event has been consumed.
     */
    private boolean handleColumnHeaderPush(int x, int y)
    {
        if (!columnHeadersVisible)
        {
            return false;
        }

        GridColumn overThis = overColumnHeader(x, y);

        if (overThis == null)
        {
            return false;
        }
        
        if (cellSelectionEnabled && overThis.getMoveable() == false)
        {
            return false;
        }      

        columnBeingPushed = overThis;

        // draw pushed
        columnBeingPushed.getHeaderRenderer().setMouseDown(true);
        columnBeingPushed.getHeaderRenderer().setHover(true);
        pushingAndHovering = true;
        redraw();

        startHeaderPushX = x;
        pushingColumn = true;

        return true;
    }

    /**
     * Sets the new width of the column being resized and fires the appropriate
     * listeners.
     * 
     * @param x mouse x
     */
    private void handleColumnResizerDragging(int x)
    {
        int newWidth = resizingColumnStartWidth + (x - resizingStartX);
        if (newWidth < MIN_COLUMN_HEADER_WIDTH)
        {
            return;
        }
        columnBeingResized.setWidth(newWidth);
        scrollValuesObsolete = true;
        redraw();
        columnBeingResized.fireResized();
    }

    /**
     * Determines if the mouse is hovering on a column resizer and changes the
     * pointer and sets field appropriately.
     * 
     * @param x mouse x
     * @param y mouse y
     * @return true if this event has been consumed.
     */
    private boolean handleHoverOnColumnResizer(int x, int y)
    {
        boolean over = false;
        if (y <= headerHeight)
        {
            int x2 = 0;

            if (rowHeaderVisible)
            {
                x2 += rowHeaderWidth;
            }

            x2 -= getHScrollSelectionInPixels();

            for (Iterator columnIterator = displayOrderedColumns.iterator(); columnIterator.hasNext(); )
            {
                GridColumn column = (GridColumn) columnIterator.next();
                if (!column.isVisible())
                {
                    continue;
                }
                x2 += column.getWidth();

                if (x2 >= (x - COLUMN_RESIZER_THRESHOLD) && x2 <= (x + COLUMN_RESIZER_THRESHOLD))
                {
                    if (column.getResizeable())
                    {
                        if (column.getColumnGroup() != null && y <= groupHeaderHeight)
                        {
                            // if this is not the last column
                            if (column != column.getColumnGroup().getLastVisibleColumn())
                            {
                                break;
                            }
                        }

                        over = true;
                        columnBeingResized = column;
                    }
                    break;
                }
            }
        }

        if (over != hoveringOnColumnResizer)
        {
            if (over)
            {
                setCursor(getDisplay().getSystemCursor(SWT.CURSOR_SIZEWE));
            }
            else
            {
                columnBeingResized = null;
                setCursor(null);
            }
            hoveringOnColumnResizer = over;
        }
        return over;
    }

    /**
     * Paints.
     * 
     * @param e paint event
     */
    private void onPaint(PaintEvent e)
    {
        if (scrollValuesObsolete)
        {
            updateScrollbars();
            scrollValuesObsolete = false;
        }

        int x = 0;
        int y = 0;

        if (columnHeadersVisible)
        {
            paintHeader(e.gc);
            y += headerHeight;
        }

        int firstVisibleIndex = 0;
        int visibleRows = getPotentiallyPaintedRows();
        visibleRows++;
        visibleRows++;

        firstVisibleIndex = getTopIndex();

        int row = firstVisibleIndex;

        for (int i = 0; i < visibleRows; i++)
        {

            x = 0;

            x -= getHScrollSelectionInPixels();

            // get the item to draw
            GridItem item = null;
            if (row < items.size())
            {
                item = (GridItem)items.get(row);

                while (!item.isVisible() && row < items.size() - 1)
                {
                    row++;
                    item = (GridItem)items.get(row);
                }
            }
            if (item != null && !item.isVisible())
            {
                item = null;
            }

            if (item != null)
            {
                boolean cellInRowSelected = false;
                

                if (rowHeaderVisible)
                {

                    // rowHeaderRenderer.setSelected(selectedItems.contains(item));
                    //
                    // rowHeaderRenderer.setBounds(0, y, rowHeaderWidth,
                    // rowHeight + 1);
                    // rowHeaderRenderer.paint(e.gc, row + 1);

                    x += rowHeaderWidth;
                }

                int focusY = y;

                // This variable is used to count how many columns are
                // skipped because the previous column spanned over them
                int skipBecauseSpanned = 0;

                int colIndex = 0;

                // draw regular cells for each column
                for (Iterator columnIterator = displayOrderedColumns.iterator(); columnIterator.hasNext(); )
                {
                    GridColumn column = (GridColumn) columnIterator.next();
                    if (!column.isVisible())
                    {
                        colIndex++;
                        if (skipBecauseSpanned > 0)
                        {
                            skipBecauseSpanned--;
                        }
                        continue;
                    }

                    if (skipBecauseSpanned == 0)
                    {
                        skipBecauseSpanned = item.getColumnSpan(indexOf(column));

                        int width = column.getWidth();

                        if (skipBecauseSpanned > 0)
                        {
                            for (int j = 0; j < skipBecauseSpanned; j++)
                            {
                                if (getColumnCount() <= colIndex + j + 1)
                                {
                                    break;
                                }
                                if (((GridColumn)displayOrderedColumns.get(colIndex + j + 1)).isVisible())
                                {
                                    width += ((GridColumn)displayOrderedColumns.get(colIndex + j + 1)).getWidth();
                                }
                            }
                        }

                        column.getCellRenderer().setBounds(x, y, width, rowHeight);

                        column.getCellRenderer().setRow(i + 1);

                        column.getCellRenderer().setSelected(selectedItems.contains(item));
                        column.getCellRenderer().setFocus(this.isFocusControl());
                        column.getCellRenderer().setRowFocus(focusItem == item);
                        column.getCellRenderer().setCellFocus(cellSelectionEnabled && focusItem == item && focusColumn == column);
                        
                        column.getCellRenderer().setRowHover(hoveringItem == item);
                        column.getCellRenderer().setColumnHover(hoveringColumn == column);
                        
                        if (selectedCells.contains(new Point(indexOf(column),row)))
                        {
                            column.getCellRenderer().setCellSelected(true);
                            cellInRowSelected = true;
                        }
                        else
                        {
                            column.getCellRenderer().setCellSelected(false);                            
                        }

                        if (hoveringItem == item && hoveringColumn == column)
                        {
                            column.getCellRenderer().setHoverDetail(hoveringDetail);
                        }
                        else
                        {
                            column.getCellRenderer().setHoverDetail("");
                        }

                        column.getCellRenderer().paint(e.gc, item);

                        x += width;

                    }
                    else
                    {
                        skipBecauseSpanned--;
                    }
                    colIndex++;

                }

                if (x < getClientArea().width)
                {

                    emptyCellRenderer.setSelected(selectedItems.contains(item));
                    emptyCellRenderer.setFocus(this.isFocusControl());
                    emptyCellRenderer.setRow(i + 1);
                    emptyCellRenderer.setBounds(x, y, getClientArea().width - x + 1, rowHeight);
                    emptyCellRenderer.paint(e.gc, item);
                }

                if (rowHeaderVisible)
                {

                    if (!cellSelectionEnabled)
                    {
                        rowHeaderRenderer.setSelected(selectedItems.contains(item));
                    }
                    else
                    {
                        rowHeaderRenderer.setSelected(cellInRowSelected);
                    }

                    rowHeaderRenderer.setBounds(0, y, rowHeaderWidth, rowHeight + 1);
                    rowHeaderRenderer.paint(e.gc, item);

                }

                // focus
                if (isFocusControl() && !cellSelectionEnabled)
                {
                    if (item == focusItem)
                    {
                        if (focusRenderer != null)
                        {
                            int focusX = 0;
                            if (rowHeaderVisible)
                            {
                                focusX = rowHeaderWidth;
                            }
                            focusRenderer
                                .setBounds(focusX, focusY - 1, getClientArea().width - focusX - 1,
                                           rowHeight + 1);
                            focusRenderer.paint(e.gc, item);
                        }
                    }
                }

            }
            else
            {

                if (rowHeaderVisible)
                {
                    emptyRowHeaderRenderer.setBounds(x, y, rowHeaderWidth, rowHeight + 1);
                    emptyRowHeaderRenderer.paint(e.gc, this);

                    x += rowHeaderWidth;
                }

                emptyCellRenderer.setBounds(x, y, getClientArea().width - x, rowHeight);
                emptyCellRenderer.setFocus(false);
                emptyCellRenderer.setSelected(false);
                emptyCellRenderer.setRow(i + 1);

                for (Iterator columnIterator = displayOrderedColumns.iterator(); columnIterator.hasNext(); )
                {
                    GridColumn column = (GridColumn) columnIterator.next();
                    if (column.isVisible())
                    {
                        emptyCellRenderer.setBounds(x, y, column.getWidth(), rowHeight);
                        emptyCellRenderer.paint(e.gc, this);

                        x += column.getWidth();
                    }
                }

                if (rowHeaderVisible)
                {
                    emptyRowHeaderRenderer.setBounds(0, y, rowHeaderWidth, rowHeight + 1);
                    emptyRowHeaderRenderer.paint(e.gc, this);

                    // x += rowHeaderWidth;
                }

                if (x < getClientArea().width)
                {
                    emptyCellRenderer.setBounds(x, y, getClientArea().width - x + 1, rowHeight);
                    emptyCellRenderer.paint(e.gc, this);
                }
            }

            row++;
            y += rowHeight + 1;
        }

        // draw drop point
        if (draggingColumn)
        {
            if ((dragDropAfterColumn != null || dragDropBeforeColumn != null)
                && (dragDropAfterColumn != columnBeingPushed && dragDropBeforeColumn != columnBeingPushed)
                && dragDropPointValid)
            {
                if (dragDropBeforeColumn != null)
                {
                    x = getColumnHeaderXPosition(dragDropBeforeColumn);
                }
                else
                {
                    x = getColumnHeaderXPosition(dragDropAfterColumn)
                        + dragDropAfterColumn.getWidth();
                }

                Point size = dropPointRenderer.computeSize(e.gc, SWT.DEFAULT, SWT.DEFAULT, null);
                x -= size.x / 2;
                if (x < 0)
                {
                    x = 0;
                }
                dropPointRenderer.setBounds(x - 1, headerHeight + DROP_POINT_LOWER_OFFSET, size.x,
                                            size.y);
                dropPointRenderer.paint(e.gc, null);
            }
        }

    }

    /**
     * Returns a column reference if the x,y coordinates are over a column
     * header (header only).
     * 
     * @param x mouse x
     * @param y mouse y
     * @return column reference which mouse is over, or null.
     */
    private GridColumn overColumnHeader(int x, int y)
    {
        GridColumn col = null;

        if (y <= headerHeight && y > 0)
        {
            col = getColumn(new Point(x, y));
            if (col != null && col.getColumnGroup() != null)
            {
                if (y <= groupHeaderHeight)
                {
                    return null;
                }
            }
        }

        return col;
    }

    /**
     * Returns a column group reference if the x,y coordinates are over a column
     * group header (header only).
     * 
     * @param x mouse x
     * @param y mouse y
     * @return column group reference which mouse is over, or null.
     */
    private GridColumnGroup overColumnGroupHeader(int x, int y)
    {
        GridColumnGroup group = null;

        if (y <= groupHeaderHeight && y > 0)
        {
            GridColumn col = getColumn(new Point(x, y));
            if (col != null)
            {
                group = col.getColumnGroup();
            }
        }

        return group;
    }

    /**
     * Paints the header.
     * 
     * @param gc gc from paint event
     */
    private void paintHeader(GC gc)
    {
        int x = 0;
        int y = 0;

        x -= getHScrollSelectionInPixels();

        if (rowHeaderVisible)
        {
            // paint left corner
            // topLeftRenderer.setBounds(0, y, rowHeaderWidth, headerHeight);
            // topLeftRenderer.paint(gc, null);
            x += rowHeaderWidth;
        }

        GridColumnGroup previousPaintedGroup = null;

        for (Iterator columnIterator = displayOrderedColumns.iterator(); columnIterator.hasNext(); )
        {
            GridColumn column = (GridColumn) columnIterator.next();
            int height = 0;

            if (!column.isVisible())
            {
                continue;
            }

            if (column.getColumnGroup() != null)
            {

                if (column.getColumnGroup() != previousPaintedGroup)
                {
                    int width = column.getWidth();

                    GridColumn nextCol = null;
                    if (displayOrderedColumns.indexOf(column) + 1 < displayOrderedColumns.size())
                    {
                        nextCol = (GridColumn)displayOrderedColumns
                            .get(displayOrderedColumns.indexOf(column) + 1);
                    }

                    while (nextCol != null && nextCol.getColumnGroup() == column.getColumnGroup())
                    {

                        if ((nextCol.getColumnGroup().getExpanded() && !nextCol.isDetail())
                            || (!nextCol.getColumnGroup().getExpanded() && !nextCol.isSummary()))
                        {
                        }
                        else
                        {
                            width += nextCol.getWidth();
                        }

                        if (displayOrderedColumns.indexOf(nextCol) + 1 < displayOrderedColumns
                            .size())
                        {
                            nextCol = (GridColumn)displayOrderedColumns.get(displayOrderedColumns
                                .indexOf(nextCol) + 1);
                        }
                        else
                        {
                            nextCol = null;
                        }
                    }
                    
                    boolean selected = true;
                    
                    for (int i = 0; i < column.getColumnGroup().getColumns().length; i++)
                    {
                        GridColumn col = column.getColumnGroup().getColumns()[i];
                        if (col.isVisible() && (column.getMoveable() || !selectedColumns.contains(col)))
                        {
                            selected = false;
                            break;
                        }
                    }

                    
                    column.getColumnGroup().getHeaderRenderer().setSelected(selected);
                    column.getColumnGroup().getHeaderRenderer()
                        .setHover(hoverColumnGroupHeader == column.getColumnGroup());
                    column.getColumnGroup().getHeaderRenderer().setHoverDetail(hoveringDetail);

                    column.getColumnGroup().getHeaderRenderer().setBounds(x, 0, width,
                                                                          groupHeaderHeight);

                    column.getColumnGroup().getHeaderRenderer().paint(gc, column.getColumnGroup());

                    previousPaintedGroup = column.getColumnGroup();
                }

                height = headerHeight - groupHeaderHeight;
                y = groupHeaderHeight;
            }
            else
            {
                height = headerHeight;
                y = 0;
            }

            if (pushingColumn)
            {
                column.getHeaderRenderer().setHover(
                                                    columnBeingPushed == column
                                                        && pushingAndHovering);
            }
            else
            {
                column.getHeaderRenderer().setHover(hoveringColumnHeader == column);
            }

            column.getHeaderRenderer().setHoverDetail(hoveringDetail);

            column.getHeaderRenderer().setBounds(x, y, column.getWidth(), height);
            
            if (cellSelectionEnabled)
                column.getHeaderRenderer().setSelected(selectedColumns.contains(column));
            
            column.getHeaderRenderer().paint(gc, column);

            x += column.getWidth();
        }

        if (x < getClientArea().width)
        {
            emptyColumnHeaderRenderer.setBounds(x, 0, getClientArea().width - x, headerHeight);
            emptyColumnHeaderRenderer.paint(gc, null);
        }

        if (rowHeaderVisible)
        {
            // paint left corner
            topLeftRenderer.setBounds(0, 0, rowHeaderWidth, headerHeight);
            topLeftRenderer.paint(gc, this);
            // x += rowHeaderWidth;
        }

        if (draggingColumn)
        {

            // TODO: determine if advanced graphics are OK
            // FIXME: BIDI support seems to be screwed up with advanced graphics
            if ((getStyle() & SWT.LEFT_TO_RIGHT) == SWT.LEFT_TO_RIGHT)
            {
                gc.setAlpha(COLUMN_DRAG_ALPHA);
            }

            columnBeingPushed.getHeaderRenderer().setSelected(false);

            int height = 0;

            if (columnBeingPushed.getColumnGroup() != null)
            {
                height = headerHeight - groupHeaderHeight;
                y = groupHeaderHeight;
            }
            else
            {
                height = headerHeight;
                y = 0;
            }

            columnBeingPushed.getHeaderRenderer()
                .setBounds(
                           getColumnHeaderXPosition(columnBeingPushed)
                               + (currentHeaderDragX - startHeaderDragX), y,
                           columnBeingPushed.getWidth(), height);
            columnBeingPushed.getHeaderRenderer().paint(gc, columnBeingPushed);
            columnBeingPushed.getHeaderRenderer().setSelected(false);

            gc.setAlpha(-1);
            gc.setAdvanced(false);
        }

    }

    /**
     * Manages the state of the scrollbars when new items are added or the
     * bounds are changed.
     */
    private void updateScrollbars()
    {
        Point preferredSize = getTableSize();

        Rectangle clientArea = getClientArea();

        // First, figure out if the scrollbars should be visible and turn them
        // on right away
        // this will allow the computations further down to accommodate the
        // correct client
        // area

        // Turn the scrollbars on if necessary and do it all over again if
        // necessary. This ensures
        // that if a scrollbar is turned on/off, the other scrollbar's
        // visibility may be affected (more
        // area may have been added/removed.
        for (int doublePass = 1; doublePass <= 2; doublePass++)
        {

            if (preferredSize.y > clientArea.height)
            {
                vScroll.setVisible(true);
            }
            else
            {
                vScroll.setVisible(false);
                vScroll.setValues(0, 0, 1, 1, 1, 1);
            }
            if (preferredSize.x > clientArea.width)
            {
                hScroll.setVisible(true);
            }
            else
            {
                hScroll.setVisible(false);
                hScroll.setValues(0, 0, 1, 1, 1, 1);
            }

            // get the clientArea again with the now visible/invisible
            // scrollbars
            clientArea = getClientArea();
        }

        // if the scrollbar is visible set its values
        if (vScroll.getVisible())
        {
            // if possible, remember selection, if selection is too large, just
            // make it the max you can
            int selection = Math.min(vScroll.getSelection(), currentVisibleItems - 1);

            int visibleRows = getPotentiallyPaintedRows();

            vScroll.setValues(selection, 0, currentVisibleItems, visibleRows, 1, visibleRows);
        }

        // if the scrollbar is visible set its values
        if (hScroll.getVisible())
        {

            if (!columnScrolling)
            {
                // horizontal scrolling works pixel by pixel

                int hiddenArea = preferredSize.x - clientArea.width + 1;

                // if possible, remember selection, if selection is too large,
                // just
                // make it the max you can
                int selection = Math.min(hScroll.getSelection(), hiddenArea - 1);

                hScroll.setValues(selection, 0, hiddenArea + clientArea.width - 1, clientArea.width,
                                 HORZ_SCROLL_INCREMENT, clientArea.width);
            }
            else
            {
                // horizontal scrolling is column by column

                int hiddenArea = preferredSize.x - clientArea.width + 1;

                int max = 0;
                int i = 0;

                while (hiddenArea > 0 && i < getColumnCount())
                {
                    if (((GridColumn)displayOrderedColumns.get(i)).isVisible())
                    {
                        hiddenArea -= ((GridColumn)displayOrderedColumns.get(max)).getWidth();
                        max++;
                    }
                    i++;
                }

                max++;

                // max should never be greater than the number of visible cols
                int visCols = 0;
                for (Iterator iter = columns.iterator(); iter.hasNext();)
                {
                    GridColumn element = (GridColumn)iter.next();
                    if (element.isVisible())
                    {
                        visCols++;
                    }
                }
                max = Math.min(visCols, max);

                // if possible, remember selection, if selection is too large,
                // just
                // make it the max you can
                int selection = Math.min(hScroll.getSelection(), max);

                hScroll.setValues(selection, 0, max, 1, 1, 1);
            }
        }

    }

    /**
     * Adds/removes items from the selected items list based on the
     * selection/deselection of the given item.
     * 
     * @param item item being selected/unselected
     * @param stateMask key state during selection
     */
    private void updateSelection(GridItem item, int stateMask)
    {
        if (!selectionEnabled)
        {
            return;
        }
        
        if (selectionType == SWT.SINGLE)
        {
            if (selectedItems.contains(item)) return;
            
            selectedItems.clear();
            selectedItems.add(item);
            redraw();

            this.fireSelectionListeners(item);
        }
        else if (selectionType == SWT.MULTI)
        {
            boolean shift = false;
            boolean ctrl = false;

            if ((stateMask & SWT.SHIFT) == SWT.SHIFT)
            {
                shift = true;
            }

            if ((stateMask & SWT.CTRL) == SWT.CTRL)
            {
                ctrl = true;
            }

            if (!shift && !ctrl)
            {
                if (selectedItems.size() == 1 && selectedItems.contains(item)) return;
                
                selectedItems.clear();

                selectedItems.add(item);

                redraw();

                shiftSelectionAnchorItem = null;

                this.fireSelectionListeners(item);
            }
            else if (shift)
            {

                if (shiftSelectionAnchorItem == null)
                {
                    shiftSelectionAnchorItem = focusItem;
                }

//                if (shiftSelectionAnchorItem == item)
//                {
//                    return;
//                }

                boolean maintainAnchorSelection = false;

                if (!ctrl)
                {
                    if (selectedItems.contains(shiftSelectionAnchorItem))
                    {
                        maintainAnchorSelection = true;
                    }
                    selectedItems.clear();
                }

                int anchorIndex = items.indexOf(shiftSelectionAnchorItem);
                int itemIndex = items.indexOf(item);

                int min = 0;
                int max = 0;

                if (anchorIndex < itemIndex)
                {
                    if (maintainAnchorSelection)
                    {
                        min = anchorIndex;
                    }
                    else
                    {
                        min = anchorIndex + 1;
                    }
                    max = itemIndex;
                }
                else
                {
                    if (maintainAnchorSelection)
                    {
                        max = anchorIndex;
                    }
                    else
                    {
                        max = anchorIndex - 1;
                    }
                    min = itemIndex;
                }

                for (int i = min; i <= max; i++)
                {
                    if (!selectedItems.contains(items.get(i)) && ((GridItem)items.get(i)).isVisible())
                    {
                        selectedItems.add((GridItem)items.get(i));
                    }
                }
                redraw();

                fireSelectionListeners(null);

            }
            else if (ctrl)
            {
                if (selectedItems.contains(item))
                {
                    selectedItems.remove(item);
                }
                else
                {
                    selectedItems.add(item);
                }
                redraw();

                shiftSelectionAnchorItem = null;

                this.fireSelectionListeners(item);
            }
        }
        
        redraw();
    }
    
    private void updateCellSelection(Point newCell, int stateMask, boolean dragging, boolean reverseDuplicateSelections)
    {
        Vector v = new Vector();
        v.add(newCell);
        updateCellSelection(v, stateMask, dragging, reverseDuplicateSelections);
    }

    private void updateCellSelection(Vector newCells, int stateMask, boolean dragging, boolean reverseDuplicateSelections)
    {
        boolean shift = false;
        boolean ctrl = false;

        if ((stateMask & SWT.SHIFT) == SWT.SHIFT)
        {
            shift = true;
        }
        else
        {
            shiftSelectionAnchorColumn = null;
            shiftSelectionAnchorItem = null;
        }
        
        if ((stateMask & SWT.CTRL) == SWT.CTRL)
        {
            ctrl = true;
        }

        if (!shift && !ctrl)
        {
            if (newCells.equals(selectedCells)) return;
            
            selectedCells.clear();            
            for (int i = 0; i < newCells.size(); i++)
            {
                addToCellSelection((Point)newCells.get(i));
            }
            
        }
        else if (shift)
        {
            
            Point newCell = (Point)newCells.get(0); //shift selection should only occur with one
            //cell, ignoring others
            
            if ((focusColumn == null) || (focusItem == null))
            {
                return;
            }
            
            shiftSelectionAnchorColumn = getColumn(newCell.x);
            shiftSelectionAnchorItem = getItem(newCell.y);
            
            if (ctrl)
            {
                selectedCells.clear();
                selectedCells.addAll(selectedCellsBeforeRangeSelect);
            }
            else
            {
                selectedCells.clear();
            }
            
           
            
            
            GridColumn currentColumn = focusColumn;
            GridItem currentItem = focusItem;
            
            GridColumn endColumn = getColumn(newCell.x);
            GridItem endItem = getItem(newCell.y);
            
            Point newRange = getSelectionRange(currentItem,currentColumn,endItem,endColumn);
            
            currentColumn = getColumn(newRange.x);
            endColumn = getColumn(newRange.y);
            
            GridColumn startCol = currentColumn;
            
            if (indexOf(currentItem) > indexOf(endItem))
            {
                GridItem temp = currentItem;
                currentItem = endItem;
                endItem = temp;
            }
            
            boolean firstLoop = true;
            
            do
            {
                if (!firstLoop)
                {
                    currentItem = getNextVisibleItem(currentItem);
                }
                
                firstLoop = false;
                
                boolean firstLoop2 = true;
                
                currentColumn = startCol;
                
                do
                {   
                    if (!firstLoop2)
                    {
                        int index = displayOrderedColumns.indexOf(currentColumn) + 1;
                        
                        if (index < displayOrderedColumns.size())
                        {
                            currentColumn = getVisibleColumn_DegradeRight(currentItem,(GridColumn)displayOrderedColumns.get(index));
                        }
                        else
                        {
                            currentColumn = null;
                        }
                        
                        if (currentColumn!= null)
                            if (displayOrderedColumns.indexOf(currentColumn) > displayOrderedColumns.indexOf(endColumn))
                                currentColumn = null;
                    }
                    
                    firstLoop2 = false;
                    
                    if (currentColumn != null)
                    {
                        Point cell = new Point(indexOf(currentColumn),indexOf(currentItem));
                        addToCellSelection(cell);
                    }
                } while (currentColumn != endColumn && currentColumn != null);                
            } while (currentItem != endItem);            
        }
        else if (ctrl)
        {
            boolean reverse = reverseDuplicateSelections;
            if (!selectedCells.containsAll(newCells))
                reverse = false;
            
            if (dragging)
            {
                selectedCells.clear();
                selectedCells.addAll(selectedCellsBeforeRangeSelect);
            }
            
            if (reverse)
            {
                selectedCells.removeAll(newCells);
            }
            else
            {            
                for (int i = 0; i < newCells.size(); i++)
                {
                    addToCellSelection((Point)newCells.get(i));
                }
            }
        }
        
        updateColumnSelection();
        
        Event e = new Event();
        if (dragging)
        {
            e.detail = SWT.DRAG;
            followupCellSelectionEventOwed = true;
        }
        
        notifyListeners(SWT.Selection,e);
        
        redraw();
    }
    
    private void addToCellSelection(Point newCell)
    {
        if (newCell.x < 0 || newCell.x >= columns.size())
            return;
        
        if (newCell.y < 0 || newCell.y >= items.size())
            return;
        
        if (getColumn(newCell.x).getCellSelectionEnabled())
        {
            selectedCells.add(newCell);
        }
    }
    
    void updateColumnSelection()
    {
        //Update the list of which columns have all their cells selected
        selectedColumns.clear();
        
        for (Iterator iter = selectedCells.iterator(); iter.hasNext();)
        {
            Point cell = (Point)iter.next();
            
            GridColumn col = getColumn(cell.x);
            
            selectedColumns.add(col);
        } 
    }
    
    /**
     * Initialize all listeners.
     */
    private void initListeners()
    {
        addPaintListener(new PaintListener()
        {
            public void paintControl(PaintEvent e)
            {
                onPaint(e);
            }
        });

        addListener(SWT.Resize, new Listener()
        {
            public void handleEvent(Event e)
            {
                onResize();
            }
        });

        if (getVerticalBar() != null)
        {
            getVerticalBar().addListener(SWT.Selection, new Listener()
            {
                public void handleEvent(Event e)
                {
                    onScrollSelection();
                }
            });
        }

        if (getHorizontalBar() != null)
        {
            getHorizontalBar().addListener(SWT.Selection, new Listener()
            {
                public void handleEvent(Event e)
                {
                    onScrollSelection();
                }
            });
        }

        addListener(SWT.KeyDown, new Listener()
        {
            public void handleEvent(Event e)
            {
                onKeyDown(e);
            }
        });

        addTraverseListener(new TraverseListener()
        {
            public void keyTraversed(TraverseEvent e)
            {
                e.doit = true;
            }
        });

        addMouseListener(new MouseListener()
        {
            public void mouseDoubleClick(MouseEvent e)
            {
                onMouseDoubleClick(e);
            }

            public void mouseDown(MouseEvent e)
            {
                onMouseDown(e);
            }

            public void mouseUp(MouseEvent e)
            {
                onMouseUp(e);
            }
        });

        addMouseMoveListener(new MouseMoveListener()
        {
            public void mouseMove(MouseEvent e)
            {
                onMouseMove(e);
            }
        });

        addMouseTrackListener(new MouseTrackListener()
        {
            public void mouseEnter(MouseEvent e)
            {
            }

            public void mouseExit(MouseEvent e)
            {
                onMouseExit(e);
            }

            public void mouseHover(MouseEvent e)
            {
            }
        });

        addFocusListener(new FocusListener()
        {
            public void focusGained(FocusEvent e)
            {
                redraw();
            }

            public void focusLost(FocusEvent e)
            {
                redraw();
            }
        });

        // Special code to reflect mouse wheel events if using an external
        // scroller
        addListener(SWT.MouseWheel, new Listener()
        {
            public void handleEvent(Event e)
            {
                onMouseWheel(e);
            }
        });
    }

    /**
     * Mouse wheel event handler.
     * 
     * @param e event
     */
    private void onMouseWheel(Event e)
    {
        if (vScroll.getVisible())
        {
            vScroll.handleMouseWheel(e);
        }
        else if (hScroll.getVisible())
        {
            hScroll.handleMouseWheel(e);
        }
    }

    /**
     * Mouse down event handler.
     * 
     * @param e event
     */
    private void onMouseDown(MouseEvent e)
    {
        // for some reason, SWT prefers the children to get focus if
        // there are any children
        // the setFocus method on Composite will not set focus to the
        // Composite if one of its
        // children can get focus instead. This only affects the table
        // when an editor is open
        // and therefore the table has a child. The solution is to
        // forceFocus()
        if ((getStyle() & SWT.NO_FOCUS) != SWT.NO_FOCUS)
        {
            forceFocus();
        }
        
        cellSelectedOnLastMouseDown = false;
        cellRowSelectedOnLastMouseDown = false;
        cellColumnSelectedOnLastMouseDown = false;

        if (hoveringOnColumnResizer)
        {
            if (e.button == 1)
            {
                resizingColumn = true;
                resizingStartX = e.x;
                resizingColumnStartWidth = columnBeingResized.getWidth();
            }
            return;
        }

        if (e.button == 1 && handleColumnHeaderPush(e.x, e.y))
        {
            return;
        }

        if (e.button == 1 && handleColumnGroupHeaderClick(e.x, e.y))
        {
            return;
        }

        if (e.button == 1 && handleCellClick(e.x, e.y))
        {
            return;
        }

        GridItem item = getItem(new Point(e.x, e.y));
        if (item != null)
        {
            if (cellSelectionEnabled)
            {
                if (e.button == 1)
                {
                    GridColumn col = getColumn(new Point(e.x, e.y));
                    if (col != null)
                    {
                        updateCellSelection(new Point(indexOf(col),indexOf(item)), e.stateMask, false, true);
                        cellSelectedOnLastMouseDown = (getCellSelectionCount() > 0);
                        
                        if (e.stateMask != SWT.SHIFT)
                        {
                            focusColumn = col;
                            focusItem = item;
                        }
                        //showColumn(col);
                        showItem(item);
                        redraw();
                    }      
                    else if (rowHeaderVisible)
                    {
                        if (e.x <= rowHeaderWidth)
                        {
                            
                            boolean shift = ((e.stateMask & SWT.SHIFT) != 0);
                            boolean ctrl = false;
                            if (!shift)
                            {
                                ctrl = ((e.stateMask & SWT.CTRL) != 0);
                            }
                            
                            Vector cells = new Vector();
                            
                            if (shift)
                            {
                                getCells(item,focusItem,cells);
                            }
                            else
                            {
                                getCells(item,cells);
                            }
                            
                            int newStateMask = SWT.NONE;
                            if (ctrl) newStateMask = SWT.CTRL;
                            
                            updateCellSelection(cells, newStateMask, shift, ctrl);
                            cellRowSelectedOnLastMouseDown = (getCellSelectionCount() > 0);
                            
                            if (!shift)
                            {
                                //set focus back to the first visible column
                                focusColumn = getColumn(new Point(rowHeaderWidth + 1,e.y));
                                
                                focusItem = item;
                            }
                            showItem(item);
                            redraw();
                        }
                    }
                    intendedFocusColumn = focusColumn;
                }
            }
            else
            {
                if (e.button != 1)
                {
                    if (selectedItems.contains(item))
                    {
                        return;
                    }
                }
                updateSelection(item, e.stateMask);
                
                focusItem = item;
                showItem(item);
            }
        }
        else if (cellSelectionEnabled && e.button == 1 && rowHeaderVisible && e.x <= rowHeaderWidth && e.y < headerHeight)
        {            
            //click on the top left corner means select everything
            selectAllCells();
            
            focusColumn = getColumn(new Point(rowHeaderWidth + 1,1));
            focusItem = getItem(getTopIndex());
        }
        else if (cellSelectionEnabled && e.button == 1 && columnHeadersVisible && e.y <= headerHeight)
        {
            //column cell selection
            GridColumn col = getColumn(new Point(e.x,e.y));
            
            if (col == null) return;
            
            if (col.getColumnGroup() != null && e.y < groupHeaderHeight)
                return;
            
            
            Vector cells = new Vector();
            
            getCells(col,cells);
            
            updateCellSelection(cells, e.stateMask, false, true);
            cellColumnSelectedOnLastMouseDown = (getCellSelectionCount() > 0);
            
            GridItem newFocusItem = getItem(0);
            
            while (newFocusItem != null && getSpanningColumn(newFocusItem, col) != null)
            {
                newFocusItem = getNextVisibleItem(newFocusItem);
            }

            if (newFocusItem != null)
            {
                focusColumn = col;
                focusItem = newFocusItem;
            }
           
            showColumn(col);
            redraw();
        }
        
    }

    /**
     * Mouse double click event handler.
     * 
     * @param e event
     */
    private void onMouseDoubleClick(MouseEvent e)
    {
        if (hoveringOnColumnResizer)
        {
            if (e.button == 1)
            {
                columnBeingResized.pack();
                resizingColumn = false;
                handleHoverOnColumnResizer(e.x, e.y);
            }
            return;
        }

        GridItem item = getItem(new Point(e.x, e.y));
        if (item != null)
        {
            Event newEvent = new Event();
            newEvent.item = item;

            notifyListeners(SWT.DefaultSelection, newEvent);
        }
    }

    /**
     * Mouse up handler.
     * 
     * @param e event
     */
    private void onMouseUp(MouseEvent e)
    {
        if (resizingColumn)
        {
            resizingColumn = false;
            handleHoverOnColumnResizer(e.x, e.y); // resets cursor if
            // necessary
            return;
        }

        if (pushingColumn)
        {
            pushingColumn = false;
            columnBeingPushed.getHeaderRenderer().setMouseDown(false);
            columnBeingPushed.getHeaderRenderer().setHover(false);
            redraw();
            if (pushingAndHovering)
            {
                columnBeingPushed.fireListeners();
            }

            return;
        }

        if (draggingColumn)
        {
            handleColumnDrop();
            return;
        }
        
        if (cellDragSelectionOccuring || cellRowDragSelectionOccuring || cellColumnDragSelectionOccuring)
        {
            cellDragSelectionOccuring = false;
            cellRowDragSelectionOccuring = false;
            cellColumnDragSelectionOccuring = false;
            setCursor(null);
            
            if (followupCellSelectionEventOwed)
            {
                notifyListeners(SWT.Selection, new Event());
                followupCellSelectionEventOwed = false;
            }
        }
    }

    /**
     * Mouse move event handler.
     * 
     * @param e event
     */
    private void onMouseMove(MouseEvent e)
    {
        if ((e.stateMask & SWT.BUTTON1) == 0)
        {
            handleHovering(e.x, e.y);
        }
        else
        {
            if (draggingColumn)
            {
                handleColumnDragging(e.x);
                return;
            }

            if (resizingColumn)
            {
                handleColumnResizerDragging(e.x);
                return;
            }
            if (pushingColumn)
            {
                handleColumnHeaderHoverWhilePushing(e.x, e.y);
                return;
            }
            if (cellSelectionEnabled)
            {                
                if (!cellDragSelectionOccuring && cellSelectedOnLastMouseDown)
                {
                    cellDragSelectionOccuring = true;
                    //XXX: make this user definable
                    setCursor(getDisplay().getSystemCursor(SWT.CURSOR_CROSS));
                    cellDragCTRL = ((e.stateMask & SWT.CTRL) != 0);
                    if (cellDragCTRL)
                    {
                        selectedCellsBeforeRangeSelect.clear();
                        selectedCellsBeforeRangeSelect.addAll(selectedCells);
                    }
                }
                if (!cellRowDragSelectionOccuring && cellRowSelectedOnLastMouseDown)
                {
                    cellRowDragSelectionOccuring = true;
                    setCursor(getDisplay().getSystemCursor(SWT.CURSOR_CROSS));
                    cellDragCTRL = ((e.stateMask & SWT.CTRL) != 0);
                    if (cellDragCTRL)
                    {
                        selectedCellsBeforeRangeSelect.clear();
                        selectedCellsBeforeRangeSelect.addAll(selectedCells);
                    }
                }
                
                if (!cellColumnDragSelectionOccuring && cellColumnSelectedOnLastMouseDown)
                {
                    cellColumnDragSelectionOccuring = true;
                    setCursor(getDisplay().getSystemCursor(SWT.CURSOR_CROSS));
                    cellDragCTRL = ((e.stateMask & SWT.CTRL) != 0);
                    if (cellDragCTRL)
                    {
                        selectedCellsBeforeRangeSelect.clear();
                        selectedCellsBeforeRangeSelect.addAll(selectedCells);
                    }
                }
                
                int ctrlFlag = (cellDragCTRL ? SWT.CTRL : SWT.NONE);
                
                if (cellDragSelectionOccuring && handleCellHover(e.x, e.y))
                {
                    GridColumn intentColumn = hoveringColumn;
                    GridItem intentItem = hoveringItem;
                    
                    if (hoveringItem == null)
                    {
                        if (e.y > headerHeight)
                        {
                            //then we must be hovering way to the bottom
                            intentItem = getPreviousVisibleItem(null);
                        }
                        else
                        {
                            intentItem = (GridItem)items.get(0);
                        }
                    }
                    
                    
                    if (hoveringColumn == null)
                    {                      
                        if (e.x > rowHeaderWidth)
                        {
                            //then we must be hovering way to the right
                            intentColumn = getVisibleColumn_DegradeLeft(intentItem,(GridColumn)displayOrderedColumns.get(displayOrderedColumns.size() - 1));
                        }
                        else
                        {
                            GridColumn firstCol = (GridColumn)displayOrderedColumns.get(0);
                            if (!firstCol.isVisible())
                            {
                                firstCol = getNextVisibleColumn(firstCol);
                            }
                            intentColumn = firstCol;
                        }
                    }
                    
                    showColumn(intentColumn);
                    showItem(intentItem);
                    updateCellSelection(new Point(indexOf(intentColumn),indexOf(intentItem)),ctrlFlag | SWT.SHIFT, true, false);
                }
                if (cellRowDragSelectionOccuring && handleCellHover(e.x, e.y))
                {
                    GridItem intentItem = hoveringItem;
                    
                    if (hoveringItem == null)
                    {
                        if (e.y > headerHeight)
                        {
                            //then we must be hovering way to the bottom
                            intentItem = getPreviousVisibleItem(null);
                        }
                        else
                        {
                            if (getTopIndex() > 0)
                            {
                                intentItem = getPreviousVisibleItem((GridItem)items.get(getTopIndex()));
                            }
                            else
                            {
                                intentItem = (GridItem)items.get(0);
                            }
                        }
                    }

                    Vector cells = new Vector();
                    
                    getCells(intentItem,focusItem,cells);
                                        
                    showItem(intentItem);
                    updateCellSelection(cells,ctrlFlag, true, false);
                }
                if (cellColumnDragSelectionOccuring && handleCellHover(e.x, e.y))
                {
                    GridColumn intentCol = hoveringColumn;
                    
                    if (intentCol == null)
                    {
                        if (e.y < rowHeaderWidth)
                        {
                            //TODO: get the first col to the left
                        }
                        else
                        {
                            //TODO: get the first col to the right
                        }
                    }
                    
                    if (intentCol == null) return;  //temporary
                    
                    GridColumn iterCol = intentCol;
                    
                    Vector newSelected = new Vector();
                    
                    boolean decreasing = (displayOrderedColumns.indexOf(iterCol) > displayOrderedColumns.indexOf(focusColumn));
                                      
                    do
                    {
                        getCells(iterCol, newSelected);
                                                
                        if (iterCol == focusColumn)
                        {
                            break;
                        }
                         
                        if (decreasing)
                        {
                            iterCol = getPreviousVisibleColumn(iterCol);
                        }
                        else
                        {
                            iterCol = getNextVisibleColumn(iterCol);
                        }
   
                    } while (true);                    
                    
                    updateCellSelection(newSelected, ctrlFlag, true, false);
                }
                
            }
        }
    }

    /**
     * Handles the assignment of the correct values to the hover* field
     * variables that let the painting code now what to paint as hovered.
     * 
     * @param x mouse x coordinate
     * @param y mouse y coordinate
     */
    private void handleHovering(int x, int y)
    {
        // TODO: need to clean up and refactor hover code

        if (columnHeadersVisible)
        {
            if (handleHoverOnColumnResizer(x, y))
            {
                if (hoveringItem != null || !hoveringDetail.equals("") || hoveringColumn != null
                    || hoveringColumnHeader != null || hoverColumnGroupHeader != null)
                {
                    hoveringItem = null;
                    hoveringDetail = "";
                    hoveringColumn = null;
                    hoveringColumnHeader = null;
                    hoverColumnGroupHeader = null;
                    redraw();
                }
                return;
            }
        }

        handleCellHover(x, y);
    }

    /**
     * Refreshes the hover* variables according to the mouse location and
     * current state of the table. This is useful is some method call, caused
     * the state of the table to change and therefore the hover effects may have
     * become out of date.
     */
    protected void refreshHoverState()
    {
        Point p = getDisplay().map(null, this, getDisplay().getCursorLocation());
        handleHovering(p.x, p.y);
    }

    /**
     * Mouse exit event handler.
     * 
     * @param e event
     */
    private void onMouseExit(MouseEvent e)
    {
        hoveringItem = null;
        hoveringDetail = "";
        hoveringColumn = null;
        redraw();
    }

    /**
     * Key down event handler.
     * 
     * @param e event
     */
    private void onKeyDown(Event e)
    {  
        
        int attemptExpandCollapse = 0;
        if ((e.character == '-' || (!cellSelectionEnabled && e.keyCode == SWT.ARROW_LEFT)) && focusItem.isExpanded())
        {
            attemptExpandCollapse = SWT.Collapse;
        }
        else if ((e.character == '+' || (!cellSelectionEnabled && e.keyCode == SWT.ARROW_RIGHT)) && !focusItem.isExpanded())
        {
            attemptExpandCollapse = SWT.Expand;
        }
               
        if (attemptExpandCollapse != 0 && focusItem != null && focusItem.hasChildren())
        {
            int performExpandCollapse = 0;
            
            if (cellSelectionEnabled && focusColumn != null && focusColumn.isTree())
            {
                performExpandCollapse = attemptExpandCollapse;
            }
            else if (!cellSelectionEnabled)
            {
                performExpandCollapse = attemptExpandCollapse;
            }

            if (performExpandCollapse == SWT.Expand)
            {
                focusItem.setExpanded(true);
                focusItem.fireEvent(SWT.Expand);
                return;
            }
            if (performExpandCollapse == SWT.Collapse)
            {
                focusItem.setExpanded(false);
                focusItem.fireEvent(SWT.Collapse);
                return;
            }
        }
        
        if (e.character == ' ')
        {
            handleSpaceBarDown();
        }
        
        
        GridItem newSelection = null;
        GridColumn newColumnFocus = null;
        
        //These two variables are used because the key navigation when the shift key is down is
        //based, not off the focus item/column, but rather off the implied focus (i.e. where the 
        //keyboard has extended focus to).  
        GridItem impliedFocusItem = focusItem;
        GridColumn impliedFocusColumn = focusColumn;
        
        if (cellSelectionEnabled && e.stateMask == SWT.SHIFT)
        {
            if (shiftSelectionAnchorColumn != null)
            {
                impliedFocusItem = shiftSelectionAnchorItem;
                impliedFocusColumn = shiftSelectionAnchorColumn;
            }
        }
        
        switch (e.keyCode)
        {
            case SWT.ARROW_RIGHT :
                if (impliedFocusItem != null && impliedFocusColumn != null)
                {                    
                    newSelection = impliedFocusItem;                    
                    
                    int index = displayOrderedColumns.indexOf(impliedFocusColumn);
                    
                    int jumpAhead = impliedFocusItem.getColumnSpan(indexOf(impliedFocusColumn));
                    
                    jumpAhead ++;
                    
                    while (jumpAhead > 0)
                    {
                        index ++;
                        if (index < displayOrderedColumns.size())
                        {
                            if (((GridColumn)displayOrderedColumns.get(index)).isVisible())
                                jumpAhead --;
                        }
                        else
                        {
                            break;
                        }
                    }
                    
                    if (index < displayOrderedColumns.size())
                    {
                        newColumnFocus = (GridColumn)displayOrderedColumns.get(index);                    
                    }
                    else
                    {
                        newColumnFocus = impliedFocusColumn;
                    }                    
                }      
                intendedFocusColumn = newColumnFocus;
                break;            
            case SWT.ARROW_LEFT :
                if (impliedFocusItem != null && impliedFocusColumn != null)
                {                    
                    newSelection = impliedFocusItem;
                    
                    int index = displayOrderedColumns.indexOf(impliedFocusColumn);
                    
                    if (index != 0)
                    {
                        newColumnFocus = (GridColumn)displayOrderedColumns.get(index -1);
                        
                        newColumnFocus = getVisibleColumn_DegradeLeft(impliedFocusItem, newColumnFocus);
                    }
                    else
                    {
                        newColumnFocus = impliedFocusColumn;
                    }                    
                }
                intendedFocusColumn = newColumnFocus;
                break;
            case SWT.ARROW_UP :
                if (impliedFocusItem != null)
                {
                    newSelection = getPreviousVisibleItem(impliedFocusItem); 
                }
                
                if (impliedFocusColumn != null)
                {
                    if (newSelection != null)
                    {
                        newColumnFocus = getVisibleColumn_DegradeLeft(newSelection, intendedFocusColumn);
                    }
                    else
                    {
                        newColumnFocus = impliedFocusColumn;
                    }
                }
                
                break;
            case SWT.ARROW_DOWN :
                if (impliedFocusItem != null)
                {
                    newSelection = getNextVisibleItem(impliedFocusItem); 
                }
                else
                {
                    if (items.size() > 0)
                    {
                        newSelection = (GridItem)items.get(0);
                    }                  
                }
                
                if (impliedFocusColumn != null)
                {
                    if (newSelection != null)
                    {
                        newColumnFocus = getVisibleColumn_DegradeLeft(newSelection, intendedFocusColumn);
                    }
                    else
                    {
                        newColumnFocus = impliedFocusColumn;
                    }
                }                
                break;
            case SWT.HOME :
                
                if (!cellSelectionEnabled)
                {
                    if (items.size() > 0)
                    {
                        newSelection = (GridItem)items.get(0);
                    }
                }
                else
                {
                    newSelection = impliedFocusItem;
                    newColumnFocus = getVisibleColumn_DegradeRight(newSelection,(GridColumn)displayOrderedColumns.get(0));
                }

                break;                
            case SWT.END :
                if (!cellSelectionEnabled)
                {
                    if (items.size() > 0)
                    {
                        newSelection = getPreviousVisibleItem(null);
                    } 
                }
                else
                {
                    newSelection = impliedFocusItem;
                    newColumnFocus = getVisibleColumn_DegradeLeft(newSelection,(GridColumn)displayOrderedColumns.get(displayOrderedColumns.size() - 1));                    
                }
                
                break;
            case SWT.PAGE_UP :
                int topIndex = getTopIndex();

                newSelection = (GridItem)items.get(topIndex);
                
                if (focusItem == newSelection)
                {
                    int pageSize = vScroll.getPageIncrement();
                    for (int i = 0; i < pageSize - 1; i++)
                    {
                        GridItem prevItem = getPreviousVisibleItem(newSelection);
                        if (prevItem == null)
                        {
                            break;
                        }
                        else
                        {
                            newSelection = prevItem;
                        }
                    }
                }
                
                newColumnFocus = focusColumn;
                break;
            case SWT.PAGE_DOWN :
                int bottomIndex = getTopIndex();
                
                newSelection = (GridItem)items.get(bottomIndex);

                int potentialRows = getPotentiallyPaintedRows() - 1;
                while (potentialRows > 0)
                {
                    GridItem nextItem = getNextVisibleItem(newSelection);
                    if (nextItem == null)
                    {
                        break;
                    }
                    else
                    {
                        newSelection = nextItem;
                        potentialRows--;
                    }
                }
                if (focusItem == newSelection)
                {
                    for (int i = 0; i < vScroll.getPageIncrement() - 1; i++)
                    {
                        GridItem nextItem = getNextVisibleItem(newSelection);
                        if (nextItem == null)
                        {
                            break;
                        }
                        else
                        {
                            newSelection = nextItem;
                        }
                    }
                }
                
                newColumnFocus = focusColumn;
                break;
            default :
                break;
        }
        
        if (newSelection == null)
        {
            return;
        }

        if (cellSelectionEnabled)
        {  
            if (e.stateMask != SWT.SHIFT)
                focusColumn = newColumnFocus;
            showColumn(newColumnFocus);
            
            if (e.stateMask != SWT.SHIFT)
                focusItem = newSelection;
            showItem(newSelection);
            
            if (e.stateMask != SWT.CTRL)
                updateCellSelection(new Point(indexOf(newColumnFocus),indexOf(newSelection)),e.stateMask, false, false);
            
            redraw();
        }
        else
        {            
            if (selectionType == SWT.SINGLE || e.stateMask != SWT.CTRL)
            {
                updateSelection(newSelection, e.stateMask);
            }
            
            focusItem = newSelection;
            showItem(newSelection);            
            redraw();
        }
    }
    
    private void handleSpaceBarDown()
    {        
        if (selectionEnabled && !cellSelectionEnabled && !selectedItems.contains(focusItem))
        {
            selectedItems.add(focusItem);
            redraw();
            fireSelectionListeners(focusItem);
        }
        
        if (!cellSelectionEnabled)
        {
            boolean checkFirstCol = false;
            boolean first = true;
            
            for (Iterator iter = columns.iterator(); iter.hasNext();)
            {
                GridColumn col = (GridColumn)iter.next();
                
                if (first)
                {
                    if (!col.isCheck()) break;
                    
                    first = false;
                    checkFirstCol = true;
                }
                else
                {
                    if (col.isCheck())
                    {
                        checkFirstCol = false;
                        break;
                    }
                }
            }
         
            if (checkFirstCol)
            {
                focusItem.setChecked(!focusItem.getChecked());
                redraw();
                focusItem.fireCheckEvent(0);            
            }
        }
    }

    /**
     * Resize event handler.
     */
    private void onResize()
    {
        scrollValuesObsolete = true;
    }

    /**
     * Scrollbar selection event handler.
     */
    private void onScrollSelection()
    {
        redraw(getClientArea().x, getClientArea().y, getClientArea().width, getClientArea().height,
               false);
    }

    /**
     * Returns the intersection of the given column and given item.
     * 
     * @param column column
     * @param item item
     * @return x,y of top left corner of the cell
     */
    protected Point getOrigin(GridColumn column, GridItem item)
    {
        int x = 0;

        if (rowHeaderVisible)
        {
            x += rowHeaderWidth;
        }

        x -= getHScrollSelectionInPixels();

        for (Iterator colIterIterator = displayOrderedColumns.iterator(); colIterIterator.hasNext(); )
        {
            GridColumn colIter = (GridColumn) colIterIterator.next();

            if (colIter == column)
            {
                break;
            }

            if (colIter.isVisible())
            {
                x += colIter.getWidth();
            }
        }

        int y = 0;
        if (item != null)
        {
            y = 1;
            if (columnHeadersVisible)
            {
                y = +headerHeight;
            }

            for (Iterator itemIterIterator = items.iterator(); itemIterIterator.hasNext(); )
            {
                GridItem itemIter = (GridItem) itemIterIterator.next();
                if (itemIter == item)
                {
                    break;
                }

                if (itemIter.isVisible())
                {
                    y += rowHeight + 1;
                }
            }

            if (vScroll.getVisible())
            {
                y -= vScroll.getSelection() * (rowHeight + 1);
            }
        }
        else
        {
            if (column.getColumnGroup() != null)
            {
                y += groupHeaderHeight;
            }
        }

        return new Point(x, y);
    }

    /**
     * Determines (which cell/if a cell) has been clicked (mouse down really)
     * and notifies the appropriate renderer. Returns true when a cell has
     * responded to this event in some way and prevents the event from
     * triggering an action further down the chain (like a selection).
     * 
     * @param x mouse x
     * @param y mouse y
     * @return true if this event has been consumed.
     */
    private boolean handleCellClick(int x, int y)
    {

        // if(!isTree)
        // return false;

        GridColumn col = getColumn(new Point(x, y));
        if (col == null)
        {
            return false;
        }

        GridItem item = getItem(new Point(x, y));

        if (item == null)
        {
            return false;
        }

        col.getCellRenderer().setBounds(item.getBounds(indexOf(col)));
        return col.getCellRenderer().notify(IInternalWidget.LeftMouseButtonDown, new Point(x, y), item);

    }

    /**
     * Sets the hovering variables (hoverItem,hoveringColumn) as well as
     * hoverDetail by talking to the cell renderers. Triggers a redraw if
     * necessary.
     * 
     * @param x mouse x
     * @param y mouse y
     * @return true if a new section of the table is now being hovered
     */
    private boolean handleCellHover(int x, int y)
    {

        String detail = "";

        GridColumn col = getColumn(new Point(x, y));
        GridItem item = getItem(new Point(x, y));

        GridColumnGroup hoverColGroup = null;
        GridColumn hoverColHeader = null;

        if (col != null)
        {
            if (item != null)
            {
                col.getCellRenderer().setBounds(item.getBounds(indexOf(col)));

                if (col.getCellRenderer().notify(IInternalWidget.MouseMove, new Point(x, y), item))
                {
                    detail = col.getCellRenderer().getHoverDetail();
                }

            }
            else
            {
                if (y < headerHeight)
                {
                    if (columnGroups.length != 0 && y < groupHeaderHeight
                        && col.getColumnGroup() != null)
                    {
                        hoverColGroup = col.getColumnGroup();
                        hoverColGroup.getHeaderRenderer().setBounds(hoverColGroup.getBounds());
                        if (hoverColGroup.getHeaderRenderer()
                            .notify(IInternalWidget.MouseMove, new Point(x, y), hoverColGroup))
                        {
                            detail = hoverColGroup.getHeaderRenderer().getHoverDetail();
                        }
                    }
                    else
                    {
                        // on col header
                        hoverColHeader = col;

                        col.getHeaderRenderer().setBounds(col.getBounds());
                        if (col.getHeaderRenderer().notify(IInternalWidget.MouseMove, new Point(x, y),
                                                           col))
                        {
                            detail = col.getHeaderRenderer().getHoverDetail();
                        }
                    }
                }
            }
        }

        if (hoveringItem != item || !hoveringDetail.equals(detail) || hoveringColumn != col
            || hoverColGroup != hoverColumnGroupHeader || hoverColHeader != hoveringColumnHeader)
        {
            hoveringItem = item;
            hoveringDetail = detail;
            hoveringColumn = col;
            hoveringColumnHeader = hoverColHeader;
            hoverColumnGroupHeader = hoverColGroup;
            redraw();
            
            return true;
        }
        
        return false;
    }

    /**
     * Marks the scroll values obsolete so they will be recalculated.
     */
    protected void setScrollValuesObsolete()
    {
        this.scrollValuesObsolete = true;
        redraw();
    }

    /**
     * Returns the row height.
     * 
     * @return Returns the rowHeight.
     */
    int getRowHeight()
    {
        return rowHeight;
    }

    /**
     * Inserts a new column into the table.
     * 
     * @param column new column
     * @param index index to insert new column
     * @return current number of columns
     */
    int newColumn(GridColumn column, int index)
    {

        if (index == -1)
        {
            columns.add(column);
            displayOrderedColumns.add(column);
        }
        else
        {
            columns.add(index, column);
            displayOrderedColumns.add(index, column);
        }

        GC gc = new GC(this);
        computeHeaderHeight(gc);
        gc.dispose();

        updatePrimaryCheckColumn();
        
        scrollValuesObsolete = true;
        redraw();

        return columns.size() - 1;
    }

    /**
     * Removes the given column from the table.
     * 
     * @param column column to remove
     */
    void removeColumn(GridColumn column)
    {
        columns.remove(column);
        displayOrderedColumns.remove(column);
        
        updatePrimaryCheckColumn();
        
        scrollValuesObsolete = true;
        redraw();
    }
    
    /**
     * Manages the setting of the checkbox column when the SWT.CHECK style was given to the 
     * table.  This method will ensure that the first column of the table always has a checkbox
     * when SWT.CHECK is given to the table.
     */
    private void updatePrimaryCheckColumn()
    {
        if ((getStyle() & SWT.CHECK) == SWT.CHECK)
        {
            boolean firstCol = true;
            
            for (Iterator iter = columns.iterator(); iter.hasNext();)
            {
                GridColumn col = (GridColumn)iter.next();
                col.setTableCheck(firstCol);
                firstCol = false;
            }
        }
    }

    /**
     * Creates the new item at the given index. Only called from GridItem
     * constructor.
     * 
     * @param item new item
     * @param index index to insert the item at
     * @return the index where the item was insert
     */
    int newItem(GridItem item, int index)
    {
        int row = 0;

        if (index == -1 || index >= items.size())
        {
            items.add(item);
            row = items.size() - 1;
        }
        else
        {
            items.add(index, item);
            row = index;
        }

        if (items.size() == 1)
        {
            GC gc = new GC(this);

            rowHeight = computeRowHeight(gc);

            lastRowHeaderWidthCalculationAt = INITIAL_ROW_HEADER_SIZING_VALUE;
            rowHeaderWidth = rowHeaderRenderer.computeSize(gc, SWT.DEFAULT, SWT.DEFAULT,
                                                           new Integer(lastRowHeaderWidthCalculationAt)).x;

            gc.dispose();
        }
        else if (items.size() > lastRowHeaderWidthCalculationAt)
        {
            GC gc = new GC(this);

            lastRowHeaderWidthCalculationAt = lastRowHeaderWidthCalculationAt
                                              * ROW_HEADER_SIZING_MULTIPLIER;
            rowHeaderWidth = rowHeaderRenderer.computeSize(gc, SWT.DEFAULT, SWT.DEFAULT,
                                                           new Integer(lastRowHeaderWidthCalculationAt)).x;
            gc.dispose();
        }

        scrollValuesObsolete = true;

        currentVisibleItems++;

        redraw();

        return row;
    }

    /**
     * Removes the given item from the table. This method is only called from
     * the item's dispose method.
     * 
     * @param item item to remove
     */
    void removeItem(GridItem item)
    {
        items.remove(item);
        if (selectedItems.contains(item))
        {
            selectedItems.remove(item);
        }

        if (focusItem == item)
        {
            focusItem = null;
        }

        scrollValuesObsolete = true;
        redraw();
    }

    /**
     * Creates the given column group at the given index. This method is only
     * called from the {@code GridColumnGroup}'s constructor.
     * 
     * @param group group to add.
     */
    void newColumnGroup(GridColumnGroup group)
    {
        GridColumnGroup[] newColumnGroups = new GridColumnGroup[columnGroups.length + 1];
        System.arraycopy(columnGroups, 0, newColumnGroups, 0, columnGroups.length);
        newColumnGroups[newColumnGroups.length - 1] = group;
        columnGroups = newColumnGroups;

        // if we just added the first col group, then we need to up the row
        // height
        if (columnGroups.length == 1)
        {
            GC gc = new GC(this);
            computeHeaderHeight(gc);
            gc.dispose();
        }

        scrollValuesObsolete = true;
        redraw();
    }

    /**
     * Removes the given column group from the table. This method is only called
     * from the {@code GridColumnGroup}'s dispose method.
     * 
     * @param group group to remove.
     */
    void removeColumnGroup(GridColumnGroup group)
    {
        GridColumnGroup[] newColumnGroups = new GridColumnGroup[columnGroups.length - 1];
        int newIndex = 0;
        for (int i = 0; i < columnGroups.length; i++)
        {
            if (columnGroups[i] != group)
            {
                newColumnGroups[newIndex] = columnGroups[i];
                newIndex++;
            }
        }
        columnGroups = newColumnGroups;

        if (columnGroups.length == 0)
        {
            GC gc = new GC(this);
            computeHeaderHeight(gc);
            gc.dispose();
        }

        scrollValuesObsolete = true;
        redraw();
    }

    /**
     * Updates the cached number of visible items by the given amount.
     * 
     * @param amount amount to update cached total
     */
    void updateVisibleItems(int amount)
    {
        currentVisibleItems += amount;
    }

    /**
     * Returns the current item in focus.
     * 
     * @return item in focus or {@code null}.
     */
    GridItem getFocusItem()
    {
        return focusItem;
    }

    /**
     * Sets the given item as the focused item.
     * 
     * @param newFocus item to get focus.
     */
    void setFocusItem(GridItem newFocus)
    {
        focusItem = newFocus;
        return;
    }

    /**
     * Returns an array of the columns in their display order.
     * 
     * @return columns in display order
     */
    GridColumn[] getColumnsInOrder()
    {
        checkWidget();
        return (GridColumn[])displayOrderedColumns.toArray(new GridColumn[columns.size()]);
    }

    /**
     * Returns true if the table is set to horizontally scroll column-by-column
     * rather than pixel-by-pixel.
     * 
     * @return true if the table is scrolled horizontally by column
     */
    public boolean getColumnScrolling()
    {
        checkWidget();
        return columnScrolling;
    }

    /**
     * Sets the table scrolling method to either scroll column-by-column (true)
     * or pixel-by-pixel (false).
     * 
     * @param columnScrolling true to horizontally scroll by column, false to
     * scroll by pixel
     */
    public void setColumnScrolling(boolean columnScrolling)
    {
        if (rowHeaderVisible && !columnScrolling)
        {
            return;
        }
        this.columnScrolling = columnScrolling;
        scrollValuesObsolete = true;
        redraw();        
    }
    
    /**
     * Returns the first visible column that is not spanned by any other column that is either the 
     * given column or any of the columns displaying to the left of the given column.  If the
     * given column and subsequent columns to the right are either not visible or spanned, this 
     * method will return null.
     * 
     * @param item
     * @param col
     * @return
     */
    private GridColumn getVisibleColumn_DegradeLeft(GridItem item, GridColumn col)
    {
        int index = displayOrderedColumns.indexOf(col);
        
        GridColumn prevCol = col;
        
        int i = 0;
        while (!prevCol.isVisible())
        {
            i ++;
            if (index - i < 0)
                return null;
            
            prevCol = (GridColumn)displayOrderedColumns.get(index - i);
        }
        
        index = displayOrderedColumns.indexOf(prevCol);
        
        for (int j = 0; j < index; j++)
        {
            GridColumn tempCol = (GridColumn)displayOrderedColumns.get(j);
            
            if (!tempCol.isVisible())
            {
                continue;
            }
            
            if (item.getColumnSpan(indexOf(tempCol)) >= index - j)
            {
                prevCol = tempCol;
                break;
            }            
        }
        
        return prevCol;
    }
    
    /**
     * Returns the first visible column that is not spanned by any other column that is either the 
     * given column or any of the columns displaying to the right of the given column.  If the
     * given column and subsequent columns to the right are either not visible or spanned, this 
     * method will return null.
     * 
     * @param item
     * @param col
     * @return
     */
    private GridColumn getVisibleColumn_DegradeRight(GridItem item, GridColumn col)
    {
        int index = displayOrderedColumns.indexOf(col);
        
        int i = 0;
        GridColumn nextCol = col;
        while (!nextCol.isVisible())
        {
            i ++;
            if (index + i == displayOrderedColumns.size())
                return null;
            
            nextCol = (GridColumn)displayOrderedColumns.get(index + i);
        }
        
        
        index = displayOrderedColumns.indexOf(nextCol);
        int startIndex = index;
        
        while (index > 0)
        {

            index --;
            GridColumn prevCol = (GridColumn)displayOrderedColumns.get(index);
            
            if (item.getColumnSpan(indexOf(prevCol)) >= startIndex - index)
            {
                if (startIndex == displayOrderedColumns.size() - 1)
                {
                    return null;
                }
                else
                {
                    return getVisibleColumn_DegradeRight(item, (GridColumn)displayOrderedColumns.get(startIndex + 1));
                }
            }
            
        }
        
        return nextCol;
    }

    /**
     * Returns true if the cells are selectable in the reciever.
     * 
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
     * Sets whether cells are selectable in the receiver.
     * 
     * @param cellSelectionEnabled the cellSelectionEnabled to set
     * 
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setCellSelectionEnabled(boolean cellSelection)
    {
        checkWidget();
        if (!cellSelection)
        {
            selectedCells.clear();
            redraw();
        }
        else
        {
            selectedItems.clear();
            redraw();
        }
        
        this.cellSelectionEnabled = cellSelection;
    }
    
    /**
     * Deselects the given cell in the receiver.  If the given cell is already
     * deselected it remains deselected.  Invalid cells are ignored.
     * 
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_NULL_ARGUMENT - if the cell is null</li>
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void deselectCell(Point cell)
    {
        checkWidget();
        
        if (cell == null)
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        
        selectedCells.remove(cell);
        redraw();
    }
    
    /**
     * Deselects the given cells.  Invalid cells are ignored.
     * 
     * @param cells the cells to deselect.
     * 
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_NULL_ARGUMENT - if the set of cells or any cell is null</li>
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void deselectCells(Point[] cells)
    {
        checkWidget();
        
        if (cells == null)
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        
        for (int i = 0; i < cells.length; i++)
        {
            if (cells[i] == null)
                SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        
        for (int i = 0; i < cells.length; i++)
        {
            selectedCells.remove(cells[i]);
        }
        
        redraw();
    }
    
    /**
     * Deselects all selected cells in the receiver.
     * 
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void deselectAllCells()
    {
        checkWidget();
        selectedCells.clear();
        redraw();
    }
    
    /**
     * Selects the given cell.  Invalid cells are ignored.
     * 
     * @param cell point whose x values is a column index and y value is an item index
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_NULL_ARGUMENT - if the item is null</li> 
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void selectCell(Point cell)
    {
        checkWidget();
        
        if (!cellSelectionEnabled) return;
        
        if (cell == null)
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        
        addToCellSelection(cell);
        redraw();
    }
    
    /**
     * Selects the given cells.  Invalid cells are ignored.
     * 
     * @param cells an arry of points whose x value is a column index and y value is an item index
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_NULL_ARGUMENT - if the set of cells or an individual cell is null</li> 
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void selectCells(Point[] cells)
    {
        checkWidget();
        
        if (!cellSelectionEnabled) return;
        
        if (cells == null)
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        
        for (int i = 0; i < cells.length; i++)
        {
            if (cells[i] == null)
                SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        
        for (int i = 0; i < cells.length; i++)
        {
            addToCellSelection(cells[i]);
        }
        redraw();
    }
    
    /**
     * Selects all cells in the receiver.
     * 
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void selectAllCells()
    {
        checkWidget();
        
        if (!cellSelectionEnabled) return;
        
        if (columns.size() == 0)
            return;
        
        int index = 0;
        GridColumn column = (GridColumn)displayOrderedColumns.get(index);
        
        while (!column.isVisible())
        {
            index ++;
            
            if (index >= columns.size())
                return;
            
            column = (GridColumn)displayOrderedColumns.get(index);                 
        }            
        
        GridColumn oldFocusColumn = focusColumn;
        GridItem oldFocusItem = focusItem;
        
        focusColumn = column;        
        focusItem = (GridItem)items.get(0);
        
        GridItem lastItem = getPreviousVisibleItem(null);
        GridColumn lastCol = getVisibleColumn_DegradeLeft(lastItem,(GridColumn)displayOrderedColumns.get(displayOrderedColumns.size() -1));
        
        updateCellSelection(new Point(indexOf(lastCol),indexOf(lastItem)),SWT.SHIFT, true, false);
        
        focusColumn = oldFocusColumn;
        focusItem = oldFocusItem;
        
        redraw();
    }
    
    /**
     * Selects the selection to the given cell.  The existing selection is cleared before
     * selecting the given cell.
     * 
     * @param cell point whose x values is a column index and y value is an item index
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_NULL_ARGUMENT - if the item is null</li> 
     * <li>ERROR_INVALID_ARGUMENT - if the cell is invalid</li>
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setCellSelection(Point cell)
    {
        checkWidget();
        
        if (!cellSelectionEnabled) return;
        
        if (cell == null)
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        
        if (!isValidCell(cell))
            SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        
        selectedCells.clear();
        addToCellSelection(cell);
        redraw();
    }
    
    /**
     * Selects the selection to the given set of cell.  The existing selection is cleared before
     * selecting the given cells.
     * 
     * @param cells point array whose x values is a column index and y value is an item index
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_NULL_ARGUMENT - if the cell array or an individual cell is null</li> 
     * <li>ERROR_INVALID_ARGUMENT - if the a cell is invalid</li>
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setCellSelection(Point[] cells)
    {
        checkWidget();
        
        if (!cellSelectionEnabled) return;
        
        if (cells == null)
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        
        for (int i = 0; i < cells.length; i++)
        {
            if (cells[i] == null)
                SWT.error(SWT.ERROR_NULL_ARGUMENT);
            
            if (!isValidCell(cells[i]))
                SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        }
        
        selectedCells.clear();
        for (int i = 0; i < cells.length; i++)
        {
            addToCellSelection(cells[i]);
        }
        redraw();
    }
    
    /**
     * Returns an array of cells that are currently selected in the
     * receiver. The order of the items is unspecified. An empty array indicates
     * that no items are selected.
     * <p>
     * Note: This is not the actual structure used by the receiver to maintain
     * its selection, so modifying the array will not affect the receiver.
     * </p>
     * 
     * @return an array representing the cell selection
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public Point[] getCellSelection()
    {
        checkWidget();        
        return (Point[])selectedCells.toArray(new Point[selectedCells.size()]);
    }
    
    
    
    
    GridColumn getFocusColumn()
    {
        return focusColumn;
    }
    
    void updateColumnFocus()
    {
        if (!focusColumn.isVisible())
        {
            int index = displayOrderedColumns.indexOf(focusColumn);
            if (index > 0)
            {
                GridColumn prev = (GridColumn)displayOrderedColumns.get(index - 1);
                prev = getVisibleColumn_DegradeLeft(focusItem,prev);
                if (prev == null)
                {
                    prev = getVisibleColumn_DegradeRight(focusItem,focusColumn);
                }
                focusColumn = prev;
            }
            else
            {
                focusColumn = getVisibleColumn_DegradeRight(focusItem,focusColumn);
            }
        }
    }
    
    private void getCells(GridColumn col, Vector cells)
    {        
        int colIndex = indexOf(col);
        
        int columnAtPosition = 0;
        for (Iterator iter = displayOrderedColumns.iterator(); iter.hasNext();)
        {
            GridColumn nextCol = (GridColumn)iter.next();                    
            if (!nextCol.isVisible()) continue;
            
            if (nextCol == col) break;
            
            columnAtPosition ++;
        }           
        
        
        GridItem item = null;
        if (getItemCount() > 0)
            item = getItem(0);    
        
        while (item != null)
        {
            //is cell spanned
            int position = -1;
            boolean spanned = false;
            for (Iterator iter = displayOrderedColumns.iterator(); iter.hasNext();)
            {
                GridColumn nextCol = (GridColumn)iter.next();                    
                if (!nextCol.isVisible()) continue;
                
                if (nextCol == col) break;                    
                
                int span = item.getColumnSpan(indexOf(nextCol));
                
                if (position + span >= columnAtPosition){
                    spanned = true;
                    break;
                }
            }
            
            if (!spanned && item.getColumnSpan(colIndex) == 0)
            {
                cells.add(new Point(colIndex,indexOf(item)));
            }
            
            item = getNextVisibleItem(item);
        }            
    }
    
    private void getCells(GridItem item, Vector cells)
    {
        int itemIndex = indexOf(item);
        
        int span = 0;
        
        for (Iterator iter = displayOrderedColumns.iterator(); iter.hasNext();)
        {
            GridColumn nextCol = (GridColumn)iter.next();   
            
            if (span > 0)
            {
                span --;
                continue;
            }
            
            if (!nextCol.isVisible()) continue;                                       
            
            span = item.getColumnSpan(indexOf(nextCol));
            
            cells.add(new Point(indexOf(nextCol),itemIndex));
        }
    } 
        

    private void getCells(GridItem fromItem, GridItem toItem, Vector cells)
    {
        boolean descending = (indexOf(fromItem) < indexOf(toItem));
        
        GridItem iterItem = toItem;
        
        do
        {
            getCells(iterItem,cells);
            
            if (iterItem == fromItem) break;
            
            if (descending)
            {
                iterItem = getPreviousVisibleItem(iterItem);
            }
            else
            {
                iterItem = getNextVisibleItem(iterItem);
            }
        } while (true);
    }
    
    private int blend(int v1, int v2, int ratio) {
        return (ratio*v1 + (100-ratio)*v2)/100;
    }
    
    private RGB blend(RGB c1, RGB c2, int ratio) {
        int r = blend(c1.red, c2.red, ratio);
        int g = blend(c1.green, c2.green, ratio);
        int b = blend(c1.blue, c2.blue, ratio);
        return new RGB(r, g, b);
    }
    
    /**
     * Returns a point whose x and y values are the to and from column indexes of the new selection
     * range inclusive of all spanned columns.
     * 
     * @param fromItem
     * @param fromColumn
     * @param toItem
     * @param toColumn
     * @return
     */
    private Point getSelectionRange(GridItem fromItem, GridColumn fromColumn, GridItem toItem, GridColumn toColumn)
    {
        if (displayOrderedColumns.indexOf(fromColumn) > displayOrderedColumns.indexOf(toColumn))
        {
            GridColumn temp = fromColumn;
            fromColumn = toColumn;
            toColumn = temp;
        }
        
        if (indexOf(fromItem) > indexOf(toItem))
        {
            GridItem temp = fromItem;
            fromItem = toItem;
            toItem = temp;
        }
        
        boolean firstTime = true;
        GridItem iterItem = fromItem;
        
        int fromIndex = indexOf(fromColumn);
        int toIndex = indexOf(toColumn);
        
        do
        {
            if (!firstTime)
            {
                iterItem = getNextVisibleItem(iterItem);
            }
            else
            {
                firstTime = false;
            }
            
            Point cols = getRowSelectionRange(iterItem, fromColumn, toColumn);
            
            //check and see if column spanning means that the range increased
            if (cols.x != fromIndex || cols.y != toIndex)
            {
                GridColumn newFrom = getColumn(cols.x);
                GridColumn newTo = getColumn(cols.y);
                
                //Unfortunately we have to start all over again from the top with the new range
                return getSelectionRange(fromItem, newFrom, toItem, newTo);                
            }
        } while (iterItem != toItem);
        
        return new Point(indexOf(fromColumn),indexOf(toColumn));
    }
    
    /**
     * Returns a point whose x and y value are the to and from column indexes of the new selection 
     * range inclusive of all spanned columns.
     * 
     * @param item
     * @param fromColumn
     * @param toColumn
     * @return
     */
    private Point getRowSelectionRange(GridItem item, GridColumn fromColumn, GridColumn toColumn)
    {                
        int newFrom = indexOf(fromColumn);
        int newTo = indexOf(toColumn);
        
        int span = 0;
        int spanningColIndex = -1;
        boolean spanningBeyondToCol = false;
        
        for (Iterator iter = displayOrderedColumns.iterator(); iter.hasNext();)
        {
            GridColumn col = (GridColumn)iter.next();
                        
            if (!col.isVisible())
            {
                if (span > 0) span --;
                continue;
            }
                
            if (span > 0)
            {
                if (col == fromColumn)
                {
                    newFrom = spanningColIndex;
                }
                else if (col == toColumn && span > 1)
                {
                    spanningBeyondToCol = true;
                }
                
                span --;
                
                if (spanningBeyondToCol && span == 0)
                {
                    newTo = indexOf(col);
                    break;
                }
            }
            else
            {
                int index = indexOf(col);
                span = item.getColumnSpan(index);
                if (span > 0) spanningColIndex = index; 
                
                if (col == toColumn && span > 0)
                    spanningBeyondToCol = true;
            }
           
                
            if (col == toColumn && !spanningBeyondToCol)
                break;
            
        }

        return new Point(newFrom,newTo);
    }
    
    /**
     * Returns the column which is spanning the given column for the given item or null if it is not
     * being spanned.
     * 
     * @param item
     * @param column
     * @return
     */
    private GridColumn getSpanningColumn(GridItem item, GridColumn column)
    {
        int span = 0;
        GridColumn spanningCol = null;
        
        for (Iterator iter = displayOrderedColumns.iterator(); iter.hasNext();)
        {
            GridColumn col = (GridColumn)iter.next();
                       
            if (col == column)
            {
                return spanningCol;
            }
            
            if (span > 0)
            {
                span --;
                if (span == 0) spanningCol = null;
            }
            else
            {
                int index = indexOf(col);
                span = item.getColumnSpan(index);
                
                if (span > 0) spanningCol = col;
            }
        }
        return spanningCol;
    }
    
    /**
     * Returns true if the given cell's x and y values are valid column and
     * item indexes respectively.
     * 
     * @param cell
     * @return
     */
    private boolean isValidCell(Point cell)
    {
        if (cell.x < 0 || cell.x >= columns.size())
            return false;
        
        if (cell.y < 0 || cell.y >= items.size())
            return false;
        
        return true;
    }
}


