/*******************************************************************************
 * Copyright (c) 2020 Laurent CARON.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API and
 * implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.carousel;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Event;

class ImageContainer extends Canvas {

	private Image image;
	private Carousel carousel;

	public ImageContainer(final Carousel parent, final int style) {
		super(parent, SWT.CENTER);
		carousel = parent;
		addListener(SWT.Paint, e -> {
			final GC gc = e.gc;
			if (image == null) {
				return;
			}
			final Rectangle clientArea = getClientArea();
			final Rectangle imageBounds = image.getBounds();
			gc.drawImage(image, (clientArea.width - imageBounds.width) / 2, (clientArea.height - imageBounds.height) / 2);
		});
	}

	@Override
	public Point computeSize(final int wHint, final int hHint, final boolean changed) {
		final Point superSize = super.computeSize(wHint, hHint, changed);

		int width = 0, height = 0;
		final List<Image> images = ((Carousel) getParent()).getImages();
		for (final Image image : images) {
			final Rectangle rect = image.getBounds();
			width = Math.max(width, rect.width);
			height = Math.max(height, rect.height);
		}
		return new Point(Math.max(superSize.x, width), Math.max(superSize.y, height));
	}

	void setImage(final Image image) {
		this.image = image;
	}

	void moveTo(final int newSelection) {
		final int direction = newSelection > carousel.getSelection() ? SWT.LEFT_TO_RIGHT : SWT.RIGHT_TO_LEFT;
		slide(newSelection, direction);
		carousel.selection = newSelection;
		fireSelectionEvent();
		carousel.imageSelector.redraw();
	}

	void moveNext() {
		int newSelection = carousel.selection + 1;
		if (newSelection == carousel.getImages().size()) {
			newSelection = 0;
		}
		slide(newSelection, SWT.LEFT_TO_RIGHT);
		carousel.selection = newSelection;
		fireSelectionEvent();
		carousel.imageSelector.redraw();
	}

	private void slide(final int target, final int direction) {
		// TODO Auto-generated method stub
		image = carousel.getImages().get(target);
		redraw();
	}

	private void fireSelectionEvent() {
		final Event e = new Event();
		e.widget = carousel;
		e.display = getDisplay();
		e.index = carousel.selection;
		e.type = SWT.Selection;

		final SelectionEvent event = new SelectionEvent(e);
		for (final SelectionListener listener : carousel.selectionListeners) {
			listener.widgetSelected(event);
			if (!event.doit) {
				break;
			}
		}
	}

	void movePrevious() {
		final Carousel carousel = (Carousel) getParent();
		int newSelection = carousel.selection - 1;
		if (newSelection == 0) {
			newSelection = carousel.getImages().size() - 1;
		}
		slide(newSelection, SWT.RIGHT_TO_LEFT);
		carousel.selection = newSelection;
		fireSelectionEvent();
		carousel.imageSelector.redraw();
	}

}
