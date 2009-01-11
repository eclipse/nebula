/****************************************************************************
 * Copyright (c) 2008 Jeremy Dowdall
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/

package org.eclipse.nebula.widgets.cdatetime;

import java.util.Calendar;

import org.eclipse.swt.SWT;

public class Footer {

	static final int CLEAR = 0;
	static final int TODAY = 1;
	static final int VERBOSE_TODAY = 2;
	
	public static Footer Clear() {
		return new Footer(CLEAR, Calendar.YEAR, Calendar.MONTH, Calendar.DATE);
	}
	
	public static Footer Today() {
		return new Footer(TODAY, Calendar.YEAR, Calendar.MONTH, Calendar.DATE);
	}
	
	public static Footer VerboseToday() {
		return new Footer(VERBOSE_TODAY, Calendar.YEAR, Calendar.MONTH, Calendar.DATE);
	}

	int type;
	int[] fields;
	int textAlignment = SWT.CENTER;
	int alignment = SWT.FILL;
	boolean grab = false;
	boolean readOnly = false;
	
	private Footer(int type, int... fields) {
		this.type = type;
		this.fields = fields;
	}
	
	public Footer align(int alignment) {
		this.alignment = alignment;
		return this;
	}

	public Footer align(int textAlignment, int controlAlignment, boolean grab) {
		this.textAlignment = textAlignment;
		this.alignment = controlAlignment;
		this.grab = grab;
		return this;
	}

	public Footer grab() {
		grab = true;
		return this;
	}

}
