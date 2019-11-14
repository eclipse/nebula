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

import org.eclipse.nebula.paperclips.core.PrintPiece;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * A canvas for displaying Print objects.
 *
 * @author Matthew
 */
public class PrintPieceCanvas extends Canvas {
	PrintPiece piece = null;

	/**
	 * Constructs a PrintCanvas with the given parent and style.
	 *
	 * @param parent
	 *            the parent Composite.
	 * @param style
	 *            the style parameter.
	 */
	public PrintPieceCanvas(Composite parent, int style) {
		super(parent, style);

		setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		setForeground(getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));

		addListener(SWT.Paint, event -> {
			if (piece == null)
				return;

			Rectangle client = getClientArea();
			piece.paint(event.gc, client.x, client.y);
		});
		addListener(SWT.Dispose, event -> {
			disposePrintPiece();
		});
	}

	/**
	 * Displays the given Print in this PrintCanvas.
	 *
	 * @param piece
	 *            the PrintPiece to display.
	 */
	public void setPrintPiece(PrintPiece piece) {
		disposePrintPiece();
		this.piece = piece;
		redraw();
	}

	/**
	 * Returns the PrintPiece being displayed by this PrintCanvas.
	 *
	 * @return the PrintPiece being displayed by this PrintCanvas.
	 */
	public PrintPiece getPrintPiece() {
		return piece;
	}

	private void disposePrintPiece() {
		if (piece != null)
			piece.dispose();
	}
}
