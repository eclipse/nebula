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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.TypedListener;

/**
 * <p>
 * SWT Widget that presents a group of radio buttons. This widget require
 * jdk-1.4+
 * </p>
 * <p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>BORDER, HORIZONTAL, VERTICAL, LEFT, RIGHT, CENTER, LEFT_TO_RIGHT,
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
 * </P>
 * 
 * @author Matthew Hall <matthall@woodcraftmill.com>
 */
public class RadioGroup extends Composite {
	RadioItem[] items = {};

	private RadioItem selectionItem = null;
	private int selectionIndex = -1;

	/**
	 * Scrolling direction flag. True : V_SCROLL, false : H_SCROLL
	 */
	private boolean vertical = true;

	int style;
	int buttonStyle;

	private RowLayout layout;

	public RadioGroup(Composite parent, int style) {
		super(parent, checkCompositeStyle(style));

		vertical = (style & SWT.VERTICAL) != 0;
		layout = new RowLayout(vertical ? SWT.VERTICAL : SWT.HORIZONTAL);
		super.setLayout(layout);

		buttonStyle = checkButtonStyle(style);
		style = super.getStyle() | buttonStyle
				| (vertical ? SWT.VERTICAL : SWT.HORIZONTAL);

		setBackgroundMode(SWT.INHERIT_DEFAULT);
	}

	public int getStyle() {
		return style;
	}

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

	private static int checkCompositeStyle(int style) {
		// V_SCROLL == VERTICAL, H_SCROLL == HORIZONTAL
		int result = style & (SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		if ((style & SWT.LEFT_TO_RIGHT) != 0)
			result |= SWT.LEFT_TO_RIGHT;
		else if ((style & SWT.RIGHT_TO_LEFT) != 0)
			result |= SWT.RIGHT_TO_LEFT;
		return result;
	}

	private static int checkButtonStyle(int style) {
		int result = SWT.RADIO;
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

	void addItem(RadioItem item, int position) {
		if (position == -1)
			position = getItemCount();
		else if (position < 0 || position > getItemCount())
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		RadioItem[] newItems = new RadioItem[items == null ? 1
				: items.length + 1];

		if (position == -1)
			position = newItems.length - 1;

		if (items == null) {
			items = new RadioItem[] { item };
		} else {
			if (items.length > position)
				item.getButton().moveAbove(items[position].getButton());

			System.arraycopy(items, 0, newItems, 0, position);
			newItems[position] = item;
			System.arraycopy(items, position, newItems, position + 1,
					items.length - position);
			items = newItems;
		}

		layout(new Control[] { item.getButton() });

		if (selectionIndex >= position)
			selectionIndex++;
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

		if (selectionIndex == position) {
			selectionItem = null;
			selectionIndex = 0;
		} else if (selectionIndex > position) {
			selectionIndex--;
		}
	}

	void itemSelected(RadioItem item, Event event) {
		if (item.getButton().getSelection()) {
			selectionItem = item;
			selectionIndex = indexOf(item);
		} else {
			selectionItem = null;
			selectionIndex = -1;
		}
		notifyListeners(SWT.Selection, null);
	}
	
	public void clear(int i) {
		checkWidget();
		RadioItem item = items[i];
		item.setText("");
		item.setImage(null);
		item.setFont(getFont());
		item.setForeground(getForeground());
		item.setBackground(getBackground());
	}

	public void remove(RadioItem item) {
		checkWidget();
		if (item.isDisposed() || item.getParent() != this)
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		item.dispose();
	}

	public void remove(int position) {
		checkWidget();
		if (position < 0 || position >= getItemCount())
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
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
		return items;
	}

	public int indexOf(RadioItem item) {
		checkWidget();
		if (items == null)
			return -1;
		for (int i = 0; i < items.length; i++) {
			if (items[i] == item)
				return i;
		}
		return -1;
	}

	public RadioItem getSelection() {
		checkWidget();
		return selectionItem;
	}

	public int getSelectionIndex() {
		checkWidget();
		return selectionIndex;
	}

	public void setSelection(RadioItem item) {
		checkWidget();
		if (item == null) {
			deselectAll();
			return;
		}
		if (item.getParent() != this)
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		item.getButton().setSelection(true);
	}

	public void deselectAll() {
		checkWidget();
		if (selectionItem != null) {
			selectionItem.getButton().setSelection(false);
			selectionItem = null;
			selectionIndex = -1;
		}
	}

	public void select(int index) {
		checkWidget();
		if (index < 0 || index >= getItemCount())
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		items[index].getButton().setSelection(true);
	}

	public void reveal(RadioItem item) {
		checkWidget();
		// TODO
	}
}
