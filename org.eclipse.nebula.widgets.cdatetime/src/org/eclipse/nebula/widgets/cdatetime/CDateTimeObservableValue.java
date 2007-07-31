/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Peter Centgraf - initial API and implementation - bug #177013
 *******************************************************************************/
package org.eclipse.nebula.widgets.cdatetime;

import java.util.Date;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.internal.databinding.provisional.swt.AbstractSWTObservableValue;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;

/**
 * An implementation of the DataBindings IObservableValue interface for the Nebula
 * CDateTime control.
 * 
 * @author pcentgraf
 * @since Mar 8, 2007
 */
public class CDateTimeObservableValue extends AbstractSWTObservableValue
		implements IObservableValue {
	
	/**
	 * The Control being observed here.
	 */
	protected final CDateTime dateTime;
	
	/**
	 * Flag to prevent infinite recursion in {@link #doSetValue(Object)}.
	 */
	protected boolean updating = false;
	
	/**
	 * The "old" selection before a selection event is fired.
	 */
	protected Date currentSelection;

	/**
	 * Observe the selection property of the provided CDateTime control.
	 * 
	 * @param dateTime the control to observe
	 */
	public CDateTimeObservableValue(CDateTime dateTime) {
		super(dateTime);
		this.dateTime = dateTime;
		currentSelection = dateTime.getSelection();
		dateTime.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				update(e);
			}
			public void widgetSelected(SelectionEvent e) {
				update(e);
			}
			public void update(SelectionEvent e) {
				if (!updating) {
					Date newSelection = CDateTimeObservableValue.this.dateTime.getSelection();
					fireValueChange(Diffs.createValueDiff(currentSelection, newSelection));
					currentSelection = newSelection;
				}
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.internal.databinding.provisional.swt.AbstractSWTObservableValue#getValueType()
	 */
	public Object getValueType() {
		return Date.class;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.databinding.observable.value.AbstractObservableValue#doGetValue()
	 */
	protected Object doGetValue() {
		return dateTime.getSelection();
	}

	protected void doSetValue(Object value) {
		Date oldValue;
		Date newValue;
		try {
			updating = true;
			oldValue = dateTime.getSelection();
			newValue = (Date) value;
			dateTime.setSelection(newValue);
			currentSelection = newValue;
			fireValueChange(Diffs.createValueDiff(oldValue, newValue));
		} finally {
			updating = false;
		}
	}
}
