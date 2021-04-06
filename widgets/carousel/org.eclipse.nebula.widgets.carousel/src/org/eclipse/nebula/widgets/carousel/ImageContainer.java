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

import org.eclipse.nebula.widgets.opal.commons.SelectionListenerUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;

class ImageContainer extends Canvas {

	private Image image, scrollImage;
	private Carousel carousel;
	private int slider;

	public ImageContainer(final Carousel parent, final int style) {
		super(parent, SWT.DOUBLE_BUFFERED);
		carousel = parent;
		slider = -1;
		addListener(SWT.Paint, e -> {
			final GC gc = e.gc;
			gc.setAntialias(SWT.ON);
			gc.setInterpolation(SWT.HIGH);

			if (image == null) {
				return;
			}

			final Rectangle clientArea = getClientArea();
			if (slider == -1) {
				final Rectangle imageBounds = image.getBounds();
				if (imageBounds.width > clientArea.width || imageBounds.height > clientArea.height) {
					// Image is too big
					final Point point = reduceImageSoItFits(image);
					final int newWidth = point.x;
					final int newHeight = point.y;

					gc.drawImage(image, 0, 0, imageBounds.width, imageBounds.height, (clientArea.width - newWidth) / 2, (clientArea.height - newHeight) / 2, newWidth, newHeight);
				} else {
					gc.drawImage(image, (clientArea.width - imageBounds.width) / 2, (clientArea.height - imageBounds.height) / 2);
				}
				return;
			}

			// Animation
			if (!scrollImage.isDisposed()) {
				gc.drawImage(scrollImage, slider, 0, clientArea.width, clientArea.height, 0, 0, clientArea.width, clientArea.height);
			}
		});
	}

	private Point reduceImageSoItFits(final Image img) {
		final Rectangle clientArea = getClientArea();
		final Rectangle imageBounds = img.getBounds();
		final float ratio = imageBounds.width * 1f / imageBounds.height * 1f;

		int newWidth = imageBounds.width;
		int newHeight = imageBounds.height;
		while (newWidth > clientArea.width - 5 || newHeight > clientArea.height - 5) {
			newWidth = (int) (newWidth * .9f);
			newHeight = (int) (newWidth / ratio);
		}
		return new Point(newWidth, newHeight);
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
		final int direction = newSelection > carousel.getSelection() ? SWT.LEFT : SWT.RIGHT;
		slide(newSelection, direction);
		carousel.selection = newSelection;
		SelectionListenerUtil.fireSelectionListeners(carousel,null);
		carousel.imageSelector.redraw();
	}

	void moveNext() {
		int newSelection = carousel.selection + 1;
		if (newSelection == carousel.getImages().size()) {
			newSelection = 0;
		}
		slide(newSelection, SWT.LEFT);
		carousel.selection = newSelection;
		SelectionListenerUtil.fireSelectionListeners(carousel,null);
		carousel.imageSelector.redraw();
	}

	private void slide(final int target, final int direction) {
		final int width = getClientArea().width;
		slider = direction == SWT.LEFT ? 0 : width;
		if (direction == SWT.LEFT) {
			createScrolledImage(image, carousel.getImages().get(target));
		} else {
			createScrolledImage(carousel.getImages().get(target), image);
		}

		getDisplay().timerExec(200, new Runnable() {

			@Override
			public void run() {
				ImageContainer.this.redraw();
				final int step = width / 5;
				if (direction == SWT.LEFT) {
					slider += step;
					if (slider >= width) {
						slider = -1;
						scrollImage.dispose();
						return;
					}
				} else {
					slider -= step;
					if (slider <= 0) {
						slider = -1;
						scrollImage.dispose();
						return;
					}
				}
				getDisplay().timerExec(50, this);
			}
		});

		image = carousel.getImages().get(target);
		redraw();
	}

	private void createScrolledImage(final Image left, final Image right) {
		final Rectangle clientArea = getClientArea();

		scrollImage = new Image(getDisplay(), clientArea.width * 2, clientArea.height);
		final GC gc = new GC(scrollImage);
		gc.setInterpolation(SWT.HIGH);

		gc.setBackground(carousel.getBackground());
		gc.fillRectangle(0, 0, clientArea.width * 2, clientArea.height);

		final Rectangle leftImageBounds = left.getBounds();
		if (leftImageBounds.width > clientArea.width || leftImageBounds.height > clientArea.height) {
			// Image is too big
			final Point point = reduceImageSoItFits(left);
			final int newWidth = point.x;
			final int newHeight = point.y;

			gc.drawImage(left, 0, 0, leftImageBounds.width, leftImageBounds.height, //
					(clientArea.width - newWidth) / 2, (clientArea.height - newHeight) / 2, newWidth, newHeight);
		} else {
			gc.drawImage(left, (clientArea.width - leftImageBounds.width) / 2, (clientArea.height - leftImageBounds.height) / 2);
		}

		final Rectangle rightImageBounds = right.getBounds();
		if (rightImageBounds.width > clientArea.width || rightImageBounds.height > clientArea.height) {
			// Image is too big
			final Point point = reduceImageSoItFits(right);
			final int newWidth = point.x;
			final int newHeight = point.y;

			gc.drawImage(right, 0, 0, rightImageBounds.width, rightImageBounds.height, //
					clientArea.width + (clientArea.width - newWidth) / 2, (clientArea.height - newHeight) / 2, newWidth, newHeight);
		} else {
			gc.drawImage(right, clientArea.width + (clientArea.width - rightImageBounds.width) / 2, (clientArea.height - rightImageBounds.height) / 2);
		}

		gc.dispose();
	}


	void movePrevious() {
		final Carousel carousel = (Carousel) getParent();
		int newSelection = carousel.selection - 1;
		if (newSelection == -1) {
			newSelection = carousel.getImages().size() - 1;
		}
		slide(newSelection, SWT.RIGHT);
		carousel.selection = newSelection;
		SelectionListenerUtil.fireSelectionListeners(carousel,null);
		carousel.imageSelector.redraw();
	}

}
