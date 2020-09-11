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
 * Laurent CARON (laurent.caron at gmail dot com) - Initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.promptsupport;

import org.eclipse.swt.custom.CCombo;

/**
 * Focus/Control listener for a CCombo widget
 */
class CComboFocusControlListener extends BaseFocusControlListener {

	/**
	 * Constructor
	 *
	 * @param control control on which this listener will be attached
	 */
	public CComboFocusControlListener(final CCombo control) {
		super(control);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.promptsupport.BaseFocusControlListener#hidePrompt()
	 */
	@Override
	protected void hidePrompt() {
		((CCombo) control).setText(EMPTY_STRING);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.promptsupport.BaseFocusControlListener#highLightPrompt()
	 */
	@Override
	protected void highLightPrompt() {
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.promptsupport.BaseFocusControlListener#fillPromptText()
	 */
	@Override
	protected void fillPromptText() {
		final String promptText = PromptSupport.getPrompt(control);
		if (promptText != null) {
			((CCombo) control).setText(promptText);
		}
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.promptsupport.BaseFocusControlListener#isFilled()
	 */
	@Override
	protected boolean isFilled() {
		final String promptText = PromptSupport.getPrompt(control);
		final String trimmedText = ((CCombo) control).getText().trim();
        if (promptText != null && promptText.equals(trimmedText) && PromptSupport.isPromptDisplayed(control)) {
			return false;
		}
		return !EMPTY_STRING.equals(trimmedText);
	}

}
