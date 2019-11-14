/*******************************************************************************
 * Copyright (c) 2013 Dirk Fauth and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Dirk Fauth <dirk.fauth@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.ganttchart.print;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.ganttchart.GanttChart;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Display;

/**
 * This class is used to print multiple GanttCharts at once. 
 * 
 * @see GanttChartPrinter
 */
public class CompoundGanttChartPrinter extends GanttChartPrinter {

	private String jobName;
	private final List<GanttChart> ganttCharts = new ArrayList<GanttChart>();
	
	/**
	 * Creates a new CompoundGanttChartPrinter.
	 * Will use the print job name of the first GanttChart in the list of
	 * GanttCharts to print.
	 */
	public CompoundGanttChartPrinter() {
		super(null);
	}
	
	/**
	 * Creates a new CompoundGanttChartPrinter that uses the given job name as printer job name.
	 * @param jobName The name that will be used for the print job.
	 */
	public CompoundGanttChartPrinter(String jobName) {
		super(null);
		this.jobName = jobName;
	}

	@Override
	public void print() {
		if (!this.ganttCharts.isEmpty()) {
			final Printer printer = setupPrinter(Display.getCurrent().getActiveShell());
			if (printer == null) {
				return;
			}
			
			String name = (this.jobName != null && this.jobName.length() > 0) ? 
					this.jobName : this.ganttCharts.get(0).getLanguageManger().getPrintJobText();
			Display.getDefault().asyncExec(new GanttChartPrintJob(
					printer, name, ganttCharts.toArray(new GanttChart[] {})));
		}
	}
	
	@Override
	protected Point getFullPageCount(Printer printer) {
		Point result = new Point(0, 0);
		
		for (GanttChart ganttChart : this.ganttCharts) {
			Image chartImage = ganttChart.getGanttComposite().getFullImage();
			Point imgPoint = PrintUtils.getPageCount(printer, chartImage);
			result.x += imgPoint.x;
			result.y += imgPoint.y;
			chartImage.dispose();
		}
		
		return result; 
	}
	
	/**
	 * Adds the given GanttChart to the list of GanttCharts that should be
	 * printed by this CompoundGanttChartPrinter.
	 * @param ganttChart The GanttChart to add to the charts to be printed.
	 */
	public void addGanttChart(GanttChart ganttChart) {
		this.ganttCharts.add(ganttChart);
	}
	
	/**
	 * Adds the given GanttChart at the specified index to the list of GanttCharts 
	 * that should be printed by this CompoundGanttChartPrinter.
	 * @param index The index at which the given GanttChart should be added.
	 * @param ganttChart The GanttChart to add to the charts to be printed.
	 */
	public void addGanttChart(int index, GanttChart ganttChart) {
		this.ganttCharts.add(index, ganttChart);
	}
	
	/**
	 * Removes the given GanttChart from the list of GanttCharts that should be
	 * printed by this CompoundGanttChartPrinter.
	 * @param ganttChart The GanttChart to remove from the charts to be printed.
	 */
	public void removeGanttChart(GanttChart ganttChart) {
		this.ganttCharts.remove(ganttChart);
	}
}
