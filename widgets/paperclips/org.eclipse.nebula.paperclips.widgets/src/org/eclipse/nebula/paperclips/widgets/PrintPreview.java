/*
 * Copyright (c) 2006 Matthew Hall and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.paperclips.core.PageEnumeration;
import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.nebula.paperclips.core.PrintJob;
import org.eclipse.nebula.paperclips.core.PrintPiece;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

/**
 * A WYSIWYG (what you see is what you get) print preview panel. This control
 * displays a preview of what a PrintJob will look like on paper, depending on
 * the selected printer.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 *
 * @author Matthew Hall
 */
public class PrintPreview extends Canvas {
	private static final int ALL_PAGES = -1;

	private PrintJob printJob = null;
	private PrinterData printerData = PaperClips.getDefaultPrinterData();
	private int pageIndex = 0;
	private boolean fitHorizontal = true;
	private boolean fitVertical = true;
	private float scale = 1.0f;
	private int horizontalPageCount = 1;
	private int verticalPageCount = 1;
	private boolean lazy = false;

	// The bounds of the paper on the printer device.
	private Point paperSize = null;
	private Printer printer = null;
	private GC gc = null;

	private PageEnumeration pageEnumeration = null;
	private List<PrintPiece> pages = null;
	private Point pageDisplaySize = null;
	private Point[] pageDisplayLocations = null;

	// Margins and page spacing include paper boilerplate.
	private Rectangle margins = new Rectangle(10, 10, 10, 10);
	private Point pageSpacing = new Point(10, 10);

	/**
	 * Constructs a PrintPreview control.
	 *
	 * @param parent
	 *            the parent control.
	 * @param style
	 *            the control style.
	 */
	public PrintPreview(Composite parent, int style) {
		super(parent, style | SWT.DOUBLE_BUFFERED);

		addListener(SWT.Paint, event -> {
			paint(event);
		});

		addListener(SWT.Resize, event -> {
			invalidatePageDisplayBounds();
			redraw();
		});

		addListener(SWT.Dispose, event -> {
			disposeResources();
		});
	}

	/**
	 * Returns the print job.
	 *
	 * @return the print job.
	 */
	public PrintJob getPrintJob() {
		checkWidget();
		return printJob;
	}

	/**
	 * Sets the print job to preview.
	 *
	 * @param printJob
	 *            the print job to preview.
	 */
	public void setPrintJob(PrintJob printJob) {
		checkWidget();
		this.printJob = printJob;
		this.pageIndex = 0;
		disposePages();
		redraw();
	}

	/**
	 * Returns the PrinterData for the printer to preview on.
	 *
	 * @return the PrinterData for the printer to preview on.
	 */
	public PrinterData getPrinterData() {
		checkWidget();
		return printerData;
	}

	/**
	 * Sets the PrinterData for the printer to preview on.
	 *
	 * @param printerData
	 *            the PrinterData for the printer to preview on.
	 */
	public void setPrinterData(PrinterData printerData) {
		checkWidget();
		this.printerData = printerData;
		this.pageIndex = 0;
		disposePrinter(); // disposes pages too
		redraw();
	}

	/**
	 * Returns the index of the first visible page.
	 *
	 * @return the index of the first visible page.
	 */
	public int getPageIndex() {
		checkWidget();
		return pageIndex;
	}

	/**
	 * Sets the index of the first visible page to the argument.
	 *
	 * @param pageIndex
	 *            the page index.
	 */
	public void setPageIndex(int pageIndex) {
		checkWidget();
		this.pageIndex = pageIndex;
		redraw();
	}

	/**
	 * Returns the known number of pages in the print job. If
	 * {@link #setLazyPageLayout(boolean)} is set to true, this method returns
	 * the number of pages laid out so far. This method returns 0 when
	 * {@link #getPrintJob()} is null or {@link #getPrinterData()} is null.
	 *
	 * @return the known number of pages in the print job.
	 */
	public int getPageCount() {
		checkWidget();
		fetchPages(lazy ? horizontalPageCount * verticalPageCount : ALL_PAGES);
		return pages == null ? 0 : pages.size();
	}

