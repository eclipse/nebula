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
package org.eclipse.nebula.widgets.pshelf;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

/**
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p> 
 * Renders a single visual unit.
 *  
 * @author chris.gross@us.ibm.com
 */
public abstract class AbstractRenderer
{

    /** Hover state. */
    private boolean hover;

    /** Renderer has focus. */
    private boolean focus;

    /** Mouse down on the renderer area. */
    private boolean mouseDown;

    /** Selection state. */
    private boolean selected;

    /** Expansion state. */
    private boolean expanded;

    /** The bounds the renderer paints on. */
    private Rectangle bounds = new Rectangle(0, 0, 0, 0);

    private boolean disposed = false;
    
    /**
     * Returns the bounds.
     * 
     * @return Rectangle describing the bounds.
     */
    public Rectangle getBounds()
    {
        return bounds;
    }

    /**
     * Sets the bounds of the drawing.
     * 
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param width Width.
     * @param height Height.
     */
    public void setBounds(int x, int y, int width, int height)
    {
        setBounds(new Rectangle(x, y, width, height));
    }

    /**
     * Sets the bounds of the drawing.
     * 
     * @param bounds Bounds.
     */
    public void setBounds(Rectangle bounds)
    {
        this.bounds = bounds;
    }

    /**
     * Returns the size.
     * 
     * @return size of the renderer.
     */
    public Point getSize()
    {
        return new Point(bounds.width, bounds.height);
    }

    /**
     * Sets the location of the drawing.
     * 
     * @param x X.
     * @param y Y.
     */
    public void setLocation(int x, int y)
    {
        setBounds(new Rectangle(x, y, bounds.width, bounds.height));
    }

    /**
     * Sets the location of the drawing.
     * 
     * @param location Location.
     */
    public void setLocation(Point location)
    {
        setBounds(new Rectangle(location.x, location.y, bounds.width, bounds.height));
    }

    /**
     * Sets the area of the drawing.
     * 
     * @param width Width.
     * @param height Height.
     */
    public void setSize(int width, int height)
    {
        setBounds(new Rectangle(bounds.x, bounds.y, width, height));
    }

    /**
     * Sets the area of the drawing.
     * 
     * @param size Size.
     */
    public void setSize(Point size)
    {
        setBounds(new Rectangle(bounds.x, bounds.y, size.x, size.y));
    }

    /**
     * Returns a boolean value indicating if this renderer has focus.
     * 
     * @return True/false if has focus.
     */
    public boolean isFocus()
    {
        return focus;
    }

    /**
     * Sets focus state.
     * 
     * @param focus focus state.
     */
    public void setFocus(boolean focus)
    {
        this.focus = focus;
    }

    /**
     * Returns the hover state.
     * 
     * @return Is the renderer in the hover state.
     */
    public boolean isHover()
    {
        return hover;
    }

    /**
     * Sets the hover state.
     * 
     * @param hover Hover state.
     */
    public void setHover(boolean hover)
    {
        this.hover = hover;
    }

    /**
     * Returns the boolean value indicating if the renderer has the mouseDown
     * state.
     * 
     * @return mouse down state.
     */
    public boolean isMouseDown()
    {
        return mouseDown;
    }

    /**
     * Sets the mouse down state.
     * 
     * @param mouseDown Mouse state.
     */
    public void setMouseDown(boolean mouseDown)
    {
        this.mouseDown = mouseDown;
    }

    /**
     * Returns the boolean state indicating if the selected state is set.
     * 
     * @return selected state.
     */
    public boolean isSelected()
    {
        return selected;
    }

    /**
     * Sets the selected state.
     * 
     * @param selected Selection state.
     */
    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }

    /**
     * Returns the expansion state.
     * 
     * @return Returns the expanded.
     */
    public boolean isExpanded()
    {
        return expanded;
    }

    /**
     * Sets the expansion state of this renderer.
     * 
     * @param expanded The expanded to set.
     */
    public void setExpanded(boolean expanded)
    {
        this.expanded = expanded;
    }

    /**
     * Paints the visual representation of the given value on the given GC. The
     * actual class of the value object is determined by the use of the
     * implementing class.
     * <p>
     * Implementors need to respect the bounds values that may have been
     * specified. The bounds values may affect the x and y values for all
     * drawing operations as well as the width and heights. Implementors may use
     * a <code>Transform</code> to translate the coordinates of all the
     * drawing operations, otherwise they will need to offset each draw.
     * </p>
     * 
     * @param gc GC to paint with
     * @param value the value being painted
     */
    public abstract void paint(GC gc, Object value);

    /**
     * Returns the size of the given value's visual representation.
     * 
     * @param gc convenience GC for string and text extents
     * @param wHint given width (or SWT.DEFAULT)
     * @param hHint given height (or SWT.DEFAULT)
     * @param value value to be sized
     * @return the size
     */
    public abstract Point computeSize(GC gc, int wHint, int hHint, Object value);

    /**
     * Performs any initialization logic (such as creating new colors or fonts).
     * 
     * @param parent control that is using the renderer
     */
    public abstract void initialize(Control parent);
    
    /**
     * Disposes of any resources managed by this renderer.
     */
    public void dispose(){
        setDisposed(true);
    }

    /**
     * @return the disposed
     */
    public boolean isDisposed()
    {
        return disposed;
    }

    /**
     * @param disposed the disposed to set
     */
    protected void setDisposed(boolean disposed)
    {
        this.disposed = disposed;
    }
}
