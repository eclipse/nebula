/*******************************************************************************
 * Copyright (c) 2011-2021 Laurent CARON
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API
 * and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.duallist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.nebula.widgets.opal.commons.SelectionListenerUtil;
import org.eclipse.nebula.widgets.opal.duallist.DLItem.LAST_ACTION;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * Instances of this class are controls that allow the user to select multiple
 * elements.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection</dd>
 * </dl>
 */

public class DualList extends Composite {

	private static final String DOUBLE_DOWN_IMAGE = "double_down.png";
	private static final String DOUBLE_UP_IMAGE = "double_up.png";
	private static final String DOUBLE_LEFT_IMAGE = "double_left.png";
	private static final String DOUBLE_RIGHT_IMAGE = "double_right.png";
	private static final String ARROW_DOWN_IMAGE = "arrow_down.png";
	private static final String ARROW_LEFT_IMAGE = "arrow_left.png";
	private static final String ARROW_UP_IMAGE = "arrow_up.png";
	private static final String ARROW_RIGHT_IMAGE = "arrow_right.png";

	private final List<DLItem> items;
	private final List<DLItem> selection;

	private Table itemsTable;
	private Table selectionTable;

	private List<SelectionChangeListener> selectionChangeListeners;
	private DLConfiguration configuration;
	private Button buttonSelectAll, buttonMoveFirst, buttonSelect, buttonMoveUp, //
			buttonDeselect, buttonMoveDown, buttonDeselectAll, buttonMoveLast;

	/**
	 * Constructs a new instance of this class given its parent and a style value
	 * describing its behavior and appearance.
	 * <p>
	 * The style value is either one of the style constants defined in class
	 * <code>SWT</code> which is applicable to instances of this class, or must be
	 * built by <em>bitwise OR</em>'ing together (that is, using the
	 * <code>int</code> "|" operator) two or more of those <code>SWT</code> style
	 * constants. The class description lists the style constants that are
	 * applicable to the class. Style bits are also inherited from superclasses.
	 * </p>
	 *
	 * @param parent a composite control which will be the parent of the new
	 *            instance (cannot be null)
	 * @param style the style of control to construct
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the parent</li>
	 *                </ul>
	 *
	 */
	public DualList(final Composite parent, final int style) {
		super(parent, style);
		items = new ArrayList<DLItem>();
		selection = new ArrayList<DLItem>();

		setLayout(new GridLayout(4, false));
		createItemsTable();
		createButtonSelectAll();
		createSelectionTable();
		createButtonMoveFirst();
		createButtonSelect();
		createButtonMoveUp();
		createButtonDeselect();
		createButtonMoveDown();
		createButtonDeselectAll();
		createButtonMoveLast();
	}

	private void createItemsTable() {
		itemsTable = createTable();
		itemsTable.addListener(SWT.MouseDoubleClick, event -> {
			selectItem();
		});
	}

	/**
	 * @return a table that will contain data
	 */
	private Table createTable() {
		final Table table = new Table(this, SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
		table.setLinesVisible(false);
		table.setHeaderVisible(false);
		final GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true, 1, 4);
		gd.widthHint = 200;
		table.setLayoutData(gd);
		new TableColumn(table, SWT.CENTER);
		new TableColumn(table, SWT.LEFT);
		table.setData(-1);
		return table;
	}

	private void createButtonSelectAll() {
		buttonSelectAll = createButton(DOUBLE_RIGHT_IMAGE, true, GridData.END);
		buttonSelectAll.addListener(SWT.Selection, e -> {
			selectAll();
		});
	}

	private void createSelectionTable() {
		selectionTable = createTable();
		selectionTable.addListener(SWT.MouseDoubleClick, event -> {
			deselectItem();
		});
	}

	private void createButtonMoveFirst() {
		buttonMoveFirst = createButton(DOUBLE_UP_IMAGE, true, GridData.END);
		buttonMoveFirst.addListener(SWT.Selection, e -> {
			moveSelectionToFirstPosition();
		});
	}

	private void createButtonSelect() {
		buttonSelect = createButton(ARROW_RIGHT_IMAGE, false, GridData.CENTER);
		buttonSelect.addListener(SWT.Selection, e -> {
			selectItem();
		});
	}

	private void createButtonMoveUp() {
		buttonMoveUp = createButton(ARROW_UP_IMAGE, false, GridData.CENTER);
		buttonMoveUp.addListener(SWT.Selection, e -> {
			moveUpItem();
		});
	}

	private void createButtonDeselect() {
		buttonDeselect = createButton(ARROW_LEFT_IMAGE, false, GridData.CENTER);
		buttonDeselect.addListener(SWT.Selection, e -> {
			deselectItem();
		});
	}

	private void createButtonMoveDown() {
		buttonMoveDown = createButton(ARROW_DOWN_IMAGE, false, GridData.CENTER);
		buttonMoveDown.addListener(SWT.Selection, e -> {
			moveDownItem();
		});
	}

