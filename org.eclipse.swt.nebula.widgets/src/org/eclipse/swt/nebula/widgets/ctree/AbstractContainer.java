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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TypedListener;

/**
 * <p>
 * NOTE: THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT. THIS IS A
 * PRE-RELEASE ALPHA VERSION. USERS SHOULD EXPECT API CHANGES IN FUTURE
 * VERSIONS.
 * </p>
 */
public abstract class AbstractContainer extends Composite implements Listener {

	public static final boolean gtk = "gtk".equals(SWT.getPlatform());
	public static final boolean win32 = "win32".equals(SWT.getPlatform());
	
	GC internalGC = new GC(Display.getDefault());

//	public static final int OP_NONE = 0;
//	public static final int OP_ADD = 1;
//	public static final int OP_REMOVE = 2;
//	public static final int OP_CELL_COLLAPSE = 3;
//	public static final int OP_CELL_EXPAND = 4;
//	public static final int OP_COLUMN_RESIZE = 5;
//	public static final int DIRTY_ORDERED = 1 << 0;
//	public static final int DIRTY_PAINTED = 1 << 1;
//	public static final int DIRTY_VISIBLE = 1 << 2;
	static final int MODE_NORMAL = 0;
	static final int MODE_MARQUEE = 1;
	static final int MODE_CREATE = 2;

	private static int checkStyle(int style) {
		int mask = SWT.BORDER | SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.MULTI
				| SWT.NO_FOCUS | SWT.CHECK;
		return (style & mask) | SWT.DOUBLE_BUFFERED;
	}

	/**
	 * A list of all items belonging to this container.<br>
	 * Items are in the order that they were added, taking into account that
	 * some may have been added at a requested index.
	 */
	protected List items = new ArrayList();
//	/**
//	 * A list of all items belonging to this container.<br>
//	 * Items are in the order that is relevant to the concrete subclass; default
//	 * is same as the items list.
//	 * <p>
//	 * Note that this is NOT a mechanism to be used for sorting and is thus not exposed
//	 * for external manipulation.
//	 * </p>
//	 * <p>
//	 * An example usage is a hierarchical layout: child items may not be next
//	 * to their siblings in the items list, but will be in the orderedItems list.
//	 * </p>
//	 */
//	protected List orderedItems = new LinkedList();
	/**
	 * A list of items that can be painted to the screen, whether they are
	 * on-screen or not, as determined by the concrete subclass; default is the
	 * orderedItems list minus the individual items that request to not be drawn
	 * due to Item.getVisible() returning false.<br>
	 * Items are in the order as specified by the orderedItems list.
	 */
	protected List visibleItems = new LinkedList();
	/**
	 * A list of items that will actually be painted to the screen.<br>
	 * Subclasses may override the order as the order of paintedItems represents
	 * the final drawing order of the items - the last item in this list is
	 * drawn to the screen last and is, therefore, drawn on top of any
	 * overlapping items.
	 */
	protected List paintedItems = new ArrayList();
	/**
	 * A list of the selected items. Only items which are in the visibleItems
	 * list can be in this list.
	 */
	protected List selection = new ArrayList();
	/**
	 * The first item selected with the SHIFT or CTRL key pressed. Used to
	 * create the selection array when selecting multiple items.
	 */
	protected AbstractItem shiftSel;
	/**
	 * Used in conjunction with MODE_MARQUEE. Signifies that a selection created
	 * with the marquee selection tool will be "held onto" during the
	 * immediately following mouse operation. Otherwise, the marquee selection
	 * could never be used.
	 */
	protected boolean holdSelection = false;
	/**
	 * Signifies whether or not the items of this container can be selected.
	 */
	protected boolean selectable = true;
	/**
	 * Signifies whether or not the items of this container are selected when
	 * their toggle is clicked on, as opposed to their body.
	 */
	protected boolean selectOnToggle = true;

	Class[] cellClasses = null;

	private Point mmPoint = null; // mouse move point
	private Point mdPoint = null; // mouse down point
	private Point muPoint = null; // mouse up point
	private Point mmDelta = null; // mouse move delta
	private int mode = MODE_NORMAL;
	private boolean marquee = false;

	protected int style = 0;
	protected Composite header;
	protected ScrollBar hBar;
	protected ScrollBar vBar;
	protected Table internalTable;

	protected boolean hasFocus = false;
	protected String emptyMessage = "";
	protected boolean linesVisible = false;
	protected boolean lastLine = false;
	protected boolean vLines = true;
	protected boolean hLines = true;
	protected SColors colors;

	// public int marginWidth = gtk ? 0 : 1;
	// public int marginHeight = gtk ? 0 : 0;
//	protected AbstractItem dirtyItem = null;
//	int operation = OP_NONE;
//	protected int dirtyFlags = 0;

	private Listener filter;
	private List paintedItemListeners;
//	protected int firstPaintedIndex;
//	protected int lastPaintedIndex;


//	protected AbstractItem[] removedItems = null;
//	protected int rowHeight = 20;
	protected boolean nativeGrid = true;
//	protected boolean autoFillCells = true;

	protected AbstractContainerLayout layout;

	// public static final int STATE_NORMAL = 0;
	// public static final int STATE_BUSY = 1;
	private List activeItems = new ArrayList();

	// private boolean selectionActive = false;

	AbstractContainer(Composite parent, int style) {
		super(parent, checkStyle(style));

		this.style = style;

		colors = new SColors(getDisplay());
		updateColors();

		createContents(style);

		filter = new Listener() {
			public void handleEvent(Event event) {
				if (AbstractContainer.this.getShell() == ((Control) event.widget)
						.getShell()) {
					handleFocus(SWT.FocusOut);
				}
			}
		};

		addListener(SWT.Dispose, new Listener() {
			public void handleEvent(Event event) {
				getDisplay().removeFilter(SWT.FocusIn, filter);
				removeAll();
				if (internalTable != null && !internalTable.isDisposed()) {
					internalTable.dispose();
				}
				if(internalGC != null && internalGC.isDisposed()) {
					internalGC.dispose();
				}
			}
		});
	}

	protected void addItem(AbstractItem item) {
		addItem(items.size(), item);
	}

