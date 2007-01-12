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
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TypedListener;


/**
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p> 
 */
public abstract class AbstractContainer extends Composite implements Listener {

	public static final boolean gtk = "gtk".equals(SWT.getPlatform());
	public static final boolean win32 = "win32".equals(SWT.getPlatform());
	public static final GC staticGC = new GC(Display.getDefault());

	public static final int OP_NONE 			= 0;
	public static final int OP_ADD 				= 1;
	public static final int OP_REMOVE 			= 2;
	public static final int OP_CELL_COLLAPSE	= 3;
	public static final int OP_CELL_EXPAND 		= 4;
	public static final int OP_COLUMN_RESIZE	= 5;
	public static final int DIRTY_ORDERED 		= 1 << 0;
	public static final int DIRTY_PAINTED		= 1 << 1;
	public static final int DIRTY_VISIBLE		= 1 << 2;
	public static final int MODE_NORMAL			= 0;
	public static final int MODE_MARQUEE		= 1;
	public static final int MODE_CREATE			= 2;

	private static int getParentStyle(int style) {
		int mask = SWT.BORDER;
		return style & mask;
	}

	/**
	 * A list of all items belonging to this container.<br>
	 * Items are in the order that they were added, taking into account that some
	 * may have been added at a requested index.
	 */
	protected List items = new ArrayList();
	/**
	 * A list of all items belonging to this container.<br>
	 * Items are in the order that is relevant to the concrete subclass; default is
	 * same as the items list.
	 */
	protected List orderedItems = new ArrayList();
	/**
	 * A list of items that can be painted to the screen, whether they are on-screen or
	 * not, as determined by the concrete subclass; default is the orderedItems list minus
	 * the individual items that request to not be drawn due to Item.getVisible() returning
	 * false.<br>
	 * Items are in the order as specified by the orderedItems list.
	 */
	protected List visibleItems = new ArrayList();
	/**
	 * A list of items that will actually be painted to the screen.<br>
	 * Subclasses may override the order as the order of paintedItems represents the
	 * final drawing order of the items - the last item in this list is drawn to the 
	 * screen last and is, therefore, drawn on top of any overlapping items.
	 */
	protected List paintedItems = new ArrayList();
	/**
	 * A list of the selected items.  Only items which are in the visibleItems list
	 * can be in this list.
	 */
	protected List selection = new ArrayList();
	/**
	 * The first item selected with the SHIFT or CTRL key pressed.  Used to create the
	 * selection array when selecting multiple items.
	 */
	protected AbstractItem shiftSel;
	/**
	 * Used in conjunction with MODE_MARQUEE.  Signifies that a selection created with
	 * the marquee selection tool will be "held onto" during the immediately following
	 * mouse operation. Otherwise, the marquee selection could never be used.
	 */
	protected boolean holdSelection = false;
	/**
	 * Signifies whether or not the items of this container can be selected.
	 */
	protected boolean selectable = true;
	/**
	 * Signifies whether or not the items of this container are selected when their
	 * toggle is clicked on, as opposed to their body.
	 */
	protected boolean selectOnToggle = true;

	private int mode = MODE_NORMAL;

	ScrolledComposite sc;
	protected Canvas body;
	protected Table internalTable;

	protected boolean hasFocus = false;
	protected String emptyMessage = "";
	protected boolean linesVisible = false;
	protected boolean lastLine = false;
	protected boolean vLines = true;
	protected boolean hLines = true;
	protected SColors colors;

	public int marginWidth = gtk ? 0 : 1;
	public int marginHeight = gtk ? 0 : 0;
	protected int style = 0;
	protected AbstractItem dirtyItem = null;
	int operation = OP_NONE;
	protected int dirtyFlags = 0;

	private Listener filter;
	private List paintedItemListeners;

	protected AbstractItem[] removedItems = null;
	protected int rowHeight = 20;
	protected boolean nativeGrid = true;
	protected boolean autoFillCells = true;

	protected AbstractLayout layout;

//	public static final int STATE_NORMAL		= 0;
//	public static final int STATE_BUSY 		= 1;
	private List activeItems = new ArrayList();
//	private boolean selectionActive = false;

	public AbstractContainer(Composite parent, int style) {
		super(parent, getParentStyle(style));

		this.style = style;

		colors = new SColors(getDisplay());
		updateColors();

		createContents(style);

		filter = new Listener() {
			public void handleEvent(Event event) {
				if(AbstractContainer.this.getShell() == ((Control)event.widget).getShell()) {
					handleFocus(SWT.FocusOut);
				}
			}
		};

		addListener(SWT.Dispose, new Listener() {
			public void handleEvent(Event event) {
				getDisplay().removeFilter(SWT.FocusIn, filter);
				if(internalTable != null && !internalTable.isDisposed()) internalTable.dispose();
				if(sc != null && !sc.isDisposed()) sc.dispose();
				if(body != null && !body.isDisposed()) body.dispose();
			}
		});
	}

//	protected void addColumn(CContainerColumn column, int style) {
//	column.column = new TableColumn(getInternalTable(), style);
//	columns.put(column, column);
//	}

	protected void addItem(AbstractItem item) {
		addItem(items.size(), item);
	}

	protected void addItem(int index, AbstractItem item) {
		if(!items.contains(item)) {
			if(index < 0) index = items.size();

			// add the item to the list
			if(index >= items.size()) {
				items.add(item);
			} else {
				items.add(index, item);
			}

			// setup properties and event handlers for the item
			item.setEnabled(selectable);
			item.updateColors();
			item.addListener(SWT.FocusIn, this);
			item.addListener(SWT.MouseDown, this);
			item.addListener(SWT.Traverse, this);

			setDirtyItem(item);
			setDirtyFlag(DIRTY_ORDERED);
			setOperation(OP_ADD);
			opLayout();
		}
	}
	