	private void createButtonDeselectAll() {
		buttonDeselectAll = createButton(DOUBLE_LEFT_IMAGE, false, GridData.BEGINNING);
		buttonDeselectAll.addListener(SWT.Selection, e -> {
			deselectAll();
		});
	}

	private void createButtonMoveLast() {
		buttonMoveLast = createButton(DOUBLE_DOWN_IMAGE, true, GridData.BEGINNING);
		buttonMoveLast.addListener(SWT.Selection, e -> {
			moveSelectionToLastPosition();
		});
	}

	/**
	 * Create a button
	 *
	 * @param fileName file name of the icon
	 * @param verticalExpand if <code>true</code>, the button will take all the
	 *            available space vertically
	 * @param alignment button alignment
	 * @return a new button
	 */
	private Button createButton(final String fileName, final boolean verticalExpand, final int alignment) {
		final Button button = new Button(this, SWT.PUSH);
		final Image image = SWTGraphicUtil.createImageFromFile("images/" + fileName);
		button.setImage(image);
		button.setLayoutData(new GridData(GridData.CENTER, alignment, false, verticalExpand));
		SWTGraphicUtil.addDisposer(button, image);
		return button;
	}

	/**
	 * Adds the argument to the end of the receiver's list.
	 *
	 * @param item the new item
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the item is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see #add(DLItem,int)
	 */
	public void add(final DLItem item) {
		checkWidget();
		if (item == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		items.add(item);
		redrawTables();
	}

	/**
	 * Adds the argument to the receiver's list at the given zero-relative index.
	 * <p>
	 * Note: To add an item at the end of the list, use the result of calling
	 * <code>getItemCount()</code> as the index or use <code>add(DLItem)</code>.
	 * </p>
	 *
	 * @param item the new item
	 * @param index the index for the item
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the item is null</li>
	 *                <li>ERROR_INVALID_RANGE - if the index is not between 0 and
	 *                the number of elements in the list (inclusive)</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see #add(String)
	 */
	public void add(final DLItem item, final int index) {
		checkWidget();
		if (item == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		if (index < 0 || index >= items.size()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		items.add(index, item);
		redrawTables();
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified when
	 * the user changes the receiver's selection, by sending it one of the messages
	 * defined in the <code>SelectionListener</code> interface.
	 *
	 * @param listener the listener which should be notified
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
		SelectionListenerUtil.addSelectionListener(this, listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will be notified
	 * when the user changes the receiver's selection.
	 *
	 * @param listener the listener which should no longer be notified
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
		SelectionListenerUtil.removeSelectionListener(this, listener);
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified when
	 * the user changes the receiver's selection, by sending it one of the messages
	 * defined in the <code>SelectionChangeListener</code> interface.
	 *
	 * @param listener the listener which should be notified
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
	 * @see SelectionChangeListener
	 * @see #removeSelectionChangeListener
	 * @see SelectionChangeEvent
	 */
	public void addSelectionChangeListener(final SelectionChangeListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		if (selectionChangeListeners == null) {
			selectionChangeListeners = new ArrayList<SelectionChangeListener>();
		}
		selectionChangeListeners.add(listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will be notified
	 * when the user changes the receiver's selection.
	 *
	 * @param listener the listener which should no longer be notified
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
	 * @see SelectionChangeListener
	 * @see #addSelectionChangeListener
	 */
	public void removeSelectionChangeListener(final SelectionChangeListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		if (selectionChangeListeners == null) {
			return;
		}
		selectionChangeListeners.remove(listener);
	}

	/**
	 * Deselects the item at the given zero-relative index in the receiver. If the
	 * item at the index was already deselected, it remains deselected. Indices that
	 * are out of range are ignored.
	 *
	 * @param index the index of the item to deselect
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void deselect(final int index) {
		deselect(index, true);
	}

	/**
	 * Deselects the item at the given zero-relative index in the receiver. If the
	 * item at the index was already deselected, it remains deselected. Indices that
	 * are out of range are ignored.
	 *
	 * @param index the index of the item to deselect
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void deselectDoNotFireEvent(final int index) {
		deselect(index, false);
	}

	/**
	 * Deselects the item at the given zero-relative index in the receiver. If the
	 * item at the index was already deselected, it remains deselected. Indices that
	 * are out of range are ignored.
	 *
	 * @param index the index of the item to deselect
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	private void deselect(final int index, final boolean shouldFireEvents) {
		checkWidget();
		if (index < 0 || index >= items.size()) {
			return;
		}
		final DLItem item = selection.remove(index);
		if (shouldFireEvents) {
			fireSelectionEvent(item);
		}

		final List<DLItem> deselectedItems = new ArrayList<DLItem>();
		item.setLastAction(LAST_ACTION.DESELECTION);
		deselectedItems.add(item);
		if (shouldFireEvents) {
			fireSelectionChangeEvent(deselectedItems);
		}
		redrawTables();
	}

	/**
	 * Deselects the items at the given zero-relative indices in the receiver. If
	 * the item at the given zero-relative index in the receiver is selected, it is
	 * deselected. If the item at the index was not selected, it remains deselected.
	 * Indices that are out of range and duplicate indices are ignored.
	 *
	 * @param indices the array of indices for the items to deselect
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the set of indices is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void deselect(final int[] indices) {
		deselect(indices, true);
	}

	/**
	 * Deselects the items at the given zero-relative indices in the receiver. If
	 * the item at the given zero-relative index in the receiver is selected, it is
	 * deselected. If the item at the index was not selected, it remains deselected.
	 * Indices that are out of range and duplicate indices are ignored.
	 *
	 * @param indices the array of indices for the items to deselect
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the set of indices is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void deselectDoNotFireEvent(final int[] indices) {
		deselect(indices, false);
	}

	/**
	 * Deselects the items at the given zero-relative indices in the receiver. If
	 * the item at the given zero-relative index in the receiver is selected, it is
	 * deselected. If the item at the index was not selected, it remains deselected.
	 * Indices that are out of range and duplicate indices are ignored.
	 *
	 * @param indices the array of indices for the items to deselect
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the set of indices is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	private void deselect(final int[] indices, final boolean shouldFireEvents) {
		checkWidget();
		if (indices == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}

		final List<DLItem> toBeRemoved = new ArrayList<DLItem>();

		for (final int index : indices) {
			if (index < 0 || index >= items.size()) {
				continue;
			}
			toBeRemoved.add(selection.get(index));
		}

		for (final DLItem item : toBeRemoved) {
			selection.remove(item);
			if (shouldFireEvents) {
				fireSelectionEvent(item);
			}
		}
		if (shouldFireEvents) {
			fireSelectionChangeEvent(toBeRemoved);
		}

		toBeRemoved.clear();
		redrawTables();
	}

	/**
	 * Deselects the items at the given zero-relative indices in the receiver. If
	 * the item at the given zero-relative index in the receiver is selected, it is
	 * deselected. If the item at the index was not selected, it remains deselected.
	 * The range of the indices is inclusive. Indices that are out of range are
	 * ignored.
	 *
	 * @param start the start index of the items to deselect
	 * @param end the end index of the items to deselect
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_RANGE - if start is greater than end</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void deselect(final int start, final int end) {
		deselect(start, end, true);
	}

	/**
	 * Deselects the items at the given zero-relative indices in the receiver. If
	 * the item at the given zero-relative index in the receiver is selected, it is
	 * deselected. If the item at the index was not selected, it remains deselected.
	 * The range of the indices is inclusive. Indices that are out of range are
	 * ignored.
	 *
	 * @param start the start index of the items to deselect
	 * @param end the end index of the items to deselect
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_RANGE - if start is greater than end</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void deselectDoNotFireEvent(final int start, final int end) {
		deselect(start, end, false);
	}

	private void deselect(final int start, final int end, final boolean shouldFireEvents) {
		checkWidget();
		if (start > end) {
			SWT.error(SWT.ERROR_INVALID_RANGE);
		}
		final List<DLItem> toBeRemoved = new ArrayList<DLItem>();

		for (int index = start; index <= end; index++) {
			if (index < 0 || index >= items.size()) {
				continue;
			}
			toBeRemoved.add(selection.get(index));
		}

		for (final DLItem item : toBeRemoved) {
			selection.remove(item);
			if (shouldFireEvents) {
				fireSelectionEvent(item);
			}
		}
		if (shouldFireEvents) {
			fireSelectionChangeEvent(toBeRemoved);
		}
		toBeRemoved.clear();
		redrawTables();
	}

	/**
	 * Deselects all selected items in the receiver.
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void deselectAll() {
		deselectAll(true);
	}

	/**
	 * Deselects all selected items in the receiver.
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void deselectAllDoNotFireEvent() {
		deselectAll(false);
	}

	/**
	 * Deselects all selected items in the receiver.
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void deselectAll(final boolean shouldFireEvents) {
		checkWidget();
		items.addAll(selection);

		final List<DLItem> deselectedItems = new ArrayList<DLItem>();
		for (final DLItem item : selection) {
			item.setLastAction(LAST_ACTION.DESELECTION);
			deselectedItems.add(item);
			if (shouldFireEvents) {
				fireSelectionEvent(item);
			}
		}
		fireSelectionChangeEvent(deselectedItems);

		selection.clear();
		redrawTables();
	}

	/**
	 * Returns the item at the given, zero-relative index in the receiver. Throws an
	 * exception if the index is out of range.
	 *
	 * @param index the index of the item to return
	 * @return the item at the given index
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_RANGE - if the index is not between 0 and
	 *                the number of elements in the list minus 1 (inclusive)</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */

	public DLItem getItem(final int index) {
		checkWidget();
		if (index < 0 || index >= items.size()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		return items.get(index);
	}

	/**
	 * Returns the number of items contained in the receiver.
	 *
	 * @return the number of items
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public int getItemCount() {
		checkWidget();
		return items.size();
	}

	/**
	 * Returns a (possibly empty) array of <code>DLItem</code>s which are the items
	 * in the receiver.
	 * <p>
	 * Note: This is not the actual structure used by the receiver to maintain its
	 * list of items, so modifying the array will not affect the receiver.
	 * </p>
	 *
	 * @return the items in the receiver's list
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public DLItem[] getItems() {
		checkWidget();
		return items.toArray(new DLItem[items.size()]);
	}

	/**
	 * Returns a (possibly empty) list of <code>DLItem</code>s which are the items
	 * in the receiver.
	 * <p>
	 * Note: This is not the actual structure used by the receiver to maintain its
	 * list of items, so modifying the array will not affect the receiver.
	 * </p>
	 *
	 * @return the items in the receiver's list
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public List<DLItem> getItemsAsList() {
		checkWidget();
		return new ArrayList<DLItem>(items);
	}

	/**
	 * Returns an array of <code>DLItem</code>s that are currently selected in the
	 * receiver. An empty array indicates that no items are selected.
	 * <p>
	 * Note: This is not the actual structure used by the receiver to maintain its
	 * selection, so modifying the array will not affect the receiver.
	 * </p>
	 *
	 * @return an array representing the selection
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public DLItem[] getSelection() {
		checkWidget();
		return selection.toArray(new DLItem[selection.size()]);
	}

	/**
	 * Returns a list of <code>DLItem</code>s that are currently selected in the
	 * receiver. An empty array indicates that no items are selected.
	 * <p>
	 * Note: This is not the actual structure used by the receiver to maintain its
	 * selection, so modifying the array will not affect the receiver.
	 * </p>
	 *
	 * @return an array representing the selection
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public List<DLItem> getSelectionAsList() {
		checkWidget();
		return new ArrayList<DLItem>(selection);
	}

	/**
	 * Returns the number of selected items contained in the receiver.
	 *
	 * @return the number of selected items
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public int getSelectionCount() {
		checkWidget();
		return selection.size();
	}

	/**
	 * Removes the item from the receiver at the given zero-relative index.
	 *
	 * @param index the index for the item
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_RANGE - if the index is not between 0 and
	 *                the number of elements in the list minus 1 (inclusive)</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void remove(final int index) {
		checkWidget();
		if (index < 0 || index >= items.size()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		items.remove(index);
		redrawTables();
	}

	/**
	 * Removes the items from the receiver at the given zero-relative indices.
	 *
	 * @param indices the array of indices of the items
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_RANGE - if the index is not between 0 and
	 *                the number of elements in the list minus 1 (inclusive)</li>
	 *                <li>ERROR_NULL_ARGUMENT - if the indices array is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void remove(final int[] indices) {
		checkWidget();
		for (final int index : indices) {
			if (index < 0 || index >= items.size()) {
				SWT.error(SWT.ERROR_INVALID_ARGUMENT);
			}
			items.remove(index);
		}
		redrawTables();
	}

	/**
	 * Removes the items from the receiver which are between the given zero-relative
	 * start and end indices (inclusive).
	 *
	 * @param start the start of the range
	 * @param end the end of the range
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_RANGE - if either the start or end are not
	 *                between 0 and the number of elements in the list minus 1
	 *                (inclusive) or if start>end</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void remove(final int start, final int end) {
		checkWidget();
		if (start > end) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		for (int index = start; index < end; index++) {
			if (index < 0 || index >= items.size()) {
				SWT.error(SWT.ERROR_INVALID_ARGUMENT);
			}
			items.remove(index);
		}
		redrawTables();
	}

	/**
	 * Searches the receiver's list starting at the first item until an item is
	 * found that is equal to the argument, and removes that item from the list.
	 *
	 * @param item the item to remove
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the item is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the item is not found in the
	 *                list</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void remove(final DLItem item) {
		checkWidget();
		if (item == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		if (!items.contains(item)) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		items.remove(item);
		redrawTables();
	}

	/**
	 * Removes all of the items from the receiver.
	 * <p>
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void removeAll() {
		checkWidget();
		items.clear();
		redrawTables();
	}

	/**
	 * Selects the item at the given zero-relative index in the receiver's list. If
	 * the item at the index was already selected, it remains selected. Indices that
	 * are out of range are ignored.
	 *
	 * @param index the index of the item to select
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void select(final int index) {
		select(index, true);
	}

	/**
	 * Selects the item at the given zero-relative index in the receiver's list. If
	 * the item at the index was already selected, it remains selected. Indices that
	 * are out of range are ignored.
	 *
	 * @param index the index of the item to select
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void selectDoNotFireEvent(final int index) {
		select(index, false);
	}

	private void select(final int index, final boolean shouldFireEvents) {
		checkWidget();
		if (index < 0 || index >= items.size()) {
			return;
		}
		final List<DLItem> selectedItems = new ArrayList<DLItem>();
		final DLItem item = items.remove(index);
		item.setLastAction(LAST_ACTION.SELECTION);
		selectedItems.add(item);
		selection.add(item);

		if (shouldFireEvents) {
			fireSelectionEvent(item);
			fireSelectionChangeEvent(selectedItems);
		}

		redrawTables();
	}

	/**
	 * Selects the items at the given zero-relative indices in the receiver. The
	 * current selection is not cleared before the new items are selected.
	 * <p>
	 * If the item at a given index is not selected, it is selected. If the item at
	 * a given index was already selected, it remains selected. Indices that are out
	 * of range and duplicate indices are ignored. If the receiver is single-select
	 * and multiple indices are specified, then all indices are ignored.
	 *
	 * @param indices the array of indices for the items to select
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the array of indices is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void select(final int[] indices) {
		select(indices, true);
	}

	/**
	 * Selects the items at the given zero-relative indices in the receiver. The
	 * current selection is not cleared before the new items are selected.
	 * <p>
	 * If the item at a given index is not selected, it is selected. If the item at
	 * a given index was already selected, it remains selected. Indices that are out
	 * of range and duplicate indices are ignored. If the receiver is single-select
	 * and multiple indices are specified, then all indices are ignored.
	 *
	 * @param indices the array of indices for the items to select
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the array of indices is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void selectDoNotFireEvent(final int[] indices) {
		select(indices, false);
	}

	private void select(final int[] indices, final boolean shouldFireEvents) {
		checkWidget();
		if (indices == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}

		final List<DLItem> selectedItems = new ArrayList<DLItem>();
		for (final int index : indices) {
			if (index < 0 || index >= items.size()) {
				continue;
			}
			final DLItem item = items.get(index);
			item.setLastAction(LAST_ACTION.SELECTION);
			selectedItems.add(item);

			selection.add(item);
			if (shouldFireEvents) {
				fireSelectionEvent(item);
			}
		}
		items.removeAll(selectedItems);
		if (shouldFireEvents) {
			fireSelectionChangeEvent(selectedItems);
		}
		redrawTables();
	}

	/**
	 * Selects the items in the range specified by the given zero-relative indices
	 * in the receiver. The range of indices is inclusive. The current selection is
	 * not cleared before the new items are selected.
	 * <p>
	 * If an item in the given range is not selected, it is selected. If an item in
	 * the given range was already selected, it remains selected. Indices that are
	 * out of range are ignored and no items will be selected if start is greater
	 * than end. If the receiver is single-select and there is more than one item in
	 * the given range, then all indices are ignored.
	 *
	 * @param start the start of the range
	 * @param end the end of the range
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see List#setSelection(int,int)
	 */
	public void select(final int start, final int end) {
		select(start, end, true);
	}

	/**
	 * Selects the items in the range specified by the given zero-relative indices
	 * in the receiver. The range of indices is inclusive. The current selection is
	 * not cleared before the new items are selected.
	 * <p>
	 * If an item in the given range is not selected, it is selected. If an item in
	 * the given range was already selected, it remains selected. Indices that are
	 * out of range are ignored and no items will be selected if start is greater
	 * than end. If the receiver is single-select and there is more than one item in
	 * the given range, then all indices are ignored.
	 *
	 * @param start the start of the range
	 * @param end the end of the range
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see List#setSelection(int,int)
	 */
	public void selectDoNotFireEvent(final int start, final int end) {
		select(start, end, false);
	}

	private void select(final int start, final int end, final boolean shouldFireEvents) {
		checkWidget();
		if (start > end) {
			SWT.error(SWT.ERROR_INVALID_RANGE);
		}
		final List<DLItem> selectedItems = new ArrayList<DLItem>();
		for (int index = start; index <= end; index++) {
			if (index < 0 || index >= items.size()) {
				continue;
			}
			final DLItem item = items.get(index);
			item.setLastAction(LAST_ACTION.SELECTION);
			selectedItems.add(item);
			selection.add(item);
			if (shouldFireEvents) {
				fireSelectionEvent(item);
			}
		}
		if (shouldFireEvents) {
			fireSelectionChangeEvent(selectedItems);
		}
		redrawTables();
	}

	/**
	 * Selects all of the items in the receiver.
	 * <p>
	 * If the receiver is single-select, do nothing.
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void selectAll() {
		selectAll(true);
	}

	/**
	 * Selects all of the items in the receiver.
	 * <p>
	 * If the receiver is single-select, do nothing.
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void selectAllDoNotFireEvent() {
		selectAll(false);
	}

	private void selectAll(final boolean shouldFireEvents) {
		checkWidget();
		selection.addAll(items);
		if (shouldFireEvents) {
			for (final DLItem item : items) {
				fireSelectionEvent(item);
			}
		}

		if (shouldFireEvents) {
			final List<DLItem> selectedItems = new ArrayList<DLItem>();
			for (final DLItem item : items) {
				item.setLastAction(LAST_ACTION.SELECTION);
				selectedItems.add(item);
			}
			fireSelectionChangeEvent(selectedItems);
		}
		items.clear();
		redrawTables();
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#setBounds(int, int, int, int)
	 */
	@Override
	public void setBounds(final int x, final int y, final int width, final int height) {
		super.setBounds(x, y, width, height);
		layout(true);
		final boolean itemsContainImage = itemsContainImage();
		final Point itemsTableDefaultSize = itemsTable.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		final Point selectionTableDefaultSize = selectionTable.computeSize(SWT.DEFAULT, SWT.DEFAULT);

		int itemsTableSize = itemsTable.getSize().x;
		if (itemsTableDefaultSize.y > itemsTable.getSize().y) {
			itemsTableSize -= itemsTable.getVerticalBar().getSize().x + 1;
		}

		int selectionTableSize = selectionTable.getSize().x;
		if (selectionTableDefaultSize.y > selectionTable.getSize().y) {
			selectionTableSize -= selectionTable.getVerticalBar().getSize().x;
		}

		if (itemsContainImage) {
			itemsTable.getColumn(0).pack();
			itemsTable.getColumn(1).setWidth(itemsTableSize - itemsTable.getColumn(0).getWidth());

			selectionTable.getColumn(0).pack();
			selectionTable.getColumn(1).setWidth(selectionTableSize - selectionTable.getColumn(0).getWidth());

		} else {
			itemsTable.getColumn(0).setWidth(0);
			itemsTable.getColumn(1).setWidth(itemsTableSize);

			selectionTable.getColumn(0).setWidth(0);
			selectionTable.getColumn(1).setWidth(selectionTableSize);
		}

	}

	/**
	 * @return <code>true</code> if any item contains an image
	 */
	private boolean itemsContainImage() {
		for (final DLItem item : items) {
			if (item.getImage() != null) {
				return true;
			}
		}

		for (final DLItem item : selection) {
			if (item.getImage() != null) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Sets the item in the receiver's list at the given zero-relative index to the
	 * item argument.
	 *
	 * @param index the index for the item
	 * @param item the new item
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_RANGE - if the index is not between 0 and
	 *                the number of elements in the list minus 1 (inclusive)</li>
	 *                <li>ERROR_NULL_ARGUMENT - if the item is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setItem(final int index, final DLItem item) {
		checkWidget();
		if (item == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		if (index < 0 || index >= items.size()) {
			SWT.error(SWT.ERROR_INVALID_RANGE);
		}
		items.set(index, item);
		redrawTables();
	}

	/**
	 * Sets the receiver's items to be the given array of items.
	 *
	 * @param items the array of items
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the items array is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if an item in the items array is
	 *                null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setItems(final DLItem[] items) {
		checkWidget();
		if (items == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}

		final List<DLItem> temp = new ArrayList<DLItem>();
		for (final DLItem item : items) {
			if (item == null) {
				SWT.error(SWT.ERROR_INVALID_ARGUMENT);
			}
			temp.add(item);
		}
		this.items.clear();
		this.items.addAll(temp);
		redrawTables();
	}

	/**
	 * Sets the receiver's items to be the given list of items.
	 *
	 * @param items the list of items
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the items list is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if an item in the items list is
	 *                null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setItems(final List<DLItem> items) {
		checkWidget();
		if (items == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}

		final List<DLItem> unselectedItems = new ArrayList<DLItem>();
		final List<DLItem> selectedItems = new ArrayList<DLItem>();
		for (final DLItem item : items) {
			if (item == null) {
				SWT.error(SWT.ERROR_INVALID_ARGUMENT);
			}
			if (item.getLastAction() == LAST_ACTION.SELECTION) {
				selectedItems.add(item);
			} else {
				unselectedItems.add(item);
			}
		}
		this.items.clear();
		this.items.addAll(unselectedItems);

		this.selection.clear();
		this.selection.addAll(selectedItems);

		redrawTables();
	}

	/**
	 * Redraws all tables that compose this widget
	 */
	private void redrawTables() {
		setRedraw(false);
		redrawTable(itemsTable, false);
		redrawTable(selectionTable, true);
		Rectangle bounds = getBounds();
		this.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
		setRedraw(true);
	}

	/**
	 * Redraw a given table
	 *
	 * @param table table to be redrawned
	 * @param isSelected if <code>true</code>, fill the table with the selection.
	 *            Otherwise, fill the table with the unselected items.
	 */
	private void redrawTable(final Table table, final boolean isSelected) {
		clean(table);
		fillData(table, isSelected);
	}

	/**
	 * Cleans the content of a table
	 *
	 * @param table table to be emptied
	 */
	private void clean(final Table table) {
		if (table == null) {
			return;
		}

		for (final TableItem item : table.getItems()) {
			item.dispose();
		}
	}

	/**
	 * Fill a table with data
	 *
	 * @param table table to be filled
	 * @param listOfData list of data
	 */
	private void fillData(final Table table, final boolean isSelected) {
		List<DLItem> listOfData = isSelected ? selection : items;
		int counter = 0;
		for (final DLItem item : listOfData) {
			final TableItem tableItem = new TableItem(table, SWT.NONE);
			tableItem.setData(item);

			if (item.getBackground() != null) {
				tableItem.setBackground(item.getBackground());
			}

			if (item.getForeground() != null) {
				tableItem.setForeground(item.getForeground());
			}

			if (item.getImage() != null) {
				tableItem.setImage(0, item.getImage());
			}

			if (item.getFont() != null) {
				tableItem.setFont(item.getFont());
			}
			tableItem.setText(1, item.getText());
			if (configuration != null && item.getBackground() == null && counter % 2 == 0) {
				if (isSelected) {
					tableItem.setBackground(configuration.getSelectionOddLinesColor());
				} else {
					tableItem.setBackground(configuration.getItemsOddLinesColor());
				}
			}
			counter++;
		}
	}

	/**
	 * Move the selected item to the first position
	 */
	protected void moveSelectionToFirstPosition() {
		if (selectionTable.getSelectionCount() == 0) {
			return;
		}

		int index = 0;
		for (final TableItem tableItem : selectionTable.getSelection()) {
			final DLItem item = (DLItem) tableItem.getData();
			selection.remove(item);
			selection.add(index++, item);
		}

		redrawTables();
		selectionTable.select(0, index - 1);
		selectionTable.forceFocus();
	}

	/**
	 * Select a given item
	 */
	protected void selectItem() {
		if (itemsTable.getSelectionCount() == 0) {
			return;
		}
		final List<DLItem> selectedItems = new ArrayList<DLItem>();
		for (final TableItem tableItem : itemsTable.getSelection()) {
			final DLItem item = (DLItem) tableItem.getData();
			item.setLastAction(LAST_ACTION.SELECTION);
			selectedItems.add(item);
			selection.add(item);
			items.remove(item);
			fireSelectionEvent(item);
		}
		fireSelectionChangeEvent(selectedItems);

		redrawTables();
	}

	/**
	 * Move the selected item up
	 */
	protected void moveUpItem() {
		if (selectionTable.getSelectionCount() == 0) {
			return;
		}

		for (final int index : selectionTable.getSelectionIndices()) {
			if (index == 0) {
				selectionTable.forceFocus();
				return;
			}
		}

		final int[] newSelection = new int[selectionTable.getSelectionCount()];
		int newSelectionIndex = 0;
		for (final TableItem tableItem : selectionTable.getSelection()) {
			final int position = selection.indexOf(tableItem.getData());
			swap(position, position - 1);
			newSelection[newSelectionIndex++] = position - 1;
		}

		redrawTables();
		selectionTable.select(newSelection);
		selectionTable.forceFocus();
	}

	/**
	 * Deselect a given item
	 */
	protected void deselectItem() {
		if (selectionTable.getSelectionCount() == 0) {
			return;
		}
		final List<DLItem> deselectedItems = new ArrayList<DLItem>();
		for (final TableItem tableItem : selectionTable.getSelection()) {
			final DLItem item = (DLItem) tableItem.getData();
			item.setLastAction(LAST_ACTION.DESELECTION);
			deselectedItems.add(item);
			items.add(item);
			selection.remove(item);
			fireSelectionEvent(item);
		}
		fireSelectionChangeEvent(deselectedItems);
		redrawTables();
	}

	/**
	 * Move the selected item down
	 */
	protected void moveDownItem() {
		if (selectionTable.getSelectionCount() == 0) {
			return;
		}

		for (final int index : selectionTable.getSelectionIndices()) {
			if (index == selectionTable.getItemCount() - 1) {
				selectionTable.forceFocus();
				return;
			}
		}

		final int[] newSelection = new int[selectionTable.getSelectionCount()];
		int newSelectionIndex = 0;
		for (final TableItem tableItem : selectionTable.getSelection()) {
			final int position = selection.indexOf(tableItem.getData());
			swap(position, position + 1);
			newSelection[newSelectionIndex++] = position + 1;
		}

		redrawTables();
		selectionTable.select(newSelection);
		selectionTable.forceFocus();
	}

	/**
	 * Swap 2 items
	 *
	 * @param first position of the first item to swap
	 * @param second position of the second item to swap
	 */
	private void swap(final int first, final int second) {
		final DLItem temp = selection.get(first);
		selection.set(first, selection.get(second));
		selection.set(second, temp);
	}

	/**
	 * Move the selected item to the last position
	 */
	protected void moveSelectionToLastPosition() {
		if (selectionTable.getSelectionCount() == 0) {
			return;
		}

		final int numberOfSelectedElements = selectionTable.getSelectionCount();
		for (final TableItem tableItem : selectionTable.getSelection()) {
			final DLItem item = (DLItem) tableItem.getData();
			selection.remove(item);
			selection.add(item);
		}

		redrawTables();
		final int numberOfElements = selectionTable.getItemCount();
		selectionTable.select(numberOfElements - numberOfSelectedElements, numberOfElements - 1);
		selectionTable.forceFocus();
	}

	/**
	 * Call all selection listeners
	 *
	 * @param item selected item
	 */
	private void fireSelectionEvent(final DLItem item) {
		final Event event = new Event();
		event.button = 1;
		event.display = getDisplay();
		event.item = null;
		event.widget = this;
		event.data = item;

		SelectionListenerUtil.fireSelectionListeners(this, event);
	}

	private void fireSelectionChangeEvent(final List<DLItem> items) {
		if (selectionChangeListeners == null) {
			return;
		}

		final Event event = new Event();
		event.button = 1;
		event.display = getDisplay();
		event.item = null;
		event.widget = this;
		final SelectionChangeEvent selectionChangeEvent = new SelectionChangeEvent(event);
		selectionChangeEvent.setItems(items);

		for (final SelectionChangeListener listener : selectionChangeListeners) {
			listener.widgetSelected(selectionChangeEvent);
		}
	}

	/**
	 * Returns the configuration of the receiver.
	 *
	 * @return the current configuration of the receiver
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public DLConfiguration getConfiguration() {
		checkWidget();
		return configuration;
	}

	/**
	 * Sets the receiver's configuration
	 *
	 * @param configuration the new configuration
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setConfiguration(DLConfiguration configuration) {
		checkWidget();
		this.configuration = configuration;
		applyNewConfiguration();
	}

	private void applyNewConfiguration() {
		try {
			setRedraw(true);
			if (configuration == null) {
				resetConfigurationToDefault();
			} else {
				modifyPanelsColors();
				modifyTextAlignment();
				modifyButtonImages();
				modifyButtonVisibility();
			}
			redrawTables();
		} finally {
			setRedraw(true);
		}
	}

	private void resetConfigurationToDefault() {
		itemsTable.setBackground(null);
		itemsTable.setForeground(null);
		selectionTable.setBackground(null);
		selectionTable.setForeground(null);

		recreateTableColumns(itemsTable, SWT.LEFT);
		recreateTableColumns(selectionTable, SWT.LEFT);

		resetButton(buttonMoveLast, DOUBLE_DOWN_IMAGE);
		resetButton(buttonMoveFirst, DOUBLE_UP_IMAGE);
		resetButton(buttonDeselectAll, DOUBLE_LEFT_IMAGE);
		resetButton(buttonSelectAll, DOUBLE_RIGHT_IMAGE);
		resetButton(buttonMoveDown, ARROW_DOWN_IMAGE);
		resetButton(buttonMoveUp, ARROW_UP_IMAGE);
		resetButton(buttonDeselect, ARROW_LEFT_IMAGE);
		resetButton(buttonSelect, ARROW_RIGHT_IMAGE);
	}

	private void resetButton(Button button, String fileName) {
		final Image image = SWTGraphicUtil.createImageFromFile("images/" + fileName);
		button.setImage(image);
		SWTGraphicUtil.addDisposer(button, image);
		button.setVisible(true);
	}

	private void modifyPanelsColors() {
		if (configuration.getItemsBackgroundColor() != null) {
			itemsTable.setBackground(configuration.getItemsBackgroundColor());
		}
		if (configuration.getItemsForegroundColor() != null) {
			itemsTable.setForeground(configuration.getItemsForegroundColor());
		}
		if (configuration.getSelectionBackgroundColor() != null) {
			selectionTable.setBackground(configuration.getSelectionBackgroundColor());
		}
		if (configuration.getSelectionForegroundColor() != null) {
			selectionTable.setForeground(configuration.getSelectionForegroundColor());
		}
	}

	private void modifyTextAlignment() {
		recreateTableColumns(itemsTable, configuration.getItemsTextAlignment());
		recreateTableColumns(selectionTable, configuration.getSelectionTextAlignment());
	}

	private void recreateTableColumns(Table table, int textAlignment) {
		for (TableColumn tc : table.getColumns()) {
			tc.dispose();
		}
		new TableColumn(table, SWT.CENTER);
		new TableColumn(table, textAlignment);
	}

	private void modifyButtonImages() {
		if (configuration.getDoubleDownImage() != null) {
			buttonMoveLast.setImage(configuration.getDoubleDownImage());
		}
		if (configuration.getDoubleUpImage() != null) {
			buttonMoveFirst.setImage(configuration.getDoubleUpImage());
		}
		if (configuration.getDoubleLeftImage() != null) {
			buttonDeselectAll.setImage(configuration.getDoubleLeftImage());
		}
		if (configuration.getDoubleRightImage() != null) {
			buttonSelectAll.setImage(configuration.getDoubleRightImage());
		}
		if (configuration.getDownImage() != null) {
			buttonMoveDown.setImage(configuration.getDownImage());
		}
		if (configuration.getUpImage() != null) {
			buttonMoveUp.setImage(configuration.getUpImage());
		}
		if (configuration.getLeftImage() != null) {
			buttonDeselect.setImage(configuration.getLeftImage());
		}
		if (configuration.getRightImage() != null) {
			buttonSelect.setImage(configuration.getRightImage());
		}
	}

	private void modifyButtonVisibility() {
		buttonMoveLast.setVisible(configuration.isDoubleDownVisible());
		buttonMoveFirst.setVisible(configuration.isDoubleUpVisible());
		buttonDeselectAll.setVisible(configuration.isDoubleLeftVisible());
		buttonSelectAll.setVisible(configuration.isDoubleRightVisible());
		buttonMoveDown.setVisible(configuration.isDownVisible());
		buttonMoveUp.setVisible(configuration.isUpVisible());
	}

}
