/*****************************************************************************
 *  Copyright (c) 2015, 2019 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *		Dirk Fauth <dirk.fauth@googlemail.com> - Initial API and implementation
 *      Laurent Caron <laurent.caron@gmail.com> - Bug 511353
 *
 *****************************************************************************/
package org.eclipse.nebula.widgets.richtext;

import java.util.Iterator;

import org.eclipse.nebula.widgets.richtext.painter.AnchorElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

// TODO add scrolling support

/**
 * Simple rich text viewer control that uses the {@link RichTextPainter} to render text with HTML
 * markups on a {@link Canvas}.
 */
public class RichTextViewer extends Canvas {

	private static final String HOVER = RichTextViewer.class.getName() + "_HOVER";

	private String htmlText;
	private RichTextPainter painter;
	private boolean clickInProgress;
	private Cursor currentCursor;

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

		addListener(SWT.Paint, e -> {
			painter.paintHTML(htmlText != null ? htmlText : "", e.gc, getClientArea());
		});

		addListener(SWT.MouseEnter, e -> {
			clickInProgress = false;
			boolean hover = getData(HOVER) != null;
			if (hover) {
				String url = extractURL(e.x, e.y);
				if (url == null) {
					setData(HOVER, null);
					setCursor(currentCursor==null?getDisplay().getSystemCursor(SWT.CURSOR_ARROW):currentCursor);
				} else {
					setData(HOVER, HOVER);
					setCursor(getDisplay().getSystemCursor(SWT.CURSOR_HAND));
				}
			} else {
				setCursor(currentCursor==null?getDisplay().getSystemCursor(SWT.CURSOR_ARROW):currentCursor);
			}
			currentCursor = getCursor();
		});

		addListener(SWT.MouseExit, e -> {
			setData(HOVER, null);
			clickInProgress = false;
			currentCursor = null;
		});

		addListener(SWT.MouseMove, e -> {
			String url = extractURL(e.x, e.y);
			boolean hover = getData(HOVER) != null;
			if (url == null) {
				if (hover) {
					setCursor(currentCursor);
				}
			} else {
				setData(HOVER, HOVER);
				setCursor(getDisplay().getSystemCursor(SWT.CURSOR_HAND));
			}
		});

		addListener(SWT.MouseDown, e -> clickInProgress = true);
		addListener(SWT.MouseUp, e -> {
			if (!clickInProgress) {
				return;
			}
			String url = extractURL(e.x, e.y);
			if (url != null) {
				Program.launch(url);
			}
			clickInProgress = false;
		});

	}

	private String extractURL(int x, int y) {
		Iterator<AnchorElement> it = painter.getAnchorElements().iterator();
		while (it.hasNext()) {
			AnchorElement element = it.next();
			if (element.area.contains(x, y)) {
				return element.url;
			}
		}
		return null;
	}

	public void setText(String text) {
		this.htmlText = text;
		redraw();
		update();
	}

	/**
	 * @param wordSplitRegex
	 *            The regular expression that will be used to determine word boundaries.
	 * @since 1.3.0
	 */
	public void setWordSplitRegex(String wordSplitRegex) {
		painter.setWordSplitRegex(wordSplitRegex);
		if (htmlText != null) {
			redraw();
			update();
		}
	}
}
