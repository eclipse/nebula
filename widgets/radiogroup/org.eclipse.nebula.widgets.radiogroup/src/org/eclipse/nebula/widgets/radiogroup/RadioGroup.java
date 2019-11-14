/*******************************************************************************
 * Copyright (c) 2008 Matthew Hall
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors :
 * Matthew Hall <matthall@woodcraftmill.com> - initial API and implementation
 * Laurent Caron - Fix bug 430114
 *******************************************************************************/

package org.eclipse.nebula.widgets.radiogroup;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.swt.widgets.Widget;

/**
 * <p>
 * SWT Widget that presents a group of radio buttons.
 * </p>
 * <p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>BORDER, FLAT, HORIZONTAL, VERTICAL, LEFT, RIGHT, CENTER, LEFT_TO_RIGHT,
 * RIGHT_TO_LEFT</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles HORIZONTAL and VERTICAL may be specified.
 * </p>
 * <p>
 * Note: Only one of the styles LEFT, RIGHT, and CENTER may be specified.
 * </p>
 * <p>
 * Note: Only one of the styles LEFT_TO_RIGHT and RIGHT_TO_LEFT may be
 * specified.
 * </p>
 * <p>
 * <dl>
 * <dt><b>Events:</b></dt>
 * <dd>Selection, DefaultSelection</dd>
 * </dl>
 * </p>
 * <p>
 * NOTE: THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT. THIS IS A
 * PRE-RELEASE ALPHA VERSION. USERS SHOULD EXPECT API CHANGES IN FUTURE
 * VERSIONS.
 * </p>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 *
 * @noextend This class is not intended to be subclassed.
 * @author Matthew Hall <matthall@woodcraftmill.com>
 */
public class RadioGroup extends Composite {
	private static int checkButtonStyle(int style) {
		int result = 0;
		if ((style & SWT.FLAT) != 0) {
			result |= SWT.FLAT;
		}
		if ((style & SWT.LEFT) != 0) {
			result |= SWT.LEFT;
		} else if ((style & SWT.CENTER) != 0) {
			result |= SWT.CENTER;
		} else if ((style & SWT.RIGHT) != 0) {
			result |= SWT.RIGHT;
		} else {
			result |= SWT.LEFT;
		}
		return result;
	}

	private static int checkCompositeStyle(int style) {
		int result = style & SWT.BORDER;
		if ((style & SWT.LEFT_TO_RIGHT) != 0) {
			result |= SWT.LEFT_TO_RIGHT;
		} else if ((style & SWT.RIGHT_TO_LEFT) != 0) {
			result |= SWT.RIGHT_TO_LEFT;
		}
		return result;
	}

	private final int cardinality;

	private final int buttonStyle;

	private RadioItem[] items = {};

	private RadioItem selection = null;

	/**
	 * Constructs a new instance of this class given its parent
	 * and a style value describing its behavior and appearance.
	 * <p>
	 * The style value is either one of the style constants defined in
	 * class <code>SWT</code> which is applicable to instances of this
	 * class, or must be built by <em>bitwise OR</em>'ing together
	 * (that is, using the <code>int</code> "|" operator) two or more
	 * of those <code>SWT</code> style constants. The class description
	 * lists the style constants that are applicable to the class.
	 * Style bits are also inherited from superclasses.
	 * </p>
	 *
	 * @param parent a widget which will be the parent of the new instance (cannot be null)
	 * @param style the style of widget to construct
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
	 *                </ul>
	 *
	 * @see SWT#NO_BACKGROUND
	 * @see SWT#NO_FOCUS
	 * @see SWT#NO_MERGE_PAINTS
	 * @see SWT#NO_REDRAW_RESIZE
	 * @see SWT#NO_RADIO_GROUP
	 * @see SWT#EMBEDDED
	 * @see SWT#DOUBLE_BUFFERED
	 * @see Widget#getStyle
	 */
	public RadioGroup(Composite parent, int style) {
		super(parent, checkCompositeStyle(style));
		cardinality = checkCardinality(style);
		buttonStyle = checkButtonStyle(style);

		super.setLayout(new RowLayout(cardinality));

		setBackgroundMode(SWT.INHERIT_DEFAULT);

		addListener(SWT.Dispose, event -> {
			handleDispose(event);
		});

	}

