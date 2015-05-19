/*****************************************************************************
 * Copyright (c) 2015 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *		Dirk Fauth <dirk.fauth@googlemail.com> - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.nebula.widgets.richtext;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

// TODO add scrolling support

/**
 * Simple rich text viewer control that uses the {@link RichTextPainter} to render text with HTML
 * markups on a {@link Canvas}.
 */
public class RichTextViewer extends Canvas {

	private String htmlText;

	private RichTextPainter painter;

	/**
	 * 
	 * @param parent
	 * @param style
	 *            The style bits to use.
	 * 
	 * @see SWT#WRAP
	 */
	public RichTextViewer(Composite parent, int style) {
		super(parent, style | SWT.DOUBLE_BUFFERED);

		final boolean wordWrap = (getStyle() & SWT.WRAP) != 0;
		this.painter = new RichTextPainter(wordWrap);

		addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				painter.paintHTML(htmlText != null ? htmlText : "", e.gc, getClientArea());
			}
		});

	}

	public void setText(String text) {
		this.htmlText = text;
		redraw();
		update();
	}
}
