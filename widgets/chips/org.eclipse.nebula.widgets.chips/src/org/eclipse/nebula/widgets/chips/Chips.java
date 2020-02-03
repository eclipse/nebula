/*******************************************************************************
 * Copyright (c) 2019 Akuiteo (http://www.akuiteo.com).
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API and
 * implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.chips;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Instances of this class represent a collection of steps, indicating
 * progress/status in a linear order. Each step has a label. One can select the
 * current step, and possibly set an error state.
 *
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SWT.CLOSE</dd>
 * <dd>SWT.CHECK</dd>
 * <dd>SWT.PUSH</dd>
 * <dt><b>Events:</b></dt>
 * <dd>SWT.Close, SWT.Selection</dd>
 * </dl>
 */
public class Chips extends Canvas {
	private Color hoverForeground, hoverBackground;
	private Color closeButtonForeground, closeButtonBackground;
	private Color closeButtonHoverForeground, closeButtonHoverBackground;
	private Color pushedStateForeground, pushedStateBackground;
	private Color borderColor, hoverBorderColor, pushedStateBorderColor;
	private String text;
	private Image image, pushImage, hoverImage;
	private boolean checked;
	private final boolean isCheck;
	private final boolean isPush;
	private final boolean isClose;
	private final List<SelectionListener> selectionListeners = new ArrayList<>();
	private final List<CloseListener> closeListeners = new ArrayList<>();

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
	 * @param parent a composite control which will be the parent of the new instance (cannot be null)
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
	 *                <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed
	 *                subclass</li>
	 *                </ul>
	 *
	 */
	public Chips(final Composite parent, final int style) {
		super(parent, checkStyle(style));
		initDefaultColors();
		isCheck = (getStyle() & SWT.CHECK) != 0;
		isPush = (getStyle() & SWT.PUSH) != 0;
		isClose = (getStyle() & SWT.CLOSE) != 0;

		addListener(SWT.Paint, e -> {
			final GC gc = e.gc;
			gc.setFont(getFont());
			gc.setAdvanced(true);
			gc.setTextAntialias(SWT.ON);
			gc.setAntialias(SWT.ON);

			int x = drawBackground(gc);
			if (isCheck) {
				x = drawCheck(gc, x);
			}
			if (image != null) {
				x = drawImage(gc, x);
			}
			if (text != null) {
				x = drawText(gc, x);
			}
			if (isClose) {
				drawClose(gc, x);
			}
		});

		// TODO CLICK
	}

	private static int checkStyle(final int style) {
		final int mask = SWT.CLOSE | SWT.CHECK | SWT.PUSH;
		int newStyle = style & mask;
		newStyle |= SWT.DOUBLE_BUFFERED;
		return newStyle;
	}

	private int drawBackground(final GC gc) {
		// border
		// TODO STOP
		return 0;
	}

	private int drawCheck(final GC gc, final int x) {
		// TODO Auto-generated method stub
		return 0;
	}

	private int drawImage(final GC gc, final int x) {
		// TODO Auto-generated method stub
		return 0;
	}

	private int drawText(final GC gc, final int x) {
		// TODO Auto-generated method stub
		return 0;
	}

	private void drawClose(final GC gc, final int x) {
		// TODO Auto-generated method stub

	}