	/**
	 * Returns whether all pages have been laid out.
	 *
	 * @return whether all pages have been laid out.
	 */
	public boolean isPageLayoutComplete() {
		checkWidget();
		fetchPages(horizontalPageCount * verticalPageCount);
		return pageEnumeration == null || !pageEnumeration.hasNext();
	}

	/**
	 * Returns whether the page scales to fit the document horizontally.
	 *
	 * @return whether the page scales to fit the document horizontally.
	 */
	public boolean isFitHorizontal() {
		checkWidget();
		return fitHorizontal;
	}

	/**
	 * Sets whether the page scales to fit the document horizontally.
	 *
	 * @param fitHorizontal
	 *            whether the page scales to fit the document horizontally.
	 */
	public void setFitHorizontal(boolean fitHorizontal) {
		checkWidget();
		if (this.fitHorizontal != fitHorizontal) {
			this.fitHorizontal = fitHorizontal;
			invalidatePageDisplayBounds();
			redraw();
		}
	}

	/**
	 * Returns whether the page scales to fit the document vertically.
	 *
	 * @return whether the page scales to fit the document vertically.
	 */
	public boolean isFitVertical() {
		checkWidget();
		return fitVertical;
	}

	/**
	 * Sets whether the page scales to fit the document vertically.
	 *
	 * @param fitVertical
	 *            whether the page scales to fit the document vertically.
	 */
	public void setFitVertical(boolean fitVertical) {
		checkWidget();
		if (this.fitVertical != fitVertical) {
			this.fitVertical = fitVertical;
			invalidatePageDisplayBounds();
			redraw();
		}
	}

	/**
	 * Returns the view scale. The document displays at this scale when
	 * !(isFitHorizontal() || isFitVertical()).
	 *
	 * @return the view scale.
	 */
	public float getScale() {
		checkWidget();
		return scale;
	}

	/**
	 * Sets the view scale.
	 *
	 * @param scale
	 *            the view scale. A scale of 1.0 causes the document to appear
	 *            at full size on the computer screen.
	 */
	public void setScale(float scale) {
		checkWidget();
		this.scale = checkScale(scale);
		if (!(fitVertical || fitHorizontal)) {
			invalidatePageDisplayBounds();
			redraw();
		}
	}

	private static float checkScale(float scale) {
		if (!(scale > 0))
			PaperClips.error(SWT.ERROR_INVALID_ARGUMENT, "Scale must be > 0"); //$NON-NLS-1$
		return scale;
	}

	/**
	 * Returns how many pages will be displayed in the horizontal direction.
	 * <p>
	 * <b>THIS API IS EXPERIMENTAL AND MAY BE REMOVED OR CHANGED IN THE
	 * FUTURE.</b>
	 *
	 * @return how many pages will be displayed in the horizontal direction.
	 */
	public int getHorizontalPageCount() {
		checkWidget();
		return horizontalPageCount;
	}

	/**
	 * Sets how many pages will be displayed in the horizontal direction.
	 * <p>
	 * <b>THIS API IS EXPERIMENTAL AND MAY BE REMOVED OR CHANGED IN THE
	 * FUTURE.</b>
	 *
	 * @param horizontalPages
	 *            how many pages will be displayed in the horizontal direction.
	 */
	public void setHorizontalPageCount(int horizontalPages) {
		checkWidget();
		if (horizontalPages < 1)
			horizontalPages = 1;
		this.horizontalPageCount = horizontalPages;
		invalidatePageDisplayBounds();
		redraw();
	}

	/**
	 * Returns how many pages will be displayed in the vertical direction.
	 * <p>
	 * <b>THIS API IS EXPERIMENTAL AND MAY BE REMOVED OR CHANGED IN THE
	 * FUTURE.</b>
	 *
	 * @return how many pages will be displayed in the vertical direction.
	 */
	public int getVerticalPageCount() {
		checkWidget();
		return verticalPageCount;
	}

