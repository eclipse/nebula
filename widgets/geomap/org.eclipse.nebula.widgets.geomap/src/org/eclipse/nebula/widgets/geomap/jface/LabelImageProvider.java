/*******************************************************************************
 * Copyright (c) 2012 Hallvard Tr�tteberg.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Hallvard Tr�tteberg - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.geomap.jface;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

/**
 * A default implementation of a LabelProvider that uses a GoogleIconDescriptor
 * to create a bubble image with the text returned by getText.
 * 
 * @author hal
 *
 */
public class LabelImageProvider extends LabelProvider
		implements IPinPointProvider, IToolTipProvider {

	private ImageRegistry imageRegistry;

	private boolean hasShadow;

	private RGB textColor = new RGB(0, 0, 0);
	private RGB fillColor = new RGB(255, 250, 200);

	private String iconStyle = GoogleIconDescriptor.icon_bubble_text_small;
	private String frameStyle = GoogleIconDescriptor.frame_style_bb;

	/**
	 * Gets the ImageRegistry use by this LabelImageProvider
	 * 
	 * @return the
	 */
	protected ImageRegistry getImageRegistry() {
		if (imageRegistry == null) {
			imageRegistry = createImageRegistry();
		}
		return imageRegistry;
	}

	/**
	 * Creates the ImageRegistry
	 * 
	 * @return the newly created ImageRegistry
	 */
	protected ImageRegistry createImageRegistry() {
		return new ImageRegistry();
	}

	@Override
	public void dispose() {
		if (imageRegistry != null) {
			imageRegistry.dispose();
			imageRegistry = null;
		}
		super.dispose();
	}

	/**
	 * Gets the setting for the shadow argument provided to GoogleIconDescriptor
	 * 
	 * @return the current shaddow value
	 */
	public boolean hasShadow() {
		return hasShadow;
	}

	/**
	 * Sets the shadow argument provided to GoogleIconDescriptor
	 * 
	 * @param hasShadow
	 *            the new shadow value
	 */
	public void setHasShadow(boolean hasShadow) {
		this.hasShadow = hasShadow;
	}

	/**
	 * Gets the setting for the textColor argument provided to
	 * GoogleIconDescriptor
	 * 
	 * @return the current textColor
	 */
	public RGB getTextColor() {
		return textColor;
	}

	/**
	 * Sets the textColor argument provided to GoogleIconDescriptor
	 * 
	 * @param textColor
	 *            the new textColor value
	 */
	public void setTextColor(RGB textColor) {
		this.textColor = textColor;
	}

	/**
	 * Gets the setting for the fillColor argument provided to
	 * GoogleIconDescriptor
	 * 
	 * @return the current fillColor
	 */
	public RGB getFillColor() {
		return fillColor;
	}

	/**
	 * Sets the fillColor argument provided to GoogleIconDescriptor
	 * 
	 * @param fillColor
	 *            the new fillColor value
	 */
	public void setFillColor(RGB fillColor) {
		this.fillColor = fillColor;
	}

	/**
	 * Gets the setting for the iconStyle argument provided to
	 * GoogleIconDescriptor
	 * 
	 * @return the current iconStyle
	 */
	public String getIconStyle() {
		return iconStyle;
	}

	/**
	 * Sets the iconStyle argument provided to GoogleIconDescriptor
	 * 
	 * @param iconStyle
	 *            the new iconStyle value
	 */
	public void setIconStyle(String iconStyle) {
		this.iconStyle = iconStyle;
	}

	/**
	 * Gets the setting for the frameStyle argument provided to
	 * GoogleIconDescriptor
	 * 
	 * @return the current frameStyle
	 */
	public String getFrameStyle() {
		return frameStyle;
	}

	/**
	 * Sets the frameStyle argument provided to GoogleIconDescriptor
	 * 
	 * @param frameStyle
	 *            the new frameStyle value
	 */
	public void setFrameStyle(String frameStyle) {
		this.frameStyle = frameStyle;
	}

	/**
	 * Gets the label image for the provided element
	 * 
	 * @param element
	 *            the element
	 * @return the label image
	 */
	protected Image getLabelImage(Object element) {
		String text = getText(element);
		if (text == null) {
			return null;
		}
		Image image = getImageRegistry().get(text);
		if (image == null) {
			GoogleIconDescriptor.Options options = new GoogleIconDescriptor.Options(
					getIconStyle(), null, getFrameStyle(), hasShadow(), text,
					getFillColor(), getTextColor());
			GoogleIconDescriptor descriptor = new GoogleIconDescriptor(options);
			imageRegistry.put(text, descriptor);
			image = getImageRegistry().get(text);
		}
		return image;
	}

	@Override
	public Image getImage(Object element) {
		return getLabelImage(element);
	}

	@Override
	public Point getPinPoint(Object element) {
		// must correspond to frameStyle
		return getPinPoint(element, 0.0f, 1.0f);
	}

	/**
	 * Helper method for computing the point based on the size of the image. The
	 * float arguments alignX and alignY are multiplied with the width and
	 * height of the image, respectively.
	 * 
	 * @param element
	 *            the element to provide the point for
	 * @param alignX
	 *            a float that is multiplied with the width of the image, to
	 *            give the x coordinate of the point
	 * @param alignY
	 *            a float that is multiplied with the height of the image, to
	 *            give the y coordinate of the point
	 * @return the computed point
	 */
	protected Point getPinPoint(Object element, float alignX, float alignY) {
		Rectangle bounds = getImage(element).getBounds();
		return new Point((int) (bounds.width * alignX),
				(int) (bounds.height * alignY));
	}

	@Override
	public Object getToolTip(Object element) {
		return getText(element);
	}
}
