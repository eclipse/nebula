/*******************************************************************************
 * Copyright (c) 2018 Akuiteo (http://www.akuiteo.com). All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API and
 * implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.badgedlabel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

/**
 * This enumeration provides a set of color for the BadgedLabel control
 */
public enum BADGE_COLOR {
	BLUE(0, 123, 255, SWT.COLOR_WHITE), //
	GREY(108, 117, 125, SWT.COLOR_WHITE), //
	GREEN(40, 167, 69, SWT.COLOR_WHITE), //
	RED(220, 53, 69, SWT.COLOR_WHITE), //
	YELLOW(255, 193, 7, SWT.COLOR_BLACK), //
	CYAN(23, 162, 184, SWT.COLOR_WHITE), //
	BLACK(52, 58, 64, SWT.COLOR_WHITE);

	private RGB rgb;
	private int textColor;

	private BADGE_COLOR(int red, int green, int blue, int textColor) {
		rgb = new RGB(red, green, blue);
		this.textColor = textColor;
	}

	public RGB getRgb() {
		return rgb;
	}

	public int getTextColor() {
		return textColor;
	}

}