	protected void addItem(int index, AbstractItem item) {
		if (!items.contains(item)) {
			if (index < 0)
				index = items.size();

			// add the item to the list
			if (index >= items.size()) {
				items.add(item);
			} else {
				items.add(index, item);
			}
//			addOrderedItem(item);

			// setup properties and event handlers for the item
			item.setEnabled(selectable);
			item.updateColors();
			item.addListener(SWT.FocusIn, this);
			item.addListener(SWT.MouseDown, this);
			item.addListener(SWT.Traverse, this);

			layout(SWT.Show, item);
		}
	}

	// public void addListener(int eventType, Listener listener) {
	// switch(eventType) {
	// case SWT.MouseDoubleClick:
	// case SWT.MouseDown:
	// case SWT.MouseEnter:
	// case SWT.MouseExit:
	// case SWT.MouseHover:
	// case SWT.MouseMove:
	// case SWT.MouseUp:
	// case SWT.MouseWheel:
	// case SWT.KeyDown:
	// case SWT.KeyUp:
	// container.addListener(eventType, listener);
	// break;
	// default:
	// super.addListener(eventType, listener);
	// break;
	// }
	// }
	//
	// public void addMouseListener(MouseListener listener) {
	// container.addMouseListener(listener);
	// }
	//	
	// public void addMouseMoveListener(MouseMoveListener listener) {
	// container.addMouseMoveListener(listener);
	// }
	//	
	// public void addMouseTrackListener(MouseTrackListener listener) {
	// container.addMouseTrackListener(listener);
	// }

//	protected void addOrderedItem(AbstractItem item) {
//		orderedItems.add(item);
//	}
	
	/**
	 * Adds the listener to the collection of listeners who will be notified
	 * when the Paint Status of an item changes.
	 * <p>
	 * An item may be considered visible, and will be returned with
	 * {@link AbstractContainer#getVisibleItems()}, even though it will not be
	 * painted on the screen. Paint status, on the other hand, refers to whether
	 * or not an item will actually be painted when the
	 * {@link AbstractContainer#paintBody(Event)} method is called.
	 * </p>
	 * <p>
	 * The Event that is passed to this listener will have the item, whose Paint
	 * Status has changed, set as Event.item. The actual Paint Status is
	 * dertermined through the value of the Event's detail field: values > 0
	 * mean the item will be painted, while values < 0 mean the item will not be
	 * painted.
	 * </p>
	 * 
	 * @param listener
	 *            the listener which should be notified
	 * @see #removePaintedItemListener
	 * @see #getPaintedItems()
	 */
	public void addPaintedItemListener(Listener listener) {
		if (paintedItemListeners == null) {
			paintedItemListeners = new ArrayList();
		}
		if (!paintedItemListeners.contains(listener)) {
			paintedItemListeners.add(listener);
		}
	}

	/**
	 * An item may be considered visible, and will be returned with
	 * {@link AbstractContainer#getVisibleItems()}, even though it will not be
	 * painted on the screen. Paint status, on the other hand, refers to whether
	 * or not an item will actually be painted when the
	 * {@link AbstractContainer#paintBody(Event)} method is called.
	 * 
	 * @return an array of items that will be painted to the screen during paint
	 *         events
	 * @see #getVisibleItems()
	 */
	// public AbstractItem[] getPaintedItems() {
	// return (AbstractItem[]) paintedItems.toArray(new
	// AbstractItem[paintedItems.size()]);
	// }
	public void addSelectionListener(SelectionListener listener) {
		checkWidget();
		if (listener != null) {
			TypedListener typedListener = new TypedListener(listener);
			addListener(SWT.Selection, typedListener);
			addListener(SWT.DefaultSelection, typedListener);
		}
	}

	/**
	 * Clears (empties) the selection
	 */
	public void clearSelection() {
		selection = new ArrayList();
		finishSelection();
	}

	private void createContents(int style) {
		hBar = getHorizontalBar();
		if (hBar != null) {
			hBar.setVisible(false);
			hBar.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (internalTable != null) {
						if (win32) { // pogramatic scrolling doesn't work on
										// win32
							int sel = hBar.getSelection();
							Rectangle hBounds = internalTable.getBounds();
							hBounds.x = -sel;
							hBounds.width += sel;
							internalTable.setBounds(hBounds);
						} else {
							internalTable.getHorizontalBar().setSelection(
									hBar.getSelection());
						}
					}
					redraw();
				}
			});
		}
		vBar = getVerticalBar();
		if (vBar != null) {
			vBar.setVisible(false);
			vBar.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					redraw();
				}
			});
		}

		setBackground(getColors().getTableBackground());
		addKeyListener(new KeyAdapter() {
		}); // traverse does not work without this...
		addListener(SWT.FocusIn, this);
		addListener(SWT.MouseDown, this);
		addListener(SWT.MouseDoubleClick, this);
		addListener(SWT.MouseMove, this);
		addListener(SWT.MouseUp, this);
		addListener(SWT.Paint, new Listener() {
			public void handleEvent(Event e) {
				if (SWT.Paint == e.type) {
					paintBody(e);
				}
			}
		});
		addListener(SWT.Traverse, this);
	}

	public void deselect(int index) {
		deselect(index, true);
	}

	private void deselect(int index, boolean finish) {
		if (index >= 0 && index < visibleItems.size()) {
			selection.remove(visibleItems.get(index));
		}

		if (finish) {
			finishSelection();
		}
	}

	public void deselect(int start, int end) {
		if ((start >= 0) && (start < visibleItems.size()) && (end >= 0)
				&& (end < visibleItems.size())) {
			int i1 = (start < end) ? start : end;
			int i2 = (start < end) ? end : start;
			for (int i = i1; i <= i2; i++) {
				deselect(i, false);
			}
			finishSelection();
		}
	}

	public void deselect(int[] indices) {
		for (int i = 0; i < indices.length; i++) {
			deselect(indices[i], false);
		}
		finishSelection();
	}

	public void deselectAll() {
		selection = new ArrayList();
		finishSelection();
	}

	private void finishSelection() {
		if (!isDisposed()) {
			for (Iterator i = visibleItems.iterator(); i.hasNext();) {
				AbstractItem item = ((AbstractItem) i.next());
				item.setSelected(selection.contains(item));
			}
			showSelection();
			redraw();
			fireSelectionEvent(false);
		}
	}

	void firePaintedItemEvent(AbstractItem item, boolean isPainted) {
		if (paintedItemListeners != null) {
			Event event = new Event();
			event.detail = isPainted ? 1 : -1;
			event.item = item;
			Listener[] la = (Listener[]) paintedItemListeners
					.toArray(new Listener[paintedItemListeners.size()]);
			for (int i = 0; i < la.length; i++) {
				la[i].handleEvent(event);
			}
		}
	}

	protected void fireSelectionEvent(boolean defaultSelection) {
		Event event = new Event();
		event.type = defaultSelection ? SWT.DefaultSelection : SWT.Selection;
		if (selection.size() == 1)
			event.item = (AbstractItem) selection.get(0);
		notifyListeners(event.type, event);
	}

