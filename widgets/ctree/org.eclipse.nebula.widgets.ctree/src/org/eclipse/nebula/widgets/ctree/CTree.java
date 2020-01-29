/****************************************************************************
 * Copyright (c) 2005-2006 Jeremy Dowdall
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/

package org.eclipse.nebula.widgets.ctree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
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
import org.eclipse.swt.widgets.Widget;


/**
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p> 
 */
public class CTree extends Composite implements Listener {


	/**
	 * true if the platform is detected as being "carbon"
	 */
	public static final boolean carbon = "carbon".equals(SWT.getPlatform());
	/**
	 * true if the platform is detected as being "gtk"
	 */
	public static final boolean gtk = "gtk".equals(SWT.getPlatform());
	/**
	 * true if the platform is detected as being "win32"
	 */
	public static final boolean win32 = "win32".equals(SWT.getPlatform());

	private static int checkStyle(int style) {
		int mask = SWT.BORDER | SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.MULTI
				| SWT.NO_FOCUS | SWT.CHECK;
		return (style & mask);
	}

	GC internalGC = new GC(Display.getDefault());

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
	protected CTreeItem shiftSel;
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

//	private Point mmPoint = null; // mouse move point
//	private Point mdPoint = null; // mouse down point
//	private Point muPoint = null; // mouse up point
//	private Point mmDelta = null; // mouse move delta

	protected int style = 0;
	protected Canvas body;
	protected Composite header;
	protected Table internalTable;
	CTreeColumn[] columns = new CTreeColumn[0];
	int[] columnOrder = new int[0];
	boolean fillerColumnSet = false;
	boolean nativeHeader = true;
	CTreeColumn sortColumn;
	int sortDirection = -1;
	protected ScrollBar hBar;
	protected ScrollBar vBar;

	protected boolean hasFocus = false;
	protected String emptyMessage = "";
	protected boolean linesVisible = false;
	protected boolean lastLine = false;
	protected boolean vLines = true;
	protected boolean hLines = true;
	protected SColors colors;

//	public int marginWidth = gtk ? 0 : 1;
//	public int marginHeight = gtk ? 0 : 0;

	private Listener filter;
	private List paintedItemListeners;

	protected boolean nativeGrid = true;

	protected CTreeLayout layout;

	public boolean drawViewportNorth = false;

	public boolean drawViewportEast = false;
	
	public boolean drawViewportSouth = false;

	public boolean drawViewportWest = false;

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

	public boolean paintGridAsBackground = false;

	List addedItems = new ArrayList();

	List removedItems = new ArrayList();

	int topOld = -1;

	int heightOld = -1;

	boolean updatePaintedList = false;

	private int checkColumn = -1;
	private boolean checkRoots = true;
	private int treeColumn = 0;
	private int treeIndent = 16;
	private boolean selectOnTreeToggle = false;


	List itemList = new ArrayList();
	
	private List visibleItems = null;

