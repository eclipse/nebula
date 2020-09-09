/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributor: Laurent CARON (laurent.caron@gmail.com) - initial API and
 * implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.promptsupport;

import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.nebula.widgets.opal.promptsupport.PromptSupport.FocusBehavior;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Control;

/**
 * Abstract class that contains code for the FocusLost, FocusGained and
 * ControlResized events
 */
abstract class BaseFocusControlListener implements FocusListener, ControlListener {

	protected Control control;
	private boolean firstDraw;
	private Font initialFont;
	private Color initialBackgroundColor;
	private Color initialForegroundColor;

	protected static final String EMPTY_STRING = "";

	/**
	 * Constructor
	 *
	 * @param control control on which this listener will be attached
	 */
	BaseFocusControlListener(final Control control) {
		this.control = control;
		storeInitialLook();
		firstDraw = true;
		PromptSupport.setPromptDisplayed(control, false);
	}

	/**
	 * @see org.eclipse.swt.events.FocusListener#focusGained(org.eclipse.swt.events.FocusEvent)
	 */
	@Override
	public void focusGained(final FocusEvent e) {
		if (isFilled()) {
			// Widget not empty
			PromptSupport.setPromptDisplayed(control, false);
			return;
		}
		applyInitialLook();
		PromptSupport.setPromptDisplayed(control, true);
		if (PromptSupport.getFocusBehavior(control) == FocusBehavior.HIDE_PROMPT) {
			hidePrompt();
		} else {
			highLightPrompt();
		}
	}

	/**
	 * Apply the initial look of the widget
	 */
    protected void applyInitialLook() {
		control.setFont(initialFont);
		control.setBackground(initialBackgroundColor);
		control.setForeground(initialForegroundColor);
	}

    /**
     * Apply the prompt look of the widget
     */
    protected void applyPromptLook() {
        final Font font = SWTGraphicUtil.buildFontFrom(control, PromptSupport.getFontStyle(control));
        control.setFont(font);
        SWTGraphicUtil.addDisposer(control, font);
        control.setBackground(PromptSupport.getBackground(control));
        control.setForeground(PromptSupport.getForeground(control));
    }

	/**
	 * Code when the focus behiaviour is "Hide"
	 */
	protected abstract void hidePrompt();

	/**
	 * Code when the focus behiaviour is "Highlight"
	 */
	protected abstract void highLightPrompt();

	/**
	 * @see org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt.events.FocusEvent)
	 */
	@Override
	public void focusLost(final FocusEvent e) {
		if (isFilled()) {
			return;
		}

		storeInitialLook();
        applyPromptLook();
		fillPromptText();
        PromptSupport.setPromptDisplayed(control, true);
	}

	/**
	 * @return <code>true</code> if the widget is filled, <code>false</code>
	 *         otherwise
	 */
	protected abstract boolean isFilled();

	/**
	 * Fill the prompt text
	 */
	protected abstract void fillPromptText();

	/**
	 * @see org.eclipse.swt.events.ControlListener#controlMoved(org.eclipse.swt.events.ControlEvent)
	 */
	@Override
	public void controlMoved(final ControlEvent e) {
	}

	/**
	 * @see org.eclipse.swt.events.ControlListener#controlResized(org.eclipse.swt.events.ControlEvent)
	 */
	@Override
	public void controlResized(final ControlEvent e) {
		if (firstDraw) {
			firstDraw = true;
			focusLost(null);
		}
	}

	/**
	 * Store the initial look of the widget
	 */
    protected void storeInitialLook() {
		initialFont = control.getFont();
		initialBackgroundColor = control.getBackground();
		initialForegroundColor = control.getForeground();
	}

    /**
     * Attach listeners to the control
     */
    void hookControl() {
        control.addFocusListener(this);
        control.addControlListener(this);
    }
}
