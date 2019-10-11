/*******************************************************************************
 * Copyright (c) 2008-2013 Matthew Hall and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Matthew Hall - initial API and implementation (bug 248956)
 * Mickael Istria - Made some useful methods public
 * Laurent Caron - Fix bug 430114
 *******************************************************************************/

package org.eclipse.nebula.widgets.radiogroup;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

/**
 * Instances of this class represent a selectable user interface object that
 * represents an radio button in a radio group.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>LEFT, CENTER, RIGHT</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles LEFT, RIGHT, and CENTER may be specified.
 * </p>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 *
 * @noextend This class is not intended to be subclassed.
 */
public class RadioItem extends Item {
	private static int checkIndex(RadioGroup parent, int position) {
		if (position == -1) {
			return parent.getItemCount();
		}
		if (position < 0 || position > parent.getItemCount()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		return position;
	}

	private static int checkStyle(int style) {
		int result = 0;
		if ((style & SWT.LEFT) != 0) {
			result |= SWT.LEFT;
		} else if ((style & SWT.CENTER) != 0) {
			result |= SWT.CENTER;
		} else if ((style & SWT.RIGHT) != 0) {
			result |= SWT.RIGHT;
		}
		return result;
	}

	private RadioGroup parent;

	private Button button;

	/**
	 * Constructs a new instance of this class given its parent
	 * and a style value describing its behavior and appearance.
	 * The item is added to the end of the items maintained by its parent.
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
	 * @param style the style of item to construct
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
	 * @see SWT
	 * @see Widget#getStyle
	 */
	public RadioItem(RadioGroup parent, int style) {
		this(parent, style, -1);
	}

	/**
	 * Constructs a new instance of this class given its parent
	 * and a style value describing its behavior and appearance,
	 * and the index at which to place it in the items maintained
	 * by its parent.
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
	 * @param style the style of item to construct
	 * @param index the zero-relative index at which to store the receiver in its parent
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the parent (inclusive)</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
	 *                </ul>
	 *
	 * @see SWT
	 * @see Widget#getStyle
	 */
	public RadioItem(final RadioGroup parent, int style, int index) {
		super(parent, checkStyle(style), checkIndex(parent, index));
		this.parent = parent;
		button = parent.createButton(getStyle(), index);

		final Listener listener = event -> {
			if (event.type == SWT.Selection) {
				handleSelection(event);
			} else if (event.type == SWT.Dispose) {
				handleDispose(event);
			}
		};
		button.addListener(SWT.Selection, listener);
		addListener(SWT.Dispose, listener);

		parent.addItem(this, index);
	}

	void clear() {
		setText("");
		setImage(null);
		setFont(parent.getFont());
		setForeground(parent.getForeground());
		setBackground(parent.getBackground());
	}

	void deselect() {
		button.setSelection(false);
		parent.itemSelected(this);
	}

	/**
	 * Returns the receiver's background color.
	 * <p>
	 * Note: This operation is a hint and may be overridden by the platform.
	 * For example, on some versions of Windows the background of a TabFolder,
	 * is a gradient rather than a solid color.
	 * </p>
	 * 
	 * @return the background color
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public Color getBackground() {
		checkWidget();
		return button.getBackground();
	}

	/**
	 * @return the button associated of this current item
	 */
	public Button getButton() {
		return button;
	}

	/**
	 * Returns the font that the receiver will use to paint textual information.
	 *
	 * @return the receiver's font
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public Font getFont() {
		checkWidget();
		return button.getFont();
	}

	/**
	 * Returns the foreground color that the receiver will use to draw.
	 *
	 * @return the receiver's foreground color
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public Color getForeground() {
		checkWidget();
		return button.getForeground();
	}

	/**
	 * @see org.eclipse.swt.widgets.Item#getImage()
	 */
	@Override
	public Image getImage() {
		checkWidget();
		return button.getImage();
	}

	/**
	 * Returns the receiver's RadioGroup parent.
	 *
	 * @return the receiver's image
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public RadioGroup getParent() {
		return parent;
	}

	/**
	 * @see org.eclipse.swt.widgets.Item#getText()
	 */
	@Override
	public String getText() {
		checkWidget();
		return button.getText();
	}

	private void handleDispose(Event event) {
		if (parent != null) {
			parent.removeItem(RadioItem.this);
		}
		if (button != null) {
			button.dispose();
			if (parent != null && !parent.isDisposed()) {
				parent.layout(false);
			}
		}
		RadioItem.this.parent = null;
		button = null;
	}

	private void handleSelection(Event event) {
		parent.itemSelected(this);
	}

	/**
	 * Returns <code>true</code> if the receiver is selected,
	 * and false otherwise.
	 * <p>
	 * When the receiver is of type <code>CHECK</code> or <code>RADIO</code>,
	 * it is selected when it is checked. When it is of type <code>TOGGLE</code>,
	 * it is selected when it is pushed in. If the receiver is of any other type,
	 * this method returns false.
	 *
	 * @return the selection state
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public boolean isSelected() {
		return button.getSelection();
	}

	void select() {
		button.setSelection(true);
		parent.itemSelected(this);
	}

	/**
	 * Sets the receiver's background color to the color specified
	 * by the argument, or to the default system color for the control
	 * if the argument is null.
	 * <p>
	 * Note: This operation is a hint and may be overridden by the platform.
	 * </p>
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
	public void setBackground(Color background) {
		checkWidget();
		if (background == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		button.setBackground(background);
	}

	/**
	 * Sets the font that the receiver will use to paint textual information
	 * to the font specified by the argument, or to the default font for that
	 * kind of control if the argument is null.
	 *
	 * @param font the new font (or null)
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
	public void setFont(Font font) {
		checkWidget();
		if (font == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		button.setFont(font);
	}

	/**
	 * Sets the receiver's foreground color to the color specified
	 * by the argument, or to the default system color for the control
	 * if the argument is null.
	 * <p>
	 * Note: This operation is a hint and may be overridden by the platform.
	 * </p>
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
	public void setForeground(Color foreground) {
		checkWidget();
		if (foreground == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		button.setForeground(foreground);
	}

	/**
	 * @see org.eclipse.swt.widgets.Item#setImage(org.eclipse.swt.graphics.Image)
	 */
	@Override
	public void setImage(Image image) {
		checkWidget();
		button.setImage(image);
		parent.layout(new Control[] { button });
	}

	/**
	 * @see org.eclipse.swt.widgets.Item#setText(java.lang.String)
	 */
	@Override
	public void setText(String string) {
		checkWidget();
		button.setText(string);
		parent.layout(new Control[] { button });
	}
}
