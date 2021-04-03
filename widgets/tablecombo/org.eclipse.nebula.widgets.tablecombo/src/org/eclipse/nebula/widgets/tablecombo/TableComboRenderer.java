/*******************************************************************************
 * Copyright (c) 2011-2021 Nebula Team.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.tablecombo;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * Renderer used to display the "text" part of the <code>TableCombo</code>
 * widget
 */
public interface TableComboRenderer {
	/**
	 * Returns the text of the given element at position
	 * <code>selectionIndex</code>.
	 * 
	 * @param selectionIndex
	 *            index of the selected element
	 * @return the text
	 */
	String getLabel(int selectionIndex);

	/**
	 * Returns the image of the given element at position
	 * <code>selectionIndex</code>.
	 * 
	 * @param selectionIndex
	 *            index of the selected element
	 * @return the image (or <code>null</code> if there is no image)
	 */
	Image getImage(int selectionIndex);

	/**
	 * Returns the background color of the given element at position
	 * <code>selectionIndex</code>.
	 * 
	 * @param selectionIndex
	 *            index of the selected element
	 * @return the background color (or null if one wants to keep the default color)
	 */
	Color getBackground(int selectionIndex);

	/**
	 * Returns the foreground color of the given element at position
	 * <code>selectionIndex</code>.
	 * 
	 * @param selectionIndex
	 *            index of the selected element *
	 * @return the foreground color (or null if one wants to keep the default color)
	 */
	Color getForeground(int selectionIndex);

	/**
	 * Returns the font of the given element at position
	 * <code>selectionIndex</code>.
	 * 
	 * @param selectionIndex
	 *            index of the selected element
	 * @return the font (or null if one wants to keep the default font)
	 */
	Font getFont(int selectionIndex);
}
