/*******************************************************************************
 * Copyright (c) 2019 Thomas Schindl & Laurent Caron.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * - Thomas Schindl (tom dot schindl at bestsolution dot at) - initial API
 * and implementation
 * - Laurent Caron (laurent dot caron at gmail dot com) - Integration to Nebula,
 * code cleaning and documentation
 *******************************************************************************/
package org.eclipse.nebula.widgets.ctreecombo;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

/**
 * Instances of this class represent a selectable user interface object
 * that represents a hierarchy of tree items in a ctreecombo widget.
 *
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 *
 * @noextend This class is not intended to be subclassed by clients.
 */
public class CTreeComboItem extends Item {
	static final String DATA_ID = "org.eclipse.nebula.widgets.ctreecombo.CTreeComboItem";
	private CTreeCombo parent;
	private CTreeComboItem parentItem;
	private List<CTreeComboItem> childItems = new ArrayList<>();
	private ArrayList<Color> backgroundColors = new ArrayList<>();
	private ArrayList<Color> foregroundColors = new ArrayList<>();
	private ArrayList<Font> fonts = new ArrayList<>();
	private ArrayList<Image> images = new ArrayList<>();
	private ArrayList<String> texts = new ArrayList<>();
	private ArrayList<Rectangle> bounds = new ArrayList<>();
	private ArrayList<Rectangle> textbounds = new ArrayList<>();
	private ArrayList<Rectangle> imageBounds = new ArrayList<>();
	private Color foreground, background;
	private Font font;
	private Rectangle bound;

	private TreeItem realTreeItem;

	/**
	 * Constructs a new instance of this class given its parent
	 * (which must be a <code>CTreeComboItemItem</code>),
	 * a style value describing its behavior and appearance, and the index
	 * at which to place it in the items maintained by its parent.
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
	 * @param parentItem a CTreeComboItem which will be the parent of the new instance (cannot be null)
	 * @param style the style of control to construct
	 * @param index the zero-relative index to store the receiver in its parent
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the parent (inclusive)</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
	 *                <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
	 *                </ul>
	 *
	 * @see SWT
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public CTreeComboItem(CTreeComboItem parentItem, int style, int index) {
		super(parentItem.parent, style);
		this.parent = parentItem.parent;
		this.parentItem = parentItem;
		this.parentItem.childItems.add(index, this);

		if (parentItem.realTreeItem != null && !parentItem.realTreeItem.isDisposed()) {
			setRealTreeItem(new TreeItem(parentItem.realTreeItem, style, index));
		}
	}

	/**
	 * Constructs a new instance of this class given its parent
	 * (which must be a <code>CTreeComboItem</code>)
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
	 * @param parentItem a CTreeComboItem control which will be the parent of the new instance (cannot be null)
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
	 * @see SWT
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public CTreeComboItem(CTreeComboItem parentItem, int style) {
		super(parentItem.parent, style);
		this.parent = parentItem.parent;
		this.parentItem = parentItem;
		this.parentItem.childItems.add(this);

		if (parentItem.realTreeItem != null && !parentItem.realTreeItem.isDisposed()) {
			setRealTreeItem(new TreeItem(parentItem.realTreeItem, style));
		}
	}

	/**
	 * Constructs a new instance of this class given its parent
	 * (which must be a <code>CTreeCombo</code> ),
	 * a style value describing its behavior and appearance, and the index
	 * at which to place it in the items maintained by its parent.
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
	 * @param parent a CTreeCombo control which will be the parent of the new instance (cannot be null)
	 * @param style the style of control to construct
	 * @param index the zero-relative index to store the receiver in its parent
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the parent (inclusive)</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
	 *                <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
	 *                </ul>
	 *
	 * @see SWT
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public CTreeComboItem(CTreeCombo parent, int style, int index) {
		super(parent, style);
		this.parent = parent;
		this.parent.items.add(index, this);

		if (this.parent.tree != null && !this.parent.tree.isDisposed()) {
			setRealTreeItem(new TreeItem(this.parent.tree, style, index));
		}
	}

	/**
	 * Constructs a new instance of this class given its parent
	 * (which must be a <code>CTreeCombo</code>)
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
	 * @param parent a CTreeCombo control which will be the parent of the new instance (cannot be null)
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
	 * @see SWT
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public CTreeComboItem(CTreeCombo parent, int style) {
		super(parent, style);
		this.parent = parent;
		this.parent.items.add(this);

		if (this.parent.tree != null && !this.parent.tree.isDisposed()) {
			setRealTreeItem(new TreeItem(this.parent.tree, style));
		}
	}

	/**
	 * Disposes of the operating system resources associated with
	 * the receiver and all its descendants. After this method has
	 * been invoked, the receiver and all descendants will answer
	 * <code>true</code> when sent the message <code>isDisposed()</code>.
	 * Any internal connections between the widgets in the tree will
	 * have been removed to facilitate garbage collection.
	 * This method does nothing if the widget is already disposed.
	 * <p>
	 * NOTE: This method is not called recursively on the descendants
	 * of the receiver. This means that, widget implementers can not
	 * detect when a widget is being disposed of by re-implementing
	 * this method, but should instead listen for the <code>Dispose</code>
	 * event.
	 * </p>
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see #addDisposeListener
	 * @see #removeDisposeListener
	 * @see #checkWidget
	 */
	public void dispose() {
		super.dispose();
		if (realTreeItem != null && !realTreeItem.isDisposed()) {
			realTreeItem.dispose();
		}

		if (this.parentItem != null && !this.parentItem.isDisposed()) {
			this.parentItem.childItems.remove(this);
		}

		for (CTreeComboItem child : childItems) {
			child.dispose();
		}
	}

