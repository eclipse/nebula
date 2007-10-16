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

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

public interface IPaintManager {

	/**
	 * Draws one checkpoint.
	 * 
	 * @param ganttComposite GanttComposite parent
	 * @param settings ISettings
	 * @param colorManager IColorManager
	 * @param ge GanttEvent 
	 * @param gc GC
	 * @param threeDee Whether 3D events is on or off
	 * @param dayWidth Width of one day
	 * @param x x location
	 * @param y y location
	 */
	public void drawCheckpoint(GanttComposite ganttComposite, ISettings settings, IColorManager colorManager, GanttEvent ge, GC gc, boolean threeDee, int dayWidth, int x, int y);
	
	/**
	 * Draws one normal event.
	 * 
	 * @param ganttComposite GanttComposite parent
	 * @param settings ISettings
	 * @param colorManager IColorManager
	 * @param ge GanttEvent
	 * @param gc GC
	 * @param isSelected Whether the event is selected or not 
	 * @param threeDee Whether 3D events is on or off
	 * @param dayWidth Width of one day
	 * @param x x location
	 * @param y y location
	 * @param eventWidth Width of event
	 */
	public void drawEvent(GanttComposite ganttComposite, ISettings settings, IColorManager colorManager, GanttEvent ge, GC gc, boolean isSelected, boolean threeDee, int dayWidth, int x, int y, int eventWidth);
	
	/**
	 * Draws the revised dates.
	 * 
	 * @param ganttComposite GanttComposite parent
	 * @param settings ISettings
	 * @param colorManager IColorManager
	 * @param ge GanttEvent
	 * @param gc GC
	 * @param threeDee Whether 3D events is on or off.
	 * @param x x location
	 * @param y y location 
	 * @param eventWidth Width of event
	 */
	public void drawRevisedDates(GanttComposite ganttComposite, ISettings settings, IColorManager colorManager, GanttEvent ge, GC gc, boolean threeDee, int x, int y, int eventWidth);
	
	/**
	 * Draws the little plaque showing how many number of days an event spans over.
	 * 
	 * @param ganttComposite GanttComposite parent
	 * @param settings ISettings
	 * @param colorManager IColorManager
	 * @param ge GanttEvent
	 * @param gc GC
	 * @param threeDee Whether 3D events is on or off
	 * @param x x location
	 * @param y y location 
	 * @param eventWidth Width of event
	 * @param daysNumber Number of days the event encompasses
	 */
	public void drawDaysOnChart(GanttComposite ganttComposite, ISettings settings, IColorManager colorManager, GanttEvent ge, GC gc, boolean threeDee, int x, int y, int eventWidth, int daysNumber);
	
	/**
	 * Draws a string shown next to an event.
	 * 
	 * @param ganttComposite GanttComposite parent
	 * @param settings ISettings
	 * @param colorManager IColorManager
	 * @param ge GanttEvent
	 * @param gc GC
	 * @param toDraw String to draw
	 * @param threeDee Whether 3D events is on or off
	 * @param x x location
	 * @param y y location
	 * @param eventWidth Width of event
	 */
	public void drawEventString(GanttComposite ganttComposite, ISettings settings, IColorManager colorManager, GanttEvent ge, GC gc, String toDraw, boolean threeDee, int x, int y, int eventWidth);
	
	/**
	 * Draws one scope.
	 * 
	 * @param ganttComposite GanttComposite parent
	 * @param settings ISettings
	 * @param colorManager IColorManager
	 * @param ge GanttEvent
	 * @param gc GC
	 * @param threeDee Whether 3D events is on or off
	 * @param dayWidth Width of one day
	 * @param x x location
	 * @param y y location 
	 * @param eventWidth Width of event
	 */
	public void drawScope(GanttComposite ganttComposite, ISettings settings, IColorManager colorManager, GanttEvent ge, GC gc, boolean threeDee, int dayWidth, int x, int y, int eventWidth);

	/**
	 * Draws one checkpoint.
	 * 
	 * @param ganttComposite GanttComposite parent
	 * @param settings ISettings
	 * @param colorManager IColorManager
	 * @param ge GanttEvent 
	 * @param gc GC
	 * @param image Image
	 * @param threeDee Whether 3D events is on or off
	 * @param dayWidth Width of one day
	 * @param x x location
	 * @param y y location
	 */
	public void drawImage(GanttComposite ganttComposite, ISettings settings, IColorManager colorManager, GanttEvent ge, GC gc, Image image, boolean threeDee, int dayWidth, int x, int y);
	
}
