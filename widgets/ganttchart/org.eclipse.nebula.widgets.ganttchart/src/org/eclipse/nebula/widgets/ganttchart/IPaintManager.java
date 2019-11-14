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

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

public interface IPaintManager {

    /**
     * Notifies a redraw is starting from scratch, so you can zero out variables etc
     */
    void redrawStarting();
    
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
	 * @param bounds full bounds of draw area
	 */
    void drawCheckpoint(GanttComposite ganttComposite, ISettings settings, IColorManager colorManager, GanttEvent ge, GC gc, boolean threeDee, int dayWidth, int x, int y, Rectangle bounds);
	
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
	 * @param bounds full bounds of draw area
	 */
	void drawEvent(GanttComposite ganttComposite, ISettings settings, IColorManager colorManager, GanttEvent ge, GC gc, boolean isSelected, boolean threeDee, int dayWidth, int x, int y, int eventWidth, Rectangle bounds);
	
	/**
	 * Draws the planned dates.
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
	 * @param bounds full bounds of draw area
	 */
	void drawPlannedDates(GanttComposite ganttComposite, ISettings settings, IColorManager colorManager, GanttEvent ge, GC gc, boolean threeDee, int x, int y, int eventWidth, Rectangle bounds);
	
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
	 * @param bounds full bounds of draw area
	 */
	void drawDaysOnChart(GanttComposite ganttComposite, ISettings settings, IColorManager colorManager, GanttEvent ge, GC gc, boolean threeDee, int x, int y, int eventWidth, int daysNumber, Rectangle bounds);
	
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
	 * @param bounds full bounds of draw area
	 */
	void drawEventString(GanttComposite ganttComposite, ISettings settings, IColorManager colorManager, GanttEvent ge, GC gc, String toDraw, boolean threeDee, int x, int y, int eventWidth, Rectangle bounds);
	
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
	 * @param bounds full bounds of draw area
	 */
	void drawScope(GanttComposite ganttComposite, ISettings settings, IColorManager colorManager, GanttEvent ge, GC gc, boolean threeDee, int dayWidth, int x, int y, int eventWidth, Rectangle bounds);

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
	 * @param bounds full bounds of draw area
	 */
	void drawImage(GanttComposite ganttComposite, ISettings settings, IColorManager colorManager, GanttEvent ge, GC gc, Image image, boolean threeDee, int dayWidth, int x, int y, Rectangle bounds);

	/**
	 * Draws the marker that shows what dates an event are locked down to
	 * 
	 * @param ganttComposite GanttComposite parent
	 * @param settings ISettings
	 * @param colorManager IColorManager
	 * @param ge GanttEvent
	 * @param gc GC
	 * @param threeDee Whether 3D events is on or off
	 * @param dayWidth Width of one day
	 * @param y y location
	 * @param xStart where to draw the being marker. Will be -1 if there is no marker to draw.
	 * @param xEnd where to draw the end marker. Will be -1 if there is no marker to draw.
	 * @param bounds
	 */
	void drawLockedDateRangeMarker(GanttComposite ganttComposite, ISettings settings, IColorManager colorManager, GanttEvent ge, GC gc, boolean threeDee, int dayWidth, int y, int xStart, int xEnd, Rectangle bounds);
	
	/**
	 * Draws an arrow head.
	 * 
	 * @param x X location
	 * @param y Y location 
	 * @param face What direction the arrows is in (one of SWT.LEFT, SWT.RIGHT, SWT.UP, SWT.DOWN)
	 * @param gc GC
	 */
	void drawArrowHead(int x, int y, int face, GC gc);		
}