	private void initDefaultColors() {
		setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
		setBackground(SWTGraphicUtil.getColorSafely(224, 224, 224));

		hoverForeground = SWTGraphicUtil.getColorSafely(62, 28, 96);
		hoverBackground = SWTGraphicUtil.getColorSafely(214, 214, 214);

		closeButtonForeground = SWTGraphicUtil.getColorSafely(229, 229, 229);
		closeButtonBackground = SWTGraphicUtil.getColorSafely(100, 100, 100);
		closeButtonHoverForeground = SWTGraphicUtil.getColorSafely(214, 214, 214);
		closeButtonHoverBackground = SWTGraphicUtil.getColorSafely(64, 64, 64);

		pushedStateForeground = SWTGraphicUtil.getColorSafely(224, 224, 224);
		pushedStateBackground = getDisplay().getSystemColor(SWT.COLOR_BLACK);
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#computeSize(int, int, boolean)
	 */
	@Override
	public Point computeSize(final int wHint, final int hHint, final boolean changed) {
		checkWidget();
		int width = 0; // Border
		int height = 16;
		if (image != null) {
			final Rectangle imageSize = image.getBounds();
			width += 8 + imageSize.width;
			height = Math.max(height, imageSize.height + 4);

		}

		if (text != null) {
			final GC gc = new GC(this);
			final Point textSize = gc.stringExtent(text);
			width += textSize.x;
			height = Math.max(height, textSize.y);
			gc.dispose();
		}

		if (isCheck) {
			width += 16;
			height = Math.max(height, 24);
		}

		width += 2 * Math.max(height, hHint); // Size for left & right half-circle
		return new Point(Math.max(width, wHint), Math.max(height, hHint));
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified when
	 * the control is closed by the user, by sending it one of the messages
	 * defined in the <code>CodeListener</code> interface.
	 * <p>
	 * <code>widgetDefaultSelected</code> is not called.
	 * </p>
	 *
	 * @param listener the listener which should be notified when the control is
	 *            closed by the user,
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
	 * @see CloseListener
	 * @see #removeCloseListener
	 * @see SelectionEvent
	 */
	public void addCloseListener(final CloseListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		closeListeners.add(listener);
	}

	/**
	 * @see org.eclipse.swt.widgets.Widget#addListener(int, org.eclipse.swt.widgets.Listener)
	 */
	@Override
	public void addListener(final int eventType, final Listener listener) {
		if (eventType == SWT.Selection) {
			selectionListeners.add(new SelectionListener() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					widgetSelection(e);
				}

				@Override
				public void widgetDefaultSelected(final SelectionEvent e) {
					widgetSelection(e);
				}

				private void widgetSelection(final SelectionEvent e) {
					final Event event = new Event();
					event.widget = Chips.this;
					event.display = getDisplay();
					event.type = SWT.Selection;
					listener.handleEvent(event);
				}
			});
			return;
		} else if (eventType == SWT.Close) {
			closeListeners.add(new CloseListener() {
				@Override
				public void onClose(final CloseEvent e) {
					final Event event = new Event();
					event.widget = Chips.this;
					event.display = getDisplay();
					event.type = SWT.Close;
					listener.handleEvent(event);
				}
			});
			return;
		}
		super.addListener(eventType, listener);
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
	public void addSelectionListener(final SelectionListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		selectionListeners.add(listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will be notified
	 * when the control is closed by the user.
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
	 * @see CloseListener
	 * @see #addCloseListener
	 */
	public void removeCloseListener(final CloseListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		closeListeners.remove(listener);
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
	public void removeSelectionListener(final SelectionListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		selectionListeners.remove(listener);
	}
	// ---- Getters & Setters

	public Color getHoverForeground() {
		checkWidget();
		return hoverForeground;
	}

	public Color getHoverBackground() {
		checkWidget();
		return hoverBackground;
	}

	public Color getCloseButtonForeground() {
		checkWidget();
		return closeButtonForeground;
	}

	public Color getCloseButtonBackground() {
		checkWidget();
		return closeButtonBackground;
	}

	public Color getCloseButtonHoverForeground() {
		checkWidget();
		return closeButtonHoverForeground;
	}

	public Color getCloseButtonHoverBackground() {
		checkWidget();
		return closeButtonHoverBackground;
	}

	public Color getPushedStateForeground() {
		checkWidget();
		return pushedStateForeground;
	}

	public Color getPushedStateBackground() {
		checkWidget();
		return pushedStateBackground;
	}

	public Color getBorderColor() {
		checkWidget();
		return borderColor;
	}

	public Color getHoverBorderColor() {
		checkWidget();
		return hoverBorderColor;
	}

	public Color getPushedStateBorderColor() {
		checkWidget();
		return pushedStateBorderColor;
	}

	public String getText() {
		checkWidget();
		return text;
	}

	public Image getImage() {
		checkWidget();
		return image;
	}

	public Image getPushImage() {
		checkWidget();
		return pushImage;
	}

	public Image getHoverImage() {
		checkWidget();
		return hoverImage;
	}

	public boolean isChecked() {
		checkWidget();
		return checked;
	}

	public void setHoverForeground(final Color hoverForeground) {
		checkWidget();
		this.hoverForeground = hoverForeground;
	}

	public void setHoverBackground(final Color hoverBackground) {
		checkWidget();
		this.hoverBackground = hoverBackground;
	}

	public void setCloseButtonForeground(final Color closeButtonForeground) {
		checkWidget();
		this.closeButtonForeground = closeButtonForeground;
	}

	public void setCloseButtonBackground(final Color closeButtonBackground) {
		checkWidget();
		this.closeButtonBackground = closeButtonBackground;
	}

	public void setCloseButtonHoverForeground(final Color closeButtonHoverForeground) {
		checkWidget();
		this.closeButtonHoverForeground = closeButtonHoverForeground;
	}

	public void setCloseButtonHoverBackground(final Color closeButtonHoverBackground) {
		checkWidget();
		this.closeButtonHoverBackground = closeButtonHoverBackground;
	}

	public void setPushedStateForeground(final Color pushedStateForeground) {
		checkWidget();
		this.pushedStateForeground = pushedStateForeground;
	}

	public void setPushedStateBackground(final Color pushedStateBackground) {
		checkWidget();
		this.pushedStateBackground = pushedStateBackground;
	}

	public void setBorderColor(final Color borderColor) {
		checkWidget();
		this.borderColor = borderColor;
	}

	public void setHoverBorderColor(final Color hoverBorderColor) {
		checkWidget();
		this.hoverBorderColor = hoverBorderColor;
	}

	public void setPushedStateBorderColor(final Color pushedStateBorderColor) {
		checkWidget();
		this.pushedStateBorderColor = pushedStateBorderColor;
	}

	public void setText(final String text) {
		checkWidget();
		this.text = text;
	}

	public void setImage(final Image image) {
		checkWidget();
		this.image = image;
	}

	public void setPushImage(final Image pushImage) {
		checkWidget();
		this.pushImage = pushImage;
	}

	public void setHoverImage(final Image hoverImage) {
		checkWidget();
		this.hoverImage = hoverImage;
	}

	public void setChecked(final boolean checked) {
		checkWidget();
		this.checked = checked;
	}

}
