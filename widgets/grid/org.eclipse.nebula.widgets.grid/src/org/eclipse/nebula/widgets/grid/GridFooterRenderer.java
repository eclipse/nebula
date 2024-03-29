/*******************************************************************************
 * Copyright (c) 2009 BestSolution.at
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    tom.schindl@bestsolution.at - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.grid;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;

/**
 * <p>
 * NOTE: THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT. THIS IS A
 * PRE-RELEASE ALPHA VERSION. USERS SHOULD EXPECT API CHANGES IN FUTURE
 * VERSIONS.
 * </p>
 * The super class for all grid header renderers. Contains the properties
 * specific to a grid header.
 *
 * @author chris.gross@us.ibm.com
 */
public abstract class GridFooterRenderer extends AbstractInternalWidget {

	/**
	 * Truncation style
	 */
	protected int truncationStyle = SWT.CENTER;

	/**
	 * Returns the bounds of the text in the cell. This is used when displaying
	 * in-place tooltips. If <code>null</code> is returned here, in-place tooltips
	 * will not be displayed. If the <code>preferred</code> argument is
	 * <code>true</code> then the returned bounds should be large enough to show the
	 * entire text. If <code>preferred</code> is <code>false</code> then the
	 * returned bounds should be be relative to the current bounds.
	 *
	 * @param value
	 *            the object being rendered.
	 * @param preferred
	 *            true if the preferred width of the text should be returned.
	 * @return bounds of the text.
	 */
	public Rectangle getTextBounds(Object value, boolean preferred) {
		return null;
	}

	/**
	 * Get the truncation style
	 *
	 * @return the truncation style.
	 */
	public int getTruncationStyle() {
		return truncationStyle;
	}

	/**
	 * Set the truncation style to use when cell content is too large.
	 *
	 * @see SWT#LEFT
	 * @see SWT#CENTER
	 * @see SWT#RIGHT
	 * @param truncationStyle
	 */
	public void setTruncationStyle(int truncationStyle) {
		this.truncationStyle = truncationStyle;
	}
}
