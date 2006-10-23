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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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


/**
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p>
 * <p>
 * Styles:
 * <ul>
 * 	<li>SWT.NONE - creates a "base" cell which must be completely custom drawn; there is
 * no Title Area or Child Area</li>
 * 	<li>SWT.DROP_DOWN - creates the Child Area</li>
 * 	<li>SWT.TITLE - creates the Title Area</li>
 * 	<li>SWT.TOGGLE - creates the CContainerCell's implementation of a toggle</li>
 * </ul>
 * </p> 
 */
public abstract class CContainerCell {
	
//	private class DeferredListener {
//		public int eventType;
//		public Listener handler;
//
//		DeferredListener(int eventType, Listener handler) {
//			this.eventType = eventType;
//			this.handler = handler;
//		}
//
//		public boolean equals(Object object) {
//			if(object instanceof DeferredListener) {
//				DeferredListener handler = (DeferredListener) object;
//				return (handler.eventType == this.eventType && handler.handler == this.handler);
//			}
//			return false;
//		}
//		
//		public boolean isType(int type) {
//			return eventType == type;
//		}
//	}

	private class EventHandler {
		List[] handlers;

		boolean add(int eventType, Listener handler) {
			if(eventType > 0 && handler != null) {
				if(handlers == null) {
					handlers = new List[4];
				}
				if(eventType >= handlers.length) {
					List[] newHandlers = new List[eventType+1];
					System.arraycopy(handlers, 0, newHandlers, 0, handlers.length);
					handlers = newHandlers;
				}
				if(handlers[eventType] == null) {
					handlers[eventType] = new ArrayList();
				}
				if(!handlers[eventType].contains(handler)) {
					handlers[eventType].add(handler);
					return true;
				}
			}
			return false;
		}

		int[] getEventTypes() {
			int count = 0;
			for(int i = 0; i < handlers.length; i++) {
				if(handlers[i] != null && !handlers[i].isEmpty()) count++;
			}
			int[] types = new int[count];
			count = 0;
			for(int i = 0; i < handlers.length; i++) {
				if(handlers[i] != null && !handlers[i].isEmpty()) types[count++] = i;
			}
			return types;
		}
		
		boolean hasHandler(int eventType) {
			return (eventType > 0 && eventType < handlers.length &&
					handlers[eventType] != null && !handlers[eventType].isEmpty());
		}

		boolean remove(int eventType, Listener handler) {
			if(eventType > 0 && eventType < handlers.length && 
					handlers[eventType] != null) {
				return handlers[eventType].remove(handler);
			}
			return false;
		}
		
		void sendEvent(Event event) {
			if(hasHandler(event.type)) {
				event.data = CContainerCell.this;
				event.item = getItem(); // TODO: necessary?
				Listener[] la = (Listener[]) handlers[event.type].toArray(new Listener[handlers[event.type].size()]);
				for(int i = 0; i < la.length; i++) {
					la[i].handleEvent(event);
				}
			}
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
	/**
	 * If true, the toggle will not be drawn, but will take up space
	 */
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
//	public int horizontalAlignment = SWT.LEFT;
//	public int verticalAlignment = SWT.CENTER;
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

	private List colorExclusions;
	private List eventExclusions;

	private EventHandler ehandler = new EventHandler();
//	private List dlisteners = new ArrayList();
	private Listener l = new Listener() {
		public void handleEvent(Event event) {
			ehandler.sendEvent(event);
		}
	};
	
	private PropertyChangeSupport pcListeners;

	/**
	 * <p>
	 * Styles:
	 * <ul>
	 * 	<li>SWT.NONE - creates a "base" cell which must be completely custom drawn; there is
	 * no Title Area or Child Area</li>
	 * 	<li>SWT.DROP_DOWN - creates the Child Area</li>
	 * 	<li>SWT.TITLE - creates the Title Area</li>
	 * 	<li>SWT.TOGGLE - creates the CContainerCell's implementation of a toggle</li>
	 * </ul>
	 * </p> 
	 */
	public CContainerCell(CContainerItem item, int style) {
		this.container = item.container;
		this.style = style;
		createContents(container.body, style);
		this.item = item;
	}

	public void addListener(int eventType, Listener handler) {
		if(ehandler.add(eventType, handler)) {
			List controls = getEventManagedControls();
			for(Iterator iter = controls.iterator(); iter.hasNext(); ) {
				Control control = (Control) iter.next();
				control.addListener(eventType, l);
			}

//			if((getStyle() & SWT.DROP_DOWN) != 0 && childArea == null) {
//				DeferredListener dl = new DeferredListener(eventType, handler);
//				boolean contains = false;
//				for(Iterator i = dlisteners.iterator(); i.hasNext(); ) {
//					if(dl.equals(i.next())) {
//						contains = true;
//						break;
//					}
//				}
//				if(!contains) dlisteners.add(dl);
//			}
		}			
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if(pcListeners == null) {
			pcListeners = new PropertyChangeSupport(this);
		}
		pcListeners.addPropertyChangeListener(listener);
	}
	
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if(pcListeners == null) {
			pcListeners = new PropertyChangeSupport(this);
		}
		pcListeners.addPropertyChangeListener(propertyName, listener);
	}
	
