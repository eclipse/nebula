/*******************************************************************************
 * Copyright (c) 2012 Hallvard Tr¾tteberg.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Hallvard Tr¾tteberg - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.geomap.jface;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

/**
 * A default implementation of a LabelProvider that uses a GoogleIconDescriptor to create a bubble image with the text returned by getText.
 * @author hal
 *
 */
public class LabelImageProvider extends LabelProvider implements IPinPointProvider, IToolTipProvider {

	private ImageRegistry imageRegistry;
	
	private boolean hasShadow;
	
	private RGB textColor = new RGB(0, 0, 0);
	private RGB fillColor = new RGB(255, 250, 200);

	private String iconStyle = GoogleIconDescriptor.icon_bubble_text_small;
	private String frameStyle = GoogleIconDescriptor.frame_style_bb;

	protected ImageRegistry getImageRegistry() {
		if (imageRegistry == null) {
			imageRegistry = createImageRegistry();
		}
		return imageRegistry;
	}
	
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
	
	public boolean hasShadow() {
		return hasShadow;
	}

	public void setHasShadow(boolean hasShadow) {
		this.hasShadow = hasShadow;
	}

	public RGB getTextColor() {
		return textColor;
	}

	public void setTextColor(RGB textColor) {
		this.textColor = textColor;
	}

	public RGB getFillColor() {
		return fillColor;
	}

	public void setFillColor(RGB fillColor) {
		this.fillColor = fillColor;
	}

	public String getIconStyle() {
		return iconStyle;
	}

	public void setIconStyle(String iconStyle) {
		this.iconStyle = iconStyle;
	}

	public String getFrameStyle() {
		return frameStyle;
	}

	public void setFrameStyle(String frameStyle) {
		this.frameStyle = frameStyle;
	}

	@Override
	public Image getImage(Object element) {
		String text = getText(element);
		if (text == null) {
			return null;
		}
		Image image = getImageRegistry().get(text);
		if (image == null) {
			GoogleIconDescriptor.Options options = new GoogleIconDescriptor.Options(getIconStyle(), null, getFrameStyle(), hasShadow(), text, getFillColor(), getTextColor());
			GoogleIconDescriptor descriptor = new GoogleIconDescriptor(options);
			imageRegistry.put(text, descriptor);
			image = getImageRegistry().get(text);
		}
		return image;
	}

	public Point getPinPoint(Object element) {
		// must correspond to frameStyle
		return getPinPoint(element, 0.0f, 1.0f);
	}
	
	/**
	 * Helper method for computing the point based on the size of the image.
	 * The float arguments alignX and alignY are multiplied with the width and height of the image, respectively. 
	 * @param element the element to provide the point for
	 * @param alignX a float that is multiplied with the width of the image, to give the x coordinate of the point 
	 * @param alignY a float that is multiplied with the height of the image, to give the y coordinate of the point
	 * @return
	 */
	protected Point getPinPoint(Object element, float alignX, float alignY) {
		Rectangle bounds = getImage(element).getBounds();
		return new Point((int) (bounds.width * alignX), (int) (bounds.height * alignY));
	}

	public Object getToolTip(Object element) {
		return getText(element);
	}
}
