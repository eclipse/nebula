/*******************************************************************************
 * Copyright (c) 2008 Matthew Hall
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors :
 *    Matthew Hall <matthall@woodcraftmill.com> - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.radiogroup;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TypedListener;

/**
 * <p>
 * SWT Widget that presents a group of radio buttons. This widget require
 * jdk-1.4+
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
	private final int cardinality;
	private final int buttonStyle;

	private RadioItem[] items = {};

	private RadioItem selection = null;

	public RadioGroup(Composite parent, int style) {
		super(parent, checkCompositeStyle(style));
		this.cardinality = checkCardinality(style);
		this.buttonStyle = checkButtonStyle(style);

		super.setLayout(new RowLayout(cardinality));

		setBackgroundMode(SWT.INHERIT_DEFAULT);

		addListener(SWT.Dispose, new Listener() {
			public void handleEvent(Event event) {
				handleDispose(event);
			}
		});
	}

	private static int checkCompositeStyle(int style) {
		int result = style & SWT.BORDER;
		if ((style & SWT.LEFT_TO_RIGHT) != 0)
			result |= SWT.LEFT_TO_RIGHT;
		else if ((style & SWT.RIGHT_TO_LEFT) != 0)
			result |= SWT.RIGHT_TO_LEFT;
		return result;
	}

	private int checkCardinality(int style) {
		if ((style & SWT.VERTICAL) != 0)
			return SWT.VERTICAL;
		return SWT.HORIZONTAL;
	}

	private static int checkButtonStyle(int style) {
		int result = 0;
		if ((style & SWT.FLAT) != 0)
			result |= SWT.FLAT;
		if ((style & SWT.LEFT) != 0)
			result |= SWT.LEFT;
		else if ((style & SWT.CENTER) != 0)
			result |= SWT.CENTER;
		else if ((style & SWT.RIGHT) != 0)
			result |= SWT.RIGHT;
		else
			result |= SWT.LEFT;
		return result;
	}

	private void handleDispose(Event event) {
		RadioItem[] items = getItems();
		for (int i = 0; i < items.length; i++)
			items[i].dispose();
	}

	public int getStyle() {
		return super.getStyle() | buttonStyle | cardinality;
	}

	// TODO Cannot override getChildren method to hide children until we are
	// doing manual layout. Currently we delegate to RowLayout which depends on
	// getChildren.

	// public Control[] getChildren() {
	// checkWidget();
	// return new Control[0];
	// }

	/**
	 * Sets the layout which is associated with the receiver to be the argument
	 * which may be null.
	 * <p>
	 * Note: No Layout can be set on this Control because it already manages the
	 * size and position of its children.
	 * </p>
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
		checkWidget();
		return;
	}

	public void clear(int position) {
		checkWidget();
		checkExistingPosition(position);
		items[position].clear();
	}

	private void checkExistingPosition(int position) {
		if (position < 0 || position >= getItemCount())
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
	}

	public void remove(RadioItem item) {
		checkWidget();
		if (item.isDisposed() || item.getParent() != this)
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		item.dispose();
	}

	public void remove(int position) {
		checkWidget();
		checkExistingPosition(position);
		items[position].dispose();
	}

	public void remove(int start, int end) {
		checkWidget();
		if (start > end)
			return;
		if (start < 0 || end >= items.length)
			SWT.error(SWT.ERROR_INVALID_RANGE);

		setLayoutDeferred(true);
		try {
			Item[] items = (Item[]) this.items.clone();
			for (int i = start; i <= end; i++) {
				items[i].dispose();
			}
		} finally {
			setLayoutDeferred(false);
		}
	}

	public void removeAll() {
		checkWidget();
		remove(0, items.length - 1);
	}

	/**
	 * Returns the number of items in the receiver.
	 * 
	 * @return the number of items in the receiver.
	 */
	public int getItemCount() {
		checkWidget();
		if (items == null)
			return 0;
		return items.length;
	}

	public void addSelectionListener(SelectionListener listener) {
		checkWidget();
		if (listener == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		TypedListener typedListener = new TypedListener(listener);
		addListener(SWT.Selection, typedListener);
		addListener(SWT.DefaultSelection, typedListener);
	}

	public void removeSelectionListener(SelectionListener listener) {
		checkWidget();
		removeListener(SWT.Selection, listener);
		removeListener(SWT.DefaultSelection, listener);
	}

	public RadioItem[] getItems() {
		checkWidget();
		if (items == null)
			return new RadioItem[0];
		RadioItem[] result = new RadioItem[items.length];
		System.arraycopy(items, 0, result, 0, items.length);
		return result;
	}

	public int indexOf(RadioItem item) {
		checkWidget();
		if (items == null)
			return -1;
		if (item == null)
			return -1;
		for (int i = 0; i < items.length; i++) {
			if (items[i] == item)
				return i;
		}
		return -1;
	}

	public RadioItem getSelection() {
		checkWidget();
		return selection;
	}

	public int getSelectionIndex() {
		checkWidget();
		return indexOf(selection);
	}

	public void setSelection(RadioItem item) {
		checkWidget();
		if (selection == item)
			return;
		if (selection != null)
			selection.deselect();
		if (item != null) {
			if (item.getParent() != this)
				SWT.error(SWT.ERROR_INVALID_ARGUMENT);
			item.select();
		}
	}

	public void select(int index) {
		checkWidget();
		checkExistingPosition(index);
		setSelection(items[index]);
	}

	public void deselectAll() {
		checkWidget();
		setSelection(null);
	}

	Button createButton(int itemStyle, int position) {
		// Check add position (which may throw exception) before creating button
		position = checkAddPosition(position);

		Button button = new Button(this, computeButtonStyle(itemStyle));

		if (position < items.length)
			button.moveAbove(items[position].getButton());

		layout(new Control[] { button });

		return button;
	}

	private int computeButtonStyle(int itemStyle) {
		int buttonStyle = SWT.RADIO | this.buttonStyle;

		int itemStyleMask = SWT.LEFT | SWT.CENTER | SWT.RIGHT;
		if ((itemStyle & itemStyleMask) != 0) {
			buttonStyle &= ~itemStyleMask;
			buttonStyle |= itemStyle;
		}

		return buttonStyle;
	}

	void addItem(RadioItem item, int position) {
		position = checkAddPosition(position);
		RadioItem[] newItems = new RadioItem[items == null ? 1
				: items.length + 1];

		if (items == null) {
			items = new RadioItem[] { item };
		} else {
			System.arraycopy(items, 0, newItems, 0, position);
			newItems[position] = item;
			System.arraycopy(items, position, newItems, position + 1,
					items.length - position);
			items = newItems;
		}
	}

	private int checkAddPosition(int position) {
		if (position == -1)
			position = getItemCount();
		else if (position < 0 || position > getItemCount())
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		return position;
	}

	void removeItem(RadioItem item) {
		checkWidget();

		int position = indexOf(item);
		if (position != -1) {
			RadioItem[] newItems = new RadioItem[items.length - 1];
			System.arraycopy(items, 0, newItems, 0, position);
			System.arraycopy(items, position + 1, newItems, position,
					newItems.length - position);
			items = newItems;
		}

		if (selection == item) {
			selection = null;
			notifyListeners(SWT.Selection, null);
		}
	}

	void itemSelected(RadioItem item) {
		RadioItem oldSelection = selection;
		RadioItem newSelection = item.isSelected() ? item : null;
		if (oldSelection == newSelection)
			return;

		selection = newSelection;

		Event event = new Event();
		event.item = selection;
		event.index = indexOf(selection);

		notifyListeners(SWT.Selection, event);
	}
}
