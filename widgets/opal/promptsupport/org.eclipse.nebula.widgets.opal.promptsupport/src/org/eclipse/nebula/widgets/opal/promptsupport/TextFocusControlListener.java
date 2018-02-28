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

import org.eclipse.swt.widgets.Text;

/**
 * Focus/Control listener for a Text widget
 */
class TextFocusControlListener extends BaseFocusControlListener {

	/**
	 * Constructor
	 *
	 * @param control control on which this listener will be attached
	 */
	public TextFocusControlListener(final Text control) {
		super(control);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.promptsupport.BaseFocusControlListener#hidePrompt()
	 */
	@Override
	protected void hidePrompt() {
		((Text) control).setText(EMPTY_STRING);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.promptsupport.BaseFocusControlListener#highLightPrompt()
	 */
	@Override
	protected void highLightPrompt() {
		// If we do a select all directly, it's not working !
		control.getDisplay().asyncExec(() -> {
			((Text) TextFocusControlListener.this.control).selectAll();
		});
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.promptsupport.BaseFocusControlListener#fillPromptText()
	 */
	@Override
	protected void fillPromptText() {
		final String promptText = PromptSupport.getPrompt(control);
		if (promptText != null) {
			((Text) control).setText(promptText);
		}
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.promptsupport.BaseFocusControlListener#isFilled()
	 */
	@Override
	protected boolean isFilled() {
		final String promptText = PromptSupport.getPrompt(control);
		if (promptText != null && promptText.equals(((Text) control).getText().trim())) {
			return false;
		}
		return !EMPTY_STRING.equals(((Text) control).getText().trim());
	}

}
