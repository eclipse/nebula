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

public interface ILanguageManager {

	/**
	 * The text drawn for the zoom level box.
	 * 
	 * @return Text
	 */
	public String getZoomLevelText();
	
	/**
	 * The text drawn instead of a number when the zoom level reaches its minimum.
	 * 
	 * @return Text
	 */
	public String getZoomMinText();

	/**
	 * The text drawn instead of a number when the zoom level reaches its maximum.
	 * 
	 * @return Text
	 */
	public String getZoomMaxText();
	
	/**
	 * The menu item text for the "Zoom in" menu item
	 * 
	 * @return Text
	 */
	public String getZoomInMenuText();
	
	/**
	 * The menu item text for the "Zoom out" menu item
	 * 
	 * @return Text
	 */
	public String getZoomOutMenuText();
	
	/**
	 * The menu item text for the "Reset Zoom Level" menu item
	 * 
	 * @return Text
	 */
	public String getZoomResetMenuText();
	
	/**
	 * The menu item text for the "Show number of days on events" menu item 
	 * 
	 * @return Text
	 */
	public String getShowNumberOfDaysOnEventsMenuText();
	
	/**
	 * The menu item text for the the "Show planned dates" menu item 
	 * 
	 * @return Text
	 */
	public String getShowPlannedDatesMenuText();
	
	/**
	 * The menu item text for the "3D events" menu item
	 * 
	 * @return Text
	 */
	public String get3DMenuText();
	
	/**
	 * The menu item text for the "Delete" menu item
	 * 
	 * @return Text
	 */
	public String getDeleteMenuText();
	
	/**
	 * The menu item text for the "Properties" menu item
	 * 
	 * @return Text
	 */
	public String getPropertiesMenuText();
	
	/**
	 * The text drawn for the word "Planned", used in tooltips
	 * 
	 * @return Text
	 */
	public String getPlannedText();
	
	/**
	 * The text drawn for the word "Revised", used in tooltips 
	 * 
	 * @return Text
	 */
	public String getRevisedText();

	/**
	 * The text drawn for the word "day", used in tooltips
	 * 
	 * @return Text
	 */
	public String getDaysText();
	
	/**
	 * The text drawn for the word "days", used in tooltips
	 * 
	 * @return Text
	 */
	public String getDaysPluralText();
	
	/**
	 * The text drawn for the word "% complete", used in tooltips
	 * 
	 * @return Text
	 */
	public String getPercentCompleteText();
	
	/**
	 * The text drawn for the word "n/a", used in tooltips
	 * 
	 * @return Text
	 */
	public String getNotAvailableText();
}
