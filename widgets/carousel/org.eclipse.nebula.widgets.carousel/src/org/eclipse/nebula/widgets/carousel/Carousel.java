/*******************************************************************************
 * Copyright (c) 2020 Laurent CARON.
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
package org.eclipse.nebula.widgets.carousel;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.nebula.widgets.opal.commons.SelectionListenerUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Instances of this class represent a "Carousel". This is a component that displays numerous images, and ones can navigate through images.
 *
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>SWT.Selection</dd>
 * </dl>
 */
public class Carousel extends Composite {

	int selection = 0;
	final ImageContainer imageContainer;
	final ImageSelector imageSelector;
	private List<Image> images = new ArrayList<>();

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
	public Carousel(final Composite parent, final int style) {
		super(parent, checkStyle(style));
		setLayout(new GridLayout());
		imageContainer = new ImageContainer(this, SWT.NONE);
		imageContainer.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		imageSelector = new ImageSelector(this, SWT.NONE);
		imageSelector.setLayoutData(new GridData(GridData.END, GridData.FILL, false, false));

		initDefaultColors();
		addListener(SWT.KeyUp, e -> {
			if (e.keyCode == SWT.ARROW_LEFT) {
				imageContainer.movePrevious();
			}
			if (e.keyCode == SWT.ARROW_RIGHT) {
				imageContainer.moveNext();
			}
		});
	}

	private static int checkStyle(final int style) {
		final int mask = SWT.BORDER;
		final int newStyle = style & mask;
		return newStyle;
	}

	private void initDefaultColors() {
		setArrowColor(getDisplay().getSystemColor(SWT.COLOR_BLACK));
		setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		setCircleBackground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
		setCircleForeground(SWTGraphicUtil.getColorSafely(153, 153, 153));
		setCircleHoverColor(SWTGraphicUtil.getColorSafely(102, 102, 102));
	}

	/**
	 * Adds the image to the collection of images
	 *
	 * @param image the image to add
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if image is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see Image
	 * @see #removeImage
	 */
	public void addImage(final Image image) {
		checkWidget();
		if (image == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		images.add(image);
		imageContainer.setImage(images.get(selection));
		layout();
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
		SelectionListenerUtil.addSelectionListener(this, listener);
	}

	/**
	 * Remove the image to the collection of images
	 *
	 * @param image the image to remove
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if image is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see Image
	 * @see #removeImage
	 */
	public void removeImage(final Image image) {
		checkWidget();
		if (image == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		images.remove(image);
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
		SelectionListenerUtil.removeSelectionListener(this, listener);
	}

	// ---- Getters & Setters

	/**
	 * Returns the receiver's arrow color.
	 *
	 * @return the arrow color
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public Color getArrowColor() {
		checkWidget();
		return imageSelector.arrowColor;
	}

	/**
	 * Returns the receiver's circle background (when selected).
	 *
	 * @return the circle background color
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public Color getCircleBackground() {
		checkWidget();
		return imageSelector.circleBackground;
	}

	/**
	 * Returns the receiver's circle foreground color.
	 *
	 * @return the circle foreground color
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public Color getCircleForeground() {
		checkWidget();
		return imageSelector.circleForeground;
	}

	/**
	 * Returns the receiver's circle foreground color when mouse is over.
	 *
	 * @return the circle foreground color when mouse is over
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public Color getCircleHoverColor() {
		checkWidget();
		return imageSelector.circleHoverColor;
	}

	/**
	 * Returns the receiver's list of images.
	 *
	 * @return the list of images
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public List<Image> getImages() {
		checkWidget();
		return images;
	}

	/**
	 * Returns <code>true</code> if the receiver is selected,
	 * and false otherwise.
	 * <p>
	 * Note: This operation is only available if the SWT.CHECK or the SWT.PUSH flag is set.
	 * </p>
	 *
	 * @return the selection state
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public int getSelection() {
		checkWidget();
		return selection;
	}

	/**
	 * Sets the receiver's arrow color to the color specified
	 * by the argument, or to the default system color for the control
	 * if the argument is null.
	 *
	 * @param color the new color (or null)
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setArrowColor(final Color arrowColor) {
		checkWidget();
		imageSelector.arrowColor = arrowColor;
		imageSelector.redraw();
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#setBackground(org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setBackground(final Color color) {
		super.setBackground(color);
		imageContainer.setBackground(color);
		imageSelector.setBackground(color);
	}

	/**
	 * Sets the receiver's circle selection color to the color specified
	 * by the argument, or to the default system color for the control
	 * if the argument is null.
	 *
	 * @param color the new color (or null)
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setCircleBackground(final Color circleBackground) {
		checkWidget();
		imageSelector.circleBackground = circleBackground;
		imageSelector.redraw();
	}

	/**
	 * Sets the circle's foreground color to the color specified
	 * by the argument, or to the default system color for the control
	 * if the argument is null.
	 *
	 * @param color the new color (or null)
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setCircleForeground(final Color circleForeground) {
		checkWidget();
		imageSelector.circleForeground = circleForeground;
		imageSelector.redraw();
	}

	/**
	 * Sets the circle's foreground color (when mouse hover) to the color specified
	 * by the argument, or to the default system color for the control
	 * if the argument is null.
	 *
	 * @param color the new color (or null)
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setCircleHoverColor(final Color circleHoverColor) {
		checkWidget();
		imageSelector.circleHoverColor = circleHoverColor;
		imageSelector.redraw();
	}

	/**
	 * Sets the receiver's list of images.
	 *
	 * @param images the new list of images
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the argument is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setImages(final List<Image> images) {
		checkWidget();
		if (images == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		this.images = images;
		selection = 0;
		imageContainer.setImage(images.get(selection));
		layout();
	}

	/**
	 * Sets the selection state of the receiver, if it is of type <code>CHECK</code> or
	 * <code>PUSH</code>.
	 *
	 * <p>
	 * When the receiver is of type <code>CHECK</code> or <code>RADIO</code>,
	 * it is selected when it is checked. When it is of type <code>TOGGLE</code>,
	 * it is selected when it is pushed in.
	 *
	 * @param selected the new selection state
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the argument is lower than 0 or greater or equals to the number of images</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setSelection(final int selected) {
		checkWidget();
		if (selected < 0 || selected >= images.size()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		selection = selected;
		imageContainer.setImage(images.get(selection));
		redraw();
	}

}
