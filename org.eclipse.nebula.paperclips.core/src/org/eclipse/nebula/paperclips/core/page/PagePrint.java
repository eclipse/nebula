/*
 * Copyright (c) 2005 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.core.page;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.paperclips.core.CompositeEntry;
import org.eclipse.nebula.paperclips.core.CompositePiece;
import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.nebula.paperclips.core.PrintIterator;
import org.eclipse.nebula.paperclips.core.PrintPiece;
import org.eclipse.nebula.paperclips.core.internal.util.PaperClipsUtil;
import org.eclipse.nebula.paperclips.core.internal.util.PrintSizeStrategy;
import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * A decorator Print which displays page headers and footers around a document
 * body, with page numbering capabilities.
 * <p>
 * PagePrint is horizontally and vertically greedy. Greedy prints take up all
 * the available space on the page.
 * <p>
 * <b>Note:</b> Avoid wrapping PagePrint in prints with space-optimizing
 * semantics (e.g. ColumnPrint equalizes columns on the last page), as this may
 * cause the total page count to be incorrect on some pages. At this time there
 * is no known fix. If wrapping a PagePrint is unavoidable, consider using a
 * custom PageNumberFormat which does not display the total page count.
 * 
 * @author Matthew Hall
 */
public class PagePrint implements Print {
	private static final int DEFAULT_GAP = 1;

	PageDecoration header;
	int headerGap = DEFAULT_GAP; // in points
	Print body;
	int footerGap = DEFAULT_GAP; // in points
	PageDecoration footer;

	/**
	 * Constructs a PagePrint with the given header and body.
	 * 
	 * @param header
	 *            a PageDecoration for creating the header. May be null.
	 * @param headerGap
	 *            the gap between the header and body, in points.
	 * @param body
	 *            the Print being decorated.
	 */
	public PagePrint(PageDecoration header, int headerGap, Print body) {
		this(header, headerGap, body, DEFAULT_GAP, null);
	}

	/**
	 * Constructs a PagePrint with the given header and body.
	 * 
	 * @param body
	 *            the Print being decorated.
	 * @param header
	 *            a PageDecoration for creating the header. May be null.
	 */
	public PagePrint(PageDecoration header, Print body) {
		this(header, DEFAULT_GAP, body);
	}

	/**
	 * Constructs a PagePrint with the given body.
	 * 
	 * @param body
	 *            the Print being decorated.
	 */
	public PagePrint(Print body) {
		this(null, body, null);
	}

	/**
	 * Constructs a PagePrint with the given body and footer.
	 * 
	 * @param body
	 *            the Print being decorated.
	 * @param footer
	 *            a PageDecoration for creating the footer. may be null.
	 */
	public PagePrint(Print body, PageDecoration footer) {
		this(body, DEFAULT_GAP, footer);
	}

	/**
	 * Constructs a PagePrint with the given body, header and footer.
	 * 
	 * @param body
	 *            the Print being decorated.
	 * @param footerGap
	 *            the gap between the body and footer, in points.
	 * @param footer
	 *            a PageDecoration for creating the footer. May be null.
	 */
	public PagePrint(Print body, int footerGap, PageDecoration footer) {
		this(null, DEFAULT_GAP, body, footerGap, footer);
	}

	/**
	 * Constructs a PagePrint with the given body, header and footer.
	 * 
	 * @param header
	 *            a PageDecoration for creating the header. May be null.
	 * @param body
	 *            the Print being decorated.
	 * @param footer
	 *            a PageDecoration for creating the footer. may be null.
	 */
	public PagePrint(PageDecoration header, Print body, PageDecoration footer) {
		this(header, DEFAULT_GAP, body, DEFAULT_GAP, footer);
	}

	/**
	 * Constructs a PagePrint with the given body, header and footer.
	 * 
	 * @param header
	 *            a PageDecoration for creating the header. May be null.
	 * @param headerGap
	 *            the gap between the header and body, in points.
	 * @param body
	 *            the Print being decorated.
	 * @param footerGap
	 *            the gap between the body and footer, in points.
	 * @param footer
	 *            a PageDecoration for creating the footer. May be null.
	 */
	public PagePrint(PageDecoration header, int headerGap, Print body,
			int footerGap, PageDecoration footer) {
		setHeader(header);
		setHeaderGap(headerGap);
		setBody(body);
		setFooterGap(footerGap);
		setFooter(footer);
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((footer == null) ? 0 : footer.hashCode());
		result = prime * result + footerGap;
		result = prime * result + ((header == null) ? 0 : header.hashCode());
		result = prime * result + headerGap;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PagePrint other = (PagePrint) obj;
		if (body == null) {
			if (other.body != null)
				return false;
		} else if (!body.equals(other.body))
			return false;
		if (footer == null) {
			if (other.footer != null)
				return false;
		} else if (!footer.equals(other.footer))
			return false;
		if (footerGap != other.footerGap)
			return false;
		if (header == null) {
			if (other.header != null)
				return false;
		} else if (!header.equals(other.header))
			return false;
		if (headerGap != other.headerGap)
			return false;
		return true;
	}

