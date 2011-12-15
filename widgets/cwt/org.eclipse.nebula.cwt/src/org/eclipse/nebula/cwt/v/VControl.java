/****************************************************************************
 * Copyright (c) 2008, 2009 Jeremy Dowdall
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/

package org.eclipse.nebula.cwt.v;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.nebula.cwt.svg.SvgDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

/**
 * A VControl is a class wich wraps an SWT Button to create a widget that acts
 * as much like a native Button as possible while adding the following features:
 * <ul>
 * <li>The appearance of Label when the mouse is not over it and it does not
 * have the focus or selection (if style is SWT.TOGGLE).</li>
 * <li>Can fit seemlessly into a larger visual piece - simple set the image to
 * that of its background and adjust the image's offset if necessary.</li>
 * <li>Can draw polygons and ovals.</li>
 * <li>Can center or otherwise align its visual display (text, image, polygon
 * or oval).</li>
 * </ul>
 */
public abstract class VControl {

	public enum Type {
		Button, Custom, Label, Native, Panel, Text, Spacer
	}

	/**
	 * true if the platform is Carbon, false otherwise
	 */
	public static final boolean carbon = "carbon".equals(SWT.getPlatform()); //$NON-NLS-1$

	/**
	 * true if the platform is GTK, false otherwise
	 */
	public static final boolean gtk = "gtk".equals(SWT.getPlatform()); //$NON-NLS-1$

	/**
	 * true if the platform is Win32, false otherwise
	 */
	public static final boolean win32 = "win32".equals(SWT.getPlatform()); //$NON-NLS-1$

	private static final int[] Points_OK = { 2, 6, 5, 9, 10, 3, 9, 2, 5, 7, 3, 5 };
	private static final int[] Points_Cancel = { 0, 1, 3, 4, 0, 7, 1, 8, 4, 5, 7, 8, 8, 7, 5, 4, 8, 1, 7, 0, 4, 3, 1, 0 };
	private static final int[] Points_Left = { 9, 0, 4, 5, 9, 10 };
	private static final int[] Points_Right = { 2, 0, 7, 5, 2, 10 };
	private static final int[] Points_Up = { 10, 8, 5, 3, 0, 8 };
	private static final int[] Points_Down = { 10, 2, 5, 7, 0, 2 };
	private static final int[] Points_Add = { 2, 4, 4, 4, 4, 2, 5, 2, 5, 4, 7, 4, 7, 5, 5, 5, 5, 7, 4, 7, 4, 5, 2, 5 };
	private static final int[] Points_Subtract = { 2, 4, 7, 4, 7, 5, 2, 5 };

	// public static final int STATE_INACTIVE = 1 << 0;
	public static final int STATE_ACTIVE = 1 << 1;
	public static final int STATE_SELECTED = 1 << 2;
//	public static final int STATE_FOCUS = 1 << 3;
	public static final int STATE_ENABLED = 1 << 4;
	public static final int STATE_MOUSE_DOWN = 1 << 5;

	protected final static boolean containsControl(Control control, Composite composite) {
		if(composite != null && !composite.isDisposed()) {
			Control[] children = composite.getChildren();
			for(Control child : children) {
				if(!child.isDisposed()) {
					if(child == control) {
						return true;
					} else if(child instanceof Composite){
						return containsControl(control, (Composite) child);
					}
				}
			}
		}
		return false;
	}
	
	Composite composite;
	VPanel parent;
	private int style;
	Menu menu;
	Image image;
	SvgDocument svg;
	String text;
	String tooltipText;
	int[] points;
	Color fill;
	Color foreground;
	Color background;
	private Cursor activeCursor;
	private Cursor inactiveCursor;

	GridData layoutData;

	private int state = STATE_ENABLED;
	Rectangle bounds;
	int marginTop = 5;
	int marginBottom = 5;
	int marginLeft = 5;
	int marginRight = 5;
	int xAlign;

	int yAlign;

	boolean disposed = false;
	boolean square = false;
	int visibility = 100;
	boolean scaleImage = false;
	boolean customToolTip = false;

	IControlPainter painter;
	Map<String, Object> dataMap;
	Map<Integer, List<Listener>> listeners = new HashMap<Integer, List<Listener>>();
	private Set<Integer> eventTypes = new HashSet<Integer>();

