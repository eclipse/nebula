/*******************************************************************************
 * Copyright (c) 2006 Chris Gross.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: schtoo@schtoo.com(Chris Gross) - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.pshelf;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;

/**
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p>
 *
 * Instances of this class represent an individual shelf.  A shelf is a container widget similar
 * to a tab folder but with a list-like visualization.
 * <p>
 * Add controls to a shelf item by constructing them on the body (<code>getBody</code>) of the item.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 */
public class PShelfItem extends Item {

	private Composite body;
	private Composite bodyParent;
	private PShelf parent;

    /**
     * Constructs a new instance of this class given its parent
     * (which must be a <code>PShelf</code>), a style value
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
     *
     * @param parent a composite control which will be the parent of the new instance (cannot be null)
     * @param style the style of control to construct
     * @param index the zero-relative index to store the receiver in its parent
     *
     * @exception IllegalArgumentException <ul>
     *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
     *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the parent (inclusive)</li>
     * </ul>
     * @exception SWTException <ul>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
     *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
     * </ul>
     */
	public PShelfItem(PShelf parent, int style,int index) {
		super(parent,style,index);

        if (index < 0 || index > parent.getItems().length)
            SWT.error(SWT.ERROR_INVALID_RANGE);

		construct(parent,index);
	}

    /**
     * Constructs a new instance of this class given its parent
     * (which must be a <code>PShelf</code>) and a style value
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
     * @exception IllegalArgumentException <ul>
     *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
     * </ul>
     * @exception SWTException <ul>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
     *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
     * </ul>
     */
	public PShelfItem(PShelf parent, int style) {
		super(parent,style);
		construct(parent,-1);
	}

	private void construct(PShelf parent,int index){
		this.parent = parent;

		bodyParent = new Composite(parent,SWT.NONE);
		body = new Composite(bodyParent,SWT.NONE);

		parent.createItem(this,index);
	}

    /**
     * Returns the client area of the shelf.  Users should add controls and a layout to the body.
     * <p>
     * @return the body composite
     *
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
	public Composite getBody() {
        checkWidget();
		return body;
	}

	Composite getBodyParent() {
		return bodyParent;
	}

	/**
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	@Override
	public void dispose() {
        checkWidget();
		parent.removeItem(this);
        bodyParent.dispose();
		super.dispose();
	}

    /**
     * @see org.eclipse.swt.widgets.Item#setImage(org.eclipse.swt.graphics.Image)
     */
    @Override
	public void setImage(Image image)
    {
        super.setImage(image);
        parent.computeItemHeight();
        parent.onResize();
        parent.redraw();
    }

    /**
     * @see org.eclipse.swt.widgets.Item#setText(java.lang.String)
     */
    @Override
	public void setText(String string)
    {
        super.setText(string);
        parent.redraw();
    }


}
