/*
 * Copyright (c) 2007-2008 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */

package org.eclipse.nebula.paperclips.core.internal.util;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

/**
 * Manages a pool of graphics resources for a graphics device (fonts, colors).
 * 
 * @author Matthew Hall
 */
public class ResourcePool {
	private static Map devices = new WeakHashMap(); // Map <Device,

	// ResourcePool>

	/**
	 * Returns a SharedGraphics which creates resources on the given device.
	 * 
	 * @param device
	 *            the device which resources will be created on.
	 * @return a SharedGraphics which creates resources on the given device.
	 */
	public synchronized static ResourcePool forDevice(Device device) {
		Util.notNull(device);
		notDisposed(device);

		ResourcePool sharedGraphics = (ResourcePool) devices.get(device);
		if (sharedGraphics == null) {
			sharedGraphics = new ResourcePool(device);
			devices.put(device, sharedGraphics);
		}
		return sharedGraphics;
	}

	private static void notDisposed(Device device) {
		if (device.isDisposed())
			PaperClips.error(SWT.ERROR_DEVICE_DISPOSED);
	}

	private final Device device;
	private final Map fonts; // Map <FontData, Font>
	private final Map colors; // Map <RGB, Color>

	private ResourcePool(Device device) {
		this.device = device;
		this.fonts = new HashMap();
		this.colors = new HashMap();
	}

	/**
	 * Returns a font for the passed in FontData.
	 * 
	 * @param fontData
	 *            FontData describing the required font.
	 * @return a font for the passed in FontData.
	 */
	public Font getFont(FontData fontData) {
		if (fontData == null)
			return null;
		notDisposed(device);

		Font font = (Font) fonts.get(fontData);
		if (font == null) {
			font = new Font(device, fontData);
			fonts.put(SWTUtil.copy(fontData), font);
		}
		return font;
	}

	/**
	 * Returns a color for the passed in RGB.
	 * 
	 * @param rgb
	 *            RGB describing the required color.
	 * @return a color for the passed in RGB.
	 */
	public Color getColor(RGB rgb) {
		if (rgb == null)
			return null;
		notDisposed(device);

		Color color = (Color) colors.get(rgb);
		if (color == null) {
			color = new Color(device, rgb);
			colors.put(SWTUtil.copy(rgb), color);
		}
		return color;
	}
}