/*******************************************************************************
 * Copyright (c) 2019  Peter Pfeifer
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *      Peter Pfeifer - initial API and implementation - bug #279782
 *******************************************************************************/
package org.eclipse.nebula.jface.cdatetime;
import java.util.Date;

import org.eclipse.jface.databinding.swt.WidgetValueProperty;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;


public class CDateTimeValueProperty extends WidgetValueProperty {

	public static final String EDITABLE = "editable";

	String property = "date";

	/**
	 * 
	 */
	public CDateTimeValueProperty() {
		super(new int[] { SWT.Selection, SWT.Modify });
	}

	/**
	 * @param property
	 */
	public CDateTimeValueProperty(String property) {
		this();
		this.property = property;
	}

	/** 
	 * @see org.eclipse.core.databinding.property.value.SimpleValueProperty#doGetValue(java.lang.Object)
	 */
	@Override
	protected Object doGetValue(Object source) {
		CDateTime dateTime = (CDateTime) source;
		// just in case editable property is bound
		if (CDateTimeValueProperty.EDITABLE.equals(this.property)) {
			return dateTime.getEditable();
		} else {
			return dateTime.getSelection();
		}
	}


	/** 
	 * @see org.eclipse.core.databinding.property.value.SimpleValueProperty#doSetValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected void doSetValue(Object source, Object value) {
		CDateTime dateTime = (CDateTime) source;
		// just in case editable property is bound
		if (CDateTimeValueProperty.EDITABLE.equals(this.property)) {
			dateTime.setEditable((Boolean) value);
		} else {
			dateTime.setSelection((Date) value);
		}
	}

	/** 
	 * @see org.eclipse.core.databinding.property.value.IValueProperty#getValueType()
	 */
	public Object getValueType() {
		// just in case editable property is bound
		if (CDateTimeValueProperty.EDITABLE.equals(this.property)) {
			return Boolean.class;
		}
		return Date.class;
	}
}
