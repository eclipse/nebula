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

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p> 
 * Instances of this class represent a selectable user interface object that
 * represents an item in a grid.
 * <p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * 
 * @author chris.gross@us.ibm.com
 */
public class GridItem extends Item
{
    /**
     * List of background colors for each column.
     */
    private ArrayList backgrounds = new ArrayList();

    /**
     * Lists of check states for each column.
     */
    private ArrayList checks = new ArrayList();

    /**
     * Lists of checkable states for each column.
     */
    private ArrayList checkable = new ArrayList();
    
    /**
     * List of children.
     */
    private ArrayList children = new ArrayList();

    /**
     * List of column spaning.
     */
    private ArrayList columnSpans = new ArrayList();

    /**
     * Default background color.
     */
    private Color defaultBackground;

    /**
     * Default font.
     */
    private Font defaultFont;

    /**
     * Default foreground color.
     */
    private Color defaultForeground;

    /**
     * Is expanded?
     */
    private boolean expanded = false;

    /**
     * Lists of fonts for each column.
     */
    private ArrayList fonts = new ArrayList();

    /**
     * List of foreground colors for each column.
     */
    private ArrayList foregrounds = new ArrayList();

    /**
     * Lists of grayed (3rd check state) for each column.
     */
    private ArrayList grayeds = new ArrayList();

    /**
     * True if has children.
     */
    private boolean hasChildren = false;

    /**
     * List of images for each column.
     */
    private ArrayList images = new ArrayList();

    /**
     * Level of item in a tree.
     */
    private int level = 0;

    /**
     * Parent grid instance.
     */
    private Grid parent;

    /**
     * Parent item (if a child item).
     */
    private GridItem parentItem;

    /**
     * List of text for each column.
     */
    private ArrayList texts = new ArrayList();

    /**
     * Is visible?
     */
    private boolean visible = true;

    /**
     * Row header text.
     */
    private String headerText = null;
    
