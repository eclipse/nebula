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

package org.eclipse.nebula.widgets.ganttchart;

import org.eclipse.swt.graphics.Color;

/**
 * Interface holding all color methods, such as line colors, background fills, etc.
 *  
 * @author Emil
 *
 */
public interface IColorManager extends IFillBackgroundColors {
	
	/**
	 * The color used for drawing lines.
	 * 
	 * @return Color
	 */
	Color getLineColor();
	
	/**
	 * The color used for drawing the vertical line showing where the next week starts (or the previous week ends). 
	 * 
	 * @return Color
	 */
	Color getWeekDividerLineColor();
	
	/**
	 * The color used for drawing text.
	 * 
	 * @return Color
	 */
	Color getTextColor();
	
	/**
	 * The foreground color of the letters of the week in the bottom header.
	 * 
	 * @return Color
	 */
	Color getWeekdayTextColor();
	
	/**
	 * The color used for drawing the Saturday letter.
	 * 
	 * @return Color
	 */
	Color getSaturdayTextColor();
		
	/**
	 * The color used for drawing the Sunday letter.
	 * 
	 * @return Color
	 */
	Color getSundayTextColor();
		
	
	/**
	 * The top gradient background color used in the header where the full date is written.
	 * 
	 * @return Color
	 */
	Color getTextHeaderBackgroundColorTop();
	
	/**
	 * The bottom gradient background color used in the header where the full date is written.
	 * 
	 * @return Color
	 */
	Color getTextHeaderBackgroundColorBottom();

	/**
	 * The top gradient background color used in the header where the days and time is written.
	 * 
	 * @return Color
	 */
	Color getTimeHeaderBackgroundColorTop();

	/**
	 * The bottom gradient background color used in the header where the days and time is written.
	 * 
	 * @return Color
	 */
	Color getTimeHeaderBackgroundColorBottom();
	
	 /**
     * The top gradient background color used in the header where phases are written.
     * 
     * @return Color
     */
    Color getPhaseHeaderBackgroundColorTop();

    /**
     * The bottom gradient background color used in the header where phases are written.
     * 
     * @return Color
     */
    Color getPhaseHeaderBackgroundColorBottom();
    
    
	/**
	 * The top gradient color of the percentage bar drawn inside an event.
	 * 
	 * @return Color
	 */
	Color getPercentageBarColorTop();

	/**
	 * The bottom gradient color of the percentage bar drawn inside an event.
	 * 
	 * @return Color
	 */
	Color getPercentageBarColorBottom();

	/**
	 * The top gradient color of the remainder percentage bar drawn inside an event. This only draws if drawFullPercentageBar() in settings returns true.
	 * 
	 * @return Color
	 */
	Color getPercentageBarRemainderColorTop();

	/**
	 * The bottom gradient color of the remainder percentage bar drawn inside an event. This only draws if drawFullPercentageBar() in settings returns true.
	 * 
	 * @return Color
	 */
	Color getPercentageBarRemainderColorBottom();

	/**
	 * The color used for dependency lines and arrowheads.
	 * 
	 * @return Color
	 */
	Color getArrowColor();

	/**
	 * The color used for reverse dependency lines and arrowheads when the connection type is set to MS PROJECT style. For any other line style it is ignored.
	 * 
	 * @return Color
	 */
	Color getReverseArrowColor();

	/**
	 * The color used to draw the border around an event.
	 * 
	 * @return Color
	 */
	Color getEventBorderColor();
	
	/**
	 * The top most drop-shadow color vertically.
	 * 
	 * @return Color
	 */
	Color getFadeOffColor1();
	
	/**
	 * The middle most drop-shadow color vertically.
	 * 
	 * @return Color
	 */
	Color getFadeOffColor2();
	
	/**
	 * The bottom most drop-shadow color vertically.
	 * 
	 * @return Color
	 */
	Color getFadeOffColor3();
	
	/**
	 * The top gradient background color used to represent the current day.
	 * 
	 * @return Color
	 */
	Color getTodayBackgroundColorTop();
	
	/**
	 * The bottom gradient background color used to represent the current day.
	 * 
	 * @return Color
	 */
	Color getTodayBackgroundColorBottom();
	
	/**
	 * The color used to draw the revised start date of an event.
	 * 
	 * @return Color
	 */
	Color getRevisedStartColor();
	
