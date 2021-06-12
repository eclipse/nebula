/*******************************************************************************
 * Copyright (c) 2011-2021 Laurent CARON
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API
 * and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.duallist;

import org.eclipse.nebula.widgets.opal.commons.OpalItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * Instances of this class represents items manipulated by this DualList widget
 */
public class DLItem extends OpalItem {

	public enum LAST_ACTION {
		NONE, SELECTION, DESELECTION
	};

	private LAST_ACTION lastAction;

	/**
	 * Constructor
	 *
	 * @param text the text displayed in the DualList widget for this item
	 */
	public DLItem(final String text) {
		this(text, null);
	}

	/**
	 * Constructor
	 *
	 * @param text the text displayed in the DualList widget for this item
	 * @param image the image displayed in the DualList widget for this item
	 */
	public DLItem(final String text, final Image image) {
		this(text, image, (Font) null, (Color) null);
	}

	/**
	 * Constructor
	 *
	 * @param text the text displayed in the DualList widget for this item
	 * @param image the image displayed in the DualList widget for this item
	 * @param font the font displayed in the DualList widget for this item
	 * @param foregroundColor the foreground color displayed in the DualList widget
	 *            for this item
	 */
	public DLItem(final String text, final Image image, final Font font, final Color foregroundColor) {
		setText(text);
		setImage(image);
		setFont(font);
		setForeground(foregroundColor);
		lastAction = LAST_ACTION.NONE;
	}

	/**
	 * Constructor
	 *
	 * @param text the text displayed in the DualList widget for this item
	 * @param image the image displayed in the DualList widget for this item
	 * @param foregroundColor the foreground color displayed in the DualList widget
	 *            for this item
	 * @param backgroundColor the background color displayed in the DualList widget
	 *            for this item
	 */
	public DLItem(final String text, final Image image, final Color foregroundColor, final Color backgroundColor) {
		setText(text);
		setImage(image);
		setForeground(foregroundColor);
		setBackground(backgroundColor);
		lastAction = LAST_ACTION.NONE;
	}

	/**
	 * Constructor
	 *
	 * @param text the text displayed in the DualList widget for this item
	 * @param image the image displayed in the DualList widget for this item
	 * @param font the font displayed in the DualList widget for this item
	 */
	public DLItem(final String text, final Image image, final Font font) {
		this(text, image, font, null);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.commons.OpalItem#getHeight()
	 */
	@Override
	public int getHeight() {
		throw new UnsupportedOperationException("DLItem does not support this method");
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.commons.OpalItem#setHeight(int)
	 */
	@Override
	public void setHeight(final int height) {
		throw new UnsupportedOperationException("DLItem does not support this method");
	}

	/**
	 * @return the last action (NONE, SELECTION, DESELECTION)
	 */
	public LAST_ACTION getLastAction() {
		return lastAction;
	}

	/**
	 * @param lastAction the last action performed on this DLItem
	 * @return 
	 */
	public DLItem setLastAction(final LAST_ACTION lastAction) {
		this.lastAction = lastAction;
		return this;
	}

	/**
	 * Change the selection state of this item
	 * 
	 * @param selection
	 * @return 
	 */
	public DLItem setSelected(boolean selection) {
		this.lastAction = selection ? LAST_ACTION.SELECTION : LAST_ACTION.DESELECTION;
		return this;
	}
}
