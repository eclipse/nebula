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

public interface IColorManager {

	/**
	 * The color used for drawing the vertical "today" line that shows where the todays date is.
	 * 
	 * @return Color
	 */
	public Color getLineTodayColor();
	
	/**
	 * The color used for drawing lines.
	 * 
	 * @return Color
	 */
	public Color getLineColor();
	
	/**
	 * The color used for drawing the vertical line showing where the next week starts (or the previous week ends). 
	 * 
	 * @return Color
	 */
	public Color getLineWeekDividerColor();
	
	/**
	 * The color used for drawing text.
	 * 
	 * @return Color
	 */
	public Color getTextColor();
	
	/**
	 * The background color used for drawing the background of the days in the header.
	 *  
	 * @return Color
	 */
	public Color getDayBackgroundColor();
	
	/**
	 * The color used for drawing the Saturday letter.
	 * 
	 * @return Color
	 */
	public Color getSaturdayColor();
	
	/**
	 * The background color used for drawing the Staturday letter box.
	 * 
	 * @return Color
	 */
	public Color getSaturdayBackgroundColor();
	
	/**
	 * The color used for drawing the Sunday letter.
	 * 
	 * @return Color
	 */
	public Color getSundayColor();
	
	/**
	 * The background color used for drawing the Sunday letter box.
	 * 
	 * @return Color
	 */
	public Color getSundayBackgroundColor();
	
	/**
	 * The background color used for drawing a weekday.
	 * 
	 * @return Color
	 */
	public Color getWeekdayBackgroundColor();
	
	/**
	 * The background color used in the header where the full date is written.
	 * 
	 * @return Color
	 */
	public Color getTextHeaderBackgroundColor();
	
	/**
	 * The color of the percentage bar drawn inside an event.
	 * 
	 * @return Color
	 */
	public Color getPercentageBarColor();
	
	/**
	 * The color used for dependency lines and arrowheads.
	 * 
	 * @return Color
	 */
	public Color getArrowColor();
	
	/**
	 * The color used to draw the border around an event.
	 * 
	 * @return Color
	 */
	public Color getEventBorderColor();
	
	/**
	 * The top most drop-shadow color vertically.
	 * 
	 * @return Color
	 */
	public Color getFadeOffColor1();
	
	/**
	 * The middle most drop-shadow color vertically.
	 * 
	 * @return Color
	 */
	public Color getFadeOffColor2();
	
	/**
	 * The bottom most drop-shadow color vertically.
	 * 
	 * @return Color
	 */
	public Color getFadeOffColor3();
	
	/**
	 * The background color used to represent the current day.
	 * 
	 * @return Color
	 */
	public Color getTodayBackgroundColor();
	
	/**
	 * The color used to draw the revised start date of an event.
	 * 
	 * @return Color
	 */
	public Color getRevisedStartColor();
	
	/**
	 * The color used to draw the revised end date of an event.
	 * 
	 * @return Color
	 */
	public Color getRevisedEndColor();
	
	/**
	 * The background color used to draw the zoom box.
	 * 
	 * @return Color
	 */
	public Color getZoomBackgroundColor();
	
	/**
	 * The color used to draw the zoom level text.
	 * 
	 * @return Color
	 */
	public Color getZoomForegroundColor();
	
	/**
	 * The background color used in all tooltips.
	 * 
	 * @return Color
	 */
	public Color getTooltipBackgroundColor();
	
	/**
	 * The foreground color used in all tooltips.
	 * 
	 * @return Color
	 */
	public Color getTooltipForegroundColor();
	
	/**
	 * The faded foreground color used in all tooltips (for less important text).
	 * 
	 * @return Color
	 */
	public Color getTooltipForegroundColorFaded();
	
	/**
	 * The border color used for drawing scopes.
	 * 
	 * @return Color
	 */
	public Color getScopeBorderColor();
	
	/**
	 * One of the gradient colors for drawing scopes.
	 * 
	 * @return Color
	 */
	public Color getScopeGradientColorTop();
	
	/**
	 * One of the gradient colors for drawing scopes.
	 * 
	 * @return Color
	 */
	public Color getScopeGradientColorBottom();
	
	/**
	 * The color black. Used in few places.
	 * 
	 * @return Color
	 */
	public Color getBlack();
	
	/**
	 * The color white. Used in few places.
	 * 
	 * @return Color
	 */
	public Color getWhite();
	
}