	/**
	 * The color used to draw the revised end date of an event.
	 * 
	 * @return Color
	 */
	Color getRevisedEndColor();

	/**
	 * The bottom gradient background color used to draw the zoom box.
	 * 
	 * @return Color
	 */
	Color getZoomBackgroundColorBottom();

	/**
	 * The top gradient background color used to draw the zoom box.
	 * 
	 * @return Color
	 */
	Color getZoomBackgroundColorTop();
	
	/**
	 * The color used to draw the zoom level box border.
	 * 
	 * @return Color
	 */
	Color getZoomBorderColor();
	
	/**
	 * The color used to draw the text in the zoom box.
	 * 
	 * @return Color
	 */
	Color getZoomTextColor();
	
	/**
	 * The background color used in all tooltips.
	 * 
	 * @return Color
	 */
	Color getTooltipBackgroundColor();
	
	/**
	 * The foreground color used in all tooltips.
	 * 
	 * @return Color
	 */
	Color getTooltipForegroundColor();
	
	/**
	 * The faded foreground color used in all tooltips (for less important text).
	 * 
	 * @return Color
	 */
	Color getTooltipForegroundColorFaded();
	
	/**
	 * The border color used for drawing scopes.
	 * 
	 * @return Color
	 */
	Color getScopeBorderColor();
	
	/**
	 * One of the gradient colors for drawing scopes.
	 * 
	 * @return Color
	 */
	Color getScopeGradientColorTop();
	
	/**
	 * One of the gradient colors for drawing scopes.
	 * 
	 * @return Color
	 */
	Color getScopeGradientColorBottom();
	
	/**
	 * The color black. Used in few places.
	 * 
	 * @return Color
	 */
	Color getBlack();
	
	/**
	 * The color white. Used in few places.
	 * 
	 * @return Color
	 */
	Color getWhite();
	
	/**
	 * The top horizontal lines are all the horizontal lines that span across the header from the left side to the right.
	 * 
	 * @return Color for top horizontal lines.
	 */
	Color getTopHorizontalLinesColor();
	
	/**
	 * The color for the dividing line between each section of the bottom header in the day view. 
	 * 
	 * @return Color
	 */
	Color getHourTimeDividerColor();

	/**
	 * The color for the dividing line between each section of the bottom header in the week view. 
	 * 
	 * @return Color
	 */
	Color getWeekTimeDividerColor();

	/**
	 * The color for the dividing line between each section of the bottom header in the month view. 
	 * 
	 * @return Color
	 */
	Color getMonthTimeDividerColor();

	/**
	 * The color for the dividing line between each section of the bottom header in the year view.
	 * 
	 * @return Color
	 */
	Color getYearTimeDividerColor();

	/**
	 * The color used for drawing the vertical "today" line that shows where the todays date is.
	 * 
	 * @return Color
	 */
	Color getTodayLineColor();
	
	/**
	 * The alpha value of the Today line. 
	 * 
	 * @return Alpha value between 0 and 255.
	 */
	int getTodayLineAlpha();
	
	/**
	 * The alpha value of the week divider line.
	 * 
	 * @return Alpha value between 0 and 255.
	 */
	int getWeekDividerAlpha();
	
	/**
	 * Alpha colors can slow down the drawing considerably, only enable on a fast system and you're 100% certain that systems other than yours (3rd party)
	 * will be able to handle it as well. If this setting is false (default is false), all getAlpha() methods will be ignored.
	 * 
	 * Alpha drawing is used for various lines that may overlay other sections as well as for drop shadows.
	 * 
	 * @return true if Alpha drawing should be on. Default is false.
	 */
	boolean useAlphaDrawing();
	
	/**
	 * Separate flag from useAlphaDrawing. If this is set to true drop shadows on 3D events will be drawn using alpha channels.
	 * 
	 * @return true if Alpha drawing should be on for drop shadows on 3D events. Default is true.
	 */
	boolean useAlphaDrawingOn3DEventDropShadows();
	
	/**
	 * The tick mark is the small line that separates dates in the top part of the header. 
	 *  
	 * @return Tick mark color
	 */
	Color getTickMarkColor();
	
