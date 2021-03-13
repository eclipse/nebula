/*******************************************************************************
 * Copyright (c) 2012 Laurent CARON.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Laurent CARON (laurent.caron@gmail.com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.starrating;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.opal.commons.SelectionListenerUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Instances of this class provide a rating element.
 * <p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>HORIZONTAL</dd>
 * <dd>VERTICAL</dd> *
 * <dt><b>Events:</b></dt>
 * <dd>Selection</dd>
 * </dl>
 * </p>
 */
public class StarRating extends Canvas {

	private static final int SIZE_SMALL = 16;
	private static final int SIZE_BIG = 32;

	public enum SIZE {
		SMALL, BIG
	};

	private SIZE sizeOfStars;
	private int maxNumberOfStars;
	private int currentNumberOfStars;
	private static final int DEFAULT_MAX_NUMBERS_OF_STARS = 5;
	private final List<Star> stars;
	private int orientation;

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
	 * @exception IllegalArgumentException <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the parent</li>
	 *                </ul>
	 * 
	 */
	public StarRating(final Composite parent, final int style) {
		super(parent, checkStyle(style) | SWT.DOUBLE_BUFFERED);
		sizeOfStars = SIZE.SMALL;
		currentNumberOfStars = 0;

		if ((style & SWT.VERTICAL) != 0) {
			orientation = SWT.VERTICAL;
		} else {
			orientation = SWT.HORIZONTAL;
		}

		stars = new ArrayList<Star>();
		setMaxNumberOfStars(DEFAULT_MAX_NUMBERS_OF_STARS);
		initListeners();
	}

	private static int checkStyle(int style) {
		if ((style & SWT.VERTICAL) != 0) {
			style = style & ~SWT.VERTICAL;
		}

		if ((style & SWT.HORIZONTAL) != 0) {
			style = style & ~SWT.HORIZONTAL;
		}
		return style;
	}

	private void initListeners() {
		final Listener listener = new Listener() {
			@Override
			public void handleEvent(final Event event) {
				switch (event.type) {
					case SWT.MouseEnter:
					case SWT.MouseMove:
						onMouseEnterOrMove(event);
						break;
					case SWT.MouseExit:
						onMouseExit(event);
						break;
					case SWT.MouseUp:
						onMouseUp(event);
						break;
					case SWT.Paint:
						onMousePaint(event);
						break;
					case SWT.Dispose:
						onDispose(event);
						break;
				}
			}
		};

		final int[] events = new int[] { SWT.MouseEnter, SWT.MouseMove, SWT.MouseExit, SWT.MouseUp, SWT.Paint, SWT.Dispose };
		for (final int event : events) {
			addListener(event, listener);
		}
	}

	private void onMouseEnterOrMove(final Event event) {
		for (final Star star : stars) {
			star.hover = false;
		}

		for (final Star star : stars) {
			final boolean mouseHover = star.bounds.contains(event.x, event.y);
			star.hover = true;
			if (mouseHover) {
				break;
			}
		}
		redraw();
		update();
	}

	private void onMouseExit(final Event event) {
		for (final Star star : stars) {
			star.hover = false;
		}
		redraw();
		update();
	}

	private void onMouseUp(final Event event) {
		for (int i = 0; i < maxNumberOfStars; i++) {
			final Star star = stars.get(i);
			final boolean selected = star.bounds.contains(event.x, event.y);
			if (selected) {
				setCurrentNumberOfStars(i + 1);
				SelectionListenerUtil.fireSelectionListeners(this,null);
				redraw();
				update();
				break;
			}
		}

	}


	private void onMousePaint(final Event event) {
		final GC gc = event.gc;
		int x = 0, y = 0;

		for (final Star star : stars) {
			star.draw(gc, x, y);
			if (orientation == SWT.VERTICAL) {
				y += sizeOfStars.equals(SIZE.BIG) ? SIZE_BIG : SIZE_SMALL;
			} else {
				x += sizeOfStars.equals(SIZE.BIG) ? SIZE_BIG : SIZE_SMALL;
			}
		}
	}