	/**
	 * Sets how many pages will be displayed in the vertical direction.
	 * <p>
	 * <b>THIS API IS EXPERIMENTAL AND MAY BE REMOVED OR CHANGED IN THE
	 * FUTURE.</b>
	 *
	 * @param verticalPages
	 *            how many pages will be displayed in the vertical direction.
	 */
	public void setVerticalPageCount(int verticalPages) {
		checkWidget();
		if (verticalPages < 1)
			verticalPages = 1;
		this.verticalPageCount = verticalPages;
		invalidatePageDisplayBounds();
		redraw();
	}

	/**
	 * Returns whether the preview lays out pages lazily. Note that total page
	 * counts in page numbers will not display correctly when this is enabled.
	 *
	 * @return whether the preview lays out pages lazily.
	 */
	public boolean isLazyPageLayout() {
		checkWidget();
		return lazy;
	}

	/**
	 * Sets whether the preview lays out pages lazily. Note that total page
	 * counts in page numbers will not display correctly when this is enabled.
	 *
	 * @param lazy
	 *            whether the preview lays out pages lazily.
	 */
	public void setLazyPageLayout(boolean lazy) {
		checkWidget();
		this.lazy = lazy;
	}

	/**
	 * Begins lazy loading in the background, invoking the callback runnable
	 * periodically as pages are laid out.
	 * <p>
	 * <b>NOTE:</b> This API is experimental and subject to change.
	 *
	 * @param callback
	 *            runnable that will be invoked periodically as pages are laid
	 *            out.
	 */
	// TODO finalize experimental API
	public void startBackgroundLayout(final Runnable callback) {
		if (isPageLayoutComplete())
			return;

		final int DELAY = 10;
		getDisplay().timerExec(DELAY, new Runnable() {
			public void run() {
				if (isDisposed())
					return;
				if (!isPageLayoutComplete() && pages != null) {
					fetchPages(pages.size() + 1);
					if (!isPageLayoutComplete()) {
						getDisplay().timerExec(DELAY, this);
					}
				}
				callback.run();
			}
		});
	}

	private void invalidatePageDisplayBounds() {
		pageDisplaySize = null;
		pageDisplayLocations = null;
	}

	private void paint(Event event) {
		drawBackground(event);

		if (printJob == null || printerData == null)
			return;

		getPrinter();
		getPaperSize();
		fetchPages(pageIndex + verticalPageCount * horizontalPageCount);

		getPageDisplaySize();
		getPageDisplayLocations();

		if (printer == null || paperSize == null || pages == null
				|| pageDisplaySize == null || pageDisplayLocations == null
				|| pageIndex < 0 || pageIndex >= pages.size())
			return;

		int count = Math.min(verticalPageCount * horizontalPageCount,
				pages.size() - pageIndex);
		for (int i = 0; i < count; i++) {
			paintPage(event, pages.get(pageIndex + i), pageDisplayLocations[i]);
		}
	}

	private void paintPage(Event event, PrintPiece page, Point location) {
		// Check whether any "paper" is in the dirty region
		Rectangle rectangle = new Rectangle(location.x, location.y,
				pageDisplaySize.x, pageDisplaySize.y);
		Rectangle dirtyBounds = new Rectangle(event.x, event.y, event.width,
				event.height);
		Rectangle dirtyPaperBounds = dirtyBounds.intersection(rectangle);
		if (dirtyPaperBounds.width == 0 || dirtyPaperBounds.height == 0)
			return;

		Image printerImage = null;
		GC printerGC = null;
		Transform printerTransform = null;
		Image displayImage = null;

		try {
			printerImage = new Image(printer, dirtyPaperBounds.width,
					dirtyPaperBounds.height);
			printerGC = new GC(printerImage);
			configureAntialiasing(printerGC);
			printerTransform = new Transform(printer);

			printerGC.getTransform(printerTransform);
			printerTransform.translate(rectangle.x - dirtyPaperBounds.x,
					rectangle.y - dirtyPaperBounds.y);
			printerTransform.scale(
					(float) rectangle.width / (float) paperSize.x,
					(float) rectangle.height / (float) paperSize.y);
			printerGC.setTransform(printerTransform);
			page.paint(printerGC, 0, 0);

			displayImage = new Image(event.display,
					printerImage.getImageData());
			event.gc.drawImage(displayImage, dirtyPaperBounds.x,
					dirtyPaperBounds.y);
		} finally {
			disposeResources(printerImage, printerGC, printerTransform,
					displayImage, page);
		}
	}