    /**
     * Creates a new instance of this class and places the item at the end of
     * the grid.
     * 
     * @param parent parent grid
     * @param style item style
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
    public GridItem(Grid parent, int style)
    {
        this(parent, style, -1);
    }

    /**
     * Creates a new instance of this class and places the item in the grid at
     * the given index.
     * 
     * @param parent parent grid
     * @param style item style
     * @param index index where to insert item
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
    public GridItem(Grid parent, int style, int index)
    {
        super(parent, style, index);

        this.parent = parent;

        List roots = parent.getRootItems();

        if (index == -1 || index >= roots.size())
        {
            parent.newItem(this, -1);
        }
        else
        {
            int ix = parent.indexOf((GridItem)roots.get(index));
            parent.newItem(this, ix);
        }
    }

    /**
     * Creates a new instance of this class as a child node of the given
     * GridItem and places the item at the end of the parents items.
     * 
     * @param parent parent item
     * @param style item style
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
    public GridItem(GridItem parent, int style)
    {
        this(parent, style, -1);
    }

    /**
     * Creates a new instance of this class as a child node of the given
     * Grid and places the item at the given index in the parent items
     * list.
     * 
     * @param parent parent item
     * @param style item style
     * @param index index to place item
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
    public GridItem(GridItem parent, int style, int index)
    {
        super(parent, style, index);

        parentItem = parent;
        this.parent = parentItem.getParent();

        if (index == -1 || index >= parentItem.getItems().length)
        {
            GridItem rightMostDescendent = parent;

            while (rightMostDescendent.getItems().length > 0)
            {
                rightMostDescendent = rightMostDescendent.getItems()[rightMostDescendent.getItems().length - 1];
            }

            this.parent.newItem(this, this.parent.indexOf(rightMostDescendent) + 1);
        }
        else
        {
            this.parent.newItem(this, this.parent.indexOf(parent.getItems()[index]));
        }
        level = parentItem.getLevel() + 1;

        parentItem.newItem(this, index);

        if (parent.isVisible() && parent.isExpanded())
        {
            setVisible(true);
        }
        else
        {
            setVisible(false);
        }

    }

    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        parent.removeItem(this);
        if (parentItem != null)
        {
            parentItem.remove(this);
        }

        for (int i = children.size() - 1; i >= 0; i--)
        {
            ((GridItem)children.get(i)).dispose();
        }
        super.dispose();
    }

    /**
     * Fires the given event type on the parent Grid instance. This method
     * should only be called from within a cell renderer. Any other use is not
     * intended.
     * 
     * @param eventId SWT event constant
     * @throws SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void fireEvent(int eventId)
    {
        checkWidget();

        Event e = new Event();
        e.display = getDisplay();
        e.widget = this;
        e.item = this;
        e.type = eventId;

        getParent().notifyListeners(eventId, e);
    }

    /**
     * Fires the appropriate events in response to a user checking/unchecking an
     * item. Checking an item fires both a selection event (with event.detail of
     * SWT.CHECK) if the checkbox is in the first column and the seperate check
     * listener (all columns). This method manages that behavior. This method
     * should only be called from within a cell renderer. Any other use is not
     * intended.
     * 
     * @param column the column where the checkbox resides
     * @throws SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void fireCheckEvent(int column)
    {
        checkWidget();

        Event selectionEvent = new Event();
        selectionEvent.display = getDisplay();
        selectionEvent.widget = this;
        selectionEvent.item = this;
        selectionEvent.type = SWT.Selection;
        selectionEvent.detail = SWT.CHECK;
        selectionEvent.index = column;

        getParent().notifyListeners(SWT.Selection, selectionEvent);
    }

    /**
     * Returns the receiver's background color.
     * 
     * @return the background color
     * @throws SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public Color getBackground()
    {
        checkWidget();

        if (defaultBackground == null)
        {
            return parent.getBackground();
        }
        return defaultBackground;
    }

    /**
     * Returns the background color at the given column index in the receiver.
     * 
     * @param index the column index
     * @return the background color
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public Color getBackground(int index)
    {
        checkWidget();
        ensureSize(backgrounds);
        Color c = (Color)backgrounds.get(index);
//        if (c == null)
//        {
//            c = getBackground();
//        }
        return c;
    }

    /**
     * Returns a rectangle describing the receiver's size and location relative
     * to its parent at a column in the table.
     * 
     * @param columnIndex the index that specifies the column
     * @return the receiver's bounding column rectangle
     * @throws SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public Rectangle getBounds(int columnIndex)
    {
        checkWidget();
        
        if (!isVisible()) return new Rectangle(0,0,0,0);
        
        int index = parent.indexOf(this);

        int topIndex = parent.getTopIndex();
        
        if (index < topIndex) return new Rectangle(0,0,0,0);
        
        int visibleRows = parent.getPotentiallyPaintedRows();
        
        boolean found = false;
        
        GridItem currentItem = parent.getItem(topIndex);
        
        if (currentItem == this)
        {
            found = true;
        }
        else
        {
            for (int i = 1; i < visibleRows; i++)
            {
                currentItem = parent.getNextVisibleItem(currentItem);
                if (currentItem == null) return new Rectangle(0,0,0,0);
                if (currentItem == this)
                {
                    found = true;
                    break;
                }
            }          
        }    

        if (!found) return new Rectangle(0,0,0,0);
        
        Point origin = parent.getOrigin(parent.getColumn(columnIndex), this);

        int width = 0;

        int span = getColumnSpan(columnIndex);
        for (int i = 0; i <= span; i++)
        {
            if (parent.getColumnCount() <= columnIndex + i)
            {
                break;
            }
            width += parent.getColumn(columnIndex + i).getWidth();
        }

        int height = parent.getItemHeight();

        return new Rectangle(origin.x, origin.y, width - 1, height);
    }

    /**
     * Returns the checked state at the first column in the receiver.
     * 
     * @return the checked state
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public boolean getChecked()
    {
        checkWidget();
        return getChecked(0);
    }

    /**
     * Returns the checked state at the given column index in the receiver.
     * 
     * @param index the column index
     * @return the checked state
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public boolean getChecked(int index)
    {
        checkWidget();
        ensureSize(checks);
        Boolean b = (Boolean)checks.get(index);
        if (b == null)
        {
            return false;
        }
        return b.booleanValue();
    }

    /**
     * Returns the column span for the given column index in the receiver.
     * 
     * @param index the column index
     * @return the number of columns spanned (0 equals no columns spanned)
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public int getColumnSpan(int index)
    {
        checkWidget();
        ensureSize(columnSpans);
        Integer i = (Integer)columnSpans.get(index);
        if (i == null)
        {
            return 0;
        }
        return i.intValue();
    }

    /**
     * Returns the font that the receiver will use to paint textual information
     * for this item.
     * 
     * @return the receiver's font
     * @throws SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public Font getFont()
    {
        if (defaultFont == null)
        {
            return parent.getFont();
        }
        return defaultFont;
    }

    /**
     * Returns the font that the receiver will use to paint textual information
     * for the specified cell in this item.
     * 
     * @param index the column index
     * @return the receiver's font
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public Font getFont(int index)
    {
        checkWidget();
        ensureSize(fonts);
        Font f = (Font)fonts.get(index);
        if (f == null)
        {
            f = getFont();
        }
        return f;
    }

    /**
     * Returns the foreground color that the receiver will use to draw.
     * 
     * @return the receiver's foreground color
     * @throws SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public Color getForeground()
    {
        if (defaultForeground == null)
        {
            return parent.getForeground();
        }
        return defaultForeground;
    }

    /**
     * Returns the foreground color at the given column index in the receiver.
     * 
     * @param index the column index
     * @return the foreground color
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public Color getForeground(int index)
    {
        checkWidget();
        ensureSize(foregrounds);
        Color c = (Color)foregrounds.get(index);
        if (c == null)
        {
            c = getForeground();
        }
        return c;
    }

    /**
     * Returns <code>true</code> if the first column in the receiver is
     * grayed, and false otherwise. When the GridColumn does not have the
     * <code>CHECK</code> style, return false.
     * 
     * @return the grayed state of the checkbox
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public boolean getGrayed()
    {
        return getGrayed(0);
    }

    /**
     * Returns <code>true</code> if the column at the given index in the
     * receiver is grayed, and false otherwise. When the GridColumn does not
     * have the <code>CHECK</code> style, return false.
     * 
     * @param index the column index
     * @return the grayed state of the checkbox
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public boolean getGrayed(int index)
    {
        checkWidget();
        ensureSize(grayeds);
        Boolean b = (Boolean)grayeds.get(index);
        if (b == null)
        {
            return false;
        }
        return b.booleanValue();
    }

    /**
     * {@inheritDoc}
     */
    public Image getImage()
    {
        checkWidget();
        return getImage(0);
    }

