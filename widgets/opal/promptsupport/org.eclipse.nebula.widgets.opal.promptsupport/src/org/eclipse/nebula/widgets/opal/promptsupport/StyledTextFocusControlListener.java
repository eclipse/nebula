/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *

 * Contributors:
 *     Laurent CARON (laurent.caron at gmail dot com) - Initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.promptsupport;

import org.eclipse.swt.custom.StyledText;

/**
 * Focus/Control listener for a StyledText widget
 */
class StyledTextFocusControlListener extends BaseFocusControlListener {

	/**
	 * Constructor
	 * 
	 * @param control control on which this listener will be attached
	 */
	public StyledTextFocusControlListener(final StyledText control) {
		super(control);
	}

	/**
	 * @see org.mihalis.opal.promptSupport.BaseFocusControlListener#hidePrompt()
	 */
	@Override
	protected void hidePrompt() {
		((StyledText) this.control).setText(EMPTY_STRING);
	}

	/**
	 * @see org.mihalis.opal.promptSupport.BaseFocusControlListener#highLightPrompt()
	 */
	@Override
	protected void highLightPrompt() {
		this.control.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				((StyledText) StyledTextFocusControlListener.this.control).selectAll();

			}
		});
	}

	/**
	 * @see org.mihalis.opal.promptSupport.BaseFocusControlListener#fillPromptText()
	 */
	@Override
	protected void fillPromptText() {
		final String promptText = PromptSupport.getPrompt(this.control);
		if (promptText != null) {
			((StyledText) this.control).setText(promptText);
		}

	}

	/**
	 * @see org.mihalis.opal.promptSupport.BaseFocusControlListener#isFilled()
	 */
	@Override
	protected boolean isFilled() {
		final String promptText = PromptSupport.getPrompt(this.control);
		if (promptText != null && promptText.equals(((StyledText) this.control).getText().trim())) {
			return false;
		}
		return !EMPTY_STRING.equals(((StyledText) this.control).getText().trim());
	}

}
