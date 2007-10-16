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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class GanttTester {

	public static void main(String args []) {
		Display display = new Display ();
		Shell shell = new Shell (display);
		shell.setText("Gantt Chart Tester");
		shell.setSize(600, 500);
		
		shell.setLayout(new FillLayout());
		Composite inner = new Composite(shell, SWT.None);
		inner.setLayout(new FillLayout()); 
		
		GanttChart ganttChart = new GanttChart(inner, SWT.NONE);
		
		GanttEvent scopeEvent = new GanttEvent(ganttChart, null, "Scope");
		
		Calendar revisedStart = Calendar.getInstance();
		Calendar revisedEnd = Calendar.getInstance();
		revisedStart.add(Calendar.DATE, -5); // started before start date
		revisedEnd.add(Calendar.DATE, 5); // ended before end date
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		end.add(Calendar.DATE, 10);
		GanttEvent ge = new GanttEvent(ganttChart, null, "Sub Project X", start, end, revisedStart, revisedEnd, 25);
		ge.setStatusColor(ColorCache.getColor(246, 159, 82));
		ge.setGradientStatusColor(ColorCache.getColor(252, 225, 201));

		Calendar start2 = Calendar.getInstance();
		Calendar end2 = Calendar.getInstance();
		start2.add(Calendar.DATE, 1);
		end2.add(Calendar.DATE, 10);
		GanttEvent ge2 = new GanttEvent(ganttChart, null, "Sub Project Y", start2, end2,  50);
		ge2.setStatusColor(ColorCache.getColor(255, 204, 0));
		ge2.setGradientStatusColor(ColorCache.getColor(255, 244, 202));
		
		Calendar startAndEnd = Calendar.getInstance();
		GanttEvent ge3 = new GanttEvent(ganttChart, null, "Parent Project XY", startAndEnd, startAndEnd, 70);		
		ge3.setCheckpoint(true);
		
		MenuItem testItem = new MenuItem(ge3.getMenu(), SWT.PUSH);
		testItem.setText("I am a custom event menu item!");
		testItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				System.out.println("You clicked the custom event!");
			}			
		});
				
		ganttChart.addConnection(ge, ge2);
		ganttChart.addConnection(ge2, ge3);
		
		ganttChart.addConnection(scopeEvent, ge);
		ganttChart.addConnection(scopeEvent, ge2);
		ganttChart.addConnection(scopeEvent, ge3);
		
		scopeEvent.addScopeEvent(ge);
		scopeEvent.addScopeEvent(ge2);
		scopeEvent.addScopeEvent(ge3);
		
		shell.open();

		ganttChart.addGanttEventListener(new IGanttEventListener() {

			public void eventDoubleClicked(GanttEvent event, MouseEvent me) {
				System.out.println("Event double clicked " + event);
			}

			public void eventPropertiesSelected(GanttEvent event) {
				System.out.println("Event properties selected " + event);
			}

			public void eventsDeleteRequest(List<GanttEvent> events, MouseEvent me) {
				System.out.println("Events delete request");
			}

			public void eventSelected(GanttEvent event, List<GanttEvent> allSelectedEvents, MouseEvent me) {
				System.out.println("Events selected");
			}

			public void eventsMoved(List<GanttEvent> events, MouseEvent me) {
				System.out.println("Events moved");
			}

			public void eventsResized(List<GanttEvent> events, MouseEvent me) {
				System.out.println("Events resized");
			}

			public void zoomedIn(int newZoomLevel) {
				System.out.println("Zoomed in to " + newZoomLevel);
			}

			public void zoomedOut(int newZoomLevel) {
				System.out.println("Zoomed out to " + newZoomLevel);
			}

			public void zoomReset() {
				System.out.println("Zoom level reset");
			}
			
		});
	
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
}
