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

public class Body {

	static final int DAYS = 0;
	static final int MONTHS = 1;
	static final int YEARS = 2;
	static final int TIME = 3;
	
	
	public static Body Days() {
		return new Body(DAYS, Calendar.DATE);
	}
	
	public static Body Months() {
		return new Body(MONTHS, Calendar.MONTH);
	}
	
	public static Body Time() {
		return new Body(TIME, Calendar.HOUR, Calendar.HOUR_OF_DAY, Calendar.MINUTE);
	}
	
	public static Body Years() {
		return new Body(YEARS, Calendar.YEAR);
	}

	int type;
	int[] fields;
	int spacing;
	boolean newColumn = false;
	boolean compact = false;

	private Body(int type, int... fields) {
		this.type = type;
		this.fields = fields;
	}

	public Body compact() {
		this.compact = true;
		return this;
	}
	
	public Body newColumn() {
		this.newColumn = true;
		return this;
	}

	public Body spacedAt(int spacing) {
		this.spacing = spacing;
		return this;
	}

}