	protected void clearCellStateFlags() {
		cellState = 0;
	}
	
	/**
	 * Compute the preferred size of the cell for its current state (open or closed, if applicable)
	 * just the way it would be done in a regular SWT layout.
	 * <p>Implementations are to implement this themselves, though if the the cell's style is 
	 * SWT.TITLE then most likely they will simply return the computed size as found by
	 * titleArea.computeSize(int, int)</p>
	 * @param wHint
	 * @param hHint
	 * @return a Point representing the preferred size of the cell
	 */
	public abstract Point computeSize(int wHint, int hHint);

	public Rectangle computeTitleClientArea(int sizeX, int sizeY) {
		Rectangle ca = new Rectangle(0,0,sizeX,sizeY);
		ca.x = marginLeft + marginWidth + indent;
		if(toggleVisible || ghostToggle) ca.x += toggleWidth;
		ca.y = marginTop + marginHeight;
		ca.width -= (ca.x + marginRight + marginWidth);
		ca.height -= (ca.y + marginBottom + marginHeight);
		return ca;
	}
	
	/**
	 * Compute the preferred size of the cell's Title Area, similar to the way it would be done 
	 * in a regular SWT layout.
	 * <p>Implementations are to implement this themselves, though if the the cell's style is 
	 * SWT.TITLE then most likely they will simply return the computed height as found by
	 * titleArea.computeSize(int, -1).y</p> 
	 * @param wHint
	 * @return an int representing the preferred height of the cell
	 */
	public abstract int computeTitleHeight(int hHint);

	/**
	 * Create the contents of your custom cell's Child Area here
	 * <p>The Child Area is the SComposite (@see SComposite)
	 * that will appear and disappear as the cell is opened and closed</p>
	 * <p>The base cell class will call this immediately after creating its body if, and only if,
	 * the style bit for SWT.DROP_DOWN is set
	 * <p>Note that NO layout has been assigned to the parameter "contents".  Implementations
	 * must provide this or things may not work as expected</p>
	 * @param contents the Child Area of the cell
	 * @param style the style that was passed to the constructor
	 * @see CContainer#createTitleContents(Composite, int)
	 */
	protected void createChildContents(SComposite contents, int style) {}

	private void createContents(Composite parent, int style) {
		bounds = new Rectangle(0,0,0,0);
		background = parent.getBackground();
		foreground = parent.getForeground();

		if((style & SWT.TITLE) != 0) {
			createTitleArea();
			createTitleContents(titleArea, style);
		}

		if((style & SWT.DROP_DOWN) != 0) {
			// NOTE: childArea is created the first time the cell is opened
			setChildVisible(true);
		}
		
		if((style & SWT.TOGGLE) != 0) {
			setToggleVisible(true, true);
		}
	}

	protected void createTitleArea() {
		if(titleArea == null) {
			titleArea = new Composite(container.body, style);
			titleArea.setBackground(background);
			titleArea.setForeground(foreground);
			titleVisible = true;
			container.addPaintedItemListener(getPaintedItemListener());
		}
	}

