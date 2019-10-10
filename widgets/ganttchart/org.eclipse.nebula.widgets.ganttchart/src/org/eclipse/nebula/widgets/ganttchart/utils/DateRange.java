/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    emil.crumhorn@gmail.com - initial API and implementation
 *******************************************************************************/ 

package org.eclipse.nebula.widgets.ganttchart.utils;

import java.util.Calendar;

/**
 * Represents one Date Range
 * 
 * @author Emil
 *
 */
public class DateRange {

	private Calendar _startDate;
	private Calendar _endDate;

	public Calendar getStartDate() {
		return _startDate;
	}

	public void setStartDate(Calendar _startDate) {
		this._startDate = _startDate;
	}

	public Calendar getEndDate() {
		return _endDate;
	}

	public void setEndDate(Calendar _endDate) {
		this._endDate = _endDate;
	}

	/**
	 * Creates a new DateRange between two dates
	 * 
	 * @param startDate Start date of range
	 * @param endDate End date of range
	 */
	public DateRange(Calendar startDate, Calendar endDate) {
		setStartDate(startDate);
		setEndDate(endDate);
	}

	/**
	 * Whether two {@link DateRange}s overlap in any way
	 * 
	 * @param other DateRange to check against
	 * @return True if they overlap
	 */
	public boolean Overlaps(DateRange other) {
		if (isWithinRange(other.getStartDate()) || isWithinRange(other.getEndDate())) {
			return true;
		}
		
		return false;
	}
		
	/**
	 * Whether a date falls between the date range
	 * 
	 * @param date Date to check
	 * @return true if date is between ranges start and end dates
	 */
	public boolean isWithinRange(Calendar date) {
		if (_startDate == null || date.after(_startDate) || date.equals(_startDate)) {
			if (_endDate == null || date.before(_endDate) || date.equals(_endDate)) {
				return true;
			}
		}
		return false;
	}
	
	public String toString() { 
		return "[DateRange: " + (_startDate == null ? null : _startDate.getTime()) + " - " + (_endDate == null ? null : _endDate.getTime()) + "]";
	}
}