    /**
     * Returns the image stored at the given column index in the receiver, or
     * null if the image has not been set or if the column does not exist.
     * 
     * @param index the column index
     * @return the image stored at the given column index in the receiver
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public Image getImage(int index)
    {
        checkWidget();
        ensureSize(images);
        return (Image)images.get(index);
    }

    /**
     * Returns the item at the given, zero-relative index in the receiver.
     * Throws an exception if the index is out of range.
     * 
     * @param index the index of the item to return
     * @return the item at the given index
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number
     * of elements in the list minus 1 (inclusive)</li>
     * </ul>
     * @throws SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public GridItem getItem(int index)
    {
        checkWidget();
        return (GridItem)children.get(index);
    }
    
    /**
     * Returns the number of items contained in the receiver
     * that are direct item children of the receiver.
     *
     * @return the number of items
     *
     * @throws SWTException
     * <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
    public int getItemCount()
    {
        checkWidget();
        return children.size();
    }
    
    /**
     * Searches the receiver's list starting at the first item
     * (index 0) until an item is found that is equal to the 
     * argument, and returns the index of that item. If no item
     * is found, returns -1.
     *
     * @param item the search item
     * @return the index of the item
     *
     * @exception IllegalArgumentException <ul>
     *    <li>ERROR_NULL_ARGUMENT - if the item is null</li>
     *    <li>ERROR_INVALID_ARGUMENT - if the item has been disposed</li>
     * </ul>
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
    public int indexOf (GridItem item) {
        checkWidget ();
        if (item == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
        if (item.isDisposed()) SWT.error(SWT.ERROR_INVALID_ARGUMENT);

        return children.indexOf(item);
    }

    /**
     * Returns a (possibly empty) array of <code>GridItem</code>s which are
     * the direct item children of the receiver.
     * <p>
     * Note: This is not the actual structure used by the receiver to maintain
     * its list of items, so modifying the array will not affect the receiver.
     * </p>
     * 
     * @return the receiver's items
     * @throws SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public GridItem[] getItems()
    {
        return (GridItem[])children.toArray(new GridItem[children.size()]);
    }

    /**
     * Returns the level of this item in the tree.
     * 
     * @return the level of the item in the tree
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public int getLevel()
    {
        checkWidget();
        return level;
    }

    /**
     * Returns the receiver's parent, which must be a <code>Grid</code>.
     * 
     * @return the receiver's parent
     * @throws SWTException
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
     * Returns the receiver's parent item, which must be a
     * <code>GridItem</code> or null when the receiver is a root.
     * 
     * @return the receiver's parent item
     * @throws SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public GridItem getParentItem()
    {
        checkWidget();
        return parentItem;
    }

    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        checkWidget();
        return getText(0);
    }

    /**
     * Returns the text stored at the given column index in the receiver, or
     * empty string if the text has not been set.
     * 
     * @param index the column index
     * @return the text stored at the given column index in the receiver
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public String getText(int index)
    {
        checkWidget();
        ensureSize(texts);
        String s = (String)texts.get(index);
        // SWT TableItem returns empty if never set
        // so we return empty to ensure API compatibility
        if (s == null)
        {
            return "";
        }
        return s;
    }

    /**
     * Returns true if this item has children.
     * 
     * @return true if this item has children
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public boolean hasChildren()
    {
        checkWidget();
        return hasChildren;
    }

    /**
     * Returns <code>true</code> if the receiver is expanded, and false
     * otherwise.
     * <p>
     * 
     * @return the expanded state
     * @throws SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public boolean isExpanded()
    {
        checkWidget();
        return expanded;
    }

    /**
     * Sets the receiver's background color to the color specified by the
     * argument, or to the default system color for the item if the argument is
     * null.
     * 
     * @param background the new color (or null)
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
     * </ul>
     * @throws SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setBackground(Color background)
    {
        checkWidget();

        if (background != null && background.isDisposed())
        {
            SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        }

        defaultBackground = background;
        parent.redraw();
    }

    /**
     * Sets the background color at the given column index in the receiver to
     * the color specified by the argument, or to the default system color for
     * the item if the argument is null.
     * 
     * @param index the column index
     * @param background the new color (or null)
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setBackground(int index, Color background)
    {
        checkWidget();
        if (background != null && background.isDisposed())
        {
            SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        }
        ensureSize(backgrounds);
        backgrounds.set(index, background);
        parent.redraw();
    }

    /**
     * Sets the checked state at the first column in the receiver.
     * 
     * @param checked the new checked state
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setChecked(boolean checked)
    {
        checkWidget();
        setChecked(0, checked);
        parent.redraw();
    }

    /**
     * Sets the checked state at the given column index in the receiver.
     * 
     * @param index the column index
     * @param checked the new checked state
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setChecked(int index, boolean checked)
    {
        checkWidget();
        ensureSize(checks);
        checks.set(index, new Boolean(checked));
        parent.redraw();
    }

    /**
     * Sets the column spanning for the column at the given index to span the
     * given number of subsequent columns.
     * 
     * @param index column index that should span
     * @param span number of subsequent columns to span
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setColumnSpan(int index, int span)
    {
        checkWidget();
        ensureSize(columnSpans);
        columnSpans.set(index, new Integer(span));
        parent.redraw();
    }

    /**
     * Sets the expanded state of the receiver.
     * <p>
     * 
     * @param expanded the new expanded state
     * @throws SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setExpanded(boolean expanded)
    {
        checkWidget();
        this.expanded = expanded;

        // We must unselect any items that are becoming invisible
        // and thus if we change the selection we have to fire a selection event
        boolean unselected = false;

        for (Iterator itemIterator = children.iterator(); itemIterator.hasNext(); )
        {
            GridItem item = (GridItem) itemIterator.next();
            item.setVisible(expanded && visible);
            if (!expanded)
            {
                if (!getParent().getCellSelectionEnabled())
                {
                    if (getParent().isSelected(item))
                    {
                        unselected = true;
                        getParent().deselect(getParent().indexOf(item));
                    }                
                    if (deselectChildren(item))
                    {
                        unselected = true;
                    }
                }
                else
                {
                    if (deselectCells(item))
                    {
                        unselected = true;
                    }
                }
            }
        }

        this.getParent().setScrollValuesObsolete();

        if (unselected)
        {
            Event e = new Event();
            e.item = this;
            getParent().notifyListeners(SWT.Selection, e);
        }
        if (getParent().getFocusItem() != null && !getParent().getFocusItem().isVisible())
        {
            getParent().setFocusItem(this);
        }
        
        if (getParent().getCellSelectionEnabled())
        {
            getParent().updateColumnSelection();
        }
    }
    
    

    private boolean deselectCells(GridItem item)
    {
        boolean flag = false;
        
        int index = getParent().indexOf(item);
        
        GridColumn[] columns = getParent().getColumns();
        
        for (int i = 0; i < columns.length; i++)
        {
            Point cell = new Point(getParent().indexOf(columns[i]),index);
            if (getParent().isCellSelected(cell))
            {
                flag = true;
                getParent().deselectCell(cell);
            }
        }
        
        GridItem[] kids = item.getItems();
        for (int i = 0; i < kids.length; i++)
        {
            if (deselectCells(kids[i]))
            {
                flag = true;
            }
        }
        
        return flag;
    }
    
    /**
     * Deselects the given item's children recursively.
     * 
     * @param item item to deselect children.
     * @return true if an item was deselected
     */
    private boolean deselectChildren(GridItem item)
    {
        boolean flag = false;
        GridItem[] kids = item.getItems();
        for (int i = 0; i < kids.length; i++)
        {
            if (getParent().isSelected(kids[i]))
            {
                flag = true;
            }
            getParent().deselect(getParent().indexOf(kids[i]));
            if (deselectChildren(kids[i]))
            {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * Sets the font that the receiver will use to paint textual information for
     * this item to the font specified by the argument, or to the default font
     * for that kind of control if the argument is null.
     * 
     * @param f the new font (or null)
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
     * </ul>
     * @throws SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setFont(Font f)
    {
        checkWidget();
        if (f != null && f.isDisposed())
        {
            SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        }
        defaultFont = f;
        parent.redraw();
    }

    /**
     * Sets the font that the receiver will use to paint textual information for
     * the specified cell in this item to the font specified by the argument, or
     * to the default font for that kind of control if the argument is null.
     * 
     * @param index the column index
     * @param font the new font (or null)
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setFont(int index, Font font)
    {
        checkWidget();
        if (font != null && font.isDisposed())
        {
            SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        }
        ensureSize(fonts);
        fonts.set(index, font);
        parent.redraw();
    }

    /**
     * Sets the receiver's foreground color to the color specified by the
     * argument, or to the default system color for the item if the argument is
     * null.
     * 
     * @param foreground the new color (or null)
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
     * </ul>
     * @throws SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setForeground(Color foreground)
    {
        checkWidget();
        if (foreground != null && foreground.isDisposed())
        {
            SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        }
        defaultForeground = foreground;
        parent.redraw();
    }

    /**
     * Sets the foreground color at the given column index in the receiver to
     * the color specified by the argument, or to the default system color for
     * the item if the argument is null.
     * 
     * @param index the column index
     * @param foreground the new color (or null)
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setForeground(int index, Color foreground)
    {
        checkWidget();
        if (foreground != null && foreground.isDisposed())
        {
            SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        }
        ensureSize(foregrounds);
        foregrounds.set(index, foreground);
        parent.redraw();
    }

    /**
     * Sets the grayed state of the checkbox for the first column. This state
     * change only applies if the GridColumn was created with the SWT.CHECK
     * style.
     * 
     * @param grayed the new grayed state of the checkbox;
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setGrayed(boolean grayed)
    {
        checkWidget();
        setGrayed(0, grayed);
        parent.redraw();
    }

    /**
     * Sets the grayed state of the checkbox for the given column index. This
     * state change only applies if the GridColumn was created with the
     * SWT.CHECK style.
     * 
     * @param index the column index
     * @param grayed the new grayed state of the checkbox;
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setGrayed(int index, boolean grayed)
    {
        checkWidget();
        ensureSize(grayeds);
        grayeds.set(index, new Boolean(grayed));
        parent.redraw();
    }

    /**
     * {@inheritDoc}
     */
    public void setImage(Image image)
    {
        setImage(0, image);
        parent.redraw();
    }

    /**
     * Sets the receiver's image at a column.
     * 
     * @param index the column index
     * @param image the new image
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_INVALID_ARGUMENT - if the image has been disposed</li>
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setImage(int index, Image image)
    {
        checkWidget();
        if (image != null && image.isDisposed())
        {
            SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        }
        ensureSize(images);
        images.set(index, image);
        parent.redraw();
    }

    /**
     * Sets the receiver's text at a column.
     * 
     * @param index the column index
     * @param text the new text
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_NULL_ARGUMENT - if the text is null</li>
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setText(int index, String text)
    {
        checkWidget();
        if (text == null)
        {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        ensureSize(texts);
        texts.set(index, text);
        parent.redraw();
    }
    
    /**
     * {@inheritDoc}
     */
    public void setText(String string)
    {
        setText(0, string);
        parent.redraw();
    }

    /**
     * Adds items to the given list to ensure the list is large enough to hold a
     * value for each column.
     * 
     * @param al list
     */
    private void ensureSize(ArrayList al)
    {
        int count = Math.max(1, parent.getColumnCount());
        al.ensureCapacity(count);
        while (al.size() <= count)
        {
            al.add(null);
        }
    }

    /**
     * Removes the given child item from the list of children.
     * 
     * @param child child to remove
     */
    private void remove(GridItem child)
    {
        children.remove(child);
        hasChildren = children.size() > 0;
    }

    /**
     * Returns true if the item is visible because its parent items are all
     * expanded. This method does not determine if the item is in the currently
     * visible range.
     * 
     * @return Returns the visible.
     */
    boolean isVisible()
    {
        return visible;
    }

    /**
     * Creates a new child item in this item at the given index.
     * 
     * @param item new child item
     * @param index index
     */
    void newItem(GridItem item, int index)
    {
        setHasChildren(true);

        if (index == -1)
        {
            children.add(item);
        }
        else
        {
            children.add(index, item);
        }
    }

    /**
     * Sets whether this item has children.
     * 
     * @param hasChildren true if this item has children
     */
    void setHasChildren(boolean hasChildren)
    {
        this.hasChildren = hasChildren;
    }

    /**
     * Sets the visible state of this item. The visible state is determined by
     * the expansion state of all of its parent items. If all parent items are
     * expanded it is visible.
     * 
     * @param visible The visible to set.
     */
    void setVisible(boolean visible)
    {
        if (this.visible == visible)
        {
            return;
        }

        this.visible = visible;

        if (visible)
        {
            parent.updateVisibleItems(1);
        }
        else
        {
            parent.updateVisibleItems(-1);
        }

        if (hasChildren)
        {
            boolean childrenVisible = visible;
            if (visible)
            {
                childrenVisible = expanded;
            }
            for (Iterator itemIterator = children.iterator(); itemIterator.hasNext(); )
            {
                GridItem item = (GridItem) itemIterator.next();

                item.setVisible(childrenVisible);
            }
        }
    }

    /**
     * Returns the receiver's row header text.  If the text is <code>null</code> the row header will
     * display the row number.
     * 
     * @param index the column index
     * @return the text stored at the given column index in the receiver
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public String getHeaderText()
    {
        checkWidget();
        return headerText;
    }

    /**
     * Sets the receiver's row header text.  If the text is <code>null</code> the row header will
     * display the row number.
     * 
     * @param text the new text
     * @throws IllegalArgumentException
     * <ul>
     * <li>ERROR_NULL_ARGUMENT - if the text is null</li>
     * </ul>
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setHeaderText(String text)
    {
        checkWidget();
        if (text == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
        if (text != headerText)
        {
            GC gc = new GC(parent);
            
            int oldWidth = parent.getRowHeaderRenderer().computeSize(gc, SWT.DEFAULT, SWT.DEFAULT, this).x;
            
            this.headerText = text;
            
            int newWidth = parent.getRowHeaderRenderer().computeSize(gc, SWT.DEFAULT, SWT.DEFAULT, this).x;
            
            gc.dispose();
            
            parent.recalculateRowHeaderWidth(this,oldWidth,newWidth);
        }
        parent.redraw();        
    }

    /**
     * Returns the checkable state at the given column index in the receiver.  If the column at 
     * the given index is not checkable then this will return false regardless of the individual 
     * cell's checkable state.  
     * 
     * @param index the column index
     * @return the checked state
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public boolean getCheckable(int index)
    {
        checkWidget();
        
        if (!parent.getColumn(index).getCheckable()) return false;
        
        ensureSize(checkable);
        Boolean b = (Boolean)checkable.get(index);
        if (b == null)
        {
            return true;
        }
        return b.booleanValue();
    }
    
    /**
     * Sets the checkable state at the given column index in the receiver.  A checkbox which is 
     * uncheckable will not be modifiable by the user but still make be modified programmatically. 
     * If the column at the given index is not checkable then individual cell will not be checkable 
     * regardless.
     * 
     * @param index the column index
     * @param checked the new checked state
     * @throws org.eclipse.swt.SWTException
     * <ul>
     * <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that
     * created the receiver</li>
     * </ul>
     */
    public void setCheckable(int index, boolean checked)
    {
        checkWidget();
        ensureSize(checkable);
        checkable.set(index, new Boolean(checked));
    }
    
    /**
     * Notifies the item that a column has been removed.
     * 
     * @param index index of column removed.
     */
    void columnRemoved(int index)
    {
        removeValue(index,backgrounds);
        removeValue(index,checks);
        removeValue(index,checkable);
        removeValue(index,fonts);
        removeValue(index,foregrounds);
        removeValue(index,grayeds);
        removeValue(index,images);
        removeValue(index,texts);
        
    }
    
    private void removeValue(int index, List list)
    {
        if (list.size() > index)
        {
            list.remove(index);
        }
    }
}