//	protected int getLastPaintedColumnIndex() {
//		Rectangle ca = getContentArea();
//		int right = getScrollPosition().x + ca.x + ca.width;
//		int gridline = getGridLineWidth();
//		int[] widths = getColumnWidths();
//		for(int i = 0; i < widths.length; i++) {
//			right -= (widths[i] + gridline);
//			if(right <= 0) return i;
//		}
//		return -1;
//	}

//	/**
//	 * Get the index of the Bottom Item, as defined by the implementation class
//	 * 
//	 * @return the index
//	 */
//	protected abstract int getLastPaintedItemIndex();

//	/**
//	 * Get the Bottom Item, as defined by the implementation class
//	 * 
//	 * @return the index
//	 */
//	protected abstract AbstractItem getLastPaintedItem();

	public SColors getColors() {
		return colors;
	}

	AbstractColumn internalGetColumn(int index) {
		return (internalTable != null) ? (AbstractColumn) internalTable.getColumn(index) : null;
	}

	public int getColumnCount() {
		return (internalTable != null) ? internalTable.getColumnCount() : 1;
	}

	public int[] getColumnOrder() {
		return (internalTable != null) ? internalTable.getColumnOrder()
				: new int[] { 0 };
	}

	AbstractColumn[] internalGetColumns() {
		if (internalTable != null) {
			Object[] nca = internalTable.getColumns();
			AbstractColumn[] cca = new AbstractColumn[nca.length];
			for (int i = 0; i < cca.length; i++) {
				cca[i] = (AbstractColumn) nca[i];
			}
			return cca;
		}
		return new AbstractColumn[0];
	}

	int[] getColumnWidths() {
		return layout.columnWidths;
	}

//	AbstractItem getDirtyItem() {
//		return dirtyItem;
//	}

	/**
	 * The Empty Message is the text message that will be displayed when there
	 * are no Items to be displayed (the CTable is empty).
	 * 
	 * @return a String representing the Empty Message. Guaranteed to NOT be
	 *         null.
	 * @see org.aspencloud.widgets.ccontainer#setEmptyMessage(java.lang.String)
	 */
	public String getEmptyMessage() {
		return emptyMessage;
	}

	public int getGridLineWidth() {
		return (internalTable != null) ? internalTable.getGridLineWidth() : 0;
	}

	public int getHeaderHeight() {
		return (internalTable != null) ? internalTable.getHeaderHeight() : 0;
	}

	public boolean getHeaderVisible() {
		return (internalTable != null) ? internalTable.getHeaderVisible()
				: false;
	}

	Composite getHeader() {
		if (header == null) {
			header = new Composite(this, SWT.NONE);
		}
		return header;
	}

	Table getInternalTable() {
		if (internalTable == null) {
			internalTable = new Table(getHeader(), SWT.NONE);
			internalTable.setLinesVisible(linesVisible);
		}
		return internalTable;
	}

	AbstractItem internalGetItem(Point pt) {
		// must iterate in reverse drawing order in case items overlap each
		// other
		for (ListIterator i = paintedItems.listIterator(paintedItems.size()); i
				.hasPrevious();) {
			AbstractItem item = (AbstractItem) i.previous();
			if (item.contains(pt))
				return item;
		}
		return null;
	}

	public int getItemCount() {
		return visibleItems.size();
	}

	/**
	 * not yet implemented
	 * <p>
	 * post a feature request if you need it enabled
	 * </p>
	 * TODO: items may (will!) have different heights...
	 */
	public int getItemHeight() {
		return 0;
	}

	/**
	 * Get a list of all items associated with the given item.
	 * <p>
	 * Actual meaning is up to the implementation class
	 * </p>
	 * 
	 * @param item
	 * @return
	 */
	protected List getItems(AbstractItem item) {
		return Collections.EMPTY_LIST;
	}

	/**
	 * Get a list of items within, or touching, the given rectangle.
	 * 
	 * @param rect
	 * @return
	 */
	protected AbstractItem[] getItems(Rectangle rect) {
		List items = new ArrayList();
		for (Iterator i = visibleItems.iterator(); i.hasNext();) {
			AbstractItem item = (AbstractItem) i.next();
			Rectangle[] ra = item.getCellBounds();
			for (int j = 0; j < ra.length; j++) {
				if (ra[j].intersects(rect)) {
					items.add(item);
					break;
				}
			}
		}
		return (items.isEmpty()) ? new AbstractItem[0] : (AbstractItem[]) items.toArray(new AbstractItem[items.size()]);
	}

	public boolean getLastLineVisible() {
		return lastLine;
	}

	public boolean getLinesVisible() {
		return linesVisible;
	}

	Point getScrollPosition() {
		return new Point(
				(hBar == null || hBar.isDisposed()) ? 0 : hBar.getSelection(),
				(vBar == null || vBar.isDisposed()) ? 0 : vBar.getSelection());
	}

//	AbstractItem[] getRemovedItems() {
//		return removedItems;
//	}

	public int getSelectionCount() {
		return selection.size();
	}

	public int getSelectionIndex() {
		int[] ixs = getSelectionIndices();
		return (ixs.length > 0) ? ixs[0] : -1;
	}

	public int[] getSelectionIndices() {
		// TODO this is the trade-off - is it a problem? I think we use
		// getSelection more often...
		int[] ixs = new int[selection.size()];
		for (int i = 0; i < ixs.length; i++) {
			ixs[i] = visibleItems.indexOf(selection.get(i));
		}
		return ixs;
	}

	/**
	 * @see AbstractContainer#setSelectOnToggle(boolean)
	 * @return
	 */
	public boolean getSelectOnToggle() {
		return selectOnToggle;
	}

	public int getStyle() {
		return style;
	}

//	protected int getFirstPaintedColumnIndex() {
//		int left = getScrollPosition().x;
//		int gridline = getGridLineWidth();
//		int[] widths = getColumnWidths();
//		for(int i = 0; i < widths.length; i++) {
//			left -= (widths[i] + gridline);
//			if(left < 0) return i;
//		}
//		return -1;
//	}

