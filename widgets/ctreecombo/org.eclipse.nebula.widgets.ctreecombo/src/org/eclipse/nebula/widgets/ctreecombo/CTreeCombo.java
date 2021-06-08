/*******************************************************************************
 * Copyright (c) 2019 Thomas Schindl & Laurent Caron.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * - Thomas Schindl (tom dot schindl at bestsolution dot at) - initial API
 * and implementation
 * - Laurent Caron (laurent dot caron at gmail dot com) - Integration to Nebula,
 * code cleaning and documentation
 *******************************************************************************/
package org.eclipse.nebula.widgets.ctreecombo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleControlAdapter;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.accessibility.AccessibleTextAdapter;
import org.eclipse.swt.accessibility.AccessibleTextEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.swt.widgets.Widget;

public class CTreeCombo extends Composite {
	static int checkStyle(int style) {
		final int mask = SWT.BORDER | SWT.READ_ONLY | SWT.FLAT | SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT;
		return SWT.NO_FOCUS | style & mask;
	}

	Text text;
	Tree tree;
	Button arrow;
	Listener listener, filter;
	Shell popup;
	boolean hasFocus;
	int visibleItemCount = 5;
	Color foreground, background;
	Font font;
	List<CTreeComboItem> items = new ArrayList<CTreeComboItem>();
	List<CTreeComboColumn> columns = new ArrayList<CTreeComboColumn>();

	List<TreeListener> treeListeners = new ArrayList<TreeListener>();

	private final TreeListener hookListener = new TreeListener() {

		@Override
		public void treeCollapsed(TreeEvent e) {
			e.item = (Widget) e.item.getData(CTreeComboItem.DATA_ID);
			e.widget = CTreeCombo.this;
			for (final TreeListener l : treeListeners) {
				l.treeCollapsed(e);
			}
		}

		@Override
		public void treeExpanded(TreeEvent e) {
			e.item = (Widget) e.item.getData(CTreeComboItem.DATA_ID);
			e.widget = CTreeCombo.this;
			for (final TreeListener l : treeListeners) {
				l.treeExpanded(e);
			}
		}

	};

