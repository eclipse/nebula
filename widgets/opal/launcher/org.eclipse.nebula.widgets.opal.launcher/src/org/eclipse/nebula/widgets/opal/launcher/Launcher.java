/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Laurent CARON (laurent.caron at gmail dot com) - Initial implementation and API
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.launcher;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
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
	private final List<SelectionListener> selectionListeners;
	private boolean needRedraw;
	private int selection = -1;

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
		selectionListeners = new ArrayList<SelectionListener>();
		needRedraw = true;
		setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));

		addListener(SWT.Resize, event -> {
			drawLauncher();
		});

		addListener(SWT.KeyUp, event -> {
			handleKeyPressedEvent(event);
		});
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
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		selectionListeners.add(listener);

	}

	/**
	 * Change the background color of a given button
	 *
	 * @param index index of the button
	 * @param isSelected if <code>true</code>, the background is the light
	 *            shadow. Otherwise, the background color is white
	 */
	private void changeColor(final int index, final boolean isSelected) {
		if (index != -1 && items.get(index).label != null) {
			items.get(index).label.setBackground(isSelected ? getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW) : getDisplay().getSystemColor(SWT.COLOR_WHITE));
		}
	}

	/**
	 * Create the buttons that will compose the launcher
	 */
	private void createButtons() {
		final GridLayout gridLayout = new GridLayout(items.size() / 2, true);
		gridLayout.horizontalSpacing = gridLayout.verticalSpacing = 0;
		setLayout(gridLayout);
		for (final LauncherItem item : items) {
			createItem(item);
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
		label.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		final GridData gd = new GridData(GridData.FILL, GridData.FILL, true, false);
		gd.widthHint = 192;
		gd.heightHint = 220;
		label.setLayoutData(gd);
		item.label = label;
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
	 * Fire the selection listeners
	 *
	 * @param originalEvent mouse event
	 * @return <code>true</code> if the selection could be changed,
	 *         <code>false</code> otherwise
	 */
	private boolean fireSelectionListeners(final Event originalEvent) {
		final Event event = new Event();

		event.button = originalEvent.button;
		event.display = getDisplay();
		event.item = null;
		event.widget = this;
		event.data = null;
		event.time = originalEvent.time;
		event.x = originalEvent.x;
		event.y = originalEvent.y;

		for (final SelectionListener listener : selectionListeners) {
			final SelectionEvent selEvent = new SelectionEvent(event);
			listener.widgetSelected(selEvent);
			if (!selEvent.doit) {
				return false;
			}
		}
		return true;
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
				startAnimation(i, event);
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
		selectionListeners.remove(listener);
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
					fireSelectionListeners(event);
				}
			}
		});

	}
}
