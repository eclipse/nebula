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

import org.eclipse.nebula.widgets.ganttchart.GanttChart;
import org.eclipse.nebula.widgets.ganttchart.ILanguageManager;
import org.eclipse.nebula.widgets.ganttchart.ISettings;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This class is used to print a GanttChart. For this it will use the already existing
 * functionality of rendering the GanttChart to an image.
 * <p>
 * There are some configuration parameters that have impact on:
 * <ul>
 * <li>The horizontal range to print - If everything should be printed, by default the range
 * 		from the earliest event start date to the latest event end date (plus possible text)
 * 		is used. But configuring a period in the ISettings will also take these values into
 * 		account for calculating the horizontal range.</li>
 * <li>The vertical range to print - If only the selected part should be printed, by default
 * 		selection means the visible part of the chart horizontally and vertically. This behavior
 * 		can be modified so that horizontally only the visible part of the chart will be printed
 * 		but vertically the whole chart gets printed.</li>
 * <li>The name of the print job</li>
 * <li>Whether a footer should be printed or not</li>
 * <li>The name of the page (can be used for localization)</li>
 * <li>The format of the date in the footer</li>
 * </ul>
 * 
 * @see ISettings#getPeriodStart()
 * @see ISettings#getPeriodEnd()
 * @see ISettings#printSelectedVerticallyComplete()
 * @see ISettings#printFooter()
 * @see ISettings#getDateFormat()
 * @see ILanguageManager#getPrintJobText()
 * @see ILanguageManager#getPrintPageText()
 */
public class GanttChartPrinter {

	private final GanttChart ganttChart;
	
	/**
	 * Creates a new GanttChartPrinter for the given GanttChart.
	 * @param ganttChart The GanttChart that should be printed by this GanttChartPrinter.
	 */
	public GanttChartPrinter(GanttChart ganttChart) {
		this.ganttChart = ganttChart;
	}

	/**
	 * First opens the PrintDialog so a user can adjust his print settings and will
	 * then print the chart based on the settings made by the user. 
	 */
	public void print() {
		final Printer printer = setupPrinter(this.ganttChart.getShell());
		if (printer == null) {
			return;
		}

		Display.getDefault().asyncExec(new GanttChartPrintJob(
				printer, ganttChart.getLanguageManger().getPrintJobText(), ganttChart));
	}

	/**
	 * Opens the PrintDialog to let the user specify the printer and print configurations to use.
	 * @param shell The Shell which should be the parent for the PrintDialog
	 * @return The selected printer with the print configuration made by the user.
	 */
	protected Printer setupPrinter(final Shell shell) {
		//Calculate the number of pages by using the full image
		//This is because on setup we want to show how many pages the full print would be
		Printer defaultPrinter = new Printer();
		Point pageCount = getFullPageCount(defaultPrinter);
		defaultPrinter.dispose();

		final PrintDialog printDialog = new PrintDialog(shell);
		
		PrinterData data = new PrinterData();
		data.orientation = PrinterData.LANDSCAPE;
		data.startPage = 1;
		data.endPage = pageCount.x * pageCount.y;
		data.scope = PrinterData.ALL_PAGES;
		printDialog.setPrinterData(data);

		PrinterData printerData = printDialog.open();
		if (printerData == null){
			return null;
		}
		return new Printer(printerData);
	}
	
	/**
	 * Calculates the number of horizontal and vertical pages needed to print the entire chart.
	 * @param printer The printer that is used to determine the page count of a full print.
	 * @return The number of horizontal and vertical pages that will be printed.
	 */
	protected Point getFullPageCount(Printer printer) {
		Image chartImage = this.ganttChart.getGanttComposite().getFullImage();
		Point result = PrintUtils.getPageCount(printer, chartImage);
		chartImage.dispose();
		return result;
	}
}
