/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    emil.crumhorn@gmail.com - initial API and implementation
 *******************************************************************************/ 

package org.eclipse.nebula.widgets.calendarcombo;

import java.util.Calendar;

/**
 * This interface is the recipient of events that happen on the CalendarCombo, mainly date selection events.
 * 
 */
public interface ICalendarListener
{
	/**
	 * When the user selects a date in the combo. Note: If the combo is a date range selection combo, the event
	 * fired for a user selection will be the dateRangeChanged event even if the start and end date are the same. 
	 * 
	 * @param date Selected date, or null if the "none" button is clicked.
	 */
    public void dateChanged(Calendar date);
    
    /**
     * When the user selects a date range in the combo (if the combo date range feature is enabled). This event
     * fires even if the start and end date selected are the same. 
     * 
     * @param start Selected start date
     * @param end Selected end date
     */
    public void dateRangeChanged(Calendar start, Calendar end);

    /**
     * When the popup is closed regardless if there was a selection or not.
     */
    public void popupClosed();

}
