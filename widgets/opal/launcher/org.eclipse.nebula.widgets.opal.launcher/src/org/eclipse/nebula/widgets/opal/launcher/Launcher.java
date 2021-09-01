/*******************************************************************************
 * Copyright (c) 2011-2021 Laurent CARON
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Laurent CARON (laurent.caron at gmail dot com) - Initial implementation and API
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.launcher;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.nebula.widgets.opal.commons.SelectionListenerUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

/**
 * Instances of this class are a launcher composed of buttons. When one clicks
 * on the button, an animation is started and a selection event is fired
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection</dd>
 * </dl>
 */
public class Launcher extends Composite {

	private final List<LauncherItem> items;
	private boolean needRedraw;
	private int selection = -1;
	private Color itemBackgroundColor, selectedItemBackgroundColor;
	private int numberOfColumns = -1;
	private boolean singleClickSelection = false;

	/**
	 * Constructs a new instance of this class given its parent and a style
	 * value describing its behavior and appearance.
	 * <p>
	 * The style value is either one of the style constants defined in class
	 * <code>SWT</code> which is applicable to instances of this class, or must
	 * be built by <em>bitwise OR</em>'ing together (that is, using the
	 * <code>int</code> "|" operator) two or more of those <code>SWT</code>
	 * style constants. The class description lists the style constants that are
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
	public Launcher(final Composite parent, final int style) {
		super(parent, style | SWT.BORDER);
		items = new ArrayList<LauncherItem>();
		needRedraw = true;
		setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));

		addListener(SWT.Resize, event -> {
			drawLauncher();
		});

		addListener(SWT.KeyUp, event -> {
			handleKeyPressedEvent(event);
		});

		final Font original = super.getFont();
		final Font defaultFont = new Font(getDisplay(), original.getFontData()[0].getName(), 18, SWT.BOLD);
		setFont(defaultFont);
		SWTGraphicUtil.addDisposer(this, defaultFont);
	}

	/**
	 * Add an item to the launcher
	 *
	 * @param title text associated to this item
	 * @param image image associated to this item
	 */
	public void addItem(final String title, final Image image) {
		checkWidget();
		items.add(new LauncherItem(title, image));
		needRedraw = true;
	}

	private void addListenerToLabel(final LauncherLabel label) {
		label.addListener(SWT.KeyUp, event -> {
			handleKeyPressedEvent(event);
		});

		label.addListener(SWT.MouseUp, event -> {
			handleClickEvent(event);
		});

		label.addListener(SWT.MouseDoubleClick, event -> {
			handleDoubleClickEvent(event);
		});
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified
	 * when the control is selected by the user, by sending it one of the
	 * messages defined in the <code>SelectionListener</code> interface.
	 * <p>
	 * <code>widgetSelected</code> is called when the control is selected by the
	 * user. <code>widgetDefaultSelected</code> is not called.
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
		SelectionListenerUtil.addSelectionListener(this, listener);
	}

