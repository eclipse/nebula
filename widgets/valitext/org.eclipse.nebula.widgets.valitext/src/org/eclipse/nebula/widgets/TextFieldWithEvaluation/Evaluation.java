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
