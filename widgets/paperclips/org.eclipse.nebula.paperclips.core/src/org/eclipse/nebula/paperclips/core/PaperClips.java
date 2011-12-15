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
package org.eclipse.nebula.paperclips.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.paperclips.core.internal.util.PaperClipsUtil;
import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;

/**
 * This class contains static constants and methods for preparing and printing
 * documents. Methods in this class supersede those in PrintUtil.
 * 
 * @author Matthew Hall
 */
public class PaperClips {
	private PaperClips() {
	} // no instances

	static boolean debug = false;

	/**
	 * Indicates that the printer's default page orientation should be used.
	 */
	public static final int ORIENTATION_DEFAULT = SWT.DEFAULT;

	/**
	 * Indicates portrait page orientation.
	 */
	public static final int ORIENTATION_PORTRAIT = SWT.VERTICAL;

	/**
	 * Indicates landscape page orientation.
	 */
	public static final int ORIENTATION_LANDSCAPE = SWT.HORIZONTAL;

	/**
	 * Triggers an appropriate exception based on the passed in error code.
	 * 
	 * @param code
	 *            the SWT error code.
	 */
	public static void error(int code) {
		SWT.error(code, null);
	}

	/**
	 * Triggers an unspecified exception with the passed in detail.
	 * 
	 * @param detail
	 *            more information about error.
	 */
	public static void error(String detail) {
		SWT.error(SWT.ERROR_UNSPECIFIED, null, detail);
	}

	/**
	 * Triggers an appropriate exception based on the passed in error code.
	 * 
	 * @param code
	 *            the SWT error code.
	 * @param detail
	 *            more information about error.
	 */
	public static void error(int code, String detail) {
		SWT.error(code, null, detail);
	}

	/**
	 * <b>EXPERIMENTAL</b>: Sets whether debug mode is enabled. This mode may be
	 * used for troubleshooting documents that cannot be laid out for some
	 * reason (e.g. "Cannot layout page x" error occurs).
	 * 
	 * <p>
	 * <b>THIS API IS EXPERIMENTAL AND MAY BE REMOVED OR CHANGED IN THE
	 * FUTURE.</b>
	 * 
	 * @param debug
	 *            true to enable debug mode, false to disable it.
	 */
	public static void setDebug(boolean debug) {
		PaperClips.debug = debug;
	}

	/**
	 * <b>EXPERIMENTAL</b>: Returns whether debug mode is enabled.
	 * 
	 * <p>
	 * <b>THIS API IS EXPERIMENTAL AND MAY BE REMOVED OR CHANGED IN THE
	 * FUTURE.</b>
	 * 
	 * @return whether debug mode is enabled.
	 */
	public static boolean getDebug() {
		return debug;
	}

	/**
	 * Returns a PrinterData for the system-default printer, or the first
	 * printer if no default printer is configured.
	 * 
	 * @return a PrinterData for the system-default printer, or the first
	 *         printer if no default printer is configured.
	 */
	public static PrinterData getDefaultPrinterData() {
		PrinterData printerData = Printer.getDefaultPrinterData();
		if (printerData == null) {
			// Linux may have one or more printers without a default printer
			PrinterData[] list = Printer.getPrinterList();
			if (list.length > 0)
				printerData = list[0];
		}
		return printerData;
	}

