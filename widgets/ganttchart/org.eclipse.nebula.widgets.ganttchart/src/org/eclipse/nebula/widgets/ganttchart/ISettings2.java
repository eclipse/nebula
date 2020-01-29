/*******************************************************************************
 * Copyright (c) 2015 Giovanni Cimmino and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    ziogiannigmail.com - Bug 464509 - Minute View Implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.ganttchart;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;

/**
 * This interface is an extension of ISettings which lets you define various settings for the GanttChart. 
 * Its initial implementation has been defined because of MinuteView, in order to both preserve the binary code compatibility and to do not amend the pre-existent ISettings interface.
 * In turn, the abstract class {@link AbstractSettings} is now implementing this interface.
 * 
 * You can change some settings by creating your own class and overriding your desired methods, instructions as follows:
 * <pre>
 * public class MySettings extends AbstractSettings {
 * 	// override your methods here
 * }
 * </pre>
 * <p />
 * Once you've overridden the settings you wish to change, simply pass an instance of your implementation class to the constructor of GanttChart: {@link GanttChart#GanttChart(org.eclipse.swt.widgets.Composite, int, ISettings)}
 *  
 * @author Giovanni Cimmino
 *
 */

public interface ISettings2 extends ISettings {
    
    /**
     * The date format to use when displaying dates in string format in the minutes view.
     * 
     * @return Date format. Default is month/day/year/ hh:mm:ss.
     * @see DateFormat
     * @see DateFormatSymbols
     */
    public String getMinuteDateFormat();
    
    /**
     * The SimpleDateFormat of the text shown in the top header for the minute view.
     * 
     * @return {@link SimpleDateFormat} string. May not be null.
     */
    public String getMinuteHeaderTextDisplayFormatTop();
    
    /**
     * The SimpleDateFormat of the text shown in the bottom header for the minute view.
     * 
     * @return SimpleDateFormat string. May not be null.
     */
    public String getMinuteHeaderTextDisplayFormatBottom();
    
    /**
     * Start the updating tread for TodayLine (red line marking current time) 
     * 
     * @return <code>true</code> Start Thread
     * 			<code>false</code> Skip Thread
     */
    public boolean enableTodayLineUpdater();

}
