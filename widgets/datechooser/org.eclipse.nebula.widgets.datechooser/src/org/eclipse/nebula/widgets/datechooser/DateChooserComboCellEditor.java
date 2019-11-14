/*******************************************************************************
 * Copyright (c) 2009 Eric Wuillai.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eric Wuillai (eric@wdev91.com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.datechooser;

import java.util.Date;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * A cell editor that manages a date field, using a DateChooserCombo.
 * The cell editor's value is a Date.
 */
public class DateChooserComboCellEditor extends CellEditor {
	/** The DateChooserCombo control */
	protected DateChooserCombo combo;

	/**
   * Default DateChooserComboCellEditor style
   * specify no borders on the widget as cell outline in table already
   * provides the look of a border.
   */
  private static final int defaultStyle = SWT.SINGLE;

  /**
   * Creates a new date cell editor parented under the given control.
   *
   * @param parent the parent control
   */
	public DateChooserComboCellEditor(Composite parent) {
		this(parent, defaultStyle);
	}

  /**
   * Creates a new date string cell editor parented under the given control.
   *
   * @param parent the parent control
   * @param style the style bits
   */
	public DateChooserComboCellEditor(Composite parent, int style) {
		super(parent, style);
		setValueValid(true);
	}

  /* (non-Javadoc)
   * Method declared on CellEditor.
   */
	protected Control createControl(Composite parent) {
		combo = new DateChooserCombo(parent, getStyle());
		combo.setFont(parent.getFont());

		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				switch ( event.type ) {
					case SWT.Traverse :
						if ( event.detail == SWT.TRAVERSE_ESCAPE
								 || event.detail == SWT.TRAVERSE_RETURN ) {
							event.doit = false;
						}
						break;

					case SWT.FocusOut :
						DateChooserComboCellEditor.this.focusLost();
						break;
				}				
			}
		};
		combo.addListener(SWT.Traverse, listener);
		combo.addListener(SWT.FocusOut, listener);

		return combo;
	}

  /**
   * The <code>DateChooserComboCellEditor</code> implementation of
   * this <code>CellEditor</code> framework method returns
   * the date value.
   *
   * @return the date value
   */
	protected Object doGetValue() {
		return combo.getValue();
	}

  /* (non-Javadoc)
   * Method declared on CellEditor.
   */
	protected void doSetFocus() {
//		combo.selectAll();
		combo.setFocus();
	}

  /**
   * The <code>DateChooserComboCellEditor</code> implementation of
   * this <code>CellEditor</code> framework method accepts
   * a date value (type <code>Date</code>).
   *
   * @param value a date value
   */
	protected void doSetValue(Object value) {
		if ( combo == null || combo.isDisposed() ) SWT.error(SWT.ERROR_WIDGET_DISPOSED);
		if ( value instanceof Date ) {
			combo.setValue((Date) value);
		}
	}

	/**
	 * Returns the wrapped DateChooserCombo widget. This allows to customize the
	 * display of the DateChooser in the popup.
	 * 
	 * @return the wrapped combo
	 */
	public DateChooserCombo getCombo() {
		return combo;
	}

	/**
	 * Returns a layout data object for this cell editor. This is called each
	 * time the cell editor is activated and controls the layout of the SWT
	 * table editor.
	 * <p>
	 * Since a text editor field is scrollable we don't set a minimumSize,
	 * except that the minimum width is 60 pixels to make sure the arrow
	 * button and some text is visible.
	 * </p>
	 *
	 * @return the layout data object
	 */
	public LayoutData getLayoutData() {
		LayoutData layoutData = new LayoutData();
		if ( combo == null || combo.isDisposed() ) {
			layoutData.minimumWidth = 60;
		}
		return layoutData;
	}
}