	/**
	 * The CTreeCombo class represents a selectable user interface object
	 * that combines a text field and a tree and issues notification
	 * when an item is selected from the tree.
	 * <p>
	 * Note that although this class is a subclass of <code>Composite</code>,
	 * it does not make sense to add children to it, or set a layout on it.
	 * </p>
	 * <dl>
	 * <dt><b>Styles:</b>
	 * <dd>BORDER, READ_ONLY, FLAT</dd>
	 * <dt><b>Events:</b>
	 * <dd>DefaultSelection, Modify, Selection, Verify</dd>
	 * </dl>
	 */
	public CTreeCombo(Composite parent, int style) {
		super(parent, style = checkStyle(style));

		int textStyle = SWT.SINGLE;
		if ((style & SWT.READ_ONLY) != 0) {
			textStyle |= SWT.READ_ONLY;
		}
		if ((style & SWT.FLAT) != 0) {
			textStyle |= SWT.FLAT;
		}
		text = new Text(this, textStyle);
		int arrowStyle = SWT.ARROW | SWT.DOWN;
		if ((style & SWT.FLAT) != 0) {
			arrowStyle |= SWT.FLAT;
		}
		arrow = new Button(this, arrowStyle);

		listener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (popup == event.widget) {
					popupEvent(event);
					return;
				}
				if (text == event.widget) {
					textEvent(event);
					return;
				}
				if (tree == event.widget) {
					treeEvent(event);
					return;
				}
				if (arrow == event.widget) {
					arrowEvent(event);
					return;
				}
				if (CTreeCombo.this == event.widget) {
					comboEvent(event);
					return;
				}
				if (getShell() == event.widget) {
					getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							if (isDisposed()) {
								return;
							}
							handleFocus(SWT.FocusOut);
						}
					});
				}
			}
		};
		filter = (event) -> {
			final Shell shell = ((Control) event.widget).getShell();
			if (shell == CTreeCombo.this.getShell()) {
				handleFocus(SWT.FocusOut);
			}
		};

		final int[] comboEvents = { SWT.Dispose, SWT.FocusIn, SWT.Move, SWT.Resize };
		for (int i = 0; i < comboEvents.length; i++) {
			addListener(comboEvents[i], listener);
		}

		final int[] textEvents = { SWT.DefaultSelection, SWT.KeyDown, SWT.KeyUp, SWT.MenuDetect, SWT.Modify, SWT.MouseDown, SWT.MouseUp, SWT.MouseDoubleClick, SWT.MouseWheel, SWT.Traverse, SWT.FocusIn, SWT.Verify };
		for (int i = 0; i < textEvents.length; i++) {
			text.addListener(textEvents[i], listener);
		}

		final int[] arrowEvents = { SWT.MouseDown, SWT.MouseUp, SWT.Selection, SWT.FocusIn };
		for (int i = 0; i < arrowEvents.length; i++) {
			arrow.addListener(arrowEvents[i], listener);
		}

		createPopup(null, null);
		initAccessible();
	}

	char _findMnemonic(String string) {
		if (string == null) {
			return '\0';
		}
		int index = 0;
		final int length = string.length();
		do {
			while (index < length && string.charAt(index) != '&') {
				index++;
			}
			if (++index >= length) {
				return '\0';
			}
			if (string.charAt(index) != '&') {
				return Character.toLowerCase(string.charAt(index));
			}
			index++;
		} while (index < length);
		return '\0';
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified when
	 * the user changes the receiver's selection, by sending it one of the messages
	 * defined in the <code>SelectionListener</code> interface.
	 * <p>
	 * <code>widgetSelected</code> is called when the combo's list selection
	 * changes. <code>widgetDefaultSelected</code> is typically called when ENTER is
	 * pressed the combo's text area.
	 * </p>
	 *
	 * @param listener
	 *            the listener which should be notified when the user changes the
	 *            receiver's selection
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
	public void addSelectionListener(final SelectionListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}

		final TypedListener typedListener = new TypedListener(listener);
		addListener(SWT.Selection, typedListener);
		addListener(SWT.DefaultSelection, typedListener);
	}

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when an item in the receiver is expanded or collapsed
	 * by sending it one of the messages defined in the <code>TreeListener</code>
	 * interface.
	 *
	 * @param listener the listener which should be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see TreeListener
	 * @see #removeTreeListener
	 */
	public void addTreeListener(TreeListener listener) {
		checkWidget();
		treeListeners.add(listener);
	}

	private void adjustShellSize() {
		final Point size = getSize();
		final int itemCount = visibleItemCount;
		final int itemHeight = tree.getItemHeight() * itemCount;
		final Point listSize = tree.computeSize(SWT.DEFAULT, itemHeight, false);
		tree.setBounds(1, 1, Math.max(size.x - 2, listSize.x), listSize.y);

		final Display display = getDisplay();
		final Rectangle listRect = tree.getBounds();
		final Rectangle parentRect = display.map(getParent(), null, getBounds());
		final Point comboSize = getSize();
		final Rectangle displayRect = getMonitor().getClientArea();
		final int width = Math.max(comboSize.x, listRect.width + 2);
		final int height = listRect.height + 2;
		int x = parentRect.x;
		int y = parentRect.y + comboSize.y;
		if (y + height > displayRect.y + displayRect.height) {
			y = parentRect.y - height;
		}
		if (x + width > displayRect.x + displayRect.width) {
			x = displayRect.x + displayRect.width - listRect.width;
		}
		popup.setBounds(x, y, width, height);

	}

	void arrowEvent(Event event) {
		switch (event.type) {
			case SWT.FocusIn: {
				handleFocus(SWT.FocusIn);
				break;
			}
			case SWT.MouseDown: {
				final Event mouseEvent = new Event();
				mouseEvent.button = event.button;
				mouseEvent.count = event.count;
				mouseEvent.stateMask = event.stateMask;
				mouseEvent.time = event.time;
				mouseEvent.x = event.x;
				mouseEvent.y = event.y;
				notifyListeners(SWT.MouseDown, mouseEvent);
				event.doit = mouseEvent.doit;
				break;
			}
			case SWT.MouseUp: {
				final Event mouseEvent = new Event();
				mouseEvent.button = event.button;
				mouseEvent.count = event.count;
				mouseEvent.stateMask = event.stateMask;
				mouseEvent.time = event.time;
				mouseEvent.x = event.x;
				mouseEvent.y = event.y;
				notifyListeners(SWT.MouseUp, mouseEvent);
				event.doit = mouseEvent.doit;
				break;
			}
			case SWT.Selection: {
				text.setFocus();
				dropDown(!isDropped());
				break;
			}
		}
	}

	/**
	 * Clears the item at the given zero-relative index in the receiver.
	 * The text, icon and other attributes of the item are set to the default
	 * value. If the tree was created with the <code>SWT.VIRTUAL</code> style,
	 * these attributes are requested again as needed.
	 *
	 * @param index the index of the item to clear
	 * @param all <code>true</code> if all child items of the indexed item should be
	 *            cleared recursively, and <code>false</code> otherwise
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see SWT#VIRTUAL
	 * @see SWT#SetData
	 */
	public void clear(int index, boolean all) {
		checkWidget();
		tree.clear(index, all);
	}

	/**
	 * Clears all the items in the receiver. The text, icon and other
	 * attributes of the items are set to their default values. If the
	 * tree was created with the <code>SWT.VIRTUAL</code> style, these
	 * attributes are requested again as needed.
	 *
	 * @param all <code>true</code> if all child items should be cleared
	 *            recursively, and <code>false</code> otherwise
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see SWT#VIRTUAL
	 * @see SWT#SetData
	 */
	public void clearAll(boolean all) {
		checkWidget();
		tree.clearAll(all);
		text.setFont(text.getFont());
		text.setText(""); //$NON-NLS-1$
	}

	void comboEvent(Event event) {
		switch (event.type) {
			case SWT.Dispose:
				if (popup != null && !popup.isDisposed()) {
					tree.removeListener(SWT.Dispose, listener);
					popup.dispose();
				}
				final Shell shell = getShell();
				shell.removeListener(SWT.Deactivate, listener);
				final Display display = getDisplay();
				display.removeFilter(SWT.FocusIn, filter);
				popup = null;
				text = null;
				tree = null;
				arrow = null;
				break;
			case SWT.FocusIn:
				final Control focusControl = getDisplay().getFocusControl();
				if (focusControl == arrow || focusControl == tree) {
					return;
				}
				if (isDropped()) {
					tree.setFocus();
				} else {
					text.setFocus();
				}
				break;
			case SWT.Move:
				dropDown(false);
				break;
			case SWT.Resize:
				internalLayout(false);
				break;
		}
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#computeSize(int, int, boolean)
	 */
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		checkWidget();
		int width = 0, height = 0;
		final TreeItem[] items = tree.getItems();
		final GC gc = new GC(text);
		final int spacer = gc.stringExtent(" ").x; //$NON-NLS-1$
		int textWidth = gc.stringExtent(text.getText()).x;
		for (int i = 0; i < items.length; i++) {
			textWidth = Math.max(gc.stringExtent(items[i].getText()).x, textWidth);
		}
		gc.dispose();
		final Point textSize = text.computeSize(SWT.DEFAULT, SWT.DEFAULT, changed);
		final Point arrowSize = arrow.computeSize(SWT.DEFAULT, SWT.DEFAULT, changed);
		final Point listSize = tree.computeSize(SWT.DEFAULT, SWT.DEFAULT, changed);
		final int borderWidth = getBorderWidth();

		height = Math.max(textSize.y, arrowSize.y + 5);
		width = Math.max(textWidth + 2 * spacer + arrowSize.x + 2 * borderWidth, listSize.x);
		if (wHint != SWT.DEFAULT) {
			width = wHint;
		}
		if (hHint != SWT.DEFAULT) {
			height = hHint;
		}
		return new Point(width + 2 * borderWidth, height + 2 * borderWidth);
	}

	void createPopup(Collection<CTreeComboItem> items, CTreeComboItem selectedItem) {
		// create shell and list
		popup = new Shell(getShell(), SWT.NO_TRIM | SWT.ON_TOP);
		final int style = getStyle();
		int listStyle = SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE;
		if ((style & SWT.FLAT) != 0) {
			listStyle |= SWT.FLAT;
		}
		if ((style & SWT.RIGHT_TO_LEFT) != 0) {
			listStyle |= SWT.RIGHT_TO_LEFT;
		}
		if ((style & SWT.LEFT_TO_RIGHT) != 0) {
			listStyle |= SWT.LEFT_TO_RIGHT;
		}
		tree = new Tree(popup, listStyle);
		tree.addTreeListener(hookListener);
		if (font != null) {
			tree.setFont(font);
		}
		if (foreground != null) {
			tree.setForeground(foreground);
		}
		if (background != null) {
			tree.setBackground(background);
		}

		final int[] popupEvents = { SWT.Close, SWT.Paint, SWT.Deactivate };
		for (int i = 0; i < popupEvents.length; i++) {
			popup.addListener(popupEvents[i], listener);
		}
		final int[] listEvents = { SWT.MouseUp, SWT.Selection, SWT.Traverse, SWT.KeyDown, SWT.KeyUp, SWT.FocusIn, SWT.Dispose, SWT.Collapse, SWT.Expand };
		for (int i = 0; i < listEvents.length; i++) {
			tree.addListener(listEvents[i], listener);
		}

		for (final CTreeComboColumn c : columns) {
			final TreeColumn col = new TreeColumn(tree, SWT.NONE);
			c.setRealTreeColumn(col);
		}

		if (items != null) {
			createTreeItems(items.toArray(new CTreeComboItem[0]));
		}

		if (selectedItem != null) {
			tree.setSelection(selectedItem.getRealTreeItem());
		}
	}

	private void createTreeItems(CTreeComboItem[] items) {
		boolean isReadOnly = (getStyle() & SWT.READ_ONLY) != 0;

		for (final CTreeComboItem item : items) {
			if (!isReadOnly && !match(item) && item.getItemCount() == 0) {
				continue;
			}
			item.buildRealTreeItem(tree, columns.size());
			createTreeItems(item.getItems());
			if (!isReadOnly && !match(item) && item.getRealTreeItem().getItemCount() == 0) {
				item.getRealTreeItem().dispose();
			}
		}
	}


	private boolean match(final CTreeComboItem item) {
		final String entry = text.getText().trim().toLowerCase();
		if (entry.equals("") || item.getText().trim().equals("")) {
			return true;
		}

		return item.getText().trim().toLowerCase().indexOf(entry) > -1;
	}

	/**
	 * Deselects all selected items in the receiver.
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void deselectAll() {
		checkWidget();
		tree.deselectAll();
	}

	void dropDown(boolean drop) {
		if (drop == isDropped() || !isVisible()) {
			return;
		}
		if (!drop) {
			popup.setVisible(false);
			if (!isDisposed() && isFocusControl()) {
				text.setFocus();
			}
			return;
		}

		CTreeComboItem selectionIndex = null;
		if (getShell() != popup.getParent()) {
			final TreeItem[] s = tree.getSelection();
			if (s.length > 0) {
				selectionIndex = (CTreeComboItem) s[0].getData(CTreeComboItem.DATA_ID);
			}
		}

		// Rebuild the popup and filter data if the widget is not read-only (based on what is typed)
		tree.removeListener(SWT.Dispose, listener);
		popup.dispose();
		popup = null;
		tree = null;
		createPopup(items, selectionIndex);

		final TreeItem[] items = tree.getSelection();
		if (items.length != 0) {
			tree.showItem(items[0]);
		}

		adjustShellSize();
		popup.setVisible(true);
		popup.setActive();
		if (isFocusControl()) {
			tree.setFocus();
		}
	}

	Label getAssociatedLabel() {
		final Control[] siblings = getParent().getChildren();
		for (int i = 0; i < siblings.length; i++) {
			if (siblings[i] == this) {
				if (i > 0 && siblings[i - 1] instanceof Label) {
					return (Label) siblings[i - 1];
				}
			}
		}
		return null;
	}

	/**
	 * Returns the column at the given, zero-relative index in the
	 * receiver. Throws an exception if the index is out of range.
	 * Columns are returned in the order that they were created.
	 * If no <code>TreeColumn</code>s were created by the programmer,
	 * this method will throw <code>ERROR_INVALID_RANGE</code> despite
	 * the fact that a single column of data may be visible in the tree.
	 * This occurs when the programmer uses the tree like a list, adding
	 * items but never creating a column.
	 *
	 * @param index the index of the column to return
	 * @return the column at the given index
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 */
	public CTreeComboColumn getColumn(int columnIndex) {
		checkWidget();
		return columns.get(columnIndex);
	}

	/**
	 * Returns the number of columns contained in the receiver.
	 * If no <code>TreeColumn</code>s were created by the programmer,
	 * this value is zero, despite the fact that visually, one column
	 * of items may be visible. This occurs when the programmer uses
	 * the tree like a list, adding items but never creating a column.
	 *
	 * @return the number of columns
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @since 3.1
	 */
	public int getColumnCount() {
		checkWidget();
		return tree.getColumnCount();
	}

	/**
	 * Returns an array of zero-relative integers that map
	 * the creation order of the receiver's items to the
	 * order in which they are currently being displayed.
	 * <p>
	 * Specifically, the indices of the returned array represent
	 * the current visual order of the items, and the contents
	 * of the array represent the creation order of the items.
	 * </p>
	 * <p>
	 * Note: This is not the actual structure used by the receiver
	 * to maintain its list of items, so modifying the array will
	 * not affect the receiver.
	 * </p>
	 *
	 * @return the current visual order of the receiver's items
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 */
	public int[] getColumnOrder() {
		checkWidget();
		return tree.getColumnOrder();
	}

	/**
	 * Gets the editable state.
	 *
	 * @return whether or not the receiver is editable
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public boolean getEditable() {
		checkWidget();
		return text.getEditable();
	}

	/**
	 * Returns the item at the given, zero-relative index in the
	 * receiver. Throws an exception if the index is out of range.
	 *
	 * @param index the index of the item to return
	 * @return the item at the given index
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public CTreeComboItem getItem(int index) {
		checkWidget();
		return items.get(index);
	}

	/**
	 * Returns the item at the given point in the receiver
	 * or null if no such item exists. The point is in the
	 * coordinate system of the receiver.
	 * <p>
	 * The item that is returned represents an item that could be selected by the user.
	 * For example, if selection only occurs in items in the first column, then null is
	 * returned if the point is outside of the item.
	 * Note that the SWT.FULL_SELECTION style hint, which specifies the selection policy,
	 * determines the extent of the selection.
	 * </p>
	 *
	 * @param point the point used to locate the item
	 * @return the item at the given point, or null if the point is not in a selectable item
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the point is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public CTreeComboItem getItem(Point p) {
		checkWidget();
		final TreeItem item = tree.getItem(p);

		if (item != null) {
			item.getData(CTreeComboItem.DATA_ID);
		}
		return null;
	}

	/**
	 * Returns the number of items contained in the receiver
	 * that are direct item children of the receiver. The
	 * number that is returned is the number of roots in the
	 * tree.
	 *
	 * @return the number of items
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public int getItemCount() {
		checkWidget();
		return tree.getItemCount();
	}

	/**
	 * Returns a (possibly empty) array of items contained in the
	 * receiver that are direct item children of the receiver. These
	 * are the roots of the tree.
	 * <p>
	 * Note: This is not the actual structure used by the receiver
	 * to maintain its list of items, so modifying the array will
	 * not affect the receiver.
	 * </p>
	 *
	 * @return the items
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public CTreeComboItem[] getItems() {
		checkWidget();
		return items.toArray(new CTreeComboItem[0]);
	}

	/**
	 * Returns an array of <code>CTreeComboItem</code>s that are currently
	 * selected in the receiver. The order of the items is unspecified.
	 * An empty array indicates that no items are selected.<br/>
	 * <strong>This array could not have more than 1 element (because it is a single selection)</strong>
	 * <p>
	 * Note: This is not the actual structure used by the receiver
	 * to maintain its selection, so modifying the array will
	 * not affect the receiver.
	 * </p>
	 *
	 * @return an array representing the selection
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public CTreeComboItem[] getSelection() {
		checkWidget();

		final TreeItem[] items = tree.getSelection();
		if (items.length > 0) {
			return new CTreeComboItem[] { (CTreeComboItem) items[0].getData(CTreeComboItem.DATA_ID) };
		}

		return new CTreeComboItem[0];
	}

	/**
	 * Returns a string containing a copy of the contents of the
	 * receiver's text field.
	 *
	 * @return the receiver's text
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public String getText() {
		checkWidget();
		return text.getText();
	}

	void handleFocus(int type) {
		if (isDisposed()) {
			return;
		}
		switch (type) {
			case SWT.FocusIn: {
				if (hasFocus) {
					return;
				}
				if (getEditable()) {
					text.selectAll();
				}
				hasFocus = true;
				final Shell shell = getShell();
				shell.removeListener(SWT.Deactivate, listener);
				shell.addListener(SWT.Deactivate, listener);
				final Display display = getDisplay();
				display.removeFilter(SWT.FocusIn, filter);
				display.addFilter(SWT.FocusIn, filter);
				final Event e = new Event();
				notifyListeners(SWT.FocusIn, e);
				break;
			}
			case SWT.FocusOut: {
				if (!hasFocus) {
					return;
				}
				final Control focusControl = getDisplay().getFocusControl();
				if (focusControl == arrow || focusControl == tree || focusControl == text) {
					return;
				}
				hasFocus = false;
				final Shell shell = getShell();
				shell.removeListener(SWT.Deactivate, listener);
				final Display display = getDisplay();
				display.removeFilter(SWT.FocusIn, filter);
				final Event e = new Event();
				notifyListeners(SWT.FocusOut, e);
				break;
			}
		}
	}

	/**
	 * Searches the receiver's list starting at the first column
	 * (index 0) until a column is found that is equal to the
	 * argument, and returns the index of that column. If no column
	 * is found, returns -1.
	 *
	 * @param column the search column
	 * @return the index of the column
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the column is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public int indexOf(CTreeComboItem item) {
		checkWidget();
		return tree.indexOf(item.getRealTreeItem());
	}

	void initAccessible() {
		final AccessibleAdapter accessibleAdapter = new AccessibleAdapter() {
			@Override
			public void getHelp(AccessibleEvent e) {
				e.result = getToolTipText();
			}

			@Override
			public void getKeyboardShortcut(AccessibleEvent e) {
				String shortcut = null;
				final Label label = getAssociatedLabel();
				if (label != null) {
					final String text = label.getText();
					if (text != null) {
						final char mnemonic = _findMnemonic(text);
						if (mnemonic != '\0') {
							shortcut = "Alt+" + mnemonic; //$NON-NLS-1$
						}
					}
				}
				e.result = shortcut;
			}

			@Override
			public void getName(AccessibleEvent e) {
				String name = null;
				final Label label = getAssociatedLabel();
				if (label != null) {
					name = stripMnemonic(label.getText());
				}
				e.result = name;
			}
		};
		getAccessible().addAccessibleListener(accessibleAdapter);
		text.getAccessible().addAccessibleListener(accessibleAdapter);
		tree.getAccessible().addAccessibleListener(accessibleAdapter);

		arrow.getAccessible().addAccessibleListener(new AccessibleAdapter() {
			@Override
			public void getHelp(AccessibleEvent e) {
				e.result = getToolTipText();
			}

			@Override
			public void getKeyboardShortcut(AccessibleEvent e) {
				e.result = "Alt+Down Arrow"; //$NON-NLS-1$
			}

			@Override
			public void getName(AccessibleEvent e) {
				e.result = isDropped() ? SWT.getMessage("SWT_Close") : SWT.getMessage("SWT_Open"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		});

		getAccessible().addAccessibleTextListener(new AccessibleTextAdapter() {
			@Override
			public void getCaretOffset(AccessibleTextEvent e) {
				e.offset = text.getCaretPosition();
			}

			@Override
			public void getSelectionRange(AccessibleTextEvent e) {
				final Point sel = text.getSelection();
				e.offset = sel.x;
				e.length = sel.y - sel.x;
			}
		});

		getAccessible().addAccessibleControlListener(new AccessibleControlAdapter() {
			@Override
			public void getChildAtPoint(AccessibleControlEvent e) {
				final Point testPoint = toControl(e.x, e.y);
				if (getBounds().contains(testPoint)) {
					e.childID = ACC.CHILDID_SELF;
				}
			}

			@Override
			public void getChildCount(AccessibleControlEvent e) {
				e.detail = 0;
			}

			@Override
			public void getLocation(AccessibleControlEvent e) {
				final Rectangle location = getBounds();
				final Point pt = getParent().toDisplay(location.x, location.y);
				e.x = pt.x;
				e.y = pt.y;
				e.width = location.width;
				e.height = location.height;
			}

			@Override
			public void getRole(AccessibleControlEvent e) {
				e.detail = ACC.ROLE_COMBOBOX;
			}

			@Override
			public void getState(AccessibleControlEvent e) {
				e.detail = ACC.STATE_NORMAL;
			}

			@Override
			public void getValue(AccessibleControlEvent e) {
				e.result = getText();
			}
		});

		text.getAccessible().addAccessibleControlListener(new AccessibleControlAdapter() {
			@Override
			public void getRole(AccessibleControlEvent e) {
				e.detail = text.getEditable() ? ACC.ROLE_TEXT : ACC.ROLE_LABEL;
			}
		});

		arrow.getAccessible().addAccessibleControlListener(new AccessibleControlAdapter() {
			@Override
			public void getDefaultAction(AccessibleControlEvent e) {
				e.result = isDropped() ? SWT.getMessage("SWT_Close") : SWT.getMessage("SWT_Open"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		});
	}

	private CTreeComboItem internalGetSelection() {
		final CTreeComboItem[] is = getSelection();
		if (is.length != 0) {
			return is[0];
		}

		return null;
	}

	void internalLayout(boolean changed) {
		if (isDropped()) {
			dropDown(false);
		}
		final Rectangle rect = getClientArea();
		final int width = rect.width;
		final int height = rect.height;
		final Point arrowSize = arrow.computeSize(SWT.DEFAULT, height, changed);
		text.setBounds(0, 0, width - arrowSize.x, height);
		arrow.setBounds(width - arrowSize.x, 0, arrowSize.x, arrowSize.y);
	}

	boolean isDropped() {
		return popup.getVisible();
	}

	void popupEvent(Event event) {
		switch (event.type) {
			case SWT.Paint:
				// draw black rectangle around list
				final Rectangle listRect = tree.getBounds();
				final Color black = getDisplay().getSystemColor(SWT.COLOR_BLACK);
				event.gc.setForeground(black);
				event.gc.drawRectangle(0, 0, listRect.width + 1, listRect.height + 1);
				break;
			case SWT.Close:
				event.doit = false;
				dropDown(false);
				break;
			case SWT.Deactivate:
				if (!"carbon".equals(SWT.getPlatform())) {
					final Point point = arrow.toControl(getDisplay().getCursorLocation());
					final Point size = arrow.getSize();
					final Rectangle rect = new Rectangle(0, 0, size.x, size.y);
					if (!rect.contains(point)) {
						dropDown(false);
					}
				} else {
					dropDown(false);
				}
				break;
		}
	}

	/**
	 * Removes all of the items from the receiver.
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void removeAll() {
		checkWidget();
		tree.removeAll();
		items.clear();
		text.setFont(text.getFont());
		text.setText(""); //$NON-NLS-1$
	}

	/**
	 * Removes the listener from the collection of listeners who will be notified
	 * when the user changes the receiver's selection.
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
	 * @see #addSelectionListener
	 */
	public void removeSelectionListener(final SelectionListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		removeListener(SWT.Selection, listener);
		removeListener(SWT.DefaultSelection, listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when items in the receiver are expanded or collapsed.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see TreeListener
	 * @see #addTreeListener
	 */
	public void removeTreeListener(TreeListener listener) {
		checkWidget();
		treeListeners.remove(listener);
	}

	/**
	 * Selects an item in the receiver. If the item was already
	 * selected, it remains selected.
	 *
	 * @param item the item to be selected
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the item is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the item has been disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void select(CTreeComboItem item) {
		checkWidget();
		if (item == null) {
			tree.deselectAll();
			text.setFont(text.getFont());
			text.setText(""); //$NON-NLS-1$
			return;
		}

		if (item != null) {
			if (item != internalGetSelection()) {
				text.setFont(text.getFont());
				text.setText(item.getText());
				text.selectAll();
				tree.select(item.getRealTreeItem());
				tree.showSelection();
			}
		}
	}

	/**
	 * Sets the number of root-level items contained in the receiver.
	 *
	 * @param count the number of items
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 */
	public void setItemCount(int count) {
		checkWidget();
		tree.setItemCount(count);
	}

	/**
	 * Sets the receiver's selection to be the given array of items.
	 * The current selection is cleared before the new items are selected,
	 * and if necessary the receiver is scrolled to make the new selection visible.
	 * <p>
	 * Items that are not in the receiver are ignored.
	 * If the receiver is single-select and multiple items are specified,
	 * then all items are ignored.
	 * </p>
	 *
	 * @param items the array of items
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the array of items is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if one of the items has been disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 * @see Tree#deselectAll()
	 */
	public void setSelection(CTreeComboItem[] newItems) {
		checkWidget();
		final TreeItem[] items = new TreeItem[newItems.length];
		for (int i = 0; i < items.length; i++) {
			items[i] = newItems[i].getRealTreeItem();
		}
		if (items.length == 0) {
			text.setFont(text.getFont());
			text.setText(""); //$NON-NLS-1$
		} else {
			text.setFont(text.getFont());
			text.setText(items[0].getText());
		}
		tree.setSelection(items);
	}

	/**
	 * Shows the item. If the item is already showing in the receiver,
	 * this method simply returns. Otherwise, the items are scrolled
	 * and expanded until the item is visible.
	 *
	 * @param item the item to be shown
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the item is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the item has been disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see Tree#showSelection()
	 */
	public void showItem(CTreeComboItem item) {
		checkWidget();
		tree.showItem(item.getRealTreeItem());
	}

	String stripMnemonic(String string) {
		int index = 0;
		final int length = string.length();
		do {
			while (index < length && string.charAt(index) != '&') {
				index++;
			}
			if (++index >= length) {
				return string;
			}
			if (string.charAt(index) != '&') {
				return string.substring(0, index - 1) + string.substring(index, length);
			}
			index++;
		} while (index < length);
		return string;
	}

	void textEvent(Event event) {
		switch (event.type) {
			case SWT.FocusIn: {
				handleFocus(SWT.FocusIn);
				break;
			}
			case SWT.DefaultSelection: {
				dropDown(false);
				final Event e = new Event();
				e.time = event.time;
				e.stateMask = event.stateMask;
				notifyListeners(SWT.DefaultSelection, e);
				break;
			}
			case SWT.KeyUp: {
				final Event e = new Event();
				e.time = event.time;
				e.character = event.character;
				e.keyCode = event.keyCode;
				e.stateMask = event.stateMask;
				notifyListeners(SWT.KeyUp, e);
				event.doit = e.doit;
				break;
			}
			case SWT.MenuDetect: {
				final Event e = new Event();
				e.time = event.time;
				notifyListeners(SWT.MenuDetect, e);
				break;
			}
			case SWT.Modify: {
				tree.deselectAll();
				final Event e = new Event();
				e.time = event.time;
				notifyListeners(SWT.Modify, e);
				break;
			}
			case SWT.MouseDown: {
				final Event mouseEvent = new Event();
				mouseEvent.button = event.button;
				mouseEvent.count = event.count;
				mouseEvent.stateMask = event.stateMask;
				mouseEvent.time = event.time;
				mouseEvent.x = event.x;
				mouseEvent.y = event.y;
				notifyListeners(SWT.MouseDown, mouseEvent);
				if (isDisposed()) {
					break;
				}
				event.doit = mouseEvent.doit;
				if (!event.doit) {
					break;
				}
				if (event.button != 1) {
					return;
				}
				if (text.getEditable()) {
					return;
				}
				final boolean dropped = isDropped();
				text.selectAll();
				if (!dropped) {
					setFocus();
				}
				dropDown(!dropped);
				break;
			}
			case SWT.MouseUp: {
				final Event mouseEvent = new Event();
				mouseEvent.button = event.button;
				mouseEvent.count = event.count;
				mouseEvent.stateMask = event.stateMask;
				mouseEvent.time = event.time;
				mouseEvent.x = event.x;
				mouseEvent.y = event.y;
				notifyListeners(SWT.MouseUp, mouseEvent);
				if (isDisposed()) {
					break;
				}
				event.doit = mouseEvent.doit;
				if (!event.doit) {
					break;
				}
				if (event.button != 1) {
					return;
				}
				if (text.getEditable()) {
					return;
				}
				text.selectAll();
				break;
			}
			case SWT.MouseDoubleClick: {
				final Event mouseEvent = new Event();
				mouseEvent.button = event.button;
				mouseEvent.count = event.count;
				mouseEvent.stateMask = event.stateMask;
				mouseEvent.time = event.time;
				mouseEvent.x = event.x;
				mouseEvent.y = event.y;
				notifyListeners(SWT.MouseDoubleClick, mouseEvent);
				break;
			}
			case SWT.Traverse: {
				switch (event.detail) {
					case SWT.TRAVERSE_ARROW_PREVIOUS:
					case SWT.TRAVERSE_ARROW_NEXT:
						// The enter causes default selection and
						// the arrow keys are used to manipulate the list contents so
						// do not use them for traversal.
						event.doit = false;
						break;
					case SWT.TRAVERSE_TAB_PREVIOUS:
						event.doit = traverse(SWT.TRAVERSE_TAB_PREVIOUS);
						event.detail = SWT.TRAVERSE_NONE;
						return;
				}
				final Event e = new Event();
				e.time = event.time;
				e.detail = event.detail;
				e.doit = event.doit;
				e.character = event.character;
				e.keyCode = event.keyCode;
				notifyListeners(SWT.Traverse, e);
				event.doit = e.doit;
				event.detail = e.detail;
				break;
			}
			case SWT.Verify: {
				final Event e = new Event();
				e.text = event.text;
				e.start = event.start;
				e.end = event.end;
				e.character = event.character;
				e.keyCode = event.keyCode;
				e.stateMask = event.stateMask;
				notifyListeners(SWT.Verify, e);
				event.doit = e.doit;
				break;
			}
		}
	}

	void treeEvent(Event event) {
		switch (event.type) {
			case SWT.Dispose:
				if (getShell() != popup.getParent()) {
					final CTreeComboItem selectionIndex = internalGetSelection();
					popup = null;
					tree = null;
					createPopup(items, selectionIndex);
				}
				break;
			case SWT.FocusIn: {
				handleFocus(SWT.FocusIn);
				break;
			}
			case SWT.Selection: {
				final TreeItem[] items = tree.getSelection();
				if (items.length != 1) {
					return;
				}

				if (items[0].getItemCount() != 0) {
					return;
				}

				text.setFont(text.getFont());
				text.setText(items[0].getText());
				text.selectAll();
				tree.setSelection(items);
				final Event e = new Event();
				e.time = event.time;
				e.stateMask = event.stateMask;
				e.doit = event.doit;
				if (event.item != null) {
					e.data = event.item.getData();
				}
				notifyListeners(SWT.Selection, e);
				event.doit = e.doit;
				dropDown(false);
				break;
			}
			case SWT.Traverse: {
				switch (event.detail) {
					case SWT.TRAVERSE_RETURN:
					case SWT.TRAVERSE_ESCAPE:
					case SWT.TRAVERSE_ARROW_PREVIOUS:
					case SWT.TRAVERSE_ARROW_NEXT:
						event.doit = false;
						break;
					case SWT.TRAVERSE_TAB_NEXT:
					case SWT.TRAVERSE_TAB_PREVIOUS:
						event.doit = text.traverse(event.detail);
						event.detail = SWT.TRAVERSE_NONE;
						if (event.doit) {
							dropDown(false);
						}
						return;
				}
				final Event e = new Event();
				e.time = event.time;
				e.detail = event.detail;
				e.doit = event.doit;
				e.character = event.character;
				e.keyCode = event.keyCode;
				notifyListeners(SWT.Traverse, e);
				event.doit = e.doit;
				event.detail = e.detail;
				break;
			}
			case SWT.KeyUp: {
				final Event e = new Event();
				e.time = event.time;
				e.character = event.character;
				e.keyCode = event.keyCode;
				e.stateMask = event.stateMask;
				notifyListeners(SWT.KeyUp, e);
				break;
			}
			case SWT.KeyDown: {
				if (event.character == SWT.ESC) {
					// Escape key cancels popup list
					dropDown(false);
				}
				if ((event.stateMask & SWT.ALT) != 0 && (event.keyCode == SWT.ARROW_UP || event.keyCode == SWT.ARROW_DOWN)) {
					dropDown(false);
				}
				if (event.character == SWT.CR) {
					// Enter causes default selection
					dropDown(false);
					final Event e = new Event();
					e.time = event.time;
					e.stateMask = event.stateMask;
					notifyListeners(SWT.DefaultSelection, e);
				}
				// At this point the widget may have been disposed.
				// If so, do not continue.
				if (isDisposed()) {
					break;
				}
				final Event e = new Event();
				e.time = event.time;
				e.character = event.character;
				e.keyCode = event.keyCode;
				e.stateMask = event.stateMask;
				notifyListeners(SWT.KeyDown, e);
				break;

			}
			case SWT.Collapse: {
				adjustShellSize();
				break;
			}
			case SWT.Expand: {
				adjustShellSize();
				break;
			}
		}
	}
}
