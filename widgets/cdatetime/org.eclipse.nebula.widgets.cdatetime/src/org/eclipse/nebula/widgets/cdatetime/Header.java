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

public class Header {

	static final int DATE_NOW = 0;
	static final int MONTH_PREV = 1;
	static final int MONTH_NEXT = 2;
	static final int YEAR_PREV = 3;
	static final int YEAR_NEXT = 4;
	static final int MONTH = 5;
	static final int YEAR = 6;
	static final int TIME = 7;
	
	public static Header DateNow() {
		return new Header(DATE_NOW, Calendar.YEAR, Calendar.MONTH);
	}
	
	public static Header Month() {
		return new Header(MONTH, Calendar.MONTH);
	}

	public static Header MonthNext() {
		return new Header(MONTH_NEXT, Calendar.MONTH);
	}
	
	public static Header MonthPrev() {
		return new Header(MONTH_PREV, Calendar.MONTH);
	}
	
	public static Header Time() {
		return new Header(TIME, Calendar.HOUR, Calendar.HOUR_OF_DAY, Calendar.MINUTE);
	}
	
	public static Header Year() {
		return new Header(YEAR, Calendar.YEAR);
	}
	
	public static Header YearNext() {
		return new Header(YEAR_NEXT, Calendar.YEAR);
	}
	
	public static Header YearPrev() {
		return new Header(YEAR_PREV, Calendar.YEAR);
	}

	int type;
	int[] fields;
	int textAlignment = SWT.CENTER;
	int alignment = SWT.FILL;
	boolean grab = false;
	boolean readOnly = false;
	
	private Header(int type, int... field) {
		this.type = type;
		this.fields = field;
	}
	
	public Header align(int alignment) {
		this.alignment = alignment;
		return this;
	}
	
	public Header align(int textAlignment, int controlAlignment, boolean grab) {
		this.textAlignment = textAlignment;
		this.alignment = controlAlignment;
		this.grab = grab;
		return this;
	}
	
	public Header grab() {
		grab = true;
		return this;
	}

	public Header readOnly() {
		readOnly = true;
		return this;
	}

}
