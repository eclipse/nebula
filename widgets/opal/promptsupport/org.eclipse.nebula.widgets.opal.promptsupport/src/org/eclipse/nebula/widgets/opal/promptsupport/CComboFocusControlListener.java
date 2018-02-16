/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
		if (promptText != null && promptText.equals(((CCombo) control).getText().trim())) {
			return false;
		}
		return !EMPTY_STRING.equals(((CCombo) control).getText().trim());
	}

}
