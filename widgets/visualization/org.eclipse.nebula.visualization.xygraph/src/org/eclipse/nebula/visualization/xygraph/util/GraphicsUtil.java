/*******************************************************************************
 * Copyright (c) 2010, 2017 Oak Ridge National Laboratory and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.util;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Transform;

/**
 * Utility function for graphics operations.
 * 
 * @author Xihui Chen
 * 
 */
public final class GraphicsUtil {

	private static boolean isRAP = SWT.getPlatform().startsWith("rap"); //$NON-NLS-1$ ;

	/**
	 * Draw vertical text.
	 * 
	 * @param graphics
	 *            draw2D graphics.
	 * @param text
	 *            text to be drawn.
	 * @param x
	 *            the x coordinate of the text, which is the left upper corner.
	 * @param y
	 *            the y coordinate of the text, which is the left upper corner.
	 */
	public static final void drawVerticalText(Graphics graphics, String text, int x, int y, boolean upToDown) {
		try {
			if (SWT.getPlatform().startsWith("rap")) //$NON-NLS-1$
				throw new Exception();
			try {
				graphics.pushState();
				graphics.translate(x, y);
				if (upToDown) {
					graphics.rotate(90);
					graphics.drawText(text, 0, -FigureUtilities.getTextExtents(text, graphics.getFont()).height);
				} else {
					graphics.rotate(270);
					graphics.drawText(text, -FigureUtilities.getTextWidth(text, graphics.getFont()), 0);
				}
			} finally {
				graphics.popState();
			}
		} catch (Exception e) {// If rotate is not supported by the graphics.
			// final Dimension titleSize = FigureUtilities.getTextExtents(text,
			// graphics.getFont());

			// final int w = titleSize.height;
			// final int h = titleSize.width + 1;
			Image image = null;
			try {
				image = SingleSourceHelper2.createVerticalTextImage(text, graphics.getFont(),
						graphics.getForegroundColor().getRGB(), upToDown);
				graphics.drawImage(image, x, y);

			} finally {
				if (image != null)
					image.dispose();
			}
		}
	}

	/**
	 * Draw vertical text.
	 * 
	 * @param graphics
	 *            draw2D graphics.
	 * @param text
	 *            text to be drawn.
	 * @param location
	 *            the left upper corner coordinates of the text.
	 */
	public static final void drawVerticalText(Graphics graphics, String text, Point location, boolean upToDown) {
		drawVerticalText(graphics, text, location.x, location.y, upToDown);
	}

	public static final boolean isRAP() {
		return isRAP;
	}

	/**
	 * Used for single sourcing, returns null if called in RAP Context.
	 * 
	 * @param image
	 * @return
	 */
	public static GC createGC(Drawable image) {
		try {
			return GC.class.getConstructor(Drawable.class).newInstance(image);
		} catch (Exception ne) {
			return null;
		}
	}

	public static void setTransform(GC gc, Transform transform) {
		try {
			GC.class.getMethod("setTransform", Transform.class).invoke(gc, transform);
		} catch (Exception ne) {
			return;
		}
	}

	public static Cursor createCursor(Device device, ImageData imageData, int width, int height) {
		try {
			return Cursor.class.getConstructor(Device.class, ImageData.class, int.class, int.class).newInstance(device,
					imageData, width, height);
		} catch (Exception ne) {
			return null;
		}
	}
}
