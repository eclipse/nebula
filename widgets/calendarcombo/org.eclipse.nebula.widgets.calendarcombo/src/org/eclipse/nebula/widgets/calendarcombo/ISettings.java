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

import java.util.List;
import java.util.Locale;

import org.eclipse.swt.graphics.Font;


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
	 * Please note that this does not support just any format. You need to stick to Month Day and Year related formats and no others. 
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
	 * Returns the width of the "Today" and "None" buttons for Carbon.
	 * 
	 * @return Width
	 */
	public int getButtonWidthCarbon();
	
	/**
	 * Returns the height of the "Today" and "None" buttons for Carbon.
	 * 
	 * @return Height
	 */
	public int getCarbonButtonHeight();

	
	/**
	 * Returns the spacing between the bottom of the calendar dates and where the "Today" and "None" buttons are.
	 * 
	 * @return Spacer size
	 */
	public int getButtonVerticalSpace();
	
	/**
	 * Returns the spacing between the bottom of the calendar dates and where the "Today" and "None" buttons are. Used on Carbon only.
	 * 
	 * @return Spacer size
	 */
	public int getCarbonButtonVerticalSpace();
	
	/**
	 * Returns the horizontal spacing between the "Today" and the "None" button.
	 * 
	 * @return Space size
	 */
	public int getButtonsHorizontalSpace();
	
	/**
	 * Returns the horizontal spacing between the "Today" and the "None" button. Used on Carbon only.
	 * 
	 * @return Space size
	 */
	public int getCarbonButtonsHorizontalSpace();
	
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
	 * Returns the text shown in the text area of the combo when there is no date selected. It's suggested you return a blank string "" (which is the default).
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
	 * @return size that the current OS. Defaults are: OSX: 25, Windows: 19, GTK: 25. 
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
	 * @return size that the current OS. Defaults are: OSX: 25, Windows: 19, GTK: 25. 
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
	 * @return size that the current OS. Defaults are: OSX: 25, Windows: 19, GTK: 25. 
	 */
	public int getGTKButtonWidth();
	
	/**
	 * Returns the Locale that the calendar should use when calculating dates etc.
	 * 
	 * @return Locale to use. Default is Locale.getDefault(). Return value may not be null.
	 */
	public Locale getLocale();
	
	/**
	 * Returns the font to draw on Carbon. Do note that the drawing does not take font size into account, so change at your own risk.
	 * 
	 * @return Font to be used on Carbon.
	 */
	public Font getCarbonDrawFont();
	
	/**
	 * Returns the font used to draw the month selector text on Windows. It is not advised to change this setting.
	 * 
	 * @return Font to be used in the month popup on Windows.
	 */
	public Font getWindowsMonthPopupDrawFont();
	
	/**
	 * Returns a list of date formats that can be parsed into a Date. For example, if this list contains "mmddYY" and the user
	 * types in the date 030508 the date will be parsed into the date and displayed and formatted according to the getDateFormat() value automatically. 
	 * The order of this list is relevant as the first matched date that "works" will be used as the date. This list is only used as a last resort if all
	 * other date parses failed.
	 * 
	 * @return List of dateformats. Default is an empty list.
	 * @see java.text.DateFormat
	 * @see ISettings#getDateFormat()
	 */
	public List getAdditionalDateFormats();
	
	/*
	 * Accelerator combination for the Today button.
	 * 
	 * @return Accelerator, default is CTRL + 't'
	 */
	//public int getTodayAccelerator();
	
	/*
	 * Accelerator combination for the None button.
	 * 
	 * @return Accelerator, default is CTRL + 'n'
	 */
	//public int getNoneAccelerator();

	/**
	 * If it returns true, keyboard down key etc will open the calendar and navigate the calendar through the keyboard.
	 * If it returns false, keyboard will instead navigate the various text parts of the calendar, so if the date in the box is 12/13/2008 and the cursor is on the
	 * "13" and arrow up/down are pressed, that number will increase. Default is true.
	 */
	public boolean keyboardNavigatesCalendar();
		

	/**
	 * Returns a list of chars that can be used to separate dates. If your users enter characters other than / - . you will want to override this and add whatever character is missing.
	 * These are used when parsing the date in the box when the user hits the arrow up/down buttons or + - on mac. Do note that if the default date format returned by the settings has no
	 * separator characters in them, this method is not called as it would not make sense. 
	 * 
	 * @return char array, default is '/', '.', '-'
	 */
	public char [] getAcceptedDateSeparatorChars();
	
	/**
	 * On OSX/Carbon the arrow up/down don't work due to an eclipse bug so they need alternative keys. You may re-define those keys here if you wish. By default Carbon uses + - instead
	 * of the arrows. Do note that this method only matters if {@link #keyboardNavigatesCalendar()} returns true.
	 * 
	 * @return Arrow up replacement for carbon. Default is '-'.
	 */
	public char getCarbonArrowUpChar();

	/**
	 * On OSX/Carbon the arrow up/down don't work due to an eclipse bug so they need alternative keys. You may re-define those keys here if you wish. By default Carbon uses + - instead
	 * of the arrows. Do note that this method only matters if {@link #keyboardNavigatesCalendar()} returns true.
	 * 
	 * @return Arrow down replacement for carbon. Default is '+'.
	 */
	public char getCarbonArrowDownChar();
}