	private void disposeResources(Image printerImage, GC printerGC,
			Transform printerTransform, Image displayImage, PrintPiece page) {
		if (printerImage != null)
			printerImage.dispose();
		if (displayImage != null)
			displayImage.dispose();
		if (printerGC != null)
			printerGC.dispose();
		if (printerTransform != null)
			printerTransform.dispose();
		page.dispose();
	}

	private void configureAntialiasing(GC printerGC) {
		printerGC.setAdvanced(true);
		printerGC.setAntialias(SWT.ON);
		printerGC.setTextAntialias(SWT.ON);
		printerGC.setInterpolation(SWT.HIGH);
	}

	private Printer getPrinter() {
		if (printer == null && printerData != null) {
			printer = new Printer(printerData);
			PaperClips.startDummyJob(printer, ""); //$NON-NLS-1$
			disposePages(); // just in case
			pageDisplaySize = null;
			pageDisplayLocations = null;
		}
		return printer;
	}

	private GC getGC() {
		if (gc == null && printer != null) {
			gc = new GC(printer);
			gc.setAdvanced(true);
		}
		return gc;
	}

	private boolean orientationRequiresRotate() {
		int orientation = printJob.getOrientation();
		Rectangle bounds = PaperClips.getPaperBounds(printer);
		return (orientation == PaperClips.ORIENTATION_PORTRAIT
				&& bounds.width > bounds.height)
				|| (orientation == PaperClips.ORIENTATION_LANDSCAPE
						&& bounds.height > bounds.width);
	}

	private Point getPaperSize() {
		Printer printer = getPrinter();
		if (paperSize == null && printer != null && printJob != null) {
			Rectangle paperBounds = PaperClips.getPaperBounds(printer);
			this.paperSize = orientationRequiresRotate()
					? new Point(paperBounds.height, paperBounds.width)
					: new Point(paperBounds.width, paperBounds.height);
		}
		return paperSize;
	}

	private void fetchPages(int endIndex) {
		if (getPrintJob() == null || getPrinter() == null)
			return;
		if (pageEnumeration == null) {
			if (getGC() == null)
				return;
			pageEnumeration = PaperClips.getPageEnumeration(printJob, printer,
					gc);
		}
		if (pages == null)
			pages = new ArrayList<>();
		boolean doRotate = orientationRequiresRotate();
		boolean allPages = endIndex == ALL_PAGES || !lazy;
		while (pageEnumeration.hasNext()
				&& (allPages || pages.size() < endIndex)) {
			PrintPiece page = pageEnumeration.nextPage();
			if (page != null) {
				if (doRotate)
					page = new RotateClockwisePrintPiece(printer, page);
				pages.add(page);
			}
		}
		if (!pageEnumeration.hasNext())
			disposeGC();
	}

	private void drawBackground(Event event) {
		Color oldBackground = event.gc.getBackground();
		Color bg = event.display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
		try {
			event.gc.setBackground(bg);
			event.gc.fillRectangle(event.x, event.y, event.width, event.height);
			event.gc.setBackground(oldBackground);
		} finally {
			bg.dispose();
		}
	}

	/**
	 * Calculates the absolute scale that the print preview is displaying at. If
	 * either of the fitHorizontal or fitVertical properties are true, this is
	 * the scale allows the page to fit within this control's current bounds.
	 * Otherwise the value of the scale property is returned.
	 *
	 * @return the absolute scale that the print preview is displaying at.
	 */
	public float getAbsoluteScale() {
		checkWidget();
		return getAbsoluteScale(getSize());
	}

