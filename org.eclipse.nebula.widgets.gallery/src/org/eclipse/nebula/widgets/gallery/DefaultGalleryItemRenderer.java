/*******************************************************************************
 * Copyright (c) 2006-2007 Nicolas Richeton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors :
 *    Nicolas Richeton (nicolas.richeton@gmail.com) - initial API and implementation
 *******************************************************************************/ 
 package org.eclipse.nebula.widgets.gallery;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p> 
 *
 * @author Nicolas Richeton (nicolas.richeton@gmail.com)
 */
public class DefaultGalleryItemRenderer extends AbstractGalleryItemRenderer {

	Vector dropShadowsColors = new Vector();

	boolean dropShadows = false;

	int dropShadowsSize = 10;

	int dropShadowsAlphaStep = 20;

	Color selectionColor;

	Color foregroundColor;

	Color backgroundColor;

	public DefaultGalleryItemRenderer() {
		selectionColor = Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION);
		foregroundColor = Display.getDefault().getSystemColor(SWT.COLOR_LIST_FOREGROUND);
		backgroundColor = Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
	}

	@Override
	public void draw(GC gc, GalleryItem item, int x, int y, int width, int height) {

		Image itemImage = item.getImage();

		int fontHeight = gc.getFontMetrics().getHeight();

		int useableHeight = height;
		if (item.getText() != null) {
			useableHeight -= fontHeight + 2;
		}

		int imageWidth = 0;
		int imageHeight = 0;
		int xShift = 0;
		int yShift = 0;
		Point size = null;

		if (itemImage != null) {
			Rectangle itemImageBounds = itemImage.getBounds();
			imageWidth = itemImageBounds.width;
			imageHeight = itemImageBounds.height;

			size = getBestSize(imageWidth, imageHeight, width - 2 - this.dropShadowsSize, useableHeight - 2 - this.dropShadowsSize);

			xShift = (width - size.x) >> 1;
			yShift = (useableHeight - size.y) >> 1;

			if (dropShadows) {
				Color c = null;
				for (int i = this.dropShadowsSize - 1; i >= 0; i--) {
					c = (Color) dropShadowsColors.get(i);
					gc.setForeground(c);

					gc.drawLine(x + width + i - xShift - 1, y + dropShadowsSize + yShift, x + width + i - xShift - 1, y + useableHeight + i - yShift);
					gc.drawLine(x + xShift + dropShadowsSize, y + useableHeight + i - yShift - 1, x + width + i - xShift, y - 1 + useableHeight + i - yShift);
				}
			}
		}

		// Draw selection background (rounded rectangles)
		if (selected) {
			gc.setBackground(selectionColor);
			gc.setForeground(selectionColor);
			gc.fillRoundRectangle(x, y, width, useableHeight, 15, 15);

			if (item.getText() != null) {
				gc.fillRoundRectangle(x, y + height - fontHeight, width, fontHeight, 15, 15);
			}
		}

		if (itemImage != null) {
			gc.drawImage(itemImage, 0, 0, imageWidth, imageHeight, x + xShift, y + yShift, size.x, size.y);
		}

		if (item.getText() != null) {
			gc.setBackground(this.backgroundColor);
			String text = createLabel(item.getText(), gc, width - 10);

			int textWidth = getTextWidth(text, gc);
			int textxShift = (width - (textWidth > width ? width : textWidth)) >> 1;

			if (selected) {
				gc.setBackground(selectionColor);
			}
			gc.setForeground(this.foregroundColor);
			gc.drawText(text, x + textxShift, y + height - fontHeight, true);
		}
	}

	private int getTextWidth(String text, GC gc) {
		int w = 0;

		for (int i = 0; i < text.length(); i++) {
			w += gc.getCharWidth(text.charAt(i));
		}
		if (Gallery.DEBUG)
			System.out.println("Text width : " + w);
		return w;
	}

	private String createLabel(String text, GC gc, int width) {
		if (Gallery.DEBUG)
			System.out.println("createLabel " + text);

		int textWidth = getTextWidth(text, gc);
		if (textWidth <= width) {
			if (Gallery.DEBUG)
				System.out.println("createLabel done");
			return text;
		}

		float averageWidth = textWidth / text.length();

		if (Gallery.DEBUG)
			System.out.println("averageWidth " + averageWidth);
		int bestLength = averageWidth > 0 ? (int) (width / averageWidth) : 0;
		if (Gallery.DEBUG)
			System.out.println("bestLength " + bestLength);
		bestLength = (bestLength - 3 < 0) ? 0 : (bestLength - 3);
		if (Gallery.DEBUG)
			System.out.println("bestLength " + bestLength);

		if (bestLength > text.length())
			bestLength = text.length();

		String result = text.substring(0, bestLength) + "...";
		if (Gallery.DEBUG)
			System.out.println("result " + result);

		if (Gallery.DEBUG)
			System.out.println("createLabel done");
		return result;
	}

	public void setDropShadowsSize(int dropShadowsSize) {
		this.dropShadowsSize = dropShadowsSize;
		this.dropShadowsAlphaStep = (dropShadowsSize == 0) ? 0 : (200 / dropShadowsSize);

		freeDropShadowsColors();
		createColors();
		// TODO: force redraw

	}

	private void createColors() {
		int step = 125 / dropShadowsSize;
		// Create new colors
		for (int i = dropShadowsSize - 1; i >= 0; i--) {
			int value = 255 - i * step;
			Color c = new Color(Display.getDefault(), value, value, value);
			dropShadowsColors.add(c);
		}
	}

	private void freeDropShadowsColors() {
		// Free colors :
		{
			Iterator i = this.dropShadowsColors.iterator();
			while (i.hasNext()) {
				Color c = (Color) i.next();
				if (c != null && !c.isDisposed())
					c.dispose();
			}
		}
	}

	public boolean isDropShadows() {
		return dropShadows;
	}

	public void setDropShadows(boolean dropShadows) {
		this.dropShadows = dropShadows;
	}

	public int getDropShadowsSize() {
		return dropShadowsSize;
	}

	@Override
	public void dispose() {
		freeDropShadowsColors();
	}

	public Point getBestSize(int originalX, int originalY, int maxX, int maxY) {
		double widthRatio = (double) originalX / (double) maxX;
		double heightRatio = (double) originalY / (double) maxY;

		double bestRatio = widthRatio > heightRatio ? widthRatio : heightRatio;

		int newWidth = (int) ((double) originalX / bestRatio);
		int newHeight = (int) ((double) originalY / bestRatio);

		return new Point(newWidth, newHeight);
	}
}
