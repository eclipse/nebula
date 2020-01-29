/*******************************************************************************
 * Copyright (c) 2009 Matthew Hall and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 293508)
 *******************************************************************************/
package org.eclipse.nebula.widgets.radiogroup.forms;

import org.eclipse.nebula.widgets.radiogroup.RadioGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Convenience class for creating RadioGroups in Eclipse forms.
 * 
 * @author Matthew Hall
 */
public class RadioGroupFormToolkit {
	/**
	 * Creates a radio group as a part of the specified form.
	 * 
	 * @param formToolkit
	 *            the form to which the radio group belongs
	 * @param parent
	 *            the radio group parent
	 * @param style
	 *            the radio group style (for example, <code>SWT.BORDER</code>)
	 * @return the button widget
	 */
	public static RadioGroup createRadioGroup(FormToolkit formToolkit,
			Composite parent, int style) {
		RadioGroup group = new RadioGroup(parent, style | SWT.FLAT);
		formToolkit.adapt(group, true, false);
		return group;
	}
}
