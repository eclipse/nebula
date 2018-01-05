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

import org.eclipse.swt.widgets.Combo;

/**
 * Focus/Control listener for a Combo widget
 */
class ComboFocusControlListener extends BaseFocusControlListener {

	/**
	 * Constructor
	 * 
	 * @param control control on which this listener will be attached
	 */
	public ComboFocusControlListener(final Combo control) {
		super(control);
	}

	/**
	 * @see org.mihalis.opal.promptSupport.BaseFocusControlListener#hidePrompt()
	 */
	@Override
	protected void hidePrompt() {
		((Combo) this.control).setText("");
	}

	/**
	 * @see org.mihalis.opal.promptSupport.BaseFocusControlListener#highLightPrompt()
	 */
	@Override
	protected void highLightPrompt() {
	}

	/**
	 * @see org.mihalis.opal.promptSupport.BaseFocusControlListener#fillPromptText()
	 */
	@Override
	protected void fillPromptText() {
		final String promptText = PromptSupport.getPrompt(this.control);
		if (promptText != null) {
			this.control.getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					((Combo) ComboFocusControlListener.this.control).setText(promptText);
				}
			});
		}
	}

	/**
	 * @see org.mihalis.opal.promptSupport.BaseFocusControlListener#isFilled()
	 */
	@Override
	protected boolean isFilled() {
		final String promptText = PromptSupport.getPrompt(this.control);
		if (promptText != null && promptText.equals(((Combo) this.control).getText().trim())) {
			return false;
		}
		return !"".equals(((Combo) this.control).getText().trim());
	}
}