	void addItem(RadioItem item, int position) {
		position = checkAddPosition(position);
		final RadioItem[] newItems = new RadioItem[items == null ? 1 : items.length + 1];

		if (items == null) {
			items = new RadioItem[] { item };
		} else {
			System.arraycopy(items, 0, newItems, 0, position);
			newItems[position] = item;
			System.arraycopy(items, position, newItems, position + 1, items.length - position);
			items = newItems;
		}
	}

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when the control is selected by the user, by sending
	 * it one of the messages defined in the <code>SelectionListener</code>
	 * interface.
	 * <p>
	 * <code>widgetSelected</code> is called when the control is selected by the user.
	 * <code>widgetDefaultSelected</code> is not called.
	 * </p>
	 * <p>
	 * When the <code>SWT.RADIO</code> style bit is set, the <code>widgetSelected</code> method is
	 * also called when the receiver loses selection because another item in the same radio group
	 * was selected by the user. During <code>widgetSelected</code> the application can use
	 * <code>getSelection()</code> to determine the current selected state of the receiver.
	 * </p>
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
	 * @see SelectionListener
	 * @see #removeSelectionListener
	 * @see SelectionEvent
	 */
	public void addSelectionListener(SelectionListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		final TypedListener typedListener = new TypedListener(listener);
		addListener(SWT.Selection, typedListener);
		addListener(SWT.DefaultSelection, typedListener);
	}

