/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Laurent CARON (laurent.caron at gmail dot com) - Initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.promptsupport;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * This is a factory of focus/control listeners
 * 
 */
class FocusControlListenerFactory {

	/**
	 * @param control control on which the listener will be added
	 * @return a BaseControlFocus Listener that can be attached to the events
	 *         focusLost, focusGained and controlResized
	 */
	static BaseFocusControlListener<?> getFocusControlListenerFor(final Control control) {
		if (control instanceof Combo) {
			return new ComboFocusControlListener((Combo) control);
		}
		if (control instanceof CCombo) {
			return new CComboFocusControlListener((CCombo) control);
		}

		if (control instanceof Text) {
			return new TextFocusControlListener((Text) control);
		}

		if (control instanceof StyledText) {
			return new StyledTextFocusControlListener((StyledText) control);
		}
		throw new IllegalArgumentException("Control should be a Text, a Combo, a CCombo or a StyledText widget");
	}

}