//	protected abstract int getFirstPaintedItemIndex();

//	protected abstract AbstractItem getFirstPaintedItem();

	protected boolean handleMouseEvents(AbstractItem item, Event event) {
		// TODO: handleMouseEvents(AbstractItem item, Event event)
//		if (SWT.MouseUp == event.type && item != null && item.isTogglePoint(muPoint)) {
//			boolean open = !item.isOpen(muPoint);
//			int etype = open ? SWT.Collapse : SWT.Expand;
//			item.removeListener(etype, this);
//			item.setOpen(muPoint, open);
//			item.addListener(etype, this);
//			layout(etype, item);
//		}
		return true;
	}

	Point mapPoint(int x, int y) {
		Point point = getScrollPosition();
		point.x += x;
		point.y += y;
		return point;
	}
	
	Rectangle mapRectangle(Rectangle rect) {
		return mapRectangle(rect.x, rect.y, rect.width, rect.height);
	}
	
	Rectangle mapRectangle(int x, int y, int width, int height) {
		Rectangle r = new Rectangle(x,y,width,height);
		Point point = getScrollPosition();
		r.x += point.x;
		r.y += point.y;
		return r;
	}
	
	private void handleMousePosition(AbstractItem item, Event event) {
		switch (event.type) {
		case SWT.MouseDoubleClick:
			if (!selectOnToggle) {
				if (item != null) {
					if (!selectOnToggle
							&& item.isTogglePoint(mapPoint(event.x, event.y)))
						break;
				}
			}
			fireSelectionEvent(true);
			break;
		case SWT.MouseDown:
			mmPoint = null;
			muPoint = null;
			mmDelta = null;
			mdPoint = mapPoint(event.x, event.y);
			if (event.widget != this) {
				if (event.widget instanceof Control) {
					mdPoint = getDisplay().map((Control) event.widget, this,
							mdPoint);
				} else {
					break;
				}
			}
			break;
		case SWT.MouseMove:
			if (mmPoint == null) {
				mmDelta = mapPoint(0, 0);
			} else {
				mmDelta.x = event.x - mmPoint.x;
				mmDelta.y = event.y - mmPoint.y;
			}
			mmPoint = mapPoint(event.x, event.y);
			break;
		case SWT.MouseUp:
			mmPoint = null;
			mdPoint = null;
			mmDelta = null;
			muPoint = mapPoint(event.x, event.y);
			break;
		default:
			break;
		}
	}

	protected void handleMouseSelection(AbstractItem item, Event event) {
		switch (event.type) {
		case SWT.MouseDoubleClick:
			if (!selectOnToggle) {
				if (item != null) {
					if (!selectOnToggle
							&& item.isTogglePoint(mapPoint(event.x, event.y)))
						break;
				}
			}
			fireSelectionEvent(true);
			break;
		case SWT.MouseDown:
			if (!hasFocus)
				setFocus();
			if (event.widget == this) {
				item = internalGetItem(mdPoint);
			} else if (items.contains(event.item)) {
				item = (AbstractItem) event.item;
			}
			if (item == null) {
				if ((event.stateMask & (SWT.CTRL | SWT.SHIFT)) == 0) {
					if (mode == MODE_MARQUEE)
						holdSelection = true;
					setCursor(getDisplay().getSystemCursor(SWT.CURSOR_CROSS));
					clearSelection();
					marquee = true;
				}
				break;
			}
			if (mode == MODE_MARQUEE)
				holdSelection = selection.contains(item);
			marquee = false;
			if (!isFocusControl()) {
				setFocus();
			}
			switch (event.button) {
			// TODO - popup menu: not just for mouse down events!
			case 3:
				Menu menu = getMenu();
				if ((menu != null) && ((menu.getStyle() & SWT.POP_UP) != 0)) {
					menu.setVisible(true);
				}
			case 1:
				if (holdSelection)
					break;
				if (!selectOnToggle && item.isTogglePoint(mdPoint))
					break;
				if ((event.stateMask & SWT.SHIFT) != 0) {
					if (shiftSel == null) {
						if (selection.isEmpty())
							selection.add(item);
						shiftSel = (AbstractItem) selection.get(selection
								.size() - 1);
					}
					int start = visibleItems.indexOf(shiftSel);
					int end = visibleItems.indexOf(item);
					setSelection(start, end);
				} else if ((event.stateMask & SWT.CONTROL) != 0) {
					toggleSelection(item);
					shiftSel = null;
				} else {
					setSelection(item);
					shiftSel = null;
				}
				break;
			}
			break;
		case SWT.MouseMove:
			if (mode == MODE_MARQUEE && marquee) {
				int x = Math.min(mdPoint.x, mmPoint.x);
				int y = Math.min(mdPoint.y, mmPoint.y);
				int w = Math.abs(mdPoint.x - mmPoint.x);
				int h = Math.abs(mdPoint.y - mmPoint.y);
				Rectangle r = new Rectangle(x, y, w, h);
				setSelection(getItems(r));
			}
			redraw();
			break;
		case SWT.MouseUp:
			marquee = false;
			setCursor(getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
			redraw();
			break;
		}

	}

	public void handleEvent(Event event) {
		switch (event.type) {
		case SWT.FocusIn:
		case SWT.FocusOut:
			handleFocus(event.type);
			break;
		case SWT.MouseDoubleClick:
		case SWT.MouseDown:
		case SWT.MouseMove:
		case SWT.MouseUp:
			AbstractItem item = null;
			if (event.widget == this) {
				item = internalGetItem(mapPoint(event.x, event.y));
			} else if (items.contains(event.item)) {
				item = (AbstractItem) event.item;
			}
			handleMousePosition(item, event);
			if (handleMouseEvents(item, event)) {
				handleMouseSelection(item, event);
				int result = 0;
				Set s = new HashSet(selection);
				s.addAll(paintedItems);
				for (Iterator i = s.iterator(); i.hasNext();) {
					item = (AbstractItem) i.next();
					result |= item.handleMouseEvent(event, !activeItems
							.isEmpty());
					if ((item.isCellState(AbstractCell.CELL_MOVING
							| AbstractCell.CELL_RESIZING))) {
						if (!activeItems.contains(item)) {
							activeItems.add(item);
						}
					} else {
						activeItems.remove(item);
					}
					if ((result & AbstractCell.RESULT_CONSUME) != 0)
						break;
				}
				if ((result & AbstractCell.RESULT_LAYOUT) != 0) {
					layout();
				}
				if ((result & AbstractCell.RESULT_REDRAW) != 0) {
					redraw();
				}
			}
			break;
		case SWT.Traverse:
			handleTraverse(event);
			break;
		}
	}

	private void handleFocus(int type) {
		if (isDisposed())
			return;
		switch (type) {
		case SWT.FocusIn: {
			if (hasFocus)
				return;
			hasFocus = true;
			updateFocus();
			Display display = getDisplay();
			display.removeFilter(SWT.FocusIn, filter);
			display.addFilter(SWT.FocusIn, filter);
			Event e = new Event();
			notifyListeners(SWT.FocusIn, e);
			break;
		}
		case SWT.FocusOut: {
			if (!hasFocus)
				return;
			Control focusControl = getDisplay().getFocusControl();
			if (focusControl == this)
				return;
			for (Iterator i = visibleItems.iterator(); i.hasNext();) {
				if (((AbstractItem) i.next()).contains(focusControl))
					return;
			}
			hasFocus = false;
			updateFocus();
			Display display = getDisplay();
			display.removeFilter(SWT.FocusIn, filter);
			Event e = new Event();
			notifyListeners(SWT.FocusOut, e);
		}
		}
	}

	private void handleTraverse(Event event) {
		switch (event.detail) {
		case SWT.TRAVERSE_RETURN:
			if (event.data instanceof AbstractCell) {
				fireSelectionEvent(true);
			}
			break;
		case SWT.TRAVERSE_ARROW_NEXT:
			int[] ia = getSelectionIndices();
			if (ia.length > 0) {
				setSelection(ia[ia.length - 1] + 1);
			}
			break;
		case SWT.TRAVERSE_ARROW_PREVIOUS:
			ia = getSelectionIndices();
			if (ia.length > 0) {
				setSelection(ia[ia.length - 1] - 1);
			}
			break;
		}
	}

	public int indexOf(AbstractItem item) {
		return visibleItems.indexOf(item);
	}

	public int indexOf(AbstractColumn column) {
		return (internalTable != null) ? internalTable.indexOf(column) : -1;
	}

//	public boolean isDirty(int opcode) {
//		return ((dirtyFlags & opcode) != 0);
//	}

	public boolean isEmpty() {
		return visibleItems.isEmpty();
	}

//	public boolean isOperation(int opcode) {
//		return operation == opcode;
//	}

	// public boolean isSelected(int index) {
	// return getItem(index).isSelected();
	// }

	/**
	 * <p>
	 *  Event types:
	 *  <ul>
	 *   <li>SWT.Collapse</li>
	 *   <li>SWT.Expand</li>
	 *   <li>SWT.Resize</li>
	 *  </ul>
	 * </p>
	 * @param eventType
	 * @param cell
	 */
	void layout(int eventType, AbstractCell cell) {
		layout.layout(eventType, cell);
		updatePaintedList = true;
	}

	/**
	 * <p>
	 *  Event types:
	 *  <ul>
	 *   <li>SWT.Move</li>
	 *   <li>SWT.Resize</li>
	 *  </ul>
	 * </p>
	 * @param eventType
	 * @param column
	 */
	void layout(int eventType, AbstractColumn column) {
		layout.layout(eventType, column);
//		updatePaintedList = true;
	}

	/**
	 * <p>
	 *  Event types:
	 *  <ul>
	 *   <li>SWT.Hide</li>
	 *   <li>SWT.Show</li>
	 *  </ul>
	 * </p>
	 * @param eventType
	 * @param item
	 */
	void layout(int eventType, AbstractItem item) {
		if(SWT.Show == eventType) {
			// TODO: won't work
			visibleItems.add(items.indexOf(item), item);
			layout.layout(eventType, item);
			updatePaintedList = true;
		} else if(SWT.Hide == eventType) {
			visibleItems.remove(item);
			layout.layout(eventType, item);
			updatePaintedList = true;
		}
	}

//	public void layout(AbstractItem item, int opcode, boolean redraw) {
//		setDirtyItem(item);
//		setOperation(opcode);
//		opLayout();
//		if (redraw)
//			redraw();
//	}

	public void move(AbstractItem item, int newIndex) {
		if (newIndex >= 0 && newIndex < visibleItems.size() && visibleItems.contains(item)) {
			int oldIndex = visibleItems.indexOf(item);
			if (visibleItems.remove(item)) {
				visibleItems.add(newIndex, item);
				layout(SWT.Move, (AbstractItem) visibleItems.get(oldIndex));
				layout(SWT.Move, item);
			}
		}
	}

	protected void paintBackground(GC gc, Rectangle ebounds) {
	}

	protected void paintColumns(GC gc, Rectangle ebounds) {
		AbstractColumn[] columns = internalGetColumns();
		for (int i = 0; i < columns.length; i++) {
			columns[i].paint(gc, ebounds);
		}
	}

	protected void paintItemBackgrounds(GC gc, Rectangle ebounds) {
	}

	protected void paintSelectionIndicators(GC gc, Rectangle ebounds) {
	}

	protected void paintGridLines(GC gc, Rectangle ebounds) {
	}

	protected void paintItems(GC gc, Rectangle ebounds) {
		if (visibleItems.isEmpty()) {
			if (emptyMessage.length() > 0) {
				Point bSize = getSize();
				Point tSize = gc.textExtent(emptyMessage);
				gc.setForeground(colors.getItemForegroundNormal());
				gc.drawText(emptyMessage, (bSize.x - tSize.x) / 2 - ebounds.x,
						4 - ebounds.y);
			}
		} else {
			for (Iterator i = paintedItems.iterator(); i.hasNext();) {
				((AbstractItem) i.next()).paint(gc, ebounds);
			}
		}
	}

	public boolean drawViewportNorth = false;
	public boolean drawViewportEast = false;
	public boolean drawViewportSouth = false;
	public boolean drawViewportWest = false;

	public boolean paintGridAsBackground = false;

	int topOld = -1;
	int heightOld = -1;
	boolean updatePaintedList = false;
	protected void paintBody(Event e) {
		int top = ((vBar == null || vBar.isDisposed()) ? -1 : vBar.getSelection());
		int height = getClientArea().height;
		if(updatePaintedList || topOld != top || heightOld != height) {
			updatePaintedList = false;
			topOld = top;
			heightOld = height;
			updatePaintedItems();
		}

		Rectangle ebounds = e.getBounds();
		Image image = new Image(e.display, ebounds);
		GC gc = new GC(image);

		paintBackground(gc, ebounds);
		if(paintGridAsBackground) paintGridLines(gc, ebounds);
		paintItemBackgrounds(gc, ebounds);
		paintSelectionIndicators(gc, ebounds);
		paintColumns(gc, ebounds);
		paintItems(gc, ebounds);
		if(!paintGridAsBackground) paintGridLines(gc, ebounds);
		paintViewport(gc, ebounds);
		paintTracker(gc, ebounds);
		paintFocus(gc, ebounds);

		e.gc.drawImage(image, ebounds.x, ebounds.y);
		gc.dispose();
		image.dispose();
	}

	protected void paintViewport(GC gc, Rectangle ebounds) {
		if (drawViewportNorth || drawViewportEast || drawViewportSouth
				|| drawViewportWest) {
			gc.setAlpha(255);
			gc.setForeground(colors.getBorder());
			Rectangle r = getClientArea();
			if (drawViewportNorth)
				gc.drawLine(r.x, r.y, r.x + r.width, r.y);
			if (drawViewportEast)
				gc.drawLine(r.x + r.width - 1, r.y, r.x + r.width - 1, r.y
						+ r.height);
			if (drawViewportSouth)
				gc.drawLine(r.x, r.y + r.height - 1, r.x + r.width, r.y
						+ r.height - 1);
			if (drawViewportWest)
				gc.drawLine(r.x, r.y, r.x, r.y + r.height);
		}
	}

	protected void paintTracker(GC gc, Rectangle ebounds) {
		if (marquee && mdPoint != null && mmPoint != null) {
			gc.setAlpha(75);
			gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_GRAY));
			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_GRAY));
			int x = (mmPoint.x > mdPoint.x ? mdPoint.x : mmPoint.x) - ebounds.x;
			int y = (mmPoint.y > mdPoint.y ? mdPoint.y : mmPoint.y) - ebounds.y;
			int w = Math.abs(mmPoint.x - mdPoint.x);
			int h = Math.abs(mmPoint.y - mdPoint.y);
			gc.fillRectangle(x, y, w, h);
			gc.setAlpha(255);
			gc.setLineStyle(SWT.LINE_DASHDOTDOT);
			gc.drawRectangle(x, y, w, h);
		}
	}

	protected void paintFocus(GC gc, Rectangle ebounds) {
		if (hasFocus) {
			if (win32 || (gtk && !paintedItems.isEmpty()))
				return;

			gc.setAlpha(255);
			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
			gc.setLineDash(new int[] { 1, 1 });
			Rectangle r = getContentArea();
			r.x += (1 - ebounds.x);
			r.y += (1 - ebounds.y);
			r.width -= 3;
			r.height -= 3;
			gc.drawRectangle(r);
		}
	}

	protected Rectangle getContentArea() {
		Rectangle area = getClientArea();
		area.y = layout.headerSize.y;
		area.height -= layout.headerSize.y;
		return area;
	}

