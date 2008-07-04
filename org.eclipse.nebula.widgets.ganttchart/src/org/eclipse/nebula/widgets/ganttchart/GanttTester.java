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
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class GanttTester {

	public static void main(String args[]) {
		try {
			Display display = new Display();
			Shell shell = new Shell(display);
			shell.setText("Gantt Chart Tester");
			shell.setSize(600, 500);

			shell.setLayout(new FillLayout());
			Composite inner = new Composite(shell, SWT.None);
			inner.setLayout(new FillLayout());

			final GanttChart ganttChart = new GanttChart(inner, SWT.NONE);

			// GanttGroup gg = new GanttGroup(ganttChart);

			/*
			 * Calendar start = Calendar.getInstance(); Calendar end = Calendar.getInstance(); end.add(Calendar.DATE, 10);
			 */

			/*
			 * GanttEvent ge = new GanttEvent(ganttChart, "A", start, end, 35); //ge.setLocked(true); ge.setStatusColor(ColorCache.getColor(70, 132, 96));
			 * ge.setGradientStatusColor(ColorCache.getColor(160, 210, 181)); //ge.setFixedRowHeight(50); //ge.setVerticalEventAlignment(SWT.CENTER);
			 * 
			 * Calendar start2 = Calendar.getInstance(); Calendar end2 = Calendar.getInstance(); start2.add(Calendar.DATE, 11); end2.add(Calendar.DATE, 15); GanttEvent ge2 = new
			 * GanttEvent(ganttChart, "B", start2, end2, 10); ge2.setStatusColor(ColorCache.getColor(81, 104, 145)); ge2.setGradientStatusColor(ColorCache.getColor(168, 185, 216));
			 * 
			 * gg.addEvent(ge); gg.setFixedRowHeight(100); gg.setVerticalEventAlignment(SWT.BOTTOM); gg.addEvent(ge2);
			 * 
			 * Calendar start3 = Calendar.getInstance(); Calendar end3 = Calendar.getInstance(); start3.add(Calendar.DATE, 1); end3.add(Calendar.DATE, 5); GanttEvent ge3 = new
			 * GanttEvent(ganttChart, "C", start3, end3, 10); ge3.setStatusColor(ColorCache.getColor(81, 104, 145)); ge3.setGradientStatusColor(ColorCache.getColor(168, 185, 216));
			 * 
			 * Calendar start4 = Calendar.getInstance(); Calendar end4 = Calendar.getInstance(); start4.add(Calendar.DATE, 1); end4.add(Calendar.DATE, 5); GanttEvent ge4 = new
			 * GanttEvent(ganttChart, "D", start4, end4, 10); ge4.setStatusColor(ColorCache.getColor(81, 104, 145)); ge4.setGradientStatusColor(ColorCache.getColor(168, 185, 216));
			 */

			// ge2.setFixedRowHeight(150);
			// ge2.setVerticalEventAlignment(SWT.CENTER);

			GanttEvent scopeEvent = new GanttEvent(ganttChart, "scope");

			Calendar start = Calendar.getInstance();
			Calendar end = Calendar.getInstance();
			end.add(Calendar.DATE, 10);

			Calendar revisedStart = Calendar.getInstance();
			Calendar revisedEnd = Calendar.getInstance();
			revisedStart.add(Calendar.DATE, -5);
			revisedEnd.add(Calendar.DATE, 5);

			GanttEvent ge = new GanttEvent(ganttChart, "a", start, end, revisedStart, revisedEnd, 35);
			// ge.setLocked(true);
			ge.setStatusColor(ColorCache.getColor(70, 132, 96));
			ge.setGradientStatusColor(ColorCache.getColor(160, 210, 181));
			ge.setFixedRowHeight(150);
			ge.setVerticalEventAlignment(SWT.CENTER);

			Calendar start2 = Calendar.getInstance();
			Calendar end2 = Calendar.getInstance();
			start2.add(Calendar.DATE, 11);
			end2.add(Calendar.DATE, 15);
			GanttEvent ge2 = new GanttEvent(ganttChart, "b", start2, end2, 10);
			ge2.setStatusColor(ColorCache.getColor(81, 104, 145));
			ge2.setGradientStatusColor(ColorCache.getColor(168, 185, 216));
			// ge2.setFixedRowHeight(150);
			// ge2.setVerticalEventAlignment(SWT.CENTER);

			Calendar startAndEnd = Calendar.getInstance();
			startAndEnd.add(Calendar.DATE, 16);
			GanttEvent ge3 = new GanttEvent(ganttChart, "chk", startAndEnd, startAndEnd, 75);
			ge3.setCheckpoint(true);

			MenuItem testItem = new MenuItem(ge3.getMenu(), SWT.PUSH);
			testItem.setText("I am a custom event menu item!");
			testItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					System.out.println("You clicked the custom event!");
				}
			});

			GanttSection gs = new GanttSection(ganttChart, "Section One");
			gs.addGanttEvent(scopeEvent);
			gs.addGanttEvent(ge);
			gs.addGanttEvent(ge2);

			GanttSection gs2 = new GanttSection(ganttChart, "Section Two");
			gs2.setWeekdayBackgroundColorTop(ColorCache.getColor(242, 231, 215));
			gs2.addGanttEvent(ge3);

			GanttSection gs3 = new GanttSection(ganttChart, "Section Three");
			gs3.setWeekdayBackgroundColorTop(ColorCache.getColor(242, 215, 235));

			GanttSection gs4 = new GanttSection(ganttChart, "Section Four");
			gs4.setWeekdayBackgroundColorTop(ColorCache.getColor(215, 242, 222));

			ganttChart.addConnection(ge, ge2);
			ganttChart.addConnection(ge2, ge);
			ganttChart.addConnection(ge2, ge3);

			ganttChart.addConnection(scopeEvent, ge);
			ganttChart.addConnection(scopeEvent, ge2);
			ganttChart.addConnection(scopeEvent, ge3);

			ganttChart.addConnection(ge3, scopeEvent);
			ganttChart.addConnection(ge3, ge2);
			ganttChart.addConnection(ge3, ge);

			scopeEvent.addScopeEvent(ge);
			scopeEvent.addScopeEvent(ge2);
			scopeEvent.addScopeEvent(ge3);

			ganttChart.getGanttComposite().jumpToEarliestEvent();

			shell.open();

			ganttChart.addGanttEventListener(new IGanttEventListener() {

				public void eventDoubleClicked(GanttEvent event, MouseEvent me) {
					System.out.println("Event double clicked " + event);
				}

				public void eventPropertiesSelected(List events) {
					System.out.println("Event properties selected " + events);
				}

				public void eventsDeleteRequest(List events, MouseEvent me) {
					System.out.println("Events delete request");
				}

				public void eventSelected(GanttEvent event, List allSelectedEvents, MouseEvent me) {
					System.out.println("Events selected");
				}

				public void eventsMoved(List events, MouseEvent me) {
					// System.out.println("Events moved");
				}

				public void eventsResized(List events, MouseEvent me) {
					System.out.println("Events resized");
				}
				

				public void eventsMoveFinished(List events, MouseEvent me) {
				}

				public void eventsResizeFinished(List events, MouseEvent me) {
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

				public void lastDraw(GC gc) {
				}

				public void eventHeaderSelected(Calendar newlySelectedDate, List allSelectedDates) {
					System.out.println(newlySelectedDate.getTime() + " " + allSelectedDates);
				}

			});

			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			display.dispose();
		} catch (Exception err) {
			err.printStackTrace();
		}
	}
}
