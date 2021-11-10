/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Laurent CARON (laurent.caron at gmail dot com) - Initial implementation and API
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.preferencewindow;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Instance of this class are tabs
 * 
 */
public class PWTab extends PWContainer {
	private final Image image;
	private final String text;
	private final List<PWRowGroup> children;

	/**
	 * Constructor
	 * 
	 * @param image image associated to the tab
	 * @param text text associated to the tab
	 */
	PWTab(final Image image, final String text) {
		this.image = image;
		this.text = text;
		this.children = new ArrayList<PWRowGroup>();
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.PWContainer#add(org.eclipse.nebula.widgets.opal.preferencewindow.PWContainer)
	 */
	@Override
	public PWContainer add(final PWContainer element) {
		if (!(element instanceof PWGroup) && !(element instanceof PWRow)) {
			throw new UnsupportedOperationException("Can only add a PWGroup or a PWRow.");
		}
		((PWRowGroup) element).setParent(this);
		this.children.add((PWRowGroup) element);
		return this;
	}

	@Override
	public PWContainer add(final PWWidget widget) {
		final PWRow row = new PWRow();
		row.setParent(this);
		row.add(widget);
		this.children.add(row);
		return this;
	}

	@Override
	public void build(final Composite parent) {
		final int numberOfColumns = computeNumberOfColums();
		parent.setLayout(new GridLayout(numberOfColumns, false));

		for (final PWRowGroup rowGroup : this.children) {
			rowGroup.setParentNumberOfColumns(numberOfColumns);
			rowGroup.build(parent);
		}

		PreferenceWindow.getInstance().fireEnablers();

	}

	/**
	 * @return the total number of columns in this tab
	 */
	private int computeNumberOfColums() {
		int numberOfColumns = 1;
		for (final PWRowGroup rowGroup : this.children) {
			if (rowGroup instanceof PWRow) {
				numberOfColumns = Math.max(numberOfColumns, rowGroup.getNumberOfColums());
			}
		}
		return numberOfColumns;
	}

	/**
	 * @return the image associate to this tab
	 */
	public Image getImage() {
		return this.image;
	}

	/**
	 * @return the text associated to this tab
	 */
	public String getText() {
		return this.text;
	}

}
