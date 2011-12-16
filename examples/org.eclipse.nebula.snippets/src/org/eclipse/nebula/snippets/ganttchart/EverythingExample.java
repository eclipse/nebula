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
import java.util.List;
import java.util.Locale;

import org.eclipse.nebula.widgets.ganttchart.AdvancedTooltip;
import org.eclipse.nebula.widgets.ganttchart.DateHelper;
import org.eclipse.nebula.widgets.ganttchart.GanttChart;
import org.eclipse.nebula.widgets.ganttchart.GanttEvent;
import org.eclipse.nebula.widgets.ganttchart.GanttGroup;
import org.eclipse.nebula.widgets.ganttchart.GanttSection;
import org.eclipse.nebula.widgets.ganttchart.themes.ColorThemeSilver;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This Snippet shows a bit of everything that the chart can do.
 *
 */
public class EverythingExample {

	public static void main(String args []) {
		Display display = new Display ();
		Shell shell = new Shell (display);
		shell.setText("Gantt Chart - Everything Example");
		shell.setSize(600, 500);		
		shell.setLayout(new FillLayout());
		
		// Create a chart
		final GanttChart ganttChart = new GanttChart(shell, SWT.NONE, null, new ColorThemeSilver(), null, null);
				
		// Create some sections
		GanttSection sectionOne = new GanttSection(ganttChart, "Section 1");
		GanttSection sectionTwo = new GanttSection(ganttChart, "Section 2");
		GanttSection sectionThree = new GanttSection(ganttChart, "Section 3");
		
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
		GanttEvent eventOne = new GanttEvent(ganttChart, "Scope Event 1", sdEventOne, edEventOne, 35);
		eventOne.setFixedRowHeight(100);
		eventOne.setVerticalEventAlignment(SWT.CENTER);
		// lock this event down to a date span with both start and end dates
		Calendar sdEventOneLock = Calendar.getInstance();
		Calendar edEventOneLock = Calendar.getInstance();
		sdEventOneLock.add(Calendar.DATE, -5);
		edEventOneLock.add(Calendar.DATE, 15);
		eventOne.setNoMoveBeforeDate(sdEventOneLock);
		eventOne.setNoMoveAfterDate(edEventOneLock);		

		// Create an advanced custom tooltip
		StringBuffer buf = new StringBuffer();
		buf.append("\\ceRevised: #rs# - #re# (#reviseddays# days)\n");
		buf.append("\\c100100100Planned: #sd# - #ed# (#days# days)\n");
		buf.append("\\c100000000Locked start: ");
		buf.append(DateHelper.getDate(sdEventOneLock, ganttChart.getSettings().getDateFormat()));
		buf.append("\n");
		buf.append("\\c100000000Locked end: ");
		buf.append(DateHelper.getDate(edEventOneLock, ganttChart.getSettings().getDateFormat()));
		buf.append("\n");
		buf.append("\\x#pc#% complete");

		AdvancedTooltip at = new AdvancedTooltip(eventOne.getName(), buf.toString());
		eventOne.setAdvancedTooltip(at);
				
		GanttEvent eventTwo = new GanttEvent(ganttChart, "Scope Event 2", sdEventTwo, edEventTwo, 10);
		
		// lock only the end date
		Calendar edEventTwoLock = Calendar.getInstance();
		edEventTwoLock.add(Calendar.DATE, 30);
		eventTwo.setNoMoveAfterDate(edEventTwoLock);
		
		GanttEvent eventThree = new GanttEvent(ganttChart, "Checkpoint", cpDate, cpDate, 75);
		eventThree.setCheckpoint(true);
				
		// opacities and layers
		eventOne.setLayer(1);
		eventTwo.setLayer(2);
		eventThree.setLayer(3);
		ganttChart.getGanttComposite().setLayerOpacity(1, 50);
		ganttChart.getGanttComposite().setLayerOpacity(2, 150);
		ganttChart.getGanttComposite().setLayerOpacity(3, 220);
		
		// Put the events in their respective sections
		sectionOne.addGanttEvent(eventOne);
		sectionOne.addGanttEvent(eventTwo);
		sectionOne.addGanttEvent(eventThree);
		
		// Create some groups
		GanttGroup groupOne = new GanttGroup(ganttChart);
		GanttGroup groupTwo = new GanttGroup(ganttChart);				
		
		// Create 5 events for each group
		for (int x = 1; x <= 2; x++) {
			int start = 1;
			for (int i = 1; i < 6; i++) {
				Calendar tempStart = Calendar.getInstance();
				Calendar tempEnd = Calendar.getInstance();
				tempStart.add(Calendar.DATE, start);
				tempEnd.add(Calendar.DATE, start+1);
				GanttEvent temp = new GanttEvent(ganttChart, x+":"+i, tempStart, tempEnd, 50);

				if (x == 1)
					groupOne.addEvent(temp);
				else
					groupTwo.addEvent(temp);
				
				start += 9;
			}
		}
		
		sectionTwo.addGanttEvent(groupOne);

		// now let's squeeze a single event in between two groups
		Calendar sdEventFour = Calendar.getInstance();
		Calendar edEventFour = Calendar.getInstance();
		sdEventFour.add(Calendar.DATE, 4);
		edEventFour.add(Calendar.DATE, 13);
		GanttEvent eventFour = new GanttEvent(ganttChart, "Scope Event 4", sdEventFour, edEventFour, 40);
		eventFour.setFixedRowHeight(50);
		eventFour.setVerticalEventAlignment(SWT.CENTER);
		sectionTwo.addGanttEvent(eventFour);
		// Create connections
		ganttChart.addConnection(eventOne, eventTwo);
		ganttChart.addConnection(eventTwo, eventThree);
		// reverse one connection back up (not logical, but doable)
		ganttChart.addConnection(eventThree, eventOne);
		
		// and another group
		sectionTwo.addGanttEvent(groupTwo);
	
				
		// Let's connect all events in one group in one direction, and the other in reverse
		// Note: It's not suggested to have connections between same-group events.
		List groupOneEvents = groupOne.getEventMembers(); 
		for (int i = 0; i < groupOneEvents.size(); i++) {
			if (i >= 1) {
				GanttEvent ge1 = (GanttEvent) groupOneEvents.get(i-1);
				GanttEvent ge2 = (GanttEvent) groupOneEvents.get(i);
				ganttChart.addConnection(ge1, ge2);
			}
		}

		// Now reverse.. do note that this makes really no sense for a lot of reasons, but it's possible to do regardless
		List groupTwoEvents = groupTwo.getEventMembers(); 
		for (int i = groupTwoEvents.size()-1; i >= 0; i--) {
			if (i > 0) {
				GanttEvent ge1 = (GanttEvent) groupTwoEvents.get(i);
				GanttEvent ge2 = (GanttEvent) groupTwoEvents.get(i-1);
				ganttChart.addConnection(ge1, ge2);
			}
		}

		// move chart start date to the earliest event
		ganttChart.getGanttComposite().jumpToEarliestEvent();
		
		// Show chart
		shell.open();	
		
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
		
	}
}