//	public void redraw(AbstractItem item) {
//		// TODO: paint / redraw individual item
//		// Rectangle r = item.getBounds();
//		// body.redraw(r.x-1, r.y-1, r.width+2, r.height+2, true);
//		redraw();
//	}

	public void redraw(AbstractCell cell) {
		Rectangle r = cell.getBounds();
		redraw(r.x-1, r.y-1, r.width+2, r.height+2, true);
	}

	public void remove(AbstractItem[] items) {
		List l = new ArrayList();
		for (int i = 0; i < items.length; i++) {
			if (this.items.contains(items[i])) {
				l.add(items[i]);
				l.addAll(getItems(items[i]));
			}
		}
		if (!l.isEmpty()) {
			boolean selChange = false;
			for(Iterator i = l.iterator(); i.hasNext(); ) {
				AbstractItem item = (AbstractItem) i.next();
				if(!selChange && selection.contains(item)) selChange = true;
				if(!item.isDisposed()) {
					layout(SWT.Hide, item);
					this.items.remove(item);
//					removeOrderedItem(item);
					item.dispose();
				}
			}
			selection.removeAll(l);

			if(selChange) fireSelectionEvent(false);
		}
	}

	public void remove(int index) {
		if ((index >= 0) && (index < visibleItems.size())) {
			((AbstractItem) visibleItems.get(index)).dispose();
		}
	}

	public void remove(int start, int end) {
		if ((start >= 0) && (start < visibleItems.size()) && (end >= 0)
				&& (end < visibleItems.size())) {
			int i1 = (start < end) ? start : end;
			int i2 = (start < end) ? end : start;
			AbstractItem[] items = new AbstractItem[i2 - i1 + 1];
			for (int i = 0; i1 <= i2; i++, i1++) {
				items[i] = (AbstractItem) visibleItems.get(i1);
			}
			remove(items);
		}
	}

	public void remove(int[] indices) {
		AbstractItem[] items = new AbstractItem[indices.length];
		for (int i = 0; i < indices.length; i++) {
			items[i] = (AbstractItem) visibleItems.get(indices[i]);
		}
		remove(items);
	}

	public void removeAll() {
		remove(0, visibleItems.size() - 1);
	}

	protected void removeItem(AbstractItem item) {
		if (items.contains(item)) {
			remove(new AbstractItem[] { item });
		}
	}

	// public void removeListener(int eventType, Listener listener) {
	// switch(eventType) {
	// case SWT.MouseDoubleClick:
	// case SWT.MouseDown:
	// case SWT.MouseEnter:
	// case SWT.MouseExit:
	// case SWT.MouseHover:
	// case SWT.MouseMove:
	// case SWT.MouseUp:
	// case SWT.MouseWheel:
	// case SWT.KeyDown:
	// case SWT.KeyUp:
	// container.removeListener(eventType, listener);
	// break;
	// default:
	// super.removeListener(eventType, listener);
	// break;
	// }
	// }
	//
	// public void removeMouseListener(MouseListener listener) {
	// container.removeMouseListener(listener);
	// }
	//	
	// public void removeMouseMoveListener(MouseMoveListener listener) {
	// container.removeMouseMoveListener(listener);
	// }
	//	
	// public void removeMouseTrackListener(MouseTrackListener listener) {
	// container.removeMouseTrackListener(listener);
	// }

	public void removePaintedItemListener(Listener listener) {
		if (paintedItemListeners != null) {
			paintedItemListeners.remove(listener);
		}
	}

	public void removeSelectionListener(SelectionListener listener) {
		checkWidget();
		if (listener != null) {
			removeListener(SWT.Selection, listener);
			removeListener(SWT.DefaultSelection, listener);
		}
	}

	void scrollTo(Point pt) {
		setScrollPosition(pt);
	}

	void scrollToX(int x) {
		setOrigin(x, getScrollPosition().y);
	}

	void scrollToY(int y) {
		setOrigin(getScrollPosition().x, y);
	}

	public void select(int index) {
		select(index, true);
	}

	private void select(int index, boolean finish) {
		if ((index >= 0) && (index < visibleItems.size())) {
			Object o = visibleItems.get(index);
			if (!selection.contains(o)) {
				selection.add(o);
			}
		}
		if (finish)
			finishSelection();
	}

	public void select(int start, int end) {
		if ((start >= 0) && (start < visibleItems.size()) && (end >= 0)
				&& (end < visibleItems.size())) {
			int i1 = (start < end) ? start : end;
			int i2 = (start < end) ? end : start;
			for (int i = i1; i <= i2; i++) {
				select(i, false);
			}
			finishSelection();
		}
	}

	public void select(int[] indices) {
		for (int i = 0; i < indices.length; i++) {
			select(indices[i], false);
		}
		finishSelection();
	}

	public void selectAll() {
		selection = new ArrayList(visibleItems);
		finishSelection();
	}

	/**
	 * disabled: use the SColors class instead
	 */
	public void setBackground(Color color) {
	}