	void setRealTreeItem(TreeItem realTreeItem) {
		this.realTreeItem = realTreeItem;
		this.realTreeItem.setData(DATA_ID, this);
	}

	TreeItem getRealTreeItem() {
		return this.realTreeItem;
	}

	/**
	 * Returns a (possibly empty) array of <code>CTreeComboItem</code>s which
	 * are the direct item children of the receiver.
	 * <p>
	 * Note: This is not the actual structure used by the receiver
	 * to maintain its list of items, so modifying the array will
	 * not affect the receiver.
	 * </p>
	 *
	 * @return the receiver's items
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public CTreeComboItem[] getItems() {
		checkWidget();
		return childItems.toArray(new CTreeComboItem[0]);
	}

	private boolean checkRealItem() {
		return realTreeItem != null && !realTreeItem.isDisposed();
	}

	/**
	 * Sets the receiver's image to the argument, which may be
	 * null indicating that no image should be displayed.
	 *
	 * @param image the image to display on the receiver (may be null)
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the image has been disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	@Override
	public void setImage(Image image) {
		super.setImage(image);
	}

	/**
	 * Sets the receiver's text.
	 * <p>
	 * Note: If control characters like '\n', '\t' etc. are used
	 * in the string, then the behavior is platform dependent.
	 * </p>
	 * 
	 * @param string the new text
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the text is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	@Override
	public void setText(String string) {
		super.setText(string);
	}

	/**
	 * Returns a rectangle describing the receiver's size and location
	 * relative to its parent at a column in the tree.
	 *
	 * @param index the index that specifies the column
	 * @return the receiver's bounding column rectangle
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public Rectangle getBounds(int index) {
		checkWidget();
		if (index < 0 || index > (bounds.size() - 1)) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		return bounds.get(index);
	}

	/**
	 * Returns a rectangle describing the size and location of the receiver's
	 * text relative to its parent.
	 *
	 * @return the bounding rectangle of the receiver's text
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public Rectangle getBounds() {
		checkWidget();
		return bound;
	}

	/**
	 * Returns the receiver's parent, which must be a <code>CTreeCombo</code>.
	 *
	 * @return the receiver's parent
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public CTreeCombo getParent() {
		checkWidget();
		return parent;
	}

	/**
	 * Returns the background color of the receiver.
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
		return background;
	}

	/**
	 * Returns the background color at the given column index in the receiver.
	 *
	 * @param index the column index
	 * @return the background color
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public Color getBackground(int index) {
		checkWidget();
		if (index < 0 || index > (backgroundColors.size() - 1)) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		return backgroundColors.get(index);
	}

	/**
	 * Returns the font that the receiver will use to paint textual information
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
		return font;
	}

	/**
	 * Returns the font that the receiver will use to paint textual information
	 * for the specified cell in this item.
	 *
	 * @param index the column index
	 * @return the receiver's font
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public Font getFont(int index) {
		checkWidget();
		if (index < 0 || index > (fonts.size() - 1)) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		return fonts.get(index);
	}

	/**
	 *
	 * Returns the foreground color of the receiver.
	 *
	 * @return the foreground color
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public Color getForeground() {
		checkWidget();
		return foreground;
	}

	/**
	 *
	 * Returns the foreground color at the given column index in the receiver.
	 *
	 * @param index the column index
	 * @return the foreground color
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public Color getForeground(int index) {
		checkWidget();
		if (index < 0 || index > (foregroundColors.size() - 1)) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		return foregroundColors.get(index);
	}

	/**
	 * Returns the image stored at the given column index in the receiver,
	 * or null if the image has not been set or if the column does not exist.
	 *
	 * @param index the column index
	 * @return the image stored at the given column index in the receiver
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public Image getImage(int index) {
		checkWidget();
		if (index < 0 || index > (images.size() - 1)) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		return images.get(index);
	}

	/**
	 * Returns the text stored at the given column index in the receiver,
	 * or empty string if the text has not been set.
	 *
	 * @param index the column index
	 * @return the text stored at the given column index in the receiver
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public String getText(int index) {
		checkWidget();
		if (index < 0 || index > (texts.size() - 1)) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		return texts.get(index);
	}

	/**
	 * Sets the background color at the given column index in the receiver
	 * to the color specified by the argument, or to the default system color for the item
	 * if the argument is null.
	 *
	 * @param index the column index
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
	 *
	 * @since 3.1
	 *
	 */
	public void setBackground(int index, Color color) {
		checkWidget();
		if (color != null && color.isDisposed()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}

		ensureArraySizes(index);
		backgroundColors.set(index, color);
	}

