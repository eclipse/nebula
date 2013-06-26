/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    emil.crumhorn@gmail.com - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.ganttchart;

import org.eclipse.swt.graphics.Color;

/**
 * Interface representing all methods that use colored fills.
 *  
 * @author Emil
 *
 */
public interface IFillBackgroundColors {
	
	/**
	 * The top background gradient color used for drawing the Saturday column.
	 * 
	 * @return Color
	 */
	Color getSaturdayBackgroundColorTop();
	
	/**
	 * The bottom background gradient color used for drawing the Saturday column.
	 * 
	 * @return Color
	 */
	Color getSaturdayBackgroundColorBottom();
	
	/**
	 * The top background gradient color used for drawing the Sunday column.
	 * 
	 * @return Color
	 */
	Color getSundayBackgroundColorTop();

	/**
	 * The bottom background gradient color used for drawing the Sunday column.
	 * 
	 * @return Color
	 */
	Color getSundayBackgroundColorBottom();
	
	/**
	 * The top background gradient color used for drawing the holiday column.
	 * 
	 * @return Color
	 */
	Color getHolidayBackgroundColorTop();

	/**
	 * The bottom background gradient color used for drawing the holiday column.
	 * 
	 * @return Color
	 */
	Color getHolidayBackgroundColorBottom();
	
	/**
	 * The bottom background gradient color used for drawing the weekday column.
	 * 
	 * @return Color
	 */
	Color getWeekdayBackgroundColorBottom();	

	/**
	 * The top background gradient color used for drawing the weekday column.
	 * 
	 * @return Color
	 */
	Color getWeekdayBackgroundColorTop();

	/**
	 * The top background gradient color used for drawing selected columns.
	 * 
	 * @return
	 */
	Color getSelectedDayColorTop();
	
	/**
	 * The bottom background gradient color used for drawing selected columns.
	 *  
	 * @return Color
	 */
	Color getSelectedDayColorBottom();

	/**
	 * The top background gradient color used for drawing selected columns in the header section.
	 * 
	 * @return Color
	 */
	Color getSelectedDayHeaderColorTop();

	/**
	 * The bottom background gradient color used for drawing selected columns in the header section.
	 * 
	 * @return Color
	 */
	Color getSelectedDayHeaderColorBottom();
}
