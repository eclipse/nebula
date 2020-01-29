/*
 * Copyright (c) 2005 Matthew Hall and others.
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

import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.nebula.paperclips.core.PrintIterator;
import org.eclipse.nebula.paperclips.core.PrintPiece;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * A JFace-style {@link Print} viewer which displays a Print in a scrollable
 * pane.
 *
 * @author Matthew
 */
public class PrintViewer {
	private final ScrolledComposite sc;
	private final PrintPieceCanvas canvas;
	private Print print;

	private int canvasWidth;

	private BackgroundUpdater backgroundUpdater;

	/**
	 * Constructs a PrintPreview with the given parent and style.
	 *
	 * @param parent
	 *            the parent component of the scroll pane.
	 * @param style
	 *            the style of the scroll pane.
	 */
	public PrintViewer(Composite parent, int style) {
		sc = new ScrolledComposite(parent, style | SWT.V_SCROLL | SWT.H_SCROLL);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.addListener(SWT.Resize, event -> {
			if (sc.getClientArea().width != canvasWidth)
				updateCanvas();
		});
		canvas = new PrintPieceCanvas(sc, SWT.DOUBLE_BUFFERED);
		sc.setContent(canvas);
	}

	/**
	 * Returns the viewer component wrapped by this PrintPreview.
	 *
	 * @return the viewer component wrapped by this PrintPreview.
	 */
	public Control getControl() {
		return sc;
	}

	/**
	 * Sets the Print to be displayed.
	 *
	 * @param print
	 *            the Print to display.
	 */
	public void setPrint(Print print) {
		this.print = print;
		updateCanvas();
	}

	/**
	 * Returns the Print being displayed.
	 *
	 * @return the Print being displayed.
	 */
	public Print getPrint() {
		return print;
	}

	void updateCanvas() {
		if (print == null) {
			sc.setMinSize(0, 0);
			canvas.setPrintPiece(null);
			return;
		}

		GC gc = null;
		try {
			gc = new GC(canvas);

			PrintIterator iterator = print.iterator(canvas.getDisplay(), gc);
			sc.setMinWidth(iterator.minimumSize().x);

			int canvasWidth = Math.max(iterator.minimumSize().x,
					sc.getClientArea().width);
			if (this.canvasWidth == canvasWidth)
				return;
			this.canvasWidth = canvasWidth;

			if (backgroundUpdater != null) {
				backgroundUpdater.cancelled = true;
				backgroundUpdater = null;
			}

			PrintPiece piece = PaperClips.next(iterator, canvasWidth,
					Integer.MAX_VALUE);

			boolean printIsVerticallyGreedy = piece != null
					&& piece.getSize().y == Integer.MAX_VALUE;
			if (printIsVerticallyGreedy)
				sc.getDisplay().timerExec(50,
						backgroundUpdater = new BackgroundUpdater());
			setPrintPiece(piece, !printIsVerticallyGreedy);
		} finally {
			if (gc != null)
				gc.dispose();
		}
	}

	private void setPrintPiece(PrintPiece piece, boolean updateMinHeight) {
		if (updateMinHeight)
			sc.setMinHeight(piece == null ? 0 : piece.getSize().y);
		canvas.setPrintPiece(piece);
	}

	private class BackgroundUpdater implements Runnable {
		private boolean cancelled = false;
		private int minHeight;
		private int maxHeight;
		private PrintIterator iterator;
		private PrintPiece piece;

		public void run() {
			if (cancelled || print == null)
				return;

			GC gc = null;
			try {
				gc = new GC(canvas);

				iterator = print.iterator(canvas.getDisplay(), gc);
				piece = canvas.getPrintPiece();

				determineValidHeightRange();
				binarySearchRangeForSmallestValidHeight();

				setPrintPiece(piece, true);
			} finally {
				if (gc != null)
					gc.dispose();
			}
		}

		private void determineValidHeightRange() {
			minHeight = iterator.preferredSize().y;
			maxHeight = Math.max(minHeight, 4096);
			while (true) {
				PrintIterator testIter = iterator.copy();
				PrintPiece testPiece = PaperClips.next(testIter, canvasWidth,
						maxHeight);
				final int factor = 4;
				if (testPiece == null) {
					// Theoretically this will never happen since we started at
					// preferred height
					minHeight = maxHeight + 1;
					maxHeight = minHeight * factor;
				} else if (testIter.hasNext()) {
					testPiece.dispose();
					minHeight = maxHeight + 1;
					maxHeight = minHeight * factor;
				} else {
					piece.dispose();
					piece = testPiece;
					break;
				}
			}
		}

		private void binarySearchRangeForSmallestValidHeight() {
			while (minHeight < maxHeight) {
				int testHeight = minHeight + (maxHeight - minHeight) / 2;
				PrintIterator testIter = iterator.copy();
				PrintPiece testPiece = PaperClips.next(testIter, canvasWidth,
						testHeight);

				if (testPiece == null) {
					minHeight = testHeight + 1;
				} else if (testIter.hasNext()) {
					testPiece.dispose();
					minHeight = testHeight + 1;
				} else {
					maxHeight = Math.min(testHeight, testPiece.getSize().y);
					piece.dispose();
					piece = testPiece;
				}
			}
		}
	}
}
