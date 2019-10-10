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

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Display;

/**
 * Helper class for computations regarding printing of GanttCharts.
 */
public class PrintUtils {

	/**
	 * The height of the footer on a print page.
	 */
	public static final int FOOTER_HEIGHT_IN_PRINTER_DPI = 200;

	/**
	 * Computes the print area, including margins
	 * @param printer The printer that will be used to print the chart
	 * @return The print area
	 */
	public static Rectangle computePrintArea(Printer printer) {
		// Get the printable area
	    Rectangle rect = printer.getClientArea();

	    // Compute the trim
	    Rectangle trim = printer.computeTrim(0, 0, 0, 0);

	    // Get the printer's DPI
	    Point dpi = printer.getDPI();
	    dpi.x = dpi.x / 2;
	    dpi.y = dpi.y / 2;

	    // Calculate the printable area, using 1 inch margins
	    int left = trim.x + dpi.x;
	    if (left < rect.x) left = rect.x;

	    int right = (rect.width + trim.x + trim.width) - dpi.x;
	    if (right > rect.width) right = rect.width;

	    int top = trim.y + dpi.y;
	    if (top < rect.y) top = rect.y;

	    int bottom = (rect.height + trim.y + trim.height) - dpi.y;
	    if (bottom > rect.height) bottom = rect.height;

	    return new Rectangle(left, top, right - left, bottom - top);
	}

	/**
	 * 
	 * @param printer The printer that will be used to print the chart
	 * @return Amount to scale the screen resolution by, to match the printer
	 * 			resolution.
	 */
	public static Point computeScaleFactor(Printer printer) {
		Point screenDPI = Display.getDefault().getDPI();
		Point printerDPI = printer.getDPI();

		int scaleFactorX = printerDPI.x / screenDPI.x;
		int scaleFactorY = printerDPI.y / screenDPI.y;
		return new Point(scaleFactorX, scaleFactorY);
	}
	
	/**
	 * Calculate number of horizontal and vertical pages needed
	 * to print the given image of the chart.
	 * @param printer The printer that will be used to print the chart
	 * @param image The image of the chart that should be printed.
	 * @return The number of horizontal and vertical pages that will be
	 * 			printed.
	 */
	public static Point getPageCount(Printer printer, Image image){
		Rectangle ganttArea = getVisibleGanttChartArea(image);
		Rectangle printArea = PrintUtils.computePrintArea(printer);
		Point scaleFactor = PrintUtils.computeScaleFactor(printer);
		
		int numOfHorizontalPages = ganttArea.width / (printArea.width / scaleFactor.x);
		int numOfVerticalPages = ganttArea.height / (printArea.height / scaleFactor.y);
		
		// Adjusting for 0 index
		return new Point(numOfHorizontalPages + 1, numOfVerticalPages + 1);
	}

	/**
	 * 
	 * @param image The image of the chart that should be printed.
	 * @return The size of the image representation of the chart that 
	 * 			should be printed.
	 */
	public static Rectangle getVisibleGanttChartArea(Image image) {
		return new Rectangle(0, 0, image.getImageData().width, image.getImageData().height);
	}

}