	/**
	 * Returns a Rectangle whose x, y, width, and height fields respectively
	 * indicate the margin at the left, top, right, and bottom edges of the
	 * control.
	 * <p>
	 * <b>THIS API IS EXPERIMENTAL AND MAY BE REMOVED OR CHANGED IN THE
	 * FUTURE.</b>
	 *
	 * @return a Rectangle whose x, y, width, and height fields respectively
	 *         indicate the margin at the left, top, right, and bottom edges of
	 *         the control.
	 */
	public Rectangle getMargins() {
		checkWidget();
		return new Rectangle(margins.x, margins.y, margins.width,
				margins.height);
	}

	/**
	 * Sets the margins at each edge of the control to the argument.
	 * <p>
	 * <b>THIS API IS EXPERIMENTAL AND MAY BE REMOVED OR CHANGED IN THE
	 * FUTURE.</b>
	 *
	 * @param margins
	 *            a Rectangle whose x, y, width, and height fields respectively
	 *            indicate the margin at the left, top, right, and bottom edges
	 *            of the control.
	 */
	public void setMargins(Rectangle margins) {
		checkWidget();
		if (margins == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		this.margins = new Rectangle(margins.x, margins.y, margins.width,
				margins.height);
		invalidatePageDisplayBounds();
		redraw();
	}

	/**
	 * Returns a Point whose x and y fields respectively indicate the horizontal
	 * and vertical spacing between pages on the control.
	 * <p>
	 * <b>THIS API IS EXPERIMENTAL AND MAY BE REMOVED OR CHANGED IN THE
	 * FUTURE.</b>
	 *
	 * @return a Point whose x and y fields respectively indicate the horizontal
	 *         and vertical spacing between pages on the control.
	 */
	public Point getPageSpacing() {
		return new Point(pageSpacing.x, pageSpacing.y);
	}

	/**
	 * Sets the horizontal and vertical spacing between pages to the argument.
	 * <p>
	 * <b>THIS API IS EXPERIMENTAL AND MAY BE REMOVED OR CHANGED IN THE
	 * FUTURE.</b>
	 *
	 * @param pageSpacing
	 *            a Point whose x and y fields respectively indicate the
	 *            horizontal and vertical spacing between pages on the control.
	 */
	public void setPageSpacing(Point pageSpacing) {
		checkWidget();
		if (pageSpacing == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		this.pageSpacing = new Point(pageSpacing.x, pageSpacing.y);
		invalidatePageDisplayBounds();
		redraw();
	}

	private Point getBoilerplateSize() {
		return new Point(
				margins.x + margins.width
						+ (horizontalPageCount - 1) * pageSpacing.x,
				margins.y + margins.height
						+ (verticalPageCount - 1) * pageSpacing.y);
	}

	private float getAbsoluteScale(Point controlSize) {
		float result = scale;

		if (getPrinter() != null && (fitHorizontal || fitVertical)) {
			Rectangle trim = computeTrim(0, 0, 0, 0);
			controlSize.x -= trim.width;
			controlSize.y -= trim.height;

			Point boilerplate = getBoilerplateSize();
			controlSize.x -= boilerplate.x;
			controlSize.x /= horizontalPageCount;
			controlSize.y -= boilerplate.y;
			controlSize.y /= verticalPageCount;

			Point displayDPI = getDisplay().getDPI();
			Point printerDPI = getPrinter().getDPI();
			Point paperSize = getPaperSize();

			if (fitHorizontal) {
				float screenWidth = (float) controlSize.x
						/ (float) displayDPI.x; // inches
				float paperWidth = (float) paperSize.x / (float) printerDPI.x; // inches
				float scaleX = screenWidth / paperWidth;
				if (fitVertical) {
					float screenHeight = (float) controlSize.y
							/ (float) displayDPI.y; // inches
					float paperHeight = (float) paperSize.y
							/ (float) printerDPI.y; // inches
					float scaleY = screenHeight / paperHeight;
					result = Math.min(scaleX, scaleY);
				} else {
					result = scaleX;
				}
			} else {
				float screenHeight = (float) controlSize.y
						/ (float) displayDPI.y; // inches
				float paperHeight = (float) paperSize.y / (float) printerDPI.y; // inches
				float scaleY = screenHeight / paperHeight;
				result = scaleY;
			}
		}
		return result;
	}

	private Point getPageDisplaySize() {
		if (pageDisplaySize == null) {
			Point size = getSize();
			Point displayDPI = getDisplay().getDPI();
			Point printerDPI = printer.getDPI();
			float absoluteScale = getAbsoluteScale(size);
			float scaleX = absoluteScale * displayDPI.x / printerDPI.x;
			float scaleY = absoluteScale * displayDPI.y / printerDPI.y;

			pageDisplaySize = new Point((int) (scaleX * paperSize.x),
					(int) (scaleY * paperSize.y));
		}
		return pageDisplaySize;
	}

	private Point[] getPageDisplayLocations() {
		if (pageDisplayLocations == null) {
			// Center pages horizontally
			Rectangle clientArea = getClientArea();
			int x0 = clientArea.x + margins.x;
			clientArea.width -= getBoilerplateSize().x;
			clientArea.width -= (pageDisplaySize.x * horizontalPageCount);
			if (clientArea.width > 0)
				x0 += clientArea.width / 2;

			pageDisplayLocations = new Point[horizontalPageCount
					* verticalPageCount];

			int y = clientArea.y + margins.y;
			for (int r = 0; r < verticalPageCount; r++) {
				int x = x0;
				for (int c = 0; c < horizontalPageCount; c++) {
					pageDisplayLocations[r * horizontalPageCount
							+ c] = new Point(x, y);
					x += pageDisplaySize.x + pageSpacing.x;
				}
				y += pageDisplaySize.y + pageSpacing.y;
			}
		}
		return pageDisplayLocations;
	}

	private void disposePages() {
		if (pages != null) {
			pageEnumeration = null;
			for (int i = 0; i < pages.size(); i++)
				pages.get(i).dispose();
			pages = null;
			paperSize = null;
			invalidatePageDisplayBounds();
		}
	}

	private void disposePrinter() {
		disposePages();
		if (printer != null) {
			disposeGC();
			PaperClips.endDummyJob(printer);
			printer.dispose();
			printer = null;
		}
	}

	private void disposeGC() {
		if (gc != null) {
			gc.dispose();
			gc = null;
		}
	}

	private void disposeResources() {
		disposePages();
		disposePrinter();
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		checkWidget();

		Point size = new Point(wHint, hHint);

		fetchPages(horizontalPageCount * verticalPageCount);
		if (getPrinter() == null || pages == null) {
			Point boilerplate = getBoilerplateSize();
			if (wHint == SWT.DEFAULT)
				size.x = boilerplate.x;
			if (hHint == SWT.DEFAULT)
				size.y = boilerplate.y;
			return addTrim(size);
		}

		double scale;
		if (wHint != SWT.DEFAULT) {
			if (hHint != SWT.DEFAULT) {
				return addTrim(size);
			}
			size.y = Integer.MAX_VALUE;
			scale = getAbsoluteScale(size);
		} else if (hHint != SWT.DEFAULT) {
			size.x = Integer.MAX_VALUE;
			scale = getAbsoluteScale(size);
		} else {
			scale = this.scale;
		}

		return computeSize(scale);
	}

	/**
	 * Returns the control size needed to display a full page at the given
	 * scale.
	 *
	 * @param scale
	 *            the absolute scale. A scale of 1, for example, yields a "life
	 *            size" preview.
	 * @return the control size needed to display a full page at the given
	 *         scale.
	 */
	public Point computeSize(double scale) {
		checkWidget();

		Point size = getBoilerplateSize();

		fetchPages(horizontalPageCount * verticalPageCount);
		if (getPrinter() != null && pages != null) {
			Point displayDPI = getDisplay().getDPI();
			Point printerDPI = getPrinter().getDPI();
			Point paperSize = getPaperSize();

			size.x += horizontalPageCount
					* (int) (scale * paperSize.x * displayDPI.x / printerDPI.x);
			size.y += verticalPageCount
					* (int) (scale * paperSize.y * displayDPI.y / printerDPI.y);
		}

		return addTrim(size);
	}

	private Point addTrim(Point size) {
		Rectangle trim = computeTrim(0, 0, 0, 0);
		return new Point(size.x + trim.width, size.y + trim.height);
	}
}
