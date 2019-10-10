/*******************************************************************************
 * Copyright (c) 2006 The Pampered Chef and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     The Pampered Chef - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.compositetable.day;

import java.util.Date;

import org.eclipse.nebula.widgets.compositetable.timeeditor.CalendarableItem;

/**
 * Class DayEditorSelection.  Represents the current selection in a DayEditor
 * control.
 * 
 * @since 3.3
 */
public class DayEditorSelection {

	/**
	 * Represents the selected CalendarableItem or null if none is selected.
	 */
	public CalendarableItem selectedCalendarable = null;
	
	/**
	 * Sets the selectedCalendarable.
	 * 
	 * @param selectedCalendarable the CalendarableItem to select
	 */
	public void setSelectedCalendarable(CalendarableItem selectedCalendarable) {
		this.selectedCalendarable = selectedCalendarable;
	}

	/**
	 * Indicates if the current selection is in an all-day event row.
	 */
	public boolean allDay = false;
	
	/**
	 * Sets the allDay flag.
	 * 
	 * @param allDay true if the selection is in an all-day event row or if
	 * the selectedCalenderable represents an all-day event; false otherwise.
	 */
	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}

	/**
	 * The date/time of the currently selected cell or null if selectedCalenderable != null
	 */
	public Date dateTime = null;
	
	/**
	 * Sets the dateTime.
	 * 
	 * @param date the Date to set.
	 */
	public void setDateTime(Date date) {
		this.dateTime = date;
	}

}