//	public void clearDirtyFlags() {
//		dirtyFlags = 0;
//	}

//	public void clearDirtyItem() {
//		dirtyItem = null;
//	}

//	public void setDirtyFlag(int flag) {
//		switch (flag) {
//		case DIRTY_ORDERED:
//			dirtyFlags |= DIRTY_ORDERED;
//		case DIRTY_VISIBLE:
//			dirtyFlags |= DIRTY_VISIBLE;
//		case DIRTY_PAINTED:
//			dirtyFlags |= DIRTY_PAINTED;
//			topIndex = -2; // topIndex will never be set to this, thus forcing
//							// a refresh
//			botIndex = -2;
//			break;
//		}
//	}

//	public void setDirtyItem(AbstractItem item) {
//		dirtyItem = item;
//	}

	/**
	 * Sets the message that will be displayed when their are no Items to be
	 * displayed (the Container is empty). The message will span all rows and be
	 * aligned to the Top and Center of the CTable. Setting <b>string</b> to
	 * null will disable the Empty Message Display feature.
	 * 
	 * @param string
	 *            the message to be displayed
	 */
	public void setEmptyMessage(String string) {
		emptyMessage = (string != null) ? string : "";
	}

	public boolean setFocus() {
		return forceFocus();
	}

	/**
	 * currently disabled: use the CTableColors class instead
	 * <p>
	 * post a feature request if you need it enabled
	 * </p>
	 */
	public void setForeground(Color color) {
	}

	public void setHeaderVisible(boolean show) {
		getInternalTable().setHeaderVisible(show);
	}

	/**
	 * Sets the layout which is associated with the receiver to be the argument
	 * which may be null. If the argument is non-null then it must be a subclass
	 * of CContainerLayout or this method will simply return.
	 * 
	 * @param layout
	 *            the receiver's new layout or null
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setLayout(Layout layout) {
		if (layout instanceof AbstractContainerLayout) {
			this.layout = (AbstractContainerLayout) layout;
			super.setLayout(layout);
		}
	}

	public void setLinesVisible(boolean visible) {
		setLinesVisible(visible, true);
	}

	/**
	 * If lastLine == false, then lines are only drawn in between items (if, of
	 * course, visible == true)
	 * 
	 * @param visible
	 * @param lastLine
	 *            whether or not the last line is set visible
	 * @see org.aspencloud.widgets.ccontainer#setLinesVisible(boolean)
	 */
	public void setLinesVisible(boolean visible, boolean lastLine) {
		if (linesVisible != visible) {
			linesVisible = visible;
			if (internalTable != null)
				internalTable.setLinesVisible(linesVisible);
			redraw();
		}
		this.lastLine = lastLine;
	}

	public void setHorizontalLinesVisible(boolean visible) {
		hLines = visible;
	}

	void setMode(int mode) {
		this.mode = mode;
	}