	/**
	 * The divider color in the advanced tooltip dialog
	 * 
	 * @return Color
	 */
	Color getAdvancedTooltipDividerColor();
	
	/**
	 * The dropshadow color of the divider line in the advanced tooltip dialog.
	 * 
	 * @return Color
	 */
	Color getAdvancedTooltipDividerShadowColor();
	
	/**
	 * The top gradient fill color of the background of the advanced tooltip dialog.
	 * 
	 * @return Color
	 */
	Color getAdvancedTooltipInnerFillTopColor();

	/**
	 * The bottom gradient fill color of the background of the advanced tooltip dialog.
	 * 
	 * @return Color
	 */
	Color getAdvancedTooltipInnerFillBottomColor();
	
	/**
	 * The default foreground color used for displaying text in the advanced tooltip dialog.
	 * 
	 * @return Color
	 */
	Color getAdvancedTooltipTextColor();
	
	/**
	 * The border color of the advanced tooltip dialog.
	 * 
	 * @return Color
	 */
	Color getAdvancedTooltipBorderColor();
	
	/**
	 * The fadeoff pixels used to make corners more rounded in the advanced tooltip dialog.
	 * 
	 * @return Color
	 */
	Color getAdvancedTooltipShadowCornerInnerColor();

	/**
	 * The fadeoff pixels used to make corners more rounded in the advanced tooltip dialog.
	 * 
	 * @return Color
	 */
	Color getAdvancedTooltipShadowCornerOuterColor();

	/**
	 * The fadeoff shadow pixels used to make corners more rounded in the advanced tooltip dialog.
	 * 
	 * @return Color
	 */
	Color getAdvancedTooltipShadowInnerCornerColor();
	
	/**
	 * The left gradient color of a section bar.
	 * 
	 * @return Color
	 */
	Color getActiveSessionBarColorLeft();

	/**
	 * The right gradient color of a section bar.
	 * 
	 * @return Color
	 */
	Color getActiveSessionBarColorRight();
	
	/**
	 * The left gradient color of the section bar that is outside of any actual section.
	 * 
	 * @return Color
	 */
	Color getNonActiveSessionBarColorLeft();

	/**
	 * The right gradient color of the section bar that is outside of any actual section.
	 * 
	 * @return Color
	 */
	Color getNonActiveSessionBarColorRight();
	
	/**
	 * The left gradient color of the section divider bar that is drawn between sections.
	 * 
	 * @return Color
	 */
	Color getSessionBarDividerColorLeft();

	/**
	 * The right gradient color of the section divider bar that is drawn between sections.
	 * 
	 * @return Color
	 */
	Color getSessionBarDividerColorRight();

	/**
	 * For vertical drag/drops a box is drawn where the event was prior to the DND started to
	 * indicate what the original location of the event was (so the user can find their way back).
	 * This is the color used to draw the indication box.
	 * 
	 * @return Color
	 */
	Color getOriginalLocationColor();
	
	/**
	 * This is the color used to draw the vertical insert marker for vertical drag and drop. 
	 * This is the line drawn between events to show where the DND would take place if the currently DND event
	 * is dropped.
	 * 
	 * @return Color
	 */
	Color getVerticalInsertMarkerColor();
	
	/**
	 * The color used for drawing the vertical period start and end line that shows where the period
	 * start and end dates are.
	 * 
	 * @return Color
	 */
	Color getPeriodLineColor();

	/**
	 * The foreground color used to draw the gradient background of the section detail area
	 * of the given GanttSection. This way it is possible to implement highlighting in the
	 * detail area based on the set data value.
	 * @param section The GanttSection for which the foreground color is requested.
	 * @return Color
	 */
	Color getSectionDetailAreaForegroundColor(GanttSection section);

	/**
	 * The background color used to draw the gradient background of the section detail area
	 * of the given GanttSection. This way it is possible to implement highlighting in the
	 * detail area based on the set data value.
	 * @param section The GanttSection for which the background color is requested.
	 * @return Color
	 */
	Color getSectionDetailAreaBackgroundColor(GanttSection section);
	
	/**
	 * Specify the direction of the gradient background of the section detail area.
	 * 
	 * @return <code>true</code> if the gradient should sweep from top to bottom, 
	 * 			else sweeps from left to right
	 */
	boolean drawSectionDetailGradientTopDown();
}
