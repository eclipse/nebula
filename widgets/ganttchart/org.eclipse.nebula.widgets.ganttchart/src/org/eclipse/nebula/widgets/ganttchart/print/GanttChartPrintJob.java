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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.nebula.widgets.ganttchart.GanttChart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Display;

/**
 * Runnable to print one ore more GanttCharts.
 */
public class GanttChartPrintJob implements Runnable {

	private final Printer printer;
	
	private final String jobName;
	
	private final GanttChart[] ganttCharts;
	
	private boolean disposePrinter;
	
	/**
	 * Creates a new GanttChartPrintJob that uses the given printer and print job name
	 * to print the specified GanttChart(s). If not changed afterwards, running this job
	 * will dispose the given printer when it is done.
	 * @param printer The printer to use.
	 * @param jobName The job name to use for the print job.
	 * @param charts The GanttCharts that should be printed.
	 */
	public GanttChartPrintJob(Printer printer, String jobName, GanttChart... charts) {
		this(printer, jobName, true, charts);
	}
	
	/**
	 * Creates a new GanttChartPrintJob that uses the given printer and print job name
	 * to print the specified GanttChart(s).
	 * @param printer The printer to use.
	 * @param jobName The job name to use for the print job.
	 * @param charts The GanttCharts that should be printed.
	 * @param disposePrinter Flag to configure whether the given printer should be disposed
	 * 			after the print job is done. Default is <code>true</code>. Only set this
	 * 			parameter to <code>false</code> if the printer should be reused for additional
	 * 			print jobs. You need to ensure that the printer will get disposed yourself
	 * 			in that case!
	 */
	public GanttChartPrintJob(Printer printer, String jobName, boolean disposePrinter, GanttChart... charts) {
		this.printer = printer;
		this.jobName = jobName;
		this.ganttCharts = charts;
		this.disposePrinter = disposePrinter;
	}
	
	public void run() {
		if (printer.startJob(jobName)) {
			GC gc = new GC(printer);

			int currentPage = 1;
			for (GanttChart ganttChart : this.ganttCharts) {
				
				Image printerImage = null;
				if (printer.getPrinterData().scope == PrinterData.SELECTION) {
					//the user selected to only print the selected area
					//as this is quite difficult in GanttChart, we specify that
					//this means to print the visible area
					//but it is possible to configure via settings what "visible"
					//area means: 
					// - really only the visible area horizontally and vertically
					// - only the horizontal visible area, but vertically everything
					printerImage = ganttChart.getSettings().printSelectedVerticallyComplete() ? 
							ganttChart.getGanttComposite().getVerticallyFullImage() : ganttChart.getGanttComposite().getImage();
				}
				else {
					printerImage = ganttChart.getGanttComposite().getFullImage();
				}
				
				final Rectangle printerClientArea = PrintUtils.computePrintArea(printer);
				final Point scaleFactor = PrintUtils.computeScaleFactor(printer);
				final Point pageCount = PrintUtils.getPageCount(printer, printerImage);

				// Print pages Left to Right and then Top to Down
				for (int verticalPageNumber = 0; verticalPageNumber < pageCount.y; verticalPageNumber++) {

					for (int horizontalPageNumber = 0; horizontalPageNumber < pageCount.x; horizontalPageNumber++) {

						// Calculate bounds for the next page
						int printerClientAreaHeight = ganttChart.getSettings().printFooter() ? 
								(printerClientArea.height - PrintUtils.FOOTER_HEIGHT_IN_PRINTER_DPI) : printerClientArea.height;
						Rectangle printBounds = new Rectangle((printerClientArea.width / scaleFactor.x) * horizontalPageNumber,
						                                      (printerClientAreaHeight / scaleFactor.y) * verticalPageNumber,
						                                      printerClientArea.width / scaleFactor.x,
						                                      printerClientAreaHeight / scaleFactor.y);

						if (shouldPrint(printer.getPrinterData(), currentPage)) {
							printer.startPage();

							Transform printerTransform = new Transform(printer);

							// Adjust for DPI difference between display and printer
							printerTransform.scale(scaleFactor.x, scaleFactor.y);

							// Adjust for margins
							printerTransform.translate(printerClientArea.x / scaleFactor.x, printerClientArea.y / scaleFactor.y);

							// GanttChart will not automatically print the pages at the left margin.
							// Example: page 1 will print at x = 0, page 2 at x = 100, page 3 at x = 300
							// Adjust to print from the left page margin. i.e x = 0
							printerTransform.translate(-1 * printBounds.x, -1 * printBounds.y);
							gc.setTransform(printerTransform);

							int imgWidthClipping = printBounds.width;
							if (((horizontalPageNumber * printBounds.width)+printBounds.width) > printerImage.getImageData().width) {
								imgWidthClipping = printerImage.getImageData().width - (horizontalPageNumber * printBounds.width);
							}
							
							int imgHeightClipping = printBounds.height;
							if (((verticalPageNumber * printBounds.height)+printBounds.height) > printerImage.getImageData().height) {
								imgHeightClipping = printerImage.getImageData().height - (verticalPageNumber * printBounds.height);
							}
							
							gc.drawImage(printerImage, 
									horizontalPageNumber * printBounds.width, 
									verticalPageNumber * printBounds.height, 
									imgWidthClipping, imgHeightClipping,
									printBounds.x, printBounds.y, imgWidthClipping, imgHeightClipping);
							
							if (ganttChart.getSettings().printFooter())
								printFooter(gc, ganttChart, currentPage, printBounds);

							printer.endPage();
							printerTransform.dispose();
							
						}
						currentPage++;
					}
				}
				
				printerImage.dispose();
			}
			
			printer.endJob();
			gc.dispose();
			
			//only dispose the printer after the print job is done if it is configured to do so
			//this configuration enables the possibility to reuse a printer for several jobs
			if (disposePrinter)
				printer.dispose();
		}
	}

