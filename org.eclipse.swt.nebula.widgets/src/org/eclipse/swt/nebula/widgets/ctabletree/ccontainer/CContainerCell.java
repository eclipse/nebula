/****************************************************************************
 * Copyright (c) 2005-2006 Jeremy Dowdall
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/

package org.eclipse.swt.nebula.widgets.ctabletree.ccontainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;


public abstract class CContainerCell {
	private class DeferredListener {
		public int eventType;
		public Listener handler;

		DeferredListener(int eventType, Listener handler) {
			this.eventType = eventType;
			this.handler = handler;
		}

		public boolean equals(Object object) {
			if(object instanceof DeferredListener) {
				DeferredListener handler = (DeferredListener) object;
				return (handler.eventType == this.eventType && handler.handler == this.handler);
			}
			return false;
		}
	}
	
	public static final boolean gtk = "gtk".equals(SWT.getPlatform());
	public static final boolean win32 = "win32".equals(SWT.getPlatform());

	public static final int CELL_NORMAL 	= 0;
	public static final int CELL_MOVING 	= 1 << 0;
	public static final int CELL_RESIZING 	= 1 << 1;
	public static final int RESULT_NONE		= 0; 
	public static final int RESULT_CONSUME	= 1 << 0; 
	public static final int RESULT_REDRAW	= 1 << 1; 
	public static final int RESULT_LAYOUT	= 1 << 2;

	private static final Color bgOver = gtk ? Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BORDER) :
		Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
	private static final Color bgNorm = gtk ? Display.getCurrent().getSystemColor(SWT.COLOR_WHITE) :
		Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	private static final Color border = gtk ? Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BORDER) :
		Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
//	private static final Color fgOver = gtk ? border :
//		Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	private static final Color fgNorm = gtk ? border :
		Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
	
	private static final int[] closedPoints = gtk ? new int[] { 2,  0,  7, 5,  2, 10 } :
//		new int[] { 2,4, 4,4, 4,2, 5,2, 5,4, 7,4, 7,5, 5,5, 5,7, 4,7, 4,5, 2,5 };
		new int[] { 2,4, 6,4, 4,4, 4,2, 4,4, 4,6 };
	private static final int[] openPoints 	= gtk ? new int[] { 10, 2,  5, 7,  0, 2  } :
		new int[] { 2,4, 6,4 };//, 7,5, 2,5 };
	private static final int pointsWidth = gtk ? 11 : 8;

	protected CContainer container;
	protected CContainerItem item;

	protected int style;
	protected Composite titleArea;
	protected SComposite childArea;
	protected boolean update = true;
	protected boolean needsLayout = true;
	protected boolean open = false;
	protected boolean selected = false;
	protected boolean visible = true;
	protected boolean toggleVisible = false;
	protected boolean titleVisible = false;
	protected boolean childVisible = false;
	protected int indent = 0;
	protected boolean ghostToggle = false;
	protected boolean mouseDown = false;
	protected boolean mouseOver = false;
	protected boolean mouseOverToggle = false;
	public int marginLeft 	= gtk ? 2 : 1;
	public int marginWidth 	= 0;
	public int marginRight 	= 0;
	public int marginTop 	= gtk ? 0 : 0;
	public int marginHeight = gtk ? 4 : 1;
	public int marginBottom = 0;
	public int horizontalSpacing = 2;
	public int horizontalAlignment = SWT.LEFT;
	public int verticalAlignment = SWT.CENTER;
	public int toggleWidth = 16;
	public int childSpacing = 1;
	public int rightChildIndent = 2;
	// TODO minX & minY - full implementation, or are these really emptyX & emptyY?
	public int minX = 1;
	public int minY = 1;
	protected Rectangle bounds;
	protected Rectangle toggleBounds = new Rectangle(0,0,0,0);
	protected int titleHeight = 0;
	protected Color background;
	protected Color foreground;
	protected Color cellBackground = null;
	protected Color cellForeground = null;
	protected boolean isGridLine = false;
	private int cellState = CELL_NORMAL;

	private List dlisteners = new ArrayList();


	public CContainerCell(CContainerItem item, int style) {
		this.container = item.container;
		this.style = style;
		createContents(container.body, style);
		if((style & SWT.SIMPLE) != 0) {
			createTitleContents(titleArea, style);
		}
		this.item = item;
	}


	public void addListener(int eventType, Listener handler) {
		List controls = getEventManagedControls();
		for(Iterator iter = controls.iterator(); iter.hasNext(); ) {
			Control control = (Control) iter.next();
			control.addListener(eventType, handler);
		}

		if((getStyle() & SWT.DROP_DOWN) != 0) {
			DeferredListener dl = new DeferredListener(eventType, handler);
			boolean contains = false;
			for(Iterator i = dlisteners.iterator(); i.hasNext(); ) {
				if(dl.equals(i.next())) {
					contains = true;
					break;
				}
			}
			if(!contains) dlisteners.add(dl);
		}
	}

	protected void clearCellStateFlags() {
		cellState = 0;
	}
	
	/**
	 * Compute the preferred size of the cell for its current state (open or closed, if applicable)
	 * just the way it would be done in a regular SWT layout
	 * <p>Implementations are to implement this themselves, though if the the cell's style is 
	 * SWT.SIMPLE then most likely they will simply return the computed size as found by
	 * titleArea.computeSize(int, int)</p> 
	 * @param wHint
	 * @param hHint
	 * @return a Point representing the preferred size of the cell
	 */
	public abstract Point computeSize(int wHint, int hHint);

	/**
	 * Compute the preferred size of the cell's Title Area, similar to the way it would be done 
	 * in a regular SWT layout
	 * <p>Implementations are to implement this themselves, though if the the cell's style is 
	 * SWT.SIMPLE then most likely they will simply return the computed height as found by
	 * titleArea.computeSize(int, -1).y</p> 
	 * @param wHint
	 * @return an int representing the preferred height of the cell
	 */
	public abstract int computeTitleHeight(int hHint);

	/**
	 * Create the contents of your custom cell's Child Area here
	 * <p>The Child Area is the SComposite (@see org.aspencloud.widgets.SComposite)
	 * that will appear and disappear as the cell is opened and closed</p>
	 * <p>The base cell class will call this immediately after creating its body if, and only if,
	 * the style bit for SWT.DROP_DOWN is set
	 * <p>Note that NO layout has been assigned to the parameter "contents".  Implementations
	 * must provide this or things may not work as expected</p>
	 * @param contents the Child Area of the cell
	 * @param style the style that was passed to the constructor
	 * @see org.aspencloud.widgets.ccontainer#createTitleContents(Composite, int)
	 */
	protected void createChildContents(SComposite contents, int style) {}

	private void createContents(Composite parent, int style) {
		if((style & SWT.SIMPLE) != 0) {
			titleArea = new Composite(parent, style);
			titleArea.setBackground(parent.getBackground());
			titleVisible = true;
		} else {
			bounds = new Rectangle(0,0,0,0);
			background = parent.getBackground();
			foreground = parent.getForeground();
		}

		if((style & SWT.DROP_DOWN) != 0) {
			// NOTE: childArea is created the first time the cell is opened
			setChildVisible(true);
		}
		
		if((style & SWT.TOGGLE) != 0) {
			setToggleVisible(true, true);
		}
	}

	/**
	 * Create the contents of your custom cell's Title Area here
	 * <p>The Title Area is the composite that makes up the body of the "traditional"
	 * cell; it is called "Title" because it sits above the Child Area like a title or header</p>
	 * <p>The base cell class will call this immediately after creating its body if, and only if,
	 * the style bit for SWT.SIMPLE is set</p>
	 * <p>Note that NO layout has been assigned to the parameter "contents".  Implementations
	 * must provide this or things may not work as expected</p>
	 * @param contents the Title Area of the cell
	 * @param style the style that was passed to the constructor
	 * @see org.aspencloud.widgets.ccontainer#createChildContents(Composite, int)
	 */
	protected void createTitleContents(Composite contents, int style) {}

	public void dispose() {
		if(titleArea != null && !titleArea.isDisposed()) titleArea.dispose();
		if(childArea != null && !childArea.isDisposed()) childArea.dispose();
	}

	public Color getBackground() {
		if((getStyle() & SWT.SIMPLE) != 0) {
			return titleArea.getBackground();
		} else {
			return background;
		}
	}

	public Rectangle getBounds() {
		if((getStyle() & SWT.SIMPLE) != 0) {
			return titleArea.getBounds();
		} else {
			return bounds;
		}
	}

	public Color getCellBackground() {
		return cellBackground;
	}

	public Color getCellForeground() {
		return cellForeground;
	}

	public int getCellState() {
		return cellState;
	}

	public Composite getChildArea() {
		return childArea;
	}

	protected List getColorManagedControls() {
		List l = new ArrayList();
		if((getStyle() & SWT.SIMPLE) != 0) {
			l.addAll(getControls(titleArea));
		}
		if((getStyle() & SWT.DROP_DOWN) != 0) {
			l.addAll(getControls(childArea));
		}
		return l;
	}

	private List getControls(Composite c) {
		if(c != null) {
			List l = new ArrayList();
			l.add(c);
			Object[] a = c.getChildren();
			for(int i = 0; i < a.length; i++) {
				if(a[i] instanceof Composite) {
					l.addAll(getControls((Composite) a[i]));
				} else {
					l.add(a[i]);
				}
			}
			return l;
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	protected Display getDisplay() {
		return container.getDisplay();
	}

	protected List getEventManagedControls() {
		List l = new ArrayList(getControls(titleArea));
		l.addAll(getControls(childArea));
		return l;
	}

	public Color getForeground() {
		if((getStyle() & SWT.SIMPLE) != 0) {
			return titleArea.getForeground();
		} else {
			return foreground;
		}
	}

	public int getIndent() {
		return indent;
	}

	public CContainerItem getItem() {
		return item;
	}

	public Point getLocation() {
		Rectangle r = getBounds();
		return new Point(r.x, r.y);
	}

	public Point getSize() {
		Rectangle r = getBounds();
		return new Point(r.width, r.height);
	}

	public int getStyle() {
		return style;
	}

	public Composite getTitleArea() {
		return titleArea;
	}

	public int getTitleHeight() {
		return titleHeight;
	}

	public Rectangle getToggleBounds() {
		return toggleBounds;
	}

	public boolean getToggleVisible() {
		return toggleVisible;
	}

	/**
	 * Give the Item a chance to handle the mouse event
	 * @param event the Event
	 * @return 0: do nothing, 1: redraw, 2: layout
	 */
	// TODO: handleMouseEvent uses bitwise OR'ed return code, allow "consumed" code?
	public int handleMouseEvent(Event event, boolean selectionActive) {
		int result = RESULT_NONE;
		switch(event.type) {
		case SWT.MouseDown:
			if(bounds != null) { // such as is the case with SWT.SIMPLE style cells
				Point pt = new Point(event.x, event.y);
				boolean tmp = bounds.contains(pt);
				if(tmp != mouseOver) {
					mouseOver = tmp;
					result |= RESULT_REDRAW;
				}
				if(!mouseOver && mouseDown) {
					mouseDown = false;
					result |= RESULT_REDRAW;
				}
				tmp = toggleVisible && toggleBounds.contains(pt);
				if(tmp != mouseOverToggle) {
					mouseOverToggle = tmp;
					result |= RESULT_REDRAW;
				}
			}
			break;
		}
		return result;
	}

	protected boolean isCellState(int opcode) {
		return ((cellState & opcode) != 0);
	}

	protected boolean isCellStateNormal() {
		return cellState == 0;
	}

	public boolean isOpen() {
		return open;
	}

	public boolean isSelected() {
		return selected;
	}

	/**
	 * Requests a layout of the cell.
	 * <p>Subclasses are to implement this themselves just as they would with a custom SWT layout</p>
	 * <p>Cells of style SWT.SIMPLE will probably just pass this call to the titleArea composite:
	 * titleArea.layout()</p>
	 */
	protected abstract void layout();

	/**
	 * Called by Container during its paint method to draw the cell's Title Area
	 * <p>Subclasses need to override if they want to have a visual representation</p>
	 * <p>If SWT.SIMPLE is set this method should probably just return, but is not required to.</p>
	 * @param gc the GC upon which the cell will be painted
	 * @param ebounds the bounding rectangle of the Container's paint event
	 */
	public abstract void paint(GC gc, Rectangle ebounds);

	protected void paintToggle(GC gc, Rectangle ebounds) {
		Rectangle bounds = getBounds();
		double x = indent + ((toggleBounds.width - pointsWidth) / 2) + bounds.x - ebounds.x;
		double y = ((toggleBounds.height - pointsWidth) / 2) + bounds.y - ebounds.y;
		int[] data = open ? openPoints : closedPoints;
		int[] pts = new int[data.length];
		for (int i = 0; i < data.length; i += 2) {
			pts[i] = data[i] + (int) x;
			pts[i+1] = data[i+1] + (int) y;
		}

		if(gtk){
			gc.setForeground(border);
			gc.setBackground((mouseOverToggle && !mouseDown) ? bgOver : bgNorm);
			gc.setAntialias(SWT.ON);
			gc.fillPolygon(pts);
			gc.drawPolygon(pts);
			gc.setAdvanced(false);
		} else {
			gc.setBackground(bgNorm);
			gc.fillRoundRectangle(
					(int) x,
					(int) y,
					pointsWidth,
					pointsWidth,
					3, 3);
			Color color = new Color(getDisplay(), 223, 229, 234);
			gc.setForeground(color);
			gc.drawLine(
					(int) x,
					(int) y + pointsWidth-2,
					(int) x + pointsWidth,
					(int) y + pointsWidth-2
					);
			gc.drawLine(
					(int) x + pointsWidth-1,
					(int) y,
					(int) x + pointsWidth-1,
					(int) y + pointsWidth-2
					);
			gc.drawLine(
					(int) x + pointsWidth-2,
					(int) y + pointsWidth-3,
					(int) x + pointsWidth-2,
					(int) y + pointsWidth-3
					);
			color.dispose();
			color = new Color(getDisplay(), 196, 206, 216);
			gc.setForeground(color);
			color.dispose();
			gc.drawLine(
					(int) x,
					(int) y + pointsWidth-1,
					(int) x + pointsWidth,
					(int) y + pointsWidth-1
					);
			gc.setForeground(border);
			gc.drawRoundRectangle(
					(int) x,
					(int) y,
					pointsWidth,
					pointsWidth,
					3, 3);
			gc.setForeground(fgNorm);
			gc.drawPolyline(pts);
		}

	}

	public void removeListener(int eventType, Listener handler) {
		List controls = getEventManagedControls();
		for(Iterator iter = controls.iterator(); iter.hasNext(); ) {
			Control control = (Control) iter.next();
			control.removeListener(eventType, handler);
		}

		if((getStyle() & SWT.DROP_DOWN) != 0) {
			DeferredListener h = new DeferredListener(eventType, handler);
			for(Iterator i = dlisteners.iterator(); i.hasNext(); ) {
				if(h.equals(i.next())) i.remove();
			}
		}
	}

	public void setBounds(int x, int y, int width, int height) {
		Rectangle bounds = new Rectangle(x, y, width, height);
		setBounds(bounds);
	}

	public void setBounds(Rectangle bounds) {
		bounds = snap(bounds);
		if((getStyle() & SWT.SIMPLE) != 0) {
			titleArea.setBounds(bounds);
		} else {
			this.bounds.x = bounds.x;
			this.bounds.y = bounds.y;
			this.bounds.width = bounds.width;
			this.bounds.height = bounds.height;
		}
		needsLayout = true;
	}

	public void setCellBackground(Color color) {
		cellBackground = color;
		updateColors();
	}
	public void setCellForeground(Color color) {
		cellForeground = color;
		updateColors();
	}

	protected void setCellStateFlag(int flag, boolean on) {
		if(on) {
			cellState |= flag;
		} else {
			cellState &= ~flag;
		}
	}
	
	public void setChildVisible(boolean visible) {
		childVisible = visible;
		updateVisibility();
	}

	protected void setCursor(int id) {
		container.body.setCursor(getDisplay().getSystemCursor(id));
	}

	public boolean setFocus() {
		// TODO setFocus not yet implemented
		return false;
	}

	public void setIndent(int indent) {
		this.indent = indent;
		needsLayout = true;
	}

	public void setIsGridLine(boolean isGridLine) {
		if(this.isGridLine != isGridLine) {
			this.isGridLine = isGridLine;
			updateColors();
		}
	}

	public void setLocation(Point location) {
		if((getStyle() & SWT.SIMPLE) != 0) {
			titleArea.setLocation(location);
		} else {
			bounds.x = location.x;
			bounds.y = location.y;
		}
	}

	/**
	 * Requests the cell to either open or close
	 * <p>Only relevant to cells that have a toggle - ie. created with the 
	 * SWT.TOGGLE or SWT.DROP_DOWN style</p>
	 * @param open if true, the cell will open, otherwise it will close
	 */
	public void setOpen(boolean open) {
		if(this.open != open) {
			this.open = open;
			if((getStyle() & SWT.DROP_DOWN) != 0) {
				if(childArea == null) {
					childArea = new SComposite(item.container.body, getStyle());
					createChildContents(childArea, getStyle());
					for(Iterator i = dlisteners.iterator(); i.hasNext(); )	{
						DeferredListener h = (DeferredListener) i.next();
						addListener(h.eventType, h.handler);
					}
					updateColors();
					update();
				}

				updateVisibility();
			}
		}
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		updateColors();
	}

	public void setSize(Point size) {
		if((getStyle() & SWT.SIMPLE) != 0) {
			titleArea.setSize(size);
		} else {
			bounds.width = size.x;
			bounds.height = size.y;
		}
		needsLayout = true;
	}

	public void setTitleVisible(boolean visible) {
		titleVisible = visible;
		updateVisibility();
	}

	/**
	 * Sets the visibility of the cell's Toggle
	 * @param visible
	 */
	public void setToggleVisible(boolean visible) {
		toggleVisible = visible;
		updateVisibility();
	}

	/**
	 * Sets the visibility of the cell's Toggle
	 * <p>If ghost is true, then the toggle will occupy space even when not visible
	 *  - this is especially usefull for building trees</p>
	 * @param visible
	 * @param ghost
	 */
	public void setToggleVisible(boolean visible, boolean ghost) {
		toggleVisible = visible;
		ghostToggle = ghost;
		updateVisibility();
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		updateVisibility();
	}

	/**
	 * Called during setBounds. To be implemented in sub-classes
	 * @param bounds
	 * @return
	 */
	protected Rectangle snap(Rectangle bounds) {
		return bounds;
	}

	/**
	 * Updates this CTableCell with the CTableItem's data object, as returned by 
	 * item.getData(). If getData() returns null, then this method simply returns false.
	 * @return true if this cell (and thus maybe the CTable) needs its layout updated
	 * @see org.aspencloud.widgets.ccontainer#update(Object, String[])
	 */
	public boolean update() {
		Object obj = null;
		if(!item.isDisposed()) {
			obj = item.getData();
		}
		return (obj == null) ? false : update(obj, null);
	}

	/**
	 * Updates the given properties of this CTableCell with the given element
	 * <p>Functionality is only provided for a basic cell, subclasses should override</p>
	 * @param element the data to use in updating the cell
	 * @param properties the properties of the cell to be updated
	 * @return true if this cell (and thus maybe the CTable) needs its layout updated,
	 * actually calling layout on the cell and/or the CTable is left up to the caller so that
	 * performance improvements can be made - for instance, if several cells are updated, the CTable
	 * need only be updated once.
	 */
	public boolean update(Object element, String[] properties) {
		if(update) {
			update = false;
			return true;
		}
		return false;
	}

	protected void updateColors() {
		Color back;
		Color fore;
		if(selected) {
			back = item.container.getColors().getItemBackgroundSelected();
			fore = item.container.getColors().getItemForegroundSelected();
		} else if(isGridLine){
			back = (cellBackground != null) ? cellBackground : item.container.getColors().getGrid();
			fore = (cellForeground != null) ? cellForeground : item.container.getColors().getItemForegroundNormal();
		} else {
			back = (cellBackground != null) ? cellBackground : item.container.getColors().getItemBackgroundNormal();
			fore = (cellForeground != null) ? cellForeground : item.container.getColors().getItemForegroundNormal();
		}
		if((getStyle() & (SWT.SIMPLE | SWT.DROP_DOWN)) != 0) {
			List l = getColorManagedControls();
			for(Iterator iter = l.iterator(); iter.hasNext(); ) {
				Control c = (Control) iter.next();
				c.setBackground(back);
				c.setForeground(fore);
			}
		}
		background = back;
		foreground = fore;
	}

	private void updateVisibility() {
		if(titleArea != null) {
			List controls = getControls(titleArea);
			for(Iterator i = controls.iterator(); i.hasNext(); ) {
				((Control) i.next()).setVisible(titleVisible&&visible);
			}
		}
		if(childArea != null) {
			List controls = getControls(childArea);
			for(Iterator i = controls.iterator(); i.hasNext(); ) {
				((Control) i.next()).setVisible(childVisible&&open&&visible);
			}
		}
	}
}