	/**
	 * Returns the page header.
	 * 
	 * @return the page header.
	 */
	public PageDecoration getHeader() {
		return header;
	}

	/**
	 * Sets the page header to the argument.
	 * 
	 * @param header
	 *            a PageDecoration which creates the header. May be null.
	 */
	public void setHeader(PageDecoration header) {
		this.header = header;
	}

	/**
	 * Returns the gap between the header and body, expressed in points.
	 * 
	 * @return the gap between the header and body, expressed in points.
	 */
	public int getHeaderGap() {
		return headerGap;
	}

	/**
	 * Sets the gap between the header and body to the argument, expressed in
	 * points.
	 * 
	 * @param points
	 *            the new gap between the header and body, expressed in points.
	 *            72 points = 1".
	 */
	public void setHeaderGap(int points) {
		this.headerGap = checkGap(points);
	}

	/**
	 * Returns the page body.
	 * 
	 * @return the page body.
	 */
	public Print getBody() {
		return body;
	}

	/**
	 * Sets the page body to the argument.
	 * 
	 * @param body
	 *            the new page body.
	 */
	public void setBody(Print body) {
		Util.notNull(body);
		this.body = body;
	}

	/**
	 * Returns the page footer.
	 * 
	 * @return the page footer.
	 */
	public PageDecoration getFooter() {
		return footer;
	}

	/**
	 * Sets the page footer to the argument.
	 * 
	 * @param footer
	 *            a PageDecoration which creates the footer. May be null.
	 */
	public void setFooter(PageDecoration footer) {
		this.footer = footer;
	}

	/**
	 * Returns the gap between the body and footer, expressed in points.
	 * 
	 * @return the gap between the body and footer, expressed in points.
	 */
	public int getFooterGap() {
		return footerGap;
	}

	/**
	 * Sets the gap between the body and footer to the argument, expressed in
	 * points.
	 * 
	 * @param points
	 *            the new gap between the body and footer (if there is a
	 *            footer).
	 */
	public void setFooterGap(int points) {
		this.footerGap = checkGap(points);
	}

	private static int checkGap(int gap) {
		if (gap < 0)
			PaperClips.error(SWT.ERROR_INVALID_ARGUMENT,
					"Gap must be >= 0 (value is " + gap + ")"); //$NON-NLS-1$ //$NON-NLS-2$
		return gap;
	}

	public PrintIterator iterator(Device device, GC gc) {
		if (header == null && footer == null)
			return body.iterator(device, gc);

		return new PageIterator(this, device, gc);
	}
}

class PageIterator implements PrintIterator {
	class PageNumberer {
		int pageCount = 0;

		synchronized PageNumber next() {
			return new InnerPageNumber();
		}

		class InnerPageNumber implements PageNumber {
			final int pageNumber = pageCount++; // POST-increment

			public int getPageCount() {
				return pageCount;
			}

			public int getPageNumber() {
				return pageNumber;
			}
		}

		PageNumberer copy() {
			PageNumberer result = new PageNumberer();
			result.pageCount = this.pageCount;
			return result;
		}
	}

	final Device device;
	final GC gc;

	final PageDecoration header;
	final int headerGap; // pixels
	final PrintIterator body;
	final int footerGap; // pixels
	final PageDecoration footer;

	final PageNumberer numberer;

	final Point minimumSize;
	final Point preferredSize;

	PageIterator(PagePrint print, Device device, GC gc) {
		this.device = device;
		this.gc = gc;

		Point dpi = device.getDPI();

		body = print.body.iterator(device, gc);
		header = print.header;
		headerGap = header == null ? 0 : print.headerGap * dpi.y / 72;
		footer = print.footer;
		footerGap = footer == null ? 0 : print.footerGap * dpi.y / 72;

		this.numberer = new PageNumberer();

		this.minimumSize = computeSize(PrintSizeStrategy.MINIMUM);
		this.preferredSize = computeSize(PrintSizeStrategy.PREFERRED);
	}

