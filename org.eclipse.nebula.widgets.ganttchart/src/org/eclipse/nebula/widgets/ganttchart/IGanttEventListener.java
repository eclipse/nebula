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

import java.util.Calendar;
import java.util.List;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;

public interface IGanttEventListener {

	/**
	 * Fires when one or more events were moved.
	 * 
	 * @param events List of modified events (post modification)
	 * @param me MouseEvent
	 */
	public void eventsMoved(List events, MouseEvent me);

	/**
	 * Fires when one or more events were resized.
	 * 
	 * @param events List of modified events (post modification)
	 * @param me MouseEvent
	 */
	public void eventsResized(List events, MouseEvent me);

	/**
	 * Fires when a move has finished (the mouse button is let go).
	 * 
	 * @param events List of modified events (post modification)
	 * @param me MouseEvent
	 */
	public void eventsMoveFinished(List events, MouseEvent me);

	/**
	 * Fires when a resize has finished (the mouse button is let go).
	 * 
	 * @param events List of modified events (post modification)
	 * @param me MouseEvent
	 */
	public void eventsResizeFinished(List events, MouseEvent me);

	/**
	 * Fires when an event is selected.
	 * 
	 * @param event Event that got selected.
	 * @param allSelectedEvents All currently selected events.
	 * @param me MouseEvent
	 */
	public void eventSelected(GanttEvent event, List allSelectedEvents, MouseEvent me);

	/**
	 * Fires when the built-in delete action is run on an event.
	 * 
	 * @param events Events requested to be deleted
	 * @param me MouseEvent
	 */
	public void eventsDeleteRequest(List events, MouseEvent me);

	/**
	 * Fires when an event is doubleclicked.
	 * 
	 * @param event Event double clicked.
	 * @param me MouseEvent
	 */
	public void eventDoubleClicked(GanttEvent event, MouseEvent me);

	/**
	 * Fires when user zoomed in.
	 * 
	 * @param newZoomLevel The new zoom level.
	 */
	public void zoomedIn(int newZoomLevel);

	/**
	 * Fires when user zoomed out.
	 * 
	 * @param newZoomLevel The new zoom level.
	 */
	public void zoomedOut(int newZoomLevel);

	/**
	 * Fires when the zoom level has been reset.
	 * 
	 */
	public void zoomReset();

	/**
	 * Fires when the "properties" menu item is selected on an event (assuming it's visible).
	 * 
	 * @param events Events to show properties on.
	 */
	public void eventPropertiesSelected(List events);

	/**
	 * Fires when a header section becomes selected.
	 * 
	 * @param newlySelectedDate The date that was just clicked
	 * @param allSelectedDates All dates that were selected previously including the currently added one
	 */
	public void eventHeaderSelected(Calendar newlySelectedDate, List allSelectedDates);

	/**
	 * This method will be called when the chart has finished drawing. It passes along the GC object for any custom drawing you may wish to do on top of the currently drawn chart.
	 * 
	 * @param gc GC graphics object
	 */
	public void lastDraw(GC gc);
}