	private void ensureArraySizes(int index) {
		backgroundColors.ensureCapacity(index);
		foregroundColors.ensureCapacity(index);
		fonts.ensureCapacity(index);
		images.ensureCapacity(index);
		texts.ensureCapacity(index);
		bounds.ensureCapacity(index);
		textbounds.ensureCapacity(index);
		imageBounds.ensureCapacity(index);
	}

	/**
	 * Sets the background color at in the receiver to the color specified by the argument,
	 * or to the default system color for the item if the argument is null.
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
	 *
	 * @since 3.1
	 *
	 */
	public void setBackground(Color color) {
		checkWidget();
		if (color != null && color.isDisposed()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		this.background = color;
	}

	/**
	 * Sets the font that the receiver will use to paint textual information
	 * for the item to the font specified by the argument, or to the default
	 * font for that kind of control if the argument is null.
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
	 *
	 * @since 3.1
	 */
	public void setFont(Font font) {
		checkWidget();
		this.font = font;
	}

	/**
	 * Sets the font that the receiver will use to paint textual information
	 * for the specified cell in this item to the font specified by the
	 * argument, or to the default font for that kind of control if the
	 * argument is null.
	 *
	 * @param index the column index
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
	 *
	 * @since 3.1
	 */
	public void setFont(int index, Font font) {
		checkWidget();
		if (font != null && font.isDisposed()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		ensureArraySizes(index);
		fonts.set(index, font);
	}

	/**
	 * Sets the foreground color at the given column index in the receiver
	 * to the color specified by the argument, or to the default system color for the item
	 * if the argument is null.
	 *
	 * @param index the column index
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
	public void setForeground(int index, Color color) {
		checkWidget();
		if (color != null && color.isDisposed()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		ensureArraySizes(index);
		foregroundColors.set(index, color);
	}

	/**
	 * Sets the foreground color in the receiver to the color specified by the argument,
	 * or to the default system color for the item if the argument is null.
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
	public void setForeground(Color color) {
		checkWidget();
		if (color != null && color.isDisposed()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		this.foreground = color;
	}

	/**
	 * Sets the receiver's image at a column.
	 *
	 * @param index the column index
	 * @param image the new image
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the image has been disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setImage(int index, Image image) {
		checkWidget();
		if (image != null && image.isDisposed()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		ensureArraySizes(index);
		images.set(index, image);
	}

	/**
	 * Sets the receiver's text at a column
	 * <p>
	 * Note: If control characters like '\n', '\t' etc. are used
	 * in the string, then the behavior is platform dependent.
	 * </p>
	 * 
	 * @param index the column index
	 * @param string the new text
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the text is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setText(int index, String string) {
		checkWidget();
		ensureArraySizes(index);
		texts.set(index, string == null ? "" : string);
	}

	/**
	 * Returns the receiver's parent item, which must be a
	 * <code>CTreeComboItem</code> or null when the receiver is a
	 * root.
	 *
	 * @return the receiver's parent item
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public CTreeComboItem getParentItem() {
		checkWidget();
		return parentItem;
	}

	/**
	 * Returns <code>true</code> if the receiver is expanded,
	 * and false otherwise.
	 * <p>
	 *
	 * @return the expanded state
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public boolean getExpanded() {
		checkWidget();
		if (checkRealItem()) {
			return realTreeItem.getExpanded();
		}
		return false;
	}

	/**
	 * Returns the number of items contained in the receiver
	 * that are direct item children of the receiver.
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
		return childItems.size();
	}

	/**
	 * Returns the item at the given, zero-relative index in the
	 * receiver. Throws an exception if the index is out of range.
	 *
	 * @param index the index of the item to return
	 * @return the item at the given index
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
	public CTreeComboItem getItem(int index) {
		checkWidget();
		return childItems.get(index);
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
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the item is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the item has been disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public int indexOf(CTreeComboItem item) {
		checkWidget();
		return childItems.indexOf(item);
	}

	/**
	 * Returns a rectangle describing the size and location
	 * relative to its parent of the text at a column in the
	 * tree.
	 *
	 * @param index the index that specifies the column
	 * @return the receiver's bounding text rectangle
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public Rectangle getTextBounds(int index) {
		checkWidget();
		if (index < 0 || index > (textbounds.size() - 1)) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		return textbounds.get(index);
	}

	/**
	 * Returns a rectangle describing the size and location
	 * relative to its parent of an image at a column in the
	 * tree.
	 *
	 * @param index the index that specifies the column
	 * @return the receiver's bounding image rectangle
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public Rectangle getImageBounds(int index) {
		checkWidget();
		if (index < 0 || index > (imageBounds.size() - 1)) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		return imageBounds.get(index);
	}

	/**
	 * Sets the expanded state of the receiver.
	 * <p>
	 *
	 * @param expanded the new expanded state
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setExpanded(boolean expand) {
		checkWidget();
		if (checkRealItem()) {
			realTreeItem.setExpanded(expand);
		}
	}

	/**
	 * Sets the number of child items contained in the receiver.
	 *
	 * @param count the number of items
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setItemCount(int count) {
		checkWidget();
		if (checkRealItem()) {
			realTreeItem.setItemCount(count);
		}
	}

	/**
	 * Clears the item at the given zero-relative index in the receiver.
	 * The text, icon and other attributes of the item are set to the default
	 * value. If the tree was created with the <code>SWT.VIRTUAL</code> style,
	 * these attributes are requested again as needed.
	 *
	 * @param index the index of the item to clear
	 * @param all <code>true</code> if all child items of the indexed item should be
	 *            cleared recursively, and <code>false</code> otherwise
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
	 *
	 * @see SWT#VIRTUAL
	 * @see SWT#SetData
	 */
	public void clear(int index, boolean all) {
		checkWidget();
		realTreeItem.clear(index, all);
	}

	/**
	 * Clears all the items in the receiver. The text, icon and other
	 * attributes of the items are set to their default values. If the
	 * tree was created with the <code>SWT.VIRTUAL</code> style, these
	 * attributes are requested again as needed.
	 *
	 * @param all <code>true</code> if all child items should be cleared
	 *            recursively, and <code>false</code> otherwise
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see SWT#VIRTUAL
	 * @see SWT#SetData
	 */
	public void clearAll(boolean all) {
		checkWidget();
		realTreeItem.clearAll(all);
	}

	public void buildRealTreeItem(Tree tree, int numberOfColumns) {
		final TreeItem ti;
		if (parentItem != null && parentItem.realTreeItem != null) {
			ti = new TreeItem(parentItem.realTreeItem, getStyle());
		} else {
			ti = new TreeItem(tree, getStyle());
		}

		if (getImage() != null) {
			ti.setImage(getImage());
		}
		if (getText() != null) {
			ti.setText(getText());
		}
		if (getBackground() != null) {
			ti.setBackground(getBackground());
		}
		if (getForeground() != null) {
			ti.setForeground(getForeground());
		}
		if (getFont() != null) {
			ti.setFont(getFont());
		}
		
		for (int i = 0; i < numberOfColumns; i++) {
			if (getFont(i) != null) {
				ti.setFont(i, getFont(i));
			}
			if (getBackground(i) != null) {
				ti.setBackground(i, getBackground(i));
			}
			if (getForeground(i) != null) {
				ti.setForeground(i, getForeground(i));
			}
			if (getImage(i) != null) {
				ti.setImage(i, getImage(i));
			}
			if (getText(i) != null) {
				ti.setText(i, getText(i));
			}
		}
		setRealTreeItem(ti);
	}
}