//	public void setOperation(int opcode) {
//		operation = opcode;
//	}

	public void setCellClass(Class clazz) {
		cellClasses = new Class[] { clazz };
	}

	public void setCellClasses(Class[] classes) {
		cellClasses = classes;
	}

	void setScrollPosition(Point origin) {
		setOrigin(origin.x,origin.y);
	}

	void setOrigin(int x, int y) {
		if(hBar != null && !hBar.isDisposed()) hBar.setSelection(x);
		if(vBar != null && !vBar.isDisposed()) vBar.setSelection(y);
		redraw();
	}

	public void setRedraw(boolean redraw) {
		super.setRedraw(redraw);
		if (internalTable != null)
			internalTable.setRedraw(redraw);
		setRedraw(redraw);
	}

//	private void setRemovedItems(AbstractItem[] items) {
//		removedItems = items;
//	}

	/**
	 * Enables items in this Container to be selected
	 * 
	 * @param selectable
	 */
	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
		for (Iterator i = items.iterator(); i.hasNext();) {
			((AbstractItem) i.next()).setEnabled(selectable);
		}
	}

	public void setSelection(AbstractItem item) {
		if (item == null) {
			selection = new ArrayList();
		} else {
			selection = new ArrayList(Collections.singleton(item));
			shiftSel = item;
		}
		finishSelection();
	}

	public void setSelection(AbstractItem[] items) {
		if (items == null || items.length == 0) {
			selection = new ArrayList();
		} else {
			selection = new ArrayList(Arrays.asList(items));
			shiftSel = items[0];
		}
		finishSelection();
	}

	public void setSelection(int index) {
		if (index >= 0 && index < visibleItems.size()) {
			AbstractItem item = (AbstractItem) visibleItems.get(index);
			selection = new ArrayList(Collections.singleton(item));
			shiftSel = item;
			finishSelection();
		}
	}

	public void setSelection(int start, int end) {
		if (start >= 0 && start < visibleItems.size() && end >= 0
				&& end < visibleItems.size()) {
			selection = new ArrayList();
			int i1 = (start < end) ? start : end;
			int i2 = (start < end) ? end : start;
			for (int i = i1; i <= i2; i++) {
				selection.add(visibleItems.get(i));
			}
			shiftSel = (AbstractItem) visibleItems.get(start);
			finishSelection();
		}
	}

	public void setSelection(int[] ixs) {
		selection = new ArrayList();
		for (int i = 0; i < ixs.length; i++) {
			selection.add(visibleItems.get(i));
		}
		shiftSel = (AbstractItem) visibleItems.get(ixs[0]);
		finishSelection();
	}

	/**
	 * If the the user clicks on the toggle of an item (treeCell or not) the
	 * corresponding item will become selected if, and only if, selectOnToggle
	 * is true
	 * 
	 * @param select
	 *            the new state of selectOnToggle
	 */
	public void setSelectOnToggle(boolean select) {
		selectOnToggle = select;
	}

	protected AbstractColumn internalGetSortColumn() {
		return (internalTable != null) ? (AbstractColumn) internalTable.getSortColumn() : null;
	}
	
	public int getSortDirection() {
		return (internalTable != null) ? internalTable.getSortDirection() : -1;
	}

	public void setSortColumn(AbstractColumn column) {
		if(internalTable != null) internalTable.setSortColumn(column);
	}
	
	public void setSortDirection(int direction) {
		if(internalTable != null) internalTable.setSortDirection(direction);
	}
	
	public void setVerticalLinesVisible(boolean visible) {
		vLines = visible;
	}

	public void showColumn(CTreeColumn column) {
		// TODO Auto-generated method stub
	}

	public void showItem(AbstractItem item) {
		Rectangle r = item.getBounds();
		Rectangle c = getContentArea();
		Point scroll = getScrollPosition();
		if(r.height > c.height || !c.contains(new Point(r.x, r.y - scroll.y))) {
			setScrollPosition(new Point(scroll.x, r.y - c.y));
		} else if(!c.contains(new Point(r.x, r.y + r.height - 1 - scroll.y))) {
			setScrollPosition(new Point(scroll.x, r.y - c.y - c.height + r.height));
		}
		// TODO: showItem needs to work horizontally as well
	}

	/**
	 * @see org.eclipse.swt.widgets.Table#showItem(java.lang.Object)
	 * @param index the index of the item to show
	 */
	public void showItem(int index) {
		if ((index >= 0) && (index < visibleItems.size())) {
			showItem((AbstractItem) visibleItems.get(index));
		}
	}

	public void showSelection() {
		if (!selection.isEmpty()) {
			int topIndex = visibleItems.indexOf(selection.get(0));
			for (Iterator i = selection.iterator(); i.hasNext();) {
				topIndex = Math.min(topIndex, visibleItems.indexOf((AbstractItem) i.next()));
			}
			showItem((AbstractItem) visibleItems.get(topIndex));
		}
	}

	/**
	 * toggle the selection state of the item
	 * 
	 * @param item
	 *            the item whose selection state is to be toggled
	 * @return true if the item's selection state was toggled on, false
	 *         otherwise
	 */
	public boolean toggleSelection(AbstractItem item) {
		boolean result = false;
		if (selection.contains(item)) {
			selection.remove(item);
		} else {
			selection.add(item);
			result = true;
		}
		finishSelection();
		return result;
	}

	public void updateColors() {
		super.setBackground(colors.getTableBackground());
	}

	private void updateFocus() {
		colors.setFocus(hasFocus);
		if (!selection.isEmpty()) {
			for (Iterator i = selection.iterator(); i.hasNext();) {
				((AbstractItem) i.next()).updateColors();
			}
			redraw();
		} else if (gtk) {
			redraw(); // focus has changed
		}
	}

