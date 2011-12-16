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
 * This snippet shows how to create layers, how to change the opacities for those layers, and also how to do showing/hiding of layers.
 * 
 */
public class LayersExample {
	
	private static int currentLayer = 1;
	private static int minLayer = 1;
	private static int maxLayer = 3;
	
	private static Runnable runnable;
	
	public static void main(String [] args) {
		Display display = new Display ();
		Shell shell = new Shell (display);
		shell.setText("Gantt Chart - Layers Example");
		shell.setSize(600, 500);		
		shell.setLayout(new FillLayout());
		
		// Create a chart
		final GanttChart ganttChart = new GanttChart(shell, SWT.NONE);
				
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
		GanttEvent eventOne = new GanttEvent(ganttChart, "Event 1", sdEventOne, edEventOne, 35);
		GanttEvent eventTwo = new GanttEvent(ganttChart, "Event 2", sdEventTwo, edEventTwo, 10);		
		GanttEvent eventThree = new GanttEvent(ganttChart, "Checkpoint", cpDate, cpDate, 75);
		eventThree.setCheckpoint(true);
		
		// Put each event on their own layer
		eventOne.setLayer(1);
		eventTwo.setLayer(2);
		eventThree.setLayer(3);

		// Set alpha opacities on each layer
		ganttChart.getGanttComposite().setLayerOpacity(1, 200);
		ganttChart.getGanttComposite().setLayerOpacity(2, 100);
		ganttChart.getGanttComposite().setLayerOpacity(3, 25);
		
		// Create connections
		ganttChart.addConnection(eventOne, eventTwo);
		ganttChart.addConnection(eventTwo, eventThree);
				
		// Show chart
		shell.open();
		
		// cycle through all layers every 1 second
		runnable = new Runnable() {

			public void run() {
				ganttChart.getGanttComposite().showLayer(currentLayer);
				currentLayer++;
				if (currentLayer > maxLayer)
					currentLayer = minLayer;
				
				ganttChart.getGanttComposite().hideLayer(currentLayer);
				
				Display.getDefault().timerExec(1000, runnable);
			}
			
		};
		
		Display.getDefault().timerExec(1000, runnable);
	
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
		
	}
}
