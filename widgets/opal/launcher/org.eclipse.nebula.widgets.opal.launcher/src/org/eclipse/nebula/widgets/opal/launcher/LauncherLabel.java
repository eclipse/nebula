/*******************************************************************************
 * Copyright (c) 2011-2021 Laurent CARON
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Laurent CARON (laurent.caron at gmail dot com) - Initial implementation and API
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.launcher;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * Instance of this class are a button with text, image and a nice animation
 * effect
 */
class LauncherLabel extends Canvas {

	private String text;
	private Image image;
	private Font font;

	static final int GAP = 12;
	private static int DRAW_FLAGS = SWT.DRAW_MNEMONIC | SWT.DRAW_TAB | SWT.DRAW_TRANSPARENT | SWT.DRAW_DELIMITER;
	private static final int DEFAULT_MARGIN = 5;
	private int leftMargin = DEFAULT_MARGIN;
	private int topMargin = DEFAULT_MARGIN;
	private int rightMargin = DEFAULT_MARGIN;
	private int bottomMargin = DEFAULT_MARGIN;
	private Point textSize;

	private static final int MAX_NUMBER_OF_STEPS = 10;
	private int animationStep = 0;

	/**
	 * Constructs a new instance of this class given its parent and a style
	 * value describing its behavior and appearance.
	 * <p>
	 * The style value is either one of the style constants defined in class
	 * <code>SWT</code> which is applicable to instances of this class, or must
	 * be built by <em>bitwise OR</em>'ing together (that is, using the
	 * <code>int</code> "|" operator) two or more of those <code>SWT</code>
	 * style constants. The class description lists the style constants that are
	 * applicable to the class. Style bits are also inherited from superclasses.
	 * </p>
	 *
	 * @param parent a composite control which will be the parent of the new
	 *            instance (cannot be null)
	 * @param style the style of control to construct
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the parent</li>
	 *                </ul>
	 *
	 */
	LauncherLabel(final Composite parent, final int style) {
		super(parent, style | SWT.BORDER | SWT.DOUBLE_BUFFERED);

		font = parent.getFont();

		addPaintListener(event -> {
			paintControl(event);
		});

	}

	/**
	 * Draw the content of the LLabel
	 *
	 * @param event paintevent
	 */
	private void paintControl(final PaintEvent event) {
		final Rectangle rect = getClientArea();
		if (rect.width == 0 || rect.height == 0) {
			return;
		}

		final Image bufferImage = new Image(getDisplay(), Math.max(1, rect.width), Math.max(1, rect.height));

		final GC gc = new GC(bufferImage);
		gc.setForeground(getForeground());
		gc.setBackground(getBackground());

		gc.fillRectangle(rect);

		final Point extent = getTotalSize(image.getBounds().width, image.getBounds().height);
		final int xImage = (rect.width - image.getBounds().width) / 2;
		final int yImage = (rect.height - extent.y) / 2;
		gc.drawImage(image, xImage, yImage);

		gc.setFont(font);
		final int xText = (rect.width - textSize.x) / 2;
		final int yText = yImage + image.getBounds().height + GAP - textSize.y / 2;
		gc.drawString(text, xText, yText);

		if (animationStep != 0) {
			final float zoom = 1f + animationStep * (Math.max(extent.x, extent.y) - Math.max(image.getBounds().width, image.getBounds().height)) / MAX_NUMBER_OF_STEPS / 100f;

			final int newSizeX = (int) (image.getBounds().width * zoom);
			final int newSizeY = (int) (image.getBounds().height * zoom);

			gc.setAntialias(SWT.ON);
			gc.setInterpolation(SWT.HIGH);

			gc.setAlpha(255 - 255 / MAX_NUMBER_OF_STEPS * animationStep);

			final Point extentZoomedImage = getTotalSize(newSizeX, newSizeY);
			final int xZoomedImage = (rect.width - newSizeX) / 2;
			final int yZoomedImage = (rect.height - extentZoomedImage.y) / 2;
			gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, xZoomedImage, yZoomedImage, (int) (image.getBounds().width * zoom), (int) (image.getBounds().height * zoom));

		}

		gc.dispose();

		event.gc.drawImage(bufferImage, 0, 0);

		bufferImage.dispose();

	}

	/**
	 * @see org.eclipse.swt.widgets.Composite#computeSize(int, int, boolean)
	 */
	@Override
	public Point computeSize(final int wHint, final int hHint, final boolean changed) {
		checkWidget();
		final Point e = getTotalSize(image.getBounds().width, image.getBounds().height);
		if (wHint == SWT.DEFAULT) {
			e.x += leftMargin + rightMargin;
		} else {
			e.x = wHint;
		}
		if (hHint == SWT.DEFAULT) {
			e.y += topMargin + bottomMargin;
		} else {
			e.y = hHint;
		}
		return e;
	}

	/**
	 * Compute the size of the content (image + text + gap)
	 *
	 * @param imgWidth image width
	 * @param imgHeight image height
	 * @return the size of the content
	 */
	private Point getTotalSize(final int imgWidth, final int imgHeight) {
		final Point size = new Point(0, 0);

		int textWidth = 0;
		int textHeight = 0;

		if (textSize == null) {
			final GC gc = new GC(this);
			gc.setFont(font);

			textSize = gc.textExtent(text, DRAW_FLAGS);
			gc.dispose();

		}
		textWidth = textSize.x;
		textHeight = textSize.y;

		size.x = Math.max(imgWidth, textWidth);
		size.y = imgHeight + GAP + textHeight;

		return size;
	}

	/**
	 * @return the text
	 */
	String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	void setText(final String text) {
		this.text = text;
	}

	/**
	 * @return the image
	 */
	Image getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	void setImage(final Image image) {
		this.image = image;
	}

	/**
	 * @return the font
	 */
	@Override
	public Font getFont() {
		return font;
	}

	/**
	 * @param font the font to set
	 */
	@Override
	public void setFont(final Font font) {
		this.font = font;
	}

	/**
	 * Increment the steps of the animation
	 *
	 * @return true if animation keeps running, false otherwise
	 */
	boolean incrementAnimation() {
		animationStep++;
		final boolean stopAnimation = animationStep > MAX_NUMBER_OF_STEPS;

		if (stopAnimation) {
			animationStep = 0;
		}
		if (!isDisposed()) {
			redraw();
		}
		return !stopAnimation;
	}

	/**
	 * @return the left margin
	 */
	public int getLeftMargin() {
		return leftMargin;
	}

	/**
	 * @param leftMargin the left margin to set
	 */
	public void setLeftMargin(final int leftMargin) {
		this.leftMargin = leftMargin;
	}

	/**
	 * @return the top margin
	 */
	public int getTopMargin() {
		return topMargin;
	}

	/**
	 * @param topMargin the top margin to set
	 */
	public void setTopMargin(final int topMargin) {
		this.topMargin = topMargin;
	}

	/**
	 * @return the right margin
	 */
	public int getRightMargin() {
		return rightMargin;
	}

	/**
	 * @param rightMargin the right margin to set
	 */
	public void setRightMargin(final int rightMargin) {
		this.rightMargin = rightMargin;
	}

	/**
	 * @return the bottom margin
	 */
	public int getBottomMargin() {
		return bottomMargin;
	}

	/**
	 * @param bottomMargin the bottom margin to set
	 */
	public void setBottomMargin(final int bottomMargin) {
		this.bottomMargin = bottomMargin;
	}

}
