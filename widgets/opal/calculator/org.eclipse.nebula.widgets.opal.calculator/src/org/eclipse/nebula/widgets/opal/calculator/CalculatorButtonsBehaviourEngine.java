/*******************************************************************************
 * Copyright (c) 2012-2017 Laurent CARON
 * All rights reserved.
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
package org.eclipse.nebula.widgets.opal.calculator;

public class CalculatorButtonsBehaviourEngine {

	private final CalculatorButtonsComposite composite;

	public CalculatorButtonsBehaviourEngine(final CalculatorButtonsComposite composite) {
		this.composite = composite;
	}

	public void processBackSpace() {
		if (composite.isReadyToEnterNewNumber()) {
			return;
		}
		final String content = getContent();
		if (content.length() < 2) {
			return;

		}
		final String newContent = content.substring(0, content.length() - 2);
		setContent(newContent.length() == 0 ? "0" : newContent);
		composite.setReadyToEnterNewNumber(false);
	}

	private String getContent() {
		return composite.getDisplayArea().getText();
	}

	private void setContent(final String newContent) {
		composite.getDisplayArea().setText(newContent);
		composite.fireModifyListeners();
	}

	public void clearResult() {
		setContent("0");
	}

	public void addDecimalPoint() {
		if (composite.isReadyToEnterNewNumber()) {
			return;
		}
		final String content = getContent();
		if (content.indexOf('.') > 0) {
			return;
		}
		final String newContent = content + ".";
		setContent(newContent);
	}

	public void addDigitToDisplay(final int digit) {
		final String content = getContent();
		if (composite.isReadyToEnterNewNumber()) {
			setContent(String.valueOf(digit));
			return;
		}
		String newContent;
		if (content.length() == 1) {
			if (digit == 0) {
				return;
			}
			if (getContent().equals("0")) {
				newContent = String.valueOf(digit);
			} else {
				newContent = content + String.valueOf(digit);
			}
		} else {
			newContent = content + String.valueOf(digit);
		}
		setContent(newContent);
	}

}
