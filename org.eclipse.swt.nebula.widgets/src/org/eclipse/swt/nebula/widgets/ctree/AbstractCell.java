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

package org.eclipse.swt.nebula.widgets.ctree;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
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
public abstract class AbstractCell {

//	private class DeferredListener {
//	public int eventType;
//	public Listener handler;

//	DeferredListener(int eventType, Listener handler) {
//	this.eventType = eventType;
//	this.handler = handler;
//	}

//	public boolean equals(Object object) {
//	if(object instanceof DeferredListener) {
//	DeferredListener handler = (DeferredListener) object;
//	return (handler.eventType == this.eventType && handler.handler == this.handler);
//	}
//	return false;
//	}

//	public boolean isType(int type) {
//	return eventType == type;
//	}
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
			if(handlers != null && eventType > 0 && eventType < handlers.length && 
					handlers[eventType] != null) {
				return handlers[eventType].remove(handler);
			}
			return false;
		}

		void sendEvent(Event event) {
			if(hasHandler(event.type)) {
				event.data = AbstractCell.this;
				event.item = getItem(); // TODO: necessary?
				Listener[] la = (Listener[]) handlers[event.type].toArray(new Listener[handlers[event.type].size()]);
				for(int i = 0; i < la.length; i++) {
					la[i].handleEvent(event);
				}
			}
		}
	}

	public static final boolean carbon = "carbon".equals(SWT.getPlatform());
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
//	Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	private static final Color fgNorm = gtk ? border :
		Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);

	private static final int[] closedPoints = gtk ? new int[] { 2,  0,  7, 5,  2, 10 } :
