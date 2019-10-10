/*******************************************************************************
 * Copyright (c) 2018 Akuiteo (http://www.akuiteo.com).
 * 
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Laurent CARON (laurent.caron at gmail dot com) - Initial
 * implementation and API
 *******************************************************************************/
package org.eclipse.nebula.widgets.splitbutton;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.swt.widgets.Widget;

/**
 * Instances of this class represent a selectable user interface object that
 * issues notification when pressed and released.
 * A menu can be attached to this button
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>PUSH, TOGGLE</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles PUSH or TOGGLE may be specified.
 * </p>
 * <p>
 * Note: Only one of the styles LEFT, RIGHT, and CENTER may be specified.
 *
 */
public class SplitButton extends Button {

	private static final int EXTRA_WIDTH = 60;

	private final List<Listener> listeners = new LinkedList<>();

	private int splitButtonAreaLeft = -1;
	private int splitButtonAreaTop = -1;
	private int splitButtonAreaRight = -1;
	private int splitButtonAreaBottom = -1;
	private Menu menu;

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
	 * @param parent a composite control which will be the parent of the new instance (cannot be null)
	 * @param style the style of control to construct
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
	 *                <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
	 *                </ul>
	 *
	 * @see SWT#PUSH
	 * @see SWT#TOGGLE
	 * @see Widget#getStyle
	 */
	public SplitButton(final Composite parent, final int style) {
		super(parent, checkStyle(style));
		setText(""); //$NON-NLS-1$
		addPaintListener();
		addMouseDownListener();
		menu = new Menu(getShell(), SWT.POP_UP);
	}

	private static int checkStyle(int style) {
		return checkBits(style, SWT.NONE, SWT.PUSH, SWT.TOGGLE);
	}

	private static int checkBits(int style, final int int0, final int int1, final int int2) {
		final int mask = int0 | int1 | int2;
		if ((style & mask) == 0) {
			style |= int0;
		}
		if ((style & int0) != 0) {
			style = style & ~mask | int0;
		}
		if ((style & int1) != 0) {
			style = style & ~mask | int1;
		}
		if ((style & int2) != 0) {
			style = style & ~mask | int2;
		}
		return style;
	}

	private void addPaintListener() {
		super.addPaintListener(new PaintListener() {

			private PaintEvent e;
			private int deltaX;
			private int deltaY;

			@Override
			public void paintControl(final PaintEvent e) {
				this.e = e;
				final Rectangle rect = getBounds();

				final Color oldForeground = e.gc.getForeground();
				final Color oldBackground = e.gc.getBackground();

				splitButtonAreaLeft = e.x + rect.width - 20;
				splitButtonAreaTop = e.y;
				splitButtonAreaRight = e.x + rect.width;
				splitButtonAreaBottom = e.y + rect.height;

				final String osName = System.getProperty("os.name"); //$NON-NLS-1$
				final boolean isMac = osName.toLowerCase().indexOf("mac os") > -1; //$NON-NLS-1$
				final int macDelta = isMac ? 2 : 0;

				deltaX = -e.gc.getClipping().x - macDelta;
				deltaY = -e.gc.getClipping().y - macDelta;

				drawLine();
				drawTriangle();

				e.gc.setForeground(oldForeground);
				e.gc.setBackground(oldBackground);
			}

			private void drawLine() {
				final Rectangle rect = getBounds();
				e.gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
				e.gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
				e.gc.setLineWidth(1);
				e.gc.drawLine(e.x + rect.width - 20 + deltaX, e.y + 6 + deltaY, //
						e.x + rect.width - 20 + deltaX, e.y + rect.height - 6 + deltaY);

			}

			private void drawTriangle() {
				final Rectangle rect = getBounds();
				e.gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
				e.gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
				e.gc.fillPolygon(new int[] { e.x + rect.width - 15 + deltaX, e.y + rect.height / 2 - 1 + deltaY, e.x + rect.width - 8 + deltaX, e.y + rect.height / 2 - 1 + deltaY, e.x + rect.width - 12 + deltaX, e.y + rect.height / 2 + 3 + deltaY });
			}
		});

	}

	private void addMouseDownListener() {
		super.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				if (shouldShowMenu(event.x, event.y)) {
					final Button button = (Button) event.widget;
					final Rectangle rect = button.getBounds();
					final Point p = button.toDisplay(rect.x, rect.y + rect.height);
					getMenu().setLocation(p.x - rect.x, p.y - rect.y);
					getMenu().setVisible(true);

				} else {
					for (final Listener listener : listeners) {
						final Event evt = new Event();
						evt.widget = SplitButton.this;
						evt.display = getDisplay();
						evt.type = SWT.Selection;
						listener.handleEvent(evt);
					}
				}
			}

			private boolean shouldShowMenu(final int x, final int y) {
				return x >= splitButtonAreaLeft && y >= splitButtonAreaTop && x <= splitButtonAreaRight && y <= splitButtonAreaBottom;
			}
		});
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified when
	 * the control is selected by the user, by sending it one of the messages
	 * defined in the <code>SelectionListener</code> interface.
	 * <p>
	 * <code>widgetDefaultSelected</code> is not called.
	 * </p>
	 *
	 * @param listener the listener which should be notified when the control is
	 *            selected by the user,
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
	@Override
	public void addSelectionListener(SelectionListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		final TypedListener typedListener = new TypedListener(listener);
		listeners.add(typedListener);
	}

	/**
	 * Removes the listener from the collection of listeners who will be notified
	 * when the control is selected by the user.
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
	@Override
	public void removeSelectionListener(SelectionListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}

		final Iterator<Listener> it = listeners.iterator();
		while (it.hasNext()) {
			final Listener current = it.next();
			if (current instanceof TypedListener) {
				final TypedListener tl = (TypedListener) current;
				if (tl.getEventListener() != null && tl.getEventListener().equals(listener)) {
					it.remove();
				}
			}
		}
	}

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when an event of the given type occurs. When the
	 * event does occur in the widget, the listener is notified by
	 * sending it the <code>handleEvent()</code> message. The event
	 * type is one of the event constants defined in class <code>SWT</code>.
	 *
	 * @param eventType the type of event to listen for
	 * @param listener the listener which should be notified when the event occurs
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
	 * @see Listener
	 * @see SWT
	 * @see #getListeners(int)
	 * @see #removeListener(int, Listener)
	 * @see #notifyListeners
	 */
	@Override
	public void addListener(int eventType, Listener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		if (eventType == SWT.Selection) {
			listeners.add(listener);
			return;
		}
		super.addListener(eventType, listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when an event of the given type occurs. The event
	 * type is one of the event constants defined in class <code>SWT</code>.
	 *
	 * @param eventType the type of event to listen for
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
	 * @see Listener
	 * @see SWT
	 * @see #addListener
	 * @see #getListeners(int)
	 * @see #notifyListeners
	 */
	@Override
	public void removeListener(int eventType, Listener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		if (eventType == SWT.Selection) {
			listeners.add(listener);
			return;
		}
		super.removeListener(eventType, listener);
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#getMenu()
	 */
	@Override
	public Menu getMenu() {
		checkWidget();
		return menu;
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#setMenu(org.eclipse.swt.widgets.Menu)
	 */
	@Override
	public void setMenu(final Menu menu) {
		checkWidget();
		this.menu = menu;
	}

	/**
	 * @see org.eclipse.swt.widgets.Widget#checkSubclass()
	 */
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#computeSize(int, int, boolean)
	 */
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		final Point point = super.computeSize(wHint, hHint, changed);
		point.x += EXTRA_WIDTH;
		return point;
	}

}
