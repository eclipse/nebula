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

import java.util.Calendar;
import java.util.List;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;

public class GanttEventListenerAdapter implements IGanttEventListener {

	public void eventDoubleClicked(GanttEvent event, MouseEvent me) {
	}

	public void eventPropertiesSelected(List events) {
	}

	public void eventsDeleteRequest(List events, MouseEvent me) {
	}

	public void eventSelected(GanttEvent event, List allSelectedEvents, MouseEvent me) {
	}

	public void eventsMoved(List events, MouseEvent me) {
	}

	public void eventsResized(List events, MouseEvent me) {
	}
	
	public void eventsMoveFinished(List events, MouseEvent me) {
	}

	public void eventsResizeFinished(List events, MouseEvent me) {
	}

	public void zoomedIn(int newZoomLevel) {
	}

	public void zoomedOut(int newZoomLevel) {
	}

	public void zoomReset() {
	}
	
	public void eventHeaderSelected(Calendar newlySelectedDate, List allSelectedDates) {
	}

	public void lastDraw(GC gc) {
	}

    public void phaseMoved(GanttPhase phase, MouseEvent me) {
    }

    public void phaseMoveFinished(GanttPhase phase, MouseEvent me) {
    }

    public void phaseResized(GanttPhase phase, MouseEvent me) {
    }

    public void phaseResizeFinished(GanttPhase phase, MouseEvent me) {
    }

    public void eventMovedToNewSection(GanttEvent ge, GanttSection oldSection, GanttSection newSection) {
    }

    public void eventReordered(GanttEvent ge) {
    }

	public void eventsDroppedOrResizedOntoUnallowedDateRange(List events, GanttSpecialDateRange range) {		
	}

}