	/**
	 * Change the background color of a given button
	 *
	 * @param index index of the button
	 * @param isSelected by default, if <code>true</code>, the background is the lightshadow. Otherwise, the background color is white
	 */
	private void changeColor(final int index, final boolean isSelected) {
		if (index != -1 && items.get(index).label != null) {
			final Color selectedItemColor = selectedItemBackgroundColor == null ? getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW) : selectedItemBackgroundColor;
			final Color itemColor = itemBackgroundColor == null ? getDisplay().getSystemColor(SWT.COLOR_WHITE) : itemBackgroundColor;
			items.get(index).label.setBackground(isSelected ? selectedItemColor : itemColor);
		}
	}

	/**
	 * Create the buttons that will compose the launcher
	 */
	private void createButtons() {
		if (numberOfColumns == -1) {
			numberOfColumns = items.size() / 2;
		}
		final GridLayout gridLayout = new GridLayout(numberOfColumns, true);
		gridLayout.horizontalSpacing = gridLayout.verticalSpacing = 0;
		setLayout(gridLayout);
		Point widthHeightHint = new Point(0, 0);

		for (final LauncherItem item : items) {
			createItem(item);
			Point itemSize = item.label.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			widthHeightHint.x = Math.max(widthHeightHint.x, itemSize.x);
			widthHeightHint.y = Math.max(widthHeightHint.y, itemSize.y);
		}

		for (final LauncherItem item : items) {
			GridData gd = (GridData) item.label.getLayoutData();
			gd.widthHint = widthHeightHint.x;
			gd.heightHint = widthHeightHint.y;
		}

	}

	private void createItem(final LauncherItem item) {
		final LauncherLabel label = createLauncherLabel(item);
		addListenerToLabel(label);
	}

	private LauncherLabel createLauncherLabel(final LauncherItem item) {
		final LauncherLabel label = new LauncherLabel(this, SWT.NONE);
		label.setText(item.title);
		label.setImage(item.image);
		label.setBackground(itemBackgroundColor == null ? getDisplay().getSystemColor(SWT.COLOR_WHITE) : //
				itemBackgroundColor);
		final GridData gd = new GridData(GridData.FILL, GridData.FILL, false, false);
		label.setLayoutData(gd);

		item.label = label;
		label.setFont(getFont());
		return label;
	}

	/**
	 * Dispose the content before a redraw
	 */
	private void disposePreviousContent() {
		for (final Control c : getChildren()) {
			c.dispose();
		}
	}

	/**
	 * Draw the launcher
	 */
	private void drawLauncher() {
		if (!needRedraw) {
			return;
		}

		disposePreviousContent();
		createButtons();
		pack();

		needRedraw = false;
	}

	/**
	 * Return the selected button
	 *
	 * @return the index of the selected button
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 *
	 */
	public int getSelection() {
		checkWidget();
		return selection;
	}

	/**
	 * Code executed when one clicks on the button
	 *
	 * @param event Event
	 */
	private void handleClickEvent(final Event event) {
		for (int i = 0; i < items.size(); i++) {
			final LauncherItem item = items.get(i);
			if (item.label != null && item.label.equals(event.widget)) {
				if (selection != i) {
					changeColor(selection, false);
					selection = i;
					changeColor(selection, true);
				}
				if (singleClickSelection) {
					startAnimation(i, event);
				}
				return;
			}
		}
	}

	/**
	 * Code executed when one double-clicks on a button
	 *
	 * @param event Event
	 */
	private void handleDoubleClickEvent(final Event event) {
		for (int i = 0; i < items.size(); i++) {
			final LauncherItem item = items.get(i);
			if (item.label != null && item.label.equals(event.widget)) {
				if (selection != i) {
					changeColor(selection, false);
					selection = i;
					changeColor(selection, true);
				}
				if (!singleClickSelection) {
					startAnimation(i, event);
				}
				return;
			}
		}
	}

	/**
	 * Code executed when a key is pressed
	 *
	 * @param event Event
	 */
	private void handleKeyPressedEvent(final Event event) {
		switch (event.keyCode) {
			case SWT.ARROW_LEFT:
				if (selection == -1) {
					selection = 0;
					changeColor(selection, true);
					return;
				}

				if (selection % 2 != 0) {
					changeColor(selection, false);
					selection--;
					changeColor(selection, true);
				}
				break;
			case SWT.ARROW_UP:
				if (selection == -1) {
					selection = 0;
					changeColor(selection, true);
					return;
				}
				if (selection >= 2) {
					changeColor(selection, false);
					selection -= 2;
					changeColor(selection, true);
				}
				break;
			case SWT.ARROW_RIGHT:
				if (selection == -1) {
					selection = 0;
					changeColor(selection, true);
					return;
				}
				if (selection % 2 == 0) {
					changeColor(selection, false);
					selection++;
					changeColor(selection, true);
				}
				break;
			case SWT.ARROW_DOWN:
				if (selection == -1) {
					selection = 0;
					changeColor(selection, true);
					return;
				}
				if (selection <= items.size() - 2) {
					changeColor(selection, false);
					selection += 2;
					changeColor(selection, true);
				}
				break;
			case SWT.HOME:
				changeColor(selection, false);
				selection = 0;
				changeColor(selection, true);
				break;
			case SWT.END:
				changeColor(selection, false);
				selection = items.size() - 1;
				changeColor(selection, true);
				break;
		}

	}

	/**
	 * Removes the listener from the collection of listeners who will be
	 * notified when the control is selected by the user.
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
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		SelectionListenerUtil.removeSelectionListener(this, listener);
	}

	/**
	 * Start the animation for a given button
	 *
	 * @param index index of the selected button
	 * @param event event (propagated to the selection listeners)
	 */
	private void startAnimation(final int index, final Event event) {
		final LauncherLabel label = items.get(index).label;
		getDisplay().timerExec(0, new Runnable() {
			@Override
			public void run() {
				if (label.incrementAnimation()) {
					getDisplay().timerExec(20, this);
				} else {
					SelectionListenerUtil.fireSelectionListeners(Launcher.this, event);
				}
			}
		});

	}

	/**
	 * @return the background color of the item, <code>null</code> if default value
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 *
	 */
	public Color getItemBackgroundColor() {
		checkWidget();
		return itemBackgroundColor;
	}

	/**
	 * Set the background color for the items. If <code>null</code>, the default value (white) is used
	 * 
	 * @param color the new color to set
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 *
	 */
	public void setItemBackgroundColor(Color color) {
		checkWidget();
		this.itemBackgroundColor = color;
	}

	/**
	 * @return the background color of the item when it is selected, <code>null</code> if default value
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 *
	 */
	public Color getSelectedItemBackgroundColor() {
		checkWidget();
		return selectedItemBackgroundColor;
	}

	/**
	 * Set the background color for the items. If <code>null</code>, the default value (white) is used
	 * 
	 * @param color the new color to set
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setSelectedItemBackgroundColor(Color color) {
		checkWidget();
		this.selectedItemBackgroundColor = color;
	}

	/**
	 * @return the number of columns (item size/2 by default)
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public int getNumberOfColumns() {
		checkWidget();
		return numberOfColumns;
	}

	/**
	 * @param numberOfColumns the number of column to display
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setNumberOfColumns(int numberOfColumns) {
		checkWidget();
		this.numberOfColumns = numberOfColumns;
		drawLauncher();
	}

	/**
	 * @return <code>true</code> if the animation (and selection event) is fired when one clicks
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public boolean isSingleClickSelection() {
		checkWidget();
		return singleClickSelection;
	}

	/**
	 * @param singleClickSelection if true, the animation (and selection event) is fired on a single click. If false the selection is performed on a double click
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setSingleClickSelection(boolean singleClickSelection) {
		checkWidget();
		this.singleClickSelection = singleClickSelection;
	}

}
