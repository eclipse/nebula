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

import org.eclipse.swt.graphics.Color;

/**
 * Evaluation object containing information about evaluation text and text font color.
 */
public class Evaluation {
	/** The evaluation text. */
	public String text;
	/** The evaluation color. */
	public Color color;

	/**
	 * Evaluation object.
	 * 
	 * @param text The evaluation text
	 * @param color the text font color
	 */
	public Evaluation(String text, Color color) {
		this.text = text;
		this.color = color;
	}

}
