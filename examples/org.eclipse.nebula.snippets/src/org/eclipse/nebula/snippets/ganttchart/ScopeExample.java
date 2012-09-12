package org.eclipse.nebula.snippets.ganttchart;

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

import java.util.Calendar;

import org.eclipse.nebula.widgets.ganttchart.GanttChart;
import org.eclipse.nebula.widgets.ganttchart.GanttEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This Snippet shows how to create a Scope that changes size depending on the start and end date of the events that are "children" of that scope. 
 *
 */
public class ScopeExample {

	public static void main(String [] args) {
		Display display = new Display ();
		Shell shell = new Shell (display);
		shell.setText("Gantt Chart - Scopes Example");
		shell.setSize(600, 500);		
		shell.setLayout(new FillLayout());
		
		// Create a chart
		GanttChart ganttChart = new GanttChart(shell, SWT.NONE);
		
		// Create a scope
		GanttEvent scopeEvent = new GanttEvent(ganttChart, "Scope of 3 events");		
		
		// Create some calendars
		Calendar sdEventOne = Calendar.getInstance();
		Calendar edEventOne = Calendar.getInstance();
		edEventOne.add(Calendar.DATE, 10); 

		Calendar sdEventTwo = Calendar.getInstance();
		Calendar edEventTwo = Calendar.getInstance();
		sdEventTwo.add(Calendar.DATE, 11);
		edEventTwo.add(Calendar.DATE, 15);

		Calendar cpDate = Calendar.getInstance();
		cpDate.add(Calendar.DATE, 16);

		// Create events
		GanttEvent eventOne = new GanttEvent(ganttChart, "Scope Event 1");		
		GanttEvent eventTwo = new GanttEvent(ganttChart, "Scope Event 2", sdEventTwo, edEventTwo, 10);		
		GanttEvent eventThree = new GanttEvent(ganttChart, "Checkpoint", cpDate, cpDate, 75);
		eventThree.setCheckpoint(true);

		// Add events to scope
		scopeEvent.addScopeEvent(eventOne);
		scopeEvent.addScopeEvent(eventTwo);
		scopeEvent.addScopeEvent(eventThree);
		
		eventOne.addScopeEvent(eventTwo);
		eventOne.addScopeEvent(eventThree);
				
		// Show chart
		shell.open();
	
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
		
	}
}
