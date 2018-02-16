/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Laurent CARON (laurent.caron@gmail.com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.promptsupport;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * This utility class allows the user to add a prompt to a text or combo
 * component (see http://designinginterfaces.com/Input_Prompt).<br/>
 * This class is inspired by work of Peter Weishapl
 */
public class PromptSupport {
	public static enum FocusBehavior {
		/**
		 * Highlight the prompt text as it would be selected.
		 */
		HIGHLIGHT_PROMPT,
		/**
		 * Hide the prompt text.
		 */
		HIDE_PROMPT
	};

	private static final String KEY = "org.eclipse.nebula.widgets.opal.promptsupport.PromptSupport";
	static final String BACKGROUND = KEY + ".background";
	static final String FOREGROUND = KEY + ".foreground";
	static final String STYLE = KEY + ".style";
	static final String BEHAVIOR = KEY + ".behavior";
	static final String PROMPT = KEY + ".prompt";
	static final String SET = KEY + ".set";

	/**
	 * <p>
	 * Convenience method to set the <code>promptText</code> and
	 * <code>promptTextColor</code> on a {@link Control}.
	 * </p>
	 *
	 * @param promptText Prompt Text
	 * @param promptForeground Foreground
	 * @param promptBackground Background
	 * @param control control
	 * @exception IllegalArgumentException if the control is not a Text Box, a
	 *                Combo Box, a StyledText or a CCombo
	 */
	public static void init(final String promptText, final Color promptForeground, final Color promptBackground, final Control control) {
		if (promptText != null && promptText.length() > 0) {
			setPrompt(promptText, control);
		}
		if (promptForeground != null) {
			setForeground(promptForeground, control);
		}
		if (promptBackground != null) {
			setBackground(promptBackground, control);
		}
	}

	/**
	 * Get the background color of the <code>control</code>, when no text is
	 * present. If no color has been set, the <code>control</code> background
	 * color will be returned.
	 *
	 * @param textComponent
	 * @return the the background color of the text component, when no text is
	 *         present
	 */
	public static Color getBackground(final Control control) {
		final Color temp = (Color) control.getData(BACKGROUND);
		return temp == null ? control.getBackground() : temp;
	}

	/**
	 * <p>
	 * Sets the prompts background color on <code>control</code>. This
	 * background color will only be used when no text is present.
	 * </p>
	 *
	 * @param background
	 * @param control
	 * @exception IllegalArgumentException if the control is not a Text Box, a
	 *                Combo Box, a StyledText or a CCombo
	 */
	public static void setBackground(final Color color, final Control control) {
		checkControl(control);
		control.setData(BACKGROUND, color);
	}

	/**
	 * Get the {@link FocusBehavior} of <code>control</code>.
	 *
	 * @param control
	 * @return the {@link FocusBehavior} or {@link FocusBehavior#HIDE_PROMPT} if
	 *         none is set
	 */
	public static FocusBehavior getFocusBehavior(final Control control) {
		final FocusBehavior temp = (FocusBehavior) control.getData(BEHAVIOR);
		return temp == null ? FocusBehavior.HIDE_PROMPT : temp;

	}

	/**
	 * Sets the {@link FocusBehavior} on <code>control</code>, if it is the
	 * focus owner.
	 *
	 * @param focusBehavior
	 * @param control
	 * @exception IllegalArgumentException if the control is not a Text Box, a
	 *                Combo Box, a StyledText or a CCombo
	 */
	public static void setFocusBehavior(final FocusBehavior focusBehavior, final Control control) {
		checkControl(control);
		control.setData(BEHAVIOR, focusBehavior);
	}

	/**
	 * Returns the font style of the prompt text, which is a OR mix of
	 * SWT.ITALIC, SWT.NONE or SWT.BOLD
	 *
	 * @param control
	 * @return font style of the prompt text
	 */
	public static int getFontStyle(final Control control) {
		final Integer temp = (Integer) control.getData(STYLE);
		return temp == null ? SWT.ITALIC : temp;

	}

	/**
	 * <p>
	 * Set the style of the prompt font, which is a OR mix of SWT.ITALIC,
	 * SWT.NONE or SWT.BOLD
	 * </p>
	 *
	 * @param fontStyle
	 * @param control
	 * @exception IllegalArgumentException if the control is not a Text Box, a
	 *                Combo Box, a StyledText or a CCombo
	 */
	public static void setFontStyle(final int fontStyle, final Control control) {
		checkControl(control);
		control.setData(STYLE, fontStyle);
	}

	/**
	 * Get the foreground color of the prompt text. If no color has been set,
	 * the <code>GREY</code> color will be returned.
	 *
	 * @param color
	 * @return the color of the prompt text or <code>GREY</code>if none is set
	 */
	public static Color getForeground(final Control control) {
		final Color temp = (Color) control.getData(FOREGROUND);
		return temp == null ? control.getForeground() : temp;

	}

	/**
	 * Sets the foreground color of the prompt on <code>control</code>. This
	 * color will be used when no text is present.
	 *
	 * @param promptTextColor
	 * @param textComponent
	 * @exception IllegalArgumentException if the control is not a Text Box, a
	 *                Combo Box, a StyledText or a CCombo
	 */
	public static void setForeground(final Color color, final Control control) {
		checkControl(control);
		control.setData(FOREGROUND, color);
	}

	/**
	 * Get the prompt text of <code>control</code>.
	 *
	 * @param control
	 * @return the prompt text
	 */
	public static String getPrompt(final Control control) {
		return (String) control.getData(PROMPT);
	}

	/**
	 * <p>
	 * Sets the prompt text on <code>control</code>
	 * </p>
	 *
	 * @param promptText
	 * @param textComponent
	 * @exception IllegalArgumentException if the control is not a Text Box, a
	 *                Combo Box, a StyledText or a CCombo
	 */
	public static void setPrompt(final String promptText, final Control control) {
		checkControl(control);

		final boolean alreadySet = control.getData(SET) == null ? false : (Boolean) control.getData(SET);
		if (alreadySet) {
			throw new IllegalArgumentException("A prompt has already been set on this control !");
		}
		control.setData(PROMPT, promptText);

		final BaseFocusControlListener focusControlListener = FocusControlListenerFactory.getFocusControlListenerFor(control);
		control.addFocusListener(focusControlListener);
		control.addControlListener(focusControlListener);
		control.setData(SET, true);
	}

	/**
	 * Check if the control is a Text, a Combo, a StyledText or a CCombo
	 *
	 * @param control control to check
	 */
	private static void checkControl(final Control control) {
		if (!(control instanceof Text) && !(control instanceof Combo) && !(control instanceof StyledText) && !(control instanceof CCombo)) {
			throw new IllegalArgumentException("PromptSupport can only be used on a Text, a Combo, a StyledText or a CCombo widget.");
		}
	}
}
