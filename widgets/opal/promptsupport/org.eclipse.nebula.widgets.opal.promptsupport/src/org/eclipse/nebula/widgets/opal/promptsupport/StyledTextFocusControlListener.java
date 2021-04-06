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
 *
 * Contributors:
 * Laurent CARON (laurent.caron at gmail dot com) - Initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.promptsupport;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

/**
 * Focus/Control listener for a StyledText widget
 */
class StyledTextFocusControlListener extends BaseFocusControlListener<StyledText> implements ModifyListener {

    protected boolean updatingPropmpt = false;

	/**
	 * Constructor
	 *
	 * @param control control on which this listener will be attached
	 */
	public StyledTextFocusControlListener(final StyledText control) {
		super(control);
	}

    @Override
    void hookControl() {
        super.hookControl();

        // Attach dedicated listeners
        control.addModifyListener(this);
    }

    @Override
    public void modifyText(ModifyEvent e) {
        if (updatingPropmpt) {
            return;
        }

        final String trimmedText = control.getText().trim();
        applyInitialLook();

        if (!EMPTY_STRING.equals(trimmedText)) {
            PromptSupport.setPromptDisplayed(control, false);
            return;
        }

        if (!control.isFocusControl()) {
            storeInitialLook();
            applyPromptLook();
            fillPromptText();
            PromptSupport.setPromptDisplayed(control, true);
            return;
        }
    }

    /**
     * @see org.eclipse.nebula.widgets.opal.promptsupport.BaseFocusControlListener#hidePrompt()
     */
	@Override
	protected void hidePrompt() {
        updatePrompt(EMPTY_STRING);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.promptsupport.BaseFocusControlListener#highLightPrompt()
	 */
	@Override
	protected void highLightPrompt() {
		control.getDisplay().asyncExec(() -> {
            if (control.isDisposed()) {
                return;
            }

            control.selectAll();
		});
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.promptsupport.BaseFocusControlListener#fillPromptText()
	 */
	@Override
	protected void fillPromptText() {
		final String promptText = PromptSupport.getPrompt(control);
		if (promptText != null) {
            updatePrompt(promptText);
		}

	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.promptsupport.BaseFocusControlListener#isFilled()
	 */
	@Override
	protected boolean isFilled() {
		final String promptText = PromptSupport.getPrompt(control);
        final String trimmedText = control.getText().trim();
        if (promptText != null && promptText.equals(trimmedText) && PromptSupport.isPromptDisplayed(control)) {
			return false;
		}
		return !EMPTY_STRING.equals(trimmedText);
	}

    protected void updatePrompt(String prompt) {
        try {
            updatingPropmpt = true;
            control.setText(prompt);
        } finally {
            updatingPropmpt = false;
        }
    }
}
