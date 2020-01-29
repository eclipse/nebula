/*******************************************************************************
 * Copyright (c) 2012 Laurent CARON
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

import java.math.BigDecimal;

import org.eclipse.nebula.widgets.opal.commons.ResourceManager;

/**
 * This is the calculator engine
 */
class CalculatorEngine {

	static final String OPERATOR_PLUS = "+";
	static final String OPERATOR_MINUS = "-";
	static final String OPERATOR_MULTIPLY = "*";
	static final String OPERATOR_DIVIDE = "/";

	private String lastOperator;
	private final CalculatorButtonsComposite composite;
	private boolean error;
	private Double lastNumber;

	/**
	 * Constructor
	 *
	 * @param calculator calculator widget associated to this engine
	 */
	CalculatorEngine(final CalculatorButtonsComposite composite) {
		this.composite = composite;
	}

	/**
	 * @param value value to display
	 */
	private void setContent(final Double value) {
		composite.getDisplayArea().setText(doubleToString(value));
		composite.fireModifyListeners();
	}

	private String doubleToString(final Double d) {
		if (d == null) {
			return "0";
		}
		// pre java 8, a value of 0 would yield "0.0" below
		if (d.doubleValue() == 0) {
			return "0";
		}

		if (Math.floor(d) == d) {
			return Integer.toString(d.intValue());
		}

		return new BigDecimal(d.toString()).stripTrailingZeros().toPlainString();
	}

	/**
	 * @return the displayed value as string
	 */
	private Double getContent() {
		final String content = composite.getDisplayArea().getText();
		return Double.valueOf(content);
	}

	/**
	 * Process equals operation
	 */
	void processEquals() {
		if (error) {
			return;
		}
		if (lastOperator == null) {
			return;
		}

		double result = 0;
		final double content = getContent();

		if (lastOperator.equals(OPERATOR_DIVIDE)) {
			if (content == 0) {
				displayErrorMessage(ResourceManager.CALCULATOR_DIVIDE_BY_ZERO);
				return;
			}
			result = lastNumber / content;
		}

		if (lastOperator.equals(OPERATOR_MULTIPLY)) {
			result = lastNumber * content;
		}

		if (lastOperator.equals(OPERATOR_MINUS)) {
			result = lastNumber - content;
		}

		if (lastOperator.equals(OPERATOR_PLUS)) {
			result = lastNumber + content;
		}

		setContent(result);
		lastOperator = null;
		lastNumber = result;
	}

	/**
	 * @param errorMessage error message
	 */
	private void displayErrorMessage(final String errorMessage) {
		composite.getDisplayArea().setText(ResourceManager.getLabel(errorMessage));
		lastOperator = null;
		lastNumber = null;
		error = true;
	}

	/**
	 * Process 1/x operation
	 */
	void processInverseOperation() {
		if (error) {
			return;
		}
		processEquals();
		try {
			final double result = 1d / getContent();
			setContent(result);
		} catch (final Exception ex) {
			displayErrorMessage(ResourceManager.CALCULATOR_DIVIDE_BY_ZERO);
		}
	}

	/**
	 * @param operator operation to process
	 */
	void processOperation(final String operator) {
		if (error) {
			return;
		}
		lastOperator = operator;
		lastNumber = getContent();
		composite.setReadyToEnterNewNumber(true);
	}

	/**
	 * Process percentage operation
	 */
	void processPerCentageOperation() {
		if (error) {
			return;
		}
		final double result = getContent();
		setContent(result / 100d);
	}

	/**
	 * Process +/- operation
	 */
	void processSignChange() {
		if (error) {
			return;
		}
		final double result = getContent();
		setContent(result * -1d);
	}

	/**
	 * Process square root operation
	 */
	void processSquareRootOperation() {
		if (error) {
			return;
		}
		processEquals();
		try {
			final double result = Math.sqrt(getContent());
			setContent(result);
		} catch (final Exception ex) {
			displayErrorMessage(ResourceManager.CALCULATOR_INVALID_VALUE);
		}
	}

	public void cancel() {
		lastOperator = null;
		error = false;
		composite.setReadyToEnterNewNumber(false);
		lastNumber = null;
	}

}