	private Listener listener = new Listener() {
		public void handleEvent(Event event) {
			if(event.type == SWT.FocusIn) {
				if(VControl.this == VTracker.getFocusControl()) {
					return;
				}
			}
			VControl.this.handleEvent(event);
		}
	};

	private boolean activatable = true;
	
	/**
	 * Javadoc out of date // TODO: update javadoc
	 * @param panel
	 * @param style
	 */
	public VControl(VPanel panel, int style) {
		setParent(panel);

		this.style = style;
		bounds = new Rectangle(0, 0, 0, 0);

		if((style & SWT.OK) != 0) {
			setPolygon(Points_OK);
			setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN));
		} else if((style & SWT.CANCEL) != 0) {
			setPolygon(Points_Cancel);
			setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED));
		} else if((style & SWT.ARROW) != 0) {
			if((style & SWT.DOWN) != 0) {
				setPolygon(Points_Down);
			} else if((style & SWT.LEFT) != 0) {
				setPolygon(Points_Left);
			} else if((style & SWT.RIGHT) != 0) {
				setPolygon(Points_Right);
			} else if((style & SWT.UP) != 0) {
				setPolygon(Points_Up);
			}
		} else if((style & SWT.UP) != 0) {
			setPolygon(Points_Add);
		} else if((style & SWT.DOWN) != 0) {
			setPolygon(Points_Subtract);
		}

		if(foreground == null) {
			setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
		}
		if(fill == null) {
			setFill(getForeground());
		}
	}

	void handleEvent(Event event) {
		event.data = this;
		filterEvent(event);
		if(listeners.containsKey(event.type)) {
			Listener[] la = listeners.get(event.type).toArray(new Listener[listeners.get(event.type).size()]);
			for(Listener listener : la) {
				listener.handleEvent(event);
			}
		}
	}
	
	void activate() {
		if(activatable && hasState(STATE_ENABLED) && setState(STATE_ACTIVE, true)) {
			setState(STATE_MOUSE_DOWN, VTracker.isMouseDown());
			setCursor(activeCursor);
			attachListeners(false);
			if(redrawOnActivate()) {
				redraw();
			}
			notifyListeners(SWT.Activate);
		}
	}
	
	void addListener(int eventType) {
		eventTypes.add(eventType);
		if(hasState(STATE_ACTIVE)) {
			composite.addListener(eventType, listener);
		}
	}

	public void addListener(int eventType, Listener listener) {
		if(!listeners.containsKey(eventType)) {
			listeners.put(eventType, new ArrayList<Listener>());
		}
		listeners.get(eventType).add(listener);
		if(hasState(STATE_ACTIVE)) {
			composite.addListener(eventType, listener);
		}
	}

	void attachListeners(boolean keyListeners) {
//			if(keyListeners && this != VTracker.getFocusControl()) {
//				System.out.println("this: " + this + ", focusControl: " + VTracker.getFocusControl());
//				throw new UnsupportedOperationException();
//			}
//			if(!keyListeners || this == VTracker.getFocusControl()) {
				Set<Integer> eventTypes = new HashSet<Integer>(this.eventTypes);
				eventTypes.addAll(listeners.keySet());
				for(Integer eventType : eventTypes) {
					if(include(keyListeners, eventType)) {
						composite.addListener(eventType, listener);
					}
				}
//			}
	}

	void detachListeners(boolean keyListeners) {
		Set<Integer> eventTypes = new HashSet<Integer>(this.eventTypes);
		eventTypes.addAll(listeners.keySet());
		for(Integer eventType : eventTypes) {
			if(include(keyListeners, eventType)) {
				composite.removeListener(eventType, listener);
			}
		}
	}
	
	public Point computeSize(int wHint, int hHint) {
		return computeSize(wHint, hHint, true);
	}

	public Point computeSize(int wHint, int hHint, boolean changed) {
		if(wHint != SWT.DEFAULT && wHint < 0) {
			wHint = 0;
		}
		if(hHint != SWT.DEFAULT && hHint < 0) {
			hHint = 0;
		}

		Point size = new Point(2, 2);

		if(image != null) {
			Rectangle r = image.getBounds();
			size.x = r.width;
			size.y = r.height;
		} else if(points != null) {
			if(points.length > 2) {
				int minX = points[0];
				int maxX = points[0];
				int minY = points[1];
				int maxY = points[1];
				for(int i = 2; i < (points.length - 1); i++) {
					minX = Math.min(minX, points[i]);
					maxX = Math.max(maxX, points[i]);
					minY = Math.min(minY, points[i + 1]);
					maxY = Math.max(maxY, points[i + 1]);
				}
				size.x += maxX - minX;
				size.y += maxY - minY;
			} else {
				size.x += points[0];
				size.y += points[1];
			}
		}

		if(text != null) {
			GC gc = new GC(composite);
			Point tSize = gc.textExtent(text);
			gc.dispose();
			size.x += tSize.x;
			size.y += tSize.y;
		}

		size.x += (marginLeft + marginRight);
		size.y += (marginTop + marginBottom);

		if(square) {
			size.x = size.y = Math.max(size.x, size.y);
		}

		return size;
	}

	public Menu createMenu() {
		menu = new Menu(composite);
		addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				if(SWT.MouseDown == event.type && event.button == 3) {
					menu.setVisible(true);
				}
			}
		});
		return menu;
	}

	void deactivate() {
		if(setState(STATE_ACTIVE, false)) {
			setState(STATE_MOUSE_DOWN, false);
			setCursor(inactiveCursor);
			detachListeners(false);
			if(redrawOnDeactivate()) {
				redraw();
			}
			notifyListeners(SWT.Deactivate);
		}
	}

	public void dispose() {
		if(!disposed) {
			disposed = true;

			notifyListeners(SWT.Dispose, new Event());

			if(this == VTracker.getActiveControl()) {
				VTracker.instance().deactivate(this);
			}
			if(this == VTracker.getFocusControl()) {
				VTracker.instance().setFocusControl(null);
			}
			if(!composite.isDisposed()) {
				detachListeners(true);
				detachListeners(false);
			}
			setParent(null);
			if(painter != null) {
				painter.dispose();
			}
			listeners.clear();
			listeners = null;
			text = null;
			// tooltip = null;
			tooltipText = null;
			image = null;
			points = null;
		}
	}

	public Color getBackground() {
		if(background != null) {
			return background;
		}
		VPanel p = parent;
		while(p != null) {
			if(p.background != null)
				return p.background;
			p = p.parent;
		}
		return composite.getBackground();
	}

	public Rectangle getBounds() {
		return new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	public Rectangle getClientArea() {
		return new Rectangle(bounds.x + marginLeft, bounds.y + marginTop, bounds.width - (marginLeft + marginRight),
				bounds.height - (marginTop + marginBottom));
	}

	public Point getClientSize() {
		return new Point(bounds.width - (marginLeft + marginRight), bounds.height - (marginTop + marginBottom));
	}

	public Composite getComposite() {
		return composite;
	}

	public Control getControl() {
		return composite;
	}

	public Object getData(Enum<?> name) {
		return getData(name.name());
	}

	public <T> T getData(Enum<?> name, Class<T> clazz) {
		return getData(name.name(), clazz);
	}

	public Object getData(String name) {
		if(dataMap != null) {
			return dataMap.get(name);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T> T getData(String name, Class<T> clazz) {
		if(dataMap != null) {
			return (T) dataMap.get(name);
		}
		return null;
	}

	public Display getDisplay() {
		return composite.getDisplay();
	}

	public boolean getEnabled() {
		return hasState(STATE_ENABLED);
	}

	public Color getForeground() {
		return foreground;
	}

	public Image getImage() {
		return image;
	}

	public GridData getLayoutData() {
		return (layoutData != null) ? layoutData : new GridData();
	}

	protected Listener[] getListeners(int eventType) {
		List<Listener> l = listeners.get(eventType);
		return l.toArray(new Listener[l.size()]);
	}
	
	public Point getLocation() {
		return new Point(bounds.x, bounds.y);
	}

	public Rectangle getMargins() {
		return new Rectangle(marginLeft, marginRight, marginTop, marginBottom);
	}

	public Menu getMenu() {
		return menu;
	}

	public VPanel getParent() {
		return parent;
	}

	public Shell getShell() {
		return composite.getShell();
	}

	public Point getSize() {
		return new Point(bounds.width, bounds.height);
	}

	public int getState() {
		return state;
	}

	public int getStyle() {
		return style;
	}

	/**
	 * @return the text string displayed on this VControl
	 */
	public String getText() {
		return text;
	}

	public String getToolTipText() {
		return (tooltipText != null) ? tooltipText : "";
	}

	public abstract Type getType();

	public int getVisibility() {
		return visibility;
	}

	public boolean getVisible() {
		return visibility > 0;
	}
	
	public Composite getWidget() {
		return getParent().getWidget();
	}

	protected void filterEvent(Event event) {
		// subclasses to implement if necessary
	}

	public boolean hasState(int state) {
		return (this.state & state) != 0;
	}

	public boolean hasStyle(int style) {
		return (this.style & style) != 0;
	}

	private boolean include(boolean key, int type) {
		if(type == SWT.Selection) {
			return false;
		}
		if(key && (type == SWT.KeyDown || type == SWT.KeyUp || type == SWT.Traverse)) {
			return true;
		}
		if(!key && !(type == SWT.KeyDown || type == SWT.KeyUp || type == SWT.Traverse)) {
			return true;
		}
		return false;
	}

	public boolean isActivatable() {
		return activatable;
	}
	
	public boolean isDisposed() {
		return disposed;
	}

	public boolean isEnabled() {
		return getEnabled() && ((parent != null) ? parent.isEnabled() : composite.isEnabled());
	}
	
	public boolean isSameWidgetAs(VControl control) {
		return control != null && getWidget() == control.getWidget();
	}

	public boolean isSameWidgetAs(Widget widget) {
		Composite w = getWidget();
		return w == widget || containsControl((Control) widget, w);
	}

	/**
	 * @return true if this VControl is to be sized as a square
	 */
	public boolean isSquare() {
		return square;
	}

	public boolean isVisible() {
		return getVisible() && composite.isVisible();
	}

	public void moveAbove(VControl control) {
		parent.move(this, null);
	}
	
	public void moveBelow(VControl control) {
		parent.move(null, this);
	}
	
	public void notifyListeners(int eventType) {
		notifyListeners(eventType, null);
	}
	
	public void notifyListeners(int eventType, Event event) {
		if(listeners.containsKey(eventType)) {
			if(event == null) {
				event = new Event();
			}
			event.data = this;
			event.type = eventType;
			if(this instanceof VNative && eventType == SWT.FocusOut) {
				System.out.println("wtf");
			}
			for(Listener listener : getListeners(eventType)) {
				listener.handleEvent(event);
			}
		}
	}

	public final void paintControl(Event e) {
		if(painter != null && bounds.intersects(e.x, e.y, e.width, e.height) && isVisible()) {
			int alpha = e.gc.getAlpha();
			int fullX, fullY, fullW, fullH;
			fullX = bounds.x - 1;
			fullY = bounds.y - 1;
			if(parent != null) {
				fullW = Math.min(parent.bounds.x + parent.bounds.width - bounds.x, bounds.x + bounds.width) + 1;
				fullH = Math.min(parent.bounds.y + parent.bounds.height - bounds.y, bounds.y + bounds.height) + 1;
			} else {
				fullW = bounds.width;
				fullH = bounds.height;
			}
			int clientX, clientY, clientW, clientH;
			clientX = fullX + marginLeft;
			clientY = fullY + marginTop;
			clientW = fullW - marginLeft - marginRight;
			clientH = fullH - marginTop - marginBottom;

			e.gc.setClipping(fullX, fullY, fullW, fullH);
			setAlpha(e.gc);
			painter.paintBackground(this, e);

			if(clientW > 0 && clientH > 0) {
				e.gc.setClipping(clientX, clientY, clientW, clientH);
				setAlpha(e.gc);
				painter.paintContent(this, e);
			}

			e.gc.setClipping(fullX, fullY, fullW, fullH);
			setAlpha(e.gc);
			painter.paintBorders(this, e);

			if(!getEnabled()) {
				setAlpha(e.gc, 25);
				e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
				e.gc.fillRectangle(fullX, fullY, fullW, fullH);
			}
			
			e.gc.setClipping((Rectangle) null);
			e.gc.setAlpha(alpha);

			if(listeners.containsKey(SWT.Paint)) {
				for(Listener listener : listeners.get(SWT.Paint)) {
					listener.handleEvent(e);
				}
			}
		}
	}

	public void redraw() {
		if(composite != null && !composite.isDisposed()) {
			composite.redraw(bounds.x, bounds.y, bounds.width, bounds.height, false);
		}
	}

	protected boolean redrawOnActivate() {
		return true;
	}

	protected boolean redrawOnDeactivate() {
		return true;
	}

	void removeListener(int eventType) {
		eventTypes.remove(eventType);
		if(hasState(STATE_ACTIVE)) {
			composite.removeListener(eventType, listener);
		}
	}

	public void removeListener(int eventType, Listener listener) {
		if(listeners.containsKey(eventType)) {
			listeners.get(eventType).remove(listener);
		}
		if(hasState(STATE_ACTIVE)) {
			composite.removeListener(eventType, listener);
		}
	}

	public void setActivatable(boolean activatable) {
		this.activatable = activatable;
	}
	
	public void setActiveCursor(Cursor cursor) {
		activeCursor = cursor;
	}
	
	/**
	 * @param x
	 * @param y
	 */
	public void setAlignment(int x, int y) {
		xAlign = x;
		yAlign = y;
	}

	public void setAlpha(GC gc) {
		gc.setAlpha((int) (2.55 * (double) visibility));
	}

	public void setAlpha(GC gc, int alpha) {
		gc.setAlpha((int) ((double) alpha * (double) visibility * (double) 0.01));
	}

	public void setBackground(Color color) {
		background = color;
	}

	public void setBounds(int x, int y, int width, int height) {
		boolean moved = (bounds.x != x || bounds.y != y);
		boolean resized = (bounds.width != width || bounds.height != height);

		bounds.x = x;
		bounds.y = y;
		bounds.width = width;
		bounds.height = height;

		Point p = getDisplay().getCursorLocation();
		if(bounds.contains(toControl(p))) {
			activate();
		} else {
			deactivate();
		}
		
		if(moved) {
			notifyListeners(SWT.Move, new Event());
		}
		if(resized) {
			notifyListeners(SWT.Resize, new Event());
		}
	}

	public void setBounds(Rectangle bounds) {
		setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	public void setCursor(Cursor cursor) {
		getComposite().setCursor(cursor);
	}
	
	public void setData(Enum<?> name, Object value) {
		setData(name.name(), value);
	}

	public void setData(String name, Object value) {
		if(value == null) {
			if(dataMap != null) {
				dataMap.remove(name);
				if(dataMap.isEmpty()) {
					dataMap = null;
				}
			}
		} else {
			if(dataMap == null) {
				dataMap = new HashMap<String, Object>();
			}
			dataMap.put(name, value);
		}
	}

	public void setEnabled(boolean enabled) {
		if(setState(STATE_ENABLED, enabled)) {
			if(this instanceof VNative) {
				Control c = getControl();
				if(c != null) {
					c.setEnabled(enabled);
				}
			}
			if(!enabled) {
				deactivate();
			}
			redraw();
		}
	}

	public void setFill(Color color) {
		fill = color;
	}

	public boolean setFocus() {
		return VTracker.instance().setFocusControl(this);
	}

	protected boolean setFocus(boolean focus) {
		if(!hasStyle(SWT.NO_FOCUS)) {
			if(focus) {
				attachListeners(true);
				notifyListeners(SWT.FocusIn);
			} else {
				notifyListeners(SWT.FocusOut);
				detachListeners(true);
			}
			return true;
		}
		return false;
	}

	public void setFont(Font font) {
		// TODO setFont
	}

	public void setForeground(Color color) {
		foreground = color;
	}

	public void setImage(Image image) {
		this.image = image;
		redraw();
	}

	public void setImage(SvgDocument svg) {
		this.svg = svg;
		redraw();
	}

	public void setInactiveCursor(Cursor cursor) {
		inactiveCursor = cursor;
	}
	
	public void setLayoutData(GridData data) {
		layoutData = data;
	}

	public void setLocation(Point location) {
		if(location != null) {
			setLocation(location.x, location.y);
		}
	}
	
	public void setLocation(int x, int y) {
		setBounds(x, y, bounds.width, bounds.height);
	}

	/**
	 * @param marginWidth
	 * @param marginHeight
	 */
	public void setMargins(int marginWidth, int marginHeight) {
		setMargins(marginWidth, marginWidth, marginHeight, marginHeight);
	}

	/**
	 * @param left
	 * @param right
	 * @param top
	 * @param bottom
	 */
	public void setMargins(int left, int right, int top, int bottom) {
		if(left >= 0) {
			marginLeft = left;
		}
		if(right >= 0) {
			marginRight = right;
		}
		if(top >= 0) {
			marginTop = top;
		}
		if(bottom >= 0) {
			marginBottom = bottom;
		}
	}

	public void setMargins(Rectangle margins) {
		setMargins(margins.x, margins.y, margins.width, margins.height);
	}

	public void setOval(int rx, int ry) {
		setPolygon(new int[] { rx, ry });
	}

	public void setOval(int rx, int ry, Color fillColor) {
		setPolygon(new int[] { rx, ry }, fillColor);
	}

	public void setPainter(IControlPainter painter) {
		this.painter = painter;
	}

	public void setParent(VPanel panel) {
		if(this.parent != null) {
			this.parent.removeChild(this);
		}
		this.parent = panel;
		if(this.parent != null) {
			this.composite = this.parent.composite;
			this.parent.addChild(this);
		}
	}

	public void setPolygon(int[] points) {
		setPolygon(points, (fill != null ? fill : ((background != null) ? background : getForeground())));
	}

	public void setPolygon(int[] points, Color fillColor) {
		if(points == null || points.length < 2 || points.length % 2 != 0) {
			return;
		}
		if(points.length == 2 && (points[0] < 1 || points[1] < 1)) {
			return;
		}
		this.points = points;
		setFill(fillColor);
		redraw();
	}

	public void setScaleImage(boolean scale) {
		this.scaleImage = scale;
	}
	
	public void setSize(Point size) {
		if(size != null) {
			setBounds(bounds.x, bounds.y, size.x, size.y);
		}
	}

	/**
	 * if parameter equal is true, the x and y sizes of this VControl will be
	 * forced equal, thus drawing a square button
	 * 
	 * @param equal
	 */
	public void setSquare(boolean equal) {
		square = equal;
	}

	protected boolean setState(int state, boolean set) {
		if(set && !hasState(state)) {
			this.state |= state;
			return true;
		} else if(!set && hasState(state)) {
			this.state &= ~state;
			return true;
		}
		return false;
	}

	public void setStyle(int style) {
		this.style = style;
	}
	
	public boolean setStyle(int style, boolean set) {
		if(set && !hasStyle(style)) {
			this.style |= style;
			return true;
		} else if(!set && hasStyle(style)) {
			this.style &= ~style;
			return true;
		}
		return false;
	}

	/**
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
		redraw();
	}

	public void setToolTipText(String text) {
		tooltipText = text;
	}

	void setVisibility(int visibility) {
		if(visibility > 100) {
			visibility = 100;
		} else if(visibility < 0) {
			visibility = 0;
		}
		this.visibility = visibility;
		if(!isVisible()) {
			if(this == VTracker.getFocusControl()) {
				VTracker.instance().setFocusControl(null);
			}
			VTracker.instance().deactivate(this);
		}
		redraw();
	}

	public void setVisible(boolean visible) {
		setVisibility(visible ? 100 : 0);
	}

	public void setVisible(final boolean visible, final int duration) {
		setVisible(visible, duration, null);
	}

	public void setVisible(final boolean visible, final int duration, final Runnable callback) {
		if(duration <= 0) {
			setVisible(visible);
		} else {
			new Thread() {
				@Override
				public void run() {
					do {
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								if(!disposed) {
									setVisibility(visibility + (visible ? 10 : -10));
									composite.update();
								}
							};
						});
						if(!disposed) {
							try {
								Thread.sleep(5 * duration / 100);
							} catch(InterruptedException e) {
								e.printStackTrace();
							}
						}
					} while(!disposed && visibility > 0 && visibility < 100);
					if(!disposed && visibility != 0 && visibility != 100) {
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								if(!disposed) {
									setVisible(visible);
									composite.update();
								}
							};
						});
					}
					if(callback != null) {
						callback.run();
					}
				}
			}.start();
		}
	}

	public Point toControl(Point point) {
		return getComposite().toControl(point);
	}
	
	public Point toControl(int x, int y) {
		return getComposite().toControl(x, y);
	}
	
	public Point toDisplay(Point point) {
		return getComposite().toDisplay(point);
	}

	public Point toDisplay(int x, int y) {
		return getComposite().toDisplay(x, y);
	}

	@Override
	public String toString() {
		return super.toString() + " {" + text + "}";
	}
	
	public void update() {
		if(composite != null && !composite.isDisposed()) {
			composite.update();
		}
	}

}
