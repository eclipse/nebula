/*
 * Copyright (c) 2007 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.core;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;

/**
 * An enumeration of pages for given print job on the given printer device. Each
 * element in the enumeration has already had the page orientation and page
 * margins applied. Therefore, when calling the paint(GC, int, int) method on
 * each page, the printer's trim should be provided as the x and y arguments. In
 * other words, the trim is taken as a minimum margin while applying calculating
 * margins, but the position where the page's content is drawn is determined
 * solely by the margin, and is not offset by the trim. This behavior is helpful
 * for screen display, and is already compensated for in the
 * {@link PaperClips#print(PrintJob, Printer) } method.
 * 
 * @see PaperClips#getPages(PrintJob, Printer)
 * @author Matthew Hall
 */
public class PageEnumeration {
	private PrintIterator document;
	private Rectangle marginBounds;
	private Rectangle paperBounds;

	private boolean hasNext;

	PageEnumeration(PrintJob job, Printer printer, GC gc) {
		// Rotate the document (and margins with it) depending on print job
		// orientation.
		job = applyOrientation(job, printer);
		Margins margins = job.getMargins();

		marginBounds = PaperClips.getMarginBounds(margins, printer);
		paperBounds = PaperClips.getPaperBounds(printer);

		document = job.getDocument().iterator(printer, gc);
		hasNext = document.hasNext();
	}

	/**
	 * Returns whether any pages remain.
	 * 
	 * @return whether any pages remain.
	 */
	public boolean hasNext() {
		return hasNext;
	}

	/**
	 * Returns the next page.
	 * 
	 * @return the next page.
	 */
	public PrintPiece nextPage() {
		if (!hasNext)
			return null;

		PrintPiece page = PaperClips.next(document, marginBounds.width,
				marginBounds.height);
		hasNext = notNull(page) && notDebugPiece(page) && document.hasNext();
		PrintPiece result = page == null ? null : createPagePiece(page);
		if (!hasNext) {
			document = null;
			marginBounds = null;
			paperBounds = null;
		}
		return result;
	}

	private PrintPiece createPagePiece(PrintPiece page) {
		Point offset = new Point(marginBounds.x - paperBounds.x, marginBounds.y
				- paperBounds.y);
		CompositeEntry entry = new CompositeEntry(page, offset);
		Point size = new Point(paperBounds.width, paperBounds.height);
		return new CompositePiece(new CompositeEntry[] { entry }, size);
	}

	private static boolean notNull(PrintPiece page) {
		return page != null;
	}

	private static boolean notDebugPiece(PrintPiece page) {
		return !(PaperClips.debug && page instanceof NullPrintPiece);
	}

	private static PrintJob applyOrientation(PrintJob printJob, Printer printer) {
		int orientation = printJob.getOrientation();

		Rectangle paperBounds = PaperClips.getPaperBounds(printer);
		if (((orientation == PaperClips.ORIENTATION_LANDSCAPE) && (paperBounds.width < paperBounds.height))
				|| ((orientation == PaperClips.ORIENTATION_PORTRAIT) && (paperBounds.height < paperBounds.width))) {
			String name = printJob.getName();
			Print document = new RotatePrint(printJob.getDocument());
			Margins margins = printJob.getMargins().rotate();
			printJob = new PrintJob(name, document).setMargins(margins)
					.setOrientation(PaperClips.ORIENTATION_DEFAULT);
		}

		return printJob;
	}
}