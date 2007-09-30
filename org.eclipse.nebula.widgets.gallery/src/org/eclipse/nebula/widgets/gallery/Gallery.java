/*******************************************************************************
 * Copyright (c) 2006-2007 Nicolas Richeton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors :
 *    Nicolas Richeton (nicolas.richeton@gmail.com) - initial API and implementation
 *    Tom Schindl      (tom.schindl@bestsolution.at) - fix for bug 174933
 *******************************************************************************/

package org.eclipse.nebula.widgets.gallery;

import java.lang.reflect.Array;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.TypedListener;

/**
 * <p>
 * SWT Widget that displays a picture gallery<br/> see
 * http://www.eclipse.org/nebula/widgets/gallery/gallery.php<br/> This widget
 * requires jdk-1.4+
 * </p>
 * <p>
 * Style <code>VIRTUAL</code> is used to create a <code>Gallery</code> whose
 * <code>GalleryItem</code>s are to be populated by the client on an
 * on-demand basis instead of up-front. This can provide significant performance
 * improvements for galleries that are very large or for which
 * <code>GalleryItem</code> population is expensive (for example, retrieving
 * values from an external source).
 * </p>
 * <p>
 * Here is an example of using a <code>Gallery</code> with style
 * <code>VIRTUAL</code>: <code><pre>
 * final Gallery gallery = new Gallery(parent, SWT.VIRTUAL | V_SCROLL | SWT.BORDER);
 * gallery.setGroupRenderer(new DefaultGalleryGroupRenderer());
 * gallery.setItemRenderer(new DefaultGalleryItemRenderer());
 * gallery.setItemCount(1000000);
 * gallery.addListener(SWT.SetData, new Listener() {
 * 	public void handleEvent(Event event) {
 * 		GalleryItem item = (GalleryItem) event.item;
 * 		int index = gallery.indexOf(item);
 * 		item.setText(&quot;Item &quot; + index);
 * 		System.out.println(item.getText());
 * 	}
 * });
 * </pre></code>
 * </p>
 * <p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SINGLE, MULTI, VIRTUAL, V_SCROLL, H_SCROLL</dd>
 * </dl>
 * </p>
 * <p>
 * Note: Only one of the styles SINGLE and MULTI may be specified.
 * </p>
 * <p>
 * Note: Only one of the styles V_SCROLL and H_SCROLL may be specified.
 * </p>
 * <p>
 * <dl>
 * <dt><b>Events:</b></dt>
 * <dd>Selection, DefaultSelection, SetData, PaintItem</dd>
 * </dl>
 * </p>
 * <p>
 * NOTE: THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT. THIS IS A
 * PRE-RELEASE ALPHA VERSION. USERS SHOULD EXPECT API CHANGES IN FUTURE
 * VERSIONS.
 * </p>
 * 
 * @author Nicolas Richeton (nicolas.richeton@gmail.com)
 */
public class Gallery extends Canvas {

	/**
	 * Used to enable debug the Gallery Widget.
	 */
	protected static boolean DEBUG = false;

	GalleryItem[] items = null;

	private GalleryItem[] selection = null;

	private int[] selectionIndices = null;

	/**
	 * Virtual mode flag.
	 */
	boolean virtual = false;

	/**
	 * Scrolling direction flag. True : V_SCROLL, false : H_SCROLL.
	 */
	boolean vertical = true;

	/**
	 * Multi selection flag
	 */
	boolean multi = false;

	int itemCount = 0;

	int interpolation = SWT.HIGH;

	int antialias = SWT.ON;

	// Internals

	private int gHeight = 0;

	private int gWidth = 0;

	int lastIndexOf = 0;

	private GalleryItem lastSingleClick = null;

	private Color backgroundColor;

	/**
	 * Current translation. Can be used by renderer during paint.
	 */
	protected int translate = 0;

	private boolean mouseClickHandled = false;

	AbstractGalleryItemRenderer itemRenderer;

	AbstractGalleryGroupRenderer groupRenderer;

	/**
	 * Return the number of root-level items in the receiver. Does not include
	 * children.
	 * 
	 * @return
	 */
	public int getItemCount() {
		checkWidget();
		if (virtual)
			return itemCount;

		if (items == null)
			return 0;

		return items.length;
	}

	/**
	 * Sets the number of root-level items contained in the receiver. Only work
	 * in VIRTUAL mode.
	 * 
	 * @return
	 */
	public void setItemCount(int count) {
		checkWidget();

		if (DEBUG)
			System.out.println("setCount" + count);

		if (virtual) {
			if (count == 0) {
				// No items
				items = null;
			} else {
				// At least one item, create a new array and copy data from the
				// old one.
				GalleryItem[] newItems = new GalleryItem[count];
				if (items != null) {
					System.arraycopy(items, 0, newItems, 0, Math.min(count, items.length));
				}
				items = newItems;
			}
			this.itemCount = count;

			updateStructuralValues(false);
			this.updateScrollBarsProperties();
			redraw();
		}
	}

	/**
	 * Get current item renderer
	 * 
	 * @return
	 */
	public AbstractGalleryItemRenderer getItemRenderer() {
		checkWidget();
		return itemRenderer;
	}