	private int checkAddPosition(int position) {
		if (position == -1) {
			position = getItemCount();
		} else if (position < 0 || position > getItemCount()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		return position;
	}

	private int checkCardinality(int style) {
		if ((style & SWT.VERTICAL) != 0) {
			return SWT.VERTICAL;
		}
		return SWT.HORIZONTAL;
	}

	private void checkExistingPosition(int position) {
		if (position < 0 || position >= getItemCount()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
	}

	/**
	 * Reset the button at position <code>position</code> (reset content to empty string, reset font, images...)
	 *
	 * @param listener the listener which should be notified
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void clear(int position) {
		checkWidget();
		checkExistingPosition(position);
		items[position].clear();
	}

	private int computeButtonStyle(int itemStyle) {
		int buttonStyle = SWT.RADIO | this.buttonStyle;

		final int itemStyleMask = SWT.LEFT | SWT.CENTER | SWT.RIGHT;
		if ((itemStyle & itemStyleMask) != 0) {
			buttonStyle &= ~itemStyleMask;
			buttonStyle |= itemStyle;
		}

		return buttonStyle;
	}

	Button createButton(int itemStyle, int position) {
		// Check add position (which may throw exception) before creating button
		position = checkAddPosition(position);

		final Button button = new Button(this, computeButtonStyle(itemStyle));

		if (position < items.length) {
			button.moveAbove(items[position].getButton());
		}

		layout(new Control[] { button });

		return button;
	}

	/**
	 * Deselects all selected items in the receiver's list.
	 * <p>
	 * Note: To clear the selection in the receiver's text field,
	 * use <code>clearSelection()</code>.
	 * </p>
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 */
	public void deselectAll() {
		checkWidget();
		setSelection(null);
	}

	/**
	 * Returns the number of items contained in the receiver's list.
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
		if (items == null) {
			return 0;
		}
		return items.length;
	}

	/**
	 * Returns a (possibly empty) array of <code>RadioItem</code>s which are
	 * the items in the receiver's list.
	 * <p>
	 * Note: This is not the actual structure used by the receiver
	 * to maintain its list of items, so modifying the array will
	 * not affect the receiver.
	 * </p>
	 *
	 * @return the items in the receiver's list
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public RadioItem[] getItems() {
		checkWidget();
		if (items == null) {
			return new RadioItem[0];
		}
		final RadioItem[] result = new RadioItem[items.length];
		System.arraycopy(items, 0, result, 0, items.length);
		return result;
	}

	/**
	 * Returns the the item which is currently selected in the receiver's list, or <code>null</code> if no item is selected.
	 *
	 * @return the selected item
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public RadioItem getSelection() {
		checkWidget();
		return selection;
	}

	/**
	 * Returns the zero-relative index of the item which is currently
	 * selected in the receiver's list, or -1 if no item is selected.
	 *
	 * @return the index of the selected item
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public int getSelectionIndex() {
		checkWidget();
		return indexOf(selection);
	}

	/**
	 * @see org.eclipse.swt.widgets.Widget#getStyle()
	 */
	@Override
	public int getStyle() {
		return super.getStyle() | buttonStyle | cardinality;
	}

	private void handleDispose(Event event) {
		final RadioItem[] items = getItems();
		for (int i = 0; i < items.length; i++) {
			items[i].dispose();
		}
	}

	/**
	 * Searches the receiver's list starting at the first item
	 * (index 0) until an item is found that is equal to the
	 * argument, and returns the index of that item. If no item
	 * is found, returns -1.
	 *
	 * @param item the search item
	 * @return the index of the item
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public int indexOf(RadioItem item) {
		checkWidget();
		if (items == null) {
			return -1;
		}
		if (item == null) {
			return -1;
		}
		for (int i = 0; i < items.length; i++) {
			if (items[i] == item) {
				return i;
			}
		}
		return -1;
	}

	void itemSelected(RadioItem item) {
		final RadioItem oldSelection = selection;
		final RadioItem newSelection = item.isSelected() ? item : null;
		if (oldSelection == newSelection) {
			return;
		}

		selection = newSelection;

		final Event event = new Event();
		event.item = selection;
		event.index = indexOf(selection);

		notifyListeners(SWT.Selection, event);
	}

	/**
	 * Removes the item from the receiver's list at the given
	 * zero-relative index.
	 *
	 * @param index the index for the item
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
	public void remove(int index) {
		checkWidget();
		checkExistingPosition(index);
		items[index].dispose();
	}

	/**
	 * Removes the items from the receiver's list which are
	 * between the given zero-relative start and end
	 * indices (inclusive).
	 *
	 * @param start the start of the range
	 * @param end the end of the range
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_RANGE - if either the start or end are not between 0 and the number of elements in the list minus 1 (inclusive)</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void remove(int start, int end) {
		checkWidget();
		if (start > end) {
			return;
		}
		if (start < 0 || end >= items.length) {
			SWT.error(SWT.ERROR_INVALID_RANGE);
		}

		setLayoutDeferred(true);
		try {
			final Item[] items = this.items.clone();
			for (int i = start; i <= end; i++) {
				items[i].dispose();
			}
		} finally {
			setLayoutDeferred(false);
		}
	}

	/**
	 * Searches the receiver's list starting at the first item
	 * until an item is found that is equal to the argument,
	 * and removes that item from the list.
	 *
	 * @param item the item to remove
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the string is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the string is not found in the list</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void remove(RadioItem item) {
		checkWidget();
		if (item.isDisposed() || item.getParent() != this) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		item.dispose();
	}

	/**
	 * Removes all of the items from the receiver's list and clear the
	 * contents of receiver's text field.
	 * <p>
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void removeAll() {
		checkWidget();
		remove(0, items.length - 1);
	}

	void removeItem(RadioItem item) {
		checkWidget();

		final int position = indexOf(item);
		if (position != -1) {
			final RadioItem[] newItems = new RadioItem[items.length - 1];
			System.arraycopy(items, 0, newItems, 0, position);
			System.arraycopy(items, position + 1, newItems, position, newItems.length - position);
			items = newItems;
		}

		if (selection == item) {
			selection = null;
			notifyListeners(SWT.Selection, null);
		}
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when the user changes the receiver's selection.
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
	 * @see SelectionListener
	 * @see #addSelectionListener
	 */
	public void removeSelectionListener(SelectionListener listener) {
		checkWidget();
		removeListener(SWT.Selection, listener);
		removeListener(SWT.DefaultSelection, listener);
	}

	/**
	 * Selects the item at the given zero-relative index in the receiver's
	 * list. If the item at the index was already selected, it remains
	 * selected. Indices that are out of range are ignored.
	 *
	 * @param index the index of the item to select
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void select(int index) {
		checkWidget();
		checkExistingPosition(index);
		setSelection(items[index]);
	}

	/**
	 * @see org.eclipse.swt.widgets.Composite#setLayout(org.eclipse.swt.widgets.Layout)
	 */
	@Override
	public void setLayout(Layout layout) {
		checkWidget();
		return;
	}

	/**
	 * Sets the selection in the receiver
	 *
	 * @param item new selection
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the item does not exist in this widget</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setSelection(RadioItem item) {
		checkWidget();
		if (selection == item) {
			return;
		}
		if (selection != null) {
			selection.deselect();
		}
		if (item != null) {
			if (item.getParent() != this) {
				SWT.error(SWT.ERROR_INVALID_ARGUMENT);
			}
			item.select();
		}
	}

}
