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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;

/**
 * ImageDescriptor that downloads the image data from
 * https://chart.googleapis.com/ Typically used by a LabelProvider with an
 * ImageRegistry to provide map icons.
 * 
 * @see LabelProvider
 * @see ImageRegistry
 * @author hal
 *
 */
public class GoogleIconDescriptor extends ImageDescriptor {

	/**
	 * Options for the GoogleIconDescriptor
	 * 
	 * @since 3.3
	 *
	 */
	public static class Options {

		private String iconClass, iconName, style;
		private boolean hasShadow;
		private String text;
		private RGB fillColor, textColor;

		/**
		 * The various options that can be provided for Google's map icons
		 * 
		 * @param iconClass
		 * @param iconName
		 * @param style
		 * @param hasShadow
		 * @param text
		 * @param fillColor
		 * @param textColor
		 */
		public Options(String iconClass, String iconName, String style,
				boolean hasShadow, String text, RGB fillColor, RGB textColor) {
			super();
			setOptions(iconClass, iconName, style, hasShadow, text, fillColor,
					textColor);
		}

		private void setOptions(String iconClass, String iconName, String style,
				boolean hasShadow, String text, RGB fillColor, RGB textColor) {
			this.iconClass = iconClass;
			this.iconName = iconName;
			this.style = style;
			this.hasShadow = hasShadow;
			this.text = text;
			this.fillColor = fillColor;
			this.textColor = textColor;
		}

		/**
		 * Copying constructor
		 * 
		 * @param options
		 */
		public Options(Options options) {
			setOptions(options.iconClass, options.iconName, options.style,
					options.hasShadow, options.text, options.fillColor,
					options.textColor);
		}
	}

	private Options options;

	/**
	 * Initializes this GoogleIconDescriptor based on the provided options
	 * 
	 * @param options
	 */
	public GoogleIconDescriptor(Options options) {
		this.options = options;
	}

	private static String baseUrl = "https://chart.googleapis.com/chart?"; //$NON-NLS-1$
	private static String argsSep = "|"; //$NON-NLS-1$

	@Override
	public String toString() {
		return "[GoogleIconDescriptor @ " + getUrlString() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Gets the URL used to fetch the map icon
	 * 
	 * @return the URL used to fetch the map icon
	 */
	public String getUrlString() {
		String chst = options.iconClass;
		if (options.hasShadow) {
			chst += "_withshadow"; //$NON-NLS-1$
		}
		Object[] args = { options.iconName, options.style, options.text,
				toHex(options.fillColor), toHex(options.textColor) };
		StringBuilder chld = new StringBuilder();
		for (Object arg : args) {
			if (arg != null) {
				if (chld.length() > 0) {
					chld.append(argsSep);
				}
				try {
					chld.append(URLEncoder.encode(arg.toString(), "utf-8")); //$NON-NLS-1$
				} catch (UnsupportedEncodingException e) {
					// ignore
				}
			}
		}
		return baseUrl + "chst=" + chst + "&chld=" + chld; //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public ImageData getImageData() {
		try {
			return getImageData(getUrlString());
		} catch (Exception e) {
			return null;
		}
	}

	private String toHex(RGB rgb) {
		return String.format("%02x%02x%02x", rgb.red, rgb.green, rgb.blue); //$NON-NLS-1$
	}

	private ImageData getImageData(String urlString) {
		InputStream inputStream = null;
		try {
			inputStream = new URL(urlString).openStream();
			return new ImageData(inputStream);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	//

	public final static String icon_map_pin_letter = "d_map_pin_letter";
	public final static String icon_bubble_text_small = "d_bubble_text_small";
	public final static String icon_bubble_icon_text_small = "d_bubble_icon_text_small";

	public final static String icon_style_pin = "pin";
	public final static String icon_style_pin_star = "pin_star";
	public final static String icon_style_pin_sleft = "pin_sleft";
	public final static String icon_style_pin_sright = "pin_sright";

	public final static String frame_style_bb = "bb";
	public final static String frame_style_bbtl = "bbtl";
	public final static String frame_style_bbtr = "bbtr";
	public final static String frame_style_bbbr = "bbbr";
	public final static String frame_style_bbT = "bbT";

	public final static String frame_style_edge_bl = "edge_bl";
	public final static String frame_style_edge_bc = "edge_bc";
	public final static String frame_style_edge_br = "edge_br";
	public final static String frame_style_edge_tl = "edge_tl";
	public final static String frame_style_edge_tc = "edge_tc";
	public final static String frame_style_edge_tr = "edge_tr";
	public final static String frame_style_edge_lt = "edge_lt";
	public final static String frame_style_edge_lc = "edge_lc";
	public final static String frame_style_edge_lb = "edge_lb";
	public final static String frame_style_edge_rt = "edge_rt";
	public final static String frame_style_edge_rc = "edge_rc";
	public final static String frame_style_edge_rb = "edge_rb";

	/**
	 * Helper method for creating a letter map icon
	 * 
	 * @param c
	 *            the letter
	 * @param hasShadow
	 *            if it as a shaddow
	 * @param fillColor
	 *            the fill color
	 * @param textColor
	 *            the text color
	 * @return the corresponding descriptor
	 */
	public static GoogleIconDescriptor letterPin(char c, boolean hasShadow,
			RGB fillColor, RGB textColor) {
		return new GoogleIconDescriptor(new Options(icon_map_pin_letter, null,
				null, hasShadow, String.valueOf(c), fillColor, textColor));
	}

	/**
	 * Helper method for creating a text bubble map icon
	 * 
	 * @param s
	 *            the text
	 * @param hasShadow
	 *            if it as a shaddow
	 * @param fillColor
	 *            the fill color
	 * @param textColor
	 *            the text color
	 * @return the corresponding descriptor
	 */
	public static GoogleIconDescriptor textBubble(String s, boolean hasShadow,
			RGB fillColor, RGB textColor) {
		return new GoogleIconDescriptor(new Options(icon_bubble_text_small,
				null, frame_style_bb, hasShadow, s, fillColor, textColor));
	}
}