//	protected void updateItemLists(int eventType) {
//		if (isDirty(DIRTY_ORDERED)) {
//			updateOrderedItems();
//			dirtyFlags &= ~DIRTY_ORDERED;
//		}
//		if (isDirty(DIRTY_VISIBLE)) {
//			updateVisibleItems();
//			dirtyFlags &= ~DIRTY_VISIBLE;
//		}
//		updatePaintedItems();
//	}

//	protected void removeOrderedItem(AbstractItem item) {
//		orderedItems.remove(item);
//	}

	protected abstract List getPaintedItems();

	protected void updatePaintedItems() {
		System.out.println("updatePaintedItems");
		
//		int top = visibleItems.isEmpty() ? -1 : getFirstPaintedItemIndex();
//		int bot = (top == -1) ? -1 : getLastPaintedItemIndex();
//		if (top != firstPaintedIndex || bot != lastPaintedIndex) {

		List newPainted = getPaintedItems();
		if(!newPainted.equals(paintedItems)) {
			System.out.println("updatePaintedItems: changed");
			
//			firstPaintedIndex = top;
//			lastPaintedIndex = bot;

			List oldPainted = (paintedItems == null) ? new ArrayList() : new ArrayList(paintedItems);
//			paintedItems = (firstPaintedIndex == -1 || firstPaintedIndex >= visibleItems.size() || lastPaintedIndex < firstPaintedIndex) ? new ArrayList()
//					: new ArrayList(visibleItems
//							.subList(firstPaintedIndex, lastPaintedIndex + 1));
			paintedItems = new ArrayList(newPainted);
//			List newPainted = new ArrayList(paintedItems);

			// remove common elements from both lists
			for (Iterator i = oldPainted.iterator(); i.hasNext();) {
				Object o = i.next();
				if (newPainted.contains(o)) {
					newPainted.remove(o);
					i.remove();
				}
			}

			// update the items
			for (Iterator i = oldPainted.iterator(); i.hasNext();) {
				((AbstractItem) i.next()).setPainted(false);
			}
			for (Iterator i = newPainted.iterator(); i.hasNext();) {
				((AbstractItem) i.next()).setPainted(true);
			}
		}
	}

	/**
	 * Resets the visibleItems list to equal the orderedItems list minus all items which
	 * return false to getVisible()
	 */
//	protected void updateVisibleItems() {
//		visibleItems = new ArrayList(orderedItems);
//		for (Iterator i = visibleItems.iterator(); i.hasNext();) {
//			AbstractItem item = (AbstractItem) i.next();
//			if(!item.getVisible()) {
//				i.remove();
//			}
//		}
//	}
}
