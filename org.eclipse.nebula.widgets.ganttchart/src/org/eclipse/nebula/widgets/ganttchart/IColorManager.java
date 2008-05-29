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

public interface IColorManager extends IFillBackgroundColors {
	
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
	public Color getWeekDividerLineColor();
	
	/**
	 * The color used for drawing text.
	 * 
	 * @return Color
	 */
	public Color getTextColor();
	
	/**
	 * The foreground color of the letters of the week in the bottom header.
	 * 
	 * @return Color
	 */
	public Color getWeekdayTextColor();
	
	/**
	 * The color used for drawing the Saturday letter.
	 * 
	 * @return Color
	 */
	public Color getSaturdayTextColor();
		
	/**
	 * The color used for drawing the Sunday letter.
	 * 
	 * @return Color
	 */
	public Color getSundayTextColor();
		
	
	/**
	 * The top gradient background color used in the header where the full date is written.
	 * 
	 * @return Color
	 */
	public Color getTextHeaderBackgroundColorTop();
	
	/**
	 * The bottom gradient background color used in the header where the full date is written.
	 * 
	 * @return Color
	 */
	public Color getTextHeaderBackgroundColorBottom();

	/**
	 * The top gradient background color used in the header where the days and time is written.
	 * 
	 * @return Color
	 */
	public Color getTimeHeaderBackgroundColorTop();

	/**
	 * The bottom gradient background color used in the header where the days and time is written.
	 * 
	 * @return Color
	 */
	public Color getTimeHeaderBackgroundColorBottom();
	
	
	/**
	 * The top gradient color of the percentage bar drawn inside an event.
	 * 
	 * @return Color
	 */
	public Color getPercentageBarColorTop();

	/**
	 * The bottom gradient color of the percentage bar drawn inside an event.
	 * 
	 * @return Color
	 */
	public Color getPercentageBarColorBottom();

	/**
	 * The top gradient color of the remainder percentage bar drawn inside an event. This only draws if drawFullPercentageBar() in settings returns true.
	 * 
	 * @return Color
	 */
	public Color getPercentageBarRemainderColorTop();

	/**
	 * The bottom gradient color of the remainder percentage bar drawn inside an event. This only draws if drawFullPercentageBar() in settings returns true.
	 * 
	 * @return Color
	 */
	public Color getPercentageBarRemainderColorBottom();

	/**
	 * The color used for dependency lines and arrowheads.
	 * 
	 * @return Color
	 */
	public Color getArrowColor();

	/**
	 * The color used for reverse dependency lines and arrowheads when the connection type is set to MS PROJECT style. For any other line style it is ignored.
	 * 
	 * @return Color
	 */
	public Color getReverseArrowColor();

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
	 * The top gradient background color used to represent the current day.
	 * 
	 * @return Color
	 */
	public Color getTodayBackgroundColorTop();
	
	/**
	 * The bottom gradient background color used to represent the current day.
	 * 
	 * @return Color
	 */
	public Color getTodayBackgroundColorBottom();
	
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
	 * The bottom gradient background color used to draw the zoom box.
	 * 
	 * @return Color
	 */
	public Color getZoomBackgroundColorBottom();

	/**
	 * The top gradient background color used to draw the zoom box.
	 * 
	 * @return Color
	 */
	public Color getZoomBackgroundColorTop();
	
	/**
	 * The color used to draw the zoom level box border.
	 * 
	 * @return Color
	 */
	public Color getZoomBorderColor();
	
	/**
	 * The color used to draw the text in the zoom box.
	 * 
	 * @return Color
	 */
	public Color getZoomTextColor();
	
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
	
	/**
	 * The top horizontal lines are all the horizontal lines that span across the header from the left side to the right.
	 * 
	 * @return Color for top horizontal lines.
	 */
	public Color getTopHorizontalLinesColor();
	
	/**
	 * The color for the dividing line between each section of the bottom header in the day view. 
	 * 
	 * @return Color
	 */
	public Color getHourTimeDividerColor();

	/**
	 * The color for the dividing line between each section of the bottom header in the week view. 
	 * 
	 * @return Color
	 */
	public Color getWeekTimeDividerColor();

	/**
	 * The color for the dividing line between each section of the bottom header in the month view. 
	 * 
	 * @return Color
	 */
	public Color getMonthTimeDividerColor();

