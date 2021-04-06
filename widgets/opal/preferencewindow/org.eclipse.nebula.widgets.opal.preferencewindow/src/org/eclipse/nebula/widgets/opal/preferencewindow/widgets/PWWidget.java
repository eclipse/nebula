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
package org.eclipse.nebula.widgets.opal.preferencewindow.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.opal.preferencewindow.enabler.Enabler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * This class is the root class for all widgets that take part of a preference
 * window
 * 
 */
public abstract class PWWidget {
	private final String propertyKey;
	private final String label;
	protected Enabler enabler;
	private final List<Control> controls;

	private int alignment = GridData.BEGINNING;
	private int indent = 0;
	private int width = 100;
	private int height = -1;
	protected int numberOfColumns = 1;
	private boolean grabExcessSpace = false;

	private boolean singleWidget = false;

	/**
	 * Constructor
	 * 
	 * @param label label associated to the widget
	 * @param propertyKey property key binded to the widget
	 * @param numberOfColumns number of columns taken by the widget
	 * @param singleWidget if true, the widget is supposed to be "alone" (used
	 *            for placement)
	 */
	protected PWWidget(final String label, final String propertyKey, final int numberOfColumns, final boolean singleWidget) {
		this.label = label;
		this.propertyKey = propertyKey;
		this.numberOfColumns = numberOfColumns;
		this.singleWidget = singleWidget;
		this.controls = new ArrayList<Control>();
	}

	/**
	 * Build the widget
	 * 
	 * @param parent parent composite
	 * @return the created control
	 */
	protected abstract Control build(Composite parent);

	/**
	 * Build the label associated to the widget
	 * 
	 * @param parent parent composite
	 * @param verticalAlignment vertical alignment
	 */
	protected void buildLabel(final Composite parent, final int verticalAlignment) {
		if (getLabel() != null) {
			final Label label = new Label(parent, SWT.NONE);
			label.setText(getLabel());
			final GridData labelGridData = new GridData(GridData.END, verticalAlignment, false, false);
			labelGridData.horizontalIndent = getIndent();
			label.setLayoutData(labelGridData);
			addControl(label);
		}
	}

	/**
	 * Check if the property can be binded to the widget
	 * 
	 * @throws UnsupportedOperationException if the property could not be binded
	 *             to the widget
	 */
	protected abstract void check();

	/**
	 * Check if the property can be binded to the widget, then build the widget
	 * 
	 * @param parent parent composite
	 * @return the created control
	 */
	public Control checkAndBuild(final Composite parent) {
		check();
		return build(parent);
	}

	/**
	 * Enable or disable the widget, depending on the associated enabler
	 */
	public boolean enableOrDisable() {
		if (this.enabler == null) {
			return true;
		}

		final boolean enabled = this.enabler.isEnabled();
		for (final Control c : this.controls) {
			if (!c.isDisposed()) {
				c.setEnabled(enabled);
			}
		}
		return enabled;
	}

	// ------------------------------- getters & setters

	/**
	 * @return the alignment (GridData.BEGINNING, GridData.CENTER, GridData.END,
	 *         GridData.FILL)
	 */
	public int getAlignment() {
		return this.alignment;
	}

	/**
	 * @return the list of controls contained in the widget
	 */
	public List<Control> getControls() {
		return this.controls;
	}

	/**
	 * @return <code>true</code> if the widget should grab the excess space
	 */
	public boolean isGrabExcessSpace() {
		return this.grabExcessSpace;
	}

	/**
	 * @return the height of the widget
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * @return the indentation space of the widget
	 */
	public int getIndent() {
		return this.indent;
	}

	/**
	 * @return the label associated to the widget (may be <code>null</code>)
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * @return the number of columns associated to the widget
	 */
	public int getNumberOfColumns() {
		return this.numberOfColumns;
	}

	/**
	 * @return the propertyKey associated to the widget
	 */
	String getPropertyKey() {
		return this.propertyKey;
	}

	/**
	 * @return the width of the widget
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * @return <code>true</code> if the widget is "alone"
	 */
	public boolean isSingleWidget() {
		return this.singleWidget;
	}

	/**
	 * Adds a control to the list of control contained in the widget
	 * 
	 * @param control control to add
	 */
	protected void addControl(final Control control) {
		this.controls.add(control);
	}

	/**
	 * @param alignment the alignment to set (GridData.BEGINNING,
	 *            GridData.CENTER, GridData.END, GridData.FILL)
	 * @return the widget
	 */
	public PWWidget setAlignment(final int alignment) {
		if (alignment != GridData.BEGINNING && alignment != GridData.CENTER && alignment != GridData.END && alignment != GridData.FILL) {
			throw new UnsupportedOperationException("Value should be one of the following :GridData.BEGINNING, GridData.CENTER, GridData.END, GridData.FILL");
		}
		this.alignment = alignment;
		return this;
	}

	/**
	 * @param enabler the enabler to set
	 * @return the widget
	 */
	public PWWidget setEnabler(final Enabler enabler) {
		this.enabler = enabler;
		this.enabler.injectWidget(this);
		return this;
	}

	/**
	 * @param grabExcessSpace true if you want the widget to grab the excess
	 *            space
	 * @return the widget
	 */
	public PWWidget setGrabExcessSpace(final boolean grabExcessSpace) {
		this.grabExcessSpace = grabExcessSpace;
		return this;
	}

	/**
	 * @param height the height to set
	 * @return the widget
	 */
	public PWWidget setHeight(final int height) {
		this.height = height;
		return this;

	}

	/**
	 * @param indent the indentation space to set
	 * @return the widget
	 */
	public PWWidget setIndent(final int indent) {
		this.indent = indent;
		return this;
	}

	/**
	 * @param width the width to set
	 * @return the widget
	 */
	public PWWidget setWidth(final int width) {
		this.width = width;
		return this;
	}

}
