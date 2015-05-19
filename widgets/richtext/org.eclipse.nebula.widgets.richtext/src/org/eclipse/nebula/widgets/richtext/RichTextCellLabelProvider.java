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

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 *
 * @see <a
 *      href="https://www.eclipse.org/articles/article.php?file=Article-CustomDrawingTableAndTreeItems/index.html">Custom
 *      Drawing Table and Tree Items</a>
 */
public abstract class RichTextCellLabelProvider<T> extends StyledCellLabelProvider {

	private int columnIndex = -1;

	private RichTextPainter painter;

	int preferredWidth = -1;

	public RichTextCellLabelProvider(final Control viewerControl) {
		this(viewerControl, 5, false);
	}

	public RichTextCellLabelProvider(final Control viewerControl, boolean wordWrap) {
		this(viewerControl, 5, wordWrap);
	}

	public RichTextCellLabelProvider(final Control viewerControl, final int leftRightMargin, boolean wordWrap) {
		super(COLORS_ON_SELECTION);

		this.painter = new RichTextPainter(wordWrap);

		if (viewerControl instanceof Tree) {
			viewerControl.addListener(SWT.MeasureItem, new Listener() {

				@Override
				public void handleEvent(Event event) {
					final Item item = (Item) event.item;

					@SuppressWarnings("unchecked")
					String html = getRichText((T) item.getData());
					if (event.index == columnIndex
							&& html != null
							&& !html.isEmpty()) {

						Rectangle bounds = null;
						if (item instanceof TableItem) {
							bounds = ((TableItem) item).getBounds(event.index);
						}
						else if (item instanceof TreeItem) {
							bounds = ((TreeItem) item).getBounds();
							bounds.x -= 3;
						}

						bounds.width -= (leftRightMargin * 2);

						if (bounds != null) {
							int topMargin = ((bounds.height - event.gc.getFontMetrics().getHeight()) / 2) - painter.getParagraphSpace();

							bounds.x += leftRightMargin;
							bounds.y += topMargin;
							painter.preCalculate(html, event.gc, bounds, false);
							event.width = painter.getPreferredSize().x + leftRightMargin;
						}
					}
				}
			});
		}

		viewerControl.addListener(SWT.PaintItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				final Item item = (Item) event.item;

				@SuppressWarnings("unchecked")
				String html = getRichText((T) item.getData());
				if (event.index == columnIndex
						&& html != null
						&& !html.isEmpty()) {

					Rectangle bounds = null;
					if (item instanceof TableItem) {
						bounds = ((TableItem) item).getBounds(event.index);
					}
					else if (item instanceof TreeItem) {
						bounds = ((TreeItem) item).getBounds();
						bounds.x -= 3;
					}

					bounds.width -= (leftRightMargin * 2);

					if (bounds != null) {
						int topMargin = ((bounds.height - event.gc.getFontMetrics().getHeight()) / 2) - painter.getParagraphSpace();

						bounds.x += leftRightMargin;
						bounds.y += topMargin;
						painter.paintHTML(html, event.gc, bounds);
					}
				}
			}
		});
	}

	@Override
	public void update(ViewerCell cell) {
		columnIndex = cell.getColumnIndex();
		super.update(cell);
	}

	public abstract String getRichText(T object);

	/**
	 * Null-safe method to return the text that should be shown.
	 * 
	 * @param object
	 *            The value object to get the text to show from.
	 * @return The text to render or an empty string if the given object is <code>null</code>
	 */
	protected String getText(T object) {
		if (object != null) {
			return getRichText(object);
		}
		return "";
	}
}
