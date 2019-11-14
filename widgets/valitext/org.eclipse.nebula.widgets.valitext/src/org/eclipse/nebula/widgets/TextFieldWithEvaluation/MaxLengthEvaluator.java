/*******************************************************************************
* Copyright (C) 2019 Uenal Akkaya <uenal.akkaya@sap.com>, Michael Gutfleisch <michael.gutfleisch@sap.com>
*
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     Uenal Akkaya
*     Michael Gutfleisch
 ******************************************************************************/
package org.eclipse.nebula.widgets.TextFieldWithEvaluation;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * An implementation of the interface {@link IEvaluator} which evaluates the
 * length of a given string.
 *
 */
public class MaxLengthEvaluator implements IEvaluator {

	private int maxLength;
	private Color validColor = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
	private Color invalidColor = Display.getCurrent().getSystemColor(SWT.COLOR_RED);

	/**
	 * Constructor with the maximum length restriction.
	 * 
	 * @param maxLength
	 *            the maximum allowed length
	 */
	public MaxLengthEvaluator(int maxLength) {
		this.maxLength = maxLength;
	}

	/**
	 * Constructor with the maximum length restriction and the colors which should
	 * be used for valid/invalid texts.
	 * 
	 * @param maxLength
	 *            the maximum allowed length
	 * @param validColor
	 *            the color to be used if text is valid
	 * @param invalidColor
	 *            the color to be used if text is invalid
	 */
	public MaxLengthEvaluator(int maxLength, Color validColor, Color invalidColor) {
		this.maxLength = maxLength;
		this.setValidColor(validColor);
		this.invalidColor = invalidColor;
	}

	@Override
	public Evaluation evaluate(String text) {
		int length = text.length();

		String evaluationText = length + " / " + maxLength;
		Color evaluationTextColor = length > maxLength ? getInvalidColor() : getValidColor();

		return new Evaluation(evaluationText, evaluationTextColor);
	}

	@Override
	public boolean isValid(String text) {
		return text.length() <= maxLength;
	}

	/**
	 * Returns the current valid color.
	 * 
	 * @return the validColor
	 */
	public Color getValidColor() {
		return validColor;
	}

	/**
	 * Sets valid color.
	 * 
	 * @param validColor
	 *            the validColor to set
	 */
	public void setValidColor(Color validColor) {
		this.validColor = validColor;
	}

	/**
	 * Returns the current invalid color.
	 * 
	 * @return the invalidColor
	 */
	public Color getInvalidColor() {
		return invalidColor;
	}

	/**
	 * Sets invalid color.
	 * 
	 * @param validColor
	 *            the invalidColor to set
	 */
	public void setInvalidColor(Color invalidColor) {
		this.invalidColor = invalidColor;
	}

}
