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

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.Widget;

/**
 * Instances of this class represent a column in a ctreecombo widget.
 * <p><dl>
 * <dt><b>Styles:</b></dt>
 * <dd>LEFT, RIGHT, CENTER</dd>
 * <dt><b>Events:</b></dt>
 * <dd> Move, Resize, Selection</dd>
 * </dl>
 * </p><p>
 * Note: Only one of the styles LEFT, RIGHT and CENTER may be specified.
 * </p><p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 *
 * @noextend This class is not intended to be subclassed by clients.
 */

public class CTreeComboColumn extends Item {
	private TreeColumn realTreeColumn;
	private CTreeCombo parent;
	private int width;

	/**
	 * Constructs a new instance of this class given its parent
	 * (which must be a <code>Tree</code>) and a style value
	 * describing its behavior and appearance. The item is added
	 * to the end of the items maintained by its parent.
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
	 * @see SWT#LEFT
	 * @see SWT#RIGHT
	 * @see SWT#CENTER
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public CTreeComboColumn(CTreeCombo parent, int style) {
		super(parent, style);
		this.parent = parent;
		this.parent.columns.add(this);

		if (this.parent.tree != null && !this.parent.tree.isDisposed()) {
			setRealTreeColumn(new TreeColumn(parent.tree, style));
		}
	}

	/**
	 * Constructs a new instance of this class given its parent
	 * (which must be a <code>Tree</code>), a style value
	 * describing its behavior and appearance, and the index
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
	 * <p>
	 * Note that due to a restriction on some platforms, the first column
	 * is always left aligned.
	 * </p>
	 * 
	 * @param parent a composite control which will be the parent of the new instance (cannot be null)
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
	 * @see SWT#LEFT
	 * @see SWT#RIGHT
	 * @see SWT#CENTER
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public CTreeComboColumn(CTreeCombo parent, int style, int index) {
		super(parent, style, index);
		this.parent = parent;
		this.parent.columns.add(this);

		if (this.parent.tree != null && !this.parent.tree.isDisposed()) {
			setRealTreeColumn(new TreeColumn(parent.tree, style, index));
		}
	}

	void setRealTreeColumn(TreeColumn realTreeColumn) {
		this.realTreeColumn = realTreeColumn;
		this.realTreeColumn.setText(getText());
		this.realTreeColumn.setWidth(width);
	}

	TreeColumn getRealTreeColumn() {
		return realTreeColumn;
	}

	private boolean checkRealColumn() {
		return realTreeColumn != null && !realTreeColumn.isDisposed();
	}

	/**
	 * Gets the width of the receiver.
	 *
	 * @return the width
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public int getWidth() {
		checkWidget();
		if (checkRealColumn()) {
			return realTreeColumn.getWidth();
		}
		return 0;
	}

	/**
	 * Sets the width of the receiver.
	 *
	 * @param width the new width
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setWidth(int width) {
		checkWidget();
		this.width = width;
		realTreeColumn.setWidth(width);
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
		checkWidget();
		super.setText(string);
		realTreeColumn.setText(string);
	}

}