	public CTree(Composite parent, int style) {
		super(parent, checkStyle(style));

		this.style = style;

		colors = new SColors(getDisplay());
		updateColors();
//		setBackground(getColors().getTableBackground());
		setBackground(getDisplay().getSystemColor(SWT.COLOR_RED));

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
					if(gtk) {
						body.setLocation(-hBar.getSelection(), body.getLocation().y);
					}
					body.redraw();
				}
			});
		}
		vBar = getVerticalBar();
		if (vBar != null) {
			vBar.setVisible(false);
			vBar.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if(gtk) {
						body.setLocation(body.getLocation().x, layout.headerSize.y-vBar.getSelection());
					}
					body.redraw();
				}
			});
		}

		body = new Canvas(this, SWT.BORDER | SWT.NO_BACKGROUND);
		body.setBackground(colors.getTableBackground());

		nativeHeader = false;
		new CTreeColumn(this, 0);
		fillerColumnSet = true;
		nativeHeader = true;

		filter = new Listener() {
			public void handleEvent(Event event) {
				if(CTree.this.getShell() == ((Control) event.widget).getShell()) {
					handleFocus(SWT.FocusOut);
				}
			}
		};

		body.addKeyListener(new KeyAdapter() {}); // traverse does not work without this...
		body.addListener(SWT.FocusIn, this);
		body.addListener(SWT.MouseDown, this);
		body.addListener(SWT.MouseDoubleClick, this);
		body.addListener(SWT.MouseMove, this);
		body.addListener(SWT.MouseUp, this);
		body.addListener(SWT.Paint, new Listener() {
			public void handleEvent(Event e) {
				if (SWT.Paint == e.type) {
					paintBody(e);
				}
			}
		});
		body.addListener(SWT.Traverse, this);

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

		if((style & SWT.CHECK) != 0) checkColumn = 0;
		setLayout(layout = new CTreeLayout(this));
	}

	void addColumn(CTreeColumn column, int style) {
		if(fillerColumnSet) {
			fillerColumnSet = false;
			columns[0].dispose();
		}
		CTreeColumn[] newColumns = new CTreeColumn[columns.length+1];
		int[] newColumnOrder = new int[columnOrder.length+1];
		System.arraycopy(columns, 0, newColumns, 0, columns.length);
		System.arraycopy(columnOrder, 0, newColumnOrder, 0, columnOrder.length);
		columns = newColumns;
		columnOrder = newColumnOrder;
		columns[columns.length-1] = column;
		columnOrder[columnOrder.length-1] = columnOrder.length-1;
	}

	void addItem(CTreeItem item) {
		addItem(-1, item);
	}

	void addItem(int index, CTreeItem item) {
		if(index < 0 || index > itemList.size()-1) {
			itemList.add(item);
		} else {
			itemList.add(index, item);
		}
		addedItems.add(item);
		visibleItems = null;
		body.redraw();
	}

	void addItems() {
		addedItems = new ArrayList();
		layout(true, true);
		updatePaintedList = true;
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified
	 * when the Paint Status of an item changes.
	 * <p>
	 * An item may be considered visible, and will be returned with
	 * {@link CTree#getVisibleItems()}, even though it will not be
	 * painted on the screen. Paint status, on the other hand, refers to whether
	 * or not an item will actually be painted when the
	 * {@link CTree#paintBody(Event)} method is called.
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
	 * {@link CTree#getVisibleItems()}, even though it will not be
	 * painted on the screen. Paint status, on the other hand, refers to whether
	 * or not an item will actually be painted when the
	 * {@link CTree#paintBody(Event)} method is called.
	 * 
	 * @return an array of items that will be painted to the screen during paint
	 *         events
	 * @see #getVisibleItems()
	 */
	// public CTreeItem[] getPaintedItems() {
	// return (CTreeItem[]) paintedItems.toArray(new
	// CTreeItem[paintedItems.size()]);
	// }
	public void addSelectionListener(SelectionListener listener) {
		checkWidget();
		if (listener != null) {
			TypedListener typedListener = new TypedListener(listener);
			addListener(SWT.Selection, typedListener);
			addListener(SWT.DefaultSelection, typedListener);
		}
	}

	public void addTreeListener(TreeListener listener) {
		checkWidget ();
		if(listener != null) {
			TypedListener typedListener = new TypedListener (listener);
			addListener (SWT.Collapse, typedListener);
			addListener (SWT.Expand, typedListener);
		}
	}

	public void clear(int index, boolean all) {
		// TODO Auto-generated method stub
	}
	
	public void clearAll(boolean all) {
		// TODO Auto-generated method stub
	}

	public void deselectAll() {
		selection = new ArrayList();
		finishSelection();
	}

	private void finishSelection() {
		if(!isDisposed()) {
			for(Iterator i = selection.iterator(); i.hasNext(); ) {
				((CTreeItem) i.next()).setSelected(true);
			}
			showSelection();
			body.redraw();
			fireSelectionEvent(false);
		}
	}

	void firePaintedItemEvent(CTreeItem item, boolean isPainted) {
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
			event.item = (CTreeItem) selection.get(0);
		notifyListeners(event.type, event);
	}

	private void fireTreeEvent(Widget item, boolean collapse) {
		Event event = new Event();
		event.type = collapse ? SWT.Collapse : SWT.Expand;
		event.item = item;
		notifyListeners(event.type, event);
	}

	Composite getBody() {
		return body;
	}

	public int getCheckColumn() {
		return checkColumn;
	}

	public boolean getCheckRoots() {
		return checkRoots;
	}

	public SColors getColors() {
		return colors;
	}

	public int getColumnCount() {
		return columns.length;
	}
	
	public int[] getColumnOrder() {
		int[] order = new int[columnOrder.length];
		System.arraycopy(columnOrder, 0, order, 0, columnOrder.length);
		return order;
	}
	
	int[] getColumnWidths() {
		int[] widths = new int[columns.length];
		for(int i = 0; i < widths.length; i++) {
			widths[i] = columns[i].getWidth();
		}
		return widths;
	}

	protected Rectangle getContentArea() {
		Rectangle area = getClientArea();
		area.y = layout.headerSize.y;
		area.height -= layout.headerSize.y;
		return area;
	}

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

	Composite getHeader() {
		if (header == null) {
			header = new Composite(this, SWT.NONE);
			body.moveBelow(header);
		}
		return header;
	}

//	public int indexOf(CTreeItem item) {
//		return items.indexOf(item);
//	}

//	public int indexOf(CTreeColumn column) {
//		return (internalTable != null) ? internalTable.indexOf(column) : -1;
//	}

//	public boolean isDirty(int opcode) {
//		return ((dirtyFlags & opcode) != 0);
//	}

	public int getHeaderHeight() {
		if(nativeHeader) {
			return internalTable.getHeaderHeight();
		}
		// TODO: getHeaderHeight... get from the first non-native column?
		return 0;
	}

//	public boolean isOperation(int opcode) {
//		return operation == opcode;
//	}

	// public boolean isSelected(int index) {
	// return getItem(index).isSelected();
	// }

	public boolean getHeaderVisible() {
		if(nativeHeader) {
			return internalTable.getHeaderVisible();
		}
		return false;
	}

	Table getInternalTable() {
		if(internalTable == null) {
			internalTable = new Table(getHeader(), SWT.NONE);
			internalTable.setLinesVisible(linesVisible);
		}
		return internalTable;
	}

	public CTreeItem getItem(int index) {
		if(isEmpty() || index < 0 || index > itemList.size()-1) return null;
		return (CTreeItem) itemList.get(index);
	}

	public int getItemCount() {
		return itemList.size();
	}

	public int getItemHeight() {
		return ((CTreeLayout) layout).getItemHeight();
	}

	/**
	 * Returns a (possibly empty) array of items contained in the
	 * receiver that are direct item children of the receiver.  These
	 * are the roots of the tree.
	 * <p>
	 * Note: This is not the actual structure used by the receiver
	 * to maintain its list of items, so modifying the array will
	 * not affect the receiver. 
	 * </p>
	 *
	 * @return the items
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public CTreeItem[] getItems() {
		if(isEmpty()) return new CTreeItem[0];
		return (CTreeItem[]) itemList.toArray(new CTreeItem[itemList.size()]);
	}

	/**
	 * returns a deep list of items belonging to the given item
	 * {@inheritDoc}
	 */
//	List getItems(CTreeItem item) {
//		return getItems(item, true);
//	}
	List getItems(CTreeItem item, boolean all) {
		List l = new ArrayList();
		CTreeItem[] items = item.getItems();
		for(int i = 0; i < items.length; i++) {
			l.add(items[i]);
			if(all || items[i].getExpanded()) {
				l.addAll(getItems(items[i], all));
			}
		}
		return l;
	}

	/**
	 * Get a list of items within, or touching, the given rectangle.
	 * 
	 * @param rect
	 * @return
	 */
	protected CTreeItem[] getItems(Rectangle rect) {
		if(isEmpty()) {
			return new CTreeItem[0];
		} else {
			List il = new ArrayList();
			for(Iterator i = paintedItems.iterator(); i.hasNext(); ) {
				CTreeItem item = (CTreeItem) i.next();
				Rectangle[] ra = item.getCellBounds();
				for (int j = 0; j < ra.length; j++) {
					if (ra[j].intersects(rect)) {
						il.add(item);
						break;
					}
				}
			}
			return (il.isEmpty()) ? new CTreeItem[0] : (CTreeItem[]) il.toArray(new CTreeItem[il.size()]);
		}
	}

	public boolean getLastLineVisible() {
		return lastLine;
	}

	public boolean getLinesVisible() {
		return linesVisible;
	}

	public boolean getNativeHeader() {
		return nativeHeader;
	}
	
	protected List getPaintedItems() {
		int top = getScrollPosition().y;
		int bot = top + body.getClientArea().height;
		int itop = 0;
		int ibot = 0;
		List list = new ArrayList();
		boolean painting = false;
		for(Iterator i = items(false).iterator(); i.hasNext(); ) {
			CTreeItem item = (CTreeItem) i.next();
			Rectangle r = item.getBounds();
			ibot = r.y+r.height;
			if(itop <= top && top < ibot) {
				painting = true;
			}
			if(painting) {
				list.add(item);
			}
			if(itop < bot && bot <= ibot) {
				break;
			}
			itop = r.y+r.height;
		}
		return list;
	}
	public CTreeItem getParentItem() {
		// TODO Auto-generated method stub
		return null;
	}
	
	Point getScrollPosition() {
		return new Point(
				(hBar == null || hBar.isDisposed()) ? 0 : hBar.getSelection(),
				(vBar == null || vBar.isDisposed()) ? 0 : vBar.getSelection());
	}
	
	public CTreeItem[] getSelection() {
		return selection.isEmpty() ? 
			new CTreeItem[0] : 
				(CTreeItem[]) selection.toArray(new CTreeItem[selection.size()]);
	}
	public int getSelectionCount() {
		return selection.size();
	}
	/**
	 * @see CTree#setSelectOnToggle(boolean)
	 * @return
	 */
	public boolean getSelectOnToggle() {
		return selectOnToggle;
	}

	/**
	 * @see CTree#setSelectOnTreeToggle(boolean)
	 * @return true if selection state is to changed when a toggle is clicked, false otherwise
	 */
	public boolean getSelectOnTreeToggle() {
		return selectOnTreeToggle;
	}

//	public void redraw(CTreeItem item) {
//		// TODO: paint / redraw individual item
//		// Rectangle r = item.getBounds();
//		// body.redraw(r.x-1, r.y-1, r.width+2, r.height+2, true);
//		redraw();
//	}

	public int getSortDirection() {
		return sortDirection;
	}

	public int getStyle() {
		return style;
	}

	public CTreeItem getTopItem() {
		int top = getScrollPosition().y;
		int itop = 0;
		int ibot = 0;
		for(Iterator i = items(false).iterator(); i.hasNext(); ) {
			CTreeItem item = (CTreeItem) i.next();
			Rectangle r = item.getBounds();
			ibot = r.y+r.height;
			if(itop <= top && top < ibot) {
				return item;
			}
			itop = r.y+r.height;
		}
		return null;
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

	public int getTreeColumn() {
		return treeColumn;
	}
	
	public int getTreeIndent() {
		return treeIndent;
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
			handleMouseEvents(event);
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
			for(Iterator i = items(false).iterator(); i.hasNext(); ) {
				if(((CTreeItem) i.next()).contains(focusControl)) return;
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

	protected void handleMouseEvents(Event event) {
		CTreeItem item = null;
		if (event.widget == body) {
			item = getItem(event.x, event.y);
		} else if (event.item instanceof CTreeItem) {
			item = (CTreeItem) event.item;
		}
		
		switch (event.type) {
		case SWT.MouseDoubleClick:
			if(item != null && (selectOnToggle || !item.isToggle(event.x, event.y))) {
				fireSelectionEvent(true);
			}
			break;
		case SWT.MouseDown:
			if(!hasFocus) setFocus();
			if(item == null) {
				if(event.widget == body) {
					item = getItem(event.x, event.y);
				} else if(event.item instanceof CTreeItem) {
					item = (CTreeItem) event.item;
				}
			}
			switch(event.button) {
			// TODO - popup menu: not just for mouse down events!
			case 3:
				Menu menu = getMenu();
				if ((menu != null) && ((menu.getStyle() & SWT.POP_UP) != 0)) {
					menu.setVisible(true);
				}
			case 1:
				if(selectOnToggle || !item.isToggle(event.x, event.y)) {
					if(selectOnTreeToggle || !item.isTreeToggle(event.x,event.y)) {
						if((event.stateMask & SWT.SHIFT) != 0) {
							if(shiftSel == null) {
								if(selection.isEmpty()) selection.add(item);
								shiftSel = (CTreeItem) selection.get(selection.size() - 1);
							}
							setSelection(shiftSel, item);
						} else if((event.stateMask & SWT.CONTROL) != 0) {
							toggleSelection(item);
							shiftSel = null;
						} else {
							setSelection(item);
							shiftSel = null;
						}
					}
				}
				break;
			}
			break;
		case SWT.MouseMove:
			// TODO: make toggles more dynamic
			break;
		case SWT.MouseUp:
			if(item.isToggle(event.x,event.y)) {
				boolean open = item.isOpen(event.x,event.y);
				if(item.isTreeToggle(event.x,event.y)) {
//					visibleItems = null;
					item.setExpanded(!open);
					fireTreeEvent(item, !open);
				} else {
					item.getCell(event.x, event.y).setOpen(!open);
				}
			}
			break;
		}

	}

	private void handleTraverse(Event event) {
		switch (event.detail) {
		case SWT.TRAVERSE_RETURN:
			if (event.data instanceof CTreeCell) {
				fireSelectionEvent(true);
			}
			break;
		case SWT.TRAVERSE_ARROW_NEXT:
			if(!selection.isEmpty()) {
				setSelection(((CTreeItem) selection.get(selection.size()-1)).nextVisible());
			}
			break;
		case SWT.TRAVERSE_ARROW_PREVIOUS:
			if(!selection.isEmpty()) {
				setSelection(((CTreeItem) selection.get(0)).previousVisible());
			}
			break;
		}
	}

	/**
	 * Convenience method indicating whether or not the treeColumn is set to an
	 * actual column, and thus the tree hierarchy will be displayed.
	 * <p>Note that if the hierarchy is not displayed, then certain methods are
	 * able to be optimized and will take advantage of this fact</p>
	 * @return true if treeColumn is set to an existing column, false otherwise
	 */
	public boolean hasTreeColumn() {
		return treeColumn >= 0 && treeColumn < getColumnCount();
	}

	public int indexOf(CTreeColumn column) {
		return indexOf(column);
	}

	public int indexOf(CTreeItem item) {
		return itemList.indexOf(item);
	}

	CTreeColumn getColumn(int index) {
		if(index >= 0 && index < columns.length) {
			return columns[index];
		}
		return null;
	}

	CTreeColumn[] getColumns() {
		CTreeColumn[] ca = new CTreeColumn[columns.length];
		System.arraycopy(columns, 0, ca, 0, columns.length);
		return ca;
	}

	CTreeItem getItem(int x, int y) {
		// must iterate in reverse drawing order in case items overlap each other
		for(ListIterator i = paintedItems.listIterator(paintedItems.size()); i.hasPrevious();) {
			CTreeItem item = (CTreeItem) i.previous();
			if(item.contains(x,y)) return item;
		}
		return null;
	}

	/**
	 * returns the sort column of this container as an CTreeColumn.
	 * subclasses should override to provide an appropriate cast.
	 * @return the sort column
	 */
	protected CTreeColumn getSortColumn() {
		return sortColumn;
	}

	public boolean isEmpty() {
		return itemList.isEmpty();
	}

	/**
	 * Use this method to find out if an item is visible, and thus has the potential to be
	 * painted.
	 * <p>An Item will be visible when every parent between it and the root of the tree
	 * is expanded</p>
	 * @param item the item in question
	 * @return true if the item can be painted to the screen, false if it is hidden due to a
	 * parent item being collapsed
	 */
	public boolean isVisible(CTreeItem item) {
		if(item.getVisible()) {
			CTreeItem parentItem = item.getParentItem();
			if(parentItem == null) return true;
			if(parentItem.getExpanded()) return isVisible(parentItem);
		}
		return false;
	}

	List items(boolean all) {
		if(visibleItems == null) {
			visibleItems = new ArrayList();
			for(Iterator i = itemList.iterator(); i.hasNext(); ) {
				CTreeItem item = (CTreeItem) i.next();
				if(all || item.isVisible()) {
					visibleItems.add(item);
					if(all || item.getExpanded()) {
						visibleItems.addAll(getItems(item, all));
					}
				}
			}
		}
		return visibleItems;
	}

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
	void layout(int eventType, CTreeCell cell) {
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
	void layout(int eventType, CTreeColumn column) {
		layout.layout(eventType, column);
//		updatePaintedList = true;
	}

	/**
	 * <p>
	 *  Event types:
	 *  <ul>
	 *   <li>SWT.Collapse</li>
	 *   <li>SWT.Expand</li>
	 *   <li>SWT.Hide</li>
	 *   <li>SWT.Move</li>
	 *   <li>SWT.Show</li>
	 *  </ul>
	 * </p>
	 * @param eventType
	 * @param item
	 */
	void layout(int eventType, CTreeItem item) {
		if(SWT.Collapse == eventType) {
			layout.layout(eventType, item);
			visibleItems = null;
			updatePaintedList = true;
		} else if(SWT.Expand == eventType) {
			layout.layout(eventType, item);
			visibleItems = null;
			updatePaintedList = true;
		} else if(SWT.Hide == eventType && isVisible((CTreeItem)item)) {
			layout.layout(eventType, item);
			updatePaintedList = true;
			item.setVisible(false);
		} else if(SWT.Show == eventType && !isVisible((CTreeItem)item)) {
			layout.layout(eventType, item);
			updatePaintedList = true;
			item.setVisible(true);
		}
	}

	Rectangle mapRectangle(int x, int y, int width, int height) {
		Rectangle r = new Rectangle(x,y,width,height);
		Point point = getScrollPosition();
		r.x += point.x;
		r.y += point.y;
		return r;
	}

	Rectangle mapRectangle(Rectangle rect) {
		return mapRectangle(rect.x, rect.y, rect.width, rect.height);
	}
	
	protected void paintBackground(GC gc, Rectangle ebounds) {}

	protected void paintBody(Event e) {
		if(!addedItems.isEmpty()) addItems();
		if(!removedItems.isEmpty()) removeItems();
		
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
		paintFocus(gc, ebounds);

		e.gc.drawImage(image, ebounds.x, ebounds.y);
		gc.dispose();
		image.dispose();
	}

	protected void paintColumns(GC gc, Rectangle ebounds) {
		CTreeColumn[] columns = getColumns();
		for (int i = 0; i < columns.length; i++) {
			columns[i].paint(gc, ebounds);
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
	
	protected void paintGridLines(GC gc, Rectangle ebounds) {
		Rectangle r = getClientArea();

		if(linesVisible && (!nativeGrid || getGridLineWidth() > 0)) {
			gc.setForeground(getColors().getGrid());

			int y = getScrollPosition().y;
			int rowHeight = 0;
			
			if(win32 && nativeGrid) {
				if(isEmpty()) {
					if(emptyMessage.length() > 0) {
						Point tSize = gc.textExtent(emptyMessage);
						rowHeight = tSize.y+2;
					}
				} else {
					int gridline = getGridLineWidth();
					for(Iterator i = items(false).iterator(); i.hasNext(); ) {
						gc.drawLine(r.x, y, r.x+r.width, y);
						CTreeItem item = (CTreeItem) i.next();
						y += item.getSize().y + gridline;
					}
					rowHeight = getItemHeight();
				}
			}

			if(hLines) {
				int gridline = getGridLineWidth();
				while(y < r.height) {
					gc.drawLine(r.x, y, r.x+r.width, y);
					y += rowHeight + gridline;
				}
			}
			if(vLines) {
				if(getColumnWidths() != null && getColumnWidths().length > 1) {
					int x = -1;
					for(int i = 0; i < getColumnWidths().length; i++) {
						x += getColumnWidths()[i];
						gc.drawLine(
								x,
								r.y,
								x,
								r.y+r.height
						);
					}
				}
			}
		}
	}
	
	protected void paintItemBackgrounds(GC gc, Rectangle ebounds) {
		if(gtk && nativeGrid && !paintedItems.isEmpty()) {
			Rectangle r = getClientArea();
			int gridline = getGridLineWidth();
			int firstPaintedIndex = items(false).indexOf(paintedItems.get(0));
			for(int i = 0; i < paintedItems.size(); i++) {
				CTreeItem item = (CTreeItem) paintedItems.get(i);
				if(linesVisible && ((firstPaintedIndex + i + 1) % 2 == 0)) {
					gc.setBackground(getColors().getGrid());
					gc.fillRectangle(
							r.x,
							item.getBounds().y-ebounds.y,
							r.width,
							item.getSize().y+gridline
					);
					item.setGridLine(true);
				} else {
					item.setGridLine(false);
				}
			}
		}
	}

	protected void paintItems(GC gc, Rectangle ebounds) {
		if(isEmpty()) {
			if (emptyMessage.length() > 0) {
				Point bSize = getSize();
				Point tSize = gc.textExtent(emptyMessage);
				gc.setForeground(colors.getItemForegroundNormal());
				gc.drawText(emptyMessage, (bSize.x - tSize.x) / 2 - ebounds.x,
						4 - ebounds.y);
			}
		} else {
			Image image = new Image(gc.getDevice(), ebounds);
			GC gc2 = new GC(image);
			for (Iterator iter = paintedItems.iterator(); iter.hasNext();) {
				CTreeItem item = (CTreeItem) iter.next();
				for(int i = 0; i < item.cells.length; i++) {
					CTreeCell cell = item.cells[i];
					cell.paint(gc, ebounds);
					Rectangle cb = cell.getClientArea();
					gc2.setBackground(getDisplay().getSystemColor(SWT.COLOR_CYAN));
					gc2.fillRectangle(ebounds);
					if(!cb.isEmpty() &&
							cell.paintClientArea(gc2, 
									new Rectangle(0,0,cb.width,cb.height))) {
						gc.drawImage(image,
							0,0,Math.min(ebounds.width, cb.width),Math.min(ebounds.height, cb.height),
							cb.x-ebounds.x,cb.y-ebounds.y,cb.width,cb.height
							);
					}
				}
			}
			gc2.dispose();
			image.dispose();
		}
	}

	protected void paintSelectionIndicators(GC gc, Rectangle ebounds) {
		if(win32 && !selection.isEmpty()) {
			for(Iterator i = selection.iterator(); i.hasNext(); ) {
				CTreeItem item = (CTreeItem) i.next();
				if(item.isVisible()) {
					Rectangle r = item.getBounds();
					r.x = 0;
					r.y -= getScrollPosition().y;
					r.width = getClientArea().width;
					r.height = item.getSize().y + getGridLineWidth();
					gc.setBackground(colors.getItemBackgroundSelected());
					gc.fillRectangle(r);
				}
			}
		}
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

	public void redraw(CTreeCell cell) {
		Rectangle r = cell.getBounds();
		redraw(r.x-1, r.y-1, r.width+2, r.height+2, true);
	}

	public void redraw(CTreeItem item) {
		Rectangle r = item.getBounds();
		redraw(r.x-1, r.y-1, r.width+2, r.height+2, true);
	}

	public void removeAll() {
		if(!isEmpty()) {
			boolean selChange = false;
			if(!selection.isEmpty()) {
				selection = new ArrayList();
				selChange = true;
			}
			visibleItems = null;
			paintedItems = new ArrayList();

			for(Iterator i = items(true).iterator(); i.hasNext(); ) {
				CTreeItem item = (CTreeItem) i.next();
				if(!item.isDisposed()) {
					removedItems.add(item);
					item.dispose();
				}
			}
			itemList = new ArrayList();

			if(selChange) fireSelectionEvent(false);
		}
	}

	void removeColumn(CTreeColumn column) {
		if(columns.length > 0) {
			CTreeColumn[] newColumns = new CTreeColumn[columns.length-1];
			int[] newColumnOrder = new int[columnOrder.length-1];
			System.arraycopy(columns, 0, newColumns, 0, newColumns.length);
			System.arraycopy(columnOrder, 0, newColumnOrder, 0, newColumnOrder.length);
			columns = newColumns;
			columnOrder = newColumnOrder;
			// TODO: removeColumn - remove sortcolumn...
		}
	}
	void removeItem(CTreeItem item) {
		itemList.remove(item);
		if(!removedItems.contains(item)) {
			removedItems.add(item);
			boolean selChange = selection.remove(item);
			if(selChange) fireSelectionEvent(false);
			redraw();
		}
	}

	private void removeItems() {
		removedItems = new ArrayList();
		visibleItems = null;
		layout(true, true);
		updatePaintedList = true;
	}

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

	public void removeTreeListener(TreeListener listener) {
		checkWidget ();
		if(listener != null) {
			removeListener(SWT.Collapse, listener);
			removeListener(SWT.Expand, listener);
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


	public void selectAll() {
		if((getStyle() & SWT.SINGLE) == 0) {
			selection = new ArrayList();
			for(Iterator i = items(true).iterator(); i.hasNext(); ) {
				CTreeItem item = (CTreeItem ) i.next();
				item.setSelected(true);
				selection.add(item);
			}
			finishSelection();
		}
	}

	/**
	 * disabled: use the SColors class instead
	 */
	public void setBackground(Color color) {
	}

	public void setCellClass(Class clazz) {
		cellClasses = new Class[] { clazz };
	}
	
	public void setCellClasses(Class[] classes) {
		cellClasses = classes;
	}

	public void setCheckColumn(int column, boolean roots) {
		if(checkColumn != column) {
			checkColumn = column;
			checkRoots = roots;
			// TODO update check cells
		}
	}
	
	public void setColumnOrder(int[] order) {
		if(internalTable != null) internalTable.setColumnOrder(order);
	}
	
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

//	public CTreeItem[] getVisibleItems() {
//		return (visibleItems.isEmpty()) ? new CTreeItem[0] : (CTreeItem[]) visibleItems.toArray(new CTreeItem[visibleItems.size()]);
//	}

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

	public void setHorizontalLinesVisible(boolean visible) {
		hLines = visible;
	}
	
	public void setInsertMark(CTreeItem item, boolean before) {
		// TODO Auto-generated method stub
	}
	public void setItemCount(int count) {
		// TODO Auto-generated method stub
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
		if (layout instanceof CTreeLayout) {
			this.layout = (CTreeLayout) layout;
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
	
	public void setNativeHeader(boolean nativeHeader) {
		this.nativeHeader = nativeHeader;
	}
	
	void setOrigin(int x, int y) {
		if(hBar != null && !hBar.isDisposed()) hBar.setSelection(x);
		if(vBar != null && !vBar.isDisposed()) vBar.setSelection(y);
		redraw();
	}
	
	public void setRedraw(boolean redraw) {
		super.setRedraw(redraw);
		if (internalTable != null) {
			internalTable.setRedraw(redraw);
		}
	}

	void setScrollPosition(Point origin) {
		setOrigin(origin.x,origin.y);
	}
	
	/**
	 * Enables items in this Container to be selected
	 * 
	 * @param selectable
	 */
	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
		for(Iterator i = items(true).iterator(); i.hasNext();) {
			((CTreeItem) i.next()).setEnabled(selectable);
		}
	}

	public void setSelection(CTreeItem item) {
		for(Iterator i = selection.iterator(); i.hasNext(); ) {
			((CTreeItem) i.next()).setSelected(false);
		}
		if(item == null) {
			selection = new ArrayList();
		} else {
			selection = new ArrayList(Collections.singleton(item));
			shiftSel = item;
		}
		finishSelection();
	}

	public void setSelection(CTreeItem from, CTreeItem to) {
		// TODO Auto-generated method stub
	}

	public void setSelection(CTreeItem[] items) {
		for(Iterator i = selection.iterator(); i.hasNext(); ) {
			((CTreeItem) i.next()).setSelected(false);
		}
		if(items == null || items.length == 0) {
			selection = new ArrayList();
		} else {
			selection = new ArrayList(Arrays.asList(items));
			shiftSel = items[0];
		}
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
	
	/**
	 * If the the user clicks on the toggle of the treeCell the corresponding item will
	 * become selected if, and only if, selectOnTreeToggle is true
	 * @param select the new state of selectOnTreeToggle
	 */
	public void setSelectOnTreeToggle(boolean select) {
		selectOnToggle = select;
	}

	public void setSortColumn(CTreeColumn column) {
		sortColumn = column;
		if(nativeHeader) {
			internalTable.setSortColumn(column.nativeColumn);
		}
	}

	public void setSortDirection(int direction) {
		sortDirection = direction;
		if(nativeHeader) {
			internalTable.setSortDirection(direction);
		}
	}

//	protected void addOrderedItem(CTreeItem item) {
//		CTreeItem cti = (CTreeItem) item;
//		if(!hasTreeColumn() || !cti.hasParentItem()) {
//			orderedItems.add(item);
//		} else {
//			CTreeItem parent = cti.getParentItem();
//			int ix = 0;//orderedItems.indexOf(parent);
//			if(parent.hasItems()) {
//				ix += parent.getItemCount();
//			}
//			orderedItems.add(ix, item);
//		}
//	}

	public void setTopItem(CTreeItem item) {
		setScrollPosition(
				new Point(getScrollPosition().x, item.getBounds().y - getContentArea().y));
	}

	/**
	 * The Tree Column indicates which column should act as the tree by placing 
	 * the expansion toggle in its cell.
	 * <p>If column is greater than the number of columns, or is less than zero
	 * then no column will have an expansion toggle (or room for one).</p>
	 * @param column the column to use for the tree
	 */
	public void setTreeColumn(int column) {
		if(treeColumn != column) {
			treeColumn = column;
		}
	}

	/**
	 * Sets the amount to indent child items from their parent.
	 * <p>Suitable defaults are set according to SWT.Platform, but the option
	 * to customize is still exposed through this metho</p>
	 * <p>Note that only the Tree Column will be indented; if there is no Tree Column
	 * then this setting will have no affect.  If you need the entire Item and all of
	 * its columns to be indented, please file a feature request at 
	 * sourceforge.net/projects/calypsorcp</p>
	 * @param indent
	 */
	public void setTreeIndent(int indent) {
		treeIndent = indent;
	}

	public void setVerticalLinesVisible(boolean visible) {
		vLines = visible;
	}

	/**
	 * TODO
	 * @param column
	 */
	public void showColumn(CTreeColumn column) {
		// TODO Auto-generated method stub
	}

//	public int getSortDirection() {
//		// TODO Auto-generated method stub
//		return 0;
//	}

	public void showItem(CTreeItem item) {
		Rectangle r = item.getBounds();
		Rectangle c = getContentArea();
		Point scroll = getScrollPosition();
		if(r.height > c.height || !c.contains(new Point(r.x, r.y - scroll.y))) {
			setScrollPosition(new Point(scroll.x, r.y - c.y));
		} else if(!c.contains(new Point(r.x, r.y + r.height - 1 - scroll.y))) {
			setScrollPosition(new Point(scroll.x, r.y - c.y - c.height + r.height));
		}
		// TODO: showItem needs to work horizontally as well (call showColumn)
	}

	public void showSelection() {
		if(!selection.isEmpty()) {
			showItem((CTreeItem) selection.get(0));
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
	public boolean toggleSelection(CTreeItem item) {
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
				((CTreeItem) i.next()).updateColors();
			}
			redraw();
		} else if (gtk) {
			redraw(); // focus has changed
		}
	}

//	public void setSortColumn(CTreeColumn column) {
//		
//	}

//	public void setSortDirection(int direction) {
//		// TODO Auto-generated method stub
//	}

	protected void updatePaintedItems() {
//		System.out.println("updatePaintedItems");
		
		List newPainted = getPaintedItems();
		if(!newPainted.equals(paintedItems)) {
//			System.out.println("updatePaintedItems: changed");
	
			List oldPainted = (paintedItems == null) ? new ArrayList() : new ArrayList(paintedItems);
			paintedItems = new ArrayList(newPainted);

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
				((CTreeItem) i.next()).setPainted(false);
			}
			for (Iterator i = newPainted.iterator(); i.hasNext();) {
				((CTreeItem) i.next()).setPainted(true);
			}
		}
	}

//	public void showColumn(CTreeColumn column) {
//		// TODO Auto-generated method stub
//	}

}