	public void addListener(int eventType, Listener listener) {
		switch(eventType) {
		case SWT.MouseDoubleClick:
		case SWT.MouseDown:
		case SWT.MouseEnter:
		case SWT.MouseExit:
		case SWT.MouseHover:
		case SWT.MouseMove:
		case SWT.MouseUp:
		case SWT.MouseWheel:
		case SWT.KeyDown:
		case SWT.KeyUp:
			body.addListener(eventType, listener);
			break;
		default:
			super.addListener(eventType, listener);
			break;
		}
	}

	public void addMouseListener(MouseListener listener) {
		body.addMouseListener(listener);
	}
	
	public void addMouseMoveListener(MouseMoveListener listener) {
		body.addMouseMoveListener(listener);
	}
	
	public void addMouseTrackListener(MouseTrackListener listener) {
		body.addMouseTrackListener(listener);
	}
	
	/**
	 * Adds the listener to the collection of listeners who will be notified when the Paint Status of 
	 * an item changes.
	 * <p>
	 * An item may be considered visible, and will be returned with {@link AbstractContainer#getVisibleItems()},
	 * even though it will not be painted on the screen. Paint status, on the other hand, refers to whether 
	 * or not an item will actually be painted when the {@link AbstractContainer#paintBody(Event)} method is called.
	 * </p>
	 * <p>
	 * The Event that is passed to this listener will have the item, whose Paint Status has changed, 
	 * set as Event.item.  The actual Paint Status is dertermined through the value of the Event's 
	 * detail field: values > 0 mean the item will be painted, while values < 0 mean the item will
	 * not be painted.
	 * </p>
	 * @param listener the listener which should be notified
	 * @see #removePaintedItemListener
	 * @see #getPaintedItems()
	 */
	public void addPaintedItemListener(Listener listener) {
		if(paintedItemListeners == null) {
			paintedItemListeners = new ArrayList();
		}
		if(!paintedItemListeners.contains(listener)) {
			paintedItemListeners.add(listener);
		}
	}

	/**
	 * An item may be considered visible, and will be returned with {@link AbstractContainer#getVisibleItems()},
	 * even though it will not be painted on the screen. Paint status, on the other hand, refers to whether 
	 * or not an item will actually be painted when the {@link AbstractContainer#paintBody(Event)} method is called.
	 * 
	 * @return an array of items that will be painted to the screen during paint events
	 * @see #getVisibleItems()
	 */
	public AbstractItem[] getPaintedItems() {
		return (AbstractItem[]) paintedItems.toArray(new AbstractItem[paintedItems.size()]);
	}

