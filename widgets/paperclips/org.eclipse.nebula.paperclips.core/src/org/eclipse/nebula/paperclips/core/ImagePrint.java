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
package org.eclipse.nebula.paperclips.core;

import org.eclipse.nebula.paperclips.core.internal.util.SWTUtil;
import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

/**
 * A Print for displaying images.
 * 
 * @author Matthew Hall
 */
public class ImagePrint implements Print {
	ImageData imageData;
	Point dpi;
	Point size;

	/**
	 * Constructs an ImagePrint with the given imageData, initialized at 72dpi.
	 * 
	 * @param imageData
	 *            the image to be displayed.
	 */
	public ImagePrint(ImageData imageData) {
		this(imageData, new Point(72, 72));
	}

	/**
	 * Constructs an ImagePrint with the given imageData and dpi.
	 * 
	 * @param imageData
	 *            the image to be displayed.
	 * @param dpi
	 *            the DPI that the image will be displayed at.
	 */
	public ImagePrint(ImageData imageData, Point dpi) {
		Util.notNull(imageData, dpi);
		this.imageData = imageData;
		setDPI(dpi);
	}

	/**
	 * Returns the ImageData of the image being printed.
	 * 
	 * @return the ImageData of the image being printed.
	 */
	public ImageData getImageData() {
		return imageData;
	}

	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		ImagePrint that = (ImagePrint) obj;
		return Util.equal(this.dpi, that.dpi)
				&& Util.equal(this.size, that.size)
				&& SWTUtil.equal(this.imageData, that.imageData);
	}

	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result + dpi.hashCode();
		result = prime * result + size.hashCode();
		result = prime * result + SWTUtil.hashCode(imageData);
		return result;
	}

	/**
	 * Sets the ImagePrint to render the image at the given size, in points. 72
	 * points = 1".
	 * 
	 * @param size
	 *            the explicit size, in points, that the image be printed at.
	 */
	public void setSize(Point size) {
		// The DPI is rounded up, so that the specified width and height will
		// not be exceeded.
		Util.notNull(size);
		dpi = new Point((int) Math.ceil(imageData.width * 72d / size.x),
				(int) Math.ceil(imageData.height * 72d / size.y));
		this.size = size;
	}

	/**
	 * Sets the ImagePrint to render the image at the given size, in points. 72
	 * points = 1".
	 * 
	 * @param width
	 *            the explicit width, in points, that the image will be printed
	 *            at.
	 * @param height
	 *            the explicit height, in points, that the image will be printed
	 *            at.
	 */
	public void setSize(int width, int height) {
		setSize(new Point(width, height));
	}

	/**
	 * Returns the size that the image will be rendered at, in points. 72 points
	 * = 1".
	 * 
	 * @return the size of the image, in points.
	 */
	public Point getSize() {
		return size;
	}

	/**
	 * Sets the ImagePrint to render the image at the DPI of the argument.
	 * 
	 * @param dpi
	 *            the DPI of the image.
	 */
	public void setDPI(Point dpi) {
		Util.notNull(dpi);
		this.dpi = dpi;
		size = new Point((int) Math.ceil(imageData.width * 72d / dpi.x),
				(int) Math.ceil(imageData.height * 72d / dpi.y));
	}

	/**
	 * Sets the ImagePrint to render the image at the given DPI.
	 * 
	 * @param dpiX
	 *            the horizontal DPI the image will be rendered at.
	 * @param dpiY
	 *            the vertical DPI the image will be rendered at.
	 */
	public void setDPI(int dpiX, int dpiY) {
		setDPI(new Point(dpiX, dpiY));
	}

	/**
	 * Returns the DPI that this image will be rendered at.
	 * 
	 * @return the DPI the image will be rendered at.
	 */
	public Point getDPI() {
		return dpi;
	}

	public PrintIterator iterator(Device device, GC gc) {
		return new ImageIterator(this, device);
	}
}

class ImageIterator implements PrintIterator {
	final Device device;

	final ImageData imageData;
	final Point size;

	boolean hasNext;

	ImageIterator(ImagePrint print, Device device) {
		Util.notNull(print, device);
		this.device = device;
		this.imageData = print.imageData;
		Point dpi = device.getDPI();
		this.size = new Point(print.size.x * dpi.x / 72, print.size.y * dpi.y
				/ 72);
		this.hasNext = true;
	}

	ImageIterator(ImageIterator that) {
		this.device = that.device;
		this.imageData = that.imageData;
		this.size = that.size;
		this.hasNext = that.hasNext;
	}

	public boolean hasNext() {
		return hasNext;
	}

	public PrintPiece next(int width, int height) {
		if (!hasNext())
			PaperClips.error("No more content."); //$NON-NLS-1$

		if (width < size.x || height < size.y)
			return null;

		hasNext = false;

		return new ImagePiece(device, imageData, size);
	}

	public Point minimumSize() {
		return new Point(size.x, size.y);
	}

	public Point preferredSize() {
		return new Point(size.x, size.y);
	}

	public PrintIterator copy() {
		return hasNext ? new ImageIterator(this) : this;
	}
}

class ImagePiece implements PrintPiece {
	private final Device device;
	private final ImageData imageData;
	private final Point size;

	private Image image;

	ImagePiece(Device device, ImageData imageData, Point size) {
		Util.notNull(device, imageData, size);
		this.device = device;
		this.imageData = imageData;
		this.size = size;
	}

	public Point getSize() {
		return new Point(size.x, size.y);
	}

	private Image getImage() {
		if (image == null)
			image = new Image(device, imageData);
		return image;
	}

	public void paint(GC gc, int x, int y) {
		gc.drawImage(getImage(), 0, 0, imageData.width, imageData.height, x, y,
				size.x, size.y);
	}

	public void dispose() {
		if (image != null) {
			image.dispose();
			image = null;
		}
	}
}