	PageIterator(PageIterator that) {
		this.device = that.device;
		this.gc = that.gc;

		this.body = that.body.copy();
		this.header = that.header;
		this.headerGap = that.headerGap;
		this.footer = that.footer;
		this.footerGap = that.footerGap;

		// FIXME: Wrapping PagePrint in a class with space-optimizing semantics
		// (ColumnPrint) can fork the total
		// page count. i.e. if the copied PageIterator is chosen as the optimal
		// layout, then previous pages will
		// have a page number spawned from a different page numberer. Thus the
		// total page count for those
		// previous pages will no longer be incremented with each new page.
		this.numberer = that.numberer.copy();
		this.pageNumber = that.pageNumber;

		this.minimumSize = that.minimumSize;
		this.preferredSize = that.preferredSize;
	}

	private Point computeSize(PrintSizeStrategy strategy) {
		Point size = strategy.computeSize(body);

		PageNumber samplePageNumber = new PageNumber() {
			public int getPageCount() {
				return 1;
			}

			public int getPageNumber() {
				return 0;
			}
		};

		if (header != null) {
			Print headerPrint = header.createPrint(samplePageNumber);
			if (headerPrint != null) {
				PrintIterator iter = headerPrint.iterator(device, gc);

				size.y += headerGap;

				Point headerSize = strategy.computeSize(iter);
				size.x = Math.max(size.x, headerSize.x);
				size.y += headerSize.y;
			}
		}

		if (footer != null) {
			Print footerPrint = footer.createPrint(samplePageNumber);
			if (footerPrint != null) {
				PrintIterator iter = footerPrint.iterator(device, gc);

				size.y += footerGap;

				Point footerSize = strategy.computeSize(iter);
				size.x = Math.max(size.x, footerSize.x);
				size.y += footerSize.y;
			}
		}

		return size;
	}

	public boolean hasNext() {
		return body.hasNext();
	}

	public Point minimumSize() {
		return new Point(minimumSize.x, minimumSize.y);
	}

	public Point preferredSize() {
		return new Point(preferredSize.x, preferredSize.y);
	}

	public PrintPiece next(int width, final int height) {
		PageNumber pageNumber = getCurrentPageNumber();

		// HEADER
		PrintPiece headerPiece = null;
		int availableHeight = height;
		if (header != null) {
			Print headerPrint = header.createPrint(pageNumber);
			if (headerPrint != null) {
				headerPiece = getDecorationPrintPiece(headerPrint, width,
						availableHeight);
				if (headerPiece == null)
					return null;
				availableHeight -= (heightOf(headerPiece) + headerGap);
			}
		}

		// FOOTER
		PrintPiece footerPiece = null;
		if (footer != null) {
			Print footerPrint = footer.createPrint(pageNumber);
			if (footerPrint != null) {
				footerPiece = getDecorationPrintPiece(footerPrint, width,
						availableHeight);
				if (footerPiece == null) {
					PaperClipsUtil.dispose(headerPiece);
					return null;
				}
				availableHeight -= (heightOf(footerPiece) + footerGap);
			}
		}

		// BODY
		PrintPiece bodyPiece = PaperClips.next(body, width, availableHeight);
		if (bodyPiece == null) {
			PaperClipsUtil.dispose(headerPiece, footerPiece);
			return null;
		}

		PrintPiece result = createResult(height, headerPiece, bodyPiece,
				footerPiece);
		advancePageNumber();
		return result;
	}

	private int heightOf(PrintPiece piece) {
		return piece.getSize().y;
	}

	PageNumber pageNumber;

	private void advancePageNumber() {
		// Null the pageNumber field so the next iteration advances to the next
		// page.
		pageNumber = null;
	}

	private PageNumber getCurrentPageNumber() {
		if (pageNumber == null)
			pageNumber = numberer.next();
		return pageNumber;
	}

	private PrintPiece createResult(int height, PrintPiece headerPiece,
			PrintPiece bodyPiece, PrintPiece footerPiece) {
		if (headerPiece == null && footerPiece == null)
			return bodyPiece;

		List entries = new ArrayList();

		if (headerPiece != null)
			entries.add(createEntry(headerPiece, 0));

		int y = headerPiece == null ? 0 : heightOf(headerPiece) + headerGap;
		entries.add(createEntry(bodyPiece, y));

		if (footerPiece != null) {
			y = height - heightOf(footerPiece);
			entries.add(createEntry(footerPiece, y));
		}

		return new CompositePiece(entries);
	}

	private CompositeEntry createEntry(PrintPiece piece, int y) {
		return new CompositeEntry(piece, new Point(0, y));
	}

	private PrintPiece getDecorationPrintPiece(Print decoration, int width,
			int height) {
		PrintIterator iterator = decoration.iterator(device, gc);
		PrintPiece piece = PaperClips.next(iterator, width, height);

		if (piece == null)
			return null;
		if (iterator.hasNext()) {
			piece.dispose();
			return null;
		}
		return piece;
	}

	public PrintIterator copy() {
		return new PageIterator(this);
	}
}