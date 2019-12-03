/*******************************************************************************
* Copyright (C) 2019 Uenal Akkaya <uenal.akkaya@sap.com>, Michael Gutfleisch <michael.gutfleisch@sap.com>
*
* This program and the accompanying materials
* are made available under the terms of the Eclipse Public License 2.0
* which accompanies this distribution, and is available at
* https://www.eclipse.org/legal/epl-2.0/
* 
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     Uenal Akkaya
*     Michael Gutfleisch
 ******************************************************************************/
package org.eclipse.nebula.widgets.TextFieldWithEvaluation;

/**
 * The IEvaluator is used to evaluate a given text.
 */
public interface IEvaluator {

	/**
	 * This method evaluates the given text.
	 * 
	 * @param text The text to evaluate
	 * @return {@link Evaluation} object
	 */
	Evaluation evaluate(String text);
	
	/**
	 * This method checks if the given text is valid.
	 * 
	 * @param text the text to validate
	 * @return true if the text is valid
	 */
	boolean isValid(String text);

}
