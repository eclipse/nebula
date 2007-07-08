/*******************************************************************************
 * Copyright (c) 2006-2007 Nicolas Richeton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors :
 *    Nicolas Richeton (nicolas.richeton@gmail.com) - initial API and implementation
 *    Richard Michalsky - bugs 195415,  195443
 *******************************************************************************/
package org.eclipse.nebula.widgets.gallery;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * <p>
 * NOTE: THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT. THIS IS A
 * PRE-RELEASE ALPHA VERSION. USERS SHOULD EXPECT API CHANGES IN FUTURE
 * VERSIONS.
 * </p>
 * 
 * @author Nicolas Richeton (nicolas.richeton@gmail.com)
 * @contributor Richard Michalsky (bugs 195415, 195443)
 */
public class DefaultGalleryItemRenderer extends AbstractGalleryItemRenderer {

	ArrayList dropShadowsColors = new ArrayList();

	boolean dropShadows = false;

	int dropShadowsSize = 0;

	int dropShadowsAlphaStep = 20;

	Color selectionForegroundColor;

	Color selectionBackgroundColor;

	Color foregroundColor;

	Color backgroundColor;

	boolean showLabels = true;

	private Font font = null;

	public boolean isShowLabels() {
		return showLabels;
	}

	public void setShowLabels(boolean showLabels) {
		this.showLabels = showLabels;
	}

	public DefaultGalleryItemRenderer() {
		foregroundColor = Display.getDefault().getSystemColor(SWT.COLOR_LIST_FOREGROUND);
		backgroundColor = Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
		selectionForegroundColor = foregroundColor;
		selectionBackgroundColor = Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION);

		// Create drop shadows
		createColors();
	}

	public void draw(GC gc, GalleryItem item, int index, int x, int y, int width, int height) {
		Image itemImage = item.getImage();
		gc.setFont(font);

		int useableHeight = height;
		int fontHeight = 0;
		if (item.getText() != null && this.showLabels) {
			fontHeight = gc.getFontMetrics().getHeight();
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

			size = getBestSize(imageWidth, imageHeight, width - 8 - 2 * this.dropShadowsSize, useableHeight - 8 - 2 * this.dropShadowsSize);

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
			gc.setBackground(selectionBackgroundColor);
			gc.setForeground(selectionBackgroundColor);
			gc.fillRoundRectangle(x, y, width, useableHeight, 15, 15);

			if (item.getText() != null && showLabels) {
				gc.fillRoundRectangle(x, y + height - fontHeight, width, fontHeight, 15, 15);
			}
		}

		// Draw image
		if (itemImage != null) {
			if (size.x > 0 && size.y > 0) {
				gc.drawImage(itemImage, 0, 0, imageWidth, imageHeight, x + xShift, y + yShift, size.x, size.y);
			}
		}

		// Draw label
		if (item.getText() != null && showLabels) {
			// Set up the GC
			gc.setBackground(this.backgroundColor);
			gc.setForeground(selected ? this.selectionForegroundColor : this.foregroundColor);
			gc.setBackground(selected ? selectionBackgroundColor : this.selectionBackgroundColor);

			// Create label
			String text = RendererHelper.createLabel(item.getText(), gc, width - 10);

			// Center text
			int textWidth = gc.textExtent(text).x;
			int textxShift = (width - (textWidth > width ? width : textWidth)) >> 1;

			// Draw
			gc.drawText(text, x + textxShift, y + height - fontHeight, true);
		}
	}

	public void setDropShadowsSize(int dropShadowsSize) {
		this.dropShadowsSize = dropShadowsSize;
		this.dropShadowsAlphaStep = (dropShadowsSize == 0) ? 0 : (200 / dropShadowsSize);

		freeDropShadowsColors();
		createColors();
		// TODO: force redraw

	}

	private void createColors() {
		if (dropShadowsSize > 0) {
			int step = 125 / dropShadowsSize;
			// Create new colors
			for (int i = dropShadowsSize - 1; i >= 0; i--) {
				int value = 255 - i * step;
				Color c = new Color(Display.getDefault(), value, value, value);
				dropShadowsColors.add(c);
			}
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

	/**
	 * Returns the font used for drawing item label or <tt>null</tt> if system
	 * font is used.
	 * 
	 * @return the font
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * Set the font for drawing item label or <tt>null</tt> to use system
	 * font.
	 * 
	 * @param font
	 *            the font to set
	 */
	public void setFont(Font font) {
		if (this.font != font) {
			this.font = font;
			if (getGallery() != null)
				getGallery().redraw();
		}
	}

	public void dispose() {
		freeDropShadowsColors();
	}

	protected Point getBestSize(int originalX, int originalY, int maxX, int maxY) {
		double widthRatio = (double) originalX / (double) maxX;
		double heightRatio = (double) originalY / (double) maxY;

		double bestRatio = widthRatio > heightRatio ? widthRatio : heightRatio;

		int newWidth = (int) ((double) originalX / bestRatio);
		int newHeight = (int) ((double) originalY / bestRatio);

		return new Point(newWidth, newHeight);
	}

	public Color getForegroundColor() {
		return foregroundColor;
	}

	public void setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
	}

	public Color getSelectionForegroundColor() {
		return selectionForegroundColor;
	}

	public void setSelectionForegroundColor(Color selectionForegroundColor) {
		this.selectionForegroundColor = selectionForegroundColor;
	}

	public Color getSelectionBackgroundColor() {
		return selectionBackgroundColor;
	}

	public void setSelectionBackgroundColor(Color selectionBackgroundColor) {
		this.selectionBackgroundColor = selectionBackgroundColor;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
}