	/**
	 * Calls iterator.next(width, height) and returns the result. This method
	 * checks multiple conditions to ensure proper usage and behavior of
	 * PrintIterators.
	 * <p>
	 * This method is intended to be used by PrintIterator classes, as a
	 * results-checking alternative to calling next(int, int) directly on the
	 * target iterator. All PrintIterator classes in the PaperClips library use
	 * this method instead of directly calling the
	 * {@link PrintIterator#next(int, int)} method.
	 * 
	 * @param iterator
	 *            the PrintIterator
	 * @param width
	 *            the available width.
	 * @param height
	 *            the available height.
	 * @return the next portion of the Print, or null if the width and height
	 *         are not enough to display any of the iterator's contents.
	 */
	public static PrintPiece next(PrintIterator iterator, int width, int height) {
		Util.notNull(iterator);
		if (width < 0 || height < 0)
			error(SWT.ERROR_INVALID_ARGUMENT,
					"PrintPiece size " + width + "x" + height + " not possible"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if (!iterator.hasNext())
			error("Iterator " + iterator + " has no more content."); //$NON-NLS-1$ //$NON-NLS-2$

		PrintPiece result = iterator.next(width, height);

		if (result != null) {
			Point size = result.getSize();
			if (size.x > width || size.y > height)
				error("Iterator " + iterator + " produced a " + size.x + "x" + size.y + " piece for a " + width //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						+ "x" + height + " area."); //$NON-NLS-1$//$NON-NLS-2$
		} else if (debug) {
			return new NullPrintPiece();
		}
		return result;
	}

	/**
	 * Prints the print job to the given printer. This method constructs a
	 * Printer, forwards to {@link #print(PrintJob, Printer)}, and disposes the
	 * printer before returning.
	 * 
	 * @param printJob
	 *            the print job.
	 * @param printerData
	 *            the PrinterData of the selected printer.
	 */
	public static void print(PrintJob printJob, PrinterData printerData) {
		Printer printer = new Printer(printerData);
		try {
			print(printJob, printer);
		} finally {
			printer.dispose();
		}
	}

	/**
	 * Prints the print job to the given printer.
	 * 
	 * @param printJob
	 *            the print job.
	 * @param printer
	 *            the printer device.
	 */
	public static void print(PrintJob printJob, Printer printer) {
		// Bug in SWT on OSX: If Printer.startJob() is not called first, the GC
		// will be disposed by
		// default.
		startJob(printer, printJob.getName());

		boolean completed = false;
		try {
			GC gc = createAndConfigureGC(printer);
			try {
				print(printJob, printer, gc);
			} finally {
				gc.dispose();
			}
			printer.endJob();
			completed = true;
		} finally {
			if (!completed)
				cancelJob(printer);
		}
	}

	private static void startJob(Printer printer, String jobName) {
		if (!printer.startJob(jobName))
			error("Unable to start print job"); //$NON-NLS-1$
	}

	private static void cancelJob(Printer printer) {
		if (isGTK())
			printer.endJob(); // Printer.cancelJob() not implemented on GTK
		else
			printer.cancelJob();
	}

	private static GC createAndConfigureGC(Printer printer) {
		GC gc = new GC(printer);
		gc.setAdvanced(true);
		return gc;
	}

	/**
	 * Prints the print job to the specified printer using the GC. This method
	 * does not manage the print job lifecycle (it does not call startJob or
	 * endJob).
	 * 
	 * @param printJob
	 *            the print job
	 * @param printer
	 *            the printer
	 * @param gc
	 *            the GC
	 */
	private static void print(PrintJob printJob, Printer printer, final GC gc) {
		final PrinterData printerData = printer.getPrinterData();

		PrintPiece[] pages = getPages(printJob, printer, gc);

		int startPage = 0;
		int endPage = pages.length - 1;
		if (printerData.scope == PrinterData.PAGE_RANGE) {
			// Convert from PrinterData's one-based indices to our zero-based
			// indices
			startPage = Math.max(startPage, printerData.startPage - 1);
			endPage = Math.min(endPage, printerData.endPage - 1);
		}

		final int collatedCopies;
		final int noncollatedCopies;
		if (printerData.collate) { // always false if printer driver performs
			// collation
			collatedCopies = printerData.copyCount; // always 1 if printer
			// driver handles copy count
			noncollatedCopies = 1;
		} else {
			noncollatedCopies = printerData.copyCount; // always 1 if printer
			// driver handles copy
			// count
			collatedCopies = 1;
		}

		printPages(printer, gc, pages, startPage, endPage, collatedCopies,
				noncollatedCopies);
	}

	private static void printPages(final Printer printer, final GC gc,
			final PrintPiece[] pages, final int startPage, final int endPage,
			final int collatedCopies, final int noncollatedCopies) {
		disposeUnusedPages(pages, startPage, endPage);

		Rectangle paperBounds = getPaperBounds(printer);
		final int x = paperBounds.x;
		final int y = paperBounds.y;

		try {
			for (int collated = 0; collated < collatedCopies; collated++) {
				for (int pageIndex = startPage; pageIndex <= endPage; pageIndex++) {
					for (int noncollated = 0; noncollated < noncollatedCopies; noncollated++) {
						if (printer.startPage()) {
							pages[pageIndex].paint(gc, x, y);
							pages[pageIndex].dispose();
							printer.endPage();
						} else {
							error("Unable to start page " + pageIndex); //$NON-NLS-1$
						}
					}
				}
			}
		} finally {
			PaperClipsUtil.dispose(pages);
		}
	}

	private static void disposeUnusedPages(PrintPiece[] pages, int startPage,
			int endPage) {
		PaperClipsUtil.dispose(pages, 0, startPage);
		PaperClipsUtil.dispose(pages, endPage + 1, pages.length);
	}

	/**
	 * Processes the print job and returns an array of pages for the given
	 * printer device. Each element in the returned array has already had the
	 * page orientation and page margins applied. Therefore, when calling the
	 * paint(GC, int, int) method on each page, the printer's trim should be
	 * provided as the x and y arguments. In other words, the trim is taken as a
	 * minimum margin while applying calculating margins, but the position where
	 * the page's content is drawn is determined solely by the margin, and is
	 * not offset by the trim. This behavior is helpful for screen display, and
	 * is already compensated for in the {@link #print(PrintJob, Printer)}
	 * method.
	 * 
	 * @param printer
	 *            the printing device.
	 * @param printJob
	 *            the print job.
	 * @return an array of all pages of the print job. Each element of the
	 *         returned array represents one page in the printed document.
	 */
	public static PrintPiece[] getPages(PrintJob printJob, Printer printer) {
		startDummyJob(printer, printJob.getName());

		try {
			GC gc = createAndConfigureGC(printer);
			try {
				return getPages(printJob, printer, gc);
			} finally {
				gc.dispose();
			}
		} finally {
			endDummyJob(printer);
		}
	}

	/**
	 * Starts a dummy job on the given Printer if the platform requires it.
	 * Dummy jobs allow the various Print components of PaperClips to perform
	 * measurements required for document layout, without actually sending a job
	 * to the printer. Only Mac OS X Carbon and Linux GTK+ are known to require
	 * dummy jobs.
	 * 
	 * @param printer
	 *            the Printer hosting the dummy print job.
	 * @param name
	 *            the name of the dummy print job.
	 */
	public static void startDummyJob(Printer printer, String name) {
		// On Mac OS X Carbon and Linux GTK+, created GC is disposed unless
		// Printer.startJob() is called
		// first.
		if (isCarbon() || isGTK())
			startJob(printer, name);
	}

	/**
	 * Ends a dummy job on the given Printer if the platform requires a dummy
	 * job.
	 * 
	 * @param printer
	 *            the Printer hosting the dummy print job.
	 */
	public static void endDummyJob(Printer printer) {
		if (isGTK()) { // Linux GTK
			// Printer.cancelJob() is not implemented in SWT since GTK has no
			// API for cancelling a print job. For now we must use endJob(),
			// even though it spits out an empty page.

			// printer.cancelJob(); // Not implemented in SWT on GTK
			printer.endJob();

			// See also:
			// http://bugzilla.gnome.org/show_bug.cgi?id=339323
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=212594
		} else if (isCarbon()) // Mac OSX
			// 2007-04-30: A bug in SWT on Mac OSX prior to 3.3 renders Printer
			// instances useless after a call to cancelJob().
			// Therefore on Mac OSX we call endJob() instead of cancelJob().
			if (SWT.getVersion() < 3346) { // Version 3.3
				printer.endJob();
			} else {
				printer.cancelJob();
			}
	}

	private static boolean isCarbon() {
		return SWT.getPlatform().equals("carbon"); //$NON-NLS-1$
	}

	private static boolean isGTK() {
		return SWT.getPlatform().equals("gtk"); //$NON-NLS-1$
	}

	private static PrintPiece[] getPages(PrintJob printJob, Printer printer,
			GC gc) {
		PageEnumeration enumeration = new PageEnumeration(printJob, printer, gc);
		List pages = new ArrayList();
		while (enumeration.hasNext()) {
			PrintPiece page = enumeration.nextPage();
			if (page == null) {
				int pageNumber = pages.size() + 1;
				PaperClipsUtil.dispose(pages);
				error("Unable to layout page " + pageNumber); //$NON-NLS-1$
			}
			pages.add(page);
		}

		return (PrintPiece[]) pages.toArray(new PrintPiece[pages.size()]);
	}

	/**
	 * Returns a {@link PageEnumeration} for the passed in PrintJob on the given
	 * Printer, using the given GC. The Printer and GC must not be disposed
	 * while the enumeration is in use.
	 * 
	 * @param printJob
	 *            the print job
	 * @param printer
	 *            the Printer device, which must not be disposed while the
	 *            PageEnumeration is in use.
	 * @param gc
	 *            the GC, which must not be disposed while the PageEnumeration
	 *            is in use.
	 * @return a {@link PageEnumeration} for the passed in PrintJob.
	 */
	public static PageEnumeration getPageEnumeration(PrintJob printJob,
			Printer printer, GC gc) {
		return new PageEnumeration(printJob, printer, gc);
	}

	/**
	 * Returns the bounding rectangle of the paper, including non-printable
	 * margins.
	 * 
	 * @param printer
	 *            the printer device.
	 * @return a rectangle whose edges correspond to the edges of the paper.
	 */
	public static Rectangle getPaperBounds(Printer printer) {
		Rectangle rect = getPrintableBounds(printer);
		return printer.computeTrim(rect.x, rect.y, rect.width, rect.height);
	}

	/**
	 * Returns the bounding rectangle of the printable area on the paper.
	 * 
	 * @param printer
	 *            the printer device.
	 * @return the bounding rectangle of the printable area on the paper.
	 */
	public static Rectangle getPrintableBounds(Printer printer) {
		return printer.getClientArea();
	}

	/**
	 * Returns the bounding rectangle of the printable area which is inside the
	 * given margins on the paper. The printer's minimum margins are reflected
	 * in the returned rectangle.
	 * 
	 * @param printer
	 *            the printer device.
	 * @param margins
	 *            the desired page margins.
	 * @return the bounding rectangle on the printable area which is within the
	 *         margins.
	 */
	public static Rectangle getMarginBounds(Margins margins, Printer printer) {
		Rectangle paperBounds = getPaperBounds(printer);

		// Calculate the pixel coordinates for the margins
		Point dpi = printer.getDPI();
		int top = paperBounds.y + (margins.top * dpi.y / 72);
		int left = paperBounds.x + (margins.left * dpi.x / 72);
		int right = paperBounds.x + paperBounds.width
				- (margins.right * dpi.x / 72);
		int bottom = paperBounds.y + paperBounds.height
				- (margins.bottom * dpi.y / 72);

		// Enforce the printer's minimum margins.
		Rectangle printableBounds = getPrintableBounds(printer);
		if (top < printableBounds.y)
			top = printableBounds.y;
		if (left < printableBounds.x)
			left = printableBounds.x;
		if (right > printableBounds.x + printableBounds.width)
			right = printableBounds.x + printableBounds.width;
		if (bottom > printableBounds.y + printableBounds.height)
			bottom = printableBounds.y + printableBounds.height;

		return new Rectangle(left, top, right - left, bottom - top);
	}
}