	public void addSelectionListener(SelectionListener listener) {
		checkWidget ();
		if(listener != null) {
			TypedListener typedListener = new TypedListener (listener);
			addListener (SWT.Selection, typedListener);
			addListener (SWT.DefaultSelection, typedListener);
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
		sc = new ScrolledComposite(this, style & (SWT.H_SCROLL | SWT.V_SCROLL));
		if(sc.getHorizontalBar() != null) {
			sc.getHorizontalBar().addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if(internalTable != null) {
						if(win32) { // pogramatic scrolling doesn't work on win32
							int sel = sc.getHorizontalBar().getSelection();
							Rectangle hBounds = internalTable.getBounds();
							hBounds.x = -sel;
							hBounds.width += sel;
							internalTable.setBounds(hBounds);
						} else {
							internalTable.getHorizontalBar().setSelection(sc.getHorizontalBar().getSelection());
						}
					}
				}
			});
		}
		if(sc.getVerticalBar() != null) {
			sc.getVerticalBar().addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
					body.redraw();
				}
				public void widgetSelected(SelectionEvent e) {
					body.redraw();
				}
			});
		}
		sc.addListener(SWT.FocusIn, this);

		setBackground(getColors().getTableBackground());
		
		body = new Canvas(sc, SWT.NO_BACKGROUND);
		body.setBackground(getColors().getTableBackground());
		body.addKeyListener(new KeyAdapter() {}); // traverse does not work without this...
		body.addListener(SWT.FocusIn, this);
		body.addListener(SWT.MouseDown, this);
		body.addListener(SWT.MouseDoubleClick, this);
		body.addListener(SWT.MouseMove, this);
		body.addListener(SWT.MouseUp, this);
		body.addListener(SWT.Paint, new Listener() {
			public void handleEvent(Event e) {
				if(SWT.Paint == e.type) {
					paintBody(e);
				}
			}
		});
		body.addListener(SWT.Traverse, this);

		// set the body as scrolled content of sc
		sc.setContent(body);

		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
	}

	public void deselect(int index) {
		deselect(index, true);
	}

	private void deselect(int index, boolean finish) {
		if(index >= 0 && index < visibleItems.size()) {
			selection.remove(visibleItems.get(index));
		}

		if(finish) {
			finishSelection();
		}
	}

	public void deselect(int start, int end) {
		if((start >= 0) && (start < visibleItems.size()) && (end >= 0) && (end < visibleItems.size())) {
			int i1 = (start < end) ? start : end;
			int i2 = (start < end) ? end : start;
			for(int i = i1; i <= i2; i++) {
				deselect(i, false);
			}
			finishSelection();
		}
	}

	public void deselect(int[] indices) {
		for(int i = 0; i < indices.length; i++) {
			deselect(indices[i], false);
		}
		finishSelection();
	}

	public void deselectAll() {
		selection = new ArrayList();
		finishSelection();
	}

	public void dispose() {
		List l = new ArrayList(items);
		for(Iterator i = l.iterator(); i.hasNext(); ) {
			((AbstractItem) i.next()).dispose();
		}
		super.dispose();
	}

	private void finishSelection() {
		if(!isDisposed()) {
			for(Iterator i = visibleItems.iterator(); i.hasNext(); ) {
				AbstractItem item = ((AbstractItem) i.next());
				item.setSelected(selection.contains(item));
			}
			showSelection();
			if(body != null && !body.isDisposed()) {
				body.redraw();
				fireSelectionEvent(false);
			}
		}
	}

	private void firePaintedItemEvent(AbstractItem item, boolean isPainted) {
		if(paintedItemListeners != null) {
			Event event = new Event();
			event.detail = isPainted ? 1 : -1;
			event.item = item;
			Listener[] la = (Listener[]) paintedItemListeners.toArray(new Listener[paintedItemListeners.size()]);
			for(int i = 0; i < la.length; i++) {
				la[i].handleEvent(event);
			}
		}
	}

	protected void fireSelectionEvent(boolean defaultSelection) {
		Event event = new Event();
		event.type = defaultSelection ? SWT.DefaultSelection : SWT.Selection;
		if(selection.size() == 1) event.item = (AbstractItem) selection.get(0);
		notifyListeners(event.type, event);
	}

	public Composite getBody() {
		return body;
	}

	public Rectangle getBodyClientArea() {
		return body.getClientArea();
	}

	public Point getBodySize() {
		return body.getSize();
	}

	public Point getScSize() {
		return sc.getSize();
	}

	/**
	 * Get the index of the Bottom Item, as defined by the implementation class
	 * @return the index
	 */
	protected abstract int getLastPaintedIndex();

	/**
	 * Get the Bottom Item, as defined by the implementation class
	 * @return the index
	 */
	protected abstract AbstractItem getLastPaintedItem();

	public SColors getColors() {
		return colors;
	}

	public AbstractColumn getColumn(int index) {
		return (internalTable != null) ? (AbstractColumn) internalTable.getColumn(index) : null;
	}

	public int getColumnCount() {
		return (internalTable != null) ? internalTable.getColumnCount() : 1;
	}

	public int[] getColumnOrder() {
		return (internalTable != null) ? internalTable.getColumnOrder() : new int[] { 0 };
	}

	public AbstractColumn[] getColumns() {
		if(internalTable != null) {
			Object[] nca = internalTable.getColumns();
			AbstractColumn[] cca = new AbstractColumn[nca.length];
			for(int i = 0; i < cca.length; i++) {
				cca[i] = (AbstractColumn) nca[i];
			}
			return cca;
		}
		return new AbstractColumn[0];
	}

	public int[] getColumnWidths() {
		return (layout != null) ? layout.columnWidths : new int[0];
	}

	public AbstractItem getDirtyItem() {
		return dirtyItem;
	}

	/**
	 * The Empty Message is the text message that will be displayed when there are no Items to be displayed (the CTable is empty).
	 * @return a String representing the Empty Message.  Guaranteed to NOT be null.
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
		return (internalTable != null) ? internalTable.getHeaderVisible() : false;
	}

	Table getInternalTable() {
		if(internalTable == null) {
			internalTable = new Table(this, SWT.NONE);
			internalTable.setLinesVisible(linesVisible);
		}
		return internalTable;
	}

//	public AbstractItem getItem(int index) {
//		AbstractItem[] ia = getItems();
//		if((index >= 0) && (index < ia.length)) {
//			return ia[index];
//		}
//		return null;
//	}

	public AbstractItem getItem(Point pt) {
		// must iterate in reverse drawing order in case items overlap each other
		for(ListIterator i = paintedItems.listIterator(paintedItems.size()); i.hasPrevious(); ) {
			AbstractItem item = (AbstractItem) i.previous();
			if(item.contains(pt)) return item;
		}
		return null;
	}

	public int getItemCount() {
		return visibleItems.size();
	}

	/**
	 * not yet implemented
	 * <p>post a feature request if you need it enabled</p>
 	 * TODO: items may (will!) have different heights...
	 */
	public int getItemHeight() {
		return 0;
	}

//	public int getItemIndex(CContainerItem item) {
//		return Arrays.asList(getItems()).indexOf(item);
//	}
//
//	public int getItemIndex(Point pt) {
//		CContainerItem item = getItem(pt);
//		return getItemIndex(item);
//	}