	/**
	 * Render the footer to a print page.
	 * @param gc The graphical context that is used for printing
	 * @param ganttChart The GanttChart which is currently printed.
	 * @param currentPage The number of the current page that is printed
	 * @param printBounds The bounds of the print area
	 */
	private void printFooter(GC gc, GanttChart ganttChart, int currentPage, Rectangle printBounds) {
		String footerDate = new SimpleDateFormat(ganttChart.getSettings().getDateFormat()).format(new Date());
		
		gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
		gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		gc.setFont(Display.getCurrent().getSystemFont());

		gc.drawLine(printBounds.x,
		            printBounds.y + printBounds.height+10,
		            printBounds.x + printBounds.width,
		            printBounds.y + printBounds.height+10);

		String pageText = ganttChart.getLanguageManger().getPrintPageText() + " " + currentPage; //$NON-NLS-1$
		gc.drawString(pageText, 
		            printBounds.x,
		            printBounds.y + printBounds.height + 15);

		Point dateExtend = gc.stringExtent(footerDate);
		gc.drawString(footerDate,
		            printBounds.x + printBounds.width - dateExtend.x,
		            printBounds.y + printBounds.height + 15);
	}

	/**
	 * Checks if a given page number should be printed.
	 * Page is allowed to print if:
	 * 	  User asked to print all pages or page in a specified range
	 * @param printerData The printer settings made by the user. Needed to determine
	 * 			if a page should be printed dependent to the scope
	 * @param currentPage The page that should be checked
	 * @return <code>true</code> if the given page should be printed, 
	 * 			<code>false</code> if not 
	 */
	private boolean shouldPrint(PrinterData printerData, int currentPage) {
		if (printerData.scope == PrinterData.PAGE_RANGE) {
			return currentPage >= printerData.startPage && currentPage <= printerData.endPage;
		}
		return true;
	}

	/**
	 * 
	 * @param dispose <code>true</code> if the printer that is set to this GanttChartPrintJob
	 * 			should be disposed after the print job is done, <code>false</code> if it should
	 * 			not be disposed so the printer can be reused for additional tasks.
	 */
	public void setDisposePrinter(boolean dispose) {
		this.disposePrinter = dispose;
	}
}