	/**
	 * The color for the dividing line between each section of the bottom header in the year view.
	 * 
	 * @return Color
	 */
	public Color getYearTimeDividerColor();

	/**
	 * The color used for drawing the vertical "today" line that shows where the todays date is.
	 * 
	 * @return Color
	 */
	public Color getTodayLineColor();
	
	/**
	 * The alpha value of the Today line. 
	 * 
	 * @return Alpha value between 0 and 255.
	 */
	public int getTodayLineAlpha();
	
	/**
	 * The alpha value of the week divider line.
	 * 
	 * @return Alpha value between 0 and 255.
	 */
	public int getWeekDividerAlpha();
	
	/**
	 * Alpha colors can slow down the drawing considerably, only enable on a fast system and you're 100% certain that systems other than yours (3rd party)
	 * will be able to handle it as well. If this setting is false (default is false), all getAlpha() methods will be ignored.
	 * 
	 * Alpha drawing is used for various lines that may overlay other sections as well as for drop shadows.
	 * 
	 * @return true if Alpha drawing should be on. Default is false.
	 */
	public boolean useAlphaDrawing();
	
	/**
	 * Separate flag from useAlphaDrawing. If this is set to true drop shadows on 3D events will be drawn using alpha channels.
	 * 
	 * @return true if Alpha drawing should be on for drop shadows on 3D events. Default is true.
	 */
	public boolean useAlphaDrawingOn3DEventDropShadows();
	
	/**
	 * The tick mark is the small line that separates dates in the top part of the header. 
	 *  
	 * @return Tick mark color
	 */
	public Color getTickMarkColor();
	
	/**
	 * The divider color in the advanced tooltip dialog
	 * 
	 * @return Color
	 */
	public Color getAdvancedTooltipDividerColor();
	
	/**
	 * The dropshadow color of the divider line in the advanced tooltip dialog.
	 * 
	 * @return Color
	 */
	public Color getAdvancedTooltipDividerShadowColor();
	
	/**
	 * The top gradient fill color of the background of the advanced tooltip dialog.
	 * 
	 * @return Color
	 */
	public Color getAdvancedTooltipInnerFillTopColor();

	/**
	 * The bottom gradient fill color of the background of the advanced tooltip dialog.
	 * 
	 * @return Color
	 */
	public Color getAdvancedTooltipInnerFillBottomColor();
	
	/**
	 * The default foreground color used for displaying text in the advanced tooltip dialog.
	 * 
	 * @return Color
	 */
	public Color getAdvancedTooltipTextColor();
	
	/**
	 * The border color of the advanced tooltip dialog.
	 * 
	 * @return Color
	 */
	public Color getAdvancedTooltipBorderColor();
	
	/**
	 * The fadeoff pixels used to make corners more rounded in the advanced tooltip dialog.
	 * 
	 * @return Color
	 */
	public Color getAdvancedTooltipShadowCornerInnerColor();

	/**
	 * The fadeoff pixels used to make corners more rounded in the advanced tooltip dialog.
	 * 
	 * @return Color
	 */
	public Color getAdvancedTooltipShadowCornerOuterColor();

	/**
	 * The fadeoff shadow pixels used to make corners more rounded in the advanced tooltip dialog.
	 * 
	 * @return Color
	 */
	public Color getAdvancedTooltipShadowInnerCornerColor();
	
	/**
	 * The left gradient color of a section bar.
	 * 
	 * @return Color
	 */
	public Color getActiveSessionBarColorLeft();

	/**
	 * The right gradient color of a section bar.
	 * 
	 * @return Color
	 */
	public Color getActiveSessionBarColorRight();
	
	/**
	 * The left gradient color of the section bar that is outside of any actual section.
	 * 
	 * @return Color
	 */
	public Color getNonActiveSessionBarColorLeft();

	/**
	 * The right gradient color of the section bar that is outside of any actual section.
	 * 
	 * @return Color
	 */
	public Color getNonActiveSessionBarColorRight();
	
	/**
	 * The left gradient color of the section divider bar that is drawn between sections.
	 * 
	 * @return Color
	 */
	public Color getSessionBarDividerColorLeft();

	/**
	 * The right gradient color of the section divider bar that is drawn between sections.
	 * 
	 * @return Color
	 */
	public Color getSessionBarDividerColorRight();

}