	private Listener paintedItemListener;
	private Listener getPaintedItemListener() {
		if(paintedItemListener == null) {
			paintedItemListener = new Listener() {
				public void handleEvent(Event event) {
					if(event.item == item) {
						// TODO: finish up
						titleVisible = event.detail == 1;
						childVisible = event.detail == 1;
						updateVisibility();
//						if(titleArea != null && !titleArea.isDisposed()) titleArea.setVisible(event.detail == 1);
//						if(childArea != null && !childArea.isDisposed()) childArea.setVisible(event.detail == 1);
//						if(event.detail == -1) {
//							if(titleArea != null) titleArea.setBounds(0,0,0,0);
//							if(childArea != null) childArea.setBounds(0,0,0,0);
//						} else {
//							layout();
//						}
					}
				}
			};
		}
		return paintedItemListener;
	}
	
	/**
	 * Create the contents of your custom cell's Title Area here
	 * <p>The Title Area is the composite that makes up the body of the "traditional"
	 * cell; it is called "Title" because it initially sat above the Child Area like 
	 * a title or header, though this is no longer a requirement.  The Title Area may
	 * also be thought of as the "always visible" portion of a cell.</p>
	 * <p>This method is called immediately after creating the body if, and only if,
	 * the style bit for SWT.TITLE is set.  This means that it is created at the same 
	 * time as the cell (rather than lazily, like the child area), so be careful how 
	 * much code is put in here or filling a large table will take forever!</p>
	 * <p>Note that NO layout has been assigned to the parameter "contents".  Implementations
	 * must provide this or things may not work as expected.</p>
	 * @param contents the Title Area of the cell
	 * @param style the style that was passed to the constructor
	 * @see org.aspencloud.widgets.ccontainer#createChildContents(Composite, int)
	 */
	protected void createTitleContents(Composite contents, int style) {}

	boolean contains(Control control) {
		return getEventManagedControls().contains(control);
	}
	
	public void dispose() {
		if(titleArea != null && !titleArea.isDisposed()) titleArea.dispose();
		if(childArea != null && !childArea.isDisposed()) childArea.dispose();
	}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		if(pcListeners != null) {
			pcListeners.firePropertyChange(propertyName, oldValue, newValue);
		}
	}
	
	public Color getBackground() {
		return background;
	}

