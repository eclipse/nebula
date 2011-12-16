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

import org.eclipse.nebula.widgets.ganttchart.GanttChart;
import org.eclipse.nebula.widgets.ganttchart.GanttEvent;
import org.eclipse.nebula.widgets.ganttchart.GanttGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This Snippet shows how to make GanttGroups that are a bunch of events on the same horizontal "line".
 *
 */
public class GanttGroupExample {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Gantt Chart - Group Example");
		shell.setSize(600, 500);
		shell.setLayout(new FillLayout());

		// Create a chart
		final GanttChart ganttChart = new GanttChart(shell, SWT.NONE);
	
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
				tempEnd.add(Calendar.DATE, start + 1);
				GanttEvent temp = new GanttEvent(ganttChart, x + ":" + i, tempStart, tempEnd, 50);

				if (x == 1)
					groupOne.addEvent(temp);
				else
					groupTwo.addEvent(temp);

				start += 9;
			}
		}

		// Let's connect all events in one group in one direction, and the other in reverse
		// Note: It's not suggested to have connections between same-group events.
		List groupOneEvents = groupOne.getEventMembers();
		for (int i = 0; i < groupOneEvents.size(); i++) {
			if (i >= 1) {
				GanttEvent ge1 = (GanttEvent) groupOneEvents.get(i - 1);
				GanttEvent ge2 = (GanttEvent) groupOneEvents.get(i);
				ganttChart.addConnection(ge1, ge2);
			}
		}

		// Now reverse.. do note that this makes really no sense for a lot of reasons, but it's possible to do regardless
		List groupTwoEvents = groupTwo.getEventMembers();
		for (int i = groupTwoEvents.size() - 1; i >= 0; i--) {
			if (i > 0) {
				GanttEvent ge1 = (GanttEvent) groupTwoEvents.get(i);
				GanttEvent ge2 = (GanttEvent) groupTwoEvents.get(i - 1);
				ganttChart.addConnection(ge1, ge2);
			}
		}

		// move chart start date to the earliest event
		ganttChart.getGanttComposite().jumpToEarliestEvent();

		// Show chart
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();

	}
}