	private void onDispose(final Event event) {
		for (final Star star : stars) {
			star.dispose();
		}
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified when the control 
	 * is selected by the user, by sending it one of the messages defined in the 
	 * <code>SelectionListener</code> interface.
	 * <p>
	 * <code>widgetDefaultSelected</code> is not called.
	 * </p>
	 * 
	 * @param listener the listener which should be notified when the control is selected by the user,
	 * 
	 * @exception IllegalArgumentException <ul>
	 *     <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *     <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *     <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
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
	 * @see org.eclipse.swt.widgets.Composite#computeSize(int, int, boolean)
	 */
	@Override
	public Point computeSize(final int wHint, final int hHint, final boolean changed) {
		if (orientation == SWT.VERTICAL) {
			return computeSizeVertical();
		}
		return computeSizeHorizontal();
	}

	private Point computeSizeVertical() {
		final int width = sizeOfStars.equals(SIZE.BIG) ? SIZE_BIG : SIZE_SMALL;
		final int height = maxNumberOfStars * width;
		return new Point(width + getBorderWidth() * 2, height + getBorderWidth() * 2);
	}

	private Point computeSizeHorizontal() {
		final int height = sizeOfStars.equals(SIZE.BIG) ? SIZE_BIG : SIZE_SMALL;
		final int width = maxNumberOfStars * height;
		return new Point(width + getBorderWidth() * 2, height + getBorderWidth() * 2);
	}

	/**
	 * @return the number of selected stars
	 * 
	 * @exception SWTException <ul>
	 *     <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *     <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 */
	public int getCurrentNumberOfStars() {
		checkWidget();
		return currentNumberOfStars;
	}

	/**
	 * @return the maximum number of stars that is displayed by this component
	 * 
	 * @exception SWTException <ul>
	 *     <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *     <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 */
	public int getMaxNumberOfStars() {
		checkWidget();
		return maxNumberOfStars;
	}

	/**
	 * @return the orientation of this widget (SWT.VERTICAL or SWT.HORIZONTAL)
	 * 
	 * @exception SWTException <ul>
	 *     <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *     <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 */
	public int getOrientation() {
		checkWidget();
		return orientation;
	}

	/**
	 * @return the size of stars
	 * 
	 * @exception SWTException <ul>
	 *     <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *     <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 */
	public SIZE getSizeOfStars() {
		checkWidget();
		return sizeOfStars;
	}

	/**
	 * Removes the listener from the collection of listeners who will be notified when the 
	 * control is selected by the user.
	 * 
	 * @param listener the listener which should no longer be notified
	 * 
	 * @exception IllegalArgumentException <ul>
	 *     <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *     <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *     <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * 
	 * @see SelectionListener
	 * @see #addSelectionListener
	 */
	public void removeSelectionListener(final SelectionListener listener) {
		checkWidget();
		SelectionListenerUtil.removeSelectionListener(this, listener);
	}

	/**
	 * Set the current number of stars
	 * 
	 * @param currentNumberOfStars current number of stars
	 * 
	 * @exception IllegalArgumentException <ul>
	 *     <li>ERROR_INVALID_ARGUMENT - if the number of star is negative or greater than the maximum number of stars</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *     <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *     <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 */
	public void setCurrentNumberOfStars(final int currentNumberOfStars) {
		checkWidget();
		if (currentNumberOfStars < 0 || currentNumberOfStars > maxNumberOfStars) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		this.currentNumberOfStars = currentNumberOfStars;
		for (final Star star : stars) {
			star.marked = false;
		}

		for (int i = 0; i < currentNumberOfStars; i++) {
			stars.get(i).marked = true;
		}
	}

	/**
	 * Set the maximum number of stars
	 * 
	 * @param currentNumberOfStars current number of stars
	 * 
	 * @exception SWTException <ul>
	 *     <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *     <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 */
	public void setMaxNumberOfStars(final int maxNumberOfStars) {
		this.maxNumberOfStars = maxNumberOfStars;
		reinitStars();
	}

	private void reinitStars() {
		for (final Star star : stars) {
			star.dispose();
		}
		stars.clear();
		for (int i = 0; i < maxNumberOfStars; i++) {
			if (sizeOfStars.equals(SIZE.BIG)) {
				stars.add(Star.initBig(this));
			} else {
				stars.add(Star.initSmall(this));
			}
		}

	}

	/**
	 * Set the current size of stars
	 * 
	 * @param sizeOfStars current number of stars
	 * 
	 * @exception IllegalArgumentException <ul>
	 *     <li>ERROR_INVALID_ARGUMENT - if the number of star is negative or greater than the maximum number of stars</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *     <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *     <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 */
	public void setSizeOfStars(final SIZE sizeOfStars) {
		checkWidget();
		this.sizeOfStars = sizeOfStars;
		reinitStars();
	}

}
