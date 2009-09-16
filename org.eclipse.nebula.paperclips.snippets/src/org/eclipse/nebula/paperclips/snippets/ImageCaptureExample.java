/*
 * Copyright (c) 2006 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.snippets;

import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.nebula.paperclips.core.PrintJob;
import org.eclipse.nebula.paperclips.core.PrintPiece;
import org.eclipse.nebula.paperclips.core.grid.DefaultGridLook;
import org.eclipse.nebula.paperclips.core.grid.GridPrint;
import org.eclipse.nebula.paperclips.core.text.TextPrint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Display;

/**
 * Demonstrate capturing the pages of a print job to in-memory images.
 * 
 * @author Matthew Hall
 */
public class ImageCaptureExample {
	/**
	 * Returns a sample print
	 * 
	 * @return a sample print
	 */
	public static Print createPrint() {
		GridPrint grid = new GridPrint(new DefaultGridLook());

		int COLS = 5;
		int ROWS = 50;

		for (int c = 0; c < COLS; c++) {
			grid.addColumn("d");
			grid.addHeader(new TextPrint("Column " + (c + 1)));
			grid.addFooter(new TextPrint("Column " + (c + 1)));
		}

		for (int r = 0; r < ROWS; r++)
			for (int c = 0; c < COLS; c++)
				grid.add(new TextPrint("Row " + (r + 1) + " Col " + (c + 1)));

		return grid;
	}

	/**
	 * Captures the page to an image and returns it.
	 * 
	 * @param printer
	 *            the printer device.
	 * @param page
	 *            the page to capture.
	 * @param imageSize
	 *            the size of the returned image
	 * @return an image of the captured page.
	 */
	public static ImageData captureImageData(Printer printer, PrintPiece page,
			Point imageSize) {
		Point pageSize = page.getSize();

		Image image = null;
		GC gc = null;
		Transform transform = null;

		try {
			image = new Image(printer, imageSize.x, imageSize.y);
			gc = new GC(image);
			gc.setAdvanced(true);
			gc.setAntialias(SWT.ON);
			gc.setTextAntialias(SWT.ON);
			gc.setInterpolation(SWT.HIGH);
			transform = new Transform(printer);

			// Scale from the page size to the image size
			gc.getTransform(transform);
			transform.scale((float) imageSize.x / (float) pageSize.x,
					(float) imageSize.y / (float) pageSize.y);
			gc.setTransform(transform);

			page.paint(gc, 0, 0);

			return image.getImageData();
		} finally {
			if (transform != null)
				transform.dispose();
			if (gc != null)
				gc.dispose();
			if (image != null)
				image.dispose();
		}
	}

	private static String getImageName(int index) {
		// Saving PNG is not supported until SWT version 3.3 or later.
		return "capture_image_" + index + ".jpg";
	}

	/**
	 * Demonstrate capturing the pages of a print to in-memory images.
	 * 
	 * @param args
	 *            command-line arguments (ignored)
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Point displayDPI = display.getDPI();
		display.dispose();

		Printer printer = new Printer(new PrinterData());
		Point printerDPI = printer.getDPI();

		try {
			PrintJob job = new PrintJob("ImageCapture.java", createPrint());

			PrintPiece[] pages = PaperClips.getPages(job, printer);

			ImageLoader imageLoader = new ImageLoader();
			for (int i = 0; i < pages.length; i++) {
				PrintPiece page = pages[i];
				Point pageSize = page.getSize();
				pageSize.x = pageSize.x * displayDPI.x / printerDPI.x;
				pageSize.y = pageSize.y * displayDPI.y / printerDPI.y;
				ImageData pageImage = captureImageData(printer, page, pageSize);

				// Do something with the image
				pageImage.scanlinePad = 1;

				imageLoader.data = new ImageData[] { pageImage };
				imageLoader.save(getImageName(i), SWT.IMAGE_JPEG);
			}

		} finally {
			printer.dispose();
		}
	}
}