	public Rectangle getBounds() {
		return new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
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

	private List getColorManagedControls() {
		List l = new ArrayList(getControls(titleArea, colorExclusions));
		l.addAll(getControls(childArea, colorExclusions));
		return l;
	}

	/**
	 * A recursive utility function used to get every child control of a composite, 
	 * including the children of children.<br/>
	 * NOTE: This method will <b>NOT</b> return disposed children.
	 * @param c the composite to start from
	 * @param exclusions a list of controls to be excluded from the return list. If
	 * an item in this list is a composite, then its children will also be excluded.
	 * @return all the children and grandchildren of the given composite
	 */
	public static List getControls(Composite c, List exclusions) {
		if(c != null && !c.isDisposed()) {
			if(exclusions == null) exclusions = Collections.EMPTY_LIST;
			List l = new ArrayList();
			l.add(c);
			Control[] a = c.getChildren();
			for(int i = 0; i < a.length; i++) {
				if(!a[i].isDisposed() && !exclusions.contains(a[i])) {
					if(a[i] instanceof Composite) {
						l.addAll(getControls((Composite) a[i], exclusions));
					} else {
						l.add(a[i]);
					}
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

	private List getEventManagedControls() {
		List l = new ArrayList(getControls(titleArea, eventExclusions));
		l.addAll(getControls(childArea, eventExclusions));
		return l;
	}

	public Color getForeground() {
		return foreground;
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

	public Rectangle getTitleClientArea() {
		return computeTitleClientArea(bounds.width, bounds.height);
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
	public int handleMouseEvent(Event event, boolean selectionActive) {
		// TODO: handleMouseEvent uses bitwise OR'ed return code, allow "consumed" code?
		int result = RESULT_NONE;
		switch(event.type) {
		case SWT.MouseDown:
			if(bounds != null) {
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
	 */
	protected abstract void layout();

	/**
	 * Called by Container during its paint method to draw the cell's Title Area
	 * <p>Subclasses need to override if they want to have a visual representation</p>
	 * <p>If SWT.TITLE is set this method may just return, but is not required to.</p>
	 * @param gc the GC upon which the cell will be painted
	 * @param ebounds the bounding rectangle of the Container's paint event
	 */
	public abstract void paint(GC gc, Rectangle ebounds);

	protected void paintToggle(GC gc, Rectangle ebounds) {
		Rectangle bounds = getBounds();
		double x = indent + ((toggleBounds.width - pointsWidth) / 2) + bounds.x - ebounds.x;
		double y = ((toggleBounds.height - pointsWidth) / 2) + toggleBounds.y - ebounds.y;
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
		if(ehandler.remove(eventType, handler)) {
			List controls = getEventManagedControls();
			for(Iterator iter = controls.iterator(); iter.hasNext(); ) {
				Control control = (Control) iter.next();
				control.removeListener(eventType, handler);
			}
	
//			if((getStyle() & SWT.DROP_DOWN) != 0) {
//				DeferredListener h = new DeferredListener(eventType, handler);
//				for(Iterator i = dlisteners.iterator(); i.hasNext(); ) {
//					if(h.equals(i.next())) i.remove();
//				}
//			}
		}
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		if(pcListeners != null) {
			pcListeners.removePropertyChangeListener(listener);
		}
	}
	
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if(pcListeners != null) {
			pcListeners.removePropertyChangeListener(propertyName, listener);
		}
	}
	
	public void setBounds(int x, int y, int width, int height) {
		Rectangle bounds = new Rectangle(x, y, width, height);
		setBounds(bounds);
	}

	public void setBounds(Rectangle bounds) {
		bounds = snap(bounds);
		this.bounds.x = bounds.x;
		this.bounds.y = bounds.y;
		this.bounds.width = bounds.width;
		this.bounds.height = bounds.height;
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

	protected void setExclusions(Control exclude) {
		colorExclusions = eventExclusions = Collections.singletonList(exclude);
	}

	protected void setExclusions(List colors, List events) {
		colorExclusions = colors;
		eventExclusions = events;
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
		bounds.x = location.x;
		bounds.y = location.y;
		if(titleArea != null && !titleArea.isDisposed()) {
			Rectangle ca = getTitleClientArea();
			titleArea.setLocation(ca.x, ca.y);
		}
	}
	
	/**
	 * Requests the cell to either open or close
	 * <p>The base implementation creates the Child Area the first time
	 * setOpen(true) is called (if, of course, the cell was created with 
	 * the SWT.DROP_DOWN style).</p>
	 * <p>What exactly open refers to can be overridden by subclasses</p>
	 * @param open if true, the cell will open, otherwise it will close
	 */
	public void setOpen(boolean open) {
		if(this.open != open) {
			this.open = open;
			if((getStyle() & SWT.DROP_DOWN) != 0) {
				if(childArea == null) {
					childArea = new SComposite(item.container.body, SComposite.NONE);
					container.addPaintedItemListener(getPaintedItemListener());
					createChildContents(childArea, getStyle());
					// TODO: review...
					int[] types = ehandler.getEventTypes();
					for(int i = 0; i < types.length; i++) {
						List controls = getEventManagedControls();
						for(Iterator iter = controls.iterator(); iter.hasNext(); ) {
							Control control = (Control) iter.next();
							control.addListener(types[i], l);
						}
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
		bounds.width = size.x;
		bounds.height = size.y;
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
	 * @return true if this cell (and thus maybe its container) needs its layout updated,
	 * actually calling layout on the cell and/or the CContainer is left up to the caller so that
	 * performance improvements can be made - for instance, if several cells are updated, the CContainer
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
		if((childArea != null && !childArea.isDisposed()) || 
				(titleArea != null && !titleArea.isDisposed())) {
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
		// TODO: finish up
		if(titleArea != null && !titleArea.isDisposed()) {
//			List controls = getControls(titleArea);
//			for(Iterator i = controls.iterator(); i.hasNext(); ) {
//				((Control) i.next()).setVisible(titleVisible&&visible);
//			}
			titleArea.setVisible(titleVisible&&visible);
		}
		if(childArea != null && !childArea.isDisposed()) {
//			List controls = getControls(childArea);
//			for(Iterator i = controls.iterator(); i.hasNext(); ) {
//				((Control) i.next()).setVisible(childVisible&&open&&visible);
//			}
			childArea.setVisible(childVisible&&visible);
		}
	}
}
