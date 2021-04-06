/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Peter Centgraf - initial API and implementation - bug #177013
 *     Jeremy Dowdall - updating
 *******************************************************************************/
package org.eclipse.nebula.jface.cdatetime;

import java.util.Date;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

/**
 * An implementation of the DataBindings IObservableValue interface for the
 * Nebula CDateTime control.
 * 
 * @author pcentgraf
 * @since Mar 8, 2007
 */
public class CDateTimeObservableValue extends AbstractObservableValue {

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

	private SelectionListener listener = new SelectionListener() {
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			if (!updating) {
				Date newSelection = CDateTimeObservableValue.this.dateTime.getSelection();
				if (((newSelection != null) && !newSelection.equals(currentSelection)) 
						|| ((currentSelection != null) && !currentSelection.equals(newSelection))) {
					
					fireValueChange(Diffs.createValueDiff(currentSelection,	newSelection));
				}
				currentSelection = newSelection;
			}
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			if (!updating) {
				Date newSelection = CDateTimeObservableValue.this.dateTime.getSelection();
				if (((newSelection != null) && !newSelection.equals(currentSelection)) 
						|| ((currentSelection != null) && !currentSelection.equals(newSelection))) {
					
					fireValueChange(Diffs.createValueDiff(currentSelection, newSelection));
				}
				currentSelection = newSelection;
			}
		}
	};

	/**
	 * Observe the selection property of the provided CDateTime control.
	 * 
	 * @param dateTime
	 *            the control to observe
	 */
	public CDateTimeObservableValue(CDateTime dateTime) {
		this.dateTime = dateTime;
		currentSelection = dateTime.getSelection();
		this.dateTime.addSelectionListener(listener);
	}

	/**
	 * Observe the selection property of the provided CDateTime control.
	 * 
	 * @param dateTime
	 *            the control to observe
	 * @param realm
	 *            the {@link Realm} to use
	 */
	public CDateTimeObservableValue(CDateTime dateTime, Realm realm) {
		super(realm);
		this.dateTime = dateTime;
		currentSelection = dateTime.getSelection();
		this.dateTime.addSelectionListener(listener);
	}

	@Override
	public synchronized void dispose() {
		dateTime.removeSelectionListener(listener);
		super.dispose();
	}

	@Override
	protected Object doGetValue() {
		if (!dateTime.isDisposed()) {
			return dateTime.getSelection();
		}
		return null;
	}

	@Override
	protected void doSetValue(Object value) {
		if ((value instanceof Date || value == null) && !dateTime.isDisposed()) {
			Date oldValue;
			Date newValue;
			try {
				updating = true;
				oldValue = dateTime.getSelection();
				newValue = (Date) value;
				dateTime.setSelection(newValue);
				currentSelection = newValue;
				if (((oldValue != null) && !oldValue.equals(newValue)) 
						|| ((newValue != null) && !newValue.equals(oldValue))) {
					
					fireValueChange(Diffs.createValueDiff(oldValue, newValue));
				}
			} finally {
				updating = false;
			}
		}
	}

	@Override
	public Object getValueType() {
		return Date.class;
	}

}