	/**
	 * Set item receiver. Usually, this does not trigger gallery update. redraw
	 * must be called right after setGroupRenderer to reflect this change.
	 * 
	 * @param itemRenderer
	 */
	public void setItemRenderer(AbstractGalleryItemRenderer itemRenderer) {
		checkWidget();
		this.itemRenderer = itemRenderer;

		if (itemRenderer != null)
			itemRenderer.setGallery(this);
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified
	 * when the receiver's selection changes, by sending it one of the messages
	 * defined in the <code>SelectionListener</code> interface.
	 * <p>
	 * When <code>widgetSelected</code> is called, the item field of the event
	 * object is valid.
	 * </p>
	 * 
	 * @param listener
	 *            the listener which should be notified
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see SelectionListener
	 * @see #removeSelectionListener
	 * @see SelectionEvent
	 */
	public void addSelectionListener(SelectionListener listener) {
		checkWidget();
		if (listener == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		addListener(SWT.Selection, new TypedListener(listener));
	}

	/**
	 * Removes the listener from the collection of listeners who will be
	 * notified when the receiver's selection changes.
	 * 
	 * @param listener
	 *            the listener which should no longer be notified
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see SelectionListener
	 * @see #addSelectionListener(SelectionListener)
	 */
	public void removeSelectionListener(SelectionListener listener) {
		checkWidget();
		removeListener(SWT.Selection, listener);
	}

	/**
	 * Send SWT.PaintItem for one item.
	 * 
	 * @param item
	 * @param index
	 * @param gc
	 * @param x
	 * @param y
	 */
	protected void sendPaintItemEvent(Item item, int index, GC gc, int x, int y, int width, int height) {

		Event e = new Event();
		e.item = item;
		e.type = SWT.PaintItem;
		e.index = index;
		//TODO: Does clipping need to be set ?
		// gc.setClipping(x, y, width, height);
		e.gc = gc;
		e.x = x;
		e.y = y;
		e.width = width;
		e.height = height;
		this.notifyListeners(SWT.PaintItem, e);
	}

	/**
	 * Send a selection event for a gallery item
	 * 
	 * @param item
	 */
	protected void notifySelectionListeners(GalleryItem item, int index) {

		Event e = new Event();
		e.widget = this;
		e.item = item;
		e.data = item.getData();
		// TODO: enable e.index
		// e.index = index;
		try {
			notifyListeners(SWT.Selection, e);
		} catch (RuntimeException e1) {
		}
	}

	/**
	 * Create a Gallery
	 * 
	 * 
	 * @param parent
	 * @param style -
	 *            SWT.VIRTUAL switches in virtual mode. <br/>SWT.V_SCROLL add
	 *            vertical slider and switches to vertical mode.
	 *            <br/>SWT.H_SCROLL add horizontal slider and switches to
	 *            horizontal mode. <br/>if both V_SCROLL and H_SCROLL are
	 *            specified, the gallery is in vertical mode by default. Mode
	 *            can be changed afterward using setVertical<br/> SWT.MULTI
	 *            allows only several items to be selected at the same time.
	 */
	public Gallery(Composite parent, int style) {
		super(parent, style | SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);
		virtual = (style & SWT.VIRTUAL) > 0;
		vertical = (style & SWT.V_SCROLL) > 0;
		multi = (style & SWT.MULTI) > 0;
		backgroundColor = getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND);

		// Dispose renderers on dispose
		this.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if (itemRenderer != null)
					itemRenderer.dispose();

				if (groupRenderer != null)
					groupRenderer.dispose();
			}
		});

		// Add listeners : redraws, mouse and keyboard
		_addResizeListeners();
		_addPaintListeners();
		_addScrollBarsListeners();
		_addMouseListeners();
		_addKeyListeners();

		// Layout
		updateStructuralValues(false);
		updateScrollBarsProperties();
		redraw();
	}

	/**
	 * Add internal paint listeners to this gallery.
	 */
	private void _addPaintListeners() {
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event) {
				onPaint(event.gc);
			}
		});
	}

	/**
	 * Add internal resize listeners to this gallery.
	 */
	private void _addResizeListeners() {
		addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent event) {
				updateStructuralValues(true);
				updateScrollBarsProperties();
				redraw();
			}
		});
	}

	/**
	 * Add internal scrollbars listeners to this gallery.
	 */
	private void _addScrollBarsListeners() {
		// Vertical bar
		ScrollBar verticalBar = getVerticalBar();
		if (verticalBar != null) {
			verticalBar.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent event) {
					if (vertical)
						scrollVertical();
				}
			});
		}

		// Horizontal bar

		ScrollBar horizontalBar = getHorizontalBar();
		if (horizontalBar != null) {
			horizontalBar.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent event) {
					if (!vertical)
						scrollHorizontal();
				}
			});
		}

	}

	private void _addKeyListeners() {
		this.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {

				switch (e.keyCode) {
				case SWT.ARROW_LEFT:
				case SWT.ARROW_RIGHT:
				case SWT.ARROW_UP:
				case SWT.ARROW_DOWN:
					GalleryItem newItem = groupRenderer.getNextItem(lastSingleClick, e.keyCode);

					if (newItem != null) {
						_deselectAll();
						setSelected(newItem, true, true);
						lastSingleClick = newItem;
						_showItem(newItem);
						redraw();
					}

					break;
				}
			}

			public void keyReleased(KeyEvent e) {
			}

		});
	}

	public void showItem(GalleryItem item) {
		this.checkWidget();
		this._showItem(item);
	}

	private void _showItem(GalleryItem item) {
		int y;
		int height;
		Rectangle rect = groupRenderer.getSize(item);
		if (rect == null) {
			return;
		}

		if (vertical) {
			y = rect.y;
			height = rect.height;
		} else {
			y = rect.x;
			height = rect.width;
		}

		if (y < translate) {
			translate = y;
		} else if (translate + this.getClientArea().height < y + height) {
			translate = y + height - this.getClientArea().height;
		}
		this.updateScrollBarsProperties();
		redraw();

	}

	/**
	 * Add internal mouse listeners to this gallery.
	 */
	private void _addMouseListeners() {
		addMouseListener(new MouseListener() {

			public void mouseDoubleClick(MouseEvent e) {
				onMouseDoubleClick(e);
			}

			public void mouseDown(MouseEvent e) {
				onMouseDown(e);
			}

			public void mouseUp(MouseEvent e) {
				onMouseUp(e);
			}

		});
	}

	private void select(int from, int to) {
		for (int i = from; i <= to; i++) {
			GalleryItem item = getItem(i);
			this._addSelection(item);
			item._selectAll();

		}
	}

	private void select(GalleryItem from, GalleryItem to) {
		GalleryItem fromParent = from.getParentItem();
		GalleryItem toParent = to.getParentItem();

		if (fromParent == toParent) {

			if (fromParent == null) {
				int fromIndex = indexOf(from);
				int toIndex = indexOf(to);
				select(fromIndex, toIndex);
			} else {
				int fromIndex = fromParent.indexOf(from);
				int toIndex = toParent.indexOf(to);
				fromParent.select(fromIndex, toIndex);
			}
		} else {
			int fromParentIndex = indexOf(fromParent);
			int toParentIndex = indexOf(toParent);
			int fromIndex = fromParent.indexOf(from);
			int toIndex = toParent.indexOf(to);

			fromParent.select(fromIndex, fromParent.getItemCount() - 1);
			for (int i = fromParentIndex + 1; i < toParentIndex; i++) {
				getItem(i)._selectAll();
			}
			toParent.select(0, toIndex);

		}
		this.notifySelectionListeners(to, indexOf(to));
		redraw();
	}

	private boolean getOrder(GalleryItem before, GalleryItem after) {

		if (before == null || after == null)
			return true;

		GalleryItem newParent = before.getParentItem();
		GalleryItem oldParent = after.getParentItem();

		int beforeParentIndex = indexOf(newParent);
		int afterParentIndex = indexOf(oldParent);

		if (newParent == oldParent) {
			int newParentIndex;
			int oldParentIndex;
			if (newParent == null) {
				newParentIndex = indexOf(before);
				oldParentIndex = indexOf(after);

			} else {
				newParentIndex = newParent.indexOf(before);
				oldParentIndex = newParent.indexOf(after);
			}
			return (newParentIndex < oldParentIndex);
		}

		return beforeParentIndex < afterParentIndex;
	}

	/**
	 * Toggle item selection status
	 * 
	 * @param i
	 * @param selected
	 * @param notifyListeners
	 *            
	 */
	protected void setSelected(GalleryItem item, boolean selected, boolean notifyListeners) {
		if (selected) {
			if (!isSelected(item)) {
				_addSelection(item);

				// Notify listeners if necessary.
				if (notifyListeners)
					notifySelectionListeners(item, indexOf(item));
			}

		} else {
			if (isSelected(item)) {
				_removeSelection(item);
			}
		}
	}

	protected void _addSelection(GalleryItem item) {
		if (this.isSelected(item))
			return;

		// Deselect all items is multi selection is disabled
		if (!multi) {
			_deselectAll();
		}

		if (item.getParentItem() != null) {
			item.getParentItem()._addSelection(item);
		} else {
			if (selectionIndices == null) {
				selectionIndices = new int[1];
			} else {
				int[] oldSelection = selectionIndices;
				selectionIndices = new int[oldSelection.length + 1];
				System.arraycopy(oldSelection, 0, selectionIndices, 0, oldSelection.length);
			}
			selectionIndices[selectionIndices.length - 1] = indexOf(item);

		}

		if (selection == null) {
			selection = new GalleryItem[1];
		} else {
			GalleryItem[] oldSelection = selection;
			selection = new GalleryItem[oldSelection.length + 1];
			System.arraycopy(oldSelection, 0, selection, 0, oldSelection.length);
		}
		selection[selection.length - 1] = item;

	}

	private void _removeSelection(GalleryItem item) {

		if (item.getParentItem() == null)
			selectionIndices = _arrayRemoveItem(selectionIndices, _arrayIndexOf(selectionIndices, _indexOf(item)));
		else
			_removeSelection(item.getParentItem(), item);

		int index = _arrayIndexOf(selection, item);
		if (index == -1)
			return;

		selection = (GalleryItem[]) _arrayRemoveItem(selection, index);

	}

	protected void _removeSelection(GalleryItem parent, GalleryItem item) {
		parent.selectionIndices = _arrayRemoveItem(parent.selectionIndices, _arrayIndexOf(parent.selectionIndices, _indexOf(parent, item)));
	}

	protected boolean isSelected(GalleryItem item) {

		if (item == null)
			return false;

		if (item.getParentItem() != null) {
			return item.getParentItem().isSelected(item);
		} else {
			if (selectionIndices == null)
				return false;

			int index = indexOf(item);
			for (int i = 0; i < selectionIndices.length; i++) {
				if (selectionIndices[i] == index)
					return true;
			}
		}
		return false;
	}

	/**
	 * Deselects the item at the given zero-relative index in the receiver.
	 */
	public void deselectAll() {
		checkWidget();
		_deselectAll();

		redraw();
	}

	protected void _deselectAll() {

		if (DEBUG)
			System.out.println("clear");

		this.selection = null;
		this.selectionIndices = null;

		if (items == null)
			return;
		for (int i = 0; i < items.length; i++) {
			if (items[i] != null)
				items[i]._deselectAll();
		}
	}

	private void onMouseDoubleClick(MouseEvent e) {
		if (DEBUG)
			System.out.println("Mouse Double Click");

		GalleryItem item = getItem(new Point(e.x, e.y));
		if (item != null) {
			// TODO: Handle double click ?
		}
		mouseClickHandled = true;
	}

	private void onMouseUp(MouseEvent e) {
		if (DEBUG)
			System.out.println("onMouseUp");

		if (mouseClickHandled) {
			if (DEBUG) {
				System.out.println("onMouseUp : mouse event already handled");
			}
			return;
		}

		if (e.button == 1) {
			GalleryItem item = getItem(new Point(e.x, e.y));
			if (item == null)
				return;

			if ((e.stateMask & SWT.MOD1) > 0) {
				onMouseHandleLeftMod1(e, item, false, true);
			} else if ((e.stateMask & SWT.SHIFT) > 0) {
				onMouseHandleLeftShift(e, item, false, true);
			} else {
				onMouseHandleLeft(e, item, false, true);
			}
		}
	}

	private void onMouseDown(MouseEvent e) {
		if (DEBUG)
			System.out.println("Mouse down ");

		mouseClickHandled = false;

		if (!_mouseDown(e)) {
			mouseClickHandled = true;
			return;
		}

		GalleryItem item = getItem(new Point(e.x, e.y));

		if (e.button == 1) {

			if (item == null) {
				_deselectAll();
				redraw();
				mouseClickHandled = true;
				lastSingleClick = null;
			} else {
				if ((e.stateMask & SWT.MOD1) > 0) {
					onMouseHandleLeftMod1(e, item, true, false);
				} else if ((e.stateMask & SWT.SHIFT) > 0) {
					onMouseHandleLeftShift(e, item, true, false);
				} else {
					onMouseHandleLeft(e, item, true, false);
				}
			}
		} else if (e.button == 3) {
			onMouseHandleRight(e, item, true, false);
		}
	}

	private void onMouseHandleLeftMod1(MouseEvent e, GalleryItem item, boolean down, boolean up) {
		if (up) {
			// if (lastSingleClick != null) {
			if (item != null) {
				if (DEBUG)
					System.out.println("setSelected : inverse");
				setSelected(item, !isSelected(item), true);
				lastSingleClick = item;
				redraw();
			}
			// }
		}
	}

	private void onMouseHandleLeftShift(MouseEvent e, GalleryItem item, boolean down, boolean up) {
		if (up) {
			if (lastSingleClick != null) {
				_deselectAll();

				if (getOrder(item, lastSingleClick))
					select(item, lastSingleClick);
				else
					select(lastSingleClick, item);
			}
		}
	}

	private void onMouseHandleLeft(MouseEvent e, GalleryItem item, boolean down, boolean up) {
		if (down) {
			if (!isSelected(item)) {
				_deselectAll();

				if (DEBUG)
					System.out.println("setSelected");
				setSelected(item, true, true);

				lastSingleClick = item;
				redraw();
				mouseClickHandled = true;
			}
		} else if (up) {
			if (item == null) {
				_deselectAll();
			} else {
				if (DEBUG)
					System.out.println("setSelected");

				_deselectAll();
				setSelected(item, true, lastSingleClick != item);
				lastSingleClick = item;
			}
			redraw();
		}
	}

	private void onMouseHandleRight(MouseEvent e, GalleryItem item, boolean down, boolean up) {
		if (down) {
			if (DEBUG)
				System.out.println("right clic");

			if (!isSelected(item)) {
				_deselectAll();
				setSelected(item, true, true);
				redraw();
				mouseClickHandled = true;
			}
		}

	}

	private synchronized void onPaint(GC gc) {
		if (DEBUG)
			System.out.println("paint");

		try {
			GC newGC = gc;

			// Linux-GTK Bug 174932
			if (!SWT.getPlatform().equals("gtk")) {
				newGC.setAdvanced(true);
			}

			if (gc.getAdvanced()) {
				newGC.setAntialias(antialias);
				newGC.setInterpolation(interpolation);
			}

			Rectangle clipping = newGC.getClipping();
			gc.setBackground(backgroundColor);
			drawBackground(newGC, clipping.x, clipping.y, clipping.width, clipping.height);

			int[] indexes = getVisibleItems(clipping);

			if (indexes != null && indexes.length > 0) {

				// Call preDraw for optimization
				if (groupRenderer != null)
					groupRenderer.preDraw(newGC);
				if (itemRenderer != null)
					itemRenderer.preDraw(newGC);

				for (int i = indexes.length - 1; i >= 0; i--) {
					if (DEBUG)
						System.out.println("Drawing group " + indexes[i]);

					_drawGroup(newGC, indexes[i]);
				}
			}
		} catch (Exception e) {
			// We can't let onPaint throw an exception because unexpected
			// results may occur in SWT.
			e.printStackTrace();
		}

	}

	private int[] getVisibleItems(Rectangle clipping) {

		if (items == null)
			return null;

		int start = vertical ? (clipping.y + translate) : (clipping.x + translate);

		int end = vertical ? (clipping.y + clipping.height + translate) : (clipping.x + clipping.width + translate);

		ArrayList al = new ArrayList();
		int index = 0;
		GalleryItem item = null;
		while (index < items.length) {
			item = getItem(index);
			if ((vertical ? item.y : item.x) > end)
				break;

			if ((vertical ? (item.y + item.height) : (item.x + item.width)) >= start)
				al.add(new Integer(index));

			index++;
		}

		int[] result = new int[al.size()];

		for (int i = 0; i < al.size(); i++)
			result[i] = ((Integer) al.get(i)).intValue();

		return result;
	}

	public void refresh(int nb) {
		checkWidget();
		if (nb < getItemCount()) {
			// TODO: refresh
		}
	}

	public void redraw(GalleryItem item) {
		checkWidget();
	}

	/**
	 * Draw a group. Used when useGroup is true and for root items.
	 * 
	 * @param gc
	 * @param index
	 */
	private void _drawGroup(GC gc, int index) {
		// Draw group
		GalleryItem item = getItem(index);
		if (item == null)
			return;
		this.groupRenderer.setExpanded(item.isExpanded());

		// Drawing area
		int x = this.vertical ? item.x : item.x - this.translate;
		int y = this.vertical ? item.y - translate : item.y;

		Rectangle clipping = gc.getClipping();
		this.groupRenderer.draw(gc, item, x, y, clipping.x, clipping.y, clipping.width, clipping.height);
	}

	/**
	 * If table is virtual and item at pos i has not been set, call the callback
	 * listener to set its value.
	 * 
	 * @return
	 */
	private void updateItem(GalleryItem parentItem, int i) {

		GalleryItem galleryItem;
		if (parentItem == null) {
			// Parent is the Gallery widget
			galleryItem = items[i];
			if (galleryItem == null && this.virtual) {
				if (DEBUG) {
					System.out.println("Virtual/creating item ");
				}

				galleryItem = new GalleryItem(this, SWT.NONE);
				items[i] = galleryItem;
				setData(galleryItem, i);
			}
		} else {
			// Parent is another GalleryItem
			galleryItem = parentItem.items[i];
			if (galleryItem == null && this.virtual) {
				if (DEBUG) {
					System.out.println("Virtual/creating item ");
				}

				galleryItem = new GalleryItem(parentItem, SWT.NONE);
				parentItem.items[i] = galleryItem;
				setData(galleryItem, i);
			}
		}

	}

	private void setData(GalleryItem galleryItem, int index) {
		Item item = galleryItem;
		Event e = new Event();
		e.item = item;
		e.type = SWT.SetData;
		e.index = index;
		this.notifyListeners(SWT.SetData, e);
	}

	/**
	 * Recalculate structural values using the group renderer<br>
	 * Gallery and item size will be updated.
	 * 
	 * @param keepLocation
	 *            if true, the current scrollbars position ratio is saved and
	 *            restored even if the gallery size has changed. (Visible items
	 *            stay visible)
	 */
	protected void updateStructuralValues(boolean keepLocation) {

		if (DEBUG)
			System.out.println("Client Area : " + this.getClientArea().x + " " + this.getClientArea().y + " " + this.getClientArea().width + " "
					+ this.getClientArea().height);

		Rectangle area = this.getClientArea();
		float pos = 0;

		if (vertical) {
			if (gHeight > 0 && keepLocation)
				pos = (float) (translate + 0.5 * area.height) / (float) gHeight;

			gWidth = area.width;
			gHeight = calculateSize();

			if (keepLocation)
				translate = (int) (gHeight * pos - 0.5 * area.height);

		} else {
			if (gWidth > 0 && keepLocation)
				pos = (float) (translate + 0.5 * area.width) / (float) gWidth;

			gWidth = calculateSize();
			gHeight = area.height;

			if (keepLocation)
				translate = (int) (gWidth * pos - 0.5 * area.width);
		}

		validateTranslation();
		if (DEBUG)
			System.out.println("Content Size : " + gWidth + " " + gHeight);

	}

	private int calculateSize() {

		if (groupRenderer != null)
			groupRenderer.preLayout(null);

		int currentHeight = 0;

		int mainItemCount = getItemCount();

		for (int i = 0; i < mainItemCount; i++) {
			GalleryItem item = this.getItem(i);
			this.groupRenderer.setExpanded(item.isExpanded());
			// TODO: Not used ATM
			// int groupItemCount = item.getItemCount();
			if (vertical) {
				item.y = currentHeight;
				item.x = getClientArea().x;
				item.width = getClientArea().width;
				item.height = -1;
				this.groupRenderer.layout(null, item);
				currentHeight += item.height;
			} else {
				item.y = getClientArea().y;
				item.x = currentHeight;
				item.width = -1;
				item.height = getClientArea().height;
				this.groupRenderer.layout(null, item);
				currentHeight += item.width;
			}
			// Point s = this.getSize(item.hCount, item.vCount, itemSizeX,
			// itemSizeY, userMargin, realMargin);

			// item.height = s.y;

		}

		return currentHeight;
	}

	/**
	 * Move the scrollbar to reflect the current visible items position.
	 * <br/>The bar which is moved depends of the current gallery scrolling :
	 * vertical or horizontal.
	 * 
	 */
	protected void updateScrollBarsProperties() {

		if (vertical) {
			updateScrollBarProperties(getVerticalBar(), getClientArea().height, gHeight);
		} else {
			updateScrollBarProperties(getHorizontalBar(), getClientArea().width, gWidth);
		}

	}

	/**
	 * Move the scrollbar to reflect the current visible items position.
	 * 
	 * @param bar -
	 *            the scroll bar to move
	 * @param clientSize -
	 *            Client (visible) area size
	 * @param totalSize -
	 *            Total Size
	 */
	private void updateScrollBarProperties(ScrollBar bar, int clientSize, int totalSize) {
		if (bar == null)
			return;

		bar.setMinimum(0);

		bar.setIncrement(16);
		bar.setPageIncrement(clientSize);
		bar.setMaximum(totalSize);
		bar.setThumb(clientSize);

		if (totalSize > clientSize) {
			if (DEBUG)
				System.out.println("Enabling scrollbar");

			bar.setEnabled(true);
			bar.setSelection(translate);

			// Ensure that translate has a valid value.
			validateTranslation();
		} else {
			if (DEBUG)
				System.out.println("Disabling scrollbar");

			bar.setEnabled(false);
			bar.setSelection(0);
			translate = 0;
		}

	}

	/**
	 * Check the current translation value. Must be &gt; 0 and &lt; gallery
	 * size.<br/> Invalid values are fixed.
	 */
	private void validateTranslation() {
		Rectangle area = this.getClientArea();
		// Ensure that translate has a valid value.
		int totalSize = 0;
		int clientSize = 0;

		// Fix negative values
		if (translate < 0)
			translate = 0;

		// Get size depending on vertical setting.
		if (vertical) {
			totalSize = gHeight;
			clientSize = area.height;
		} else {
			totalSize = gWidth;
			clientSize = area.width;
		}

		if (totalSize > clientSize) {
			// Fix translate too big.
			if (translate + clientSize > totalSize)
				translate = totalSize - clientSize;
		} else
			translate = 0;

	}

	// TODO: not used ATM
	// private void scroll() {
	// if (vertical)
	// scrollVertical();
	// else
	// scrollHorizontal();
	// }

	private void scrollVertical() {
		int areaHeight = getClientArea().height;

		if (gHeight > areaHeight) {
			// image is higher than client area
			ScrollBar bar = getVerticalBar();
			scroll(0, translate - bar.getSelection(), 0, 0, getClientArea().width, areaHeight, false);
			translate = bar.getSelection();
		} else {
			translate = 0;
		}
	}

	private void scrollHorizontal() {

		int areaWidth = getClientArea().width;
		if (gWidth > areaWidth) {
			// image is higher than client area
			ScrollBar bar = getHorizontalBar();
			scroll(translate - bar.getSelection(), 0, 0, 0, areaWidth, getClientArea().height, false);
			translate = bar.getSelection();
		} else {
			translate = 0;
		}

	}

	protected void addItem(GalleryItem i) {
		checkWidget();
		if (!virtual) {

			if (items == null) {
				items = new GalleryItem[1];
			} else {
				Item[] oldItems = items;
				items = new GalleryItem[oldItems.length + 1];
				System.arraycopy(oldItems, 0, items, 0, oldItems.length);
			}
			items[items.length - 1] = i;
			updateStructuralValues(false);
			updateScrollBarsProperties();
		}
	}

	/**
	 * Get the item at index.<br/> If SWT.VIRTUAL is used and the item has not
	 * been used yet, the item is created and a SWT.SetData event is fired.
	 * 
	 * @param index :
	 *            index of the item.
	 * @return : the GalleryItem or null if index is out of bounds
	 */
	public GalleryItem getItem(int index) {
		checkWidget();
		return _getItem(index);
	}

	/**
	 * This method is used by items to implement getItem( index )
	 * 
	 * @param parent
	 * @param index
	 * @return
	 */
	protected GalleryItem _getItem(GalleryItem parent, int index) {

		if (index < parent.getItemCount()) {
			// Refresh item if it is not set yet
			updateItem(parent, index);
			return parent.items[index];
		}

		return null;
	}

	/**
	 * Get the item at index.<br/> If SWT.VIRTUAL is used and the item has not
	 * been used, the item is created and a SWT.SetData is fired.<br/>
	 * 
	 * This is the internat implementation of this method : checkWidget() is not
	 * used.
	 * 
	 * @param index
	 * @return
	 */
	protected GalleryItem _getItem(int index) {

		if (index < getItemCount()) {
			updateItem(null, index);
			return items[index];
		}

		return null;
	}

	/**
	 * Forward the mouseDown event to the corresponding group accorrding to the
	 * mouse position.
	 * 
	 * @param e
	 * @return
	 */
	protected boolean _mouseDown(MouseEvent e) {
		if (DEBUG)
			System.out.println("getitem " + e.x + " " + e.y);

		GalleryItem group = this.getGroup(new Point(e.x, e.y));
		if (group != null) {
			int pos = vertical ? (e.y + translate) : (e.x + translate);
			return groupRenderer.mouseDown(group, e, new Point(vertical ? e.x : pos, vertical ? pos : e.y));
		}

		return true;
	}

	/**
	 * Get item at pixel position
	 * 
	 * @param coords
	 * @return GalleryItem or null
	 */
	public GalleryItem getItem(Point coords) {
		checkWidget();

		if (DEBUG)
			System.out.println("getitem " + coords.x + " " + coords.y);
		int pos = vertical ? (coords.y + translate) : (coords.x + translate);

		GalleryItem group = this.getGroup(coords);
		if (group != null)
			return groupRenderer.getItem(group, new Point(vertical ? coords.x : pos, vertical ? pos : coords.y));

		return null;
	}

	/**
	 * Get group at pixel position
	 * 
	 * @param coords
	 * @return
	 */
	private GalleryItem getGroup(Point coords) {
		// If there is no item in the gallery, return asap
		if( items == null )
			return null;
		
		int pos = vertical ? (coords.y + translate) : (coords.x + translate);

		int index = 0;
		GalleryItem item = null;
		while (index < items.length) {
			item = getItem(index);

			if ((vertical ? item.y : item.x) > pos)
				break;

			if ((vertical ? (item.y + item.height) : (item.x + item.width)) >= pos)
				return item;

			index++;
		}

		return null;
	}

	// TODO: Not used ATM
	// private void clear() {
	// checkWidget();
	// if (virtual) {
	// setItemCount(0);
	// } else {
	// items = null;
	// }
	//
	// updateStructuralValues(true);
	// updateScrollBarsProperties();
	// }

	/**
	 * Clear all items.<br/>
	 * 
	 * All items are removed and dispose events are fired if the gallery is not
	 * virtual.<br/>
	 * 
	 * If the Gallery is virtual, the item count is not reseted and all items
	 * will be created again at their first use.<br/>
	 * 
	 */
	public void clearAll() {
		checkWidget();
		if (items != null) {
			// Clear items
			for (int i = 0; i < items.length; i++) {

				// Dispose items if not virtual
				if (!virtual) {
					if (items[i] != null) {
						// TODO: send a dispose event
						items[i].dispose();
					}
				}

				// Empty item
				items[i] = null;
			}
		}

		// Free array if not virtual
		if (!virtual) {
			items = null;
		}

		// TODO: I'm clearing selection here
		// but we have to check that Table has the same behavior
		this._deselectAll();

		updateStructuralValues(false);
		updateScrollBarsProperties();
		redraw();

	}

	/**
	 * Clear one item.<br/>
	 * 
	 * @param i
	 */
	public void clear(int i) {
		checkWidget();

		// TODO: When a Gallery is not virtual
		// Item must be removed and a dispose event must be fired.

		if (virtual) {
			items[i] = null;

			updateStructuralValues(false);
			updateScrollBarsProperties();
		}
	}

	/**
	 * Returns the index of a GalleryItem.
	 * 
	 * @param parentItem
	 * @param item
	 * @return
	 */
	public int indexOf(GalleryItem item) {
		checkWidget();
		if (item.getParentItem() == null)
			return _indexOf(item);
		else
			return _indexOf(item.getParentItem(), item);
	}

	/**
	 * Returns the index of a GalleryItem when it is a root Item
	 * 
	 * @param parentItem
	 * @param item
	 * @return
	 */
	protected int _indexOf(GalleryItem item) {
		int itemCount = getItemCount();
		if (item == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		if (1 <= lastIndexOf && lastIndexOf < itemCount - 1) {
			if (items[lastIndexOf] == item)
				return lastIndexOf;
			if (items[lastIndexOf + 1] == item)
				return ++lastIndexOf;
			if (items[lastIndexOf - 1] == item)
				return --lastIndexOf;
		}
		if (lastIndexOf < itemCount / 2) {
			for (int i = 0; i < itemCount; i++) {
				if (items[i] == item)
					return lastIndexOf = i;
			}
		} else {
			for (int i = itemCount - 1; i >= 0; --i) {
				if (items[i] == item)
					return lastIndexOf = i;
			}
		}
		return -1;
	}

	/**
	 * Returns the index of a GalleryItem when it is not a root Item
	 * 
	 * @param parentItem
	 * @param item
	 * @return
	 */
	protected int _indexOf(GalleryItem parentItem, GalleryItem item) {
		int itemCount = parentItem.getItemCount();
		if (item == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		if (1 <= parentItem.lastIndexOf && parentItem.lastIndexOf < itemCount - 1) {
			if (parentItem.items[parentItem.lastIndexOf] == item)
				return parentItem.lastIndexOf;
			if (parentItem.items[parentItem.lastIndexOf + 1] == item)
				return ++parentItem.lastIndexOf;
			if (parentItem.items[parentItem.lastIndexOf - 1] == item)
				return --parentItem.lastIndexOf;
		}
		if (parentItem.lastIndexOf < itemCount / 2) {
			for (int i = 0; i < itemCount; i++) {
				if (parentItem.items[i] == item)
					return parentItem.lastIndexOf = i;
			}
		} else {
			for (int i = itemCount - 1; i >= 0; --i) {
				if (parentItem.items[i] == item)
					return parentItem.lastIndexOf = i;
			}
		}
		return -1;
	}

	public GalleryItem[] getItems() {
		checkWidget();
		GalleryItem[] itemsLocal = new GalleryItem[this.items.length];
		System.arraycopy(items, 0, itemsLocal, 0, this.items.length);

		return itemsLocal;
	}

	public boolean isVertical() {
		checkWidget();
		return vertical;
	}

	public void setVertical(boolean vertical) {
		checkWidget();
		this.vertical = vertical;
		this.updateStructuralValues(true);
		redraw();
	}

	public AbstractGalleryGroupRenderer getGroupRenderer() {
		return groupRenderer;
	}

	public void setGroupRenderer(AbstractGalleryGroupRenderer groupRenderer) {
		this.groupRenderer = groupRenderer;
		groupRenderer.setGallery(this);
		this.updateStructuralValues(true);
		this.updateScrollBarsProperties();
		this.redraw();
	}

	public GalleryItem[] getSelection() {
		return selection;
	}

	public int getSelectionCount() {
		if (selection == null)
			return 0;

		return selection.length;
	}

	/**
	 * Selects all of the items in the receiver.
	 */
	public void selectAll() {
		checkWidget();
		_selectAll();
		redraw();
	}

	protected void _selectAll() {
		select(0, this.getItemCount() - 1);
	}

	public void setSelection(GalleryItem[] items) {
		checkWidget();
		_deselectAll();
		for (int i = 0; i < items.length; i++) {
			this.setSelected(items[i], true, false);
			
			// Ensure item is visibme
			_showItem(items[i]);
			
			// Simulate mouse click to enable keyboard navigation
			lastSingleClick = items[i];
		}
		redraw();
	}

	public void remove(GalleryItem item) {
		checkWidget();
		if (!virtual) {
			int index = this.indexOf(item);
			remove(index);
		}
	}

	public void remove(int index) {
		checkWidget();
		if (!virtual) {
			if (isSelected(items[index])) {
				setSelected(items[index], false, false);
			}

			this.items = (GalleryItem[]) this._arrayRemoveItem(this.items, index);

			updateStructuralValues(false);
			updateScrollBarsProperties();
			redraw();
		}
	}

	protected void _remove(GalleryItem parent, int index) {
		if (!virtual) {
			if (isSelected(parent.items[index])) {
				setSelected(parent.items[index], false, false);
			}

			parent.items = (GalleryItem[]) this._arrayRemoveItem(parent.items, index);
			updateStructuralValues(false);
			updateScrollBarsProperties();
			redraw();
		}
	}

	protected Object[] _arrayRemoveItem(Object[] array, int index) {

		if (array == null)
			return null;

		if (array.length == 1 && index == 0)
			return null;

		Object[] newArray = (Object[]) Array.newInstance(array[0].getClass(), array.length - 1);

		if (index > 0)
			System.arraycopy(array, 0, newArray, 0, index);

		if (index + 1 < array.length)
			System.arraycopy(array, index + 1, newArray, index, newArray.length - index);

		return newArray;
	}

	protected int _arrayIndexOf(int[] array, int value) {
		if (array == null)
			return -1;

		for (int i = array.length - 1; i >= 0; --i) {
			if (array[i] == value) {
				return i;

			}
		}
		return -1;
	}

	protected int _arrayIndexOf(Object[] array, Object value) {
		if (array == null)
			return -1;

		for (int i = array.length - 1; i >= 0; --i) {
			if (array[i] == value) {
				return i;

			}
		}
		return -1;
	}

	protected int[] _arrayRemoveItem(int[] array, int index) {

		if (array == null)
			return null;

		if (array.length == 1 && index == 0)
			return null;

		int[] newArray = new int[array.length - 1];

		if (index > 0)
			System.arraycopy(array, 0, newArray, 0, index);

		if (index + 1 < array.length)
			System.arraycopy(array, index + 1, newArray, index, newArray.length - index);

		return newArray;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
}
