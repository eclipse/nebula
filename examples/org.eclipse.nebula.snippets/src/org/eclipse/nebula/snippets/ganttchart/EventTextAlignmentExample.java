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
import org.eclipse.nebula.widgets.ganttchart.GanttSection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This Snippet shows how to align text in different areas around an event.
 * 
 */
public class EventTextAlignmentExample {

	public static void main(String [] args) {
		Display display = new Display ();
		Shell shell = new Shell (display);
		shell.setText("Gantt Chart Sample");
		shell.setSize(600, 500);		
		shell.setLayout(new FillLayout());
		
		// Create a chart
		GanttChart ganttChart = new GanttChart(shell, SWT.NONE);
				
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
		
		GanttSection gs = new GanttSection(ganttChart, "One Big Text");
		gs.setTextOrientation(SWT.HORIZONTAL);

		// Create events
		GanttEvent eventOne = new GanttEvent(ganttChart, "Event 1", sdEventOne, edEventOne, 35);		
		eventOne.setHorizontalTextLocation(SWT.LEFT);
		
		GanttEvent eventTwo = new GanttEvent(ganttChart, "Event 2", sdEventTwo, edEventTwo, 10);
		eventTwo.setHorizontalTextLocation(SWT.CENTER);
		
		GanttEvent eventThree= new GanttEvent(ganttChart, "Event 3", sdEventTwo, edEventTwo, 40);
		eventThree.setHorizontalTextLocation(SWT.RIGHT);

		GanttEvent eventFour = new GanttEvent(ganttChart, "Event 1", sdEventOne, edEventOne, 35);		
		eventFour.setHorizontalTextLocation(SWT.LEFT);
		eventFour.setVerticalTextLocation(SWT.TOP);
		
		GanttEvent eventFive = new GanttEvent(ganttChart, "Event 2", sdEventTwo, edEventTwo, 10);
		eventFive.setHorizontalTextLocation(SWT.CENTER);
		eventFive.setVerticalTextLocation(SWT.BOTTOM);
		
		GanttEvent eventSix = new GanttEvent(ganttChart, "Event 3", sdEventTwo, edEventTwo, 40);
		eventSix.setHorizontalTextLocation(SWT.RIGHT);
		eventSix.setVerticalTextLocation(SWT.BOTTOM);
		
		gs.addGanttEvent(eventOne);
		gs.addGanttEvent(eventTwo);
		gs.addGanttEvent(eventThree);
		gs.addGanttEvent(eventFour);
		gs.addGanttEvent(eventFive);
		gs.addGanttEvent(eventSix);
				
		// Show chart
		shell.open();
	
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
}
