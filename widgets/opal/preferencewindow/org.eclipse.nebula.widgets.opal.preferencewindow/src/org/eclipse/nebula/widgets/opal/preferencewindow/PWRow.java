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

import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWButton;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWLabel;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Instances of this class are rows
 */
public class PWRow extends PWRowGroup {
	protected final List<PWWidget> widgets;

	/**
	 * Constructor
	 */
	public PWRow() {
		this.widgets = new ArrayList<PWWidget>();
	}

	@Override
	public PWContainer add(final PWWidget widget) {
		this.widgets.add(widget);
		addColumn(widget.getNumberOfColumns());
		return this;
	}

	@Override
	public PWContainer add(final PWContainer element) {
		if (element instanceof PWRow || element instanceof PWGroup) {
			return this.parent.add(element);
		} else {
			throw new UnsupportedOperationException("Can only add a PWGroup or a PWRow.");
		}
	}

	@Override
	public void build(final Composite parent) {
		final int size = this.widgets.size();
		int columIndex = 0;
		for (int i = 0; i < size; i++) {
			final PWWidget widget = this.widgets.get(i);
			final Control control = widget.checkAndBuild(parent);
			if (control != null && control.getLayoutData() == null) {
				final int colSpan;
				final boolean grabExcessSpace;
				final int alignment;
				if (size == 1) {
					if (widget.isSingleWidget()) {
						colSpan = this.parentNumberOfColums;
					} else {
						colSpan = this.parentNumberOfColums - widget.getNumberOfColumns() + 1;
					}
					grabExcessSpace = true;
				} else {
					if (i == size - 1) {
						colSpan = this.parentNumberOfColums - columIndex;
						grabExcessSpace = widget.isGrabExcessSpace();
					} else {
						colSpan = 1;
						grabExcessSpace = widget instanceof PWButton && i == 0 ? true : widget.isGrabExcessSpace();
					}
				}
				columIndex += widget.getNumberOfColumns();

				if (i == 0 && grabExcessSpace && size > 1) {
					if (widget instanceof PWLabel || widget instanceof PWButton) {
						alignment = GridData.END;
					} else {
						alignment = GridData.BEGINNING;
					}
				} else {
					alignment = widget.getAlignment();
				}

				final GridData gd = new GridData(alignment, GridData.BEGINNING, grabExcessSpace, false, colSpan, 1);
				gd.horizontalIndent = widget.getIndent();
				gd.minimumWidth = widget.getWidth();
				if (widget.getHeight() != -1) {
					gd.minimumHeight = widget.getHeight();
				}
				control.setLayoutData(gd);
			}
		}
	}

	@Override
	protected void checkParent(final PWContainer parent) {
		if (parent instanceof PWTab || parent instanceof PWGroup) {
			return;
		}
		throw new UnsupportedOperationException("Bad parent, should be only PWTab or PWGroup");
	}

	@Override
	public void enableOrDisable() {
		if (this.enabler == null) {
			return;
		}

		final boolean enabled = this.enabler.isEnabled();
		for (final PWWidget widget : this.widgets) {
			final boolean widgetEnable = widget.enableOrDisable();
			for (final Control c : widget.getControls()) {
				if (!c.isDisposed()) {
					c.setEnabled(enabled && widgetEnable);
				}
			}
		}
	}

}