//	public AbstractItem[] getItems() {
//		updateItemLists();
//		return (orderedItems.isEmpty()) ? new AbstractItem[0] : 
//			(AbstractItem[]) orderedItems.toArray(new AbstractItem[orderedItems.size()]);
//	}

	/**
	 * Get a list of all items associated with the given item.
	 * <p>Actual meaning is up to the implementation class</p>
	 * @param item
	 * @return
	 */
	protected List getItems(AbstractItem item) {
		return Collections.EMPTY_LIST;
	}

	/**
	 * Get a list of items within, or touching, the given rectangle.
	 * @param rect
	 * @return
	 */
	protected AbstractItem[] getItems(Rectangle rect) {
		List items = new ArrayList();
		for(Iterator i = visibleItems.iterator(); i.hasNext(); ) {
			AbstractItem item = (AbstractItem) i.next();
			Rectangle[] ra = item.getCellBounds();
			for(int j = 0; j < ra.length; j++) {
				if(ra[j].intersects(rect)) {
					items.add(item);
					break;
				}
			}
		}
		return (items.isEmpty()) ? new AbstractItem[0] :
			(AbstractItem[]) items.toArray(new AbstractItem[items.size()]);
	}

	public boolean getLastLineVisible() {
		return lastLine;
	}

	public boolean getLinesVisible() {
		return linesVisible;
	}

	public Point getOrigin() {
		return sc.getOrigin();
	}

	public AbstractItem[] getRemovedItems() {
		return removedItems;
	}

	public AbstractItem[] getSelection() {
		return selection.isEmpty() ? 
				new AbstractItem[0] : 
					(AbstractItem[]) selection.toArray(new AbstractItem[selection.size()]);
	}

	public int getSelectionCount() {
		return selection.size();
	}

	public int getSelectionIndex() {
		int[] ixs = getSelectionIndices();
		return (ixs.length > 0) ? ixs[0] : -1;
	}

	public int[] getSelectionIndices() {
		// TODO this is the trade-off - is it a problem?  I think we use getSelection more often...
		int[] ixs = new int[selection.size()];
		for(int i = 0; i < ixs.length; i++) {
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

	protected abstract int getFirstPaintedIndex();

	protected abstract AbstractItem getFirstPaintedItem();

	public AbstractItem[] getVisibleItems() {
		updateItemLists();
		return (visibleItems.isEmpty()) ? new AbstractItem[0] : (AbstractItem[]) visibleItems.toArray(new AbstractItem[visibleItems.size()]);
	}

	private Point mmPoint = null;	// mouse move point
	private Point mdPoint = null;	// mouse down point
	private Point muPoint = null;	// mouse up point
	private Point mmDelta = null;	// mouse move delta
	private boolean marquee = false;

	protected boolean handleMouseEvents(AbstractItem item, Event event) {
		if(SWT.MouseUp == event.type &&
				item != null && item.isTogglePoint(muPoint)) {
			boolean open = !item.isOpen(muPoint);
			setDirtyFlag(DIRTY_VISIBLE);
			setOperation(open ? OP_CELL_EXPAND : OP_CELL_COLLAPSE);
			item.removeListener(open ? SWT.Expand : SWT.Collapse, this);
			item.setOpen(muPoint, open);
			item.addListener(open ? SWT.Expand : SWT.Collapse, this);
			layout(item);
		}
		return true;
	}
	
	private void handleMousePosition(AbstractItem item, Event event) {
		switch (event.type) {
		case SWT.MouseDoubleClick:
			if(!selectOnToggle) {
				if(item != null) {
					if(!selectOnToggle && item.isTogglePoint(new Point(event.x, event.y))) break;
				}
			}
			fireSelectionEvent(true);
			break;
		case SWT.MouseDown:
			mmPoint = null;
			muPoint = null;
			mmDelta = null;
			mdPoint = new Point(event.x, event.y);
			if(event.widget != body) {
				if(event.widget instanceof Control) {
					mdPoint = getDisplay().map((Control) event.widget, body, mdPoint);
				} else {
					break;
				}
			}
			break;
		case SWT.MouseMove:
			if(mmPoint == null) {
				mmDelta = new Point(0,0);
			} else {
				mmDelta.x = event.x - mmPoint.x;
				mmDelta.y = event.y - mmPoint.y;
			}
			mmPoint = new Point(event.x, event.y);
			break;
		case SWT.MouseUp:
			mmPoint = null;
			mdPoint = null;
			mmDelta = null;
			muPoint = new Point(event.x, event.y);
			break;
		default:
			break;
		}
	}
	
	protected void handleMouseSelection(AbstractItem item, Event event) {
		switch (event.type) {
		case SWT.MouseDoubleClick:
			if(!selectOnToggle) {
				if(item != null) {
					if(!selectOnToggle && item.isTogglePoint(new Point(event.x, event.y))) break;
				}
			}
			fireSelectionEvent(true);
			break;
		case SWT.MouseDown:
			if(!hasFocus) setFocus();
			if(event.widget == body) {
				item = getItem(mdPoint);
			} else if(items.contains(event.item)) {
				item = (AbstractItem) event.item;
			}
			if(item == null) {
				if((event.stateMask & (SWT.CTRL | SWT.SHIFT)) == 0) {
					if(mode == MODE_MARQUEE) holdSelection = true;
					body.setCursor(getDisplay().getSystemCursor(SWT.CURSOR_CROSS));
					clearSelection();
					marquee = true;
				}
				break;
			}
			if(mode == MODE_MARQUEE) holdSelection = selection.contains(item);
			marquee = false;
			if(!body.isFocusControl()) {
				setFocus();
			}
			switch (event.button) {
			// TODO - popup menu: not just for mouse down events!
			case 3:
				Menu menu = getMenu();
				if((menu != null) && ((menu.getStyle() & SWT.POP_UP) != 0)) {
					menu.setVisible(true);
				}
			case 1:
				if(holdSelection) break;
				if(!selectOnToggle && item.isTogglePoint(mdPoint)) break;
				if((event.stateMask & SWT.SHIFT) != 0) {
					if(shiftSel == null) {
						if(selection.isEmpty()) selection.add(item);
						shiftSel = (AbstractItem) selection.get(selection.size() - 1);
					}
					int start = visibleItems.indexOf(shiftSel);
					int end = visibleItems.indexOf(item);
					setSelection(start, end);
				} else if((event.stateMask & SWT.CONTROL) != 0) {
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
			if(mode == MODE_MARQUEE && marquee) {
				int x = Math.min(mdPoint.x, mmPoint.x);
				int y = Math.min(mdPoint.y, mmPoint.y);
				int w = Math.abs(mdPoint.x - mmPoint.x);
				int h = Math.abs(mdPoint.y - mmPoint.y);
				Rectangle r = new Rectangle(x, y, w, h);
				setSelection(getItems(r));
			}
			body.redraw();
			break;
		case SWT.MouseUp:
			marquee = false;
			body.setCursor(getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
			body.redraw();
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
			if(event.widget == body) {
				item = getItem(new Point(event.x, event.y));
			} else if(items.contains(event.item)) {
				item = (AbstractItem) event.item;
			}
			handleMousePosition(item, event);
			if(handleMouseEvents(item, event)) {
				handleMouseSelection(item, event);
				int result = 0;
				Set s = new HashSet(selection);
				s.addAll(paintedItems);
				for(Iterator i = s.iterator(); i.hasNext(); ) {
					item = (AbstractItem) i.next();
					result |= item.handleMouseEvent(event, !activeItems.isEmpty());
					if((item.isCellState(AbstractCell.CELL_MOVING | AbstractCell.CELL_RESIZING))) {
						if(!activeItems.contains(item)) {
							activeItems.add(item);
						}
					} else {
						activeItems.remove(item);
					}
					if((result & AbstractCell.RESULT_CONSUME) != 0) break;
				}
				if((result & AbstractCell.RESULT_LAYOUT) != 0) {
					layout();
				}
				if((result & AbstractCell.RESULT_REDRAW) != 0) {
					body.redraw();
				}
			}
			break;
		case SWT.Traverse:
			handleTraverse(event);
			break;
		}
	}

	private void handleFocus(int type) {
		if (isDisposed ()) return;
		switch (type) {
		case SWT.FocusIn: {
			if(hasFocus) return;
			hasFocus = true;
			updateFocus();
			Display display = getDisplay ();
			display.removeFilter (SWT.FocusIn, filter);
			display.addFilter (SWT.FocusIn, filter);
			Event e = new Event ();
			notifyListeners (SWT.FocusIn, e);
			break;
		}
		case SWT.FocusOut: {
			if(!hasFocus) return;
			Control focusControl = getDisplay ().getFocusControl ();
			if(focusControl == body) return;
			for(Iterator i = visibleItems.iterator(); i.hasNext(); ) {
				if(((AbstractItem) i.next()).contains(focusControl)) return;
			}
			hasFocus = false;
			updateFocus();
			Display display = getDisplay ();
			display.removeFilter (SWT.FocusIn, filter);
			Event e = new Event ();
			notifyListeners (SWT.FocusOut, e);
		}
		}
	}

	private void handleTraverse(Event event) {
		switch (event.detail) {
		case SWT.TRAVERSE_RETURN:
			if(event.data instanceof AbstractCell) {
				fireSelectionEvent(true);
			}
			break;
		case SWT.TRAVERSE_ARROW_NEXT:
			int[] ia = getSelectionIndices();
			if(ia.length > 0) {
				setSelection(ia[ia.length-1] + 1);
			}
			break;
		case SWT.TRAVERSE_ARROW_PREVIOUS:
			ia = getSelectionIndices();
			if(ia.length > 0) {
				setSelection(ia[ia.length-1] - 1);
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

	public boolean isDirty(int opcode) {
		return ((dirtyFlags & opcode) != 0);
	}

	public boolean isEmpty() {
		return visibleItems.isEmpty();
	}

	public boolean isOperation(int opcode) {
		return operation == opcode;
	}

//	public boolean isSelected(int index) {
//		return getItem(index).isSelected();
//	}

	public void layout(AbstractItem item) {
		setDirtyItem(item);
		opLayout();
	}

	public void layout(AbstractItem item, int opcode, boolean redraw) {
		setDirtyItem(item);
		setOperation(opcode);
		opLayout();
		if(redraw) body.redraw();
	}

	public void move(AbstractItem item, int newIndex) {
		if(newIndex >= 0 && newIndex < visibleItems.size() && visibleItems.contains(item)) {
			int oldIndex = visibleItems.indexOf(item);
			if(visibleItems.remove(item)) {
				visibleItems.add(newIndex, item);
				if(newIndex > oldIndex) {
					setDirtyItem((AbstractItem) visibleItems.get(oldIndex));
				} else {
					setDirtyItem(item);
				}
			}
		}
	}

	public void opLayout() {
		updateItemLists();
		if(!isDisposed()) ((AbstractLayout) getLayout()).opLayout();
	}

	protected void paintBackground(GC gc, Rectangle ebounds) {
	}

	protected void paintColumns(GC gc, Rectangle ebounds) {
		AbstractColumn[] columns = getColumns();
		for(int i = 0; i < columns.length; i++) {
			columns[i].paint(gc, ebounds);
		}
	}

	protected void paintItemBackgrounds(GC gc, Rectangle ebounds) {
	}

	protected void paintSelectionIndicators(GC gc, Rectangle ebounds) {
	}

	protected void paintGridLines(GC gc, Rectangle ebounds) {
		Rectangle r = getBodyClientArea();

		if(linesVisible && (!nativeGrid || getGridLineWidth() > 0)) {
			gc.setForeground(getColors().getGrid());

			int y = -getOrigin().y;

			if(win32 && nativeGrid) {
				if(visibleItems.isEmpty()) {
					if(emptyMessage.length() > 0) {
						Point tSize = gc.textExtent(emptyMessage);
						rowHeight = tSize.y+2;
					}
				} else {
					int gridline = getGridLineWidth();
					for(Iterator i = visibleItems.iterator(); i.hasNext(); ) {
						gc.drawLine(r.x, y, r.x+r.width, y);
						AbstractItem item = (AbstractItem) i.next();
						y += item.getBounds().height + gridline;
					}
					rowHeight = ((AbstractItem) visibleItems.get(
							visibleItems.size() - 1)).fixedTitleHeight;
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

	protected void paintItems(GC gc, Rectangle ebounds) {
		if(visibleItems.isEmpty()) {
			if(emptyMessage.length() > 0) {
				Point bSize = getSize();
				Point tSize = gc.textExtent(emptyMessage);
				gc.setForeground(colors.getItemForegroundNormal());
				gc.drawText(emptyMessage, (bSize.x-tSize.x)/2-ebounds.x, 4-ebounds.y);
			}
		} else {
			for(Iterator i = paintedItems.iterator(); i.hasNext(); ) {
				((AbstractItem) i.next()).paint(gc, ebounds);
			}
		}
	}

	public boolean drawViewportNorth = false;
	public boolean drawViewportEast = false;
	public boolean drawViewportSouth = false;
	public boolean drawViewportWest = false;

	public boolean paintGridAsBackground = false;
	protected void paintBody(Event e) {
		updateItemLists();

		Rectangle ebounds = e.getBounds();
		Image image = new Image(e.display, ebounds);
		GC gc = new GC(image);
//		gc.setBackground(colors.getTableBackground());
//		gc.fillRectangle(ebounds);

		paintBackground(gc, ebounds);
		if(paintGridAsBackground) paintGridLines(gc, ebounds);
		paintItemBackgrounds(gc, ebounds);
		paintSelectionIndicators(gc, ebounds);
		paintColumns(gc, ebounds);
		paintItems(gc, ebounds);
		if(!paintGridAsBackground) paintGridLines(gc, ebounds);
		paintViewport(gc, ebounds);
		paintTracker(gc, ebounds);
		paintFocus(gc,ebounds);

		e.gc.drawImage(image, ebounds.x, ebounds.y);
		gc.dispose();
		image.dispose();
	}

	protected void paintViewport(GC gc, Rectangle ebounds) {
		if(drawViewportNorth || drawViewportEast || drawViewportSouth || drawViewportWest) {
			gc.setAlpha(255);
			gc.setForeground(colors.getBorder());
			Rectangle r = sc.getClientArea();
			if(drawViewportNorth) gc.drawLine(r.x, r.y, r.x+r.width, r.y);
			if(drawViewportEast)  gc.drawLine(r.x+r.width-1, r.y, r.x+r.width-1, r.y + r.height);
			if(drawViewportSouth) gc.drawLine(r.x, r.y+r.height-1, r.x + r.width, r.y+r.height-1);
			if(drawViewportWest)  gc.drawLine(r.x, r.y, r.x, r.y + r.height);
		}
	}

	protected void paintTracker(GC gc, Rectangle ebounds) {
		if(marquee && mdPoint != null && mmPoint != null) {
			gc.setAlpha(75);
			gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_GRAY));
			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_GRAY));
			int x = (mmPoint.x > mdPoint.x ? mdPoint.x : mmPoint.x)-ebounds.x;
			int y = (mmPoint.y > mdPoint.y ? mdPoint.y : mmPoint.y)-ebounds.y;
			int w = Math.abs(mmPoint.x - mdPoint.x);
			int h = Math.abs(mmPoint.y - mdPoint.y);
			gc.fillRectangle(x, y, w, h);
			gc.setAlpha(255);
			gc.setLineStyle(SWT.LINE_DASHDOTDOT);
			gc.drawRectangle(x, y, w, h);
		}
	}

	protected void paintFocus(GC gc, Rectangle ebounds) {
		if(hasFocus) {
			if(win32 || (gtk && !paintedItems.isEmpty())) return;

			gc.setAlpha(255);
			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
			gc.setLineDash(new int[] { 1, 1 } );
			Rectangle r = sc.getClientArea();
			r.x += (1 - ebounds.x);
			r.y += 1;
			r.width -= 3;
			r.height -= 3;
			gc.drawRectangle(r);
		}
	}

	public void redraw() {
		body.redraw();
		super.redraw();
	}

	public void redraw(AbstractItem item) {
		// TODO: paint / redraw individual item
//		Rectangle r = item.getBounds();
//		body.redraw(r.x-1, r.y-1, r.width+2, r.height+2, true);
		body.redraw();
	}

	public void remove(AbstractItem[] items) {
		List l = new ArrayList();
		for(int i = 0; i < items.length; i++) {
			if(this.items.contains(items[i])) {
				l.add(items[i]);
				l.addAll(getItems(items[i]));
			}
		}
		if(!l.isEmpty()) {
			this.items.removeAll(l);
			boolean selChange = false;
			for(Iterator i = l.iterator(); i.hasNext(); ) {
				AbstractItem item = (AbstractItem) i.next();
				if(!selChange && selection.contains(item)) selChange = true;
				if(!item.isDisposed()) item.dispose();
			}
			selection.removeAll(l);

			setRemovedItems(l.isEmpty() ? new AbstractItem[0] :
				(AbstractItem[]) l.toArray(new AbstractItem[l.size()]));

			setDirtyFlag(DIRTY_ORDERED);
			setOperation(OP_REMOVE);
			updateItemLists();
			opLayout();

			if(selChange) fireSelectionEvent(false);
		}
	}

	public void remove(int index) {
		if((index >= 0) && (index < visibleItems.size())) {
			((AbstractItem) visibleItems.get(index)).dispose(); // calls remove(ContainerItem[]) which sets dirtyStructure = true;
		}
	}

	public void remove(int start, int end) {
		if((start >= 0) && (start < visibleItems.size()) && (end >= 0) && (end < visibleItems.size())) {
			int i1 = (start < end) ? start : end;
			int i2 = (start < end) ? end : start;
			AbstractItem[] items = new AbstractItem[i2-i1+1];
			for(int i = 0; i1 <= i2; i++, i1++) {
				items[i] = (AbstractItem) visibleItems.get(i1);
			}
			remove(items);
		}
	}
	public void remove(int[] indices) {
		AbstractItem[] items = new AbstractItem[indices.length];
		for(int i = 0; i < indices.length; i++) {
			items[i] = (AbstractItem) visibleItems.get(indices[i]);
		}
		remove(items);
	}

	public void removeAll() {
		remove(0, visibleItems.size()-1);
	}

	protected void removeItem(AbstractItem item) {
		if(items.contains(item)) {
			remove(new AbstractItem[] { item } );
		}
	}

	public void removeListener(int eventType, Listener listener) {
		switch(eventType) {
		case SWT.MouseDoubleClick:
		case SWT.MouseDown:
		case SWT.MouseEnter:
		case SWT.MouseExit:
		case SWT.MouseHover:
		case SWT.MouseMove:
		case SWT.MouseUp:
		case SWT.MouseWheel:
		case SWT.KeyDown:
		case SWT.KeyUp:
			body.removeListener(eventType, listener);
			break;
		default:
			super.removeListener(eventType, listener);
			break;
		}
	}

	public void removeMouseListener(MouseListener listener) {
		body.removeMouseListener(listener);
	}
	
	public void removeMouseMoveListener(MouseMoveListener listener) {
		body.removeMouseMoveListener(listener);
	}
	
	public void removeMouseTrackListener(MouseTrackListener listener) {
		body.removeMouseTrackListener(listener);
	}
	
	public void removePaintedItemListener(Listener listener) {
		if(paintedItemListeners != null) {
			paintedItemListeners.remove(listener);
		}
	}

	public void removeSelectionListener(SelectionListener listener) {
		checkWidget ();
		if(listener != null) {
			removeListener(SWT.Selection, listener);
			removeListener(SWT.DefaultSelection, listener);
		}
	}

	public void scrollTo(Point pt) {
		sc.setOrigin(pt);
	}

	public void scrollToX(int x) {
		sc.setOrigin(x, sc.getOrigin().y);
	}

	public void scrollToY(int y) {
		sc.setOrigin(sc.getOrigin().x, y);
	}

	public void select(int index) {
		select(index, true);
	}

	private void select(int index, boolean finish) {
		if((index >= 0) && (index < visibleItems.size())) {
			Object o = visibleItems.get(index);
			if(!selection.contains(o)) {
				selection.add(o);
			}
		}
		if(finish) finishSelection();
	}

	public void select(int start, int end) {
		if((start >= 0) && (start < visibleItems.size()) && (end >= 0) && (end < visibleItems.size())) {
			int i1 = (start < end) ? start : end;
			int i2 = (start < end) ? end : start;
			for(int i = i1; i <= i2; i++) {
				select(i, false);
			}
			finishSelection();
		}
	}

	public void select(int[] indices) {
		for(int i = 0; i < indices.length; i++) {
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

	public void clearDirtyFlags() {
		dirtyFlags = 0;
	}

	public void clearDirtyItem() {
		dirtyItem = null;
	}

	public void setDirtyFlag(int flag) {
		switch(flag) {
		case DIRTY_ORDERED:
			dirtyFlags |= DIRTY_ORDERED;
		case DIRTY_VISIBLE:
			dirtyFlags |= DIRTY_VISIBLE;
		case DIRTY_PAINTED:
			dirtyFlags |= DIRTY_PAINTED;
			topIndex = -2; // topIndex will never be set to this, thus forcing a refresh
			botIndex = -2;
			break;
		}
	}

	public void setDirtyItem(AbstractItem item) {
		dirtyItem = item;
	}

	/**
	 * Sets the message that will be displayed when their are no Items to be displayed
	 * (the Container is empty).  The message will span all rows and be aligned to the
	 * Top and Center of the CTable.  Setting <b>string</b> to null will disable the 
	 * Empty Message Display feature.
	 * @param string the message to be displayed
	 */
	public void setEmptyMessage(String string) {
		emptyMessage = (string != null) ? string : "";
	}

	public boolean setFocus() {
		return body.forceFocus();
	}

	/**
	 * currently disabled: use the CTableColors class instead
	 * <p>post a feature request if you need it enabled</p>
	 */
	public void setForeground(Color color) {
	}

	public void setHeaderVisible(boolean show) {
		getInternalTable().setHeaderVisible(show);
	}

	/**
	 * Sets the layout which is associated with the receiver to be
	 * the argument which may be null.
	 * If the argument is non-null then it must be a subclass of
	 * CContainerLayout or this method will simply return.
	 *
	 * @param layout the receiver's new layout or null
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public void setLayout(Layout layout) {
		if(layout instanceof AbstractLayout) {
			this.layout = (AbstractLayout) layout;
			super.setLayout(layout);
		}
	}
	
	public void setLinesVisible(boolean visible) {
		setLinesVisible(visible, true);
	}

	/**
	 * If lastLine == false, then lines are only drawn in between items (if, of course, visible == true)
	 * @param visible
	 * @param lastLine whether or not the last line is set visible
	 * @see org.aspencloud.widgets.ccontainer#setLinesVisible(boolean)
	 */
	public void setLinesVisible(boolean visible, boolean lastLine) {
		if(linesVisible != visible) {
			linesVisible = visible;
			if(internalTable != null) internalTable.setLinesVisible(linesVisible);
			body.redraw();
		}
		this.lastLine = lastLine;
	}

	public void setHorizontalLinesVisible(boolean visible) {
		hLines = visible;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public void setOperation(int opcode) {
		operation = opcode;
	}

	Class[] cellClasses = null;
	public void setCellClass(Class clazz) {
		cellClasses = new Class[] { clazz };
	}
	public void setCellClasses(Class[] classes) {
		cellClasses = classes;
	}
	
	public void setRedraw(boolean redraw) {
		super.setRedraw(redraw);
		if(internalTable != null) internalTable.setRedraw(redraw);
		sc.setRedraw(redraw);
		body.setRedraw(redraw);
	}

	private void setRemovedItems(AbstractItem[] items) {
		removedItems = items;
	}

	/**
	 * Enables items in this Container to be selected
	 * @param selectable
	 */
	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
		for(Iterator i = items.iterator(); i.hasNext(); ) {
			((AbstractItem) i.next()).setEnabled(selectable);
		}
	}

	public void setSelection(AbstractItem item) {
		if(item == null) {
			selection = new ArrayList();
		} else {
			selection = new ArrayList(Collections.singleton(item));
			shiftSel = item;
		}
		finishSelection();
	}

	public void setSelection(AbstractItem[] items) {
		if(items == null || items.length == 0) {
			selection = new ArrayList();
		} else {
			selection = new ArrayList(Arrays.asList(items));
			shiftSel = items[0];
		}
		finishSelection();
	}

	public void setSelection(int index) {
		if(index >= 0 && index < visibleItems.size()) {
			AbstractItem item = (AbstractItem) visibleItems.get(index);
			selection = new ArrayList(Collections.singleton(item));
			shiftSel = item;
			finishSelection();
		}
	}

	public void setSelection(int start, int end) {
		if(start >= 0 && start < visibleItems.size() && end >= 0 && end < visibleItems.size()) {
			selection = new ArrayList();
			int i1 = (start < end) ? start : end;
			int i2 = (start < end) ? end : start;
			for(int i = i1; i <= i2; i++) {
				selection.add(visibleItems.get(i));
			}
			shiftSel = (AbstractItem) visibleItems.get(start);
			finishSelection();
		}
	}

	public void setSelection(int[] ixs) {
		selection = new ArrayList();
		for(int i = 0; i < ixs.length; i++) {
			selection.add(visibleItems.get(i));
		}
		shiftSel = (AbstractItem) visibleItems.get(ixs[0]);
		finishSelection();
	}

	/**
	 * If the the user clicks on the toggle of an item (treeCell or not) the corresponding item will
	 * become selected if, and only if, selectOnToggle is true
	 * @param select the new state of selectOnToggle
	 */
	public void setSelectOnToggle(boolean select) {
		selectOnToggle = select;
	}

	/**
	 * not yet implemented
	 * <p>post a feature request if you need it enabled</p>
	 */
	public abstract void setTopIndex(int index);

	/**
	 * not yet implemented
	 * <p>post a feature request if you need it enabled</p>
	 */
	public abstract void setTopItem(AbstractItem item);

	public void setVerticalLinesVisible(boolean visible) {
		vLines = visible;
	}

	public void showItem(AbstractItem item) {
		Rectangle r = item.getBounds();
		Rectangle c = sc.getClientArea();
		if(r.height > c.height || !c.contains(new Point(0,r.y-sc.getOrigin().y))) {
			sc.setOrigin(new Point(0,r.y));
		} else if(!c.contains(new Point(0,r.y+r.height-1-sc.getOrigin().y))) {
			sc.setOrigin(new Point(0,r.y-c.height+r.height));
		}
	}

	/**
	 * @see org.eclipse.swt.widgets.Table#showItem(java.lang.Object)
	 * @param index the index of the item to show
	 */
	public void showItem(int index) {
		if((index >= 0) && (index < visibleItems.size())) {
			showItem((AbstractItem) visibleItems.get(index));
		}
	}

	public void showSelection() {
		if(!selection.isEmpty()) {
			int topIndex = visibleItems.indexOf(selection.get(0));
			for(Iterator i = selection.iterator(); i.hasNext(); ) {
				topIndex = Math.min(topIndex, visibleItems.indexOf((AbstractItem) i.next()));
			}
			showItem((AbstractItem) visibleItems.get(topIndex));
		}
	}

	/**
	 * toggle the selection state of the item
	 * @param item the item whose selection state is to be toggled
	 * @return true if the item's selection state was toggled on, false otherwise
	 */
	public boolean toggleSelection(AbstractItem item) {
		boolean result = false;
		if(selection.contains(item)) {
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
		if(!selection.isEmpty()) {
			for(Iterator i = selection.iterator(); i.hasNext(); ) {
				((AbstractItem) i.next()).updateColors();
			}
			body.redraw();
		} else if(gtk){
			body.redraw(); // focus has changed
		}
	}

	protected void updateItemLists() {
		if(isDirty(DIRTY_ORDERED)) {
			updateOrderedItems();
			dirtyFlags &= ~DIRTY_ORDERED;
		}
		if(isDirty(DIRTY_VISIBLE)) {
			updateVisibleItems();
			dirtyFlags &= ~DIRTY_VISIBLE;
		}
		updatePaintedItems();
	}

	protected void updateOrderedItems() {
		orderedItems = new ArrayList(items);
	}

	protected int topIndex;
	protected int botIndex;
	protected void updatePaintedItems() {
		int top = visibleItems.isEmpty() ? -1 : getFirstPaintedIndex();
		int bot = (top == -1) ? -1 : getLastPaintedIndex();
		if(top != topIndex || bot != botIndex) {
			topIndex = top;
			botIndex = bot;

			List oldPainted = (paintedItems == null) ? new ArrayList() : new ArrayList(paintedItems);
			paintedItems = (topIndex == -1 || topIndex >= visibleItems.size() || botIndex < topIndex) ?
					new ArrayList() :
						new ArrayList(visibleItems.subList(topIndex, botIndex + 1));
					List newPainted = new ArrayList(paintedItems);

					// remove common elements from both lists
					for(Iterator i = oldPainted.iterator(); i.hasNext(); ) {
						Object o = i.next();
						if(newPainted.contains(o)) {
							newPainted.remove(o);
							i.remove();
						}
					}

					for(Iterator i = oldPainted.iterator(); i.hasNext(); ) {
						firePaintedItemEvent((AbstractItem) i.next(), false);
					}
					for(Iterator i = newPainted.iterator(); i.hasNext(); ) {
						firePaintedItemEvent((AbstractItem) i.next(), true);
					}
		}
	}

	/**
	 * Sets the visibility of all items
	 */
	protected void updateVisibleItems() {
		if(isOperation(OP_ADD)) {
			visibleItems.add(orderedItems.indexOf(dirtyItem), dirtyItem);
		} else if(isOperation(OP_REMOVE)) {
			visibleItems.removeAll(Arrays.asList(removedItems));
		} else {
			visibleItems = new ArrayList(orderedItems);
			for(Iterator i = visibleItems.iterator(); i.hasNext(); ) {
				AbstractItem item = (AbstractItem) i.next();
				if(!item.getVisible()) {
					i.remove();
				}
			}
		}
	}
}
