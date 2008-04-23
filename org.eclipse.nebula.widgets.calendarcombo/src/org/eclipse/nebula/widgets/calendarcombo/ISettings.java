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


public interface ISettings {
	
	/**
	 * Returns the width of the calendar.
	 * 
	 * @return Calendar width
	 */
	public int getCalendarWidth();
	
	/**
	 * Returns the height of the calendar.
	 * 
	 * @return Calendar height
	 */
	public int getCalendarHeight();
	
	/**
	 * CALLED ON MACINTOSH ONLY!
	 * 
	 * Returns the width of the calendar.
	 * 
	 * @return Calendar width
	 */
	public int getCalendarWidthMacintosh();
	
	/**
	 * CALLED ON MACINTOSH ONLY!
	 * 
	 * Returns the height of the calendar.
	 * 
	 * @return Calendar height
	 */
	public int getCalendarHeightMacintosh();
	
	/**
	 * Returns the left margin size of the header.
	 *  
	 * @return Left margin width
	 */
	public int getHeaderLeftMargin();

	/**
	 * Returns the right margin size of the header.
	 *  
	 * @return Right margin width
	 */
	public int getHeaderRightMargin();
	
	/**
	 * Returns the top margin size of the header.
	 *  
	 * @return Top margin width
	 */
	public int getHeaderTopMargin();
	
	/**
	 * Returns the height of the header.
	 *  
	 * @return Header height
	 */
	public int getHeaderHeight();
	
	/**
	 * Returns the left spacing before the arrow.
	 *  
	 * @return Left arrow spacing
	 */
	public int getArrowLeftSpacing();

	/**
	 * Returns the top margin size of the arrows.
	 *  
	 * @return Top arrow spacing
	 */
	public int getArrowTopSpacing();

	/**
	 * Returns the left margin size where the dates are shown.
	 *  
	 * @return Dates left margin width
	 */
	public int getDatesLeftMargin();

	/**
	 * Returns the right margin size where the dates are shown.
	 *  
	 * @return Dates right margin width
	 */
	public int getDatesRightMargin();
	
	/**
	 * Returns the size (width) of the box that one date is contained in.
	 * 
	 * @return Calendar day box size
	 */
	public int getOneDateBoxSize();
	
	/**
	 * Returns the width between each calendar date.
	 * 
	 * @return Calendar date spacer width
	 */
	public int getBoxSpacer();
	
	/**
	 * True if the calendar should be drawn with the right hand side of the calendar aligned with the drop down button. False if it should be aligned in the opposite way.
	 * 
	 * @return True to align in right corner
	 */
	public boolean showCalendarInRightCorner();

	/**
	 * Returns the date format to be used in the text display area.
	 * 
	 * @return Date format string
	 * @see java.text.DateFormat
	 */
	public String getDateFormat();
	
	/**
	 * Returns the height of the "Today" and "None" buttons.
	 * 
	 * @return Height
	 */
	public int getButtonHeight();
	
	/**
	 * Returns the width of the "Today" and "None" buttons.
	 * 
	 * @return Width
	 */
	public int getButtonWidth();
	
	/**
	 * Returns the spacing between the bottom of the calendar dates and where the "Today" and "None" buttons are.
	 * 
	 * @return Spacer size
	 */
	public int getButtonVerticalSpace();
	
	/**
	 * Returns the horizontal spacing between the "Today" and the "None" button.
	 * 
	 * @return Space size
	 */
	public int getButtonsHorizontalSpace();
	
	/**
	 * Returns the text written on the "Today" button
	 * 
	 * @return String
	 */
	public String getTodayText();
	
	/**
	 * Returns the text written on the "None" button
	 * 
	 * @return String
	 */
	public String getNoneText();
	
	/**
	 * TODO: Returns the text shown in the text area of the combo when there is no date selected.
	 * <b>Not currently used</b>
	 * 
	 * @return String (never null!)
	 */
	public String getNoDateSetText();
	
	/**
	 * Whether to show the month selector pop-up when the mouse is pressed and held on the month name in the calendar window.
	 * The month popup is a small borderless window that allows the user to quickly select a month in the future or past by 
	 * holding down the mouse button and moving the pointer either up (to scroll into the past) or down (to scroll into the future).
	 * 
	 * Please refer to the Microsoft Outlook calendar for a visual reference.  
	 * 
	 * @return true if to show the dialog. Default is true.
	 */
	public boolean showMonthPickerOnMonthNameMousePress();
	
	/**
	 * Returns the arrow width of the combo button itself for Windows.
	 * <br><br>
	 * IMPORTANT:<br>
	 * This does not change the combo button size, it is used to determine when the user clicks the arrow button on the combo
	 * and not the text field. If you notice that there is some area of the button that is either too large or small when clicked
	 * so that the popup does not show, you will probably need to adjust this.
	 * 
	 * @return size that the current OS. Defaults are: OSX: 22, Windows: 19, GTK: 25. 
	 */
	public int getWindowsButtonWidth();

	/**
	 * Returns the arrow width of the combo button itself for Carbon.
	 * <br><br>
	 * IMPORTANT:<br>
	 * This does not change the combo button size, it is used to determine when the user clicks the arrow button on the combo
	 * and not the text field. If you notice that there is some area of the button that is either too large or small when clicked
	 * so that the popup does not show, you will probably need to adjust this.
	 * 
	 * @return size that the current OS. Defaults are: OSX: 22, Windows: 19, GTK: 25. 
	 */
	public int getCarbonButtonWidth();
	
	/**
	 * Returns the arrow width of the combo button itself for GTK.
	 * <br><br>
	 * IMPORTANT:<br>
	 * This does not change the combo button size, it is used to determine when the user clicks the arrow button on the combo
	 * and not the text field. If you notice that there is some area of the button that is either too large or small when clicked
	 * so that the popup does not show, you will probably need to adjust this.
	 * 
	 * @return size that the current OS. Defaults are: OSX: 22, Windows: 19, GTK: 25. 
	 */
	public int getGTKButtonWidth();
}