//		new int[] { 2,4, 4,4, 4,2, 5,2, 5,4, 7,4, 7,5, 5,5, 5,7, 4,7, 4,5, 2,5 };
		new int[] { 2,4, 6,4, 4,4, 4,2, 4,4, 4,6 };
	private static final int[] openPoints 	= gtk ? new int[] { 10, 2,  5, 7,  0, 2  } :
		new int[] { 2,4, 6,4 };//, 7,5, 2,5 };
	private static final int pointsWidth = gtk ? 11 : 8;

	/**
	 * A recursive utility function used to get every child control of a composite, 
	 * including the children of children.<br/>
	 * NOTE: This method will <b>NOT</b> return disposed children.
	 * @param c the composite to start from
	 * @param exclusions a list of controls to be excluded from the return list. If
	 * an item in this list is a composite, then its children will also be excluded.
	 * @return all the children and grandchildren of the given composite
	 */
	private static List getControls(Control c, List exclusions) {
		if(c != null && !c.isDisposed()) {
			if(exclusions == null) {
				exclusions = Collections.EMPTY_LIST;
			} else if(exclusions.contains(c)) {
				return Collections.EMPTY_LIST;
			}

			List l = new ArrayList();
			l.add(c);
			if(c instanceof Composite) {
				Control[] a = ((Composite) c).getChildren();
				for(int i = 0; i < a.length; i++) {
					if(!a[i].isDisposed() && !exclusions.contains(a[i])) {
						if(a[i] instanceof Composite) {
							l.addAll(getControls(a[i], exclusions));
						} else {
							l.add(a[i]);
						}
					}
				}
			}
			return l;
		} else {
			return Collections.EMPTY_LIST;
		}
	}
	protected AbstractContainer container;

	protected AbstractItem item;
	protected int style;
	private Button check;
	private Control control;
	int hAlign = SWT.FILL;
	int vAlign = SWT.FILL;
	private Control childControl;
	protected boolean update = true;
	protected boolean open = false;
	protected boolean selected = false;
	protected boolean visible = true;
	protected boolean childVisible = false;
	protected boolean toggleVisible = false;
	private   boolean painted = false;
	private	  boolean firstPainting = true;
	private	  boolean firstChildPainting = true;
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

	public int toggleWidth = 16;
	protected Rectangle bounds;
	protected Rectangle boundsOld;
	protected Rectangle childBounds;
	protected Rectangle childBoundsOld;
	private boolean hasChild;

	private Point scrollPos;
	protected Rectangle toggleBounds = new Rectangle(0,0,0,0);
	protected Font font;
	protected Color activeBackground;
	protected Color activeForeground;
	protected Color storedBackground = null;
	protected Color storedForeground = null;
	protected boolean isGridLine = false;

	private int cellState = CELL_NORMAL;
	private List colorExclusions;

	private List eventExclusions;
	private EventHandler ehandler = new EventHandler();

	private Listener l = new Listener() {
		public void handleEvent(Event event) {
			ehandler.sendEvent(event);
		}
	};

	private PropertyChangeSupport pcListeners;

	private boolean isCheck = false;

	private boolean isChecked = false;

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
	public AbstractCell(AbstractItem item, int style) {
		this.container = item.container;
		this.item = item;
		this.style = style;
		bounds = new Rectangle(0,0,0,0);
		if((style & SWT.CHECK) != 0) {
			isCheck = true;
		}
		if((style & SWT.DROP_DOWN) != 0) {
			hasChild = true;
			setChildVisible(true);
		} else {
			hasChild = false;
		}
		if((style & SWT.TOGGLE) != 0) {
			setToggleVisible(true, true);
		}
	}

	public void addListener(int eventType, Listener handler) {
		if(ehandler.add(eventType, handler)) {
			List controls = getEventManagedControls();
			for(Iterator iter = controls.iterator(); iter.hasNext(); ) {
				Control control = (Control) iter.next();
				control.addListener(eventType, l);
			}

//			if((getStyle() & SWT.DROP_DOWN) != 0 && childArea == null) {
//			DeferredListener dl = new DeferredListener(eventType, handler);
//			boolean contains = false;
//			for(Iterator i = dlisteners.iterator(); i.hasNext(); ) {
//			if(dl.equals(i.next())) {
//			contains = true;
//			break;
//			}
//			}
//			if(!contains) dlisteners.add(dl);
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

	Point computeChildSize(int wHint, int hHint) { return null; }

	abstract public Point computeClientSize(int wHint, int hHint);
	Point computeControlSize(int wHint, int hHint) {
		return (control != null) ? control.computeSize(wHint, hHint) : new Point(0,0);
	}
	/**
	 * Compute the preferred size of the cell for its current state (open or closed, if applicable)
	 * just the way it would be done in a regular SWT layout.
	 * @param wHint
	 * @param hHint
	 * @return a Point representing the preferred size of the cell
	 */
	abstract Point computeSize(int wHint, int hHint);

	boolean contains(Control control) {
		return getEventManagedControls().contains(control);
	}

	private Button createCheck() {
		if(isCheck) {
			Button b = new Button(container, SWT.CHECK);
			b.setBackground(activeBackground);
			b.setForeground(activeForeground);
			b.setSelection(isChecked);
			b.addListener(SWT.FocusIn, new Listener() {
				public void handleEvent(Event event) {
					if(SWT.FocusIn == event.type) container.setFocus();
				}
			});
			b.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					if(SWT.Selection == event.type) {
						isChecked = ((Button) event.widget).getSelection();
					}
				}
			});
			// TODO set size of check
			b.setSize(b.computeSize(-1, -1));
			return b;
		}
		return null;
	}

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
	 * @see AbstractContainer#createControl(Composite, int)
	 */
	protected Control createChildControl(Composite contents) { return null; }

	/**
	 * Create the contents of your custom cell here
	 * <p>The Title Area is the composite that makes up the body of the "traditional"
	 * cell; it is called "Title" because it initially sat above the Child Area like 
	 * a title or header, though this is no longer a requirement.  The Title Area may
	 * also be thought of as the "always visible" portion of a cell.</p>
	 * <p>This method is called immediately before the cell is painted for the first time.
	 * @param style the style that was passed to the constructor
	 * @see org.aspencloud.widgets.ccontainer#createChildControl(Composite, int)
	 */
	protected Control createControl(Composite contents) { return null; }

	void dispose() {
//		if(titleArea != null && !titleArea.isDisposed()) titleArea.dispose();
//		if(childArea != null && !childArea.isDisposed()) childArea.dispose();
	}

	protected void doFirstChildPainting() {}

	protected void doFirstPainting() {}

	protected abstract void doPaint(GC gc, Point offset);

	protected void doSetPainted(boolean painted) {}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		if(pcListeners != null) {
			pcListeners.firePropertyChange(propertyName, oldValue, newValue);
		}
	}

	public Color getBackground() {
		return activeBackground;
	}

	public Rectangle getBounds() {
		return new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	public Color getCellBackground() {
		return storedBackground;
	}

	public Color getCellForeground() {
		return storedForeground;
	}

	public int getCellState() {
		return cellState;
	}

	public boolean getChecked() {
		return isChecked;
	}

	public Rectangle getClientArea() {
		Rectangle ca = new Rectangle(0,0,bounds.width, bounds.height);
		ca.x = marginLeft + marginWidth + indent;
		if(toggleVisible || ghostToggle) ca.x += toggleWidth;
		ca.y = marginTop + marginHeight;
		ca.width -= (ca.x + marginRight + marginWidth);
		ca.height -= (ca.y + marginBottom + marginHeight);
		return ca;
	}

	private List getColorManagedControls() {
		List l = new ArrayList(getControls(control, colorExclusions));
		if(check != null) l.add(check);
		return l;
	}

	protected Display getDisplay() {
		return container.getDisplay();
	}

	private List getEventManagedControls() {
		List l = new ArrayList(getControls(control, eventExclusions));
		if(check != null) l.add(check);
		return l;
	}

	public Font getFont() {
		return (font == null || font.isDisposed()) ? null : font;
	}

	public Color getForeground() {
		return activeForeground;
	}

	public int getIndent() {
		return indent;
	}

	public AbstractItem getItem() {
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

	public Rectangle getToggleBounds() {
		return mapRectangle(toggleBounds);
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

	abstract void internalFirstPainting();

	protected GC internalGC() {
		return container.internalGC;
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

	protected void layout() {}


	protected abstract void layout(Control control);
	
	private void layout(int eventType) {
		container.layout(eventType, this);
	}

	protected void layoutChild() {}

	protected void layoutChild(Control childControl) {}

	protected abstract void layoutInternal();

	protected abstract void locate(Control control);

	protected abstract void locateCheck(Button check);

	//	protected abstract void internalLocateChild(Control control) {}
	protected void locateChild() {}

	// TODO: map methods?
	Rectangle mapRectangle(int x, int y, int width, int height) {
		Rectangle r = new Rectangle(x,y,width,height);
		r.x += bounds.x;
		r.y += bounds.y;
		return r;
	}

	Rectangle mapRectangle(Rectangle rect) {
		return mapRectangle(rect.x, rect.y, rect.width, rect.height);
	}

	/**
	 * Called by Container during its paint method to draw the cell's Title Area
	 * <p>Subclasses need to override if they want to have a visual representation</p>
	 * <p>If SWT.TITLE is set this method may just return, but is not required to.</p>
	 * @param gc the GC upon which the cell will be painted
	 * @param ebounds the bounding rectangle of the Container's paint event
	 */
	void paint(GC gc, Rectangle ebounds) {
		if(!painted || bounds.isEmpty()) return;

		updateColors();
		layoutInternal();
		layout();

		scrollPos = container.getScrollPosition();
		doPaint(gc, new Point(bounds.x-scrollPos.x-ebounds.x,bounds.y-scrollPos.y-ebounds.y));
	}

	void adjust() {
		if(boundsOld == null) boundsOld = new Rectangle(-1,-1,0,0);
		if(scrollPos == null) scrollPos = new Point(-1,-1);

		boolean didLayout = false;
		if(bounds.width != boundsOld.width || bounds.height != boundsOld.height) {
			layoutInternal();
			if(check != null) locateCheck(check);
			layout();
			if(control != null) layout(control);
			boundsOld.x = bounds.x;
			boundsOld.y = bounds.y;
			boundsOld.width = bounds.width;
			boundsOld.height = bounds.height;
			didLayout = true;
		}

		if(check != null) locateCheck(check);
		if(control != null) locate(control);
		Point newSPos = container.getScrollPosition();
		if(!didLayout && (bounds.x != boundsOld.x || bounds.y != boundsOld.y || 
				scrollPos.x != newSPos.x || scrollPos.y != newSPos.y)) {
			if(check != null) locateCheck(check);
			if(control != null) locate(control);
			boundsOld.x = bounds.x;
			boundsOld.y = bounds.y;
		}
		if(hasChild && open) {
			if(firstChildPainting) {
				firstChildPainting = false;
				childBoundsOld = new Rectangle(-1,-1,0,0);
				childBounds = new Rectangle(0,0,10,10);
				doFirstChildPainting();
			}
//			if(!holdChild) childControl = createChildControl(container);
			didLayout = false;
			if(childBounds.width != childBoundsOld.width || childBounds.height != childBoundsOld.height) {
				layoutChild();
				if(childControl != null) layoutChild(childControl);
				childBoundsOld.x = childBounds.x;
				childBoundsOld.y = childBounds.y;
				childBoundsOld.width = childBounds.width;
				childBoundsOld.height = childBounds.height;
				didLayout = true;
			}
			if(!didLayout && (childBounds.x != childBoundsOld.x || childBounds.y != childBoundsOld.y || 
					scrollPos.x != newSPos.x || scrollPos.y != newSPos.y)) {
				locateChild();
//				if(childControl != null) locateChild(childControl);
				childBoundsOld.x = childBounds.x;
				childBoundsOld.y = childBounds.y;
			}
		}
		scrollPos = newSPos;
	}

	protected void paintToggle(GC gc, Point offset) {
		double x = indent + ((toggleBounds.width - pointsWidth) / 2) + offset.x;
		double y = ((toggleBounds.height - pointsWidth) / 2) + toggleBounds.y + offset.y;
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
		} else if(carbon) {
			// TODO: carbon toggle
			gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_GRAY));
			gc.setAntialias(SWT.ON);
			gc.fillPolygon(pts);
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

	public void redraw() {
		container.redraw(this);
	}

	public void removeListener(int eventType, Listener handler) {
		if(ehandler.remove(eventType, handler)) {
			List controls = getEventManagedControls();
			for(Iterator iter = controls.iterator(); iter.hasNext(); ) {
				Control control = (Control) iter.next();
				control.removeListener(eventType, handler);
			}

//			if((getStyle() & SWT.DROP_DOWN) != 0) {
//			DeferredListener h = new DeferredListener(eventType, handler);
//			for(Iterator i = dlisteners.iterator(); i.hasNext(); ) {
//			if(h.equals(i.next())) i.remove();
//			}
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

	protected void restoreState(Map memento) {
		// base implementation so subclass do not need to override
	}

//	public void setCellBackground(Color color) {
//	cellBackground = color;
//	updateColors();
//	}
//	public void setCellForeground(Color color) {
//	cellForeground = color;
//	updateColors();
//	}

	protected Map retrieveState() {
		return Collections.EMPTY_MAP;
	}

	public void setBackground(Color color) {
		storedBackground = color;
		updateColors();
	}

	protected void setBounds(int x, int y, int width, int height) {
		bounds.x = x;
		bounds.y = y;
		bounds.width = width;
		bounds.height = height;
		snap(bounds);
	}

	protected void setBounds(Rectangle bounds) {
		setBounds(bounds.x,bounds.y,bounds.width,bounds.height);
	}

	protected void setCellStateFlag(int flag, boolean on) {
		if(on) {
			cellState |= flag;
		} else {
			cellState &= ~flag;
		}
	}

	public void setCheck(boolean check) {
		if(isCheck != check) {
			isCheck = check;
			if(!check) {
				isChecked = false;
				if(this.check != null) {
					this.check.dispose();
					this.check = null;
				}
			}
		}
	}

	public void setChecked(boolean checked) {
		if(isCheck) isChecked = checked;
	}

	public void setChildVisible(boolean visible) {
		childVisible = visible;
		updateVisibility();
	}

	protected void setControlLayoutData(int horizontalAlignment, int verticalAlignment) {
		hAlign = horizontalAlignment;
		vAlign = verticalAlignment;
	}

	protected void setCursor(int id) {
		container.setCursor(getDisplay().getSystemCursor(id));
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

	public void setFont(Font font) {
		this.font = font;
//		TODO: redraw();
	}

	public void setForeground(Color color) {
		storedForeground = color;
		updateColors();
	}
	public void setIndent(int indent) {
		this.indent = indent;
	}
	public void setIsGridLine(boolean isGridLine) {
		if(this.isGridLine != isGridLine) {
			this.isGridLine = isGridLine;
			updateColors();
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
			if(hasChild) {
				// TODO: setOpen - childArea
//				if(childArea == null) {
//				childArea = new SComposite(container, SComposite.NONE);
//				container.addPaintedItemListener(getPaintedItemListener());
//				createChildContents(childArea, getStyle());
//				// TODO: review...
//				int[] types = ehandler.getEventTypes();
//				for(int i = 0; i < types.length; i++) {
//				List controls = getEventManagedControls();
//				for(Iterator iter = controls.iterator(); iter.hasNext(); ) {
//				Control control = (Control) iter.next();
//				control.addListener(types[i], l);
//				}
//				}
//				updateColors();
//				update();
//				}

				updateVisibility();
			}
			layout(SWT.Resize);
		}
	}

	private boolean holdControl = true;
	private boolean holdChild = true;
	void setPainted(boolean painted) {
		if(this.painted != painted) {
			this.painted = painted;
			if(painted) {
				if(firstPainting) {
					firstPainting = false;
					internalFirstPainting();
					doFirstPainting();
					check = createCheck();
				}
//				check = createCheck();
				if(check != null) check.setVisible(true);
				if(control == null) {
					control = createControl(container);
				} else {
					control.setVisible(true);
				}
			} else {
				boundsOld = null;
				scrollPos = null;
				if(check != null) check.setVisible(false);
//				if(check != null) {
//					check.dispose();
//					check = null;
//				}
				if(control != null) {
					if(holdControl) {
						control.setVisible(false);
					} else {
						control.dispose();
						control = null;
					}
				}
				if(childControl != null) {
					if(holdChild) {
						childControl.setVisible(painted);
					} else {
						childControl.dispose();
						childControl = null;
					}
				}
			}

			doSetPainted(painted);
		}
	}

	public Control getControl() { return check; }
	
	public void setSelected(boolean selected) {
		this.selected = selected;
		updateColors();
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
	 */
	protected void snap(Rectangle bounds) {
		// subclasses to implement
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
			back = (storedBackground != null) ? storedBackground : item.container.getColors().getGrid();
			fore = (storedForeground != null) ? storedForeground : item.container.getColors().getItemForegroundNormal();
		} else {
			back = (storedBackground != null) ? storedBackground : item.container.getColors().getItemBackgroundNormal();
			fore = (storedForeground != null) ? storedForeground : item.container.getColors().getItemForegroundNormal();
		}

		activeBackground = back;
		activeForeground = fore;

		// TODO: updateColors - childArea
//		if((childArea != null && !childArea.isDisposed()) || 
//		(titleArea != null && !titleArea.isDisposed())) {
		List l = getColorManagedControls();
		for(Iterator iter = l.iterator(); iter.hasNext(); ) {
			Control c = (Control) iter.next();
//			c.setBackground(getDisplay().getSystemColor(SWT.COLOR_BLUE));
			c.setBackground(activeBackground);
//			c.setForeground(activeForeground);
		}
//		}
	}

	private void updateVisibility() {
		// TODO: redo updateVisibility
//		if(titleArea != null && !titleArea.isDisposed()) {
//		List controls = getControls(titleArea);
//		for(Iterator i = controls.iterator(); i.hasNext(); ) {
//		((Control) i.next()).setVisible(titleVisible&&visible);
//		}
//		titleArea.setVisible(titleVisible&&visible);
//		}
//		if(childArea != null && !childArea.isDisposed()) {
//		List controls = getControls(childArea);
//		for(Iterator i = controls.iterator(); i.hasNext(); ) {
//		((Control) i.next()).setVisible(childVisible&&open&&visible);
//		}
//		childArea.setVisible(childVisible&&visible);
//		}
	}
}
