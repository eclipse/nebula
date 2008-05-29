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

package org.eclipse.nebula.widgets.ganttchart;

import org.eclipse.swt.graphics.Color;

public interface IFillBackgroundColors {
	
	/**
	 * The top background gradient color used for drawing the Saturday column.
	 * 
	 * @return Color
	 */
	public Color getSaturdayBackgroundColorTop();
	
	/**
	 * The bottom background gradient color used for drawing the Saturday column.
	 * 
	 * @return Color
	 */
	public Color getSaturdayBackgroundColorBottom();
	
	/**
	 * The top background gradient color used for drawing the Sunday column.
	 * 
	 * @return Color
	 */
	public Color getSundayBackgroundColorTop();

	/**
	 * The bottom background gradient color used for drawing the Sunday column.
	 * 
	 * @return Color
	 */
	public Color getSundayBackgroundColorBottom();
	
	/**
	 * The bottom background gradient color used for drawing the weekday column.
	 * 
	 * @return Color
	 */
	public Color getWeekdayBackgroundColorBottom();	

	/**
	 * The top background gradient color used for drawing the weekday column.
	 * 
	 * @return Color
	 */
	public Color getWeekdayBackgroundColorTop();


}
